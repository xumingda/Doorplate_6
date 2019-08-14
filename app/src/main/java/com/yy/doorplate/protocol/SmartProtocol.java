package com.yy.doorplate.protocol;

import com.yy.doorplate.MyApplication;

public class SmartProtocol {

	public static byte Index = 0;

	public static final byte STX = (byte) 0xAA; // ��ʼ
	public static final byte ETX = (byte) 0x55; // ����

	public static final byte EQU_CTRL = (byte) 0x01; // �豸����
	public static final byte SCENE_CTRL = (byte) 0x02; // �龰����
	public static final byte EQU_SET = (byte) 0x03; // �豸ѧϰ
	public static final byte SCENE_SET = (byte) 0x04; // �龰ѧϰ

	public static final byte EQU_ON = (byte) 0x01; // �豸��
	public static final byte EQU_OFF = (byte) 0x02; // �豸��
	public static final byte EQU_STOP = (byte) 0x03; // �豸ͣ��

	public static final byte SUCCESS = (byte) 0x01; // �ɹ�
	public static final byte FALSE = (byte) 0x02; // ʧ��

	public static final byte ROOM = (byte) 0x01; // ����

	public static final byte ALL_OFF = (byte) 0x01; // ȫ��
	public static final byte ALL_ON = (byte) 0x02; // ȫ��
	public static final byte LEAVE = (byte) 0x03; // ���ģʽ
	public static final byte BACK = (byte) 0x04; // �ؼ�ģʽ
	public static final byte MEET = (byte) 0x05; // ���
	public static final byte FUN = (byte) 0x06; // ����
	public static final byte RELAX = (byte) 0x07; // ����
	public static final byte SLEEPING = (byte) 0x08; // ˯��
	public static final byte EAT = (byte) 0x09; // �ò�
	public static final byte READ = (byte) 0x10; // �Ķ�
	public static final byte SONG = (byte) 0x11; // ����

	public static final byte EQU_1 = (byte) 0x21; // �豸1
	public static final byte EQU_2 = (byte) 0x22; // �豸2
	public static final byte EQU_3 = (byte) 0x23; // �豸3
	public static final byte EQU_4 = (byte) 0x24; // �豸4
	public static final byte EQU_5 = (byte) 0x25; // �豸5
	public static final byte EQU_6 = (byte) 0x26; // �豸6

	private SmartSend mCallBack;

	public SmartProtocol(SmartSend callback) {
		mCallBack = callback;
	}

	public interface SmartSend {
		public abstract void sendData(byte[] data, int len);
	}

	private byte countCheck(byte[] data, int len) {

		byte crc = 0;

		for (int i = 1; i < len; i++) {
			crc ^= data[i];
		}
		return crc;
	}

	/*
	 * ���ݴ�� cmd ���� id ��ֵ data �������� out ��������� return len ��������ݳ���
	 */
	private int pack(byte cmd, byte id, byte data, byte[] out) {

		int len = 0;
		out[len] = STX;
		len++;
		System.arraycopy(MyApplication.getSMEquId().getBytes(), 1, out, 1, 3);
		len += 3;
		out[len] = id;
		len++;
		out[len] = cmd;
		len++;
		out[len] = data;
		len += 2;
		out[len] = (byte) (Index & 0xFF);
		Index++;
		len += 2;
		out[len] = countCheck(out, len);
		len++;
		out[len] = ETX;
		len++;
		return len;
	}

	// ȫ��ģʽ
	public void allOn() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, ALL_ON, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void allOff() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, ALL_OFF, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void leave() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, LEAVE, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void back() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, BACK, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void meet() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, MEET, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void fun() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, FUN, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void relax() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, RELAX, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void sleep() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, SLEEPING, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void eat() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, EAT, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void read() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, READ, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	public void song() {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, SONG, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	// �龰����
	public void equCtrl(byte mode) {
		byte[] out = new byte[64];
		int len = pack(SCENE_CTRL, mode, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	// �龰����
	public void equSet(byte mode) {
		byte[] out = new byte[64];
		int len = pack(SCENE_SET, mode, (byte) 0, out);
		mCallBack.sendData(out, len);
	}

	// �豸����
	public void deviceCtrl(byte id, byte data) {
		byte[] out = new byte[64];
		int len = pack(EQU_CTRL, id, data, out);
		mCallBack.sendData(out, len);
	}

	// �豸ѧϰ
	public void deviceSet(byte id, byte data) {
		byte[] out = new byte[64];
		int len = pack(EQU_SET, id, data, out);
		mCallBack.sendData(out, len);
	}

}
