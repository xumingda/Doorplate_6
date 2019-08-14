package com.advertisement.resource;

import java.util.ArrayList;

public class ResPlaybill {
	
	public class PlayList{
		public String orderno=null;   //--订单号
		public String datebegin=null; //--起始日期 19700101
		public String dateend=null;   //--结束日期20160101
		
		public PlayList(String orderno,String datebegin,String dateend) {
			this.orderno=orderno;
			this.datebegin=datebegin;
			this.dateend=dateend;
			
			mImageAd=null;
			mVideoAd=null;
			mLabelAd=null;
		
		}
		
		public class AdBase{
			public int id=0;           
			public String url=null;   //--图片/视频 改url保存路径，label的时候保存文字内容
			public String name=null;  //--名称
			public int pri=0;         //--优先级
			public int max=0;         //--最大播放次数，65535表示不限次数
			public int length=0;      //--播放时间长度，以秒为单位,0-65535秒
			public class Periods{
				public int loop=0;    //--循环次数
				public String time=null; //--当日起始结束时间 00001200 00点00分启动，12点00分结束
				public int max=0;     //--单日最大播放次数，当本时间段的播放次数超过设定值时，本时间段不再播放此广告
				
				public Periods(int loop,String time,int max){
					this.loop=loop;
					this.time=time;
					this.max=max;
				}
		
			}
			
			public Periods periods=null;
			
			public void addPeriods(int loop,String time,int max){
				
				periods=new Periods(loop, time, max);
			}
			/*
			public AdBase(int id,String url,String name,int pri,int max) {
				// TODO Auto-generated constructor stub
				this.id=id;
				this.url=url;
				this.name=name;
				this.pri=pri;
				this.max=max;
			}
			*/
			public void setAdBase(int id,String url,String name,int pri,int max) {
				// TODO Auto-generated constructor stub
				this.id=id;
				this.url=url;
				this.name=name;
				this.pri=pri;
				this.max=max;
			}
			
			public void setPeriods(int loop,String time,int max){
				periods=new Periods(loop, time, max);
			}
		}
		
		public class ImageAd extends AdBase{
	
		}
		
		public class VideoAd extends AdBase{

			public int volume=0; //--音量
			public String tone=null;//--亮度
			
			public void setOpt(int volume,String tone){;
				this.volume=volume;
				this.tone=tone;
			}
		}
		
		public class LabelAd extends AdBase{

			
		
			public class Sytle{
				public String fontName=null; //--字体名称
				public int fontSize=0;       //--字体大小
				public String color=null;    //--前景色
				public String bgcolor=null;  //--背景色
				public String fly=null;      //--滚动方向
				public int scroll=0;         //--卷屛速度
				public int suspend=0;        

			}
			
			public Sytle sytle=new Sytle();
		}
		
		public class AppcodeAd extends AdBase{
			public int rescode=-1;
			
		}
		
		
		public ImageAd mImageAd=null;
		public VideoAd mVideoAd=null;
		public LabelAd mLabelAd=null;
		public AppcodeAd mAppcodeAd=null;
		
		public AdBase addImageAd(){
			mImageAd=new ImageAd();
			return mImageAd;
		}
		
		public AdBase addVideoAd(){
			mVideoAd=new VideoAd();
			return mVideoAd;
		}
		
		public AdBase addLabelAd(){
			mLabelAd=new LabelAd();
			return mLabelAd;
		}

		public AdBase addAppcodeAd(){
			mAppcodeAd=new AppcodeAd();
			return mAppcodeAd;
		}
	}
	

	
	public ArrayList<PlayList> mPlayList=new ArrayList<PlayList>(); 
	
	public ArrayList<PlayList> mExtPlayList=new ArrayList<PlayList>();
	
	public PlayList addPlayList(String orderno,String datebegin,String dateend){
		if(orderno==null&&datebegin==null&&dateend==null){
			return null;
		}
		PlayList playList=new PlayList(orderno, datebegin, dateend);
		mPlayList.add(playList);
		return playList;
	}
	
	public PlayList addExtPlayList(String orderno,String datebegin,String dateend){
		if(orderno==null&&datebegin==null&&dateend==null){
			return null;
		}
		PlayList extPlayList=new PlayList(orderno, datebegin, dateend);
		mExtPlayList.add(extPlayList);
		return extPlayList;
	}
}
