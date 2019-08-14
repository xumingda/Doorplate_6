package com.yy.doorplate.tool;

import android.content.Context;
import android.util.Log;

import com.common.AppLog;

public class SystemManager {
	public static SystemManager Instacne = new SystemManager();

	private static final boolean isLogInfoOn = true;
	private static final boolean isLogErrOn = true;
	private static final boolean isLogWarnOn = true;
	private static final boolean isLogDbgOn = true;

	private Context mContext = null;

	public static void LOGI(String tag, String msg) {
		 if(isLogInfoOn){
		//Log.i(tag, msg);
		AppLog.i(tag,msg);
		 }
	}

	public static void LOGE(String tag, String msg) {
		if (isLogErrOn) {
			//Log.e(tag, msg);
			AppLog.e(msg);
		}
	}

	public static void LOGW(String tag, String msg) {
		if (isLogWarnOn) {
			//Log.w(tag, msg);
			AppLog.w(msg);
		}
	}

	public static void LOGD(String tag, String msg) {
		if (isLogDbgOn) {
			//Log.d(tag, msg);
			AppLog.d(tag,msg);
		}
	}

	public static void LogHex(String tag, byte[] data, int datalen) {
		String hexStr = "";
		for (int i = 0; i < datalen; i++) {

			if ((i % 16) == 0) {
				Log.d(tag, hexStr);
				hexStr = "";
			}
			if (Integer.toHexString(data[i] & 0xff).length() == 1) {
				hexStr += "0";
			}
			hexStr += Integer.toHexString(data[i] & 0xff).toUpperCase() + " ";
		}
		//Log.d(tag, hexStr);
		AppLog.d(tag,hexStr);
	}
}
