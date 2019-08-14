package com.yy.doorplate.model;

import java.util.List;

public class StudentInfoModel {

	// 学生信息
	public String id; // 学生信息ID
	public String remark; // 说明
	public String xsxh; // 学生学号
	public String xm; // 姓名
	public String zwh; // 座位号
	public String rxny; // 入学年月
	public String ssxy; // 所属学院
	public String ssxb; // 所属系部
	public String ssxbmc; // 所属系部名称
	public String sszy; // 所属专业
	public String sszymc; // 所属专业名称
	public String ssbj; // 所属班级
	public String ssbjmc; // 所属班级名称
	public String xslb; // 学生类别 1招考生 2交换生 3旁听生
	public String xszt; // 学生状态 0不在校 1在校
	public String xb; // 性别 1男 2女
	public String zjlx; // 证件类型 0身份证
	public String zjh; // 证件号
	public String zp; // 个人照片地址
	public String xszw; // 学生职务
	public String bzr; // 班主任
	public String dzxx; // 电子信箱
	public String csny; // 出生年月
	public String yddh; // 联系电话
	public String lxrdh; // 紧急联系电话
	public String jtdz; // 家庭地址Info
	public String tc; // 特长
	public String cardid; // 卡ID
	public String xsjzuid; // 学生家长uid
	public String uid; // 学生uid
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
