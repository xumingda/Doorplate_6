package com.yy.doorplate.model;

public class OptionModel {

	// �����ʾ�����ѡ��
	public String questionCode; // �������
	public String optionCode; // ѡ�����
	public String optionName; // ѡ������

	@Override
	public String toString() {
		return "OptionModel [questionCode=" + questionCode + ", optionCode="
				+ optionCode + ", optionName=" + optionName + "]";
	}
}
