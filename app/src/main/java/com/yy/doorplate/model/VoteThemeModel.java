package com.yy.doorplate.model;

import java.util.List;

public class VoteThemeModel {

	// ͶƱ����
	public String voteThemeId; // ID
	public String themeName; // ��������
	public String themeIcon; // ͼƬ
	public String themeValue; // ����
	public String voteKinds; // ͶƱ���ͣ�1����ѡ��2:��ѡ
	public String allowNum; // ��ѡ����ͶƱ��
	public String participationCount; // ��ͶƱ����
	public String activityKind; // ͶƱ���ͣ�ͶƱ���ȣ�vote,�����:activity
	public String endTime; // ��ʼʱ��
	public String startTime; // ����ʱ��
	public String themeIconTitle; // ������ͼ
	public String belongTo; // ��������
	public List<VoteOptionModel> voteOptionList; // ѡ����Ϣ�б�

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
