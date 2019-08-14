package com.advertisement.system;

public class ConstData {

	
	
	/*Handler��Ϣ����*/
	public class ActivityMsgType{
		/*��Activity״̬�л���Ϣ*/
		public static final int MSG_INIT_VIEW=1;
		public static final int MSG_INIT_ENV=2;
		public static final int MSG_START_SERVICE=3;
		public static final int MSG_STOP_SERVICE=4;
		public static final int MSG_RESUME=5;
		/*��̨������Activity������Ϣ*/
		public static final int MSG_S2U_INSTALL_ST=20; //--��̨���񷵻ذ�װ״̬
		public static final int MSG_U2S_INSTALL_ST=30; //--ǰ̨UI����װ״̬
		public static final int MSG_U2S_INSTALL=31;    //--ǰ̨UI����װע���豸
		public static final int MSG_U2S_GET_SERVER=32; //--ǰ̨UI��ȡ��������Ϣ
		public static final int MSG_S2U_GET_SERVER=33; //--��̨Ӧ��ǰ̨UI��������Ϣ
		public static final int MSG_U2S_SET_SERVER=34; //--ǰ̨UI���÷�������Ϣ
		public static final int MSG_S2U_SET_SERVER=35; //--��̨Ӧ��ǰ̨UI���ý��
		public static final int MSG_U2S_GET_CONFIG=36; //--ǰ̨UI��ȡ������Ϣ
		public static final int MSG_S2U_GET_CONFIG=37; //--��̨Ӧ��������Ϣ
		public static final int MSG_U2S_UDISK_UPDATE=38; //--ǰ̨UI����U�̸��²�����Դ
		public static final int MSG_S2U_UDISK_UPDATE=39; //--��̨Ӧ��ǰ̨���½��
		public static final int MSG_S2U_UDISK_REPORT=40; //--��̨�ϱ�����������Ϣ
		public static final int MSG_U2S_REBOOT=41;       //--ϵͳ����
		
		/*���öԻ�����ʧ*/
		public static final int MSG_DIALOG_DISMISS=40; //--�Ի�����ʧ
	}
	
	/*״̬��*/
	public enum ActivityState{
		STATE_INIT_VIEW,
		STATE_INIT_ENV,
		STATE_START,
		STATE_STOP
	}
	
	/*�������״̬��*/
	public class NetworkState{
		public static final int STATE_INIT=1;
		public static final int STATE_NETWORK_DISABLED=2;
		public static final int STATE_NETWORK_ENABLED=3;
		public static final int STATE_CONNECTED=4;
		public static final int STATE_DISCONNECTED=5;
		public static final int STATE_RECV=6;
		public static final int STATE_SEND=7;
	}
	/*�������Handler��Ϣ����*/
	public class NetworkMsgType{
		public static final int MSG_NETWORK_DISABLED=1;
		public static final int MSG_NETWORK_ENABLED=2;
		public static final int MSG_CONNECTED=4;
		public static final int MSG_DISCONNECTED=5;
		public static final int MSG_REPORT_TRANS_STATUS=6;
	}
	/*�������ݱ��ķ���/���ջ�������*/
	public static final int NETWORK_SEND_BUF_SIZE=2048;
	public static final int NETWORK_RECV_BUF_SIZE=1700;
	/*�������Ļ�������*/
	public static final int NETOWRK_ANALYSE_BUF_SIZE=2048;
	
	/*��Դ������Ϣ����*/
	public class ResMsgType{
		public static final int MSG_ADD_DOWNLOAD=1;
		public static final int MSG_ADD_UPLOAD=2;
		public static final int MSG_TASK_FINISH=3;
		public static final int MSG_NETWORK_ENABLED=4;
		public static final int MSG_NETWORK_DISABLED=5;
	}
	
	/*��Դ��������������*/
	public class DownloadError{
		public static final int ERROR_NONE=0;
		public static final int ERROR_LOGIN=1;
		public static final int ERROR_GET_SIZE=2;
		public static final int ERROR_RECV_DATA=3;
		public static final int ERROR_PARAMER=4;
		public static final int ERROR_UNKNONWN=5; //--δ֪����
	}
	
	/*��Դ����*/
	public class FileTrans{
		//--���䷽��
		public static final byte DIR_DOWNLOAD=0X0;      //--����
		public static final byte DIR_UPLOAD=0X01;       //--�ϴ�
		//--����״̬
		public static final byte STATUS_TRANSING=0X06;  //--���������
		public static final byte STATUS_TRANS_FINISH=0X07;//--�������
		public static final byte STATUS_TRANS_ERROR=0X08; //--����ʧ��
	
		//--�ļ�����
		public static final int FILE_TYPE_PLAYBILL=0;   //--��Ŀ��
		public static final int FILE_TYPE_RESERVED=1;   //--����
		public static final int FILE_TYPE_TM_PARMER=2;  //--�ն˲���
		public static final int FILE_TYPE_APP=3;        //--�����ļ�
		public static final int FILE_TYPE_RES=4;        //--�ز��ļ�
		public static final int FILE_TYPE_DEF_ADV=5;    //--Ĭ�Ϲ��
		public static final int FILE_TYPE_THEME=6;      //--�����ļ�
		public static final int FILE_TYPE_THEME_POLICY=7;   //--�����л�
		public static final int FILE_TYPE_INSERT_POLICY=8;  //--�岥�����л�
		public static final int FILE_TYPE_PAD_POLICY=9; //--��Ƭ�����л�
		
		//--��Ŀ������
		public static final int PLAYBILL_TYPE_UNDEF=0;
		public static final int PLAYBILL_TYPE_IMAGE=1;
		public static final int PLAYBILL_TYPE_VIDEO=2;
		public static final int PLAYBILL_TYPE_LABEL=3;
		public static final int PLAYBILL_TYPE_CLOCK=4;
		public static final int PLAYBILL_TYPE_PANEL=5;
		public static final int PLAYBILL_TYPE_APPCODE=6;
	}
	
	/*ϵͳ������Ϣ����*/
	public class SystemMsgType{
		public static final int MSG_UPDATE_ERROR=-1;
		public static final int MSG_UPDATE_THEME_POLICY=1;
		public static final int MSG_REPORT_INSTALL_STATUS=2;
		public static final int MSG_REBOOT=3;
		public static final int MSG_SHUTDOWN=4;
		public static final int MSG_POWERON=5;
		public static final int MSG_UPDATE_APP=6;
	}
	
	
	/*����Activity�㲥action*/
	public static final String ACTIVITY_UPDATE="com.adv.update";
	/*���͸�����Ӧ�õĹ㲥action*/
	public static final String VMC_ACTION="com.vmc.action";
	/*����������Ǯ�ұ仯*/
	public static final String VMC_CASHBOX_ACTION="com.vmc.cashbox.action";
	
	/*��̨����Ӧ��UI���氲װ״̬*/
	public static class DevInstallStatus{
		public boolean isInstalled;
		public String installNo;
		public String installKey;
	}
	
	/*��̨������ǰ̨UI���潻������������*/
	public static class ServerInfo{
		public String ip1; //--��ѡ������IP
		public int port1;  //--��ѡ�������˿�
		public String ip2; //--���÷�����IP
		public int port2;  //--���÷������˿�
	}
	
	/*���Ź���������������������Ϣ*/
	public class DisplayMsgType{
		public static final int MSG_DISPLAY_FINISH=1; //--�������������һ��
		public static final int MSG_STOP_FAIL=2;      //--���������ʧ��
	}
	
	/*���˵������㲥��Ϣtheme��*/
	public static final String EXT_INFO_FILTER="ext_TEST";
	
	/*��̨�ϱ�UI������������Ϣ*/
	public static class UpdateInfo{
		public static final int TYPE_START=1;
		public static final int TYPE_COPY=2;
		public static final int TYPE_FINISH=3;
		public static final int TYPE_CP_RESULT=4;
		public static final int TYPE_SCAN=5;
		public int type;       //--����
		public int total;      //--�����ļ�����
		public int id;         //--�����ļ����
		public String src;     //--ԭʼ��ַ
		public String dst;     //--Ŀ���ַ
		public boolean success; //--�������
	}
}
