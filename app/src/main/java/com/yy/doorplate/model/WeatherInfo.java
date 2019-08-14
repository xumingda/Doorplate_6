package com.yy.doorplate.model;

public class WeatherInfo {

	// 天气
	public String currentCity;// 当前城市
	public String date;// 日期
	public String dayPictureUrl;// 白天图片路径
	public String nightPictureUrl;// 夜晚图片路径
	public String weather;// 天气
	public String wind;// 风
	public String temperature;// 温度

	@Override
	public String toString() {
		return "WeatherInfo [currentCity=" + currentCity + ", date=" + date
				+ ", dayPictureUrl=" + dayPictureUrl + ", nightPictureUrl="
				+ nightPictureUrl + ", weather=" + weather + ", wind=" + wind
				+ ", temperature=" + temperature + "]";
	}

}
