package com.scenery7f.timeaxis.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scenery7f.timeaxis.R;
import com.scenery7f.timeaxis.model.PeriodTime;
import com.scenery7f.timeaxis.util.DensityUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by snoopy on 2017/9/15.
 */

public class TimerShaft extends RelativeLayout {

    /**
     * 公用变量
     */
    private Context mContext;

    private DisplayMetrics outMetrics;// 用于计算屏幕尺寸
    private int halfWidth;

    private TextView timeTextView;

    private int width;
    private int height;

    private OnTimeChange onTimeChange;
    public void setOnTimeChange(OnTimeChange onTimeChange) {
        this.onTimeChange = onTimeChange;
    }
    //    private int marginTopBotton = 8;

    /**
     * TimerShaft 使用变量
     */
    private TimerHorizontalScrollView horizontalScrollView;
    private TimerView timerView;

    /**
     * TimerView 使用变量
     */
    private int markColor = Color.GREEN;// 画笔颜色
    private int scaleColor = Color.GRAY;
    public List<PeriodTime> recordList;

    public TimerShaft(Context context) {
        super(context);
        initUI(context);
    }

    public TimerShaft(Context context,int markColor,int scaleColor) {
        super(context);

        this.markColor = markColor;
        this.scaleColor = scaleColor;

        initUI(context);
    }

    public TimerShaft(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TimerShaft);
        markColor = array.getColor(R.styleable.TimerShaft_markColor,Color.GREEN);
        scaleColor = array.getColor(R.styleable.TimerShaft_scaleColor,Color.GRAY);

        initUI(context);
    }


    private void initUI (Context context) {
        mContext = context;

        DensityUtil.setContext(mContext);

        // 获取屏幕尺寸
        outMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(outMetrics);

        initView();

    }

    private void initView() {
        if (horizontalScrollView == null) {
            LayoutParams layoutParams;

            LinearLayout baseLinear = new LinearLayout(mContext);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            baseLinear.setOrientation(LinearLayout.VERTICAL);
            baseLinear.setLayoutParams(layoutParams);
            baseLinear.setGravity(Gravity.CENTER);
            addView(baseLinear);

            timeTextView = new TextView(mContext);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.bottomMargin = DensityUtil.dip2px(1);
            layoutParams.topMargin = DensityUtil.dip2px(1);
            timeTextView.setLayoutParams(layoutParams);
            timeTextView.setBackgroundColor(Color.BLACK);
            timeTextView.setTextSize(DensityUtil.dip2px(4));
            timeTextView.setTextColor(Color.WHITE);
            timeTextView.setText("00:00:00");
            timeTextView.setPadding(DensityUtil.dip2px(2),DensityUtil.dip2px(1),DensityUtil.dip2px(2),DensityUtil.dip2px(1));
            baseLinear.addView(timeTextView);

            RelativeLayout rl = new RelativeLayout(mContext);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            rl.setLayoutParams(layoutParams);
            baseLinear.addView(rl);



            // 滚动视图
            horizontalScrollView = new TimerHorizontalScrollView(mContext);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            horizontalScrollView.setLayoutParams(layoutParams);
            horizontalScrollView.setHorizontalScrollBarEnabled(false);// 不显示滚动条
            rl.addView(horizontalScrollView);

            // 添加视图
            LinearLayout linearLayout = new LinearLayout(mContext);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            linearLayout.setLayoutParams(layoutParams);
            horizontalScrollView.addView(linearLayout);

            // 初始化自定义视图，并添加
            timerView = new TimerView(mContext);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            timerView.setLayoutParams(layoutParams);
            linearLayout.addView(timerView);

            // 创建红色指针线
            LineView redLine = new LineView(mContext);
            layoutParams = new LayoutParams(DensityUtil.dip2px(14),DensityUtil.dip2px(84));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            redLine.setLayoutParams(layoutParams);
            rl.addView(redLine);

        }
    }

    /**
     * 设置数据并刷新页面
     * @param recordList
     */
    public void setRecordList(List<PeriodTime> recordList) {
        this.recordList = recordList;
        timerView.postInvalidate();
    }

    /**
     * 移动到指定时间
     * @param calendar
     */
    public void moveScroll(Calendar calendar) {
        SimpleDateFormat fmtH = new SimpleDateFormat("HH");
        int m  = calendar.get(Calendar.MINUTE);
        int h = Integer.valueOf(fmtH.format(calendar.getTime()));
        int s = calendar.get(Calendar.SECOND);
        horizontalScrollView.scrollTo(timeToX(h,m,s)-halfWidth,0);// 定位到指定位置
        timeTextView.setText(xToTime(horizontalScrollView.getScrollX()));// 显示指定位置代表的时间
//        timeTextView.setText((h>9?h:("0"+h))+":"+(m>9?m:("0"+m))+":"+(s>9?s:("0"+s)));

    }

    /**
     * 根据时间转换成坐标
     * @param h
     * @param m
     * @param s
     * @return
     */
    private int timeToX(int h,int m,int s) {
        int x = halfWidth;

        x += h*60 * 6 + m * 6 + (s%10 == 0?s/10:(s/10+1));

        return x;
    }

    /**
     * 根据位置换成时间
     * @param scrollX
     */
    private String xToTime(int scrollX) {
        int s = scrollX % 6;

        scrollX -= s;
        scrollX /= 6;

        int m = scrollX % 60;

        scrollX -= m;

        int h = scrollX/60;
//        TestUtil.showToast(mContext,"时间："+h+":"+m+":"+s+"0");
        return (h>9?h:("0"+h))+":"+(m>9?m:("0"+m))+":"+s+"0";
    }


    /**
     * ---------设置红色指针---------------------------------------------------
     */
    private class LineView extends View {

        private Paint line;

        public LineView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // 绘制画笔
            initPaint();
            // 绘制竖线
            canvas.drawLine(DensityUtil.dip2px(7),0,DensityUtil.dip2px(7),height - 0,line);
            // 绘制实心三角形
            Path path = new Path();
            path.moveTo(DensityUtil.dip2px(0),0);// 左上角
            path.lineTo(DensityUtil.dip2px(14),0);// 右上角
            path.lineTo(DensityUtil.dip2px(7),DensityUtil.dip2px(8)+0); // 下角
            path.close();
            canvas.drawPath(path,line);
        }

        protected void initPaint() {
            line = new Paint();
            // 设置画笔为抗锯齿
            line.setAntiAlias(true);
            line.setStyle(Paint.Style.FILL);
            line.setStrokeWidth(DensityUtil.dip2px(1));
            line.setColor(Color.RED);
        }

    }

    /**
     * ----------设置横向滚动轴-------------------------------------------------
     */
    private class TimerHorizontalScrollView extends HorizontalScrollView {

        private Handler mHandler;
        /**
         * 记录当前滚动的距离
         */
        private int saveX = 0;

        /**
         * 滚动监听间隔
         */
        private int scrollDealy = 50;
        /**
         * 滚动监听runnable
         */
        private Runnable scrollRunnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(getScrollX() == saveX){
                    //滚动停止  取消监听线程
                    mHandler.removeCallbacks(this);
                    if (onTimeChange != null)
                        onTimeChange.timeChangeOver(xToTime(getScrollX()));
                    return;
                }else{
                    //手指离开屏幕    view还在滚动的时候

                }
                saveX = getScrollX();
                mHandler.postDelayed(this, scrollDealy);
            }
        };

        public TimerHorizontalScrollView(Context context) {
            super(context);

            setHorizontalScrollBarEnabled(false);// 设置不显示滚动条
            saveX = getScrollX();
            mHandler = new Handler();
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    //手指在上面移动的时候   取消滚动监听线程
                    mHandler.removeCallbacks(scrollRunnable);
                    break;
                case MotionEvent.ACTION_UP:
                    //手指移动的时候
                    mHandler.post(scrollRunnable);
                    break;
            }
            return super.onTouchEvent(ev);
        }

        @Override
        protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
            super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
            if (timeTextView != null) {
                timeTextView.setText(xToTime(scrollX));
            }
            if (onTimeChange != null) {
                onTimeChange.timeChangeAction();
            }
        }


    }


    /**
     * ----------创建时间轴刻度---------------------------------------------------
     */
    private class TimerView extends View {

        private Paint scale;// 刻度 灰色
        private Paint mark;// 标记

        public TimerView(Context context) {
            super(context);
            initPaint();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            int width = outMetrics.widthPixels + 6 * 60 * 24;
            int height = DensityUtil.dip2px(84);

            //设置宽度和高度
            setMeasuredDimension(width, height);

            halfWidth = outMetrics.widthPixels/2;

            TimerShaft.this.width = width;
            TimerShaft.this.height = height;

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            drawMark(canvas);
        }

        /**
         * 绘制标记
         */
        private void drawMark(Canvas canvas) {
            if (recordList != null) {
                for (int i=0;i<recordList.size();i++){
                    PeriodTime pt = recordList.get(i);
                    int startx = transform(pt.getStartTime());
                    int endX = transform(pt.getStopTime());

                    canvas.drawRect(startx,0,endX,height - 0,mark);//矩形

                    if (i == recordList.size()-1) {
                        moveScroll(pt.getStartTime());
                    }
                }
                recordList = null;
            }

            drawScale(canvas);
        }


        /**
         * 绘制刻度
         * @param canvas
         */
        private void drawScale(Canvas canvas) {
//          绘制上横线
            canvas.drawLine(0,0,width,0,scale);
//          绘制下横线
            canvas.drawLine(0,height-0,width,height-0,scale);

            // 绘制刻度
            for (int startx=halfWidth;startx<=width-halfWidth;startx+=6) {

                int i = startx - halfWidth;

                if (i%15 == 0) {// 每个十五分钟为一个刻度
                    if (i%(15*6*4) == 0) { // 每个一小时为一个大刻度
                        canvas.drawLine(startx,0,startx,DensityUtil.dip2px(14),scale);// 上刻度
                        canvas.drawLine(startx,height - 0,startx,height - DensityUtil.dip2px(14),scale);// 下刻度
                        int h = i/(15*6*4);
                        canvas.drawText((h>9?h:"0"+h)+":00",startx-DensityUtil.dip2px(14),DensityUtil.dip2px(26),scale);

                    } else if (i%(15*6) == 0) {
                        canvas.drawLine(startx,0,startx,DensityUtil.dip2px(9),scale);// 上刻度
                        canvas.drawLine(startx,height - 0,startx,height - DensityUtil.dip2px(9),scale);// 下刻度
                    } else {
                        canvas.drawLine(startx,0,startx,DensityUtil.dip2px(6),scale);// 上刻度
                        canvas.drawLine(startx,height - 0,startx,height - DensityUtil.dip2px(6),scale);// 下刻度
                    }
                } else {
                    canvas.drawLine(startx,0,startx,DensityUtil.dip2px(3),scale);// 上刻度
                    canvas.drawLine(startx,height - 0,startx,height - DensityUtil.dip2px(3),scale);// 下刻度
                }
            }
        }


        /**
         * 设置 画笔
         */
        private void initPaint() {
            scale = new Paint();
            // 设置画笔为抗锯齿
            scale.setAntiAlias(true);
            scale.setStyle(Paint.Style.FILL);
            scale.setStrokeWidth(DensityUtil.dip2px(1));
            scale.setColor(scaleColor);
            // 设置字体大小
            scale.setTextSize(DensityUtil.dip2px(12));

            mark = new Paint();
            // 设置画笔为抗锯齿
            mark.setAntiAlias(true);
            mark.setStyle(Paint.Style.FILL);
            mark.setColor(markColor);
        }

        private int transform(Calendar c) {

            SimpleDateFormat fmtH = new SimpleDateFormat("HH");
            int m  = c.get(Calendar.MINUTE);
            int h = Integer.valueOf(fmtH.format(c.getTime()));
            int s = c.get(Calendar.SECOND);

            return timeToX(h,m,s);
        }

    }
    public interface OnTimeChange {
        void timeChangeOver(String time);
        void timeChangeAction();
    }
}
