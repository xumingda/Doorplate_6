package com.yy.doorplate.model;

public class CoursewareModel {

	// �μ���Ϣ
	public String id; // �μ�ID
	public String skjhid; // �ڿμƻ�ID
	public String fileName; // �μ�����
	public String filePath; // �μ�·��

	@Override
	public String toString() {
		return "CoursewareModel [id=" + id + ", skjhid=" + skjhid
				+ ", fileName=" + fileName + ", filePath=" + filePath + "]";
	}
}
