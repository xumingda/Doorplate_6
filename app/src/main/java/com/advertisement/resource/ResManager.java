package com.advertisement.resource;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.advertisement.resource.ResFileParse.ResPlayPlan;
import com.advertisement.resource.ResPlaybill.PlayList;
import com.advertisement.resource.ResTheme.Form.Appcode;
import com.advertisement.resource.ResTheme.Form.Image;
import com.advertisement.resource.ResTheme.Form.Label;
import com.advertisement.resource.ResTheme.Form.Video;
import com.advertisement.resource.ResThemeSwitch.ThemePolicy;
import com.advertisement.system.BaseManager;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemConfig;
import com.advertisement.system.SystemManager;
import com.common.Mutex;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.FileDownloadProcessModel;

public class ResManager extends BaseManager {

	private ResHandler mHandler = null;
	private Mutex mAddDownloadMutex = new Mutex();
	private Mutex mAddUploadMutex = new Mutex();
	private Mutex mTaskFinishMutex = new Mutex();
	private ArrayList<DownloadTask> downloadTaskList = new ArrayList<DownloadTask>();
	private ArrayList<UploadTask> uploadTaskList = new ArrayList<UploadTask>();
	private int downloadId = 0;
	private int uploadId = 0;
	private int storeDownId = 0;
	private static final String TAG = "ResManager";
	private String storeStr = "";
	private static final String FTP_STORE_DOWNLOAD_KEY = "FtpDownloadTotal";
	private static final String FTP_STORE_DOWNLOAD_TAG = "FTP_DOWNLOAD_";
	private boolean isNetworkEnabled = false;
	private ResFileParse mResFileParse = new ResFileParse();

	private String schedule; // 排期

	public ResManager(SystemManager systemManager) {
		super(systemManager);
		// TODO Auto-generated constructor stub
		if (mHandler == null) {
			mHandler = new ResHandler();
		}

		SystemManager.getSystemManager().getConfig()
				.writeParam(FTP_STORE_DOWNLOAD_KEY, "");
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		downloadId = 0;
		uploadId = 0;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		// --停止下载任务
		for (ResTask task : downloadTaskList) {
			task.stop();
		}
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return mHandler;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	@SuppressWarnings("unused")
	private class ResHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case ConstData.ResMsgType.MSG_ADD_DOWNLOAD:

				doAddDownloadTask(msg.arg1, (String) msg.obj);
				break;
			case ConstData.ResMsgType.MSG_ADD_UPLOAD:
				doAddUploadTask(msg.arg1, (String) msg.obj);
				break;
			case ConstData.ResMsgType.MSG_TASK_FINISH:
				doFinishTask((ResTask) msg.obj, msg.arg1);
				break;
			case ConstData.ResMsgType.MSG_NETWORK_ENABLED:
				SystemManager.LOGD(TAG, "Network enable!");
				doNetworkEnabled();
				break;
			case ConstData.ResMsgType.MSG_NETWORK_DISABLED:
				SystemManager.LOGD(TAG, "Network disable!");
				doNetworkDisabled();
				break;
			}
			super.handleMessage(msg);
		}
	}

	private void storeBackupDownloadTask(ResTask task, boolean isAdd) {
		SystemConfig config = SystemManager.getSystemManager().getConfig();
		storeStr = config.readParam(FTP_STORE_DOWNLOAD_KEY, storeStr);
		SystemManager.LOGD(TAG, "old [FTP_STORE_DOWNLOAD_KEY]:" + storeStr);
		if (isAdd) {
			// --[0][1][2][3][4]...[100]
			storeStr += "[" + task.getTaskId() + "]";
			config.writeParam(FTP_STORE_DOWNLOAD_KEY, storeStr);
			// -- FTP_DOWNLOAD_0 FTP_DOWNLOAD_1 FTP_DOWNLOAD_2 FTP_DOWNLOAD_3...
			config.writeParam(FTP_STORE_DOWNLOAD_TAG + task.getTaskId(),
					task.ftpUrl);
			// --加入TYPE
			config.writeParam(FTP_STORE_DOWNLOAD_TAG + task.getTaskId()
					+ "_TYPE", task.fileType);
		} else {

			if (storeStr.length() == 0) {
				return;
			}

			int startIndex = storeStr.indexOf(String.valueOf(task.getTaskId()));
			if (startIndex < 0) {
				return;
			}
			String prevStr = storeStr.substring(0, startIndex - 1);
			String nextStr = storeStr.substring(startIndex);
			if (nextStr.indexOf("[") < 0) {
				nextStr = null;
			} else {
				nextStr = nextStr.substring(nextStr.indexOf("["));
			}
			if (nextStr == null) {
				storeStr = prevStr;
			} else {
				storeStr = prevStr + nextStr;
			}
			config.writeParam(FTP_STORE_DOWNLOAD_KEY, storeStr);
			config.removeParam(FTP_STORE_DOWNLOAD_TAG + task.getTaskId());
			config.removeParam(FTP_STORE_DOWNLOAD_TAG + task.getTaskId()
					+ "_TYPE");
		}

		SystemManager.LOGD(TAG, "new [FTP_STORE_DOWNLOAD_KEY]:" + storeStr);
	}

	private void getBackupDownloadTasks() {
		SystemConfig config = SystemManager.getSystemManager().getConfig();
		// --读取上一次的断点，保存
		String tmpStoreStr = config.readParam(FTP_STORE_DOWNLOAD_KEY, storeStr);
		SystemManager.LOGD(TAG,
				"getBackupDownloadTasks [FTP_STORE_DOWNLOAD_KEY]:"
						+ tmpStoreStr);
		// --清空断点
		config.writeParam(FTP_STORE_DOWNLOAD_KEY, "");
		if (tmpStoreStr.length() == 0) {
			return;
		}

		int offer = 0;
		int start = 0;
		int stop = 0;
		String idStr = null;
		String url = null;
		int type = 0;
		int length = tmpStoreStr.length();
		do {
			start = tmpStoreStr.indexOf("[");
			stop = tmpStoreStr.indexOf("]");
			if (start < 0 || stop < 0) {
				break;
			}
			idStr = tmpStoreStr.substring(start + 1, stop);

			url = config.readParam(FTP_STORE_DOWNLOAD_TAG + idStr, "");
			type = config
					.readParam(FTP_STORE_DOWNLOAD_TAG + idStr + "_TYPE", 0);
			config.removeParam(FTP_STORE_DOWNLOAD_TAG + idStr);
			config.removeParam(FTP_STORE_DOWNLOAD_TAG + idStr + "_TYPE");

			doAddDownloadTask(type, url);
			offer += (stop - start + 1);

			tmpStoreStr = tmpStoreStr.substring(stop + 1);
		} while (offer != length);
	}

	private void doAddDownloadTask(int type, String url) {

		SystemManager.LOGI(TAG, "doAddDownloadTask->type:" + type + " url:"
				+ url);

		// --删除同类型文件任务，有可能当文件处于下载过程中时，此时有新的同类文件文件任务，则删除当前的任务
		if (type == ConstData.FileTrans.FILE_TYPE_APP
				|| type == ConstData.FileTrans.FILE_TYPE_INSERT_POLICY
				|| type == ConstData.FileTrans.FILE_TYPE_PAD_POLICY
				|| type == ConstData.FileTrans.FILE_TYPE_THEME_POLICY) {
			if (downloadTaskList.size() > 0) {
				for (DownloadTask task : downloadTaskList) {
					/*
					 * if(task.fileType==type){ task.stop();//--停止当前任务
					 * delDownloadTask(task);//--删除当前任务 task=null; break; }
					 */

					task.stop();// --停止当前任务
					delDownloadTask(task);// --删除当前任务
					task = null;
				}

			}

		}
		mAddDownloadMutex.lock(0);

		DownloadTask task = new DownloadTask(this, downloadId, type, url);
		downloadId++;
		if (downloadId == 200) {
			downloadId = 0;
		}
		// --添加到链表
		downloadTaskList.add(task);
		// --增加到本地备份，支持断点下载
		storeBackupDownloadTask(task, true);
		// --网络正常情况下，启动任务
		if (isNetworkEnabled) {
			task.start();
		}
		mAddDownloadMutex.unlock();
	}

	private void delDownloadTask(ResTask task) {
		mAddDownloadMutex.lock(0);
		// --删除下载任务表
		downloadTaskList.remove(task);
		// --删除本地存储记录
		storeBackupDownloadTask(task, false);

		mAddDownloadMutex.unlock();
	}

	private void doAddUploadTask(int type, String url) {
		mAddUploadMutex.lock(0);
		mAddUploadMutex.unlock();
	}

	private void delUploadTask(ResTask task) {
		uploadTaskList.remove(task);
	}

	private void doFinishTask(ResTask task, int error) {
		mTaskFinishMutex.lock(0);

		SystemManager.LOGD(TAG, "doFinishTask taskId:" + task.getTaskId()
				+ " url:" + task.ftpUrl + " error:" + error);
		// --停止任务
		task.stop();
		SystemManager.LOGD(TAG, "task stop!");
		if (task.dir == ResTask.TASK_DOWNLOAD) {
			switch (error) {
			case ConstData.DownloadError.ERROR_NONE:
				SystemManager.LOGI(TAG, "FTP Download Successful!");
				String path = task.fileLocalPath;
				int type = task.fileType;
				delDownloadTask(task);
				// --下载完成之后，解析xml文件是否有继续下载的文件
				checkNextResTasks(path, type);
				break;
			case ConstData.DownloadError.ERROR_LOGIN:
				SystemManager.LOGE(TAG, "FTP Download Login fail!");
				delDownloadTask(task);
				reportDownloadError(ConstData.DownloadError.ERROR_LOGIN);
				break;
			case ConstData.DownloadError.ERROR_GET_SIZE:
				SystemManager.LOGE(TAG, "FTP Download get size fail!");
				delDownloadTask(task);
				reportDownloadError(ConstData.DownloadError.ERROR_GET_SIZE);
				break;
			case ConstData.DownloadError.ERROR_RECV_DATA:
				SystemManager.LOGE(TAG, "FTP Download recv data fail!");
				delDownloadTask(task);
				reportDownloadError(ConstData.DownloadError.ERROR_RECV_DATA);
				break;
			case ConstData.DownloadError.ERROR_PARAMER:
				SystemManager.LOGE(TAG, "Paramer error!");
				delDownloadTask(task);
				reportDownloadError(ConstData.DownloadError.ERROR_PARAMER);
				break;
			case ConstData.DownloadError.ERROR_UNKNONWN:
				SystemManager.LOGE(TAG, "Paramer unknown!");
				delDownloadTask(task);
				reportDownloadError(ConstData.DownloadError.ERROR_UNKNONWN);
				break;
			}

		} else {
			delUploadTask(task);
		}
		mTaskFinishMutex.unlock();
	}

	private void doNetworkEnabled() {
		if (isNetworkEnabled == true) {
			return;
		}
		isNetworkEnabled = true;
		if (downloadTaskList.size() == 0) {
			// --读取备份断点任务，添加到队列中,出现在第一次联网过程中
			getBackupDownloadTasks();
		} else {
			// --中途断网之后网络恢复正常之后，重启任务
			for (ResTask task : downloadTaskList) {
				task.start();
			}
		}
	}

	private void doNetworkDisabled() {
		if (isNetworkEnabled == false) {
			return;
		}
		isNetworkEnabled = false;
		if (downloadTaskList.size() > 0) {
			// --中途断网停止任务
			for (ResTask task : downloadTaskList) {
				task.stop();
			}
		}
	}

	public void reportFileTransStatus(ResTask task, byte status) {
		// Message msg = new Message();
		// msg.what = ConstData.NetworkMsgType.MSG_REPORT_TRANS_STATUS;
		// if (task.dir == ResTask.TASK_DOWNLOAD) {
		// msg.arg1 = ConstData.FileTrans.DIR_DOWNLOAD;
		// } else {
		// msg.arg1 = ConstData.FileTrans.DIR_UPLOAD;
		// }
		//
		// msg.arg2 = (int) (status & 0xff);
		// msg.obj = task.ftpUrl;
		// mSystemManager.sendMsgToManager(mSystemManager.getNetworkManager(),
		// msg);

		// yy add
		if (status == ConstData.FileTrans.STATUS_TRANS_FINISH) {

			ResPlayPlan plan = null;

			List<FileDownloadProcessModel> list = new ArrayList<FileDownloadProcessModel>();
			FileDownloadProcessModel model = new FileDownloadProcessModel();
			model.equCode = MyApplication.getEquId();
			model.schedule = schedule;

			if (task.fileType == ConstData.FileTrans.FILE_TYPE_PAD_POLICY
					|| task.fileType == ConstData.FileTrans.FILE_TYPE_INSERT_POLICY
					|| task.fileType == ConstData.FileTrans.FILE_TYPE_THEME_POLICY) {
				plan = ResFileParse.getPlayPlanByPolicy(task.fileLocalPath);
				if (plan != null && plan.mThemeSwitch != null
						&& plan.mThemeSwitch.mThemePolicyList.size() > 0) {
					schedule = plan.mThemeSwitch.mThemePolicyList.get(0).id;
					model.schedule = schedule;
					Log.e(TAG, "排期号:" + schedule);
				}
			}

			model.fileName = task.fileName;
			model.status = "7";
			list.add(model);
			Log.e(TAG, "===============commitFileProcess=================");
			mSystemManager.getApplication().httpProcess.commitFileProcess(list);
		}

	}

	private void reportUpdateThemePolicy() {

		Message msg = new Message();

		msg.what = ConstData.SystemMsgType.MSG_UPDATE_THEME_POLICY;
		msg.arg1 = SystemManager.getSystemManager().getConfig().downloadPolicyType;

		mSystemManager.sendMsgToSystem(msg);
	}

	private void reportDownloadError(int type) {

		Message msg = new Message();

		msg.what = ConstData.SystemMsgType.MSG_UPDATE_ERROR;
		msg.arg1 = SystemManager.getSystemManager().getConfig().downloadPolicyType;
		msg.arg2 = type;

		mSystemManager.sendMsgToSystem(msg);
	}

	private void reportUpdateApp(String appPath) {
		Message msg = new Message();

		msg.what = ConstData.SystemMsgType.MSG_UPDATE_APP;
		msg.obj = appPath;

		mSystemManager.sendMsgToSystem(msg);
	}

	private int checkFilesDownloadFinish() {
		SystemConfig config = SystemManager.getSystemManager().getConfig();
		config.downloadPolicyPath = config.readParam("downloadPolicyPath",
				config.downloadPolicyPath);

		SystemManager.LOGD(TAG, "checkFilesDownloadFinish playPlan:"
				+ config.downloadPolicyPath);

		if (config.downloadPolicyPath != null) {
			if (config.downloadPolicyPath.length() < 1) {
				return -1;
			}

			ResPlayPlan playPlan = ResFileParse
					.getPlayPlanByPolicy(config.downloadPolicyPath);
			SystemManager.LOGD(TAG, "checkFilesDownloadFinish playPlan:"
					+ playPlan);
			if (playPlan != null) {
				SystemManager.LOGD(TAG,
						"checkFilesDownloadFinish playPlan.isFilesExist:"
								+ playPlan.isFilesExist);
				// --判断所有资源文件是否下载完成
				if (playPlan.isFilesExist) {
					SystemManager
							.LOGD(TAG,
									"checkFilesDownloadFinish: DOWNLOAD ALL FILES FINISH ====");
					// --标记当前的下载类型 和 策略路径
					if (config.downloadPolicyType == ConstData.FileTrans.FILE_TYPE_THEME_POLICY) {
						/*
						 * if (config.isNormalExist) { if
						 * (config.normalThemePolicy
						 * .equals(config.downloadPolicyPath)) { return 0;//
						 * --不需要更新界面 } }
						 */
						config.normalThemePolicy = config.downloadPolicyPath;
						config.writeParam("normalThemePolicy",
								config.normalThemePolicy);
						config.isNormalExist = true;
						config.writeParam("isNormalExist", config.isNormalExist);
					} else if (config.downloadPolicyType == ConstData.FileTrans.FILE_TYPE_INSERT_POLICY) {
						/*
						 * if(config.isInsertExist){
						 * if(config.insertThemePolicy.
						 * equals(config.downloadPolicyPath)){ return
						 * 0;//--不需要更新界面 } }
						 */
						config.insertThemePolicy = config.downloadPolicyPath;
						config.writeParam("insertThemePolicy",
								config.insertThemePolicy);
						config.isInsertExist = true;
						config.writeParam("isInsertExist", config.isInsertExist);
					} else if (config.downloadPolicyType == ConstData.FileTrans.FILE_TYPE_PAD_POLICY) {
						if (config.isDefaultExist) {
							if (config.defaultThemePolicy
									.equals(config.downloadPolicyPath)) {
								return 0;// --不需要更新界面
							}
						}
						config.defaultThemePolicy = config.downloadPolicyPath;
						config.writeParam("defaultThemePolicy",
								config.defaultThemePolicy);
						config.isDefaultExist = true;
						config.writeParam("isDefaultExist",
								config.isDefaultExist);
					}

					config.downloadPolicyPath = null;
					config.removeParam("downloadPolicyPath");
					return 1;
				}
			}

			playPlan = null;
		}

		return -1;
	}

	private void checkNextResTasks(String path, int type) {
		SystemConfig config = SystemManager.getSystemManager().getConfig();
		// --判断是否资源文件下载完成
		if (type == ConstData.FileTrans.FILE_TYPE_RES) {

			String tmpStoreStr = config.readParam(FTP_STORE_DOWNLOAD_KEY,
					storeStr);
			// --判断当前是否所有任务完成
			if (tmpStoreStr.length() > 3) {

				return;
			}

			SystemManager.LOGI(TAG, "=================================");
			SystemManager.LOGI(TAG, " CHECK FINISH ALL DOWNLOAD TASK !");
			SystemManager.LOGI(TAG, "=================================");
			// --检测是否所有资源文件下载完成
			int ret = checkFilesDownloadFinish();
			if (ret == 1) {

				reportUpdateThemePolicy();
				SystemManager.LOGI(TAG, "===========================");
				SystemManager.LOGI(TAG, " UPDATE THEME POLICY!");
				SystemManager.LOGI(TAG, "===========================");
			}

		}

		// --程序下载完成
		if (type == ConstData.FileTrans.FILE_TYPE_APP) {
			reportUpdateApp(path);
		}
		// --判断是否为xml配置文件
		if (type >= ConstData.FileTrans.FILE_TYPE_RESERVED
				&& type <= ConstData.FileTrans.FILE_TYPE_RES) {
			return;
		}

		// --XML文件解析，并添加到资源下载任务
		switch (type) {

		// --策略文件下载完成
		case ConstData.FileTrans.FILE_TYPE_INSERT_POLICY:
		case ConstData.FileTrans.FILE_TYPE_PAD_POLICY:
		case ConstData.FileTrans.FILE_TYPE_THEME_POLICY:
			SystemManager.LOGD(TAG, "----- parse theme policy " + path);
			ResThemeSwitch themeSwitch = mResFileParse
					.parseThemeSwitchXmlFile(path);
			if (themeSwitch != null) {
				// --将当前需要更新的策略保存下来

				config.downloadPolicyPath = path;
				config.writeParam("downloadPolicyPath",
						config.downloadPolicyPath);
				config.downloadPolicyType = type;
				config.writeParam("downloadPolicyType",
						config.downloadPolicyType);

				// --检测是否所有资源文件下载完成
				int ret = checkFilesDownloadFinish();
				if (ret == 1) {
					reportUpdateThemePolicy();// 已经下载文件完成，需要更新界面
				} else if (ret < 0) {
					// 未下载文件完成，需要添加下载任务
					for (ThemePolicy policy : themeSwitch.mThemePolicyList) {
						SystemManager.LOGD(TAG, "next theme url:" + policy.url);
						doAddDownloadTask(ConstData.FileTrans.FILE_TYPE_THEME,
								policy.url);
					}
					if (themeSwitch.mThemePolicyList.size() == 0) {
						reportDownloadError(ConstData.DownloadError.ERROR_UNKNONWN);
					}
				}

				// for (ThemePolicy policy : themeSwitch.mThemePolicyList) {
				// SystemManager.LOGD(TAG, "next theme url:" + policy.url);
				// if (policy.url != null) {
				// doAddDownloadTask(ConstData.FileTrans.FILE_TYPE_THEME,
				// policy.url);
				// }
				// }
				//
				// for (ThemePolicy ext : themeSwitch.mExtThemePolicyList) {
				// SystemManager.LOGD(TAG, "next theme url:" + ext.url);
				// if (ext.url != null) {
				// doAddDownloadTask(ConstData.FileTrans.FILE_TYPE_THEME,
				// ext.url);
				// }
				// }
			}
			break;
		// --主题布局下载完成
		case ConstData.FileTrans.FILE_TYPE_THEME:
			SystemManager.LOGD(TAG, "----- parse theme " + path);
			ResTheme theme = mResFileParse.parseThemeXmlFile(path);
			if (theme != null) {
				if (theme.getForm() != null) {
					if (theme.getForm().background != null) {
						SystemManager.LOGD(TAG, "next form background url:"
								+ theme.getForm().background);
						doAddDownloadTask(ConstData.FileTrans.FILE_TYPE_RES,
								theme.getForm().background);
					}
					for (Image image : theme.getForm().mImageList) {
						SystemManager.LOGD(TAG, "next image url:"
								+ image.playbill);
						if (image.playbill != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_PLAYBILL,
									image.playbill);
						}
					}
					for (Video video : theme.getForm().mVideoList) {
						SystemManager.LOGD(TAG, "next video url:"
								+ video.playbill);
						if (video.playbill != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_PLAYBILL,
									video.playbill);
						}
					}
					for (Label label : theme.getForm().mLabelList) {
						SystemManager.LOGD(TAG, "next label url:"
								+ label.playbill);
						if (label.playbill != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_PLAYBILL,
									label.playbill);
						}

					}

					for (Appcode app : theme.getForm().mAppcodeList) {
						SystemManager.LOGD(TAG, "next label url:"
								+ app.playbill);
						if (app.playbill != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_PLAYBILL,
									app.playbill);
						}

					}

					// --若为clock控件，所涉及的属性参数存在于theme中，无需继续下载playbill和元素文件
					if (theme.getForm().mClockList.size() > 0) {
						int ret = checkFilesDownloadFinish();
						if (ret == 1) {
							reportUpdateThemePolicy();
							break;
						}
					}
				}

			}
			break;
		// --播放清单下载完成
		case ConstData.FileTrans.FILE_TYPE_PLAYBILL:
			SystemManager.LOGD(TAG, "----- parse playbill " + path);
			ResPlaybill playbill = mResFileParse.parsePlaybillXmlFile(path);
			if (playbill != null) {
				for (PlayList playlist : playbill.mPlayList) {
					if (playlist.mImageAd != null) {
						SystemManager.LOGD(TAG, "----- image url +"
								+ playlist.mImageAd.url);
						if (playlist.mImageAd.url != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_RES,
									playlist.mImageAd.url);
						}
					}
					if (playlist.mVideoAd != null) {
						SystemManager.LOGD(TAG, "----- video url +"
								+ playlist.mVideoAd.url);
						if (playlist.mVideoAd.url != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_RES,
									playlist.mVideoAd.url);
						}
					}
					if (playlist.mLabelAd != null) {
						int ret = checkFilesDownloadFinish();

						if (ret == 1) {
							reportUpdateThemePolicy();
							break;
						}
					}

					if (playlist.mAppcodeAd != null) {
						if (playlist.mAppcodeAd.rescode / 1000 == 1
								&& playlist.mAppcodeAd.url != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_RES,
									playlist.mAppcodeAd.url);
						} else {
							int ret = checkFilesDownloadFinish();
							if (ret == 1) {
								reportUpdateThemePolicy();
								break;
							}
						}
					}
				}

				for (PlayList playlist : playbill.mExtPlayList) {
					if (playlist.mImageAd != null) {
						if (playlist.mImageAd.url != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_RES,
									playlist.mImageAd.url);
						}
					}
					if (playlist.mVideoAd != null) {
						if (playlist.mVideoAd.url != null) {
							doAddDownloadTask(
									ConstData.FileTrans.FILE_TYPE_RES,
									playlist.mVideoAd.url);
						}
					}
					if (playlist.mLabelAd != null) {
						int ret = checkFilesDownloadFinish();
						if (ret == 1) {
							reportUpdateThemePolicy();
							break;
						}
					}
				}
			}
			break;
		}

	}
}
