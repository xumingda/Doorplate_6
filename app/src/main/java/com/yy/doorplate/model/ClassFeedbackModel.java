package com.yy.doorplate.model;

public class ClassFeedbackModel {

	// �ڿη�����Ϣ
	public String id; // ����ID
	public String skjhid; // �ڿμƻ�ID
	public String feedback; // ��������
	public String level; // �����Ǽ�
	public String rybh; // ������Ա���

	@Override
	public String toString() {
		return "ClassFeedbackModel [id=" + id + ", skjhid=" + skjhid
				+ ", feedback=" + feedback + ", level=" + level + ", rybh="
				+ rybh + "]";
	}
}
