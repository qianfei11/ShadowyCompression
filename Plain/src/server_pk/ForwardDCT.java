package server_pk;

public class ForwardDCT {

	static int[] ZigZag = { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9,
			11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47,
			50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };

	public static double[] forwardDCT(int[] Imgs) {
		double[] res = new double[64];
		for (int v = 0; v < 8; v++) {
			for (int u = 0; u < 8; u++) {
				double temp = 0;
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						double data = Imgs[y * 8 + x];
						temp += data * Math.cos((2 * x + 1) * u * Math.PI / 16.0f)
								* Math.cos((2 * y + 1) * v * Math.PI / 16.0f);
					}
				}
				temp *= Cfunc(u) * Cfunc(v);
				res[ZigZag[v * 8 + u]] = temp;
			}
		}
		return res;
	}

	public static float Cfunc(int x) {
		if (x == 0) {
			return (float) (1 / Math.sqrt(8f));
		} else {
			return 0.5f;
		}
	}
}
