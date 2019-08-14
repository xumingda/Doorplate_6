package com.yy.doorplate.model;

public class DiyMenuModel {

	public String id;// 编号
	public String itemCode;// 菜单编码
	public String parmentCode;// 父级菜单编码
	public String name;// 菜单名称
	public String iconAddress;// 菜单图标地址
	public String valueAddress;// 菜单内容地址

	@Override
	public String toString() {
		return "DiyMenuModel [id=" + id + ", itemCode=" + itemCode
				+ ", parmentCode=" + parmentCode + ", name=" + name
				+ ", iconAddress=" + iconAddress + ", valueAddress="
				+ valueAddress + "]";
	}

}
