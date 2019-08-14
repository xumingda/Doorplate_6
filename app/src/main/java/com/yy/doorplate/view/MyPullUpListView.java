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

	/** �ײ���ʾ���ڼ��ص�ҳ�� */
	private View footerView = null;
	private TextView textView;

	private int totalItemCount;// ������
	private int lastVisibieItem;// ���һ���ɼ���item;
	private boolean isLoading = false;// �жϱ���
	private IloadListener iLoadListener;// �ӿڱ���

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
				// ���ظ��ࣨ��ȡ�ӿڣ�
				iLoadListener.onLoad();
			}
		}
	}

	// ���ظ������ݵĻص��ӿ�
	public interface IloadListener {
		public void onLoad();
	}

	public void setInterface(IloadListener iLoadListener) {
		this.iLoadListener = iLoadListener;
	}

	// �������֪ͨ����
	public void loadComplete() {
		isLoading = false;
		footerView.setVisibility(View.GONE);
	}

	public void loadStart() {
		isLoading = true;
		footerView.setVisibility(View.VISIBLE);
	}

}
