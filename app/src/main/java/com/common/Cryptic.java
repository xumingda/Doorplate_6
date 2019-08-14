package com.common;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Cryptic {

	/**
	 * @see DESº”√‹
	 */
	public static byte[] desEncrypt(byte[] data, byte[] key) throws Exception {
		data = dataTo8(data);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		return cipher.doFinal(data);
	}

	/**
	 * @see DESΩ‚√‹
	 */
	public static byte[] desDecrypt(byte[] data, byte[] key) throws Exception {
		data = dataTo8(data);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return cipher.doFinal(data);
	}

	public static byte[] des3Encrypt(byte[] data, byte[] key) throws Exception {
		data = dataTo8(data);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		return cipher.doFinal(data);
	}

	public static byte[] des3Decrypt(byte[] data, byte[] key) throws Exception {
		data = dataTo8(data);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return cipher.doFinal(data);
	}

	private static byte[] dataTo8(byte[] data) {
		if (data.length % 8 == 0) {
			return data;
		} else {
			byte[] d = new byte[(data.length / 8 + 1) * 8];
			System.arraycopy(data, 0, d, 0, data.length);
			return d;
		}
	}
}
