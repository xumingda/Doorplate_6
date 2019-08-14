package com.yy.doorplate.model;

public class OrgModel {
	public String orgId; // 机构ID
	public String orgCode; // 机构编码
	public String orgName; // 机构名称
	public String parentOrgId; // 上级机构

	@Override
	public String toString() {
		return "Org [orgId=" + orgId + ", orgCode=" + orgCode + ", orgName="
				+ orgName + ", parentOrgId=" + parentOrgId + "]";
	}
}
