package com.yy.doorplate.model;

public class DiyMenuModel {

	public String id;// ���
	public String itemCode;// �˵�����
	public String parmentCode;// �����˵�����
	public String name;// �˵�����
	public String iconAddress;// �˵�ͼ���ַ
	public String valueAddress;// �˵����ݵ�ַ

	@Override
	public String toString() {
		return "DiyMenuModel [id=" + id + ", itemCode=" + itemCode
				+ ", parmentCode=" + parmentCode + ", name=" + name
				+ ", iconAddress=" + iconAddress + ", valueAddress="
				+ valueAddress + "]";
	}

}
