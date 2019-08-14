package com.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.camera.CameraManager;
import com.camera.CameraManager.CameraFrameCallback;
import com.common.AppLog;
import com.smart.videochat.VideoChatSdk;
import com.smart.videochat.VideoChatSdk.VideoChatListener;
import com.yy.doorplate.MyApplication;

public class NetworkManager {

	private static final String TAG = "NetworkManager";

	private static final boolean isDebug = true;

	// Socket通信端口
	public static final int PORT = 30000;
	// 超时时间
	public static final int TIMEOUT = 5000;
	// 心跳时间
	public static final int HEARTBEAT = 3000;
	// 心跳超时次数最大限制
	public static final int HEARTBEAT_TIMEOUT_COUNT = 2;
	// 主动呼叫等待多少次心跳时间视为超时
	public static final int CALL_TIMEOUT_COUNT = 10;
	// --DES加密key
	public byte[] PacketKey = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
			0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 };
	// 是否Base64打包
	public static final boolean isBase64Packet = true;

	// 接收byte的固定长度
	private static final int recvbuf_lenght = 2048;

	private MyApplication mContext = null;

	private ServerSocket mServerSocket = null;

	private NetworkHandler mHandler = null;
	// serversocket服务线程
	private ServerSocketThread serverSocketThread = null;
	// clientsocket线程
	private ClientSocketThread clientSocketThread = null;
	// clientSocketThread_urgent线程
	private ClientSocketThread clientSocketThread_urgent = null;
	// 被呼叫中的DoCalledRunnable
	private ServerSocketThread.DoCalledRunnable doCalledRunnable = null;
	// 监控呼叫中的DoCalledRunnable
	private ServerSocketThread.DoCalledRunnable doCalledRunnable_momitor = null;
	// 线程池，处理所有被呼叫线程
	private ExecutorService executoService = null;

	// 被叫长时间没应答的复位处理
	private Timer timer_called = null;

	// webrtc
	public static VideoChatSdk mVideoChatSdk = null;

	// 本地ID
	private int LOCAL_ID = NetworkProtocol.SRC_ID;

	// 回调 监听当前状态
	private NetworkStateListener mNetworkStateListener = null;

	// 标记当前状态，改变时回调给上层
	public static int networdState = NetworkState.STATE_INIT;

	// 主动呼叫室内机的类列表
	private List<NetworkIndoor> networkIndoors = null;

	// 主动呼叫主动挂断室内机的标志
	private boolean active_hangup_indoor = false;

	private CameraManager cameraManager;

	/**
	 * 
	 * @param context
	 * @param listener
	 *            NetworkStateListener
	 * @param local
	 *            本地视频的RelativeLayout，可为null
	 * @param remote
	 *            远程视频的RelativeLayout，可为null
	 */
	public NetworkManager(Context context, NetworkStateListener listener,
			RelativeLayout local, RelativeLayout remote) {
		mContext = (MyApplication) context;
		mNetworkStateListener = listener;
		mHandler = new NetworkHandler();
		//lph20190406
//		mVideoChatSdk = new VideoChatSdk(mContext);
//		mVideoChatSdk.init();
//		mVideoChatSdk.setServerIp(mContext.equInfoModel.accSysIp, 8906);
//		mVideoChatSdk.setVideoTxRx(true, true);
//		mVideoChatSdk.setLayoutView(local, remote);
	}

	public void enableRecord(final String ip) {
		mVideoChatSdk.enableRecord();
		mVideoChatSdk.setListener(new VideoChatListener() {

			@Override
			public void onRecordFinish(String path, int Elapse) {
				Log.d(TAG, "onRecordFinish path:" + path + " Elapse:" + Elapse);
				// sendVideo(ip, path);
				Bundle bundle = new Bundle();
				bundle.putString("ip", ip);
				bundle.putString("path", path);
				Message msg = new Message();
				msg.what = 100;
				msg.setData(bundle);
				mHandler.sendMessageDelayed(msg, 1000);
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onCallOut(boolean isSuccess) {
			}

			@Override
			public void onCallIn(boolean isSuccess) {
			}

			@Override
			public void OnStatusChanaged(int inFps, int outFps, int inKbps,
					int outKbps, int width, int height, int packetLoss) {
			}
		});
	}

	public void disableRecord() {
		mVideoChatSdk.disableRecord();
		mVideoChatSdk.setListener(null);
	}

	public void closeCamera() {
		if (cameraManager != null) {
			cameraManager.close();
			cameraManager = null;
		}
	}

	public void startPreview(RelativeLayout local) {
		cameraManager = new CameraManager(mContext, new CameraFrameCallback() {
			@Override
			public void onCameraFrame(byte[] data, int offset, int length) {
			}
		});
		cameraManager.startPreview(mContext, local, false, null);
	}

	/* 网络管理Handler消息类型 */
	public class NetworkMsgType {
		public static final int MSG_NETWORK_DISABLED = 1;
		public static final int MSG_NETWORK_ENABLED = 2;
		public static final int MSG_SERVER_ENABLED = 3;
		public static final int MSG_CLIENT_DISCONNECTED = 4;
		public static final int MSG_CLIENT_WEBRTC_START = 5;
		public static final int MSG_SERVER_WEBRTC_STOP = 6;
		public static final int MSG_CLIENT_MONITOR = 7;
		public static final int MSG_SERVER_MONITOR = 8;
		public static final int MSG_INDOOR_WEBRTC_START = 9;
		public static final int MSG_INDOOR_CHANGE = 10;
	}

	String ip_startcall;
	int dstId_startcall;
	byte type_startcall;
	int indoorId_startcall;
	String[] ip_startcalls;
	int[] dstId_startcalls;

	private class NetworkHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NetworkMsgType.MSG_NETWORK_DISABLED: {
				// 网络断开
				LogMsg("------MSG_NETWORK_DISABLED------");
				stopClientThread();
				stopClientThread_urgent();
				stopServerThread();
				if (networdState == NetworkState.STATE_CALL_IN
						|| networdState == NetworkState.STATE_CALL_IN_MONITOR) {
					LogMsg("webrtc stop");
					mVideoChatSdk.stop();
					setCamLight(false);
				}
				sendStateToUI(NetworkState.STATE_NETWORK_DISABLED);
				break;
			}
			case NetworkMsgType.MSG_NETWORK_ENABLED: {
				// 网络连接正常
				LogMsg("------MSG_NETWORK_ENABLED------");
				startServerThread();
				break;
			}
			case NetworkMsgType.MSG_SERVER_ENABLED: {
				// Socket服务开启
				LogMsg("------MSG_SERVER_ENABLED------");
				sendStateToUI(NetworkState.STATE_NETWORK_ENABLED);
				break;
			}
			case NetworkMsgType.MSG_CLIENT_DISCONNECTED: {
				// 主动呼叫断开
				LogMsg("------MSG_CLIENT_DISCONNECTED------");
				if (networdState == NetworkState.STATE_CALL_IN
						|| networdState == NetworkState.STATE_CALL_IN_MONITOR) {
					LogMsg("webrtc stop");
					mVideoChatSdk.stop();
					setCamLight(false);
				}
				if (clientSocketThread != null
						&& clientSocketThread.isMomitor != 0) {
					if (doCalledRunnable != null) {
						mNetworkStateListener.OnCalled(doCalledRunnable.ip,
								doCalledRunnable.dstId, doCalledRunnable.type,
								doCalledRunnable.indoorId);
						sendStateToUI(clientSocketThread.isMomitor);
						stopClientThread();
					} else {
						stopClientThread();
						byte[] data = new byte[5];
						data[0] = type_startcall;
						data[1] = (byte) (indoorId_startcall & 0xff);
						data[2] = (byte) ((indoorId_startcall >> 8) & 0xff);
						data[3] = (byte) ((indoorId_startcall >> 16) & 0xff);
						data[4] = (byte) ((indoorId_startcall >> 24) & 0xff);
						clientSocketThread = new ClientSocketThread(
								ip_startcall, dstId_startcall, data, null);
						clientSocketThread.start();
					}
					break;
				}
				stopClientThread();
				sendStateToUI(msg.arg1);
				break;
			}
			case NetworkMsgType.MSG_CLIENT_WEBRTC_START: {
				// 开始视频通话
				LogMsg("------MSG_CLIENT_WEBRTC_START------" + msg.arg2);
				mVideoChatSdk.setDstIp(msg.obj.toString());
				mVideoChatSdk.enableAudio();
				closeCamera();
				// 0代表主动呼叫一方，1代表被动呼叫一方
				if (msg.arg2 == 0) {
					mVideoChatSdk.start(mContext.equInfoModel.ip,
							msg.obj.toString(), false);
				} else if (msg.arg2 == 1) {
					mVideoChatSdk.start(mContext.equInfoModel.ip,
							msg.obj.toString(), true);
				}
				setCamLight(true);
				LogMsg("webrtc start " + msg.obj.toString());
				sendStateToUI(msg.arg1);
				break;
			}
			case NetworkMsgType.MSG_SERVER_WEBRTC_STOP: {
				LogMsg("------MSG_SERVER_WEBRTC_STOP------");
				mVideoChatSdk.stop();
				setCamLight(false);
				break;
			}
			case NetworkMsgType.MSG_CLIENT_MONITOR: {
				LogMsg("------MSG_CLIENT_MONITOR------");
				mVideoChatSdk.setDstIp(msg.obj.toString());
				mVideoChatSdk.disableAudio();
				mVideoChatSdk.start();
				setCamLight(true);
				sendStateToUI(NetworkState.STATE_CALL_IN_MONITOR);
				break;
			}
			case NetworkMsgType.MSG_SERVER_MONITOR: {
				LogMsg("------MSG_SERVER_MONITOR------");
				mVideoChatSdk.setDstIp(msg.obj.toString());
				mVideoChatSdk.disableAudio();
				mVideoChatSdk.start();
				setCamLight(true);
				sendStateToUI(NetworkState.STATE_CALL_IN_MONITOR);
				break;
			}
			case NetworkMsgType.MSG_INDOOR_WEBRTC_START: {
				LogMsg("------MSG_INDOOR_WEBRTC_START------");
				for (int i = 0; i < networkIndoors.size(); i++) {
					NetworkIndoor indoor = networkIndoors.get(i);
					if (i == msg.arg2) {
						clientSocketThread = indoor.clientSocketThread;
						indoor.networdState = msg.arg1;
						continue;
					} else if (indoor.isAline) {
						indoor.clientSocketThread.endRequest();
					}
				}
				mVideoChatSdk.setDstIp(msg.obj.toString());
				mVideoChatSdk.enableAudio();
				mVideoChatSdk.start();
				setCamLight(true);
				LogMsg("webrtc start " + msg.obj.toString());
				sendStateToUI(msg.arg1);
				break;
			}
			case NetworkMsgType.MSG_INDOOR_CHANGE: {
				LogMsg("------MSG_INDOOR_CHANGE------networkState:" + msg.arg1
						+ " list_count:" + msg.arg2);
				if (msg.arg1 == NetworkState.STATE_CALL_WAIT) {
					NetworkIndoor indoor = networkIndoors.get(msg.arg2);
					indoor.networdState = msg.arg1;
					indoor.isAline = true;
					sendStateToUI(msg.arg1);
				} else if (msg.arg1 == NetworkState.STATE_CALL_END
						|| msg.arg1 == NetworkState.STATE_CALL_ERROR
						|| msg.arg1 == NetworkState.STATE_TIMEOUT
						|| msg.arg1 == NetworkState.STATE_CALL_BUSY
						|| msg.arg1 == NetworkState.STATE_CALL_REFUSE
						|| msg.arg1 == NetworkState.STATE_CALL_CANNOT) {
					boolean isClientSocketThread = false;
					if (networdState == NetworkState.STATE_CALL_IN
							&& clientSocketThread == networkIndoors
									.get(msg.arg2).clientSocketThread) {
						LogMsg("webrtc stop");
						mVideoChatSdk.stop();
						setCamLight(false);
					}

					NetworkIndoor indoor = networkIndoors.get(msg.arg2);
					stopClientThread_indoor(indoor.clientSocketThread);
					if (clientSocketThread == indoor.clientSocketThread) {
						isClientSocketThread = true;
						clientSocketThread = null;
					}
					indoor.networdState = msg.arg1;
					indoor.isAline = false;

					if (!listIsAline()) {
						if (isClientSocketThread || listIsSame(msg.arg2)
								|| active_hangup_indoor) {
							sendStateToUI(msg.arg1);
						} else {
							sendStateToUI(NetworkState.STATE_CALL_CANNOT);
						}
						networkIndoors.clear();
						networkIndoors = null;
					}
				}
				break;
			}
			case 100: {
				Bundle bundle = msg.getData();
				sendVideo(bundle.getString("ip"), bundle.getString("path"));
				break;
			}
			}
		}
	}

	/**
	 * 开启通讯服务
	 * 
	 * @param localId
	 *            本地id
	 */
	public void startServer(int localId) {
		LOCAL_ID = localId;
		// LogMsg("startServer");
		// checkNetworkHw();

		// *创建网卡状态变化广播接收
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(connectionReceiver, intentFilter);
	}

	/**
	 * 关闭通讯服务，释放资源
	 */
	public void stopServer() {
		stopServerThread();
		stopClientThread();
		stopClientThread_urgent();
		mVideoChatSdk.uninit();
		mContext.unregisterReceiver(connectionReceiver);
		sendStateToUI(NetworkState.STATE_INIT);
	}

	/**
	 * 主动呼叫或者接听
	 * 
	 * @param ip
	 *            主动呼叫的IP地址；接听时可为空
	 * @param dstId
	 *            主动呼叫的对方id；接听时可为0
	 * @param type
	 *            主动呼叫的类型，正常呼叫or紧急呼叫；接听时可为0
	 * @param indoorId
	 *            若是中心机房呼叫室内机，此id为室内机id，其他情况传入id为0
	 */
	public void startCall(String ip, int dstId, byte type, int indoorId) {
		if (type == NetworkProtocol.DATA_CALL_URGENT
				&& clientSocketThread_urgent == null) {
			byte[] data = new byte[5];
			data[0] = type;
			data[1] = (byte) (indoorId & 0xff);
			data[2] = (byte) ((indoorId >> 8) & 0xff);
			data[3] = (byte) ((indoorId >> 16) & 0xff);
			data[4] = (byte) ((indoorId >> 24) & 0xff);
			clientSocketThread_urgent = new ClientSocketThread(ip, dstId, data,
					null);
			clientSocketThread_urgent.start();
			return;
		}
		if (networdState == NetworkState.STATE_NETWORK_ENABLED
				&& clientSocketThread == null && doCalledRunnable == null) {
			byte[] data = new byte[5];
			data[0] = type;
			data[1] = (byte) (indoorId & 0xff);
			data[2] = (byte) ((indoorId >> 8) & 0xff);
			data[3] = (byte) ((indoorId >> 16) & 0xff);
			data[4] = (byte) ((indoorId >> 24) & 0xff);
			clientSocketThread = new ClientSocketThread(ip, dstId, data, null);
			clientSocketThread.start();
		} else if (networdState == NetworkState.STATE_CALL_ED
				&& clientSocketThread == null && doCalledRunnable != null) {
			doCalledRunnable.sendMsgToClient(NetworkProtocol.DATA_CALL_SUCCESS);
		} else if (networdState == NetworkState.STATE_CALL_IN_MONITOR) {
			this.ip_startcall = ip;
			this.dstId_startcall = dstId;
			this.type_startcall = type;
			this.indoorId_startcall = indoorId;
			if (clientSocketThread != null && doCalledRunnable_momitor == null) {
				clientSocketThread.endRequest();
				clientSocketThread.isMomitor = NetworkState.STATE_NETWORK_ENABLED;
			} else if (clientSocketThread == null
					&& doCalledRunnable_momitor != null) {
				doCalledRunnable_momitor
						.sendMsgToClient(NetworkProtocol.CMD_CALL_END);
				doCalledRunnable_momitor.isMomitor = NetworkState.STATE_NETWORK_ENABLED;
			}
		}
	}

	/**
	 * 主动呼叫室内机（全数字版本呼叫室内机时需要同时建立多条链路同时呼叫与室内机绑定的手机）
	 * 
	 * @param ips
	 *            对方的ip集合
	 * @param ids
	 *            对方的id集合 。ip和id顺序上需要一一对应
	 */
	public void startCall_Indoor(String[] ips, int[] ids) {
		if (ips == null || ids == null) {
			return;
		}
		if (ips.length == 0 || ids.length == 0) {
			return;
		}
		if (ips.length != ids.length) {
			return;
		}
		networkIndoors = new ArrayList<NetworkIndoor>();
		active_hangup_indoor = false;
		if (networdState == NetworkState.STATE_NETWORK_ENABLED
				&& clientSocketThread == null && doCalledRunnable == null) {
			byte[] data = { 0x00, 0x00, 0x00, 0x00, 0x00 };
			for (int i = 0; i < ips.length; i++) {
				ClientSocketThread thread = new ClientSocketThread(ips[i],
						ids[i], data, null);
				thread.list_count = i;
				thread.start();
				NetworkIndoor indoor = new NetworkIndoor();
				indoor.clientSocketThread = thread;
				indoor.isAline = true;
				networkIndoors.add(i, indoor);
			}
		} else if (networdState == NetworkState.STATE_CALL_IN_MONITOR) {
			if (clientSocketThread == null && doCalledRunnable_momitor != null) {
				this.ip_startcalls = ips;
				this.dstId_startcalls = ids;
				doCalledRunnable_momitor
						.sendMsgToClient(NetworkProtocol.CMD_CALL_END);
				doCalledRunnable_momitor.isMomitor = NetworkState.STATE_NETWORK_ENABLED;
			}
		}
	}

	/**
	 * 挂断
	 */
	public void stopCall() {
		if (networdState == NetworkState.STATE_CALL_IN
				|| networdState == NetworkState.STATE_CALL_IN_MONITOR) {
			// 通话中挂断
			if (networkIndoors != null && clientSocketThread != null
					&& doCalledRunnable == null
					&& doCalledRunnable_momitor == null) {
				active_hangup_indoor = true;
				for (NetworkIndoor indoor : networkIndoors) {
					if (indoor.isAline) {
						indoor.clientSocketThread.endRequest();
						break;
					}
				}
			} else {
				if (clientSocketThread != null && doCalledRunnable == null
						&& doCalledRunnable_momitor == null) {
					// 主动呼叫时主动挂断
					clientSocketThread.endRequest();
				} else if (clientSocketThread == null
						&& doCalledRunnable != null) {
					// 被动呼叫时主动挂断
					doCalledRunnable
							.sendMsgToClient(NetworkProtocol.CMD_CALL_END);
				} else if (clientSocketThread == null
						&& doCalledRunnable_momitor != null) {
					// 监控呼叫时主动挂断
					doCalledRunnable_momitor
							.sendMsgToClient(NetworkProtocol.CMD_CALL_END);
				}
			}
		} else if (networdState == NetworkState.STATE_CALL_WAIT) {
			// 等待对方接通中挂断
			if (networkIndoors != null && clientSocketThread == null
					&& doCalledRunnable == null) {
				active_hangup_indoor = true;
				for (NetworkIndoor indoor : networkIndoors) {
					if (indoor.isAline) {
						indoor.clientSocketThread.endRequest();
					}
				}
			} else {
				if (clientSocketThread != null && doCalledRunnable == null) {
					clientSocketThread.endRequest();
				}
			}
		} else if (networdState == NetworkState.STATE_CALL_ED) {
			// 被叫时挂断
			if (clientSocketThread == null && doCalledRunnable != null) {
				doCalledRunnable
						.sendMsgToClient(NetworkProtocol.DATA_CALL_REFUSE);
			}
		}
	}

	/**
	 * 设置展示布局的ViewGroup
	 * 
	 * @param local
	 *            本地视频的RelativeLayout，可为null
	 * @param remote
	 *            远程视频的RelativeLayout，可为null
	 */
	public void setLayoutView(ViewGroup local, ViewGroup remote) {
		if (mVideoChatSdk != null) {
			mVideoChatSdk.setLayoutView(local, remote);
		}
	}

	/**
	 * 设置可视对讲的收发状态
	 * 
	 * @param tx
	 *            true为开启视频发送，false为关闭
	 * @param rx
	 *            true为开启视频接收，false为关闭
	 */
	public void setVideoTxRx(boolean tx, boolean rx) {
		if (mVideoChatSdk != null) {
			mVideoChatSdk.setVideoTxRx(tx, rx);
		}
	}

	// --设置交叉主动呼叫为false被呼设为true
	public void setMix(boolean mix) {
		if (mVideoChatSdk != null) {
			// mVideoChatSdk.setMix(mix);
		}
	}

	/**
	 * 发送开锁请求
	 */
	public void unLock() {
		if (clientSocketThread != null && doCalledRunnable == null) {
			clientSocketThread.unLock();
		} else if (clientSocketThread == null && doCalledRunnable != null) {
			doCalledRunnable.sendMsgToClient(NetworkProtocol.CMD_UNLOCK);
		}
	}

	public void sendCard(String card) {
		if (clientSocketThread != null && doCalledRunnable == null
				&& !TextUtils.isEmpty(card)) {
			clientSocketThread.sendCard(card);
		}
	}

	/**
	 * 发送留言
	 * 
	 * @param ip
	 *            对方ip
	 * @param dstId
	 *            对方id
	 * @param path
	 *            音频文件的绝对路径
	 * @return
	 */
	// public boolean sendWords(String ip, int dstId, String path) {
	// if (clientSocketThread == null
	// && networdState != NetworkState.STATE_INIT
	// && networdState != NetworkState.STATE_NETWORK_DISABLED) {
	// File file = new File(path);
	// if (file.exists() && !file.isDirectory()
	// && file.getName().endsWith(".amr")) {
	// clientSocketThread = new ClientSocketThread(ip, dstId, null,
	// path);
	// clientSocketThread.start();
	// }
	// }
	// return false;
	// }

	public void sendVideo(String ip, String path) {
		if (clientSocketThread == null
				&& networdState != NetworkState.STATE_INIT
				&& networdState != NetworkState.STATE_NETWORK_DISABLED) {
			File file = new File(path);
			if (file.exists() && !file.isDirectory()
					&& file.getName().endsWith(".mp4")) {
				Log.e(TAG, "=============sendVideo============= ip:" + ip
						+ " path:" + path);
				clientSocketThread = new ClientSocketThread(ip, 0, null, path);
				clientSocketThread.start();
			}
		}
	}

	private class ServerSocketThread extends Thread {

		public boolean isRunning = true;

		public ServerSocketThread() {
		}

		public void run() {
			super.run();
			if (mServerSocket == null) {
				try {
					mServerSocket = new ServerSocket(PORT);
					mHandler.sendEmptyMessage(NetworkMsgType.MSG_SERVER_ENABLED);

					isRunning = true;
					executoService = Executors.newFixedThreadPool(10);
					while (!interrupted() && isRunning) {
						Socket socket = mServerSocket.accept();
						LogMsg("socket连接服务器成功");
						DoCalledRunnable runnable = new DoCalledRunnable(socket);
						executoService.execute(runnable);
					}
				} catch (IOException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(NetworkMsgType.MSG_NETWORK_DISABLED);
				}
			}
		}

		public class DoCalledRunnable implements Runnable {

			public boolean isRunning = true;
			private int timeout_count = 0;

			private Socket socket = null;

			private OutputStream outputStream = null;
			private InputStream inputStream = null;
			private NetworkProtocol prot = new NetworkProtocol();

			private String ip;
			private int dstId;
			private byte type = 0X10;
			private int indoorId;

			private byte[] data_file;
			private int length_file = 0;
			private int length_data = 0;

			// 标记：若是有监控通话存在的情况下需要进行其他操作，则走正常流程先结束当前监控通话，待到当前通话正常结束再进行其他操作
			public int isMomitor = 0;

			public DoCalledRunnable(Socket socket) {
				this.socket = socket;
			}

			@Override
			public void run() {
				if (socket != null) {
					try {
						// ip = socket.getInetAddress().getHostAddress();
						String s = socket.getRemoteSocketAddress().toString()
								.split(":")[0];
						ip = s.substring(1, s.length());
						inputStream = socket.getInputStream();
						outputStream = socket.getOutputStream();
						socket.setSoTimeout(HEARTBEAT);
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					while (isRunning) {
						// LogMsg("------------Server onRunning------------");
						if (networdState == NetworkState.STATE_CALL_IN
								|| networdState == NetworkState.STATE_CALL_IN_MONITOR) {
							timeout_count++;
							if (timeout_count > HEARTBEAT_TIMEOUT_COUNT) {
								stopThisRun(NetworkState.STATE_TIMEOUT);
								break;
							}
						}
						try {
							byte[] recvbuf = new byte[recvbuf_lenght];
							int recvlen = inputStream.read(recvbuf);
							if (recvlen > 0) {
								// LogMsg("receive data:");
								// LogHex(recvbuf, recvlen);
								prot = dataToProtocol(recvbuf);
								dstId = prot.packet.app.srcId;
								serverDoRecvProtocol();
							}
						} catch (IOException e) {
						}
					}
				}
			}

			// 解析请求，逻辑处理
			private void serverDoRecvProtocol() {
				if (prot == null) {
					stopThisRun(NetworkState.STATE_CALL_ERROR);
					return;
				}
				// LogMsg("recv data:" + prot.packet.toString());
				if (prot.packet.app.type == NetworkProtocol.TYPE_REQUEST
				/* && prot.packet.app.dstId == LOCAL_ID */) {
					if (prot.packet.app.cmd == NetworkProtocol.CMD_CALL_REQUEST) {
						type = prot.packet.app.data[0];
						indoorId = prot.packet.app.data[1] & 0xff;
						indoorId += ((prot.packet.app.data[2] & 0xff) << 8);
						indoorId += ((prot.packet.app.data[3] & 0xff) << 16);
						indoorId += ((prot.packet.app.data[4] & 0xff) << 24);
						if (type == NetworkProtocol.DATA_CALL_NORMAL) {
							if (networdState == NetworkState.STATE_NETWORK_ENABLED) {
								LogMsg("收到" + ip + "呼叫");
								doCalledRunnable = this;
								sendStateToUI(NetworkState.STATE_CALL_ED);
								mNetworkStateListener.OnCalled(ip, dstId, type,
										indoorId);
							} else if (networdState == NetworkState.STATE_CALL_IN_MONITOR) {
								if (clientSocketThread != null
										&& doCalledRunnable == null
										&& doCalledRunnable_momitor == null) {
									doCalledRunnable = this;
									clientSocketThread.endRequest();
									clientSocketThread.isMomitor = NetworkState.STATE_CALL_ED;
								} else if (clientSocketThread == null
										&& doCalledRunnable == null
										&& doCalledRunnable_momitor != null) {
									doCalledRunnable_momitor.isMomitor = NetworkState.STATE_CALL_ED;
									doCalledRunnable_momitor
											.sendMsgToClient(NetworkProtocol.CMD_CALL_END);
									doCalledRunnable = this;
									sendStateToUI(NetworkState.STATE_CALL_ED);
									mNetworkStateListener.OnCalled(ip, dstId,
											type, indoorId);
								}
							} else {
								// 发送线路繁忙应答
								LogMsg("收到" + ip + "呼叫");
								LogMsg("发送线路繁忙应答");
								prot.packet.app.srcId = LOCAL_ID;
								prot.packet.app.dstId = dstId;
								prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
								byte[] d = new byte[1];
								d[0] = NetworkProtocol.DATA_CALL_BUSY;
								prot.packet.app.length = d.length;
								prot.packet.app.data = d;
								byte[] mSendBuf = null;
								if (isBase64Packet) {
									mSendBuf = prot.packet
											.toBase64Bytes(PacketKey);
								} else {
									mSendBuf = prot.packet.toBytes(PacketKey);
								}
								LogMsg("endRespond send data:"
										+ prot.packet.toString());
								if (sendData(mSendBuf, mSendBuf.length)) {
									stopThisRun(NetworkState.STATE_CALL_BUSY);
								}
							}
						} else if (type == NetworkProtocol.DATA_CALL_URGENT) {
							LogMsg("收到" + ip + "紧急呼叫");
							mNetworkStateListener.OnCalled(ip, dstId, type,
									indoorId);
							isRunning = false;
							try {
								socket.close();
								socket = null;
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else if (type == NetworkProtocol.DATA_CALL_MONITOR) {
							if (networdState == NetworkState.STATE_NETWORK_ENABLED) {
								LogMsg("收到" + ip + "监控呼叫");
								doCalledRunnable_momitor = this;
								LogMsg("发送呼叫成功应答");
								prot.packet.app.srcId = LOCAL_ID;
								prot.packet.app.dstId = dstId;
								prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
								byte[] d = new byte[1];
								d[0] = NetworkProtocol.DATA_CALL_SUCCESS;
								prot.packet.app.length = d.length;
								prot.packet.app.data = d;
								byte[] mSendBuf = null;
								if (isBase64Packet) {
									mSendBuf = prot.packet
											.toBase64Bytes(PacketKey);
								} else {
									mSendBuf = prot.packet.toBytes(PacketKey);
								}
								LogMsg("sendMsgToClient send data:"
										+ prot.packet.toString());
								if (sendData(mSendBuf, mSendBuf.length)) {
									Message msg = Message.obtain();
									msg.what = NetworkMsgType.MSG_SERVER_MONITOR;
									msg.obj = ip;
									mHandler.sendMessage(msg);
								}
							} else {
								// 发送线路繁忙应答
								LogMsg("收到" + ip + "监控呼叫");
								LogMsg("发送线路繁忙应答");
								prot.packet.app.srcId = LOCAL_ID;
								prot.packet.app.dstId = dstId;
								prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
								byte[] d = new byte[1];
								d[0] = NetworkProtocol.DATA_CALL_BUSY;
								prot.packet.app.length = d.length;
								prot.packet.app.data = d;
								byte[] mSendBuf = null;
								if (isBase64Packet) {
									mSendBuf = prot.packet
											.toBase64Bytes(PacketKey);
								} else {
									mSendBuf = prot.packet.toBytes(PacketKey);
								}
								LogMsg("endRespond send data:"
										+ prot.packet.toString());
								if (sendData(mSendBuf, mSendBuf.length)) {
									stopThisRun(NetworkState.STATE_CALL_BUSY);
								}
							}
						}
					} else if (prot.packet.app.cmd == NetworkProtocol.CMD_HEART_BEAT) {
						timeout_count = 0;
						LogMsg("收到心跳请求");
						LogMsg("发送心跳应答");
						// 发送心跳应答
						prot.packet.app.srcId = LOCAL_ID;
						prot.packet.app.dstId = dstId;
						prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
						byte[] mSendBuf = null;
						if (isBase64Packet) {
							mSendBuf = prot.packet.toBase64Bytes(PacketKey);
						} else {
							mSendBuf = prot.packet.toBytes(PacketKey);
						}
						LogMsg("onAlive send data:" + prot.packet.toString());
						sendData(mSendBuf, mSendBuf.length);
					} else if (prot.packet.app.cmd == NetworkProtocol.CMD_CALL_END) {
						LogMsg("收到结束通话请求");
						LogMsg("发送结束通话应答");
						// 发送结束通话应答
						prot.packet.app.srcId = LOCAL_ID;
						prot.packet.app.dstId = dstId;
						prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
						byte[] d = new byte[1];
						d[0] = NetworkProtocol.DATA_END_SUCCESS;
						prot.packet.app.length = d.length;
						prot.packet.app.data = d;
						byte[] mSendBuf = null;
						if (isBase64Packet) {
							mSendBuf = prot.packet.toBase64Bytes(PacketKey);
						} else {
							mSendBuf = prot.packet.toBytes(PacketKey);
						}
						LogMsg("endRespond send data:" + prot.packet.toString());
						sendData(mSendBuf, mSendBuf.length);
						stopThisRun(NetworkState.STATE_CALL_END);
					} else if (prot.packet.app.cmd == NetworkProtocol.CMD_UNLOCK) {
						LogMsg("接收到开锁请求");
						mNetworkStateListener.OnUnlock();
					} else if (prot.packet.app.cmd == NetworkProtocol.CMD_WORDS) {
						LogMsg("接收到留言请求 " + prot.packet.app.sn);
						timeout_count = 0;
						// LogHex(prot.packet.app.data,
						// prot.packet.app.data.length);
						if (prot.packet.app.sn == 0) {
							mNetworkStateListener.SendRecvResult(
									NetworkProtocol.CMD_WORDS,
									NetworkState.STATE_SEND_ING);
							length_file = (prot.packet.app.data[0] & 0xff)
									| ((prot.packet.app.data[1] & 0xff) << 8)
									| ((prot.packet.app.data[2] & 0xff) << 16)
									| ((prot.packet.app.data[3] & 0xff) << 24)
									| ((prot.packet.app.data[4] & 0xff) << 32);
							data_file = new byte[length_file];
							LogMsg("---------length_file:" + length_file);
						} else {
							System.arraycopy(prot.packet.app.data, 0,
									data_file, length_data,
									prot.packet.app.data.length);
							length_data += prot.packet.app.data.length;
							LogMsg("---------length_data:" + length_data);
						}
						prot.packet.app.srcId = LOCAL_ID;
						prot.packet.app.dstId = dstId;
						prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
						byte[] d = new byte[1];
						d[0] = NetworkProtocol.DATA_WORDS_SUCCESS;
						prot.packet.app.length = d.length;
						prot.packet.app.data = d;
						byte[] mSendBuf = null;
						if (isBase64Packet) {
							mSendBuf = prot.packet.toBase64Bytes(PacketKey);
						} else {
							mSendBuf = prot.packet.toBytes(PacketKey);
						}
						// LogMsg("endRespond send data:" +
						// prot.packet.toString());
						sendData(mSendBuf, mSendBuf.length);
						if (length_data >= length_file) {
							// LogHex(data_file, data_file.length);
							String fileName = ip + "_" + getTime() + ".mp4";
							byte2File(data_file,
									"/mnt/sdcard/DoorplateCenter/CallLog/",
									fileName);
							mNetworkStateListener.OnWords(ip, dstId,
									"/mnt/sdcard/DoorplateCenter/CallLog/"
											+ fileName);
							mNetworkStateListener.SendRecvResult(
									NetworkProtocol.CMD_WORDS,
									NetworkState.STATE_SEND_SUCCESS);
							isRunning = false;
							try {
								socket.close();
								socket = null;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else if (prot.packet.app.cmd == NetworkProtocol.CMD_CARD) {
						LogMsg("接收到卡号请求 ");
						String card = new String(prot.packet.app.data);
						mNetworkStateListener.OnCard(card);
					}
				} else if (prot.packet.app.type == NetworkProtocol.TYPE_REPLY
						/*
						 * && prot.packet.app.dstId == LOCAL_ID &&
						 * prot.packet.app.srcId == prot.packet.app.dstId
						 */
						&& prot.packet.app.cmd == NetworkProtocol.CMD_CALL_END
						&& (networdState == NetworkState.STATE_CALL_IN || networdState == NetworkState.STATE_CALL_IN_MONITOR)) {
					// 收到结束通话应答
					LogMsg("收到结束通话应答");
					stopThisRun(NetworkState.STATE_CALL_END);
				} else {
					stopThisRun(NetworkState.STATE_CALL_ERROR);
				}
			}

			// 主动向客户端发起请求或者应答
			public void sendMsgToClient(int type) {
				if (type == NetworkProtocol.DATA_CALL_SUCCESS) {
					LogMsg("发送呼叫成功应答");
					// 主动发起呼叫成功应答
					prot.packet.init();
					prot.packet.app.srcId = LOCAL_ID;
					prot.packet.app.dstId = dstId;
					prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
					prot.packet.app.cmd = NetworkProtocol.CMD_CALL_REQUEST;
					byte[] d = new byte[1];
					d[0] = NetworkProtocol.DATA_CALL_SUCCESS;
					prot.packet.app.length = d.length;
					prot.packet.app.data = d;
					byte[] mSendBuf = null;
					if (isBase64Packet) {
						mSendBuf = prot.packet.toBase64Bytes(PacketKey);
					} else {
						mSendBuf = prot.packet.toBytes(PacketKey);
					}
					LogMsg("sendMsgToClient send data:"
							+ prot.packet.toString());
					if (sendData(mSendBuf, mSendBuf.length)) {
						Message msg = Message.obtain();
						msg.what = NetworkMsgType.MSG_CLIENT_WEBRTC_START;
						msg.arg2 = 0;
						msg.obj = ip;
						if (this.type == NetworkProtocol.DATA_CALL_NORMAL) {
							msg.arg1 = NetworkState.STATE_CALL_IN;
						}
						mHandler.sendMessage(msg);
					}
				} else if (type == NetworkProtocol.CMD_CALL_END) {
					LogMsg("发起结束通话请求");
					// 主动发起结束通话请求
					prot.packet.init();
					prot.packet.app.srcId = LOCAL_ID;
					prot.packet.app.dstId = dstId;
					prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
					prot.packet.app.cmd = NetworkProtocol.CMD_CALL_END;
					byte[] mSendBuf = null;
					if (isBase64Packet) {
						mSendBuf = prot.packet.toBase64Bytes(PacketKey);
					} else {
						mSendBuf = prot.packet.toBytes(PacketKey);
					}
					LogMsg("sendMsgToClient send data:"
							+ prot.packet.toString());
					sendData(mSendBuf, mSendBuf.length);
					if (this.isMomitor == NetworkState.STATE_CALL_ED) {
						doCalledRunnable_momitor.stopThisRun(0);
					}
				} else if (type == NetworkProtocol.DATA_CALL_REFUSE) {
					LogMsg("发起拒绝接听应答");
					// 主动发起拒绝接听应答
					prot.packet.init();
					prot.packet.app.srcId = LOCAL_ID;
					prot.packet.app.dstId = dstId;
					prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
					prot.packet.app.cmd = NetworkProtocol.CMD_CALL_REQUEST;
					byte[] d = new byte[1];
					d[0] = NetworkProtocol.DATA_CALL_REFUSE;
					prot.packet.app.length = d.length;
					prot.packet.app.data = d;
					byte[] mSendBuf = null;
					if (isBase64Packet) {
						mSendBuf = prot.packet.toBase64Bytes(PacketKey);
					} else {
						mSendBuf = prot.packet.toBytes(PacketKey);
					}
					LogMsg("sendMsgToClient send data:"
							+ prot.packet.toString());
					if (sendData(mSendBuf, mSendBuf.length)) {
						stopThisRun(NetworkState.STATE_CALL_REFUSE);
					}
				} else if (type == NetworkProtocol.CMD_UNLOCK) {
					LogMsg("发起开锁请求");
					prot.packet.init();
					prot.packet.app.srcId = LOCAL_ID;
					prot.packet.app.dstId = dstId;
					prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
					prot.packet.app.cmd = NetworkProtocol.CMD_UNLOCK;
					byte[] mSendBuf = null;
					if (isBase64Packet) {
						mSendBuf = prot.packet.toBase64Bytes(PacketKey);
					} else {
						mSendBuf = prot.packet.toBytes(PacketKey);
					}
					LogMsg("sendMsgToClient send data:"
							+ prot.packet.toString());
					sendData(mSendBuf, mSendBuf.length);
				}
			}

			private void stopThisRun(int state) {
				LogMsg(this.toString() + " stopThisRun : " + state);
				isRunning = false;
				try {
					socket.close();
					socket = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (state == NetworkState.STATE_CALL_BUSY) {
					return;
				}
				if (networdState == NetworkState.STATE_CALL_IN
						|| networdState == NetworkState.STATE_CALL_IN_MONITOR) {
					mHandler.sendEmptyMessage(NetworkMsgType.MSG_SERVER_WEBRTC_STOP);
				}
				if (clientSocketThread == null
						&& (doCalledRunnable != null || doCalledRunnable_momitor != null)) {
					if (this.isMomitor == 0) {
						sendStateToUI(state);
					} else if (this.isMomitor == NetworkState.STATE_NETWORK_ENABLED) {
						SystemClock.sleep(2000);
						if (networkIndoors == null) {
							byte[] data = new byte[5];
							data[0] = type_startcall;
							data[1] = (byte) (indoorId_startcall & 0xff);
							data[2] = (byte) ((indoorId_startcall >> 8) & 0xff);
							data[3] = (byte) ((indoorId_startcall >> 16) & 0xff);
							data[4] = (byte) ((indoorId_startcall >> 24) & 0xff);
							clientSocketThread = new ClientSocketThread(
									ip_startcall, dstId_startcall, data, null);
							clientSocketThread.start();
						} else {
							byte[] data = { 0x00, 0x00, 0x00, 0x00, 0x00 };
							for (int i = 0; i < ip_startcalls.length; i++) {
								ClientSocketThread thread = new ClientSocketThread(
										ip_startcalls[i], dstId_startcalls[i],
										data, null);
								thread.list_count = i;
								thread.start();
								NetworkIndoor indoor = new NetworkIndoor();
								indoor.clientSocketThread = thread;
								indoor.isAline = true;
								networkIndoors.add(i, indoor);
							}
						}
					}
				}
				if (doCalledRunnable != null && doCalledRunnable == this) {
					doCalledRunnable = null;
				}
				if (doCalledRunnable_momitor != null
						&& doCalledRunnable_momitor == this) {
					doCalledRunnable_momitor = null;
				}
			}

			// 发送数据
			private boolean sendData(byte[] data, int datalen) {
				if (socket != null) {
					try {
						outputStream.write(data, 0, datalen);
						return true;
					} catch (IOException e) {
						LogMsg("SendData IOException");
						e.printStackTrace();
						stopThisRun(NetworkState.STATE_CALL_ERROR);
						return false;
					}
				}
				return false;
			}
		}
	}

	// 标记clientsocket线程的当前操作
	private enum ClientThreadState {
		STATE_CONNECT, STATE_DISCONNECT, STATE_TOSERVER, STATE_RUNNING, STATE_RUNNING_HEART, STATE_RUNNING_WORDS, STATE_ALIVE, STATE_UNKNOWN
	}

	// 主动呼叫线程，所有主动呼叫流程都在改线程执行
	public class ClientSocketThread extends Thread {

		public boolean isRunning = true;

		private Socket mSocketClient = null;
		private String ip;
		private ClientThreadState clientThreadState = ClientThreadState.STATE_UNKNOWN;
		private DataOutputStream outputStream = null;
		private DataInputStream inputStream = null;
		private NetworkProtocol prot = new NetworkProtocol();

		// 呼叫等待次数
		private int runWait = 0;
		// 未收到心跳包次数
		private int runHeart = 0;
		// 结束包未收到次数
		private int runEnd = 0;
		// 未收到留言应答次数
		private int runWords = 0;

		// 主动发送结束通话的标记，主动发送结束通话后停止心跳包发送，等待结束应答
		private boolean isEnd = false;
		// 标记：若是有监控通话存在的情况下需要进行其他操作，则走正常流程先结束当前监控通话，待到当前通话正常结束再进行其他操作
		public int isMomitor = 0;

		private int dstId;
		private int sn = 0;
		private byte[] data;
		private String path;
		private byte[] data_file;

		// 标记：该线程位于List<NetworkIndoor>中的那一个位置，从0开始
		public int list_count = -1;

		public ClientSocketThread(String ip, int dstId, byte[] data, String path) {
			this.ip = ip;
			this.dstId = dstId;
			this.data = data;
			this.path = path;
			if (!TextUtils.isEmpty(path)) {
				File file = new File(path);
				if (file.exists() && !file.isDirectory()) {
					this.data_file = file2byte(path);
				}
			}
		}

		public void run() {
			super.run();
			isRunning = true;
			clientThreadState = ClientThreadState.STATE_CONNECT;
			while (!interrupted() && isRunning) {
				if (clientThreadState == ClientThreadState.STATE_CONNECT) {
					onConnect();
				} else if (clientThreadState == ClientThreadState.STATE_DISCONNECT) {
					onDisconnect();
					break;
				} else if (clientThreadState == ClientThreadState.STATE_TOSERVER) {
					if (!TextUtils.isEmpty(path) && data_file != null) {
						WordsToServer();
					} else {
						RequestToServer();
					}
				} else if (clientThreadState == ClientThreadState.STATE_RUNNING) {
					onRunning(NetworkProtocol.CMD_CALL_REQUEST);
				} else if (clientThreadState == ClientThreadState.STATE_RUNNING_HEART) {
					onRunning(NetworkProtocol.CMD_HEART_BEAT);
				} else if (clientThreadState == ClientThreadState.STATE_RUNNING_WORDS) {
					onRunning(NetworkProtocol.CMD_WORDS);
				} else if (clientThreadState == ClientThreadState.STATE_ALIVE) {
					onAlive();
				}
			}
		}

		// 连接
		private boolean onConnect() {
			LogMsg("------------onConnect------------");
			try {
				if (mSocketClient == null) {
					mSocketClient = new Socket();
					SocketAddress serverAddress = new InetSocketAddress(ip,
							PORT);
					LogMsg("CONNECT:" + serverAddress.toString());
					// --设置连接服务器5秒超时,防止连接服务器卡死
					mSocketClient.connect(serverAddress, TIMEOUT);
					LogMsg("Socket连接成功");
					// --设置输入输出句柄
					outputStream = new DataOutputStream(
							mSocketClient.getOutputStream());
					inputStream = new DataInputStream(
							mSocketClient.getInputStream());
					// --设置心跳时间作为传输接收超时
					mSocketClient.setSoTimeout(HEARTBEAT);

					clientThreadState = ClientThreadState.STATE_TOSERVER;

					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				Log.e(TAG, " onConnect Exception ");
				e.printStackTrace();
				clientThreadState = ClientThreadState.STATE_DISCONNECT;
				if (TextUtils.isEmpty(path)) {
					if (data[0] == NetworkProtocol.DATA_CALL_URGENT) {
						stopClientThread_urgent();
						mNetworkStateListener.SendRecvResult(
								NetworkProtocol.DATA_CALL_URGENT,
								NetworkState.STATE_SEND_ERROR);
					} else {
						if (networkIndoors == null) {
							sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
									NetworkState.STATE_CALL_ERROR);
						} else {
							Message msg = Message.obtain();
							msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
							msg.arg1 = NetworkState.STATE_CALL_ERROR;
							msg.arg2 = list_count;
							mHandler.sendMessage(msg);
						}
					}
				} else {
					mNetworkStateListener.SendRecvResult(
							NetworkProtocol.CMD_WORDS,
							NetworkState.STATE_SEND_ERROR);
					stopClientThread();
				}
			}
			return false;
		}

		// 断开连接
		private boolean onDisconnect() {
			LogMsg("------------onDisconnect------------");
			if (mSocketClient != null && !mSocketClient.isClosed()) {
				try {
					mSocketClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mSocketClient = null;
			clientThreadState = ClientThreadState.STATE_UNKNOWN;
			return true;
		}

		// 客户端向服务器请求通话
		private void RequestToServer() {
			LogMsg("------------RequestToServer------------");
			if (mSocketClient != null) {
				LogMsg("发送通话请求");
				prot.packet.init();
				prot.packet.app.srcId = LOCAL_ID;
				prot.packet.app.dstId = dstId;
				prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
				prot.packet.app.sn = sn;
				prot.packet.app.cmd = NetworkProtocol.CMD_CALL_REQUEST;
				prot.packet.app.length = data.length;
				prot.packet.app.data = data;

				byte[] mSendBuf = null;
				if (isBase64Packet) {
					mSendBuf = prot.packet.toBase64Bytes(PacketKey);
				} else {
					mSendBuf = prot.packet.toBytes(PacketKey);
				}
				LogMsg("RequestToServer send data:" + prot.packet.toString());
				LogHex(mSendBuf, mSendBuf.length);
				if (sendData(mSendBuf, mSendBuf.length)) {
					if (data[0] == NetworkProtocol.DATA_CALL_NORMAL) {
						clientThreadState = ClientThreadState.STATE_RUNNING;
						if (networkIndoors == null) {
							sendStateToUI(NetworkState.STATE_CALL_WAIT);
						} else {
							Message msg = Message.obtain();
							msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
							msg.arg1 = NetworkState.STATE_CALL_WAIT;
							msg.arg2 = list_count;
							mHandler.sendMessage(msg);
						}
					} else if (data[0] == NetworkProtocol.DATA_CALL_URGENT) {
						clientThreadState = ClientThreadState.STATE_DISCONNECT;
						mNetworkStateListener.SendRecvResult(
								NetworkProtocol.DATA_CALL_URGENT,
								NetworkState.STATE_SEND_SUCCESS);
						stopClientThread_urgent();
					} else if (data[0] == NetworkProtocol.DATA_CALL_MONITOR) {
						clientThreadState = ClientThreadState.STATE_RUNNING;
					}
				} else {
					clientThreadState = ClientThreadState.STATE_DISCONNECT;
					if (networkIndoors == null) {
						sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
								NetworkState.STATE_CALL_ERROR);
					} else {
						Message msg = Message.obtain();
						msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
						msg.arg1 = NetworkState.STATE_CALL_ERROR;
						msg.arg2 = list_count;
						mHandler.sendMessage(msg);
					}
				}

			}
		}

		// 发送留言请求
		private void WordsToServer() {
			LogMsg("------------WordsToServer------------");
			if (mSocketClient != null) {
				LogMsg("发送留言请求");
				prot.packet.init();
				prot.packet.app.srcId = LOCAL_ID;
				prot.packet.app.dstId = dstId;
				prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
				prot.packet.app.sn = sn;
				prot.packet.app.cmd = NetworkProtocol.CMD_WORDS;
				int length = data_file.length;
				data = new byte[5];
				data[0] = (byte) (length & 0xff);
				data[1] = (byte) ((length >> 8) & 0xff);
				data[2] = (byte) ((length >> 16) & 0xff);
				data[3] = (byte) ((length >> 24) & 0xff);
				data[4] = (byte) ((length >> 32) & 0xff);
				LogMsg("length = " + length);
				LogHex(data, data.length);
				prot.packet.app.length = data.length;
				prot.packet.app.data = data;

				byte[] mSendBuf = null;
				if (isBase64Packet) {
					mSendBuf = prot.packet.toBase64Bytes(PacketKey);
				} else {
					mSendBuf = prot.packet.toBytes(PacketKey);
				}
				LogMsg("RequestToServer send data:" + prot.packet.toString());
				LogHex(mSendBuf, mSendBuf.length);
				if (sendData(mSendBuf, mSendBuf.length)) {
					clientThreadState = ClientThreadState.STATE_RUNNING_WORDS;
					mNetworkStateListener.SendRecvResult(
							NetworkProtocol.CMD_WORDS,
							NetworkState.STATE_SEND_ING);
				} else {
					clientThreadState = ClientThreadState.STATE_DISCONNECT;
					stopClientThread();
					mNetworkStateListener.SendRecvResult(
							NetworkProtocol.CMD_WORDS,
							NetworkState.STATE_SEND_ERROR);
				}
			}
		}

		// 呼叫等待应答
		private void onRunning(byte cmd) {
			LogMsg("------------onRunning------------" + cmd);
			if (mSocketClient == null) {
				return;
			}
			if (isEnd) {
				runEnd++;
				if (runEnd > 1) {
					clientThreadState = ClientThreadState.STATE_DISCONNECT;
					if (networkIndoors == null) {
						sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
								NetworkState.STATE_CALL_END);
					} else {
						Message msg = Message.obtain();
						msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
						msg.arg1 = NetworkState.STATE_CALL_END;
						msg.arg2 = list_count;
						mHandler.sendMessage(msg);
					}
					return;
				}
			} else if (cmd == NetworkProtocol.CMD_CALL_REQUEST) {
				runWait++;
				if (runWait > CALL_TIMEOUT_COUNT) {
					// 对方长时间未应答，返回无法接听
					clientThreadState = ClientThreadState.STATE_DISCONNECT;
					if (networkIndoors == null) {
						sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
								NetworkState.STATE_CALL_CANNOT);
					} else {
						Message msg = Message.obtain();
						msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
						msg.arg1 = NetworkState.STATE_CALL_CANNOT;
						msg.arg2 = list_count;
						mHandler.sendMessage(msg);
					}
					return;
				}
			} else if (cmd == NetworkProtocol.CMD_HEART_BEAT
					&& (networdState == NetworkState.STATE_CALL_IN || networdState == NetworkState.STATE_CALL_IN_MONITOR)) {
				runHeart++;
				if (runHeart > HEARTBEAT_TIMEOUT_COUNT) {
					// 连续2次没有收到心跳包应答，返回通话结束
					clientThreadState = ClientThreadState.STATE_DISCONNECT;
					if (networkIndoors == null) {
						sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
								NetworkState.STATE_TIMEOUT);
					} else {
						Message msg = Message.obtain();
						msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
						msg.arg1 = NetworkState.STATE_TIMEOUT;
						msg.arg2 = list_count;
						mHandler.sendMessage(msg);
					}
					return;
				}
			} else if (cmd == NetworkProtocol.CMD_WORDS) {
				runWords++;
				if (runWords > 3) {
					clientThreadState = ClientThreadState.STATE_DISCONNECT;
					stopClientThread();
					mNetworkStateListener.SendRecvResult(
							NetworkProtocol.CMD_WORDS,
							NetworkState.STATE_TIMEOUT);
				}
			}
			try {
				byte[] recvbuf = new byte[recvbuf_lenght];
				int recvlen = inputStream.read(recvbuf);
				if (recvlen > 0) {
					LogMsg("recv data:");
					LogHex(recvbuf, recvlen);
					clientDoRecvProtocol(dataToProtocol(recvbuf));
				}
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}

		// 心跳包
		private void onAlive() {
			// LogMsg("------------onAlive------------");
			if (isEnd) {
				clientThreadState = ClientThreadState.STATE_RUNNING_HEART;
				return;
			}
			if ((networdState == NetworkState.STATE_CALL_IN || networdState == NetworkState.STATE_CALL_IN_MONITOR)
					&& mSocketClient != null) {
				LogMsg("发送心跳包");
				prot.packet.init();
				prot.packet.app.srcId = LOCAL_ID;
				prot.packet.app.dstId = dstId;
				prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
				prot.packet.app.sn = ++sn;
				prot.packet.app.cmd = NetworkProtocol.CMD_HEART_BEAT;
				prot.packet.app.length = 0;
				prot.packet.app.data = null;

				byte[] mSendBuf = null;
				if (isBase64Packet) {
					mSendBuf = prot.packet.toBase64Bytes(PacketKey);
				} else {
					mSendBuf = prot.packet.toBytes(PacketKey);
				}
				LogMsg("onAlive send data:" + prot.packet.toString());
				LogHex(mSendBuf, mSendBuf.length);
				sendData(mSendBuf, mSendBuf.length);

				clientThreadState = ClientThreadState.STATE_RUNNING_HEART;
			}
		}

		// 解析服务器应答，逻辑处理
		private void clientDoRecvProtocol(NetworkProtocol protocol) {
			LogMsg("------------clientDoRecvProtocol------------");
			if (protocol == null) {
				clientThreadState = ClientThreadState.STATE_DISCONNECT;
				if (networkIndoors == null) {
					sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
							NetworkState.STATE_CALL_ERROR);
				} else {
					Message msg = Message.obtain();
					msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
					msg.arg1 = NetworkState.STATE_CALL_ERROR;
					msg.arg2 = list_count;
					mHandler.sendMessage(msg);
				}
				return;
			}
			// LogMsg("recv data:" + protocol.packet.toString());
			if (protocol.packet.app.type == NetworkProtocol.TYPE_REPLY
			/*
			 * && protocol.packet.app.dstId == LOCAL_ID &&
			 * protocol.packet.app.srcId == dstId&& protocol.packet.app.cmd ==
			 * prot.packet.app.cmd
			 */
			) {
				if (protocol.packet.app.cmd == NetworkProtocol.CMD_CALL_REQUEST) {
					// 呼叫应答
					if (protocol.packet.app.data[0] == NetworkProtocol.DATA_CALL_SUCCESS) {
						LogMsg("收到呼叫应答");
						if (networkIndoors == null) {
							Message msg = Message.obtain();
							msg.what = NetworkMsgType.MSG_CLIENT_WEBRTC_START;
							msg.arg2 = 1;
							msg.obj = ip;
							if (data[0] == NetworkProtocol.DATA_CALL_NORMAL) {
								msg.arg1 = NetworkState.STATE_CALL_IN;
							} else if (data[0] == NetworkProtocol.DATA_CALL_MONITOR) {
								msg.what = NetworkMsgType.MSG_CLIENT_MONITOR;
							}
							mHandler.sendMessage(msg);
						} else {
							Message msg = Message.obtain();
							msg.what = NetworkMsgType.MSG_INDOOR_WEBRTC_START;
							msg.arg1 = NetworkState.STATE_CALL_IN;
							msg.arg2 = list_count;
							msg.obj = ip;
							mHandler.sendMessage(msg);
						}
						clientThreadState = ClientThreadState.STATE_ALIVE;
					} else if (protocol.packet.app.data[0] == NetworkProtocol.DATA_CALL_BUSY) {
						LogMsg("收到繁忙应答");
						clientThreadState = ClientThreadState.STATE_DISCONNECT;
						if (networkIndoors == null) {
							sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
									NetworkState.STATE_CALL_BUSY);
						} else {
							Message msg = Message.obtain();
							msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
							msg.arg1 = NetworkState.STATE_CALL_BUSY;
							msg.arg2 = list_count;
							mHandler.sendMessage(msg);
						}
					} else if (protocol.packet.app.data[0] == NetworkProtocol.DATA_CALL_REFUSE) {
						LogMsg("收到拒绝接听应答");
						clientThreadState = ClientThreadState.STATE_DISCONNECT;
						if (networkIndoors == null) {
							sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
									NetworkState.STATE_CALL_REFUSE);
						} else {
							Message msg = Message.obtain();
							msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
							msg.arg1 = NetworkState.STATE_CALL_REFUSE;
							msg.arg2 = list_count;
							mHandler.sendMessage(msg);
						}
					} else if (protocol.packet.app.data[0] == NetworkProtocol.DATA_CALL_CANNOT) {
						LogMsg("收到无法接通应答");
						clientThreadState = ClientThreadState.STATE_DISCONNECT;
						if (networkIndoors == null) {
							sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
									NetworkState.STATE_CALL_CANNOT);
						} else {
							Message msg = Message.obtain();
							msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
							msg.arg1 = NetworkState.STATE_CALL_CANNOT;
							msg.arg2 = list_count;
							mHandler.sendMessage(msg);
						}
					}
				} else if (protocol.packet.app.cmd == NetworkProtocol.CMD_CALL_END) {
					// 结束应答
					LogMsg("收到结束通话应答");
					clientThreadState = ClientThreadState.STATE_DISCONNECT;
					if (networkIndoors == null) {
						sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
								NetworkState.STATE_CALL_END);
					} else {
						Message msg = Message.obtain();
						msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
						msg.arg1 = NetworkState.STATE_CALL_END;
						msg.arg2 = list_count;
						mHandler.sendMessage(msg);
					}
				} else if (protocol.packet.app.cmd == NetworkProtocol.CMD_HEART_BEAT) {
					// 心跳应答
					LogMsg("收到心跳应答");
					runHeart = 0;
					try {
						Thread.sleep(HEARTBEAT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					clientThreadState = ClientThreadState.STATE_ALIVE;
				} else if (protocol.packet.app.cmd == NetworkProtocol.CMD_WORDS) {
					// 留言应答
					LogMsg("收到留言应答");
					runWords = 0;
					if (protocol.packet.app.data[0] == NetworkProtocol.DATA_WORDS_SUCCESS) {
						LogMsg("收到留言接收成功应答" + ++sn);
						if ((sn - 1) * 1024 > data_file.length) {
							clientThreadState = ClientThreadState.STATE_DISCONNECT;
							stopClientThread();
							mNetworkStateListener.SendRecvResult(
									NetworkProtocol.CMD_WORDS,
									NetworkState.STATE_SEND_SUCCESS);
						} else {
							LogMsg("发送留言请求 " + (sn - 1) * 1024 + " "
									+ data_file.length);
							prot.packet.app.srcId = LOCAL_ID;
							prot.packet.app.dstId = dstId;
							prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
							prot.packet.app.sn = sn;
							if (sn * 1024 > data_file.length) {
								data = new byte[data_file.length - (sn - 1)
										* 1024];
								System.arraycopy(data_file, (sn - 1) * 1024,
										data, 0, data_file.length - (sn - 1)
												* 1024);
							} else {
								data = new byte[1024];
								System.arraycopy(data_file, (sn - 1) * 1024,
										data, 0, 1024);
							}
							// LogHex(data, data.length);
							prot.packet.app.length = data.length;
							prot.packet.app.data = data;

							byte[] mSendBuf = null;
							if (isBase64Packet) {
								mSendBuf = prot.packet.toBase64Bytes(PacketKey);
							} else {
								mSendBuf = prot.packet.toBytes(PacketKey);
							}
							// LogMsg("RequestToServer send data:"
							// + prot.packet.toString());
							// LogHex(mSendBuf, mSendBuf.length);
							if (sendData(mSendBuf, mSendBuf.length)) {
								clientThreadState = ClientThreadState.STATE_RUNNING_WORDS;
							} else {
								clientThreadState = ClientThreadState.STATE_DISCONNECT;
								stopClientThread();
								mNetworkStateListener.SendRecvResult(
										NetworkProtocol.CMD_WORDS,
										NetworkState.STATE_SEND_ERROR);
							}
						}
					} else if (protocol.packet.app.data[0] == NetworkProtocol.DATA_WORDS_ERROR) {
						LogMsg("收到留言接收异常应答");
						clientThreadState = ClientThreadState.STATE_DISCONNECT;
						stopClientThread();
						mNetworkStateListener.SendRecvResult(
								NetworkProtocol.CMD_WORDS,
								NetworkState.STATE_SEND_ERROR);
					}
				}
			} else if (protocol.packet.app.type == NetworkProtocol.TYPE_REQUEST
			/* && protocol.packet.app.dstId == LOCAL_ID */) {
				// 收到结束通话请求
				if (protocol.packet.app.cmd == NetworkProtocol.CMD_CALL_END) {
					LogMsg("发送结束通话应答");
					endRespond(protocol.packet.app.sn);
				} else if (protocol.packet.app.cmd == NetworkProtocol.CMD_UNLOCK) {
					LogMsg("接收到开锁请求");
					mNetworkStateListener.OnUnlock();
				}
			} else {
				// clientThreadState = ClientThreadState.STATE_DISCONNECT;
				// if (networkIndoors == null) {
				// sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
				// NetworkState.STATE_CALL_ERROR);
				// } else {
				// Message msg = Message.obtain();
				// msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
				// msg.arg1 = NetworkState.STATE_CALL_ERROR;
				// msg.arg2 = list_count;
				// mHandler.sendMessage(msg);
				// }
			}
		}

		// 发送结束通话请求
		public void endRequest() {
			LogMsg("------------endRequest------------");
			prot.packet.init();
			prot.packet.app.srcId = LOCAL_ID;
			prot.packet.app.dstId = dstId;
			prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
			prot.packet.app.sn = ++sn;
			prot.packet.app.cmd = NetworkProtocol.CMD_CALL_END;
			prot.packet.app.length = 0;
			prot.packet.app.data = null;

			byte[] mSendBuf = null;
			if (isBase64Packet) {
				mSendBuf = prot.packet.toBase64Bytes(PacketKey);
			} else {
				mSendBuf = prot.packet.toBytes(PacketKey);
			}
			LogMsg("发送结束通话请求");
			LogMsg("endRequest send data:" + prot.packet.toString());
			LogHex(mSendBuf, mSendBuf.length);
			if (sendData(mSendBuf, mSendBuf.length)) {
				isEnd = true;
			} else {
				clientThreadState = ClientThreadState.STATE_DISCONNECT;
				if (networkIndoors == null) {
					sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
							NetworkState.STATE_CALL_ERROR);
				} else {
					Message msg = Message.obtain();
					msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
					msg.arg1 = NetworkState.STATE_CALL_ERROR;
					msg.arg2 = list_count;
					mHandler.sendMessage(msg);
				}
			}
		}

		// 发送结束通话应答
		private void endRespond(int sn) {
			LogMsg("------------endRespond------------");
			prot.packet.init();
			prot.packet.app.srcId = LOCAL_ID;
			prot.packet.app.dstId = dstId;
			prot.packet.app.type = NetworkProtocol.TYPE_REPLY;
			prot.packet.app.sn = sn;
			prot.packet.app.cmd = NetworkProtocol.CMD_CALL_END;
			byte[] d = new byte[1];
			d[0] = NetworkProtocol.DATA_END_SUCCESS;
			prot.packet.app.length = d.length;
			prot.packet.app.data = d;

			byte[] mSendBuf = null;
			if (isBase64Packet) {
				mSendBuf = prot.packet.toBase64Bytes(PacketKey);
			} else {
				mSendBuf = prot.packet.toBytes(PacketKey);
			}
			LogMsg("endRespond send data:" + prot.packet.toString());
			LogHex(mSendBuf, mSendBuf.length);
			sendData(mSendBuf, mSendBuf.length);

			clientThreadState = ClientThreadState.STATE_DISCONNECT;
			if (networkIndoors == null) {
				sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
						NetworkState.STATE_CALL_END);
			} else {
				Message msg = Message.obtain();
				msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
				msg.arg1 = NetworkState.STATE_CALL_END;
				msg.arg2 = list_count;
				mHandler.sendMessage(msg);
			}
		}

		// 发送开锁请求
		public void unLock() {
			LogMsg("------------unLock------------");
			prot.packet.init();
			prot.packet.app.srcId = LOCAL_ID;
			prot.packet.app.dstId = dstId;
			prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
			prot.packet.app.sn = ++sn;
			prot.packet.app.cmd = NetworkProtocol.CMD_UNLOCK;
			prot.packet.app.length = 0;
			prot.packet.app.data = null;

			byte[] mSendBuf = null;
			if (isBase64Packet) {
				mSendBuf = prot.packet.toBase64Bytes(PacketKey);
			} else {
				mSendBuf = prot.packet.toBytes(PacketKey);
			}
			LogMsg("发送开锁请求");
			LogMsg("unLock send data:" + prot.packet.toString());
			LogHex(mSendBuf, mSendBuf.length);
			sendData(mSendBuf, mSendBuf.length);
		}

		// 发送卡号
		public void sendCard(String card) {
			LogMsg("------------sendCard------------");
			prot.packet.init();
			prot.packet.app.srcId = LOCAL_ID;
			prot.packet.app.dstId = dstId;
			prot.packet.app.type = NetworkProtocol.TYPE_REQUEST;
			prot.packet.app.sn = ++sn;
			prot.packet.app.cmd = NetworkProtocol.CMD_CARD;
			prot.packet.app.length = card.getBytes().length;
			prot.packet.app.data = card.getBytes();
			byte[] mSendBuf = null;
			if (isBase64Packet) {
				mSendBuf = prot.packet.toBase64Bytes(PacketKey);
			} else {
				mSendBuf = prot.packet.toBytes(PacketKey);
			}
			LogMsg("发送卡号");
			LogMsg("unLock send data:" + prot.packet.toString());
			LogHex(mSendBuf, mSendBuf.length);
			sendData(mSendBuf, mSendBuf.length);
		}

		// 发送数据
		private boolean sendData(byte[] data, int datalen) {
			if (mSocketClient != null && mSocketClient.isConnected()
					&& !mSocketClient.isClosed()) {
				try {
					outputStream.write(data, 0, datalen);
					return true;
				} catch (IOException e) {
					LogMsg("SendData IOException");
					e.printStackTrace();
					clientThreadState = ClientThreadState.STATE_DISCONNECT;
					if (networkIndoors == null) {
						sendMsg(NetworkMsgType.MSG_CLIENT_DISCONNECTED,
								NetworkState.STATE_CALL_ERROR);
					} else {
						Message msg = Message.obtain();
						msg.what = NetworkMsgType.MSG_INDOOR_CHANGE;
						msg.arg1 = NetworkState.STATE_CALL_ERROR;
						msg.arg2 = list_count;
						mHandler.sendMessage(msg);
					}
					return false;
				}
			}
			return false;
		}

		private void sendMsg(int what, int state) {
			Message msg = Message.obtain();
			msg.what = what;
			msg.arg1 = state;
			mHandler.sendMessage(msg);
		}
	}

	private void startServerThread() {
		if (serverSocketThread == null && mServerSocket == null) {
			serverSocketThread = new ServerSocketThread();
			serverSocketThread.start();
		}
	}

	private void stopServerThread() {
		if (mServerSocket != null && !mServerSocket.isClosed()) {
			try {
				mServerSocket.close();
				mServerSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (serverSocketThread != null) {
			LogMsg("------------stopServerThread------------");
			if (executoService != null) {
				executoService.shutdown();
			}
			serverSocketThread.isRunning = false;
			if (doCalledRunnable != null) {
				doCalledRunnable.isRunning = false;
				try {
					doCalledRunnable.socket.close();
				} catch (IOException e) {
				}
				doCalledRunnable = null;
			}
			if (doCalledRunnable_momitor != null) {
				doCalledRunnable_momitor.isRunning = false;
				try {
					doCalledRunnable_momitor.socket.close();
				} catch (IOException e) {
				}
				doCalledRunnable_momitor = null;
			}
			serverSocketThread.interrupt();
			serverSocketThread = null;
			executoService = null;
		}
	}

	private void stopClientThread() {
		if (clientSocketThread != null) {
			LogMsg("------------stopClientThread------------");
			clientSocketThread.isRunning = false;
			clientSocketThread.interrupt();
			clientSocketThread = null;
		}
	}

	private void stopClientThread_urgent() {
		if (clientSocketThread_urgent != null) {
			LogMsg("------------clientSocketThread_urgent------------");
			clientSocketThread_urgent.isRunning = false;
			clientSocketThread_urgent.interrupt();
			clientSocketThread_urgent = null;
		}
	}

	private void stopClientThread_indoor(ClientSocketThread thread) {
		if (thread != null) {
			LogMsg("------------stopClientThread_indoor------------");
			thread.isRunning = false;
			thread.interrupt();
			clientSocketThread_urgent = null;
		}
	}

	private boolean listIsAline() {
		boolean listIsAline = false;
		for (NetworkIndoor networkIndoor : networkIndoors) {
			if (networkIndoor.isAline) {
				listIsAline = true;
				break;
			}
		}
		return listIsAline;
	}

	private boolean listIsSame(int i) {
		boolean isSame = true;
		for (NetworkIndoor networkIndoor : networkIndoors) {
			if (networkIndoor.networdState != networkIndoors.get(i).networdState) {
				isSame = false;
				break;
			}
		}
		return isSame;
	}

	private NetworkProtocol dataToProtocol(byte[] data) {
		NetworkProtocol protocol = new NetworkProtocol();
		protocol.packet.init();
		boolean ret = false;
		if (isBase64Packet) {
			ret = protocol.packet.getPacketFromBase64(data, 0, data.length,
					PacketKey);
		} else {
			ret = protocol.packet.toPacket(PacketKey, data, 0, data.length);
		}
		if (ret) {
			return protocol;
		} else {
			return null;
		}
	}

	private boolean checkNetworkHw() {

		boolean isOkay = false;
		ConnectivityManager connectMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ethNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// LogMsg("eth connect:" + ethNetInfo.isConnected());
		// LogMsg("wifi connect:" + wifiNetInfo.isConnected());
		// LogMsg("eth isAvailable:" + ethNetInfo.isAvailable());
		// LogMsg("wifi isAvailable:" + wifiNetInfo.isAvailable());

		if (!ethNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			LogMsg("connectionReceiver->disConnected!");
			// --网卡未开启（以太网或者wifi关闭了）
			isOkay = false;

		} else {
			// --wifi或者以太网以开启
			LogMsg("connectionReceiver->Connected!");
			if (!ethNetInfo.isAvailable() && !wifiNetInfo.isAvailable()) { // --无法获取IP或者无法正常使用
				LogMsg("connectionReceiver->isAvailable false!");
				isOkay = false;
			} else {// --网卡正常使用
				LogMsg("connectionReceiver->isAvailable true!");
				isOkay = true;
			}

		}
		if (isOkay) {
			mHandler.sendEmptyMessage(NetworkMsgType.MSG_NETWORK_ENABLED);
			// --通知资源管理器，网络可用
		} else {
			mHandler.sendEmptyMessage(NetworkMsgType.MSG_NETWORK_DISABLED);
			// --通知资源管理器，网络不可用
		}
		return isOkay;
	}

	private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// LogMsg("connectionReceiver");
			checkNetworkHw();
		}
	};

	private void setCamLight(boolean onoff) {
		// BoardCtrl boardCtrl = new BoardCtrl();
		// boardCtrl.start();
		// boardCtrl.setCamLight(onoff);
		// boardCtrl.stop();
	}

	// 向上层发送状态
	private void sendStateToUI(int state) {
		LogMsg("networdState " + networdState + " to " + state);
		if (networdState != state) {
			networdState = state;
			mNetworkStateListener.NetworkStateChange(networdState);
			if (timer_called != null) {
				timer_called.cancel();
				timer_called = null;
			}
		}
		// 接收到中断状态时要把状态重新设置
		if (networdState == NetworkState.STATE_CALL_END
				|| networdState == NetworkState.STATE_CALL_BUSY
				|| networdState == NetworkState.STATE_CALL_REFUSE
				|| networdState == NetworkState.STATE_CALL_CANNOT
				|| networdState == NetworkState.STATE_CALL_ERROR
				|| networdState == NetworkState.STATE_TIMEOUT) {
			if (serverSocketThread != null) {
				networdState = NetworkState.STATE_NETWORK_ENABLED;
			} else {
				networdState = NetworkState.STATE_NETWORK_DISABLED;
			}
		} else if (networdState == NetworkState.STATE_CALL_ED) {
			if (timer_called == null) {
				timer_called = new Timer();
				timer_called.schedule(new TimerTask() {
					@Override
					public void run() {
						sendStateToUI(NetworkState.STATE_NETWORK_ENABLED);
						doCalledRunnable.isRunning = false;
						try {
							doCalledRunnable.socket.close();
						} catch (IOException e) {
						}
						doCalledRunnable = null;
					}
				}, CALL_TIMEOUT_COUNT * HEARTBEAT);
			}
		}
	}

	private byte[] file2byte(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	private void byte2File(byte[] buf, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	public int getNetworkState() {
		return networdState;
	}

	public static void LogHex(byte[] data, int datalen) {
		if (isDebug) {
			String hexStr = "";
			for (int i = 0; i < datalen; i++) {

				if ((i % 16) == 0) {
					Log.i(TAG, hexStr);
					hexStr = "";
				}
				if (Integer.toHexString(data[i] & 0xff).length() == 1) {
					hexStr += "0";
				}
				hexStr += Integer.toHexString(data[i] & 0xff).toUpperCase()
						+ " ";
			}
			Log.i(TAG, hexStr);
		}
	}

	public static void LogMsg(String msg) {
		if (isDebug) {
			Log.i(TAG, msg);
			//AppLog.d(msg);
		}
	}

	public interface NetworkStateListener {
		/**
		 * 状态改变时触发
		 * 
		 * @param state
		 *            状态值
		 */
		public abstract void NetworkStateChange(int state);

		/**
		 * 被呼叫时触发
		 * 
		 * @param ip
		 *            呼叫方IP
		 * @param dstId
		 *            呼叫方ID
		 * @param callType
		 *            呼叫类型，普通or紧急
		 * @param indoorId
		 *            呼叫室内机的房号，其他情况下为0
		 */
		public abstract void OnCalled(String ip, int dstId, byte callType,
				int indoorId);

		/**
		 * 收到开锁请求时触发
		 */
		public abstract void OnUnlock();

		/**
		 * 收到留言时触发
		 * 
		 * @param ip
		 *            呼叫方IP
		 * @param dstId
		 *            呼叫方ID
		 * @param path
		 *            留言绝对路径
		 */
		public abstract void OnWords(String ip, int dstId, String path);

		/**
		 * 返回发送结果
		 * 
		 * @param type
		 *            发送类型
		 * @param state
		 *            返回结果
		 */
		public abstract void SendRecvResult(byte type, int state);

		public abstract void OnCard(String card);
	}
}
