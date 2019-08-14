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

	// Socketͨ�Ŷ˿�
	public static final int PORT = 30000;
	// ��ʱʱ��
	public static final int TIMEOUT = 5000;
	// ����ʱ��
	public static final int HEARTBEAT = 3000;
	// ������ʱ�����������
	public static final int HEARTBEAT_TIMEOUT_COUNT = 2;
	// �������еȴ����ٴ�����ʱ����Ϊ��ʱ
	public static final int CALL_TIMEOUT_COUNT = 10;
	// --DES����key
	public byte[] PacketKey = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
			0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10 };
	// �Ƿ�Base64���
	public static final boolean isBase64Packet = true;

	// ����byte�Ĺ̶�����
	private static final int recvbuf_lenght = 2048;

	private MyApplication mContext = null;

	private ServerSocket mServerSocket = null;

	private NetworkHandler mHandler = null;
	// serversocket�����߳�
	private ServerSocketThread serverSocketThread = null;
	// clientsocket�߳�
	private ClientSocketThread clientSocketThread = null;
	// clientSocketThread_urgent�߳�
	private ClientSocketThread clientSocketThread_urgent = null;
	// �������е�DoCalledRunnable
	private ServerSocketThread.DoCalledRunnable doCalledRunnable = null;
	// ��غ����е�DoCalledRunnable
	private ServerSocketThread.DoCalledRunnable doCalledRunnable_momitor = null;
	// �̳߳أ��������б������߳�
	private ExecutorService executoService = null;

	// ���г�ʱ��ûӦ��ĸ�λ����
	private Timer timer_called = null;

	// webrtc
	public static VideoChatSdk mVideoChatSdk = null;

	// ����ID
	private int LOCAL_ID = NetworkProtocol.SRC_ID;

	// �ص� ������ǰ״̬
	private NetworkStateListener mNetworkStateListener = null;

	// ��ǵ�ǰ״̬���ı�ʱ�ص����ϲ�
	public static int networdState = NetworkState.STATE_INIT;

	// �����������ڻ������б�
	private List<NetworkIndoor> networkIndoors = null;

	// �������������Ҷ����ڻ��ı�־
	private boolean active_hangup_indoor = false;

	private CameraManager cameraManager;

	/**
	 * 
	 * @param context
	 * @param listener
	 *            NetworkStateListener
	 * @param local
	 *            ������Ƶ��RelativeLayout����Ϊnull
	 * @param remote
	 *            Զ����Ƶ��RelativeLayout����Ϊnull
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

	/* �������Handler��Ϣ���� */
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
				// ����Ͽ�
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
				// ������������
				LogMsg("------MSG_NETWORK_ENABLED------");
				startServerThread();
				break;
			}
			case NetworkMsgType.MSG_SERVER_ENABLED: {
				// Socket������
				LogMsg("------MSG_SERVER_ENABLED------");
				sendStateToUI(NetworkState.STATE_NETWORK_ENABLED);
				break;
			}
			case NetworkMsgType.MSG_CLIENT_DISCONNECTED: {
				// �������жϿ�
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
				// ��ʼ��Ƶͨ��
				LogMsg("------MSG_CLIENT_WEBRTC_START------" + msg.arg2);
				mVideoChatSdk.setDstIp(msg.obj.toString());
				mVideoChatSdk.enableAudio();
				closeCamera();
				// 0������������һ����1����������һ��
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
	 * ����ͨѶ����
	 * 
	 * @param localId
	 *            ����id
	 */
	public void startServer(int localId) {
		LOCAL_ID = localId;
		// LogMsg("startServer");
		// checkNetworkHw();

		// *��������״̬�仯�㲥����
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(connectionReceiver, intentFilter);
	}

	/**
	 * �ر�ͨѶ�����ͷ���Դ
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
	 * �������л��߽���
	 * 
	 * @param ip
	 *            �������е�IP��ַ������ʱ��Ϊ��
	 * @param dstId
	 *            �������еĶԷ�id������ʱ��Ϊ0
	 * @param type
	 *            �������е����ͣ���������or�������У�����ʱ��Ϊ0
	 * @param indoorId
	 *            �������Ļ����������ڻ�����idΪ���ڻ�id�������������idΪ0
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
	 * �����������ڻ���ȫ���ְ汾�������ڻ�ʱ��Ҫͬʱ����������·ͬʱ���������ڻ��󶨵��ֻ���
	 * 
	 * @param ips
	 *            �Է���ip����
	 * @param ids
	 *            �Է���id���� ��ip��id˳������Ҫһһ��Ӧ
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
	 * �Ҷ�
	 */
	public void stopCall() {
		if (networdState == NetworkState.STATE_CALL_IN
				|| networdState == NetworkState.STATE_CALL_IN_MONITOR) {
			// ͨ���йҶ�
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
					// ��������ʱ�����Ҷ�
					clientSocketThread.endRequest();
				} else if (clientSocketThread == null
						&& doCalledRunnable != null) {
					// ��������ʱ�����Ҷ�
					doCalledRunnable
							.sendMsgToClient(NetworkProtocol.CMD_CALL_END);
				} else if (clientSocketThread == null
						&& doCalledRunnable_momitor != null) {
					// ��غ���ʱ�����Ҷ�
					doCalledRunnable_momitor
							.sendMsgToClient(NetworkProtocol.CMD_CALL_END);
				}
			}
		} else if (networdState == NetworkState.STATE_CALL_WAIT) {
			// �ȴ��Է���ͨ�йҶ�
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
			// ����ʱ�Ҷ�
			if (clientSocketThread == null && doCalledRunnable != null) {
				doCalledRunnable
						.sendMsgToClient(NetworkProtocol.DATA_CALL_REFUSE);
			}
		}
	}

	/**
	 * ����չʾ���ֵ�ViewGroup
	 * 
	 * @param local
	 *            ������Ƶ��RelativeLayout����Ϊnull
	 * @param remote
	 *            Զ����Ƶ��RelativeLayout����Ϊnull
	 */
	public void setLayoutView(ViewGroup local, ViewGroup remote) {
		if (mVideoChatSdk != null) {
			mVideoChatSdk.setLayoutView(local, remote);
		}
	}

	/**
	 * ���ÿ��ӶԽ����շ�״̬
	 * 
	 * @param tx
	 *            trueΪ������Ƶ���ͣ�falseΪ�ر�
	 * @param rx
	 *            trueΪ������Ƶ���գ�falseΪ�ر�
	 */
	public void setVideoTxRx(boolean tx, boolean rx) {
		if (mVideoChatSdk != null) {
			mVideoChatSdk.setVideoTxRx(tx, rx);
		}
	}

	// --���ý�����������Ϊfalse������Ϊtrue
	public void setMix(boolean mix) {
		if (mVideoChatSdk != null) {
			// mVideoChatSdk.setMix(mix);
		}
	}

	/**
	 * ���Ϳ�������
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
	 * ��������
	 * 
	 * @param ip
	 *            �Է�ip
	 * @param dstId
	 *            �Է�id
	 * @param path
	 *            ��Ƶ�ļ��ľ���·��
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
						LogMsg("socket���ӷ������ɹ�");
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

			// ��ǣ������м��ͨ�����ڵ��������Ҫ���������������������������Ƚ�����ǰ���ͨ����������ǰͨ�����������ٽ�����������
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

			// ���������߼�����
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
								LogMsg("�յ�" + ip + "����");
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
								// ������·��æӦ��
								LogMsg("�յ�" + ip + "����");
								LogMsg("������·��æӦ��");
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
							LogMsg("�յ�" + ip + "��������");
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
								LogMsg("�յ�" + ip + "��غ���");
								doCalledRunnable_momitor = this;
								LogMsg("���ͺ��гɹ�Ӧ��");
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
								// ������·��æӦ��
								LogMsg("�յ�" + ip + "��غ���");
								LogMsg("������·��æӦ��");
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
						LogMsg("�յ���������");
						LogMsg("��������Ӧ��");
						// ��������Ӧ��
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
						LogMsg("�յ�����ͨ������");
						LogMsg("���ͽ���ͨ��Ӧ��");
						// ���ͽ���ͨ��Ӧ��
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
						LogMsg("���յ���������");
						mNetworkStateListener.OnUnlock();
					} else if (prot.packet.app.cmd == NetworkProtocol.CMD_WORDS) {
						LogMsg("���յ��������� " + prot.packet.app.sn);
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
						LogMsg("���յ��������� ");
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
					// �յ�����ͨ��Ӧ��
					LogMsg("�յ�����ͨ��Ӧ��");
					stopThisRun(NetworkState.STATE_CALL_END);
				} else {
					stopThisRun(NetworkState.STATE_CALL_ERROR);
				}
			}

			// ������ͻ��˷����������Ӧ��
			public void sendMsgToClient(int type) {
				if (type == NetworkProtocol.DATA_CALL_SUCCESS) {
					LogMsg("���ͺ��гɹ�Ӧ��");
					// ����������гɹ�Ӧ��
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
					LogMsg("�������ͨ������");
					// �����������ͨ������
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
					LogMsg("����ܾ�����Ӧ��");
					// ��������ܾ�����Ӧ��
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
					LogMsg("����������");
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

			// ��������
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

	// ���clientsocket�̵߳ĵ�ǰ����
	private enum ClientThreadState {
		STATE_CONNECT, STATE_DISCONNECT, STATE_TOSERVER, STATE_RUNNING, STATE_RUNNING_HEART, STATE_RUNNING_WORDS, STATE_ALIVE, STATE_UNKNOWN
	}

	// ���������̣߳����������������̶��ڸ��߳�ִ��
	public class ClientSocketThread extends Thread {

		public boolean isRunning = true;

		private Socket mSocketClient = null;
		private String ip;
		private ClientThreadState clientThreadState = ClientThreadState.STATE_UNKNOWN;
		private DataOutputStream outputStream = null;
		private DataInputStream inputStream = null;
		private NetworkProtocol prot = new NetworkProtocol();

		// ���еȴ�����
		private int runWait = 0;
		// δ�յ�����������
		private int runHeart = 0;
		// ������δ�յ�����
		private int runEnd = 0;
		// δ�յ�����Ӧ�����
		private int runWords = 0;

		// �������ͽ���ͨ���ı�ǣ��������ͽ���ͨ����ֹͣ���������ͣ��ȴ�����Ӧ��
		private boolean isEnd = false;
		// ��ǣ������м��ͨ�����ڵ��������Ҫ���������������������������Ƚ�����ǰ���ͨ����������ǰͨ�����������ٽ�����������
		public int isMomitor = 0;

		private int dstId;
		private int sn = 0;
		private byte[] data;
		private String path;
		private byte[] data_file;

		// ��ǣ����߳�λ��List<NetworkIndoor>�е���һ��λ�ã���0��ʼ
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

		// ����
		private boolean onConnect() {
			LogMsg("------------onConnect------------");
			try {
				if (mSocketClient == null) {
					mSocketClient = new Socket();
					SocketAddress serverAddress = new InetSocketAddress(ip,
							PORT);
					LogMsg("CONNECT:" + serverAddress.toString());
					// --�������ӷ�����5�볬ʱ,��ֹ���ӷ���������
					mSocketClient.connect(serverAddress, TIMEOUT);
					LogMsg("Socket���ӳɹ�");
					// --��������������
					outputStream = new DataOutputStream(
							mSocketClient.getOutputStream());
					inputStream = new DataInputStream(
							mSocketClient.getInputStream());
					// --��������ʱ����Ϊ������ճ�ʱ
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

		// �Ͽ�����
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

		// �ͻ��������������ͨ��
		private void RequestToServer() {
			LogMsg("------------RequestToServer------------");
			if (mSocketClient != null) {
				LogMsg("����ͨ������");
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

		// ������������
		private void WordsToServer() {
			LogMsg("------------WordsToServer------------");
			if (mSocketClient != null) {
				LogMsg("������������");
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

		// ���еȴ�Ӧ��
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
					// �Է���ʱ��δӦ�𣬷����޷�����
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
					// ����2��û���յ�������Ӧ�𣬷���ͨ������
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

		// ������
		private void onAlive() {
			// LogMsg("------------onAlive------------");
			if (isEnd) {
				clientThreadState = ClientThreadState.STATE_RUNNING_HEART;
				return;
			}
			if ((networdState == NetworkState.STATE_CALL_IN || networdState == NetworkState.STATE_CALL_IN_MONITOR)
					&& mSocketClient != null) {
				LogMsg("����������");
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

		// ����������Ӧ���߼�����
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
					// ����Ӧ��
					if (protocol.packet.app.data[0] == NetworkProtocol.DATA_CALL_SUCCESS) {
						LogMsg("�յ�����Ӧ��");
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
						LogMsg("�յ���æӦ��");
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
						LogMsg("�յ��ܾ�����Ӧ��");
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
						LogMsg("�յ��޷���ͨӦ��");
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
					// ����Ӧ��
					LogMsg("�յ�����ͨ��Ӧ��");
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
					// ����Ӧ��
					LogMsg("�յ�����Ӧ��");
					runHeart = 0;
					try {
						Thread.sleep(HEARTBEAT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					clientThreadState = ClientThreadState.STATE_ALIVE;
				} else if (protocol.packet.app.cmd == NetworkProtocol.CMD_WORDS) {
					// ����Ӧ��
					LogMsg("�յ�����Ӧ��");
					runWords = 0;
					if (protocol.packet.app.data[0] == NetworkProtocol.DATA_WORDS_SUCCESS) {
						LogMsg("�յ����Խ��ճɹ�Ӧ��" + ++sn);
						if ((sn - 1) * 1024 > data_file.length) {
							clientThreadState = ClientThreadState.STATE_DISCONNECT;
							stopClientThread();
							mNetworkStateListener.SendRecvResult(
									NetworkProtocol.CMD_WORDS,
									NetworkState.STATE_SEND_SUCCESS);
						} else {
							LogMsg("������������ " + (sn - 1) * 1024 + " "
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
						LogMsg("�յ����Խ����쳣Ӧ��");
						clientThreadState = ClientThreadState.STATE_DISCONNECT;
						stopClientThread();
						mNetworkStateListener.SendRecvResult(
								NetworkProtocol.CMD_WORDS,
								NetworkState.STATE_SEND_ERROR);
					}
				}
			} else if (protocol.packet.app.type == NetworkProtocol.TYPE_REQUEST
			/* && protocol.packet.app.dstId == LOCAL_ID */) {
				// �յ�����ͨ������
				if (protocol.packet.app.cmd == NetworkProtocol.CMD_CALL_END) {
					LogMsg("���ͽ���ͨ��Ӧ��");
					endRespond(protocol.packet.app.sn);
				} else if (protocol.packet.app.cmd == NetworkProtocol.CMD_UNLOCK) {
					LogMsg("���յ���������");
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

		// ���ͽ���ͨ������
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
			LogMsg("���ͽ���ͨ������");
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

		// ���ͽ���ͨ��Ӧ��
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

		// ���Ϳ�������
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
			LogMsg("���Ϳ�������");
			LogMsg("unLock send data:" + prot.packet.toString());
			LogHex(mSendBuf, mSendBuf.length);
			sendData(mSendBuf, mSendBuf.length);
		}

		// ���Ϳ���
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
			LogMsg("���Ϳ���");
			LogMsg("unLock send data:" + prot.packet.toString());
			LogHex(mSendBuf, mSendBuf.length);
			sendData(mSendBuf, mSendBuf.length);
		}

		// ��������
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
			// --����δ��������̫������wifi�ر��ˣ�
			isOkay = false;

		} else {
			// --wifi������̫���Կ���
			LogMsg("connectionReceiver->Connected!");
			if (!ethNetInfo.isAvailable() && !wifiNetInfo.isAvailable()) { // --�޷���ȡIP�����޷�����ʹ��
				LogMsg("connectionReceiver->isAvailable false!");
				isOkay = false;
			} else {// --��������ʹ��
				LogMsg("connectionReceiver->isAvailable true!");
				isOkay = true;
			}

		}
		if (isOkay) {
			mHandler.sendEmptyMessage(NetworkMsgType.MSG_NETWORK_ENABLED);
			// --֪ͨ��Դ���������������
		} else {
			mHandler.sendEmptyMessage(NetworkMsgType.MSG_NETWORK_DISABLED);
			// --֪ͨ��Դ�����������粻����
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

	// ���ϲ㷢��״̬
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
		// ���յ��ж�״̬ʱҪ��״̬��������
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
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
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
		 * ״̬�ı�ʱ����
		 * 
		 * @param state
		 *            ״ֵ̬
		 */
		public abstract void NetworkStateChange(int state);

		/**
		 * ������ʱ����
		 * 
		 * @param ip
		 *            ���з�IP
		 * @param dstId
		 *            ���з�ID
		 * @param callType
		 *            �������ͣ���ͨor����
		 * @param indoorId
		 *            �������ڻ��ķ��ţ����������Ϊ0
		 */
		public abstract void OnCalled(String ip, int dstId, byte callType,
				int indoorId);

		/**
		 * �յ���������ʱ����
		 */
		public abstract void OnUnlock();

		/**
		 * �յ�����ʱ����
		 * 
		 * @param ip
		 *            ���з�IP
		 * @param dstId
		 *            ���з�ID
		 * @param path
		 *            ���Ծ���·��
		 */
		public abstract void OnWords(String ip, int dstId, String path);

		/**
		 * ���ط��ͽ��
		 * 
		 * @param type
		 *            ��������
		 * @param state
		 *            ���ؽ��
		 */
		public abstract void SendRecvResult(byte type, int state);

		public abstract void OnCard(String card);
	}
}
