package com.camera;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.common.Mutex;
import com.driver.DriverService;

public class BoardCtrl {

	private byte[] mSNInforBuf = null;
	private static final byte CMD_GET_SN = 0X00;
	private static final byte CMD_CAM_LIGHT = 0X08; // --ÉãÏñÍ·²¹¹â
	private byte[] mBoardRecvBuf = new byte[600];
	private Mutex mutexBoard = new Mutex();
	private DriverService mBoardDrv = new DriverService();

	private OutputStream mBoardOutputStream = null;
	private InputStream mBoardInputStream = null;
	private static final String BOARD_DEV_PATH = "/dev/product_driver";

	public void start() {
		if (mBoardDrv.open(BOARD_DEV_PATH, 0) > 0) {
			mBoardOutputStream = mBoardDrv.getOutputStream();
			mBoardInputStream = mBoardDrv.getInputStream();
		}
	}

	public void stop() {
		if (mBoardOutputStream != null) {
			mBoardDrv.close();
			try {
				mBoardOutputStream.close();
				mBoardInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private synchronized void boardCmdWrite(byte cmd, int len, byte[] data) {
		if (mBoardInputStream == null || mBoardOutputStream == null) {
			return;
		}
		byte[] write_buf = new byte[255];
		write_buf[0] = cmd;
		write_buf[1] = (byte) len;
		write_buf[2] = 0;
		write_buf[3] = 0;
		write_buf[4] = 0;
		if (data != null || len > 0) {
			System.arraycopy(data, 0, write_buf, 5, len);
		}
		try {
			mBoardOutputStream.write(write_buf, 0, len + 5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized int boardCmdRead() {

		int ret = -1;

		if (mBoardInputStream == null || mBoardOutputStream == null) {
			return ret;
		}

		try {
			ret = mBoardInputStream.read(mBoardRecvBuf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	public void getSnInfo() {

		if (mSNInforBuf != null) {
			return;
		}

		if (!mutexBoard.lock(500)) {
			return;
		}

		boardCmdWrite(CMD_GET_SN, 0, null);

		if (boardCmdRead() <= 0) {

			mutexBoard.unlock();
		}

		if ((mBoardRecvBuf[0] & 0XFF) != CMD_GET_SN) {
			mutexBoard.unlock();
		}

		int len = 0;
		len = mBoardRecvBuf[1] & 0xff;
		len += (mBoardRecvBuf[2] & 0xff) * 256;

		// Log.i(TAG, "len:" + len);
		if (len > 0) {
			mSNInforBuf = new byte[len];
			System.arraycopy(mBoardRecvBuf, 5, mSNInforBuf, 0, len);
		} else {
			mSNInforBuf = null;
		}

		mutexBoard.unlock();
	}

	public String getLocalSN() {
		if (mSNInforBuf == null) {
			return null;
		}
		int len = 0;
		len = mSNInforBuf[0] & 0xff;
		len += (mSNInforBuf[1] & 0xff) * 256;
		if (len > 0) {
			return new String(mSNInforBuf, 2, len);
		} else {
			return null;
		}
	}

	public void setCamLight(boolean onoff) {
		if (mSNInforBuf != null) {
			return;
		}

		if (!mutexBoard.lock(500)) {
			return;
		}

		byte[] data = new byte[1];
		if (onoff) {
			data[0] = 1;
		} else {
			data[0] = 0;
		}
		boardCmdWrite(CMD_CAM_LIGHT, 1, data);

		mutexBoard.unlock();
	}
}
