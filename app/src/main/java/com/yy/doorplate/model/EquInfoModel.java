package com.yy.doorplate.model;

public class EquInfoModel {

	// 设备信息
	public String equCode; // 设备编码
	public String equName; // 设备名称
	public String jssysdm; // 教师实验室代码
	public String jssysmc; // 教师实验室名称
	public String ip; // IP
	public String gateway; // 网关
	public String subMask; // 子网掩码
	public String dns; // 首选域名服务器
	public String dns2; // 备用域名服务器
	public String mac; // mac地址
	public String status; // 00:离线 01:在线 02：故障
	public String equMode; // 设备模式
	public String bjdm; // 班级代码
	public String accSysIp; // 接入系统IP
	public String accSysPort; // 接入系统端口
	public String equType; // 设备类型
	public String networkType; // 网络类型 动态IP:DYNAMIC 静态IP:STATIC
	public String readCardRule; // 读卡规则，110：正序10进制，116:正序16进制，210:反序10进，216:反序16进制
	public String equVolume; // 音量（0-100）
	public String orgId; // 组织Id

	@Override
	public String toString() {
		return "EquInfoModel [equCode=" + equCode + ", equName=" + equName
				+ ", jssysdm=" + jssysdm + ", jssysmc=" + jssysmc + ", ip="
				+ ip + ", gateway=" + gateway + ", subMask=" + subMask
				+ ", dns=" + dns + ", dns2=" + dns2 + ", mac=" + mac
				+ ", status=" + status + ", equMode=" + equMode + ", bjdm="
				+ bjdm + ", accSysIp=" + accSysIp + ", accSysPort="
				+ accSysPort + ", equType=" + equType + ", networkType="
				+ networkType + ", readCardRule=" + readCardRule
				+ ", equVolume=" + equVolume + ", orgId=" + orgId + "]";
	}
}
