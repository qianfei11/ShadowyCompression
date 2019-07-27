package server_pk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import supplement.Paillier;
import supplement.PublicKey;

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
	static PublicKey PK;
	static int m;
	static int idx;
	static int idx2;
	static Paillier p;

	public static void main(String[] args) throws Exception {

		System.out.println(">>> Server PK started! <<<");

		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";

		Scanner in = new Scanner(System.in);
//		System.out.println("Please input Server PK's ip(default 127.0.0.1):");
//		server_pk_ip = in.nextLine();
//		System.out.println("Please input Server PK's port(default 44444):");
//		server_pk_port = in.nextLine();
//		System.out.println("Please input Server SK's ip(default 127.0.0.1):");
//		server_sk_ip = in.nextLine();
//		System.out.println("Please input Server SK's port(default 55555):");
//		server_sk_port = in.nextLine();
		in.close();

		p = new Paillier();

		client_ip = "127.0.0.1";
		client_port = "33333";

		ServerSocket server = new ServerSocket(Integer.valueOf(server_pk_port));
		while (true) {
			Socket socket = server.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			int cmd = is.readInt();
			if (cmd == 0) {
				System.out.println(">>> Receiving EI2 from Client... <<<");
				Object obj = is.readObject();
				EI2 = (BigInteger[][]) obj;
				System.out.println(">>> Receiving Compeleted! <<<");

				System.out.println(">>> Receiving PK from Client... <<<");
				obj = is.readObject();
				PK = (PublicKey) obj;
				System.out.println(">>> Receiving Compeleted! <<<");
			}

			if (cmd == 1) {
				System.out.println(">>> Receiving EI1 from Server SK... <<<");
				Object obj = is.readObject();
				EI1 = (BigInteger[][]) obj;
				System.out.println(">>> I1 was received from Server SK successfully! <<<");

				System.out.println(">>> Start calculate... <<<");
				long calculateStartTime = System.currentTimeMillis();
				EI = GetEI.calEI(EI1, EI2, PK);
				long calculateEndTime = System.currentTimeMillis();
				System.out.println("[*] Calculating takes " + (calculateEndTime - calculateStartTime) + "ms");

				System.out.println(">>> Start spilit bmp file... <<<");
				long spilitStartTime = System.currentTimeMillis();
				Images = Spilit.spilitBMP(EI);
				long spilitEndTime = System.currentTimeMillis();
				System.out.println("[*] Spilit takes " + (spilitEndTime - spilitStartTime) + "ms");

				System.out.println(">>> Start convert colorspace... <<<");
				long convertStartTime = System.currentTimeMillis();
				YUVarr = convertTo1D(ConvertColorspace.convertRGB2YUV(Images, PK));
				long convertEndTime = System.currentTimeMillis();
				System.out.println("[*] Convert colorspace takes " + (convertEndTime - convertStartTime) + "ms");

				System.out.println(">>> Start DCT... <<<");
				long dctStartTime = System.currentTimeMillis();
				dctArr = new BigInteger[YUVarr.length][3][64];
				for (int i = 0; i < YUVarr.length; i++) {
					for (int m = 0; m < 3; m++) {
						dctArr[i][m] = ForwardDCT.forwardDCT(YUVarr[i][m], PK);
					}
				}
				long dctEndTime = System.currentTimeMillis();
				System.out.println("[*] DCT takes " + (dctEndTime - dctStartTime) + "ms");

				long quantificationStartTime = System.currentTimeMillis();
				System.out.println(">>> Sending DCT Array to Server SK and start Quantification... <<<");
				int quality_scale = 50;
				Quantification.initQualityTables(quality_scale);

				Socket temp = new Socket(server_sk_ip, Integer.valueOf(server_sk_port));
				ObjectOutputStream temp_os = new ObjectOutputStream(temp.getOutputStream());

				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(temp.getOutputStream()));
				BufferedReader br = new BufferedReader(new InputStreamReader(temp.getInputStream()));

				temp_os.writeInt(1);
				temp_os.flush();
				temp_os.writeObject(dctArr.length);
				temp_os.flush();

				quanArr = new BigInteger[dctArr.length][3][64];
//				System.out.println("[+] Delay time = " + (dctArr.length * 3 * 64 * 5) + "ms");
				Random r = new Random();
				BigInteger rData = null;

				for (int i = 0; i < dctArr.length; i++) {
					for (int m = 0; m < 3; m++) {
						for (int j = 0; j < 64; j++) {
							Thread.sleep(5);

							int rd = r.nextInt(Integer.MAX_VALUE);

							BigInteger src = dctArr[i][m][j];
							rData = p.cipher_mul(PK, src, new BigInteger(String.valueOf(rd)));

							bw.write(rData.toString());
							bw.newLine();
							bw.flush();

							boolean sig = Boolean.parseBoolean(br.readLine());

							BigInteger data = null;
							BigInteger n = BigInteger.ONE;
							String flag = "";

							BigInteger res = null;
							BigInteger remain = BigInteger.ZERO;

							for (int l = 0; l < 2048 / 16; l++) {
								rd = r.nextInt(Integer.MAX_VALUE);
								for (int k = 0; k < 16; k++) {
									n = new BigInteger(String.valueOf(16 * l + k));
									data = Quantification.calculate(src, n, m, j, sig, PK);
									rData = p.cipher_mul(PK, data, new BigInteger(String.valueOf(rd)));

									bw.write(rData.toString());
									bw.newLine();
									bw.flush();
								}

								flag = br.readLine();

								if (flag.equals("yes")) {
									res = new BigInteger(br.readLine());
									remain = new BigInteger(br.readLine());
									break;
								} else {
									continue;
								}

							}

							remain = remain.divide(new BigInteger("1000000000000"));
							remain = remain.divide(new BigInteger(String.valueOf(rd)));

							if (Quantification.judge(remain, m, j)) {
								res = p.cipher_add(PK, res, p.En(PK, BigInteger.ONE));
							}

							if (sig) {
								res = p.cipher_mul(PK, res, new BigInteger("-1"));
							}

							quanArr[i][m][j] = res;
						}
					}
				}
				temp.close();
				long quantificationEndTime = System.currentTimeMillis();
				System.out.println(
						"[*] Quantification takes " + (quantificationEndTime - quantificationStartTime) + "ms");

				Thread.sleep(100);
				System.out.println(">>> Sending Quantification Array and some infos to Client... <<<");
				temp = new Socket(client_ip, Integer.valueOf(client_port));
				temp_os = new ObjectOutputStream(temp.getOutputStream());
				temp_os.writeInt(0);
				temp_os.flush();
				temp_os.writeObject(quanArr.length);
				temp_os.flush();
				temp_os.writeObject(quanArr);
				temp_os.flush();
				temp.close();
				System.out.println(">>> Quantification Array and some infos was sent to Client successfully! <<<");
			}

			if (cmd == 10) {
				server.close();
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
}
