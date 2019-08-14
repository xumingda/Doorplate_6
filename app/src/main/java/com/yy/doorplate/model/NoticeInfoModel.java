package com.yy.doorplate.model;

import java.io.Serializable;

public class NoticeInfoModel implements Serializable {

	// 通知
	public String id; // 信息主题ID
	public String lmdm; // 栏目代码，学校通知:bpxxtz,学校新闻:bpxxxw,班级通知:bpbjtz,班级新闻:bpbjxw
	public String lmmc; // 栏目名称
	public String xxzt; // 主题
	public String xxnr; // 发布内容
	public String fbr; // 发布人
	public String fbrxm; // 发布人姓名
	public String fbrbm; // 发布人部门
	public String fbrbmmc; // 发布人部门名称
	public String fbsj; // 发布时间
	public String hhs; // 回复数
	public String dzs; // 点赞数
	public String zttp; // 主题图片
	public String tplb; // 图片列表
	public String wjlb; // 文件列表
	public String remark; // 说明
	public String create_time; // 增加时间
	public String update_time; // 更新时间
	public String deltag; // 删除标志
	public String oprybh; // 操作人员
	public String xxztcode; // 信息主题CODE
	public String xxztlb; // 学校新闻栏目类别
	public String xxztzt; // 信息主题状态 1禁用 0启用
	public String nrlb; // 内容类别，0:普通信息,1:调查问卷;2:投票评比;3:活动报名
	public String gldm; // 关联内容ID
	public String endTime; // 失效时间

	@Override
	public String toString() {
		return "NoticeInfoModel [id=" + id + ", lmdm=" + lmdm + ", lmmc="
				+ lmmc + ", xxzt=" + xxzt + ", xxnr=" + xxnr + ", fbr=" + fbr
				+ ", fbrxm=" + fbrxm + ", fbrbm=" + fbrbm + ", fbrbmmc="
				+ fbrbmmc + ", fbsj=" + fbsj + ", hhs=" + hhs + ", dzs=" + dzs
				+ ", zttp=" + zttp + ", tplb=" + tplb + ", wjlb=" + wjlb
				+ ", remark=" + remark + ", create_time=" + create_time
				+ ", update_time=" + update_time + ", deltag=" + deltag
				+ ", oprybh=" + oprybh + ", xxztcode=" + xxztcode + ", xxztlb="
				+ xxztlb + ", xxztzt=" + xxztzt + ", nrlb=" + nrlb + ", gldm="
				+ gldm + ", end_time=" + endTime + "]";
	}
}
