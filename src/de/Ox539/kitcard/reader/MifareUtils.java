package de.Ox539.kitcard.reader;

public class MifareUtils {

	static int toUInt16BE(byte b1, byte b2) {
		return (MifareUtils.byteToInt(b1) << 8) + MifareUtils.byteToInt(b2);
	}

	static int byteToInt(byte value) {
		return (value < 0) ? value + 256 : value;
	}

	static byte intToByte(int value) {
		return (byte)((value > 128) ? value - 256 : value);
	}

	static int toUInt16LE(byte b1, byte b2) {
		return toUInt16BE(b2, b1);
	}

}
