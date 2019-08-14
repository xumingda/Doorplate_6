package com.yy.doorplate.model;

public class VoteInfoModel {

	// 投票信息
	public String voteThemeId; // 主题ID
	public String voteOptionId; // 选项ID
	public String rybh; // 人员编号

	@Override
	public String toString() {
		return "VoteInfoModel [voteThemeId=" + voteThemeId + ", voteOptionId="
				+ voteOptionId + ", rybh=" + rybh + "]";
	}
}
