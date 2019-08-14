package com.advertisement.ui;

import com.advertisement.activity.DisplayManager;
import com.advertisement.resource.ResTheme.Form.BaseForm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.RelativeLayout;

public abstract class AdvViewBase extends RelativeLayout{

	public DisplayManager mDisplayManager=null;
	public BaseForm mAdvForm=null;
	public Handler managerHandler=null;
	public int type=0;
	public AdvViewBase(Context context,DisplayManager displayManager) {
		super(context);
		mDisplayManager=displayManager;
		
	}
	
	protected void setType(int type){
		this.type=type;
	}

	public void setManagerHandler(Handler managerHandler){
		this.managerHandler=managerHandler;
	}
	
	public DisplayManager getDisplayManager(){
		return mDisplayManager;
	}
	
	public void setBaseForm(BaseForm form){
		mAdvForm=form;
	}
	
	public abstract void play(String playbillPath);
	public abstract void pause();
	public abstract void resume();
	public abstract void stop();
	public abstract void destroy();
	public abstract Handler getHandler();
	
	public void sendMessageToManager(Message msg){
		if(managerHandler!=null){
			managerHandler.sendMessage(msg);
		}
	}
}
