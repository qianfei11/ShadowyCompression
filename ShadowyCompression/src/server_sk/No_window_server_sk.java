package server_sk;

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
import java.util.Scanner;

import supplement.Paillier;
import supplement.PublicKey;

public class No_window_server_sk {
	static PublicKey PK;
	static BigInteger sk;
	static BigInteger[][] EI1;
	static BigInteger[][][] dctArr;
	static BigInteger[][][] quanArr;
	static String server_sk_port;
	static String server_pk_port;
	static String server_sk_ip;
	static String server_pk_ip;
	static int idx;
	static Paillier p;

	public static void main(String[] args) throws Exception {

		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";

		Scanner in = new Scanner(System.in);
		System.out.println("Please input Server PK's ip(default 127.0.0.1):");
		server_pk_ip = in.nextLine();
		System.out.println("Please input Server PK's port(default 44444):");
		server_pk_port = in.nextLine();
		System.out.println("Please input Server SK's ip(default 127.0.0.1):");
		server_sk_ip = in.nextLine();
		System.out.println("Please input Server SK's port(default 55555):");
		server_sk_port = in.nextLine();
		in.close();

		idx = 0;
		p = new Paillier();

		ServerSocket server = new ServerSocket(Integer.valueOf(server_sk_port));

		while (true) {
			Socket s = server.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
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

				System.out.println("[*] Receiving PK and SK from Client...");
				obj = is.readObject();
				PK = (PublicKey) obj;
				obj = is.readObject();
				sk = (BigInteger) obj;
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
				System.out.println("[*] Receiving DCT Array from Server PK and start Quanlification...");

				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

//				BigInteger val = PK.n.divide(BigInteger.TWO);
				BigInteger val = new BigInteger("1267650600228229401496703205376"); // 2**100

				System.out.println("[+] n = " + PK.n);

				Object obj = is.readObject();
				int len = (int) obj;
				dctArr = new BigInteger[len][3][64];
				for (int i = 0; i < dctArr.length; i++) {
					for (int m = 0; m < 3; m++) {
						for (int j = 0; j < 64; j++) {

//							System.out.println("Receive " + i + " " + m + " " + j);

							BigInteger rex = new BigInteger(br.readLine());
							BigInteger rx = p.De(PK, sk, rex);
							rx = rx.divide(new BigInteger("1000000000000"));

							boolean sig = false;
							if (rx.compareTo(val) == 1) {
								sig = true;
								bw.write("true");
								bw.newLine();
								bw.flush();
							} else {
								bw.write("false");
								bw.newLine();
								bw.flush();
							}

							BigInteger remain = BigInteger.ZERO;
							BigInteger res = null;

							if (sig == true) {

								for (int l = 0; l < 2048 / 16; l++) {
									Boolean[] judgement = new Boolean[16];
									for (int k = 0; k < 16; k++) {
										judgement[k] = false;
									}

									int idx = 0;

									for (int k = 0; k < 16; k++) {
										rex = new BigInteger(br.readLine());
										rx = p.De(PK, sk, rex);

//										if (rx.compareTo(val) == 1) {
//											System.out.println("rx" + j + " = " + rx.subtract(PK.n));
//										} else {
//											System.out.println("rx" + j + " = " + rx);
//										}

										if (rx.compareTo(BigInteger.ZERO) == 0) {
											judgement[k] = false;
											remain = rx;
											continue;
										}

										if (rx.compareTo(val) == -1) {
											judgement[k] = true;
											idx = k;
//											System.out.println("idx = " + idx);

											for (int h = 0; h < 15 - idx; h++) {
												br.readLine();
											}

											break;
										} else {
											judgement[k] = false;
											remain = rx;
											continue;
										}

									}

									if (judgement[idx] == true) {
										bw.write("yes");
										bw.newLine();
										bw.flush();

										res = new BigInteger(String.valueOf(16 * l + idx - 1));
										res = p.En(PK, res);

//										System.out.println("write res: " + p.De(PK, sk, res));
										bw.write(res.toString());
										bw.newLine();
										bw.flush();
										break;
									} else {
										bw.write("no");
										bw.newLine();
										bw.flush();
										continue;
									}
								}

							} else {

								for (int l = 0; l < 2048 / 16; l++) {
									Boolean[] judgement = new Boolean[16];
									for (int k = 0; k < 16; k++) {
										judgement[k] = false;
									}

									int idx = 0;

									for (int k = 0; k < 16; k++) {
										rex = new BigInteger(br.readLine());
										rx = p.De(PK, sk, rex);

//										if (rx.compareTo(val) == 1) {
//											System.out.println("rx" + j + " = " + rx.subtract(PK.n));
//										} else {
//											System.out.println("rx" + j + " = " + rx);
//										}

										if (rx.compareTo(val) == 1) {
											judgement[k] = true;
											idx = k;
//											System.out.println("idx = " + idx);

											for (int h = 0; h < 15 - idx; h++) {
												br.readLine();
											}

											break;
										} else {
											judgement[k] = false;
											remain = rx;
											continue;
										}
									}

									if (judgement[idx] == true) {
										bw.write("yes");
										bw.newLine();
										bw.flush();

										res = new BigInteger(String.valueOf(16 * l + idx - 1));
										res = p.En(PK, res);

//										System.out.println("write res: " + p.De(PK, sk, res));
										bw.write(res.toString());
										bw.newLine();
										bw.flush();
										break;
									} else {
										bw.write("no");
										bw.newLine();
										bw.flush();
										continue;
									}
								}
							}

							if (remain.compareTo(val) == 1) {
								remain = remain.subtract(PK.n);
							}
//							System.out.println("remain = " + remain);
							bw.write(remain.toString());
							bw.newLine();
							bw.flush();
						}
					}
				}
				System.out.println("[*] compeleted!");

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
