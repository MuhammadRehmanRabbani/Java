import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
	// defining socket for client
	private static Socket clientSocket = null;
	// defining PrintStream as output stream
	private static PrintStream outToServer = null;
	// defining input stream to read data from server
	private static DataInputStream inFromServer = null;
	//defining BufferedReader to read data from user
	private static BufferedReader inFromUser = null;
    // declaring boolean variable in order to close the connection 
	private static boolean closed = false;

	
	public static void main(String[] args) {
		
		// setting the default port
		int port = 2222;
		// setting default host
		String host = "localhost";

		if (args.length < 2) {
			System.out.println("MultiThreadClient <host> <portNumber>\n"
					+ "using host=" + host + ", portNumber=" + port);
		} else {
			host = args[0];
			port = Integer.valueOf(args[1]).intValue();
		}

		try {
			// create client socket to connect to server
			clientSocket = new Socket(host, port);
			//create new input stream to take input from user
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
			// create output stream attached to socket
			outToServer = new PrintStream(clientSocket.getOutputStream());
			// create input stream attached to socket
			inFromServer = new DataInputStream(clientSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host "
					+ host);
		}

		/*
		 * After initializing everything, now we then we want to write some data to the socket
		 */
		
		if (clientSocket != null && outToServer != null && inFromServer != null) {
			try {
				//create a new thread for new client to read data from server
				new Thread(new Client()).start();
				while (!closed) {
					// send the typed message to server
					outToServer.println(inFromUser.readLine().trim());
				}
				
				outToServer.close(); //closing output stream
				inFromServer.close(); //closing input stream
				clientSocket.close(); //closing socket
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}
	
	/*
	 * defining run function
	 * it will create new thread to read data from server
	 */
  public void run() {
	  /*
	   * until "Bye" is received from server keep on reading data from the socket
	   */
	  String response;
	  try {
		  while ((response = inFromServer.readLine()) != null) {
			  System.out.println(response);
			  //if "Bye" is received, break
			  if (response.indexOf("*** Bye") != -1)
				  break;
		  }
		  closed = true;
	  } catch (IOException e) {
		  System.err.println("IOException:  " + e);
	  }
  	}
}
