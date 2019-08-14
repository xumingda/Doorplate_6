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
		// ����¼��洢·��
		mAnyChat.SetSDKOptionString(AnyChatDefine.BRAC_SO_RECORD_TMPDIR,
				Environment.getExternalStorageDirectory()+path);
		// ����¼���ʽ��0��ʾmp4��
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
		int mdwFlags=AnyChatDefine.BRAC_RECORD_FLAGS_AUDIO    ///< ¼����Ƶ
				+ AnyChatDefine.BRAC_RECORD_FLAGS_VIDEO       ///< ¼����Ƶ
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_MIXAUDIO ///< ¼����Ƶʱ���������˵��������Ӻ�¼��
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_MIXVIDEO ///< ¼����Ƶʱ���������˵���Ƶ���Ӻ�¼��
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_STEREO   ///< ¼����Ƶʱ���������˵��������Ϊ��������¼��
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_ABREAST; ///< ¼����Ƶʱ���������˵���Ƶ����¼��
		*/
		mRecordFlags=AnyChatDefine.BRAC_RECORD_FLAGS_AUDIO    ///< ¼����Ƶ
				+ AnyChatDefine.BRAC_RECORD_FLAGS_VIDEO       ///< ¼����Ƶ
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_MIXVIDEO  ///< ¼����Ƶʱ���������˵���Ƶ����¼��                
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_MIXAUDIO ///< ¼����Ƶʱ���������˵��������Ӻ�¼��
				+ AnyChatDefine.ANYCHAT_RECORD_FLAGS_STEREO;   ///< ¼����Ƶʱ���������˵��������Ϊ��������¼��
				
		//--��ʼ¼�� ���¼��
		mAnyChat.StreamRecordCtrlEx(-1, 1, mRecordFlags, 0, "StartRecording");
	}
	
	public void stopRecord(){
		isRecord=false;
		VideoChatCore.DEBUG(" ==== STOP RECORD =====");
		//--ֹͣ¼�� ���¼��
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
