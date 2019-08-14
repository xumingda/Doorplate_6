package com.yy.doorplate.communication;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yy.doorplate.communication.MySocket.Callback;
import com.yy.doorplate.protocol.TcpProtocol;
import com.yy.doorplate.protocol.TcpProtocol.AppData;
import com.yy.doorplate.tool.General;
import com.yy.doorplate.tool.MyEncrypt;
import com.yy.doorplate.tool.SystemManager;

public class TcpProcess {

	public static final byte[] FinalBao = { 0x38, 0x33, 0x35, 0x39, 0x32, 0x36,
			0x30, 0x30 };
	private static final String TAG = "TcpProcess";
	private MySocket mMySocket;

	private TcpCallback mCallback;

	private TcpProtocol mTcpProtocol;
	private boolean isStart = false;

	public static final int SERVICE_TYPE_PRE = 0;
	public static final int SERVICE_TYPE_CTRL = 1;

	public static byte EncryptType = TcpProtocol.ENCRYPT_TYPE_NO;

	public int mStep = 0;
	private Timer mTimerHeart = null;

	public static final int STEP_START = 1;
	public static final int STEP_AUTH = 2;
	public static final int STEP_HEART = 3;
	public static final int STEP_END = 4;

	private String ip;
	private int port;

	public TcpProcess(TcpCallback callback) {

		mCallback = callback;
		mTcpProtocol = new TcpProtocol();
	}

	public interface TcpCallback {
		public abstract void request(byte cmd, byte[] data, String sn);

		public abstract void connectResult(byte cmd, boolean ret);
	}

	public void init() {
		mMySocket = new MySocket(new Callback() {

			@Override
			public void read(byte[] data, int len) {
				byte[] app = new byte[64];
				int applen = mTcpProtocol.unpack(data, len, app);
				if (applen != 0) {
					analisys(app, applen);
				} else {
					switch (mStep) {
					case STEP_START:
						start();
						break;
					case STEP_AUTH:
						auth();
						break;
					case STEP_HEART:
						heart();
						break;
					}
				}
			}

			@Override
			public void getState(int state) {
				switch (state) {
				case MySocket.CONNECT_STATE_ON:
					isStart = true;
					start();
					break;
				case MySocket.CONNECT_STATE_OFF:
					isStart = false;
					if (mTimerHeart != null) {
						mTimerHeart.cancel();
						mTimerHeart = null;
					}
					break;
				}
			}

			@Override
			public void send(byte cmd, boolean ret) {
				if (!ret) {
					switch (cmd) {
					case TcpProtocol.PACKET_CMD_START:
						start();
						break;
					case TcpProtocol.PACKET_CMD_AUTH:
						auth();
						break;
					}
				}
			}
		});
	}

	public void startTcp(String ip, String port) {
		if (!isStart) {
			if (port != null && ip != null) {
				isStart = true;
				this.ip = ip;
				this.port = Integer.parseInt(port);
				mMySocket.initTCP(ip, Integer.parseInt(port));
				mMySocket.startTCP();
			}
		}
	}

	public void stopTcp() {
		mMySocket.stopTCP();
		isStart = false;
	}

	public void reStart(String ip, String port) {
		if (this.ip.equals(ip) && this.port == Integer.parseInt(port)) {
			return;
		}
		Log.d(TAG, "------reStart------");
		stopTcp();
		this.ip = ip;
		this.port = Integer.parseInt(port);
		handler.sendEmptyMessageDelayed(0, 15 * 1000);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				startTcp(ip, port + "");
			}
		};
	};

	public boolean isTcpStart() {
		return isStart;
	}

	private void analisys(byte[] app, int applen) {

		AppData para;

		para = mTcpProtocol.unpackApp(app, applen);

		if (para != null) {
			// 从接入平台发来的数据
			if (para.from == TcpProtocol.SYSTEM_CODE_FSB) {
				switch (para.cmd) {
				// start应答
				case TcpProtocol.PACKET_CMD_START:
					if (para.datalen != 0) {
						System.arraycopy(para.data, 0, mTcpProtocol.PacketKey,
								0, para.datalen);
						// mTcpProtocol.PacketKey = para.data;
						// SystemManager.LogHex(TAG,
						// mTcpProtocol.PacketKey.getBytes(),
						// mTcpProtocol.PacketKey.length());
						// 鉴权
						mCallback.connectResult(para.cmd, true);
						auth();
					} else {
						mCallback.connectResult(para.cmd, false);
						start();
					}
					break;
				// 鉴权应答
				case TcpProtocol.PACKET_CMD_AUTH:
					if (para.datalen != 0) {

						byte[] data = MyEncrypt.desUnEncrypt(para.data,
								mTcpProtocol.MyKey.getBytes());
						/*
						 * SystemManager.LOGI(TAG,
						 * "---------------------解密密钥--------------");
						 * SystemManager.LogHex(TAG,
						 * mTcpProtocol.MyKey.getBytes(), 8);
						 * SystemManager.LOGI(TAG,
						 * "------------------------------------------");
						 * SystemManager.LOGI(TAG,
						 * "---------------------解密数据--------------");
						 * SystemManager.LogHex(TAG, para.data, 8);
						 * SystemManager.LOGI(TAG,
						 * "------------------------------------------");
						 * SystemManager.LOGI(TAG,
						 * "---------------------解密后数据--------------");
						 * SystemManager.LogHex(TAG, data, 8);
						 * SystemManager.LOGI(TAG,
						 * "------------------------------------------");
						 */
						if (!General.checkByte(data, FinalBao, FinalBao.length)) {
							Log.i(TAG, "CHECK err");
							mCallback.connectResult(para.cmd, false);
						} else {
							SystemManager.LOGI(TAG, "鉴权成功");
							mCallback.connectResult(para.cmd, true);
							// 心跳包
							if (mTimerHeart == null) {
								mTimerHeart = new Timer();
								mTimerHeart.schedule(new TimerTask() {
									@Override
									public void run() {
										heart();
									}
								}, 1, 20 * 1000);
							}
						}
					} else {
						SystemManager.LOGI(TAG, "para.datalen = 0");
						mCallback.connectResult(para.cmd, false);
					}
					break;
				case TcpProtocol.PACKET_CMD_END:
					SystemManager.LOGI(TAG, "rec PACKET_CMD_END resp");
					break;
				case TcpProtocol.PACKET_CMD_HEART:
					SystemManager.LOGI(TAG, "rec PACKET_CMD_HEART resp");
					break;
				}
			}
			// 收到服务器下发指令
			else if (para.from == TcpProtocol.SYSTEM_CODE_DP) {
				byte[] sendbuf = new byte[64];
				SystemManager.LOGI(TAG, "from = " + (para.from & 0xFF));
				int sendlen = mTcpProtocol.response(para.cmd, sendbuf,
						(byte) 0x00, para.from);
				SystemManager.LOGI(TAG, "-----------应答----------");
				SystemManager.LogHex(TAG, sendbuf, sendlen);
				SystemManager.LOGI(TAG, "-----------应答----------");
				mMySocket.send(para.cmd, sendbuf, sendlen);
				mCallback.request(para.cmd, para.data, para.sn);
				SystemManager.LOGI(TAG, "收到平台下发指令");
			}
		}

	}

	public void start() {

		Log.i(TAG, "start");
		byte[] send = new byte[128];
		int sendlen = 0;
		mTcpProtocol.EncryptType = TcpProtocol.ENCRYPT_TYPE_NO;
		byte cmd = TcpProtocol.PACKET_CMD_START;
		mStep = STEP_START;
		sendlen = mTcpProtocol.request(cmd, null, 0, send,
				TcpProtocol.SYSTEM_CODE_FSB);


		 SystemManager.LOGI(TAG, "---------数据发送start数据-----------");
		 SystemManager.LogHex(TAG, send, sendlen);
		 SystemManager.LOGI(TAG, "----------start数据发送结束----------");
		mMySocket.send(cmd, send, sendlen);
	}

	public void end() {
		int sendlen = 0;
		byte[] send = new byte[128];
		byte cmd = TcpProtocol.PACKET_CMD_END;
		sendlen = mTcpProtocol.request(TcpProtocol.PACKET_CMD_AUTH, null, 0,
				send, TcpProtocol.SYSTEM_CODE_FSB);
		mStep = STEP_END;

		SystemManager.LOGI(TAG, "---------数据发送end数据-----------");
		SystemManager.LogHex(TAG, send, sendlen);
		SystemManager.LOGI(TAG, "----------end数据发送结束----------");
		mMySocket.send(cmd, send, sendlen);
	}

	// 鉴权
	public void auth() {

		Log.i(TAG, "鉴权");

		mTcpProtocol.MyKey = General.getrandom(false, 8);
		// 令牌
		byte[] token = MyEncrypt.desEncrypt(FinalBao, mTcpProtocol.PacketKey);
		SystemManager.LOGI(TAG, "-------------------令牌----------------");
		SystemManager.LogHex(TAG, token, token.length);
		SystemManager.LOGI(TAG, "--------------------------------------");
		int sendlen = 0;
		byte[] send = new byte[128];
		byte cmd = TcpProtocol.PACKET_CMD_AUTH;
		mStep = STEP_AUTH;
		byte[] data = new byte[16];
		System.arraycopy(token, 0, data, 0, token.length);
		SystemManager.LOGI(TAG, "-------------------mykey----------------");
		SystemManager.LOGI(TAG, "mykey = " + mTcpProtocol.MyKey);
		SystemManager.LogHex(TAG, mTcpProtocol.MyKey.getBytes(), 8);
		SystemManager.LOGI(TAG, "--------------------------------------");
		System.arraycopy(mTcpProtocol.MyKey.getBytes(), 0, data, 8,
				mTcpProtocol.MyKey.length());


		sendlen = mTcpProtocol.request(TcpProtocol.PACKET_CMD_AUTH, data,
				data.length, send, TcpProtocol.SYSTEM_CODE_FSB);

		SystemManager.LOGI(TAG, "---------发送鉴权数据-----------");
		SystemManager.LogHex(TAG, send, sendlen);
		SystemManager.LOGI(TAG, "------------鉴权数据发送结束-----------");
		mMySocket.send(cmd, send, sendlen);
	}

	// 心跳
	public void heart() {
		SystemManager.LOGI(TAG, "-------------------心跳----------------");
		int sendlen = 0;
		byte[] send = new byte[128];
		byte cmd = TcpProtocol.PACKET_CMD_HEART;
		sendlen = mTcpProtocol.request(TcpProtocol.PACKET_CMD_HEART, null, 0,
				send, TcpProtocol.SYSTEM_CODE_FSB);

		SystemManager.LOGI(TAG, "---------发送心跳数据-----------");
		SystemManager.LogHex(TAG, send, sendlen);
		SystemManager.LOGI(TAG, "-----------心跳数据发送结束------");
		mMySocket.send(cmd, send, sendlen);
	}

}
