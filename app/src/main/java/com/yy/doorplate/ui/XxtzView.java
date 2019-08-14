package com.yy.doorplate.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.advertisement.activity.DisplayManager;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.NoticeDetailsActivity;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class XxtzView extends AppCodeView implements OnClickListener {

	private ImageLoader imageLoader;
	private String activity;

	private RelativeLayout ly_main_new1, ly_main_new2, ly_banner;
	private TextView txt_main_nonew, txt_main_new_title1, txt_main_new_title2,
			txt_main_new_nr1, txt_main_new_nr2, txt_main_new_time1,
			txt_main_new_time2;
	private SurfaceView sv_pj_main;
	private ImageView img_main_new1, img_main_new2;
	private ImageSwitcher img_main_banner;

	private SurfaceHolder surfaceHolder = null;
	private PlayTaskModel region_media_video = null;

	private List<NoticeInfoModel> noticeInfoModels = null;

	private String new_rl_1, new_url_1, new_path_1, new_rl_2, new_url_2,
			new_path_2;
	private List<Map<String, String>> new_pics = null;
	private int new_pics_i = 0;
	private List<PlayTaskModel> region_media_img = null;
	private int region_media_img_i = 0;

	private DisplayManager displayManager_media = null;

	public XxtzView(Context context, MyApplication application,
			ImageLoader imageLoader, String activity, String nextPath) {
		super(context, application, nextPath);
		this.imageLoader = imageLoader;
		this.activity = activity;
		initView();
		updata();
	}

	public void initView() {
		View.inflate(context, R.layout.ly_xxtz, this);
		ly_main_new1 = (RelativeLayout) findViewById(R.id.ly_main_new1);
		ly_main_new2 = (RelativeLayout) findViewById(R.id.ly_main_new2);
		ly_banner = (RelativeLayout) findViewById(R.id.ly_banner);
		txt_main_nonew = (TextView) findViewById(R.id.txt_main_nonew);
		txt_main_new_title1 = (TextView) findViewById(R.id.txt_main_new_title1);
		txt_main_new_title2 = (TextView) findViewById(R.id.txt_main_new_title2);
		txt_main_new_nr1 = (TextView) findViewById(R.id.txt_main_new_nr1);
		txt_main_new_nr2 = (TextView) findViewById(R.id.txt_main_new_nr2);
		txt_main_new_time1 = (TextView) findViewById(R.id.txt_main_new_time1);
		txt_main_new_time2 = (TextView) findViewById(R.id.txt_main_new_time2);
		sv_pj_main = (SurfaceView) findViewById(R.id.sv_pj_main);
		img_main_banner = (ImageSwitcher) findViewById(R.id.img_main_banner);
		img_main_new1 = (ImageView) findViewById(R.id.img_main_new1);
		img_main_new2 = (ImageView) findViewById(R.id.img_main_new2);

		img_main_banner.setFactory(new ViewFactory() {
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
		img_main_banner.setInAnimation(AnimationUtils.loadAnimation(context,
				R.anim.enteralpha));
		img_main_banner.setOutAnimation(AnimationUtils.loadAnimation(context,
				R.anim.exitalpha));

		surfaceHolder = sv_pj_main.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.setKeepScreenOn(true);
		surfaceHolder.addCallback(new surFaceView());

		ly_main_new1.setOnClickListener(this);
		ly_main_new2.setOnClickListener(this);
		img_main_banner.setOnClickListener(this);
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_NOTIC || type == APPCODE_UPDATA_EQU) {
			updata();
		} else if (type == APPCODE_UPDATA_REGION_MEDIA) {
			updata_banner();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		handler.removeMessages(3);
		handler.removeMessages(7);
		handler.removeMessages(10);
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
		handler.removeMessages(10);
		new_pics.clear();
		new_pics_i = 0;
		if (displayManager_media != null) {
			displayManager_media.stop();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		handler.removeMessages(3);
		handler.removeMessages(7);
		handler.removeMessages(10);
		if (displayManager_media != null) {
			displayManager_media.stop();
			displayManager_media.destroy();
		}
	}

	private void updata() {
		updata_new_notice();
		updata_banner();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ly_main_new1: {
			Intent intent = new Intent(context, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校通知");
			intent.putExtra("from", "PjMainActivity");
			intent.putExtra("type", 1);
			intent.putExtra("id", noticeInfoModels.get(0).id);
			context.startActivity(intent);
			break;
		}
		case R.id.ly_main_new2: {
			Intent intent = new Intent(context, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校通知");
			intent.putExtra("from", "PjMainActivity");
			intent.putExtra("type", 1);
			intent.putExtra("id", noticeInfoModels.get(1).id);
			context.startActivity(intent);
			break;
		}
		case R.id.img_main_banner: {
			if (new_pics != null && new_pics.size() > 0) {
				int i = new_pics_i - 1;
				if (i < 0) {
					i = 0;
				}
				Intent intent = new Intent(context, NoticeDetailsActivity.class);
				intent.putExtra("top", "学校新闻");
				intent.putExtra("from", "PjMainActivity");
				intent.putExtra("type", 4);
				intent.putExtra("id", new_pics.get(i).get("id"));
				context.startActivity(intent);
			}
			break;
		}
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
					// Log.d(TAG, path);
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null)
										img_main_banner
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
							}, 772, 307);
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
			case 4: {
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					img_main_new1.setVisibility(View.VISIBLE);
					img_main_new1.setImageBitmap(bitmap);
					android.view.ViewGroup.LayoutParams params = txt_main_new_title1
							.getLayoutParams();
					params.width = 450;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_title1.setLayoutParams(params);
					params = txt_main_new_nr1.getLayoutParams();
					params.width = 450;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_nr1.setLayoutParams(params);
				} else {
					img_main_new1.setVisibility(View.INVISIBLE);
					android.view.ViewGroup.LayoutParams params = txt_main_new_title1
							.getLayoutParams();
					params.width = 712;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_title1.setLayoutParams(params);
					params = txt_main_new_nr1.getLayoutParams();
					params.width = 712;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_nr1.setLayoutParams(params);
				}
				break;
			}
			case 5: {
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					img_main_new2.setVisibility(View.VISIBLE);
					img_main_new2.setImageBitmap(bitmap);
					android.view.ViewGroup.LayoutParams params = txt_main_new_title2
							.getLayoutParams();
					params.width = 460;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_title2.setLayoutParams(params);
					params = txt_main_new_nr2.getLayoutParams();
					params.width = 460;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_nr2.setLayoutParams(params);
				} else {
					img_main_new2.setVisibility(View.INVISIBLE);
					android.view.ViewGroup.LayoutParams params = txt_main_new_title2
							.getLayoutParams();
					params.width = 712;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_title2.setLayoutParams(params);
					params = txt_main_new_nr2.getLayoutParams();
					params.width = 712;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_nr2.setLayoutParams(params);
				}
				break;
			}
			case 7:
				mediaPlayer_region_play();
				break;
			case 10:
				if (new_pics != null && new_pics.size() > 0) {
					if (new_pics.size() == 1) {
						File file = new File(new_pics.get(0).get("path"));
						if (!file.exists()) {
							downLoadImg(new_pics.get(0).get("url"), new_pics
									.get(0).get("path"));
						} else {
							imageLoader.getBitmapFormUrl(
									new_pics.get(0).get("path"),
									new OnImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap, String url) {
											if (bitmap != null)
												img_main_banner
														.setImageDrawable(new BitmapDrawable(
																bitmap));
										}
									}, 772, 307);
						}
					} else {
						File file = new File(new_pics.get(new_pics_i).get(
								"path"));
						if (!file.exists()) {
							downLoadImg(new_pics.get(new_pics_i).get("url"),
									new_pics.get(new_pics_i).get("path"));
						} else {
							imageLoader.getBitmapFormUrl(
									new_pics.get(new_pics_i).get("path"),
									new OnImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap, String url) {
											if (bitmap != null)
												img_main_banner
														.setImageDrawable(new BitmapDrawable(
																bitmap));
											if (new_pics_i == new_pics.size() - 1) {
												new_pics_i = 0;
											} else {
												new_pics_i++;
											}
											handler.sendEmptyMessageDelayed(10,
													5 * 1000);
										}
									}, 772, 307);
						}
					}
				} else {
					updata_banner_region_media();
				}
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

	private void updata_new_notice() {
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		noticeInfoModels = database.query_by_lmdm("bpxxtz", 0, 2);
		if (noticeInfoModels != null && noticeInfoModels.size() > 0) {
			txt_main_nonew.setVisibility(View.INVISIBLE);
			ly_main_new1.setVisibility(View.VISIBLE);
			txt_main_new_title1.setText(noticeInfoModels.get(0).xxzt);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			txt_main_new_time1.setText(formatter.format(Long
					.parseLong(noticeInfoModels.get(0).fbsj)));
			txt_main_new_nr1.setText("");
			if (!TextUtils.isEmpty(noticeInfoModels.get(0).xxnr)) {
				Document document = Jsoup.parse(noticeInfoModels.get(0).xxnr);
				Element element = document.select("img").first();
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					new_rl_1 = "";
					for (Element e : p) {
						if (TextUtils.isEmpty(new_rl_1)) {
							new_rl_1 = e.text();
						} else {
							new_rl_1 = new_rl_1 + "\n" + e.text();
						}
						if (!TextUtils.isEmpty(new_rl_1)
								&& new_rl_1.length() > 100) {
							break;
						}
					}
					txt_main_new_nr1.setText(new_rl_1);
				}
				if (element != null) {
					new_url_1 = element.attr("src");
					if (!TextUtils.isEmpty(new_url_1)) {
						String[] s = new_url_1.split("/");
						new_path_1 = MyApplication.PATH_ROOT
								+ MyApplication.PATH_NOTICE + "/"
								+ s[s.length - 1];
						// Log.d(TAG, new_path_1);
						File file = new File(new_path_1);
						if (file.exists()) {
							imageLoader.getBitmapFormUrl(new_path_1,
									new OnImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap, String url) {
											if (bitmap != null) {
												Message msg = Message.obtain();
												msg.what = 4;
												msg.obj = bitmap;
												handler.sendMessage(msg);
											} else {
												handler.sendEmptyMessage(4);
											}
										}
									}, 250, 180);
						} else {
							downLoadImg(new_url_1, new_path_1, 4);
						}
					} else {
						handler.sendEmptyMessage(4);
					}
				} else {
					handler.sendEmptyMessage(4);
				}
			}
			if (noticeInfoModels.size() > 1) {
				ly_main_new2.setVisibility(View.VISIBLE);
				txt_main_new_title2.setText(noticeInfoModels.get(1).xxzt);
				txt_main_new_time2.setText(formatter.format(Long
						.parseLong(noticeInfoModels.get(1).fbsj)));
				txt_main_new_nr2.setText("");
				if (!TextUtils.isEmpty(noticeInfoModels.get(1).xxnr)) {
					Document document = Jsoup
							.parse(noticeInfoModels.get(1).xxnr);
					Element element = document.select("img").first();
					Elements p = document.select("p");
					if (p != null && p.size() > 0) {
						new_rl_2 = "";
						for (Element e : p) {
							if (TextUtils.isEmpty(new_rl_2)) {
								new_rl_2 = e.text();
							} else {
								new_rl_2 = new_rl_2 + "\n" + e.text();
							}
							if (!TextUtils.isEmpty(new_rl_2)
									&& new_rl_2.length() > 100) {
								break;
							}
						}
						txt_main_new_nr2.setText(new_rl_2);
					}
					if (element != null) {
						new_url_2 = element.attr("src");
						if (!TextUtils.isEmpty(new_url_2)) {
							String[] s = new_url_2.split("/");
							new_path_2 = MyApplication.PATH_ROOT
									+ MyApplication.PATH_NOTICE + "/"
									+ s[s.length - 1];
							File file = new File(new_path_2);
							if (file.exists()) {
								imageLoader.getBitmapFormUrl(new_path_2,
										new OnImageLoaderListener() {
											@Override
											public void onImageLoader(
													Bitmap bitmap, String url) {
												if (bitmap != null) {
													Message msg = Message
															.obtain();
													msg.what = 5;
													msg.obj = bitmap;
													handler.sendMessage(msg);
												} else {
													handler.sendEmptyMessage(5);
												}
											}
										}, 250, 180);
							} else {
								downLoadImg(new_url_2, new_path_2, 5);
							}
						} else {
							handler.sendEmptyMessage(5);
						}
					} else {
						handler.sendEmptyMessage(5);
					}
				}
			}
		} else {
			txt_main_nonew.setVisibility(View.VISIBLE);
			ly_main_new1.setVisibility(View.INVISIBLE);
			ly_main_new2.setVisibility(View.INVISIBLE);
		}
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
		handler.removeMessages(10);
		// imageLoader.clearCache();
		img_main_banner.setVisibility(View.VISIBLE);
		sv_pj_main.setVisibility(View.INVISIBLE);
		application.regionIsPlaying = false;
		if (application.mediaPlayer_region != null) {
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
		} else {
			application.mediaPlayer_region = new MediaPlayer();
		}
		new_pics = new ArrayList<Map<String, String>>();
		new_pics_i = 0;

		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				NoticeInfoDatabase database = new NoticeInfoDatabase();
				List<NoticeInfoModel> models = database.query_by_lmdm("bpxxxw");
				if (models != null && models.size() > 0) {
					for (NoticeInfoModel model : models) {
						if (!TextUtils.isEmpty(model.xxnr)) {
							Document document = Jsoup.parse(model.xxnr);
							Element element = document.select("img").first();
							if (element != null) {
								String src = element.attr("src");
								if (!TextUtils.isEmpty(src)) {
									String[] s = src.split("/");
									String path = MyApplication.PATH_ROOT
											+ MyApplication.PATH_NOTICE + "/"
											+ s[s.length - 1];
									Map<String, String> map = new HashMap<String, String>();
									map.put("url", src);
									map.put("path", path);
									map.put("id", model.id);
									new_pics.add(map);
								}
							}
						}
						if (new_pics.size() >= 5) {
							break;
						}
					}
				}
				handler.sendEmptyMessage(10);
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

	private void updata_banner_region_media() {
		if (application.playTaskModels != null
				&& application.playTaskModels.size() > 0) {
			for (PlayTaskModel model : application.playTaskModels) {
				if (model.playType.equals("REGION_MEDIA")
						&& model.position.equals("01")
						&& model.srcType.equals("TEMPLATE")) {
					String policyPath = application.getFtpPath(model.srcPath);
					File file = new File(policyPath);
					if (file.exists()) {
						ly_banner.removeAllViews();
						if (displayManager_media == null) {
							displayManager_media = new DisplayManager(context,
									application, ly_banner);
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
						&& model.position.equals("01")) {
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
				img_main_banner.setVisibility(View.INVISIBLE);
				sv_pj_main.setVisibility(View.VISIBLE);
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
		img_main_banner.setVisibility(View.VISIBLE);
		sv_pj_main.setVisibility(View.INVISIBLE);
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
										img_main_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
								}
							}, 772, 307);
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
		img_main_banner.setVisibility(View.VISIBLE);
		sv_pj_main.setVisibility(View.INVISIBLE);
		imageLoader.getBitmapFormRes(R.drawable.pj_ly_banner,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						img_main_banner.setImageDrawable(new BitmapDrawable(
								bitmap));
					}
				});
	}

	private void downLoadImg(final String urlStr, final String path,
			final int what) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			return;
		}
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							handler.sendEmptyMessage(what + 4);
						}
						ftpManager.disConnect();
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						handler.sendEmptyMessage(what + 4);
					}
				}
			}
		});
	}

	private void downLoadImg(final String urlStr, final String path) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			return;
		}
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							handler.sendEmptyMessage(10);
						} else {
							if (new_pics.size() > new_pics_i) {
								new_pics.remove(new_pics_i);
								new_pics_i = 0;
								handler.sendEmptyMessage(10);
							}
						}
					} else {
						if (new_pics.size() > new_pics_i) {
							new_pics.remove(new_pics_i);
							new_pics_i = 0;
							handler.sendEmptyMessage(10);
						}
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						handler.sendEmptyMessage(10);
					} else {
						if (new_pics.size() > new_pics_i) {
							new_pics.remove(new_pics_i);
							new_pics_i = 0;
							handler.sendEmptyMessage(10);
						}
					}
				}
			}
		});
	}
}
