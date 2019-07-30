package client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

import compression.ConvertColorspace;
import compression.ForwardDCT;
import compression.Huffman;
import compression.Quantification;
import compression.Spilit;
import utils.Refresh;
import utils.EasyDispose;
import utils.EncryptArray;
import utils.GetEI;
import utils.Paillier;
import utils.PublicKey;

public class Client {
	static int width;
	static int height;
	static int quality_scale;

	static int[][] I;
	static BigInteger[][] I1;
	static BigInteger[][] I2;

	static BigInteger[][] EI1;
	static BigInteger[][] EI2;
	static BigInteger[][] EI;

	static BigInteger[][][][] Images;
	static BigInteger[][][] YUVarr;
	static BigInteger[][][] dctArr;
	static int[][][] quanArr;

	static PublicKey PK;
	static BigInteger sk;
	static Paillier p;

	static long total;

	public static void main(String[] args) {
		System.out.println(
				"███████╗██╗  ██╗ █████╗ ██████╗  ██████╗ ██╗    ██╗██╗   ██╗ ██████╗ ██████╗ ███╗   ███╗██████╗ ██████╗ ███████╗███████╗███████╗██╗ ██████╗ ███╗   ██╗");
		System.out.println(
				"██╔════╝██║  ██║██╔══██╗██╔══██╗██╔═══██╗██║    ██║╚██╗ ██╔╝██╔════╝██╔═══██╗████╗ ████║██╔══██╗██╔══██╗██╔════╝██╔════╝██╔════╝██║██╔═══██╗████╗  ██║");
		System.out.println(
				"███████╗███████║███████║██║  ██║██║   ██║██║ █╗ ██║ ╚████╔╝ ██║     ██║   ██║██╔████╔██║██████╔╝██████╔╝█████╗  ███████╗███████╗██║██║   ██║██╔██╗ ██║");
		System.out.println(
				"╚════██║██╔══██║██╔══██║██║  ██║██║   ██║██║███╗██║  ╚██╔╝  ██║     ██║   ██║██║╚██╔╝██║██╔═══╝ ██╔══██╗██╔══╝  ╚════██║╚════██║██║██║   ██║██║╚██╗██║");
		System.out.println(
				"███████║██║  ██║██║  ██║██████╔╝╚██████╔╝╚███╔███╔╝   ██║   ╚██████╗╚██████╔╝██║ ╚═╝ ██║██║     ██║  ██║███████╗███████║███████║██║╚██████╔╝██║ ╚████║");
		System.out.println(
				"╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝  ╚═════╝  ╚══╝╚══╝    ╚═╝    ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝  ╚═╝╚══════╝╚══════╝╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═══╝");

		String imagePath = null;

		Scanner in = new Scanner(System.in);
		System.out.print("Please input imagePath: ");
		imagePath = in.nextLine();
		in.close();

		String outputPath = imagePath.replaceAll("bmp", "jpg");

		quality_scale = 50;

		p = new Paillier();
		PK = p.KeyGen(1024, 128);
		sk = p.get_sk();

		long startTime = System.currentTimeMillis();

		try {
			getImage(imagePath);

			System.out.println("Start create matrix...");
			long createStartTime = System.currentTimeMillis();
			EasyDispose ed = new EasyDispose(I);
			long createEndTime = System.currentTimeMillis();
			System.out.println("[+] Create matrix takes " + (createEndTime - createStartTime) + "ms");

			System.out.println("Start calculate EI...");
			long calculateStartTime = System.currentTimeMillis();
			I1 = new BigInteger[ed.I1.length][ed.I1[0].length];
			I2 = new BigInteger[ed.I2.length][ed.I2[0].length];
			for (int i = 0; i < I1.length; i++) {
				for (int j = 0; j < I1[0].length; j++) {
					I1[i][j] = new BigInteger(String.valueOf(ed.I1[i][j]));
				}
			}
			for (int i = 0; i < I2.length; i++) {
				for (int j = 0; j < I2[0].length; j++) {
					I2[i][j] = new BigInteger(String.valueOf(ed.I2[i][j]));
				}
			}
			EI1 = new BigInteger[I1.length][I1[0].length];
			EI2 = new BigInteger[I2.length][I2[0].length];
			EI1 = EncryptArray.encrypt(PK, I1);
			EI2 = EncryptArray.encrypt(PK, I2);
			total = EI1.length * EI1[0].length;
			EI = GetEI.calEI(EI1, EI2, PK, total);
			long calculateEndTime = System.currentTimeMillis();
			System.out.println("\n[+] Calculating takes " + (calculateEndTime - calculateStartTime) + "ms");

			System.out.println("Start spilit bmp file...");
			long spilitStartTime = System.currentTimeMillis();
			Images = Spilit.spilitBMP(EI);
			long spilitEndTime = System.currentTimeMillis();
			System.out.println("[+] Spilit takes " + (spilitEndTime - spilitStartTime) + "ms");

			long convertStartTime = System.currentTimeMillis();
			YUVarr = convertTo1D(ConvertColorspace.convertRGB2YUV(Images, PK, total));
			long convertEndTime = System.currentTimeMillis();
			System.out.println("\n[+] Convert colorspace takes " + (convertEndTime - convertStartTime) + "ms");

			long dctStartTime = System.currentTimeMillis();
			System.out.println("[*] Start DCT...");
			long dctProgressBarStartTime = System.currentTimeMillis();
			long dctProgressBarIdx = 1;
			dctArr = new BigInteger[YUVarr.length][3][64];
			for (int i = 0; i < YUVarr.length; i++) {
				for (int m = 0; m < 3; m++) {
					dctArr[i][m] = ForwardDCT.forwardDCT(YUVarr[i][m], PK, dctProgressBarStartTime, total,
							dctProgressBarIdx);
					dctProgressBarIdx += 64;
				}
			}
			long dctEndTime = System.currentTimeMillis();
			System.out.println("\n[+] DCT takes " + (dctEndTime - dctStartTime) + "ms");

			System.out.println("Start Quanlification...");
			long quanlificationStartTime = System.currentTimeMillis();
			long quanlificationProgressBarStartTime = System.currentTimeMillis();
			long quanlificationProgressBarIdx = 1;
			quanArr = new int[dctArr.length][3][64];
			quality_scale = 50;
			Quantification.initQualityTables(quality_scale);
			Random r = new Random();
			BigInteger rData = null;
			BigInteger val = new BigInteger("1267650600228229401496703205376"); // 2**100
			for (int i = 0; i < dctArr.length; i++) {
				for (int m = 0; m < 3; m++) {
					for (int j = 0; j < 64; j++) {
						Refresh.printProgress(quanlificationProgressBarStartTime, total, quanlificationProgressBarIdx);
						quanlificationProgressBarIdx += 1;
						int rd = r.nextInt(Integer.MAX_VALUE);
						BigInteger src = dctArr[i][m][j];
						rData = p.cipher_mul(PK, src, new BigInteger(String.valueOf(rd)));
						BigInteger rx = p.De(PK, sk, rData);
						rx = rx.divide(new BigInteger("1000000000000"));
						// judge sign
						boolean sig = false;
						if (rx.compareTo(val) == 1) {
							sig = true;
						} else {
							sig = false;
						}
						BigInteger remain = BigInteger.ZERO;
						BigInteger res = null;
						BigInteger data = null;
						BigInteger n = BigInteger.ONE;
						if (sig == true) {
							for (int l = 0; l < 2048 / 4; l++) {
								rd = r.nextInt(Integer.MAX_VALUE);
								Boolean[] judgement = new Boolean[16];
								for (int k = 0; k < 4; k++) {
									judgement[k] = false;
								}
								int idx = 0;
								for (int k = 0; k < 4; k++) {
									n = new BigInteger(String.valueOf(4 * l + k));
									data = Quantification.calculate(src, n, m, j, sig, PK);
									rData = p.cipher_mul(PK, data, new BigInteger(String.valueOf(rd)));
									rx = p.De(PK, sk, rData);
									if (rx.compareTo(BigInteger.ZERO) == 0) {
										judgement[k] = false;
										remain = rx;
										continue;
									}
									if (rx.compareTo(val) == -1) {
										judgement[k] = true;
										idx = k;
										break;
									} else {
										judgement[k] = false;
										remain = rx;
										continue;
									}

								}
								if (judgement[idx] == true) {
									res = new BigInteger(String.valueOf(4 * l + idx - 1));
									res = p.En(PK, res);
									break;
								} else {
									continue;
								}
							}
						} else {
							for (int l = 0; l < 2048 / 4; l++) {
								Boolean[] judgement = new Boolean[4];
								for (int k = 0; k < 4; k++) {
									judgement[k] = false;
								}
								int idx = 0;
								for (int k = 0; k < 4; k++) {
									n = new BigInteger(String.valueOf(4 * l + k));
									data = Quantification.calculate(src, n, m, j, sig, PK);
									rData = p.cipher_mul(PK, data, new BigInteger(String.valueOf(rd)));
									rx = p.De(PK, sk, rData);
									if (rx.compareTo(val) == 1) {
										judgement[k] = true;
										idx = k;
										break;
									} else {
										judgement[k] = false;
										remain = rx;
										continue;
									}
								}
								if (judgement[idx] == true) {
									res = new BigInteger(String.valueOf(4 * l + idx - 1));
									res = p.En(PK, res);
									break;
								} else {
									continue;
								}
							}
						}
						if (remain.compareTo(val) == 1) {
							remain = remain.subtract(PK.n);
						}
						remain = remain.divide(new BigInteger("1000000000000"));
						remain = remain.divide(new BigInteger(String.valueOf(rd)));
						if (Quantification.judge(remain, m, j)) {
							res = p.cipher_add(PK, res, p.En(PK, BigInteger.ONE));
						}
						if (sig) {
							res = p.cipher_mul(PK, res, new BigInteger("-1"));
						}
						res = p.De(PK, sk, res);
						if (res.compareTo(PK.n.divide(BigInteger.TWO)) == 1) {
							res = res.subtract(PK.n);
						}
						quanArr[i][m][j] = res.intValue();
					}
				}
			}
			long quanlificationEndTime = System.currentTimeMillis();
			System.out.println("\n[+] Quanlification takes " + (quanlificationEndTime - quanlificationStartTime) + "ms");

			long huffmanStartTime = System.currentTimeMillis();
			System.out.println("Start Canonical Huffman Encode...");
			Huffman.CanonicalHuffman(quanArr, outputPath, width, height, quality_scale);
			long huffmanEndTime = System.currentTimeMillis();
			System.out.println("[+] Canonical Huffman takes " + (huffmanEndTime - huffmanStartTime) + "ms");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("[*] Total Time: " + (endTime - startTime) + "ms");
	}

	public static void getImage(String inputPath) {
		try {
			FileInputStream fis = new FileInputStream(inputPath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.skip(0x12);
			byte[] b1 = new byte[4];
			bis.read(b1);
			width = byte2Int(b1);
			byte[] b2 = new byte[4];
			bis.read(b2);
			height = byte2Int(b2);
			System.out.println("[+] Image format: " + width + "*" + height);
			int start = 0x36;
			bis.skip(start - 0x12 - 4 - 4);
			I = new int[height][width * 3];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width * 3; j++) {
					I[i][j] = bis.read();
				}
			}
			bis.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int byte2Int(byte[] by) {
		int t1 = by[3] & 0xff;
		int t2 = by[2] & 0xff;
		int t3 = by[1] & 0xff;
		int t4 = by[0] & 0xff;
		int num = t1 << 24 | t2 << 16 | t3 << 8 | t4;
		return num;
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
