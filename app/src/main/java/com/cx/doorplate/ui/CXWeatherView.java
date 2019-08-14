package com.cx.doorplate.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.ui.AppCodeView;

public class CXWeatherView extends AppCodeView {

	private TextView txt_weather;

	public CXWeatherView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		View.inflate(context, R.layout.cx_ly_weather, this);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());
		application.httpProcess.getWeather(application.equInfoModel.jssysdm);
	}

	@Override
	public void update(int type) {
		// TODO 自动生成的方法存根
		if (type == APPCODE_UPDATA_WEATHER) {
			txt_weather.setText(application.currentCity.trim() + "   "
					+ application.temperature.trim() + "\n"
					+ application.weather.trim());
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
