package com.yy.doorplate.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.yy.doorplate.R;

public class MyPullUpListView extends ListView implements OnScrollListener {

	private final String TAG = "MyPullUpListView";

	private Context context;

	/** 底部显示正在加载的页面 */
	private View footerView = null;
	private TextView textView;

	private int totalItemCount;// 总数量
	private int lastVisibieItem;// 最后一个可见的item;
	private boolean isLoading = false;// 判断变量
	private IloadListener iLoadListener;// 接口变量

	public MyPullUpListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initListView();
	}

	public MyPullUpListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initListView();
	}

	public MyPullUpListView(Context context) {
		super(context);
		this.context = context;
		initListView();
	}

	private void initListView() {
		LayoutInflater inflater = LayoutInflater.from(context);
		footerView = inflater.inflate(R.layout.listview_loadbar, null);
		textView = (TextView) footerView
				.findViewById(R.id.txt_listview_loadbar);
		addFooterView(footerView);
		footerView.setVisibility(View.GONE);
		setOnScrollListener(this);
		// setFooterDividersEnabled(false);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.lastVisibieItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (totalItemCount == lastVisibieItem
				&& scrollState == SCROLL_STATE_IDLE) {
			Log.d(TAG, "onScrollStateChanged");
			if (!isLoading && iLoadListener != null) {
				// 加载更多（获取接口）
				iLoadListener.onLoad();
			}
		}
	}

	// 加载更多数据的回调接口
	public interface IloadListener {
		public void onLoad();
	}

	public void setInterface(IloadListener iLoadListener) {
		this.iLoadListener = iLoadListener;
	}

	// 加载完成通知隐藏
	public void loadComplete() {
		isLoading = false;
		footerView.setVisibility(View.GONE);
	}

	public void loadStart() {
		isLoading = true;
		footerView.setVisibility(View.VISIBLE);
	}

}
