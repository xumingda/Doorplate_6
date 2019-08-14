package com.advertisement.ui;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceView;

import com.advertisement.activity.DisplayManager;
import com.advertisement.resource.ResFileParse;
import com.advertisement.resource.ResPlaybill;
import com.advertisement.resource.ResPlaybill.PlayList;
import com.advertisement.resource.ResPlaybill.PlayList.LabelAd;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemManager;
import com.media.TextMove;

public class AdvLabelView extends AdvViewBase{

	private String mPlaybillPath=null;
	private ResPlaybill mResPlaybill=null;
	private SurfaceView mSurfaceView=null;
	private Context mContext=null;
	private TextMove mTextMove=null;
	private static final String TAG="AdvLabelView";

	
	public AdvLabelView(Context context, DisplayManager displayManager) {
		super(context, displayManager);
		mContext=context;
		setType(ConstData.FileTrans.PLAYBILL_TYPE_LABEL);
	}

	int FontSize;
	int Speed;
	String Fly;
	String BgColor;
	String Color;
	public void setOpt(int fontSize,String fly,int speed,String bgColor,String color){
		this.FontSize=fontSize;
		this.Speed=speed;
		this.Fly=fly;
		this.BgColor=bgColor;
		this.Color=color;
	}
	
	@Override
	public void play(String playbillPath) {
		mPlaybillPath=playbillPath;
		mResPlaybill=ResFileParse.parsePlaybillXmlFile(mPlaybillPath);
		
		mSurfaceView=new SurfaceView(mContext);
		LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		addView(mSurfaceView,lp);
		
		mTextMove=new TextMove();
		
		LabelAd adv=getAdvLabel(0);
		if(adv!=null){
			SystemManager.LOGD(TAG,"url:"+adv.url);
			/*
			SystemManager.LOGD(TAG, "fontSize:"+adv.sytle.fontSize);
			SystemManager.LOGD(TAG, "fontName:"+adv.sytle.fontName);
			SystemManager.LOGD(TAG, "fly:"+adv.sytle.fly);
			SystemManager.LOGD(TAG, "speed:"+adv.sytle.scroll);
			SystemManager.LOGD(TAG, "bgcolor:"+adv.sytle.bgcolor);
			SystemManager.LOGD(TAG, "fontColor:"+adv.sytle.color);
			
			
			if(adv.sytle.fly.equals("R2L")){
				mTextMove.setOrientation(TextMove.MOVE_LEFT);
			}else{
				mTextMove.setOrientation(TextMove.MOVE_RIGHT);
			}
			*/
			
			if(Fly.equals("R2L")){
				mTextMove.setOrientation(TextMove.MOVE_LEFT);
			}else{
				mTextMove.setOrientation(TextMove.MOVE_RIGHT);
			}
			mTextMove.setContent(adv.url);
			mTextMove.setFontSize(FontSize);
			mTextMove.setBgColor(BgColor);
			mTextMove.setFontColor(Color);
			mTextMove.setSpeed(Speed);
			mTextMove.setMove(true);
			mTextMove.setLoop(true);
			mTextMove.setSurfaceView(mSurfaceView);
		}
		
	}
	
	private LabelAd getAdvLabel(int id){
		PlayList playList=null;
		if(mResPlaybill.mPlayList.size()>0){
			playList=mResPlaybill.mPlayList.get(id);
		}else if(mResPlaybill.mExtPlayList.size()>0){
			playList=mResPlaybill.mExtPlayList.get(id);
		}
		
		return playList.mLabelAd;
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		if(mTextMove!=null){
			mTextMove.pause();
		}
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		if(mTextMove!=null){
			mTextMove.resume();
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if(mTextMove!=null){
			mTextMove.stop();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		if(mTextMove!=null){
			mTextMove.stop();
			removeAllViews();
		}
	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
