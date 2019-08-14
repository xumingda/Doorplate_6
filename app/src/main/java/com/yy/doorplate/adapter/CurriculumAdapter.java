package com.yy.doorplate.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.CurriculumInfoModel;

public class CurriculumAdapter extends BaseAdapter {

	private final String TAG = "CurriculumAdapter";

	private MyApplication application;

	private List<CurriculumInfoModel> list;

	public CurriculumAdapter(Context context, List<CurriculumInfoModel> list) {
		this.application = (MyApplication) context;
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
		private TextView txt_curriculum_time, txt_curriculum_jc,
				txt_curriculum_name, txt_curriculum_jsxm, txt_curriculum_banji;
		private ImageView img_curriculum;
	}

	public List<CurriculumInfoModel> getList() {
		return list;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(application).inflate(
					R.layout.item_curriculum, null);
			viewHolder.txt_curriculum_time = (TextView) view
					.findViewById(R.id.txt_curriculum_time);
			viewHolder.txt_curriculum_jc = (TextView) view
					.findViewById(R.id.txt_curriculum_jc);
			viewHolder.txt_curriculum_name = (TextView) view
					.findViewById(R.id.txt_curriculum_name);
			viewHolder.txt_curriculum_jsxm = (TextView) view
					.findViewById(R.id.txt_curriculum_jsxm);
			viewHolder.txt_curriculum_banji = (TextView) view
					.findViewById(R.id.txt_curriculum_banji);
			viewHolder.img_curriculum = (ImageView) view
					.findViewById(R.id.img_curriculum);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		if (!TextUtils.isEmpty(list.get(position).jc)) {
			viewHolder.txt_curriculum_time.setText(MyApplication.jcToTime(list
					.get(position).jc));
			if (!TextUtils.isEmpty(list.get(position).jcshow)) {
				String str = "µÚ ";
				String[] js;
				js = list.get(position).jcshow.split("-");
				for (int i = 0; i < js.length; i++) {
					try {
						if (Integer.parseInt(js[i]) > 100) {
							js[i] = (Integer.parseInt(js[i]) - 100) + "";
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
						str = list.get(position).jcshow;
						break;
					}
					if (i == js.length - 1) {
						str = str + js[i] + " ½Ú";
					} else {
						str = str + js[i] + ",";
					}
				}
				viewHolder.txt_curriculum_jc.setText(str);
			}
		}
		if (application.curriculumInfoModel_now != null
				&& list.get(position).id
						.equals(application.curriculumInfoModel_now.id)) {
			viewHolder.img_curriculum
					.setImageResource(R.drawable.img_curriculum_click);
			viewHolder.txt_curriculum_name.setTextColor(application
					.getResources().getColor(R.color.yellow));
		}
		viewHolder.txt_curriculum_name.setText(list.get(position).kcmc);
		// change for 8
		// viewHolder.txt_curriculum_banji.setText(list.get(position).skbjmc);
		viewHolder.txt_curriculum_jsxm.setText(list.get(position).skjsxm);
		return view;
	}
}
