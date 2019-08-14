package com.advertisement.ui;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;

import com.advertisement.activity.DisplayManager;
import com.advertisement.resource.ResTheme.Form.Clock;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemManager;
import com.yy.doorplate.R;

public class AdvClockView extends AdvViewBase{

	private static final String TAG="AdvClockView";
	private Clock mClockForm=null;
	private Context mContext=null;
	private TextView mTextView=null;
	private static final int TYPE_DATE=1;
	private static final int TYPE_WEEK=2;
	private static final int TYPE_TIME=3;
	private static final int TYPE_TIME_SEC=4;
	private int Type=0;
	private Timer mTimer=null;

	public AdvClockView(Context context, DisplayManager displayManager) {
		super(context, displayManager);
		mContext=context;
		setType(ConstData.FileTrans.PLAYBILL_TYPE_CLOCK);
	}

	@Override
	public void play(String playbillPath) {
		
	}
	
	@SuppressLint("NewApi") public void play(){
	
		mClockForm=(Clock)mAdvForm;
		if(mClockForm==null){
			return;
		}
		mTextView=new TextView(mContext); 
		LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		addView(mTextView,lp);
		
		SystemManager.LOGD(TAG, "fontName:"+mClockForm.fontName);
		SystemManager.LOGD(TAG, "fontSize:"+mClockForm.fontSize);
		SystemManager.LOGD(TAG,"timeFormat:"+mClockForm.timeFormat);
		SystemManager.LOGD(TAG,"transp:"+mClockForm.transp);
		
		if(mClockForm.timeFormat.equals("%Y-%m-%d")){
			Type=TYPE_DATE;
		}
		if(mClockForm.timeFormat.equals("%X")){
			Type=TYPE_WEEK;
		}
		if(mClockForm.timeFormat.equals("%H:%M:%S")){
			Type=TYPE_TIME_SEC;
		}
		if(mClockForm.timeFormat.equals("%H:%M")){
			Type=TYPE_TIME;
		}
		
		mTextView.setTextSize(mClockForm.fontSize);
		mTextView.setTextColor(Color.parseColor(mClockForm.color));
		mTextView.setGravity(Gravity.CENTER);
		
		show();
		
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		
		mTimer=new Timer();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		}, 100, 100);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
	}

	@Override
	public void resume() {
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		
		mTimer=new Timer();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		}, 100, 100);
	}

	@Override
	public void stop() {
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	public void show(){
		String text="";
		Calendar calendar = Calendar.getInstance(); 
		switch(Type){
		case TYPE_DATE:
			
			text=calendar.get(Calendar.YEAR)+mContext.getResources().getString(R.string.year)+
					(calendar.get(Calendar.MONTH)+1)+mContext.getResources().getString(R.string.month)+
					calendar.get(Calendar.DAY_OF_MONTH)+mContext.getResources().getString(R.string.day);
			break;
		case TYPE_WEEK:
			if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				text=mContext.getResources().getString(R.string.sunday);
			}
			if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
				text=mContext.getResources().getString(R.string.monday);
			}
			if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
				text=mContext.getResources().getString(R.string.tuesday);
			}
			if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
				text=mContext.getResources().getString(R.string.wednesday);
			}
			if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
				text=mContext.getResources().getString(R.string.thursday);
			}
			if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
				text=mContext.getResources().getString(R.string.friday);
			}
			if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
				text=mContext.getResources().getString(R.string.saturday);
			}
			break;
		case TYPE_TIME:
			if(calendar.get(Calendar.HOUR_OF_DAY)<10){
				text+="0";
			}
			text+=calendar.get(Calendar.HOUR_OF_DAY)+":";
			
			if(calendar.get(Calendar.MINUTE)<10){
				text+="0";
			}
			text+=calendar.get(Calendar.MINUTE);
			break;
		case TYPE_TIME_SEC:
			if(calendar.get(Calendar.HOUR_OF_DAY)<10){
				text+="0";
			}
			text+=calendar.get(Calendar.HOUR_OF_DAY)+":";
			
			if(calendar.get(Calendar.MINUTE)<10){
				text+="0";
			}
			text+=calendar.get(Calendar.MINUTE)+":";
			if(calendar.get(Calendar.SECOND)<10){
				text+="0";
			}
			text+=calendar.get(Calendar.SECOND);
			
			break;
		}
	
		mTextView.setText(text);
	}
	
	public Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			show();
		};
	};
}
