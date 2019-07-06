package supplement;

import java.math.BigInteger;
import java.util.Random;

public class Test {
	public static void main(String[] args) throws Exception {
		Paillier p = new Paillier();
		PublicKey PK = p.KeyGen(1024, 128);
		BigInteger sk = p.get_sk();
		System.out.println("n = " + PK.n);

		BigInteger x = new BigInteger("11");
		BigInteger ex = p.En(PK, x);
		System.out.println("ex = " + ex);

		System.out.println(Boolean.parseBoolean("true"));

		Random r = new Random();
		int val = r.nextInt(Integer.MAX_VALUE);
		System.out.println(val);

		System.out.println("rx = " + x.multiply(new BigInteger(String.valueOf(val))));

		System.out.println("rE(x) = " + p.cipher_mul(PK, ex, new BigInteger(String.valueOf(val))));

		System.out.println(
				"rx = " + p.De(PK, sk, p.cipher_mul(PK, ex, new BigInteger(String.valueOf(val)))).subtract(PK.n));

		BigInteger y = new BigInteger("1000000000000");

		System.out.println("1000000000000x = " + x.multiply(y));

		System.out.println("1000000000000E(x) = " + p.cipher_mul(PK, ex, y));

		System.out.println("1000000000000x = " + p.De(PK, sk, p.cipher_mul(PK, ex, y)).subtract(PK.n));

		System.out.println("MAX_INT = " + Integer.MAX_VALUE);

		System.out.println("-x = " + p.De(PK, sk, p.cipher_mul(PK, ex, new BigInteger("-1"))).subtract(PK.n));
		
		System.out.println("MAX_SHORT = " + Short.MAX_VALUE);
		
		long start = System.currentTimeMillis();
		Thread.sleep(3000);
		long end = System.currentTimeMillis();
		
		System.out.println("Spend " + (end - start));

	}
}
