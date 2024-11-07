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
					- ADD
					- SUB
					- MUL
					
					Please use the operations with the following syntax :
					OPERATION num1 num2
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
					System.out.println("Client connected");  // LOG
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
					String clientResponse;
					while (!(clientResponse = reader.readLine()).equals(EXIT_CODE)) {
						// Process client input and send result to client
						try {
							writer.write(Integer.toString(calculateFromString(clientResponse)) + '\n');
							writer.flush();
						} catch (NumberFormatException e) {
							System.out.println("Server received an invalid number.");  // LOG
							writer.write(ERROR_NUMBER_FORMAT_CODE + '\n');
							writer.flush();
						} catch (IllegalOperationException e) {
							System.out.println("Server received an invalid operation.");  // LOG
							writer.write(ERROR_UNKNOWN_OPERATION_CODE + '\n');
							writer.flush();
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static int calculateFromString(String message) throws NumberFormatException, IllegalOperationException {
		String[] messageParts = message.split(" ");
		String operation = messageParts[0].toUpperCase();

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