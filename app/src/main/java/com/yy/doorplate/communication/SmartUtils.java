package com.yy.doorplate.communication;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.ect.gpio.gpioctlManager;

import com.driver.DriverService;
import com.driver.UsbProcess;
import com.yy.doorplate.protocol.SmartProtocol;
import com.yy.doorplate.protocol.SmartProtocol.SmartSend;

public class SmartUtils {

	private DriverService dev;
	private UsbProcess mUsb;

	private SmartProtocol mSmartProtocol;

	private static final String Path = "/dev/ttyS3";
	// ttySWK2 信息学院23寸
	// ttyUSB1 信息学院11寸
	// ttyS3 3288

	private Timer timer_gpio = null;

	public void init() {
		dev = new DriverService();
		mUsb = new UsbProcess(dev);
		int fd = -1;
		// int i = 0;
		fd = mUsb.initUsb(Path, 9600);
		mSmartProtocol = new SmartProtocol(new SmartSend() {

			@Override
			public void sendData(byte[] data, int len) {
				mUsb.write(data, len);

			}
		});
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { while (true) { byte[] buffer = new
		 * byte[30]; int len = mUsb.read(buffer, 3);
		 * MyApplication.debugHex("uart_read", buffer, len);
		 * 
		 * } } }).start();
		 */
	}

	// 全开模式
	public void allOn() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.allOn();
	}

	//
	public void allOff() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.allOff();
	}

	public void leave() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.leave();
	}

	public void back() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.back();
	}

	public void meet() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.meet();
	}

	public void fun() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.fun();
	}

	public void relax() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.relax();
	}

	public void sleep() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.sleep();
	}

	public void eat() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.eat();
	}

	public void read() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.read();
	}

	// 听歌模式
	public void song() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.song();
	}

	public void setOn() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.ALL_ON);
	}

	public void setOff() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.ALL_OFF);
	}

	public void setLeave() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.LEAVE);
	}

	public void setBack() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.BACK);
	}

	public void setMeet() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.MEET);
	}

	public void setFun() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.FUN);
	}

	public void setRelax() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.RELAX);
	}

	public void setSleep() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.SLEEPING);
	}

	public void setEat() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.EAT);
	}

	public void setRead() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.READ);
	}

	public void setSong() {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(SmartProtocol.SONG);
	}

	// 情景控制
	public void equCtrl(byte mode) {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equCtrl(mode);
	}

	// 情景设置
	public void equSet(byte mode) {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.equSet(mode);
	}

	public void deviceCtrl(byte id, byte data) {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.deviceCtrl(id, data);
	}

	public void deviceSet(byte id, byte data) {
		if (mSmartProtocol == null) {
			return;
		}
		mSmartProtocol.deviceSet(id, data);
	}

	public void openDoor(final Context context, int time) {
		try {
			if (timer_gpio != null) {
				timer_gpio.cancel();
				timer_gpio = null;
			}
			if (time > 0 && timer_gpio == null) {
				timer_gpio = new Timer();
				timer_gpio.schedule(new TimerTask() {
					@Override
					public void run() {
						gpioctlManager mgpioctlManager = new gpioctlManager(
								context);
						mgpioctlManager.SetMode(gpioctlManager.GPIO2, 1);
						mgpioctlManager.SetState(gpioctlManager.GPIO2, 0);
					}
				}, time);
			}
			gpioctlManager mgpioctlManager = new gpioctlManager(context);
			mgpioctlManager.SetMode(gpioctlManager.GPIO2, 1);
			mgpioctlManager.SetState(gpioctlManager.GPIO2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeDoor(Context context) {
		try {
			if (timer_gpio != null) {
				timer_gpio.cancel();
				timer_gpio = null;
			}
			gpioctlManager mgpioctlManager = new gpioctlManager(context);
			mgpioctlManager.SetMode(gpioctlManager.GPIO2, 1);
			mgpioctlManager.SetState(gpioctlManager.GPIO2, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
