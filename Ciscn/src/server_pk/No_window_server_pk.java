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
	static PublicKey PK;
	static int m;
	static int idx;
	static int idx2;
	static Paillier p;

	public static void main(String[] args) throws Exception {

		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";

		p = new Paillier();

		ServerSocket server = new ServerSocket(Integer.valueOf(server_pk_port));
		while (true) {
			Socket s = server.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
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

				System.out.println("[*] Receiving PK from Client...");
				obj = is.readObject();
				PK = (PublicKey) obj;
				System.out.println("[*] Receiving Compeleted!");
			}

			if (cmd == 1) {
				System.out.println("[*] Receiving EI1 from Server SK...");
				Object obj = is.readObject();
				EI1 = (BigInteger[][]) obj;
				obj = is.readObject();
				BigInteger sk = (BigInteger) obj;
				System.out.println("[*] I1 was received from Server SK successfully!");

				EI = GetEI.calEI(EI1, EI2, PK, sk);

				long spilitStartTime = System.currentTimeMillis();
				Images = Spilit.spilitBMP(EI);
				long spilitEndTime = System.currentTimeMillis();
				System.out.println("[*] Spilit takes " + (spilitEndTime - spilitStartTime) + "ms");

				long convertStartTime = System.currentTimeMillis();
				YUVarr = convertTo1D(ConvertColorspace.convertRGB2YUV(Images, PK, sk));
				long convertEndTime = System.currentTimeMillis();
				System.out.println("[*] Convert colorspace takes " + (convertEndTime - convertStartTime) + "ms");

				long dctStartTime = System.currentTimeMillis();
				System.out.println("[*] Start DCT...");
				dctArr = new BigInteger[YUVarr.length][3][64];
				for (int i = 0; i < YUVarr.length; i++) {
					for (int m = 0; m < 3; m++) {
//						dctArr[i][m] = ForwardDCT.forwardDCT(YUVarr[i][m]);
						dctArr[i][m] = ForwardDCT.forwardDCT(YUVarr[i][m], PK, sk);
					}
				}
//				for (int i = 0; i < dctArr.length; i++) {
//					printArray(dctArr[i]);
//				}
				System.out.println("[*] Finish DCT!");
				long dctEndTime = System.currentTimeMillis();
				System.out.println("[*] DCT takes " + (dctEndTime - dctStartTime) + "ms");

				long quanlificationStartTime = System.currentTimeMillis();
				System.out.println("[*] Sending DCT Array to Server SK and start Quanlification...");
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
				temp.close();

				quanArr = new BigInteger[dctArr.length][3][64];
				Random r = new Random();
				BigInteger rData = null;

				for (int i = 0; i < dctArr.length; i++) {
					for (int m = 0; m < 3; m++) {
						for (int j = 0; j < 64; j++) {
//							Thread.sleep(10);

							int rd = r.nextInt(Integer.MAX_VALUE);
//							rd = 1;

							BigInteger src = dctArr[i][m][j];
//							BigInteger src = p.En(PK, dctArr[i][m][j]);
//							src = p.cipher_mul(PK, src, new BigInteger("1000000000000"));
							rData = p.cipher_mul(PK, src, new BigInteger(String.valueOf(rd)));

							System.out.println("Send " + i + " " + m + " " + j);

							bw.write(rData.toString());
							bw.newLine();
							bw.flush();

							boolean sig = Boolean.parseBoolean(br.readLine());

							BigInteger data = null;
							BigInteger n = BigInteger.ONE;
							String flag = "";

							BigInteger res = null;
							BigInteger remain = null;

							for (int l = 0; l < 2048 / 64; l++) {
								rd = r.nextInt(Integer.MAX_VALUE);
//								rd = 1;
								for (int k = 0; k < 64; k++) {
									n = new BigInteger(String.valueOf(64 * l + k));
									data = Quantification.calculate(src, n, m, j, sig, PK);
									rData = p.cipher_mul(PK, data, new BigInteger(String.valueOf(rd)));

//									System.out.println("send " + p.De(PK, sk, data));
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
//							System.out.println("remain = " + remain);

							if (Quantification.judge(remain, m, j)) {
								res = p.cipher_add(PK, res, p.En(PK, BigInteger.ONE));
							}

							if (sig) {
//								System.out.println("res = " + p.De(PK, sk, res));
								res = p.cipher_mul(PK, res, new BigInteger("-1"));
//								System.out.println("res_sig = " + p.De(PK, sk, res).subtract(PK.n));
							}

							quanArr[i][m][j] = res;
						}
					}
				}
				long quanlificationEndTime = System.currentTimeMillis();
				System.out.println(
						"[*] Quanlification takes " + (quanlificationEndTime - quanlificationStartTime) + "ms");

				System.out.println("[*] Sending Quantification Array to Client...");
//				for (int i = 0; i < quanArr.length; i++) {
//					printArray(quanArr[i]);
//				}

//				for (int i = 0; i < quanArr.length; i++) {
//					for (int m = 0; m < 3; m++) {
//						for (int j = 0; j < 64; j++) {
//							if (p.De(PK, sk, quanArr[i][m][j]).compareTo(BigInteger.ZERO) == 0) {
//								System.out.print("0 ");
//							} else if (p.De(PK, sk, quanArr[i][m][j]).compareTo(PK.n.divide(BigInteger.TWO)) == 1){
//								System.out.print(p.De(PK, sk, quanArr[i][m][j]).subtract(PK.n) + " ");
//							} else {
//								System.out.print(p.De(PK, sk, quanArr[i][m][j]) + " ");
//							}
//						}
//						System.out.println();
//					}
//					System.out.println();
//				}

				BufferedWriter bw_c = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

				for (int i = 0; i < quanArr.length; i++) {
					for (int m = 0; m < 3; m++) {
						for (int j = 0; j < 64; j++) {
							bw_c.write(quanArr[i][m][j].toString());
							bw_c.newLine();
							bw_c.flush();
						}
					}
				}
				System.out.println("[*] Quantification Array was sent to Client successfully!");

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
