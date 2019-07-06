package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;

import supplement.EncryptArray;
import supplement.PublicKey;

public class Upload {

	public static Boolean upload(String server_pk_ip, String server_pk_port, String server_sk_ip, String server_sk_port,
			PublicKey PK, BigInteger sk, int[][] I) throws IOException {
		long starttime = System.currentTimeMillis();

		EasyDispose ed = new EasyDispose(I);

		BigInteger[][] I1 = new BigInteger[ed.I1.length][ed.I1[0].length];
		BigInteger[][] I2 = new BigInteger[ed.I2.length][ed.I2[0].length];

//		System.out.println("I1:");
		for (int i = 0; i < I1.length; i++) {
			for (int j = 0; j < I1[0].length; j++) {
				I1[i][j] = new BigInteger(String.valueOf(ed.I1[i][j]));
//				System.out.print(I1[i][j] + " ");
			}
//			System.out.println();
		}

//		System.out.println("I2:");
		for (int i = 0; i < I2.length; i++) {
			for (int j = 0; j < I2[0].length; j++) {
				I2[i][j] = new BigInteger(String.valueOf(ed.I2[i][j]));
//				System.out.print(I2[i][j] + " ");
			}
//			System.out.println();
		}
//		System.out.println();

		BigInteger[][] EI1 = new BigInteger[I1.length][I1[0].length];
		BigInteger[][] EI2 = new BigInteger[I2.length][I2[0].length];

		EI1 = EncryptArray.encrypt(PK, I1);
		EI2 = EncryptArray.encrypt(PK, I2);

		Socket socket = null;
		ObjectOutputStream os = null;
		try {
			socket = new Socket(server_sk_ip, Integer.valueOf(server_sk_port));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		os = new ObjectOutputStream(socket.getOutputStream());

		os.writeInt(0);
		os.flush();
		os.writeObject(EI1);
		os.flush();
		os.writeObject(PK);
		os.flush();
		os.writeObject(sk);
		os.flush();
		os.close();
		socket.close();

		try {
			socket = new Socket(server_pk_ip, Integer.valueOf(server_pk_port));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		os = new ObjectOutputStream(socket.getOutputStream());
		os.writeInt(0);
		os.flush();
		os.writeObject(EI2);
		os.flush();
		os.writeObject(PK);
		os.flush();
		os.close();
		socket.close();

		long endtime = System.currentTimeMillis();
		System.out.println("[*] Client spent " + (endtime - starttime) / 1000.0 + " seconds.");

		return true;
	}

}