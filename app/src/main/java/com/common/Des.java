package com.common;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class Des {

	/**
	 * @see DESº”√‹
	 */
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception 
	{
		try 
		{
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			cipher.init(1, secretKeySpec);
			return cipher.doFinal(data);
		} 
		catch (Exception e) 
		{
			Log.e("ExcepDESECB", e.toString());
		}
		throw new Exception();
	}

	/**
	 * @see DESΩ‚√‹
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception 
	{
		try 
		{
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			cipher.init(2, secretKeySpec);
			return cipher.doFinal(data);
		} 
		catch (Exception e) 
		{
			Log.e("ExcepDESECB", e.toString());
		}
		throw new Exception();
	}


}
