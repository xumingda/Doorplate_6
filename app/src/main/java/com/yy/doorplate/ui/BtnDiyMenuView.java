package com.yy.doorplate.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.advertisement.activity.ActivityAppcodeo;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.WebviewActivity;
import com.yy.doorplate.database.DiyMenuDatabase;
import com.yy.doorplate.model.DiyMenuModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class BtnDiyMenuView extends AppCodeView {

	private Button button;

	private int rescode, w, h;

	private ImageLoader imageLoader;

	private DiyMenuModel diyMenuModel;
	private DiyMenuDatabase database = new DiyMenuDatabase();
	private List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();

	public BtnDiyMenuView(Context context, MyApplication application,
			ImageLoader imageLoader, String nextPath, int rescode, int w, int h) {
		super(context, application, nextPath);
		this.imageLoader = imageLoader;
		this.rescode = rescode % 2000;
		this.w = w;
		this.h = h;
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_btn_diymenu, this);
		button = (Button) findViewById(R.id.btn_diymenu);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!TextUtils.isEmpty(nextPath)) {
					Intent intent = new Intent(context, ActivityAppcodeo.class);
					intent.putExtra("ThemePolicyPath", nextPath);
					context.startActivity(intent);
				} else {
					open(diyMenuModel);
				}
			}
		});

		List<DiyMenuModel> diyMenuModels = database.query("parmentCode = ?",
				new String[] { "-1" });
		if (diyMenuModels != null && diyMenuModels.size() > rescode) {
			diyMenuModel = diyMenuModels.get(rescode);
			if (!TextUtils.isEmpty(diyMenuModel.iconAddress)) {
				String[] s = diyMenuModel.iconAddress.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/diyMenuIcon_"
						+ s[s.length - 1];
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							button.setBackground(new BitmapDrawable(bitmap));
						}
					}
				});
			}
		}
	}

	private void open(DiyMenuModel model) {
		if (model == null) {
			return;
		}
		Log.i("BtnDiyMenuView", model.toString());
		List<DiyMenuModel> diyMenuModels = database.query("parmentCode = ?",
				new String[] { model.itemCode });
		if (diyMenuModels != null && diyMenuModels.size() > 0) {
			showDialog(diyMenuModels);
		} else if (!TextUtils.isEmpty(model.valueAddress)) {
			Intent intent = new Intent(context, WebviewActivity.class);
			intent.putExtra("title", model.name);
			intent.putExtra("url", model.valueAddress);
			context.startActivity(intent);
			if (application.dialog != null && application.dialog.isShowing()) {
				application.dialog.dismiss();
			}
		}
	}

	private void showDialog(final List<DiyMenuModel> diyMenuModels) {
		maps.clear();
		if (application.dialog != null && application.dialog.isShowing()) {
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(application);
			application.dialog = builder.create();
		}
		application.dialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		application.dialog.setCanceledOnTouchOutside(true);
		application.dialog.show();
		application.dialog.getWindow().setLayout(
				w * diyMenuModels.size() + 20 * diyMenuModels.size(), h);
		application.dialog.getWindow().setContentView(R.layout.dialog_diymenu);
		LinearLayout ly_dialog_diymenu = (LinearLayout) application.dialog
				.findViewById(R.id.ly_dialog_diymenu);
		ly_dialog_diymenu.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
		for (int i = 0; i < diyMenuModels.size(); i++) {
			Button button = new Button(application);
			button.setTag(i);
			if (!TextUtils.isEmpty(diyMenuModels.get(i).iconAddress)) {
				String[] s = diyMenuModels.get(i).iconAddress.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/diyMenuIcon_"
						+ s[s.length - 1];
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("path", path);
				map.put("button", button);
				maps.add(map);
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							for (Map<String, Object> map : maps) {
								if (map.get("path".toString()).equals(url)) {
									Button button = (Button) map.get("button");
									button.setBackground(new BitmapDrawable(
											bitmap));
								}
							}
						}
					}
				});
			}
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int i = (Integer) v.getTag();
					open(diyMenuModels.get(i));
				}
			});
			if (i > 0) {
				params.leftMargin = 20;
			}
			ly_dialog_diymenu.addView(button, params);
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
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

}
