package com.anychat;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.anychat.VideoChatRecord.VideoChatRecordCallback;
import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatObjectEvent;
import com.bairuitech.anychat.AnyChatOutParam;
import com.bairuitech.anychat.AnyChatStateChgEvent;
import com.bairuitech.anychat.AnyChatUserInfoEvent;
import com.bairuitech.anychat.AnyChatVideoCallEvent;

public class VideoChatCore implements VideoChatRecordCallback {

	public static VideoChatCore instance = new VideoChatCore();

	private static final String TAG = "VideoChatCore";
	private static final boolean isDebug = true;
	private Context mContext = null;
	private SurfaceView mLocalSurfaceView = null; // --������Ƶ��ʾ����
	private SurfaceView mRemoteSurfaceView = null; // --Զ����Ƶ��ʾ����
	private AnyChatCoreSDK mAnyChat = null; // --ͨѶ����
	private boolean isNeedRelease = false; // --�Ƿ��ʼ�����
	private boolean isLogin = false; // --�Ƿ��Ե�¼
	private boolean isConnected = false;
	private String serverIp = null;
	private int serverPort = 0;
	private boolean isCallOut = true;
	private Timer mCallTimer = null;
	private boolean isCameraDelayStart = true;

	private static final int STATUS_IDLE = 0X00; // --����״̬
	private static final int STATUS_CONNECT_SERVER = 0X01; // --���ӷ�����״̬
	private static final int STATUS_LOGIN_SERVER = 0X02; // --��¼������״̬
	private static final int STATUS_VIDEOCALL_REQUEST = 0X03; // --��������״̬
	private static final int STATUS_VIDEOCALL_RUNNING = 0X04; // --���ȻỰ״̬
	private static final int STATUS_VIDEOCALL_FINISH = 0X05; // --���Ƚ���״̬

	private static final int REQUEST_TIMEOUT = 10000;// --��������ʱ30s

	private int mStatus = STATUS_IDLE;

	private int[] mRemoteUserIds = new int[2];
	private String[] mRemoteUserNames = new String[2];
	private int mRemoteUserId = -1;

	private int mLocalUserId = -1; // --�����û�ID
	private String mLocalUserName = null; // --�����û���
	private int mEnterRoomId = -1; // --��ǰ����ķ���ID

	private int videoIndex = 0;

	private boolean isRecordEnable = false;

	private static final String RecordDirPath = "/WarnChat/Record/";

	public interface VideoChatCallback {
		public abstract void onConnect(boolean isSuccess, int errcode);

		public abstract void onLogin(boolean isSuccess, int errcode);

		public abstract void onCallOut(boolean isSuccess);

		public abstract void onCallIn(boolean isSuccess);

		public abstract void onRunning(boolean isSuccess);

		public abstract void onFinish();

		public abstract void onLinkClose();

		public abstract void onRecordFinish(String path, int Elapse);

	}

	private VideoChatCallback mCallback = null;

	private static final int LEFT_AUDIO = 0;
	private static final int RIGHT_AUDIO = 1;
	private static final int BROADCAST_SPEAKER = RIGHT_AUDIO;
	private static final int VIDEOCALL_SPEAKER = LEFT_AUDIO;
	private boolean isBroadcast = false;

	private VideoChatRecord mRecord = null;
	private VideoChatProcess[] mProcess = new VideoChatProcess[2];
	private VideoChatMessage mVideoChatMsg = null;
	private VideoChatSession mSession = null;
	private Timer mRequestTimer = null;

	public static void DEBUG(String content) {
		if (isDebug) {
			Log.d(TAG, content);
		}
	}

	public static void DEBUG_HEX(byte[] data, int datalen) {
		if (isDebug) {
			String hexStr = "";
			for (int i = 0; i < datalen; i++) {

				if ((i % 16) == 0) {
					Log.d(TAG, hexStr);
					hexStr = "";
				}
				if (Integer.toHexString(data[i] & 0xff).length() == 1) {
					hexStr += "0";
				}
				hexStr += Integer.toHexString(data[i] & 0xff).toUpperCase()
						+ " ";
			}
			Log.d(TAG, hexStr);
		}
	}

	public static VideoChatCore getInstance() {
		return instance;
	}

	public VideoChatCore() {
		mLocalSurfaceView = null;
		mRemoteSurfaceView = null;
	}

	public void setContext(Context context) {
		mContext = context;
	}

	public VideoChatCore(Context context, SurfaceView local, SurfaceView remote) {
		mContext = context;
		mLocalSurfaceView = local;
		mRemoteSurfaceView = remote;
	}

	public VideoChatCore(Context context) {
		mContext = context;
	}

	public void setSurfaceView(SurfaceView local, SurfaceView remote) {
		mLocalSurfaceView = local;
		mRemoteSurfaceView = remote;
	}

	public void setCallback(VideoChatCallback callback) {
		mCallback = callback;
	}

	public void setServerIpPort(String ip, int port) {
		serverIp = ip;
		serverPort = port;
	}

	public void init() {
		mRemoteUserIds[0] = -1;
		mRemoteUserIds[1] = -1;
		// isCameraDelayStart=true;
		// --��ʼ��
		initChatSDK();
		// --��������Ƶ����
		ApplyVideoConfig();

		isNeedRelease = true;
	}

	public void uninit() {

		if (!isNeedRelease) {
			return;
		}

		mRemoteUserNames[0] = null;
		mRemoteUserNames[1] = null;
		mRemoteUserId = -1;
		mEnterRoomId = -1;

		if (mLocalSurfaceView == null) {
			// ����ʾ������Ƶʱ��ʹ�øýӿ�
			AnyChatCoreSDK.mCameraHelper.stop();
		} else {
			if (isCameraDelayStart) {
				AnyChatCoreSDK.mCameraHelper.stop();
			}
		}

		if (isNeedRelease) {
			mAnyChat.Release();
		}

		isNeedRelease = false;

	}

	public void enableRecord() {
		isRecordEnable = true;
	}

	public void disableRecord() {
		isRecordEnable = false;
	}

	/**
	 * @see:���ӷ�����
	 * @param: ip->������IP��ַ port->�������˿ں�
	 **/
	public void connect(String ip, int port) {

		if (!isNeedRelease) {
			return;
		}

		serverIp = ip;
		serverPort = port;
		mAnyChat.Connect(ip, port);
	}

	public boolean start(int localId, int dstId, boolean isCallOut) {
		this.isCallOut = isCallOut;

		// init();
		mLocalUserName = String.valueOf(localId);
		mRemoteUserNames[0] = String.valueOf(dstId);
		mAnyChat.Connect(serverIp, serverPort);

		return true;
	}

	public boolean start(String localId, String dstId, boolean isCallOut) {
		this.isCallOut = isCallOut;

		// init();
		mLocalUserName = localId;
		mRemoteUserNames[0] = dstId;
		mAnyChat.Connect(serverIp, serverPort);

		return true;
	}

	public void stop() {

		if (mEnterRoomId != -1) {
			FinishCall();
		}
		// uninit();
		if (isLogin) {
			mAnyChat.Logout();
			isLogin = false;
		}

	}

	/**
	 * @see:�޸�����ͷ��ת����
	 **/
	public void setCameraDisplayOrientation() {
		AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation();
	}

	public void setCameraDisplayOrientation(int degrees) {
		AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation(degrees);
	}

	private void initChatSDK() {
		// --new ���Ķ���
		mAnyChat = AnyChatCoreSDK.getInstance(mContext);
		// --���û����¼��ص�
		mAnyChat.SetBaseEvent(mAnyChatBaseEvent);
		// --����ҵ�����ص�
		mAnyChat.SetObjectEvent(mAnyChatObjectEvent);
		// --�����û���Ϣ�ص�
		mAnyChat.SetUserInfoEvent(mAnyChatUserInfoEvent);
		// --״̬�л��ص�
		mAnyChat.SetStateChgEvent(mAnyChatStateChgEvent);
		// --��Ƶ���д���
		mAnyChat.SetVideoCallEvent(mAnyChatVideoCallEvent);
		// --������¼��
		mRecord = new VideoChatRecord(mContext, mAnyChat, this);
		mRecord.setRecordDirPath(RecordDirPath);
		// --������̴���
		mProcess[0] = null;
		mProcess[1] = null;
		// --��ʼ��SDK
		mAnyChat.InitSDK(android.os.Build.VERSION.SDK_INT, 0);

		AnyChatCoreSDK.mCameraHelper.SetContext(mContext);

		mStatus = STATUS_IDLE;
	}

	// ���������ļ�������Ƶ����
	private void ApplyVideoConfig() {
		ConfigEntity configEntity = ConfigService.LoadConfig(mContext);
		if (configEntity.configMode == 1) // �Զ�����Ƶ��������
		{
			// ���ñ�����Ƶ��������ʣ��������Ϊ0�����ʾʹ����������ģʽ��
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL,
					configEntity.videoBitrate);
			if (configEntity.videoBitrate == 0) {
				// ���ñ�����Ƶ���������
				AnyChatCoreSDK.SetSDKOptionInt(
						AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,
						configEntity.videoQuality);
			}
			// ���ñ�����Ƶ�����֡��
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,
					configEntity.videoFps);
			// ���ñ�����Ƶ����Ĺؼ�֡���
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,
					configEntity.videoFps * 4);
			// ���ñ�����Ƶ�ɼ��ֱ���
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,
					configEntity.resolution_width);
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,
					configEntity.resolution_height);
			// ������Ƶ����Ԥ�������ֵԽ�󣬱�������Խ�ߣ�ռ��CPU��ԴҲ��Խ�ߣ�
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,
					configEntity.videoPreset);
		}
		// ����Ƶ������Ч
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
				configEntity.configMode);
		// P2P����
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,
				configEntity.enableP2P);
		// ������ƵOverlayģʽ����
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,
				configEntity.videoOverlay);
		// ������������
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,
				configEntity.enableAEC);
		// ƽ̨Ӳ����������
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,
				configEntity.useHWCodec);
		// ��Ƶ��תģʽ����
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
				configEntity.videorotatemode);
		// ������Ƶ�ɼ�ƫɫ��������
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
				configEntity.fixcolordeviation);
		// ��ƵGPU��Ⱦ����
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
				configEntity.videoShowGPURender);
		// ������Ƶ�Զ���ת����
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
				configEntity.videoAutoRotation);

		if ((configEntity.videoOverlay != 0) && (mLocalSurfaceView != null)) {
			mLocalSurfaceView.getHolder().setType(
					SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		if (mLocalSurfaceView != null) {
			mLocalSurfaceView.setZOrderOnTop(true);
			// ����ǲ���Java��Ƶ�ɼ���������Surface��CallBack
			if (AnyChatCoreSDK
					.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
				mLocalSurfaceView.getHolder().addCallback(
						AnyChatCoreSDK.mCameraHelper);
				if (isCameraDelayStart) {
					AnyChatCoreSDK.mCameraHelper.start(mLocalSurfaceView);
				}
				Log.i("ANYCHAT", "VIDEOCAPTRUE---" + "JAVA");
			}
		}

		if (AnyChatCoreSDK
				.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
			if (AnyChatCoreSDK.mCameraHelper.GetCameraNumber() > 1) {
				// Ĭ�ϴ�ǰ������ͷ
				AnyChatCoreSDK.mCameraHelper
						.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
			}
		} else {
			String[] strVideoCaptures = mAnyChat.EnumVideoCapture();
			if (strVideoCaptures != null && strVideoCaptures.length > 1) {
				// Ĭ�ϴ�ǰ������ͷ
				for (int i = 0; i < strVideoCaptures.length; i++) {
					String strDevices = strVideoCaptures[i];
					if (strDevices.indexOf("Front") >= 0) {
						mAnyChat.SelectVideoCapture(strDevices);
						break;
					}
				}
			}
		}
	}

	/**
	 * ====================================================== ���������ص��ӿ�
	 * ======================================================
	 **/
	private AnyChatBaseEvent mAnyChatBaseEvent = new AnyChatBaseEvent() {

		@Override
		public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
			DEBUG("=============================================");
			DEBUG(" OnAnyChatUserAtRoomMessage:");
			DEBUG(" dwUserId:" + dwUserId);
			DEBUG(" bEnter:" + bEnter);
			DEBUG("=============================================");
			if (bEnter) {
				if (dwUserId == mRemoteUserId) {
					remoteMediaControl(1);

				}
			} else {
				if (dwUserId == mRemoteUserId) {
					remoteMediaControl(0);
				}
			}
		}

		@Override
		public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
			DEBUG("=============================================");
			DEBUG(" OnAnyChatOnlineUserMessage:");
			DEBUG(" dwUserNum:" + dwUserNum);
			DEBUG(" dwRoomId:" + dwRoomId);
			DEBUG("=============================================");
			if (mRemoteUserId != -1) {
				remoteMediaControl(1);
				if (isRecordEnable) {
					mRecord.startRecord();
				}
			}
		}

		@Override
		public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
			DEBUG("=============================================");
			DEBUG(" OnAnyChatLoginMessage:");
			DEBUG(" dwUserId:" + dwUserId);
			DEBUG(" dwErrorCode:" + dwErrorCode);
			DEBUG("=============================================");
			isLogin = (dwErrorCode == 0) ? true : false;
			if (mCallback != null) {
				mCallback.onLogin((dwErrorCode == 0) ? true : false,
						dwErrorCode);
				if (dwErrorCode != 0) {
					mStatus = STATUS_CONNECT_SERVER;
					mLocalUserId = -1;
				} else {
					mLocalUserId = dwUserId;

					mCallTimer = new Timer();
					if (isCallOut) {
						mCallTimer.schedule(new TimerTask() {
							@Override
							public void run() {
								if (mEnterRoomId == -1 && mRemoteUserId != -1) {
									// --û�к��гɹ�����;��������
									mAnyChat.VideoCallControl(
											AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY,
											mRemoteUserId,
											AnyChatDefine.BRAC_ERRORCODE_SESSION_QUIT,
											0, 0, "");
								}
								if (mCallback != null) {
									mCallback.onCallOut(false);// --����ʧ��
								}

							}
						}, REQUEST_TIMEOUT);
					} else {
						mCallTimer.schedule(new TimerTask() {
							@Override
							public void run() {

								if (mCallback != null) {
									mCallback.onCallIn(false);// --����ʧ��
								}

							}
						}, REQUEST_TIMEOUT);
					}
				}
			}
		}

		@Override
		public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
			if (mCallback != null) {
				mCallback.onLinkClose();
			}

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					mAnyChat.Connect(serverIp, serverPort);
				}
			}, 3000);
		}

		@Override
		public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
			DEBUG("=============================================");
			DEBUG(" OnAnyChatEnterRoomMessage:");
			DEBUG(" dwRoomId:" + dwRoomId);
			DEBUG(" dwErrorCode:" + dwErrorCode);
			DEBUG("=============================================");
			if (dwErrorCode == 0) {
				mEnterRoomId = dwRoomId;

				localMediaControl(1);
				if (mCallback != null) {
					mCallback.onRunning(true);
				}
			} else {
				if (mCallback != null) {
					mCallback.onRunning(false);
				}
			}
		}

		@Override
		public void OnAnyChatConnectMessage(boolean bSuccess) {
			DEBUG("=============================================");
			DEBUG(" OnAnyChatConnectMessage:");
			DEBUG(" bSuccess:" + bSuccess);
			DEBUG("=============================================");
			if (mCallback != null) {
				mCallback.onConnect(bSuccess, -1);
				if (!bSuccess) {
					mStatus = STATUS_IDLE;
				}
			}

			isConnected = bSuccess;
			// --������ӷ�����ʧ�ܣ���3��֮����������
			if (!isConnected) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						mAnyChat.Connect(serverIp, serverPort);
					}
				}, 3000);
			} else {

				mAnyChat.Login(mLocalUserName, "");
			}
		}
	};

	private AnyChatObjectEvent mAnyChatObjectEvent = new AnyChatObjectEvent() {

		@Override
		public void OnAnyChatObjectEvent(int dwObjectType, int dwObjectId,
				int dwEventType, int dwParam1, int dwParam2, int dwParam3,
				int dwParam4, String strParam) {
			DEBUG("=============================================");
			DEBUG(" OnAnyChatObjectEvent:");
			DEBUG(" dwObjectType:" + dwObjectType);
			DEBUG(" dwObjectId:" + dwObjectId);
			DEBUG(" dwEventType:" + dwEventType);
			DEBUG(" dwParam1:" + dwParam1);
			DEBUG(" dwParam2:" + dwParam2);
			DEBUG(" dwParam3:" + dwParam3);
			DEBUG(" dwParam4:" + dwParam4);
			DEBUG(" strParam:" + strParam);
			DEBUG("=============================================");
		}
	};

	/**
	 * ====================================================== �û���Ϣ�仯�ص��ӿ�
	 * ======================================================
	 **/
	private AnyChatUserInfoEvent mAnyChatUserInfoEvent = new AnyChatUserInfoEvent() {

		@Override
		public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {
			DEBUG("=============================================");
			DEBUG(" OnAnyChatUserInfoUpdate:");
			DEBUG(" dwUserId:" + dwUserId);
			DEBUG(" dwType:" + dwType);
			DEBUG("=============================================");
		}

		@Override
		public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {
			DEBUG("=============================================");
			DEBUG(" OnAnyChatFriendStatus:");
			DEBUG(" dwUserId:" + dwUserId);
			DEBUG(" dwStatus:" + dwStatus);
			DEBUG("=============================================");

			if (dwUserId != mLocalUserId && dwStatus != 0) {

				if (mRemoteUserNames[0] != null) {
					if (mRemoteUserNames[0].equals(mAnyChat
							.GetUserName(dwUserId))) {
						mRemoteUserIds[0] = dwUserId;
						mRemoteUserId = mRemoteUserIds[0];
						DEBUG("=== REMOTE ID:" + mRemoteUserId);
						// --����Է����ߣ���ֱ�Ӻ���ȥ
						if (isCallOut) {
							CallOut(dwUserId);
						}
					}
				}
			} else if ((dwUserId != mLocalUserId) && (dwStatus == 0)) {
				if (mRemoteUserId == dwUserId) {
					mRemoteUserId = -1;
				}
			}
		}
	};

	private AnyChatStateChgEvent mAnyChatStateChgEvent = new AnyChatStateChgEvent() {

		@Override
		public void OnAnyChatP2PConnectStateMessage(int dwUserId, int dwState) {

		}

		@Override
		public void OnAnyChatMicStateChgMessage(int dwUserId, boolean bOpenMic) {
			// TODO Auto-generated method stub

		}

		@Override
		public void OnAnyChatChatModeChgMessage(int dwUserId,
				boolean bPublicChat) {
			// TODO Auto-generated method stub

		}

		@Override
		public void OnAnyChatCameraStateChgMessage(int dwUserId, int dwState) {
			// --Ŀ�������ͷ
			if ((dwUserId == mRemoteUserId) && (dwState == 2)) {
				if (mAnyChat.GetUserVideoWidth(mRemoteUserId) != 0
						&& mRemoteSurfaceView != null) {

					SurfaceHolder holder = mRemoteSurfaceView.getHolder();
					// ����ǲ����ں���Ƶ��ʾ����Java������������Ҫ����Surface�Ĳ���
					if (AnyChatCoreSDK
							.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
						holder.setFormat(PixelFormat.RGB_565);
						holder.setFixedSize(mAnyChat.GetUserVideoWidth(-1),
								mAnyChat.GetUserVideoHeight(-1));
					}
					Surface s = holder.getSurface();
					if (AnyChatCoreSDK
							.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
						mAnyChat.mVideoHelper.SetVideoUser(videoIndex,
								mRemoteUserId);
					} else
						mAnyChat.SetVideoPos(mRemoteUserId, s, 0, 0, 0, 0);
				}
				if (mLocalSurfaceView != null) {
					SurfaceHolder holder = mLocalSurfaceView.getHolder();
					if (AnyChatCoreSDK
							.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
						holder.setFormat(PixelFormat.RGB_565);
						holder.setFixedSize(mAnyChat.GetUserVideoWidth(-1),
								mAnyChat.GetUserVideoHeight(-1));
					}
					Surface s = holder.getSurface();
					mAnyChat.SetVideoPos(-1, s, 0, 0, 0, 0);
				}
			}
		}

		@Override
		public void OnAnyChatActiveStateChgMessage(int dwUserId, int dwState) {

		}
	};

	private AnyChatVideoCallEvent mAnyChatVideoCallEvent = new AnyChatVideoCallEvent() {

		@Override
		public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId,
				int dwErrorCode, int dwFlags, int dwParam, String userStr) {
			if (dwUserId != mRemoteUserId) {
				return;
			}
			switch (dwEventType) {
			case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY:
				VideoChatCore.DEBUG(" VIDEOCALL REPLY!");
				VideoChatCore.DEBUG(" REPLY:" + dwErrorCode);
				if (mCallTimer != null) {
					mCallTimer.cancel();
					mCallTimer = null;
				}
				if (dwErrorCode == 0) {
					// --�����з�Ӧ��ɹ�
					if (isCallOut) {
						if (mCallback != null) {
							mCallback.onCallOut(true);
						}
					}
				} else {
					// --�����з��ܾ�
					if (isCallOut) {
						if (mCallback != null) {
							mCallback.onCallOut(false);
						}
					}
				}

			case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST:
				VideoChatCore.DEBUG(" VIDEOCALL REQUEST!");
				if (!isCallOut) {
					if (mCallTimer != null) {
						mCallTimer.cancel();
						mCallTimer = null;
					}
					// --�Է�����,ֱ��Ӧ��ɹ�
					mAnyChat.VideoCallControl(
							AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY,
							mRemoteUserId, 0, 0, 0, "");
					if (mCallback != null) {
						mCallback.onCallIn(true);
					}
				}
				break;
			case AnyChatDefine.BRAC_VIDEOCALL_EVENT_START:
				VideoChatCore.DEBUG(" VIDEOCALL START!");
				onStartVideoCall(dwParam);
				break;
			case AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH:
				VideoChatCore.DEBUG(" VIDEOCALL FINISH!");
				onFinishVideoCall();
				break;
			}

		}
	};

	/**
	 * @see:���ض�ý�����
	 * @param: isOpen->1: ���� 0:�ر�
	 **/
	private void localMediaControl(int isOpen) {
		DEBUG("======= localMediaControl open:" + isOpen);
		if (mLocalSurfaceView == null) {
			// ����ʾ������Ƶʱ��ʹ�øýӿ�
			if (isOpen == 1) {
				AnyChatCoreSDK.mCameraHelper.start();
			} else {
				AnyChatCoreSDK.mCameraHelper.stop();
			}

		} else {
			if (isOpen == 1) {
				mLocalSurfaceView.setZOrderOnTop(true);
				// ����ǲ���Java��Ƶ�ɼ���������Surface��CallBack
				if (AnyChatCoreSDK
						.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
					mLocalSurfaceView.getHolder().addCallback(
							AnyChatCoreSDK.mCameraHelper);
					if (isCameraDelayStart) {
						AnyChatCoreSDK.mCameraHelper.start(mLocalSurfaceView);
					}
					Log.i("ANYCHAT", "VIDEOCAPTRUE---" + "JAVA");
				}
			} else {
				AnyChatCoreSDK.mCameraHelper.stop();
			}

		}
		mAnyChat.UserCameraControl(-1, isOpen);
		if (isBroadcast) {// --�㲥ʱ����ֹMIC
			mAnyChat.UserSpeakControl(-1, 0);
		} else {
			mAnyChat.UserSpeakControl(-1, isOpen);
		}
	}

	/**
	 * @see:Զ�˶�ý�����
	 * @param: isOpen->1: ���� 0:�ر�
	 **/
	private void remoteMediaControl(int isOpen) {
		DEBUG("======= remoteMediaControl open:" + isOpen);
		if (isOpen != 0) {
			if (mRemoteSurfaceView != null) {
				// ����ǲ���Java��Ƶ��ʾ��������Surface��CallBack
				if (AnyChatCoreSDK
						.GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
					videoIndex = mAnyChat.mVideoHelper
							.bindVideo(mRemoteSurfaceView.getHolder());
					mAnyChat.mVideoHelper.SetVideoUser(videoIndex,
							mRemoteUserId);
					Log.i("ANYCHAT", "VIDEOSHOW---" + "JAVA");
				}

				mAnyChat.UserCameraControl(mRemoteUserId, isOpen);
			}
		}

		mAnyChat.UserSpeakControl(mRemoteUserId, isOpen);
	}

	private void CallOut(int remoteId) {
		mEnterRoomId = -1;
		mAnyChat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST,
				remoteId, 0, 0, 0, "");

	}

	private void FinishCall() {
		if (mEnterRoomId != -1) {
			mAnyChat.VideoCallControl(
					AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH, mRemoteUserId,
					0, 0, 0, "");
			onFinishVideoCall();
		}
	}

	/**
	 * @see:������Ƶ���лỰ�ص�
	 * @param: roomId->��ʱ�����
	 **/
	private void onStartVideoCall(int roomId) {

		mEnterRoomId = roomId;

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				mAnyChat.EnterRoom(mEnterRoomId, "");
			}
		}, 500);

	}

	/**
	 * @see:������Ƶ���лỰ�ص�
	 **/
	private void onFinishVideoCall() {

		if (isRecordEnable) {
			mRecord.stopRecord();
		}

		if (mEnterRoomId >= 0) {
			localMediaControl(0);
			remoteMediaControl(0);
			mAnyChat.LeaveRoom(mEnterRoomId);
			mEnterRoomId = -1;
		}

		if (mCallback != null) {
			mCallback.onFinish();
		}

	}

	// --¼��ص�
	@Override
	public void onRecordCallback(String path, int Elapse) {
		// TODO Auto-generated method stub
		if (mCallback != null) {

			AnyChatOutParam outParam = new AnyChatOutParam();
			mAnyChat.TransFile(mRemoteUserId, path, 1, 2, 0, outParam);
			DEBUG("== TransFile out:" + outParam.GetIntValue());
			mCallback.onRecordFinish(path, Elapse);
		}
	}

	// --�����ص�
	@Override
	public void onSnapCallback(String path) {
		// TODO Auto-generated method stub

	}

}
