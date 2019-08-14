package com.yy.doorplate.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;

public class WebviewActivity extends Activity {

	private static final String TAG = "WebviewActivity";

	private final static String URL = "http://videoup.chaoxing.com/DongGuanst/hbook/book/cata" /* "http://zxx.chaoxing.com/api/readsys/first" */;

	private MyApplication application;

	private Button btn_webview_back;
	private WebView webView;
	private ProgressBar pb_webview;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		String url = getIntent().getStringExtra("url");

		btn_webview_back = (Button) findViewById(R.id.btn_webview_back);
		pb_webview = (ProgressBar) findViewById(R.id.pb_webview);
		webView = (WebView) findViewById(R.id.webView);
		if (TextUtils.isEmpty(url)) {
			webView.loadUrl(URL);
		} else {
			webView.loadUrl(url);
		}
		Log.i(TAG, "webView.getUrl():" + webView.getUrl());

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		btn_webview_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (webView.canGoBack()) {
					webView.goBack();// 返回上一页面
				} else {
					finish();// 退出程序
				}
			}
		});
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return true;
			}
		});

		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					pb_webview.setVisibility(View.INVISIBLE);
				} else {
					pb_webview.setVisibility(View.VISIBLE);
					pb_webview.setProgress(newProgress);
				}
			}
		});

		// 如果webView中需要用户手动输入用户名、密码或其他，则webview必须设置支持获取手势焦点
		webView.requestFocus();
		webView.requestFocusFromTouch();
		// webView.addJavascriptInterface(new JavaScriptObject(this),
		// "ManageCenterJSObj");

		WebSettings webSettings = webView.getSettings();
		// 告诉WebView启用JavaScript的执行
		webSettings.setJavaScriptEnabled(true);
		// 设置是否在概述模式，即WebView加载页面， 缩小到适合屏幕的内容
		webSettings.setLoadWithOverviewMode(true);
		// 设置渲染优先级最高
		// webSettings.setRenderPriority(RenderPriority.HIGH);
		// 设置WebView是否应该启用“viewport” HTML标签的支持还是应该使用各种视图。
		webSettings.setUseWideViewPort(true);
		// 图片加载放在最后渲染
		// webSettings.setBlockNetworkImage(true);
		// 支持自动加载图片
		webSettings.setLoadsImagesAutomatically(true);
		// 多窗口
		webSettings.supportMultipleWindows();
		// 设置WebView是否应该使用其内置的放大机制
		webSettings.setBuiltInZoomControls(false);
		// 设置WebView是否应该显示屏幕缩放控件时， 使用内置的放大机制
		webSettings.setDisplayZoomControls(false);
		// 设置WebView是否需要用户手势播放媒体
		webSettings.setMediaPlaybackRequiresUserGesture(true);
		// 设置WebView是否应该支持缩放使用屏幕上的变焦 控制和手势
		webSettings.setSupportZoom(true);

		// 设置缓存模式
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// 开启 DOM storage API 功能
		webSettings.setDomStorageEnabled(true);
		// 开启 database storage API 功能
		webSettings.setDatabaseEnabled(true);
		// 开启 Application Caches 功能
		webSettings.setAppCacheEnabled(true);

		application.openVoice();
	}

	// private class JavaScriptObject {
	//
	// private Activity mActivity;
	//
	// public JavaScriptObject(Activity activity) {
	// mActivity = activity;
	// }
	//
	// public void FinishManageCenter() {
	// mActivity.finish();
	// }
	// }

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case KeyEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case MotionEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		application.closeVoice();
		webView.removeAllViews();
		webView.destroy();
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

}
