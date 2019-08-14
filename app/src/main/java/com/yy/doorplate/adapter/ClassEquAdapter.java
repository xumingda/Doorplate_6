package com.yy.doorplate.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yy.doorplate.R;
import com.yy.doorplate.model.ClassEquInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class ClassEquAdapter extends BaseAdapter {

	private final String TAG = "ClassEquAdapter";

	private Context context;

	private List<ClassEquInfoModel> list;

	public ClassEquAdapter(Context context, List<ClassEquInfoModel> list) {
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

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	private class ViewHolder {
		private TextView txt_classequ_num;
		private Button btn_classequ_state;
		private CustomTextView txt_classequ_name, txt_classequ_xh;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_classequ,
					null);
			viewHolder.txt_classequ_name = (CustomTextView) view
					.findViewById(R.id.txt_classequ_name);
			viewHolder.txt_classequ_num = (TextView) view
					.findViewById(R.id.txt_classequ_num);
			viewHolder.txt_classequ_xh = (CustomTextView) view
					.findViewById(R.id.txt_classequ_xh);
			viewHolder.btn_classequ_state = (Button) view
					.findViewById(R.id.btn_classequ_state);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.txt_classequ_name.setText(list.get(position).zcsbmc);
		viewHolder.txt_classequ_num.setText("1" + list.get(position).jldw);
		viewHolder.txt_classequ_xh.setText(list.get(position).ggxh);
		if (!TextUtils.isEmpty(list.get(position).sbzk)
				&& list.get(position).sbzk.equals("1")) {
			viewHolder.btn_classequ_state.setText(context.getResources()
					.getString(R.string.unused));
		} else if (!TextUtils.isEmpty(list.get(position).sbzk)
				&& list.get(position).sbzk.equals("2")) {
			viewHolder.btn_classequ_state.setText(context.getResources()
					.getString(R.string.useing));
		} else if (!TextUtils.isEmpty(list.get(position).sbzk)
				&& list.get(position).sbzk.equals("3")) {
			viewHolder.btn_classequ_state.setText(context.getResources()
					.getString(R.string.repair));
			viewHolder.btn_classequ_state.setTextColor(context.getResources()
					.getColor(R.color.white));
			viewHolder.btn_classequ_state
					.setBackgroundResource(R.drawable.btn_classequ_state);
		} else if (!TextUtils.isEmpty(list.get(position).sbzk)
				&& list.get(position).sbzk.equals("4")) {
			viewHolder.btn_classequ_state.setText(context.getResources()
					.getString(R.string.scrap));
			viewHolder.btn_classequ_state.setTextColor(context.getResources()
					.getColor(R.color.white));
			viewHolder.btn_classequ_state
					.setBackgroundResource(R.drawable.btn_classequ_state);
		} else {
			viewHolder.btn_classequ_state.setText(context.getResources()
					.getString(R.string.none));
		}
		return view;
	}

}
