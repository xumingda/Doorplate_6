package com.anychat;

import android.content.Context;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatTransDataEvent;
public class VideoChatMessage implements AnyChatTransDataEvent{

	private Context mContext=null;
	private AnyChatCoreSDK mAnyChat=null;
	private int mStatus=0;
	
	public static final int REMOTE_REPLY_CANCEL=0;            //--远端应答，拒绝请求
	public static final int REMOTE_REPLY_VIDEOCALL=1;         //--远端应答，同意对讲，使用对讲通道
	public static final int REMOTE_REPLY_BROADCAST=2;         //--远端应答，同意对讲，使用喊话通道
	public static final int REMOTE_REQUEST_BROADCAST=3;       //--远端请求，喊话
	public static final int REMOTE_REQUEST_LED_ON=4;          //--远端请求，开启警灯
	public static final int REMOTE_REQUEST_LED_OFF=5;         //--远端请求，关闭警灯
	public static final int REMOTE_REQUEST_LIGHT_ON=6;        //--远端请求，开启大灯
	public static final int REMOTE_REQUEST_LIGHT_OFF=7;       //--远端请求，关闭大灯
	public static final int REMOTE_REQUEST_SWITCH=8;          //--远端请求，转接
	public static final int REMOTE_REQUEST_BROADCAST_ON=9;    //--开启高音喇叭
	public static final int REMOTE_REQUEST_BROADCAST_OFF=10;  //--关闭高音喇叭
	
	public static final int REPORT_SESSION_START=1;           //--本地通知其他用户会话开始
	public static final int REPORT_SESSION_STOP=0;            //--本地通知其他用户会话结束
	

	public interface VideoChatMsgCallback{
		public abstract void onRequestCallback(int userId,int errcode);
		public abstract void onRemoteRequestCallback(int userId,int errcode);
	}
	
	private VideoChatMsgCallback mCallback=null;
	
	private class Packet{
		
		private static final byte CMD_REQUEST=0X01;
		private static final byte CMD_REPLY=0X02;
		private static final byte CMD_CANCEL=0X03;
		private static final byte CMD_REPORT=0X04;
		
		public byte cmd=0x0;
		public byte len=0x0;
		public byte[] data=null;
		public byte xor=0x0;
		
		public int toBytes(byte[] out,int offset){
			int offer=0;
			out[offset+offer]=cmd;
			offer++;
			out[offset+offer]=len;
			offer++;
			if(len>0){
				System.arraycopy(data, 0, out, offer+offset, (int)(len&0xff));
				offer+=(int)(len&0xff);
			}
			xor=0x0;
			for(int i=0;i<offer;i++){
				xor^=out[i+offset];
			}
			out[offer+offset]=xor;
			offer++;
			return offer;
		}
		
		public boolean toPacket(byte[] in,int offset){
			int offer=0;
			cmd=in[offset+offer];
			offer++;
			len=in[offset+offer];
			offer++;
			if(len>0){
				data=new byte[len];
				System.arraycopy(in, offset+offer, data, 0, (int)(len&0xff));
				offer+=(int)(len&0xff);
			}
			xor=0x00;
			for(int i=0;i<offer;i++){
				xor^=in[i+offset];
			}
			if(xor==in[offer+offset]){
				return true;
			}
			
			return false;
		}
	}
	
	public VideoChatMessage(Context context,AnyChatCoreSDK anychat,VideoChatMsgCallback callback) {
		mContext=context;
		mAnyChat=anychat;
		mAnyChat.SetTransDataEvent(this);
		mCallback=callback;
	}
	
	/**
	 * ======================================================
	 *                数据传输回调接口
	 * ======================================================
	 **/
	@Override
	public void OnAnyChatTransFile(int dwUserid, String FileName,
			String TempFilePath, int dwFileLength, int wParam, int lParam,
			int dwTaskId) {
		
		
	}
	@Override
	public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {
		VideoChatCore.DEBUG("=============================================");
		VideoChatCore.DEBUG(" OnAnyChatTransBuffer:");
		VideoChatCore.DEBUG(" dwUserid:"+dwUserid);
		VideoChatCore.DEBUG(" dwLen:"+dwLen);
		VideoChatCore.DEBUG_HEX(lpBuf,dwLen);
		VideoChatCore.DEBUG("=============================================");
		VideoChatCore.DEBUG(" mStatus:"+mStatus);
	
		Packet packet=new Packet();
		if(packet.toPacket(lpBuf, 0)){
			if(packet.cmd==packet.CMD_REPLY&&packet.len==1){
				
				mStatus=0;
				
				if(mCallback!=null){
					mCallback.onRequestCallback(dwUserid,(int)packet.data[0]);
				}
			}
			if(packet.cmd==packet.CMD_REQUEST&&packet.len==1){
				VideoChatCore.DEBUG(" CMD_REQUEST:");
				if(mCallback!=null){
					mCallback.onRemoteRequestCallback(dwUserid,(int)packet.data[0]);
				}
			}
		}else{
			VideoChatCore.DEBUG("PACKET ERROR!");
		}
		
		
	}
	@Override
	public void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen,
			int wparam, int lparam, int taskid) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {
		// TODO Auto-generated method stub
		
	}
	
	public void msgRequest(int userId){

		Packet packet=new Packet();
		packet.cmd=Packet.CMD_REQUEST;
		packet.len=0;
		byte[] sendbuf=new byte[10];
		int sendlen=packet.toBytes(sendbuf, 0);
		mStatus=1;
		mAnyChat.TransBuffer(userId, sendbuf, sendlen);
		packet=null;
	}
	
	public void msgReply(int userId,boolean isSure){
		Packet packet=new Packet();
		packet.cmd=Packet.CMD_REPLY;
		packet.len=1;
		byte[] sendbuf=new byte[10];
		packet.data=new byte[1];
		if(isSure){
			packet.data[0]=0X01;
		}else{
			packet.data[0]=0x00;
		}
		int sendlen=packet.toBytes(sendbuf, 0);
		mAnyChat.TransBuffer(userId, sendbuf, sendlen);
		packet=null;
	}
	
	public void msgCancel(int userId){
		Packet packet=new Packet();
		packet.cmd=Packet.CMD_CANCEL;
		packet.len=0;
		byte[] sendbuf=new byte[10];
		int sendlen=packet.toBytes(sendbuf, 0);
		mAnyChat.TransBuffer(userId, sendbuf, sendlen);
		packet=null;
		mStatus=0;
	}
	
	public void msgReset(){
		mStatus=0;
	}
	
	public void msgReportSession(int userId,boolean isStart){
		Packet packet=new Packet();
		packet.cmd=Packet.CMD_REPORT;
		packet.len=1;
		packet.data=new byte[1];
		if(isStart){
			packet.data[0]=REPORT_SESSION_START;
		}else{
			packet.data[0]=REPORT_SESSION_STOP;
		}
		byte[] sendbuf=new byte[10];
		int sendlen=packet.toBytes(sendbuf, 0);
		mAnyChat.TransBuffer(userId, sendbuf, sendlen);
		packet=null;
	}
	

}
