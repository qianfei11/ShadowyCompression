package supplement;

import java.math.BigInteger;

@SuppressWarnings("serial")
public class PublicKey implements java.io.Serializable {
	public BigInteger n;
	public BigInteger n_square;
	public BigInteger g;
	public int bitLength;

	public PublicKey(BigInteger n, BigInteger n_square, BigInteger g, int bitLength) {
		this.n = n;
		this.n_square = n_square;
		this.g = g;
		this.bitLength = bitLength;
	}

	public BigInteger getG() {
		return this.g;
	}

	public BigInteger getN() {
		return this.n;
	}

	public BigInteger getNS() {
		return this.n_square;
	}

	public int getB() {
		return this.bitLength;
	}
}