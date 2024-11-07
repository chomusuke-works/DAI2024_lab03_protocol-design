package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {
	final String SERVER_ADDRESS = "localhost";
	final int SERVER_PORT = 25565;

	final String EXIT_CODE = "BYE";

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

			int lineCount = Integer.parseInt(is.readLine());

			for (int i = 0; i < lineCount; ++i) {
				System.out.printf("%s\n", is.readLine());
			}

			Scanner scanner = new Scanner(System.in);

			String input;

			while (!(input = scanner.nextLine()).equals(EXIT_CODE)) {
				os.write(input + '\n');
				os.flush();

				System.out.println(is.readLine());
			}

			is.close();
			os.close();
		} catch (IOException e) {
			System.err.println("Exception was handled");
			System.err.println(e.getMessage());
		}
	}
}