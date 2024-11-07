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
	private static final String welcomeMsg =
			"""
			Welcome to this remote calculator.
			The currently supported operations are
			- ADD a b
			- SUB a b
			- MUL a b
			- BYE
			""";


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
					System.out.println("Client connected \n");  // LOG
					var writer = new BufferedWriter(new OutputStreamWriter(
						client.getOutputStream(),
						StandardCharsets.UTF_8
					));
					BufferedReader reader = new BufferedReader(new InputStreamReader(
						client.getInputStream(),
						StandardCharsets.UTF_8
					));

					// Send welcome message
					writer.write(String.format("%d\n%s", 8, welcomeMsg));
					writer.flush();

					// Wait for user input
					String clientRequest;
					while (true) {
						clientRequest = reader.readLine().toUpperCase();
						System.out.println("Client requests : " + clientRequest);  // LOG

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
						serverResponse += '\n';
						System.out.println("Server responds : " + serverResponse);  // LOG
						writer.write(serverResponse);
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