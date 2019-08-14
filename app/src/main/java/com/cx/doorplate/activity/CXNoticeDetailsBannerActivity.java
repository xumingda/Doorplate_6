package com.cx.doorplate.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.SchoolInfoDatabase;
import com.yy.doorplate.database.XyxxDatabase;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.SchoolInfoModel;
import com.yy.doorplate.model.XyxxTypeModel;
import com.yy.doorplate.view.CustomTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class CXNoticeDetailsBannerActivity extends Activity {

    private final String TAG = "CXNoticeDetailsActivity";

    private MyApplication application;
    private Timer timer;

    private NoticeInfoModel noticeInfoModel = null;

    private Button btn_noticedetails_back;

//	private ImageView img_notice_title;

    private TextClock textClock;
    private TextView txt_weather, txt_notice_details_title,
            txt_notice_details_time,txt_notice_title;
    private CustomTextView txt_school_name;
    private WebView web_notice;
    private ProgressBar pb_webview;
    private int i=0;

    private ProgressDialog progressDialog = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_activity_notice_details);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        application = (MyApplication) getApplicationContext();




        Intent intent = this.getIntent();
        List<NoticeInfoModel> list = (List<NoticeInfoModel>) intent.getSerializableExtra("NoticeInfoModelList");

        Random ra =new Random();
        initView();
		timer=new Timer();
		timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(i<list.size()){
                    noticeInfoModel=list.get(i);
                    i++;
                }else{
                    i=0;
                    noticeInfoModel=list.get(i);
                }

                handler.sendEmptyMessage(200);
                Log.e("�л�","֪ͨ�л�"+i+"  size"+list.size());


            }
        },1000,10000);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        application.destroyFloating();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        application.destroyFloating();
        web_notice.removeAllViews();
        web_notice.destroy();

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

    private void initView() {
        btn_noticedetails_back = (Button) findViewById(R.id.btn_noticedetails_back);
        txt_weather = (TextView) findViewById(R.id.txt_weather);
        txt_notice_details_title = (TextView) findViewById(R.id.txt_notice_details_title);
        txt_notice_details_time = (TextView) findViewById(R.id.txt_notice_details_time);
        txt_school_name = (CustomTextView) findViewById(R.id.txt_school_name);
        txt_notice_title = (TextView) findViewById(R.id.txt_notice_title);
        web_notice = (WebView) findViewById(R.id.web_notice);
        pb_webview = (ProgressBar) findViewById(R.id.pb_webview);

        textClock = (TextClock) findViewById(R.id.textClock);
        textClock.setFormat12Hour(null);
        textClock.setFormat24Hour("yyyy��MM��dd��\nEEEE      HH : mm");

        txt_weather.setText(application.currentCity.trim() + "   "
                + application.temperature.trim() + "\n"
                + application.weather.trim());



        WebSettings webSettings = web_notice.getSettings();
        // ���û���ģʽ
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // // ����Ĭ�������С
        webSettings.setDefaultFontSize(35);
        // ����WebView����JavaScript��ִ��
        webSettings.setJavaScriptEnabled(true);
        // // �����Ƿ��ڸ���ģʽ����WebView����ҳ�棬 ��С���ʺ���Ļ������
        // // webSettings.setLoadWithOverviewMode(true);
        // // ������Ⱦ���ȼ����
        // webSettings.setRenderPriority(RenderPriority.HIGH);
        // ����WebView�Ƿ�Ӧ�����á�viewport�� HTML��ǩ��֧�ֻ���Ӧ��ʹ�ø�����ͼ��
        webSettings.setUseWideViewPort(true);
        // // ͼƬ���ط��������Ⱦ
        // webSettings.setBlockNetworkImage(true);
        // ֧���Զ�����ͼƬ
        webSettings.setLoadsImagesAutomatically(true);
        // �ര��
        webSettings.supportMultipleWindows();
        // // ����WebView�Ƿ�Ӧ��ʹ�������õķŴ����
        // webSettings.setBuiltInZoomControls(false);
        // // ����WebView�Ƿ�Ӧ����ʾ��Ļ���ſؼ�ʱ�� ʹ�����õķŴ����
        // webSettings.setDisplayZoomControls(false);
        // // ����WebView�Ƿ���Ҫ�û����Ʋ���ý��
        // webSettings.setMediaPlaybackRequiresUserGesture(true);
        // // ����WebView�Ƿ�Ӧ��֧������ʹ����Ļ�ϵı佹 ���ƺ�����
        // webSettings.setSupportZoom(true);
        // // ���� DOM storage API ����
        // webSettings.setDomStorageEnabled(true);
        // // ���� database storage API ����
        // webSettings.setDatabaseEnabled(true);
        // // ���� Application Caches ����
        // webSettings.setAppCacheEnabled(true);

        web_notice.setWebChromeClient(new WebChromeClient() {
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

        web_notice.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // view.loadUrl(url);
                Log.d(TAG, "shouldOverrideUrlLoading " + url);
                if (url.endsWith(".doc") || url.endsWith(".docx")
                        || url.endsWith(".xls") || url.endsWith(".xlsx")
                        || url.endsWith(".ppt") || url.endsWith(".pptx")
                        || url.endsWith(".pdf")) {
                    String[] s = url.split("/");
                    String name = s[s.length - 1];
                    String path = MyApplication.PATH_ROOT
                            + MyApplication.PATH_NOTICE + "/" + name;
                    downLoadoffice(url, path);
                    application.handler_touch.removeMessages(0);
                    return true;
                }
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                Log.d(TAG, "shouldInterceptRequest " + url);
                if (url.startsWith("ftp")) {
                    String[] s = url.split("/");
                    String path = MyApplication.PATH_ROOT
                            + MyApplication.PATH_NOTICE + "/" + s[s.length - 1];
                    File file = new File(path);
                    try {
                        if (file.exists()) {
                            InputStream is = new FileInputStream(path);
                            WebResourceResponse response = new WebResourceResponse(
                                    "image/png", "UTF-8", is);
                            return response;
                        } else {
                            FTPManager ftpManager = new FTPManager();
                            if (ftpManager.connect(url, path)) {
                                if (ftpManager.download()) {
                                    InputStream is = new FileInputStream(path);
                                    WebResourceResponse response = new WebResourceResponse(
                                            "image/png", "UTF-8", is);
                                    return response;
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (url.startsWith("//")) {
                    // ��ֹ��������ȱ��httpͷ
                    return super.shouldInterceptRequest(view, "http:" + url);
                }
                return super.shouldInterceptRequest(view, url);
            }
        });

        web_notice.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        btn_noticedetails_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                timer.cancel();
                // String from = getIntent().getStringExtra("from");
                // if (!TextUtils.isEmpty(from) &&
                // from.endsWith("MainActivity")) {
                // int type = getIntent().getIntExtra("type", 1);
                // Intent intent = new Intent(CXNoticeDetailsActivity.this,
                // PjNoticeListActivity.class);
                // intent.putExtra("type", type);
                // startActivity(intent);
                // }
            }
        });


    }



    private void downLoadoffice(final String urlStr, final String path) {
        if (TextUtils.isEmpty(urlStr)) {
            return;
        }
        final File file = new File(path);
        if (file.exists()) {
            openWPS(path);
            return;
        }
        handler.sendEmptyMessage(0);
        application.executoService.execute(new Runnable() {
            @Override
            public void run() {
                if (urlStr.startsWith("http")) {
                    if (application.httpDownLoad(urlStr, path)) {
                        handler.sendEmptyMessage(1);
                        openWPS(path);
                    } else {
                        handler.sendEmptyMessage(-1);
                        handler.sendEmptyMessage(1);
                    }
                } else if (urlStr.startsWith("ftp")) {
                    FTPManager ftpManager = new FTPManager();
                    if (ftpManager.connect(urlStr, path)) {
                        if (ftpManager.download()) {
                            handler.sendEmptyMessage(1);
                            openWPS(path);
                        } else {
                            handler.sendEmptyMessage(-1);
                            handler.sendEmptyMessage(1);
                        }
                        ftpManager.disConnect();
                    } else {
                        handler.sendEmptyMessage(-1);
                        handler.sendEmptyMessage(1);
                    }
                } else {
                    handler.sendEmptyMessage(-1);
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    private void openWPS(String path) {
        handler.sendEmptyMessage(2);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("OpenMode", "ReadOnly");
        bundle.putBoolean("SendCloseBroad", true);
        bundle.putString("ThirdPackage", "com.yy.doorplate");
        bundle.putBoolean("ClearBuffer", true);
        bundle.putBoolean("ClearTrace", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClassName(MyApplication.WPS,
                "cn.wps.moffice.documentmanager.PreStartActivity2");
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:{
                    //��ʾ
                    if (noticeInfoModel != null ) {
                        txt_notice_details_title.setText(noticeInfoModel.xxzt);
                        if (!TextUtils.isEmpty(noticeInfoModel.fbsj)) {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            txt_notice_details_time.setText(formatter.format(Long
                                    .parseLong(noticeInfoModel.fbsj)));
                        }
                        if (!TextUtils.isEmpty(noticeInfoModel.xxnr)) {
                            web_notice.loadDataWithBaseURL(null, noticeInfoModel.xxnr,
                                    "text/html", "utf-8", null);
                        }
                    }
                    break;
                }
                case -1:
                    application.showToast("����ʧ��");
                    break;
                case 0:
                    if (progressDialog == null) {
                        progressDialog = ProgressDialog.show(
                                CXNoticeDetailsBannerActivity.this, null, "������...",
                                false, false);
                    }
                    break;
                case 1:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case 2:
                    application.createFloating();
                    break;

                case 9:
                    finish();
                    break;

            }
        };
    };
}
