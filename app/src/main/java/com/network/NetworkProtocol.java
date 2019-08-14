package com.network;

import android.util.Base64;

import com.common.Cryptic;

public class NetworkProtocol {

	public static final String TAG = "NetworkProtocol";

	// --包头包尾
	public static final byte STX = (byte) 0xf3;
	public static final byte ETX = (byte) 0xf4;

	// --网络协议传输方向
	public static final byte DIR_DEFAULT = 0X00;// 默认值

	// --发起方的默认ID
	public static final int SRC_ID = 0X00000000;

	// --目标方的的默认ID
	public static final int DST_ID = 0X00000000;

	// --报文类型
	public static final byte TYPE_REQUEST = 0X00;// --请求
	public static final byte TYPE_REPLY = 0X01; // --应答

	// --加密类型
	public static final byte ENC_NONE = 0X00;// --不加密
	public static final byte ENC_3DES = 0X01;// --3DES加密
	public static final byte ENC_DES = 0X02;// --DES加密
	public static final byte ENC_AES = 0X03;// --AES加密
	public static final byte ENC_RSA = 0X04;// --RSA加密

	// --设备向服务器请求
	public static final byte CMD_CALL_REQUEST = 0X01; // 呼叫请求
	public static final byte CMD_CALL_RECONNECT = 0X02; // 呼叫重连
	public static final byte CMD_CALL_END = 0X03; // 结束通话
	public static final byte CMD_HEART_BEAT = 0X04; // 心跳
	public static final byte CMD_UNLOCK = 0X05; // 开锁
	public static final byte CMD_WORDS = 0X06; // 留言
	public static final byte CMD_CARD = 0X07; // 发送卡号

	// --请求数据域内容
	public static final byte DATA_CALL_NORMAL = 0X00; // 正常呼叫
	public static final byte DATA_CALL_URGENT = 0X01; // 紧急呼叫
	public static final byte DATA_CALL_MONITOR = 0X02; // 监控呼叫

	// --应答数据域内容
	public static final byte DATA_CALL_SUCCESS = 0X00; // 呼叫成功
	public static final byte DATA_CALL_BUSY = 0X01; // 线路繁忙
	public static final byte DATA_CALL_REFUSE = 0X02; // 拒绝接听
	public static final byte DATA_CALL_CANNOT = 0X03; // 无法接听
	public static final byte DATA_RECONNECT_SUCCESS = 0X00; // 重连完成
	public static final byte DATA_RECONNECT_FAIL = 0X01; // 重连失败
	public static final byte DATA_END_SUCCESS = 0X00; // 结束成功
	public static final byte DATA_WORDS_SUCCESS = 0X00; // 留言接收成功
	public static final byte DATA_WORDS_ERROR = 0X01; // 留言接收异常

	public class PacketApp {

		public static final int APP_BASE_LENGTH = 17;

		public byte dir;
		public int srcId;
		public int dstId;
		public byte type;
		public int sn;
		public byte cmd;
		public int length;
		public byte[] data;

		public int toBytes(byte[] out, int offset) {
			int len = offset;
			out[len++] = dir;
			out[len++] = (byte) (srcId & 0xff);
			out[len++] = (byte) ((srcId >> 8) & 0xff);
			out[len++] = (byte) ((srcId >> 16) & 0xff);
			out[len++] = (byte) ((srcId >> 24) & 0xff);
			out[len++] = (byte) (dstId & 0xff);
			out[len++] = (byte) ((dstId >> 8) & 0xff);
			out[len++] = (byte) ((dstId >> 16) & 0xff);
			out[len++] = (byte) ((dstId >> 24) & 0xff);
			out[len++] = type;
			out[len++] = (byte) (sn & 0xff);
			out[len++] = (byte) ((sn >> 8) & 0xff);
			out[len++] = (byte) ((sn >> 16) & 0xff);
			out[len++] = (byte) ((sn >> 24) & 0xff);
			out[len++] = cmd;
			out[len++] = (byte) (length & 0xff);
			out[len++] = (byte) ((length >> 8) & 0xff);
			if (length > 0) {
				System.arraycopy(data, 0, out, len, length);
				len += length;
			}
			return len - offset;
		}

		public int getLengthOfBytes() {
			return (APP_BASE_LENGTH + length);
		}

		public boolean toApp(byte[] in, int offset, int len) {

			if (len < APP_BASE_LENGTH) {
				return false;
			}

			dir = in[offset++];
			srcId = in[offset++] & 0xff;
			srcId += ((in[offset++] & 0xff) << 8);
			srcId += ((in[offset++] & 0xff) << 16);
			srcId += ((in[offset++] & 0xff) << 24);
			dstId = in[offset++] & 0xff;
			dstId += ((in[offset++] & 0xff) << 8);
			dstId += ((in[offset++] & 0xff) << 16);
			dstId += ((in[offset++] & 0xff) << 24);
			type = in[offset++];
			sn = in[offset++] & 0xff;
			sn += ((in[offset++] & 0xff) << 8);
			sn += ((in[offset++] & 0xff) << 16);
			sn += ((in[offset++] & 0xff) << 24);
			cmd = in[offset++];
			length = in[offset++] & 0xff;
			length += ((in[offset++] & 0xff) << 8);
			if (length > 0) {
				data = new byte[length];
				System.arraycopy(in, offset, data, 0, length);
			}
			return true;
		}
	}

	public class Packet {

		public static final int PACKET_BASE_LENGTH = 9;

		public byte stx;
		public int length;
		public byte encrypt;
		public PacketApp app;
		public int check;
		public byte etx;

		@Override
		public String toString() {
			String str = "-----------------------------------";
			str += "\r\nstx:" + stx;
			str += "\r\nlength:" + length;
			str += "\r\nencrypt:" + encrypt;
			str += "\r\napp:";
			str += "\r\n     dir:" + app.dir;
			str += "\r\n     srcId:" + app.srcId;
			str += "\r\n     dstId:" + app.dstId;
			str += "\r\n     type:" + app.type;
			str += "\r\n     sn:" + app.sn;
			str += "\r\n     cmd:" + app.cmd;
			str += "\r\n     length:" + app.length;
			str += "\r\ncheck:" + check;
			str += "\r\netx:" + etx;
			str += "\r\n-----------------------------------";
			return str;
		}

		public byte[] toBytes(byte[] packetKey) {
			int len = 0;
			if (app == null) {
				return null;
			}
			// --加密报文
			byte[] out = encryptApp(packetKey, 4);

			length += 1;
			out[len++] = STX;
			out[len++] = (byte) (length & 0xff);
			out[len++] = (byte) ((length >> 8) & 0xff);
			out[len++] = encrypt;
			len += (length - 1);
			// --计算校验值
			check = 0;
			for (int i = 0; i < (length - 1); i++) {
				check += (out[4 + i] & 0XFF);
			}
			out[len++] = (byte) (check & 0xff);
			out[len++] = (byte) ((check >> 8) & 0xff);
			out[len++] = (byte) ((check >> 16) & 0xff);
			out[len++] = (byte) ((check >> 24) & 0xff);
			out[len++] = ETX;

			return out;
		}

		public byte[] toBase64Bytes(byte[] packetKey) {
			byte[] out = toBytes(packetKey);

			return Base64.encode(out, 0, out.length, Base64.DEFAULT);
		}

		private byte[] encryptApp(byte[] key, int offset) {

			int appLen = app.getLengthOfBytes();

			byte[] tmp = new byte[appLen];

			app.toBytes(tmp, 0);

			byte[] encBuf = null;

			try {
				switch (encrypt) {
				case ENC_NONE:
					encBuf = tmp;
					break;
				case ENC_DES:
					encBuf = Cryptic.desEncrypt(tmp, key);
					break;
				case ENC_3DES:
					encBuf = Cryptic.des3Encrypt(tmp, key);
					break;
				case ENC_AES:
					break;
				case ENC_RSA:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				encBuf = tmp;
			}

			byte[] out = new byte[encBuf.length + PACKET_BASE_LENGTH];

			System.arraycopy(encBuf, 0, out, offset, encBuf.length);

			this.length = encBuf.length;

			encBuf = null;

			return out;
		}

		public boolean getPacketFromBase64(byte[] in, int offset, int len,
				byte[] packetKey) {

			byte[] packetByte = Base64.decode(in, offset, len, Base64.DEFAULT);

			return toPacket(packetKey, packetByte, 0, packetByte.length);

		}

		public boolean toPacket(byte[] packetKey, byte[] in, int offset, int len) {

			if (len < (PACKET_BASE_LENGTH + PacketApp.APP_BASE_LENGTH)) {
				return false;
			}

			if (in[offset++] != STX) {
				return false;
			}

			this.length = in[offset++] & 0xff;
			this.length += ((in[offset++] & 0xff) * 256);
			this.encrypt = in[offset++];
			byte[] tmp = new byte[this.length - 1];
			System.arraycopy(in, offset, tmp, 0, this.length - 1);

			offset += (length - 1);

			this.check = in[offset++] & 0xff;
			this.check += ((in[offset++] & 0xff) << 8);
			this.check += ((in[offset++] & 0xff) << 16);
			this.check += ((in[offset++] & 0xff) << 24);

			// Log.i("PROT", "check:" + check);

			int check_tmp = 0;
			for (int i = 0; i < (length - 1); i++) {
				check_tmp += (in[4 + i] & 0xff);
			}
			// Log.i("PROT", "check_tmp:" + check_tmp);
			if (check != check_tmp) {
				return false;
			}

			this.etx = in[offset++];

			if (this.etx != ETX) {
				return false;
			}

			try {
				byte[] encBuf = null;
				switch (encrypt) {
				case ENC_DES:
					encBuf = Cryptic.desDecrypt(tmp, packetKey);
					break;
				case ENC_3DES:
					encBuf = Cryptic.des3Decrypt(tmp, packetKey);
					break;
				case ENC_AES:
					break;
				case ENC_NONE:
					encBuf = tmp;
					break;
				}

				if (app.toApp(encBuf, 0, encBuf.length) == false) {
					return false;
				}

				encBuf = null;
				tmp = null;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;

		}

		public void init() {
			stx = STX;
			length = 0;
			check = 0;
			encrypt = ENC_3DES;
			etx = ETX;
			app.dir = DIR_DEFAULT;
			app.srcId = SRC_ID;
			app.dstId = DST_ID;
			app.type = TYPE_REQUEST;
			app.sn = 0;
			app.cmd = 0;
			app.length = 0;
			app.data = null;
		}

		public int getLengthOfBytes() {
			return (PACKET_BASE_LENGTH + length - 1);
		}
	}

	public Packet packet = null;

	public NetworkProtocol() {
		packet = new Packet();
		packet.app = new PacketApp();
	}
}
