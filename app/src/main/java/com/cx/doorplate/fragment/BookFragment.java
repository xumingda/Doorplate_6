package com.cx.doorplate.fragment;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.model.BooksInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class BookFragment extends Fragment implements OnClickListener {

	private final String TAG = "BookFragment";

	private MyApplication application;

	private RelativeLayout ly_fragment_book1, ly_fragment_book2,
			ly_fragment_book3, ly_fragment_book4, ly_fragment_book5;
	private ImageView img_fragment_book1, img_fragment_book2,
			img_fragment_book3, img_fragment_book4, img_fragment_book5;
	private TextView txt_fragment_book1, txt_fragment_book2,
			txt_fragment_book3, txt_fragment_book4, txt_fragment_book5;

	private ImageLoader imageLoader = null;

	private List<BooksInfoModel> list = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cx_fragment_book, null);
		ly_fragment_book1 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_book1);
		ly_fragment_book2 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_book2);
		ly_fragment_book3 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_book3);
		ly_fragment_book4 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_book4);
		ly_fragment_book5 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_book5);
		img_fragment_book1 = (ImageView) view
				.findViewById(R.id.img_fragment_book1);
		img_fragment_book2 = (ImageView) view
				.findViewById(R.id.img_fragment_book2);
		img_fragment_book3 = (ImageView) view
				.findViewById(R.id.img_fragment_book3);
		img_fragment_book4 = (ImageView) view
				.findViewById(R.id.img_fragment_book4);
		img_fragment_book5 = (ImageView) view
				.findViewById(R.id.img_fragment_book5);
		txt_fragment_book1 = (TextView) view
				.findViewById(R.id.txt_fragment_book1);
		txt_fragment_book2 = (TextView) view
				.findViewById(R.id.txt_fragment_book2);
		txt_fragment_book3 = (TextView) view
				.findViewById(R.id.txt_fragment_book3);
		txt_fragment_book4 = (TextView) view
				.findViewById(R.id.txt_fragment_book4);
		txt_fragment_book5 = (TextView) view
				.findViewById(R.id.txt_fragment_book5);

		ly_fragment_book1.setOnClickListener(this);
		ly_fragment_book2.setOnClickListener(this);
		ly_fragment_book3.setOnClickListener(this);
		ly_fragment_book4.setOnClickListener(this);
		ly_fragment_book5.setOnClickListener(this);

		if (list != null) {
			if (list.size() > 0) {
				ly_fragment_book1.setVisibility(View.VISIBLE);
				txt_fragment_book1.setText(list.get(0).title);
				if (!TextUtils.isEmpty(list.get(0).cover)) {
					String[] s = list.get(0).cover.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_BOOK + "/" + s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						Message msg = Message.obtain();
						msg.what = 1;
						msg.obj = path;
						handler.sendMessage(msg);
					} else {
						downLoadImg(list.get(0).cover, path, 1);
					}
				}
			}
			if (list.size() > 1) {
				ly_fragment_book2.setVisibility(View.VISIBLE);
				txt_fragment_book2.setText(list.get(1).title);
				if (!TextUtils.isEmpty(list.get(1).cover)) {
					String[] s = list.get(1).cover.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_BOOK + "/" + s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						Message msg = Message.obtain();
						msg.what = 2;
						msg.obj = path;
						handler.sendMessage(msg);
					} else {
						downLoadImg(list.get(1).cover, path, 2);
					}
				}
			}
			if (list.size() > 2) {
				ly_fragment_book3.setVisibility(View.VISIBLE);
				txt_fragment_book3.setText(list.get(2).title);
				if (!TextUtils.isEmpty(list.get(2).cover)) {
					String[] s = list.get(2).cover.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_BOOK + "/" + s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						Message msg = Message.obtain();
						msg.what = 3;
						msg.obj = path;
						handler.sendMessage(msg);
					} else {
						downLoadImg(list.get(2).cover, path, 3);
					}
				}
			}
			if (list.size() > 3) {
				ly_fragment_book4.setVisibility(View.VISIBLE);
				txt_fragment_book4.setText(list.get(3).title);
				if (!TextUtils.isEmpty(list.get(3).cover)) {
					String[] s = list.get(3).cover.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_BOOK + "/" + s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						Message msg = Message.obtain();
						msg.what = 4;
						msg.obj = path;
						handler.sendMessage(msg);
					} else {
						downLoadImg(list.get(3).cover, path, 4);
					}
				}
			}
			if (list.size() > 4) {
				ly_fragment_book5.setVisibility(View.VISIBLE);
				txt_fragment_book5.setText(list.get(4).title);
				if (!TextUtils.isEmpty(list.get(4).cover)) {
					String[] s = list.get(4).cover.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_BOOK + "/" + s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						Message msg = Message.obtain();
						msg.what = 5;
						msg.obj = path;
						handler.sendMessage(msg);
					} else {
						downLoadImg(list.get(4).cover, path, 5);
					}
				}
			}
		}
		return view;
	}

	private void downLoadImg(final String urlStr, final String path,
			final int what) {
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							Message msg = Message.obtain();
							msg.what = what;
							msg.obj = path;
							handler.sendMessage(msg);
						}
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						Message msg = Message.obtain();
						msg.what = what;
						msg.obj = path;
						handler.sendMessage(msg);
					}
				}
			}
		});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				imageLoader.getBitmapFormUrl(msg.obj.toString(),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null) {
									img_fragment_book1.setImageBitmap(bitmap);
								}
							}
						}, 112, 160);
				break;
			}
			case 2: {
				imageLoader.getBitmapFormUrl(msg.obj.toString(),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null) {
									img_fragment_book2.setImageBitmap(bitmap);
								}
							}
						}, 112, 160);
				break;
			}
			case 3: {
				imageLoader.getBitmapFormUrl(msg.obj.toString(),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null) {
									img_fragment_book3.setImageBitmap(bitmap);
								}
							}
						}, 112, 160);
				break;
			}
			case 4: {
				imageLoader.getBitmapFormUrl(msg.obj.toString(),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null) {
									img_fragment_book4.setImageBitmap(bitmap);
								}
							}
						}, 112, 160);
				break;
			}
			case 5: {
				imageLoader.getBitmapFormUrl(msg.obj.toString(),
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null) {
									img_fragment_book5.setImageBitmap(bitmap);
								}
							}
						}, 112, 160);
				break;
			}
			}
		};
	};

	@Override
	public void onClick(View v) {
		LocalBroadcastManager broadcastManager = LocalBroadcastManager
				.getInstance(application);
		Intent intent = new Intent(MyApplication.BROADCAST);
		intent.putExtra(MyApplication.BROADCAST_TAG, "open_book");
		broadcastManager.sendBroadcast(intent);
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public List<BooksInfoModel> getList() {
		return list;
	}

	public void setList(List<BooksInfoModel> list) {
		this.list = list;
	}
}
