package com.advertisement.resource;

import java.util.ArrayList;

public class ResTheme {

	public class Form {
		public String name = null; // --模版名称
		public String id = null; // --模版序号
		public String background = null; // --模版背景图
		public int width = 0; // --模版宽度
		public int height = 0; // --模版高度

		public Form(String name, String id, String background, int width,
				int height) {
			this.name = name;
			this.id = id;
			this.background = background;
			this.width = width;
			this.height = height;
		}

		public class BaseForm {
			public String id = null; // --元素序号
			public int left = 0; // --元素左起点
			public int top = 0; // --元素顶点
			public int width = 0; // --元素宽度
			public int height = 0; // --元素高度
			public String color = null;// --元素前景色
			public String bgcolor = null;// --元素背景色
			public String playbill = null;// --元素播放列表

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

			int rate = 0; // --切换速度
			int transp = 0; // --透明度

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

			public String fontName = null; // --字体名称
			public int fontSize = 0; // --字体大小
			public String FLY = null; // --字体滚动模式
			public int rate = 0; // --字体滚动速度

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

			public String fontName = null; // --字体名称
			public int fontSize = 0; // --字体大小
			public String timeFormat = null;// --时间显示格式
			public int transp = 0; // --透明值

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
