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
 * ��Ļ��
 * @package com.farben.ams.widget
 * @author Keynes Cao
 * @create 2011-12-5 ����3:21:25 * 
 */
public class TextMove implements Callback{

    /**
     *�Ƿ���� 
     */
    private boolean         isMove = false;
    /**
     * �ƶ�����
     */
    private int             orientation = 0;
    /**
     * �����ƶ�
     */
    public final static int MOVE_LEFT = 0;
    /**
     * �����ƶ�
     */
    public final static int MOVE_RIGHT = 1;
    /**
     * �ƶ��ٶȡ�1.5s���ƶ�һ��
     */
    private long             speed = 1;
    /**
     *��Ļ���� 
     */
    private String             content;
    
    /**
     * ��Ļ����ɫ
     * */
    private String             bgColor = "#000000";//"#E7E7E7";
    
    /**
     * ��Ļ͸���ȡ�Ĭ�ϣ�60
     */
    private int             bgalpha = 0;
    
    /**
     * ������ɫ ��Ĭ�ϣ���ɫ (#FFFFFF)
     */ 
    private String             fontColor = "#FFFFFF";
    
    /**
     * ����͸���ȡ�Ĭ�ϣ���͸��(255) 
     */
    private int             fontAlpha = 255;
    
    /**
     * �����С ��Ĭ�ϣ�20
     */ 
    private float             fontSize = 40f;
    /**
     * ����
     */
    private SurfaceHolder     mSurfaceHolder;
    /**
     * �߳̿���
     */
    private boolean         loop = true;    
    private boolean         loopBakup=false;
    /**
     * ���ݹ���λ����ʼ����
     */
    private float             x=0;
    
    private float             y=0;
    
    private SurfaceView mSurfaceView;
    
    private Timer mTimer=null;
    
    
    public TextMove(){
    	
    }
    
    /**
     * @param context
     * <see>Ĭ�Ϲ���</see>
     */
    public void setSurfaceView(SurfaceView surfaceview) {
        
        mSurfaceHolder = surfaceview.getHolder();
        mSurfaceHolder.addCallback(this);
        
        mSurfaceView=surfaceview;
        
        //���û���������Ϊ��ɫ���̳�Surefaceʱ�����������͸��
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        //����ɫ
//        mSurfaceView.setBackgroundColor(Color.parseColor(bgColor));
        //����͸��
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
     * ��ͼ 
     */
    private void draw(){
        //��������
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if(mSurfaceHolder == null || canvas == null){
            return;
        }        
        Paint paint = new Paint();        
        //����
        canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
        //���
        paint.setAntiAlias(true);
        //����
        paint.setTypeface(Typeface.SANS_SERIF);
        //�����С
        paint.setTextSize(fontSize);
        //������ɫ
        paint.setColor(Color.parseColor(fontColor));
        //����͸����
        //paint.setAlpha(fontAlpha);
        //������
         
        canvas.drawText(content,x,y, paint);        
        //������ʾ
        mSurfaceHolder.unlockCanvasAndPost(canvas);
        //����Ч��
        if(isMove){
            //������ռ����
            float conlen = paint.measureText(content);
            //������
            int w = mSurfaceView.getWidth();
            //����
            if(orientation == MOVE_LEFT){//����
                if(x< -conlen){
                    x = w;
                }else{
                    x -= 1;
                }
            }else if(orientation == MOVE_RIGHT){//����
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
     *  <li>����ѡ���ྲ̬����</li>
     *  <li>1.MOVE_RIGHT ���� (Ĭ��)</li>
     *  <li>2.MOVE_LEFT  ����</li>
*/
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    private long getSpeed() {
        return speed;
    }

    /**
     * @param speed
     * <li>�ٶ��Ժ�����������ƶ�֮���ʱ����</li>
     * <li>Ĭ��Ϊ 1500 ����</li>
     */
    public void setSpeed(long speed) {
        this.speed = speed;
    }
    public boolean isMove() {
        return isMove;
    }
    /**
     * @param isMove
     * <see>Ĭ�Ϲ���</see>
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
