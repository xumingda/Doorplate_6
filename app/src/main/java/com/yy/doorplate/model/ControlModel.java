package com.yy.doorplate.model;

public class ControlModel {

	// ������Ϣ
	public String id; // �豸�򳡾�ID
	public String type; // ���ͣ�00:�豸��01:����
	public String name; // ����
	public String status; // ״̬,00����,01:��

	@Override
	public String toString() {
		return "ControlModel [id=" + id + ", type=" + type + ", name=" + name
				+ ", status=" + status + "]";
	}
}
