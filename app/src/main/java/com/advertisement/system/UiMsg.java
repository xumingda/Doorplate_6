package com.advertisement.system;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class UiMsg implements ServiceConnection{

	private static Context mContext;
	private static final String TAG="UiMsg";
	private static IBinder serviceBinder=null;
	/* Activity->Service 发送消息的消息管理器 */
	private static Messenger msgToService=null;  
	/* Service->Activity 发送消息的消息管理器 */
	private static Messenger msgFromService=null;
	/* Activity 接收service消息的处理接口 */
	private static Handler handler=null;
	
	private static Message lastMsg=null;
	
	private void showLog(String context){
		
		Log.i(TAG,context);
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		showLog("onServiceConnected :"+name.getClassName());
		serviceBinder=service;
		
		/* 创建 本地UI->后台服务 发送消息管理器 */
		msgToService=new Messenger(serviceBinder);
		
		/* 创建 后台服务->本地UI 发送消息管理器 */
		msgFromService=new Messenger(handler);
		
		showLog("msgToService:"+msgToService);
		
		showLog("msgFromService:"+msgFromService);
		/* 将本地消息管理器发送给service */
		sendMsger();
		
		/* 发送未绑定成功时发送的消息 */
		if(lastMsg!=null){
			
			sendMessage(lastMsg);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		showLog("onServiceDisconnected :"+name.getClassName());
		serviceBinder=null;
	}

	public UiMsg(Context context){
		mContext=context;
	}
	
	/**
	 * 绑定后台服务
	 * @param handler->activity 用于接收Message的接口
	 **/
	public void bindBgService(Class cls,Handler handler){
		
		this.handler=handler;
		
		Intent intent = new Intent().setClass( mContext , cls );  
		mContext.bindService(intent, this,  Context.BIND_AUTO_CREATE);
	}
	
	public void bindBgService(Intent intent,Handler handler){
		
		this.handler=handler;
		
		mContext.bindService(intent, this,  Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * 解除绑定后台服务
	 **/
	public void unbindBgService(){
		
		mContext.unbindService(this);
	}
	
	private void sendMsger() {  
	    Message msg = Message.obtain(null, 0);
	    msg.replyTo = msgFromService;  
	    try {  
	        msgToService.send(msg);  
	    } catch (RemoteException e) {  
	        e.printStackTrace();  
	    }  
	} 
	
	/**
	 * Activity 向 Service发送消息
	 **/
	public void sendMessage(Message msg){
		
		try {  
			if(msgToService!=null){
				msgToService.send(msg);
			}else{
				
				lastMsg=msg;
			}
	    } catch (RemoteException e) {  
	        e.printStackTrace();  
	    } 
	}
}
