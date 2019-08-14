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
import com.yy.doorplate.model.TeacherInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class TeacherFragment extends Fragment implements OnClickListener {

	private final String TAG = "TeacherFragment";

	private MyApplication application;

	private RelativeLayout ly_fragment_teacher1, ly_fragment_teacher2,
			ly_fragment_teacher3, ly_fragment_teacher4, ly_fragment_teacher5;
	private ImageView img_fragment_teacher1, img_fragment_teacher2,
			img_fragment_teacher3, img_fragment_teacher4,
			img_fragment_teacher5;
	private TextView txt_fragment_teacher1, txt_fragment_teacher2,
			txt_fragment_teacher3, txt_fragment_teacher4,
			txt_fragment_teacher5, txt_fragment_teacher11,
			txt_fragment_teacher22, txt_fragment_teacher33,
			txt_fragment_teacher44, txt_fragment_teacher55;

	private ImageLoader imageLoader = null;

	private List<TeacherInfoModel> list = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cx_fragment_teacher, null);
		ly_fragment_teacher1 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_teacher1);
		ly_fragment_teacher2 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_teacher2);
		ly_fragment_teacher3 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_teacher3);
		ly_fragment_teacher4 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_teacher4);
		ly_fragment_teacher5 = (RelativeLayout) view
				.findViewById(R.id.ly_fragment_teacher5);
		img_fragment_teacher1 = (ImageView) view
				.findViewById(R.id.img_fragment_teacher1);
		img_fragment_teacher2 = (ImageView) view
				.findViewById(R.id.img_fragment_teacher2);
		img_fragment_teacher3 = (ImageView) view
				.findViewById(R.id.img_fragment_teacher3);
		img_fragment_teacher4 = (ImageView) view
				.findViewById(R.id.img_fragment_teacher4);
		img_fragment_teacher5 = (ImageView) view
				.findViewById(R.id.img_fragment_teacher5);
		txt_fragment_teacher1 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher1);
		txt_fragment_teacher2 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher2);
		txt_fragment_teacher3 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher3);
		txt_fragment_teacher4 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher4);
		txt_fragment_teacher5 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher5);
		txt_fragment_teacher11 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher11);
		txt_fragment_teacher22 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher22);
		txt_fragment_teacher33 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher33);
		txt_fragment_teacher44 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher44);
		txt_fragment_teacher55 = (TextView) view
				.findViewById(R.id.txt_fragment_teacher55);

		ly_fragment_teacher1.setOnClickListener(this);
		ly_fragment_teacher2.setOnClickListener(this);
		ly_fragment_teacher3.setOnClickListener(this);
		ly_fragment_teacher4.setOnClickListener(this);
		ly_fragment_teacher5.setOnClickListener(this);

		if (list != null) {
			if (list.size() > 0) {
				ly_fragment_teacher1.setVisibility(View.VISIBLE);
				txt_fragment_teacher1.setText(list.get(0).xm);
				String jgzc = "";
				if (!TextUtils.isEmpty(list.get(0).jgzc)) {
					jgzc = list.get(0).jgzc;
				}
				txt_fragment_teacher11.setText(jgzc);
				if (!TextUtils.isEmpty(list.get(0).zp)) {
					String[] s = list.get(0).zp.split("\\.");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/teacher_"
							+ list.get(0).rybh + "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_teacher1
												.setImageBitmap(bitmap);
									}
								}
							}, 180, 180);
				}
			}
			if (list.size() > 1) {
				ly_fragment_teacher2.setVisibility(View.VISIBLE);
				txt_fragment_teacher2.setText(list.get(1).xm);
				String jgzc = "";
				if (!TextUtils.isEmpty(list.get(1).jgzc)) {
					jgzc = list.get(1).jgzc;
				}
				txt_fragment_teacher22.setText(jgzc);
				if (!TextUtils.isEmpty(list.get(1).zp)) {
					String[] s = list.get(1).zp.split("\\.");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/teacher_"
							+ list.get(1).rybh + "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_teacher2
												.setImageBitmap(bitmap);
									}
								}
							}, 180, 180);
				}
			}
			if (list.size() > 2) {
				ly_fragment_teacher3.setVisibility(View.VISIBLE);
				txt_fragment_teacher3.setText(list.get(2).xm);
				String jgzc = "";
				if (!TextUtils.isEmpty(list.get(2).jgzc)) {
					jgzc = list.get(2).jgzc;
				}
				txt_fragment_teacher33.setText(jgzc);
				if (!TextUtils.isEmpty(list.get(2).zp)) {
					String[] s = list.get(2).zp.split("\\.");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/teacher_"
							+ list.get(2).rybh + "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_teacher3
												.setImageBitmap(bitmap);
									}
								}
							}, 180, 180);
				}
			}
			if (list.size() > 3) {
				ly_fragment_teacher4.setVisibility(View.VISIBLE);
				txt_fragment_teacher4.setText(list.get(3).xm);
				String jgzc = "";
				if (!TextUtils.isEmpty(list.get(3).jgzc)) {
					jgzc = list.get(3).jgzc;
				}
				txt_fragment_teacher44.setText(jgzc);
				if (!TextUtils.isEmpty(list.get(3).zp)) {
					String[] s = list.get(3).zp.split("\\.");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/teacher_"
							+ list.get(3).rybh + "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_teacher4
												.setImageBitmap(bitmap);
									}
								}
							}, 180, 180);
				}
			}
			if (list.size() > 4) {
				ly_fragment_teacher5.setVisibility(View.VISIBLE);
				txt_fragment_teacher5.setText(list.get(4).xm);
				String jgzc = "";
				if (!TextUtils.isEmpty(list.get(4).jgzc)) {
					jgzc = list.get(4).jgzc;
				}
				txt_fragment_teacher55.setText(jgzc);
				if (!TextUtils.isEmpty(list.get(4).zp)) {
					String[] s = list.get(4).zp.split("\\.");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/teacher_"
							+ list.get(4).rybh + "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_fragment_teacher5
												.setImageBitmap(bitmap);
									}
								}
							}, 180, 180);
				}
			}
		}

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_fragment_teacher1:
			application.showTeacherDialog(list.get(0));
			break;
		case R.id.ly_fragment_teacher2:
			application.showTeacherDialog(list.get(1));
			break;
		case R.id.ly_fragment_teacher3:
			application.showTeacherDialog(list.get(2));
			break;
		case R.id.ly_fragment_teacher4:
			application.showTeacherDialog(list.get(3));
			break;
		case R.id.ly_fragment_teacher5:
			application.showTeacherDialog(list.get(4));
			break;
		}
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public List<TeacherInfoModel> getList() {
		return list;
	}

	public void setList(List<TeacherInfoModel> list) {
		this.list = list;
	}

}
