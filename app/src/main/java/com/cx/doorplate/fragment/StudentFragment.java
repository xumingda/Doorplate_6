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
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class StudentFragment extends Fragment implements OnClickListener {

	private final String TAG = "StudentFragment";

	private MyApplication application;

	private RelativeLayout ly_fragment_student1, ly_fragment_student2, ly_fragment_student3, ly_fragment_student4,
			ly_fragment_student5, ly_fragment_student6;
	private ImageView img_fragment_student1, img_fragment_student2, img_fragment_student3, img_fragment_student4,
			img_fragment_student5, img_fragment_student6;
	private TextView txt_fragment_student1, txt_fragment_student2, txt_fragment_student3, txt_fragment_student4,
			txt_fragment_student5, txt_fragment_student6, txt_fragment_student11, txt_fragment_student22,
			txt_fragment_student33, txt_fragment_student44, txt_fragment_student55, txt_fragment_student66;

	private ImageLoader imageLoader = null;

	private List<StudentInfoModel> list = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cx_fragment_student, null);
		ly_fragment_student1 = (RelativeLayout) view.findViewById(R.id.ly_fragment_student1);
		ly_fragment_student2 = (RelativeLayout) view.findViewById(R.id.ly_fragment_student2);
		ly_fragment_student3 = (RelativeLayout) view.findViewById(R.id.ly_fragment_student3);
		ly_fragment_student4 = (RelativeLayout) view.findViewById(R.id.ly_fragment_student4);
		ly_fragment_student5 = (RelativeLayout) view.findViewById(R.id.ly_fragment_student5);
		ly_fragment_student6 = (RelativeLayout) view.findViewById(R.id.ly_fragment_student6);

		img_fragment_student1 = (ImageView) view.findViewById(R.id.img_fragment_student1);
		img_fragment_student2 = (ImageView) view.findViewById(R.id.img_fragment_student2);
		img_fragment_student3 = (ImageView) view.findViewById(R.id.img_fragment_student3);
		img_fragment_student4 = (ImageView) view.findViewById(R.id.img_fragment_student4);
		img_fragment_student5 = (ImageView) view.findViewById(R.id.img_fragment_student5);
		img_fragment_student6 = (ImageView) view.findViewById(R.id.img_fragment_student6);

		txt_fragment_student1 = (TextView) view.findViewById(R.id.txt_fragment_student1);
		txt_fragment_student2 = (TextView) view.findViewById(R.id.txt_fragment_student2);
		txt_fragment_student3 = (TextView) view.findViewById(R.id.txt_fragment_student3);
		txt_fragment_student4 = (TextView) view.findViewById(R.id.txt_fragment_student4);
		txt_fragment_student5 = (TextView) view.findViewById(R.id.txt_fragment_student5);
		txt_fragment_student6 = (TextView) view.findViewById(R.id.txt_fragment_student6);

		txt_fragment_student11 = (TextView) view.findViewById(R.id.txt_fragment_student11);
		txt_fragment_student22 = (TextView) view.findViewById(R.id.txt_fragment_student22);
		txt_fragment_student33 = (TextView) view.findViewById(R.id.txt_fragment_student33);
		txt_fragment_student44 = (TextView) view.findViewById(R.id.txt_fragment_student44);
		txt_fragment_student55 = (TextView) view.findViewById(R.id.txt_fragment_student55);

		txt_fragment_student66 = (TextView) view.findViewById(R.id.txt_fragment_student66);

		ly_fragment_student1.setOnClickListener(this);
		ly_fragment_student2.setOnClickListener(this);
		ly_fragment_student3.setOnClickListener(this);
		ly_fragment_student4.setOnClickListener(this);
		ly_fragment_student5.setOnClickListener(this);
		ly_fragment_student6.setOnClickListener(this);
		if (list != null) {
			if (list.size() > 0) {
				ly_fragment_student1.setVisibility(View.VISIBLE);
				txt_fragment_student1.setText(list.get(0).xm);
				txt_fragment_student11.setText(list.get(0).xszw);
				if (!TextUtils.isEmpty(list.get(0).zp)) {
					String[] s = list.get(0).zp.split("\\.");
					String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/student_" + list.get(0).id
							+ "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (bitmap != null) {
								img_fragment_student1.setImageBitmap(bitmap);
							}
						}
					}, 180, 180);
				}
			}
			if (list.size() > 1) {
				ly_fragment_student2.setVisibility(View.VISIBLE);
				txt_fragment_student2.setText(list.get(1).xm);
				txt_fragment_student22.setText(list.get(1).xszw);
				if (!TextUtils.isEmpty(list.get(1).zp)) {
					String[] s = list.get(1).zp.split("\\.");
					String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/student_" + list.get(1).id
							+ "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (bitmap != null) {
								img_fragment_student2.setImageBitmap(bitmap);
							}
						}
					}, 180, 180);
				}
			}
			if (list.size() > 2) {
				ly_fragment_student3.setVisibility(View.VISIBLE);
				txt_fragment_student3.setText(list.get(2).xm);
				txt_fragment_student33.setText(list.get(2).xszw);
				if (!TextUtils.isEmpty(list.get(2).zp)) {
					String[] s = list.get(2).zp.split("\\.");
					String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/student_" + list.get(2).id
							+ "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (bitmap != null) {
								img_fragment_student3.setImageBitmap(bitmap);
							}
						}
					}, 180, 180);
				}
			}
			if (list.size() > 3) {
				ly_fragment_student4.setVisibility(View.VISIBLE);
				txt_fragment_student4.setText(list.get(3).xm);
				txt_fragment_student44.setText(list.get(3).xszw);
				if (!TextUtils.isEmpty(list.get(3).zp)) {
					String[] s = list.get(3).zp.split("\\.");
					String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/student_" + list.get(3).id
							+ "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (bitmap != null) {
								img_fragment_student4.setImageBitmap(bitmap);
							}
						}
					}, 180, 180);
				}
			}
			if (list.size() > 4) {
				ly_fragment_student5.setVisibility(View.VISIBLE);
				txt_fragment_student5.setText(list.get(4).xm);
				txt_fragment_student55.setText(list.get(4).xszw);
				if (!TextUtils.isEmpty(list.get(4).zp)) {
					String[] s = list.get(4).zp.split("\\.");
					String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/student_" + list.get(4).id
							+ "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							if (bitmap != null) {
								img_fragment_student5.setImageBitmap(bitmap);
							}
						}
					}, 180, 180);
				}
			}
		}

		if (list.size() > 5) {
			ly_fragment_student6.setVisibility(View.VISIBLE);
			txt_fragment_student6.setText(list.get(5).xm);
			txt_fragment_student66.setText(list.get(5).xszw);
			if (!TextUtils.isEmpty(list.get(5).zp)) {
				String[] s = list.get(5).zp.split("\\.");
				String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE + "/student_" + list.get(5).id + "."
						+ s[s.length - 1];
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							img_fragment_student6.setImageBitmap(bitmap);
						}
					}
				}, 180, 180);
			}
		}

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_fragment_student1:
			application.showStudentDialog(list.get(0));
			break;
		case R.id.ly_fragment_student2:
			application.showStudentDialog(list.get(1));
			break;
		case R.id.ly_fragment_student3:
			application.showStudentDialog(list.get(2));
			break;
		case R.id.ly_fragment_student4:
			application.showStudentDialog(list.get(3));
			break;
		case R.id.ly_fragment_student5:
			application.showStudentDialog(list.get(4));
			break;
		case R.id.ly_fragment_student6:
			application.showStudentDialog(list.get(5));
			break;
		}
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public List<StudentInfoModel> getStudentInfoModels() {
		return list;
	}

	public void setStudentInfoModels(List<StudentInfoModel> list) {
		this.list = list;
	}

}
