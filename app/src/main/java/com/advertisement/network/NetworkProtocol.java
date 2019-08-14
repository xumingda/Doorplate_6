package com.advertisement.network;

import com.advertisement.system.SystemManager;

public class NetworkProtocol {

	public class FrameHead{
		public static final int FRAME_HEAD_LEN=12; //--����ͷ����
		
		public static final int FLAG_SYN=0X80;//--����Ự
		public static final int FLAG_FIN=0X40;//--�����Ự
		public static final int FLAG_EOT=0x20;//--Ӧ�ñ���
		
		public static final int RSVD_APP=0;//--Ĭ�ϱ���
		public static final int RSVD_ALIVE=1;//--��������
		public static final int RSVD_ACK=2;//--�������ط��صı���
		
		public static final int CONNID_DEF_NO=4;//--�Ự����˿ں�,ֵ��0-15
		public static final int CONNID_DEF_ID=0;//--�ỰID��������ƽ̨���ã��ն�Ĭ��Ϊ0
		
		public int flag;  //--���ı��
		public int rsvd;  //--�����������
		public int conn_id; //--�Ự���
		public int tx_sn;  //--�������к�
		public int ack_sn; //--Ӧ�����к�
		public int sum;    //--����У���
		public int len;    //--�����峤��
		
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
		public int msg_code; //--��������
		public int msg_len;  //--�������ݳ���,����msg_sn�ֶ�2�ֽ�
		public int msg_sn;   //--������ˮ
		public byte[] msg_body;//--������
		
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
			System.arraycopy(msg_body, 0, data, offer, msg_len-2);//--��ȥ2�ֽڵ���ˮ��
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
			msg_body=new byte[msg_len-2];//--��ȥ2�ֽڵ���ˮ��
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
