package server_pk;

import java.math.BigInteger;

import supplement.Paillier;
import supplement.PublicKey;

public class GetEI {

	public static BigInteger[][] calEI(BigInteger[][] EI1, BigInteger[][] EI2, PublicKey PK, BigInteger sk) {
		System.out.println("[*] Calculate I...");

		Paillier p = new Paillier();

		BigInteger[][] res = new BigInteger[EI1.length][EI1[0].length];
		for (int i = 0; i < EI1.length; i++) {
			for (int j = 0; j < EI1[0].length; j++) {
				res[i][j] = p.cipher_sub(PK, EI1[i][j], EI2[i][j]);
//				res[i][j] = p.De(PK, sk, p.cipher_sub(PK, EI1[i][j], EI2[i][j]));
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
