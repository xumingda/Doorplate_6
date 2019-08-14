package com.yy.doorplate.ui;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.VideoListActivity;
import com.yy.doorplate.database.VideoDatabase;
import com.yy.doorplate.model.VideoInfoModel;

public class SmallVideoView extends AppCodeView {

	private TextView txt_classnew_novideo;
	private SurfaceView sv_video;

	private SurfaceHolder surfaceHolder_video = null;

	private List<VideoInfoModel> vedioInfoModels = null;
	private int vedioInfoModels_i = 0;
	public MediaPlayer mediaPlayer = null;

	public SmallVideoView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		// TODO 自动生成的方法存根
		View.inflate(context, R.layout.ly_small_video, this);
		txt_classnew_novideo = (TextView) findViewById(R.id.txt_classnew_novideo);
		sv_video = (SurfaceView) findViewById(R.id.sv_video);

		sv_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, VideoListActivity.class);
				context.startActivity(intent);
			}
		});

		surfaceHolder_video = sv_video.getHolder();
		surfaceHolder_video.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder_video.setKeepScreenOn(true);
		surfaceHolder_video.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
					}
					mediaPlayer.release();
					mediaPlayer = null;
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				video_play();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});

		VideoDatabase database = new VideoDatabase();
		vedioInfoModels = database.query(
				"relaObjType = ? and relaObjValue = ?", new String[] { "bjdm",
						application.equInfoModel.bjdm });
		vedioInfoModels_i = 0;
		if (vedioInfoModels != null && vedioInfoModels.size() > 0) {
			sv_video.setVisibility(View.VISIBLE);
			txt_classnew_novideo.setVisibility(View.INVISIBLE);
		} else {
			sv_video.setVisibility(View.INVISIBLE);
			txt_classnew_novideo.setVisibility(View.VISIBLE);
		}
	}

	private void video_play() {
		if (vedioInfoModels.size() <= vedioInfoModels_i) {
			vedioInfoModels_i = 0;
		}
		String[] s = vedioInfoModels.get(vedioInfoModels_i).resPath.split("/");
		String path = MyApplication.PATH_ROOT + MyApplication.PATH_VIDEO + "/"
				+ s[s.length - 1];
		if (!new File(path).exists()) {
			return;
		}
		try {
			if (mediaPlayer == null) {
				mediaPlayer = new MediaPlayer();
			} else {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
			}
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(path);
			mediaPlayer.setDisplay(surfaceHolder_video);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					if (!application.isGuangbo) {
						mediaPlayer.start();
					}
				}
			});
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							vedioInfoModels_i++;
							video_play();
						}
					});
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void pause() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void resume() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void stop() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void destroy() {
		// TODO 自动生成的方法存根

	}

}
