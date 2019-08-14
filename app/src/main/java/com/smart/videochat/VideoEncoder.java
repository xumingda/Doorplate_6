package com.smart.videochat;

import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

public class VideoEncoder {
	private MediaCodec mediaCodec = null;
	private MediaFormat mediaFormat = null;
	private static final String TAG = "VideoEncoder";
	// private MediaCodec.BufferInfo bufferInfo = null;
	private byte[] outBuf = null;

	public interface VideoEncoderCallback {
		public abstract void onVideoEncoderFrame(byte[] data, int offset,
				int length);
	}

	private VideoEncoderCallback videoEncoderCallback = null;
	private int colorFormat = 0;

	@SuppressLint("NewApi")
	public VideoEncoder(int width, int height) {
		mediaCodec = MediaCodec.createEncoderByType("video/avc");
		if (mediaCodec == null) {
			Log.e(TAG, "MediaCodec.createEncoderByType H.264 fail!");
			return;
		}
		mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);// --H264分辨率
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);// --位率
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15); // --帧率
		// --设置编码源为YUV420格式
		// mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
		// MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
		int colorFormat = selectColorFormat(selectCodec("video/avc"),
				"video/avc");
		Log.i(TAG, "colorFormat:" + colorFormat);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
		// --关键帧间隔时间 1s
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
		// --设置当前为编码器
		mediaCodec.configure(mediaFormat, null, null,
				MediaCodec.CONFIGURE_FLAG_ENCODE);

		mediaCodec.start();

		outBuf = new byte[(width * height * 3) / 2];
	}

	public int getColorFormat() {
		return colorFormat;
	}

	/**
	 * Returns the first codec capable of encoding the specified MIME type, or
	 * null if no match was found.
	 */
	@SuppressLint("NewApi")
	private static MediaCodecInfo selectCodec(String mimeType) {
		int numCodecs = MediaCodecList.getCodecCount();
		for (int i = 0; i < numCodecs; i++) {
			MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

			if (!codecInfo.isEncoder()) {
				continue;
			}

			String[] types = codecInfo.getSupportedTypes();
			for (int j = 0; j < types.length; j++) {
				if (types[j].equalsIgnoreCase(mimeType)) {
					return codecInfo;
				}
			}
		}
		return null;
	}

	/**
	 * Returns a color format that is supported by the codec and by this test
	 * code. If no match is found, this throws a test failure -- the set of
	 * formats known to the test should be expanded for new platforms.
	 */
	@SuppressLint("NewApi")
	private static int selectColorFormat(MediaCodecInfo codecInfo,
			String mimeType) {
		MediaCodecInfo.CodecCapabilities capabilities = codecInfo
				.getCapabilitiesForType(mimeType);
		for (int i = 0; i < capabilities.colorFormats.length; i++) {
			int colorFormat = capabilities.colorFormats[i];
			if (isRecognizedFormat(colorFormat)) {
				return colorFormat;
			}
		}
		Log.e(TAG,
				"couldn't find a good color format for " + codecInfo.getName()
						+ " / " + mimeType);
		return 0; // not reached
	}

	/**
	 * Returns true if this is a color format that this test code understands
	 * (i.e. we know how to read and generate frames in this format).
	 */
	private static boolean isRecognizedFormat(int colorFormat) {
		switch (colorFormat) {
		// these are the formats we know how to handle for this test
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
		case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
		case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
			return true;
		default:
			return false;
		}
	}

	public void start() {
		if (mediaCodec != null) {
			// mediaCodec.start();
		}
	}

	public void stop() {
		if (mediaCodec != null) {
			mediaCodec.stop();
		}
	}

	public void setVideoEncoderCallback(VideoEncoderCallback callback) {
		videoEncoderCallback = callback;
	}

	private byte[] mMediaHead = null;

	@SuppressLint("NewApi")
	public int encode(byte[] input, byte[] output) {
		int pos = 0;
		ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
		ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
		int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			inputBuffer.put(input);
			mediaCodec
					.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);
		}

		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		while (outputBufferIndex >= 0) {
			ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];

			byte[] data = new byte[bufferInfo.size];
			outputBuffer.get(data);

			Log.d(TAG, "offerEncoder InputBufSize:" + outputBuffer.capacity()
					+ " outputSize:" + data.length + " bytes written");

			if (mMediaHead != null) {
				System.arraycopy(data, 0, output, pos, data.length);
				pos += data.length;
			} else // 保存pps sps 只有开始时 第一个帧里有， 保存起来后面用
			{
				Log.d(TAG, "offer Encoder save sps head,len:" + data.length);
				ByteBuffer spsPpsBuffer = ByteBuffer.wrap(data);
				if (spsPpsBuffer.getInt() == 0x00000001) {
					mMediaHead = new byte[data.length];
					System.arraycopy(data, 0, mMediaHead, 0, data.length);
				} else {
					Log.e(TAG, "not found media head.");
					return -1;
				}
			}

			mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
			outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

		}

		if (output[4] == 0x65) // key frame 编码器生成关键帧时只有 00 00 00 01 65 没有pps
								// sps， 要加上
		{
			System.arraycopy(output, 0, input, 0, pos);
			System.arraycopy(mMediaHead, 0, output, 0, mMediaHead.length);
			System.arraycopy(input, 0, output, mMediaHead.length, pos);
			pos += mMediaHead.length;
		}
		return pos;
	}

	@SuppressLint("NewApi")
	public void pushFrame(byte[] data, int offset, int length) {
		ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
		ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
		int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			inputBuffer.put(data, offset, length);
			mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, 0, 0);
		}

		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

		int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

		// byte[] outBuf=new byte[bufferInfo.size+3];

		while (outputBufferIndex >= 0) {
			ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
			if (videoEncoderCallback != null) {
				outputBuffer.get(outBuf, 3, bufferInfo.size);
				// --H264起始帧可以滤掉
				if ((outBuf[3] == 0 && outBuf[4] == 0 && outBuf[5] == 1)
						|| (outBuf[3] == 0 && outBuf[4] == 0 && outBuf[5] == 0 && outBuf[6] == 1)) {
					// Log.i(TAG, "start frame");
					videoEncoderCallback.onVideoEncoderFrame(outBuf, 3,
							bufferInfo.size);
				} else {
					outBuf[0] = 0;
					outBuf[1] = 0;
					outBuf[2] = 1;
					videoEncoderCallback.onVideoEncoderFrame(outBuf, 0,
							bufferInfo.size + 3);
				}

			}
			mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
			outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
		}
	}
}
