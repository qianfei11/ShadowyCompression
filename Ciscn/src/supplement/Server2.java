package supplement;

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

public class Server2 {

	static String ip1;
	static String port1;
	static String ip2;
	static String port2;

	static PublicKey PK;
	static BigInteger sk;
	static Paillier p;

	static BigInteger[] dctArr;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		ip1 = "127.0.0.1";
		port1 = "10001";
		ip2 = "127.0.0.1";
		port2 = "10002";

		p = new Paillier();

		ServerSocket ss = new ServerSocket(Integer.valueOf(port2));
		while (true) {
			Socket s = ss.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
			int cmd = is.readInt();
			if (cmd == 0) {
				System.out.println("[*] Init...");

				Object obj = is.readObject();
				PK = (PublicKey) obj;
				obj = is.readObject();
				sk = (BigInteger) obj;

				System.out.println("[*] Finished!");

				System.out.println("[*] Sending requests to Test...");
				Socket temp = new Socket(ip1, Integer.valueOf(port1));
				ObjectOutputStream temp_os = new ObjectOutputStream(temp.getOutputStream());
				temp_os.writeInt(1);
				temp_os.flush();
				System.out.println("[*] Finished!");
			}

			if (cmd == 1) {
				System.out.println("[*] Start judge...");

				System.out.println("n = " + PK.n);

				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

//				BigInteger val = PK.n.divide(BigInteger.TWO);
				BigInteger val = new BigInteger("1267650600228229401496703205376");

				dctArr = new BigInteger[64];
				for (int i = 0; i < 64; i++) {

					BigInteger rex = new BigInteger(br.readLine());
					BigInteger rx = p.De(PK, sk, rex);
					rx = rx.divide(new BigInteger("1000000000000"));

					boolean sig = false;
					if (rx.compareTo(val) == 1) {
						sig = true;
						rx = rx.subtract(PK.n);
						bw.write("true");
						bw.newLine();
						bw.flush();
					} else {
						bw.write("false");
						bw.newLine();
						bw.flush();
					}

//					System.out.println("num" + i + " " + rx + " " + sig);

					System.out.println("[*] Judging num " + i);
					BigInteger remain = BigInteger.ZERO;
					BigInteger res = null;

					if (sig == true) {

						for (int l = 0; l < 2048 / 64; l++) {
							Boolean[] judgement = new Boolean[64];
							for (int j = 0; j < 64; j++) {
								judgement[j] = false;
							}

							int idx = 0;

							for (int j = 0; j < 64; j++) {
								rex = new BigInteger(br.readLine());
								rx = p.De(PK, sk, rex);

//								if (rx.compareTo(val) == 1) {
//									System.out.println("rx" + j + " = " + rx.subtract(PK.n));
//								} else {
//									System.out.println("rx" + j + " = " + rx);
//								}

								if (rx.compareTo(BigInteger.ZERO) == 0) {
									judgement[j] = false;
									remain = rx;
									continue;
								}

								if (rx.compareTo(val) == -1) {
									judgement[j] = true;
									idx = j;
//									System.out.println("idx = " + idx);

									for (int k = 0; k < 63 - idx; k++) {
										br.readLine();
									}

									break;
								} else {
									judgement[j] = false;
									remain = rx;
									continue;
								}

							}

							if (judgement[idx] == true) {
								bw.write("yes");
								bw.newLine();
								bw.flush();

								res = new BigInteger(String.valueOf(64 * l + idx - 1));
								res = p.En(PK, res);

//								System.out.println("write res: " + p.De(PK, sk, res));
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

						for (int l = 0; l < 2048 / 64; l++) {
							Boolean[] judgement = new Boolean[64];
							for (int j = 0; j < 64; j++) {
								judgement[j] = false;
							}

							int idx = 0;

							for (int j = 0; j < 64; j++) {
								rex = new BigInteger(br.readLine());
								rx = p.De(PK, sk, rex);

//								if (rx.compareTo(val) == 1) {
//									System.out.println("rx" + j + " = " + rx.subtract(PK.n));
//								} else {
//									System.out.println("rx" + j + " = " + rx);
//								}

								if (rx.compareTo(val) == 1) {
									judgement[j] = true;
									idx = j;
//									System.out.println("idx = " + idx);

									for (int k = 0; k < 63 - idx; k++) {
										br.readLine();
									}

									break;
								} else {
									judgement[j] = false;
									remain = rx;
									continue;
								}
							}

							if (judgement[idx] == true) {
								bw.write("yes");
								bw.newLine();
								bw.flush();

								res = new BigInteger(String.valueOf(64 * l + idx - 1));
								res = p.En(PK, res);

//								System.out.println("write res: " + p.De(PK, sk, res));
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
					System.out.println("remain = " + remain);
					bw.write(remain.toString());
					bw.newLine();
					bw.flush();

				}

				System.out.println("[*] Finished!");
			}

			if (cmd == 10) {
				ss.close();
				break;
			}
		}

	}
}
