package com.yy.doorplate.model;

public class EquInfoModel {

	// �豸��Ϣ
	public String equCode; // �豸����
	public String equName; // �豸����
	public String jssysdm; // ��ʦʵ���Ҵ���
	public String jssysmc; // ��ʦʵ��������
	public String ip; // IP
	public String gateway; // ����
	public String subMask; // ��������
	public String dns; // ��ѡ����������
	public String dns2; // ��������������
	public String mac; // mac��ַ
	public String status; // 00:���� 01:���� 02������
	public String equMode; // �豸ģʽ
	public String bjdm; // �༶����
	public String accSysIp; // ����ϵͳIP
	public String accSysPort; // ����ϵͳ�˿�
	public String equType; // �豸����
	public String networkType; // �������� ��̬IP:DYNAMIC ��̬IP:STATIC
	public String readCardRule; // ��������110������10���ƣ�116:����16���ƣ�210:����10����216:����16����
	public String equVolume; // ������0-100��
	public String orgId; // ��֯Id

	@Override
	public String toString() {
		return "EquInfoModel [equCode=" + equCode + ", equName=" + equName
				+ ", jssysdm=" + jssysdm + ", jssysmc=" + jssysmc + ", ip="
				+ ip + ", gateway=" + gateway + ", subMask=" + subMask
				+ ", dns=" + dns + ", dns2=" + dns2 + ", mac=" + mac
				+ ", status=" + status + ", equMode=" + equMode + ", bjdm="
				+ bjdm + ", accSysIp=" + accSysIp + ", accSysPort="
				+ accSysPort + ", equType=" + equType + ", networkType="
				+ networkType + ", readCardRule=" + readCardRule
				+ ", equVolume=" + equVolume + ", orgId=" + orgId + "]";
	}
}
