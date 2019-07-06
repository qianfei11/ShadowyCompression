package server_sk;

public class Quantification {

	public static int[] ZigZag = { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41,
			43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34,
			37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };
	public static int[] Luminance_Quantization_Table = { 16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55,
			14, 13, 16, 24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 24, 35,
			55, 64, 81, 104, 113, 92, 49, 64, 78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99 };
	public static int[] Chrominance_Quantization_Table = { 17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99,
			99, 24, 26, 56, 99, 99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };
	public static byte[] m_YTable, m_CbCrTable;

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

	public static int[] quantizeBlock(double[] inputData, int code) {
		int[] outputData = new int[64];
		double temp = 0;
		short res = 0;
		for (int v = 0; v < 8; v++) {
			for (int u = 0; u < 8; u++) {
				switch (code) {
				case 0:
					temp = inputData[ZigZag[v * 8 + u]] / m_YTable[ZigZag[v * 8 + u]];
					res = (short) ((short) (temp + 16384.5) - 16384);
					outputData[ZigZag[v * 8 + u]] = res;
					break;
				case 1:
					temp = inputData[ZigZag[v * 8 + u]] / m_CbCrTable[ZigZag[v * 8 + u]];
					temp = inputData[ZigZag[v * 8 + u]] / m_YTable[ZigZag[v * 8 + u]];
					res = (short) ((short) (temp + 16384.5) - 16384);
					outputData[ZigZag[v * 8 + u]] = res;
					break;
				case 2:
					temp = inputData[ZigZag[v * 8 + u]] / m_CbCrTable[ZigZag[v * 8 + u]];
					temp = inputData[ZigZag[v * 8 + u]] / m_YTable[ZigZag[v * 8 + u]];
					res = (short) ((short) (temp + 16384.5) - 16384);
					outputData[ZigZag[v * 8 + u]] = res;
					break;
				default:
					break;
				}
			}
		}
		return outputData;
	}
}
