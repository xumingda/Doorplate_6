package com.yy.doorplate.model;

public class AnswerModel {

	// �����ʾ��
	public String questionCode; // �������
	public String answer; // ��
	public String replyPerson; // �ظ���Ա
	public String questionnaireCode; // �����ʾ����
	public String skjhid; // �ڿμƻ�ID

	@Override
	public String toString() {
		return "AnswerModel [questionCode=" + questionCode + ", answer="
				+ answer + ", replyPerson=" + replyPerson
				+ ", questionnaireCode=" + questionnaireCode + ", skjhid="
				+ skjhid + "]";
	}

}
