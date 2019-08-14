package com.yy.doorplate.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;

public class WeatherTimeView extends AppCodeView {

	private TextClock textClock;
	private TextView txt_weather, txt_location;

	public WeatherTimeView(Context context, MyApplication application,
			String nextPath) {
		super(context, application, nextPath);
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		View.inflate(context, R.layout.ly_weather_and_time, this);
		textClock = (TextClock) findViewById(R.id.textClock);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		txt_location = (TextView) findViewById(R.id.txt_location);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy-MM-dd HH:mm");

		txt_location.setText(application.currentCity);
		txt_weather
				.setText(application.weather + " " + application.temperature);
	}

	@Override
	public void update(int type) {
		// TODO Auto-generated method stub
		if (type == APPCODE_UPDATA_WEATHER) {
			txt_location.setText(application.currentCity);
			txt_weather.setText(application.weather + " "
					+ application.temperature);
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

}
