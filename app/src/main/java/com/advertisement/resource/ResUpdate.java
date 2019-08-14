package com.advertisement.resource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.content.Context;
import android.os.Message;

import com.advertisement.resource.ResFileParse.ResPlayPlan;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemConfig;
import com.advertisement.system.SystemManager;
import com.common.Unit;

public class ResUpdate {

	private Context mContext = null;
	private static final String TAG = "ResUpdate";
	private static final String UDISK_ROOT_PATH = "/mnt/usb_storage/";
	private static final String UDISK_TAG = "EYun";
	private String selectedPath = null;

	public interface UpdateListener {
		public abstract void onStart(boolean success);

		public abstract void onUpdate(String src, String dst, boolean success);

		public abstract void onStop();
	}

	private UpdateListener mListener = null;

	public UpdateThread mThread = null;

	public ResUpdate(Context context, UpdateListener listener) {
		mContext = context;
		mListener = listener;
	}

	public void startUdisk(String rootPath) {

		if (mThread != null) {
			mThread.interrupt();
			mThread = null;
		}

		selectedPath = rootPath;

		mThread = new UpdateThread(UDISK_ROOT_PATH,
				UpdateThread.UPDATE_TYPE_UDISK);
		mThread.start();

	}

	public void stop() {
		if (mThread != null) {
			mThread.interrupt();
			mThread = null;
		}
	}

	private class UpdateThread extends Thread {
		public static final int UPDATE_TYPE_UDISK = 0;
		public static final int UPDATE_TYPE_LOCAL = 1;

		private String rootPath = null;
		private int updateType = 0;

		public UpdateThread(String rootPath, int type) {
			this.rootPath = rootPath;
			this.updateType = type;
		}

		private void checkPath() {
			File file = new File(rootPath);

			if (!file.exists()) {
				rootPath = null;
				return;
			}
			boolean gotIt = false;
			int state = 0;
			if (updateType == UPDATE_TYPE_UDISK) {

				/* 解析当前U盘分区 /mnt/storage/USB_DISK1 USB_DISK2 USB_DISK3... */
				String[] partions = file.list();

				/* 进入二级目录 /mnt/storage/USB_DISK1/usbdisk0 */
				for (int i = 0; i < partions.length; i++) {
					File file2 = new File(rootPath + partions[i]);

					String[] dirs = file2.list();

					for (int j = 0; j < dirs.length; j++) {

						File file3 = new File(rootPath + partions[i] + "/"
								+ dirs[j]);
						// --过滤查找指定文件夹
						String[] eyun = file3.list(new FilenameFilter() {

							@Override
							public boolean accept(File dir, String filename) {
								SystemManager.LOGI(TAG, "fileName:" + filename);
								return filename.equals(UDISK_TAG);
							}
						});

						SystemManager.LOGI(TAG, "eyun:" + eyun);
						// --检测到文件
						if (eyun != null && eyun.length > 0) {
							gotIt = true;
							rootPath = rootPath + "/" + partions[i] + "/"
									+ dirs[j] + "/" + eyun[0];
							return;
						}
					}

				}

			}
			if (updateType == UPDATE_TYPE_LOCAL) {

			}
			if (gotIt == false) {
				rootPath = null;
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			if (selectedPath == null) {
				checkPath();
			} else {
				rootPath = selectedPath;
			}

			ConstData.UpdateInfo info = new ConstData.UpdateInfo();
			info.type = ConstData.UpdateInfo.TYPE_SCAN;
			if (rootPath == null) {
				SystemManager.LOGE(TAG, "can't find the update dir");
				info.success = false;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				return;
			}

			SystemManager.LOGD(TAG, "rootPath:" + rootPath);

			// --查询策略文件夹是否存在
			File file = new File(rootPath + "/upload/themeswitch");

			if (!file.exists()) {
				SystemManager.LOGE(TAG, "can't find  upload");
				info.success = false;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				return;
			}
			// --查询日期文件夹
			String[] fileList = file.list();
			if (fileList == null || fileList.length == 0) {
				SystemManager.LOGE(TAG, "can't find  upload/themeswitch");
				info.success = false;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				return;
			}
			// --查询策略文件
			File file1 = new File(rootPath + "/upload/themeswitch/"
					+ fileList[0]);
			if ((!file1.exists()) || (!file1.isDirectory())) {
				SystemManager.LOGE(TAG, "can't find  upload/themeswitch");
				info.success = false;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				return;
			}
			String[] policyPathList = file1.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					if (filename.indexOf("res_themeswitch") == 0) {
						return true;
					}
					return false;
				}
			});

			String policyPath = "/upload/themeswitch/" + fileList[0] + "/"
					+ policyPathList[0];
			SystemManager.LOGD(TAG, "find policy:" + policyPath);

			ResPlayPlan plan = ResFileParse.getPlayPlanByPolicyBase(rootPath
					+ "/" + policyPath, rootPath);
			if (plan == null) {
				SystemManager.LOGE(TAG, "get plan fail!");

				info.success = false;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				return;
			}
			if (!plan.isFilesExist) {
				SystemManager.LOGE(TAG, "plan is not complete!");
				info.success = false;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				return;
			}
			SystemManager.LOGD(TAG, "plan is complete!");

			// --获取将要拷贝的资源文件路径
			ArrayList<String> list = ResFileParse.getFilePathListByPolicy(
					policyPath, rootPath);

			if (list == null) {
				info.success = false;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				return;
			}

			info.success = true;
			SystemManager.getSystemManager().uiReportUpdateInfo(info);

			info.type = ConstData.UpdateInfo.TYPE_START;
			info.total = list.size();
			SystemManager.getSystemManager().uiReportUpdateInfo(info);
			info.id = 0;
			for (String subPath : list) {
				String oldPath = rootPath + "/" + subPath;
				String newPath = SystemManager.getSystemManager().getConfig()
						.getStoreRootPath()
						+ "/" + subPath;
				File f_old = new File(oldPath);
				// --判断原始文件是否存在
				if (!f_old.exists()) {
					continue;
				}
				// --将要拷贝到的目的地址
				File f_new = new File(newPath);
				if (!f_new.getParentFile().exists()) {
					f_new.getParentFile().mkdirs();
				}

				SystemManager.LOGD(TAG, "COPY " + oldPath + " TO " + newPath);

				info.type = ConstData.UpdateInfo.TYPE_COPY;
				info.id++;
				info.src = oldPath;
				info.dst = newPath;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				boolean ret = Unit.copyFile(oldPath, newPath);
				info.success = ret;
				info.type = ConstData.UpdateInfo.TYPE_CP_RESULT;
				if (ret) {
					SystemManager.LOGD(TAG, "COPY OK");
					SystemManager.getSystemManager().uiReportUpdateInfo(info);
				} else {
					SystemManager.LOGE(TAG, "COPY FAIL");
					SystemManager.getSystemManager().uiReportUpdateInfo(info);
					return;
				}
			}
			info.type = ConstData.UpdateInfo.TYPE_FINISH;
			plan = ResFileParse.getPlayPlanByPolicy(SystemManager
					.getSystemManager().getConfig().getStoreRootPath()
					+ policyPath);
			if (plan == null || !plan.isFilesExist) {
				SystemManager.LOGE(TAG, "COPY POLICY FAIL!");
				info.success = false;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
				return;
			} else {
				SystemManager.LOGD(TAG, "COPY POLICY OK!");
				info.success = true;
				SystemManager.getSystemManager().uiReportUpdateInfo(info);
			}

			SystemConfig config = SystemManager.getSystemManager().getConfig();

			// --更新正常播放广告的策略文件路径

			config.writeParam("normalThemePolicy", SystemManager
					.getSystemManager().getConfig().getStoreRootPath()
					+ policyPath);
			config.normalThemePolicy = SystemManager.getSystemManager()
					.getConfig().getStoreRootPath()
					+ policyPath;
			config.writeParam("isNormalExist", true);
			config.isNormalExist = true;
			// --发送广播到系统切换当前的播放内容
			Message msg = new Message();
			msg.what = ConstData.SystemMsgType.MSG_UPDATE_THEME_POLICY;
			msg.arg1 = ConstData.FileTrans.FILE_TYPE_THEME_POLICY;
			SystemManager.getSystemManager().sendMsgToSystem(msg);
		}
	}

}
