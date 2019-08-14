package com.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.text.format.Time;
import android.util.Log;

import com.yy.doorplate.MyApplication;

public class UsbProcess {

	private static final String TAG = "UsbProcess";

	private InputStream inputStream = null;
	private OutputStream outputStream = null;

	public DriverService mDev;

	private static final String HubRootPath = "/sys/devices/101c0000.usb/usb1/1-1/";
	private static final String Hub1_tag = "1-1.3";
	private static String Hub1_Path = HubRootPath + Hub1_tag;
	private static final String Hub2_tag = "1-1.4";
	private static String Hub2_Path = HubRootPath + Hub2_tag;

	public UsbProcess(DriverService dev) {
		mDev = dev;
	}

	public int initUsb(String path, int baudrate) {
		int fd = -1;

		mDev.close();
		fd = mDev.open(path, 0);
		if (fd < 0) {
			Log.e(TAG, "串口" + path + "打开失败");
			// return -1;
		} else {
			Log.i(TAG, "串口" + path + "打开成功");
		}
		// --堵塞
		mDev.configSerial(baudrate, 8, 1, 'N', (char) 0, (char) 0);

		outputStream = mDev.getOutputStream();
		inputStream = mDev.getInputStream();
		return fd;
	}

	public void close() {
		mDev.close();
	}

	public void write(byte[] str, int sendlen) {

		Log.i(TAG, "sendlen = " + sendlen);
		try {
			MyApplication.debugHex("write", str, sendlen);
			outputStream.write(str, 0, sendlen);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int read(byte[] buffer, int timeout) {

		int readlen = 0;
		boolean isWait = true;
		Time startTime = new Time();
		Time curTime = new Time();
		if (mDev.isOpen()) {
		} else {
			return 0;
		}
		startTime.setToNow();
		do {
			curTime.setToNow();
			try {
				readlen = inputStream.read(buffer);
				// Log.i(TAG, "READLEN = " + readlen);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (readlen > 0) {
				// Log.i(TAG, "readlen = " + readlen);
				return readlen;
			}
			if ((curTime.toMillis(false) - startTime.toMillis(false)) >= timeout * 1000) {

				Log.d(TAG, "uart Timeout");
				isWait = false;
				break;
			}
		} while (isWait);

		return 0;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
}
