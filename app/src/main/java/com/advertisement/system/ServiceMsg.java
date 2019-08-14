package com.advertisement.system;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ServiceMsg {

	private  Messenger msgFromActivity=null;
	private  Messenger msgToActivity=null;
	private static final String TAG="ServiceMsg";
	private ServiceMsgListener mListener=null;
	private Context mContext=null;
	
	private void showLog(String content){
		
		Log.i(TAG,content);
	}
	
	public interface ServiceMsgListener{
		public abstract void OnHandleMessage(Message msg);
	}
	
	public ServiceMsg(Context context,ServiceMsgListener listener){
		mContext=context;
		mListener=listener;
	}
	
	public IBinder createBinder(){
		showLog("=== onBind ===");
		
		/**
		 * 创建接收ACTIVITY的消息管理器
		 **/
		msgFromActivity=new Messenger(mHandler);
		
		return msgFromActivity.getBinder();
	}
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			if(msg.what==0){
				msgToActivity=msg.replyTo;
			}else{
				if(mListener!=null){
					mListener.OnHandleMessage(msg);
				}
			}
			
		};
	};
	
	public void sendMessage(Message msg){
		if(msgToActivity!=null){
			try {
				msgToActivity.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
