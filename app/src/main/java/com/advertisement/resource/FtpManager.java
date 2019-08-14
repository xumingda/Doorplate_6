package com.advertisement.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.TimeZone;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

import com.advertisement.system.SystemManager;

public class FtpManager {

	private FTPClient mFtpClient=null;
	private InputStream mInputStream=null;
	private boolean isLogin=false;
	private static final String TAG="FtpManager";
	
	public FtpManager(){
		
		mFtpClient=new FTPClient();
		
		isLogin=false;
	}
	
	public boolean login(String ip,int port,String user,String password){
		
		if(isLogin){
			return true;
		}
		
		FTPClientConfig ftpClientConfig = new FTPClientConfig();  
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());  
        mFtpClient.setControlEncoding("GBK");  
        mFtpClient.configure(ftpClientConfig);
        
        try{
        	
        	mFtpClient.setConnectTimeout(5000); // 5秒钟，如果超过就判定超时了
        	//GY,20160117, 加入这个语句，防止socket因为服务器无响应而永远阻塞。
        	mFtpClient.setDefaultTimeout(30*1000);  
            if (port > 0) {  
            	mFtpClient.connect(ip,port);  
            } else {  
            	mFtpClient.connect(ip);  
            }  
           //GY,20160117, 加入这个语句，防止socket因为服务器无响应而永远阻塞。
            mFtpClient.setSoTimeout(30*1000); 
            // FTP服务器连接回答   
            int reply = mFtpClient.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
            	mFtpClient.disconnect();  
            	SystemManager.LOGE(TAG,"-------------------------------------");
                SystemManager.LOGE(TAG,"FTP Login isPositiveCompletion fail!"); 
                SystemManager.LOGE(TAG,"-------------------------------------");
                return isLogin;  
            }  
            mFtpClient.login(user, password);  
            // 设置传输协议   
            mFtpClient.enterLocalPassiveMode();  
            mFtpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            SystemManager.LOGI(TAG,"-----------------------"); 
            SystemManager.LOGI(TAG,"FTP Login successful!");  
            SystemManager.LOGI(TAG,"-----------------------"); 
            isLogin = true;  
        } catch (Exception e) {  
        	SystemManager.LOGE(TAG,"-------------------------------------");
            SystemManager.LOGE(TAG,"FTP Login  fail!"); 
            SystemManager.LOGE(TAG,"-------------------------------------");
        }  
        //--设置接收发送缓存大小
        mFtpClient.setBufferSize(1024 * 2);  
        //--设置接收发送数据应答超时时间
        mFtpClient.setDataTimeout(30 * 1000);  
        return isLogin;  
	}
	
	public boolean loginOut(){
		
		if(!mFtpClient.isConnected()){
			return true;
		}
		
		try {  
            boolean reuslt = mFtpClient.logout();// 退出FTP服务器   
            if (reuslt) {  
            	SystemManager.LOGI(TAG,"Logout FTP Server OK!");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
            SystemManager.LOGE(TAG,"Logout FTP Server Error!" + e.getMessage());  
        } finally {  
            try {  
            	
            	mFtpClient.disconnect();// 关闭FTP服务器的连接   
            	SystemManager.LOGI(TAG,"Disconnect FTP Server OK!"); 
            } catch (IOException e) {  
                e.printStackTrace();  
                SystemManager.LOGE(TAG,"Disconnect FTP Server Error!");  
            }  
        } 
		
		isLogin=false;
		
		return true;
	}
	
	public long getFileSize(String folder,String filename){
		
		// 先判断服务器文件是否存在  
        try {
			FTPFile[] files = mFtpClient.listFiles(folder);
			if(files==null){
				return -1;
			}
			if(files.length==0){
				return -1;//--文件不存在
			}
			
			for(int i=0;i<files.length;i++){
				if(files[i].getName().equals(filename)){
					return files[i].getSize();
				}
			}
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return 0;
	}
	
	public boolean setTransEnv(String remotePath,long offset){
		try {
			mFtpClient.changeWorkingDirectory(remotePath);
			mFtpClient.setRestartOffset(offset);
			mInputStream=mFtpClient.retrieveFileStream(remotePath);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public int recvFile(byte[] data){
		
		if(mInputStream==null){
			return 0;
		}
		
		try {
			return mInputStream.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean getLoginState(){
		return isLogin;
	}
}
