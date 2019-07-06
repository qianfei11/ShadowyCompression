package server_pk;

import java.math.BigInteger;

public class GetEI {

	public static BigInteger[][] calEI(BigInteger[][] EI1, BigInteger[][] EI2) {
		System.out.println("[*] Calculate I...");

		BigInteger[][] res = new BigInteger[EI1.length][EI1[0].length];
		for (int i = 0; i < EI1.length; i++) {
			for (int j = 0; j < EI1[0].length; j++) {
				res[i][j] = EI1[i][j].subtract(EI2[i][j]);
			}
		}

		for (int i = 0; i < res.length; i++) {
			for (int j = 0; j < res[0].length; j++) {
				System.out.print(res[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("[*] Finish calculating!");

		return res;
	}
}
