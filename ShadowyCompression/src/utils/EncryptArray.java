package utils;

import java.math.BigInteger;

public class EncryptArray {
	public static BigInteger[][] encrypt(PublicKey PK, BigInteger[][] inputData) {
		Paillier p = new Paillier();
		BigInteger[][] res = new BigInteger[inputData.length][inputData[0].length];
		for (int i = 0; i < inputData.length; i++) {
			for (int j = 0; j < inputData[0].length; j++) {
				res[i][j] = p.En(PK, inputData[i][j]);
			}
		}
		return res;
	}
}
