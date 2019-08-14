package com.media;


import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * 字幕区
 * @package com.farben.ams.widget
 * @author Keynes Cao
 * @create 2011-12-5 下午3:21:25 * 
 */
public class TextMove implements Callback{

    /**
     *是否滚动 
     */
    private boolean         isMove = false;
    /**
     * 移动方向
     */
    private int             orientation = 0;
    /**
     * 向左移动
     */
    public final static int MOVE_LEFT = 0;
    /**
     * 向右移动
     */
    public final static int MOVE_RIGHT = 1;
    /**
     * 移动速度　1.5s　移动一次
     */
    private long             speed = 1;
    /**
     *字幕内容 
     */
    private String             content;
    
    /**
     * 字幕背景色
     * */
    private String             bgColor = "#000000";//"#E7E7E7";
    
    /**
     * 字幕透明度　默认：60
     */
    private int             bgalpha = 0;
    
    /**
     * 字体颜色 　默认：白色 (#FFFFFF)
     */ 
    private String             fontColor = "#FFFFFF";
    
    /**
     * 字体透明度　默认：不透明(255) 
     */
    private int             fontAlpha = 255;
    
    /**
     * 字体大小 　默认：20
     */ 
    private float             fontSize = 40f;
    /**
     * 容器
     */
    private SurfaceHolder     mSurfaceHolder;
    /**
     * 线程控制
     */
    private boolean         loop = true;    
    private boolean         loopBakup=false;
    /**
     * 内容滚动位置起始坐标
     */
    private float             x=0;
    
    private float             y=0;
    
    private SurfaceView mSurfaceView;
    
    private Timer mTimer=null;
    
    
    public TextMove(){
    	
    }
    
    /**
     * @param context
     * <see>默认滚动</see>
     */
    public void setSurfaceView(SurfaceView surfaceview) {
        
        mSurfaceHolder = surfaceview.getHolder();
        mSurfaceHolder.addCallback(this);
        
        mSurfaceView=surfaceview;
        
        //设置画布背景不为黑色　继承Sureface时这样处理才能透明
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        //背景色
//        mSurfaceView.setBackgroundColor(Color.parseColor(bgColor));
        //设置透明
        //mSurfaceView.getBackground().setAlpha(bgalpha);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
    	
    	Log.i("TextMove","===surfaceChanged===");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("TextMove","===surfaceCreated===");
        Log. i("WIDTH:",""+mSurfaceView.getWidth());
        
        x=0;
        y=(mSurfaceView.getHeight()/2)+(mSurfaceView.getHeight()/4);
        
        fontSize=(float)(mSurfaceView.getHeight()/2);
        

        
        if(mTimer!=null){
        	mTimer.cancel();
        	mTimer=null;
        }
        mTimer=new Timer();
        mTimer.schedule(new RefreshTask(), 10,speed);
      
    }

    public void stop(){
    	if(mTimer!=null){
        	mTimer.cancel();
        	mTimer=null;
        }
    }
    
    public void pause(){
    	
    	stop();
    }
    
    public void resume(){
    	if(mTimer!=null){
        	mTimer.cancel();
        	mTimer=null;
        }
    	Log.i("TEXTMOVE","=== resume ===");
    	loop=loopBakup;
        mTimer=new Timer();
        mTimer.schedule(new RefreshTask(), 10,speed);
    }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
    	
    	Log.i("TextMove","===surfaceDestroyed===");
    	
        loop = false;
    }
    /**
     * 画图 
     */
    private void draw(){
        //锁定画布
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if(mSurfaceHolder == null || canvas == null){
            return;
        }        
        Paint paint = new Paint();        
        //清屏
        canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
        //锯齿
        paint.setAntiAlias(true);
        //字体
        paint.setTypeface(Typeface.SANS_SERIF);
        //字体大小
        paint.setTextSize(fontSize);
        //字体颜色
        paint.setColor(Color.parseColor(fontColor));
        //字体透明度
        //paint.setAlpha(fontAlpha);
        //画文字
         
        canvas.drawText(content,x,y, paint);        
        //解锁显示
        mSurfaceHolder.unlockCanvasAndPost(canvas);
        //滚动效果
        if(isMove){
            //内容所占像素
            float conlen = paint.measureText(content);
            //组件宽度
            int w = mSurfaceView.getWidth();
            //方向
            if(orientation == MOVE_LEFT){//向左
                if(x< -conlen){
                    x = w;
                }else{
                    x -= 1;
                }
            }else if(orientation == MOVE_RIGHT){//向右
                if(x >= w){
                    x = -conlen;
                }else{
                    x+=1;
                }
            }
        }        
    }

    
    private class RefreshTask extends TimerTask{

		@Override
		public void run() {
			if(loop){
				synchronized (mSurfaceHolder) {
	                draw();
	            }
			}
		}
    	
    }
    
    /******************************set get method***********************************/

    private int getOrientation() {
        return orientation;
    }

    /**
     * @param orientation
     *  <li>可以选择类静态变量</li>
     *  <li>1.MOVE_RIGHT 向右 (默认)</li>
     *  <li>2.MOVE_LEFT  向左</li>
*/
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    private long getSpeed() {
        return speed;
    }

    /**
     * @param speed
     * <li>速度以毫秒计算两次移动之间的时间间隔</li>
     * <li>默认为 1500 毫秒</li>
     */
    public void setSpeed(long speed) {
        this.speed = speed;
    }
    public boolean isMove() {
        return isMove;
    }
    /**
     * @param isMove
     * <see>默认滚动</see>
     */
    public void setMove(boolean isMove) {
        this.isMove = isMove;
    }
    public void setLoop(boolean loop) {
        this.loop = loop;
        this.loopBakup=loop;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }
    public void setBgalpha(int bgalpha) {
        this.bgalpha = bgalpha;
    }
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }
    public void setFontAlpha(int fontAlpha) {
        this.fontAlpha = fontAlpha;
    }
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }
    
}
