package utils;

import java.util.Random;

public class EasyDispose {
	public int[][] I1 = null;
	public int[][] I2 = null;

	public EasyDispose(int[][] I) {
		// I = I1 - I2
		Random r = new Random();
		I1 = new int[I.length][I[0].length];
		I2 = new int[I.length][I[0].length];

		for (int i = 0; i < I.length; i++)
			for (int j = 0; j < I[0].length; j++) {
				I1[i][j] = r.nextInt(128) + 256;
				I2[i][j] = I1[i][j] - I[i][j];
			}

	}
}
