package com.yy.doorplate.tool;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

/**
 * wifi����������
 * 
 * @author lenovo
 * 
 */
public class WifiUtils {

	static final int SECURITY_NONE = 0;
	static final int SECURITY_WEP = 1;
	static final int SECURITY_PSK = 2;
	static final int SECURITY_EAP = 3;

	private Context mContext;
	private WifiManager mWifiManager;// �ṩWifi����ĸ�����ҪAPI����Ҫ����wifi��ɨ�衢�������ӡ�������Ϣ��
	private List<WifiConfiguration> mWifiConfigList = new ArrayList<WifiConfiguration>();// WIFIConfiguration����WIFI��������Ϣ������SSID��SSID���ء�password�ȵ�����
	private List<ScanResult> mWifiResultList = new ArrayList<ScanResult>(); // ɨ����
	private WifiInfo mWifiInfo;// �Ѿ��������������ӵ���Ϣ
	private WifiLock mWifiLock;// �ֻ���������ֹWIFIҲ����˯��״̬��WIFI�Ĺر�

	private DhcpInfo mDhcpInfo;// ��ȡwifi ��̬��Ϣ

	private String mStaticIp = sNullIp;// ��̬ip
	private String mStaticGateway = sNullIp;// ��̬����
	private String mStaticDns = sNullIp;// ��̬DNS
	private final static String sNullIp = "0.0.0.0";

	public WifiUtils(Context context) {
		this.mContext = context;
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * ssid��ɨ���б����Ƿ����
	 * 
	 * @param ssid
	 * @return
	 */
	public boolean IsExsitsSsid(String ssid) {

		for (int i = 0; i < mWifiResultList.size(); i++) {
			if (mWifiResultList.get(i).SSID.equals(ssid))
				return true;
		}
		return false;
	}

	/**
	 * ssid�������б����Ƿ����
	 * 
	 * @param ssid
	 * @return
	 */
	public boolean IsConfigurationExsitsSsid(String ssid) {

		mWifiConfigList = getListConfiguration();
		String g2 = "\"" + ssid + "\"";
		if (mWifiConfigList != null && mWifiConfigList.size() > 0) {
			for (int i = 0; i < mWifiConfigList.size(); i++) {
				if (mWifiConfigList.get(i).SSID.equals(g2))
					return true;
			}
		}
		return false;
	}

	public WifiConfiguration ConfigurationExsitsSsid(String ssid) {

		mWifiConfigList = getListConfiguration();
		String g2 = "\"" + ssid + "\"";
		for (int i = 0; i < mWifiConfigList.size(); i++) {
			if (mWifiConfigList.get(i).SSID.equals(g2))
				return mWifiConfigList.get(i);
		}
		return null;
	}

	/**
	 * �ж�ɨ�����Ƿ�������
	 * 
	 * @param result
	 * @return
	 */
	public boolean isConnect(ScanResult result) {
		if (result == null) {
			return false;
		}
		mWifiInfo = getWifiConnectedInfo();
		String g2 = "\"" + result.SSID + "\"";
		Log.i("WifiUtils", result.SSID + "---------" + mWifiInfo.getSSID());
		if (mWifiInfo.getSSID() != null && mWifiInfo.getSSID().equals(g2)) {
			return true;
		}
		return false;
	}

	/**
	 * ��ȡ��������
	 * 
	 * @param result
	 * @return
	 */
	private int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	/**
	 * ���þ�̬ip(��Ҫ.һ��Ҫ����Ϊ0.0.0.0��ʽ.��������Ϊ000.000.000.000��ʽ)
	 * 
	 * @param ip
	 */
	public void setWifiInfoFromStaticIP(String ip) {
		mStaticIp = EthernetUtils.ipFormatConversion1(ip);
	}

	/*
	 * ���þ�ͷ����
	 */
	public void setWifiInfoFromStaticGateway(String gateway) {
		mStaticGateway = EthernetUtils.ipFormatConversion1(gateway);
	}

	/**
	 * ���þ�̬dns
	 * 
	 * @param dns
	 */
	public void setWifiInfoFromStaticDns(String dns) {
		mStaticDns = EthernetUtils.ipFormatConversion1(dns);
	}

	/**
	 * ʹ��wifi��
	 */
	public void SetWifiEnable() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	/**
	 * ʹ��wifi��
	 */
	public void SetWifiDiable() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * ��ȡwifi�Ƿ���
	 */
	public boolean isWifiEnabled() {

		return mWifiManager.isWifiEnabled();
	}

	/** ��̫���Ƿ����� */
	public boolean isWiFiConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	// Scan���תΪSting
	public List<String> scanResultToString(List<ScanResult> list) {

		List<String> strReturnList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			ScanResult strScan = list.get(i);
			String str = strScan.toString();
			Log.d("hsl", "ɨ����:" + str);
			boolean bool = strReturnList.add(str);
			if (!bool) {
				Log.i("scanResultToSting", "Addfail");
			}
		}
		return strReturnList;
	}

	/**
	 * ��ȡwifi��������Ϣ
	 */
	public void getConfiguration() {
		mWifiConfigList = mWifiManager.getConfiguredNetworks();// �õ����úõ�������Ϣ
	}

	/**
	 * �õ��������ӵ���Ϣ
	 * 
	 * @return
	 */
	public WifiInfo getWifiConnectedInfo() {
		mWifiInfo = mWifiManager.getConnectionInfo();
		return mWifiInfo;
	}

	/**
	 * ��ȡwifi dhcp��̬ixnx
	 */
	public DhcpInfo getWifiDhcpInfo() {
		mDhcpInfo = mWifiManager.getDhcpInfo();
		return mDhcpInfo;
	}

	/**
	 * ��ȡwifi��̬IP��ַ
	 */
	public String getWifiInfoFromDhcpIp() {
		return intToIp(mWifiManager.getDhcpInfo().ipAddress);
	}

	/**
	 * ��ȡwifi��̬��������
	 */
	public String getWifiInfoFromDhcpNetmask() {
		return intToIp(mWifiManager.getDhcpInfo().netmask);
	}

	/**
	 * ��ȡwifi��̬����
	 */
	public String getWifiInfoFromDhcpGateway() {
		return intToIp(mWifiManager.getDhcpInfo().gateway);
	}

	/**
	 * ��ȡwifi��̬DNS1
	 */
	public String getWifiInfoFromDhcpDNS1() {
		return intToIp(mWifiManager.getDhcpInfo().dns1);
	}

	/**
	 * ��ȡwifi��̬DNS1
	 */
	public String getWifiInfoFromDhcpDNS2() {
		return intToIp(mDhcpInfo.dns2);
	}

	/**
	 * ��ȡwifi��̬IP��ַ
	 */
	public static InetAddress getWifiInfoFromStaticIp(WifiConfiguration wifiConf) {
		InetAddress address = null;
		try {
			Object linkProperties = ReflectionUtil.getField(wifiConf,
					"linkProperties");
			if (linkProperties == null)
				return null;
			if (linkProperties != null) {
				ArrayList<?> mLinkAddresses = (ArrayList<?>) ReflectionUtil
						.getDeclaredField(linkProperties, "mLinkAddresses");
				if (mLinkAddresses != null && mLinkAddresses.size() > 0) {
					Object linkAddressObj = mLinkAddresses.get(0);
					address = (InetAddress) linkAddressObj.getClass()
							.getMethod("getAddress", new Class[] {})
							.invoke(linkAddressObj, new  Object[]{});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}

	/**
	 * ��ȡwifi��̬���ص�ַ
	 */
	public static String getNetworkPrefixLength(WifiConfiguration wifiConf) {
		String address = "";
		try {
			Object linkProperties = ReflectionUtil.getField(wifiConf,
					"linkProperties");
			if (linkProperties == null)
				return null;
			if (linkProperties != null) {
				ArrayList<?> mLinkAddresses = (ArrayList<?>) ReflectionUtil
						.getDeclaredField(linkProperties, "mLinkAddresses");
				if (mLinkAddresses != null && mLinkAddresses.size() > 0) {
					Object linkAddressObj = mLinkAddresses.get(0);
					address = linkAddressObj
							.getClass()
							.getMethod("getNetworkPrefixLength", new Class[] {})
							.invoke(linkAddressObj, new  Object[]{})
							+ "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}

	/**
	 * ��ȡwifi��̬���ص�ַ
	 */
	public static InetAddress getWifiInfoFromStaticGateway(
			WifiConfiguration wifiConf) {
		InetAddress address = null;
		try {
			Object linkProperties = ReflectionUtil.getField(wifiConf,
					"linkProperties");

			if (linkProperties != null) {
				ArrayList<?> mRoutes = (ArrayList<?>) ReflectionUtil
						.getDeclaredField(linkProperties, "mRoutes");
				if (mRoutes != null && mRoutes.size() > 0) {
					Object linkAddressObj = mRoutes.get(0);
					address = (InetAddress) linkAddressObj.getClass()
							.getMethod("getGateway", new Class[] {})
							.invoke(linkAddressObj, new  Object[]{});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}

	/**
	 * ��ȡwifi��̬dns
	 */
	public static InetAddress getWifiInfoFromStaticDns(
			WifiConfiguration wifiConf) {
		InetAddress address = null;
		try {
			Object linkProperties = ReflectionUtil.getField(wifiConf,
					"linkProperties");
			if (linkProperties != null) {
				ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) ReflectionUtil
						.getDeclaredField(linkProperties, "mDnses");
				if (mDnses != null && mDnses.size() > 0) {
					address = (InetAddress) mDnses.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}

	/**
	 * �õ����ӵ�MAC��ַ
	 * 
	 * @return
	 */
	public String getConnectedMacAddr() {
		getWifiConnectedInfo();
		return (mWifiInfo == null) ? null : mWifiInfo.getMacAddress();
	}

	/**
	 * �õ����ӵ�����SSID
	 * 
	 * @return
	 */
	public String getConnectedSSID() {
		getWifiConnectedInfo();
		return (mWifiInfo == null) ? null : mWifiInfo.getSSID();
	}

	/**
	 * �õ����ӵ�IP��ַ
	 * 
	 * @return
	 */
	public int getConnectedIPAddr() {
		getWifiConnectedInfo();
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	/**
	 * �õ����ӵ�ID
	 * 
	 * @return
	 */
	public int getConnectedID() {
		getWifiConnectedInfo();
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * ��ȡwifi״̬
	 * 
	 * @return
	 */
	public int getWifiState() {
		return mWifiManager.getWifiState();
	}

	/**
	 * ��ʼɨ��wifi��Ϣ
	 */
	public void startScanWifi() {
		mWifiManager.startScan();
		// ��ȡ������Ϣ
		getConfiguration();
		getWifiConnectedInfo();
	}

	/**
	 * ��ȡɨ����
	 */
	public List<ScanResult> getScanResults() {

		mWifiResultList = mWifiManager.getScanResults();
		return mWifiResultList;
	}

	/**
	 * ��ȡwifi��������Ϣ�б�
	 */
	public List<WifiConfiguration> getListConfiguration() {
		mWifiConfigList = mWifiManager.getConfiguredNetworks();// �õ����úõ�������Ϣ
		return mWifiConfigList;
	}

	/**
	 * 
	 * @param ssid
	 * @return
	 */
	public ScanResult getScanResult(String ssid) {

		for (int i = 0; i < mWifiResultList.size(); i++) {
			if (mWifiResultList.get(i).SSID.equals(ssid))
				return mWifiResultList.get(i);
		}
		return null;
	}

	/**
	 * ����wifi
	 * 
	 * @param SSID
	 *            wifi��
	 * @param Password
	 *            ����
	 * @param Type
	 *            ���ܷ�ʽ 1��WIFICIPHER_NOPASS 2��WIFICIPHER_WEP 3��WIFICIPHER_WPA
	 */
	public WifiConfiguration connectWifi(String SSID, String Password,
			int wepId, boolean isDHCP) {
		System.out.println("==========WifiConfiguration=========");
		WifiConfiguration config = new WifiConfiguration();
		System.out.println("==========WifiConfiguration=========" + config);
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		int type = getSecurity(getScanResult(SSID));

		if (type == SECURITY_NONE) // WIFICIPHER_NOPASS
		{
			Log.d("hsl", "WIFICIPHER_NOPASS");
			// config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			// config.wepTxKeyIndex = wepId;
		}
		if (type == SECURITY_WEP) // WIFICIPHER_WEP
		{
			Log.d("hsl", "WIFICIPHER_WEP:" + wepId);
			config.hiddenSSID = true;
			// config.wepKeys[0] = Password;

			if (Password.length() != 0) {
				int length = Password.length();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58)
						&& Password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[wepId] = Password;
				} else {
					config.wepKeys[wepId] = '"' + Password + '"';
				}
			}
			if (!isDHCP) {
				setStatic(config, SSID);
				System.out.println("------sssss------");
			}
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			/*
			 * config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
			 * ;
			 * config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP
			 * );
			 * config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40
			 * );
			 * config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104
			 * );
			 */
			config.wepTxKeyIndex = wepId;
			config.status = WifiConfiguration.Status.ENABLED;

		}
		if (type == SECURITY_PSK) // WIFICIPHER_WPA
		{
			Log.d("hsl", "SECURITY_PSK");
			if (Password.length() != 0) {
				if (Password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = Password;
				} else {
					config.preSharedKey = '"' + Password + '"';
				}
			}
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			/*
			 * config.hiddenSSID = true;
			 * config.allowedAuthAlgorithms.set(WifiConfiguration
			 * .AuthAlgorithm.OPEN);
			 * config.allowedGroupCiphers.set(WifiConfiguration
			 * .GroupCipher.TKIP);
			 * config.allowedKeyManagement.set(WifiConfiguration
			 * .KeyMgmt.WPA_PSK);
			 * config.allowedPairwiseCiphers.set(WifiConfiguration
			 * .PairwiseCipher.TKIP);
			 * config.allowedGroupCiphers.set(WifiConfiguration
			 * .GroupCipher.CCMP);
			 * config.allowedPairwiseCiphers.set(WifiConfiguration
			 * .PairwiseCipher.CCMP);
			 */
			// config.wepTxKeyIndex = wepId;
			config.status = WifiConfiguration.Status.ENABLED;
			if (!isDHCP) {
				setStatic(config, SSID);
				System.out.println("------��̬����------");
			}

		}
		return config;
	}

	/**
	 * wifi����
	 * 
	 * @param wifiName
	 *            wifi��
	 * @param wifiPswd
	 *            ����
	 * @param type
	 *            ����
	 * @param isDHCP
	 *            �Ƿ���dhcp����
	 * @param static_ip
	 *            ��̬ip
	 * @param static_gateway
	 *            ��̬����
	 * @param static_dns
	 *            dns
	 */
	public boolean wifiConnect(String wifiName, String wifiPswd, int wepId,
			boolean isDHCP) {
		if (wifiName != null && !"".equals(wifiName)) {
			WifiConfiguration existingConfig = IsExsitsConfigure(wifiName);
			if (existingConfig != null) {
				int netid = existingConfig.networkId;
				// �Ͽ�����
				mWifiManager.disableNetwork(netid);
				// �Ƴ�
				mWifiManager.removeNetwork(netid);
			}
			System.out.println("=======con========" + existingConfig);
			// ����
			WifiConfiguration configuration = connectWifi(wifiName, wifiPswd,
					wepId, isDHCP);
			System.out.println("=======con========" + configuration);
			return addNewWifi(configuration);
		}
		return false;
	}

	// ����
	public boolean addNewWifi(WifiConfiguration newConfig) {
		mWifiManager.updateNetwork(newConfig);
		int netID = mWifiManager.addNetwork(newConfig);// ���
		if (netID == -1) {
			return false;
		}
		mWifiManager.enableNetwork(netID, true);// ����
		boolean bRet = mWifiManager.saveConfiguration();
		return bRet;
	}

	// �Ͽ�ָ��ID������
	public void disConnectionWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiManager.removeNetwork(netId);
		mWifiManager.saveConfiguration();
	}

	/**
	 * ���þ�̬ip
	 * 
	 * @param wifiName
	 * @param static_ip
	 * @param static_gateway
	 * @param static_dns
	 */
	public void setStatic(final WifiConfiguration config, String wifiName) {

		if (config != null) {
			try {
				setIpAssignment(config, "STATIC");
				setWifiStaticIp(config, InetAddress.getByName(mStaticIp), 23);
				setWifiStaticGateway(config,
						InetAddress.getByName(mStaticGateway));
				setWifiStaticDNS(config, InetAddress.getByName(mStaticDns));
				mWifiManager.updateNetwork(config);
			} catch (Exception e) {
				Log.e("ken", "eeeee");
				e.printStackTrace();
			}
		}
	}

	/**
	 * �ж�ĳssid��AP�Ƿ��б����������Ϣ
	 * 
	 * @param SSID
	 * @return
	 */
	public WifiConfiguration IsExsitsConfigure(String SSID) {
		if (mWifiConfigList != null) {
			for (WifiConfiguration existingConfig : mWifiConfigList) {
				if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
					return existingConfig;
				}
			}
		}
		return null;
	}

	/**
	 * ������������Ϊ��̬
	 * 
	 * @param assign
	 * @param wifiConf
	 */
	public static void setIpAssignment(WifiConfiguration wifiConf, String assign) {
		ReflectionUtil.setEnumField(wifiConf, "ipAssignment", assign);
	}

	/**
	 * ����wifi�ľ�̬ip
	 * 
	 * @param addr
	 * @param prefixLength
	 * @param wifiConf
	 */
	@SuppressWarnings("unchecked")
	public static void setWifiStaticIp(WifiConfiguration wifiConf,
			InetAddress addr, int prefixLength) {
		Object linkProperties;
		try {
			linkProperties = ReflectionUtil
					.getField(wifiConf, "linkProperties");
			if (linkProperties == null)
				return;
			Class<?> laClass = Class.forName("android.net.LinkAddress");
			Constructor<?> laConstructor = laClass.getConstructor(new Class[] {
					InetAddress.class, int.class });
			Object linkAddress = laConstructor.newInstance(addr, prefixLength);
			@SuppressWarnings("rawtypes")
			ArrayList mLinkAddresses = (ArrayList) ReflectionUtil
					.getDeclaredField(linkProperties, "mLinkAddresses");
			mLinkAddresses.clear();
			mLinkAddresses.add(linkAddress);

			Log.d("hsl", "��ǰ�ľ�̬IP" + mLinkAddresses.get(0));// 192.168.118.110/23
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ����wifi�ľ�̬�ľ�̬����
	 * 
	 * @param wifiConf
	 * @param gateway
	 */
	@SuppressWarnings("unchecked")
	public static void setWifiStaticGateway(WifiConfiguration wifiConf,
			InetAddress gateway) {
		Object linkProperties;
		try {
			linkProperties = ReflectionUtil
					.getField(wifiConf, "linkProperties");
			if (linkProperties == null)
				return;
			if (android.os.Build.VERSION.SDK_INT >= 14) { // android4.x�汾
				Class<?> routeInfoClass = Class
						.forName("android.net.RouteInfo");
				Constructor<?> routeInfoConstructor = routeInfoClass
						.getConstructor(new Class[] { InetAddress.class });
				Object routeInfo = routeInfoConstructor.newInstance(gateway);
				ArrayList<Object> mRoutes = (ArrayList<Object>) ReflectionUtil
						.getDeclaredField(linkProperties, "mRoutes");
				mRoutes.clear();
				mRoutes.add(routeInfo);

				Log.d("hsl", "��ǰ�ľ�̬����" + mRoutes.get(0));// 0.0.0.0/0 ->
															// 192.168.118.2
			} else { // android3.x�汾
				ArrayList<InetAddress> mGateways = (ArrayList<InetAddress>) ReflectionUtil
						.getDeclaredField(linkProperties, "mGateways");
				mGateways.clear();
				mGateways.add(gateway);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ����wifi�ľ�̬dns
	 * 
	 * @param dns
	 * @param wifiConf
	 */
	public static void setWifiStaticDNS(WifiConfiguration wifiConf,
			InetAddress dns) {
		Object linkProperties;
		try {

			linkProperties = ReflectionUtil
					.getField(wifiConf, "linkProperties");
			if (linkProperties == null) {
				return;
			}
			@SuppressWarnings("unchecked")
			ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) ReflectionUtil
					.getDeclaredField(linkProperties, "mDnses");
			mDnses.clear(); // or add a new dns address , here I just want to
							// replace DNS1
			mDnses.add(dns);
			// /218.85.157.99

			Log.d("hsl", "��ǰ��dns" + mDnses.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * int���͵�ipתΪString����
	 * 
	 * @param paramInt
	 * @return
	 */
	public String intToIp(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
				+ (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
	}
}
