package com.yy.doorplate.tool;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * ��̫������������
 */
public class EthernetUtils {

	private Context mContext;
	private static final String ETHERNET_USE_STATIC_IP = "ethernet_use_static_ip";
	private static final String ETHERNET_STATIC_IP = "ethernet_static_ip";
	private static final String ETHERNET_STATIC_GATEWAY = "ethernet_static_gateway";
	private static final String ETHERNET_STATIC_NETMASK = "ethernet_static_netmask";
	private static final String ETHERNET_STATIC_DNS1 = "ethernet_static_dns1";
	private static final String ETHERNET_STATIC_DNS2 = "ethernet_static_dns2";

	// ��̫��״̬
	public static final String EXTRA_ETHERNET_STATE = "ethernet_state";
	public static final int ETHER_STATE_DISCONNECTED = 0;
	public static final int ETHER_STATE_CONNECTING = 1;
	public static final int ETHER_STATE_CONNECTED = 2;

	private final static String sNullIp = "0.0.0.0";
	private final static String sEthernetIfaceName = "eth0";

	public EthernetUtils(Context context) {
		this.mContext = context;
	}

	/**
	 * ���þ�̬ip
	 * 
	 * @param ip
	 */
	public void setEthInfoFromStaticIP(String ip) {
		Settings.System.putString(mContext.getContentResolver(),
				ETHERNET_STATIC_IP, ipFormatConversion1(ip));
	}

	/**
	 * ���þ�̬����
	 * 
	 * @param ip
	 */
	public void setEthInfoFromStaticGATEWAY(String gateWay) {
		Settings.System.putString(mContext.getContentResolver(),
				ETHERNET_STATIC_GATEWAY, ipFormatConversion1(gateWay));
	}

	/**
	 * ���þ�̬��������
	 * 
	 * @param ip
	 */
	public void setEthInfoFromStaticNetmask(String netMask) {
		Settings.System.putString(mContext.getContentResolver(),
				ETHERNET_STATIC_NETMASK, ipFormatConversion1(netMask));
	}

	/**
	 * ���þ�̬DNS1
	 */
	public void setEthInfoFromStaticDNS1(String dns1) {
		Settings.System.putString(mContext.getContentResolver(),
				ETHERNET_STATIC_DNS1, ipFormatConversion1(dns1));
	}

	/**
	 * ���þ�̬DNS2
	 */
	public void SetEthInfoFromStaticDNS2(String dns2) {
		Settings.System.putString(mContext.getContentResolver(),
				ETHERNET_STATIC_DNS2, ipFormatConversion1(dns2));
	}

	/**
	 * ��ȡ��̬IP
	 * 
	 * @return
	 */
	public String getEthInfoFromStaticIP() {
		return ipFormatConversion1(Settings.System.getString(
				mContext.getContentResolver(), ETHERNET_STATIC_IP));
	}

	/**
	 * ��ȡ��̬����
	 * 
	 * @return
	 */
	public String getEthInfoFromStaticGATEWAY() {
		return ipFormatConversion1(Settings.System.getString(
				mContext.getContentResolver(), ETHERNET_STATIC_GATEWAY));
	}

	/**
	 * ��ȡ��̬��������
	 * 
	 * @return
	 */
	public String getEthInfoFromStaticNetmask() {
		return ipFormatConversion1(Settings.System.getString(
				mContext.getContentResolver(), ETHERNET_STATIC_NETMASK));
	}

	/**
	 * ��ȡ��̬DNS1
	 * 
	 * @return
	 */
	public String getEthInfoFromStaticDNS1() {
		return ipFormatConversion1(Settings.System.getString(
				mContext.getContentResolver(), ETHERNET_STATIC_DNS1));
	}

	/**
	 * ��ȡ��̬DNS2
	 * 
	 * @return
	 */
	public String getEthInfoFromStaticDNS2() {
		return ipFormatConversion1(Settings.System.getString(
				mContext.getContentResolver(), ETHERNET_STATIC_DNS2));
	}

	/**
	 * ��ȡ��̬IP
	 * 
	 * @return
	 */
	public static String getEthInfoFromDhcpIP() {
		String tempIpInfo;
		tempIpInfo = MySystemProperties.get("dhcp." + sEthernetIfaceName
				+ ".ipaddress");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			return tempIpInfo;
		} else {
			return sNullIp;
		}
	}

	/**
	 * ��ȡ��̬��������
	 * 
	 * @return
	 */
	public String getEthInfoFromDhcpNetmask() {
		String tempIpInfo;
		tempIpInfo = MySystemProperties.get("dhcp." + sEthernetIfaceName
				+ ".mask");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			return tempIpInfo;
		} else {
			return sNullIp;
		}
	}

	/**
	 * ��ȡ��̬Ĭ������
	 * 
	 * @return
	 */
	public String getEthInfoFromDhcpGATEWAY() {
		String tempIpInfo;
		tempIpInfo = MySystemProperties.get("dhcp." + sEthernetIfaceName
				+ ".gateway");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			return tempIpInfo;
		} else {
			return sNullIp;
		}
	}

	/**
	 * ��ȡ��̬DNS1
	 * 
	 * @return
	 */
	public String getEthInfoFromDhcpDNS1() {
		String tempIpInfo;
		tempIpInfo = MySystemProperties.get("dhcp." + sEthernetIfaceName
				+ ".dns1");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			return tempIpInfo;
		} else {
			return sNullIp;
		}
	}

	/**
	 * ��ȡ��̬DNS2
	 * 
	 * @return
	 */
	public String getEthInfoFromDhcpDNS2() {
		String tempIpInfo;
		tempIpInfo = MySystemProperties.get("dhcp." + sEthernetIfaceName
				+ ".dns2");
		if ((tempIpInfo != null) && (!tempIpInfo.equals(""))) {
			return tempIpInfo;
		} else {
			return sNullIp;
		}
	}

	/**
	 * �Ƿ�ʹ�þ�̬ip
	 * 
	 * @return
	 */
	public boolean isUsingStaticIp() {
		return Settings.System.getInt(mContext.getContentResolver(),
				ETHERNET_USE_STATIC_IP, 0) == 1 ? true : false;
	}

	/**
	 * ���þ�̬ipʹ��
	 * 
	 * @return
	 */
	public void setStaticIpEnable() {
		Settings.System.putInt(mContext.getContentResolver(),
				ETHERNET_USE_STATIC_IP, 1);
	}

	/**
	 * ���þ�̬ipʧЧ
	 * 
	 * @return
	 */
	public void setStaticIpDisable() {
		Settings.System.putInt(mContext.getContentResolver(),
				ETHERNET_USE_STATIC_IP, 0);
	}

	/**
	 * ����̫������
	 */
	public void setEtherentEnable() {
		/*
		 * Intent intent = new Intent(ConstArg.ActionReceiver.ETHERNET_ENABLE);
		 * mContext.sendBroadcast(intent);
		 */
		Intent intent = new Intent();
		intent.setAction("ethernet_enable");
		mContext.sendBroadcast(intent);
	}

	/**
	 * �ر���̫������
	 */
	public void setEtherentDisable() {
		/*
		 * Intent intent = new Intent(ConstArg.ActionReceiver.ETHERNET_DISABLE);
		 * mContext.sendBroadcast(intent);
		 */
		Intent intent = new Intent();
		intent.setAction("ethernet_disable");
		mContext.sendBroadcast(intent);
	}

	/**
	 * ����ȡ����ip��ʽתΪ xxx.xxx.xxx.xxx��ʽ ����3λǰ�油0
	 * 
	 * @param ip
	 * @return
	 */
	public static String ipFormatConversion0(String ip) {
		StringBuilder ipResult = new StringBuilder();
		String tempIp[];
		if ((ip == null) || (ip.equals(""))) {
			return sNullIp;
		}
		tempIp = ip.split("\\.");
		if (tempIp.length != 4) {
			return sNullIp;
		}
		for (int i = 0; i < tempIp.length; i++) {
			if (tempIp[i].length() == 1) {
				tempIp[i] = "00" + tempIp[i];
			} else if (tempIp[i].length() == 2) {
				tempIp[i] = "0" + tempIp[i];
			}
			ipResult.append(tempIp[i]);
			if (i != (tempIp.length - 1))
				ipResult.append(".");
		}
		return ipResult.toString();
	}

	/**
	 * ����ȡ����ip��ʽתΪ ������ʽ ���� 001 -- 1 (��Ȼд�뾲̬ʱ���ᵼ���쳣)
	 * 
	 * @param ip
	 * @return
	 */
	public static String ipFormatConversion1(String ip) {
		StringBuilder ipResult = new StringBuilder();
		String tempIp[];
		if ((ip == null) || (ip.equals(""))) {
			return sNullIp;
		}
		tempIp = ip.split("\\.");
		if (tempIp.length != 4) {
			return sNullIp;
		}
		for (int i = 0; i < tempIp.length; i++) {
			ipResult.append(Integer.parseInt(tempIp[i]) + "");
			if (i != (tempIp.length - 1))
				ipResult.append(".");
		}
		return ipResult.toString();
	}

	/** ��̫���Ƿ����� */
	public boolean isEthernetConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

}
