package com.smart.videochat;

import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

public class VideoDecoder {

	private MediaCodec mediaCodec = null;
	private MediaFormat mediaFormat = null;
	private static final String TAG = "VideoDecoder";
	private static final int FRAME_RATE = 15;
	private int mCount = 0;

	@SuppressLint("NewApi")
	public VideoDecoder(Surface surface, int width, int height) {
		mediaCodec = MediaCodec.createDecoderByType("video/avc");
		if (mediaCodec == null) {
			Log.e(TAG, "MediaCodec.createEncoderByType H.264 fail!");
			return;
		}
		mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);// --H264·Ö±æÂÊ
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
				MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
		mediaCodec.configure(mediaFormat, surface, null, 0);
		mediaCodec.start();
	}

	@SuppressLint("NewApi")
	public void pullFrame(byte[] intput, int offset, int length) {
		ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
		int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			inputBuffer.put(intput, offset, length);
			mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount
					* 1000000 / FRAME_RATE, 0);
			mCount++;
		}
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		while (outputBufferIndex >= 0) {
			mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
			outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		}
	}
}
