package com.yy.doorplate.model;

import java.util.List;

public class VoteOptionModel {

	// ͶƱѡ����Ϣ
	public String voteOptionId; // ID
	// public String voteThemeId; // ͶƱ����ID
	public String optionPic; // ͼƬ·��
	public String optionTitle; // ����
	public String optionValue; // ����
	public String optionVoteCount; // ��ͶƱ����
	public List<VoteInfoModel> voteInfoList; // ��ͶƱ��Ϣ�б�

	@Override
	public String toString() {
		return "VoteOptionModel [voteOptionId=" + voteOptionId + ", optionPic="
				+ optionPic + ", optionTitle=" + optionTitle + ", optionValue="
				+ optionValue + ", optionVoteCount=" + optionVoteCount
				+ ", voteInfoList=" + voteInfoList + "]";
	}
}
