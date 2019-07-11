package compression;

import java.math.BigInteger;

public class Spilit {

	public static BigInteger[][][][] spilitBMP(BigInteger[][] I) {
		BigInteger[][][] temp = new BigInteger[I.length][I[0].length][3];
		int width = I.length;
		int height = I[0].length / 3;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				temp[I.length - i - 1][j][0] = I[i][3 * j + 2];
				temp[I.length - i - 1][j][1] = I[i][3 * j + 1];
				temp[I.length - i - 1][j][2] = I[i][3 * j];
			}
		}

		int idx = 0;
		BigInteger[][][][] res = new BigInteger[width / 8 * height / 8][3][8][8];
		for (int xPos = 0; xPos < width / 8; xPos++) {
			for (int yPos = 0; yPos < height / 8; yPos++) {
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						res[idx][0][y][x] = temp[8 * xPos + x][8 * yPos + y][0];
						res[idx][1][y][x] = temp[8 * xPos + x][8 * yPos + y][1];
						res[idx][2][y][x] = temp[8 * xPos + x][8 * yPos + y][2];
					}
				}
				idx++;
			}
		}
		return res;
	}
}
