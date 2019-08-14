package com.media;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Parcel;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class VideoPlayer  implements SurfaceHolder.Callback{

	
	public MediaPlayer mediaPlayer=null;
	
	private SurfaceHolder surfaceHolder;
	
	
	private String dataUrl;
	
	private String TAG="Player";
	
	private boolean isNet=false;              /* 是否网络视频 */
			
	private boolean isAutoStart=false;        /* 是否自动启动播放 */
	
	private int audioVolume=0;                /* 音量大小 */
	private int backAudioVolume=0;

	public interface SurfaceViewHandler{
		
		public abstract void doDestroy();
	}
	
	private SurfaceViewHandler surfaceHandler=null;
	
	public interface VideoPlayerListener{
		public abstract void onPlayStart();//--播放器启动完成
		public abstract void onPlayComplete();//--播放完成一个文件
		public abstract void onError();   //--错误
		public abstract void onSurfaceViewDestroy(); //--surfaceview被清空
	}
	
	private VideoPlayerListener mListener=null; 

	public VideoPlayer(SurfaceView surfaceview,boolean isAutoStart,
			boolean isNetRes,VideoPlayerListener listener){
		
		DBG("=== VedioPlayer ===");
		
		DBG("surfaceView:"+surfaceview);
		
		surfaceHolder=surfaceview.getHolder();
		
		surfaceHolder.addCallback(this);
		
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		this.isAutoStart=isAutoStart;
		
		this.isNet=isNetRes;
	
		this.mListener=listener;
	}


	
	
	
	public void DBG(String content){
		
		Log.i(TAG,content);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
		DBG("== surfaceCreated ==");
		
		if(isAutoStart==false){
			
			return ;
		}

		if(mListener!=null){
			mListener.onPlayStart();
		}
		
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		DBG("== surfaceChanged ==");
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		DBG("== surfaceDestroyed ==");
		
		release();
		
		if(surfaceHandler!=null){
			
			surfaceHandler.doDestroy();
		}
		
		if(mListener!=null){
			mListener.onSurfaceViewDestroy();
		}
	}
	
	public void setSurfaceViewHandler(SurfaceViewHandler handler){
		
		surfaceHandler=handler;
	}
	
	public void release(){
		if(mediaPlayer!=null){
			
			mediaPlayer.release();
			mediaPlayer=null;
		}
		
	}
	
	public void pause(){
		
		if(mediaPlayer!=null){
			
			if(mediaPlayer.isPlaying()){
				
				mediaPlayer.pause();
				
			}
		}
	}
	
	public void resume(){
		if(mediaPlayer!=null){
			
			mediaPlayer.start();
		}
	}
	
	public void stop(){
		
		if(mediaPlayer!=null){
			
			mediaPlayer.stop();
		}
	}
	
	public void start(String path){
		
		play(path,isNet);
		
	}
	
	public boolean isPlaying(){
		if(mediaPlayer==null){
			return false;
		}
		
		return mediaPlayer.isPlaying();
	}
	
	public void prepare(){
		
		if(mediaPlayer!=null){
			
			mediaPlayer.prepareAsync();
		}
	}
	

	public void setAudioMute(boolean mode){
		
		if(mode){
			
			backAudioVolume=audioVolume;
			audioVolume=0;
		}else{
			audioVolume=backAudioVolume;
		}
		if(mediaPlayer!=null){
			
			mediaPlayer.setVolume(audioVolume, audioVolume);
		}
	}
	
	public void setAudioVolume(int volume){
		
		audioVolume=volume;
		backAudioVolume=volume;
		if(mediaPlayer!=null){
			
			mediaPlayer.setVolume(audioVolume, audioVolume);
		}
	}
	
	public void play(String path,boolean mode){
		
		DBG("== play==");
		
		dataUrl=path;
		isNet=mode;
		
		DBG("dataUrl="+dataUrl);
		
		if(mediaPlayer==null){
			
			mediaPlayer = new MediaPlayer();
		}
		

		mediaPlayer.reset();
		
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				DBG("== onCompletion ==");
				
				mediaPlayer.release();
				mediaPlayer=null;
				if(mListener!=null){
					mListener.onPlayComplete();
				}
				
			}
		});
		
		mediaPlayer.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
				Log.i(TAG,"onInfo="+arg1);
				switch(arg1){
				
				/*it can't decode frames fast enough. Possibly only the audio plays fine at this stage.*/
				/* 无法解码当前帧，只能播放音频在本阶段 */
				case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
					
					Log.i(TAG,"MEDIA_INFO_VIDEO_TRACK_LAGGING");
					
					break;
				
				case MediaPlayer.MEDIA_INFO_BUFFERING_START: /*暂停状态*/
					
					Log.i(TAG,"MEDIA_INFO_BUFFERING_START");
					break;
					
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:   /* 播放状态 */
					
					Log.i(TAG,"MEDIA_INFO_BUFFERING_END");
					
					break;
				
				case MediaPlayer.MEDIA_INFO_METADATA_UPDATE: /* 数组数据更新 */
					Log.i(TAG,"MEDIA_INFO_METADATA_UPDATE");
					break;
			
				case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
					Log.i(TAG,"MEDIA_INFO_BAD_INTERLEAVING");
					break;
				
				/*The player just pushed the very first video frame for rendering.*/
				/* 渲染第一帧 */
				case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
					Log.i(TAG,"MEDIA_INFO_BAD_INTERLEAVING");
					break;
				
				}

				return false;
			}
		});
		
		
		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				Log.e(TAG,"onError="+arg1);
				switch(arg1){
				
				case MediaPlayer.MEDIA_ERROR_IO:
					Log.e(TAG,"IO ERROR");
					break;
				case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
					Log.e(TAG,"PLAY ERROR");
					break;
				case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
					Log.e(TAG,"UNSUPPORTED ERROR");
					break;
				case MediaPlayer.MEDIA_ERROR_UNKNOWN:
					Log.e(TAG,"UNKNOWN ERROR");
					break;
				}
				//--故障之后，提示上层表示播放完成
				if(mListener!=null){
					mListener.onPlayComplete();
				}
				return false;
			}
		});
		
		
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				DBG("== onPrepared ==");
				
				mediaPlayer.start();  
				/*
				Parcel data=Parcel.obtain();
				
				data.writeInt(8);
				
				try {
					
					Class[] argClasses = new Class[2];
					argClasses[0] = int.class;
					argClasses[1] = Parcel.class;
					Method method = mediaPlayer.getClass().getMethod("setParameter", argClasses);
					
					try {
						method.invoke(mediaPlayer, 1255,data);
						
						
						
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
			
		});
		
		try {
			mediaPlayer.setDataSource(dataUrl);	
			
			mediaPlayer.setDisplay(surfaceHolder);
			
			if(isNet){
				mediaPlayer.prepareAsync();
			}else{
				mediaPlayer.prepare();
			}
			  
			
			 
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
}

