package com.bairuitech.anychat;

import com.bairuitech.anychat.AnyChatOutParam;

// ���ݼ��ܡ����ܽӿ�
public interface AnyChatDataEncDecEvent {

	// ���ݼӣ��⣩�ܱ�־���壨DataEncDec�ص�������
	public static final int BRAC_DATAENCDEC_FLAGS_ENCMODE	=	0x01;	///< ����ģʽ
	public static final int BRAC_DATAENCDEC_FLAGS_DECMODE	=	0x02;	///< ����ģʽ
	public static final int BRAC_DATAENCDEC_FLAGS_AUDIO		=	0x10;	///< ��Ƶ��������
	public static final int BRAC_DATAENCDEC_FLAGS_VIDEO		=	0x20;	///< ��Ƶ��������
	public static final int BRAC_DATAENCDEC_FLAGS_BUFFER	=	0x40;	///< ͸��ͨ������
	public static final int BRAC_DATAENCDEC_FLAGS_TXTMSG	=	0x80;	///< ������������

	// ���ݼ��ܡ����ܻص���������
	public int OnAnyChatDataEncDec(int userid, int flags, byte[] lpBuf, int dwLen, AnyChatOutParam outParam);
}
