package com.yy.doorplate.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yy.doorplate.R;
import com.yy.doorplate.model.PlayTaskModel;

public class AdAdapter extends BaseAdapter {

	private final String TAG = "AdAdapter";

	private Context context;

	private List<PlayTaskModel> list;

	public AdAdapter(Context context, List<PlayTaskModel> list) {
		this.context = context;
		this.list = list;
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
		private TextView txt_item_ad_name, txt_item_ad_start, txt_item_ad_end,
				txt_item_ad_type;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_guanggao,
					null);
			viewHolder.txt_item_ad_name = (TextView) view
					.findViewById(R.id.txt_item_ad_name);
			viewHolder.txt_item_ad_start = (TextView) view
					.findViewById(R.id.txt_item_ad_start);
			viewHolder.txt_item_ad_end = (TextView) view
					.findViewById(R.id.txt_item_ad_end);
			viewHolder.txt_item_ad_type = (TextView) view
					.findViewById(R.id.txt_item_ad_type);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.txt_item_ad_name.setText(list.get(position).taskName);
		if (!TextUtils.isEmpty(list.get(position).playStartDate)
				|| !TextUtils.isEmpty(list.get(position).playStartTime)) {
			viewHolder.txt_item_ad_start.setText("开始时间 : "
					+ list.get(position).playStartDate + " "
					+ list.get(position).playStartTime);
		}
		if (!TextUtils.isEmpty(list.get(position).playEndDate)
				|| !TextUtils.isEmpty(list.get(position).playEndTime)) {
			viewHolder.txt_item_ad_end.setText("结束时间 : "
					+ list.get(position).playEndDate + " "
					+ list.get(position).playEndTime);
		}
		if (list.get(position).srcType.equals("VIDEO")) {
			viewHolder.txt_item_ad_type.setText("类型 : 视频");
		} else if (list.get(position).srcType.equals("IMAGE")) {
			viewHolder.txt_item_ad_type.setText("类型 : 图片");
		}
		return view;
	}
}
