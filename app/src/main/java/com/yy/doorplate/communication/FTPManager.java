package com.yy.doorplate.communication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.TimeZone;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

public class FTPManager {

	private static final String TAG = "FTPManager";

	// ip
	private String address;
	// 端口
	private int port;
	// 用户名
	private String userName;
	// 密码
	private String password;
	// ftp路径
	private String remotePath;
	// 本地文件名
	private String localName;
	// 本地保存路径
	private String localPath;

	private FTPClient ftpClient;

	public FTPManager() {
	}

	public void parseUrl(String url, String path) {
		try {
			String tempUrl = url.replace("ftp://", "");
			int temp = tempUrl.indexOf(':');
			userName = tempUrl.substring(0, temp);
			int temp1 = tempUrl.indexOf('@');
			password = tempUrl.substring(temp + 1, temp1);
			tempUrl = tempUrl.replace(userName + ":" + password + "@", "");
			temp = tempUrl.indexOf(':');
			address = tempUrl.substring(0, temp);
			temp1 = tempUrl.indexOf('/');
			port = Integer.parseInt(tempUrl.substring(temp + 1, temp1));
			remotePath = tempUrl.substring(temp1 + 1);
			temp = tempUrl.lastIndexOf('/');
			localName = tempUrl.substring(temp);
			localPath = path;

			Log.d(TAG, "address : " + address + "\nport : " + port
					+ "\nuserName : " + userName + "\npassword : " + password
					+ "\nremotePath : " + remotePath + "\nlocalName : "
					+ localName + "\nlocalPath : " + localPath);
		} catch (Exception e) {
			// e.printStackTrace();
			parseUrlAnonymous(url, path);
		}
	}

	public void parseUrlAnonymous(String url, String path) {
		try {
			String tempUrl = url.replace("ftp://", "");
			userName = "anonymous";
			password = "anonymous";
			String[] tempUrls = tempUrl.split("/");
			address = tempUrls[0];
			port = 21;
			remotePath = tempUrl.replace(address + "/", "");
			localName = tempUrls[tempUrls.length - 1];
			localPath = path;

			Log.d(TAG, "address : " + address + "\nport : " + port
					+ "\nuserName : " + userName + "\npassword : " + password
					+ "\nremotePath : " + remotePath + "\nlocalName : "
					+ localName + "\nlocalPath : " + localPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * FTP服务器连接
	 * 
	 * @return
	 */
	public boolean connect(String url, String path) {
		parseUrl(url, path);
		try {
			ftpClient = new FTPClient();

			FTPClientConfig ftpClientConfig = new FTPClientConfig();
			ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
			ftpClient.setControlEncoding("GBK");
			ftpClient.configure(ftpClientConfig);

			ftpClient.setConnectTimeout(5000);
			ftpClient.setControlKeepAliveReplyTimeout(1000);
			ftpClient.setDefaultTimeout(30 * 1000);
			ftpClient.connect(address, port);// 连接FTP服务器
			ftpClient.setSoTimeout(30 * 1000);
			ftpClient.login(userName, password);// 登陆FTP服务器
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				Log.i(TAG, "FTP服务器连接" + address + ":" + port + "失败，用户名或密码错误");
				ftpClient.disconnect();
			} else {
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.setUseEPSVwithIPv4(true);
				// --设置接收发送缓存大小
				ftpClient.setBufferSize(1024 * 2);
				// --设置接收发送数据应答超时时间
				ftpClient.setDataTimeout(30 * 1000);
				Log.i(TAG, "FTP服务器连接" + address + ":" + port + "成功");
				return true;
			}
		} catch (SocketException e) {
			Log.i(TAG, "FTP服务器连接" + address + ":" + port + "失败，连接超时");
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException f) {
				}
			}
		} catch (IOException e) {
			Log.i(TAG, "FTP服务器连接" + address + ":" + port + "失败");
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException f) {
				}
			}
		}
		return false;
	}

	/**
	 * 关闭连接
	 */
	public void disConnect() {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				Log.i(TAG, e.getMessage());
			}
		}
	}

	public long getFileSize(String folder, String filename) {
		// 先判断服务器文件是否存在
		try {
			FTPFile[] files = ftpClient.listFiles(folder);
			if (files == null) {
				return -1;
			}
			if (files.length == 0) {
				return -1;// --文件不存在
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equals(filename)) {
					return files[i].getSize();
				}
			}
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 下载
	 * 
	 * @return
	 */
	public boolean download() {
		OutputStream output = null;
		try {
			File file = new File(localPath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			output = new FileOutputStream(file);
			if (ftpClient.retrieveFile(remotePath, output)) {
				Log.i(TAG, "文件下载成功:" + remotePath);
				return true;
			} else {
				file.delete();
				Log.i(TAG, "文件下载失败:" + remotePath);
				return false;
			}

		} catch (Exception e) {
			return false;
		} finally {
			try {
				output.close();
				if (ftpClient.isConnected()) {
					ftpClient.disconnect();
				}
			} catch (Exception e) {
			}
		}
	}

}
