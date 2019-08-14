package com.yy.doorplate.protocol;

import android.util.Log;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.tool.General;
import com.yy.doorplate.tool.MyEncrypt;
import com.yy.doorplate.tool.SystemManager;

import org.apache.http.util.ByteArrayBuffer;

public class TcpProtocol {

	private static final String TAG = "TcpProtocol";

	public static final byte[] FinalBao = { 0x38, 0x33, 0x35, 0x39, 0x32, 0x36,
			0x30, 0x30 };

	private static final byte PACKET_STX = (byte) 0xF3;
	private static final byte PACKET_ETX = (byte) 0xF4;

	public static final byte ENCRYPT_TYPE_NO = 0x00; // ������
	public static final byte ENCRYPT_TYPE_3DES = 0x01; // 3DES
	public static final byte ENCRYPT_TYPE_DES = 0x02; // DES
	public static final byte ENCRYPT_TYPE_AES = 0x03; // AES
	public static final byte ENCRYPT_TYPE_RSA = 0x04; // RSA

	public static final byte PACKET_CMD_START = (byte) 0x01; // ��ʼ����
	public static final byte PACKET_CMD_END = (byte) 0x02; // �����Ự
	public static final byte PACKET_CMD_AUTH = (byte) 0x03; // ��Ȩ
	public static final byte PACKET_CMD_HEART = (byte) 0x04; // ����
	public static final byte PACKET_CMD_PRE_DATA = (byte) 0x04; // ͨ��ǰ�÷�������ȡIP

	public static final byte PACKET_CMD_UPDATA_EQ = (byte) 0x01; // �豸���ø���
	public static final byte PACKET_CMD_NOTICE = (byte) 0x02; // ����֪ͨ
	public static final byte PACKET_CMD_CURRICULUM = (byte) 0x03; // �α����
	public static final byte PACKET_CMD_UPDATA_TIME = (byte) 0x04; // ͬ��ʱ��
	public static final byte PACKET_CMD_UPDATA_EQLIST = (byte) 0x05; // �����豸�б�
	public static final byte PACKET_CMD_PLAY_TASK = (byte) 0x06; // ���������·�
	public static final byte PACKET_CMD_UPDATA_CONTROL = (byte) 0x07; // ���ܼҾӿ����б�ͬ��
	public static final byte PACKET_CMD_CONTROL = (byte) 0x08; // ���ܼҾӿ��������·���data���������͡�ID��״̬����������1�ֽ�00���豸��01��������ID
																// 1�ֽڣ�״̬��00������01:��;
	public static final byte PACKET_CMD_PRIZE = (byte) 0x09; // ����ǽ��Ϣ����
	public static final byte PACKET_CMD_BLACKBOARD = (byte) 0x0A; // С�ڰ���Ϣ����
	public static final byte PACKET_CMD_ONDUTY = (byte) 0x0B; // ֵ����Ϣ����
	public static final byte PACKET_CMD_SCHOOL = (byte) 0x0C; // ѧУ��Ϣ����
	public static final byte PACKET_CMD_QUEST = (byte) 0x0D; // �ʾ������Ϣ����
	public static final byte PACKET_CMD_EQUINFO = (byte) 0x0E; // �豸������Ϣ����
	public static final byte PACKET_CMD_EQUMODE = (byte) 0x0F; // �豸ģʽ�������
	public static final byte PACKET_CMD_EXAM = (byte) 0x10; // ���԰��Ÿ���
	public static final byte PACKET_CMD_PAGE = (byte) 0x11; // ҳ�沼�ָ���
	public static final byte PACKET_CMD_VIDEO = (byte) 0x12; // С��Ƶ�·�
	public static final byte PACKET_CMD_USER = (byte) 0x13; // ������Ȩ�û�
	public static final byte PACKET_CMD_VOTE = (byte) 0x14; // ͶƱ����
	public static final byte PACKET_CMD_ACTIVITY = (byte) 0x15; // ���������
	public static final byte PACKET_CMD_PHOTO = (byte) 0x16; // �������
	public static final byte PACKET_CMD_SYNEQU = (byte) 0x17; // ͬ���豸��Ϣ
	public static final byte PACKET_CMD_UPDATEATTEND = (byte) 0x18; // ���¿�����Ϣ

	public static final byte PACKET_CMD_OFF = (byte) 0xA0; // �ػ�
	public static final byte PACKET_CMD_REBOOT = (byte) 0xA1; // ����
	public static final byte PACKET_CMD_RELOAD = (byte) 0xA2; // ����Ӧ��
	public static final byte PACKET_CMD_VERSION_CHECK = (byte) 0xA3; // �汾��ѯ

	public static final byte SYSTEM_CODE_FSB = (byte) 0xA0; // ǰ�ÿ��Ʒ�����
	public static final byte SYSTEM_CODE_DP = (byte) 0xB3; // ���Ӱ���ƽ̨
	public static final byte SYSTEM_EQU_DP = (byte) 0xA5; // ���Ӱ����豸����
	public static final byte SYSTEM_CODE_PRE = (byte) 0xC0; // ǰ�÷�����

	public static final byte PACKET_TYPE_REQUEST = 0x01; // ����
	public static final byte PACKET_TYPE_ANSWER = 0x02; // Ӧ��

	public byte[] PacketKey = new byte[8];
	public String MyKey = "";
	public static byte[] ServerSn = new byte[8 + 1];

	public byte[] myID = new byte[8];
	public String serverID = "00000000";
	public static byte EncryptType = ENCRYPT_TYPE_NO;

	private int countCheck(byte[] app, int length) {
		int check = 0;
		int i = 0;
		// General.debugHex(TAG, app, length);
		for (i = 0; i < length; i++) {
			check += (byte) app[i] & 0xFF;
		}
		return check;
	}

	public class AppData {
		public byte from;
		public byte cmd;
		public byte[] data;
		public int datalen;
		public String sn;

		public String toString(){
			return "AppData��{"+"from:"+String.format("%02x",from)+","
					+"cmd:"+String.format("%02x",cmd)+","
					+ "data:["+byteArraytoString(data)+"],"
					+"sn"
					+"}";
		}

		private String byteArraytoString(byte[] data){
			String str="";
			for (byte b:data) {
				str+=String.format("%02X ",b);
			}
			return str;
		}
	}

	/*
	 * ��� app app������ length app���� packetout ���������� encrypt_type �������� ����ֵ ������ݳ���
	 */
	public int pack(byte[] app, int length, byte[] packetout, byte encrypttype) {
		int len = 0;
		packetout[len] = PACKET_STX;
		len += 1;
		// Log.i(TAG, "length = " + length);
		// 4�ֽڳ��� ��������

		len += 2;

		packetout[len] = encrypttype;
		len += 1;
		// Log.i(TAG, "������Կ = "+PacketKey);
		switch (encrypttype) {
		case ENCRYPT_TYPE_3DES: {
			try {
				app = MyEncrypt.des3Encrypt(app, PacketKey);
				if (app == null) {
					return 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			break;
		case ENCRYPT_TYPE_DES: {
			try {
				app = MyEncrypt.desEncrypt(app, PacketKey);
				if (app == null) {
					return 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		}
		// byte[] data = Base64.encode(app, Base64.DEFAULT);

		System.arraycopy(app, 0, packetout, len, length);
		packetout[1] = (byte) (length & 0xFF);
		packetout[2] = (byte) ((length >> 8) & 0xFF);
		// Log.i(TAG, "BASE 64 len = "+ data.length);
		len += length;
		int check = countCheck(app, length);
		packetout[len] = (byte) (check & 0xFF);
		packetout[len + 1] = (byte) ((check >> 8) & 0xFF);
		packetout[len + 2] = (byte) ((check >> 16) & 0xFF);
		packetout[len + 3] = (byte) ((check >> 24) & 0xFF);
		len += 4;
		packetout[len] = PACKET_ETX;
		len += 1;
		return len;
	}

	/*
	 * ��� data ���յ������� length ���յ������ݳ��� app ���ڲ������������APP�� ����ֵ��app����
	 */
	public int unpack(byte[] data, int length, byte[] app) {
		int tmp = 0;
		int len = 0;
		byte[] apptmp = new byte[1024];
		int check = 0;
		if (data == null) {
			Log.i(TAG, "data = null");
			apptmp = null;
			return 0;
		}
		SystemManager.LogHex(TAG, data, length);
		if (data[tmp] != PACKET_STX || data[length - 1] != PACKET_ETX) {

			Log.i(TAG, "��ͷ��β��");
			apptmp = null;
			return 0;
		}

		len = (int) ((data[1] & 0xFF) | ((data[2] & 0xFF) << 8));
		if (len + 9 != length) {
			Log.i(TAG, "���ȴ���");
			apptmp = null;
			return 0;
		}
		tmp += 3;
		EncryptType = data[tmp];
		tmp += 1;
		System.arraycopy(data, tmp, apptmp, 0, len);
		tmp += len;

		check = (int) ((data[tmp] & 0xFF) | ((data[tmp + 1] & 0xFF) << 8)
				| ((data[tmp + 2] & 0xFF) << 16) | ((data[tmp + 3] & 0xFF) << 24));
		tmp += 4;
		if (check != countCheck(apptmp, len)) {
			Log.i(TAG, "check err"+countCheck(apptmp, len));
			apptmp = null;
			return 0;
		}
		// byte[] datatmp = Base64.decode(apptmp, Base64.DEFAULT);

		try {
			switch (EncryptType) {
			case ENCRYPT_TYPE_3DES:
				app = MyEncrypt.des3UnEncrypt(apptmp, MyKey.getBytes());
				break;
			case ENCRYPT_TYPE_AES:
				break;
			case ENCRYPT_TYPE_DES:
				app = MyEncrypt.desUnEncrypt(apptmp, MyKey.getBytes());
				break;
			case ENCRYPT_TYPE_NO:
				System.arraycopy(apptmp, 0, app, 0, len);
				break;
			case ENCRYPT_TYPE_RSA:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		apptmp = null;
		return len;
	}

	/*
	 * APP��� data �����data�� cmd ���� type ����/Ӧ�� out ������� return int ��������ݳ���
	 */
	public int packApp(AppData app, byte type, byte[] out, byte to) {
		int len = 0;

		if (type == PACKET_TYPE_REQUEST) {
			out[len] = SYSTEM_EQU_DP;
			len++;
			out[len] = to;
			len++;
			myID = MyApplication.getTcpEquId();
			System.arraycopy(myID, 0, out, len, myID.length);
			len += 8;
			System.arraycopy(serverID.getBytes(), 0, out, len, 8);
			len += 8;
			System.arraycopy(General.gettimems(), 0, out, len, 8);
			len += 8;
		} else {
			out[len] = SYSTEM_EQU_DP;
			len++;
			out[len] = app.from;
			len++;
			myID = MyApplication.getTcpEquId();
			System.arraycopy(myID, 0, out, len, myID.length);
			len += 8;
			System.arraycopy(serverID.getBytes(), 0, out, len, 8);
			len += 8;
			System.arraycopy(ServerSn, 0, out, len, 8);
			len += 8;
		}

		out[len] = app.cmd;
		len += 1;
		out[len] = (byte) (app.datalen & 0xFF);
		out[len + 1] = (byte) ((app.datalen >> 8) & 0xFF);
		len += 2;
		if (app.datalen > 0) {
			System.arraycopy(app.data, 0, out, len, app.datalen);
		}
		len += app.datalen;

		SystemManager.LOGI(TAG, "---------�������--------");
		SystemManager.LogHex(TAG, out, len);
		SystemManager.LOGI(TAG, "--------------------------");

		return len;
	}

	/*
	 * ���APP app ��Ҫ������app������ applen ���� ����ֵ AppData �����������
	 */
	public AppData unpackApp(byte[] app, int applen) {
		AppData appdata = new AppData();
		int len = 0;
		// �жϴ��ĸ�ϵͳ����������
		appdata.from = app[len];
		// SystemManager.LOGI(TAG, "from =" + appdata.from);
		len += 2;
		serverID = new String(app, len, 8);
		len += 16;
		byte[] sn = new byte[8];
		System.arraycopy(app, len, ServerSn, 0, 8);
		len += 8;
		System.arraycopy(ServerSn, 0, sn, 0, 8);
		appdata.cmd = app[len];
		len++;

		int length = (int) ((app[len] & 0xFF) | ((app[len + 1] & 0xFF) << 8));
		len += 2;

		byte[] data = new byte[length];
		System.arraycopy(app, len, data, 0, length);
		appdata.data = data;
		appdata.datalen = length;

		appdata.sn = "";
		for (int i = 0; i < 6; i++) {
			int temp = sn[i] & 0xff;
			if (temp < 10) {
				appdata.sn += "0" + temp;
			} else {
				appdata.sn += temp;
			}
		}
		int temp = 0;
		temp = sn[6] & 0xff;
		temp += ((sn[7] & 0xff) << 8);
		if (temp < 10) {
			appdata.sn += "00" + temp;
		} else if (temp < 100) {
			appdata.sn += "0" + temp;
		} else {
			appdata.sn += temp;
		}
		SystemManager.LOGI(TAG, "---------��������--------");
		SystemManager.LOGI(TAG,appdata.toString());
		SystemManager.LOGI(TAG, "--------------------------");

		return appdata;
	}

	/*
	 * Ӧ�� cmd Ӧ������ out ���͵����� ����ֵ�� len �����ĳ���
	 */
	public int response(byte cmd, byte[] out, byte ret, byte from) {
		AppData app = new AppData();
		app.cmd = cmd;
		byte[] data = new byte[64];
		byte[] app_data = new byte[1];
		app_data[0] = ret;
		app.data = app_data;
		app.datalen = 1;
		app.from = from;
		SystemManager.LOGI(TAG, "Ӧ��");
		int applen = packApp(app, PACKET_TYPE_ANSWER, data, (byte) 0);
		return pack(data, applen, out, EncryptType);
	}

	public int request(byte cmd, byte[] data, int datalen, byte[] out, byte to) {
		AppData app = new AppData();
		app.cmd = cmd;
		byte[] appdata = new byte[64];
		if (data != null) {
			app.data = data;
			app.datalen = datalen;
		} else {
			app.datalen = 0;
		}
		int applen = packApp(app, PACKET_TYPE_REQUEST, appdata, to);
		return pack(appdata, applen, out, EncryptType);
	}
}
