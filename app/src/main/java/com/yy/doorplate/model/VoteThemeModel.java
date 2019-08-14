package com.yy.doorplate.model;

import java.util.List;

public class VoteThemeModel {

	// 投票主题
	public String voteThemeId; // ID
	public String themeName; // 主题名称
	public String themeIcon; // 图片
	public String themeValue; // 内容
	public String voteKinds; // 投票类型，1：单选，2:多选
	public String allowNum; // 多选允许投票数
	public String participationCount; // 已投票人数
	public String activityKind; // 投票类型，投票评比：vote,活动报名:activity
	public String endTime; // 开始时间
	public String startTime; // 结束时间
	public String themeIconTitle; // 主题主图
	public String belongTo; // 活动主题归类
	public List<VoteOptionModel> voteOptionList; // 选项信息列表

	@Override
	public String toString() {
		return "VoteThemeModel [voteThemeId=" + voteThemeId + ", themeName="
				+ themeName + ", themeIcon=" + themeIcon + ", themeValue="
				+ themeValue + ", voteKinds=" + voteKinds + ", allowNum="
				+ allowNum + ", participationCount=" + participationCount
				+ ", activityKind=" + activityKind + ", endTime=" + endTime
				+ ", startTime=" + startTime + ", themeIconTitle="
				+ themeIconTitle + ", belongTo=" + belongTo
				+ ", voteOptionList=" + voteOptionList + "]";
	}

}
