package com.yy.doorplate.model;

public class ClassEquInfoModel {

	// 教室设备信息
	public String id; // 设备ID
	public String zcsblbdm; // 资产设备列表代码
	public String zcsblbmc; // 资产设备列表名称
	public String zcsbbh; // 资产设备编号
	public String zcsbmc; // 资产设备名称
	public String zcsbgys; // 设备供应商
	public String zcsbgysmc; // 设备供应商名称
	public String zcsbpp; // 品牌
	public String ggxh; // 规格型号
	public String jldw; // 计量单位
	public String sbxx; // 设备信息
	public String gzsj; // 购置时间
	public String sbje; // 设备金额
	public String cfdddm; // 存放地点
	public String cfddmc; // 存放地点名称
	public String glbmdm; // 负责管理部门
	public String glbmmc; // 负责管理部门名称
	public String sbglfzr; // 设备管理负责人
	public String sbglfzrxm; // 设备管理负责人姓名
	public String sbsyr; // 设备使用人
	public String sbsyrxm; // 设备使用人姓名
	public String sbzk; // 状态 1闲置 2使用 3维修 4报废
	public String remark; // 说明

	@Override
	public String toString() {
		return "ClassEquInfoModel [id=" + id + ", zcsblbdm=" + zcsblbdm
				+ ", zcsblbmc=" + zcsblbmc + ", zcsbbh=" + zcsbbh + ", zcsbmc="
				+ zcsbmc + ", zcsbgys=" + zcsbgys + ", zcsbgysmc=" + zcsbgysmc
				+ ", zcsbpp=" + zcsbpp + ", ggxh=" + ggxh + ", jldw=" + jldw
				+ ", sbxx=" + sbxx + ", gzsj=" + gzsj + ", sbje=" + sbje
				+ ", cfdddm=" + cfdddm + ", cfddmc=" + cfddmc + ", glbmdm="
				+ glbmdm + ", glbmmc=" + glbmmc + ", sbglfzr=" + sbglfzr
				+ ", sbglfzrxm=" + sbglfzrxm + ", sbsyr=" + sbsyr
				+ ", sbsyrxm=" + sbsyrxm + ", sbzk=" + sbzk + ", remark="
				+ remark + "]";
	}
}
