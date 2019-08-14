package com.yy.doorplate.ui;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.HonorActivity;
import com.yy.doorplate.database.PrizeInfoDatabase;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class ClassHonorView extends AppCodeView implements OnClickListener {

	private ImageLoader imageLoader;

	private RelativeLayout ly_classnew_honor_;
	private Button btn_classnew_left, btn_classnew_right;
	private TextView txt_classnew_honor1, txt_classnew_honor2,
			txt_classnew_nohonor;
	private ImageView img_classnew_honor;

	private List<PrizeInfoModel> prizeInfoModels = null;
	private int prize = 0;

	public ClassHonorView(Context context, MyApplication application,
			ImageLoader imageLoader, String nextPath) {
		super(context, application, nextPath);
		this.imageLoader = imageLoader;
		initView();
		updata_honor();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_class_honor, this);
		ly_classnew_honor_ = (RelativeLayout) findViewById(R.id.ly_classnew_honor_);
		btn_classnew_left = (Button) findViewById(R.id.btn_classnew_left);
		btn_classnew_right = (Button) findViewById(R.id.btn_classnew_right);
		txt_classnew_honor1 = (TextView) findViewById(R.id.txt_classnew_honor1);
		txt_classnew_honor2 = (TextView) findViewById(R.id.txt_classnew_honor2);
		txt_classnew_nohonor = (TextView) findViewById(R.id.txt_classnew_nohonor);
		img_classnew_honor = (ImageView) findViewById(R.id.img_classnew_honor);

		ly_classnew_honor_.setOnClickListener(this);
		btn_classnew_left.setOnClickListener(this);
		btn_classnew_right.setOnClickListener(this);
	}

	private void updata_honor() {
		prize = 0;
		PrizeInfoDatabase database = new PrizeInfoDatabase();
		prizeInfoModels = database.query_by_prizeType("CLASS");
		if (prizeInfoModels != null && prizeInfoModels.size() > 0) {
			ly_classnew_honor_.setVisibility(View.VISIBLE);
			txt_classnew_nohonor.setVisibility(View.INVISIBLE);
			if (prizeInfoModels.size() > 1) {
				btn_classnew_left.setVisibility(View.VISIBLE);
				btn_classnew_right.setVisibility(View.VISIBLE);
			}
			txt_classnew_honor1.setText(prizeInfoModels.get(0).ranking);
			txt_classnew_honor2.setText(prizeInfoModels.get(0).prizeName);
			if (!TextUtils.isEmpty(prizeInfoModels.get(0).iconUrl)) {
				String[] s = prizeInfoModels.get(0).iconUrl.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/prizeIcon_"
						+ s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									img_classnew_honor.setImageBitmap(bitmap);
								}
							}, 160, 160);
				}
			} else {
				img_classnew_honor.setImageBitmap(null);
			}
		} else {
			ly_classnew_honor_.setVisibility(View.INVISIBLE);
			btn_classnew_left.setVisibility(View.INVISIBLE);
			btn_classnew_right.setVisibility(View.INVISIBLE);
			txt_classnew_nohonor.setVisibility(View.VISIBLE);
		}
	}

	private void updata_honor(int i) {
		if (prizeInfoModels != null && prizeInfoModels.size() > i) {
			txt_classnew_honor1.setText(prizeInfoModels.get(i).ranking);
			txt_classnew_honor2.setText(prizeInfoModels.get(i).awardsUnit);
			if (!TextUtils.isEmpty(prizeInfoModels.get(i).iconUrl)) {
				String[] s = prizeInfoModels.get(i).iconUrl.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/prizeIcon_"
						+ s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									img_classnew_honor.setImageBitmap(bitmap);
								}
							}, 160, 160);
				} else {
					img_classnew_honor.setImageBitmap(null);
				}
			} else {
				img_classnew_honor.setImageBitmap(null);
			}
		}
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_HONOR || type == APPCODE_UPDATA_EQU) {
			updata_honor();
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
		case R.id.btn_classnew_left:
			if (prize == 0) {
				prize = prizeInfoModels.size() - 1;
				updata_honor(prize);
			} else {
				updata_honor(--prize);
			}
			break;
		case R.id.btn_classnew_right:
			if (prize == prizeInfoModels.size() - 1) {
				prize = 0;
				updata_honor(prize);
			} else {
				updata_honor(++prize);
			}
			break;
		case R.id.ly_classnew_honor_: {
			Intent intent = new Intent(context, HonorActivity.class);
			intent.putExtra("prize", prize);
			intent.putExtra("prizeType", "CLASS");
			intent.putExtra("prizeCode", prizeInfoModels.get(prize).prizeCode);
			context.startActivity(intent);
			break;
		}
		}

	}

}
