package com.yy.doorplate.model;

public class PrizeInfoModel {

	// ����ǽ��Ϣ
	public String prizeId; // ��״ID
	public String prizeCode; // ��״����
	public String prizeName; // ��״����
	public String iconUrl; // ͼƬ·��
	public String prizeDate; // ������
	public String prizeType; // ��״���ͣ�ѧУ��SCHOOL���༶:CLASS
	public String relaCode; // �������룬����ΪѧУʱ����ѧУ���룬����Ϊ�༶ʱΪ�༶����
	public String prizePerson; // ����Ա
	public String prizeUnit; // �񽱵�λ
	public String ranking; // ����
	public String awardsUnit; // �佱��λ
	public String xsxh; // ѧ��ѧ��

	@Override
	public String toString() {
		return "PrizeInfoModel [prizeId=" + prizeId + ", prizeCode="
				+ prizeCode + ", prizeName=" + prizeName + ", iconUrl="
				+ iconUrl + ", prizeDate=" + prizeDate + ", prizeType="
				+ prizeType + ", relaCode=" + relaCode + ", prizePerson="
				+ prizePerson + ", prizeUnit=" + prizeUnit + ", ranking="
				+ ranking + ", awardsUnit=" + awardsUnit + ", xsxh=" + xsxh
				+ "]";
	}

}
