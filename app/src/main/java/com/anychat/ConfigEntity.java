package com.anychat;

public class ConfigEntity {
	public static final int VIDEO_MODE_SERVERCONFIG = 0;	// ��������Ƶ������??
	public static final int VIDEO_MODE_CUSTOMCONFIG = 1;	// �Զ�����Ƶ������??
	
	public static final int VIDEO_QUALITY_NORMAL = 2;		// ��???��Ƶ��??
	public static final int VIDEO_QUALITY_GOOD = 3;			// �е���Ƶ����
	public static final int VIDEO_QUALITY_BEST = 4;			// �Ϻ���Ƶ����
	
	public boolean IsSaveNameAndPw;
	public String name = "";
	public String password = "";

	public String ip = "";
	public String guid = "";
	public int port;
	
	public int configMode = VIDEO_MODE_CUSTOMCONFIG;//VIDEO_MODE_SERVERCONFIG;
	public int resolution_width = 0;
	public int resolution_height = 0;
	
	public int videoBitrate = 150*1000;						// ������Ƶ����
	public int videoFps = 10;								// ������Ƶ֡��
	public int videoQuality = VIDEO_QUALITY_BEST;//VIDEO_QUALITY_GOOD;
	public int videoPreset = 1;
	public int videoOverlay = 1;							// ������Ƶ�Ƿ����Overlayģʽ
	public int videorotatemode = 0;							// ������Ƶ��תģʽ
	public int fixcolordeviation = 0;						// ����������Ƶ�ɼ�ƫɫ??0 �ر�(Ĭ�ϣ��� 1 ????
	public int videoShowGPURender = 0;						// ��Ƶ����ͨ��GPUֱ����Ⱦ??0  �ر�(Ĭ��)?? 1 ????
	public int videoAutoRotation = 1;						// ������Ƶ�Զ���ת���ƣ�����Ϊint�ͣ� 0��ʾ�ر�?? 1 ??��[Ĭ��]����Ƶ��תʱ??Ҫ�ο�������Ƶ�豸���������
	
	public int enableP2P = 1;
	public int useARMv6Lib = 0;								// �Ƿ�ǿ��ʹ��ARMv6ָ���Ĭ�����ں��Զ���??
	public int enableAEC = 1;								// �Ƿ�ʹ�û�����������
	public int useHWCodec = 0;								// �Ƿ�ʹ��ƽ̨����Ӳ���������
}
