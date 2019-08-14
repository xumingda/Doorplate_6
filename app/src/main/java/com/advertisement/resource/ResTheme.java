package com.advertisement.resource;

import java.util.ArrayList;

public class ResTheme {

	public class Form {
		public String name = null; // --ģ������
		public String id = null; // --ģ�����
		public String background = null; // --ģ�汳��ͼ
		public int width = 0; // --ģ����
		public int height = 0; // --ģ��߶�

		public Form(String name, String id, String background, int width,
				int height) {
			this.name = name;
			this.id = id;
			this.background = background;
			this.width = width;
			this.height = height;
		}

		public class BaseForm {
			public String id = null; // --Ԫ�����
			public int left = 0; // --Ԫ�������
			public int top = 0; // --Ԫ�ض���
			public int width = 0; // --Ԫ�ؿ��
			public int height = 0; // --Ԫ�ظ߶�
			public String color = null;// --Ԫ��ǰ��ɫ
			public String bgcolor = null;// --Ԫ�ر���ɫ
			public String playbill = null;// --Ԫ�ز����б�

			public BaseForm(String id, int left, int top, int width,
					int height, String color, String bgcolor, String playbill) {
				this.id = id;
				this.left = left;
				this.top = top;
				this.width = width;
				this.height = height;
				this.color = color;
				this.bgcolor = bgcolor;
				this.playbill = playbill;
			}

		}

		public class Image extends BaseForm {
			public Image(String id, int left, int top, int width, int height,
					String color, String bgcolor, String playbill) {
				super(id, left, top, width, height, color, bgcolor, playbill);
				// TODO Auto-generated constructor stub
			}

			int rate = 0; // --�л��ٶ�
			int transp = 0; // --͸����

			public void setOpt(int rate, int transp) {
				this.rate = rate;
				this.transp = transp;
			}

		}

		public class Video extends BaseForm {

			public Video(String id, int left, int top, int width, int height,
					String color, String bgcolor, String playbill) {
				super(id, left, top, width, height, color, bgcolor, playbill);
			}

		}

		public class Label extends BaseForm {

			public Label(String id, int left, int top, int width, int height,
					String color, String bgcolor, String playbill) {
				super(id, left, top, width, height, color, bgcolor, playbill);
				// TODO Auto-generated constructor stub
			}

			public String fontName = null; // --��������
			public int fontSize = 0; // --�����С
			public String FLY = null; // --�������ģʽ
			public int rate = 0; // --��������ٶ�

			public void setOpt(String fontName, int fontSize, String FLY,
					int rate) {
				this.fontName = fontName;
				this.fontSize = fontSize;
				this.FLY = FLY;
				this.rate = rate;
			}
		}

		public class Appcode extends BaseForm {
			public Appcode(String id, int left, int top, int width, int height,
					String color, String bgcolor, String playbill) {
				// TODO Auto-generated constructor stub

				super(id, left, top, width, height, color, bgcolor, playbill);
			}
		}

		public class Clock extends BaseForm {

			public Clock(String id, int left, int top, int width, int height,
					String color, String bgcolor, String playbill) {
				super(id, left, top, width, height, color, bgcolor, playbill);
				// TODO Auto-generated constructor stub
			}

			public String fontName = null; // --��������
			public int fontSize = 0; // --�����С
			public String timeFormat = null;// --ʱ����ʾ��ʽ
			public int transp = 0; // --͸��ֵ

			public void setOpt(String fontName, int fontSize,
					String timeFormat, int transp) {
				this.fontName = fontName;
				this.fontSize = fontSize;
				this.timeFormat = timeFormat;
				this.transp = transp;
			}

		}

		public class Panel extends BaseForm {

			public Panel(String id, int left, int top, int width, int height,
					String color, String bgcolor, String playbill) {
				super(id, left, top, width, height, color, bgcolor, playbill);
				// TODO Auto-generated constructor stub
			}

		}

		public ArrayList<Image> mImageList = new ArrayList<Image>();
		public ArrayList<Video> mVideoList = new ArrayList<Video>();
		public ArrayList<Label> mLabelList = new ArrayList<Label>();
		public ArrayList<Clock> mClockList = new ArrayList<Clock>();
		public ArrayList<Panel> mPanelList = new ArrayList<Panel>();
		public ArrayList<Appcode> mAppcodeList = new ArrayList<Appcode>();

		public Image addImage(String id, int left, int top, int width,
				int height, String color, String bgcolor, String playbill,
				int rate, int transp) {
			Image image = new Image(id, left, top, width, height, color,
					bgcolor, playbill);
			image.setOpt(rate, transp);
			mImageList.add(image);
			return image;
		}

		public Video addVideo(String id, int left, int top, int width,
				int height, String color, String bgcolor, String playbill) {
			Video video = new Video(id, left, top, width, height, color,
					bgcolor, playbill);
			mVideoList.add(video);
			return video;
		}

		public Label addLabel(String id, int left, int top, int width,
				int height, String color, String bgcolor, String playbill,
				String fontName, int fontSize, String FLY, int rate) {
			Label label = new Label(id, left, top, width, height, color,
					bgcolor, playbill);
			label.setOpt(fontName, fontSize, FLY, rate);
			mLabelList.add(label);
			return label;
		}

		public Clock addClock(String id, int left, int top, int width,
				int height, String color, String bgcolor, String playbill,
				String fontName, int fontSize, String timeFormat, int transp) {
			Clock clock = new Clock(id, left, top, width, height, color,
					bgcolor, playbill);
			clock.setOpt(fontName, fontSize, timeFormat, transp);
			mClockList.add(clock);
			return clock;
		}

		public Appcode addAppcode(String id, int left, int top, int width,
				int height, String color, String bgcolor, String playbill) {
			Appcode appcode = new Appcode(id, left, top, width, height, color,
					bgcolor, playbill);
			mAppcodeList.add(appcode);
			return appcode;
		}
	}

	public Form mForm = null;

	public void addForm(String name, String id, String background, int width,
			int height) {
		mForm = new Form(name, id, background, width, height);
	}

	public Form getForm() {
		return mForm;
	}
}
