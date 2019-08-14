package com.cx.doorplate.fragment;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.PicDreOrActivityModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class PhotoFragment extends Fragment implements OnItemClickListener {

	private final String TAG = "PhotoFragment";

	private MyApplication application;

	private GridView gv_photo;

	private ImageLoader imageLoader = null;

	private List<PicDreOrActivityModel> list = null;

	private LocalBroadcastManager broadcastManager;

	private int layer = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getActivity().getApplicationContext();
		broadcastManager = LocalBroadcastManager.getInstance(application);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cx_fragment_photo, null);
		gv_photo = (GridView) view.findViewById(R.id.gv_photo);
		gv_photo.setOnItemClickListener(this);
		if (list != null) {
			MyAdapter adapter = new MyAdapter();
			gv_photo.setAdapter(adapter);
		} else {
			gv_photo.setAdapter(null);
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if ("1".equals(list.get(arg2).objectType)) {
			Intent intent = new Intent(MyApplication.BROADCAST);
			intent.putExtra(MyApplication.BROADCAST_TAG, "photo_show");
			intent.putExtra("objectEntityAddress",
					list.get(arg2).objectEntityAddress);
			broadcastManager.sendBroadcast(intent);
		} else {
			Intent intent = new Intent(MyApplication.BROADCAST);
			intent.putExtra(MyApplication.BROADCAST_TAG, "photo_next");
			intent.putExtra("objectId", list.get(arg2).objectId);
			broadcastManager.sendBroadcast(intent);
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			private ImageView img_photo;
			private TextView txt_photo_name, txt_photo_data;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(application).inflate(
						R.layout.cx_item_photo, null);
				viewHolder.img_photo = (ImageView) view
						.findViewById(R.id.img_photo);
				viewHolder.txt_photo_name = (TextView) view
						.findViewById(R.id.txt_photo_name);
				viewHolder.txt_photo_data = (TextView) view
						.findViewById(R.id.txt_photo_data);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.txt_photo_name.setText(list.get(position).objectName);
			viewHolder.txt_photo_data.setText(list.get(position).createData);
			if (list.get(position).objectType.equals("2")) {
				viewHolder.img_photo.setImageResource(R.drawable.full);
				return view;
			}
			String imgUrl = null;
			if (!TextUtils.isEmpty(list.get(position).objectIcon)) {
				imgUrl = list.get(position).objectIcon;
			} else if (!TextUtils
					.isEmpty(list.get(position).objectEntityAddress)) {
				imgUrl = list.get(position).objectEntityAddress;
			}
			if (!TextUtils.isEmpty(imgUrl)) {
				String[] s = imgUrl.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PHOTO + "/" + s[s.length - 1];
				viewHolder.img_photo.setTag(path);
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						ImageView mImageView = (ImageView) gv_photo
								.findViewWithTag(url);
						if (mImageView != null && bitmap != null) {
							mImageView.setImageBitmap(bitmap);
						}
					}
				}, 380, 260);
			} else {
				if (list.get(position).objectType.equals("3")) {
					viewHolder.img_photo.setImageResource(R.drawable.full);
				}
			}
			return view;
		}
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public List<PicDreOrActivityModel> getList() {
		return list;
	}

	public void setList(List<PicDreOrActivityModel> list) {
		this.list = list;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
}
