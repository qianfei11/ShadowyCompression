package supplement;

import java.math.BigInteger;

public class DecryptArray {
	public static BigInteger[][] decrypt(PublicKey PK, BigInteger sk, BigInteger[][] inputData) {
		Paillier p = new Paillier();
		BigInteger[][] res = new BigInteger[inputData.length][inputData[0].length];
		for (int i = 0; i < inputData.length; i++) {
			for (int j = 0; j < inputData[0].length; j++) {
				res[i][j] = p.De(PK, sk, inputData[i][j]);
			}
		}
		return res;
	}
}
