package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Upload {

	public static Boolean upload(String server_pk_ip, String server_pk_port, String server_sk_ip, String server_sk_port,
			int[][] I) throws IOException {

		EasyDispose ed = new EasyDispose(I);
		int[][] I1 = ed.I1;
		int[][] I2 = ed.I2;

//		System.out.println("I1:");
//		for (int i = 0; i < I1.length; i++) {
//			for (int j = 0; j < I1[0].length; j++) {
//				System.out.print(I1[i][j] + " ");
//			}
//			System.out.println();
//		}

//		System.out.println("I2:");
//		for (int i = 0; i < I2.length; i++) {
//			for (int j = 0; j < I2[0].length; j++) {
//				System.out.print(I2[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println();

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
		os.writeObject(I1);
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
		os.writeObject(I2);
		os.flush();
		os.close();
		socket.close();

		return true;
	}

}
