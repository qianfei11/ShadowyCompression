package server_pk;

public class ConvertColorspace {

	public static int[][][][] convertRGB2YUV(int[][][][] Images) {
		System.out.println("[*] Convert RGB to YUV...");
		int[][][][] out = new int[Images.length][3][8][8];
		for (int i = 0; i < Images.length; i++) {
			for (int m = 0; m < 8; m++) {
				for (int n = 0; n < 8; n++) {
					int R = Images[i][0][m][n];
					int G = Images[i][1][m][n];
					int B = Images[i][2][m][n];
					int Y = (int) (0.299f * R + 0.587f * G + 0.114f * B - 128);
					int Cb = (int) (-0.1687f * R - 0.3313f * G + 0.5f * B);
					int Cr = (int) (0.5f * R - 0.4187f * G - 0.0813f * B);
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

	public static void printArray(int[][][] F) {
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
