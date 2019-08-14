package com.yy.doorplate.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yy.doorplate.R;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class NoticeAdapter extends BaseAdapter {

	private final String TAG = "NoticeAdapter";

	private Context context;

	private List<NoticeInfoModel> list;

	public NoticeAdapter(Context context, List<NoticeInfoModel> list) {
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
		private TextView txt_item_notice_time;
		private CustomTextView txt_item_notice_title;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_notice,
					null);
			viewHolder.txt_item_notice_title = (CustomTextView) view
					.findViewById(R.id.txt_item_notice_title);
			viewHolder.txt_item_notice_time = (TextView) view
					.findViewById(R.id.txt_item_notice_time);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.txt_item_notice_title.setText(list.get(position).xxzt);
		if (!TextUtils.isEmpty(list.get(position).fbsj)) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				viewHolder.txt_item_notice_time.setText(formatter.format(Long
						.parseLong(list.get(position).fbsj)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				viewHolder.txt_item_notice_time
						.setText(list.get(position).fbsj);
			}
		}
		return view;
	}
}
