package com.advertisement.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.advertisement.activity.DisplayManager;
import com.advertisement.resource.ResFileParse;
import com.advertisement.resource.ResFileParse.FileParamer;
import com.advertisement.resource.ResPlaybill;
import com.advertisement.resource.ResPlaybill.PlayList;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemManager;
import com.common.Unit;

public class AdvImageView extends AdvViewBase implements AnimationListener {
	private static final String TAG = "AdvImageView";
	private Context mContext = null;
	private String mPlaybillPath = null;
	private ResPlaybill mResPlaybill = null;
	private ImageView mImageView = null;
	private ViewHandler mHandler = null;
	private TranslateAnimation mTranslateAnimation = null;
	private int mPlayId = 0;
	private int mPlayTotal = 0;
	private Timer mTimer = null;
	private TimerTask mTimerTask = null;
	private BitmapDrawable mBmpDrawable = null;
	private Bitmap mBitmap = null;

	public class FlyType {
		public static final int L2R = 0;// --从左到右
		public static final int R2L = 1;// --从右到左
		public static final int T2B = 3;// --从上到下
		public static final int B2T = 4;// --从下到上
		public static final int P2P = 5;// --翻页显示
	}

	public AdvImageView(Context context, DisplayManager displayManager) {
		super(context, displayManager);
		mContext = context;
		mHandler = new ViewHandler();
		setType(ConstData.FileTrans.PLAYBILL_TYPE_IMAGE);
	}

	@Override
	public void play(String playbillPath) {
		// TODO Auto-generated method stub

		mPlaybillPath = playbillPath;
		mResPlaybill = ResFileParse.parsePlaybillXmlFile(mPlaybillPath);

		mImageView = new ImageView(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// --自适应大小
		mImageView.setAdjustViewBounds(true);
		mImageView.setMaxHeight(getHeight());
		mImageView.setMaxWidth(getWidth());
		mImageView.setScaleType(ScaleType.FIT_XY);
		addView(mImageView, lp);
		mBitmap = null;
		startTask();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		stopTask();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		if (mPlayId == mPlayTotal) {
			mPlayId = 0;
		}
		setChange();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		stopTask();
		mPlaybillPath = null;
		mResPlaybill = null;
	}

	@Override
	public void destroy() {

	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return mHandler;
	}

	private String getImagePath(int id) {
		PlayList playList = null;
		if (mResPlaybill.mPlayList.size() > 0) {
			playList = mResPlaybill.mPlayList.get(id);
		} else if (mResPlaybill.mExtPlayList.size() > 0) {
			playList = mResPlaybill.mExtPlayList.get(id);
		}
		FileParamer filePar = new FileParamer(playList.mImageAd.url);

		return filePar.fileLocalPath;

	}

	private void startTask() {

		mPlayId = 0;
		mPlayTotal = mResPlaybill.mPlayList.size();
		if (mPlayTotal == 0) {
			mPlayTotal = mResPlaybill.mExtPlayList.size();
		}

		SystemManager.LOGI(TAG, "playbill path:" + mPlaybillPath);
		SystemManager.LOGI(TAG, "ext size:" + mResPlaybill.mExtPlayList.size());
		SystemManager.LOGI(TAG, "size:" + mResPlaybill.mPlayList.size());

		// --利用延时刷新图片
		mTimer = null;
		mTimer = new Timer();
		mTimerTask = null;
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// --到时更新下一张图片
				mHandler.sendEmptyMessage(1);
				cancel();
			}
		};
		if (mPlayTotal > 0) {
			mTimer.schedule(mTimerTask, 1000);
		}

	}

	private void stopTask() {
		/*
		 * if(mTranslateAnimation!=null){ mTranslateAnimation.cancel(); }
		 */
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	private void loadImage() {
		/*
		 * if(mBmpDrawable!=null){ //--图片资源回收
		 * if(!mBmpDrawable.getBitmap().isRecycled()){
		 * mBmpDrawable.getBitmap().recycle(); } mBmpDrawable=null;
		 * 
		 * System.gc(); } mBmpDrawable=new
		 * BitmapDrawable(getImagePath(mPlayId));
		 * mImageView.setImageDrawable(mBmpDrawable);
		 */
		if (mBitmap != null) {
			if (!mBitmap.isRecycled()) {
				mBitmap.recycle();
			}
			mBitmap = null;
			System.gc();
		}
		mBitmap = Unit.readBitmapFromFileDescriptor(getImagePath(mPlayId),
				super.getWidth(), super.getHeight());
		mImageView.setImageBitmap(mBitmap);
		mImageView.invalidate();

	}

	private class ViewHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (mResPlaybill == null) {
				return;
			}
			switch (msg.what) {
			case 1:
				if (mResPlaybill.mExtPlayList.size() > 0) {
					if (mPlayId == mPlayTotal) {

						// --当处于插入播放时，播放完成，此时需要结束，则不再继续添加播放任务
						/*
						 * Message msg1=new Message();
						 * msg1.what=ConstData.DisplayMsgType
						 * .MSG_DISPLAY_FINISH; msg1.obj=AdvImageView.this;
						 * sendMessageToManager(msg1);
						 */
						getDisplayManager().onViewFinish(AdvImageView.this);
						return;
					}
				}
				if (mPlayId == mPlayTotal) {

					// --判断是否退出
					if (getDisplayManager().onViewFinish(AdvImageView.this)) {
						return;
					}
					// --若只有一个图片，则不刷新
					if (mPlayTotal == 1) {
						mPlayId = 0;
						// setChange();
						return;
					}
					mPlayId = 0;
				}
				loadImage();
				setChange();
				mPlayId++;
				break;
			}
		}
	}

	private TranslateAnimation getTranslateAnimation(int type, int speed) {

		TranslateAnimation animation = null;
		switch (type) {
		case FlyType.L2R:
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			break;
		case FlyType.R2L:
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			break;
		case FlyType.T2B:
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					-1, Animation.RELATIVE_TO_SELF, 0);
			break;
		case FlyType.B2T:
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					1, Animation.RELATIVE_TO_SELF, 0);
			break;
		case FlyType.P2P:
			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					-1, Animation.RELATIVE_TO_SELF, 0);
			break;
		}
		animation.setDuration(speed);// --设置持续时间
		animation.setFillAfter(false);// --设置不填充
		animation.setAnimationListener(this);
		return animation;
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animation animation) {

		setChange();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	private void setChange() {

		SystemManager.LOGI(TAG,
				"setChange size:" + mResPlaybill.mPlayList.size());
		SystemManager.LOGI(TAG, "setChange ext size:"
				+ mResPlaybill.mExtPlayList.size());
		SystemManager.LOGI(TAG, "setChange playId:" + mPlayId);
		if (mPlayId >= mPlayTotal) {
			SystemManager.LOGI(TAG, "setChange over exit! ");
			return;
		}

		// --根据播放的持续时间来切换下一张图片
		mTimer = null;
		mTimer = new Timer();
		mTimerTask = null;
		mTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// --到时更新下一张图片
				mHandler.sendEmptyMessage(1);
				cancel();
			}
		};

		SystemManager.LOGI(TAG, "EXT SIZE:" + mResPlaybill.mExtPlayList.size());

		if (mResPlaybill.mExtPlayList.size() > 0) {
			SystemManager
					.LOGI(TAG,
							"EXT LENGTH:"
									+ mResPlaybill.mExtPlayList.get(mPlayId).mImageAd.length);
		}
		if (mResPlaybill.mPlayList.size() > 0) {
			mTimer.schedule(mTimerTask,
					1000 * mResPlaybill.mPlayList.get(mPlayId).mImageAd.length);
		} else if (mResPlaybill.mExtPlayList.size() > 0) {

			mTimer.schedule(
					mTimerTask,
					1000 * mResPlaybill.mExtPlayList.get(mPlayId).mImageAd.length);
		}
	}

}
