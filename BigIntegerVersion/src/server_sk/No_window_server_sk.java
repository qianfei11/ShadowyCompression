package server_sk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class No_window_server_sk {
	static BigInteger[][] EI1;
	static BigInteger[][][] dctArr;
	static BigInteger[][][] quanArr;
	static String server_sk_port;
	static String server_pk_port;
	static String server_sk_ip;
	static String server_pk_ip;

	public static void main(String[] args) throws NumberFormatException, IOException, ClassNotFoundException {

		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";
		ServerSocket server = new ServerSocket(Integer.valueOf(server_sk_port));

		while (true) {
			Socket socket = server.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			int cmd = is.readInt();
			if (cmd == 0) {
				System.out.println("[*] Receiving EI1 from Client...");
				Object obj = is.readObject();
				EI1 = (BigInteger[][]) obj;
//				for (int i = 0; i < EI1.length; i++) {
//					for (int j = 0; j < EI1[0].length; j++) {
//						System.out.print(EI1[i][j] + " ");
//					}
//					System.out.println();
//				}
//				System.out.println();
				System.out.println("[*] Receiving Compeleted!");
				System.out.println("[*] Sending EI1 to Server PK...");
				Socket temp = new Socket(server_pk_ip, Integer.valueOf(server_pk_port));
				ObjectOutputStream temp_os = new ObjectOutputStream(temp.getOutputStream());
				temp_os.writeInt(1);
				temp_os.flush();
				temp_os.writeObject(EI1);
				temp_os.flush();
				temp.close();
				System.out.println("[*] EI1 was sent to Server PK successfully!");
			}
			if (cmd == 1) {
				System.out.println("[*] Receiving DCT Array from Server PK");
				Object obj = is.readObject();
				dctArr = (BigInteger[][][]) obj;
//				for (int i = 0; i < dctArr.length; i++) {
//					printArray(dctArr[i]);
//				}
				System.out.println("[*] Receiving compeleted!");
				System.out.println("[*] Start Quantification...");
				int quality_scale = 50;
				Quantification.initQualityTables(quality_scale);
				quanArr = new BigInteger[dctArr.length][3][];
				for (int i = 0; i < dctArr.length; i++) {
					for (int m = 0; m < 3; m++) {
						quanArr[i][m] = Quantification.quantizeBlock(dctArr[i][m], m);
					}
				}
//				for (int i = 0; i < quanArr.length; i++) {
//					printArray(quanArr[i]);
//				}
				System.out.println("[*] Quantification finished successfully!");
				System.out.println("[*] Sending Quantification Array to Server PK...");
				Socket temp = new Socket(server_pk_ip, Integer.valueOf(server_pk_port));
				ObjectOutputStream temp_os = new ObjectOutputStream(temp.getOutputStream());
				temp_os.writeInt(2);
				temp_os.flush();
				temp_os.writeObject(quanArr);
				temp_os.flush();
				temp_os.writeObject(quality_scale);
				temp_os.flush();
				temp.close();
				System.out.println("[*] Quantification Array was sent to Server PK successfully!");
			}
			if (cmd == 10) {
				System.out.println("[*] Closing Server SK...");
				server.close();
				System.out.println("[*] Server SK closed successfully!");
			}
		}
	}

	public static void printArray(BigInteger[][][] F) {
		int X = F[0].length;
		int Y = F[0][0].length;
		for (int m = 0; m < 3; m++) {
			for (int y = 0; y < Y; y++) {
				for (int x = 0; x < X; x++) {
					System.out.print(F[m][x][y] + "  ");
					System.out.print("\t");
				}
				System.out.println("");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	public static void printArray(BigInteger[][] F) {
		int X = F[0].length;
		for (int m = 0; m < 3; m++) {
			for (int x = 0; x < X; x++) {
				System.out.print(F[m][x] + "  ");
				System.out.print("\t");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	public static void printArray(int[][] F) {
		int X = F[0].length;
		for (int m = 0; m < 3; m++) {
			for (int x = 0; x < X; x++) {
				System.out.print(F[m][x] + "  ");
				System.out.print("\t");
			}
			System.out.println("");
		}
		System.out.println("");
	}
}