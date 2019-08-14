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
	// �˿�
	private int port;
	// �û���
	private String userName;
	// ����
	private String password;
	// ftp·��
	private String remotePath;
	// �����ļ���
	private String localName;
	// ���ر���·��
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
	 * FTP����������
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
			ftpClient.connect(address, port);// ����FTP������
			ftpClient.setSoTimeout(30 * 1000);
			ftpClient.login(userName, password);// ��½FTP������
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				Log.i(TAG, "FTP����������" + address + ":" + port + "ʧ�ܣ��û������������");
				ftpClient.disconnect();
			} else {
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.setUseEPSVwithIPv4(true);
				// --���ý��շ��ͻ����С
				ftpClient.setBufferSize(1024 * 2);
				// --���ý��շ�������Ӧ��ʱʱ��
				ftpClient.setDataTimeout(30 * 1000);
				Log.i(TAG, "FTP����������" + address + ":" + port + "�ɹ�");
				return true;
			}
		} catch (SocketException e) {
			Log.i(TAG, "FTP����������" + address + ":" + port + "ʧ�ܣ����ӳ�ʱ");
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException f) {
				}
			}
		} catch (IOException e) {
			Log.i(TAG, "FTP����������" + address + ":" + port + "ʧ��");
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
	 * �ر�����
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
		// ���жϷ������ļ��Ƿ����
		try {
			FTPFile[] files = ftpClient.listFiles(folder);
			if (files == null) {
				return -1;
			}
			if (files.length == 0) {
				return -1;// --�ļ�������
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
	 * ����
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
				Log.i(TAG, "�ļ����سɹ�:" + remotePath);
				return true;
			} else {
				file.delete();
				Log.i(TAG, "�ļ�����ʧ��:" + remotePath);
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
