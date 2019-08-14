package com.advertisement.ui;

import java.util.ArrayList;
import java.util.Timer;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceView;

import com.advertisement.activity.DisplayManager;
import com.advertisement.resource.ResFileParse;
import com.advertisement.resource.ResFileParse.FileParamer;
import com.advertisement.resource.ResPlaybill;
import com.advertisement.resource.ResPlaybill.PlayList;
import com.advertisement.resource.ResPlaybill.PlayList.VideoAd;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemManager;
import com.media.VideoPlayer;
import com.media.VideoPlayer.VideoPlayerListener;

public class AdvVideoView extends AdvViewBase implements VideoPlayerListener {

	private String mPlaybillPath = null;
	private ResPlaybill mResPlaybill = null;
	private SurfaceView mSurfaceView = null;
	private Context mContext = null;
	private VideoPlayer mVideoPlayer = null;
	private int mPlayId = 0;
	private ArrayList<PlayList> mPlayList = null;
	private int status = 0;
	private Timer mTimer = null;

	public AdvVideoView(Context context, DisplayManager displayManager) {
		super(context, displayManager);
		mContext = context;
		setType(ConstData.FileTrans.PLAYBILL_TYPE_VIDEO);
	}

	@Override
	public void play(String playbillPath) {

		mPlaybillPath = playbillPath;
		mResPlaybill = ResFileParse.parsePlaybillXmlFile(mPlaybillPath);

		if (mResPlaybill.mPlayList.size() > 0) {

			mPlayList = mResPlaybill.mPlayList;

		} else if (mResPlaybill.mExtPlayList.size() > 0) {
			mPlayList = mResPlaybill.mExtPlayList;

		} else {
			mPlayList = null;

			return;
		}

		mPlayId = 0;

		mSurfaceView = new SurfaceView(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		addView(mSurfaceView, lp);

		mVideoPlayer = new VideoPlayer(mSurfaceView, true, false, this);

	}

	@Override
	public void pause() {
		if (mVideoPlayer != null) {
			mVideoPlayer.pause();
		}
	}

	@Override
	public void resume() {
		if (mVideoPlayer != null) {
			// --如果Surfaceview没有销毁，则按正常的暂停，唤醒启动流程
			if (status == 0) {
				mVideoPlayer.resume();
			} else {
				// --如果Surfaceview销毁之后，此时我们唤醒需要重新加载播放内容
				playAdvVideo();
				status = 0;
			}
		}

	}

	@Override
	public void stop() {
		if (mVideoPlayer != null) {
			mVideoPlayer.pause();
			mVideoPlayer.stop();
			SystemManager.LOGI("AdvVideoView", "STOP OK!");
		}
	}

	@Override
	public void destroy() {
		mVideoPlayer = null;
		mSurfaceView = null;
	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPlayStart() {

		if (status == 0) {
			playAdvVideo();
		}

	}

	@Override
	public void onPlayComplete() {

		if (mPlayId == mPlayList.size()) {
			/*
			 * Message msg1=new Message();
			 * msg1.what=ConstData.DisplayMsgType.MSG_DISPLAY_FINISH;
			 * msg1.obj=AdvVideoView.this; sendMessageToManager(msg1);
			 */
			if (getDisplayManager().onViewFinish(AdvVideoView.this)) {
				// --当广告模版已经播放完成将不继续播放，否则继续播放
				SystemManager.LOGD("AdvVideoView", "VIDEO STOP!");
				return;
			}
		}
		SystemManager.LOGD("AdvVideoView", "VIDEO PLAY NEXT!");
		playAdvVideo();

	}

	@Override
	public void onError() {

	}

	@Override
	public void onSurfaceViewDestroy() {
		// TODO Auto-generated method stub
		// --当切换到其他Activity时会导致Surfaceview销毁，故将置位状态
		status = 1;
	}

	public void playAdvVideo() {

		VideoAd adv = null;

		if (mPlayId == mPlayList.size()) {
			mPlayId = 0;
		}

		adv = mPlayList.get(mPlayId).mVideoAd;

		if (adv.url != null) {
			mVideoPlayer.start(new FileParamer(adv.url).fileLocalPath);
		}

		mPlayId++;

	}

}
