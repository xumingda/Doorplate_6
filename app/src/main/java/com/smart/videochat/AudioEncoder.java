package com.smart.videochat;

import net.iwebrtc.audioprocess.sdk.AudioProcess;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioEncoder {

	static final int frequency = 44100;
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord audioRecord;
	private Context mContext=null;
	private AudioProcess mAudioProcess=null;
	private AudioRecordTask mAudioRecordTask=null;
	public interface AudioEncoderCallack{
		public abstract void OnEncode(byte[] data,int length);
	}
	AudioEncoderCallack mCallback=null;
	public AudioEncoder(Context context,AudioProcess audioProcess) {
		mContext=context;
		mAudioProcess=audioProcess;
	}
	public void setCallback(AudioEncoderCallack callback){
		mCallback=callback;
	}
	public void Init(){
		int recBufSize=0;
		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		Log.i("", "recBufSize:" + recBufSize);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recBufSize);
		Log.i("", "audioRecord:" + audioRecord);
	}
	
	public void Start(){
		mAudioRecordTask=new AudioRecordTask();
		mAudioRecordTask.start();
	}
	
	public void Stop(){
		mAudioRecordTask.interrupt();
		mAudioRecordTask=null;
	}
	
	private class AudioRecordTask extends Thread{
		@Override
		public void run() {
			super.run();
			int bufferSize = mAudioProcess.calculateBufferSize(frequency, 2, 1);
			byte[] buffer = new byte[bufferSize];
			audioRecord.startRecording();//开始录制
			while(!interrupted()){
				
				//setp 1 从MIC保存数据到缓冲区
				int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
				if(bufferReadResult<=0){
					continue;
				}
				byte[] tmpBuf_src = new byte[bufferReadResult];
				System.arraycopy(buffer, 0, tmpBuf_src, 0, bufferReadResult);

				//setp 2 进行处理
				byte[] tmpBuf_processed = new byte[bufferReadResult];
				mAudioProcess.processStream10msData(tmpBuf_src, tmpBuf_src.length, tmpBuf_processed);
				
				if(mCallback!=null){
					mCallback.OnEncode(tmpBuf_processed, tmpBuf_processed.length);
				}
				
			}
		}
	}
	
}
