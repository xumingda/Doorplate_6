package com.yy.doorplate.model;

public class StarModel {

	// �༶֮��
	public String id; // ���
	public String starId; // ����
	public String starName; // ����
	public String starType; // ����1ѧУ��2�༶
	public String starIsCode; // ֮������
	public String getStarPersonCode; // ֮�ǻ�ȡ��Ա���
	public String getStarPersonName; // ֮�ǻ�ȡ��Ա����
	public String awardsPerson; // �佱������
	public String awardsUnit; // �佱��λ
	public String awardsData; // �佱����
	public String iconUrl; // ͼƬ��ַ
	public String Info; // ������

	@Override
	public String toString() {
		return "StarModel [id=" + id + ", starId=" + starId + ", starName="
				+ starName + ", starType=" + starType + ", starIsCode="
				+ starIsCode + ", getStarPersonCode=" + getStarPersonCode
				+ ", getStarPersonName=" + getStarPersonName
				+ ", awardsPerson=" + awardsPerson + ", awardsUnit="
				+ awardsUnit + ", awardsData=" + awardsData + ", iconUrl="
				+ iconUrl + ", Info=" + Info + "]";
	}
}
