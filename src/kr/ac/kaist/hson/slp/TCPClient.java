package kr.ac.kaist.hson.slp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClient {
	public static void main(String[] args) {
		String sentence;
		String modifiedSentence;
		Socket clientSocket;
		try {
			clientSocket = new Socket("localhost", 427);
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			sentence = "Hello";
			long before = System.currentTimeMillis();
			outToServer.writeBytes(sentence + '\n');
			modifiedSentence = inFromServer.readLine();
			System.out.println("FROM SERVER: " + modifiedSentence);
			long after = System.currentTimeMillis();
			System.out.println("[Elapsed Time]: "+(after-before));
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
