package com.cx.doorplate.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cx.doorplate.activity.CXTeacherActivity;
import com.cx.doorplate.fragment.BookFragment;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.MyViewPagerAdapter;
import com.yy.doorplate.database.BooksInfoDatabase;
import com.yy.doorplate.database.TeacherInfoDatabase;
import com.yy.doorplate.model.BooksInfoModel;
import com.yy.doorplate.model.TeacherInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.ui.AppCodeView;
import com.yy.doorplate.view.CustomViewpager;

public class CXMainBookView extends AppCodeView implements OnClickListener {

	private FragmentActivity fragmentActivity;
	private ImageLoader imageLoader;

	private RelativeLayout ly_cxmain_book, ly_cxmain_teacher;
	private Button btn_book_left, btn_book_right, btn_main_teacher_more;
	private TextView txt_main_teacher1, txt_main_teacher2;
	private ImageView img_main_teacher;
	private CustomViewpager vp_book;

	private int fragment_total, fragment_i;

	public CXMainBookView(Context context, MyApplication application,
			String nextPath, FragmentActivity fragmentActivity,
			ImageLoader imageLoader) {
		super(context, application, nextPath);
		this.fragmentActivity = fragmentActivity;
		this.imageLoader = imageLoader;
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.cx_ly_book, this);
		ly_cxmain_book = (RelativeLayout) findViewById(R.id.ly_cxmain_book);
		ly_cxmain_teacher = (RelativeLayout) findViewById(R.id.ly_cxmain_teacher);
		btn_book_left = (Button) findViewById(R.id.btn_book_left);
		btn_book_right = (Button) findViewById(R.id.btn_book_right);
		btn_main_teacher_more = (Button) findViewById(R.id.btn_main_teacher_more);
		txt_main_teacher1 = (TextView) findViewById(R.id.txt_main_teacher1);
		txt_main_teacher2 = (TextView) findViewById(R.id.txt_main_teacher2);
		img_main_teacher = (ImageView) findViewById(R.id.img_main_teacher);
		vp_book = (CustomViewpager) findViewById(R.id.vp_book);

		btn_book_left.setOnClickListener(this);
		btn_book_right.setOnClickListener(this);
		btn_main_teacher_more.setOnClickListener(this);

		updata_book();
		updata_teacher();
		application.httpProcess.QryBook(application.equInfoModel.jssysdm, "0",
				"25", "", true, null);
	}

	private void updata_book() {
		BooksInfoDatabase database = new BooksInfoDatabase();
		List<BooksInfoModel> list = database.query_all();
		fragment_i = 0;
		fragment_total = 0;
		List<Fragment> fragments = new ArrayList<Fragment>();
		if (list != null && list.size() > 0) {
			if (list.size() % 5 == 0) {
				fragment_total = list.size() / 5;
			} else {
				fragment_total = list.size() / 5 + 1;
			}
		}
		if (fragment_total > 0) {
			for (int i = 0; i < fragment_total; i++) {
				BookFragment fragment = new BookFragment();
				fragment.setImageLoader(imageLoader);
				if (i == fragment_total - 1) {
					fragment.setList(list.subList(5 * i, list.size()));
				} else {
					fragment.setList(list.subList(5 * i, 5 * i + 5));
				}
				fragments.add(fragment);
			}
			MyViewPagerAdapter adapter = new MyViewPagerAdapter(
					fragmentActivity.getSupportFragmentManager(), fragments);
			vp_book.setAdapter(adapter);
			btn_book_left.setVisibility(View.VISIBLE);
			btn_book_right.setVisibility(View.VISIBLE);
		} else {
			vp_book.setAdapter(null);
			btn_book_left.setVisibility(View.INVISIBLE);
			btn_book_right.setVisibility(View.INVISIBLE);
		}
	}

	private void updata_teacher() {
		if (application.curriculumInfoModel_now == null) {
			ly_cxmain_book.setVisibility(View.VISIBLE);
			ly_cxmain_teacher.setVisibility(View.INVISIBLE);
		} else {
			ly_cxmain_book.setVisibility(View.INVISIBLE);
			ly_cxmain_teacher.setVisibility(View.VISIBLE);
			TeacherInfoDatabase database = new TeacherInfoDatabase();
			TeacherInfoModel model = database
					.query_by_id(application.curriculumInfoModel_now.skjs);
			String xm = "", byzy = "", zc = "", byxx = "", zgxlmc = "", jl = "";
			if (model != null) {
				if (!TextUtils.isEmpty(model.zp)) {
					String[] s = model.zp.split("\\.");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/teacher_"
							+ model.rybh + "." + s[s.length - 1];
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										img_main_teacher.setImageBitmap(bitmap);
									} else {
										img_main_teacher
												.setImageResource(R.drawable.user_icon);
									}
								}
							}, 188, 188);
				} else {
					img_main_teacher.setImageResource(R.drawable.user_icon);
				}
				if (!TextUtils.isEmpty(model.xm)) {
					xm = model.xm;
				}
				if (!TextUtils.isEmpty(model.byzy)) {
					byzy = model.byzy;
				}
				if (!TextUtils.isEmpty(model.jgzc)) {
					zc = model.jgzc;
				}
				if (!TextUtils.isEmpty(model.byxx)) {
					byxx = model.byxx;
				}
				if (!TextUtils.isEmpty(model.zgxlmc)) {
					zgxlmc = model.zgxlmc;
				}
				if (!TextUtils.isEmpty(model.jgjl)) {
					jl = model.jgjl;
				}
			} else {
				img_main_teacher.setImageResource(R.drawable.user_icon);
			}
			txt_main_teacher1.setText("教师姓名 : " + xm + "\n所学专业 : " + byzy
					+ "\n职称 : " + zc);
			txt_main_teacher2.setText("毕业院校 : " + byxx + "\n学历学位 : " + zgxlmc
					+ "\n教师教龄 : " + jl);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_book_left: {
			if (fragment_i > 0) {
				fragment_i--;
				vp_book.setCurrentItem(fragment_i);
			}
			break;
		}
		case R.id.btn_book_right: {
			if (fragment_i < fragment_total - 1) {
				fragment_i++;
				vp_book.setCurrentItem(fragment_i);
			}
			break;
		}
		case R.id.btn_main_teacher_more: {
			Intent intent = new Intent(context, CXTeacherActivity.class);
			context.startActivity(intent);
			break;
		}
		}
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根
		if (type == APPCODE_UPDATA_CURRICULUM || type == APPCODE_UPDATA_EQU) {
			updata_teacher();
		} else if (type == APPCODE_UPDATA_BOOK) {
			updata_book();
		}
	}

	@Override
	public void pause() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void resume() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void stop() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void destroy() {
		// TODO 自动生成的方法存根

	}
}
