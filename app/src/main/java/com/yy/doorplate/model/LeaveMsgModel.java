package com.yy.doorplate.model;

public class LeaveMsgModel {

	public String kind; // �������� stu:ѧ��,patriarch���ҳ�, tearch:��ʦ
	public String bjdm; // ���������ڰ༶
	public String formRyType; // ������Ա���� stu:ѧ��,patriarch���ҳ�, tearch:��ʦ
	public String formRybh; // ������Ա���
	public String formRyXm; // ������Ա����
	public String toRyType; // ���Զ�����Ա���� stu:ѧ��,patriarch���ҳ�, tearch:��ʦ
	public String toRybh; // ����Ŀ����Ա���
	public String toRyXm; // ����Ŀ����Ա����
	public String msg; // ��������
	public String msgDate; // ��������

	@Override
	public String toString() {
		return "LeaveMsgModel [kind=" + kind + ", bjdm=" + bjdm
				+ ", formRyType=" + formRyType + ", formRybh=" + formRybh
				+ ", formRyXm=" + formRyXm + ", toRyType=" + toRyType
				+ ", toRybh=" + toRybh + ", toRyXm=" + toRyXm + ", msg=" + msg
				+ ", msgDate=" + msgDate + "]";
	}
}
