package com.eyunsdk.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.eyunsdk.device.DeviceManager;
import com.eyunsdk.device.DeviceManager.TX260ReadListener;

public class EYunSDKCore {

	private Context mContext = null;
	private DeviceManager mDevManager = null;

	private static final String TAG = "EYunSDKCore";

	public static void DEBUG(String content) {
		Log.d(TAG, content);
	}

	public static void DEBUG_HEX(byte[] data, int offset, int datalen) {
		String hexStr = "";
		for (int i = 0; i < datalen; i++) {

			if ((i % 16) == 0) {
				// Log.d(TAG,hexStr);
				hexStr = "";
			}
			if (Integer.toHexString(data[offset + i] & 0xff).length() == 1) {
				hexStr += "0";
			}
			hexStr += Integer.toHexString(data[offset + i] & 0xff)
					.toUpperCase() + " ";
		}
		Log.d(TAG, hexStr);
	}

	public static void DEBUG_HEX(char[] data, int offset, int datalen) {
		String hexStr = "";
		for (int i = 0; i < datalen; i++) {

			if ((i % 16) == 0) {
				// Log.d(TAG,hexStr);
				hexStr = "";
			}
			if (Integer.toHexString(data[offset + i] & 0xff).length() == 1) {
				hexStr += "0";
			}
			hexStr += Integer.toHexString(data[offset + i] & 0xff)
					.toUpperCase() + " ";
		}
		Log.d(TAG, hexStr);
	}

	public EYunSDKCore(Context context) {
		mContext = context;
		mDevManager = new DeviceManager(context);

	}

	public int GetRfidId(byte[] out, int offset, int[] outlen) {
		return mDevManager.getRfidId(out, offset, outlen);
	}

	@SuppressLint("NewApi")
	public void Init() {
		mDevManager.Init();
	}

	public void startTX260Read(TX260ReadListener TX260listener) {
		mDevManager.startTX260Read(TX260listener);
	}

	// 0:¾²Òô 1£º¹ã²¥ 2£ºÄÚ²¿
	public void setAudioMode(int mode) {
		mDevManager.setAudioMode(mode);
	}

	public void setDoor(boolean enable) {
		mDevManager.setDoor(enable);
	}

	public void setRebootClock(int date, int hour, int min) {
		mDevManager.setRebootClock(date, hour, min);
	}
	
	public void setKeycode(int code) {
		mDevManager.setKeycode(code);
	}

	public void Finish() {
		mDevManager.Finish();
	}
}
