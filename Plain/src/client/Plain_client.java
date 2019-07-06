package client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Plain_client {
	static int sum;
	static int width;
	static int height;
	static int[][] Images;
	static int[][][] quanArr;
	static int quality_scale;

	static String server_pk_ip;
	static String server_pk_port;
	static String server_sk_ip;
	static String server_sk_port;
	static String client_ip;
	static String client_port;

	public static void main(String[] args) {

		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";
		quality_scale = 50;
		client_ip = "127.0.0.1";
		client_port = "33333";

		String imagePath = "/Users/qianfei/Downloads/6.bmp";

		long startTime = System.currentTimeMillis();

		try {
			getImage(imagePath);
			Upload.upload(server_pk_ip, server_pk_port, server_sk_ip, server_sk_port, Images);
			quanArr = Receive.receiveQuantificationArray(sum, client_ip, client_port);
			System.out.println("[*] Start Canonical Huffman encode...");
			String outputPath = "/Users/qianfei/Downloads/test.jpg";
			Huffman.CanonicalHuffman(quanArr, outputPath, width, height, quality_scale);
			System.out.println("[*] Canonical Huffman encoded successfully!");
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
			sum = (width / 8) * (height / 8);
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
