package com.yy.doorplate.model;

public class UserInfoModel {

	// �û���Ϣ
	public String userName; // �û���
	public String password; // ���룬���ܺ�����
	public String realName; // ��ʵ����
	public String cardNo; // ������

	@Override
	public String toString() {
		return "UserInfoModel [userName=" + userName + ", password=" + password
				+ ", realName=" + realName + ", cardNo=" + cardNo + "]";
	}

}
