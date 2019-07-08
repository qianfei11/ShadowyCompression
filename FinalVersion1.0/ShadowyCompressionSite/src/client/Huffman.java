package client;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import supplement.BitString;

public class Huffman {
	public static String res;
	public static byte[] finalData;
	public static BitString[] outputBitString;
	public static String finalBinString;
	public static int bitStringLength;
	public static int finalLength;
	public static int m_height;
	public static int m_width;
	public static int prev_DC_Y = 0, prev_DC_Cb = 0, prev_DC_Cr = 0;
	public static int quality_scale;

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

	public static int[] Standard_DC_Luminance_NRCodes = { 0, 0, 7, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
	public static int[] Standard_DC_Luminance_Values = { 4, 5, 3, 2, 6, 1, 0, 7, 8, 9, 10, 11 };

	public static int[] Standard_DC_Chrominance_NRCodes = { 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 };
	public static int[] Standard_DC_Chrominance_Values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	public static int[] Standard_AC_Luminance_NRCodes = { 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 0x7d };
	public static int[] Standard_AC_Luminance_Values = { 0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31,
			0x41, 0x06, 0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1, 0x08, 0x23, 0x42, 0xb1, 0xc1,
			0x15, 0x52, 0xd1, 0xf0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0a, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x25, 0x26,
			0x27, 0x28, 0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49,
			0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x73,
			0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92, 0x93, 0x94,
			0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4,
			0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4,
			0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf1, 0xf2,
			0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa };

	public static int[] Standard_AC_Chrominance_NRCodes = { 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 0x77 };
	public static int[] Standard_AC_Chrominance_Values = { 0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06,
			0x12, 0x41, 0x51, 0x07, 0x61, 0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91, 0xa1, 0xb1, 0xc1, 0x09,
			0x23, 0x33, 0x52, 0xf0, 0x15, 0x62, 0x72, 0xd1, 0x0a, 0x16, 0x24, 0x34, 0xe1, 0x25, 0xf1, 0x17, 0x18, 0x19,
			0x1a, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48,
			0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a,
			0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92,
			0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2,
			0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2,
			0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf2,
			0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa };

	public static BitString[] m_Y_DC_Huffman_Table;
	public static BitString[] m_Y_AC_Huffman_Table;
	public static BitString[] m_CbCr_DC_Huffman_Table;
	public static BitString[] m_CbCr_AC_Huffman_Table;

	public static void CanonicalHuffman(int[][][] Images, String outputPath, int width, int height, int qs) {

		System.out.println("CanonicalHuffman start");
		m_width = width;
		m_height = height;
		quality_scale = qs;
		initQualityTables(quality_scale);
		InitHuffmanTable();
		finalBinString = "";
		try {
			FileOutputStream fos = new FileOutputStream(outputPath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			writeJpegHeader(bos);
			res = "";
			for (int i = 0; i < Images.length; i++) {
				outputBitString = new BitString[128];
				bitStringLength = 0;
				DoHuffmanEncoding(Images[i][0], 0, m_Y_DC_Huffman_Table, m_Y_AC_Huffman_Table);
				WriteBitString();
				DoHuffmanEncoding(Images[i][1], 1, m_CbCr_DC_Huffman_Table, m_CbCr_AC_Huffman_Table);
				WriteBitString();
				DoHuffmanEncoding(Images[i][2], 2, m_CbCr_DC_Huffman_Table, m_CbCr_AC_Huffman_Table);
				WriteBitString();
				System.gc();
			}
			finalLength = finalBinString.length() / 8;
			for (int i = 0; i < finalBinString.length() / 8; i++) {
				String t = finalBinString.substring(8 * i, 8 * i + 8);
				int n = Integer.parseInt(t, 2);
				if (n == 0xff) {
					String next = finalBinString.substring(8 * (i + 1), 8 * (i + 1) + 8);
					if (Integer.parseInt(next, 2) == 0xff) {
						finalLength -= 2;
						i++;
					} else {
						res += "ff00";
						finalLength++;
					}
					continue;
				}
				String hex = Integer.toHexString(n);
				if (hex.length() == 1) {
					hex = "0" + hex;
				}
				res += hex;
			}
			finalData = new byte[finalLength];
			for (int i = 0; i < finalLength; i++) {
				byte tmp = (byte) Integer.parseInt(res.substring(2 * i, 2 * i + 2), 16);
				finalData[i] = tmp;
			}
			for (int i = 0; i < finalLength; i++) {
				bos.write(finalData[i]);
				bos.flush();
			}
			bos.flush();
			byte[] end = { (byte) 0xFF, (byte) 0xD9 };
			bos.write(end);
			bos.flush();
			bos.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void InitHuffmanTable() {
		m_Y_DC_Huffman_Table = new BitString[256];
		m_Y_AC_Huffman_Table = new BitString[256];
		m_CbCr_DC_Huffman_Table = new BitString[256];
		m_CbCr_AC_Huffman_Table = new BitString[256];
		ComputeHuffmanTable(Standard_DC_Luminance_NRCodes, Standard_DC_Luminance_Values, m_Y_DC_Huffman_Table);
		ComputeHuffmanTable(Standard_AC_Luminance_NRCodes, Standard_AC_Luminance_Values, m_Y_AC_Huffman_Table);
		ComputeHuffmanTable(Standard_DC_Chrominance_NRCodes, Standard_DC_Chrominance_Values, m_CbCr_DC_Huffman_Table);
		ComputeHuffmanTable(Standard_AC_Chrominance_NRCodes, Standard_AC_Chrominance_Values, m_CbCr_AC_Huffman_Table);
	}

	public static void ComputeHuffmanTable(int[] nr_codes, int[] std_table, BitString[] huffman_table) {
		int pos_in_table = 0;
		short code_value = 0;
		for (int k = 1; k <= 16; k++) {
			for (int j = 1; j <= nr_codes[k - 1]; j++) {
				BitString t = new BitString();
				t.value = code_value;
				t.length = k;
				huffman_table[std_table[pos_in_table]] = t;
				pos_in_table++;
				code_value++;
			}
			code_value <<= 1;
		}
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

	public static void writeJpegHeader(BufferedOutputStream bos) throws IOException {
		byte[] SOI = { (byte) 0xFF, (byte) 0xD8 };
		bos.write(SOI);
		bos.flush();
		byte[] APPO = { (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 1, 1, 0, 0, 1, 0, 1, 0, 0 };
		bos.write(APPO);
		bos.flush();
		byte[] DQT = { (byte) 0xFF, (byte) 0xDB, 0, (byte) 132 };
		bos.write(DQT);
		bos.flush();
		bos.write(0);
		bos.flush();
		bos.write(m_YTable);
		bos.flush();
		bos.write(1);
		bos.flush();
		bos.write(m_CbCrTable);
		bos.flush();
		String hex_h = Integer.toHexString(m_height);
		if (hex_h.length() < 4) {
			int patch = 4 - hex_h.length();
			for (int i = 0; i < patch; i++) {
				hex_h = "0" + hex_h;
			}
		}
		byte h_1 = (byte) Integer.parseInt(hex_h.substring(0, 2), 16);
		byte h_2 = (byte) Integer.parseInt(hex_h.substring(2, 4), 16);
		String hex_w = Integer.toHexString(m_width);
		if (hex_w.length() < 4) {
			int patch = 4 - hex_w.length();
			for (int i = 0; i < patch; i++) {
				hex_w = "0" + hex_w;
			}
		}
		byte w_1 = (byte) Integer.parseInt(hex_w.substring(0, 2), 16);
		byte w_2 = (byte) Integer.parseInt(hex_w.substring(2, 4), 16);
		byte[] SOFO = { (byte) 0xFF, (byte) 0xC0, 0, 17, 8, h_1, h_2, w_1, w_2, 3, 1, 0x11, 0, 2, 0x11, 1, 3, 0x11, 1 };
		bos.write(SOFO);
		bos.flush();
		byte[] DHT = { (byte) 0xFF, (byte) 0xC4, 0x01, (byte) 0xA2, 0 };
		bos.write(DHT);
		bos.flush();
		for (int i = 0; i < Standard_DC_Luminance_NRCodes.length; i++) {
			bos.write((byte) Standard_DC_Luminance_NRCodes[i]);
			bos.flush();
		}
		for (int i = 0; i < Standard_DC_Luminance_Values.length; i++) {
			bos.write((byte) Standard_DC_Luminance_Values[i]);
			bos.flush();
		}
		bos.write(0x10);
		bos.flush();
		for (int i = 0; i < Standard_AC_Luminance_NRCodes.length; i++) {
			bos.write((byte) Standard_AC_Luminance_NRCodes[i]);
			bos.flush();
		}
		for (int i = 0; i < Standard_AC_Luminance_Values.length; i++) {
			bos.write((byte) Standard_AC_Luminance_Values[i]);
			bos.flush();
		}
		bos.write(0x01);
		bos.flush();
		for (int i = 0; i < Standard_DC_Chrominance_NRCodes.length; i++) {
			bos.write((byte) Standard_DC_Chrominance_NRCodes[i]);
			bos.flush();
		}
		for (int i = 0; i < Standard_DC_Chrominance_Values.length; i++) {
			bos.write((byte) Standard_DC_Chrominance_Values[i]);
			bos.flush();
		}
		bos.write(0x11);
		bos.flush();
		for (int i = 0; i < Standard_AC_Chrominance_NRCodes.length; i++) {
			bos.write((byte) Standard_AC_Chrominance_NRCodes[i]);
			bos.flush();
		}
		for (int i = 0; i < Standard_AC_Chrominance_Values.length; i++) {
			bos.write((byte) Standard_AC_Chrominance_Values[i]);
			bos.flush();
		}
		byte[] SOS = { (byte) 0xFF, (byte) 0xDA, 0, 12, 3, 1, 0, 2, 0x11, 3, 0x11, 0, 0x3F, 0 };
		bos.write(SOS);
		bos.flush();
	}

	public static int[] convertTo1D(int[][] array, int rows, int cols) {
		int[] res = new int[rows * cols];
		int idx = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				res[idx++] = array[i][j];
			}
		}
		return res;
	}

	public static void DoHuffmanEncoding(int[] quant, int code, BitString[] DC_HuffmanTable,
			BitString[] AC_HuffmanTable) {
		BitString EOB = AC_HuffmanTable[0x00];
		BitString sixteen_zeros = AC_HuffmanTable[0xF0];
		int idx = 0;
		int DCdiff = 0;
		switch (code) {
		case 0:
			DCdiff = (quant[0] - prev_DC_Y);
			prev_DC_Y = quant[0];
			break;
		case 1:
			DCdiff = (quant[0] - prev_DC_Cb);
			prev_DC_Cb = quant[0];
			break;
		case 2:
			DCdiff = (quant[0] - prev_DC_Cr);
			prev_DC_Cr = quant[0];
			break;
		default:
			break;
		}
		if (DCdiff == 0) {
			outputBitString[idx] = DC_HuffmanTable[0];
			idx++;
		} else {
			BitString bs = GetBitCode(DCdiff);
			outputBitString[idx] = DC_HuffmanTable[bs.length];
			idx++;
			outputBitString[idx] = bs;
			idx++;
		}
		int endPos = 63;
		while ((endPos > 0) && (quant[endPos] == 0)) {
			endPos--;
		}
		for (int i = 1; i <= endPos;) {
			int startPos = i;
			while ((quant[i] == 0) && (i <= endPos)) {
				i++;
			}
			int zeroCounts = i - startPos;
			if (zeroCounts >= 16) {
				for (int j = 1; j <= zeroCounts / 16; j++) {
					outputBitString[idx++] = sixteen_zeros;
				}
				zeroCounts = zeroCounts % 16;
			}
			BitString bs = GetBitCode(quant[i]);
			outputBitString[idx] = AC_HuffmanTable[(zeroCounts << 4) | bs.length];
			idx++;
			outputBitString[idx] = bs;
			idx++;
			i++;
		}
		if (endPos != 63) {
			outputBitString[idx++] = EOB;
		}
		bitStringLength = idx;
	}

	public static BitString GetBitCode(int value) {
		BitString ret = new BitString();
		int v = (value > 0) ? value : -value;
		int length = 0;
		for (length = 0; v != 0; v >>= 1) {
			length++;
		}
		ret.value = value > 0 ? value : ((1 << length) + value - 1);
		ret.length = length;
		return ret;
	}

	public static void WriteBitString() {
		String binString = "";

		for (int i = 0; i < bitStringLength; i++) {
			int val = outputBitString[i].value;
			int len = outputBitString[i].length;
			String t = Integer.toBinaryString(val);
			if (t.length() != len) {
				int p = len - t.length();
				for (int j = 0; j < p; j++) {
					t = "0" + t;
				}
			}
			binString += t;
		}
		finalBinString += binString;
	}
}
