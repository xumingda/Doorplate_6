package com.smart.videochat;

import org.webrtc.webrtcdemo.MediaEngine;
import org.webrtc.webrtcdemo.MediaEngineObserver;
import org.webrtc.webrtcdemo.NativeWebRtcContextRegistry;

import android.content.Context;
import android.graphics.Color;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.anychat.VideoChatCore;
import com.anychat.VideoChatCore.VideoChatCallback;

public class VideoChatSdk {

	private Context mContext = null;
	private ViewGroup mLocalView = null;
	private ViewGroup mRemoteView = null;
	private SurfaceView mLocalSurfaceView = null;
	private SurfaceView mRemoteSurfaceView = null;
	private NativeWebRtcContextRegistry contextRegistry = null;
	private MediaEngine mediaEngine = null;

	private static final int MODE_WEBRTC = 0;
	private static final int MODE_ANYCHAT = 1;
	private static final int MODE_EYUN = 2;

	private String dstIp = null;
	private int DEFAULT_MODE = MODE_ANYCHAT;// MODE_WEBRTC;
	private VideoChatCore mVideoChatCore = null;

	public interface VideoChatListener {
		public void OnStatusChanaged(int inFps, int outFps, int inKbps,
				int outKbps, int width, int height, int packetLoss);

		public void onFinish();

		public void onCallOut(boolean isSuccess);

		public void onCallIn(boolean isSuccess);
		
		public void onRecordFinish(String path, int Elapse);
	}

	private VideoChatListener mListener = null;

	public VideoChatSdk(Context context) {
		mContext = context;
	}

	public int getMode() {
		return DEFAULT_MODE;
	}

	public void webrtcInit() {

		contextRegistry = new NativeWebRtcContextRegistry();
		contextRegistry.register(mContext);
		mediaEngine = new MediaEngine(mContext);
		// --开启调试
		mediaEngine.setTrace(true);
		// --默认屏蔽音频
		mediaEngine.setAudio(false);
		// --设置音频处理器ISAC
		mediaEngine.setAudioCodec(mediaEngine.getIsacIndex());
		// --设置音频接收端口
		mediaEngine.setAudioRxPort(VideoChatConfig.AUDIO_RX_PORT);
		// --设置音频远端端口
		mediaEngine.setAudioTxPort(VideoChatConfig.AUDIO_TX_PORT);

		// --开启音频调试
		mediaEngine.setDebuging(true);
		// --设置视频接收端口
		mediaEngine.setVideoRxPort(VideoChatConfig.VIDEO_RX_PORT);
		// --设置视频发送端口
		mediaEngine.setVideoTxPort(VideoChatConfig.VIDEO_TX_PORT);
		// --设置视频处理器
		mediaEngine.setVideoCodec(VideoChatConfig.VIDEO_CODEC_OPENGL);
		mediaEngine.setViewSelection(VideoChatConfig.VIDEO_CODEC_OPENGL);
		// --设置不应答
		mediaEngine.setNack(true);
		// --设置视频发送像素为640x480
		// mediaEngine.setResolutionIndex(MediaEngine.numberOfResolutions() -
		// 2);
		// --设置视频发送像素为320x240,由于设置640x480导致声音传输有问题，故降为320x240
		mediaEngine.setResolutionIndex(1);

		/*
		 * ======================================= 音频配置
		 * =======================================
		 */
		// --防止回声(AECM)
		mediaEngine.setEc(true);
		// --开启防噪声(NS)
		mediaEngine.setNs(true);
		// --开启自增益(AGC)
		mediaEngine.setAgc(true);
		// --开启喇叭
		mediaEngine.setSpeaker(true);

		// --设置状态回调
		mediaEngine.setObserver(mObserver);

	}

	public void setListener(VideoChatListener listener) {
		mListener = listener;
	}

	public void webrtcUninit() {
		mediaEngine.dispose();
		contextRegistry.unRegister();
	}

	private void anychatInit() {
		mVideoChatCore = new VideoChatCore(mContext);
		mVideoChatCore.setCallback(mVideoChatCallback);
		mVideoChatCore.init();
	}

	private void anychatUninit() {
		mVideoChatCore.uninit();
		mVideoChatCore = null;
	}

	public void init() {
		if (DEFAULT_MODE == MODE_WEBRTC) {
			webrtcInit();
		}
		if (DEFAULT_MODE == MODE_ANYCHAT) {
			anychatInit();
		}
	}

	public void uninit() {
		if (DEFAULT_MODE == MODE_WEBRTC) {
			webrtcUninit();
		}
		if (DEFAULT_MODE == MODE_ANYCHAT) {
			anychatUninit();
		}
	}

	public void setLayoutView(ViewGroup local, ViewGroup remote) {
		mLocalView = local;
		mRemoteView = remote;

	}

	public void setDstIp(String ip) {
		if (DEFAULT_MODE == MODE_WEBRTC) {

			mediaEngine.setRemoteIp(ip);
		}
	}

	public void setServerIp(String ip, int port) {
		if (DEFAULT_MODE == MODE_ANYCHAT) {
			mVideoChatCore.setServerIpPort(ip, port);
		}
	}

	public void setVideoTxRx(boolean tx, boolean rx) {
		if (DEFAULT_MODE == MODE_WEBRTC) {
			mediaEngine.setSendVideo(tx);
			mediaEngine.setReceiveVideo(rx);
		}
	}

	public void enableAudio() {
		if (DEFAULT_MODE == MODE_WEBRTC) {
			mediaEngine.setAudio(true);
		}
	}

	public void disableAudio() {
		if (DEFAULT_MODE == MODE_WEBRTC) {
			mediaEngine.setAudio(false);
		}
	}

	public void enableRecord() {
		mVideoChatCore.enableRecord();
	}

	public void disableRecord() {
		mVideoChatCore.disableRecord();
	}

	public void start() {

		mediaEngine.start();
		SurfaceView remoteSurfaceView = mediaEngine.getRemoteSurfaceView();
		if (mRemoteView != null && remoteSurfaceView != null) {

			mRemoteView.addView(remoteSurfaceView);
		}
		SurfaceView localSurfaceView = mediaEngine.getLocalSurfaceView();
		if (mLocalView != null && localSurfaceView != null) {

			mLocalView.addView(localSurfaceView);
		}

	}

	public void stop() {
		if (DEFAULT_MODE == MODE_WEBRTC) {
			SurfaceView remoteSurfaceView = mediaEngine.getRemoteSurfaceView();
			SurfaceView localSurfaceView = mediaEngine.getLocalSurfaceView();
			if (mRemoteView != null && remoteSurfaceView != null) {
				mRemoteView.removeView(remoteSurfaceView);
			}
			if (mLocalView != null && localSurfaceView != null) {
				mLocalView.removeView(localSurfaceView);
			}
			mediaEngine.stop();
		}
		if (DEFAULT_MODE == MODE_ANYCHAT) {
			mVideoChatCore.stop();
			if (mLocalView != null && mLocalSurfaceView != null) {
				mLocalView.removeView(mLocalSurfaceView);
				mLocalSurfaceView = null;
			}
			if (mRemoteView != null && mRemoteSurfaceView != null) {
				mRemoteView.removeView(mRemoteSurfaceView);
				mRemoteSurfaceView = null;
			}
		}
	}

	private MediaEngineObserver mObserver = new MediaEngineObserver() {

		@Override
		public void onStateChanged(int inFps, int outFps, int inKbps,
				int outKbps, int width, int height, int packetLoss) {

			if (mListener != null) {
				mListener.OnStatusChanaged(inFps, outFps, inKbps, outKbps,
						width, height, packetLoss);
			}
		}

		@Override
		public void newStats(String stats) {

		}
	};

	public void start(int localId, int remoteId, boolean isCallOut) {
		if (DEFAULT_MODE != MODE_ANYCHAT) {
			return;
		}

		if (mLocalView != null) {
			mLocalSurfaceView = new SurfaceView(mContext);
			mLocalView.addView(mLocalSurfaceView);
		}

		if (mRemoteView != null) {
			mRemoteSurfaceView = new SurfaceView(mContext);
			mRemoteView.addView(mRemoteSurfaceView);
		}

		mVideoChatCore.setSurfaceView(mLocalSurfaceView, mRemoteSurfaceView);
		mVideoChatCore.start(localId, remoteId, isCallOut);
	}

	public void start(String localId, String remoteId, boolean isCallOut) {
		if (DEFAULT_MODE != MODE_ANYCHAT) {
			return;
		}

		if (mLocalView != null) {
			mLocalSurfaceView = new SurfaceView(mContext);
			mLocalView.addView(mLocalSurfaceView);

			View view = new View(mLocalView.getContext());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 50);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			view.setBackgroundColor(Color.BLACK);
			mLocalView.addView(view, params);

		}

		if (mRemoteView != null) {
			mRemoteSurfaceView = new SurfaceView(mContext);
			mRemoteView.addView(mRemoteSurfaceView);

			View view = new View(mRemoteView.getContext());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 50);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			view.setBackgroundColor(Color.BLACK);
			mRemoteView.addView(view, params);
		}

		mVideoChatCore.setSurfaceView(mLocalSurfaceView, mRemoteSurfaceView);
		mVideoChatCore.start(localId, remoteId, isCallOut);

	}

	public VideoChatCore.VideoChatCallback mVideoChatCallback = new VideoChatCallback() {

		@Override
		public void onRunning(boolean isSuccess) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecordFinish(String path, int Elapse) {
			if (mListener != null) {
				mListener.onRecordFinish(path, Elapse);
			}
		}

		@Override
		public void onLogin(boolean isSuccess, int errcode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLinkClose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFinish() {
			if (mListener != null) {
				mListener.onFinish();
			}
		}

		@Override
		public void onConnect(boolean isSuccess, int errcode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCallOut(boolean isSuccess) {
			if (mListener != null) {
				mListener.onCallOut(isSuccess);
			}
		}

		@Override
		public void onCallIn(boolean isSuccess) {
			if (mListener != null) {
				mListener.onCallIn(isSuccess);
			}
		}
	};

}
