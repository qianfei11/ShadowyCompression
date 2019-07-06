package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;

import supplement.Paillier;
import supplement.PublicKey;

public class Receive {

	static int[][][] quanArr;
	static String server_sk_port;
	static String server_pk_port;
	static String server_sk_ip;
	static String server_pk_ip;

	public static int[][][] receiveQuantificationArray(PublicKey PK, BigInteger sk, int sum) throws Exception {

		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";
		Paillier p = new Paillier();

		System.out.println("[*] Receiving Quantification Array from Server PK...");
		Socket s = new Socket(server_pk_ip, Integer.valueOf(server_pk_port));
		BufferedReader br_c = new BufferedReader(new InputStreamReader(s.getInputStream()));

		quanArr = new int[sum][3][64];
		BigInteger[][][] temp = new BigInteger[sum][3][64];
		for (int i = 0; i < sum; i++) {
			for (int m = 0; m < 3; m++) {
				for (int j = 0; j < 64; j++) {
					temp[i][m][j] = new BigInteger(br_c.readLine());
				}
			}
		}

		for (int i = 0; i < sum; i++) {
			for (int m = 0; m < 3; m++) {
				for (int j = 0; j < 64; j++) {
					BigInteger res = p.De(PK, sk, temp[i][m][j]);
//					System.out.println("res = " + res);
					if (res.compareTo(PK.n.divide(BigInteger.TWO)) == 1) {
						res = res.subtract(PK.n);
					}
//					System.out.println("res_plain = " + res);
					quanArr[i][m][j] = res.intValue();
				}
			}
		}
		s.close();
		System.out.println("[*] Complete!");

		return quanArr;
	}
}
