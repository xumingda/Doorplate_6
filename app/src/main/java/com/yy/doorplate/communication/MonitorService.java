package com.yy.doorplate.communication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaCodecInfo;
import android.os.IBinder;
import android.util.Log;

import com.camera.CameraManager;
import com.camera.CameraManager.CameraFrameCallback;
import com.network.NetworkState;
import com.smart.videochat.VideoEncoder;
import com.smart.videochat.VideoEncoder.VideoEncoderCallback;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.protocol.MonitorProtocol;

public class MonitorService extends Service implements CameraFrameCallback,
		VideoEncoderCallback {

	private final String TAG = "MonitorService";

	private MyApplication application;

	private ServerSocket serverSocket = null;

	private boolean isRunning = true;

	private MonitorProtocol monitorProtocol = null;

	private List<Socket> sockets = null;;

	private CameraManager mCameraManager = null;
	private Camera mCamera = null;
	private VideoEncoder mVideoEncoder = null;

	private byte[] i420Buf = null;

	@Override
	public void onCreate() {
		super.onCreate();
		application = (MyApplication) getApplication();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mCameraManager = new CameraManager(application, this);
		monitorProtocol = new MonitorProtocol();
		mVideoEncoder = new VideoEncoder(640, 480);
		mVideoEncoder.setVideoEncoderCallback(this);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				startServer();
			}
		});
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	// 开始运行服务器
	private void startServer() {
		try {
			sockets = new ArrayList<Socket>();
			serverSocket = new ServerSocket(18888);
			System.out.println("***监控服务器开启***");
			while (isRunning) {
				System.out.println("***等待客户端的连接***");
				Socket socket = serverSocket.accept();

				InetAddress address = socket.getInetAddress();
				System.out.println("当前客户端的IP：" + address.getHostAddress());

				if (!application.getRunningActivityName().equals(
						"com.yy.doorplate.activity.CallActivity")
						&& application.networdState != NetworkState.STATE_CALL_ED
						&& application.networdState != NetworkState.STATE_CALL_IN
						&& application.networdState != NetworkState.STATE_CALL_WAIT
						&& !application.isTakePicture) {
					if (mCamera == null) {
						mCamera = mCameraManager.open();
						mCameraManager.startPreview();
						i420Buf = new byte[mCameraManager.getFrameBufferSize()];
						System.out.println("***打开摄像头预览***");
					}
					if (mCamera != null) {
						sockets.add(socket);
					} else {
						socket.close();
						System.out.println("***断开" + address.getHostAddress()
								+ "连接***");
					}
				} else {
					socket.close();
					System.out.println("***断开" + address.getHostAddress()
							+ "连接***");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopServer() {
		try {
			isRunning = false;
			serverSocket.close();
			mCameraManager.close();
			mCamera = null;
			sockets = null;
			serverSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCameraFrame(byte[] data, int offset, int length) {
		// Log.d(TAG, "-------onCameraFrame-------" + length);
		if (i420Buf != null) {
			Camera.Parameters p = mCamera.getParameters();
			if (mVideoEncoder.getColorFormat() == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
				mCameraManager.YV12toYUV420Planar(data, i420Buf,
						p.getPreviewSize().width, p.getPreviewSize().height);
				mVideoEncoder.pushFrame(i420Buf, 0, i420Buf.length);
			} else if (mVideoEncoder.getColorFormat() == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar) {
				mCameraManager.YV12toYUV420PackedSemiPlanar(data, i420Buf,
						p.getPreviewSize().width, p.getPreviewSize().height);
				mVideoEncoder.pushFrame(i420Buf, 0, i420Buf.length);
			} else {
				mVideoEncoder.pushFrame(data, 0, data.length);
			}
		}
	}

	private OutputStream outputStream;

	@Override
	public void onVideoEncoderFrame(byte[] data, int offset, int length) {
		if (sockets != null && sockets.size() > 0) {
			for (int i = 0; i < sockets.size(); i++) {
				Socket socket = sockets.get(i);
				if (socket != null && socket.isConnected()
						&& !socket.isClosed()) {
					try {
						outputStream = socket.getOutputStream();
						byte[] out = monitorProtocol.pack(data, offset, length);
						if (out != null) {
							Log.d(TAG, "-------onVideoEncoderFrame-------"
									+ out.length);
							outputStream.write(out);
						}
						// outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
						sockets.remove(i);
					}
				} else {
					sockets.remove(i);
				}
			}
		} else {
			mCameraManager.close();
			mCamera = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopServer();
	}
}
