package utils;

import java.math.BigInteger;
import java.util.Random;

public class Paillier {
	private BigInteger p, q, lambda;
	private BigInteger n; // n = p*q
	private BigInteger n_square; // n_square = n*n
	private BigInteger g;
	private int bitLength;

	public Paillier(int bitLengthVal, int certainty) {
		KeyGen(bitLengthVal, certainty);
	}

	public Paillier() {
	}

	public PublicKey KeyGen(int bitLengthVal, int certainty) {
		bitLength = bitLengthVal;
		p = new BigInteger(bitLength / 2, certainty, new Random());
		q = new BigInteger(bitLength / 2, certainty, new Random());

		n = p.multiply(q);
		n_square = n.multiply(n);
		g = new BigInteger("2");
		lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
				.divide(p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));

		if (g.modPow(lambda, n_square).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1) {
			System.out.println("g is not good enough!");
			System.exit(1);
		}
		return new PublicKey(n, n_square, g, bitLength);
	}

	public BigInteger En(PublicKey PK, BigInteger m) {
		BigInteger r = new BigInteger(PK.bitLength, new Random());
		return PK.g.modPow(m, PK.n_square).multiply(r.modPow(PK.n, PK.n_square)).mod(PK.n_square);
	}

	public BigInteger En(PublicKey PK, BigInteger m, BigInteger r) {
		return PK.g.modPow(m, PK.n_square).multiply(r.modPow(PK.n, PK.n_square)).mod(PK.n_square);
	}

	public BigInteger De(PublicKey PK, BigInteger lambda, BigInteger c) {
		BigInteger u = PK.g.modPow(lambda, PK.n_square).subtract(BigInteger.ONE).divide(PK.n).modInverse(PK.n);
		return c.modPow(lambda, PK.n_square).subtract(BigInteger.ONE).divide(PK.n).multiply(u).mod(PK.n);
	}

	public BigInteger cipher_add(PublicKey PK, BigInteger em1, BigInteger em2) {
		return em1.multiply(em2).mod(PK.n_square);
	}

	public BigInteger cipher_mul(PublicKey PK, BigInteger em1, BigInteger m2) {
		return em1.modPow(m2, PK.n_square);
	}

	public BigInteger cipher_sub(PublicKey PK, BigInteger em1, BigInteger em2) {
		return em1.multiply(em2.modInverse(PK.n_square)).mod(PK.n_square);
	}

	public BigInteger get_sk() {
		return lambda;
	}
}
