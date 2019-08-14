package com.yy.doorplate.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher.ViewFactory;

import com.advertisement.activity.DisplayManager;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class SchoolBannerView extends AppCodeView {

	private ImageLoader imageLoader;
	private String activity;

	private RelativeLayout ly_schoolnew_banner;
	private SurfaceView sv_schoolnew;
	private ImageSwitcher img_schoolnew_banner;

	private SurfaceHolder surfaceHolder = null;
	private PlayTaskModel region_media_video = null;
	private List<PlayTaskModel> region_media_img = null;
	private int region_media_img_i = 0;

	private DisplayManager displayManager_media = null;

	public SchoolBannerView(Context context, MyApplication application,
			ImageLoader imageLoader, String activity, String nextPath) {
		super(context, application, nextPath);
		this.imageLoader = imageLoader;
		this.activity = activity;
		initView();
		updata_banner();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.ly_school_banner, this);
		ly_schoolnew_banner = (RelativeLayout) findViewById(R.id.ly_schoolnew_banner);
		sv_schoolnew = (SurfaceView) findViewById(R.id.sv_schoolnew);
		img_schoolnew_banner = (ImageSwitcher) findViewById(R.id.img_schoolnew_banner);

		img_schoolnew_banner.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(context);
				ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
						ImageSwitcher.LayoutParams.MATCH_PARENT,
						ImageSwitcher.LayoutParams.MATCH_PARENT);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(params);
				return imageView;
			}
		});
		img_schoolnew_banner.setInAnimation(AnimationUtils.loadAnimation(
				context, R.anim.enteralpha));
		img_schoolnew_banner.setOutAnimation(AnimationUtils.loadAnimation(
				context, R.anim.exitalpha));

		surfaceHolder = sv_schoolnew.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.setKeepScreenOn(true);
		surfaceHolder.addCallback(new surFaceView());
	}

	private void updata_banner() {
		// if (!application.getRunningActivityName().equals(activity)) {
		// return;
		// }
		region_media_video = null;
		region_media_img = new ArrayList<PlayTaskModel>();
		region_media_img_i = 0;
		handler.removeMessages(3);
		handler.removeMessages(7);
		img_schoolnew_banner.setVisibility(View.VISIBLE);
		sv_schoolnew.setVisibility(View.INVISIBLE);
		application.regionIsPlaying = false;
		if (application.mediaPlayer_region != null) {
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
		} else {
			application.mediaPlayer_region = new MediaPlayer();
		}

		if (application.playTaskModels != null
				&& application.playTaskModels.size() > 0) {
			for (PlayTaskModel model : application.playTaskModels) {
				if (model.playType.equals("REGION_MEDIA")
						&& model.position.equals("02")
						&& model.srcType.equals("TEMPLATE")) {
					String policyPath = application.getFtpPath(model.srcPath);
					File file = new File(policyPath);
					if (file.exists()) {
						ly_schoolnew_banner.removeAllViews();
						if (displayManager_media == null) {
							displayManager_media = new DisplayManager(context,
									application, ly_schoolnew_banner);
							displayManager_media.start(policyPath);
						} else {
							displayManager_media.stop();
							displayManager_media.destroy();
							displayManager_media.start(policyPath);
						}
						return;
					}
				}
			}
			for (PlayTaskModel model : application.playTaskModels) {
				if (model.playType.equals("REGION_MEDIA")
						&& model.position.equals("02")) {
					if (model.srcType.equals("VIDEO")) {
						region_media_video = model;
						break;
					} else if (model.srcType.equals("IMAGE")) {
						region_media_img.add(model);
					}
				}
			}
		}
		if (region_media_video != null
				&& !TextUtils.isEmpty(region_media_video.srcPath)) {
			String[] s = region_media_video.srcPath.split("/");
			File file = new File(MyApplication.PATH_ROOT
					+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1]);
			if (file.exists()) {
				img_schoolnew_banner.setVisibility(View.INVISIBLE);
				sv_schoolnew.setVisibility(View.VISIBLE);
				return;
			}
		}
		if (region_media_img.size() > 0) {
			play_img();
		} else {
			updata_banner_default();
		}
	}

	private void play_img() {
		img_schoolnew_banner.setVisibility(View.VISIBLE);
		sv_schoolnew.setVisibility(View.INVISIBLE);
		if (region_media_img.size() == 1) {
			if (!TextUtils.isEmpty(region_media_img.get(0).srcPath)) {
				String[] s = region_media_img.get(0).srcPath.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null)
										img_schoolnew_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
								}
							}, 1210, 477);
				} else {
					updata_banner_default();
				}
			} else {
				updata_banner_default();
			}
		} else if (region_media_img.size() > 1) {
			boolean b = false;
			for (PlayTaskModel model : region_media_img) {
				if (!TextUtils.isEmpty(model.srcPath)) {
					String[] s = model.srcPath.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PLAYTASK + "/"
							+ s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						b = true;
						break;
					}
				}
			}
			if (b) {
				handler.sendEmptyMessage(3);
			} else {
				updata_banner_default();
			}
		}
	}

	private void updata_banner_default() {
		img_schoolnew_banner.setVisibility(View.VISIBLE);
		sv_schoolnew.setVisibility(View.INVISIBLE);
		imageLoader.getBitmapFormRes(R.drawable.ly_schoolnew_banner,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						img_schoolnew_banner
								.setImageDrawable(new BitmapDrawable(bitmap));
					}
				});
	}

	private void mediaPlayer_region_play() {
		if (!application.getRunningActivityName().equals(activity)
				|| region_media_video == null) {
			return;
		}
		try {
			String[] s = region_media_video.srcPath.split("/");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PLAYTASK
					+ "/" + s[s.length - 1];
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
			application.mediaPlayer_region
					.setAudioStreamType(AudioManager.STREAM_MUSIC);
			application.mediaPlayer_region.setDataSource(path);
			application.mediaPlayer_region.setDisplay(surfaceHolder);
			application.mediaPlayer_region
					.setOnErrorListener(new OnErrorListener() {

						@Override
						public boolean onError(MediaPlayer arg0, int arg1,
								int arg2) {
							updata_banner_default();
							return false;
						}
					});
			application.mediaPlayer_region
					.setOnPreparedListener(new OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer mediaPlayer) {
							application.regionIsPlaying = true;
							if (!application.isGuangbo) {
								mediaPlayer.start();
							}
						}
					});
			application.mediaPlayer_region
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							mediaPlayer_region_play();
						}
					});
			application.mediaPlayer_region.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
			updata_banner_default();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 3: {
				if (!TextUtils
						.isEmpty(region_media_img.get(region_media_img_i).srcPath)) {
					String[] s = region_media_img.get(region_media_img_i).srcPath
							.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PLAYTASK + "/"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null)
										img_schoolnew_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
									if (region_media_img_i == region_media_img
											.size() - 1) {
										region_media_img_i = 0;
									} else {
										region_media_img_i++;
									}
									handler.sendEmptyMessageDelayed(3, 5 * 1000);
								}
							}, 1210, 477);
				} else {
					if (region_media_img_i == region_media_img.size() - 1) {
						region_media_img_i = 0;
					} else {
						region_media_img_i++;
					}
					handler.sendEmptyMessageDelayed(3, 1000);
				}
				break;
			}
			case 7:
				mediaPlayer_region_play();
				break;
			}
		}
	};

	private class surFaceView implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			handler.sendEmptyMessageDelayed(7, 3000);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			handler.removeMessages(7);
			application.regionIsPlaying = false;
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_REGION_MEDIA || type == APPCODE_UPDATA_EQU) {
			updata_banner();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		handler.removeMessages(3);
		handler.removeMessages(7);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		updata_banner();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		handler.removeMessages(3);
		handler.removeMessages(7);
		if (displayManager_media != null) {
			displayManager_media.stop();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		handler.removeMessages(3);
		handler.removeMessages(7);
		if (displayManager_media != null) {
			displayManager_media.stop();
			displayManager_media.destroy();
		}
	}

}
