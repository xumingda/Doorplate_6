package com.yy.doorplate.protocol;

import android.util.Log;

public class DogProtocol {

	public static final byte STX = (byte) 0x7E; // 开始
	public static final byte ETX = (byte) 0x7F; // 结束

	public static final byte USARTCMDHEARTTIME = (byte) 0x02; // 启动看门狗
	public static final byte USARTCMDOPENPWD = (byte) 0x03; // 开启3288电源
	public static final byte USARTCMDCLOSEPWD = (byte) 0x04; // 关闭3288电源
	public static final byte USARTCMDSTOPHEART = (byte) 0x05; // 关闭看门狗
	public static final byte USARTCMDOPENAUDIO = (byte) 0x06; // 开启功放
	public static final byte USARTCMDCLOSEAUDIO = (byte) 0x07; // 关闭功放
	public static final byte USARTCMDGETLIGHT = (byte) 0x08; // 光感值
	public static final byte USARTCMDGETLIGHTBACK  = (byte) 0x09; // 光感值返回值
	public static final byte USARTCMDOFF  = (byte) 0x0D; // 关闭电源应答
	public static final byte USARTCMDON  = (byte) 0x0E; // 开启电源应答
	public static final byte USARTCMDVERSION = (byte) 0x10; // 获取版本号

	public interface DogSend {
		public abstract void sendData(byte[] data, int len);
	}

	private DogSend dogSend = null;

	public DogProtocol(DogSend dogSend) {
		this.dogSend = dogSend;
	}

	public class Dog {
		public byte cmd;
		public byte[] data;
	}

	public Dog unpack(byte[] recvbuf, int recvLen) {
		if (recvbuf == null) {
			// Log.e("Dog", "unpackActive error");
			return null;
		}
		if (recvbuf[0] != STX) {
			// Log.e("Dog", "stx error " + recvbuf[0]);
			return null;
		}
		if (recvbuf[recvLen - 1] != ETX) {
			// Log.e("Dog", "etx erro " + recvbuf[recvLen - 1]);
			return null;
		}
		Dog dog = new Dog();
		dog.cmd = recvbuf[1];
		int len = (int) recvbuf[2];
		if (len > 0) {
			dog.data = new byte[len];
			System.arraycopy(recvbuf, 4, dog.data, 0, len);
		}
		if (countCheck(recvbuf, 4 + len) != recvbuf[recvLen - 2]) {
			Log.e("Dog", "check error " + recvbuf[recvLen - 2]);
			return null;
		}
		return dog;
	}

	private int pack(byte cmd, byte[] data, int datalen, byte[] out) {
		int len = 0;
		out[len] = STX;
		len++;
		out[len] = cmd;
		len++;
		out[len] = (byte) (datalen & 0xff);
		len++;
		out[len] = (byte) ((datalen >> 8) & 0xff);
		len++;
		if (datalen > 0) {
			System.arraycopy(data, 0, out, len, datalen);
			len += datalen;
		}
		out[len] = countCheck(out, len);
		len++;
		out[len] = ETX;
		len++;
		return len;
	}

	private byte countCheck(byte[] data, int len) {
		byte crc = 0;
		for (int i = 0; i < len; i++) {
			crc += data[i];
		}
		return crc;
	}

	public void startDog(int timeout) {
		timeout = timeout * 1000;
		byte[] data = new byte[4];
		data[0] = (byte) (timeout & 0xff);
		data[1] = (byte) ((timeout >> 8) & 0xff);
		data[2] = (byte) ((timeout >> 16) & 0xff);
		data[3] = (byte) ((timeout >> 24) & 0xff);
		byte[] out = new byte[10];
		int len = pack(USARTCMDHEARTTIME, data, 4, out);
		dogSend.sendData(out, len);
	}

	public void stopDog() {
		byte[] out = new byte[10];
		int len = pack(USARTCMDSTOPHEART, null, 0, out);
		dogSend.sendData(out, len);
	}

	public void feedDog() {
		byte[] out = new byte[10];
		int len = pack((byte) 0, null, 0, out);
		dogSend.sendData(out, len);
	}

	public void closeAudio() {
		byte[] out = new byte[10];
		int len = pack(USARTCMDCLOSEAUDIO, null, 0, out);
		dogSend.sendData(out, len);
	}

	public void openAudio() {
		byte[] out = new byte[10];
		int len = pack(USARTCMDOPENAUDIO, null, 0, out);
		dogSend.sendData(out, len);
	}

	public void closePower() {
		byte[] out = new byte[10];
		int len = pack(USARTCMDCLOSEPWD, null, 0, out);
		dogSend.sendData(out, len);
	}
	
	public void getVersion() {
		byte[] out = new byte[10];
		int len = pack(USARTCMDVERSION, null, 0, out);
		dogSend.sendData(out, len);
	}
}
