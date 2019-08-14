package com.advertisement.resource;

import java.util.ArrayList;

import com.advertisement.system.SystemManager;

public class ResThemeSwitch {

	public class ThemePolicy {
		public String id=null;    //--���
		public String name=null;  //--������������
		public String period=null;//--���ŵ�ʱ��Σ����в����Ը�ʱ��Ϊ׼
		public String act=null; //--���Ŵ���
		public String url=null; //--����theme����������
		
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
