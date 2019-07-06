package client;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receive {

	static int[][][] quanArr;

	public static int[][][] receiveQuantificationArray(int sum, String client_ip, String client_port) throws Exception {

		ServerSocket server = new ServerSocket(Integer.valueOf(client_port));

		while (true) {
			Socket socket = server.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			int cmd = is.readInt();
			if (cmd == 0) {
				System.out.println("[*] Receiving Quantification Array from Server PK...");
				Object obj = is.readObject();
				int len = (int) obj;
				quanArr = new int[len][3][64];
				obj = is.readObject();
				quanArr = (int[][][]) obj;
				System.out.println("[*] Quantification Array was received from Server SK succcessfully!");

				System.out.println("[*] Close Client socket...");
				server.close();
				System.out.println("[*] Client socket closed!");
				break;
			}
		}

		return quanArr;
	}
}
