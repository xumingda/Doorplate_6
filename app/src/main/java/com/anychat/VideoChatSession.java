package com.anychat;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatVideoCallEvent;

public class VideoChatSession implements AnyChatVideoCallEvent{

	private Context mContext=null;
	private AnyChatCoreSDK mAnyChat=null;
	public static final int VIDEOCHAT_SESSION_IDLE=0;
	public static final int VIDEOCHAT_SESSION_REQUEST=1;
	public static final int VIDEOCHAT_SESSION_RUNNING=2;
	public static final int VIDEOCHAT_SESSION_REPLY=3;
	private static final int SESSION_TIMEOUT=30000;
	private Timer mTimer=null;
	private int mStatus=0;
	private int mRemoteId=-1;
	public interface AnyChatSessionCallback{
		public abstract void onVideoChatSessionStart(int remoteId,int roomId);
		public abstract void onVideoChatSessionFinish(int remoteId);
		public abstract void onVideoChatSessionReply(int remoteId,boolean isSuccessful);
		public abstract void onVideoChatSessionRequest(int remoteId,int errcode);
	}
	
	private AnyChatSessionCallback mCallback=null;
	
	public VideoChatSession(Context context,AnyChatCoreSDK anyChat,AnyChatSessionCallback callback) {
		mContext=context;
		mAnyChat=anyChat;
		mCallback=callback;
		mAnyChat.SetVideoCallEvent(this);
	}
	
	@Override
	public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId,
			int dwErrorCode, int dwFlags, int dwParam, String userStr) {
		
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		switch(dwEventType){
		case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY:
			VideoChatCore.DEBUG(" VIDEOCALL REPLY!");
			if(mCallback!=null){
				if(dwErrorCode==0){
					mCallback.onVideoChatSessionReply(dwUserId,true);
				}else{
					mCallback.onVideoChatSessionReply(dwUserId,false);
				}
			}
		case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST:
			VideoChatCore.DEBUG(" VIDEOCALL REQUEST!");
			if(mCallback!=null){
				mCallback.onVideoChatSessionRequest(dwUserId,dwErrorCode);
			}
			break;
		case AnyChatDefine.BRAC_VIDEOCALL_EVENT_START:
			VideoChatCore.DEBUG(" VIDEOCALL START!");
			if(mCallback!=null){
				mCallback.onVideoChatSessionStart(dwUserId,dwParam);
			}
			break;
		case AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH:
			VideoChatCore.DEBUG(" VIDEOCALL FINISH!");
			if(mCallback!=null){
				mCallback.onVideoChatSessionFinish(dwUserId);
			}
			mStatus=VIDEOCHAT_SESSION_IDLE;
			break;
		}
		
	}
	
	public void request(int remoteId){
		if(VIDEOCHAT_SESSION_IDLE!=mStatus){
			return ;
		}
		mRemoteId=remoteId;
		mStatus=VIDEOCHAT_SESSION_REQUEST;
		mAnyChat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST, 
				remoteId, 0,	0, 0, "");
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		mTimer=new Timer();
		mTimer.schedule(new SeesionTimeoutTask(), SESSION_TIMEOUT);
	}
	
	public void reply(int remoteId){
		if(VIDEOCHAT_SESSION_IDLE!=mStatus){
			return ;
		}
		mRemoteId=remoteId;
		mStatus=VIDEOCHAT_SESSION_REPLY;
		mAnyChat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY, 
				remoteId, 0,	0, 0, "");
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		mTimer=new Timer();
		mTimer.schedule(new SeesionTimeoutTask(), SESSION_TIMEOUT);
	}
	
	public void cancel(int remoteId){
		if(mStatus==VIDEOCHAT_SESSION_RUNNING){
			mAnyChat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH,
					remoteId, 0, 0, 0, "");
		}
		if(mStatus==VIDEOCHAT_SESSION_REQUEST||mStatus==VIDEOCHAT_SESSION_REPLY){
			mAnyChat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY,
					remoteId, AnyChatDefine.BRAC_ERRORCODE_SESSION_QUIT, 0, 0, "");
		}
		mStatus=VIDEOCHAT_SESSION_IDLE;
		
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
	}
	
	private class SeesionTimeoutTask extends TimerTask{
		@Override
		public void run() {
			if(mCallback!=null){
				mCallback.onVideoChatSessionReply(mRemoteId,false);
			}
			cancel();
		}
	}

}
