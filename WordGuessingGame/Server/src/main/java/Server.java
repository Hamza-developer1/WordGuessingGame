import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server{

	int count = 1;
	int currentCategory;
	int category1 = 3;
	int category2 = 3;
	int category3 = 3;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	Hangman hangman;
	int port;
	private Consumer<Serializable> callback;

	Server(Consumer<Serializable> call){

		callback = call;
		server = new TheServer();
		server.start();
	}

	public class TheServer extends Thread{

		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555)){
				System.out.println("Server is waiting for a client!");


				while(true) {

					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();

					count++;

				}
			}//end of try
			catch(Exception e) {
				callback.accept("Server socket did not launch");
			}
		}//end of while
	}

	class ClientThread extends Thread{


		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;

		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;
		}

		public void updateClients(String message) {
			for(int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				try {
					t.out.writeObject(message);
				}
				catch(Exception e) {}
			}
		}

		public void run() {
			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			} catch (Exception e) {
				System.out.println("Streams not open");
			}

			//updateClients("new client on server: client #" + count);

			while (true) {
				try {

					String data = in.readObject().toString();
					callback.accept("client: " + count + " sent: " + data);

					if (data.equals("1") || data.equals("2") || data.equals("3")) {

						int categoryIndex = Integer.parseInt(data);
						currentCategory = categoryIndex;
						hangman = new Hangman(categoryIndex);
						callback.accept("Hangman initialized for client #" + count + " in Category " + categoryIndex);
						// Send the initial attempts to the client
						out.writeObject("Word: " + hangman.getEncodedWord());
						out.writeObject("Attempts: " + hangman.getAttempts());
						out.writeObject("Server Response: Category " + categoryIndex);
						if(currentCategory == 1){
							out.writeObject("Category Attempts: " + category1);
						}
						else if(currentCategory == 2){
							out.writeObject("Category Attempts: " + category2);
						}
						else if(currentCategory == 3){
							out.writeObject("Category Attempts: " + category3);
						}



					}

					if (data.length() == 1 && Character.isLetter(data.charAt(0))) {
						char guess = Character.toLowerCase(data.charAt(0)); // Convert to lowercase for consistency

						hangman.userGuess(guess);
						out.writeObject("Word: " + hangman.getEncodedWord());
						out.writeObject("Attempts: " + hangman.getAttempts());
						String serverResponse = "Server Response: " + hangman.serverResponse(guess);

						if (hangman.isGameWon()) {
							serverResponse = "Server Response: You guessed the word correctly in category " + currentCategory;

						} else if (hangman.isGameOver()) {
							if(currentCategory == 1){
								category1--;
							}
							else if(currentCategory == 2){
								category2--;
							}
							else if(currentCategory == 3){
								category3--;
							}

							serverResponse = "Server Response: Sorry, you lost this round. The word was: " + hangman.getAnswer() +
									". Click the Category " + currentCategory + " button to retry if you still have attempts";

						}
						out.writeObject(serverResponse);
						if(currentCategory == 1){
							out.writeObject("Category Attempts: " + category1);
						}
						else if(currentCategory == 2){
							out.writeObject("Category Attempts: " + category2);
						}
						else if(currentCategory == 3){
							out.writeObject("Category Attempts: " + category3);
						}

					}

				} catch (Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					updateClients("Client #" + count + " has left the server!");
					clients.remove(this);
					break;
				}
			}
		}// end of run

	}//end of client thread
}
