package com.yy.doorplate.model;

public class ClockInfoModel {

	// �򿨼�¼
	public String id; // �򿨼�¼ID
	public String xm; // ����
	public String js; // ��ɫ��1:��ʦ,2:ѧ��
	public String rybh; // ��Ա���
	public String dkrq; // ������
	public String dksj; // ��ʱ��
	public String dklx; // �����ͣ�1��ˢ��
	public String isupload; // ������¼�Ƿ��ϴ�ƽ̨��0��δ�ϴ���1�����ϴ�

	@Override
	public String toString() {
		return "ClockInfoModel [id=" + id + ", xm=" + xm + ", js=" + js
				+ ", rybh=" + rybh + ", dkrq=" + dkrq + ", dksj=" + dksj
				+ ", dklx=" + dklx + ", isupload=" + isupload + "]";
	}

}
