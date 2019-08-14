package com.network;

public class NetworkState {
	/**
	 * 初始化状态
	 */
	public static final int STATE_INIT = 1;
	/**
	 * 网络不可用
	 */
	public static final int STATE_NETWORK_DISABLED = 2;
	/**
	 * 网络可用，此状态下才能主动呼叫或者被叫
	 */
	public static final int STATE_NETWORK_ENABLED = 3;
	/**
	 * 被叫
	 */
	public static final int STATE_CALL_ED = 4;
	/**
	 * 通话中
	 */
	public static final int STATE_CALL_IN = 5;
	/**
	 * 通话结束
	 */
	public static final int STATE_CALL_END = 6;
	/**
	 * 主动呼叫，等待对方应答
	 */
	public static final int STATE_CALL_WAIT = 7;
	/**
	 * 对方返回应答，线路繁忙
	 */
	public static final int STATE_CALL_BUSY = 8;
	/**
	 * 对方返回应答，拒绝接听
	 */
	public static final int STATE_CALL_REFUSE = 9;
	/**
	 * 对方返回应答，无法接听
	 */
	public static final int STATE_CALL_CANNOT = 10;
	/**
	 * 监控通话中
	 */
	public static final int STATE_CALL_IN_MONITOR = 16;
	/**
	 * 呼叫异常
	 */
	public static final int STATE_CALL_ERROR = -1;
	/**
	 * 通讯超时
	 */
	public static final int STATE_TIMEOUT = -2;
	/**
	 * 发送成功
	 */
	public static final int STATE_SEND_SUCCESS = 0;
	/**
	 * 发送异常
	 */
	public static final int STATE_SEND_ERROR = -1;
	/**
	 * 发送中，等待应答
	 */
	public static final int STATE_SEND_ING = 1;
}
