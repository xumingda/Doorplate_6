package com.advertisement.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.advertisement.resource.ResPlaybill.PlayList;
import com.advertisement.resource.ResPlaybill.PlayList.AdBase;
import com.advertisement.resource.ResPlaybill.PlayList.AdBase.Periods;
import com.advertisement.resource.ResPlaybill.PlayList.ImageAd;
import com.advertisement.resource.ResPlaybill.PlayList.LabelAd;
import com.advertisement.resource.ResPlaybill.PlayList.VideoAd;
import com.advertisement.resource.ResPlaybill.PlayList.AppcodeAd;
import com.advertisement.resource.ResTheme.Form.Appcode;
import com.advertisement.resource.ResTheme.Form.Clock;
import com.advertisement.resource.ResTheme.Form.Image;
import com.advertisement.resource.ResTheme.Form.Label;
import com.advertisement.resource.ResTheme.Form.Panel;
import com.advertisement.resource.ResTheme.Form.Video;
import com.advertisement.resource.ResThemeSwitch.ThemePolicy;
import com.advertisement.system.ConstData;
import com.advertisement.system.SystemConfig;
import com.advertisement.system.SystemManager;
import com.common.XmlParse;
import com.common.XmlParse.ParseXmlHandler;

public class ResFileParse {

	private static final String TAG = "ResFileParse";

	public static class FileParamer {

		public String ftpUrl = null;
		public String loginUser = null;
		public String loginPwd = null;
		public String ftpIp = null;
		public int ftpPort = 0;
		public String fileRemotePath = null;
		public String fileName = null;
		public String fileFolder = null;
		public String fileLocalPath = null;

		public void parseXmlFile(String url, String rootPath) {
			if (url == null) {
				return;
			}

			// ftp://smedia:smedia@120.25.65.121:51/upload/themeswitch/201512/res_themeswitch_t73_20151227164541808.xml
			int startIndex = 0;
			int stopIndex = 0;
			ftpUrl = url;

			String tmp;
			startIndex = ftpUrl.indexOf("ftp://");
			if (startIndex != 0) {
				SystemManager.LOGE(TAG, "Can't parse url");
				return;
			}
			startIndex += 6;
			tmp = ftpUrl.substring(startIndex);
			stopIndex = tmp.indexOf(":");
			loginUser = tmp.substring(0, stopIndex);
			startIndex = stopIndex + 1;
			tmp = tmp.substring(startIndex);
			stopIndex = tmp.indexOf("@");
			loginPwd = tmp.substring(0, stopIndex);
			startIndex = stopIndex + 1;
			tmp = tmp.substring(startIndex);
			stopIndex = tmp.indexOf(":");
			ftpIp = tmp.substring(0, stopIndex);
			startIndex = stopIndex + 1;
			tmp = tmp.substring(startIndex);
			stopIndex = tmp.indexOf("/");
			ftpPort = Integer.valueOf(tmp.substring(0, stopIndex));
			startIndex = stopIndex + 1;
			fileRemotePath = tmp.substring(startIndex);
			String[] strs = fileRemotePath.split("/");

			if (strs.length == 4) {

				fileName = strs[3];
				fileFolder = strs[0] + "/" + strs[1] + "/" + strs[2] + "/";

			}
			fileLocalPath = rootPath + "/" + fileFolder + fileName;
		}

		public FileParamer(String url) {

			parseXmlFile(url, SystemConfig.getStoreRootPath());
		}

		public FileParamer(String url, String rootPath) {
			parseXmlFile(url, rootPath);
		}
	}

	public static ResThemeSwitch parseThemeSwitchXmlFile(String filePath) {

		File fp = new File(filePath);
		if (!fp.exists())
			return null;
		XmlPullParser parser = Xml.newPullParser();
		InputStream inStream;
		int tmp = 0;
		ResThemeSwitch themeSwitch = null;
		try {
			inStream = new FileInputStream(filePath);

			try {
				parser.setInput(inStream, "UTF-8");
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				int eventType = parser.getEventType();

				while (eventType != XmlPullParser.END_DOCUMENT) {

					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理

						break;
					case XmlPullParser.START_TAG:// 开始元素事件

						if (parser.getName().equals("root")) {
							if (themeSwitch == null) {
								themeSwitch = new ResThemeSwitch();
							}
							tmp = 0;
						} else if (parser.getName().equals("theme_policy")) {

							tmp = 1;

						} else if (parser.getName().equals("theme_ext")) {

							tmp = 2;

						} else if (parser.getName().equals("theme")) {
							if (tmp == 1) {
								themeSwitch.addThemePolicy(XmlParse
										.getAttributeString(parser, "id"),
										XmlParse.getAttributeString(parser,
												"name"), XmlParse
												.getAttributeString(parser,
														"period"), XmlParse
												.getAttributeString(parser,
														"act"), XmlParse
												.getAttributeString(parser,
														"url"));

							} else if (tmp == 2) {

								themeSwitch.addExtThemePolicy(XmlParse
										.getAttributeString(parser, "id"),
										XmlParse.getAttributeString(parser,
												"name"), XmlParse
												.getAttributeString(parser,
														"period"), XmlParse
												.getAttributeString(parser,
														"act"), XmlParse
												.getAttributeString(parser,
														"url"));

							}
						}

						break;
					case XmlPullParser.END_TAG:// 结束元素事件

						break;
					}

					try {
						eventType = parser.next();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					inStream.close();
					return themeSwitch;
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		return themeSwitch;
	}

	public static ResTheme parseThemeXmlFile(String filePath) {

		File fp = new File(filePath);
		if (!fp.exists())
			return null;
		XmlPullParser parser = Xml.newPullParser();
		InputStream inStream;

		ResTheme theme = null;
		try {
			inStream = new FileInputStream(filePath);

			try {
				parser.setInput(inStream, "UTF-8");
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			try {
				int eventType = parser.getEventType();

				while (eventType != XmlPullParser.END_DOCUMENT) {

					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理

						break;
					case XmlPullParser.START_TAG:// 开始元素事件

						if (parser.getName().equals("form")) {
							if (theme == null) {
								theme = new ResTheme();
							}
							theme.addForm(XmlParse.getAttributeString(parser,
									"Name"), XmlParse.getAttributeString(
									parser, "ID"), XmlParse.getAttributeString(
									parser, "background"), XmlParse
									.getAttributeInt(parser, "Width"), XmlParse
									.getAttributeInt(parser, "Height"));

						} else if (parser.getName().equals("Image")) {
							if (theme.getForm() == null) {
								return theme;
							}
							theme.getForm()
									.addImage(
											XmlParse.getAttributeString(parser,
													"Id"),
											XmlParse.getAttributeInt(parser,
													"Left"),
											XmlParse.getAttributeInt(parser,
													"Top"),
											XmlParse.getAttributeInt(parser,
													"Width"),
											XmlParse.getAttributeInt(parser,
													"Height"),
											XmlParse.getAttributeString(parser,
													"Color"),
											XmlParse.getAttributeString(parser,
													"BgColor"),
											XmlParse.getAttributeString(parser,
													"Playbill"),
											XmlParse.getAttributeInt(parser,
													"Rate"),
											XmlParse.getAttributeInt(parser,
													"Transp"));
						} else if (parser.getName().equals("Video")) {
							if (theme.getForm() == null) {
								return theme;
							}
							theme.getForm()
									.addVideo(
											XmlParse.getAttributeString(parser,
													"Id"),
											XmlParse.getAttributeInt(parser,
													"Left"),
											XmlParse.getAttributeInt(parser,
													"Top"),
											XmlParse.getAttributeInt(parser,
													"Width"),
											XmlParse.getAttributeInt(parser,
													"Height"),
											XmlParse.getAttributeString(parser,
													"Color"),
											XmlParse.getAttributeString(parser,
													"BgColor"),
											XmlParse.getAttributeString(parser,
													"Playbill"));
						} else if (parser.getName().equals("Label")) {
							if (theme.getForm() == null) {
								return theme;
							}
							theme.getForm()
									.addLabel(
											XmlParse.getAttributeString(parser,
													"Id"),
											XmlParse.getAttributeInt(parser,
													"Left"),
											XmlParse.getAttributeInt(parser,
													"Top"),
											XmlParse.getAttributeInt(parser,
													"Width"),
											XmlParse.getAttributeInt(parser,
													"Height"),
											XmlParse.getAttributeString(parser,
													"Color"),
											XmlParse.getAttributeString(parser,
													"BgColor"),
											XmlParse.getAttributeString(parser,
													"Playbill"),
											XmlParse.getAttributeString(parser,
													"FontName"),
											XmlParse.getAttributeInt(parser,
													"FontSize"),
											XmlParse.getAttributeString(parser,
													"FLY"),
											XmlParse.getAttributeInt(parser,
													"Rate"));
						} else if (parser.getName().equals("Clock")) {
							if (theme.getForm() == null) {
								return theme;
							}
							theme.getForm()
									.addClock(
											XmlParse.getAttributeString(parser,
													"Id"),
											XmlParse.getAttributeInt(parser,
													"Left"),
											XmlParse.getAttributeInt(parser,
													"Top"),
											XmlParse.getAttributeInt(parser,
													"Width"),
											XmlParse.getAttributeInt(parser,
													"Height"),
											XmlParse.getAttributeString(parser,
													"Color"),
											XmlParse.getAttributeString(parser,
													"BgColor"),
											XmlParse.getAttributeString(parser,
													"Playbill"),
											XmlParse.getAttributeString(parser,
													"FontName"),
											XmlParse.getAttributeInt(parser,
													"FontSize"),
											XmlParse.getAttributeString(parser,
													"TimeFormat"),
											XmlParse.getAttributeInt(parser,
													"Transp"));

						} else if (parser.getName().equals("Appcode")) {
							if (theme.getForm() == null) {
								return theme;
							}

							theme.getForm()
									.addAppcode(
											XmlParse.getAttributeString(parser,
													"Id"),
											XmlParse.getAttributeInt(parser,
													"Left"),
											XmlParse.getAttributeInt(parser,
													"Top"),
											XmlParse.getAttributeInt(parser,
													"Width"),
											XmlParse.getAttributeInt(parser,
													"Height"),
											XmlParse.getAttributeString(parser,
													"Color"),
											XmlParse.getAttributeString(parser,
													"BgColor"),
											XmlParse.getAttributeString(parser,
													"Playbill"));
						}

						break;
					case XmlPullParser.END_TAG:// 结束元素事件

						break;
					}

					try {
						eventType = parser.next();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					inStream.close();
					return theme;
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		return theme;
	}

	public static ResPlaybill parsePlaybillXmlFile(String filePath) {
		return parsePlaybillXmlFileBase(filePath, SystemManager
				.getSystemManager().getConfig().getStoreRootPath());
	}

	public static ResPlaybill parsePlaybillXmlFileBase(String filePath,
			String rootPath) {

		File fp = new File(filePath);
		if (!fp.exists())
			return null;
		XmlPullParser parser = Xml.newPullParser();
		InputStream inStream;

		ResPlaybill playbill = null;
		ResPlaybill.PlayList playList = null;
		int type = getPlaybillType(filePath, rootPath);
		AdBase mAd = null;

		int tmp = 0;
		boolean isExt = false;
		String text = null;
		SystemManager.LOGD(TAG, "play bill type :" + type);

		if (type == 0) {
			return null;
		}
		try {
			inStream = new FileInputStream(filePath);

			try {
				parser.setInput(inStream, "UTF-8");
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			try {
				int eventType = parser.getEventType();

				while (eventType != XmlPullParser.END_DOCUMENT) {

					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理

						break;
					case XmlPullParser.TEXT:

						if (parser.getText() == null) {
							break;
						}

						if (tmp == 1) {
							break;
						}
						if (tmp == 2) {
							mAd.id = Integer.parseInt(parser.getText());
						} else if (tmp == 3) {
							SystemManager.LOGD(TAG, "mAd:" + mAd + " parser:"
									+ parser);
							mAd.url = parser.getText();
							SystemManager.LOGD(TAG, "Playlist url:" + mAd.url);
							tmp = 0;
						} else if (tmp == 4) {
							mAd.name = parser.getText();

						} else if (tmp == 5) {
							mAd.pri = Integer.parseInt(parser.getText());
						} else if (tmp == 6) {
							mAd.max = Integer.parseInt(parser.getText());
						} else if (tmp == 7) {
							mAd.periods.max = Integer
									.parseInt(parser.getText());
						} else if (tmp == 8) {
							mAd.length = Integer.parseInt(parser.getText());
						} else if (tmp == 9) {
							((LabelAd) mAd).sytle.fontName = parser.getText();
						} else if (tmp == 10) {
							((LabelAd) mAd).sytle.fontSize = Integer
									.parseInt(parser.getText());
						} else if (tmp == 11) {
							((LabelAd) mAd).sytle.color = parser.getText();
						} else if (tmp == 12) {
							((LabelAd) mAd).sytle.bgcolor = parser.getText();
						} else if (tmp == 13) {
							((LabelAd) mAd).sytle.scroll = Integer
									.parseInt(parser.getText());
						} else if (tmp == 14) {
							((LabelAd) mAd).sytle.suspend = Integer
									.parseInt(parser.getText());
						} else if (tmp == 15) {
							if (type == ConstData.FileTrans.PLAYBILL_TYPE_VIDEO)
								((VideoAd) mAd).volume = Integer
										.parseInt(parser.getText());
						} else if (tmp == 16) {
							if (type == ConstData.FileTrans.PLAYBILL_TYPE_VIDEO)
								((VideoAd) mAd).tone = parser.getText();
						} else if (tmp == 17) {
							((LabelAd) mAd).sytle.fly = parser.getText();
						} else if (tmp == 200 || tmp == 201) {
							break;
						} else if (tmp == 18) {
							if (type == ConstData.FileTrans.PLAYBILL_TYPE_APPCODE) {
								((AppcodeAd) mAd).rescode = Integer
										.parseInt(parser.getText());
							}
						}

						tmp = 0;
						break;
					case XmlPullParser.START_TAG:// 开始元素事件
						if (parser.getName().equals("playbill")) {
							if (playbill == null) {
								playbill = new ResPlaybill();
							}
							isExt = false;
						} else if (parser.getName().equals("playbill_ext")) {
							if (playbill == null) {
								playbill = new ResPlaybill();
							}
							isExt = true;
						} else if (parser.getName().equals("playlist")) {

							if (XmlParse.getAttributeString(parser, "orderno") != null) {
								if (!isExt) {
									SystemManager
											.LOGD(TAG, "== addPlayList ==");
									playList = playbill.addPlayList(XmlParse
											.getAttributeString(parser,
													"orderno"), XmlParse
											.getAttributeString(parser,
													"datebegin"), XmlParse
											.getAttributeString(parser,
													"dateend"));
									tmp = 0;
								} else {
									SystemManager.LOGD(TAG,
											"== addExtPlayList ==");
									playList = playbill.addExtPlayList(XmlParse
											.getAttributeString(parser,
													"orderno"), XmlParse
											.getAttributeString(parser,
													"datebegin"), XmlParse
											.getAttributeString(parser,
													"dateend"));
									tmp = 0;
								}

							}

						} else if (parser.getName().equals("ad")) {
							if (playList != null) {
								switch (type) {
								case ConstData.FileTrans.PLAYBILL_TYPE_IMAGE:
									mAd = playList.addImageAd();
									break;
								case ConstData.FileTrans.PLAYBILL_TYPE_VIDEO:
									mAd = playList.addVideoAd();
									break;
								case ConstData.FileTrans.PLAYBILL_TYPE_LABEL:
									mAd = playList.addLabelAd();
									break;
								case ConstData.FileTrans.PLAYBILL_TYPE_APPCODE:
									mAd = playList.addAppcodeAd();
									break;
								case ConstData.FileTrans.PLAYBILL_TYPE_CLOCK:
									break;
								case ConstData.FileTrans.PLAYBILL_TYPE_PANEL:
									break;
								}
							}

						} else if (parser.getName().equals("id")) {
							if (mAd != null) {

								tmp = 2;
							}
						} else if (parser.getName().equals("url")) {
							tmp = 3;
						} else if (parser.getName().equals("name")) {
							tmp = 4;
						} else if (parser.getName().equals("pri")) {
							tmp = 5;
						} else if (parser.getName().equals("max")) {
							tmp = 6;
						} else if (parser.getName().equals("periods")) {
							tmp = 1;
						} else if (parser.getName().equals("p")) {
							if (tmp == 1) {
								mAd.addPeriods(XmlParse.getAttributeInt(parser,
										"Loop"), XmlParse.getAttributeString(
										parser, "time"), 0);
								tmp = 7;
							}
						} else if (parser.getName().equals("Length")) {
							tmp = 8;
						} else if (parser.getName().equals("FontName")) {
							tmp = 9;
						} else if (parser.getName().equals("FontSize")) {
							tmp = 10;
						} else if (parser.getName().equals("Color")) {
							tmp = 11;
						} else if (parser.getName().equals("Bgcolor")) {
							tmp = 12;
						} else if (parser.getName().equals("Scroll")) {
							tmp = 13;
						} else if (parser.getName().equals("Suspend")) {
							tmp = 14;
						} else if (parser.getName().equals("Volume")) {

							tmp = 15;
						} else if (parser.getName().equals("Tone")) {
							tmp = 16;
						} else if (parser.getName().equals("Fly")) {
							tmp = 17;
						} else if (parser.getName().equals("rescode")) {
							tmp = 18;
						}
						break;
					case XmlPullParser.END_TAG:// 结束元素事件
						break;
					}

					try {
						eventType = parser.next();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					inStream.close();
					return playbill;
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		return playbill;
	}

	public static int getPlaybillType(String playbillPath, String rootPath) {
		ResTheme theme = null;
		int start = playbillPath.indexOf("res_");
		if (start < 0) {
			return ConstData.FileTrans.PLAYBILL_TYPE_UNDEF;
		}
		/*
		 * String str1=playbillPath.substring(start+4);
		 * 
		 * start=str1.indexOf("_");
		 * 
		 * str1=str1.substring(start+1);
		 * 
		 * int stop=str1.indexOf("_");
		 * 
		 * str1=str1.substring(0, stop);
		 * 
		 * String cmpStr="res_theme_"+str1;
		 */
		String cmpStr = playbillPath.substring(start);
		SystemManager.LOGD(TAG, "cmpStr:" + cmpStr);

		String dirPath = rootPath + "/upload/theme";
		File file = new File(dirPath);

		if (!file.exists()) {
			SystemManager.LOGD(TAG, "getPlaybillType  " + dirPath
					+ " is not exists");
			return 0;
		}

		if (file.isDirectory()) {

			// --跳过日期文件夹
			File[] fileDirs = file.listFiles();

			for (int i = 0; i < fileDirs.length; i++) {

				File dateDir = fileDirs[i];

				if (!dateDir.isDirectory()) {
					continue;
				}

				File[] files = dateDir.listFiles();

				for (int j = 0; j < files.length; j++) {

					SystemManager.LOGD(TAG, "file:" + files[j].getName());

					// if(files[j].getName().indexOf(cmpStr)==0){
					{
						theme = parseThemeXmlFile(dirPath + "/"
								+ dateDir.getName() + "/" + files[j].getName());

						if (theme == null) {
							continue;
						}

						for (Image image : theme.getForm().mImageList) {
							FileParamer fileParamer = new FileParamer(
									image.playbill, rootPath);
							if (fileParamer.fileLocalPath != null) {
								if (fileParamer.fileLocalPath
										.equals(playbillPath)) {
									return ConstData.FileTrans.PLAYBILL_TYPE_IMAGE;// --返回image类型
								}
							}
						}

						for (Video video : theme.getForm().mVideoList) {

							FileParamer fileParamer = new FileParamer(
									video.playbill, rootPath);
							if (fileParamer.fileLocalPath != null) {
								if (fileParamer.fileLocalPath
										.equals(playbillPath)) {
									return ConstData.FileTrans.PLAYBILL_TYPE_VIDEO;// --返回video类型
								}
							}
						}

						for (Label label : theme.getForm().mLabelList) {
							FileParamer fileParamer = new FileParamer(
									label.playbill, rootPath);
							if (fileParamer.fileLocalPath != null) {
								if (fileParamer.fileLocalPath
										.equals(playbillPath)) {
									return ConstData.FileTrans.PLAYBILL_TYPE_LABEL;// --返回label类型
								}
							}
						}

						for (Clock clock : theme.getForm().mClockList) {

							FileParamer fileParamer = new FileParamer(
									clock.playbill, rootPath);
							if (fileParamer.fileLocalPath != null) {
								if (fileParamer.fileLocalPath
										.equals(playbillPath)) {
									return ConstData.FileTrans.PLAYBILL_TYPE_CLOCK;// --返回clock类型
								}
							}
						}

						for (Panel panel : theme.getForm().mPanelList) {
							FileParamer fileParamer = new FileParamer(
									panel.playbill, rootPath);
							if (fileParamer.fileLocalPath != null) {
								if (fileParamer.fileLocalPath
										.equals(playbillPath)) {
									return ConstData.FileTrans.PLAYBILL_TYPE_PANEL;// --返回panel类型
								}
							}
						}

						for (Appcode app : theme.getForm().mAppcodeList) {
							FileParamer fileParamer = new FileParamer(
									app.playbill, rootPath);
							if (fileParamer.fileLocalPath != null) {
								if (fileParamer.fileLocalPath
										.equals(playbillPath)) {
									return ConstData.FileTrans.PLAYBILL_TYPE_APPCODE;// --返回app类型
								}
							}
						}
					}
				}
			}

		}

		return 0;
	}

	public static class ResPlayPlan {

		public ResThemeSwitch mThemeSwitch = null;
		public ArrayList<ResTheme> mThemeList = new ArrayList<ResTheme>();
		public ArrayList<ResPlaybill> mPlaybillList = new ArrayList<ResPlaybill>();
		public ArrayList<String> mFileList = new ArrayList<String>();
		public boolean isFilesExist = false;

		public void setThemeSwitch(ResThemeSwitch themeSwitch) {
			mThemeSwitch = themeSwitch;
		}

		public void addTheme(ResTheme theme) {
			mThemeList.add(theme);
		}

		public void addPlaybill(ResPlaybill playbill) {
			mPlaybillList.add(playbill);
		}

		public void addFile(String filePath) {
			mFileList.add(filePath);
		}
	}

	public static ResPlayPlan getPlayPlanByPolicyBase(String policyPath,
			String rootPath) {
		ResPlayPlan playPlan = new ResPlayPlan();
		boolean isJustPlaybill = false;

		if (!new File(policyPath).exists()) {
			playPlan = null;
			return null;
		}
		// --添加策略结构
		playPlan.setThemeSwitch(parseThemeSwitchXmlFile(policyPath));

		if (playPlan.mThemeSwitch == null) {
			playPlan = null;
			return null;
		}
		// --添加主题文件
		for (ThemePolicy policy : playPlan.mThemeSwitch.mThemePolicyList) {
			if (policy.url != null) {

				FileParamer fileParamer = new FileParamer(policy.url, rootPath);
				if (fileParamer.fileLocalPath != null) {
					if (!new File(fileParamer.fileLocalPath).exists()) {
						continue;
					}

					playPlan.addTheme(parseThemeXmlFile(fileParamer.fileLocalPath));

					SystemManager.LOGI(TAG, "add Theme path:"
							+ fileParamer.fileLocalPath);
				}
			}
		}

		for (ThemePolicy ext : playPlan.mThemeSwitch.mExtThemePolicyList) {
			if (ext.url != null) {

				FileParamer fileParamer = new FileParamer(ext.url, rootPath);
				if (fileParamer.fileLocalPath != null) {
					if (!new File(fileParamer.fileLocalPath).exists()) {
						continue;
					}

					playPlan.addTheme(parseThemeXmlFile(fileParamer.fileLocalPath));

					SystemManager.LOGI(TAG, "add Theme path:"
							+ fileParamer.fileLocalPath);
				}
			}
		}

		// --添加播放计划文件
		for (ResTheme theme : playPlan.mThemeList) {

			if (theme.mForm.background != null) {
				SystemManager.LOGD(TAG, "THEME BG :" + theme.mForm.background);
				FileParamer fileParamer = new FileParamer(
						theme.mForm.background, rootPath);
				if (fileParamer.fileLocalPath != null) {
					if (!new File(fileParamer.fileLocalPath).exists()) {
						playPlan = null;
						return null;
					}
					playPlan.addFile(fileParamer.fileLocalPath);
				}
			}

			for (Image image : theme.mForm.mImageList) {
				if (image.playbill != null) {
					SystemManager
							.LOGD(TAG, "IMAGE PLAYBILL :" + image.playbill);
					FileParamer fileParamer = new FileParamer(image.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						if (!new File(fileParamer.fileLocalPath).exists()) {
							playPlan = null;
							return null;
						}
						playPlan.addPlaybill(parsePlaybillXmlFileBase(
								fileParamer.fileLocalPath, rootPath));
					}
				}
			}

			for (Video video : theme.mForm.mVideoList) {
				if (video.playbill != null) {

					FileParamer fileParamer = new FileParamer(video.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						if (!new File(fileParamer.fileLocalPath).exists()) {
							playPlan = null;
							return null;
						}
						playPlan.addPlaybill(parsePlaybillXmlFileBase(
								fileParamer.fileLocalPath, rootPath));
					}
				}
			}

			for (Label label : theme.mForm.mLabelList) {
				if (label.playbill != null) {
					FileParamer fileParamer = new FileParamer(label.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						if (!new File(fileParamer.fileLocalPath).exists()) {
							playPlan = null;
							return null;
						}
						playPlan.addPlaybill(parsePlaybillXmlFileBase(
								fileParamer.fileLocalPath, rootPath));
					}
				}
			}

			for (Appcode app : theme.mForm.mAppcodeList) {
				if (app.playbill != null) {
					FileParamer fileParamer = new FileParamer(app.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						if (!new File(fileParamer.fileLocalPath).exists()) {
							playPlan = null;
							return null;
						}
						playPlan.addPlaybill(parsePlaybillXmlFileBase(
								fileParamer.fileLocalPath, rootPath));
					}
				}
			}

			for (Clock clock : theme.mForm.mClockList) {
				if (clock.playbill != null) {
					FileParamer fileParamer = new FileParamer(clock.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						if (!new File(fileParamer.fileLocalPath).exists()) {
							playPlan = null;
							return null;
						}
						playPlan.addPlaybill(parsePlaybillXmlFileBase(
								fileParamer.fileLocalPath, rootPath));
					}
				} else {
					// --CLOCK属性存在于theme配置文件选中，无Playbill文件需要配置
					isJustPlaybill = true;
				}
			}

			for (Panel panel : theme.mForm.mPanelList) {
				if (panel.playbill != null) {
					FileParamer fileParamer = new FileParamer(panel.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						if (!new File(fileParamer.fileLocalPath).exists()) {
							playPlan = null;
							return null;
						}
						playPlan.addPlaybill(parsePlaybillXmlFileBase(
								fileParamer.fileLocalPath, rootPath));
					}
				}
			}
		}

		// --根据playbill查询目录当前的文件是否可用
		for (ResPlaybill playbill : playPlan.mPlaybillList) {
			for (PlayList playlist : playbill.mPlayList) {
				if (playlist.mImageAd != null) {
					SystemManager.LOGD(TAG, "IMAGE url:"
							+ playlist.mImageAd.url);
					if (playlist.mImageAd.url != null) {
						FileParamer fileParamer = new FileParamer(
								playlist.mImageAd.url, rootPath);

						if (fileParamer.fileLocalPath != null) {
							if (!new File(fileParamer.fileLocalPath).exists()) {
								playPlan = null;
								return null;
							}
							playPlan.addFile(fileParamer.fileLocalPath);
						}
					}
				}
				if (playlist.mVideoAd != null) {
					if (playlist.mVideoAd.url != null) {
						SystemManager.LOGD(TAG, "VIDEO url:"
								+ playlist.mVideoAd.url);
						FileParamer fileParamer = new FileParamer(
								playlist.mVideoAd.url, rootPath);
						if (fileParamer.fileLocalPath != null) {
							if (!new File(fileParamer.fileLocalPath).exists()) {
								playPlan = null;
								return null;
							}
							playPlan.addFile(fileParamer.fileLocalPath);
						}
					}
				}

				if (playlist.mLabelAd != null) {
					isJustPlaybill = true;
				}

				if (playlist.mAppcodeAd != null) {
					isJustPlaybill = true;
				}
			}

			for (PlayList playlist : playbill.mExtPlayList) {
				if (playlist.mImageAd != null) {
					if (playlist.mImageAd.url != null) {
						FileParamer fileParamer = new FileParamer(
								playlist.mImageAd.url, rootPath);
						if (fileParamer.fileLocalPath != null) {
							if (!new File(fileParamer.fileLocalPath).exists()) {
								playPlan = null;
								return null;
							}
							playPlan.addFile(fileParamer.fileLocalPath);
						}
					}
				}
				if (playlist.mVideoAd != null) {
					if (playlist.mVideoAd.url != null) {
						FileParamer fileParamer = new FileParamer(
								playlist.mVideoAd.url, rootPath);
						if (fileParamer.fileLocalPath != null) {
							if (!new File(fileParamer.fileLocalPath).exists()) {
								playPlan = null;
								return null;
							}
							playPlan.addFile(fileParamer.fileLocalPath);
						}
					}
				}

				if (playlist.mLabelAd != null) {
					isJustPlaybill = true;
				}

			}
		}

		// --判断所有文件是否存在
		playPlan.isFilesExist = false;
		if (isJustPlaybill) {
			playPlan.isFilesExist = true;
		} else {
			for (String path : playPlan.mFileList) {
				if (new File(path).exists()) {
					playPlan.isFilesExist = true;
				} else {
					playPlan.isFilesExist = false;
				}
			}
		}

		return playPlan;
	}

	public static ResPlayPlan getPlayPlanByPolicy(String policyPath) {
		return getPlayPlanByPolicyBase(policyPath,
				SystemConfig.getStoreRootPath());
	}

	public static ArrayList<String> getFilePathListByPolicy(String policyPath,
			String rootPath) {

		ResPlayPlan plan = ResFileParse.getPlayPlanByPolicyBase(rootPath + "/"
				+ policyPath, rootPath);

		if (plan == null || !plan.isFilesExist) {
			return null;
		}

		ArrayList<String> list = new ArrayList<String>();
		// --添加策略文件自身
		list.add(policyPath);
		// --添加主题布局文件
		for (ThemePolicy policy : plan.mThemeSwitch.mThemePolicyList) {

			FileParamer fileParamer = new FileParamer(policy.url, rootPath);
			if (fileParamer != null) {
				if (fileParamer.fileLocalPath != null) {
					list.add(fileParamer.fileFolder + fileParamer.fileName);
				}
			}
		}

		// --添加播放计划
		for (ResTheme theme : plan.mThemeList) {

			if (theme.mForm.background != null) {
				SystemManager.LOGD(TAG, "THEME BG :" + theme.mForm.background);
				FileParamer fileParamer = new FileParamer(
						theme.mForm.background, rootPath);
				if (fileParamer.fileLocalPath != null) {
					list.add(fileParamer.fileFolder + fileParamer.fileName);
				}
			}

			for (Image image : theme.mForm.mImageList) {
				if (image.playbill != null) {
					FileParamer fileParamer = new FileParamer(image.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						list.add(fileParamer.fileFolder + fileParamer.fileName);
					}
				}
			}
			for (Video video : theme.mForm.mVideoList) {
				if (video.playbill != null) {
					FileParamer fileParamer = new FileParamer(video.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						list.add(fileParamer.fileFolder + fileParamer.fileName);
					}
				}
			}
			for (Clock clock : theme.mForm.mClockList) {
				if (clock.playbill != null) {
					FileParamer fileParamer = new FileParamer(clock.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						list.add(fileParamer.fileFolder + fileParamer.fileName);
					}
				}
			}
			for (Label label : theme.mForm.mLabelList) {
				if (label.playbill != null) {
					FileParamer fileParamer = new FileParamer(label.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						list.add(fileParamer.fileFolder + fileParamer.fileName);
					}
				}
			}
			for (Panel panel : theme.mForm.mPanelList) {
				if (panel.playbill != null) {
					FileParamer fileParamer = new FileParamer(panel.playbill,
							rootPath);
					if (fileParamer.fileLocalPath != null) {
						list.add(fileParamer.fileFolder + fileParamer.fileName);
					}
				}
			}
		}

		// --添加文件
		for (ResPlaybill playbill : plan.mPlaybillList) {

			for (PlayList playlist : playbill.mPlayList) {
				if (playlist.mImageAd != null) {
					if (playlist.mImageAd.url != null) {
						FileParamer fileParamer = new FileParamer(
								playlist.mImageAd.url, rootPath);
						if (fileParamer.fileLocalPath != null) {
							list.add(fileParamer.fileFolder
									+ fileParamer.fileName);
						}
					}
				}
				if (playlist.mVideoAd != null) {
					if (playlist.mVideoAd.url != null) {
						FileParamer fileParamer = new FileParamer(
								playlist.mVideoAd.url, rootPath);
						if (fileParamer.fileLocalPath != null) {
							if (fileParamer.fileLocalPath != null) {
								list.add(fileParamer.fileFolder
										+ fileParamer.fileName);
							}
						}
					}
				}

			}

			for (PlayList playlist : playbill.mExtPlayList) {
				if (playlist.mImageAd != null) {
					if (playlist.mImageAd.url != null) {
						FileParamer fileParamer = new FileParamer(
								playlist.mImageAd.url, rootPath);
						if (fileParamer.fileLocalPath != null) {
							if (fileParamer.fileLocalPath != null) {
								list.add(fileParamer.fileFolder
										+ fileParamer.fileName);
							}
						}
					}
				}
				if (playlist.mVideoAd != null) {
					if (playlist.mVideoAd.url != null) {
						FileParamer fileParamer = new FileParamer(
								playlist.mVideoAd.url, rootPath);
						if (fileParamer.fileLocalPath != null) {
							if (fileParamer.fileLocalPath != null) {
								list.add(fileParamer.fileFolder
										+ fileParamer.fileName);
							}
						}
					}
				}
			}
		}

		return list;
	}
}
