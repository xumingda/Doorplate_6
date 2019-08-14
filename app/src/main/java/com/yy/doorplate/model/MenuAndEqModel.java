package com.yy.doorplate.model;

public class MenuAndEqModel {

	// 对讲目录与设备关联信息
	public int id;
	public String menuId;
	public String equCode; // 设备编码

	@Override
	public String toString() {
		return "MenuAndEqModel [id=" + id + ", menuId=" + menuId + ", equCode="
				+ equCode + "]";
	}
}
