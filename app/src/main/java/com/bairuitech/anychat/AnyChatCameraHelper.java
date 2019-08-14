package com.bairuitech.anychat;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class AnyChatCameraHelper implements SurfaceHolder.Callback {
	private final static String TAG = "ANYCHAT";
	private Camera mCamera = null;
	private boolean bIfPreview = false;
	private boolean bNeedCapture = false;
	private int iCurrentCameraId = 0;
	private SurfaceHolder currentHolder = null;
	private int mVideoPixfmt = -1;
	private final int iCaptureBuffers = 3;

	private Context mContext = null;
	private int mCameraOrientation = 0;
	private int mCameraFacing = 0;
	private int mDeviceOrientation = 0;

	public final int CAMERA_FACING_BACK = 0;
	public final int CAMERA_FACING_FRONT = 1;

	// 鐠佸墽鐤嗛悥鍓佺崶閸欙絽褰為弻锟�?
	public void SetContext(Context ctx) {
		mContext = ctx;
	}

	private Size s;

	@SuppressLint("NewApi")
	private void initCamera() {
		if (null == mCamera)
			return;
		try {
			if (bIfPreview) {
				mCamera.stopPreview();// stopCamera();
				mCamera.setPreviewCallbackWithBuffer(null);
			}
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(iCurrentCameraId, cameraInfo);
			mCameraOrientation = cameraInfo.orientation;
			mCameraFacing = cameraInfo.facing;
			mDeviceOrientation = getDeviceOrientation();
			Log.i(TAG, "allocate: device orientation=" + mDeviceOrientation
					+ ", camera orientation=" + mCameraOrientation
					+ ", facing=" + mCameraFacing);

			// setCameraDisplayOrientation();

			/* Camera Service settings */
			Camera.Parameters parameters = mCamera.getParameters();

			List<Size> previewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();

			int iSettingsWidth = AnyChatCoreSDK
					.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL);
			int iSettingsHeight = AnyChatCoreSDK
					.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL);
			boolean bSetPreviewSize = false;
			for (int i = 0; i < previewSizes.size(); i++) {
				s = previewSizes.get(i);
				AnyChatCoreSDK.SetSDKOptionString(
						AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
						"Camera Preview size: " + s.width + " x " + s.height);
				if (s.width == iSettingsWidth && s.height == iSettingsHeight) {
					bSetPreviewSize = true;
					parameters.setPreviewSize(iSettingsWidth, iSettingsHeight);
					break;
				}
			}
			if (!bSetPreviewSize)
				parameters.setPreviewSize(320, 240);

			List<int[]> fpsRange = parameters.getSupportedPreviewFpsRange();
			for (int i = 0; i < fpsRange.size(); i++) {
				int[] r = fpsRange.get(i);
				AnyChatCoreSDK.SetSDKOptionString(
						AnyChatDefine.BRAC_SO_CORESDK_WRITELOG,
						"Camera FrameRate: " + r[0] + " , " + r[1]);
				if (r[0] >= 25000 && r[1] >= 25000) {
					parameters.setPreviewFpsRange(r[0], r[1]);
					break;
				}
			}

			// 鐠佸墽鐤嗙憴鍡涱暥閺佺増宓侀弽鐓庣础
			parameters.setPreviewFormat(ImageFormat.NV21);
			// 閸欏倹鏆熺拋鍓х枂閻㈢喐鏅�?
			try {
				mCamera.setParameters(parameters);
			} catch (Exception e) {

			}
			Camera.Size captureSize = mCamera.getParameters().getPreviewSize();
			int bufSize = captureSize.width * captureSize.height
					* ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
			for (int i = 0; i < iCaptureBuffers; i++) {
				mCamera.addCallbackBuffer(new byte[bufSize]);
			}

			// setCameraDisplayOrientation(180);

			// 鐠佸墽鐤嗙憴鍡涱暥鏉堟挸鍤崶鐐剁殶閸戣姤鏆熼敍�?勶拷姘崇箖AnyChat閻ㄥ嫬顦婚柈銊潒妫版垼绶崗銉�?复閸欙絼绱堕崗顧噉yChat閸愬懏鐗虫潻娑滎攽婢跺嫮鎮�
			mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					if (data.length != 0 && bNeedCapture) {
						// try {
						// byte[] tmp = rotateYUV420Degree180(data, s.width,
						// s.height);
						// AnyChatCoreSDK.InputVideoData(tmp, tmp.length, 0);
						AnyChatCoreSDK.InputVideoData(data, data.length, 0);
						// } catch (Exception e) {
						//
						// }
					}
					if (mCamera != null) {
						mCamera.addCallbackBuffer(data);
					}
				}
			});
			mCamera.startPreview(); // 閹垫挸绱戞０鍕潔閻㈠娼�
			bIfPreview = true;

			// 閼惧嘲褰囩拋鍓х枂閸氬海娈戦惄绋垮彠閸欏�?�鏆�?
			if (mCamera.getParameters().getPreviewFormat() == ImageFormat.NV21)
				mVideoPixfmt = AnyChatDefine.BRAC_PIX_FMT_NV21;
			else if (mCamera.getParameters().getPreviewFormat() == ImageFormat.YV12)
				mVideoPixfmt = AnyChatDefine.BRAC_PIX_FMT_YV12;
			else if (mCamera.getParameters().getPreviewFormat() == ImageFormat.NV16)
				mVideoPixfmt = AnyChatDefine.BRAC_PIX_FMT_NV16;
			else if (mCamera.getParameters().getPreviewFormat() == ImageFormat.YUY2)
				mVideoPixfmt = AnyChatDefine.BRAC_PIX_FMT_YUY2;
			else if (mCamera.getParameters().getPreviewFormat() == ImageFormat.RGB_565)
				mVideoPixfmt = AnyChatDefine.BRAC_PIX_FMT_RGB565;
			else
				Log.e(TAG, "unknow camera privew format:"
						+ mCamera.getParameters().getPreviewFormat());

			Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_CORESDK_EXTVIDEOINPUT, 1);

			int iCurPreviewRange[] = new int[2];
			parameters.getPreviewFpsRange(iCurPreviewRange);
			AnyChatCoreSDK.SetInputVideoFormat(mVideoPixfmt, previewSize.width,
					previewSize.height, iCurPreviewRange[1] / 1000, 0);
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_CAMERAFACE,
					cameraInfo.facing);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] rotateYUV420Degree180(byte[] data, int imageWidth,
			int imageHeight) {

		byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];

		int i = 0;
		int count = 0;

		for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
			yuv[count] = data[i];
			count++;
		}

		i = imageWidth * imageHeight * 3 / 2 - 1;
		for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
				* imageHeight; i -= 2) {
			yuv[count++] = data[i - 1];
			yuv[count++] = data[i];
		}
		return yuv;
	}

	private byte[] rotateYUV420Degree90(byte[] data, int imageWidth,
			int imageHeight) {
		byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
		// 旋转Y
		int i = 0;
		for (int x = 0; x < imageWidth; x++) {
			for (int y = imageHeight - 1; y >= 0; y--) {
				yuv[i] = data[y * imageWidth + x];
				i++;
			}

		}
		// 旋转U和V
		i = imageWidth * imageHeight * 3 / 2 - 1;
		for (int x = imageWidth - 1; x > 0; x = x - 2) {
			for (int y = 0; y < imageHeight / 2; y++) {
				yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
				i--;
				yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
						+ (x - 1)];
				i--;
			}
		}
		return yuv;
	}

	// 閹藉嫬鍎氭径鎾櫚闂嗗棙甯堕崚锟�?
	public void CaptureControl(boolean bCapture) {
		bNeedCapture = bCapture;
		if (bNeedCapture && mVideoPixfmt != -1) {
			try {
				Camera.Size previewSize = mCamera.getParameters()
						.getPreviewSize();
				AnyChatCoreSDK.SetSDKOptionInt(
						AnyChatDefine.BRAC_SO_CORESDK_EXTVIDEOINPUT, 1);
				AnyChatCoreSDK.SetInputVideoFormat(mVideoPixfmt,
						previewSize.width, previewSize.height, mCamera
								.getParameters().getPreviewFrameRate(), 0);
				AnyChatCoreSDK.SetSDKOptionInt(
						AnyChatDefine.BRAC_SO_LOCALVIDEO_CAMERAFACE,
						mCameraFacing);
			} catch (Exception ex) {

			}
		} else {
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_CORESDK_EXTVIDEOINPUT, 0);
		}
	}

	// 閸忔娊妫撮幗鍕剼婢讹拷
	public void CloseCamera() {
		try {
			if (null != mCamera) {
				mCamera.stopPreview();
				mCamera.setPreviewCallbackWithBuffer(null);
				bIfPreview = false;
				mVideoPixfmt = -1;
				mCamera.release();
				mCamera = null;
			}
		} catch (Exception ex) {

		}
	}

	// 閼惧嘲褰囩化鑽ょ埠娑擃厽鎲氶崓蹇撱仈閻ㄥ嫭鏆熼柌锟�
	@SuppressLint("NewApi")
	public int GetCameraNumber() {
		try {
			return Camera.getNumberOfCameras();
		} catch (Exception ex) {
			return 0;
		}
	}

	// 閼奉亜濮╃�鍦�?
	public void CameraAutoFocus() {
		if (mCamera == null || !bIfPreview)
			return;
		try {
			mCamera.autoFocus(null);
		} catch (Exception ex) {

		}
	}

	// 閸掑洦宕查幗鍕剼婢讹拷
	@SuppressLint("NewApi")
	public void SwitchCamera() {
		try {
			if (Camera.getNumberOfCameras() == 1 || currentHolder == null)
				return;
			iCurrentCameraId = (iCurrentCameraId == 0) ? 1 : 0;
			if (null != mCamera) {
				mCamera.stopPreview();
				mCamera.setPreviewCallbackWithBuffer(null);
				bIfPreview = false;
				mVideoPixfmt = -1;
				mCamera.release();
				mCamera = null;
			}

			mCamera = Camera.open(iCurrentCameraId);
			mCamera.setPreviewDisplay(currentHolder);
			initCamera();
		} catch (Exception ex) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
				mVideoPixfmt = -1;
			}
		}
	}

	// 閺嶈宓�?幗鍕剼婢跺娈戦弬鐟版倻闁瀚ㄩ幗鍕剼婢惰揪绱欓崜宥囩枂閵嗕礁鎮楃純顕嗙礆
	@SuppressLint("NewApi")
	public void SelectVideoCapture(int facing) {
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == facing) {
				iCurrentCameraId = i;
				break;
			}
		}
	}

	// 閺嶈宓�?幗鍕剼婢跺娈戞惔蹇撳娇闁瀚ㄩ幗鍕剼婢惰揪绱�? -
	// GetCameraNumber()閿涳�?
	@SuppressLint("NewApi")
	public void SelectCamera(int iCameraId) {
		try {
			if (Camera.getNumberOfCameras() <= iCameraId
					|| currentHolder == null)
				return;
			if (null != mCamera && iCurrentCameraId == iCameraId)
				return;
			iCurrentCameraId = iCameraId;
			if (null != mCamera) {
				mCamera.stopPreview();
				mCamera.setPreviewCallbackWithBuffer(null);
				bIfPreview = false;
				mVideoPixfmt = -1;
				mCamera.release();
				mCamera = null;
			}

			mCamera = Camera.open(iCameraId);
			mCamera.setPreviewDisplay(currentHolder);
			initCamera();
		} catch (Exception ex) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
				mVideoPixfmt = -1;
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera = Camera.open(iCurrentCameraId);
			currentHolder = holder;
			mCamera.setPreviewDisplay(holder);// set the surface to be used for
												// live preview
			initCamera();
		} catch (Exception ex) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
				mVideoPixfmt = -1;
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (null != mCamera) {
			try {
				mCamera.stopPreview();
				mCamera.setPreviewCallbackWithBuffer(null);
				bIfPreview = false;
				mCamera.release();
				mCamera = null;
			} catch (Exception ex) {
				mCamera = null;
				bIfPreview = false;
			}
		}
		currentHolder = null;
		mVideoPixfmt = -1;
	}

	@SuppressLint("NewApi")
	public void start() {
		try {
			mCamera = Camera.open(iCurrentCameraId);
			currentHolder = null;
			mCamera.setPreviewDisplay(null);// set the surface to be used for
											// live preview

			Log.i(TAG, "=== mCamera: " + mCamera.toString());

			initCamera();
		} catch (Exception ex) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
				mVideoPixfmt = -1;
			}
		}
	}

	@SuppressLint("NewApi")
	public void start(SurfaceView surfaceview) {
		try {
			mCamera = Camera.open(iCurrentCameraId);
			currentHolder = surfaceview.getHolder();
			mCamera.setPreviewDisplay(surfaceview.getHolder());// set the
																// surface to be
																// used for live
																// preview

			Log.i(TAG, "=== mCamera: " + mCamera.toString());

			initCamera();
		} catch (Exception ex) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
				mVideoPixfmt = -1;
			}
		}
	}

	public void stop() {
		if (null != mCamera) {
			try {
				mCamera.stopPreview();
				// mCamera.setPreviewCallbackWithBuffer(null);
				bIfPreview = false;
				mCamera.release();
				mCamera = null;
			} catch (Exception ex) {
				mCamera = null;
				bIfPreview = false;
			}
		}
		currentHolder = null;
		mVideoPixfmt = -1;
	}

	private int getDeviceOrientation() {
		int orientation = 0;
		if (mContext != null) {
			WindowManager wm = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			// Log.i(TAG, "wm.getDefaultDisplay().getRotation():" +
			// wm.getDefaultDisplay().getRotation());
			switch (wm.getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_90:
				orientation = 90;
				break;
			case Surface.ROTATION_180:
				orientation = 180;
				break;
			case Surface.ROTATION_270:
				orientation = 270;
				break;
			case Surface.ROTATION_0:
			default:
				orientation = 0;
				break;
			}
		}
		return orientation;
	}

	@SuppressLint("NewApi")
	public void setCameraDisplayOrientation() {
		if (mContext == null)
			return;
		try {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(iCurrentCameraId, cameraInfo);

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

	@SuppressLint("NewApi")
	public void setCameraDisplayOrientation(int degrees) {
		if (mContext == null)
			return;
		try {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(iCurrentCameraId, cameraInfo);

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

}