package com.advertisement.resource;

import java.util.ArrayList;

public class ResPlaybill {
	
	public class PlayList{
		public String orderno=null;   //--������
		public String datebegin=null; //--��ʼ���� 19700101
		public String dateend=null;   //--��������20160101
		
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
			public String url=null;   //--ͼƬ/��Ƶ ��url����·����label��ʱ�򱣴���������
			public String name=null;  //--����
			public int pri=0;         //--���ȼ�
			public int max=0;         //--��󲥷Ŵ�����65535��ʾ���޴���
			public int length=0;      //--����ʱ�䳤�ȣ�����Ϊ��λ,0-65535��
			public class Periods{
				public int loop=0;    //--ѭ������
				public String time=null; //--������ʼ����ʱ�� 00001200 00��00��������12��00�ֽ���
				public int max=0;     //--������󲥷Ŵ���������ʱ��εĲ��Ŵ��������趨ֵʱ����ʱ��β��ٲ��Ŵ˹��
				
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

			public int volume=0; //--����
			public String tone=null;//--����
			
			public void setOpt(int volume,String tone){;
				this.volume=volume;
				this.tone=tone;
			}
		}
		
		public class LabelAd extends AdBase{

			
		
			public class Sytle{
				public String fontName=null; //--��������
				public int fontSize=0;       //--�����С
				public String color=null;    //--ǰ��ɫ
				public String bgcolor=null;  //--����ɫ
				public String fly=null;      //--��������
				public int scroll=0;         //--����ٶ�
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
