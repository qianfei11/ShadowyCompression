package client;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class Receive {
	
	static BigInteger[][][] quanArr;
	static String client_ip;
	static String client_port;
	
	public static BigInteger[][][] receiveQuantificationArray() throws Exception {
		client_ip = "127.0.0.1";
		client_port = "33333";
		
		ServerSocket server = new ServerSocket(Integer.valueOf(client_port));
		
		while (true) {
			Socket socket = server.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			int cmd = is.readInt();
			if(cmd == 0) {
				System.out.println("[*] Receiving Quantification Array from Server PK...");
				Object obj = is.readObject();
				quanArr = (BigInteger[][][]) obj;
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
