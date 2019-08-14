package com.advertisement.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.advertisement.system.ConstData;
import com.advertisement.system.SystemConfig;
import com.advertisement.system.SystemManager;
import com.common.Mutex;

public class DownloadTask extends ResTask{

	private String mTmpPath=null;
	private static final String TMP_TAG=".TMP";
	private long mFilePostion=0;
	private long mFileSize=0;
	private long mRecvSize=0;
	private DownloadThread mDownloadThread=null;
	private Mutex mTaskMutex=new Mutex();
	private FtpManager mFtpManager=null;
	private int mFtpLoginCount=0;
	private File mFile=null;
	private RandomAccessFile mFos=null;
	private byte[] mRecvBuf=new byte[2048];
	private static final int FTP_LOGIN_MAX=10; 
	private  String TAG="DownloadTask";
	private int ftpRetry=0;
	
	private enum TaskMode{
		MODE_CHECK,
		MODE_TMP_CHECK,
		MODE_NORMAL
	}
	private TaskMode mTaskMode=TaskMode.MODE_NORMAL;
	private enum TaskState{
		STATE_LOGIN_FTP,
		STATE_GET_SIZE,
		STATE_DOWNLOAD,
		STATE_FINISH,
		STATE_IDLE
	}
	private TaskState mTaskState=TaskState.STATE_LOGIN_FTP;
	
	private int mTaskError=ConstData.DownloadError.ERROR_UNKNONWN;
	
	public DownloadTask(ResManager manager, int taskId, int type, String url) {
		super(manager, taskId, type, url);
		this.dir=ResTask.TASK_DOWNLOAD;
		TAG=TAG+"_"+String.valueOf(mTaskId);
	}

	public boolean initParam(){
		//ftp://smedia:smedia@120.25.65.121:51/upload/themeswitch/201512/res_themeswitch_t73_20151227164541808.xml
		int startIndex=0;
		int stopIndex=0;
		String tmp;
		
		if (TextUtils.isEmpty(ftpUrl)) {
			return false;
		}
		
		startIndex=ftpUrl.indexOf("ftp://");
		if(startIndex!=0){
			SystemManager.LOGE(TAG, "Can't parse url");
			return false;
		}
		startIndex+=6;
		tmp=ftpUrl.substring(startIndex);
		stopIndex=tmp.indexOf(":");
		if(stopIndex<0){
			SystemManager.LOGE(TAG,"initParamer  can't find tag \':\'-1!");
			return false;
		}
		loginUser=tmp.substring(0, stopIndex);
		startIndex=stopIndex+1;
		tmp=tmp.substring(startIndex);
		stopIndex=tmp.indexOf("@");
		if(stopIndex<0){
			SystemManager.LOGE(TAG,"initParamer  can't find tag \'@\'!");
			return false;
		}
		loginPwd=tmp.substring(0, stopIndex);
		startIndex=stopIndex+1;
		tmp=tmp.substring(startIndex);
		stopIndex=tmp.indexOf(":");
		if(stopIndex<0){
			SystemManager.LOGE(TAG,"initParamer  can't find tag \':\'-2!");
			return false;
		}
		ftpIp=tmp.substring(0,stopIndex);
		startIndex=stopIndex+1;
		tmp=tmp.substring(startIndex);
		stopIndex=tmp.indexOf("/");
		if(stopIndex<0){
			SystemManager.LOGE(TAG,"initParamer  can't find tag \'/\'!");
			return false;
		}
		ftpPort=Integer.valueOf(tmp.substring(0, stopIndex));
		startIndex=stopIndex+1;
		fileRemotePath=tmp.substring(startIndex);
		
		String[] strs=fileRemotePath.split("/");
		
		for(int i=0;i<strs.length;i++){
			
			SystemManager.LOGD(TAG, strs[i]);
		}
		
		if(strs.length==4){
			
			fileName=strs[3];
			fileFolder=strs[0]+"/"+strs[1]+"/"+strs[2]+"/";
		}else{
			SystemManager.LOGE(TAG,"initParamer can't find tag \'res\'!");
			return false;
		}
		/*
		stopIndex=tmp.indexOf("res");
		if(stopIndex<0){
			SystemManager.LOGE(TAG,"initParamer can't find tag \'res\'!");
			return false;
		}
		fileName=fileRemotePath.substring(stopIndex-3);
		fileFolder=fileRemotePath.substring(0,stopIndex-3);
		*/
		//--创建文件夹
		if(!new File(SystemConfig.getStoreRootPath()+"/"+fileFolder).exists()){
			new File(SystemConfig.getStoreRootPath()+"/"+fileFolder).mkdirs();
		}
		
		fileLocalPath=SystemConfig.getStoreRootPath()+"/"+fileFolder+fileName;
		
		mTmpPath=fileLocalPath+TMP_TAG;
		
		mTaskMode=TaskMode.MODE_NORMAL;
		mFilePostion=0;
		//--判断文件是否存在，如果存在，就进入校验任务状态
		mFile=new File(fileLocalPath);
		if(mFile!=null){
			if(mFile.exists()){
				mTaskMode=TaskMode.MODE_CHECK;
			}else{
				//--判读临时文件是否存在，如果存在，进进入临时文件校验状态
				mFile=new File(mTmpPath);
				if(mFile!=null){
					if(mFile.exists()){
						mTaskMode=TaskMode.MODE_TMP_CHECK;
					}
				}
			}
		}
		

		SystemManager.LOGD(TAG, "======== FTP DOWNLOAD INFO =========");
		SystemManager.LOGD(TAG,"loginUser:"+loginUser);
		SystemManager.LOGD(TAG,"loginPwd:"+loginPwd);
		SystemManager.LOGD(TAG,"ftpIp:"+ftpIp);
		SystemManager.LOGD(TAG,"ftpPort:"+ftpPort);
		SystemManager.LOGD(TAG,"fileRemotePath:"+fileRemotePath);
		SystemManager.LOGD(TAG,"fileFolder:"+fileFolder);
		SystemManager.LOGD(TAG,"fileName:"+fileName);
		SystemManager.LOGD(TAG,"fileLocalPath:"+fileLocalPath);
		SystemManager.LOGD(TAG,"mTmpPath:"+mTmpPath);
		if(mTaskMode==TaskMode.MODE_NORMAL){
			SystemManager.LOGD(TAG,"==== Normal to download ====");
		}else if(mTaskMode==TaskMode.MODE_CHECK){
			SystemManager.LOGD(TAG,"=== Check file ===");
		}else if(mTaskMode==TaskMode.MODE_TMP_CHECK){
			SystemManager.LOGD(TAG,"=== Check tmp file ===");
		}
		SystemManager.LOGD(TAG,"====================================");
		
		return true;
	}
	
	@Override
	public void start() {
		
		SystemManager.LOGI(TAG, "---------------------");
		SystemManager.LOGI(TAG, "|   Download start  |");
		SystemManager.LOGI(TAG, "---------------------");
		if(initParam()==false){
			
			Message msg=mResManager.getHandler().obtainMessage();
			msg.what=ConstData.ResMsgType.MSG_TASK_FINISH;
			msg.arg1=ConstData.DownloadError.ERROR_PARAMER;//--参数错误
			msg.obj=this;
			mResManager.getHandler().sendMessage(msg);
			SystemManager.LOGI(TAG, "---------------------");
			SystemManager.LOGI(TAG, "|Download start fail|");
			SystemManager.LOGI(TAG, "---------------------");
			return ;
		}
		
		if(mDownloadThread!=null){
			stop();
		}
		if(mFtpManager==null){
			mFtpManager=new FtpManager();
		}
		ftpRetry=0;
		mDownloadThread=new DownloadThread();
		mFtpLoginCount=0;
		
		mResManager.reportFileTransStatus(this,ConstData.FileTrans.STATUS_TRANSING);
		
		mDownloadThread.start();
		
		SystemManager.LOGI(TAG, "---------------------");
		SystemManager.LOGI(TAG, "|Download start ok  |");
		SystemManager.LOGI(TAG, "---------------------");
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		SystemManager.LOGI(TAG, "---------------------");
		SystemManager.LOGI(TAG, "|   Download stop   |");
		SystemManager.LOGI(TAG, "---------------------");
		if(mDownloadThread!=null){
			mDownloadThread.interrupt();
			mTaskMutex.lock(10000);
			mTaskMutex.unlock();
			mDownloadThread=null;
		}
		
		SystemManager.LOGI(TAG, "---------------------");
		SystemManager.LOGI(TAG, "|   Download stop ok|");
		SystemManager.LOGI(TAG, "---------------------");
	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unused")
	private class DownloadThread extends Thread{
		
		@Override
		public void run() {
			super.run();
			
			if(!mTaskMutex.lock(10000)){
				return ;
			}
			mTaskState=TaskState.STATE_LOGIN_FTP;
			while(!interrupted()){

				if(mTaskState==TaskState.STATE_LOGIN_FTP){
					SystemManager.LOGI(TAG, "[STATE_LOGIN_FTP]");
					onStateLoginFtp();
				}else if(mTaskState==TaskState.STATE_GET_SIZE){
					SystemManager.LOGI(TAG, "[STATE_GET_SIZE]");
					onStateGetSize();
				}else if(mTaskState==TaskState.STATE_DOWNLOAD){
					onStateDownload();
				}else if(mTaskState==TaskState.STATE_FINISH){
					SystemManager.LOGI(TAG, "[STATE_FINISH]");
					onStateFinish();
				}
				
				if(mTaskState==TaskState.STATE_IDLE){
					break;
				}

			}
	
			SystemManager.LOGI(TAG,"=== Download Task interrupted ===!");
			
			if(mTaskState!=TaskState.STATE_IDLE){
				onStateFinish();
			}
			
			mTaskMutex.unlock();
			
			SystemManager.LOGI(TAG,"=== Download Task finish ===!");
			
			reportFinishToManager();
			
		}
	}
	
	private void reportFinishToManager(){
		
		Message msg=mResManager.getHandler().obtainMessage();
		msg.what=ConstData.ResMsgType.MSG_TASK_FINISH;
		msg.arg1=mTaskError;
		msg.obj=this;
		mResManager.getHandler().sendMessage(msg);
	}
	
	private void onStateLoginFtp(){
		
		mFtpManager.login(ftpIp,ftpPort,loginUser, loginPwd);
		if(mFtpManager.getLoginState()){
			mTaskState=TaskState.STATE_GET_SIZE;
		}else{
			mFtpLoginCount++;
			//--超过登录次数就结束当前流程
			if(mFtpLoginCount==FTP_LOGIN_MAX){
				mTaskError=ConstData.DownloadError.ERROR_LOGIN;
				mTaskState=TaskState.STATE_FINISH;
			}else{
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressLint("NewApi") private void onStateGetSize(){
		mFileSize=mFtpManager.getFileSize(fileFolder,fileName);
		SystemManager.LOGI(TAG,"--- ftp file size:"+mFileSize);
		if(mFileSize<=0){
			mTaskError=ConstData.DownloadError.ERROR_GET_SIZE;
			mTaskState=TaskState.STATE_FINISH;
		}else{
			ftpRetry=0;
			if(mTaskMode==TaskMode.MODE_CHECK){
				mFile=new File(fileLocalPath);
				if(mFile.exists()){
					//--校验
					if(mFile.length()==mFileSize){
						mTaskError=ConstData.DownloadError.ERROR_NONE;
						mTaskState=TaskState.STATE_FINISH;
						return ;
					}else{
						mFile.delete();//--文件长度不同即校验失败，删除该文件重新进入下载模式
					}
				}
					
				mFilePostion=0;//--归零文件偏移
				mTaskMode=TaskMode.MODE_NORMAL;
				
			}else if(mTaskMode==TaskMode.MODE_TMP_CHECK){
				mFile=new File(mTmpPath);
				if(mFile.exists()){
					//--校验文件
					if(mFile.length()==mFileSize){
						mTaskError=ConstData.DownloadError.ERROR_NONE;
						mTaskState=TaskState.STATE_FINISH;
						return ;
					}else{
						mFilePostion=mFile.length();//--记录断点
						
					}
				}

				mTaskMode=TaskMode.MODE_NORMAL;
			}
			
			if(mTaskMode==TaskMode.MODE_NORMAL){
				mTaskState=TaskState.STATE_DOWNLOAD;
				mRecvSize=mFilePostion;
				mFile=new File(mTmpPath);
				try {
					if(!mFile.exists()){
						mFile.createNewFile();
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					mFos=new RandomAccessFile(mFile, "rw");
					mFos.seek(mFilePostion);//--设置文件偏移
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//--设置传输的断点与文件路径
				if(!mFtpManager.setTransEnv(fileRemotePath, mFilePostion)){
					mTaskError=ConstData.DownloadError.ERROR_GET_SIZE;
					mTaskState=TaskState.STATE_FINISH;
					return ;
				}
			}
		}
	}

	private void onStateDownload(){
		
		int recvSize=-1;
		
		if(mFos==null){
			mTaskError=ConstData.DownloadError.ERROR_RECV_DATA;
			mTaskState=TaskState.STATE_FINISH;
			return ;
		}
		//--接收FTP服务器数据流
		recvSize=mFtpManager.recvFile(mRecvBuf);
		if(recvSize>0){
			ftpRetry=0;
			try {
				mFos.write(mRecvBuf, 0, recvSize);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mRecvSize+=recvSize;
		}
		
		SystemManager.LOGD(TAG,"=== DOWNLOAD "+"recv size:"+mRecvSize);
		float percent=(mRecvSize*100)/mFileSize;
		SystemManager.LOGE(TAG,"=== DOWNLOAD "+String.valueOf(percent)+"%");
		if(mRecvSize==mFileSize){
			
			try {
				mFos.close();
				//--重命名为实际文件
				new File(mTmpPath).renameTo(new File(fileLocalPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mFos=null;
				mTaskError=ConstData.DownloadError.ERROR_RECV_DATA;
				mTaskState=TaskState.STATE_FINISH;
				return ;
			}
			mFos=null;
			
			mTaskError=ConstData.DownloadError.ERROR_NONE;
			mTaskState=TaskState.STATE_FINISH;
		}else{
			if(recvSize<=0){
				mTaskError=ConstData.DownloadError.ERROR_RECV_DATA;
				mTaskState=TaskState.STATE_FINISH;
				try {
					mFos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mFos=null;
			}
		}
	}
	
	private void onStateFinish(){
		
		if(mFtpManager.getLoginState()){
			mFtpManager.loginOut();
		}
		
		if(mFos!=null){
			try {
				mFos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mFos=null;
		}
		//--故障出现之后重试
		if(mTaskError==ConstData.DownloadError.ERROR_RECV_DATA||
				mTaskError==ConstData.DownloadError.ERROR_GET_SIZE){
			ftpRetry++;
			if(mTaskError==ConstData.DownloadError.ERROR_RECV_DATA){
				SystemManager.LOGE(TAG,"FTP RECV DATA RETEY:"+ftpRetry);
			}
			if(mTaskError==ConstData.DownloadError.ERROR_GET_SIZE){
				SystemManager.LOGE(TAG,"FTP GET SIZE RETEY:"+ftpRetry);
			}
			
			if(ftpRetry!=10){
				mTaskState=TaskState.STATE_LOGIN_FTP;
				initParam();
				return ;
			}
		}
		
		if(mTaskError==ConstData.DownloadError.ERROR_NONE){
			mResManager.reportFileTransStatus(this, ConstData.FileTrans.STATUS_TRANS_FINISH);
		}else{
			mResManager.reportFileTransStatus(this, ConstData.FileTrans.STATUS_TRANS_ERROR);
		}
			
		mTaskState=TaskState.STATE_IDLE;

	}
	
}
