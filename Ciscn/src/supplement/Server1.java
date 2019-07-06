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
import java.util.Random;

public class Server1 {

	static PublicKey PK;
	static BigInteger sk;
	static Paillier p;

	static int[] ZigZag = { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9,
			11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47,
			50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
	static int[] Luminance_Quantization_Table = { 16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14,
			13, 16, 24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 24, 35, 55,
			64, 81, 104, 113, 92, 49, 64, 78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99 };
	static int[] Chrominance_Quantization_Table = { 17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99, 99, 24,
			26, 56, 99, 99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };
	static byte[] m_YTable, m_CbCrTable;

	static String ip1;
	static String port1;
	static String ip2;
	static String port2;

	static BigInteger[] dctArr;
	static BigInteger[] quanArr;
	static int quality_scale;

	static Random r;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		p = new Paillier();
		PK = p.KeyGen(1024, 128);
		sk = p.get_sk();

		ip1 = "127.0.0.1";
		port1 = "10001";
		ip2 = "127.0.0.1";
		port2 = "10002";

		r = new Random();

		int[] temp_table = { 907, 113, 82, 12, -83, -34, -17, 13, -21, -25, 0, 27, 24, 27, 17, 9, -14, -11, -6, 0, 0,
				16, 3, -6, -15, -10, -20, -27, 20, 36, 29, 27, 11, -10, -23, -18, 23, 24, 14, -10, -24, -30, -25, 16,
				11, 0, -17, -23, -15, 9, 18, 16, 7, 0, -7, -12, -10, -2, -3, 4, 6, 0, 6, -4 };
		dctArr = new BigInteger[64];
		quanArr = new BigInteger[64];
		for (int i = 0; i < temp_table.length; i++) {
			dctArr[i] = new BigInteger(String.valueOf(temp_table[i]));
		}

		quality_scale = 50;
		initQualityTables(quality_scale);

		for (int i = 0; i < m_YTable.length; i++) {
			System.out.print(m_YTable[i] + " ");
		}
		System.out.println();
		for (int i = 0; i < m_CbCrTable.length; i++) {
			System.out.print(m_CbCrTable[i] + " ");
		}
		System.out.println();

		Socket temp = new Socket(ip2, Integer.valueOf(port2));
		ObjectOutputStream temp_os = new ObjectOutputStream(temp.getOutputStream());
		temp_os.writeInt(0);
		temp_os.flush();
		temp_os.writeObject(PK);
		temp_os.flush();
		temp_os.writeObject(sk);
		temp_os.flush();

		ServerSocket ss = new ServerSocket(Integer.valueOf(port1));
		while (true) {
			Socket s = ss.accept();
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
			int cmd = is.readInt();
			if (cmd == 0) {
				;
			}

			if (cmd == 1) {
				temp = new Socket(ip2, Integer.valueOf(port2));
				temp_os = new ObjectOutputStream(temp.getOutputStream());

				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(temp.getOutputStream()));
				BufferedReader br = new BufferedReader(new InputStreamReader(temp.getInputStream()));

				System.out.println("[*] Start calculate...");
				temp_os.writeInt(1);
				temp_os.flush();
				for (int i = 0; i < 64; i++) {
//					System.out.println("src: " + dctArr[i]);
					
					BigInteger rData = null;

					int rd = r.nextInt(Integer.MAX_VALUE);
//					rd = 1;
					BigInteger src = p.En(PK, dctArr[i]);
					src = p.cipher_mul(PK, src, new BigInteger("1000000000000"));
					rData = p.cipher_mul(PK, src, new BigInteger(String.valueOf(rd)));

//					System.out.println("send " + p.De(PK, sk, src));
					bw.write(rData.toString());
					bw.newLine();
					bw.flush();

					boolean sig = Boolean.parseBoolean(br.readLine());

//					System.out.println("num" + i + " " + dctArr[i] + " " + sig);

					BigInteger n = BigInteger.ONE;
					String flag = "";
					BigInteger data = null;

					BigInteger res = null;
					BigInteger remain = null;

					for (int l = 0; l < 2048 / 64; l++) {
						rd = r.nextInt(Integer.MAX_VALUE);
//						rd = 1;
						for (int j = 0; j < 64; j++) {
							n = new BigInteger(String.valueOf(64 * l + j));
							data = calculate(src, n, i, sig);
							rData = p.cipher_mul(PK, data, new BigInteger(String.valueOf(rd)));

//							System.out.println("send " + p.De(PK, sk, data));
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
//					System.out.println("remain = " + remain);

					if (judge(remain, i)) {
						res = p.cipher_add(PK, res, p.En(PK, BigInteger.ONE));
					}

					res = p.De(PK, sk, res);

					if (sig) {
						res = res.negate();
					}

					System.out.println("Result: " + res);
				}
			}

			if (cmd == 10) {
				ss.close();
				break;
			}
		}

	}

	public static boolean judge(BigInteger remain, int idx) {
		BigInteger x = new BigInteger(String.valueOf(m_YTable[idx]));
		BigInteger y = x.divide(BigInteger.TWO);
		
//		System.out.println("b: " + x);

//		System.out.println(remain + " " + y);

		if (remain.abs().compareTo(y) == -1) {
			return false;
		} else {
			return true;
		}

	}

	public static BigInteger calculate(BigInteger data, BigInteger n, int idx, boolean sig) {
		BigInteger x = new BigInteger(String.valueOf(m_YTable[idx]));
		BigInteger nx = x.multiply(new BigInteger(String.valueOf(n)));
		BigInteger enx = p.En(PK, nx);
		enx = p.cipher_mul(PK, enx, new BigInteger("1000000000000"));

		if (sig == false) {
			return p.cipher_sub(PK, data, enx);
		} else {
			return p.cipher_add(PK, data, enx);
		}
	}

	public static void initQualityTables(int quality_scale) {
		m_YTable = new byte[64];
		m_CbCrTable = new byte[64];

		if (quality_scale <= 0) {
			quality_scale = 1;
		}
		if (quality_scale >= 100) {
			quality_scale = 99;
		}

		for (int i = 0; i < 64; i++) {
			byte temp = (byte) ((int) (Luminance_Quantization_Table[i] * quality_scale + 50) / 100);
			if (temp <= 0) {
				temp = 1;
			}
			if (temp > 0xFF) {
				temp = (byte) 0xFF;
			}
			m_YTable[ZigZag[i]] = temp;

			temp = (byte) ((int) (Chrominance_Quantization_Table[i] * quality_scale + 50) / 100);
			if (temp <= 0) {
				temp = 1;
			}
			if (temp > 0xFF) {
				temp = (byte) 0xFF;
			}
			m_CbCrTable[ZigZag[i]] = temp;
		}
	}
}
