package com.advertisement.network;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Message;

import com.advertisement.system.ConstData;
import com.advertisement.system.SystemConfig;
import com.advertisement.system.SystemManager;
import com.common.Des;
import com.common.Unit;

public class NetworkParamExec {

	private static final String TAG="NetworkParamExec";
	public abstract class ParamExec{
		public int ParamId=0;
		public ParamExec(int id) {
			this.ParamId=id;
		}
		
		public abstract boolean onSet(byte[] inBuf,int offset,int size);
		public abstract int onGet(byte[] outBuf,int offset,int size);
	}
	//--参数1000
	//--服务器端口号
	public ParamExec Param1000=new ParamExec(1000) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			String port=new String(inBuf, offset, size);
			
			SystemManager.getSystemManager().getConfig().serverPortNum=Integer.valueOf(port);
			SystemManager.getSystemManager().getConfig().serverPortNum2=Integer.valueOf(port);
			
			SystemManager.getSystemManager().getConfig().writeParam("1000", SystemManager.getSystemManager().getConfig().serverPortNum);
			SystemManager.getSystemManager().getConfig().writeParam("F002", SystemManager.getSystemManager().getConfig().serverPortNum2);
			
			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {
			String str=String.valueOf(SystemManager.getSystemManager().getConfig().serverPortNum);
			byte[] b=str.getBytes();
			System.arraycopy(b, 0, outBuf, offset, b.length);
			return b.length;
		}
	};
	
	public ParamExec Param1001=new ParamExec(1001) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	public ParamExec Param1002=new ParamExec(1002) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	//--参数1003
	//--终端密钥
	public ParamExec Param1003=new ParamExec(1003) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {
			
			SystemManager.LOGD(TAG, "=== Param1003 ===");
			byte[] buf=new byte[size];
			System.arraycopy(inBuf, offset, buf, 0, size);
			
			SystemManager.LOGD(TAG, "update key:"+(new String(buf)));
			try {
				byte[] seckey = new byte[8];
				byte[] key = new byte[8];
				//--平台下发的终端密钥为字符串
				Unit.AsciiToHexByte(seckey, buf);
				SystemManager.LOGD(TAG,"SECKEY HEX:");
				SystemManager.LogHex(TAG, seckey, 8);
				//--利用平台下发的密钥解密当前终端输入的随机验证码
				key=Des.decrypt(seckey, SystemManager.getSystemManager().getConfig().installKey.getBytes());
				
				SystemManager.LOGD(TAG,"NEW HEX:");
				SystemManager.LogHex(TAG, key, 8);
				
				//--保存当前解密出的key
				byte[] newSecKey = new byte[16];
				Unit.HexToAsciiByte(newSecKey, key);
				SystemManager.getSystemManager().getConfig().tmSecretKey=new String(newSecKey);
				SystemManager.getSystemManager().getConfig().writeParam("0x1003", SystemManager.getSystemManager().getConfig().tmSecretKey);
				SystemManager.getSystemManager().getConfig().tmSecretKey=SystemManager.getSystemManager().getConfig().readParam("0x1003",
						SystemManager.getSystemManager().getConfig().tmSecretKey);
				SystemManager.LOGD(TAG, "seckey:"+SystemManager.getSystemManager().getConfig().tmSecretKey);
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	//--参数1004
	//--同步平台时间
	public ParamExec Param1004=new ParamExec(1004) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return true;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	public ParamExec Param1005=new ParamExec(1005) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	public ParamExec Param1006=new ParamExec(1006) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	//--查询软件版本
	public ParamExec Param1007=new ParamExec(1007) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			Context context=SystemManager.getSystemManager().getContext();
			PackageManager  pm=context.getPackageManager();
			
			try {
				String version=pm.getPackageInfo(context.getPackageName(),0).versionName;
				
				byte[] tmp=version.getBytes();
				
				System.arraycopy(tmp, 0, outBuf, offset, tmp.length);
				
				return tmp.length;
				
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}
	};
	
	public ParamExec Param1008=new ParamExec(1008) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	public ParamExec Param1009=new ParamExec(1009) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			SystemManager.LOGD(TAG, "=== Param1009 ===");
			byte[] buf=new byte[size];
			SystemConfig config=SystemManager.getSystemManager().getConfig();
			System.arraycopy(inBuf,offset, buf, 0, size);
			config.tmId=new String(buf);
			config.writeParam("0x1009",config.tmId);
			
			SystemManager.LOGD(TAG, "===[Terminal ID:"+config.tmId+"]===");
			
			if(!config.installState){
				
				config.installState=true;
			}
			SystemManager.LOGD(TAG, "Install tm:"+config.installState);
			return true;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	public ParamExec Param1010=new ParamExec(1010) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	//--容量查询和设置
	public ParamExec Param1011=new ParamExec(1011) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			long space=SystemManager.getSdcardUsableSpace();
			space=space/1024; //--转换城KB
			String str=String.valueOf(space);
			byte[] tmp=str.getBytes();
			System.arraycopy(tmp, 0, outBuf, offset, tmp.length);
			return tmp.length;
		}
	};
	
	public ParamExec Param1018=new ParamExec(1018) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	public ParamExec Param1019=new ParamExec(1019) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};

	public ParamExec Param1020=new ParamExec(1020) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	public ParamExec Param1021=new ParamExec(1021) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	public ParamExec Param1022=new ParamExec(1022) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1023=new ParamExec(1023) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	//--音量设置，范围0-56
	public ParamExec Param1024=new ParamExec(1024) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			String str=new String(inBuf,offset,size);
			int vol=Integer.valueOf(str);
			
			AudioManager audioManager=(AudioManager)SystemManager.getSystemManager().getContext().getSystemService(Service.AUDIO_SERVICE);
			
			if(vol!=0){//--解除静音
				SystemManager.getSystemManager().getConfig().writeParam("0x1025", false);
				SystemManager.getSystemManager().getConfig().isMute=false;
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			}
			
			
			int max=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			
			vol=vol/57*max;
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
			
			SystemManager.getSystemManager().getConfig().writeParam("0x1024", vol);
			SystemManager.getSystemManager().getConfig().setVolume=vol;
			
			SystemManager.LOGI("Param","set volum:"+str);
			SystemManager.LOGI("Param","max volum:"+max);
			SystemManager.LOGI("Param","cur volum:"+vol);
			
			
			
			return true;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	//--静音
	public ParamExec Param1025=new ParamExec(1025) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {
			
			AudioManager audioManager=(AudioManager)SystemManager.getSystemManager().getContext().getSystemService(Service.AUDIO_SERVICE);
			audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
			SystemManager.LOGI("Param","set mute");
			SystemManager.getSystemManager().getConfig().writeParam("0x1025", true);
			SystemManager.getSystemManager().getConfig().isMute=true;
			return true;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {
			
			return 0;
		}
	};
	public ParamExec Param1026=new ParamExec(1026) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1027=new ParamExec(1027) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1028=new ParamExec(1028) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1029=new ParamExec(1029) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1030=new ParamExec(1030) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1031=new ParamExec(1031) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1032=new ParamExec(1032) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1033=new ParamExec(1033) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1034=new ParamExec(1034) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1035=new ParamExec(1035) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1036=new ParamExec(1036) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1037=new ParamExec(1037) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1038=new ParamExec(1038) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1039=new ParamExec(1039) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	public ParamExec Param1040=new ParamExec(1040) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {
			SystemManager.LOGD(TAG,"======================");
			SystemManager.LOGD(TAG," Param1040 GET ");
			SystemManager.LOGD(TAG,"======================");
			return 0;
		}
	};
	public ParamExec Param1041=new ParamExec(1041) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {

			return false;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	//--音量分段控制
	public ParamExec Param1044=new ParamExec(1044) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {
			String vol=new String(inBuf,offset,size);
			SystemManager.getSystemManager().getConfig().writeParam("0x1044", vol);
			SystemManager.getSystemManager().getConfig().volumeGroup=vol;
			SystemManager.LOGD(TAG,"Volum Group:"+vol);
			
			return true;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	public ParamExec Param2000=new ParamExec(2000) {
		
		@Override
		public boolean onSet(byte[] inBuf, int offset, int size) {
			if(inBuf[offset]=='1'){
				
				Message msg=new Message();
				msg.what=ConstData.SystemMsgType.MSG_REBOOT;
				SystemManager.getSystemManager().sendMsgToSystem(msg);
			}
			if(inBuf[offset]=='2'){
				
				Message msg=new Message();
				msg.what=ConstData.SystemMsgType.MSG_SHUTDOWN;
				SystemManager.getSystemManager().sendMsgToSystem(msg);
			}
			if(inBuf[offset]=='3'){
				
				Message msg=new Message();
				msg.what=ConstData.SystemMsgType.MSG_POWERON;
				SystemManager.getSystemManager().sendMsgToSystem(msg);
			}
			return true;
		}
		
		@Override
		public int onGet(byte[] outBuf, int offset, int size) {

			return 0;
		}
	};
	
	private ArrayList<ParamExec> mParamExecList=new ArrayList<ParamExec>();
	
	public NetworkParamExec() {
		mParamExecList.add(Param1000);
		mParamExecList.add(Param1001);
		mParamExecList.add(Param1002);
		mParamExecList.add(Param1003);
		mParamExecList.add(Param1004);
		mParamExecList.add(Param1005);
		mParamExecList.add(Param1006);
		mParamExecList.add(Param1007);
		mParamExecList.add(Param1008);
		mParamExecList.add(Param1009);
		mParamExecList.add(Param1010);
		mParamExecList.add(Param1011);
		mParamExecList.add(Param1018);
		mParamExecList.add(Param1019);
		mParamExecList.add(Param1020);
		mParamExecList.add(Param1021);
		mParamExecList.add(Param1022);
		mParamExecList.add(Param1023);
		mParamExecList.add(Param1024);
		mParamExecList.add(Param1025);
		mParamExecList.add(Param1026);
		mParamExecList.add(Param1027);
		mParamExecList.add(Param1028);
		mParamExecList.add(Param1029);
		mParamExecList.add(Param1030);
		mParamExecList.add(Param1031);
		mParamExecList.add(Param1032);
		mParamExecList.add(Param1033);
		mParamExecList.add(Param1034);
		mParamExecList.add(Param1035);
		mParamExecList.add(Param1036);
		mParamExecList.add(Param1037);
		mParamExecList.add(Param1038);
		mParamExecList.add(Param1039);
		mParamExecList.add(Param1040);
		mParamExecList.add(Param1041);
		mParamExecList.add(Param1044);
		mParamExecList.add(Param2000);
	}
	
	public ArrayList<ParamExec> getParamList(){
		return mParamExecList;
	}
}
