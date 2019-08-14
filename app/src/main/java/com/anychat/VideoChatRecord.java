package com.anychat;

import android.content.Context;
import android.os.Environment;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatRecordEvent;

public class VideoChatRecord implements AnyChatRecordEvent{

	private Context mContext=null;
	private AnyChatCoreSDK mAnyChat=null;
	private int mRecordFlags=0;
	
	public interface VideoChatRecordCallback{
		public abstract void onRecordCallback(String path,int Elapse);
		public abstract void onSnapCallback(String path);
	}
	
	private VideoChatRecordCallback mCallback=null;
	private boolean isRecord=false;
	
	public VideoChatRecord(Context context ,AnyChatCoreSDK anychat,VideoChatRecordCallback callback) {
		mContext=context;
		mAnyChat=anychat;
		mAnyChat.SetRecordSnapShotEvent(this);
		mCallback=callback;
	}
	
	public void setRecordDirPath(String path){
		// 设置录像存储路径
		mAnyChat.SetSDKOptionString(AnyChatDefine.BRAC_SO_RECORD_TMPDIR,
				Environment.getExternalStorageDirectory()+path);
		// 设置录像格式（0表示mp4）
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_RECORD_FILETYPE, 0);
	}
	
	public void startRecord(){
		
		if(isRecord)
		{
			return ;
		
		}
		
		isRecord=true;
		
		VideoChatCore.DEBUG(" ==== START RECORD =====");
		/*
		int mdwFlags=AnyChatDefine.BRAC_RECORD_FLAGS_AUDIO    ///< 录制音频
				+ AnyChatDefine.BRAC_RECORD_FLAGS_VIDEO       ///< 录制视频
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_MIXAUDIO ///< 录制音频时，将其它人的声音迭加后录制
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_MIXVIDEO ///< 录制视频时，将其它人的视频迭加后录制
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_STEREO   ///< 录制音频时，将其它人的声音混合为立体声后录制
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_ABREAST; ///< 录制视频时，将其它人的视频并列录制
		*/
		mRecordFlags=AnyChatDefine.BRAC_RECORD_FLAGS_AUDIO    ///< 录制音频
				+ AnyChatDefine.BRAC_RECORD_FLAGS_VIDEO       ///< 录制视频
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_MIXVIDEO  ///< 录制视频时，将其它人的视频并列录制                
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_MIXAUDIO ///< 录制音频时，将其它人的声音迭加后录制
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_STEREO;   ///< 录制音频时，将其它人的声音混合为立体声后录制
				
		//--开始录制 混合录制
		mAnyChat.StreamRecordCtrlEx(-1, 1, mRecordFlags, 0, "StartRecording");
	}
	
	public void stopRecord(){
		isRecord=false;
		VideoChatCore.DEBUG(" ==== STOP RECORD =====");
		//--停止录制 混合录制
		mAnyChat.StreamRecordCtrlEx(-1, 0, mRecordFlags, 0, "StopRecording");
	}
	@Override
	public void OnAnyChatRecordEvent(int dwUserId, int dwErrorCode,
			String lpFileName, int dwElapse, int dwFlags, int dwParam,
			String lpUserStr) {
		VideoChatCore.DEBUG("=============================================");
		VideoChatCore.DEBUG(" OnAnyChatSnapShotEvent:");
		VideoChatCore.DEBUG(" dwUserId:"+dwUserId);
		VideoChatCore.DEBUG(" dwErrorCode:"+dwErrorCode);
		VideoChatCore.DEBUG(" lpFileName:"+lpFileName);
		VideoChatCore.DEBUG(" dwFlags:"+dwFlags);
		VideoChatCore.DEBUG(" dwParam:"+dwParam);
		VideoChatCore.DEBUG(" lpUserStr:"+lpUserStr);
		VideoChatCore.DEBUG("=============================================");
		if(mCallback!=null){
			mCallback.onRecordCallback(lpFileName, dwElapse);
		}
		
	}
	@Override
	public void OnAnyChatSnapShotEvent(int dwUserId, int dwErrorCode,
			String lpFileName, int dwFlags, int dwParam, String lpUserStr) {
		// TODO Auto-generated method stub
		VideoChatCore.DEBUG("=============================================");
		VideoChatCore.DEBUG(" OnAnyChatSnapShotEvent:");
		VideoChatCore.DEBUG(" dwUserId:"+dwUserId);
		VideoChatCore.DEBUG(" dwErrorCode:"+dwErrorCode);
		VideoChatCore.DEBUG(" lpFileName:"+lpFileName);
		VideoChatCore.DEBUG(" dwFlags:"+dwFlags);
		VideoChatCore.DEBUG(" dwParam:"+dwParam);
		VideoChatCore.DEBUG(" lpUserStr:"+lpUserStr);
		VideoChatCore.DEBUG("=============================================");
		if(mCallback!=null){
			mCallback.onSnapCallback(lpFileName);
		}
	}
}
