package client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

import supplement.Paillier;
import supplement.PublicKey;

public class No_window_client {
	static int width;
	static int height;
	static int[][] Images;
	static int[][][] quanArr;
	static int quality_scale;
	static int sum;

	static String server_pk_ip;
	static String server_pk_port;
	static String server_sk_ip;
	static String server_sk_port;

	static PublicKey PK;
	static BigInteger sk;
	static Paillier p;

	public static void main(String[] args) {

		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";
		quality_scale = 50;

		p = new Paillier();
		PK = p.KeyGen(512, 64);
		sk = p.get_sk();

		String imagePath = "/Users/qianfei/Downloads/2.bmp";

		long startTime = System.currentTimeMillis();

		try {

			getImage(imagePath);
			Upload.upload(server_pk_ip, server_pk_port, server_sk_ip, server_sk_port, PK, sk, Images);
			quanArr = Receive.receiveQuantificationArray(PK, sk, sum);
			for (int i = 0; i < quanArr.length; i++) {
				for (int m = 0; m < 3; m++) {
					for (int j = 0; j < 64; j++) {
						System.out.print(quanArr[i][m][j] + " ");
					}
					System.out.println();
				}
				System.out.println();
			}
			System.out.println();

			long huffmanStartTime = System.currentTimeMillis();
			System.out.println("[*] Start Canonical Huffman encode...");
			String outputPath = "/Users/qianfei/Downloads/test.jpg";
			Huffman.CanonicalHuffman(quanArr, outputPath, width, height, quality_scale);
			System.out.println("[*] Canonical Huffman encoded successfully!");
			long huffmanEndTime = System.currentTimeMillis();
			System.out.println("[*] Canonical Huffman takes " + (huffmanEndTime - huffmanStartTime) + "ms");

			System.out.println("[*] Done!");

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
			System.out.println(width + "*" + height);
			sum = (height / 8) * (width / 8);
			int start = 0x36;
			bis.skip(start - 0x12 - 4 - 4);
			Images = new int[height][width * 3];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width * 3; j++) {
					Images[i][j] = bis.read();
				}
			}
//			for (int i = 0; i < height; i++) {
//				for (int j = 0; j < width * 3; j++) {
//					System.out.print(Images[i][j] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
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
}
