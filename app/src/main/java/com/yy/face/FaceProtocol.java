package com.yy.face;

import java.util.Arrays;

import android.util.Log;

public class FaceProtocol {

	private static final String TAG = "FaceProtocol";

	public static final byte STX = (byte) 0xFB; // 开始

	// 报文类型
	public static final byte TYPE_REQUEST_FACE = (byte) 0x01; // 人脸识别命令报文
	public static final byte TYPE_REQUEST_WARN = (byte) 0x02; // 疲劳预警命令报文
	public static final byte TYPE_REQUEST_GESTURE = (byte) 0x03; // 手势模块命令报文
	public static final byte TYPE_REQUEST_SYSTEM = (byte) 0x09; // 系统命令报文
	public static final byte TYPE_RESPONSE = (byte) 0x12; // 响应报文

	// 命令类型
	public static final byte[] CMD_PERSON_REGISTER = new byte[] { 0x01, 0x01 }; // 注册人员信息
	public static final byte[] CMD_PERSON_DEL = new byte[] { 0x01, 0x02 }; // 删除人员信息
	public static final byte[] CMD_PERSON_CLEAR = new byte[] { 0x01, 0x03 }; // 清空人员信息
	public static final byte[] CMD_PERSON_UPDATA = new byte[] { 0x01, 0x04 }; // 修改人员信息
	public static final byte[] CMD_PERSON_QUERY = new byte[] { 0x01, 0x05 }; // 查询人员信息
	public static final byte[] CMD_PERSON_SEARCH = new byte[] { 0x01, 0x06 }; // 搜索人员信息
	public static final byte[] CMD_PERSON_GET_COUNT = new byte[] { 0x01, 0x07 }; // 查询人员数量
	public static final byte[] CMD_FACE_REGISTER = new byte[] { 0x01, 0x11 }; // 注册人脸数据
	public static final byte[] CMD_FACE_CLEAR = new byte[] { 0x01, 0x12 }; // 清空人脸数据
	public static final byte[] CMD_FACECHECK = new byte[] { 0x01, 0x21 }; // 人脸校验
	public static final byte[] CMD_FACEREC = new byte[] { 0x01, 0x31 }; // 人脸识别
	public static final byte[] CMD_FACEREC_SET = new byte[] { 0x01, 0x38 }; // 设置人脸识别参数
	public static final byte[] CMD_FACEREC_REALTIME = new byte[] { 0x01, 0x3A }; // 实时人脸识别
	public static final byte[] CMD_SYSTEM_HEART = new byte[] { 0x09, 0x01 }; // 心跳
	public static final byte[] CMD_SYSTEM_GET_VERSON = new byte[] { 0x09, 0x02 }; // 获取版本信息
	public static final byte[] CMD_SYSTEM_SET_TIME = new byte[] { 0x09, 0x03 }; // 设置系统时间
	public static final byte[] CMD_SYSTEM_GET_PIC = new byte[] { 0x09, 0x04 }; // 摄像头抓图
	public static final byte[] CMD_SYSTEM_SET_RATE = new byte[] { 0x09, 0x05 }; // 修改串口波特率
	public static final byte[] CMD_SYSTEM_FACE_OPEN = new byte[] { 0x09, 0x21 }; // 启动人脸检测
	public static final byte[] CMD_SYSTEM_FACE_CLOSE = new byte[] { 0x09, 0x22 }; // 关闭人脸检测
	public static final byte[] CMD_SYSTEM_FACE_NOTIFY = new byte[] { 0x09, 0x23 }; // 人脸检测通知
	public static final byte[] CMD_SYSTEM_GET_FACE = new byte[] { 0x09, 0x24 }; // 人脸抓拍
	public static final byte[] CMD_SYSTEM_GET_FACE_SEND = new byte[] { 0x09,
			0x25 }; // 抓拍图片分片传输
	public static final byte[] CMD_SYSTEM_REBOOT = new byte[] { 0x09, 0x30 }; // 重启
	public static final byte[] CMD_SYSTEM_OPEN_CAMERA = new byte[] { 0x09, 0x31 }; // 打开摄像头
	public static final byte[] CMD_SYSTEM_CLOSE_CAMERA = new byte[] { 0x09,
			0x32 }; // 关闭摄像头

	// 返回码
	public static final byte[] ERROR_SUCCESS = new byte[] { 0x00, 0x00 }; // 成功
	public static final byte[] ERROR_FAIL_1 = new byte[] { (byte) 0xff, 0x01 }; // 报文接收超时
	public static final byte[] ERROR_FAIL_2 = new byte[] { (byte) 0xff, 0x02 }; // 报文协议解析错误
	public static final byte[] ERROR_FAIL_3 = new byte[] { (byte) 0xff, 0x03 }; // 报文校验错误
	public static final byte[] ERROR_FAIL_4 = new byte[] { (byte) 0xff, 0x04 }; // 报文类型不存在
	public static final byte[] ERROR_FAIL_5 = new byte[] { (byte) 0xff, 0x05 }; // 报文命令不存在
	public static final byte[] ERROR_FAIL_6 = new byte[] { (byte) 0xff, 0x06 }; // 报文数据解析错误
	public static final byte[] ERROR_FAIL_7 = new byte[] { (byte) 0xff, 0x07 }; // 请求冲突
	public static final byte[] ERROR_FAIL_10 = new byte[] { (byte) 0xff, 0x10 }; // 数据库连接失败
	public static final byte[] ERROR_FAIL_11 = new byte[] { (byte) 0xff, 0x11 }; // 数据库文件不存在
	public static final byte[] ERROR_FAIL_12 = new byte[] { (byte) 0xff, 0x12 }; // 数据库查询超时
	public static final byte[] ERROR_FAIL_20 = new byte[] { (byte) 0xff, 0x20 }; // 系统内存不足
	public static final byte[] ERROR_FAIL_21 = new byte[] { (byte) 0xff, 0x21 }; // 系统永久存储空间不足
	public static final byte[] ERROR_FAIL_31 = new byte[] { (byte) 0xff, 0x31 }; // 摄像头连接失败
	public static final byte[] ERROR_FAIL_41 = new byte[] { (byte) 0xff, 0x41 }; // 获取的数据不存在
	public static final byte[] ERROR_FAIL_FE1 = new byte[] { (byte) 0xfe, 0x01 }; // 人员ID已经存在
	public static final byte[] ERROR_FAIL_FE2 = new byte[] { (byte) 0xfe, 0x02 }; // 人员ID不存在
	public static final byte[] ERROR_FAIL_FE3 = new byte[] { (byte) 0xfe, 0x03 }; // 人员没有注册人脸
	public static final byte[] ERROR_FAIL_FE4 = new byte[] { (byte) 0xfe, 0x04 }; // 查询的人脸不存在
	public static final byte[] ERROR_FAIL_FE5 = new byte[] { (byte) 0xfe, 0x05 }; // 人脸特征长度错误
	public static final byte[] ERROR_FAIL_FE6 = new byte[] { (byte) 0xfe, 0x06 }; // 查询的人脸图片不存在
	public static final byte[] ERROR_FAIL_FE7 = new byte[] { (byte) 0xfe, 0x07 }; // 人员注册记录达到上限
	public static final byte[] ERROR_FAIL_FD1 = new byte[] { (byte) 0xfd, 0x01 }; // 录像未完成
	public static final byte[] ERROR_FAIL_FD2 = new byte[] { (byte) 0xfd, 0x02 }; // 录像不存在
	public static final byte[] ERROR_FAIL_FD3 = new byte[] { (byte) 0xfd, 0x03 }; // 错误的录像分片偏移

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
