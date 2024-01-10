import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
	private Socket socketClient;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Consumer<Serializable> callback;
	private int port;
	private String serverAddress;

	Client(Consumer<Serializable> call, int port, String serverAddress) {
		callback = call;
		this.port = port;
		this.serverAddress = "serverAddress";

	}

	public void run() {
		try {
			socketClient = new Socket(serverAddress,port);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error establishing connection to the server: " + e.getMessage());
			return; // Exit the thread if there is an error during initialization
		}

		while (true) {
			try {
				String message = in.readObject().toString();
				callback.accept(message);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error reading from the server: " + e.getMessage());
				break; // Exit the thread if there is an error while reading
			}
		}


	}


	public void send(String data, String target) {
		try {
			out.writeObject(target + ":" + data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
