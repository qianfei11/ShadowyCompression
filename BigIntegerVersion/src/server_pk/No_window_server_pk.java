package server_pk;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class No_window_server_pk {
	static BigInteger[][] EI2;
	static BigInteger[][] EI1;
	static BigInteger[][] EI;
	static BigInteger[][][][] Images;
	static BigInteger[][][] YUVarr;
	static BigInteger[][][] dctArr;
	static BigInteger[][][] quanArr;
	static int quality_scale;
	static int width;
	static int height;
	static String server_sk_port;
	static String server_pk_port;
	static String server_sk_ip;
	static String server_pk_ip;
	static String client_ip;
	static String client_port;

	public static void main(String[] args) throws Exception {

		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";
		client_ip = "127.0.0.1";
		client_port = "33333";

		ServerSocket server = new ServerSocket(Integer.valueOf(server_pk_port));
		while (true) {
			Socket socket = server.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			int cmd = is.readInt();
			if (cmd == 0) {
				System.out.println("[*] Receiving EI2 from Client...");
				Object obj = is.readObject();
				EI2 = (BigInteger[][]) obj;
//				for (int i = 0; i < EI2.length; i++) {
//					for (int j = 0; j < EI2[0].length; j++) {
//						System.out.print(EI2[i][j] + " ");
//					}
//					System.out.println();
//				}
//				System.out.println();
				System.out.println("[*] Receiving Compeleted!");
			}
			if (cmd == 1) {
				System.out.println("[*] Receiving EI1 from Server SK...");
				Object obj = is.readObject();
				EI1 = (BigInteger[][]) obj;
				System.out.println("[*] I1 was received from Server PK successfully!");
				EI = GetEI.calEI(EI1, EI2);
				Images = Spilit.spilitBMP(EI);
				YUVarr = convertTo1D(ConvertColorspace.convertRGB2YUV(Images));
				System.out.println("[*] Start DCT...");
				dctArr = new BigInteger[YUVarr.length][3][];
				for (int i = 0; i < YUVarr.length; i++) {
					for (int m = 0; m < 3; m++) {
						dctArr[i][m] = ForwardDCT.forwardDCT(YUVarr[i][m]);
					}
				}
//				for (int i = 0; i < dctArr.length; i++) {
//					printArray(dctArr[i]);
//				}
				System.out.println("[*] Finish DCT!");
				System.out.println("[*] Sending DCT Array to Server SK...");
				Socket temp = new Socket(server_sk_ip, Integer.valueOf(server_sk_port));
				ObjectOutputStream temp_os = new ObjectOutputStream(temp.getOutputStream());
				temp_os.writeInt(1);
				temp_os.flush();
				temp_os.writeObject(dctArr);
				temp_os.flush();
				temp.close();
				System.out.println("[*] DCT Array was sent to Server SK successfully!");
			}
			if (cmd == 2) {
				System.out.println("[*] Receiving Quantification Array from Server SK...");
				Object obj = is.readObject();
				quanArr = (BigInteger[][][]) obj;
				obj = is.readObject();
				quality_scale = (int) obj;
//				for (int i = 0; i < quanArr.length; i++) {
//					printArray(quanArr[i]);
//				}
				System.out.println("[*] Quantification Array was received from Server SK successfully!");
				System.out.println("[*] Sending Quantification Array and some infos to Client...");
				Socket temp = new Socket(client_ip, Integer.valueOf(client_port));
				ObjectOutputStream temp_os = new ObjectOutputStream(temp.getOutputStream());
				temp_os.writeInt(0);
				temp_os.writeObject(quanArr);
				temp.close();
				System.out.println("[*] Quantification Array and some infos was sent to Client successfully!");
			}
			if (cmd == 10) {
				System.out.println("[*] Closing Server PK...");
				server.close();
				System.out.println("[*] Server PK closed successfully!");
			}
		}

	}

	public static BigInteger[][][] convertTo1D(BigInteger[][][][] arr) {
		BigInteger[][][] res = new BigInteger[arr.length][3][64];
		for (int i = 0; i < arr.length; i++) {
			for (int m = 0; m < 3; m++) {
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						res[i][m][8 * x + y] = arr[i][m][y][x];
					}
				}
			}
		}
		return res;
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
