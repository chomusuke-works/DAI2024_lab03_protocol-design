package ch.heig.dai.lab.protocoldesign;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
	final int SERVER_PORT = 1234;

	// Welcome msg
	private static final String welcomeMsg =
			"Welcome to this remote calculator.\n"
			+ "The currently supported operations are\n"
			+ "- ADD\n"
			+ "- SUBTRACT\n"
			+ "- MULTIPLY\n\n"
			+ "Please use the operations with the following syntax :\n"
			+ "OPERATION num1 num2\n";


	public static void main(String[] args) {
		// Create a new server and run it
		Server server = new Server();
		server.run();
	}

	private void run() {
		// Create socket
		try (ServerSocket serverSocket = new ServerSocket(25565)) {  // TODO Replace 25565
			System.out.println("Server is listening on port " + 25565);

			// For each client
			while (true) {
				try (Socket client = serverSocket.accept()) {
					System.out.println("Client connected");  // LOG
					var writer =
							new BufferedWriter(
									new OutputStreamWriter(
											client.getOutputStream(),
											StandardCharsets.UTF_8)
							);
					// Send welcome message
					writer.write(welcomeMsg);
					writer.flush();
					System.out.println("Client disconected");  // LOG
				}
			}


		// Wait for user input

		// Process user input

		// Send result

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}