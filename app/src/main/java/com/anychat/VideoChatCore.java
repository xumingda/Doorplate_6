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
	private SurfaceView mLocalSurfaceView = null; // --本地视频显示区域
	private SurfaceView mRemoteSurfaceView = null; // --远端视频显示区域
	private AnyChatCoreSDK mAnyChat = null; // --通讯核心
	private boolean isNeedRelease = false; // --是否初始化完成
	private boolean isLogin = false; // --是否以登录
	private boolean isConnected = false;
	private String serverIp = null;
	private int serverPort = 0;
	private boolean isCallOut = true;
	private Timer mCallTimer = null;
	private boolean isCameraDelayStart = true;

	private static final int STATUS_IDLE = 0X00; // --空闲状态
	private static final int STATUS_CONNECT_SERVER = 0X01; // --连接服务器状态
	private static final int STATUS_LOGIN_SERVER = 0X02; // --登录服务器状态
	private static final int STATUS_VIDEOCALL_REQUEST = 0X03; // --呼救请求状态
	private static final int STATUS_VIDEOCALL_RUNNING = 0X04; // --呼救会话状态
	private static final int STATUS_VIDEOCALL_FINISH = 0X05; // --呼救结束状态

	private static final int REQUEST_TIMEOUT = 10000;// --呼叫请求超时30s

	private int mStatus = STATUS_IDLE;

	private int[] mRemoteUserIds = new int[2];
	private String[] mRemoteUserNames = new String[2];
	private int mRemoteUserId = -1;

	private int mLocalUserId = -1; // --本地用户ID
	private String mLocalUserName = null; // --本地用户名
	private int mEnterRoomId = -1; // --当前进入的房间ID

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
		// --初始化
		initChatSDK();
		// --配置音视频参数
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
			// 不显示本地视频时，使用该接口
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
	 * @see:连接服务器
	 * @param: ip->服务器IP地址 port->服务器端口号
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
	 * @see:修改摄像头旋转方向
	 **/
	public void setCameraDisplayOrientation() {
		AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation();
	}

	public void setCameraDisplayOrientation(int degrees) {
		AnyChatCoreSDK.mCameraHelper.setCameraDisplayOrientation(degrees);
	}

	private void initChatSDK() {
		// --new 核心对象
		mAnyChat = AnyChatCoreSDK.getInstance(mContext);
		// --设置基本事件回调
		mAnyChat.SetBaseEvent(mAnyChatBaseEvent);
		// --设置业务对象回调
		mAnyChat.SetObjectEvent(mAnyChatObjectEvent);
		// --设置用户信息回调
		mAnyChat.SetUserInfoEvent(mAnyChatUserInfoEvent);
		// --状态切换回调
		mAnyChat.SetStateChgEvent(mAnyChatStateChgEvent);
		// --视频呼叫处理
		mAnyChat.SetVideoCallEvent(mAnyChatVideoCallEvent);
		// --截屏、录像
		mRecord = new VideoChatRecord(mContext, mAnyChat, this);
		mRecord.setRecordDirPath(RecordDirPath);
		// --请求过程处理
		mProcess[0] = null;
		mProcess[1] = null;
		// --初始化SDK
		mAnyChat.InitSDK(android.os.Build.VERSION.SDK_INT, 0);

		AnyChatCoreSDK.mCameraHelper.SetContext(mContext);

		mStatus = STATUS_IDLE;
	}

	// 根据配置文件配置视频参数
	private void ApplyVideoConfig() {
		ConfigEntity configEntity = ConfigService.LoadConfig(mContext);
		if (configEntity.configMode == 1) // 自定义视频参数配置
		{
			// 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL,
					configEntity.videoBitrate);
			if (configEntity.videoBitrate == 0) {
				// 设置本地视频编码的质量
				AnyChatCoreSDK.SetSDKOptionInt(
						AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,
						configEntity.videoQuality);
			}
			// 设置本地视频编码的帧率
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,
					configEntity.videoFps);
			// 设置本地视频编码的关键帧间隔
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,
					configEntity.videoFps * 4);
			// 设置本地视频采集分辨率
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,
					configEntity.resolution_width);
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,
					configEntity.resolution_height);
			// 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,
					configEntity.videoPreset);
		}
		// 让视频参数生效
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
				configEntity.configMode);
		// P2P设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,
				configEntity.enableP2P);
		// 本地视频Overlay模式设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,
				configEntity.videoOverlay);
		// 回音消除设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,
				configEntity.enableAEC);
		// 平台硬件编码设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,
				configEntity.useHWCodec);
		// 视频旋转模式设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
				configEntity.videorotatemode);
		// 本地视频采集偏色修正设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
				configEntity.fixcolordeviation);
		// 视频GPU渲染设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
				configEntity.videoShowGPURender);
		// 本地视频自动旋转设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
				configEntity.videoAutoRotation);

		if ((configEntity.videoOverlay != 0) && (mLocalSurfaceView != null)) {
			mLocalSurfaceView.getHolder().setType(
					SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		if (mLocalSurfaceView != null) {
			mLocalSurfaceView.setZOrderOnTop(true);
			// 如果是采用Java视频采集，则设置Surface的CallBack
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
				// 默认打开前置摄像头
				AnyChatCoreSDK.mCameraHelper
						.SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_FRONT);
			}
		} else {
			String[] strVideoCaptures = mAnyChat.EnumVideoCapture();
			if (strVideoCaptures != null && strVideoCaptures.length > 1) {
				// 默认打开前置摄像头
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
	 * ====================================================== 基本操作回调接口
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
									// --没有呼叫成功，中途结束呼叫
									mAnyChat.VideoCallControl(
											AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY,
											mRemoteUserId,
											AnyChatDefine.BRAC_ERRORCODE_SESSION_QUIT,
											0, 0, "");
								}
								if (mCallback != null) {
									mCallback.onCallOut(false);// --呼出失败
								}

							}
						}, REQUEST_TIMEOUT);
					} else {
						mCallTimer.schedule(new TimerTask() {
							@Override
							public void run() {

								if (mCallback != null) {
									mCallback.onCallIn(false);// --呼入失败
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
			// --如果连接服务器失败，则3秒之后重新连接
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
	 * ====================================================== 用户信息变化回调接口
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
						// --如果对方上线，则直接呼出去
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
			// --目标打开摄像头
			if ((dwUserId == mRemoteUserId) && (dwState == 2)) {
				if (mAnyChat.GetUserVideoWidth(mRemoteUserId) != 0
						&& mRemoteSurfaceView != null) {

					SurfaceHolder holder = mRemoteSurfaceView.getHolder();
					// 如果是采用内核视频显示（非Java驱动），则需要设置Surface的参数
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
					// --被呼叫方应答成功
					if (isCallOut) {
						if (mCallback != null) {
							mCallback.onCallOut(true);
						}
					}
				} else {
					// --被呼叫方拒绝
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
					// --对方呼入,直接应答成功
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
	 * @see:本地多媒体控制
	 * @param: isOpen->1: 开启 0:关闭
	 **/
	private void localMediaControl(int isOpen) {
		DEBUG("======= localMediaControl open:" + isOpen);
		if (mLocalSurfaceView == null) {
			// 不显示本地视频时，使用该接口
			if (isOpen == 1) {
				AnyChatCoreSDK.mCameraHelper.start();
			} else {
				AnyChatCoreSDK.mCameraHelper.stop();
			}

		} else {
			if (isOpen == 1) {
				mLocalSurfaceView.setZOrderOnTop(true);
				// 如果是采用Java视频采集，则设置Surface的CallBack
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
		if (isBroadcast) {// --广播时，禁止MIC
			mAnyChat.UserSpeakControl(-1, 0);
		} else {
			mAnyChat.UserSpeakControl(-1, isOpen);
		}
	}

	/**
	 * @see:远端多媒体控制
	 * @param: isOpen->1: 开启 0:关闭
	 **/
	private void remoteMediaControl(int isOpen) {
		DEBUG("======= remoteMediaControl open:" + isOpen);
		if (isOpen != 0) {
			if (mRemoteSurfaceView != null) {
				// 如果是采用Java视频显示，则设置Surface的CallBack
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
	 * @see:启动视频呼叫会话回调
	 * @param: roomId->临时房间号
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
	 * @see:结束视频呼叫会话回调
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

	// --录像回调
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

	// --截屏回调
	@Override
	public void onSnapCallback(String path) {
		// TODO Auto-generated method stub

	}

}
