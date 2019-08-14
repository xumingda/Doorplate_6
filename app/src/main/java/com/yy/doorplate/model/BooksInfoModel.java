package com.yy.doorplate.model;

public class BooksInfoModel {

	// 推荐图书信息
	public String id; // 图书ID
	public String title; // 书名
	public String bookNum; // 图书编号
	public String publish; // 出版社
	public String author; // 作者
	public String updateTime; // 更新时间
	public String summary; // 简介
	public String cover; // 图书封面
	public String sort; // 排序
	public String dataSources; // 数据来源
	public String thirdbookId; // 第三方图书ID

	@Override
	public String toString() {
		return "BooksInfoModel [id=" + id + ", title=" + title + ", bookNum="
				+ bookNum + ", publish=" + publish + ", author=" + author
				+ ", updateTime=" + updateTime + ", summary=" + summary
				+ ", cover=" + cover + ", sort=" + sort + ", dataSources="
				+ dataSources + ", thirdbookId=" + thirdbookId + "]";
	}
}
