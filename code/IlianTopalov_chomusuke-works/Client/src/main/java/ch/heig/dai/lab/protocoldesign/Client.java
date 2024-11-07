package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
	final String SERVER_ADDRESS = "localhost";
	final int SERVER_PORT = 25565;

	static final String EXIT_CODE = "BYE";
	static final String ERROR_UNKNOWN_OPERATION_CODE = "EUO";
	static final String ERROR_NUMBER_FORMAT_CODE = "ENF";

	public static void main(String[] args) {
		// Create a new client and run it
		Client client = new Client();
		client.run();
	}

	private void run() {
		try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
			var is = new BufferedReader(new InputStreamReader(
				socket.getInputStream(),
				StandardCharsets.UTF_8
			));

			var os = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream(),
				StandardCharsets.UTF_8
			));

			Scanner scanner = new Scanner(System.in);

			int lineCount = Integer.parseInt(is.readLine());

			for (int i = 0; i < lineCount; ++i) {
				System.out.printf("%s\n", is.readLine());
			}

			String input;

			while (true) {
				input = scanner.nextLine();

				os.write(input + '\n');
				os.flush();

				if (input.equalsIgnoreCase(EXIT_CODE)) {
					break;
				}

				System.out.println(is.readLine() + '\n');
			}

			is.close();
			os.close();
		} catch (IOException e) {
			System.err.println("Exception was handled");
			System.err.println(e.getMessage());
		}
	}
}