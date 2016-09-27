package kr.ac.kaist.hson.slp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerTest {
	public static void main(String[] args) {
		try {
			new TcpServerThread();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * TCP server thread.
	 */
	private static final class TcpServerThread extends Thread {
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket;

		/**
		 * creates and starts a new TCP server thread.
		 * 
		 * @throws IOException
		 *             if socket creation fails.
		 */
		public TcpServerThread() throws IOException {
			welcomeSocket = new ServerSocket(427);
			start();
		}

		/**
		 * thread loop.
		 */
		public void run() {
			while (true) {
				try {
					Socket connectionSocket = welcomeSocket.accept();
					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(
									connectionSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(
							connectionSocket.getOutputStream());
					clientSentence = inFromClient.readLine();
					System.out.println("Received: " + clientSentence);
					capitalizedSentence = clientSentence.toUpperCase() + '\n';
					outToClient.writeBytes(capitalizedSentence);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
