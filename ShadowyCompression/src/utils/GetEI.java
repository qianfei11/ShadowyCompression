package utils;

import java.math.BigInteger;

public class GetEI {

	public static BigInteger[][] calEI(BigInteger[][] EI1, BigInteger[][] EI2, PublicKey PK) {
		Paillier p = new Paillier();

		BigInteger[][] res = new BigInteger[EI1.length][EI1[0].length];
		for (int i = 0; i < EI1.length; i++) {
			for (int j = 0; j < EI1[0].length; j++) {
				res[i][j] = p.cipher_sub(PK, EI1[i][j], EI2[i][j]);
			}
		}
		return res;
	}
}
