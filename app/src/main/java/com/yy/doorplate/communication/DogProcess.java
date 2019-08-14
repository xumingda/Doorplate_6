package com.yy.doorplate.communication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.driver.DriverService;
import com.driver.UsbProcess;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.protocol.DogProtocol;
import com.yy.doorplate.protocol.DogProtocol.Dog;
import com.yy.doorplate.protocol.DogProtocol.DogSend;

public class DogProcess {

	private DriverService dev;
	private UsbProcess mUsb;

	private DogProtocol dogProtocol = null;

	private static final String TAG = "DogProcess";
	private static final String Path = "/dev/ttyS1";

	int fd = -1;

	private DogThread dogThread = null;
	private boolean ReadisRunning = true;

	private Context context;
	private MyApplication application;

	private int brightness = 0;

	public int init() {
		dev = new DriverService();
		mUsb = new UsbProcess(dev);
		fd = mUsb.initUsb(Path, 9600);
		dogProtocol = new DogProtocol(new DogSend() {

			@Override
			public void sendData(byte[] data, int len) {
				if (fd > 0) {
					mUsb.write(data, len);
				}
			}
		});
		return fd;
	}

	private int recvLen = 0;
	private byte[] recvbuf = new byte[1024];
	private byte[] data = new byte[1024];
	private int datalen = 0;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			datalen = 0;
			data = new byte[1024];
		};
	};

	private class DogThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (ReadisRunning) {
				recvLen = dev.select(0, 1, 1000 * 1000 * 1);
				if (recvLen == DriverService.REQ_TIMEOUT) {
					// Log.e(TAG, "wait timeout !");
					continue;
				}
				if (recvLen != DriverService.READ_ALLOWED) {
					Log.e(TAG, "read is not allowed!");
					continue;
				}
				try {
					recvLen = mUsb.getInputStream().read(recvbuf);
					if (recvLen > 0) {
						System.arraycopy(recvbuf, 0, data, datalen, recvLen);
						datalen += recvLen;
						handler.removeMessages(0);
						handler.sendEmptyMessageDelayed(0, 1000);
						if (datalen == 8 || datalen == 6) {
							MyApplication.debugHex("dog read", data, datalen);
							Dog dog = dogProtocol.unpack(data, datalen);
							if (dog != null
									&& context != null
									&& dog.cmd == DogProtocol.USARTCMDGETLIGHTBACK
									&& application.isAutoBrightness) {
								int light = (dog.data[1] & 0xFF) << 8;
								light += dog.data[0] & 0xFF;
								light = light / 256;
								if (light > 250) {
									light = 250;
								}
								if (Math.abs(light - brightness) > 30) {
									Intent intent = new Intent();
									intent.setAction("set_screen_brightness");
									intent.putExtra("screen_brightness", light);
									context.sendBroadcast(intent);
								}
							} else if (dog != null
									&& dog.cmd == DogProtocol.USARTCMDOFF) {
								dogProtocol.closePower();
							} else if (dog != null
									&& dog.cmd == DogProtocol.USARTCMDVERSION) {
								application.dogVersion = (dog.data[1] & 0xFF) << 8;
								application.dogVersion += dog.data[0] & 0xFF;
							}
							if (dog != null) {
								datalen = 0;
								data = new byte[1024];
								handler.removeMessages(0);
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void enbleLight(Context context) {
		this.context = context;
		this.application = (MyApplication) context;
		if (fd > 0 && dogThread == null) {
			ReadisRunning = true;
			dogThread = new DogThread();
			dogThread.start();
			getVersion();
		}
	}

	public void disenbleLight() {
		if (dogThread != null) {
			ReadisRunning = false;
			dogThread.interrupt();
			dogThread = null;
		}
	}

	public void startDog(int timeout) {
		if (dogProtocol == null) {
			return;
		}
		dogProtocol.startDog(timeout);
	}

	public void stopDog() {
		if (dogProtocol == null) {
			return;
		}
		dogProtocol.stopDog();
	}

	public void feedDog() {
		if (dogProtocol == null) {
			return;
		}
		dogProtocol.feedDog();
	}

	// 0:¾²Òô 1£º¹ã²¥ 2£ºÄÚ²¿
	public void setAudioMode(int mode) {
		if (dogProtocol == null) {
			return;
		}
		if (mode == 0) {
			dogProtocol.closeAudio();
		} else {
			dogProtocol.openAudio();
		}
	}

	public void getVersion() {
		if (dogProtocol == null) {
			return;
		}
		dogProtocol.getVersion();
	}

	public void close() {
		mUsb.close();
	}
}
