package com.yy.doorplate.model;

public class TelMenuInfoModel {

	// �Խ�Ŀ¼��Ϣ
	public String menuId; // id
	public String menuName; // �˵�����
	public String menuSort; // ����
	public String menuCode; // ����
	public String menuIcon; // ͼ��
	public String equInfoList; // �豸��Ϣ�б�
	public String menuType; // �豸��Ϣ�б�����

	@Override
	public String toString() {
		return "TelMenuInfoModel [menuId=" + menuId + ", menuName=" + menuName
				+ ", menuSort=" + menuSort + ", menuCode=" + menuCode
				+ ", menuIcon=" + menuIcon + ", equInfoList=" + equInfoList
				+ ", menuType=" + menuType + "]";
	}

}
