package com.advertisement.resource;

import android.os.Handler;

public abstract class ResTask {
	public ResManager mResManager=null;
	public int mTaskId=0;
	public int fileType=0;
	public String ftpUrl=null;
	public String ftpIp=null;
	public int ftpPort=0;
	public String fileLocalPath=null;
	public String fileRemotePath=null;
	public String fileFolder=null;
	public String fileName=null;
	public static final int TASK_UPLOAD=1;
	public static final int TASK_DOWNLOAD=0;
	public int dir=TASK_DOWNLOAD;//--0£ºÏÂÔØ 1£ºÉÏ´«
	public String loginUser=null;
	public String loginPwd=null;
	public ResTask(ResManager manager,int taskId,int type,String url){
		mResManager=manager;
		mTaskId=taskId;
		fileType=type;
		ftpUrl=url;
	}
	public int getTaskId(){
		return mTaskId;
	}
	public abstract void start();
	public abstract void stop();
	public abstract Handler getHandler();
}
