package com.yy.doorplate.model;

public class OrgModel {
	public String orgId; // ����ID
	public String orgCode; // ��������
	public String orgName; // ��������
	public String parentOrgId; // �ϼ�����

	@Override
	public String toString() {
		return "Org [orgId=" + orgId + ", orgCode=" + orgCode + ", orgName="
				+ orgName + ", parentOrgId=" + parentOrgId + "]";
	}
}
