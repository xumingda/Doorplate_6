package com.yy.doorplate.model;

public class BooksInfoModel {

	// �Ƽ�ͼ����Ϣ
	public String id; // ͼ��ID
	public String title; // ����
	public String bookNum; // ͼ����
	public String publish; // ������
	public String author; // ����
	public String updateTime; // ����ʱ��
	public String summary; // ���
	public String cover; // ͼ�����
	public String sort; // ����
	public String dataSources; // ������Դ
	public String thirdbookId; // ������ͼ��ID

	@Override
	public String toString() {
		return "BooksInfoModel [id=" + id + ", title=" + title + ", bookNum="
				+ bookNum + ", publish=" + publish + ", author=" + author
				+ ", updateTime=" + updateTime + ", summary=" + summary
				+ ", cover=" + cover + ", sort=" + sort + ", dataSources="
				+ dataSources + ", thirdbookId=" + thirdbookId + "]";
	}
}
