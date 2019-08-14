package com.smart.videochat;

import net.iwebrtc.audioprocess.sdk.AudioProcess;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioDecoder {

	static final int frequency = 16000;
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private AudioTrack audioTrack=null;
	private Context mContext=null;
	private AudioProcess mAudioProcess=null;
	public AudioDecoder(Context context,AudioProcess audioProcess) {
		mContext=context;
		mAudioProcess=audioProcess;
	}
	
	public void Init(){
		int playBufSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufSize, AudioTrack.MODE_STREAM);
	}
	
	public void OnDecode(byte[] data,int offset,int length){
		audioTrack.write(data, offset, length);
		byte[] process=new byte[length];
		System.arraycopy(data, offset, process, 0, length);
		audioTrack.write(process, 0, length);
		mAudioProcess.AnalyzeReverseStream10msData(process, length);
	}
}
