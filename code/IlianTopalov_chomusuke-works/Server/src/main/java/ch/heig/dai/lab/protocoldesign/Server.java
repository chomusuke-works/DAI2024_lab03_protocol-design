package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
	final int SERVER_PORT = 25565;
	static final String EXIT_CODE = "BYE";
	static final String ERROR_UNKNOWN_OPERATION_CODE = "EUO";
	static final String ERROR_NUMBER_FORMAT_CODE = "ENF";

	// Welcome msg
	private static final String WELCOME_MSG =
			"""
			Welcome to this remote calculator.
			
			These operations are supported:
			- ADD <a> <b>    Add a and b
			- SUB <a> <b>    Subtract b from a
			- MUL <a> <b>    Multiply a by b
			- BYE            Close the connection
			""";
	private static final int WELCOME_MSG_LINE_COUNT = 7;


	public static void main(String[] args) {
		// Create a new server and run it
		Server server = new Server();
		server.run();
	}

	private void run() {
		// Create socket
		try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
			System.out.println("Server is listening on port " + SERVER_PORT);

			// For each client
			while (true) {
				try (Socket client = serverSocket.accept()) {
					System.out.printf("%s connected \n", client.getInetAddress().getHostAddress());  // LOG

					var writer = new BufferedWriter(new OutputStreamWriter(
						client.getOutputStream(),
						StandardCharsets.UTF_8
					));

					BufferedReader reader = new BufferedReader(new InputStreamReader(
						client.getInputStream(),
						StandardCharsets.UTF_8
					));

					// Send welcome message
					writer.write(String.format("%d\n%s", WELCOME_MSG_LINE_COUNT, WELCOME_MSG));
					writer.flush();

					// Wait for user input
					String clientRequest;
					while (true) {
						clientRequest = reader.readLine().toUpperCase();
						System.out.println("C > " + clientRequest);  // LOG

						// Process client input and send serverResponse to client
						if (clientRequest.equals(EXIT_CODE)) {
							System.out.println("Client disconnected \n");  // LOG
							break;
						}

						String serverResponse;
						try {
							serverResponse = String.valueOf(calculateFromString(clientRequest));
						} catch (NumberFormatException e) {
							serverResponse = ERROR_NUMBER_FORMAT_CODE;
						} catch (IllegalOperationException e) {
							serverResponse = ERROR_UNKNOWN_OPERATION_CODE;
						}
						System.out.println("S > " + serverResponse);  // LOG

						writer.write(serverResponse + '\n');
						writer.flush();
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static int calculateFromString(String message) throws NumberFormatException, IllegalOperationException {
		String[] messageParts = message.split(" ");
		if (messageParts.length != 3) {
			throw new IllegalOperationException("Too many arguments");
		}
		String operation = messageParts[0];
		int num1 = Integer.parseInt(messageParts[1]);
		int num2 = Integer.parseInt(messageParts[2]);

		return switch (operation) {
			case "ADD" -> num1 + num2;
			case "SUB" -> num1 - num2;
			case "MUL" -> num1 * num2;
			default -> throw new IllegalOperationException("Unknown operation: " + operation);
		};
	}

	private static class IllegalOperationException extends IllegalArgumentException {
		public IllegalOperationException(String message) {
			super(message);
		}
	}
}