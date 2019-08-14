package com.advertisement.resource;

import java.util.ArrayList;

import com.advertisement.system.SystemManager;

public class ResThemeSwitch {

	public class ThemePolicy {
		public String id=null;    //--序号
		public String name=null;  //--播放主题名称
		public String period=null;//--播放的时间段，所有播放以该时间为准
		public String act=null; //--播放次数
		public String url=null; //--主题theme的下载链接
		
		public ThemePolicy(String id,String name,String period,String act,String url){
			this.id=id;
			this.name=name;
			this.period=period;
			this.act=act;
			this.url=url;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "id="+id+" name="+name+" period="+period+" act="+act+" url="+url;
		}
	}
	
	public ArrayList<ThemePolicy> mThemePolicyList=new ArrayList<ThemePolicy>();
	public ArrayList<ThemePolicy> mExtThemePolicyList=new ArrayList<ThemePolicy>();
	
	public ThemePolicy addThemePolicy(String id,String name,String period,String act,String url){
		ThemePolicy themePolicy=new ThemePolicy(id, name, period, act, url);
		mThemePolicyList.add(themePolicy);
		SystemManager.LOGI("ResThemeSwitch", "policy->"+themePolicy.toString());
		return themePolicy;
	} 
	
	public ThemePolicy addExtThemePolicy(String id,String name,String period,String act,String url){
		ThemePolicy themePolicy=new ThemePolicy(id, name, period, act, url);
		mExtThemePolicyList.add(themePolicy);
		SystemManager.LOGI("ResThemeSwitch", "ext->"+themePolicy.toString());
		return themePolicy;
	} 
	
	
}
