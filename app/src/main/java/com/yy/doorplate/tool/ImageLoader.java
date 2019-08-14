package com.yy.doorplate.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

public class ImageLoader {

	private final String TAG = "ImageLoader";

	private Context context;

	private LruCache<String, Bitmap> mLruCache;

	private ExecutorService mImageThreadPool = null;

	public ImageLoader() {
	}

	public ImageLoader(Context context) {
		this.context = context;

		// 获取我们应用的最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 4;
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
			}
		};
	}

	/**
	 * 添加Bitmap到内存缓存
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null
				&& !TextUtils.isEmpty(key)) {
			mLruCache.put(key, bitmap);
		}
	}

	/**
	 * 从内存缓存中获取一个Bitmap
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {
		return mLruCache.get(key);
	}

	/**
	 * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
	 * 
	 * @return
	 */
	public ExecutorService getThreadPool() {
		if (mImageThreadPool == null) {
			synchronized (ExecutorService.class) {
				if (mImageThreadPool == null) {
					// 为了下载图片更加的流畅，我们用了2个线程来下载图片
					mImageThreadPool = Executors.newFixedThreadPool(2);
				}
			}
		}
		return mImageThreadPool;
	}

	public void getBitmapFormUrl(final String path,
			final OnImageLoaderListener listener, final int width,
			final int height) {
		if (TextUtils.isEmpty(path) || listener == null) {
			return;
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				listener.onImageLoader((Bitmap) msg.obj, path);
			}
		};
		if (getBitmapFromMemCache(path) != null) {
			Message msg = handler.obtainMessage();
			msg.obj = getBitmapFromMemCache(path);
			handler.sendMessage(msg);
			return;
		}
		File file = new File(path);
		if (!file.exists()) {
			Message msg = handler.obtainMessage();
			msg.obj = null;
			handler.sendMessage(msg);
			return;
		}
		getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = decodeSampledBitmapFromPath(path, width, height);
				Message msg = handler.obtainMessage();
				msg.obj = bitmap;
				handler.sendMessage(msg);
				addBitmapToMemoryCache(path, bitmap);
			}
		});
	}

	public void getBitmapFormUrl(final String path,
			final OnImageLoaderListener listener) {
		if (TextUtils.isEmpty(path) || listener == null) {
			return;
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				listener.onImageLoader((Bitmap) msg.obj, path);
			}
		};
		if (getBitmapFromMemCache(path) != null) {
			Message msg = handler.obtainMessage();
			msg.obj = getBitmapFromMemCache(path);
			handler.sendMessage(msg);
			return;
		}
		File file = new File(path);
		if (!file.exists()) {
			Message msg = handler.obtainMessage();
			msg.obj = null;
			handler.sendMessage(msg);
			return;
		}
		getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeFile(path, options);
				Message msg = handler.obtainMessage();
				msg.obj = bitmap;
				handler.sendMessage(msg);
				addBitmapToMemoryCache(path, bitmap);
			}
		});
	}

	public void getBitmapFormRes(final int res,
			final OnImageLoaderListener listener) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				listener.onImageLoader((Bitmap) msg.obj, res + "");
			}
		};
		if (getBitmapFromMemCache(res + "") != null) {
			Bitmap bitmap = getBitmapFromMemCache(res + "");
			Message msg = handler.obtainMessage();
			msg.obj = bitmap;
			handler.sendMessage(msg);
			return;
		}
		getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = decodeSampledBitmapFromRes(res);
				Message msg = handler.obtainMessage();
				msg.obj = bitmap;
				handler.sendMessage(msg);
				addBitmapToMemoryCache(res + "", bitmap);
			}
		});

	}

	/**
	 * 异步下载图片的回调接口
	 * 
	 * @author len
	 * 
	 */
	public interface OnImageLoaderListener {
		void onImageLoader(Bitmap bitmap, String url);
	}

	/**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
		// 获得图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = caculateInSampleSize(options, width, height);

		// 使用获得到的InSampleSize再次解析图片
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = null;
		try {
			InputStream is = new FileInputStream(path);
			bitmap = BitmapFactory.decodeStream(is, null, options);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public Bitmap decodeSampledBitmapFromRes(int resource) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resource);
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(is, null, opt);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bm;
	}

	/**
	 * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
	 * 
	 * @param options
	 * @param width
	 * @param height
	 * @return
	 */
	public int caculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;

		int inSampleSize = 1;

		if (width > reqWidth || height > reqHeight) {
			int widthRadio = Math.round(width * 1.0f / reqWidth);
			int heightRadio = Math.round(height * 1.0f / reqHeight);

			inSampleSize = Math.max(widthRadio, heightRadio);
		}

		return inSampleSize;
	}

	/**
	 * 取消正在下载的任务
	 */
	public synchronized void cancelTask() {
		if (mImageThreadPool != null) {
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}

	public void clearCache() {
		if (mLruCache != null && mLruCache.size() > 0) {
			mLruCache.evictAll();
		}
	}

}
