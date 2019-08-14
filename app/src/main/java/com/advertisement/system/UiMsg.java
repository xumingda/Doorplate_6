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
	/* Activity->Service ������Ϣ����Ϣ������ */
	private static Messenger msgToService=null;  
	/* Service->Activity ������Ϣ����Ϣ������ */
	private static Messenger msgFromService=null;
	/* Activity ����service��Ϣ�Ĵ���ӿ� */
	private static Handler handler=null;
	
	private static Message lastMsg=null;
	
	private void showLog(String context){
		
		Log.i(TAG,context);
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		showLog("onServiceConnected :"+name.getClassName());
		serviceBinder=service;
		
		/* ���� ����UI->��̨���� ������Ϣ������ */
		msgToService=new Messenger(serviceBinder);
		
		/* ���� ��̨����->����UI ������Ϣ������ */
		msgFromService=new Messenger(handler);
		
		showLog("msgToService:"+msgToService);
		
		showLog("msgFromService:"+msgFromService);
		/* ��������Ϣ���������͸�service */
		sendMsger();
		
		/* ����δ�󶨳ɹ�ʱ���͵���Ϣ */
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
	 * �󶨺�̨����
	 * @param handler->activity ���ڽ���Message�Ľӿ�
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
	 * ����󶨺�̨����
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
	 * Activity �� Service������Ϣ
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
