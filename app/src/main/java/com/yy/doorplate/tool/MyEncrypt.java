package com.yy.doorplate.tool;

import com.common.Cryptic;

public class MyEncrypt {

	private static final String tag = "MyEncrypt";

	public static byte[] desEncrypt(byte[] data, byte[] key) {

		try {
			SystemManager.LogHex(tag, key, key.length);
			return Cryptic.desEncrypt(data, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] desUnEncrypt(byte[] data, byte[] key) {
		try {
			return Cryptic.desDecrypt(data, key);
		} catch (Exception e) {

		}
		return null;
	}

	public static byte[] des3Encrypt(byte[] data, byte[] key) {

		try {
			return Cryptic.des3Encrypt(data, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] des3UnEncrypt(byte[] data, byte[] key) {
		try {
			return Cryptic.des3Decrypt(data, key);
		} catch (Exception e) {

		}
		return null;
	}

}
