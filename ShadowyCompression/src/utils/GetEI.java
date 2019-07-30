package utils;

import java.math.BigInteger;

import utils.Refresh;

public class GetEI {

	public static BigInteger[][] calEI(BigInteger[][] EI1, BigInteger[][] EI2, PublicKey PK, long total) {
		Paillier p = new Paillier();
		long startTime = System.currentTimeMillis();
		long idx = 1;

		BigInteger[][] res = new BigInteger[EI1.length][EI1[0].length];
		for (int i = 0; i < EI1.length; i++) {
			for (int j = 0; j < EI1[0].length; j++) {
				res[i][j] = p.cipher_sub(PK, EI1[i][j], EI2[i][j]);
				Refresh.printProgress(startTime, total, idx);
				idx += 1;
			}
		}
		return res;
	}
}
