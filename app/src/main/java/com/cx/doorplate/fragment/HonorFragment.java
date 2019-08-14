package com.cx.doorplate.fragment;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class HonorFragment extends Fragment implements OnClickListener {

	private final String TAG = "HonorFragment";

	private MyApplication application;

	private RelativeLayout ly_fragment_honor1, ly_fragment_honor2,
			ly_fragment_honor3, ly_fragment_honor4;
	private ImageView img_fragment_honor1, img_fragment_honor2,
			img_fragment_honor3, img_fragment_honor4;
	private TextView txt_fragment_honor1, txt_fragment_honor2,
			txt_fragment_honor3, txt_fragment_honor4, txt_fragment_honor11,
			txt_fragment_honor22, txt_fragment_honor33, txt_fragment_honor44,
			txt_fragment_honor111, txt_fragment_honor222,
			txt_fragment_honor333, txt_fragment_honor444;

	private ImageLoader imageLoader = null;

	private List<PrizeInfoModel> prizeInfoModels = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cx_fragment_honor, null);
		ly_fragment_honor1 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_honor1);
		ly_fragment_honor2 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_honor2);
		ly_fragment_honor3 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_honor3);
		ly_fragment_honor4 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_honor4);
		img_fragment_honor1 = (ImageView) view
				.findViewById(R.id.img_fragment_honor1);
		img_fragment_honor2 = (ImageView) view
				.findViewById(R.id.img_fragment_honor2);
		img_fragment_honor3 = (ImageView) view
				.findViewById(R.id.img_fragment_honor3);
		img_fragment_honor4 = (ImageView) view
				.findViewById(R.id.img_fragment_honor4);
		txt_fragment_honor1 = (TextView) view
				.findViewById(R.id.txt_fragment_honor1);
		txt_fragment_honor2 = (TextView) view
				.findViewById(R.id.txt_fragment_honor2);
		txt_fragment_honor3 = (TextView) view
				.findViewById(R.id.txt_fragment_honor3);
		txt_fragment_honor4 = (TextView) view
				.findViewById(R.id.txt_fragment_honor4);
		txt_fragment_honor11 = (TextView) view
				.findViewById(R.id.txt_fragment_honor11);
		txt_fragment_honor22 = (TextView) view
				.findViewById(R.id.txt_fragment_honor22);
		txt_fragment_honor33 = (TextView) view
				.findViewById(R.id.txt_fragment_honor33);
		txt_fragment_honor44 = (TextView) view
				.findViewById(R.id.txt_fragment_honor44);
		txt_fragment_honor111 = (TextView) view
				.findViewById(R.id.txt_fragment_honor111);
		txt_fragment_honor222 = (TextView) view
				.findViewById(R.id.txt_fragment_honor222);
		txt_fragment_honor333 = (TextView) view
				.findViewById(R.id.txt_fragment_honor333);
		txt_fragment_honor444 = (TextView) view
				.findViewById(R.id.txt_fragment_honor444);

		ly_fragment_honor1.setOnClickListener(this);
		ly_fragment_honor2.setOnClickListener(this);
		ly_fragment_honor3.setOnClickListener(this);
		ly_fragment_honor4.setOnClickListener(this);

		if (prizeInfoModels != null) {
			if (prizeInfoModels.size() > 0) {
				PrizeInfoModel prizeInfoModel = prizeInfoModels.get(0);
				ly_fragment_honor1.setVisibility(View.VISIBLE);
				txt_fragment_honor1.setText(prizeInfoModel.prizeName);
				txt_fragment_honor11.setText("获奖人员 : "
						+ prizeInfoModel.prizePerson);
				txt_fragment_honor111.setText("获奖日期 : "
						+ prizeInfoModel.prizeDate);
				if (!TextUtils.isEmpty(prizeInfoModel.iconUrl)) {
					String[] s = prizeInfoModel.iconUrl.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/prizeIcon_"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_honor1
												.setImageBitmap(bitmap);
									}
								}
							}, 356, 492);
				}
			}
			if (prizeInfoModels.size() > 1) {
				PrizeInfoModel prizeInfoModel = prizeInfoModels.get(1);
				ly_fragment_honor2.setVisibility(View.VISIBLE);
				txt_fragment_honor2.setText(prizeInfoModel.prizeName);
				txt_fragment_honor22.setText("获奖人员 : "
						+ prizeInfoModel.prizePerson);
				txt_fragment_honor222.setText("获奖日期 : "
						+ prizeInfoModel.prizeDate);
				if (!TextUtils.isEmpty(prizeInfoModel.iconUrl)) {
					String[] s = prizeInfoModel.iconUrl.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/prizeIcon_"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_honor2
												.setImageBitmap(bitmap);
									}
								}
							}, 356, 492);
				}
			}
			if (prizeInfoModels.size() > 2) {
				PrizeInfoModel prizeInfoModel = prizeInfoModels.get(2);
				ly_fragment_honor3.setVisibility(View.VISIBLE);
				txt_fragment_honor3.setText(prizeInfoModel.prizeName);
				txt_fragment_honor33.setText("获奖人员 : "
						+ prizeInfoModel.prizePerson);
				txt_fragment_honor333.setText("获奖日期 : "
						+ prizeInfoModel.prizeDate);
				if (!TextUtils.isEmpty(prizeInfoModel.iconUrl)) {
					String[] s = prizeInfoModel.iconUrl.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/prizeIcon_"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_honor3
												.setImageBitmap(bitmap);
									}
								}
							}, 356, 492);
				}
			}
			if (prizeInfoModels.size() > 3) {
				PrizeInfoModel prizeInfoModel = prizeInfoModels.get(3);
				ly_fragment_honor4.setVisibility(View.VISIBLE);
				txt_fragment_honor4.setText(prizeInfoModel.prizeName);
				txt_fragment_honor44.setText("获奖人员 : "
						+ prizeInfoModel.prizePerson);
				txt_fragment_honor444.setText("获奖日期 : "
						+ prizeInfoModel.prizeDate);
				if (!TextUtils.isEmpty(prizeInfoModel.iconUrl)) {
					String[] s = prizeInfoModel.iconUrl.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/prizeIcon_"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_honor4
												.setImageBitmap(bitmap);
									}
								}
							}, 356, 492);
				}
			}
		}

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_fragment_honor1:
			break;
		case R.id.ly_fragment_honor2:
			break;
		case R.id.ly_fragment_honor3:
			break;
		case R.id.ly_fragment_honor4:
			break;
		}
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public List<PrizeInfoModel> getPrizeInfoModels() {
		return prizeInfoModels;
	}

	public void setPrizeInfoModels(List<PrizeInfoModel> prizeInfoModels) {
		this.prizeInfoModels = prizeInfoModels;
	}

}
