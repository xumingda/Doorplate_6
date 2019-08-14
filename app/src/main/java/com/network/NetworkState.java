package com.network;

public class NetworkState {
	/**
	 * ��ʼ��״̬
	 */
	public static final int STATE_INIT = 1;
	/**
	 * ���粻����
	 */
	public static final int STATE_NETWORK_DISABLED = 2;
	/**
	 * ������ã���״̬�²����������л��߱���
	 */
	public static final int STATE_NETWORK_ENABLED = 3;
	/**
	 * ����
	 */
	public static final int STATE_CALL_ED = 4;
	/**
	 * ͨ����
	 */
	public static final int STATE_CALL_IN = 5;
	/**
	 * ͨ������
	 */
	public static final int STATE_CALL_END = 6;
	/**
	 * �������У��ȴ��Է�Ӧ��
	 */
	public static final int STATE_CALL_WAIT = 7;
	/**
	 * �Է�����Ӧ����·��æ
	 */
	public static final int STATE_CALL_BUSY = 8;
	/**
	 * �Է�����Ӧ�𣬾ܾ�����
	 */
	public static final int STATE_CALL_REFUSE = 9;
	/**
	 * �Է�����Ӧ���޷�����
	 */
	public static final int STATE_CALL_CANNOT = 10;
	/**
	 * ���ͨ����
	 */
	public static final int STATE_CALL_IN_MONITOR = 16;
	/**
	 * �����쳣
	 */
	public static final int STATE_CALL_ERROR = -1;
	/**
	 * ͨѶ��ʱ
	 */
	public static final int STATE_TIMEOUT = -2;
	/**
	 * ���ͳɹ�
	 */
	public static final int STATE_SEND_SUCCESS = 0;
	/**
	 * �����쳣
	 */
	public static final int STATE_SEND_ERROR = -1;
	/**
	 * �����У��ȴ�Ӧ��
	 */
	public static final int STATE_SEND_ING = 1;
}
