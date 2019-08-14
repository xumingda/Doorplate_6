package com.bairuitech.anychat;

public class AnyChatObjectDefine {

	// �������Ͷ���
	public static final int ANYCHAT_OBJECT_TYPE_AREA		=	4;		///< ��������
	public static final int ANYCHAT_OBJECT_TYPE_QUEUE		=	5;		///< ���ж���
	public static final int ANYCHAT_OBJECT_TYPE_AGENT		=	6;		///< �ͷ�����
	public static final int ANYCHAT_OBJECT_TYPE_CLIENTUSER	=	8;		///< �ͻ����û������������������������

	// ͨ�ñ�ʶ����
	public static final int ANYCHAT_OBJECT_FLAGS_CLIENT		=	0;		///< ��ͨ�ͻ�
	public static final int ANYCHAT_OBJECT_FLAGS_AGENT		=	2;		///< ��ϯ�û�
	public static final int ANYCHAT_OBJECT_FLAGS_MANANGER	=	4;		///< �����û�

	public static final int ANYCHAT_INVALID_OBJECT_ID		=	-1;		///< ��Ч�Ķ���ID

	// ��ϯ����״̬����
	public static final int ANYCHAT_AGENT_STATUS_CLOSEED	=	0;		///< �رգ��������ṩ����
	public static final int ANYCHAT_AGENT_STATUS_WAITTING	=	1;		///< �ȴ��У�����ʱ�����û�����
	public static final int ANYCHAT_AGENT_STATUS_WORKING	=	2;		///< �����У�����Ϊ�û�����
	public static final int ANYCHAT_AGENT_STATUS_PAUSED		=	3;		///< ��ͣ����


	/**
	 *	�������Զ���
	 */

	// ���󹫹���Ϣ���Ͷ���
	public static final int ANYCHAT_OBJECT_INFO_FLAGS		=	7;		///< �������Ա�־
	public static final int ANYCHAT_OBJECT_INFO_NAME		=	8;		///< ��������
	public static final int ANYCHAT_OBJECT_INFO_PRIORITY	=	9;		///< �������ȼ�
	public static final int ANYCHAT_OBJECT_INFO_ATTRIBUTE	=	10;		///< ����ҵ������
	public static final int ANYCHAT_OBJECT_INFO_DESCRIPTION	=	11;		///< ��������
	public static final int ANYCHAT_OBJECT_INFO_INTTAG		=	12;		///< �����ǩ�����ͣ��ϲ�Ӧ���Զ���
	public static final int ANYCHAT_OBJECT_INFO_STRINGTAG	=	13;		///< �����ǩ���ַ������ϲ�Ӧ���Զ���

	// ����������Ϣ���Ͷ���
	public static final int ANYCHAT_AREA_INFO_AGENTCOUNT	=	401;	///< ��������ͷ��û���
	public static final int ANYCHAT_AREA_INFO_GUESTCOUNT	=	402;	///< ���������ڷÿ͵��û�����û��������е��û���
	public static final int ANYCHAT_AREA_INFO_QUEUEUSERCOUNT=	403;	///< �����������Ŷӵ��û���
	public static final int ANYCHAT_AREA_INFO_QUEUECOUNT	=	404;	///< ���������ڶ��е�����

	// ����״̬��Ϣ���Ͷ���
	public static final int ANYCHAT_QUEUE_INFO_MYSEQUENCENO	=	501;	///< �Լ��ڸö����е����
	public static final int ANYCHAT_QUEUE_INFO_BEFOREUSERNUM=	502;	///< �����Լ�ǰ	����û���
	public static final int ANYCHAT_QUEUE_INFO_MYENTERQUEUETIME=503;	///< ������е�ʱ��
	public static final int ANYCHAT_QUEUE_INFO_LENGTH		=	504;	///< ���г��ȣ��ж��������Ŷӣ�������
	public static final int ANYCHAT_QUEUE_INFO_WAITTIMESECOND=	508;	///< �Լ��ڶ����еĵȴ�ʱ�䣨�Ŷ�ʱ��������λ����

	// �ͷ�״̬��Ϣ���Ͷ���
	public static final int ANYCHAT_AGENT_INFO_SERVICESTATUS=	601;	///< ����״̬������
	public static final int ANYCHAT_AGENT_INFO_SERVICEUSERID=	602;	///< ��ǰ������û�ID������
	public static final int ANYCHAT_AGENT_INFO_SERVICEBEGINTIME=603;	///< ��ǰ����Ŀ�ʼʱ�䣬����
	public static final int ANYCHAT_AGENT_INFO_SERVICETOTALTIME=604;	///< �ۼƷ���ʱ�䣬���ͣ���λ����
	public static final int ANYCHAT_AGENT_INFO_SERVICETOTALNUM=	605;	///< �ۼƷ�����û���������


	/**
	 *	���󷽷�����
	 */

	// ���󹫹��������Ƴ�������
	public static final int ANYCHAT_OBJECT_CTRL_CREATE		=	2;		///< ����һ������
	public static final int ANYCHAT_OBJECT_CTRL_SYNCDATA	=	3;		///< ͬ���������ݸ�ָ���û���dwObjectId=-1����ʾͬ�������͵����ж���
	public static final int ANYCHAT_OBJECT_CTRL_DEBUGOUTPUT	=	4;		///< ���������Ϣ���

	// ����������Ƴ�������
	public static final int ANYCHAT_AREA_CTRL_USERENTER		=	401;	///< �����������
	public static final int ANYCHAT_AREA_CTRL_USERLEAVE		=	402;	///< �뿪��������

	// ���в������Ƴ�������
	public static final int ANYCHAT_QUEUE_CTRL_USERENTER	=	501;	///< �������
	public static final int ANYCHAT_QUEUE_CTRL_USERLEAVE	=	502;	///< �뿪����

	// �ͷ��������Ƴ�������
	public static final int ANYCHAT_AGENT_CTRL_SERVICESTATUS=	601;	///< ��ϯ����״̬���ƣ���ͣ���񡢹����С��رգ�
	public static final int ANYCHAT_AGENT_CTRL_SERVICEREQUEST=	602;	///< ��������
	public static final int ANYCHAT_AGENT_CTRL_FINISHSERVICE=	604;	///< ��������
	public static final int ANYCHAT_AGENT_CTRL_EVALUATION	=	605;	///< �������ۣ�wParamΪ�ͷ�userid��lParamΪ���֣�lpStrValueΪ����



	/**
	 *	�����첽�¼�����
	 */

	// ���󹫹��¼���������
	public static final int ANYCHAT_OBJECT_EVENT_UPDATE			= 1;	///< �������ݸ���
	public static final int ANYCHAT_OBJECT_EVENT_SYNCDATAFINISH = 2;	///< ��������ͬ������

	// ���������¼���������
	public static final int ANYCHAT_AREA_EVENT_STATUSCHANGE	=	401;	///< ��������״̬�仯
	public static final int ANYCHAT_AREA_EVENT_ENTERRESULT	=	402;	///< �������������
	public static final int ANYCHAT_AREA_EVENT_USERENTER	=	403;	///< �û������������
	public static final int ANYCHAT_AREA_EVENT_USERLEAVE	=	404;	///< �û��뿪��������
	public static final int ANYCHAT_AREA_EVENT_LEAVERESULT	=	405;	///< �뿪����������

	// �����¼���������
	public static final int ANYCHAT_QUEUE_EVENT_STATUSCHANGE=	501;	///< ����״̬�仯
	public static final int ANYCHAT_QUEUE_EVENT_ENTERRESULT	=	502;	///< ������н��
	public static final int ANYCHAT_QUEUE_EVENT_USERENTER	=	503;	///< �û��������
	public static final int ANYCHAT_QUEUE_EVENT_USERLEAVE	=	504;	///< �û��뿪����
	public static final int ANYCHAT_QUEUE_EVENT_LEAVERESULT	=	505;	///< �뿪���н��

	// ��ϯ�¼���������
	public static final int ANYCHAT_AGENT_EVENT_STATUSCHANGE=	601;	///< ��ϯ״̬�仯
	public static final int ANYCHAT_AGENT_EVENT_SERVICENOTIFY=	602;	///< ��ϯ����֪ͨ���ĸ��û����ĸ��ͷ�����ҵ��
	public static final int ANYCHAT_AGENT_EVENT_WAITINGUSER	=	603;	///< ��ʱû�пͻ�����ȴ�

	
	 
}