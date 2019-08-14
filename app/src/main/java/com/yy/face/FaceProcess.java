package com.yy.face;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.driver.DriverService;
import com.yy.doorplate.MyApplication;
import com.yy.face.FaceProtocol.Packet;

public class FaceProcess {

	private DriverService dev;

	private FaceProtocol faceProtocol;

	private static final String TAG = "FaceProcess";
	private static final String Path = "/dev/ttyS3";
	private static final int RATE = 115200;

	private int fd = -1;

	private Context context;
	private MyApplication application;

	private InputStream inputStream = null;
	private OutputStream outputStream = null;

	private FaceThread faceThread = null;
	private boolean isFaceThreadRunning = false;

	private LocalBroadcastManager broadcastManager;

	public FaceProcess(Context context) {
		this.context = context;
		this.application = (MyApplication) context;
		dev = new DriverService();
		faceProtocol = new FaceProtocol();
		broadcastManager = LocalBroadcastManager.getInstance(application);
	}

	public int init() {
		close();
		fd = dev.open(Path, 0);
		if (fd < 0) {
			Log.e(TAG, "����" + Path + "��ʧ��");
		} else {
			Log.e(TAG, "����" + Path + "�򿪳ɹ�");
			dev.configSerial(RATE, 8, 1, 'N', (char) 0, (char) 0);
			outputStream = dev.getOutputStream();
			inputStream = dev.getInputStream();
			// ���������߳�
			if (faceThread == null) {
				isFaceThreadRunning = true;
				faceThread = new FaceThread();
				faceThread.start();
			}
		}
		return fd;
	}

	private int tempLen = 0;
	private byte[] tempBuf = new byte[1024];
	private int dataLen = 0;
	private byte[] dataBuf = new byte[1024];
	private int packLen = 0, unpackLen = 0;

	private class FaceThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (isFaceThreadRunning) {
				tempLen = dev.select(0, 1, 1000 * 1000 * 1);
				if (tempLen == DriverService.REQ_TIMEOUT) {
					// Log.e(TAG, "wait timeout !");
					continue;
				}
				if (tempLen != DriverService.READ_ALLOWED) {
					Log.e(TAG, "read is not allowed!");
					continue;
				}
				try {
					tempLen = inputStream.read(tempBuf);
					if (tempLen <= 0) {
						continue;
					}
					// MyApplication.debugHex("FaceProcess tempBuf", tempBuf,
					// tempLen);
					System.arraycopy(tempBuf, 0, dataBuf, dataLen, tempLen);
					dataLen += tempLen;
					if (dataBuf[0] != FaceProtocol.STX) {
						clearByte(dataBuf);
						dataLen = 0;
						packLen = 0;
						unpackLen = 0;
						continue;
					}
					if (dataLen >= 5) {
						packLen = (int) ((dataBuf[1] & 0xFF)
								| ((dataBuf[2] & 0xFF) << 8)
								| ((dataBuf[3] & 0xFF) << 16) | ((dataBuf[4] & 0xFF) << 24));
						// Log.d(TAG, "packLen = " + packLen);
					}
					if (packLen == 0 || dataLen < packLen) {
						continue;
					}
					while (unpackLen < dataLen) {
						packLen = (int) ((dataBuf[unpackLen + 1] & 0xFF)
								| ((dataBuf[unpackLen + 2] & 0xFF) << 8)
								| ((dataBuf[unpackLen + 3] & 0xFF) << 16) | ((dataBuf[unpackLen + 4] & 0xFF) << 24));
						byte[] data = new byte[packLen];
						System.arraycopy(dataBuf, unpackLen, data, 0, packLen);
						unpackLen += packLen;
						MyApplication.debugHex("FaceProcess data", data,
								packLen);
						analisys(data, packLen);
						if (unpackLen >= dataLen) {
							clearByte(dataBuf);
							dataLen = 0;
							packLen = 0;
							unpackLen = 0;
							break;
						} else {
							System.arraycopy(dataBuf, unpackLen, dataBuf, 0,
									dataLen - unpackLen);
							dataLen = dataLen - unpackLen;
							unpackLen = 0;
							packLen = (int) ((dataBuf[unpackLen + 1] & 0xFF)
									| ((dataBuf[unpackLen + 2] & 0xFF) << 8)
									| ((dataBuf[unpackLen + 3] & 0xFF) << 16) | ((dataBuf[unpackLen + 4] & 0xFF) << 24));
							if (packLen > dataLen) {
								break;
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ����������
	private void analisys(byte[] data, int len) {
		Packet packet = faceProtocol.unpackPacket(data, packLen);
		if (packet == null) {
			return;
		}
		if (compareByte(packet.error, FaceProtocol.ERROR_SUCCESS)) {
			if (packet.type == FaceProtocol.TYPE_REQUEST_FACE) {
				analisys_req_face(packet);
			} else if (packet.type == FaceProtocol.TYPE_REQUEST_WARN) {
			} else if (packet.type == FaceProtocol.TYPE_REQUEST_GESTURE) {
			} else if (packet.type == FaceProtocol.TYPE_REQUEST_SYSTEM) {
				analisys_req_system(packet);
			} else if (packet.type == FaceProtocol.TYPE_RESPONSE) {
				analisys_res(packet);
			}
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_1)) {
			Log.e(TAG, "���Ľ��ճ�ʱ");
			sendBroadcastError("���Ľ��ճ�ʱ");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_2)) {
			Log.e(TAG, "����Э���������");
			sendBroadcastError("����Э���������");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_3)) {
			Log.e(TAG, "����У�����");
			sendBroadcastError("����У�����");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_4)) {
			Log.e(TAG, "�������Ͳ�����");
			// reboot();
			sendBroadcastError("�������Ͳ�����");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_5)) {
			Log.e(TAG, "�����������");
			sendBroadcastError("�����������");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_6)) {
			Log.e(TAG, "�������ݽ�������");
			sendBroadcastError("�������ݽ�������");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_7)) {
			Log.e(TAG, "�����ͻ");
			sendBroadcastError("�����ͻ");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_10)) {
			Log.e(TAG, "���ݿ�����ʧ��");
			sendBroadcastError("���ݿ�����ʧ��");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_11)) {
			Log.e(TAG, "���ݿ��ļ�������");
			sendBroadcastError("���ݿ��ļ�������");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_12)) {
			Log.e(TAG, "���ݿ��ѯ��ʱ");
			sendBroadcastError("���ݿ��ѯ��ʱ");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_20)) {
			Log.e(TAG, "ϵͳ�ڴ治��");
			sendBroadcastError("ϵͳ�ڴ治��");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_21)) {
			Log.e(TAG, "ϵͳ���ô洢�ռ䲻��");
			sendBroadcastError("ϵͳ���ô洢�ռ䲻��");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_31)) {
			Log.e(TAG, "����ͷ����ʧ��");
			sendBroadcastError("����ͷ����ʧ��");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_41)) {
			Log.e(TAG, "��ȡ�����ݲ�����");
			sendBroadcastError("��ȡ�����ݲ�����");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FE1)) {
			Log.e(TAG, "��ԱID�Ѿ�����");
			sendBroadcastError("��ԱID�Ѿ�����");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FE2)) {
			Log.e(TAG, "��ԱID������");
			sendBroadcastError("��ԱID������");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FE3)) {
			Log.e(TAG, "��Աû��ע������");
			sendBroadcastError("��Աû��ע������");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FE4)) {
			Log.e(TAG, "��ѯ������������");
			sendBroadcastError("��ѯ������������");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FE5)) {
			Log.e(TAG, "�����������ȴ���");
			sendBroadcastError("�����������ȴ���");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FE6)) {
			Log.e(TAG, "��ѯ������ͼƬ������");
			sendBroadcastError("��ѯ������ͼƬ������");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FE7)) {
			Log.e(TAG, "��Աע���¼�ﵽ����");
			sendBroadcastError("��Աע���¼�ﵽ����");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FD1)) {
			Log.e(TAG, "¼��δ���");
			sendBroadcastError("¼��δ���");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FD2)) {
			Log.e(TAG, "¼�񲻴���");
			sendBroadcastError("¼�񲻴���");
		} else if (compareByte(packet.error, FaceProtocol.ERROR_FAIL_FD3)) {
			Log.e(TAG, "�����¼���Ƭƫ��");
			sendBroadcastError("�����¼���Ƭƫ��");
		}
	}

	private void analisys_req_face(Packet packet) {
		if (compareByte(packet.cmd, FaceProtocol.CMD_FACEREC_REALTIME)) {
			if (packet.data[0] == 1) {
				byte[] id = new byte[8];
				System.arraycopy(packet.data, 9, id, 0, 8);
				String hexStr = "";
				for (int i = 0; i < id.length; i++) {
					if (Integer.toHexString(id[i] & 0xff).length() == 1) {
						hexStr += "0";
					}
					hexStr += Integer.toHexString(id[i] & 0xff);
				}
				Log.e(TAG, "����ʶ��ɹ� " + hexStr);
				sendBroadcast(hexStr, "CMD_FACEREC_REALTIME");
			} else if (packet.data[0] == 2) {
				Log.e(TAG, "����ʶ��ʧ��");
				sendBroadcastError("����Աδע��������Ϣ");
			} else if (packet.data[0] == 3) {
				Log.e(TAG, "����δͨ��");
				sendBroadcastError("����δͨ��");
			}
			// face_rec_set(0, 1, 0, 1, 1, 0, 0, 0, 1, 1);
		}
	}

	private void analisys_req_system(Packet packet) {
		if (compareByte(packet.cmd, FaceProtocol.CMD_SYSTEM_HEART)) {
			// sendHeart();
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_SYSTEM_FACE_NOTIFY)) {
		}
	}

	private void analisys_res(Packet packet) {
		Log.i(TAG, packet.toString());
		if (compareByte(packet.cmd, FaceProtocol.CMD_PERSON_REGISTER)) {
			String hexStr = "";
			for (int i = 0; i < packet.data.length; i++) {
				if (Integer.toHexString(packet.data[i] & 0xff).length() == 1) {
					hexStr += "0";
				}
				hexStr += Integer.toHexString(packet.data[i] & 0xff);
			}
			Log.e(TAG, "��Աע��ɹ�  " + hexStr);
			sendBroadcast(hexStr, "CMD_PERSON_REGISTER");
			face_register(packet.data);
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_PERSON_DEL)) {
			Log.e(TAG, "��Աɾ���ɹ�");
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_PERSON_CLEAR)) {
			Log.e(TAG, "��Ա����ɹ�");
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_PERSON_UPDATA)) {

		} else if (compareByte(packet.cmd, FaceProtocol.CMD_PERSON_QUERY)) {

		} else if (compareByte(packet.cmd, FaceProtocol.CMD_PERSON_SEARCH)) {

		} else if (compareByte(packet.cmd, FaceProtocol.CMD_PERSON_GET_COUNT)) {

		} else if (compareByte(packet.cmd, FaceProtocol.CMD_FACE_REGISTER)) {
			if (packet.data[0] == 0) {
				Log.e(TAG, "�ɼ���");
				sendBroadcast(null, "CMD_FACE_REGISTER_ING");
			} else if (packet.data[0] == 1) {
				Log.e(TAG, "�ɼ����");
				sendBroadcast(null, "CMD_FACE_REGISTER_FINISH");
			} else if (packet.data[0] == 2) {
				Log.e(TAG, "�ɼ���ʱ");
				sendBroadcastError("�����ɼ���ʱ");
				sendBroadcast(null, "CMD_FACE_REGISTER_TIMEOUT");
			}
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_FACE_CLEAR)) {
			Log.e(TAG, "����������ݳɹ�");
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_FACEREC)) {
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_FACEREC_SET)) {
			Log.e(TAG, "��������ʶ������ɹ�");
			sendBroadcast(null, "CMD_FACEREC_SET");
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_SYSTEM_GET_VERSON)) {
			byte[] a = new byte[20];
			byte[] b = new byte[20];
			byte[] c = new byte[20];
			System.arraycopy(packet.data, 0, a, 0, 20);
			System.arraycopy(packet.data, 20, b, 0, 20);
			System.arraycopy(packet.data, 40, c, 0, 20);
			Log.e(TAG, "Ӳ���汾:" + new String(a) + " �̼��汾:" + new String(b)
					+ " ����汾:" + new String(c));
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_SYSTEM_SET_TIME)) {
			Log.e(TAG, "����ʱ��ɹ�");
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_SYSTEM_REBOOT)) {
			Log.e(TAG, "�����ɹ�");
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_SYSTEM_OPEN_CAMERA)) {
			Log.e(TAG, "������ͷ�ɹ�");
		} else if (compareByte(packet.cmd, FaceProtocol.CMD_SYSTEM_CLOSE_CAMERA)) {
			Log.e(TAG, "�ر�����ͷ�ɹ�");
		}
	}

	// ��Աע��
	public void person_register() {
		if (faceProtocol == null) {
			return;
		}
		int dataLen = 0;
		byte[] data = new byte[1024];
		for (int i = 0; i < (8 + 64 + 20 + 20 + 48 + 48 + 48 + 48 + 48); i++) {
			data[i] = 0;
			dataLen++;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_FACE,
				FaceProtocol.CMD_PERSON_REGISTER, data, dataLen, out);
		send(out, len);
	}

	// ��Աɾ��
	public void person_del(byte[] id) {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_FACE,
				FaceProtocol.CMD_PERSON_DEL, id, 8, out);
		send(out, len);
	}

	// ��Ա���
	public void person_clear() {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_FACE,
				FaceProtocol.CMD_PERSON_CLEAR, null, 0, out);
		send(out, len);
	}

	// ����ע��
	public void face_register(byte[] id) {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_FACE,
				FaceProtocol.CMD_FACE_REGISTER, id, 8, out);
		send(out, len);
	}

	// ����ɾ��
	public void face_clear(byte[] id) {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_FACE,
				FaceProtocol.CMD_FACE_CLEAR, null, 0, out);
		send(out, len);
	}

	// ��������ʶ��
	public void face_rec_enable() {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_FACE,
				FaceProtocol.CMD_FACEREC, null, 0, out);
		send(out, len);
	}

	// ��������ʶ�����
	public void face_rec_set(int setting_mask0, int setting_mask1,
			int setting_mask2, int setting_mask3, int setting_mask4,
			int maxrecord, int saveImageOnOff, int timeout,
			int liveDetectionOnOff, int realTimeIdentify) {
		if (faceProtocol == null) {
			return;
		}
		int dataLen = 0;
		byte[] data = new byte[1024];

		int setting_mask = (int) (setting_mask0 + setting_mask1
				* Math.pow(2, 1) + setting_mask2 * Math.pow(2, 2)
				+ setting_mask3 * Math.pow(2, 3) + setting_mask4
				* Math.pow(2, 4));
		byte[] setting_mask_byte = new byte[4];
		setting_mask_byte[0] = (byte) (setting_mask & 0xff);
		setting_mask_byte[1] = (byte) ((setting_mask >> 8) & 0xff);
		setting_mask_byte[2] = (byte) ((setting_mask >> 16) & 0xff);
		setting_mask_byte[3] = (byte) ((setting_mask >> 24) & 0xff);
		System.arraycopy(setting_mask_byte, 0, data, dataLen, 4);
		dataLen += 4;

		byte[] maxrecord_byte = new byte[4];
		maxrecord_byte[0] = (byte) (maxrecord & 0xff);
		maxrecord_byte[1] = (byte) ((maxrecord >> 8) & 0xff);
		maxrecord_byte[2] = (byte) ((maxrecord >> 16) & 0xff);
		maxrecord_byte[3] = (byte) ((maxrecord >> 24) & 0xff);
		System.arraycopy(maxrecord_byte, 0, data, dataLen, 4);
		dataLen += 4;

		data[dataLen++] = (byte) (saveImageOnOff & 0xff);
		data[dataLen++] = (byte) (timeout & 0xff);
		data[dataLen++] = (byte) (liveDetectionOnOff & 0xff);
		data[dataLen++] = (byte) (realTimeIdentify & 0xff);

		for (int i = dataLen; i < dataLen + 24; i++) {
			data[i] = 0;
		}
		dataLen += 24;

		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_FACE,
				FaceProtocol.CMD_FACEREC_SET, data, dataLen, out);
		send(out, len);
	}

	// ��������Ӧ��
	public void sendHeart() {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_RESPONSE,
				FaceProtocol.CMD_SYSTEM_HEART, null, 0, out);
		send(out, len);
	}

	// ��ȡ�汾��
	public void getVersion() {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_SYSTEM,
				FaceProtocol.CMD_SYSTEM_GET_VERSON, null, 0, out);
		send(out, len);
	}

	// ����ʱ��
	public void setTime(String time) {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		Log.i(TAG, "setTime : " + time);
		try {
			int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_SYSTEM,
					FaceProtocol.CMD_SYSTEM_SET_TIME, time.getBytes("utf-8"),
					19, out);
			send(out, len);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	// ����
	public void reboot() {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_SYSTEM,
				FaceProtocol.CMD_SYSTEM_REBOOT, null, 0, out);
		send(out, len);
	}

	// ������ͷ
	public void openCamera() {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_SYSTEM,
				FaceProtocol.CMD_SYSTEM_OPEN_CAMERA, null, 0, out);
		send(out, len);
	}

	// �ر�����ͷ
	public void closeCamera() {
		if (faceProtocol == null) {
			return;
		}
		byte[] out = new byte[1024];
		int len = faceProtocol.packPacket(FaceProtocol.TYPE_REQUEST_SYSTEM,
				FaceProtocol.CMD_SYSTEM_CLOSE_CAMERA, null, 0, out);
		send(out, len);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				application.showToast(msg.obj.toString());
				break;
			}
		};
	};

	public void send(byte[] str, int sendlen) {
		if (outputStream != null) {
			try {
				MyApplication.debugHex("FaceProcess send", str, sendlen);
				outputStream.write(str, 0, sendlen);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (faceThread != null) {
			isFaceThreadRunning = false;
			faceThread.interrupt();
			faceThread = null;
		}
		dev.close();
		fd = -1;
	}

	private void sendBroadcast(String msg, String cmd) {
		Intent intent = new Intent(MyApplication.BROADCAST);
		intent.putExtra(MyApplication.BROADCAST_TAG, "FaceBack");
		intent.putExtra(MyApplication.CMD_MSG, msg);
		intent.putExtra("FaceCMD", cmd);
		broadcastManager.sendBroadcast(intent);
	}

	private void sendBroadcastError(String msg) {
		Intent intent = new Intent(MyApplication.BROADCAST);
		intent.putExtra(MyApplication.BROADCAST_TAG, "FaceError");
		intent.putExtra(MyApplication.CMD_MSG, msg);
		broadcastManager.sendBroadcast(intent);
		Message message = Message.obtain();
		message.what = -1;
		message.obj = msg;
		handler.sendMessage(message);
	}

	public static boolean compareByte(byte[] a, byte[] b) {
		if (a != null && a.length == 2 && b != null && b.length == 2) {
			if (a[0] == b[0] && a[1] == b[1]) {
				return true;
			}
		}
		return false;
	}

	private void clearByte(byte[] data) {
		if (data == null) {
			return;
		}
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
	}
}
