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

public class SchoolHonorView extends AppCodeView implements OnClickListener {

	private ImageLoader imageLoader;

	private RelativeLayout ly_schoolnew_honor_;
	private Button btn_schoolnew_left, btn_schoolnew_right;
	private TextView txt_schoolnew_honor1, txt_schoolnew_honor2,
			txt_schoolnew_nohonor;
	private ImageView img_schoolnew_honor;

	private List<PrizeInfoModel> prizeInfoModels = null;
	private int prize = 0;

	public SchoolHonorView(Context context, MyApplication application,
			ImageLoader imageLoader, String nextPath) {
		super(context, application, nextPath);
		this.imageLoader = imageLoader;
		initView();
		updata_honor();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_school_honor, this);
		ly_schoolnew_honor_ = (RelativeLayout) findViewById(R.id.ly_schoolnew_honor_);
		btn_schoolnew_left = (Button) findViewById(R.id.btn_schoolnew_left);
		btn_schoolnew_right = (Button) findViewById(R.id.btn_schoolnew_right);
		txt_schoolnew_honor1 = (TextView) findViewById(R.id.txt_schoolnew_honor1);
		txt_schoolnew_honor2 = (TextView) findViewById(R.id.txt_schoolnew_honor2);
		txt_schoolnew_nohonor = (TextView) findViewById(R.id.txt_schoolnew_nohonor);
		img_schoolnew_honor = (ImageView) findViewById(R.id.img_schoolnew_honor);

		ly_schoolnew_honor_.setOnClickListener(this);
		btn_schoolnew_left.setOnClickListener(this);
		btn_schoolnew_right.setOnClickListener(this);
	}

	private void updata_honor() {
		prize = 0;
		PrizeInfoDatabase database = new PrizeInfoDatabase();
		prizeInfoModels = database.query_by_prizeType("SCHOOL");
		if (prizeInfoModels != null && prizeInfoModels.size() > 0) {
			ly_schoolnew_honor_.setVisibility(View.VISIBLE);
			txt_schoolnew_nohonor.setVisibility(View.INVISIBLE);
			if (prizeInfoModels.size() > 1) {
				btn_schoolnew_left.setVisibility(View.VISIBLE);
				btn_schoolnew_right.setVisibility(View.VISIBLE);
			}
			txt_schoolnew_honor1.setText(prizeInfoModels.get(0).ranking);
			txt_schoolnew_honor2.setText(prizeInfoModels.get(0).prizeName);
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
									img_schoolnew_honor.setImageBitmap(bitmap);
								}
							}, 160, 160);
				}
			} else {
				img_schoolnew_honor.setImageBitmap(null);
			}
		} else {
			ly_schoolnew_honor_.setVisibility(View.INVISIBLE);
			btn_schoolnew_left.setVisibility(View.INVISIBLE);
			btn_schoolnew_right.setVisibility(View.INVISIBLE);
			txt_schoolnew_nohonor.setVisibility(View.VISIBLE);
		}
	}

	private void updata_honor(int i) {
		if (prizeInfoModels != null && prizeInfoModels.size() > i) {
			txt_schoolnew_honor1.setText(prizeInfoModels.get(i).ranking);
			txt_schoolnew_honor2.setText(prizeInfoModels.get(i).awardsUnit);
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
									img_schoolnew_honor.setImageBitmap(bitmap);
								}
							}, 160, 160);
				} else {
					img_schoolnew_honor.setImageBitmap(null);
				}
			} else {
				img_schoolnew_honor.setImageBitmap(null);
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
		case R.id.ly_schoolnew_honor_: {
			Intent intent = new Intent(context, HonorActivity.class);
			intent.putExtra("prize", prize);
			intent.putExtra("prizeType", "SCHOOL");
			intent.putExtra("prizeCode", prizeInfoModels.get(prize).prizeCode);
			context.startActivity(intent);
			break;
		}
		case R.id.btn_schoolnew_left:
			if (prize == 0) {
				prize = prizeInfoModels.size() - 1;
				updata_honor(prize);
			} else {
				updata_honor(--prize);
			}
			break;
		case R.id.btn_schoolnew_right:
			if (prize == prizeInfoModels.size() - 1) {
				prize = 0;
				updata_honor(prize);
			} else {
				updata_honor(++prize);
			}
			break;
		}
	}

}
