package com.yy.doorplate.model;

public class ClassRoomInfoModel {

	// ������Ϣ
	public String id; // ����ʵ������ϢID
	public String lx; // ����ʵ��������
	public String jssysdm; // ��ʦʵ���Ҵ���
	public String jssysmc; // ��ʦʵ��������
	public String xsrl; // ѧ������
	public String bjrl; // �༶����
	public String gxjgdm; // ��Ͻ�������루��������ָ
	public String gxjgmc; // ��Ͻ�������ƣ���������ָ
	public String remark; // ˵��
	public String cdglyxm; // ���ع���Ա����
	public String cdglygh; // ���ع���Ա����
	public String cdglydh; // ���ع���Ա�绰
	public String cdglyzp; // ���ع���Ա��Ƭ
	public String cdglykh; // ���ع���Ա����

	@Override
	public String toString() {
		return "ClassRoomInfoModel [id=" + id + ", lx=" + lx + ", jssysdm="
				+ jssysdm + ", jssysmc=" + jssysmc + ", xsrl=" + xsrl
				+ ", bjrl=" + bjrl + ", gxjgdm=" + gxjgdm + ", gxjgmc="
				+ gxjgmc + ", remark=" + remark + ", cdglyxm=" + cdglyxm
				+ ", cdglygh=" + cdglygh + ", cdglydh=" + cdglydh
				+ ", cdglyzp=" + cdglyzp + ", cdglykh=" + cdglykh + "]";
	}
}
