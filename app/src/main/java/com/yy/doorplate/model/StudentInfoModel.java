package com.yy.doorplate.model;

import java.util.List;

public class StudentInfoModel {

	// ѧ����Ϣ
	public String id; // ѧ����ϢID
	public String remark; // ˵��
	public String xsxh; // ѧ��ѧ��
	public String xm; // ����
	public String zwh; // ��λ��
	public String rxny; // ��ѧ����
	public String ssxy; // ����ѧԺ
	public String ssxb; // ����ϵ��
	public String ssxbmc; // ����ϵ������
	public String sszy; // ����רҵ
	public String sszymc; // ����רҵ����
	public String ssbj; // �����༶
	public String ssbjmc; // �����༶����
	public String xslb; // ѧ����� 1�п��� 2������ 3������
	public String xszt; // ѧ��״̬ 0����У 1��У
	public String xb; // �Ա� 1�� 2Ů
	public String zjlx; // ֤������ 0���֤
	public String zjh; // ֤����
	public String zp; // ������Ƭ��ַ
	public String xszw; // ѧ��ְ��
	public String bzr; // ������
	public String dzxx; // ��������
	public String csny; // ��������
	public String yddh; // ��ϵ�绰
	public String lxrdh; // ������ϵ�绰
	public String jtdz; // ��ͥ��ַInfo
	public String tc; // �س�
	public String cardid; // ��ID
	public String xsjzuid; // ѧ���ҳ�uid
	public String uid; // ѧ��uid
	public List<KcxxInfoType> kcxxInfoList;
	public class KcxxInfoType{
		public String kcdm;
		public String kcmc;

	}

	@Override
	public String toString() {
		return "StudentInfoModel{" +
				"id='" + id + '\'' +
				", remark='" + remark + '\'' +
				", xsxh='" + xsxh + '\'' +
				", xm='" + xm + '\'' +
				", zwh='" + zwh + '\'' +
				", rxny='" + rxny + '\'' +
				", ssxy='" + ssxy + '\'' +
				", ssxb='" + ssxb + '\'' +
				", ssxbmc='" + ssxbmc + '\'' +
				", sszy='" + sszy + '\'' +
				", sszymc='" + sszymc + '\'' +
				", ssbj='" + ssbj + '\'' +
				", ssbjmc='" + ssbjmc + '\'' +
				", xslb='" + xslb + '\'' +
				", xszt='" + xszt + '\'' +
				", xb='" + xb + '\'' +
				", zjlx='" + zjlx + '\'' +
				", zjh='" + zjh + '\'' +
				", zp='" + zp + '\'' +
				", xszw='" + xszw + '\'' +
				", bzr='" + bzr + '\'' +
				", dzxx='" + dzxx + '\'' +
				", csny='" + csny + '\'' +
				", yddh='" + yddh + '\'' +
				", lxrdh='" + lxrdh + '\'' +
				", jtdz='" + jtdz + '\'' +
				", tc='" + tc + '\'' +
				", cardid='" + cardid + '\'' +
				", xsjzuid='" + xsjzuid + '\'' +
				", uid='" + uid + '\'' +
				", kcxxInfoList=" + kcxxInfoList +
				'}';
	}
}
