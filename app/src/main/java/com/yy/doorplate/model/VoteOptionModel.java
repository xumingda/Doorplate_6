package com.yy.doorplate.model;

import java.util.List;

public class VoteOptionModel {

	// 投票选项信息
	public String voteOptionId; // ID
	// public String voteThemeId; // 投票主题ID
	public String optionPic; // 图片路径
	public String optionTitle; // 标题
	public String optionValue; // 内容
	public String optionVoteCount; // 已投票人数
	public List<VoteInfoModel> voteInfoList; // 已投票信息列表

	@Override
	public String toString() {
		return "VoteOptionModel [voteOptionId=" + voteOptionId + ", optionPic="
				+ optionPic + ", optionTitle=" + optionTitle + ", optionValue="
				+ optionValue + ", optionVoteCount=" + optionVoteCount
				+ ", voteInfoList=" + voteInfoList + "]";
	}
}
