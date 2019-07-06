package supplement;

import java.math.BigInteger;

public class Test {
	public static void main(String[] args) {
		BigInteger x = new BigInteger(String.valueOf(-12));
		BigInteger y = new BigInteger(String.valueOf(25));
		System.out.println("x = " + x);
		System.out.println("y = " + y);
		System.out.println("y / x = " + divide(y, x));
	}

	public static BigInteger substract(BigInteger a, BigInteger b) {
		return a.add(b.negate());
	}

	public static boolean positive(BigInteger a, BigInteger b) {
		if((a.signum() == 1 && b.signum() == 1) || (a.signum() == -1 && b.signum() == -1))
			return true;
		else
			return false;
	}

	public static BigInteger times(BigInteger a, BigInteger b) {
		if (a.abs().compareTo(b.abs()) == 1)
			return times(b, a); // faster
		BigInteger result = BigInteger.ZERO;
		for(BigInteger i = BigInteger.ZERO; i.compareTo(a.abs()) == -1; i = i.add(BigInteger.ONE)) {
			result = result.add(b.abs());
		}
		if (positive(a, b) == true)
			return result;
		else
			return result.negate();
	}

	public static BigInteger divide(BigInteger a, BigInteger b) {
		BigInteger count = BigInteger.ZERO;
		for(BigInteger i = a.abs(); i.compareTo(b.abs()) != -1; i = i.subtract(b.abs())) {
			count = count.add(BigInteger.ONE);
		}
		BigInteger remaining = substract(a.abs(), times(b.abs(), new BigInteger(String.valueOf(count))));
		if(times(remaining, BigInteger.TWO).compareTo(b.abs()) != -1) {
			count = count.add(BigInteger.ONE);
		}
		if (positive(a, b) == true)
			return count;
		else
			return count.negate();
	}
}
