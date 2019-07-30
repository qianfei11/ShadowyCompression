package client;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

import supplement.Paillier;
import supplement.PublicKey;

public class Receive {

	static int[][][] quanArr;
	static String client_ip;
	static String client_port;

	public static int[][][] receiveQuantificationArray(PublicKey PK, BigInteger sk) throws Exception {

		Paillier p = new Paillier();

		client_ip = "127.0.0.1";
		client_port = "33333";

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
				BigInteger[][][] temp = (BigInteger[][][]) obj;
				
//				for(int i = 0; i < len; i++) {
//					for (int j = 0; j < 3; j++) {
//						for (int k = 0; k < 64; k++ ) {
//							System.out.print(temp[i][j][k] + " ");
//						}
//						System.out.println();
//					}
//					System.out.println();
//				}

				BigInteger val = new BigInteger("1267650600228229401496703205376"); // 2**100

				for (int i = 0; i < temp.length; i++) {
					for (int m = 0; m < 3; m++) {
						for (int j = 0; j < 64; j++) {
							BigInteger res = p.De(PK, sk, temp[i][m][j]);
							if (res.compareTo(val) == 1) {
								res = res.subtract(PK.n);
							}
							quanArr[i][m][j] = res.intValue();
						}
					}
				}
				System.out.println("[*] Quantification Array was received from Server SK succcessfully!");
				server.close();
				break;
			}
		}

		return quanArr;
	}
}
