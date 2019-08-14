package com.yy.doorplate.model;

public class LeaveModel {

	public String leaveId; // ���id
	public String leaveType; // �������1�¼�2���� 13����
	public String leaveInfo; // �������
	public String leaveRybh; // �������Ա���
	public String dealRybh; // ��������Ա���
	public String leaveStatus; // ���״̬����0 ͨ��1 �ܾ�2
	public String startDate; // ��ٿ�ʼ���� (yyyy-MM-dd)
	public String startTime; // ��ٿ�ʼʱ�� (HH:mm:ss)
	public String endDate; // ��ٽ������� (yyyy-MM-dd)
	public String endTime; // ��ٽ���ʱ�� (HH:mm:ss)
	public String creatTime; // ����ʱ�� (yyyy-MMdd HH:mm:ss)
	public String updateTime; // ����ʱ�� (yyyy-MMdd HH:mm:ss)
	public String leaveName; // ���������
	public String dealName; // ����������
	public String bjdm; // ����˰༶
	public String bjmc; // ����˰༶����
	public String leaveTimeLength; // ���ʱ��

	@Override
	public String toString() {
		return "LeaveModel [leaveId=" + leaveId + ", leaveType=" + leaveType
				+ ", leaveInfo=" + leaveInfo + ", leaveRybh=" + leaveRybh
				+ ", dealRybh=" + dealRybh + ", leaveStatus=" + leaveStatus
				+ ", startDate=" + startDate + ", startTime=" + startTime
				+ ", endDate=" + endDate + ", endTime=" + endTime
				+ ", creatTime=" + creatTime + ", updateTime=" + updateTime
				+ ", leaveName=" + leaveName + ", dealName=" + dealName
				+ ", bjdm=" + bjdm + ", bjmc=" + bjmc + ", leaveTimeLength="
				+ leaveTimeLength + "]";
	}
}
