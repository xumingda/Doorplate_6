package com.advertisement.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.advertisement.resource.ResFileParse;
import com.advertisement.resource.ResFileParse.FileParamer;
import com.advertisement.resource.ResFileParse.ResPlayPlan;
import com.advertisement.resource.ResPlaybill;
import com.advertisement.resource.ResPlaybill.PlayList;
import com.advertisement.resource.ResTheme;
import com.advertisement.resource.ResTheme.Form.Appcode;
import com.advertisement.resource.ResTheme.Form.Clock;
import com.advertisement.resource.ResTheme.Form.Image;
import com.advertisement.resource.ResTheme.Form.Label;
import com.advertisement.resource.ResTheme.Form.Video;
import com.advertisement.resource.ResThemeSwitch.ThemePolicy;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemManager;
import com.advertisement.ui.AdvClockView;
import com.advertisement.ui.AdvImageView;
import com.advertisement.ui.AdvLabelView;
import com.advertisement.ui.AdvVideoView;
import com.advertisement.ui.AdvViewBase;
import com.common.Mutex;
import com.cx.doorplate.ui.CXBtnBackView;
import com.cx.doorplate.ui.CXBtnBjdcView;
import com.cx.doorplate.ui.CXBtnBjhdView;
import com.cx.doorplate.ui.CXBtnBjkqView;
import com.cx.doorplate.ui.CXBtnBjlyView;
import com.cx.doorplate.ui.CXBtnBjtzView;
import com.cx.doorplate.ui.CXBtnBjxcView;
import com.cx.doorplate.ui.CXBtnClassView;
import com.cx.doorplate.ui.CXBtnControlView;
import com.cx.doorplate.ui.CXBtnFaceView;
import com.cx.doorplate.ui.CXBtnGrxxView;
import com.cx.doorplate.ui.CXBtnLeaveView;
import com.cx.doorplate.ui.CXBtnMoreView;
import com.cx.doorplate.ui.CXBtnMsgView;
import com.cx.doorplate.ui.CXBtnReadView;
import com.cx.doorplate.ui.CXBtnSchoolView;
import com.cx.doorplate.ui.CXBtnSettingView;
import com.cx.doorplate.ui.CXBtnStarView;
import com.cx.doorplate.ui.CXBtnTalkView;
import com.cx.doorplate.ui.CXBtnWeatherView;
import com.cx.doorplate.ui.CXBtnXxdcView;
import com.cx.doorplate.ui.CXBtnXxhdView;
import com.cx.doorplate.ui.CXBtnXxtzView;
import com.cx.doorplate.ui.CXBtnXxxcView;
import com.cx.doorplate.ui.CXClassNameView;
import com.cx.doorplate.ui.CXMainAttendView;
import com.cx.doorplate.ui.CXMainBookView;
import com.cx.doorplate.ui.CXMainClassMottoView;
import com.cx.doorplate.ui.CXMainNewView;
import com.cx.doorplate.ui.CXMainScheduleView;
import com.cx.doorplate.ui.CXTimeView;
import com.cx.doorplate.ui.CXWeatherView;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.ui.AppCodeView;
import com.yy.doorplate.ui.BjtzView;
import com.yy.doorplate.ui.BtnClassView;
import com.yy.doorplate.ui.BtnControlView;
import com.yy.doorplate.ui.BtnDiyMenuView;
import com.yy.doorplate.ui.BtnNewClassView;
import com.yy.doorplate.ui.BtnNewSchoolView;
import com.yy.doorplate.ui.BtnNoticeView;
import com.yy.doorplate.ui.BtnPjNoticeView;
import com.yy.doorplate.ui.BtnReadView;
import com.yy.doorplate.ui.BtnScheduleView;
import com.yy.doorplate.ui.BtnSettingView;
import com.yy.doorplate.ui.BtnTalkView;
import com.yy.doorplate.ui.ClassBannerView;
import com.yy.doorplate.ui.ClassHonorView;
import com.yy.doorplate.ui.ClassInfoView;
import com.yy.doorplate.ui.EnglishView;
import com.yy.doorplate.ui.LogoView;
import com.yy.doorplate.ui.MainInfoNoCView;
import com.yy.doorplate.ui.MainInfoView;
import com.yy.doorplate.ui.MainNoticeView;
import com.yy.doorplate.ui.QuestView;
import com.yy.doorplate.ui.ScheduleNowView;
import com.yy.doorplate.ui.ScheduleTodayView;
import com.yy.doorplate.ui.SchoolBannerView;
import com.yy.doorplate.ui.SchoolHonorView;
import com.yy.doorplate.ui.SchoolInfoView;
import com.yy.doorplate.ui.SmallVideoView;
import com.yy.doorplate.ui.TopClassView;
import com.yy.doorplate.ui.TopSchoolView;
import com.yy.doorplate.ui.WeatherTimeView;
import com.yy.doorplate.ui.XxtzView;
import com.yy.doorplate.ui.XxxwView;

public class DisplayManager {
	private static final String TAG = "DisplayManager";
	private Context mContext = null;
	private MyApplication application;
	private String mPolicyPath = null;
	private ResPlayPlan mResPlayPlan = null;
	private boolean isExtPlay = false;
	private boolean isExtPlayBack = false;
	private String backPolicyPath = null;
	private int mPlayId = 0;
	private int mPlayTotal = 0;
	private ResTheme mTheme = null;
	private RelativeLayout mainLayout = null;
	private ArrayList<AdvViewBase> mAdvList = new ArrayList<AdvViewBase>();
	private ArrayList<AppCodeView> mViews = new ArrayList<AppCodeView>();
	private BitmapDrawable mBmpDrawable = null;
	private int iFinishCount = 0;

	private String mUpdatePoliycPath = null;
	public static final int STATE_IDLE = 0;
	public static final int STATE_RUN = 1;
	public static final int STATE_PAUSE = 2;

	public Activity mActivity = null;
	public FragmentActivity fragmentActivity = null;

	private int iState = STATE_IDLE;

	private int mHostAdvType = ConstData.FileTrans.PLAYBILL_TYPE_UNDEF;

	private ImageLoader imageLoader;
	private String activity;

	private Mutex mutex_updata = new Mutex();

	public DisplayManager(Context context, MyApplication application,
			RelativeLayout layout) {
		mContext = context;
		this.application = application;
		mainLayout = layout;
		iState = STATE_IDLE;
		mUpdatePoliycPath = null;
		isExtPlay = false;
		isExtPlayBack = false;
		backPolicyPath = null;
		mPolicyPath = null;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public int getState() {
		return iState;
	}

	public void setUpdatePolicyPath(String path) {
		mUpdatePoliycPath = path;
	}

	public boolean start(String policyPath) {

		stop();
		mResPlayPlan = ResFileParse.getPlayPlanByPolicy(policyPath);

		if (mResPlayPlan == null || !mResPlayPlan.isFilesExist) {
			return false;
		}
		mPolicyPath = policyPath;
		mPlayId = 0;

		if (mResPlayPlan.mThemeSwitch.mExtThemePolicyList.size() > 0) {
			isExtPlay = true;
			mPlayTotal = mResPlayPlan.mThemeSwitch.mExtThemePolicyList.size();
		} else {
			isExtPlay = false;
			mPlayTotal = mResPlayPlan.mThemeSwitch.mThemePolicyList.size();
		}
		mainLayout.removeAllViews();
		mAdvList.clear();
		mViews.clear();
		startPlayTheme();
		iState = STATE_IDLE;

		return true;
	}

	public void stop() {
		isExtPlayBack = isExtPlay;
		backPolicyPath = mPolicyPath;
		stopPlayTheme();

		iState = STATE_IDLE;
	}

	public void pause() {
		pausePlayTheme();
		iState = STATE_PAUSE;
	}

	public void resume() {
		if (mUpdatePoliycPath != null) {
			// --更新的路径不是当前的路径则立即更新,当暂停状态时，有更新，当唤醒时可以直接播放最新的广告
			if (!mUpdatePoliycPath.equals(mPolicyPath)) {
				stop();
				start(mUpdatePoliycPath);
				mUpdatePoliycPath = null;
				return;
			}
		}

		resumePlayTheme();
		iState = STATE_RUN;
	}

	public void destroy() {
		mainLayout.removeAllViewsInLayout();
		iState = STATE_IDLE;
	}

	public void stopTheme() {

		for (AdvViewBase adv : mAdvList) {
			adv.stop();
		}
		for (AppCodeView view : mViews) {
			view.stop();
		}
	}

	public ThemePolicy getThemePolicy() {
		ThemePolicy mThemePolicy = null;
		if (isExtPlay) {
			mThemePolicy = mResPlayPlan.mThemeSwitch.mExtThemePolicyList
					.get(mPlayId);
		} else {
			mThemePolicy = mResPlayPlan.mThemeSwitch.mThemePolicyList
					.get(mPlayId);
		}

		return mThemePolicy;
	}

	public void startPlayTheme() {
		if (mResPlayPlan == null) {
			return;
		}

		ThemePolicy mThemePolicy = getThemePolicy();
		FileParamer filePar = new FileParamer(mThemePolicy.url);

		mTheme = ResFileParse.parseThemeXmlFile(filePar.fileLocalPath);

		if (mTheme == null) {
			return;
		}
		if (mTheme.mForm.background != null) {
			filePar = new FileParamer(mTheme.mForm.background);
			SystemManager.LOGI(TAG, "background:" + filePar.fileLocalPath);
			if (new File(filePar.fileLocalPath).exists()) {
				// --回收上一次的图片资源
				if (mBmpDrawable != null) {
					if (mBmpDrawable.getBitmap() != null) {
						if (!mBmpDrawable.getBitmap().isRecycled()) {
							mBmpDrawable.getBitmap().recycle();
						}
					}
					mBmpDrawable = null;
				}
				mBmpDrawable = new BitmapDrawable(filePar.fileLocalPath);
				mainLayout.setBackgroundDrawable(mBmpDrawable);
			}
		}

		iFinishCount = 0;

		LayoutParams lp = null;
		mAdvList.clear();
		mViews.clear();
		for (Image image : mTheme.mForm.mImageList) {
			if (image.playbill == null) {
				continue;
			}
			// --界面布局
			AdvImageView adv = new AdvImageView(mContext, this);
			adv.setBaseForm(image);
			SystemManager.LOGI(TAG, "w:" + image.width + "h:" + image.height
					+ " t:" + image.top + " l:" + image.left);
			lp = new LayoutParams(image.width, image.height);
			lp.leftMargin = image.left;
			lp.topMargin = image.top;
			// --将播放控件添加到主layout中
			mainLayout.addView(adv, lp);
			// --将控件view添加到链表中
			mAdvList.add(adv);
			filePar = new FileParamer(image.playbill);
			adv.setManagerHandler(mHandler);
			// --播放控件中的内容
			adv.play(filePar.fileLocalPath);
		}

		for (Video video : mTheme.mForm.mVideoList) {
			if (video.playbill == null) {
				continue;
			}
			AdvVideoView adv = new AdvVideoView(mContext, this);
			adv.setBaseForm(video);
			lp = new LayoutParams(video.width, video.height);
			lp.leftMargin = video.left;
			lp.topMargin = video.top;
			mainLayout.addView(adv, lp);
			mAdvList.add(adv);
			adv.setManagerHandler(mHandler);
			filePar = new FileParamer(video.playbill);
			adv.play(filePar.fileLocalPath);
		}
		for (Label label : mTheme.mForm.mLabelList) {
			if (label.playbill == null) {
				continue;
			}
			AdvLabelView adv = new AdvLabelView(mContext, this);
			adv.setBaseForm(label);
			lp = new LayoutParams(label.width, label.height);
			lp.leftMargin = label.left;
			lp.topMargin = label.top;
			mainLayout.addView(adv, lp);
			mAdvList.add(adv);
			adv.setManagerHandler(mHandler);
			filePar = new FileParamer(label.playbill);
			adv.setOpt(label.fontSize, label.FLY, label.rate, label.bgcolor,
					label.color);
			adv.play(filePar.fileLocalPath);

		}
		for (Clock clock : mTheme.mForm.mClockList) {
			AdvClockView adv = new AdvClockView(mContext, this);
			adv.setBaseForm(clock);
			lp = new LayoutParams(clock.width, clock.height);
			lp.leftMargin = clock.left;
			lp.topMargin = clock.top;
			mainLayout.addView(adv, lp);
			mAdvList.add(adv);
			adv.setManagerHandler(mHandler);
			if (clock.playbill != null) {
				filePar = new FileParamer(clock.playbill);
				adv.play(filePar.fileLocalPath);
			} else {
				adv.play();
			}
		}
		for (Appcode appcode : mTheme.mForm.mAppcodeList) {
			filePar = new FileParamer(appcode.playbill);
			ResPlaybill mResPlaybill = ResFileParse
					.parsePlaybillXmlFile(filePar.fileLocalPath);
			if (mResPlaybill.mPlayList.size() > 0) {
				PlayList playList = mResPlaybill.mPlayList.get(0);
				Log.i(TAG, "-------------rescode-------------"
						+ playList.mAppcodeAd.rescode);
				Log.i(TAG, "-------------rescode-------------"
						+ playList.mAppcodeAd.url);
				if (1000 <= playList.mAppcodeAd.rescode
						&& playList.mAppcodeAd.rescode < 2000) {
					LogoView view = new LogoView(mContext, application,
							imageLoader, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					view.setLogo(playList.mAppcodeAd.url);
					mViews.add(view);
				}
				if (2000 <= playList.mAppcodeAd.rescode
						&& playList.mAppcodeAd.rescode < 3000) {
					BtnDiyMenuView view = new BtnDiyMenuView(mContext,
							application, imageLoader, playList.mAppcodeAd.url,
							playList.mAppcodeAd.rescode, appcode.width,
							appcode.height);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
				}
				switch (playList.mAppcodeAd.rescode) {
				case 101: {
					MainInfoView view = new MainInfoView(mContext, application,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 102: {
					XxtzView view = new XxtzView(mContext, application,
							imageLoader, activity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 103: {
					WeatherTimeView view = new WeatherTimeView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 104: {
					BtnPjNoticeView view = new BtnPjNoticeView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 105: {
					BtnNewClassView view = new BtnNewClassView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 106: {
					BtnScheduleView view = new BtnScheduleView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 107: {
					BtnNewSchoolView view = new BtnNewSchoolView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 108: {
					BtnReadView view = new BtnReadView(mContext, application,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 109: {
					BtnSettingView view = new BtnSettingView(mContext,
							application, mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 110: {
					BtnControlView view = new BtnControlView(mContext,
							application, mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 111: {
					BtnTalkView view = new BtnTalkView(mContext, application,
							mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 112: {
					BtnClassView view = new BtnClassView(mContext, application,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 113: {
					MainInfoNoCView view = new MainInfoNoCView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 200: {
					TopSchoolView view = new TopSchoolView(mContext,
							application, mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 201: {
					SchoolBannerView view = new SchoolBannerView(mContext,
							application, imageLoader, activity,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 202: {
					XxxwView view = new XxxwView(mContext, application,
							imageLoader, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 203: {
					SchoolHonorView view = new SchoolHonorView(mContext,
							application, imageLoader, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 204: {
					SchoolInfoView view = new SchoolInfoView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 205: {
					QuestView view = new QuestView(mContext, application,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 300: {
					TopClassView view = new TopClassView(mContext, application,
							mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 301: {
					ClassBannerView view = new ClassBannerView(mContext,
							application, imageLoader, activity,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 302: {
					BjtzView view = new BjtzView(mContext, application,
							imageLoader, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 303: {
					ClassInfoView view = new ClassInfoView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 304: {
					ClassHonorView view = new ClassHonorView(mContext,
							application, imageLoader, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 305: {
					EnglishView view = new EnglishView(mContext, application,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 306: {
					SmallVideoView view = new SmallVideoView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 400: {
					MainNoticeView view = new MainNoticeView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 401: {
					ScheduleNowView view = new ScheduleNowView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 402: {
					ScheduleTodayView view = new ScheduleTodayView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 403: {
					BtnNoticeView view = new BtnNoticeView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3000: {
					CXClassNameView view = new CXClassNameView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3001: {
					CXTimeView view = new CXTimeView(mContext, application,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3002: {
					CXWeatherView view = new CXWeatherView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3003: {
					CXMainNewView view = new CXMainNewView(mContext,
							application, playList.mAppcodeAd.url, imageLoader,
							activity);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3004: {
					CXMainAttendView view = new CXMainAttendView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3005: {
					CXMainScheduleView view = new CXMainScheduleView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3006: {
					CXMainBookView view = new CXMainBookView(mContext,
							application, playList.mAppcodeAd.url,
							fragmentActivity, imageLoader);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3100: {
					CXBtnClassView view = new CXBtnClassView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3101: {
					CXBtnSchoolView view = new CXBtnSchoolView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3102: {
					CXBtnReadView view = new CXBtnReadView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3103: {
					CXBtnMsgView view = new CXBtnMsgView(mContext, application,
							playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3104: {
					CXBtnMoreView view = new CXBtnMoreView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3105: {
					CXBtnTalkView view = new CXBtnTalkView(mContext,
							application, mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3106: {
					CXBtnControlView view = new CXBtnControlView(mContext,
							application, mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3107: {
					CXBtnSettingView view = new CXBtnSettingView(mContext,
							application, mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3108: {
					CXBtnLeaveView view = new CXBtnLeaveView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3109: {
					CXBtnGrxxView view = new CXBtnGrxxView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3110: {
					CXBtnWeatherView view = new CXBtnWeatherView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3111: {
					CXBtnBjdcView view = new CXBtnBjdcView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3112: {
					CXBtnBjtzView view = new CXBtnBjtzView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3113: {
					CXBtnBjhdView view = new CXBtnBjhdView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3114: {
					CXBtnBjxcView view = new CXBtnBjxcView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3115: {
					CXBtnStarView view = new CXBtnStarView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3116: {
					CXBtnBjlyView view = new CXBtnBjlyView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3117: {
					CXBtnXxdcView view = new CXBtnXxdcView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3118: {
					CXBtnXxtzView view = new CXBtnXxtzView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3119: {
					CXBtnXxhdView view = new CXBtnXxhdView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3120: {
					CXBtnXxxcView view = new CXBtnXxxcView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3121: {
					CXBtnBjkqView view = new CXBtnBjkqView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3122: {
					CXBtnFaceView view = new CXBtnFaceView(mContext,
							application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3123: {
					CXMainClassMottoView view = new CXMainClassMottoView(
							mContext, application, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				case 3200: {
					CXBtnBackView view = new CXBtnBackView(mContext,
							application, mActivity, playList.mAppcodeAd.url);
					lp = new LayoutParams(appcode.width, appcode.height);
					lp.leftMargin = appcode.left;
					lp.topMargin = appcode.top;
					mainLayout.addView(view, lp);
					mViews.add(view);
					break;
				}
				// TODO
				}
			}
		}

		if (mTheme.mForm.mImageList.size() > 0) {
			mHostAdvType = ConstData.FileTrans.PLAYBILL_TYPE_IMAGE;
		}

		if (mTheme.mForm.mVideoList.size() > 0) {
			mHostAdvType = ConstData.FileTrans.PLAYBILL_TYPE_VIDEO;
		}

	}

	private void stopPlayTheme() {
		if (mResPlayPlan == null) {
			return;
		}
		ThemePolicy mThemePolicy = getThemePolicy();

		FileParamer filePar = new FileParamer(mThemePolicy.url);

		mTheme = ResFileParse.parseThemeXmlFile(filePar.fileLocalPath);

		for (AdvViewBase adv : mAdvList) {
			if (adv != null) {
				adv.stop();
				mainLayout.removeView(adv);
				adv = null;
			}
		}
		for (AppCodeView view : mViews) {
			if (view != null) {
				view.stop();
				mainLayout.removeView(view);
				view = null;
			}
		}
		mResPlayPlan = null;
	}

	private void pausePlayTheme() {
		if (mResPlayPlan == null) {
			return;
		}
		ThemePolicy mThemePolicy = getThemePolicy();

		FileParamer filePar = new FileParamer(mThemePolicy.url);

		mTheme = ResFileParse.parseThemeXmlFile(filePar.fileLocalPath);

		for (AdvViewBase adv : mAdvList) {

			if (adv != null) {
				adv.pause();
			}
		}
		for (AppCodeView view : mViews) {

			if (view != null) {
				view.pause();
			}
		}
	}

	public void playNext() {
		/*
		 * if(mPlayTotal==1){ resume(); return ; }
		 */
		if (mPlayTotal == 0) {
			return;
		}
		stopPlayTheme();
		mPlayId++;
		if (mPlayId == mPlayTotal) {
			mPlayId = 0;
		}
		SystemManager.LOGD(TAG, "mPlayTotal:" + mPlayTotal + " mPlayId:"
				+ mPlayId);
		mResPlayPlan = ResFileParse.getPlayPlanByPolicy(mPolicyPath);
		startPlayTheme();
	}

	public void updata(int type) {
		mutex_updata.lock(0);

		Log.e(TAG, "--------------------updata type : " + type);

		for (AppCodeView view : mViews) {

			if (view != null) {
				view.update(type);
			}
		}

		mutex_updata.unlock();
	}

	private void resumePlayTheme() {
		if (mResPlayPlan == null) {
			return;
		}

		ThemePolicy mThemePolicy = getThemePolicy();

		FileParamer filePar = new FileParamer(mThemePolicy.url);

		mTheme = ResFileParse.parseThemeXmlFile(filePar.fileLocalPath);

		for (AdvViewBase adv : mAdvList) {

			if (adv != null) {
				adv.resume();
			}
		}

		for (AppCodeView view : mViews) {

			if (view != null) {
				view.resume();
			}
		}
	}

	public boolean onViewFinish(AdvViewBase adv) {
		if (isExtPlay) {
			if (mAdvList != null) {
				if (mAdvList.size() > 0) {
					iFinishCount++;
					// --所有组件播放完成一轮
					if (mAdvList.size() == iFinishCount) {
						// --若当前为插播，播放完一轮组件，直接返回上一次的播放
						if (isExtPlay && (!isExtPlayBack)
								&& backPolicyPath != null) {
							stopPlayTheme();
							start(backPolicyPath);
						}
					}
				}
			}
		} else {
			if (adv.type == mHostAdvType) {
				SystemManager.LOGI(TAG, "=======================");
				SystemManager.LOGI(TAG, " VIDEO PLAY COMPLETE");
				SystemManager.LOGI(TAG, "=======================");
				if (mPlayTotal == 1) {
					return false;
				}

				mHandler.sendEmptyMessageDelayed(
						ConstData.DisplayMsgType.MSG_DISPLAY_FINISH, 10);

				return true;
			}
		}

		return false;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case ConstData.DisplayMsgType.MSG_DISPLAY_FINISH:
				SystemManager.LOGI(TAG, " stopPlayTheme 1");
				stopPlayTheme();
				SystemManager.LOGI(TAG, " stopPlayTheme 2");
				mPlayId++;
				if (mPlayId == mPlayTotal) {
					mPlayId = 0;
				}
				SystemManager.LOGD(TAG, "mPlayTotal:" + mPlayTotal
						+ " mPlayId:" + mPlayId);
				mResPlayPlan = ResFileParse.getPlayPlanByPolicy(mPolicyPath);
				startPlayTheme();
				break;
			case ConstData.DisplayMsgType.MSG_STOP_FAIL:
				// --出现停止故障，则结束activity，系统自动启动
				if (mActivity != null) {
					mActivity.finish();
				}
				break;
			}

		};
	};

	public void setActivity(Activity activity) {
		mActivity = activity;
	}

	public void setFragmentActivity(FragmentActivity fragmentActivity) {
		this.fragmentActivity = fragmentActivity;
	}

}
