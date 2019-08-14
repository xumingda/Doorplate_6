package com.cx.doorplate.fragment;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.VideoInfoModel;

public class GrxxVideoFragment extends Fragment implements OnItemClickListener {

	private final String TAG = "GrxxVideoFragment";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;

	private GridView gv_video;

	private List<VideoInfoModel> videoInfoModels;

	private DisplayImageOptions options;
	private ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getActivity().getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(application);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cx_fragment_grxxvideo, null);
		gv_video = (GridView) view.findViewById(R.id.gv_video);

		gv_video.setOnItemClickListener(this);

		if (videoInfoModels != null) {
			MyAdapter adapter = new MyAdapter();
			gv_video.setAdapter(adapter);
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String[] s = videoInfoModels.get(arg2).resPath.split("/");
		String path = MyApplication.PATH_ROOT + MyApplication.PATH_VIDEO + "/"
				+ s[s.length - 1];
		Intent intent = new Intent(MyApplication.BROADCAST);
		intent.putExtra(MyApplication.BROADCAST_TAG, "video_show");
		intent.putExtra("path", path);
		broadcastManager.sendBroadcast(intent);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return videoInfoModels.size();
		}

		@Override
		public Object getItem(int position) {
			return videoInfoModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			private ImageView img_item_video;
			private TextView txt_item_video;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(application).inflate(
						R.layout.cx_item_video, null);
				viewHolder.img_item_video = (ImageView) view
						.findViewById(R.id.img_item_video);
				viewHolder.txt_item_video = (TextView) view
						.findViewById(R.id.txt_item_video);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.txt_item_video
					.setText(videoInfoModels.get(position).fileName);
			// …Ë÷√Àı¬‘Õº
			try {
				String[] s = videoInfoModels.get(position).resPath.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_VIDEO + "/" + s[s.length - 1];
				Uri uri = Uri.fromFile(new File(path));
				imageLoader.displayImage(uri + "", viewHolder.img_item_video,
						options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return view;
		}

	}

	public List<VideoInfoModel> getVideoInfoModels() {
		return videoInfoModels;
	}

	public void setVideoInfoModels(List<VideoInfoModel> videoInfoModels) {
		this.videoInfoModels = videoInfoModels;
	}

	public DisplayImageOptions getOptions() {
		return options;
	}

	public void setOptions(DisplayImageOptions options) {
		this.options = options;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}
}
