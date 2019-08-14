package com.yy.doorplate.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class HonorAdapter extends BaseAdapter {

	private final String TAG = "HonorAdapter";

	private Context context;

	private List<PrizeInfoModel> list;

	private GridView gridView;

	private ImageLoader imageLoader;

	private int select = 0;

	public HonorAdapter(Context context, List<PrizeInfoModel> list,
			GridView gridView, ImageLoader imageLoader) {
		this.context = context;
		this.list = list;
		this.gridView = gridView;
		this.imageLoader = imageLoader;
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

	private class ViewHolder {
		RelativeLayout ly_item_honor;
		ImageView img_item_honor;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_honor,
					null);
			viewHolder.ly_item_honor = (RelativeLayout) view
					.findViewById(R.id.ly_item_honor);
			viewHolder.img_item_honor = (ImageView) view
					.findViewById(R.id.img_item_honor);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		if (!TextUtils.isEmpty(list.get(position).iconUrl)) {
			String[] s = list.get(position).iconUrl.split("/");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE
					+ "/prizeIcon_" + s[s.length - 1];
			viewHolder.img_item_honor.setTag(path);
			imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					ImageView mImageView = (ImageView) gridView
							.findViewWithTag(url);
					if (mImageView != null && bitmap != null) {
						mImageView.setImageBitmap(bitmap);
					}
				}
			}, 235, 222);
		}
		if (select == position) {
			viewHolder.ly_item_honor
					.setBackgroundResource(R.drawable.item_honor_click);
		} else {
			viewHolder.ly_item_honor
					.setBackgroundResource(R.drawable.item_honor);
		}
		return view;
	}

	public void updataSelectedView(int position) {
		select = position;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				View view = gridView.getChildAt(i);
				if (view == null) {
					break;
				}
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				if (i == position) {
					viewHolder.ly_item_honor
							.setBackgroundResource(R.drawable.item_honor_click);
				} else {
					viewHolder.ly_item_honor
							.setBackgroundResource(R.drawable.item_honor);
				}
			}
		}
	}
}
