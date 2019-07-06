package server_sk;

import java.math.BigInteger;

public class Quantification {

	static int[] ZigZag = { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9,
			11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47,
			50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
	static int[] Luminance_Quantization_Table = { 16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14,
			13, 16, 24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 24, 35, 55,
			64, 81, 104, 113, 92, 49, 64, 78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99 };
	static int[] Chrominance_Quantization_Table = { 17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99, 99, 24,
			26, 56, 99, 99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };
	static byte[] m_YTable, m_CbCrTable;

	public static void main(String[] args) {

		initQualityTables(50);

		int[] temp_table = { 907, 113, 82, 12, -83, -34, -17, 13, -21, -25, 0, 27, 24, 27, 17, 9, -14, -11, -6, 0, 0,
				16, 3, -6, -15, -10, -20, -27, 20, 36, 29, 27, 11, -10, -23, -18, 23, 24, 14, -10, -24, -30, -25, 16,
				11, 0, -17, -23, -15, 9, 18, 16, 7, 0, -7, -12, -10, -2, -3, 4, 6, 0, 6, -4 };

		BigInteger[] dctArr = new BigInteger[64];
		BigInteger[] quanArr = new BigInteger[64];

		for (int i = 0; i < temp_table.length; i++) {
			dctArr[i] = new BigInteger(String.valueOf(temp_table[i]));
		}

		quanArr = quantizeBlock(dctArr, 0);

		for (int i = 0; i < quanArr.length; i++) {
			System.out.print(quanArr[i] + " ");
		}
		System.out.println();
	}

	public static void initQualityTables(int quality_scale) {
		m_YTable = new byte[64];
		m_CbCrTable = new byte[64];

		if (quality_scale <= 0) {
			quality_scale = 1;
		}
		if (quality_scale >= 100) {
			quality_scale = 99;
		}

		for (int i = 0; i < 64; i++) {
			byte temp = (byte) ((int) (Luminance_Quantization_Table[i] * quality_scale + 50) / 100);
			if (temp <= 0) {
				temp = 1;
			}
			if (temp > 0xFF) {
				temp = (byte) 0xFF;
			}
			m_YTable[ZigZag[i]] = temp;

			temp = (byte) ((int) (Chrominance_Quantization_Table[i] * quality_scale + 50) / 100);
			if (temp <= 0) {
				temp = 1;
			}
			if (temp > 0xFF) {
				temp = (byte) 0xFF;
			}
			m_CbCrTable[ZigZag[i]] = temp;
		}
	}

	public static BigInteger[] quantizeBlock(BigInteger[] inputData, int code) {
		BigInteger[] outputData = new BigInteger[64];
		BigInteger temp = null;
		for (int v = 0; v < 8; v++) {
			for (int u = 0; u < 8; u++) {
				switch (code) {
				case 0:
					temp = new BigInteger(String.valueOf(m_YTable[ZigZag[v * 8 + u]]));
					outputData[ZigZag[v * 8 + u]] = divide(inputData[ZigZag[v * 8 + u]], temp);
					break;
				case 1:
					temp = new BigInteger(String.valueOf(m_CbCrTable[ZigZag[v * 8 + u]]));
					outputData[ZigZag[v * 8 + u]] = divide(inputData[ZigZag[v * 8 + u]], temp);
					break;
				case 2:
					temp = new BigInteger(String.valueOf(m_CbCrTable[ZigZag[v * 8 + u]]));
					outputData[ZigZag[v * 8 + u]] = divide(inputData[ZigZag[v * 8 + u]], temp);
					break;
				default:
					break;
				}
			}
		}
		return outputData;
	}

	public static BigInteger substract(BigInteger a, BigInteger b) {
		return a.add(b.negate());
	}

	public static boolean positive(BigInteger a, BigInteger b) {
		if ((a.signum() == 1 && b.signum() == 1) || (a.signum() == -1 && b.signum() == -1))
			return true;
		else
			return false;
	}

	public static BigInteger times(BigInteger a, BigInteger b) {
		if (a.abs().compareTo(b.abs()) == 1)
			return times(b, a); // faster
		BigInteger result = BigInteger.ZERO;
		for (BigInteger i = BigInteger.ZERO; i.compareTo(a.abs()) == -1; i = i.add(BigInteger.ONE)) {
			result = result.add(b.abs());
		}
		if (positive(a, b) == true)
			return result;
		else
			return result.negate();
	}

	public static BigInteger divide(BigInteger a, BigInteger b) {
		BigInteger count = BigInteger.ZERO;
		for (BigInteger i = a.abs(); i.compareTo(b.abs()) != -1; i = i.subtract(b.abs())) {
			count = count.add(BigInteger.ONE);
		}
		BigInteger remaining = substract(a.abs(), times(b.abs(), new BigInteger(String.valueOf(count))));
		if (times(remaining, BigInteger.TWO).compareTo(b.abs()) != -1) {
			count = count.add(BigInteger.ONE);
		}
		if (positive(a, b) == true)
			return count;
		else
			return count.negate();
	}
}