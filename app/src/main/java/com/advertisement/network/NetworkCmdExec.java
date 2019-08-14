package com.advertisement.network;

import java.util.ArrayList;

import android.content.IntentSender.SendIntentException;
import android.os.Message;

import com.advertisement.network.NetworkParamExec.ParamExec;
import com.advertisement.network.NetworkProtocol.FrameHead;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemConfig;
import com.advertisement.system.SystemManager;
import com.common.Des;
import com.common.Unit;

public class NetworkCmdExec {

	private static final String TAG="NetworkCmdExec";
	public abstract class CmdExec{
		int cmdId=0;
		public CmdExec(int cmdId) {
			// TODO Auto-generated constructor stub
			this.cmdId=cmdId;
		}
		public abstract void onExec(NetworkProcess process,
				byte[] data,int datalen,NetworkProtocol  packet);
	}
	
	//--命令：0X00D0
	//--安装设备
	public  CmdExec Cmd00D0=new CmdExec(0X00D0) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {

		}
	};
	
	//--命令：0X00D1
	//--平台身份认证请求
	public  CmdExec Cmd00D1=new CmdExec(0X00D1) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {
			SystemManager.LOGD(TAG,"======================");
			SystemManager.LOGD(TAG," Cmd00D1 ");
			SystemManager.LOGD(TAG,"======================");
			//--查询是否安装，若未安装就请求安装
			if(!SystemManager.getSystemManager().getConfig().installState){
				//--请求安装设备
				process.requestInstall(packet.head);
				return;
			}
			
			//--身份认证
			NetworkProtocol ackPacket=new NetworkProtocol();
			ackPacket.head.flag=FrameHead.FLAG_EOT;
			ackPacket.head.rsvd=FrameHead.RSVD_APP;
			ackPacket.head.conn_id=packet.head.conn_id;
			ackPacket.head.ack_sn=packet.head.ack_sn;
			ackPacket.head.len=4+28;
			//--请求认证
			ackPacket.body.msg_code=0X00D2;
			ackPacket.body.msg_len=ackPacket.head.len-4;
			ackPacket.body.msg_sn=packet.body.msg_sn;
			ackPacket.body.msg_body=new byte[ackPacket.body.msg_len-2];
			try {
				SystemConfig config=SystemManager.getSystemManager().getConfig();
				byte[] secKey=new byte[8];
				//--将字符串保存的key转成16进制
				Unit.AsciiToHexByte(secKey, config.tmSecretKey.getBytes());
				//--加密平台的字符串
				byte[] result=Des.encrypt(packet.body.msg_body,secKey);
				//--拷贝填充结果
				System.arraycopy(result, 0, ackPacket.body.msg_body, 0, 8);
				//--终端随机字符串
				for(int i=0;i<8;i++){
					ackPacket.body.msg_body[i+8]=(byte)0xff;
				}
				//--填充终端号
				System.arraycopy(config.tmId.getBytes(), 0, ackPacket.body.msg_body, 16, 10);
				
				process.sendAckPacket(ackPacket);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	//--命令：0X00D2
	//--应答终端身份认证
	public  CmdExec Cmd00D2=new CmdExec(0X00D2) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {
			
			//--当平台应答该步骤时，表明此时认证成功
			SystemConfig config=SystemManager.getSystemManager().getConfig();
			config.writeParam("0xA000", true);
			config.installState=config.readParam("0xA000", false);
			//--上报系统管理器安装状态
			Message msg=new Message();
			msg.what=ConstData.SystemMsgType.MSG_REPORT_INSTALL_STATUS;
			process.mNetworkManager.mSystemManager.sendMsgToSystem(msg);
		}
	};
	
	//--命令：0X00D3
	//--请求传输文件
	public CmdExec Cmd00D3=new CmdExec(0X00D3) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {

		}
	};
	
	//--命令0X00D4
	//--平台向终端请求下发文件
	public CmdExec Cmd00D4=new CmdExec(0X00D4) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {
			SystemManager.LOGD(TAG,"======================");
			SystemManager.LOGD(TAG," Cmd00D4 ");
			SystemManager.LOGD(TAG,"======================");
			int fileType=0;
			int urlLen=0;
			String fileUrl=null;
			int offer=0;
			SystemConfig config=SystemManager.getSystemManager().getConfig();
			fileType=(int)(packet.body.msg_body[offer++]&0xff);
			fileType+=(int)((packet.body.msg_body[offer++]&0xff)*256);
			urlLen=(int)(packet.body.msg_body[offer++]&0xff);
			urlLen+=(int)((packet.body.msg_body[offer++]&0xff)*256);
			byte[] secUrl=new byte[urlLen/2];
			//--将ASCII表示的16进制数转换成实际数据
			Unit.AsciiToHexByte(secUrl,0,packet.body.msg_body,offer,urlLen);
			SystemManager.LOGD(TAG,"secUrl:");
			SystemManager.LogHex(TAG,secUrl,urlLen/2);
			
			byte[] secKey=new byte[8];
			Unit.AsciiToHexByte(secKey,config.tmSecretKey.getBytes());
			try {
				byte[] url=Des.decrypt(secUrl, secKey);
				
				fileUrl=new String(url,0,Unit.IndexOfByte(url, 0, url.length, (byte)0x00));
				SystemManager.LOGD(TAG," fileType:"+fileType);
				SystemManager.LOGD(TAG," fileUrl:"+fileUrl);
				
				Message msg=new Message();
				msg.what=ConstData.ResMsgType.MSG_ADD_DOWNLOAD;
				msg.obj=fileUrl;
				msg.arg1=fileType;
				SystemManager.getSystemManager().sendMsgToManager(
						SystemManager.getSystemManager().getResManager(), msg);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			NetworkProtocol ackPacket=new NetworkProtocol();
			process.createAckHead(packet.head, ackPacket.head);
			ackPacket.head.len=4+2+1;
			ackPacket.body.msg_code=packet.body.msg_code;
			ackPacket.body.msg_sn=packet.body.msg_sn;
			ackPacket.body.msg_len=ackPacket.head.len-4;
			ackPacket.body.msg_body=new byte[1];
			ackPacket.body.msg_body[0]=0X03;//--收到命令
			process.sendAckPacket(ackPacket);
		}
	};
	
	public CmdExec Cmd00D5=new CmdExec(0X00D5) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {

		}
	};
	
	public CmdExec Cmd00D6=new CmdExec(0X00D6) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {

		}
	};
	
	//--命令0X00D7
	//--查询参数
	public CmdExec Cmd00D7=new CmdExec(0X00D7) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {
			SystemManager.LOGD(TAG,"======================");
			SystemManager.LOGD(TAG," Cmd00D7 ");
			SystemManager.LOGD(TAG,"======================");	
			int offer=0;
			int paramTotal=(int)(packet.body.msg_body[offer++]&0xff);
			byte[] getBuf=new byte[500];
			int id=0;
			int len=0;
			//--填充应答参数总数
			getBuf[len++]=packet.body.msg_body[0];
			for(int i=0;i<paramTotal;i++){
				//--填充应答参数序号
				getBuf[len++]=packet.body.msg_body[offer];
				getBuf[len++]=packet.body.msg_body[offer+1];
				if((packet.body.msg_body[offer]&0xff)<0xe8){
					id=((int)(packet.body.msg_body[offer++]&0xff))*256;
					id+=((int)(packet.body.msg_body[offer++]&0xff));
				}else{
					id=((int)(packet.body.msg_body[offer++]&0xff));
					id+=((int)(packet.body.msg_body[offer++]&0xff))*256;
				}
				SystemManager.LOGD(TAG,"GET "+id);
				
				for(ParamExec param:mNetworkParamExec.getParamList()){
					if(param.ParamId==id){
						//--读取并填充参数值
						int ret=param.onGet(getBuf, len+1, 0);
						//--填充长度
						getBuf[len]=(byte)(ret&0xff);
						len+=(ret+1);
						break;
					}
				}
			}
			
			NetworkProtocol ackPacket=new NetworkProtocol();
			
			process.createAckHead(packet.head, ackPacket.head);
			ackPacket.head.len=4+2+len;
			ackPacket.body.msg_code=packet.body.msg_code;
			ackPacket.body.msg_sn=packet.body.msg_sn;
			ackPacket.body.msg_len=ackPacket.head.len-4;
			ackPacket.body.msg_body=getBuf;
			
			process.sendAckPacket(ackPacket);
		}
	};
	//--命令0X00D8
	//--更新参数
	public CmdExec Cmd00D8=new CmdExec(0X00D8) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {
			SystemManager.LOGD(TAG,"======================");
			SystemManager.LOGD(TAG," Cmd00D8 ");
			SystemManager.LOGD(TAG,"======================");
			int offer=0;
			int paramTotal=(int)(packet.body.msg_body[offer++]&0xff);
			boolean[] setRet=new boolean[paramTotal];
			int id=0;
			int len=0;
			SystemManager.LOGD(TAG,"Toatl:"+paramTotal);
			for(int i=0;i<paramTotal;i++){
				id=(int)(packet.body.msg_body[offer++]&0xff);
				id+=((int)(packet.body.msg_body[offer++]&0xff))*256;
				len=(int)(packet.body.msg_body[offer++]&0xff);
				SystemManager.LOGD(TAG,"id:"+id+" len:"+len);
				setRet[i]=false;
				for(ParamExec param:mNetworkParamExec.getParamList()){
					if(param.ParamId==id){
						SystemManager.LOGD(TAG,"SET:"+id+" LEN:"+len);
						setRet[i]=param.onSet(packet.body.msg_body, offer, len);
						break;
					}
				}
				offer+=len;
			}
			
			NetworkProtocol ackPacket=new NetworkProtocol();
			
			process.createAckHead(packet.head, ackPacket.head);
			ackPacket.head.len=4+(3*paramTotal)+1+2;
			ackPacket.body.msg_code=packet.body.msg_code;
			ackPacket.body.msg_sn=packet.body.msg_sn;
			ackPacket.body.msg_len=ackPacket.head.len-4;
			ackPacket.body.msg_body=new byte[ackPacket.body.msg_len-2];
			int offer2=0;
			ackPacket.body.msg_body[offer2++]=(byte)(paramTotal&0xff);
			offer=1;
			for(int j=0;j<paramTotal;j++){
				ackPacket.body.msg_body[offer2++]=packet.body.msg_body[offer++];
				ackPacket.body.msg_body[offer2++]=packet.body.msg_body[offer++];
				len=(int)(packet.body.msg_body[offer++]&0xff);
				offer+=len;
				if(setRet[j]){
					ackPacket.body.msg_body[offer2++]=0x00;
				}else{
					ackPacket.body.msg_body[offer2++]=0x02;
				}
			}
			
			process.sendAckPacket(ackPacket);
		}
	};
	
	public CmdExec Cmd00D9=new CmdExec(0X00D9) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {

		}
	};
	
	public CmdExec Cmd00DA=new CmdExec(0X00DA) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {

		}
	};
	
	public CmdExec Cmd00DB=new CmdExec(0X00DB) {
		
		@Override
		public void onExec(NetworkProcess process, byte[] data, int datalen,
				NetworkProtocol packet) {

		}
	};
	
	private static ArrayList<CmdExec> CmdList=new ArrayList<CmdExec>();
	private NetworkParamExec mNetworkParamExec=new NetworkParamExec();
	
	public NetworkCmdExec(){
		CmdList.add(Cmd00D0);
		CmdList.add(Cmd00D1);
		CmdList.add(Cmd00D2);
		CmdList.add(Cmd00D3);
		CmdList.add(Cmd00D4);
		CmdList.add(Cmd00D5);
		CmdList.add(Cmd00D6);
		CmdList.add(Cmd00D7);
		CmdList.add(Cmd00D8);
		CmdList.add(Cmd00D9);
		CmdList.add(Cmd00DA);
		CmdList.add(Cmd00DB);
	}
	
	public ArrayList<CmdExec> getCmdExecList(){
		return CmdList;
	}
}
