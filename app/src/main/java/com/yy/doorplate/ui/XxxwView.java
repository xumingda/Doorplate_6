package com.yy.doorplate.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.NoticeDetailsActivity;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.view.CustomTextView;

public class XxxwView extends AppCodeView implements OnClickListener,
		OnItemClickListener {

	private ImageLoader imageLoader;

	private RelativeLayout ly_schoolnew_new_;
	private TextView txt_schoolnew_new_rl, txt_schoolnew_new_time;
	private CustomTextView txt_schoolnew_new_title;
	private ImageView img_class_new;
	private ListView list_schoolnew_new;

	private List<NoticeInfoModel> noticeInfoModels = null;
	private String new_rl_1, new_url_1, new_path_1;

	public XxxwView(Context context, MyApplication application,
			ImageLoader imageLoader, String nextPath) {
		super(context, application, nextPath);
		this.imageLoader = imageLoader;
		initView();
		updata_news();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_xxxw, this);
		ly_schoolnew_new_ = (RelativeLayout) findViewById(R.id.ly_schoolnew_new_);
		txt_schoolnew_new_rl = (TextView) findViewById(R.id.txt_schoolnew_new_rl);
		txt_schoolnew_new_time = (TextView) findViewById(R.id.txt_schoolnew_new_time);
		txt_schoolnew_new_title = (CustomTextView) findViewById(R.id.txt_schoolnew_new_title);
		list_schoolnew_new = (ListView) findViewById(R.id.list_schoolnew_new);
		img_class_new = (ImageView) findViewById(R.id.img_class_new);

		ly_schoolnew_new_.setOnClickListener(this);
		list_schoolnew_new.setOnItemClickListener(this);
	}

	private void updata_news() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		noticeInfoModels = database.query_by_lmdm("bpxxxw", 0, 6);
		if (noticeInfoModels != null && noticeInfoModels.size() > 0) {
			ly_schoolnew_new_.setVisibility(View.VISIBLE);
			txt_schoolnew_new_title.setText(noticeInfoModels.get(0).xxzt);
			txt_schoolnew_new_time.setText(formatter.format(Long
					.parseLong(noticeInfoModels.get(0).fbsj)));
			txt_schoolnew_new_rl.setText("");
			if (!TextUtils.isEmpty(noticeInfoModels.get(0).xxnr)) {
				Document document = Jsoup.parse(noticeInfoModels.get(0).xxnr);
				Element element = document.select("img").first();
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					new_rl_1 = "";
					for (Element e : p) {
						if (TextUtils.isEmpty(new_rl_1)) {
							new_rl_1 = e.text();
						} else {
							new_rl_1 = new_rl_1 + "\n" + e.text();
						}
						if (!TextUtils.isEmpty(new_rl_1)
								&& new_rl_1.length() > 100) {
							break;
						}
					}
					txt_schoolnew_new_rl.setText(new_rl_1);
				}
				if (element != null) {
					new_url_1 = element.attr("src");
					if (!TextUtils.isEmpty(new_url_1)) {
						String[] s = new_url_1.split("/");
						new_path_1 = MyApplication.PATH_ROOT
								+ MyApplication.PATH_NOTICE + "/"
								+ s[s.length - 1];
						File file = new File(new_path_1);
						if (file.exists()) {
							imageLoader.getBitmapFormUrl(new_path_1,
									new OnImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap, String url) {
											if (bitmap != null) {
												Message msg = Message.obtain();
												msg.what = 1;
												msg.obj = bitmap;
												handler.sendMessage(msg);
											} else {
												handler.sendEmptyMessage(1);
											}
										}
									}, 230, 200);
						} else {
							downLoadImg(new_url_1, new_path_1);
						}
					} else {
						handler.sendEmptyMessage(1);
					}
				} else {
					handler.sendEmptyMessage(1);
				}
			}
			if (noticeInfoModels.size() > 1) {
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				for (int i = 1; i < noticeInfoModels.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("title", noticeInfoModels.get(i).xxzt);
					map.put("time", formatter.format(Long
							.parseLong(noticeInfoModels.get(i).fbsj)));
					list.add(map);
				}
				list_schoolnew_new.setAdapter(new SimpleAdapter(application,
						list, R.layout.item_schoolnew, new String[] { "title",
								"time" }, new int[] {
								R.id.txt_item_schoolnew_title,
								R.id.txt_item_schoolnew_time }));
			} else {
				list_schoolnew_new.setAdapter(null);
			}
		} else {
			ly_schoolnew_new_.setVisibility(View.INVISIBLE);
			list_schoolnew_new.setAdapter(null);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					img_class_new.setVisibility(View.VISIBLE);
					img_class_new.setImageBitmap(bitmap);
					android.view.ViewGroup.LayoutParams params = txt_schoolnew_new_title
							.getLayoutParams();
					params.width = 250;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_title.setLayoutParams(params);
					params = txt_schoolnew_new_rl.getLayoutParams();
					params.width = 250;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_rl.setLayoutParams(params);
					params = txt_schoolnew_new_time.getLayoutParams();
					params.width = 250;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_time.setLayoutParams(params);
				} else {
					img_class_new.setVisibility(View.INVISIBLE);
					android.view.ViewGroup.LayoutParams params = txt_schoolnew_new_title
							.getLayoutParams();
					params.width = 500;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_title.setLayoutParams(params);
					params = txt_schoolnew_new_rl.getLayoutParams();
					params.width = 500;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_rl.setLayoutParams(params);
					params = txt_schoolnew_new_time.getLayoutParams();
					params.width = 500;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_schoolnew_new_time.setLayoutParams(params);
				}
				break;
			}
			}
		}
	};

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_NOTIC || type == APPCODE_UPDATA_EQU) {
			updata_news();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ly_schoolnew_new_: {
			Intent intent = new Intent(context, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校新闻");
			intent.putExtra("id", noticeInfoModels.get(0).id);
			context.startActivity(intent);
			break;
		}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, NoticeDetailsActivity.class);
		intent.putExtra("top", "学校新闻");
		intent.putExtra("id", noticeInfoModels.get(arg2 + 1).id);
		context.startActivity(intent);
	}

	private void downLoadImg(final String urlStr, final String path) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			return;
		}
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							handler.sendEmptyMessage(6);
						}
						ftpManager.disConnect();
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						handler.sendEmptyMessage(6);
					}
				}
			}
		});
	}

}
