package com.yy.doorplate.model;

import java.io.Serializable;

public class EquModelTaskModel implements Serializable {

	// �豸ģʽ������Ϣ
	private static final long serialVersionUID = 1L;

	public String startTime; // ��ʼʱ��
	public String stopTime; // ����ʱ��
	public String equModel; // �豸ģʽ 00:���� 01:���� 02:����
	public String screenModelCode; // չʾģ�����

	@Override
	public String toString() {
		return "EquModelTaskModel [startTime=" + startTime + ", stopTime="
				+ stopTime + ", equModel=" + equModel + ", screenModelCode="
				+ screenModelCode + "]";
	}

}
