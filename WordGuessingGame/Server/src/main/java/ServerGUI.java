import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerGUI extends Application {

    private ListView<String> serverLog;
    private TextField portField;
    private Label labelOne;

    private Server server;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Word Guessing Game Server");

        // Server log area
        ObservableList<String> messagesList = FXCollections.observableArrayList();
        serverLog = new ListView<>(messagesList);
        serverLog.setEditable(false);

        // Port input
        Label portLabel = new Label("Enter Port:");
        portField = new TextField("5555");
        portField.setPromptText("Enter port number");

        // Label for displaying port information
        labelOne = new Label();

        // Start server button
        Button startServerButton = new Button("Start Server");
        startServerButton.setOnAction(e ->{
            try {
                String input = portField.getText().trim();
                int data = Integer.parseInt(input);
                server = new Server(message -> {
                    // Update GUI with messages received from the server
                    Platform.runLater(() -> messagesList.add((String) message));  // Add the message to the ObservableList
                } );

                portField.clear();
                portField.setVisible(false); // Assuming you want to hide the port field after starting the server
                labelOne.setText("Logged Into Port: " + data);
                startServerButton.setDisable(true); // Assuming you want to disable the button after starting the server

            } catch (NumberFormatException ex) {
                System.err.println("Invalid port number: " + portField.getText());
            }
        });

        // Layout
        VBox layout = new VBox(10);
        layout.setStyle("-fx-background-color: lightblue;");
        layout.getChildren().addAll(serverLog, portLabel, portField, startServerButton, labelOne);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    @Override
    public void stop() {
        // Stop the server when the GUI is closed
        if (server != null) {
            // server.stopServer();
        }
    }
}
