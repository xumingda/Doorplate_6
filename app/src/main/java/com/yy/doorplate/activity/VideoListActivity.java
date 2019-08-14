package com.yy.doorplate.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.VideoDatabase;
import com.yy.doorplate.model.VideoInfoModel;

public class VideoListActivity extends Activity implements OnItemClickListener {

	private final String TAG = "VideoListActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private Button btn_video_back;
	private GridView grid_video;

	private List<VideoInfoModel> vedioInfoModels = null;

	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_list);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	private void initView() {
		btn_video_back = (Button) findViewById(R.id.btn_video_back);
		grid_video = (GridView) findViewById(R.id.grid_video);

		grid_video.setOnItemClickListener(this);
		btn_video_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();

		VideoDatabase database = new VideoDatabase();
		vedioInfoModels = database.query(
				"relaObjType = ? and relaObjValue = ?", new String[] { "bjdm",
						application.equInfoModel.bjdm });
		if (vedioInfoModels != null && vedioInfoModels.size() > 0) {
			MyAdapter adapter = new MyAdapter();
			grid_video.setAdapter(adapter);
		} else {
			grid_video.setAdapter(null);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String[] s = vedioInfoModels.get(arg2).resPath.split("/");
		String path = MyApplication.PATH_ROOT + MyApplication.PATH_VIDEO + "/"
				+ s[s.length - 1];
		Intent intent = new Intent(this, VideoPlayActivity.class);
		intent.putExtra("path", path);
		startActivity(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case KeyEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case MotionEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return vedioInfoModels.size();
		}

		@Override
		public Object getItem(int position) {
			return vedioInfoModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(VideoListActivity.this)
						.inflate(R.layout.item_video, null);
				vh.txt_video_name = (TextView) convertView
						.findViewById(R.id.txt_video_name);
				vh.txt_video_desc = (TextView) convertView
						.findViewById(R.id.txt_video_desc);
				vh.txt_video_like = (TextView) convertView
						.findViewById(R.id.txt_video_like);
				vh.img_item_video = (ImageView) convertView
						.findViewById(R.id.img_item_video);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.txt_video_name.setText(vedioInfoModels.get(position).fileName);
			vh.txt_video_desc.setText(vedioInfoModels.get(position).fileDesc);
			vh.txt_video_like.setText("点赞数:"
					+ vedioInfoModels.get(position).likeNum);
			// 设置缩略图
			String[] s = vedioInfoModels.get(position).resPath.split("/");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_VIDEO
					+ "/" + s[s.length - 1];
			Uri uri = Uri.fromFile(new File(path));
			imageLoader.displayImage(uri + "", vh.img_item_video, options);

			vh.txt_video_like.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					commitLike(position, (TextView) v);
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView txt_video_name, txt_video_desc, txt_video_like;
			ImageView img_item_video;
		}

		private void commitLike(int position, TextView textView) {
			VideoInfoModel vedioInfoModel = vedioInfoModels.get(position);
			int like = Integer.parseInt(vedioInfoModel.likeNum);
			like++;
			vedioInfoModel.likeNum = like + "";
			textView.setText("点赞数:" + vedioInfoModel.likeNum);

			VideoDatabase database = new VideoDatabase();
			database.update(vedioInfoModel);

			application.httpProcess.commitVideo(null, vedioInfoModel);
		}

	}
}
