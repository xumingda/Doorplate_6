package com.camera;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class FileOperate {
	
	static String TAG="FileOperate";
	
	public static synchronized File open(String path){
		
		File file=new File(path);
		
		return file;
	}
	
	public static synchronized long getSize(File file){
		
		if(file==null)
		{
			return 0;
		}
		
		return file.length();
	}
	
	public static synchronized boolean write(File file,byte[] data) throws FileNotFoundException{
		
		if(file==null)
		{
			Log.i(TAG,"file = null");
			return false ;
		}
		

		
		BufferedOutputStream stream = null;
		
		FileOutputStream fstream = new FileOutputStream(file); 
		
		
        stream = new BufferedOutputStream(fstream); 
        try {
			stream.write(data);
			
			Log.i(TAG,"write success!");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
		finally{
			
			if(stream!=null)
			{
				try {
					stream.close();
					fstream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					return false;
				}
			}
		}
        
        return true;
	}
	
	public static synchronized boolean read(File file,byte[] data){
		
		if(file==null)
		{
			return false;
		}
		
		if(file.canRead()==false)
		{
			return false;
		}
		
		BufferedInputStream stream = null;
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
        stream = new BufferedInputStream(fstream); 
        try {
			stream.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
		finally{
			
			if(stream!=null)
			{
				try {
					stream.close();
					fstream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		}
        
        return true;
		
	}
}
