package com.yy.doorplate.model;

public class ClassMottoInfoModel {
	//班训信息
	public String id;				//班训信息I
	public String bjdm	;			//True	String	班级代码
	public String bjmc	;			//	False	String	班级名称
	public String fdy;				//True	String	班主任人员编号
	public String fdyxm;			//	False	String	班主任姓名
	public String classMotto;		//	False	String	班训
	public String Tagger;			//	False	String	奋斗目标
	public String aphorism;		//	False	String	班主任格言
	public String lifePhoto;		//	False	String	班主任生活照

	@Override
	public String toString() {
		return "ClassMottonfoModel [bjdm=" + bjdm + ", bjmc=" + bjmc + ", fdy="
				+ fdy + ", fdyxm=" + fdyxm + ", classMotto=" + classMotto
				+ ", Tagger=" + Tagger + ", aphorism=" + aphorism + ", lifePhoto="
				+ lifePhoto +"]";
	}
}
