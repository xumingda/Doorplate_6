package com.camera;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public final class CameraManager {

	private static final String TAG = "CameraManager";

	private static final boolean isDebug = true;

	private Context mContext;

	private Camera mCamera = null; // ����ͷ��

	private boolean previewing = false; // �ж���ʱ�Ƿ���Ԥ��������

	private RelativeLayout mRelativeLayout = null;
	private SurfaceView mSurfaceView = null;
	private SurfaceHolder mSurfaceHolder = null; // Ԥ�������ľ��

	private String savePath = null; // ��������ͼƬ��·��

	private byte[] picData = null;

	private boolean af = false; // �Զ��Խ�״̬

	private int tabSize = 100;

	private byte[] previewBuf = null;

	/*
	 * ============================ ���ƣ�public Camera open(int
	 * cameraId,SurfaceHolder holder) ���ܣ���������ͷ
	 * ������cameraId->���API9���ϵ�camera���ж�������Ժ��ڿ��Ե�����Ĭ�ϴ�����ͷ0 hodler->Ԥ���Ŀؼ����
	 * ============================
	 */
	boolean isFirstPreview = false;

	private CameraFrameCallback cameraFrameCallback;
	private CameraManagerListener cameraManagerListener;

	public interface CameraFrameCallback {
		public abstract void onCameraFrame(byte[] data, int offset, int length);
	}

	public CameraManager(Context context, CameraFrameCallback callback) {
		mContext = context;
		cameraManagerListener = null;
		cameraFrameCallback = callback;
	}

	public CameraManager(Context context, CameraManagerListener listener) {
		mContext = context;
		cameraFrameCallback = null;
		cameraManagerListener = listener;
	}

	public Camera open() {
		LogMsg("Camera num = " + Camera.getNumberOfCameras());
		if (Camera.getNumberOfCameras() <= 0) {
			return null;
		}
		mCamera = Camera.open();
		// setCamLight(true);
		if (mCamera == null) {
			return mCamera;
		}
		LogMsg("open ok");
		/* ���ô���ص����� */
		mCamera.setErrorCallback(new ErrorCallback() {

			@Override
			public void onError(int error, Camera camera) {
				LogMsg("error = " + error);
				mCamera.release();
				mCamera = null;
			}
		});
		mCamera.stopPreview();
		try {
			if (mSurfaceHolder != null) {
				/* ����Ԥ����� */
				mCamera.setPreviewDisplay(mSurfaceHolder);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* ��������ͷ���� */
		// Camera.Parameters p = mCamera.getParameters();
		// List<Size> previewSizes = p.getSupportedPreviewSizes();
		// for (Size s : previewSizes) {
		// Log.i(TAG, "Support Preview Size: w:" + s.width + " h:" + s.height);
		// }
		// List<Integer> previewFormats = p.getSupportedPreviewFormats();
		// for (Integer f : previewFormats) {
		// Log.i(TAG, "Support Preview format:" + f);
		// }
		// Log.i(TAG, "PREVIEW TYPE:" + p.getPreviewFormat());
		// Log.i(TAG, "PIC TYPE:" + p.getPictureFormat());
		// Log.i(TAG, "White Balance:" + p.getSupportedWhiteBalance());
		// p.setWhiteBalance("auto");
		// p.setPreviewFrameRate(25);
		// p.setPreviewSize(640, 480);
		// p.setPreviewFormat(ImageFormat.YV12);
		// mCamera.setParameters(p);
		//
		// Size previewSize = p.getPreviewSize();
		// Log.i(TAG, "SIZE: w=" + previewSize.width + " h=" +
		// previewSize.height);
		// previewBuf = new byte[(previewSize.height * previewSize.width * 3) /
		// 2];
		// mCamera.addCallbackBuffer(previewBuf);
		mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				// Log.i("CAM", "onFrameCallback:" + cameraFrameCallback);
				if (cameraFrameCallback != null) {
					cameraFrameCallback.onCameraFrame(data, 0, data.length);
				}
				camera.addCallbackBuffer(data);
			}
		});

		return mCamera;
	}

	private Camera open(final boolean isTakePicture) {

		LogMsg("Camera num = " + Camera.getNumberOfCameras());
		if (Camera.getNumberOfCameras() <= 0) {
			return null;
		}

		try {
			if (mCamera == null) {
				mCamera = Camera.open();
				// setCamLight(true);
				LogMsg("open ok");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

		if (mCamera == null) {
			LogMsg("mCamera == null");
		}

		/* ���ô���ص����� */
		mCamera.setErrorCallback(new ErrorCallback() {

			@Override
			public void onError(int error, Camera camera) {
				LogMsg("error = " + error);
				mCamera.release();
				mCamera = null;
			}
		});

		mCamera.stopPreview();

		try {
			if (mSurfaceHolder != null) {
				/* ����Ԥ����� */
				mCamera.setPreviewDisplay(mSurfaceHolder);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* ��������ͷ���� */
		// setCameraDisplayOrientation(180);
		// setCameraDisplayOrientation();
		Camera.Parameters p = mCamera.getParameters();
		List<Size> previewSizes = p.getSupportedPreviewSizes();
		for (Size s : previewSizes) {
			Log.i(TAG, "Support Preview Size: w:" + s.width + " h:" + s.height);
		}
		List<Integer> previewFormats = p.getSupportedPreviewFormats();
		for (Integer f : previewFormats) {
			Log.i(TAG, "Support Preview format:" + f);
		}
		Log.i(TAG, "PREVIEW TYPE:" + p.getPreviewFormat());
		Log.i(TAG, "PIC TYPE:" + p.getPictureFormat());
		Log.i(TAG, "White Balance:" + p.getSupportedWhiteBalance());
		p.setWhiteBalance("auto");
		p.setPreviewFrameRate(25);
		p.setPreviewSize(640, 480);
		p.setPreviewFormat(ImageFormat.YV12);
		mCamera.setParameters(p);

		Size previewSize = p.getPreviewSize();
		LogMsg("SIZE: w=" + previewSize.width + " h=" + previewSize.height);
		previewBuf = new byte[(previewSize.height * previewSize.width * 3) / 2];
		mCamera.addCallbackBuffer(previewBuf);

		isFirstPreview = false;
		mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				// LogMsg("onPreviewFrameWithBuffer");
				previewing = true;
				if (isTakePicture && !isFirstPreview) {
					isFirstPreview = true;
					LogMsg("=====takePicture=====");
					mCamera.takePicture(shutterCallback, rawCallback,
							jpegCallback);
				}
				mCamera.addCallbackBuffer(data);
			}
		});

		return mCamera;
	}

	public int getFrameBufferSize() {
		Camera.Parameters p = mCamera.getParameters();
		Size previewSize = p.getPreviewSize();
		return (previewSize.height * previewSize.width * 3) / 2;
	}

	// ��ǰAPI�ĵȼ�����9ʱ������֧�ֶ�����ͷ
	public int getCamNumbers() {

		return Camera.getNumberOfCameras();
	}

	/*
	 * ============================���ƣ� public void setDisplayOrientation(int
	 * arg)���ܣ�����ͼ���������arg->�����ת���� ============================
	 */
	public void setDisplayOrientation(int arg) {
		mCamera.setDisplayOrientation(arg);
	}

	/*
	 * ============================���ƣ� public void setSavePath(String path)
	 * ���ܣ����ô洢ͼƬ��·��������path->·�� ============================
	 */
	public void setSavePath(String path) {
		savePath = path;
	}

	/*
	 * ============================���ƣ� public void close()���ܣ��ر�����ͷ
	 * ============================
	 */
	public void close() {

		if (mCamera != null && previewing == true) {
			mCamera.setPreviewCallbackWithBuffer(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			previewing = false;
			LogMsg("Camera close");
		}

		if (mRelativeLayout != null && mSurfaceView != null) {
			mRelativeLayout.removeView(mSurfaceView);
			mSurfaceView = null;
			mSurfaceHolder = null;
			mRelativeLayout = null;
			LogMsg("View is null");
		}

		// setCamLight(false);
	}

	public void startPreview() {
		if (mCamera != null && previewing == false) {
			mCamera.startPreview();
			previewing = true;
		}
	}

	/**
	 * ���ܣ�����Ԥ��
	 * 
	 * @param context
	 * @param layout
	 *            Ԥ����RelativeLayout������Ϊ��
	 */
	public void startPreview(Context context, RelativeLayout layout,
			final boolean isTakePicture, String path) {

		savePath = path;

		mContext = context;
		mRelativeLayout = layout;
		if (mRelativeLayout == null) {
			return;
		}
		mSurfaceView = new SurfaceView(mContext);
		mRelativeLayout.addView(mSurfaceView, new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				mCamera = open(isTakePicture);
				if (mCamera != null && previewing == false) {
					mCamera.startPreview();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});
	}

	/**
	 * ���ܣ�ֹͣԤ�����ͷ���Դ
	 */
	public void stopPreview() {
		close();
	}

	public boolean getPreview() {
		return previewing;
	}

	/*
	 * ============================���ƣ� public void setCameraPreviewZoom()
	 * ���ܣ���������ʱ��Ԥ��ͼ������ش�С��Ĭ���ǻ�ȡ����ͷ��СԤ��ͼ�������width->���ؿ�� height->���ظ߶�
	 * ============================
	 */
	public void setCameraPreviewZoom(int width, int height) {

		Camera.Parameters p = mCamera.getParameters();

		p.setPreviewSize(width, height);

		mCamera.setParameters(p);
	}

	/*
	 * ============================���ƣ� public void setCameraTakeZoom()
	 * ���ܣ���������ʱ�Ļ�ȡͼ��Ĵ�С��Ĭ���ǻ�ȡ����ͷ���ͼ�������width->���ؿ�� height->���ظ߶�
	 * ============================
	 */
	public void setCameraTakeZoom(int width, int height) {

		Camera.Parameters p = mCamera.getParameters();

		p.setPictureSize(width, height);

		mCamera.setParameters(p);
	}

	/*
	 * ============================���ƣ� public void autoFocus()���ܣ�����ͷ�Զ��Խ�
	 * ============================
	 */
	public boolean autoFocus() {
		/*
		 * if(mCamera==null&&previewing==false) { return false; }
		 */
		if (mCamera == null) {

			return false;
		}

		mCamera.autoFocus(autoFocusCallback);

		LogMsg("auofocus:" + af);
		return af;
	}

	private AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success == true) {
				LogMsg("auto focus success!");

				af = true;

				// takePicture();

				camera.cancelAutoFocus();
			} else {
				LogMsg("auto focus fail!");

				af = false;
			}
		}

	};

	/*
	 * ============================���ƣ� public void touchFocus()���ܣ�����ͷ����ָ���Խ�
	 * ============================
	 */
	public boolean touchFocus(MotionEvent event) {

		Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f);
		Rect meteringRect = calculateTapArea(event.getRawX(), event.getRawY(),
				1.5f);

		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

		LogMsg("touchFocus entry");

		if (parameters.getMaxNumFocusAreas() > 0) {
			List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
			focusAreas.add(new Camera.Area(focusRect, tabSize));

			parameters.setFocusAreas(focusAreas);

			LogMsg("touchFocus 1");
		}

		if (parameters.getMaxNumMeteringAreas() > 0) {
			List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
			meteringAreas.add(new Camera.Area(meteringRect, tabSize));

			parameters.setMeteringAreas(meteringAreas);

			LogMsg("touchFocus 2");
		}

		LogMsg("touchFocus 3");
		mCamera.setParameters(parameters);
		mCamera.autoFocus(autoFocusCallback);

		LogMsg("touchFocus 4");

		return true;
	}

	private Camera.Size getResolution() {

		Camera.Parameters params = mCamera.getParameters();

		return params.getPreviewSize();
	}

	private Rect calculateTapArea(float x, float y, float coefficient) {
		float focusAreaSize = 300;
		int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

		int centerX = (int) (x / getResolution().width - tabSize);
		int centerY = (int) (y / getResolution().height - tabSize);

		int left = clamp(centerX - areaSize / 2, -tabSize, tabSize);
		int top = clamp(centerY - areaSize / 2, -tabSize, tabSize);

		RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);

		return new Rect(Math.round(rectF.left), Math.round(rectF.top),
				Math.round(rectF.right), Math.round(rectF.bottom));
	}

	private int clamp(int x, int min, int max) {
		if (x > max) {
			return max;
		}
		if (x < min) {
			return min;
		}
		return x;
	}

	/**
	 * ���ܣ�����
	 * 
	 * @param path
	 *            ͼƬ�洢·��
	 * @return ͼƬ�ֽ���
	 */
	public boolean takePicture(String path) {

		if (TextUtils.isEmpty(path)) {
			return false;
		}
		savePath = path;

		Log.i(TAG, "path:" + savePath);
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		if (mCamera == null) {
			mCamera = open(true);
			if (mCamera != null) {
				mCamera.startPreview();
				LogMsg("camera startPreview");
				return true;
			} else {
				return false;
			}
		} else {
			LogMsg("takePicture");
			// ���տ�ʼ
			mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
			return true;
		}

	}

	/*
	 * ============================���ƣ� private ShutterCallback shutterCallback
	 * ���ܣ����մ����Ļص������������������������� ============================
	 */
	private ShutterCallback shutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
		}

	};

	/*
	 * ============================���ƣ� private PictureCallback rawCallback
	 * ���ܣ����մ����Ļص�����������������ȡ��ʱRAWͼ������ ============================
	 */
	private PictureCallback rawCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
		}

	};

	/*
	 * ============================���ƣ� private PictureCallback jpegCallback
	 * ���ܣ����մ����Ļص�����������������ȡ�����ݽ���JPEG���� ============================
	 */
	private PictureCallback jpegCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			/*
			 * if(previewHodler==null) {
			 * Log.e(TAG,"jpegCallback:previewHodler=null"); return ; }
			 */
			LogMsg("=====jpegCallback=====");

			CameraPicture cameraPicture = new CameraPicture();
			try {

				LogMsg("DecodePicture...");
				Camera.Parameters p = mCamera.getParameters();

				picData = cameraPicture
						.DecodePicture(data, p.getPictureSize().width,
								p.getPictureSize().height, 90);
				LogMsg("DecodePicture length=" + picData.length);

				File file = FileOperate.open(savePath);

				FileOperate.write(file, picData);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (mRelativeLayout == null) {
					close();
				} else {
					mCamera.startPreview();
				}
				if (cameraManagerListener != null) {
					cameraManagerListener.takePictureCompletion(savePath);
				}
			}

		}

	};

	public void unLock() {
		mCamera.unlock();
	}

	public void reConnect() {
		try {
			mCamera.reconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setCameraDisplayOrientation() {
		if (mContext == null)
			return;
		try {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(0, cameraInfo);

			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			int rotation = wm.getDefaultDisplay().getRotation();
			int degrees = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}

			int result;
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (cameraInfo.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (cameraInfo.orientation - degrees + 360) % 360;
			}

			mCamera.setDisplayOrientation(result);
		} catch (Exception ex) {

		}
	}

	private void setCameraDisplayOrientation(int degrees) {
		if (mContext == null)
			return;
		try {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(0, cameraInfo);

			int result;
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (cameraInfo.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (cameraInfo.orientation - degrees + 360) % 360;
			}

			mCamera.setDisplayOrientation(result);
		} catch (Exception ex) {

		}
	}

	public void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width,
			int height) {

		System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
		System.arraycopy(yv12bytes, width * height + width * height / 4,
				i420bytes, width * height, width * height / 4);
		System.arraycopy(yv12bytes, width * height, i420bytes, width * height
				+ width * height / 4, width * height / 4);
	}

	public static byte[] YV12toYUV420PackedSemiPlanar(final byte[] input,
			final byte[] output, final int width, final int height) {
		/*
		 * COLOR_TI_FormatYUV420PackedSemiPlanar is NV12 We convert by putting
		 * the corresponding U and V bytes together (interleaved).
		 */
		final int frameSize = width * height;
		final int qFrameSize = frameSize / 4;
		System.arraycopy(input, 0, output, 0, frameSize); // Y
		for (int i = 0; i < qFrameSize; i++) {
			output[frameSize + i * 2] = input[frameSize + i + qFrameSize]; // Cb
																			// (U)
			output[frameSize + i * 2 + 1] = input[frameSize + i]; // Cr (V)
		}
		return output;
	}

	public static byte[] YV12toYUV420Planar(byte[] input, byte[] output,
			int width, int height) {
		/*
		 * COLOR_FormatYUV420Planar is I420 which is like YV12, but with U and V
		 * reversed. So we just have to reverse U and V.
		 */
		final int frameSize = width * height;
		final int qFrameSize = frameSize / 4;
		System.arraycopy(input, 0, output, 0, frameSize); // Y
		System.arraycopy(input, frameSize, output, frameSize + qFrameSize,
				qFrameSize); // Cr (V)
		System.arraycopy(input, frameSize + qFrameSize, output, frameSize,
				qFrameSize); // Cb (U)
		return output;
	}

	public static void LogMsg(String msg) {
		if (isDebug) {
			Log.i(TAG, msg);
		}
	}

	public interface CameraManagerListener {
		public abstract void takePictureCompletion(String path);
	}

	private void setCamLight(boolean onoff) {
		BoardCtrl boardCtrl = new BoardCtrl();
		boardCtrl.start();
		boardCtrl.setCamLight(onoff);
		boardCtrl.stop();
	}
}
