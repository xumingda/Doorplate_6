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
import com.yy.doorplate.model.StarModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class StarFragment extends Fragment implements OnClickListener {

	private final String TAG = "StarFragment";

	private MyApplication application;

	private RelativeLayout ly_fragment_star1, ly_fragment_star2,
			ly_fragment_star3, ly_fragment_star4;
	private ImageView img_fragment_star1, img_fragment_star2,
			img_fragment_star3, img_fragment_star4;
	private TextView txt_fragment_star1, txt_fragment_star2,
			txt_fragment_star3, txt_fragment_star4, txt_fragment_star11,
			txt_fragment_star22, txt_fragment_star33, txt_fragment_star44;

	private ImageLoader imageLoader = null;

	private List<StarModel> starModels = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cx_fragment_star, null);
		ly_fragment_star1 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_star1);
		ly_fragment_star2 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_star2);
		ly_fragment_star3 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_star3);
		ly_fragment_star4 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_star4);
		img_fragment_star1 = (ImageView) view
				.findViewById(R.id.img_fragment_star1);
		img_fragment_star2 = (ImageView) view
				.findViewById(R.id.img_fragment_star2);
		img_fragment_star3 = (ImageView) view
				.findViewById(R.id.img_fragment_star3);
		img_fragment_star4 = (ImageView) view
				.findViewById(R.id.img_fragment_star4);
		txt_fragment_star1 = (TextView) view
				.findViewById(R.id.txt_fragment_star1);
		txt_fragment_star2 = (TextView) view
				.findViewById(R.id.txt_fragment_star2);
		txt_fragment_star3 = (TextView) view
				.findViewById(R.id.txt_fragment_star3);
		txt_fragment_star4 = (TextView) view
				.findViewById(R.id.txt_fragment_star4);
		txt_fragment_star11 = (TextView) view
				.findViewById(R.id.txt_fragment_star11);
		txt_fragment_star22 = (TextView) view
				.findViewById(R.id.txt_fragment_star22);
		txt_fragment_star33 = (TextView) view
				.findViewById(R.id.txt_fragment_star33);
		txt_fragment_star44 = (TextView) view
				.findViewById(R.id.txt_fragment_star44);

		ly_fragment_star1.setOnClickListener(this);
		ly_fragment_star2.setOnClickListener(this);
		ly_fragment_star3.setOnClickListener(this);
		ly_fragment_star4.setOnClickListener(this);

		if (starModels != null) {
			if (starModels.size() > 0) {
				StarModel starModel = starModels.get(0);
				ly_fragment_star1.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(starModel.getStarPersonName)) {
					txt_fragment_star1.setText(starModel.starName);
				} else {
					txt_fragment_star1.setText(starModel.starName + ":"
							+ starModel.getStarPersonName);
				}
				txt_fragment_star11.setText(starModel.Info);
				if (!TextUtils.isEmpty(starModel.iconUrl)) {
					String[] s = starModel.iconUrl.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/star_"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_star1
												.setImageBitmap(bitmap);
									}
								}
							}, 356, 492);
				}
			}
			if (starModels.size() > 1) {
				StarModel starModel = starModels.get(1);
				ly_fragment_star2.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(starModel.getStarPersonName)) {
					txt_fragment_star2.setText(starModel.starName);
				} else {
					txt_fragment_star2.setText(starModel.starName + ":"
							+ starModel.getStarPersonName);
				}
				txt_fragment_star22.setText(starModel.Info);
				if (!TextUtils.isEmpty(starModel.iconUrl)) {
					String[] s = starModel.iconUrl.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/star_"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_star2
												.setImageBitmap(bitmap);
									}
								}
							}, 356, 492);
				}
			}
			if (starModels.size() > 2) {
				StarModel starModel = starModels.get(2);
				ly_fragment_star3.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(starModel.getStarPersonName)) {
					txt_fragment_star3.setText(starModel.starName);
				} else {
					txt_fragment_star3.setText(starModel.starName + ":"
							+ starModel.getStarPersonName);
				}
				txt_fragment_star33.setText(starModel.Info);
				if (!TextUtils.isEmpty(starModel.iconUrl)) {
					String[] s = starModel.iconUrl.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/star_"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_star3
												.setImageBitmap(bitmap);
									}
								}
							}, 356, 492);
				}
			}
			if (starModels.size() > 3) {
				StarModel starModel = starModels.get(3);
				ly_fragment_star4.setVisibility(View.VISIBLE);
				if (TextUtils.isEmpty(starModel.getStarPersonName)) {
					txt_fragment_star4.setText(starModel.starName);
				} else {
					txt_fragment_star4.setText(starModel.starName + ":"
							+ starModel.getStarPersonName);
				}
				txt_fragment_star44.setText(starModel.Info);
				if (!TextUtils.isEmpty(starModel.iconUrl)) {
					String[] s = starModel.iconUrl.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/star_"
							+ s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_star4
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
		case R.id.ly_fragment_star1:
			break;
		case R.id.ly_fragment_star2:
			break;
		case R.id.ly_fragment_star3:
			break;
		case R.id.ly_fragment_star4:
			break;
		}
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public List<StarModel> getStarModels() {
		return starModels;
	}

	public void setStarModels(List<StarModel> starModels) {
		this.starModels = starModels;
	}

}
