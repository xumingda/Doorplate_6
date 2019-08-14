package com.yy.doorplate.model;

public class TelMenuInfoModel {

	// 对讲目录信息
	public String menuId; // id
	public String menuName; // 菜单名称
	public String menuSort; // 排序
	public String menuCode; // 编码
	public String menuIcon; // 图标
	public String equInfoList; // 设备信息列表
	public String menuType; // 设备信息列表类型

	@Override
	public String toString() {
		return "TelMenuInfoModel [menuId=" + menuId + ", menuName=" + menuName
				+ ", menuSort=" + menuSort + ", menuCode=" + menuCode
				+ ", menuIcon=" + menuIcon + ", equInfoList=" + equInfoList
				+ ", menuType=" + menuType + "]";
	}

}
