package utils;

import java.math.BigInteger;

public class EncryptArray {
	public static BigInteger[][] encrypt(PublicKey PK, BigInteger[][] inputData, long total) {
		Paillier p = new Paillier();
		long startTime = System.currentTimeMillis();
		long idx = 1;
		BigInteger[][] res = new BigInteger[inputData.length][inputData[0].length];
		for (int i = 0; i < inputData.length; i++) {
			for (int j = 0; j < inputData[0].length; j++) {
				res[i][j] = p.En(PK, inputData[i][j]);
				Refresh.printProgress(startTime, total, idx);
				idx += 1;
			}
		}
		return res;
	}
}
