package com.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UsbHubManager {

	public class HubUsbDevice {
		private int mHubPortId;
		private int mDevNumber;
		private String mFileDesc;
		private boolean isConnected = false;

		public HubUsbDevice(int hubPortId, int devNumber, String fileDesc) {
			// TODO Auto-generated constructor stub
			mHubPortId = hubPortId;
			mDevNumber = devNumber;
			mFileDesc = fileDesc;
			isConnected = true;
		}

		public int getHubPortId() {
			return mHubPortId;
		}

		public int getDevNumber() {
			return mDevNumber;
		}

		public String getFileDesc() {
			return mFileDesc;
		}

		public boolean isConnected() {
			return isConnected;
		}
	}

	private static int Byte2Integer(byte[] buf, int length) {
		int ret = 0;
		for (int i = 0; i < length; i++) {
			if (buf[i] < 0x30) {
				break;
			}
			ret *= 10;
			ret += (buf[i] - 0x30);
		}

		return ret;
	}

	public int ScanUsbDevices(String hubpath, String tag, String devname,
			HubUsbDevice[] hubUsbDevs) {
		int maxDevs = hubUsbDevs.length;
		int mCount = 0;
		File hubdir = new File(hubpath);
		if (hubdir == null) {
			return mCount;
		}

		if (!hubdir.exists()) {
			// SystemApplication.Debug("can't open hubdir");
			return mCount;
		}

		// SystemApplication.Debug("open hubdir ok!");

		File[] fileList = hubdir.listFiles();

		if (fileList.length == 0) {
			// SystemApplication.Debug("can't read file list!");
			return mCount;
		}

		for (int i = 0; i < fileList.length; i++) {
			if (!fileList[i].isDirectory()) {
				continue;
			}

			int j = 0;
			for (j = 0; j < maxDevs; j++) {
				// * /sys/devices/101c0000.usb/usb1/1-1/1-1.3/1-1.3.1

				if (fileList[i].getName().equals(tag + "." + (j + 1))) {
					break;
				}
			}

			if (j == maxDevs) {
				continue;
			}

			// SystemApplication.Debug("find tag device:"+tag+"."+(j+1));

			// * /sys/devices/101c0000.usb/usb1/1-1/1-1.3/devnum
			String path = hubpath + "/" + tag + "." + (j + 1) + "/devnum";
			// SystemApplication.Debug("path:"+path);
			File file = new File(path);
			if (!file.exists()) {
				continue;
			}

			FileInputStream fin;
			byte[] buffer = new byte[10];
			int length = 0;

			try {
				fin = new FileInputStream(path);
				if (fin.available() == 0) {
					continue;
				}
				length = fin.read(buffer, 0, 3);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int devnum = Byte2Integer(buffer, length);

			// * /sys/devices/101c0000.usb/usb1/1-1/1-1.3/1-1.3:1.0
			path = hubpath + "/" + tag + "." + (j + 1) + "/" + tag + "."
					+ (j + 1) + ":1.0";

			file = new File(path);

			if (!file.exists()) {
				continue;
			}

			if (!file.isDirectory()) {
				continue;
			}

			File[] devFileList = file.listFiles();
			int x;
			for (x = 0; x < devFileList.length; x++) {
				if (devFileList[x].getName().indexOf(devname) == 0) {
					break;
				}
			}

			if (x == devFileList.length) {
				continue;
			}
			// SystemApplication.Debug("====================================");
			// SystemApplication.Debug("find target usb devices!");
			// SystemApplication.Debug("port:"+j+" devnum:"+devnum+"  filedesc:/dev/"+devFileList[x].getName());
			// SystemApplication.Debug("====================================");
			if (hubUsbDevs[j] == null) {
				hubUsbDevs[j] = new HubUsbDevice(j, devnum, "/dev/"
						+ devFileList[x].getName());
			}
			mCount++;
		}

		return mCount;
	}
}
