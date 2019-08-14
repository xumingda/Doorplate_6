package com.advertisement.system;



import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SystemConfig {
	public  int serverPortNum=7001; //0x1000:服务器端口号1
	public  int aliveTime = 30;//0x1002 心跳包的间隔时间，终端手动设置=>平台只能查
	public  String tmSecretKey = "5804673838878211";// 0x1003:  16 字节  终端密钥
	public  String installKey = "68160507";      // 0x1003_1:  8字节 随机验证码
	public  String clock="2015-02-26 17:20:30";//0x1004:时钟
	public  String tmType="rk3128";//0x1005:设备型号 (eg: SDM6000)
	public  String tmHardVersion="H-V1.0";//0x1006:硬件版本(eg: H-V1.0)
	public  String tmSoftVersion="V0.0.2";//0x1007:软件版本(eg:V5.0.3)
	public  String tmPlayBillVersion="M-V1.0";//0x1008:播放列表版本(M-V1.0) ，实际未使用=>平台只能查
	public  String tmId="0000007233";//0x1009:终端ID
	public  int tmState;//0x1010:终端状态(eg: 0)，实际未使用=>平台只能查
	public  String tmAdCapacity;//0x1011:可用硬盘(/磁盘)空间大小
	public  int tmAutoRelinkRoute =1;//0x1018:终端自动重连网关=>平台只能查 0:断线后不重连  1:断线后重连
	public  int tmAutoRelinkTimeInterval;//0x1019:终端自动重连时间间隔 
	public  String serverIp="120.76.164.218";//"120.25.65.121";//0x1020:服务器IP1
	public  int receiveOverTime;//0x1021:接收超时时间=>平台只能查
	public  int allowOverTimeNum;//0x1022:允许的超时次数=>平台只能查
	public  int idleTimeLen;//0x1023:空闲时长=>平台只能查
	public  int setVolume=25;//0x1024:音量大小
	public  boolean isMute=false;//0x1025:静音音量
	public  String userName;//0x1026:维护人用户名
	public  String userPassword;//0x1027:维护人密码：SIM卡IMSI
	public  int tmNetMode;//0x1028:网络方式(1034&1028)
	public  String tmIP;//0x1029:设置的终端静态IP 
	public  String tmNetMask;//0x1030:设置的终端静态子网淹码
	public  String tmRoute;//0x1031:设置的终端静态网关
	public  String masterDns;//0x1032:设置的终端的主DNS
	public  String tmBakeupDns;//0x1033:设置的终端的备DNS
	public  int netDevice;//0x1034:网络设备(1034&1028)
	public  String tmWifiSsid;//0x1035:连接的无线ap名称
	public  int ftpRelinkTime;//0x1039:下载文件时，重连ftp时间间隔
	public  String playRecordRoom;//0x1040
	public  String verifiedTimePrecison;//0x1041:校时精度
	public  String ftpGroup;//0x1042
	public  String equipModel="RK";//0x1043:设备类型
	public  String volumeGroup=null;//0x1044：音量设置组合
	
	public  String tmLanguage;//0x1046:语言设置
	
	public  String apn3g;//0x1050:配置的apn信息
	public  String apn3gBack;//0x1051:备份的apn使用记录
	public  String osdPassword="83592600";//0x1067:OSD密码
	
	public  int tmCurrentState;//0x2000:终端当前控制命令(/状态)：1 复位 ，2 关机 ， 3 开机
	
	public  boolean installState=false;//0xA000:安装状态　false要安装， true已安装/不要安装
	public  String installJobNum="0000000076";//0xA001:安装工号 10字节安装工号

	public  String serverIp2="120.76.164.218";//0xF001:服务器ＩＰ2
	public  int serverPortNum2=7001;//0xF002:服务器端口2

	public  int netType=1;//nettype: 当前连接的网络类型 0:有线PPPOE  1:有线DHCP 2:有线LAN 3:无线DHCP 4:无线静态IP 5:3G
	public  String wifiSsid="Sunnada-WLAN2.4G";//wifissid:无线ssid名称
	public  String wifiPsw="accesspoint";//wifipsw:无线密码
	public  int wifiDefKey=0;//wifidefkey:默认key
	
	public  String wifiStaticIp="192.168.001.002";//wifistaticip:无线静态ip
	public  String wifiStaticDns="100.100.010.001";//wifistaticdns:无线静态dns
	public  String wifiStaticGateway="255.255.255.000";//wifistaticgateway:无线静态网关
	public  String wifiStaticSubNetMask="192.168.001.001";//wifistaticsubnetmask:无线静态网关
	
	public  String ethStaticIp="172.016.204.100";//ethstaticip:以太网静态ip
	public  String ethStaticDns="144.144.144.144";//ethstaticdns:以太网静态dns
	public  String ethStaticGateway="172.016.204.001";//ethstaticgateway:以太网静态网关
	public  String ethStaticSubNetMask="255.255.255.000";//ethstaticsubnetmask:以太网静态网关
	

	public int downloadPolicyType=0; //downloadPolicyType:当前下载主题策略的类型
	public String downloadPolicyPath=null;//downloadPolicyPath: 当前下载的主题切换策略的路径
	public String defaultThemePolicy=null;//defaultThemePolicy:垫片主题切换策略路径
	public String insertThemePolicy=null; //insertThemePolicy:插播主题切换策略路径
	public String normalThemePolicy=null; //normalThemePolicy:正常主题切换策略路径
	public boolean isNormalExist=false;   //isNormalExist：是否存在正常播放策略文件
	public boolean isDefaultExist=false;  //isDefaultExist:是否存在垫片播放策略文件
	public boolean isInsertExist=false;   //isInsertExist:是否存在插播文件


	//*配置文件有效标记
	private static final String EXIST_KEY="isExist";
	//*配置文件读句柄
	private SharedPreferences mReaderPref=null;
	//*配置文件写句柄
	private SharedPreferences.Editor mWriterPref=null;

	private Context mContext=null;
	//*配置文件名称
	private static final String CONFIG_FILENAME="AdvConfig";
	
	//*存储FTP下载资源的主文件路径
	private static final String ADV_STORE_FILE_PATH="/mnt/sdcard/Adv";
	
	public SystemConfig(Context context){
		mContext=context;
		mReaderPref=null;
		mWriterPref=null;
		mReaderPref = mContext.getSharedPreferences(CONFIG_FILENAME, Context.MODE_PRIVATE);
		initConfig();
		syncConfig();
	}
	
	public static String getStoreRootPath(){
		return ADV_STORE_FILE_PATH;
	}
	
	
	/**
	 * @param 无
	 * @return 无
	 * @author sky
	 */
	@SuppressLint("SdCardPath") public void initConfig(){
		
		if(!new File(ADV_STORE_FILE_PATH).exists()){
			new File(ADV_STORE_FILE_PATH).mkdir();
		}
		
		if(mReaderPref==null){
			return ;
		}
		
		try{
			if( readParam(EXIST_KEY, false )){
				mWriterPref=mReaderPref.edit();
				return;
			}
		}catch(ClassCastException  e){
			e.printStackTrace();
		}
		mWriterPref=mReaderPref.edit();
		mWriterPref.clear();
		mWriterPref.commit();
		writeParam(EXIST_KEY, true);
	}
	
	/**
	 * @see 该接口用于读取整型参数
	 * @param key ：键值
	 * @param defValue:默认值
	 * @return 整型值
	 * @author sky
	 */
	public int readParam(String key,int defValue){
		if(mReaderPref==null){
			return defValue;
		}
		return mReaderPref.getInt(key, defValue);
	}
	
	/**
	 * @see 该接口用于读取字符串参数
	 * @param key ：键值
	 * @param defValue:默认值
	 * @return 字符串
	 * @author sky
	 */
	public String readParam(String key,String defValue){
		if(mReaderPref==null){
			return defValue;
		}
		return mReaderPref.getString(key, defValue);
	}
	
	/**
	 * @see 该接口用于读取布尔值
	 * @param key ：键值
	 * @param defValue:默认值
	 * @return 布尔值
	 * @author sky
	 */
	public boolean readParam(String key,boolean defValue){
		if(mReaderPref==null){
			return defValue;
		}
		return mReaderPref.getBoolean(key, defValue);
	}
	
	/**
	 * @see 该接口用于写入整型参数
	 * @param key ：键值
	 * @param value:写入值
	 * @return true->成功   false->失败
	 * @author sky
	 */
	public boolean writeParam(String key,int value){
		if(mWriterPref==null){
			return false;
		}
		mWriterPref.putInt(key, value);
		return mWriterPref.commit();
	}
	
	/**
	 * @see 该接口用于写入字符串
	 * @param key ：键值
	 * @param value:写入值
	 * @return true->成功   false->失败
	 * @author sky
	 */
	public boolean writeParam(String key,String value){
		if(mWriterPref==null){
			return false;
		}
		mWriterPref.putString(key, value);
		return mWriterPref.commit();
	}
	
	/**
	 * @see 该接口用于写入布尔值
	 * @param key ：键值
	 * @param value:写入值
	 * @return true->成功   false->失败
	 * @author sky
	 */
	public boolean writeParam(String key,boolean value){
		if(mWriterPref==null){
			return false;
		}
		mWriterPref.putBoolean(key, value);
		return mWriterPref.commit();
	}
	
	public boolean removeParam(String key){
		if(mWriterPref==null){
			return false;
		}
		mWriterPref.remove(key);
		return true;
	}
	
	public void syncConfig(){
		serverPortNum = readParam("0x1000",serverPortNum);//0x1000:服务器端口号1
		aliveTime = readParam("0x1002",aliveTime);//0x1002 心跳包的间隔时间，终端手动设置=>平台只能查
		tmSecretKey = readParam("0x1003",tmSecretKey);//0x1000:服务器端口号1
		installKey = readParam("0x1003_1",installKey);// 0x1003_1:  8字节 随机验证码
		clock = readParam("0x1004",clock);//0x1004:时钟
		tmType = readParam("0x1005",tmType);////0x1005:设备型号 (eg: SDM6000)
		tmHardVersion = readParam("0x1006",tmHardVersion);//0x1006:硬件版本(eg: H-V1.0)
		tmSoftVersion = readParam("0x1007",tmSoftVersion);//0x1007:软件版本(eg:V5.0.3)
		tmPlayBillVersion = readParam("0x1008",tmPlayBillVersion);//0x1008:播放列表版本(M-V1.0) ，实际未使用=>平台只能查
		tmId = readParam("0x1009",tmId);//0x1009:终端ID
		tmState = readParam("0x1010",tmState);//0x1010:终端状态(eg: 0)，实际未使用=>平台只能查
		tmAdCapacity = readParam("0x1011",tmAdCapacity);//0x1011:可用硬盘(/磁盘)空间大小
		tmAutoRelinkRoute = readParam("0x1018",tmAutoRelinkRoute);//:终端自动重连网关=>平台只能查
		tmAutoRelinkTimeInterval = readParam("0x1019",tmAutoRelinkTimeInterval);//0x1019:终端自动重连时间间隔
		serverIp = readParam("0x1020",serverIp);//0x1020:
		receiveOverTime = readParam("0x1021",receiveOverTime);//0x1021:接收超时时间=>平台只能查
		allowOverTimeNum = readParam("0x1022",allowOverTimeNum);//0x1022:允许的超时次数=>平台只能查
		idleTimeLen = readParam("0x1023",idleTimeLen);//0x1023:空闲时长=>平台只能查
		setVolume = readParam("0x1024",setVolume);//0x1024:音量大小
		isMute = readParam("0x1025",isMute);//0x1025:静音音量
		userName = readParam("0x1026",userName);//0x1026:维护人用户名
		userPassword = readParam("0x1027",userPassword);//0x1027:维护人密码：SIM卡IMSI
		tmNetMode = readParam("0x1028",tmNetMode);//0x1028:网络方式(1034&1028)
		tmIP = readParam("0x1029",tmIP);//0x1029:终端IP
		tmNetMask = readParam("0x1030",tmNetMask);//0x1030:子网淹码
		tmRoute = readParam("0x1031",tmRoute);//0x1031:路由
		masterDns = readParam("0x1032",masterDns);//0x1032:主DNS
		tmBakeupDns = readParam("0x1033",tmBakeupDns);//0x1033:备DNS
		netDevice = readParam("0x1034",netDevice);//0x1034:网络设备(1034&1028)
		tmWifiSsid = readParam("0x1035",tmWifiSsid);//0x1035:的SSID
		
		ftpRelinkTime = readParam("0x1039",ftpRelinkTime);//0x1039:下载文件时，重连ftp时间间隔
		playRecordRoom = readParam("0x1040",playRecordRoom);//0x1040
		verifiedTimePrecison = readParam("0x1041",verifiedTimePrecison);//0x1041:校时精度
		ftpGroup = readParam("0x1042",ftpGroup);//0x1042
		equipModel = readParam("0x1043",equipModel);//0x1043:设备类型
		volumeGroup = readParam("0x1044",volumeGroup);//0x1044：音量设置
		tmLanguage = readParam("0x1046",tmLanguage);//0x1046:语言设置
		apn3g = readParam("0x1050",apn3g);//0x1050:配置的apn信息
		apn3gBack = readParam("0x1051",apn3gBack);//0x1051:备份的apn使用记录
		osdPassword = readParam("0x1067",osdPassword);//0x1067:OSD密码
		
		
		tmCurrentState = readParam("0x2000",tmCurrentState);//0x2000:终端当前控制命令(/状态)：1 复位 ，2 关机 ， 3 开机
		installState = readParam("0xA000",installState);//0xA000:安装状态　false 要安装， true已安装/不要安装
		installJobNum = readParam("0xA001",installJobNum);//0xA001:安装工号 10字节安装工号
		
	
		serverIp2 = readParam("0xF001",serverIp2);//0xF001:服务器ＩＰ2
		serverPortNum2 = readParam("0xF002",serverPortNum2);//0xF002:服务器端口2

		
		netType = readParam("nettype",netType);
		wifiSsid = readParam("wifissid",wifiSsid);
		wifiPsw = readParam("wifipsw",wifiPsw);
		wifiDefKey = readParam("wifidefkey",wifiDefKey);
		
		
		wifiStaticIp = readParam("wifistaticip",wifiStaticIp);
		wifiStaticDns = readParam("wifistaticdns",wifiStaticDns);
		wifiStaticGateway = readParam("wifistaticgateway",wifiStaticGateway);
		wifiStaticSubNetMask = readParam("wifistaticsubnetmask",wifiStaticSubNetMask);
	
		ethStaticIp = readParam("ethstaticip",ethStaticIp);
		ethStaticDns = readParam("ethstaticdns",ethStaticDns);
		ethStaticGateway = readParam("ethstaticgateway",ethStaticGateway);
		ethStaticSubNetMask = readParam("ethstaticsubnetmask",ethStaticSubNetMask);
	
		downloadPolicyPath = readParam("downloadPolicyPath",downloadPolicyPath);
		
		defaultThemePolicy = readParam("defaultThemePolicy",defaultThemePolicy);
		insertThemePolicy = readParam("insertThemePolicy",insertThemePolicy);
		normalThemePolicy = readParam("normalThemePolicy",normalThemePolicy);
		downloadPolicyType = readParam("downloadPolicyType", downloadPolicyType);
		
		isNormalExist = readParam("isNormalExist",isNormalExist);
		isDefaultExist = readParam("isDefaultExist",isDefaultExist);
		isInsertExist = readParam("isInsertExist",isInsertExist);
	}
}
