package com.driver;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DriverService {

	String TAG = "DriverService";
	public static final int WRITE_ALLOWED = 1;
	public static final int READ_ALLOWED = 2;
	public static final int REQ_TIMEOUT = 0;
	private int fd = -1;

	public OutputStream mOutputStream;
	public InputStream mInputStream;
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public DriverService() {

	}

	public int open(String path, int flag) {

		fd = drvOpen(path, flag);

		mFd = drvIO(fd);

		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
		mOutputStream = (OutputStream) mFileOutputStream;
		mInputStream = (InputStream) mFileInputStream;

		return fd;
	}

	public boolean isOpen() {
		if (fd < 0) {
			return false;
		} else {
			return true;
		}

	}

	public void close() {
		if (fd < 0) {
			return;
		}
		drvClose(fd);
		fd = -1;
	}

	public int write(byte[] data, int size) {
		try {
			mOutputStream.write(data, 0, size);
			return size;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int read(byte[] data, int size) {
		try {
			return mInputStream.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void relase() {
		close();
		try {
			mInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public InputStream getInputStream() {

		return mInputStream;
	}

	public OutputStream getOutputStream() {

		return mOutputStream;
	}

	// --minrecv->最小接收字节，用于堵塞读取
	// --timeout->接收等待超时，单位为0.1s一个计数
	public int configSerial(int baudrate, int databits, int stopbits,
			char parity, char minrecv, char timeout) {
		return drvConfigSerial(fd, baudrate, databits, stopbits, parity,
				minrecv, timeout);
	}

	/*
	 * public int setBlocking(){ return drvFcntl(fd, 0); }
	 */

	public int select(int iswrite, int isread, int timeout) {
		if (fd < 0) {
			return -2;
		}
		return drvSelect(fd, iswrite, isread, timeout);
	}

	public native int drvOpen(String path, int flag);

	public native void drvClose(int fd);

	public native int drvWrite(int fd, int size, byte[] data);

	public native int drvRead(int fd, int size, byte[] data);

	public native int drvConfigSerial(int fd, int baudrate, int databits,
			int stopbits, char parity, char minrecv, char timeout);

	public native FileDescriptor drvIO(int fd);

	public native int drvSelect(int fd, int iswrite, int isread, int timeout);

	public native int drvFcntl(int fd, int flag);

	static {
		System.loadLibrary("driverJni");
	}
}
