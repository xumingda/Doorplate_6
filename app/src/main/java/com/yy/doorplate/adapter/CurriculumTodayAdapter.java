package com.yy.doorplate.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class CurriculumTodayAdapter extends BaseAdapter {

	private final String TAG = "CurriculumTodayAdapter";

	private Context context;

	private List<CurriculumInfoModel> list;

	public CurriculumTodayAdapter(Context context,
			List<CurriculumInfoModel> list) {
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
		private TextView txt_curriculum_today_teacher,
				txt_curriculum_today_time;
		private CustomTextView txt_curriculum_today_class,
				txt_curriculum_today_banji;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.item_curriculum_today, null);
			viewHolder.txt_curriculum_today_class = (CustomTextView) view
					.findViewById(R.id.txt_curriculum_today_class);
			viewHolder.txt_curriculum_today_teacher = (TextView) view
					.findViewById(R.id.txt_curriculum_today_teacher);
			viewHolder.txt_curriculum_today_time = (TextView) view
					.findViewById(R.id.txt_curriculum_today_time);
			viewHolder.txt_curriculum_today_banji = (CustomTextView) view
					.findViewById(R.id.txt_curriculum_today_banji);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.txt_curriculum_today_class.setText(list.get(position).kcmc);
		viewHolder.txt_curriculum_today_teacher
				.setText(list.get(position).skjsxm);
		viewHolder.txt_curriculum_today_time.setText(MyApplication
				.jcToTime(list.get(position).jc));
		viewHolder.txt_curriculum_today_banji
				.setText(list.get(position).skbjmc);
		return view;
	}

}
