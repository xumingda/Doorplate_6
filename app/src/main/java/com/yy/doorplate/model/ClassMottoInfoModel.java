package com.yy.doorplate.model;

public class ClassMottoInfoModel {
	//��ѵ��Ϣ
	public String id;				//��ѵ��ϢI
	public String bjdm	;			//True	String	�༶����
	public String bjmc	;			//	False	String	�༶����
	public String fdy;				//True	String	��������Ա���
	public String fdyxm;			//	False	String	����������
	public String classMotto;		//	False	String	��ѵ
	public String Tagger;			//	False	String	�ܶ�Ŀ��
	public String aphorism;		//	False	String	�����θ���
	public String lifePhoto;		//	False	String	������������

	@Override
	public String toString() {
		return "ClassMottonfoModel [bjdm=" + bjdm + ", bjmc=" + bjmc + ", fdy="
				+ fdy + ", fdyxm=" + fdyxm + ", classMotto=" + classMotto
				+ ", Tagger=" + Tagger + ", aphorism=" + aphorism + ", lifePhoto="
				+ lifePhoto +"]";
	}
}
