package com.yy.doorplate.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.R;
import com.yy.doorplate.model.QuestionNaireModel;

public class QuestAdapter extends BaseAdapter {

	private final String TAG = "QuestAdapter";

	private Context context;

	private List<QuestionNaireModel> list;

	private ListView listView;

	private int select = 0;

	public QuestAdapter(Context context, List<QuestionNaireModel> list,
			ListView listView) {
		this.context = context;
		this.list = list;
		this.listView = listView;
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
		RelativeLayout ly_item_quest;
		TextView txt_item_quest;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_quest,
					null);
			viewHolder.ly_item_quest = (RelativeLayout) view
					.findViewById(R.id.ly_item_quest);
			viewHolder.txt_item_quest = (TextView) view
					.findViewById(R.id.txt_item_quest);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.txt_item_quest.setText(list.get(position).title);
		if (select == position) {
			viewHolder.ly_item_quest
					.setBackgroundResource(R.drawable.item_quest_click);
		} else {
			viewHolder.ly_item_quest
					.setBackgroundResource(R.drawable.item_quest);
		}
		return view;
	}

	public void updataSelectedView(int position) {
		select = position;
		if (list != null && list.size() > 0) {
			int startShownIndex = listView.getFirstVisiblePosition();
			int endShownIndex = listView.getLastVisiblePosition();
			for (int i = startShownIndex; i <= endShownIndex; i++) {
				View view = listView.getChildAt(i - startShownIndex);
				if (view == null) {
					break;
				}
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				if (select == i) {
					viewHolder.ly_item_quest
							.setBackgroundResource(R.drawable.item_quest_click);
				} else {
					viewHolder.ly_item_quest
							.setBackgroundResource(R.drawable.item_quest);
				}
			}
		}
	}
}
