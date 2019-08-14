package com.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import android.annotation.SuppressLint;
import android.util.Xml;

public class XmlParse {

	private String xmlFilePath=null;
	
	public interface ParseXmlHandler{
		
		public abstract void doStartDocumentHandler();
		public abstract void doStartTagHandler(XmlPullParser parser);
		public abstract void doEndTagHandler(XmlPullParser parser);
		
	}
	
	public ParseXmlHandler parseXmlHandler=null;
	

	
	public void setParseOpt(String filePath,ParseXmlHandler handler){
		this.xmlFilePath=filePath;
		this.parseXmlHandler=handler;
	}
	
	
	public static String getAttributeString(XmlPullParser parser,String keyName) {
		if(parser.getAttributeValue(null, keyName) == null) 
			return null;
		else
			return parser.getAttributeValue(null, keyName);
	}
	

	@SuppressLint("NewApi")
	public static int getAttributeInt(XmlPullParser parser,String keyName) {
		String    str = parser.getAttributeValue(null, keyName);
		if(str == null) 
			return -1;
		else {
			if(str.isEmpty()) return -1;
			return Integer.valueOf(str);
		}
	}
	
	
	public boolean parse(){
		
		File    fp = new File(xmlFilePath);
		if(!fp.exists()) return false;
		XmlPullParser parser = Xml.newPullParser();
		InputStream inStream;

		
		try {
			inStream = new FileInputStream(xmlFilePath);
			
			try {
				parser.setInput(inStream, "UTF-8");
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				int eventType = parser.getEventType();
				
				while (eventType != XmlPullParser.END_DOCUMENT) {
					
					switch (eventType) {
						case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
							
							if(parseXmlHandler!=null){
								
								parseXmlHandler.doStartDocumentHandler();
							}
							
							break;
						case XmlPullParser.START_TAG://开始元素事件
							
							if(parseXmlHandler!=null){
								
								parseXmlHandler.doStartTagHandler(parser);
							}
							
							break;
						case XmlPullParser.END_TAG://结束元素事件
							
							if(parseXmlHandler!=null){
								
								parseXmlHandler.doEndTagHandler(parser);
							}
							
							break;
					}
					
					try {
						eventType = parser.next();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					inStream.close();
					
					return true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return false;
	}
	
}

