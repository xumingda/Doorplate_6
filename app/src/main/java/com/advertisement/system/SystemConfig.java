package com.advertisement.system;



import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SystemConfig {
	public  int serverPortNum=7001; //0x1000:�������˿ں�1
	public  int aliveTime = 30;//0x1002 �������ļ��ʱ�䣬�ն��ֶ�����=>ƽֻ̨�ܲ�
	public  String tmSecretKey = "5804673838878211";// 0x1003:  16 �ֽ�  �ն���Կ
	public  String installKey = "68160507";      // 0x1003_1:  8�ֽ� �����֤��
	public  String clock="2015-02-26 17:20:30";//0x1004:ʱ��
	public  String tmType="rk3128";//0x1005:�豸�ͺ� (eg: SDM6000)
	public  String tmHardVersion="H-V1.0";//0x1006:Ӳ���汾(eg: H-V1.0)
	public  String tmSoftVersion="V0.0.2";//0x1007:����汾(eg:V5.0.3)
	public  String tmPlayBillVersion="M-V1.0";//0x1008:�����б�汾(M-V1.0) ��ʵ��δʹ��=>ƽֻ̨�ܲ�
	public  String tmId="0000007233";//0x1009:�ն�ID
	public  int tmState;//0x1010:�ն�״̬(eg: 0)��ʵ��δʹ��=>ƽֻ̨�ܲ�
	public  String tmAdCapacity;//0x1011:����Ӳ��(/����)�ռ��С
	public  int tmAutoRelinkRoute =1;//0x1018:�ն��Զ���������=>ƽֻ̨�ܲ� 0:���ߺ�����  1:���ߺ�����
	public  int tmAutoRelinkTimeInterval;//0x1019:�ն��Զ�����ʱ���� 
	public  String serverIp="120.76.164.218";//"120.25.65.121";//0x1020:������IP1
	public  int receiveOverTime;//0x1021:���ճ�ʱʱ��=>ƽֻ̨�ܲ�
	public  int allowOverTimeNum;//0x1022:����ĳ�ʱ����=>ƽֻ̨�ܲ�
	public  int idleTimeLen;//0x1023:����ʱ��=>ƽֻ̨�ܲ�
	public  int setVolume=25;//0x1024:������С
	public  boolean isMute=false;//0x1025:��������
	public  String userName;//0x1026:ά�����û���
	public  String userPassword;//0x1027:ά�������룺SIM��IMSI
	public  int tmNetMode;//0x1028:���緽ʽ(1034&1028)
	public  String tmIP;//0x1029:���õ��ն˾�̬IP 
	public  String tmNetMask;//0x1030:���õ��ն˾�̬��������
	public  String tmRoute;//0x1031:���õ��ն˾�̬����
	public  String masterDns;//0x1032:���õ��ն˵���DNS
	public  String tmBakeupDns;//0x1033:���õ��ն˵ı�DNS
	public  int netDevice;//0x1034:�����豸(1034&1028)
	public  String tmWifiSsid;//0x1035:���ӵ�����ap����
	public  int ftpRelinkTime;//0x1039:�����ļ�ʱ������ftpʱ����
	public  String playRecordRoom;//0x1040
	public  String verifiedTimePrecison;//0x1041:Уʱ����
	public  String ftpGroup;//0x1042
	public  String equipModel="RK";//0x1043:�豸����
	public  String volumeGroup=null;//0x1044�������������
	
	public  String tmLanguage;//0x1046:��������
	
	public  String apn3g;//0x1050:���õ�apn��Ϣ
	public  String apn3gBack;//0x1051:���ݵ�apnʹ�ü�¼
	public  String osdPassword="83592600";//0x1067:OSD����
	
	public  int tmCurrentState;//0x2000:�ն˵�ǰ��������(/״̬)��1 ��λ ��2 �ػ� �� 3 ����
	
	public  boolean installState=false;//0xA000:��װ״̬��falseҪ��װ�� true�Ѱ�װ/��Ҫ��װ
	public  String installJobNum="0000000076";//0xA001:��װ���� 10�ֽڰ�װ����

	public  String serverIp2="120.76.164.218";//0xF001:�������ɣ�2
	public  int serverPortNum2=7001;//0xF002:�������˿�2

	public  int netType=1;//nettype: ��ǰ���ӵ��������� 0:����PPPOE  1:����DHCP 2:����LAN 3:����DHCP 4:���߾�̬IP 5:3G
	public  String wifiSsid="Sunnada-WLAN2.4G";//wifissid:����ssid����
	public  String wifiPsw="accesspoint";//wifipsw:��������
	public  int wifiDefKey=0;//wifidefkey:Ĭ��key
	
	public  String wifiStaticIp="192.168.001.002";//wifistaticip:���߾�̬ip
	public  String wifiStaticDns="100.100.010.001";//wifistaticdns:���߾�̬dns
	public  String wifiStaticGateway="255.255.255.000";//wifistaticgateway:���߾�̬����
	public  String wifiStaticSubNetMask="192.168.001.001";//wifistaticsubnetmask:���߾�̬����
	
	public  String ethStaticIp="172.016.204.100";//ethstaticip:��̫����̬ip
	public  String ethStaticDns="144.144.144.144";//ethstaticdns:��̫����̬dns
	public  String ethStaticGateway="172.016.204.001";//ethstaticgateway:��̫����̬����
	public  String ethStaticSubNetMask="255.255.255.000";//ethstaticsubnetmask:��̫����̬����
	

	public int downloadPolicyType=0; //downloadPolicyType:��ǰ����������Ե�����
	public String downloadPolicyPath=null;//downloadPolicyPath: ��ǰ���ص������л����Ե�·��
	public String defaultThemePolicy=null;//defaultThemePolicy:��Ƭ�����л�����·��
	public String insertThemePolicy=null; //insertThemePolicy:�岥�����л�����·��
	public String normalThemePolicy=null; //normalThemePolicy:���������л�����·��
	public boolean isNormalExist=false;   //isNormalExist���Ƿ�����������Ų����ļ�
	public boolean isDefaultExist=false;  //isDefaultExist:�Ƿ���ڵ�Ƭ���Ų����ļ�
	public boolean isInsertExist=false;   //isInsertExist:�Ƿ���ڲ岥�ļ�


	//*�����ļ���Ч���
	private static final String EXIST_KEY="isExist";
	//*�����ļ������
	private SharedPreferences mReaderPref=null;
	//*�����ļ�д���
	private SharedPreferences.Editor mWriterPref=null;

	private Context mContext=null;
	//*�����ļ�����
	private static final String CONFIG_FILENAME="AdvConfig";
	
	//*�洢FTP������Դ�����ļ�·��
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
	 * @param ��
	 * @return ��
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
	 * @see �ýӿ����ڶ�ȡ���Ͳ���
	 * @param key ����ֵ
	 * @param defValue:Ĭ��ֵ
	 * @return ����ֵ
	 * @author sky
	 */
	public int readParam(String key,int defValue){
		if(mReaderPref==null){
			return defValue;
		}
		return mReaderPref.getInt(key, defValue);
	}
	
	/**
	 * @see �ýӿ����ڶ�ȡ�ַ�������
	 * @param key ����ֵ
	 * @param defValue:Ĭ��ֵ
	 * @return �ַ���
	 * @author sky
	 */
	public String readParam(String key,String defValue){
		if(mReaderPref==null){
			return defValue;
		}
		return mReaderPref.getString(key, defValue);
	}
	
	/**
	 * @see �ýӿ����ڶ�ȡ����ֵ
	 * @param key ����ֵ
	 * @param defValue:Ĭ��ֵ
	 * @return ����ֵ
	 * @author sky
	 */
	public boolean readParam(String key,boolean defValue){
		if(mReaderPref==null){
			return defValue;
		}
		return mReaderPref.getBoolean(key, defValue);
	}
	
	/**
	 * @see �ýӿ�����д�����Ͳ���
	 * @param key ����ֵ
	 * @param value:д��ֵ
	 * @return true->�ɹ�   false->ʧ��
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
	 * @see �ýӿ�����д���ַ���
	 * @param key ����ֵ
	 * @param value:д��ֵ
	 * @return true->�ɹ�   false->ʧ��
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
	 * @see �ýӿ�����д�벼��ֵ
	 * @param key ����ֵ
	 * @param value:д��ֵ
	 * @return true->�ɹ�   false->ʧ��
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
		serverPortNum = readParam("0x1000",serverPortNum);//0x1000:�������˿ں�1
		aliveTime = readParam("0x1002",aliveTime);//0x1002 �������ļ��ʱ�䣬�ն��ֶ�����=>ƽֻ̨�ܲ�
		tmSecretKey = readParam("0x1003",tmSecretKey);//0x1000:�������˿ں�1
		installKey = readParam("0x1003_1",installKey);// 0x1003_1:  8�ֽ� �����֤��
		clock = readParam("0x1004",clock);//0x1004:ʱ��
		tmType = readParam("0x1005",tmType);////0x1005:�豸�ͺ� (eg: SDM6000)
		tmHardVersion = readParam("0x1006",tmHardVersion);//0x1006:Ӳ���汾(eg: H-V1.0)
		tmSoftVersion = readParam("0x1007",tmSoftVersion);//0x1007:����汾(eg:V5.0.3)
		tmPlayBillVersion = readParam("0x1008",tmPlayBillVersion);//0x1008:�����б�汾(M-V1.0) ��ʵ��δʹ��=>ƽֻ̨�ܲ�
		tmId = readParam("0x1009",tmId);//0x1009:�ն�ID
		tmState = readParam("0x1010",tmState);//0x1010:�ն�״̬(eg: 0)��ʵ��δʹ��=>ƽֻ̨�ܲ�
		tmAdCapacity = readParam("0x1011",tmAdCapacity);//0x1011:����Ӳ��(/����)�ռ��С
		tmAutoRelinkRoute = readParam("0x1018",tmAutoRelinkRoute);//:�ն��Զ���������=>ƽֻ̨�ܲ�
		tmAutoRelinkTimeInterval = readParam("0x1019",tmAutoRelinkTimeInterval);//0x1019:�ն��Զ�����ʱ����
		serverIp = readParam("0x1020",serverIp);//0x1020:
		receiveOverTime = readParam("0x1021",receiveOverTime);//0x1021:���ճ�ʱʱ��=>ƽֻ̨�ܲ�
		allowOverTimeNum = readParam("0x1022",allowOverTimeNum);//0x1022:����ĳ�ʱ����=>ƽֻ̨�ܲ�
		idleTimeLen = readParam("0x1023",idleTimeLen);//0x1023:����ʱ��=>ƽֻ̨�ܲ�
		setVolume = readParam("0x1024",setVolume);//0x1024:������С
		isMute = readParam("0x1025",isMute);//0x1025:��������
		userName = readParam("0x1026",userName);//0x1026:ά�����û���
		userPassword = readParam("0x1027",userPassword);//0x1027:ά�������룺SIM��IMSI
		tmNetMode = readParam("0x1028",tmNetMode);//0x1028:���緽ʽ(1034&1028)
		tmIP = readParam("0x1029",tmIP);//0x1029:�ն�IP
		tmNetMask = readParam("0x1030",tmNetMask);//0x1030:��������
		tmRoute = readParam("0x1031",tmRoute);//0x1031:·��
		masterDns = readParam("0x1032",masterDns);//0x1032:��DNS
		tmBakeupDns = readParam("0x1033",tmBakeupDns);//0x1033:��DNS
		netDevice = readParam("0x1034",netDevice);//0x1034:�����豸(1034&1028)
		tmWifiSsid = readParam("0x1035",tmWifiSsid);//0x1035:��SSID
		
		ftpRelinkTime = readParam("0x1039",ftpRelinkTime);//0x1039:�����ļ�ʱ������ftpʱ����
		playRecordRoom = readParam("0x1040",playRecordRoom);//0x1040
		verifiedTimePrecison = readParam("0x1041",verifiedTimePrecison);//0x1041:Уʱ����
		ftpGroup = readParam("0x1042",ftpGroup);//0x1042
		equipModel = readParam("0x1043",equipModel);//0x1043:�豸����
		volumeGroup = readParam("0x1044",volumeGroup);//0x1044����������
		tmLanguage = readParam("0x1046",tmLanguage);//0x1046:��������
		apn3g = readParam("0x1050",apn3g);//0x1050:���õ�apn��Ϣ
		apn3gBack = readParam("0x1051",apn3gBack);//0x1051:���ݵ�apnʹ�ü�¼
		osdPassword = readParam("0x1067",osdPassword);//0x1067:OSD����
		
		
		tmCurrentState = readParam("0x2000",tmCurrentState);//0x2000:�ն˵�ǰ��������(/״̬)��1 ��λ ��2 �ػ� �� 3 ����
		installState = readParam("0xA000",installState);//0xA000:��װ״̬��false Ҫ��װ�� true�Ѱ�װ/��Ҫ��װ
		installJobNum = readParam("0xA001",installJobNum);//0xA001:��װ���� 10�ֽڰ�װ����
		
	
		serverIp2 = readParam("0xF001",serverIp2);//0xF001:�������ɣ�2
		serverPortNum2 = readParam("0xF002",serverPortNum2);//0xF002:�������˿�2

		
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
