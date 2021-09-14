import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {

	// defining ServerSocket
	private static ServerSocket serverSocket = null;
	// defining socket for client
	private static Socket clientSocket = null;
	// defining variable to set limit for number of clients
	private static final int maxClients = 10;
	// creating an array of threads, one thread for each client
	private static final clientThread[] threads = new clientThread[maxClients];

	public static void main(String args[]) {

		// setting the default port
		int port = 2222;
		if (args.length < 1) {
			System.out.println("MultiThreadServer <portNumber>\n"
					+ "using port number=" + port);
		} else {
			port = Integer.valueOf(args[0]).intValue();
		}

		try {
			// creating server socket at previously defined port number
			serverSocket = new ServerSocket(port);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		/*
		 * Create a new client socket for each connection
		 * then assign it to a new client thread
		 */
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxClients; i++) {
					if (threads[i] == null) {
						// add new thread to threads array and start it
						(threads[i] = new clientThread(clientSocket, threads)).start();
						break;
					}
				}
				// if threads array is full, don't take more connections
				if (i == maxClients) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				 	}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
}

// defining clientThread class to handle the functionality of client

class clientThread extends Thread {
	// declaring variable to get client name
	private String clientName = null;
	//declaring input stream to read date from client
	private DataInputStream inFromClient = null;
	// declaring output stream to client
	private PrintStream outToClient = null;
	// declaring client socket
	private Socket clientSocket = null;
	// declaring array for clients threads
	private final clientThread[] threads;
	// declaring count for max number of clients i.e.10
	private int maxClients;

	// defining constructor with arguments
	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClients = threads.length;
	}
	
	// defining run method
	public void run() {
		int maxClients = this.maxClients;
		clientThread[] threads = this.threads;
		try {
			//create input stream attached to socket
			inFromClient =  new DataInputStream(clientSocket.getInputStream());
			// create output stream attached to socket
			outToClient = new PrintStream(clientSocket.getOutputStream());
			
			String name;
			while (true) {
				//get client name
				outToClient.println("Enter your name: ");
				name = inFromClient.readLine().trim();
				if (name.indexOf('@') == -1) {
					break;
				} else {
					outToClient.println("The name should not contain '@' character.");
				}
			}
			
			outToClient.println("Welcome " + name
					+ " to the chat room.\nTo quit enter 'bye'.");
			
			// storing client name
			synchronized (this) {
				for (int i = 0; i < maxClients; i++) {
					if (threads[i] != null && threads[i] == this) {
						clientName = "@" + name;
						break;
					}
				}
				// telling about new client to all the clients
				for (int i = 0; i < maxClients; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].outToClient.println("-----New client: " + name
								+ " entered the chat room-----");
					}
				}
			}
			// starting the conversation here
			while (true) {
				String line = inFromClient.readLine();
				if (line.startsWith("bye")||line.startsWith("Bye")) {
					break;
				}
				// if a client wants to send private message to the other client
				if (line.startsWith("@")) {
					String[] words = line.split("\\s", 2);
					if (words.length > 1 && words[1] != null) {
						words[1] = words[1].trim();
						if (!words[1].isEmpty()) {
							synchronized (this) {
								for (int i = 0; i < maxClients; i++) {
									if (threads[i] != null && threads[i] != this
											&& threads[i].clientName != null
											&& threads[i].clientName.equals(words[0])) {
										threads[i].outToClient.println("<" + name + "> " + words[1]);
										// tell the recipient from which client he has received message
										this.outToClient.println(">" + name + "> " + words[1]);
										break;
									}
								}
							}
						}
					}
				} else {
					// if client wants to send message to all the clients
					synchronized (this) {
						for (int i = 0; i < maxClients; i++) {
							if (threads[i] != null && threads[i].clientName != null) {
								threads[i].outToClient.println("<" + name + "> " + line);//print client name
							}
						}
					}
				}
			}
			// if a client has entered 'bye' to end chat from his side
			synchronized (this) {
				for (int i = 0; i < maxClients; i++) {
					if (threads[i] != null && threads[i] != this
							&& threads[i].clientName != null) {
						threads[i].outToClient.println("Client " + name
								+ " is leaving the chat room...");
					}
				}
			}
			// give greeting message to the leaving client
			outToClient.println("*** Bye " + name + " ...");
			
			//setting current thread to null so that server can accept another client
			synchronized (this) {
				for (int i = 0; i < maxClients; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}

			inFromClient.close(); // closing input stream
			outToClient.close(); // closing output stream
			clientSocket.close(); // closing socket
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}