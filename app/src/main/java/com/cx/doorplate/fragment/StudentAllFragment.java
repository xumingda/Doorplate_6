package com.cx.doorplate.fragment;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class StudentAllFragment extends Fragment implements OnItemClickListener {

	private final String TAG = "StudentAllFragment";

	private MyApplication application;

	private GridView gv_student;

	private ImageLoader imageLoader = null;

	private List<StudentInfoModel> list = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cx_fragment_studentall, null);
		gv_student = (GridView) view.findViewById(R.id.gv_student);

		gv_student.setOnItemClickListener(this);

		if (list != null) {
			MyAdapter adapter = new MyAdapter();
			gv_student.setAdapter(adapter);
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		application.showStudentDialog(list.get(arg2));
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			private ImageView img_student_touxiang;
			private TextView txt_student_xm, txt_student_zhiwu;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(application).inflate(
						R.layout.cx_item_student, null);
				viewHolder.img_student_touxiang = (ImageView) view
						.findViewById(R.id.img_student_touxiang);
				viewHolder.txt_student_xm = (TextView) view
						.findViewById(R.id.txt_student_xm);
				viewHolder.txt_student_zhiwu = (TextView) view
						.findViewById(R.id.txt_student_zhiwu);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.txt_student_xm.setText(list.get(position).xm);
			viewHolder.txt_student_zhiwu.setText(list.get(position).xszw);
			if (!TextUtils.isEmpty(list.get(position).zp)) {
				String[] s = list.get(position).zp.split("\\.");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PICTURE + "/student_"
						+ list.get(position).id + "." + s[s.length - 1];
				viewHolder.img_student_touxiang.setTag(path);
				imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						ImageView mImageView = (ImageView) gv_student
								.findViewWithTag(url);
						if (mImageView != null && bitmap != null) {
							mImageView.setImageBitmap(bitmap);
						}
					}
				}, 180, 180);
			}
			return view;
		}

	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public List<StudentInfoModel> getList() {
		return list;
	}

	public void setList(List<StudentInfoModel> list) {
		this.list = list;
	}
}
