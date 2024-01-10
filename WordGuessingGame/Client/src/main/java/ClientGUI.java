import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientGUI extends Application {

    private TextField serverAddressField;
    private TextField portField;
    private TextArea gameProgressArea;
    private Button connectButton;
    private Stage primaryStage;
    private Socket mysocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int port;
    private boolean buttonsDisabled = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Word Guessing Game Client");

        Label portLabel = new Label("Port:");
        portField = new TextField("5555");
        portField.setPromptText("Enter port number");

        gameProgressArea = new TextArea();
        gameProgressArea.setEditable(false);

        connectButton = new Button("Connect");
        connectButton.setOnAction(e -> {
            try {
                String portText = portField.getText().trim();
                if (!portText.isEmpty()) {
                    port = Integer.parseInt(portText);
                    mysocket = new Socket("127.0.0.1", port);

                    out = new ObjectOutputStream(mysocket.getOutputStream());
                    in = new ObjectInputStream(mysocket.getInputStream());
                    mysocket.setTcpNoDelay(true);

                    // Transition to hangman game screen
                    showHangmanGame();
                }
            } catch (IOException ex) {
                ex.printStackTrace();  // Handle the exception appropriately
            }
        });

        VBox layout = new VBox(10);
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        layout.getChildren().addAll(portLabel, portField, gameProgressArea, connectButton);
        layout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showHangmanGame() {
        // Create UI components for the hangman game screen
        Label categoryLabel = new Label("Select a category:");
        Button category1Button = new Button("Category 1: Colors");
        Button category2Button = new Button("Category 2: Fruits");
        Button category3Button = new Button("Category 3: Countries");
        Label enterGuess = new Label("Enter your Guess ");
        TextField letterGuessField = new TextField();
        letterGuessField.setPromptText("Enter your letter guess");
        Label attemptsLabel = new Label("Attempts: ");
        Label encodedWordLabel = new Label("Encoded Word: ");
        Label serverLabel = new Label("Server Response: ");
        Label categoryAttemptLabel = new Label("Category Attempts: ");



        Button guessButton = new Button("Guess");
        guessButton.setOnAction(e -> {
            // Handle the letter guess, send it to the server, and update game progress
            String letterGuess = letterGuessField.getText().trim();
            // Send letterGuess to the server (implement this part)
            try {
                out.writeObject(letterGuess);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            // Update game progress based on the server response
            Platform.runLater(() -> {
                gameProgressArea.appendText("You guessed: " + letterGuess + "\n");
                letterGuessField.setText("");
            });
        });
        Button playAgainButton = new Button("Play Again");
        playAgainButton.setDisable(true);
        Button quitButton = new Button("Quit");
        quitButton.setDisable(true);

        quitButton.setOnAction(e -> {

            try {
                mysocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            primaryStage.close();
        });
        playAgainButton.setOnAction(e -> {
            try {
                // Send a signal to the server indicating the desire to play again
                out.writeObject("Again");

                // Reset game state
                category1Button.setDisable(false);
                category2Button.setDisable(false);
                category3Button.setDisable(false);
                guessButton.setDisable(false);
                buttonsDisabled = false;

                // Clear game progress information
                Platform.runLater(() -> {
                    attemptsLabel.setText("Attempts: ");
                    encodedWordLabel.setText("Encoded Word: ");
                    serverLabel.setText("Server Response: ");
                    categoryAttemptLabel.setText("Category Attempts: ");
                    gameProgressArea.clear();  // Clear the game progress area
                });

                // Disable playAgainButton until the next game starts
                playAgainButton.setDisable(true);
                quitButton.setDisable(true);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });



        category1Button.setOnAction(e -> {
            int categorySelection = 1;
            try{
                out.writeObject(categorySelection);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        category2Button.setOnAction(e -> {
            int categorySelection = 2;
            try{
                out.writeObject(categorySelection);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        category3Button.setOnAction(e -> {
            int categorySelection = 3;
            try{
                out.writeObject(categorySelection);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        new Thread(() -> {
            while (true) {
                try {
                    //Read data from server
                    String attempts = (String) in.readObject();
                    String encodedWord = (String) in.readObject();
                    String serverResponse = (String) in.readObject();
                    String categoryResponse = (String) in.readObject();



                    // Update the UI with the initial attempts and encoded word
                    Platform.runLater(() -> {
                        attemptsLabel.setText(attempts);
                        encodedWordLabel.setText(encodedWord);
                        serverLabel.setText(serverResponse);
                        categoryAttemptLabel.setText(categoryResponse);


                        if (serverResponse.contains("Server Response: You guessed the word correctly in category 1") || categoryResponse.equals("Category Attempts: 0")) {
                            category1Button.setDisable(true);
                            buttonsDisabled = true;
                        }

                        if (serverResponse.contains("Server Response: You guessed the word correctly in category 2") || categoryResponse.equals("Category Attempts: 0")) {
                            category2Button.setDisable(true);
                            buttonsDisabled = true;
                        }

                        if (serverResponse.contains("Server Response: You guessed the word correctly in category 3") || categoryResponse.equals("Category Attempts: 0")) {
                            category3Button.setDisable(true);
                            buttonsDisabled = true;
                        }

                        boolean allButtonsDisabled = category1Button.isDisabled() && category2Button.isDisabled() && category3Button.isDisabled();
                        guessButton.setDisable(categoryResponse.equals("Category Attempts: 0") || allButtonsDisabled);
                        playAgainButton.setDisable(!guessButton.isDisabled() || !allButtonsDisabled);
                        quitButton.setDisable(!guessButton.isDisabled() || !allButtonsDisabled);

                    });

                } catch (IOException | ClassNotFoundException e) {
                    // Handle exceptions as needed
                    e.printStackTrace();
                }
            }
        }).start();



        VBox hangmanLayout = new VBox(10);

        hangmanLayout.getChildren().addAll(categoryLabel, category1Button, category2Button, category3Button,
                enterGuess,letterGuessField, guessButton,attemptsLabel, encodedWordLabel,serverLabel, categoryAttemptLabel, quitButton, playAgainButton);
        hangmanLayout.setAlignment(javafx.geometry.Pos.CENTER);
        hangmanLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));

        Scene hangmanScene = new Scene(hangmanLayout, 400, 400);
        primaryStage.setScene(hangmanScene);
    }

}
