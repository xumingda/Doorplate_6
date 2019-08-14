package com.advertisement.system;

public class ConstData {

	
	
	/*Handler消息类型*/
	public class ActivityMsgType{
		/*主Activity状态切换消息*/
		public static final int MSG_INIT_VIEW=1;
		public static final int MSG_INIT_ENV=2;
		public static final int MSG_START_SERVICE=3;
		public static final int MSG_STOP_SERVICE=4;
		public static final int MSG_RESUME=5;
		/*后台服务与Activity交互消息*/
		public static final int MSG_S2U_INSTALL_ST=20; //--后台服务返回安装状态
		public static final int MSG_U2S_INSTALL_ST=30; //--前台UI请求安装状态
		public static final int MSG_U2S_INSTALL=31;    //--前台UI请求安装注册设备
		public static final int MSG_U2S_GET_SERVER=32; //--前台UI获取服务器信息
		public static final int MSG_S2U_GET_SERVER=33; //--后台应答前台UI服务器信息
		public static final int MSG_U2S_SET_SERVER=34; //--前台UI设置服务器信息
		public static final int MSG_S2U_SET_SERVER=35; //--后台应答前台UI设置结果
		public static final int MSG_U2S_GET_CONFIG=36; //--前台UI获取配置信息
		public static final int MSG_S2U_GET_CONFIG=37; //--后台应答配置信息
		public static final int MSG_U2S_UDISK_UPDATE=38; //--前台UI请求U盘更新播放资源
		public static final int MSG_S2U_UDISK_UPDATE=39; //--后台应答前台更新结果
		public static final int MSG_S2U_UDISK_REPORT=40; //--后台上报升级过程信息
		public static final int MSG_U2S_REBOOT=41;       //--系统重启
		
		/*设置对话框消失*/
		public static final int MSG_DIALOG_DISMISS=40; //--对话框消失
	}
	
	/*状态机*/
	public enum ActivityState{
		STATE_INIT_VIEW,
		STATE_INIT_ENV,
		STATE_START,
		STATE_STOP
	}
	
	/*网络管理状态机*/
	public class NetworkState{
		public static final int STATE_INIT=1;
		public static final int STATE_NETWORK_DISABLED=2;
		public static final int STATE_NETWORK_ENABLED=3;
		public static final int STATE_CONNECTED=4;
		public static final int STATE_DISCONNECTED=5;
		public static final int STATE_RECV=6;
		public static final int STATE_SEND=7;
	}
	/*网络管理Handler消息类型*/
	public class NetworkMsgType{
		public static final int MSG_NETWORK_DISABLED=1;
		public static final int MSG_NETWORK_ENABLED=2;
		public static final int MSG_CONNECTED=4;
		public static final int MSG_DISCONNECTED=5;
		public static final int MSG_REPORT_TRANS_STATUS=6;
	}
	/*网络数据报文发送/接收缓存容量*/
	public static final int NETWORK_SEND_BUF_SIZE=2048;
	public static final int NETWORK_RECV_BUF_SIZE=1700;
	/*解析报文缓存容量*/
	public static final int NETOWRK_ANALYSE_BUF_SIZE=2048;
	
	/*资源管理消息类型*/
	public class ResMsgType{
		public static final int MSG_ADD_DOWNLOAD=1;
		public static final int MSG_ADD_UPLOAD=2;
		public static final int MSG_TASK_FINISH=3;
		public static final int MSG_NETWORK_ENABLED=4;
		public static final int MSG_NETWORK_DISABLED=5;
	}
	
	/*资源下载任务错误编码*/
	public class DownloadError{
		public static final int ERROR_NONE=0;
		public static final int ERROR_LOGIN=1;
		public static final int ERROR_GET_SIZE=2;
		public static final int ERROR_RECV_DATA=3;
		public static final int ERROR_PARAMER=4;
		public static final int ERROR_UNKNONWN=5; //--未知错误
	}
	
	/*资源传输*/
	public class FileTrans{
		//--传输方向
		public static final byte DIR_DOWNLOAD=0X0;      //--下载
		public static final byte DIR_UPLOAD=0X01;       //--上传
		//--传输状态
		public static final byte STATUS_TRANSING=0X06;  //--传输过程中
		public static final byte STATUS_TRANS_FINISH=0X07;//--传输完成
		public static final byte STATUS_TRANS_ERROR=0X08; //--传输失败
	
		//--文件类型
		public static final int FILE_TYPE_PLAYBILL=0;   //--节目单
		public static final int FILE_TYPE_RESERVED=1;   //--保留
		public static final int FILE_TYPE_TM_PARMER=2;  //--终端参数
		public static final int FILE_TYPE_APP=3;        //--程序文件
		public static final int FILE_TYPE_RES=4;        //--素材文件
		public static final int FILE_TYPE_DEF_ADV=5;    //--默认广告
		public static final int FILE_TYPE_THEME=6;      //--主题文件
		public static final int FILE_TYPE_THEME_POLICY=7;   //--主题切换
		public static final int FILE_TYPE_INSERT_POLICY=8;  //--插播主题切换
		public static final int FILE_TYPE_PAD_POLICY=9; //--垫片主题切换
		
		//--节目单类型
		public static final int PLAYBILL_TYPE_UNDEF=0;
		public static final int PLAYBILL_TYPE_IMAGE=1;
		public static final int PLAYBILL_TYPE_VIDEO=2;
		public static final int PLAYBILL_TYPE_LABEL=3;
		public static final int PLAYBILL_TYPE_CLOCK=4;
		public static final int PLAYBILL_TYPE_PANEL=5;
		public static final int PLAYBILL_TYPE_APPCODE=6;
	}
	
	/*系统管理消息类型*/
	public class SystemMsgType{
		public static final int MSG_UPDATE_ERROR=-1;
		public static final int MSG_UPDATE_THEME_POLICY=1;
		public static final int MSG_REPORT_INSTALL_STATUS=2;
		public static final int MSG_REBOOT=3;
		public static final int MSG_SHUTDOWN=4;
		public static final int MSG_POWERON=5;
		public static final int MSG_UPDATE_APP=6;
	}
	
	
	/*界面Activity广播action*/
	public static final String ACTIVITY_UPDATE="com.adv.update";
	/*发送给售卖应用的广播action*/
	public static final String VMC_ACTION="com.vmc.action";
	/*接收售卖机钱币变化*/
	public static final String VMC_CASHBOX_ACTION="com.vmc.cashbox.action";
	
	/*后台服务应答UI界面安装状态*/
	public static class DevInstallStatus{
		public boolean isInstalled;
		public String installNo;
		public String installKey;
	}
	
	/*后台服务与前台UI界面交互服务器配置*/
	public static class ServerInfo{
		public String ip1; //--首选服务器IP
		public int port1;  //--首选服务器端口
		public String ip2; //--备用服务器IP
		public int port2;  //--备用服务器端口
	}
	
	/*播放管理器与各个播放组件的消息*/
	public class DisplayMsgType{
		public static final int MSG_DISPLAY_FINISH=1; //--各个组件播放完一轮
		public static final int MSG_STOP_FAIL=2;      //--各组件结束失败
	}
	
	/*过滤第三方点播信息theme名*/
	public static final String EXT_INFO_FILTER="ext_TEST";
	
	/*后台上报UI层升级过程信息*/
	public static class UpdateInfo{
		public static final int TYPE_START=1;
		public static final int TYPE_COPY=2;
		public static final int TYPE_FINISH=3;
		public static final int TYPE_CP_RESULT=4;
		public static final int TYPE_SCAN=5;
		public int type;       //--类型
		public int total;      //--拷贝文件总数
		public int id;         //--拷贝文件序号
		public String src;     //--原始地址
		public String dst;     //--目标地址
		public boolean success; //--拷贝结果
	}
}
