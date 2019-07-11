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

	public static int[][][] receiveQuantificationArray(PublicKey PK, BigInteger sk, String client_ip, String client_port) throws Exception {

		Paillier p = new Paillier();

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
				for (int i = 0; i < temp.length; i++) {
					for (int m = 0; m < 3; m++) {
						for (int j = 0; j < 64; j++) {
							BigInteger res = p.De(PK, sk, temp[i][m][j]);
//							System.out.println("res = " + res);
							if (res.compareTo(PK.n.divide(BigInteger.TWO)) == 1) {
								res = res.subtract(PK.n);
							}
//							System.out.println("res_plain = " + res);
							quanArr[i][m][j] = res.intValue();
						}
					}
				}
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
