package server_pk;

public class GetI {

	public static int[][] calI(int[][] I1, int[][] I2) {
		System.out.println("[*] Calculate I...");

		int[][] res = new int[I1.length][I1[0].length];
		for (int i = 0; i < I1.length; i++) {
			for (int j = 0; j < I1[0].length; j++) {
				res[i][j] = I1[i][j] - I2[i][j];
			}
		}

//		for (int i = 0; i < res.length; i++) {
//			for (int j = 0; j < res[0].length; j++) {
//				System.out.print(res[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println();
		System.out.println("[*] Finish calculating!");

		return res;
	}
}
