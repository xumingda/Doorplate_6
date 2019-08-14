package com.yy.doorplate.protocol;


public class MonitorProtocol {

	public static final String TAG = "MonitorProtocol";

	// --°üÍ·°üÎ²
	public static final byte STX = (byte) 0x7E;
	public static final byte ETX = (byte) 0x7F;

	public byte[] pack(byte[] data, int offset, int length) {
		if (data != null && data.length > 0) {
			byte[] out = new byte[length + 5];
			int len = 0;
			out[len++] = STX;
			out[len++] = (byte) (length & 0xff);
			out[len++] = (byte) ((length >> 8) & 0xff);
			System.arraycopy(data, offset, out, len, length);
			len += length;
			byte check = 0;
			for (int i = 0; i < length + 3; i++) {
				check ^= out[i];
			}
			out[len++] = check;
			out[len++] = ETX;
			return out;
		}
		return null;
	}
}
