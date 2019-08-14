package com.yy.doorplate.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class StudentAdapter extends BaseAdapter implements OnScrollListener {

	private final String TAG = "StudentAdapter";

	private Context context;

	private ListView listView;

	private List<StudentInfoModel> list = null;

	private ImageLoader imageLoader = null;

	/**
	 * 一屏中第一个item的位置
	 */
	private int mFirstVisibleItem;

	/**
	 * 一屏中所有item的个数
	 */
	private int mVisibleItemCount;

	private boolean isFirstEnter = true;

	public StudentAdapter(Context context, ListView listView,
			List<StudentInfoModel> list) {
		this.context = context;
		this.listView = listView;
		this.list = list;
		imageLoader = new ImageLoader(context);
		listView.setOnScrollListener(this);
	}

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

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	private class ViewHolder {
		private TextView txt_student_zwh, txt_student_xh, txt_student_xm,
				txt_student_sex, txt_student_zhiwu;
		private ImageView img_student_touxiang;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_student,
					null);
			viewHolder.img_student_touxiang = (ImageView) view
					.findViewById(R.id.img_student_touxiang);
			viewHolder.txt_student_zwh = (TextView) view
					.findViewById(R.id.txt_student_zwh);
			viewHolder.txt_student_xh = (TextView) view
					.findViewById(R.id.txt_student_xh);
			viewHolder.txt_student_xm = (TextView) view
					.findViewById(R.id.txt_student_xm);
			viewHolder.txt_student_sex = (TextView) view
					.findViewById(R.id.txt_student_sex);
			viewHolder.txt_student_zhiwu = (TextView) view
					.findViewById(R.id.txt_student_zhiwu);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.txt_student_zwh.setText(list.get(position).zwh);
		viewHolder.txt_student_xh.setText(list.get(position).xsxh);
		viewHolder.txt_student_xm.setText(list.get(position).xm);
		if (!TextUtils.isEmpty(list.get(position).xb)
				&& list.get(position).xb.equals("1")) {
			viewHolder.txt_student_sex.setText("男");
		} else if (!TextUtils.isEmpty(list.get(position).xb)
				&& list.get(position).xb.equals("2")) {
			viewHolder.txt_student_sex.setText("女");
		}
		viewHolder.txt_student_zhiwu.setText(list.get(position).xszw);

		if (!TextUtils.isEmpty(list.get(position).zp)) {
			String[] s = list.get(position).zp.split("\\.");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE
					+ "/student_" + list.get(position).id + "."
					+ s[s.length - 1];
			viewHolder.img_student_touxiang.setTag(path);
			if (imageLoader.getBitmapFromMemCache(path) != null) {
				viewHolder.img_student_touxiang.setImageBitmap(imageLoader
						.getBitmapFromMemCache(path));
			}
		}
		return view;
	}

	private void showImage(int firstVisibleItem, int visibleItemCount) {
		for (int i = firstVisibleItem; i <= firstVisibleItem + visibleItemCount; i++) {
			if (i >= list.size() || i < 0) {
				return;
			}
			if (TextUtils.isEmpty(list.get(i).zp)) {
				return;
			}
			String[] s = list.get(i).zp.split("\\.");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE
					+ "/student_" + list.get(i).id + "." + s[s.length - 1];
			imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					ImageView mImageView = (ImageView) listView
							.findViewWithTag(url);
					if (mImageView != null && bitmap != null) {
						mImageView.setImageBitmap(bitmap);
					}
				}
			}, 80, 80);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		if (isFirstEnter && visibleItemCount > 0) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 仅当静止时才去下载图片，滑动时取消所有正在下载的任务
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
		} else {
			imageLoader.cancelTask();
		}
	}

	public void clearCache() {
		imageLoader.cancelTask();
		imageLoader.clearCache();
	}

}
