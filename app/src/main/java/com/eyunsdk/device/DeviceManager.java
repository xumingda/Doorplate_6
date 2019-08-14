package com.eyunsdk.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.common.Mutex;
import com.common.Unit;
import com.driver.DriverService;
import com.eyunsdk.core.EYunDefine;
import com.yy.doorplate.MyApplication;

public class DeviceManager {

	private Context mContext = null;
	private DriverService mExternDrv = new DriverService();
	private OutputStream mExternOutputStream = null;
	private InputStream mExternInputStream = null;
	private OutputStream mBoardOutputStream = null;
	private InputStream mBoardInputStream = null;
	private static final String EXTERN_DEV_PATH = "/dev/ttyS4";

	private static final String TAG = "EYunSDK";

	private Mutex mutexExtern = new Mutex();

	private TX260ReadThread TX260Thread = null;
	private TX260ReadListener TX260listener = null;
	private boolean TX260isRunning = true;

	public DeviceManager(Context context) {
		mContext = context;
	}

	public void Init() {
		mExternOutputStream = null;
		mExternInputStream = null;
		mBoardInputStream = null;
		mBoardOutputStream = null;

		if (mExternDrv.open(EXTERN_DEV_PATH, 0) > 0) {
			// 3288
			mExternDrv.configSerial(9600, 8, 1, 'N', (char) 1, (char) 0);
			// mExternDrv.configSerial(9600, 8, 1, 'N', (char) 0, (char) 0);
			mExternOutputStream = mExternDrv.getOutputStream();
			mExternInputStream = mExternDrv.getInputStream();
		}
	}

	public void Finish() {
		if (TX260Thread != null) {
			TX260isRunning = false;
		}
		mExternDrv.close();
	}

	public void startTX260Read(TX260ReadListener TX260listener) {
		this.TX260listener = TX260listener;
		TX260isRunning = true;
		if (TX260Thread == null) {
			TX260Thread = new TX260ReadThread();
			TX260Thread.start();
		}
	}

	private int recvLen = 0;
	private byte[] recvbuf = new byte[1024];

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			recvLen = 0;
			recvbuf = new byte[1024];
		};
	};

	public interface TX260ReadListener {
		public abstract void OnCrad(String card);
	}

	private class TX260ReadThread extends Thread {

		byte[] card = null;

		@Override
		public void run() {
			super.run();
			while (mExternInputStream != null && TX260isRunning) {
				// TX260
				// recvLen = mExternDrv.select(0, 1, 1000 * 1000);
				// if (recvLen == DriverService.REQ_TIMEOUT) {
				// // Log.e(TAG, "wait timeout !");
				// continue;
				// }
				// if (recvLen != DriverService.READ_ALLOWED) {
				// Log.e(TAG, "read is not allowed!");
				// continue;
				// }
				// try {
				// recvLen = mExternInputStream.read(recvbuf);
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// if (recvLen > 0) {
				// MyApplication.debugHex(TAG, recvbuf, recvLen);
				// analysis_260();
				// }
				// TX600
				int len = 0;
				byte[] temp = new byte[1024];
				try {
					len = mExternInputStream.read(temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (len > 0) {
					System.arraycopy(temp, 0, recvbuf, recvLen, len);
					recvLen += len;
					handler.removeMessages(0);
					handler.sendEmptyMessageDelayed(0, 1000);
					if (recvLen == 5) {
						MyApplication.debugHex(TAG, recvbuf, recvLen);
						analysis_600();
						recvLen = 0;
						recvbuf = new byte[1024];
						handler.removeMessages(0);
					}
				}
			}
		}

		private void analysis_600() {
			if ((recvbuf[0] ^ recvbuf[1] ^ recvbuf[2] ^ recvbuf[3]) == recvbuf[4]) {
				String hexStr = "";
				MyApplication application = (MyApplication) mContext;
				if (TextUtils.isEmpty(application.equInfoModel.readCardRule)) {
					for (int i = recvLen - 2; i >= 0; i--) {
						if (Integer.toHexString(recvbuf[i] & 0xff).length() == 1) {
							hexStr += "0";
						}
						hexStr += Integer.toHexString(recvbuf[i] & 0xff);
					}
					hexStr = Long.parseLong(hexStr, 16) + "";
					if (hexStr.length() == 9) {
						hexStr = "0" + hexStr;
					} else if (hexStr.length() == 8) {
						hexStr = "00" + hexStr;
					}
					if (TX260listener != null) {
						TX260listener.OnCrad(hexStr);
					}
					return;
				}
				if (application.equInfoModel.readCardRule.equals("110")) {
					for (int i = 0; i < recvLen - 1; i++) {
						if (Integer.toHexString(recvbuf[i] & 0xff).length() == 1) {
							hexStr += "0";
						}
						hexStr += Integer.toHexString(recvbuf[i] & 0xff);
					}
					hexStr = Long.parseLong(hexStr, 16) + "";
					if (hexStr.length() == 9) {
						hexStr = "0" + hexStr;
					} else if (hexStr.length() == 8) {
						hexStr = "00" + hexStr;
					}
				} else if (application.equInfoModel.readCardRule.equals("116")) {
					for (int i = 0; i < recvLen - 1; i++) {
						if (Integer.toHexString(recvbuf[i] & 0xff).length() == 1) {
							hexStr += "0";
						}
						hexStr += Integer.toHexString(recvbuf[i] & 0xff)
								.toUpperCase();
					}
				} else if (application.equInfoModel.readCardRule.equals("210")) {
					for (int i = recvLen - 2; i >= 0; i--) {
						if (Integer.toHexString(recvbuf[i] & 0xff).length() == 1) {
							hexStr += "0";
						}
						hexStr += Integer.toHexString(recvbuf[i] & 0xff);
					}
					hexStr = Long.parseLong(hexStr, 16) + "";
					if (hexStr.length() == 9) {
						hexStr = "0" + hexStr;
					} else if (hexStr.length() == 8) {
						hexStr = "00" + hexStr;
					}
				} else if (application.equInfoModel.readCardRule.equals("216")) {
					for (int i = recvLen - 2; i >= 0; i--) {
						if (Integer.toHexString(recvbuf[i] & 0xff).length() == 1) {
							hexStr += "0";
						}
						hexStr += Integer.toHexString(recvbuf[i] & 0xff)
								.toUpperCase();
					}
				}
				if (TX260listener != null) {
					TX260listener.OnCrad(hexStr);
				}
			}
		}

		private void analysis_260() {
			if (recvLen != 12 || recvbuf[0] != (byte) 0x55
					|| recvbuf[1] != (byte) 0xAA) {
				Log.e(TAG, "TX260 head is error");
				return;
			}
			byte check = 0;
			for (int i = 2; i < 11; i++) {
				check ^= recvbuf[i];
			}
			if (check != recvbuf[11]) {
				Log.e(TAG, "TX260 check is error");
				return;
			}
			switch (recvbuf[2]) {
			case (byte) 0x00:
				break;
			case (byte) 0x01: {
				card = new byte[4];
				byte[] out = new byte[4];
				System.arraycopy(recvbuf, 7, card, 0, 4);
				for (int i = 0; i < 4; i++) {
					out[i] = card[3 - i];
				}
				MyApplication.debugHex(TAG, out, out.length);
				if (TX260listener != null) {
					TX260listener.OnCrad(Unit.byteToHexStr(out, 0, 4));
				}
				break;
			}
			case (byte) 0x02: {
				card = new byte[7];
				byte[] out = new byte[7];
				System.arraycopy(recvbuf, 4, card, 0, 7);
				for (int i = 0; i < 7; i++) {
					out[i] = card[6 - i];
				}
				MyApplication.debugHex(TAG, out, out.length);
				if (TX260listener != null) {
					TX260listener.OnCrad(Unit.byteToHexStr(out, 0, 7));
				}
				break;
			}
			case (byte) 0x03:
				break;
			case (byte) 0x04:
				break;
			}
		}
	}

	private class ExternPacket {

		public static final byte PACKET_STX = 0X7E;
		public static final byte PACKET_ETX = 0X7F;

		public static final byte CMD_GET_RFID = 0X01;
		public static final byte CMD_UNLOCK = 0X02;
		public static final byte CMD_GET_LOCK = 0X03;

		public byte stx;
		public byte cmd;
		public int length;
		public byte[] data;
		public byte check;
		public byte etx;

		public int toBytes(byte[] out, int offset) {
			int offer = offset;

			out[offer++] = PACKET_STX;
			out[offer++] = cmd;
			out[offer++] = (byte) (length & 0xff);
			out[offer++] = (byte) ((length >> 8) & 0xff);
			if (length > 0 && data != null) {
				System.arraycopy(data, 0, out, offer, length);
			}
			offer += length;

			check = 0;
			for (int i = 0; i < offer; i++) {
				check ^= out[i];
			}

			out[offer++] = check;
			out[offer++] = PACKET_ETX;

			return (offer - offset);
		}

		public int getBytesLength() {
			return length + 6;
		}

	}

	public ExternPacket externCmdOperate(ExternPacket request) {

		if (mExternOutputStream == null || mExternInputStream == null) {
			// EYunSDKCore.DEBUG("=== didn't open port!");
			return null;
		}

		ExternPacket respond = null;
		Time startTime = new Time();
		Time curTime = new Time();
		boolean isWait = true;
		byte[] sendbuf = new byte[request.getBytesLength()];
		byte[] recvbuf = new byte[100];
		int recvlen = 0;
		int state = 0;
		int count = 0;
		request.toBytes(sendbuf, 0);

		try {
			// EYunSDKCore.DEBUG("send:");
			// EYunSDKCore.DEBUG_HEX(sendbuf, 0, sendbuf.length);
			mExternOutputStream.write(sendbuf, 0, sendbuf.length);
			startTime.setToNow();
			while (isWait) {
				recvlen = mExternInputStream.read(recvbuf);
				if (recvlen <= 0) {
					curTime.setToNow();
					if ((curTime.toMillis(false) - startTime.toMillis(false)) > 1000) {
						isWait = false;
						respond = null;
						// EYunSDKCore.DEBUG("wait timeout!");

					}
					continue;
				}
				// EYunSDKCore.DEBUG("recv:");
				// EYunSDKCore.DEBUG_HEX(recvbuf, 0, recvlen);
				for (int i = 0; i < recvlen; i++) {
					switch (state) {
					case 0:
						if (recvbuf[i] == ExternPacket.PACKET_STX) {
							state++;
							respond = new ExternPacket();
							respond.check = 0;
							respond.check ^= recvbuf[i];
						}
						break;
					case 1:
						respond.cmd = recvbuf[i];
						state++;
						respond.check ^= recvbuf[i];
						break;
					case 2:
						respond.length = recvbuf[i];
						state++;
						respond.check ^= recvbuf[i];
						break;
					case 3:
						respond.length |= (recvbuf[i] << 8);
						state++;
						respond.check ^= recvbuf[i];
						if (respond.length == 0) {
							state++;
						} else {
							respond.data = new byte[respond.length];
						}
						count = 0;
						break;
					case 4:
						respond.data[count++] = recvbuf[i];
						respond.check ^= recvbuf[i];
						if (count == respond.length) {
							state++;
						}
						break;
					case 5:
						if (respond.check != recvbuf[i]) {
							state = 0;
							respond = null;
						} else {
							state++;
						}
						break;
					case 6:
						if (recvbuf[i] != ExternPacket.PACKET_ETX) {
							state = 0;
							respond = null;
						} else {
							state++;
						}
						break;
					}
				}

				startTime.setToNow();
				if (state != 7) {
					continue;
				}
				isWait = false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return respond;
	}

	private class BoardCtrl {
		private byte[] mSNInforBuf = null;
		private static final byte CMD_GET_SN = 0X00;
		private byte[] mBoardRecvBuf = new byte[600];
		private Mutex mutexBoard = new Mutex();
		private static final byte CMD_AUDIO_MODE = 0x06;
		private static final byte CMD_SET_DOOR = 0x0D;
		private static final byte CMD_SET_ALARM = 0x04;
		private static final byte CMD_SET_REBOOT = 0x05;
		private static final byte CMD_SET_KEYCODE = 0x0A;

		public void setKeycode(int code) {
			if (!mutexBoard.lock(500)) {
				return;
			}
			byte[] buf = new byte[1];
			buf[0] = (byte) (code & 0xff);
			boardCmdWrite(CMD_SET_KEYCODE, 1, buf);
			mutexBoard.unlock();
		}

		// 0:静音 1：广播 2：内部
		public void setAudioMode(int mode) {
			if (!mutexBoard.lock(500)) {
				return;
			}
			Log.d(TAG, "setAudioMode mode : " + mode);
			byte[] buf = new byte[1];
			buf[0] = (byte) (mode & 0xff);
			boardCmdWrite(CMD_AUDIO_MODE, 1, buf);
			mutexBoard.unlock();
		}

		public void setDoor(boolean enable) {
			if (!mutexBoard.lock(500)) {
				return;
			}
			Log.d(TAG, "setDoor  : " + enable);
			byte[] buf = new byte[1];
			if (enable) {
				buf[0] = 1;
			} else {
				buf[0] = 0;
			}
			boardCmdWrite(CMD_SET_DOOR, 1, buf);
			mutexBoard.unlock();
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
				// Log.i(TAG, "CMD ERROR!");
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

		// 重启时间设置 hour:小时 min:分钟
		public void setRebootClock(int date, int hour, int min) {

			if (!mutexBoard.lock(500)) {
				return;
			}
			Log.d(TAG, "date :" + date + " hour :" + hour + " min:" + min);
			byte[] buf = new byte[3];
			buf[0] = (byte) (date & 0xff);
			buf[1] = (byte) (hour & 0xff);
			buf[2] = (byte) (min & 0xff);
			boardCmdWrite(CMD_SET_ALARM, 3, buf);

			buf[0] = (byte) (0X55); // 0X55:掉电定时重启 0X11:立即掉电重启

			boardCmdWrite(CMD_SET_REBOOT, 1, buf);

			mutexBoard.unlock();

		}

	}

	public int getRfidId(byte[] out, int offset, int[] outlen) {
		int ret = EYunDefine.RfidType.NoRfidCard;
		if (!mutexExtern.lock(500)) {
			return ret;
		}

		ExternPacket request = new ExternPacket();
		request.length = 0;
		request.cmd = ExternPacket.CMD_GET_RFID;
		// Log.i(TAG, "2  mExternOutputStream = " + mExternOutputStream
		// + " mExternInputStream = " + mExternInputStream);
		ExternPacket respond = externCmdOperate(request);

		if (respond != null) {

			if (respond.length > 3) {
				if (respond.data[0] == 0X00) {
					switch (respond.data[1]) {
					case 'M':
						ret = EYunDefine.RfidType.MifareCard;
						break;
					case 'A':
						ret = EYunDefine.RfidType.TypeACard;
						break;
					case 'B':
						ret = EYunDefine.RfidType.TypeBCard;
						break;
					case 'D':
						ret = EYunDefine.RfidType.CHNIDCard;
						break;
					}
					outlen[0] = respond.data[2];
					System.arraycopy(respond.data, 3, out, offset, outlen[0]);
				}
			}
		}
		mutexExtern.unlock();
		return ret;
	}

	public String getDeviceSN() {
		BoardCtrl boardCtrl = new BoardCtrl();
		boardCtrl.getSnInfo();
		return boardCtrl.getLocalSN();
	}

	public void setAudioMode(int mode) {
		BoardCtrl boardCtrl = new BoardCtrl();
		boardCtrl.setAudioMode(mode);
	}

	public void setDoor(boolean enable) {
		BoardCtrl boardCtrl = new BoardCtrl();
		boardCtrl.setDoor(enable);
	}

	public void setRebootClock(int date, int hour, int min) {
		BoardCtrl boardCtrl = new BoardCtrl();
		boardCtrl.setRebootClock(date, hour, min);
	}

	public void setKeycode(int code) {
		BoardCtrl boardCtrl = new BoardCtrl();
		boardCtrl.setKeycode(code);
	}
}
