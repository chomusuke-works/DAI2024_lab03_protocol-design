package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
	final int SERVER_PORT = 1234;
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
		try (ServerSocket serverSocket = new ServerSocket(25565)) {  // TODO Replace 25565
			System.out.println("Server is listening on port " + 25565);

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
					writer.write(welcomeMsg);
					writer.flush();

					// Wait for user input
					String clientResponse;
					clientResponse = reader.readLine();  // Only one line is expected

					// Process client input and send result to client
					try {
						writer.write(Integer.toString(calculateFromString(clientResponse)));
						writer.flush();
					} catch (NumberFormatException e) {
						System.out.println("Server received an invalid number.");  // LOG
						writer.write("Sever received an invalid number.");  // TODO Specs
						writer.flush();
					} catch (IllegalStateException e) {
						System.out.println("Server received an invalid operation.");  // LOG
						writer.write("Server received an invalid operation.");  // TODO Specs
						writer.flush();
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static int calculateFromString(String message) {
		String[] messageParts = message.split(" ");
		String operation = messageParts[0].toUpperCase();

		try {
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