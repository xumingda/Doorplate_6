package com.yy.face;

import java.util.Arrays;

import android.util.Log;

public class FaceProtocol {

	private static final String TAG = "FaceProtocol";

	public static final byte STX = (byte) 0xFB; // ��ʼ

	// ��������
	public static final byte TYPE_REQUEST_FACE = (byte) 0x01; // ����ʶ�������
	public static final byte TYPE_REQUEST_WARN = (byte) 0x02; // ƣ��Ԥ�������
	public static final byte TYPE_REQUEST_GESTURE = (byte) 0x03; // ����ģ�������
	public static final byte TYPE_REQUEST_SYSTEM = (byte) 0x09; // ϵͳ�����
	public static final byte TYPE_RESPONSE = (byte) 0x12; // ��Ӧ����

	// ��������
	public static final byte[] CMD_PERSON_REGISTER = new byte[] { 0x01, 0x01 }; // ע����Ա��Ϣ
	public static final byte[] CMD_PERSON_DEL = new byte[] { 0x01, 0x02 }; // ɾ����Ա��Ϣ
	public static final byte[] CMD_PERSON_CLEAR = new byte[] { 0x01, 0x03 }; // �����Ա��Ϣ
	public static final byte[] CMD_PERSON_UPDATA = new byte[] { 0x01, 0x04 }; // �޸���Ա��Ϣ
	public static final byte[] CMD_PERSON_QUERY = new byte[] { 0x01, 0x05 }; // ��ѯ��Ա��Ϣ
	public static final byte[] CMD_PERSON_SEARCH = new byte[] { 0x01, 0x06 }; // ������Ա��Ϣ
	public static final byte[] CMD_PERSON_GET_COUNT = new byte[] { 0x01, 0x07 }; // ��ѯ��Ա����
	public static final byte[] CMD_FACE_REGISTER = new byte[] { 0x01, 0x11 }; // ע����������
	public static final byte[] CMD_FACE_CLEAR = new byte[] { 0x01, 0x12 }; // �����������
	public static final byte[] CMD_FACECHECK = new byte[] { 0x01, 0x21 }; // ����У��
	public static final byte[] CMD_FACEREC = new byte[] { 0x01, 0x31 }; // ����ʶ��
	public static final byte[] CMD_FACEREC_SET = new byte[] { 0x01, 0x38 }; // ��������ʶ�����
	public static final byte[] CMD_FACEREC_REALTIME = new byte[] { 0x01, 0x3A }; // ʵʱ����ʶ��
	public static final byte[] CMD_SYSTEM_HEART = new byte[] { 0x09, 0x01 }; // ����
	public static final byte[] CMD_SYSTEM_GET_VERSON = new byte[] { 0x09, 0x02 }; // ��ȡ�汾��Ϣ
	public static final byte[] CMD_SYSTEM_SET_TIME = new byte[] { 0x09, 0x03 }; // ����ϵͳʱ��
	public static final byte[] CMD_SYSTEM_GET_PIC = new byte[] { 0x09, 0x04 }; // ����ͷץͼ
	public static final byte[] CMD_SYSTEM_SET_RATE = new byte[] { 0x09, 0x05 }; // �޸Ĵ��ڲ�����
	public static final byte[] CMD_SYSTEM_FACE_OPEN = new byte[] { 0x09, 0x21 }; // �����������
	public static final byte[] CMD_SYSTEM_FACE_CLOSE = new byte[] { 0x09, 0x22 }; // �ر��������
	public static final byte[] CMD_SYSTEM_FACE_NOTIFY = new byte[] { 0x09, 0x23 }; // �������֪ͨ
	public static final byte[] CMD_SYSTEM_GET_FACE = new byte[] { 0x09, 0x24 }; // ����ץ��
	public static final byte[] CMD_SYSTEM_GET_FACE_SEND = new byte[] { 0x09,
			0x25 }; // ץ��ͼƬ��Ƭ����
	public static final byte[] CMD_SYSTEM_REBOOT = new byte[] { 0x09, 0x30 }; // ����
	public static final byte[] CMD_SYSTEM_OPEN_CAMERA = new byte[] { 0x09, 0x31 }; // ������ͷ
	public static final byte[] CMD_SYSTEM_CLOSE_CAMERA = new byte[] { 0x09,
			0x32 }; // �ر�����ͷ

	// ������
	public static final byte[] ERROR_SUCCESS = new byte[] { 0x00, 0x00 }; // �ɹ�
	public static final byte[] ERROR_FAIL_1 = new byte[] { (byte) 0xff, 0x01 }; // ���Ľ��ճ�ʱ
	public static final byte[] ERROR_FAIL_2 = new byte[] { (byte) 0xff, 0x02 }; // ����Э���������
	public static final byte[] ERROR_FAIL_3 = new byte[] { (byte) 0xff, 0x03 }; // ����У�����
	public static final byte[] ERROR_FAIL_4 = new byte[] { (byte) 0xff, 0x04 }; // �������Ͳ�����
	public static final byte[] ERROR_FAIL_5 = new byte[] { (byte) 0xff, 0x05 }; // �����������
	public static final byte[] ERROR_FAIL_6 = new byte[] { (byte) 0xff, 0x06 }; // �������ݽ�������
	public static final byte[] ERROR_FAIL_7 = new byte[] { (byte) 0xff, 0x07 }; // �����ͻ
	public static final byte[] ERROR_FAIL_10 = new byte[] { (byte) 0xff, 0x10 }; // ���ݿ�����ʧ��
	public static final byte[] ERROR_FAIL_11 = new byte[] { (byte) 0xff, 0x11 }; // ���ݿ��ļ�������
	public static final byte[] ERROR_FAIL_12 = new byte[] { (byte) 0xff, 0x12 }; // ���ݿ��ѯ��ʱ
	public static final byte[] ERROR_FAIL_20 = new byte[] { (byte) 0xff, 0x20 }; // ϵͳ�ڴ治��
	public static final byte[] ERROR_FAIL_21 = new byte[] { (byte) 0xff, 0x21 }; // ϵͳ���ô洢�ռ䲻��
	public static final byte[] ERROR_FAIL_31 = new byte[] { (byte) 0xff, 0x31 }; // ����ͷ����ʧ��
	public static final byte[] ERROR_FAIL_41 = new byte[] { (byte) 0xff, 0x41 }; // ��ȡ�����ݲ�����
	public static final byte[] ERROR_FAIL_FE1 = new byte[] { (byte) 0xfe, 0x01 }; // ��ԱID�Ѿ�����
	public static final byte[] ERROR_FAIL_FE2 = new byte[] { (byte) 0xfe, 0x02 }; // ��ԱID������
	public static final byte[] ERROR_FAIL_FE3 = new byte[] { (byte) 0xfe, 0x03 }; // ��Աû��ע������
	public static final byte[] ERROR_FAIL_FE4 = new byte[] { (byte) 0xfe, 0x04 }; // ��ѯ������������
	public static final byte[] ERROR_FAIL_FE5 = new byte[] { (byte) 0xfe, 0x05 }; // �����������ȴ���
	public static final byte[] ERROR_FAIL_FE6 = new byte[] { (byte) 0xfe, 0x06 }; // ��ѯ������ͼƬ������
	public static final byte[] ERROR_FAIL_FE7 = new byte[] { (byte) 0xfe, 0x07 }; // ��Աע���¼�ﵽ����
	public static final byte[] ERROR_FAIL_FD1 = new byte[] { (byte) 0xfd, 0x01 }; // ¼��δ���
	public static final byte[] ERROR_FAIL_FD2 = new byte[] { (byte) 0xfd, 0x02 }; // ¼�񲻴���
	public static final byte[] ERROR_FAIL_FD3 = new byte[] { (byte) 0xfd, 0x03 }; // �����¼���Ƭƫ��

	public FaceProtocol() {
	}

	public class Packet {
		public int len;
		public byte type;
		public byte[] cmd;
		public byte[] error;
		public byte[] data;

		@Override
		public String toString() {
			return "Packet [len=" + len + ", type=" + type + ", cmd="
					+ Arrays.toString(cmd) + ", error="
					+ Arrays.toString(error) + ", data="
					+ Arrays.toString(data) + "]";
		}
	}

	public int packPacket(byte type, byte[] cmd, byte[] data, int dataLen,
			byte[] packetout) {
		int len = -1;
		if (cmd != null && cmd.length == 2) {
			len = 0;
			packetout[len] = STX;
			len++;
			int packLen = 15;
			if (data != null && dataLen > 0) {
				packLen += dataLen;
			}
			packetout[len] = (byte) (packLen & 0xff);
			len++;
			packetout[len] = (byte) ((packLen >> 8) & 0xff);
			len++;
			packetout[len] = (byte) ((packLen >> 16) & 0xff);
			len++;
			packetout[len] = (byte) ((packLen >> 24) & 0xff);
			len++;
			for (int i = 0; i < 4; i++) {
				packetout[len] = 0;
				len++;
			}
			packetout[len] = type;
			len++;
			packetout[len] = cmd[1];
			len++;
			packetout[len] = cmd[0];
			len++;
			for (int i = 0; i < 2; i++) {
				packetout[len] = 0;
				len++;
			}
			if (data != null) {
				System.arraycopy(data, 0, packetout, len, dataLen);
				len += dataLen;
			}
			packetout[len] = countCheck(packetout, len);
			len++;
		}
		return len;
	}

	public Packet unpackPacket(byte[] data, int dataLen) {
		if (data == null || dataLen < 15) {
			Log.e(TAG, "data error");
			return null;
		}
		if (data[0] != STX) {
			Log.e(TAG, "STX error");
			return null;
		}
		int packLen = (int) ((data[1] & 0xFF) | ((data[2] & 0xFF) << 8)
				| ((data[3] & 0xFF) << 16) | ((data[4] & 0xFF) << 24));
		if (packLen != dataLen) {
			Log.e(TAG, "packLen error");
			return null;
		}
		byte check = countCheck(data, dataLen - 1);
		if (data[dataLen - 1] != check) {
			Log.e(TAG, "check error");
			return null;
		}
		Packet packet = new Packet();
		packet.len = dataLen;
		packet.type = data[9];
		packet.cmd = new byte[2];
		packet.cmd[0] = data[11];
		packet.cmd[1] = data[10];
		packet.error = new byte[2];
		packet.error[0] = data[13];
		packet.error[1] = data[12];
		if (dataLen - 15 > 0) {
			packet.data = new byte[dataLen - 15];
			System.arraycopy(data, 14, packet.data, 0, dataLen - 15);
		}
		return packet;
	}

	private byte countCheck(byte[] data, int len) {
		int check = 0;
		for (int i = 0; i < len; i++) {
			check += (byte) data[i] & 0xFF;
		}
		byte b = (byte) (0x100 - (byte) (check & 0xff));
		return b;
	}
}
