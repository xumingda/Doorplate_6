package com.advertisement.network;

import com.advertisement.system.SystemManager;

public class NetworkProtocol {

	public class FrameHead{
		public static final int FRAME_HEAD_LEN=12; //--报文头长度
		
		public static final int FLAG_SYN=0X80;//--请求会话
		public static final int FLAG_FIN=0X40;//--结束会话
		public static final int FLAG_EOT=0x20;//--应用报文
		
		public static final int RSVD_APP=0;//--默认报文
		public static final int RSVD_ALIVE=1;//--心跳报文
		public static final int RSVD_ACK=2;//--接入网关返回的报文
		
		public static final int CONNID_DEF_NO=4;//--会话服务端口号,值域0-15
		public static final int CONNID_DEF_ID=0;//--会话ID，由网关平台设置，终端默认为0
		
		public int flag;  //--报文标记
		public int rsvd;  //--报文心跳标记
		public int conn_id; //--会话序号
		public int tx_sn;  //--发送序列号
		public int ack_sn; //--应答序列号
		public int sum;    //--报文校验和
		public int len;    //--报文体长度
		
		public FrameHead() {
			// TODO Auto-generated constructor stub
			flag=0;
			rsvd=0;
			conn_id=0;
			tx_sn=0;
			ack_sn=0;
			sum=0;
			len=0;
		}
		
		public int toBytes(byte[] data,int offer){
			data[offer++]=(byte)(flag&0xff);
			data[offer++]=(byte)(rsvd&0xff);
			data[offer++]=(byte)(conn_id&0xff);
			data[offer++]=(byte)((conn_id>>8)&0xff);
			data[offer++]=(byte)(tx_sn&0xff);
			data[offer++]=(byte)((tx_sn>>8)&0xff);
			data[offer++]=(byte)(ack_sn&0xff);
			data[offer++]=(byte)((ack_sn>>8)&0xff);
			data[offer++]=(byte)(sum&0xff);
			data[offer++]=(byte)((sum>>8)&0xff);
			data[offer++]=(byte)(len&0xff);
			data[offer++]=(byte)((len>>8)&0xff);
			return offer;
		}
		
		public void toHead(byte[] data,int offer){
			flag=(int)(data[offer++]&0xff);
			rsvd=(int)(data[offer++]&0xff);
			conn_id=(int)(data[offer++]&0xff);
			conn_id+=((int)(data[offer++]&0xff)*256);
			tx_sn=(int)(data[offer++]&0xff);
			tx_sn+=((int)(data[offer++]&0xff)*256);
			ack_sn=(int)(data[offer++]&0xff);
			ack_sn+=((int)(data[offer++]&0xff)*256);
			sum=(int)(data[offer++]&0xff);
			sum+=((int)(data[offer++]&0xff)*256);
			len=(int)(data[offer++]&0xff);
			len+=((int)(data[offer++]&0xff)*256);
		}
	}
	
	public class FrameBody{
		public int msg_code; //--报文命令
		public int msg_len;  //--报文数据长度,包含msg_sn字段2字节
		public int msg_sn;   //--命令流水
		public byte[] msg_body;//--报文体
		
		public FrameBody(){
			msg_code=0;
			msg_len=0;
			msg_body=null;
		}
		public int toBytes(byte[] data,int offer){
			data[offer++]=(byte)(msg_code&0xff);
			data[offer++]=(byte)((msg_code>>8)&0xff);
			data[offer++]=(byte)(msg_len&0xff);
			data[offer++]=(byte)((msg_len>>8)&0xff);
			data[offer++]=(byte)(msg_sn&0xff);
			data[offer++]=(byte)((msg_sn>>8)&0xff);
			System.arraycopy(msg_body, 0, data, offer, msg_len-2);//--减去2字节的流水号
			offer+=(msg_len-2);
			return offer;
		}
		
		public void toBody(byte[] data,int offer){
			msg_code=(int)(data[offer++]&0xff);
			msg_code+=((int)(data[offer++]&0xff)*256);
			msg_len=(int)(data[offer++]&0xff);
			msg_len+=((int)(data[offer++]&0xff)*256);
			msg_sn=(int)(data[offer++]&0xff);
			msg_sn+=((int)(data[offer++]&0xff)*256);
			msg_body=new byte[msg_len-2];//--减去2字节的流水号
			System.arraycopy(data, offer, msg_body, 0, msg_len-2);
		}
	}
	
	public FrameHead head=new FrameHead();
	public FrameBody body=new FrameBody();
	
	public int calcCheckSum(byte[] data){
		int sum=0;
		int i=0;
		for(i=0;i<FrameHead.FRAME_HEAD_LEN;i++){
			sum+=(data[i]&0xff);
		}
	
		if(head.len==0){
			return sum;
		}
		for(i=FrameHead.FRAME_HEAD_LEN;i<(head.len+FrameHead.FRAME_HEAD_LEN);i++){
			sum+=(data[i]&0xff);
		}
	
		return sum;
	}
	
	public int calcCheckSum(byte[] data ,int offer){
		int sum=0;
		int i=0;
		for(i=0;i<FrameHead.FRAME_HEAD_LEN;i++){
			sum+=(data[i+offer]&0xff);
		}
	
		if(head.len==0){
			return sum;
		}
		for(i=FrameHead.FRAME_HEAD_LEN;i<(head.len+FrameHead.FRAME_HEAD_LEN);i++){
			sum+=(data[i+offer]&0xff);
		}
	
		return sum;
	}
}
