package com.yy.doorplate.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.R.color;
import com.yy.doorplate.tool.EthernetUtils;
import com.yy.doorplate.tool.WifiUtils;

public class NetSetActivity extends Activity {

	private final static String TAG = "NetSetActivity";
	/** ��wifi������ */
	private final static int THIS_WIFI_STATE_CONNECTED = 1;
	/** ��wifiδ���� */
	private final static int THIS_WIFI_STATE_NOCONNECTED = 0;
	/** ��wifi�ѱ��� */
	private final static int THIS_WIFI_STATE_SAVED = 2;
	/** ��¼��ǰ�������wifi״̬ */
	private int thisWifi = -1;
	/** ��õ�ǰ�������wifi��Ϣ */
	private ScanResult thisScanResult;

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private RelativeLayout netset_left_option_ethernet,
			netset_left_option_wifi, btn_netset_tip;
	private TextView netset_dialog_tv_wifiname;
	private CheckBox netset_cb_ethernet, netset_cb_wifi,
			netset_cb_right_staticip, netset_dialog_cb_displaypassword;
	private Button btn_netset_back, netset_btn_right_confirm,
			netset_btn_right_cancel, netset_dialog_btn_confirm,
			netset_dialog_btn_cancel;

	private EditText netset_dialog_edt_password, netset_tv_right_ip,
			netset_tv_right_subnetmask, netset_tv_right_gateway,
			netset_tv_right_maindns, netset_tv_right_sparedns;
	/** ��ߵ���̫������ */
	private LinearLayout netset_layout_ethernet;
	/** ��ߵ�wifi���� */
	private RelativeLayout netset_layout_wifi;
	/** ��̫�������� */
	private EthernetUtils ethernetUtils;
	/** wifi������ */
	private WifiUtils wifiUtils;
	private ListView netset_lv;
	private WifiListAdapt wifiListAdapt;
	/** ����WiFiʱ��ʾ��ʾ�ı� */
	private TextView netset_tv_wifitip;
	/** ɨ����������������б� */
	private List<ScanResult> list_scanResults = new ArrayList<ScanResult>();
	/** ɨ����������������б� 1 */
	private List<ScanResult> list_scanResults1 = new ArrayList<ScanResult>();
	/** ��ɨ���������List<Map<String, Object>>�� */
	private List<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>();
	/** wifi�Ƿ����� */
	private boolean isConnect = false;
	/** ��Ҫ��ͨ��wifi Ӳ����ɨ������ȡһЩ�ܱߵ�wifi �ȵ����Ϣ�� */
	private ScanResult scanResult1 = null;
	private WifiReceiver receiver = null;
	/** �Ƿ��ֶ��Ͽ�wifi */
	private boolean isDisConnect = false;
	/** ��һ�ν���activity */
	private boolean isFirst = true;
	/** �Ƿ��ڽ��������֤ */
	private boolean is = false;

	private TextView item_netset_wifiname, item_netset_state;
	private ImageView item_netset_choice, item_netset_wifi, item_netset_lock;
	private TextView netset_dialog_tv_connected;

	private boolean wifiInitOk = false;

	private AlertDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_netset);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplication();

		ethernetUtils = new EthernetUtils(application);
		wifiUtils = new WifiUtils(application);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectionReceiver, intentFilter);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		intview();
		listviewlisten();
		judgeNetwork();
		listenCheckBox();
	}

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (wifiUtils.isWifiEnabled() && !ethernetUtils.isEthernetConnected()) {
			list_scanResults.clear();
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		unregisterReceiver(connectionReceiver);
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case KeyEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case MotionEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private void intview() {

		btn_netset_back = (Button) findViewById(R.id.btn_netset_back);

		netset_left_option_ethernet = (RelativeLayout) findViewById(R.id.netset_left_option_ethernet);
		netset_left_option_wifi = (RelativeLayout) findViewById(R.id.netset_left_option_wifi);
		btn_netset_tip = (RelativeLayout) findViewById(R.id.btn_netset_tip);
		netset_left_option_ethernet.setBackgroundColor(color.netset_cilck);

		netset_tv_right_ip = (EditText) findViewById(R.id.netset_tv_right_ip);
		netset_tv_right_subnetmask = (EditText) findViewById(R.id.netset_tv_right_subnetmask);
		netset_tv_right_gateway = (EditText) findViewById(R.id.netset_tv_right_gateway);
		netset_tv_right_maindns = (EditText) findViewById(R.id.netset_tv_right_maindns);
		netset_tv_right_sparedns = (EditText) findViewById(R.id.netset_tv_right_sparedns);
		netset_tv_right_ip.addTextChangedListener(application.textWatcher);
		netset_tv_right_subnetmask
				.addTextChangedListener(application.textWatcher);
		netset_tv_right_gateway.addTextChangedListener(application.textWatcher);
		netset_tv_right_maindns.addTextChangedListener(application.textWatcher);
		netset_tv_right_sparedns
				.addTextChangedListener(application.textWatcher);

		netset_lv = (ListView) findViewById(R.id.netset_lv);
		wifiListAdapt = new WifiListAdapt();

		netset_cb_ethernet = (CheckBox) findViewById(R.id.netset_cb_ethernet);
		netset_cb_wifi = (CheckBox) findViewById(R.id.netset_cb_wifi);
		netset_cb_right_staticip = (CheckBox) findViewById(R.id.netset_cb_right_staticip);

		netset_layout_ethernet = (LinearLayout) findViewById(R.id.netset_layout_ethernet);
		netset_layout_wifi = (RelativeLayout) findViewById(R.id.netset_layout_wifi);

		netset_btn_right_confirm = (Button) findViewById(R.id.netset_btn_right_confirm);
		netset_btn_right_cancel = (Button) findViewById(R.id.netset_btn_right_cancel);
		// startIPisOperation(false);

		netset_tv_wifitip = (TextView) findViewById(R.id.netset_tv_wifitip);
		netset_dialog_btn_cancel = (Button) findViewById(R.id.netset_dialog_btn_cancel);
	}

	/** ����״̬�ж�,������ui */
	private void judgeNetwork() {
		try {
			int i = Settings.Secure.getInt(getContentResolver(), "ethernet_on");
			if (i == 0) {
				netset_cb_ethernet.setChecked(false);
			} else if (i == 1) {
				netset_cb_ethernet.setChecked(true);
			}
		} catch (SettingNotFoundException e1) {
			e1.printStackTrace();
		}
		netset_cb_wifi.setChecked(wifiUtils.isWifiEnabled());// wifi
		netset_cb_right_staticip.setChecked(ethernetUtils.isUsingStaticIp());// ��̬ip

		if (wifiUtils.isWifiEnabled()) {
			netset_tv_wifitip.setText(getResources().getString(
					R.string.wifi_scan));
		} else {
			netset_tv_wifitip.setText(getResources().getString(
					R.string.wifi_disenble));
		}

		if (ethernetUtils.isEthernetConnected()) {// ��̫������
			ethernetConnected();

			netset_tv_wifitip.setText(getResources().getString(
					R.string.wifi_not_available));
		} else {
			btn_netset_tip.setVisibility(View.VISIBLE);
			netset_cb_right_staticip.setEnabled(false);
			startIPisOperation(false);
			setStarIpEdt("", "", "", "", "");

			if (wifiUtils.isWifiEnabled()) {
				netset_cb_wifi.setEnabled(false);
			}
			// ע��㲥������ʾwifi״̬
			wifiUtils.startScanWifi();
			try {
				receiver = new WifiReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
				filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
				filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
				filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
				filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
				filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
				filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
				filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
				registerReceiver(receiver, filter);
			} catch (Exception e) {
			}
			if (wifiUtils.isWiFiConnected()) {
				btn_netset_tip.setVisibility(View.INVISIBLE);
			} else {
				btn_netset_tip.setVisibility(View.VISIBLE);
			}
		}
	}

	/** CheckBox ���� */
	private void listenCheckBox() {
		// ��������
		netset_cb_ethernet
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {
						if (isChecked) {
							Log.i(TAG, "----------���� ��̫��---------");
							ethernetUtils.setEtherentEnable();
							netset_cb_right_staticip.setEnabled(true);
						} else {
							Log.i(TAG, "----------�ر� ��̫��---------");
							ethernetUtils.setEtherentDisable();
							netset_cb_right_staticip.setEnabled(false);
							startIPisOperation(false);
							setStarIpEdt("", "", "", "", "");

							if (!wifiUtils.isWiFiConnected()) {
								btn_netset_tip.setVisibility(View.VISIBLE);
							}
							if (!wifiUtils.isWifiEnabled()) {
								netset_tv_wifitip.setText(getResources()
										.getString(R.string.wifi_disenble));
							} else {
								ListShow();
							}
						}
					}
				});
		// wifi
		netset_cb_wifi
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {
						Log.i(TAG, "	// wifi");
						if (isChecked) {
							Log.i(TAG, "----------���� wifi---------");
							// wifiConnected();
							wifiUtils.SetWifiEnable();
							if (!ethernetUtils.isEthernetConnected()) {
								netset_tv_wifitip.setText(getResources()
										.getString(R.string.wifi_scan));
								netset_cb_wifi.setEnabled(false);
								isFirst = false;
							} else {
								netset_tv_wifitip
										.setText(getResources().getString(
												R.string.wifi_not_available));
							}
						} else {
							Log.i(TAG, "----------�ر� wifi---------");
							wifiUtils.SetWifiDiable();
							list_map.clear();
							list_scanResults1.clear();
							wifiListAdapt.notifyDataSetChanged();
							if (!ethernetUtils.isEthernetConnected()) {
								btn_netset_tip.setVisibility(View.VISIBLE);
								netset_tv_wifitip.setText(getResources()
										.getString(R.string.wifi_disenble));
							} else {
								netset_tv_wifitip
										.setText(getResources().getString(
												R.string.wifi_not_available));
							}
						}
					}
				});
		// ��̬ip
		netset_cb_right_staticip
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {
						startIPisOperation(isChecked);
						if (isChecked) {
							Log.i(TAG, "----------ʹ�þ�̬ip---------");
							ethernetUtils.setStaticIpEnable();
							setStarIpEdt(
									ethernetUtils.getEthInfoFromStaticIP(),
									ethernetUtils.getEthInfoFromStaticNetmask(),
									ethernetUtils.getEthInfoFromStaticGATEWAY(),
									ethernetUtils.getEthInfoFromStaticDNS1(),
									ethernetUtils.getEthInfoFromStaticDNS2());
						} else {
							Log.i(TAG, "----------�رվ�̬ip---------");
							ethernetUtils.setStaticIpDisable();
							if (Build.VERSION.SDK_INT >= 21) {
								Intent intent = new Intent();
								intent.setAction("ethernet_staticip_disable");
								application.sendBroadcast(intent);
							}
							setStarIpEdt(EthernetUtils.getEthInfoFromDhcpIP(),
									ethernetUtils.getEthInfoFromDhcpNetmask(),
									ethernetUtils.getEthInfoFromDhcpGATEWAY(),
									ethernetUtils.getEthInfoFromDhcpDNS1(),
									ethernetUtils.getEthInfoFromDhcpDNS2());
						}
					}
				});
	}

	public void myclick(View view) {
		switch (view.getId()) {
		case R.id.btn_netset_back:
			finish();
			break;
		case R.id.netset_left_option_ethernet:// ��������
			netset_left_option_ethernet.setBackgroundColor(color.netset_cilck);
			netset_left_option_wifi.setBackgroundColor(Color.TRANSPARENT);
			netset_layout_ethernet.setVisibility(View.VISIBLE);
			netset_layout_wifi.setVisibility(View.GONE);
			break;
		case R.id.netset_left_option_wifi:// wifi
			netset_left_option_ethernet.setBackgroundColor(Color.TRANSPARENT);
			netset_left_option_wifi.setBackgroundColor(color.netset_cilck);
			netset_layout_ethernet.setVisibility(View.GONE);
			netset_layout_wifi.setVisibility(View.VISIBLE);
			break;
		case R.id.netset_btn_right_confirm:// �ύ��̬ip
			Log.i(TAG,
					"-------�ύ��̬ip----------"
							+ (!TextUtils.isEmpty(netset_tv_right_ip.getText()
									.toString()))
							+ "-netset_tv_subnetmask.getText().toString()-"
							+ netset_tv_right_subnetmask.getText().toString()
							+ "--"
							+ (!TextUtils.isEmpty(netset_tv_right_gateway
									.getText().toString()))
							+ "--"
							+ (!TextUtils.isEmpty(netset_tv_right_maindns
									.getText().toString()))
							+ "--"
							+ (!TextUtils.isEmpty(netset_tv_right_sparedns
									.getText().toString())));
			if (!TextUtils.isEmpty(netset_tv_right_ip.getText().toString())
					&& !TextUtils.isEmpty(netset_tv_right_subnetmask.getText()
							.toString())
					&& !TextUtils.isEmpty(netset_tv_right_gateway.getText()
							.toString())
					&& !TextUtils.isEmpty(netset_tv_right_maindns.getText()
							.toString())) {
				String regex = "^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9]).){1,3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])?$";
				if (netset_tv_right_ip.getText().toString().matches(regex)
						&& netset_tv_right_subnetmask.getText().toString()
								.matches(regex)
						&& netset_tv_right_gateway.getText().toString()
								.matches(regex)) {
					// ethernetUtils.setEthInfoFromStaticIP(netset_tv_right_ip
					// .getText().toString());
					// ethernetUtils
					// .setEthInfoFromStaticGATEWAY(netset_tv_right_gateway
					// .getText().toString());
					// ethernetUtils
					// .setEthInfoFromStaticNetmask(netset_tv_right_subnetmask
					// .getText().toString());
					// ethernetUtils
					// .setEthInfoFromStaticDNS1(netset_tv_right_maindns
					// .getText().toString());
					// ethernetUtils
					// .SetEthInfoFromStaticDNS2(netset_tv_right_sparedns
					// .getText().toString());
					// application.showToast(getResources().getString(
					// R.string.set_success));
					netset_btn_right_confirm.setEnabled(false);
					setIP(netset_tv_right_ip.getText().toString(),
							netset_tv_right_gateway.getText().toString(),
							netset_tv_right_subnetmask.getText().toString(),
							netset_tv_right_maindns.getText().toString(),
							netset_tv_right_sparedns.getText().toString());
				} else {
					application.showToast(getResources().getString(
							R.string.input_format_error));
				}
			} else {
				application.showToast(getResources().getString(
						R.string.input_none));
			}
			break;
		case R.id.netset_btn_right_cancel:
			netset_cb_right_staticip.setChecked(false);
			break;
		default:
			break;
		}
	}

	/** ������̫�� �������������ӵ�UI���� */
	private void ethernetConnected() {
		btn_netset_tip.setVisibility(View.INVISIBLE);
		netset_cb_right_staticip.setEnabled(true);
		startIPisOperation(ethernetUtils.isUsingStaticIp());
		if (!ethernetUtils.isUsingStaticIp()) {// ��̬ipδʹ��ʹ�ö�̬ip
			Log.i(TAG, "--��̬ip--" + EthernetUtils.getEthInfoFromDhcpIP()
					+ "---");
			setStarIpEdt(EthernetUtils.getEthInfoFromDhcpIP(),
					ethernetUtils.getEthInfoFromDhcpNetmask(),
					ethernetUtils.getEthInfoFromDhcpGATEWAY(),
					ethernetUtils.getEthInfoFromDhcpDNS1(),
					ethernetUtils.getEthInfoFromDhcpDNS2());
		} else {
			Log.i(TAG, "--��̬ip--" + ethernetUtils.getEthInfoFromStaticIP()
					+ "---");
			setStarIpEdt(ethernetUtils.getEthInfoFromStaticIP(),
					ethernetUtils.getEthInfoFromStaticNetmask(),
					ethernetUtils.getEthInfoFromStaticGATEWAY(),
					ethernetUtils.getEthInfoFromStaticDNS1(),
					ethernetUtils.getEthInfoFromStaticDNS2());
		}
	}

	/** ��̬ip�����ܷ���� */
	private void startIPisOperation(boolean operation) {
		netset_tv_right_ip.setEnabled(operation);
		netset_tv_right_subnetmask.setEnabled(operation);
		netset_tv_right_gateway.setEnabled(operation);
		netset_tv_right_maindns.setEnabled(operation);
		netset_tv_right_sparedns.setEnabled(operation);
		netset_btn_right_confirm.setEnabled(operation);
		netset_btn_right_cancel.setEnabled(operation);
	}

	/** ���þ�̬ip������ֵ */
	private void setStarIpEdt(String ip, String subnetmask, String gatway,
			String maindns, String sparedns) {
		netset_tv_right_ip.setText(ip);
		netset_tv_right_subnetmask.setText(subnetmask);
		netset_tv_right_gateway.setText(gatway);
		netset_tv_right_maindns.setText(maindns);
		netset_tv_right_sparedns.setText(sparedns);
	}

	/**
	 * �㲥������
	 * 
	 * @author yy
	 * 
	 */
	private class WifiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// �������wifi������״̬���Ƿ�������һ����Ч����·�ɣ�
			// ���ϱ߹㲥��״̬��WifiManager.WIFI_STATE_DISABLING��
			// ��WIFI_STATE_DISABLED��ʱ�򣬸�������ӵ�����㲥��
			// ���ϱ߹㲥�ӵ��㲥��WifiManager.WIFI_STATE_ENABLED״̬��
			// ͬʱҲ��ӵ�����㲥����Ȼ�մ�wifi�϶���û�����ӵ���Ч������
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				Log.d(TAG, "WifiManager.NETWORK_STATE_CHANGED_ACTION");
				DetailedState state = ((NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
						.getDetailedState();
				changeState(state);
			} else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				Log.d(TAG, "WifiManager.SCAN_RESULTS_AVAILABLE_ACTION");
				isFirst = false;
				ListShow();
				netset_cb_wifi.setEnabled(true);
			} else if (action
					.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {// �������wifi������״̬
				Log.d(TAG, "WifiManager.SUPPLICANT_STATE_CHANGED_ACTION");
				DetailedState state = WifiInfo
						.getDetailedStateOf((SupplicantState) intent
								.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
				changeState(state);
			}
		}
	}

	/**
	 * ��ʾwifi�б�
	 */
	private void ListShow() {
		list_map.clear();
		list_scanResults1.clear();
		// ��ȡɨ����
		list_scanResults = wifiUtils.getScanResults();
		if (isConnect) {
			// �����������������ź���ʾ����һ��
			for (ScanResult result : list_scanResults) {
				if (wifiUtils.isConnect(result)) {
					scanResult1 = result;
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", result.SSID);
					map.put("status",
							WifiManager.calculateSignalLevel(result.level, 100));
					map.put("desc",
							getResources().getString(R.string.wifi_connect));
					list_map.add(map);
					list_scanResults1.add(result);
					break;
				}
			}
			// �����������������ź���ʾ���������źź���
			for (ScanResult result : list_scanResults) {
				if (wifiUtils.IsConfigurationExsitsSsid(result.SSID)
						&& !wifiUtils.isConnect(result)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", result.SSID);
					map.put("status",
							WifiManager.calculateSignalLevel(result.level, 100));
					String desc = getResources().getString(
							R.string.wifi_save_none);
					if (result.capabilities.contains("WPA")
							&& result.capabilities.contains("WPA2")) {
						desc = getResources().getString(
								R.string.wifi_save_wpawpa2);
					} else if (result.capabilities.contains("WPA2")) {
						desc = getResources()
								.getString(R.string.wifi_save_wpa2);
					} else if (result.capabilities.contains("WPA")) {
						desc = getResources().getString(R.string.wifi_save_wpa);
					} else if (result.capabilities.contains("WEP")) {
						desc = getResources().getString(R.string.wifi_save_wep);
					}
					if (result.capabilities.contains("WPS")) {
						desc = desc
								+ getResources().getString(
										R.string.wifi_save_wps);
					}
					map.put("desc", desc);
					list_map.add(map);
					list_scanResults1.add(result);
				}
			}
			// �������ź���ʾ�����
			for (ScanResult result : list_scanResults) {
				if (!wifiUtils.isConnect(result)
						&& !wifiUtils.IsConfigurationExsitsSsid(result.SSID)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", result.SSID);
					map.put("status",
							WifiManager.calculateSignalLevel(result.level, 100));
					String desc = getResources().getString(R.string.wifi_none);
					if (result.capabilities.contains("WPA")
							&& result.capabilities.contains("WPA2")) {
						desc = getResources().getString(R.string.wifi_wpawpa2);
					} else if (result.capabilities.contains("WPA2")) {
						desc = getResources().getString(R.string.wifi_wpa2);
					} else if (result.capabilities.contains("WPA")) {
						desc = getResources().getString(R.string.wifi_wpa);
					} else if (result.capabilities.contains("WEP")) {
						desc = getResources().getString(R.string.wifi_wep);
					}
					if (result.capabilities.contains("WPS")) {
						desc = desc
								+ getResources().getString(
										R.string.wifi_save_wps);
					}
					map.put("desc", desc);
					list_map.add(map);
					list_scanResults1.add(result);
				}
			}
		} else {
			for (ScanResult result : list_scanResults) {
				if (wifiUtils.IsConfigurationExsitsSsid(result.SSID)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", result.SSID);
					map.put("status",
							WifiManager.calculateSignalLevel(result.level, 100));
					String desc = getResources().getString(
							R.string.wifi_save_none);
					if (result.capabilities.contains("WPA")
							&& result.capabilities.contains("WPA2")) {
						desc = getResources().getString(
								R.string.wifi_save_wpawpa2);
					} else if (result.capabilities.contains("WPA2")) {
						desc = getResources()
								.getString(R.string.wifi_save_wpa2);
					} else if (result.capabilities.contains("WPA")) {
						desc = getResources().getString(R.string.wifi_save_wpa);
					} else if (result.capabilities.contains("WEP")) {
						desc = getResources().getString(R.string.wifi_save_wep);
					}
					if (result.capabilities.contains("WPS")) {
						desc = desc
								+ getResources().getString(
										R.string.wifi_save_wps);
					}
					map.put("desc", desc);
					list_map.add(map);
					list_scanResults1.add(result);
				}
			}
			// �������ź���ʾ�����
			for (ScanResult result : list_scanResults) {
				if (!wifiUtils.IsConfigurationExsitsSsid(result.SSID)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", result.SSID);
					map.put("status",
							WifiManager.calculateSignalLevel(result.level, 100));
					String desc = getResources().getString(R.string.wifi_none);
					if (result.capabilities.contains("WPA")
							&& result.capabilities.contains("WPA2")) {
						desc = getResources().getString(R.string.wifi_wpawpa2);
					} else if (result.capabilities.contains("WPA2")) {
						desc = getResources().getString(R.string.wifi_wpa2);
					} else if (result.capabilities.contains("WPA")) {
						desc = getResources().getString(R.string.wifi_wpa);
					} else if (result.capabilities.contains("WEP")) {
						desc = getResources().getString(R.string.wifi_wep);
					}
					if (result.capabilities.contains("WPS")) {
						desc = desc
								+ getResources().getString(
										R.string.wifi_save_wps);
					}
					map.put("desc", desc);
					list_map.add(map);
					list_scanResults1.add(result);
				}
			}
		}
		if (ethernetUtils.isEthernetConnected()) {
			netset_lv.setAdapter(null);
			// ��������������ʾ�ı�
			netset_tv_wifitip.setText(getResources().getString(
					R.string.wifi_not_available));
		} else {
			netset_lv.setAdapter(wifiListAdapt);
			// ��������������ʾ�ı�
			netset_tv_wifitip.setText("");
		}
	}

	/**
	 * ��ʾwifi����״̬�е��б�
	 */
	private void ListShowConnectting(String str) {
		list_map.clear();
		list_scanResults1.clear();
		// ��ȡɨ����
		list_scanResults = wifiUtils.getScanResults();
		// �����������������ź���ʾ����һ��
		if (scanResult1 != null) {
			for (ScanResult result : list_scanResults) {
				if (result.SSID.equals(scanResult1.SSID)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", result.SSID);
					map.put("status",
							WifiManager.calculateSignalLevel(result.level, 100));
					map.put("desc", str);
					list_map.add(map);
					list_scanResults1.add(result);
					break;
				}
			}
		}

		// �����������������ź���ʾ���������źź���
		Log.i(TAG, "list_scanResults   " + list_scanResults);
		for (ScanResult result : list_scanResults) {
			if (wifiUtils.IsConfigurationExsitsSsid(result.SSID)
					&& !result.SSID.equals(scanResult1.SSID)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", result.SSID);
				map.put("status",
						WifiManager.calculateSignalLevel(result.level, 100));
				String desc = getResources().getString(R.string.wifi_save_none);
				if (result.capabilities.contains("WPA")
						&& result.capabilities.contains("WPA2")) {
					desc = getResources().getString(R.string.wifi_save_wpawpa2);
				} else if (result.capabilities.contains("WPA2")) {
					desc = getResources().getString(R.string.wifi_save_wpa2);
				} else if (result.capabilities.contains("WPA")) {
					desc = getResources().getString(R.string.wifi_save_wpa);
				} else if (result.capabilities.contains("WEP")) {
					desc = getResources().getString(R.string.wifi_save_wep);
				}
				if (result.capabilities.contains("WPS")) {
					desc = desc
							+ getResources().getString(R.string.wifi_save_wps);
				}
				map.put("desc", desc);
				list_map.add(map);
				list_scanResults1.add(result);
			}
		}
		// �������ź���ʾ�����
		for (ScanResult result : list_scanResults) {
			if (!result.SSID.equals(scanResult1.SSID)
					&& !wifiUtils.IsConfigurationExsitsSsid(result.SSID)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", result.SSID);
				map.put("status",
						WifiManager.calculateSignalLevel(result.level, 100));
				String desc = getResources().getString(R.string.wifi_none);
				if (result.capabilities.contains("WPA")
						&& result.capabilities.contains("WPA2")) {
					desc = getResources().getString(R.string.wifi_wpawpa2);
				} else if (result.capabilities.contains("WPA2")) {
					desc = getResources().getString(R.string.wifi_wpa2);
				} else if (result.capabilities.contains("WPA")) {
					desc = getResources().getString(R.string.wifi_wpa);
				} else if (result.capabilities.contains("WEP")) {
					desc = getResources().getString(R.string.wifi_wep);
				}
				if (result.capabilities.contains("WPS")) {
					desc = desc
							+ getResources().getString(R.string.wifi_save_wps);
				}
				map.put("desc", desc);
				list_map.add(map);
				list_scanResults1.add(result);
			}
		}

		wifiListAdapt.notifyDataSetChanged();

	}

	/** ui�Ŀ��� */
	private void changeState(DetailedState aState) {
		if (aState == DetailedState.AUTHENTICATING) {
			is = true;
			Log.i("ConnectWifiActivity", "���ڽ��������֤");
			if (scanResult1 != null && !isFirst)
				ListShowConnectting(getResources()
						.getString(R.string.wifi_auth));
		} else if (aState == DetailedState.CONNECTED) {
			Log.i("ConnectWifiActivity", "������");
			isConnect = true;
			ListShow();
		} else if (aState == DetailedState.CONNECTING) {
			Log.i("ConnectWifiActivity", "������");
			if (scanResult1 != null && !isFirst)
				ListShowConnectting(getResources().getString(
						R.string.wifi_connecting));
		} else if (aState == DetailedState.DISCONNECTED) {
			Log.i("ConnectWifiActivity", "�Ͽ��ɹ�");
			isConnect = false;
			if (isDisConnect) {
				scanResult1 = null;
				isDisConnect = false;
				ListShow();
			}
			if (scanResult1 != null && !isFirst && is) {
				Log.i("ConnectWifiActivity", "�����֤����");
				is = false;
				ListShowConnectting(getResources().getString(
						R.string.wifi_auth_error));
			}
		} else if (aState == DetailedState.DISCONNECTING) {
			Log.i("ConnectWifiActivity", "�ο���");
		} else if (aState == DetailedState.FAILED) {
			Log.i("ConnectWifiActivity", "����ʧ��");
		} else if (aState == DetailedState.IDLE) {
			Log.i("ConnectWifiActivity", "IDLE");
		} else if (aState == DetailedState.OBTAINING_IPADDR) {
			Log.i("ConnectWifiActivity", "IP��ַ��ȡ��");
			if (scanResult1 != null && !isFirst)
				ListShowConnectting(getResources().getString(
						R.string.wifi_getip));
		} else if (aState == DetailedState.SCANNING) {
			Log.i("ConnectWifiActivity", "����ɨ��");
		} else if (aState == DetailedState.SUSPENDED) {
			Log.i("ConnectWifiActivity", "====SUSPENDED====");
		}
	}

	/** wifi�б��� */
	private void listviewlisten() {
		netset_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (wifiInitOk) {
					dialogWifiInput(position, list_map.get(position)
							.get("desc").toString());
				} else {
					application.showToast(getResources().getString(
							R.string.wifi_load));
				}
			}
		});
	}

	private void dialogWifiInput(int position, String desc) {
		// �Զ��嵯��
		LayoutInflater inflater = LayoutInflater.from(this);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_wifi_input, null);
		Builder builder = new AlertDialog.Builder(this);
		dialog = builder.create();
		dialog.setView(layout);
		dialog.show();
		dialog.getWindow().setLayout(764, 520);
		dialog.getWindow().setContentView(R.layout.dialog_wifi_input);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(getWindow().getDecorView()
							.getWindowToken(), 0);
				}
			}
		});
		netset_dialog_tv_wifiname = (TextView) dialog
				.findViewById(R.id.netset_dialog_tv_wifiname);
		netset_dialog_edt_password = (EditText) dialog
				.findViewById(R.id.netset_dialog_edt_password);
		netset_dialog_tv_connected = (TextView) dialog
				.findViewById(R.id.netset_dialog_tv_connected);
		netset_dialog_cb_displaypassword = (CheckBox) dialog
				.findViewById(R.id.netset_dialog_cb_displaypassword);
		netset_dialog_btn_confirm = (Button) dialog
				.findViewById(R.id.netset_dialog_btn_confirm);
		netset_dialog_btn_cancel = (Button) dialog
				.findViewById(R.id.netset_dialog_btn_cancel);

		thisScanResult = list_scanResults1.get(position);

		netset_dialog_tv_wifiname.setText(thisScanResult.SSID);
		netset_dialog_edt_password.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		// ����WiFi��������״̬������ʾ�����ӣ���ʾIP���������������
		if (desc.equals(getResources().getString(R.string.wifi_connect))) {
			netset_dialog_edt_password.setVisibility(View.INVISIBLE);
			netset_dialog_cb_displaypassword.setVisibility(View.INVISIBLE);
			netset_dialog_tv_connected.setVisibility(View.VISIBLE);
			netset_dialog_tv_connected.setText(getResources().getString(
					R.string.wifi_connect));
			thisWifi = THIS_WIFI_STATE_CONNECTED;
			netset_dialog_btn_confirm.setText(getResources().getString(
					R.string.ok));
			netset_dialog_btn_cancel.setText(getResources().getString(
					R.string.wifi_forget));
		} else if (wifiUtils.ConfigurationExsitsSsid(thisScanResult.SSID) != null) {
			netset_dialog_edt_password.setVisibility(View.INVISIBLE);
			netset_dialog_cb_displaypassword.setVisibility(View.INVISIBLE);
			netset_dialog_tv_connected.setVisibility(View.VISIBLE);
			netset_dialog_tv_connected.setText(getResources().getString(
					R.string.wifi_save));
			thisWifi = THIS_WIFI_STATE_SAVED;
			netset_dialog_btn_confirm.setText(getResources().getString(
					R.string.wifi_connect_));
			netset_dialog_btn_cancel.setText(getResources().getString(
					R.string.wifi_forget));
		} else {
			dialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			// ����WiFi�����뱣��������ʾ���������
			if (!thisScanResult.capabilities.contains("WPA")
					&& !thisScanResult.capabilities.contains("WPA2")
					&& !thisScanResult.capabilities.contains("WEP")) {
				netset_dialog_edt_password.setVisibility(View.INVISIBLE);
				netset_dialog_cb_displaypassword.setVisibility(View.INVISIBLE);
			} else {
				netset_dialog_edt_password.setVisibility(View.VISIBLE);
				netset_dialog_cb_displaypassword.setVisibility(View.VISIBLE);

			}
			netset_dialog_btn_confirm.setText(getResources().getString(
					R.string.ok));
			netset_dialog_btn_cancel.setText(getResources().getString(
					R.string.cancel));
			netset_dialog_tv_connected.setVisibility(View.INVISIBLE);
			thisWifi = THIS_WIFI_STATE_NOCONNECTED;
		}
		// �Ƿ���ʾ����
		netset_dialog_cb_displaypassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// ��ʾ����
							netset_dialog_edt_password
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							netset_dialog_edt_password
									.setSelection(netset_dialog_edt_password
											.getText().toString().length());
						} else {
							// ��������
							netset_dialog_edt_password
									.setInputType(InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							netset_dialog_edt_password
									.setSelection(netset_dialog_edt_password
											.getText().toString().length());
						}
					}
				});
		netset_dialog_btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (thisWifi == THIS_WIFI_STATE_CONNECTED) {
					dialog.dismiss();
				} else if (thisWifi == THIS_WIFI_STATE_NOCONNECTED) {
					linkThisWifi();
				} else if (thisWifi == THIS_WIFI_STATE_SAVED) {
					scanResult1 = thisScanResult;
					wifiUtils.addNewWifi(wifiUtils
							.ConfigurationExsitsSsid(thisScanResult.SSID));
					dialog.dismiss();
				}
			}
		});
		netset_dialog_btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (thisWifi == THIS_WIFI_STATE_CONNECTED) {
					// �Ͽ�WiFi
					isDisConnect = true;
					wifiUtils.disConnectionWifi(wifiUtils.getConnectedID());
					// wifiListAdapt.notifyDataSetChanged();
					Log.i("WifiUtils", thisScanResult.SSID + "---------"
							+ wifiUtils.isConnect(thisScanResult));
				} else if (thisWifi == THIS_WIFI_STATE_SAVED) {
					isDisConnect = true;
					wifiUtils.disConnectionWifi(wifiUtils
							.ConfigurationExsitsSsid(thisScanResult.SSID).networkId);
				}
				dialog.dismiss();
			}
		});
	}

	/** ����ѡ�е�wifi */
	private void linkThisWifi() {
		Log.i(TAG, "-------����ѡ�е�wifi----------");
		scanResult1 = thisScanResult;
		if (netset_dialog_edt_password.getVisibility() == View.VISIBLE) {// ���������ʾ��
			String pwd = netset_dialog_edt_password.getText().toString();
			if (pwd.length() >= 8) {
				// ����wifi
				wifiUtils.wifiConnect(thisScanResult.SSID, pwd, 3, true);
				ListShowConnectting(getResources().getString(
						R.string.wifi_connecting));
				dialog.dismiss();
			} else {
				application.showToast(getResources().getString(
						R.string.wifi_pwd_error));
			}
		} else {
			// ����wifi
			wifiUtils.wifiConnect(thisScanResult.SSID, null, 3, true);
			ListShowConnectting(getResources().getString(
					R.string.wifi_connecting));
			dialog.dismiss();
		}
	}

	private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (application.checkNetworkHw()) {
				btn_netset_tip.setVisibility(View.INVISIBLE);
				if (ethernetUtils.isEthernetConnected()) {
					netset_cb_ethernet.setChecked(true);
					ethernetConnected();

					netset_lv.setAdapter(null);
					netset_tv_wifitip.setText(getResources().getString(
							R.string.wifi_not_available));
				}
			} else {
				btn_netset_tip.setVisibility(View.VISIBLE);
				// netset_cb_right_staticip.setEnabled(false);
				// startIPisOperation(false);
				setStarIpEdt("", "", "", "", "");
			}
		}
	};

	/** ����wifi�б���� */
	public class WifiListAdapt extends BaseAdapter {

		@Override
		public int getCount() {
			return list_map.size();
		}

		@Override
		public Object getItem(int position) {
			return list_map.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(NetSetActivity.this).inflate(
						R.layout.item_netset, null);
			}
			item_netset_wifiname = (TextView) convertView
					.findViewById(R.id.item_netset_wifiname);
			item_netset_state = (TextView) convertView
					.findViewById(R.id.item_netset_state);
			item_netset_choice = (ImageView) convertView
					.findViewById(R.id.item_netset_choice);
			item_netset_wifi = (ImageView) convertView
					.findViewById(R.id.item_netset_wifi);
			item_netset_lock = (ImageView) convertView
					.findViewById(R.id.item_netset_lock);
			// �����������������ź�
			ScanResult resultConnectde = null;
			for (ScanResult result : list_scanResults) {
				if (result.SSID.equals(list_map.get(position).get("title"))) {
					resultConnectde = result;
					break;
				}
			}
			if (list_map.get(position).get("desc").toString()
					.equals(getResources().getString(R.string.wifi_connect))) {// �ж��Լ��Ƿ�����
				item_netset_choice.setVisibility(View.VISIBLE);
			} else {
				item_netset_choice.setVisibility(View.INVISIBLE);
			}
			item_netset_wifiname.setText(list_map.get(position).get("title")
					.toString());
			String state = list_map.get(position).get("desc").toString();
			if (state.equals(getResources().getString(R.string.wifi_save_none))
					|| state.equals(getResources()
							.getString(R.string.wifi_none))) {
				item_netset_lock.setVisibility(View.INVISIBLE);
			}
			item_netset_state.setText(state);
			int level = (Integer) list_map.get(position).get("status");
			if (level > 80) {
				item_netset_wifi.setImageResource(R.drawable.net_icon_wifi);
			} else if (level > 50) {
				item_netset_wifi.setImageResource(R.drawable.net_icon_wifi_1);
			} else {
				item_netset_wifi.setImageResource(R.drawable.net_icon_wifi_2);
			}
			wifiInitOk = true;
			return convertView;
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

	private Timer timer_ip = null;
	private int timer_count = 5;

	private void setIP(final String ip, final String gateway,
			final String mask, final String dns1, final String dns2) {
		Log.d(TAG, "ip:" + ip + " gateway:" + gateway + " mask:" + mask
				+ " dns1:" + dns1 + " dns2:" + dns2);
		final EthernetUtils ethernetUtils = new EthernetUtils(application);
		ethernetUtils.setEtherentDisable();
		timer_count = 5;
		timer_ip = new Timer();
		timer_ip.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!isEthernetConnected(application)) {
					Log.i(TAG, "---setStaticIp--- ip:" + ip + " mask:" + mask
							+ " gateway:" + gateway + " dns1:" + dns1
							+ " dns2:" + dns2);
					if (Build.VERSION.SDK_INT >= 21) {
						Intent intent = new Intent();
						intent.setAction("ethernet_staticip_enable");
						intent.putExtra("ethernet_setip", ip);
						intent.putExtra("ethernet_setgateway", gateway);
						intent.putExtra("ethernet_setmask", mask);
						intent.putExtra("ethernet_setdns1", dns1);
						if (!TextUtils.isEmpty(dns2)) {
							intent.putExtra("ethernet_setdns2", dns2);
						}
						sendBroadcast(intent);
					} else {
						ethernetUtils.setStaticIpEnable();
						ethernetUtils.setEthInfoFromStaticIP(ip);
						ethernetUtils.setEthInfoFromStaticGATEWAY(gateway);
						ethernetUtils.setEthInfoFromStaticNetmask(mask);
						ethernetUtils.setEthInfoFromStaticDNS1(dns1);
						if (!TextUtils.isEmpty(dns2)) {
							ethernetUtils.SetEthInfoFromStaticDNS2(dns2);
						}
					}
					ethernetUtils.setEtherentEnable();
					timer_ip.cancel();
					timer_count = 5;
					timer_ip = new Timer();
					timer_ip.schedule(new TimerTask() {
						@Override
						public void run() {
							if (checkNetworkHw()) {
								Log.d(TAG, "���þ�̬IP�ɹ�");
								if (timer_ip != null) {
									timer_ip.cancel();
									timer_ip = null;
								}
								handler.sendEmptyMessage(0);
							} else {
								if (timer_count <= 0) {
									Log.d(TAG, "���þ�̬IPʧ��");
									if (timer_ip != null) {
										timer_ip.cancel();
										timer_ip = null;
									}
									handler.sendEmptyMessage(-1);
								}
								timer_count--;
							}
						}
					}, 1000, 1000);
				} else {
					if (timer_count <= 0) {
						Log.d(TAG, "���þ�̬IPʧ��");
						if (timer_ip != null) {
							timer_ip.cancel();
							timer_ip = null;
						}
						handler.sendEmptyMessage(-1);
					}
					timer_count--;
				}
			}
		}, 1000, 1000);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			netset_btn_right_confirm.setEnabled(true);
			switch (msg.what) {
			case -1:
				application.showToast("���þ�̬IPʧ��");
				break;
			case 0:
				application.showToast("���þ�̬IP�ɹ�");
				break;
			}
		};
	};

	private boolean isEthernetConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if (networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private boolean checkNetworkHw() {
		boolean isOkay = false;
		ConnectivityManager connectMgr = (ConnectivityManager) application
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ethNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!ethNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			// --����δ��������̫������wifi�ر��ˣ�
			isOkay = false;

		} else {
			// --wifi������̫���Կ���
			if (!ethNetInfo.isAvailable() && !wifiNetInfo.isAvailable()) { // --�޷���ȡIP�����޷�����ʹ��
				isOkay = false;
			} else {// --��������ʹ��
				isOkay = true;
			}

		}
		return isOkay;
	}

}
