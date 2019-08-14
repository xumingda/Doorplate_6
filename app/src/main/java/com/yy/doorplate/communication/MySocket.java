package com.yy.doorplate.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.util.Log;

import com.yy.doorplate.tool.SystemManager;

public class MySocket {

	private static final String TAG = "SocketProcess";
	// private static final String file = "test.txt";

	public static final int CONNECT_STATE_ON = 0; // ���ӳɹ�
	public static final int CONNECT_STATE_OFF = 1; // �Ͽ�
	public static final int CONNECT_STATE_CONNECT = 2; // ��������
	public static final int CONNECT_READ_TIMEOUT = 3;

	public ServerSocket mServerSocket = null;
	// private PrintWriter mPrintWriter = null;
	// private BufferedReader mBufferedReader = null;
	public long mUpSpeed = 0;
	public long mDownSpeed = 0;
	public long UseTime = 0;

	// private Time startTime = new Time();
	// private Time endTime = new Time();

	private int mThreadStop;
	private Thread TCPThread;

	private String mIP;
	private int mPort;

	private int mTimeOut = 60;

	private Socket mSocket = null;

	public Callback mCallback;

	public MySocket(Callback callback) {
		mCallback = callback;
	}

	public interface Callback {
		public abstract void read(byte[] data, int len);

		public abstract void getState(int state);

		public abstract void send(byte cmd, boolean ret);
	}

	public void initTCP(String ip, int port) {

		mIP = ip;
		mPort = port;
	}

	public void startTCP() {
		// mCallback.getState(CONNECT_STATE_OFF);
		mThreadStop = 0;
		TCPThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					if (mCallback != null) {
						mCallback.getState(CONNECT_STATE_OFF);
						mCallback.getState(CONNECT_STATE_CONNECT);
					}
					Log.i(TAG, "mIP = " + mIP);
					Log.i(TAG, "mPort = " + mPort);

					try {
						mSocket = SocketClientSever(mIP, mPort);
						// Log.i(TAG, "socket = " + mSocket);
						if (mSocket != null) {
							mSocket.setSoTimeout(mTimeOut * 1000);
							if (mCallback != null) {
								mCallback.getState(CONNECT_STATE_ON);
							}
							// ���������߳�
							recv();
							mSocket = null;
							Log.i(TAG, "stop = " + mThreadStop);
							if (mThreadStop == 1) {
								Log.i(TAG, "return");
								return;
							}
						}
					} catch (SocketException e1) {
						e1.printStackTrace();
					}
					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		TCPThread.start();
	}

	public void recv() {

		int readlen = 0;
		byte[] recv = new byte[1024];
		byte[] data = new byte[1024];
		int datalen = 0;
		int temp = 0;
		int unpacklen = 0;
		for (;;) {
			// ���շ���������
			if (mThreadStop == 1) {
				try {
					if (mSocket != null) {
						mSocket.close();
						mSocket = null;
					}
					mCallback.getState(CONNECT_STATE_OFF);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			SystemManager.LOGI(TAG, "temp = " + temp);
			readlen = SocketRead(recv);
			if (readlen > 0) {

				Log.i(TAG, "read len = " + readlen);
				SystemManager.LOGI(TAG, "temp = " + temp);
				if ((temp + readlen) > 1024) {
					SystemManager.LOGI(TAG, "temp+readlen = "
							+ (temp + readlen));
					temp = 0;
					continue;
				}
				System.arraycopy(recv, 0, data, temp, readlen);
				temp += readlen;
				if (data[0] != (byte) 0xF3) {
					SystemManager.LOGI(TAG, "data[0] != 0xF3");
					temp = 0;
					unpacklen = 0;
					continue;
				}
				SystemManager.LOGI(TAG, "-------data------------");
				SystemManager.LogHex(TAG, data, temp);
				SystemManager.LOGI(TAG, "-------data-----------");
				datalen = (int) ((data[1] & 0xFF) | ((data[2] & 0xFF) << 8));
				if (datalen > 1024) {
					temp = 0;
					unpacklen = 0;
					continue;
				}
				if (temp < datalen + 9 || temp < 3) {
					SystemManager.LOGI(TAG, "temp = " + temp + " "
							+ "datalen = " + datalen);
					SystemManager.LOGI(TAG, "temp < datalen || temp < 3");
					continue;
				}
				// ��ֹճ��
				do {
					datalen = (int) ((data[unpacklen + 1] & 0xFF) | ((data[unpacklen + 2] & 0xFF) << 8));
					SystemManager.LOGI(TAG, "datalen = " + datalen);
					byte[] datatmp = new byte[datalen + 10];
					System.arraycopy(data, unpacklen, datatmp, 0, datalen + 9);
					unpacklen += datalen + 9;
					// �ص�
					SystemManager.LOGI(TAG, "unpacklen = " + unpacklen);
					mCallback.read(datatmp, unpacklen);
					datatmp = null;
					SystemManager.LOGI(TAG, "temp 0 = " + temp);
					if (unpacklen >= temp) {
						temp = 0;
						unpacklen = 0;
						break;
					} else {
						// ճ������
						SystemManager.LOGI(TAG, "temp1 = " + temp);
						System.arraycopy(data, unpacklen, data, 0, temp
								- unpacklen);
						temp = temp - unpacklen;
						SystemManager.LOGI(TAG, "temp2 = " + temp);
						unpacklen = 0;
						datalen = (int) ((data[unpacklen + 1] & 0xFF) | ((data[unpacklen + 2] & 0xFF) << 8));
						if (temp < datalen + 9) {
							break;
						}
					}
				} while (unpacklen < temp);
			} else if (readlen < 0) {

				temp = 0;
				unpacklen = 0;
				// < 0Ϊ��������Ͽ����� �˳����գ����½�������
				SystemManager.LOGI(TAG, "read len = " + readlen);
				break;
			}

		}
	}

	public void send(final byte cmd, final byte[] sendbuf, final int sendlen) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (SocketSend(sendbuf, sendlen)) {
					mCallback.send(cmd, true);
				} else {
					mCallback.send(cmd, false);
				}
			}
		}).start();
	}

	public void stopTCP() {
		mThreadStop = 1;
		if (mSocket != null) {
			try {
				mSocket.close();
				mSocket = null;
				mCallback.getState(CONNECT_STATE_OFF);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean SocketSend(byte[] sendbuf, int sendlen) {

		if (mSocket != null) {
			try {
				DataOutputStream outputStream = new DataOutputStream(
						mSocket.getOutputStream());
				// SystemManager.LOGI("TAG", "---------���ݷ���-----------");
				// SystemManager.LogHex(TAG, sendbuf, sendlen);
				// SystemManager.LOGI("TAG", "---------------------------");
				outputStream.write(sendbuf, 0, sendlen);

				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * SOCKET ��ȡ����, size:��ȡ��С timeout:��ʱʱ�� ���أ���ȡ����
	 */
	private int SocketRead(byte[] readbuf) {

		int readlen = 0;

		DataInputStream inputStream = null;
		if (mSocket == null) {
			return -1;
		}

		try {
			inputStream = new DataInputStream(mSocket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
			return -1;
		}

		try {
			if (inputStream != null) {
				readlen = inputStream.read(readbuf);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (e.getClass() == SocketTimeoutException.class) {
				// setTaskState(TaskState.STATE_ALIVE);
				mCallback.getState(CONNECT_READ_TIMEOUT);
				readlen = -1;
			} else {
				SystemManager.LOGE(TAG, "================");
				SystemManager.LOGE(TAG, " onRunning IOException ");
				e.printStackTrace();
				// --����
				// mTaskState=TaskState.STATE_DISCONNECT;
				mCallback.getState(CONNECT_STATE_OFF);
				SystemManager.LOGE(TAG, "================");
				readlen = -1;
			}
		}
		if (mThreadStop == 1) {
			Log.i(TAG, "stop");
			readlen = 0;
			// break;
		}
		return readlen;
	}

	public Socket SocketClientSever(String ip, int port) {
		try {
			Socket socket = new Socket();
			SocketAddress socAddress = new InetSocketAddress(ip, port);
			socket.connect(socAddress, 5000);
			if (socket != null) {
				return socket;
			} else {
				Log.i(TAG, "SOCKET IS NULL");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void SocketClose(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
