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
					webView.goBack();// ������һҳ��
				} else {
					finish();// �˳�����
				}
			}
		});
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return true;
			}
		});

		// ����WebViewĬ��ʹ�õ�������ϵͳĬ�����������ҳ����Ϊ��ʹ��ҳ��WebView��
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// ����ֵ��true��ʱ�����ȥWebView�򿪣�Ϊfalse����ϵͳ�����������������
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

		// ���webView����Ҫ�û��ֶ������û������������������webview��������֧�ֻ�ȡ���ƽ���
		webView.requestFocus();
		webView.requestFocusFromTouch();
		// webView.addJavascriptInterface(new JavaScriptObject(this),
		// "ManageCenterJSObj");

		WebSettings webSettings = webView.getSettings();
		// ����WebView����JavaScript��ִ��
		webSettings.setJavaScriptEnabled(true);
		// �����Ƿ��ڸ���ģʽ����WebView����ҳ�棬 ��С���ʺ���Ļ������
		webSettings.setLoadWithOverviewMode(true);
		// ������Ⱦ���ȼ����
		// webSettings.setRenderPriority(RenderPriority.HIGH);
		// ����WebView�Ƿ�Ӧ�����á�viewport�� HTML��ǩ��֧�ֻ���Ӧ��ʹ�ø�����ͼ��
		webSettings.setUseWideViewPort(true);
		// ͼƬ���ط��������Ⱦ
		// webSettings.setBlockNetworkImage(true);
		// ֧���Զ�����ͼƬ
		webSettings.setLoadsImagesAutomatically(true);
		// �ര��
		webSettings.supportMultipleWindows();
		// ����WebView�Ƿ�Ӧ��ʹ�������õķŴ����
		webSettings.setBuiltInZoomControls(false);
		// ����WebView�Ƿ�Ӧ����ʾ��Ļ���ſؼ�ʱ�� ʹ�����õķŴ����
		webSettings.setDisplayZoomControls(false);
		// ����WebView�Ƿ���Ҫ�û����Ʋ���ý��
		webSettings.setMediaPlaybackRequiresUserGesture(true);
		// ����WebView�Ƿ�Ӧ��֧������ʹ����Ļ�ϵı佹 ���ƺ�����
		webSettings.setSupportZoom(true);

		// ���û���ģʽ
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// ���� DOM storage API ����
		webSettings.setDomStorageEnabled(true);
		// ���� database storage API ����
		webSettings.setDatabaseEnabled(true);
		// ���� Application Caches ����
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
