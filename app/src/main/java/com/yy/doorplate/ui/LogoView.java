package com.yy.doorplate.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class LogoView extends AppCodeView {

	private ImageView img_logo;

	private ImageLoader imageLoader;

	public LogoView(Context context, MyApplication application,
			ImageLoader imageLoader, String nextPath) {
		super(context, application, nextPath);
		this.imageLoader = imageLoader;
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_logo, this);
		img_logo = (ImageView) findViewById(R.id.img_logo);
	}

	public void setLogo(String path) {
		if (TextUtils.isEmpty(application.logoUrl)) {
			nextPath = application.getFtpPath(path);
			if (TextUtils.isEmpty(nextPath)) {
				return;
			}
			imageLoader.getBitmapFormUrl(nextPath, new OnImageLoaderListener() {
				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if (bitmap != null) {
						img_logo.setImageBitmap(bitmap);
					} else {
						img_logo.setImageResource(R.drawable.logo11);
					}
				}
			});
		} else {
			imageLoader.getBitmapFormUrl(application.logoUrl,
					new OnImageLoaderListener() {
						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (bitmap != null) {
								img_logo.setImageBitmap(bitmap);
							} else {
								img_logo.setImageResource(R.drawable.logo11);
							}
						}
					});
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_EQU) {
			setLogo(null);
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
}
