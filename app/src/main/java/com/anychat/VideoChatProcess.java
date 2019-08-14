package com.anychat;

import java.util.Timer;
import java.util.TimerTask;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatTransDataEvent;

import android.content.Context;

public class VideoChatProcess{

	private Context mContext=null;
	private AnyChatCoreSDK mAnyChat=null;
	private VideoChatMessage videoChatMsg=null;
	private VideoChatSession mChatSession=null;
	private Timer mTimer=null;
	private int mRemoteId=-1;
	public static final int REQUEST_TIMEOUT=30000;
	public static final int VIDEOCHAT_PROCESS_IDLE=0;         //--空闲状态
	public static final int VIDEOCHAT_PROCESS_CALL_OUT=1;  //--呼出
	public static final int VIDEOCHAT_PROCESS_CALL_IN=2;   //--呼入
	public static final int VIDEOCHAT_PROCESS_SESSION=3;      //--会话中
	
	private int mStatus=VIDEOCHAT_PROCESS_IDLE;
		

	public VideoChatProcess(AnyChatCoreSDK anychat,Context context,VideoChatSession chatSession) {
		mContext=context;
		mAnyChat=anychat;
		
		mChatSession=chatSession;
		
		mStatus=VIDEOCHAT_PROCESS_IDLE;
		
	}
	
	public void setRemoteUsrId(int userId){
		mRemoteId=userId;
	}
	
	public void CallOut(){
		VideoChatCore.DEBUG("== requestCall ==");
		if(mChatSession!=null){
			mStatus=VIDEOCHAT_PROCESS_CALL_OUT;
			mChatSession.request(mRemoteId);
		}
	}
	
	public void CallCancel(){
		if(mChatSession!=null){
			mStatus=VIDEOCHAT_PROCESS_IDLE;
			mChatSession.cancel(mRemoteId);
		}
	}
	
	public void CallSession(){
		if(mChatSession!=null){
			mStatus=VIDEOCHAT_PROCESS_SESSION;
		}
	}
	
	public void CallIn(){
		if(mChatSession!=null){
			mStatus=VIDEOCHAT_PROCESS_CALL_IN;
		}
	}
	
	public void reportSession(boolean isStart){

	}
	
	public int getRemoteUserId(){
		return mRemoteId;
	}
	
	public int getStatus(){
		return mStatus;
	}
	

}
