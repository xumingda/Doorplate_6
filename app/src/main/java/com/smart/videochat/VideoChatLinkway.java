package com.smart.videochat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class VideoChatLinkway {

	private Context mContext = null;
	private static final String TAG = "VideoChatLinkway";
	private TcpToUdpTask mAudioRxTask = null;
	private UdpToTcpTask mAudioTxTask = null;
	private TcpToUdpTask mVideoRxTask = null;
	private UdpToTcpTask mVideoTxTask = null;
	private TcpToUdpTask mSendAudioRtcpTxTask = null;
	private TcpToUdpTask mSendAudioRtcpRxTask = null;
	private TcpToUdpTask mSendVideoRtcpTxTask = null;
	private TcpToUdpTask mSendVideoRtcpRxTask = null;

	private UdpToTcpTask mRecvAudioRtcpTxTask = null;
	private UdpToTcpTask mRecvAudioRtcpRxTask = null;
	private UdpToTcpTask mRecvVideoRtcpTxTask = null;
	private UdpToTcpTask mRecvVideoRtcpRxTask = null;

	private void DEBUG(String content) {
		Log.d(TAG, content);
	}

	public VideoChatLinkway(Context context) {
		this.mContext = context;
	}

	public void start(int audioTxPort, int audioRxPort, int videoTxPort, int videoRxPort, String dstIp) {
		stop();
		SystemClock.sleep(50);
		if (mAudioRxTask != null) {
			mAudioRxTask.stopTask();
		}

		if (mAudioTxTask != null) {
			mAudioTxTask.stopTask();
		}

		if (mVideoRxTask != null) {
			mVideoRxTask.stopTask();
		}

		if (mVideoTxTask != null) {
			mVideoTxTask.stopTask();
		}

		mAudioRxTask = new TcpToUdpTask(VideoChatConfig.TCP_AUDIO_RTP_PORT, VideoChatConfig.AUDIO_RX_PORT);
		mAudioTxTask = new UdpToTcpTask(VideoChatConfig.AUDIO_TX_PORT, VideoChatConfig.TCP_AUDIO_RTP_PORT, dstIp);

		mVideoRxTask = new TcpToUdpTask(VideoChatConfig.TCP_VIDEO_RTP_PORT, VideoChatConfig.VIDEO_RX_PORT);

		mVideoTxTask = new UdpToTcpTask(VideoChatConfig.VIDEO_TX_PORT, VideoChatConfig.TCP_VIDEO_RTP_PORT, dstIp);

		mSendAudioRtcpTxTask = new TcpToUdpTask(VideoChatConfig.TCP_AUDIO_RTCP_TX_PORT,
				VideoChatConfig.AUDIO_RX_PORT + 1);
		mRecvAudioRtcpTxTask = new UdpToTcpTask(VideoChatConfig.AUDIO_TX_PORT + 1,
				VideoChatConfig.TCP_AUDIO_RTCP_TX_PORT, dstIp);

		mSendVideoRtcpTxTask = new TcpToUdpTask(VideoChatConfig.TCP_VIDEO_RTCP_TX_PORT,
				VideoChatConfig.VIDEO_RX_PORT + 1);
		mRecvVideoRtcpTxTask = new UdpToTcpTask(VideoChatConfig.VIDEO_TX_PORT + 1,
				VideoChatConfig.TCP_VIDEO_RTCP_TX_PORT, dstIp);

		mSendAudioRtcpRxTask = new TcpToUdpTask(VideoChatConfig.TCP_AUDIO_RTCP_RX_PORT,
				VideoChatConfig.AUDIO_TX_PORT + 1);
		mRecvAudioRtcpRxTask = new UdpToTcpTask(VideoChatConfig.AUDIO_RX_PORT + 1,
				VideoChatConfig.TCP_AUDIO_RTCP_RX_PORT, dstIp);

		mSendVideoRtcpRxTask = new TcpToUdpTask(VideoChatConfig.TCP_VIDEO_RTCP_RX_PORT,
				VideoChatConfig.VIDEO_TX_PORT + 1);
		mRecvVideoRtcpRxTask = new UdpToTcpTask(VideoChatConfig.VIDEO_RX_PORT + 1,
				VideoChatConfig.TCP_VIDEO_RTCP_RX_PORT, dstIp);

		mAudioRxTask.start();
		mAudioTxTask.start();
		mVideoRxTask.start();
		mVideoTxTask.start();

		mSendAudioRtcpTxTask.start();
		mRecvAudioRtcpTxTask.start();
		mSendVideoRtcpTxTask.start();
		mRecvVideoRtcpTxTask.start();
		mSendAudioRtcpRxTask.start();
		mRecvAudioRtcpRxTask.start();
		mSendVideoRtcpRxTask.start();
		mRecvVideoRtcpRxTask.start();

	}

	public void stop() {
		if (mAudioRxTask != null) {
			mAudioRxTask.stopTask();
		}
		if (mAudioTxTask != null) {
			mAudioTxTask.stopTask();
		}
		if (mVideoRxTask != null) {
			mVideoRxTask.stopTask();
		}
		if (mVideoTxTask != null) {
			mVideoTxTask.stopTask();
		}

		if (mSendAudioRtcpTxTask != null) {
			mSendAudioRtcpTxTask.stopTask();
		}
		if (mRecvAudioRtcpTxTask != null) {
			mRecvAudioRtcpTxTask.stopTask();
		}
		if (mSendVideoRtcpTxTask != null) {
			mSendVideoRtcpTxTask.stopTask();
		}
		if (mRecvVideoRtcpTxTask != null) {
			mRecvVideoRtcpTxTask.stopTask();
		}
		if (mSendAudioRtcpRxTask != null) {
			mSendAudioRtcpRxTask.stopTask();
		}
		if (mRecvAudioRtcpRxTask != null) {
			mRecvAudioRtcpRxTask.stopTask();
		}
		if (mSendVideoRtcpRxTask != null) {
			mSendVideoRtcpRxTask.stopTask();
		}
		if (mRecvVideoRtcpRxTask != null) {
			mRecvVideoRtcpRxTask.stopTask();
		}

		mAudioRxTask = null;
		mAudioTxTask = null;
		mVideoRxTask = null;
		mVideoTxTask = null;

		mSendAudioRtcpTxTask = null;
		mRecvAudioRtcpTxTask = null;
		mSendVideoRtcpTxTask = null;
		mRecvVideoRtcpTxTask = null;
		mSendAudioRtcpRxTask = null;
		mRecvAudioRtcpRxTask = null;
		mSendVideoRtcpRxTask = null;
		mRecvVideoRtcpRxTask = null;
	}

	private class UdpToTcpTask extends Thread {

		private int iSrcPort = 0;
		private int iDstPort = 0;
		private String mDstIp = null;
		private Socket tcpSocket = null;
		private DataOutputStream outputStream = null;
		private DatagramSocket udpSocket = null;

		public UdpToTcpTask(int srcPort, int dstPort, String dstIp) {
			this.iSrcPort = srcPort;
			this.iDstPort = dstPort;
			this.mDstIp = dstIp;
		}

		private boolean tcpConnect() {
			boolean ret = false;
			tcpSocket = new Socket();
			SocketAddress serverAddress = new InetSocketAddress(mDstIp, iDstPort);

			// --设置连接服务器3秒超时,防止连接服务器卡死
			try {
				tcpSocket.connect(serverAddress, 1000);
				DEBUG("CONNECT OK!");
				// --设置输入输出句柄
				outputStream = new DataOutputStream(tcpSocket.getOutputStream());
				ret = true;
			} catch (NumberFormatException e) {
				DEBUG("================");
				DEBUG(" onConnect NumberFormatException ");
				e.printStackTrace();
				DEBUG("================");
				tcpSocket = null;
			} catch (UnknownHostException e) {
				DEBUG("================");
				DEBUG(" onConnect UnknownHostException ");
				e.printStackTrace();
				DEBUG("================");
				tcpSocket = null;
			} catch (IOException e) {
				DEBUG("================");
				DEBUG(" onConnect IOException ");
				e.printStackTrace();
				DEBUG("================");
				tcpSocket = null;
			}

			return ret;

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int retry = 0;

			for (retry = 0; retry < 5; retry++) {
				DEBUG("retry:" + retry);
				if (tcpConnect()) {
					break;
				}
			}

			if (retry == 10) {
				return;
			}

			try {
				DEBUG("UdpToTcp srcPort:" + iSrcPort);
				udpSocket = new DatagramSocket(iSrcPort, InetAddress.getByName("127.0.0.1"));

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DEBUG("UdpToTcp udpSocket:" + udpSocket);
			if (udpSocket == null) {
				return;
			}

			byte[] recvBuf = new byte[1500];
			DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
			DEBUG("start udp recv to tcp send...");
			while (!interrupted()) {
				try {
					// --接收到的数据发送出去
					udpSocket.receive(packet);

					if (packet.getLength() > 0 && outputStream != null) {
						// DEBUG(iSrcPort+" send "+iDstPort+"
						// len:"+packet.getLength());
						outputStream.write(packet.getData(), 0, packet.getLength());
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		public void stopTask() {
			interrupt();
			if (udpSocket != null) {
				udpSocket.close();
			}
			if (tcpSocket != null) {
				try {
					tcpSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class TcpToUdpTask extends Thread {

		private int iSrcPort = 0;
		private int iDstPort = 0;
		private DatagramSocket udpSocket = null;
		private ServerSocket tcpSocket = null;
//		private Socket remoteSocket = null;
		private ClientTask clientTask = null;
		private InetAddress address;

		public TcpToUdpTask(int srcPort, int dstPort) {
			iSrcPort = srcPort;
			iDstPort = dstPort;

			try {
				address = InetAddress.getByName("127.0.0.1");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			try {
				udpSocket = new DatagramSocket();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (udpSocket == null) {
				return;
			}
			try {
				tcpSocket = new ServerSocket(iSrcPort);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DEBUG("TcpToUdp tcpSocket:" + tcpSocket);
			if (tcpSocket == null) {
				return;
			}

			while (!interrupted()) {
				Socket socket = null;
				try {
					socket = tcpSocket.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					return;
				}
				if (clientTask != null) {
					clientTask.stopTask();
					clientTask = null;
				}
				DEBUG("new remote accept!");
				// DEBUG("remoteSocket:"+remoteSocket.getInetAddress().getHostName());
				clientTask = new ClientTask(socket);
				clientTask.start();

			}
		}

		private class ClientTask extends Thread {

			private DataInputStream inputStream = null;
			private byte[] recvBuf = new byte[1500];
			private Socket mSocket = null;

			public ClientTask(Socket socket) {
				// TODO Auto-generated constructor stub
				mSocket = socket;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					inputStream = new DataInputStream(mSocket.getInputStream());// 2.23此处报空。崩
					// --使能常连接探测
					mSocket.setKeepAlive(true);
					// --设置心跳时间
					mSocket.setSoTimeout(1500);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while (!interrupted()) {
					int ret = 0;
					try {
						ret = inputStream.read(recvBuf);
						if (ret > 0 && udpSocket != null) {
							DEBUG("recvlen:" + ret);

							// --将接收到的TCP数据发送给UDP

							DatagramPacket packet = new DatagramPacket(recvBuf, 0, ret, address, iDstPort);
							udpSocket.send(packet);
						}
					} catch (SocketTimeoutException e) {// 2.18甘
						DEBUG("read timeout!");
					} catch (IOException e) {
						DEBUG("=== IOException ===");
						// TODO Auto-generated catch block
						e.printStackTrace();
						// --断开链路
						stopTask();
					}
				}
			}

			public void stopTask() {
				if (mSocket != null) {
					clientTask.interrupt();
					try {
						mSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mSocket = null;

				}
			}

		}

		public void stopTask() {
			if (clientTask != null) {
				clientTask.stopTask();
			}

			interrupt();
			if (tcpSocket != null) {
				try {

					tcpSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
