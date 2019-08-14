package com.media;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

public class AudioPlayer implements OnPreparedListener,OnCompletionListener{

	private MediaPlayer mediaPlayer=null;
	private Context mContext=null;
	private int maxVolume=0;
	
	
	public void showLog(String content){
		
		Log.i("AudioPlayer",content);
	}
	
	
	public AudioPlayer(Context context){
		
		mContext=context;
		
		AudioManager audioManager=(AudioManager)mContext.getSystemService(Service.AUDIO_SERVICE);
		maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
	}
	
	public void play(String path){
		
		if(mediaPlayer!=null){
			
			if(mediaPlayer.isPlaying()){
				
				mediaPlayer.stop();
			}
			
			mediaPlayer.release();
			
			mediaPlayer=null;
		}
		
		mediaPlayer=new MediaPlayer();
		
		mediaPlayer.reset();
		
		/* 防止与视频播放音冲突,设置为电话玲声 */
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		//mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		//mediaPlayer.setVolume(maxVolume, maxVolume);
		
		mediaPlayer.setOnCompletionListener(this);
		
		mediaPlayer.setOnPreparedListener(this);
		
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
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
	
	public void stop(){
		
		if(mediaPlayer!=null){
			
			if(mediaPlayer.isPlaying()){
				
				mediaPlayer.stop();
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		showLog("==onCompletion==");
		
		if(mediaPlayer!=null){
			
			mediaPlayer.release();
			mediaPlayer=null;
		}
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		showLog("==onPrepared==");
		mediaPlayer.start();
	}
}
