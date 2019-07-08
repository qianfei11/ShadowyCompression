package server_pk;

import java.math.BigInteger;

import supplement.Paillier;
import supplement.PublicKey;

public class ConvertColorspace {

	public static BigInteger[][][][] convertRGB2YUV(BigInteger[][][][] Images, PublicKey PK) {
		System.out.println("[*] Convert RGB to YUV...");
		BigInteger[][][][] out = new BigInteger[Images.length][3][8][8];
		Paillier p = new Paillier();
		for (int i = 0; i < Images.length; i++) {
			for (int m = 0; m < 8; m++) {
				for (int n = 0; n < 8; n++) {
					BigInteger R = new BigInteger(String.valueOf(Images[i][0][m][n]));
					BigInteger G = new BigInteger(String.valueOf(Images[i][1][m][n]));
					BigInteger B = new BigInteger(String.valueOf(Images[i][2][m][n]));
					BigInteger Y = p.cipher_mul(PK, R, new BigInteger("2990"));
					Y = p.cipher_add(PK, Y, p.cipher_mul(PK, G, new BigInteger("5870")));
					Y = p.cipher_add(PK, Y, p.cipher_mul(PK, B, new BigInteger("1140")));
					Y = p.cipher_add(PK, Y, p.En(PK, new BigInteger("-1280000")));
					BigInteger Cb = p.cipher_mul(PK, R, new BigInteger("-1687"));
					Cb = p.cipher_add(PK, Cb, p.cipher_mul(PK, G, new BigInteger("-3313")));
					Cb = p.cipher_add(PK, Cb, p.cipher_mul(PK, B, new BigInteger("5000")));
					BigInteger Cr = p.cipher_mul(PK, R, new BigInteger("5000"));
					Cr = p.cipher_add(PK, Cr, p.cipher_mul(PK, G, new BigInteger("-4187")));
					Cr = p.cipher_add(PK, Cr, p.cipher_mul(PK, B, new BigInteger("-813")));
					out[i][0][m][n] = Y;
					out[i][1][m][n] = Cb;
					out[i][2][m][n] = Cr;
				}
			}
		}

//		for (int i = 0; i < out.length; i++) {
//			printArray(out[i]);
//		}

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
