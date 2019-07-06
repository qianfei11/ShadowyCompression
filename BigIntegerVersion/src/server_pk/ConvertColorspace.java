package server_pk;

import java.math.BigInteger;

public class ConvertColorspace {

	public static BigInteger[][][][] convertRGB2YUV(BigInteger[][][][] Images) {
		System.out.println("[*] Convert RGB to YUV...");
		BigInteger[][][][] out = new BigInteger[Images.length][3][8][8];
		for (int i = 0; i < Images.length; i++) {
			for (int m = 0; m < 8; m++) {
				for (int n = 0; n < 8; n++) {
					BigInteger R = new BigInteger(String.valueOf(Images[i][0][m][n]));
					BigInteger G = new BigInteger(String.valueOf(Images[i][1][m][n]));
					BigInteger B = new BigInteger(String.valueOf(Images[i][2][m][n]));
					BigInteger Y = R.multiply(new BigInteger(String.valueOf("2990"))).add(G.multiply(new BigInteger(String.valueOf("5870")))).add(B.multiply(new BigInteger(String.valueOf("1140")))).subtract(new BigInteger(String.valueOf("1280000")));
					BigInteger Cb = R.multiply(new BigInteger(String.valueOf("-1687"))).add(G.multiply(new BigInteger(String.valueOf("-3313")))).add(B.multiply(new BigInteger(String.valueOf("5000"))));
					BigInteger Cr = R.multiply(new BigInteger(String.valueOf("5000"))).add(G.multiply(new BigInteger(String.valueOf("-4187")))).add(B.multiply(new BigInteger(String.valueOf("-813"))));
					out[i][0][m][n] = Y.divide(new BigInteger(String.valueOf("10000")));
					out[i][1][m][n] = Cb.divide(new BigInteger(String.valueOf("10000")));
					out[i][2][m][n] = Cr.divide(new BigInteger(String.valueOf("10000")));
				}
			}
		}

		for (int i = 0; i < out.length; i++) {
			printArray(out[i]);
		}

		System.out.println("[*] Finish converting!");
		return out;
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
}
