package com.yy.doorplate.model;

public class ClockRuleModel {

	// ���ڹ���
	public String sbkyxtq; // �ϰ࿨������ǰ������
	public String xbkyxtq; // �°࿨������ǰ������
	public String sbkyxyh; // �ϰ࿨�����Ӻ������
	public String xbkyxyh; // �°࿨�����Ӻ������
	public String jbddk; // �Ӱ൥��
	public String jbdxxdk; // �Ӱ൥��Ϣ��
	public String xbbdk; // �°಻�� 0 ���� 1��
	public String yxcd; // ����ٵ�������
	public String yzcd; // ���سٵ�������
	public String cgcd; // �����ٵ�������
	public String dkgz; // �򿨹���0�����ݿα��1����������ʱ��δ�
	public String js; // ��ɫ��1:��ʦ,2:ѧ��

	@Override
	public String toString() {
		return "ClockRuleModel [sbkyxtq=" + sbkyxtq + ", xbkyxtq=" + xbkyxtq
				+ ", sbkyxyh=" + sbkyxyh + ", xbkyxyh=" + xbkyxyh + ", jbddk="
				+ jbddk + ", jbdxxdk=" + jbdxxdk + ", xbbdk=" + xbbdk
				+ ", yxcd=" + yxcd + ", yzcd=" + yzcd + ", cgcd=" + cgcd
				+ ", dkgz=" + dkgz + ", js=" + js + "]";
	}
}
