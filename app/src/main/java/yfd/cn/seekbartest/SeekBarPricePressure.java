package yfd.cn.seekbartest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;


/**
 * Created by Administrator on 2016/4/19.
 */
public class SeekBarPricePressure extends View{
    private static final String TAG = "SeekBarPressure";
    private static final int CLICK_ON_LOW = 1;      //点击在前滑块上
    private static final int CLICK_ON_HIGH = 2;     //点击在后滑块上
    private static final int CLICK_IN_LOW_AREA = 3;
    private static final int CLICK_IN_HIGH_AREA = 4;
    private static final int CLICK_OUT_AREA = 5;
    private static final int CLICK_INVAILD = 0;
    /*
     * private static final int[] PRESSED_STATE_SET = {
     * android.R.attr.state_focused, android.R.attr.state_pressed,
     * android.R.attr.state_selected, android.R.attr.state_window_focused, };
     */
    private static final int[] STATE_NORMAL = {};
    private static final int[] STATE_PRESSED = {
            android.R.attr.state_pressed, android.R.attr.state_window_focused,
    };
    private Drawable hasScrollBarBg;        //滑动条滑动后背景图
    private Drawable notScrollBarBg;        //滑动条未滑动背景图
    private Drawable mThumbLow;         //前滑块
    private Drawable mThumbHigh;        //后滑块

    private int mScollBarWidth;     //控件宽度=滑动条宽度+滑动块宽度
    private int mScollBarHeight;    //滑动条高度

    private int mThumbWidth;        //滑动块宽度
    private int mThumbHeight;       //滑动块高度


    private double mOffsetLow = 0;     //前滑块中心坐标
    private double mOffsetHigh = 0;    //后滑块中心坐标
    private int mDistance = 0;      //总刻度是固定距离 两边各去掉半个滑块距离

    private int mThumbMarginTop = 70;   //滑动块顶部距离上边框距离，也就是距离字体顶部的距离

    private int mFlag = CLICK_INVAILD;
    private OnSeekBarChangeListener mBarChangeListener;


    private double defaultScreenLow = 0;    //默认前滑块位置百分比
    private double defaultScreenHigh = 100;  //默认后滑块位置百分比

    private boolean isEdit = false;     //输入框是否正在输入

    private boolean upState = true;

    public SeekBarPricePressure(Context context) {
        this(context, null);
    }

    public SeekBarPricePressure(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPricePressure(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        this.setBackgroundColor(Color.BLACK);

        Resources resources = getResources();
        notScrollBarBg = resources.getDrawable(R.drawable.seekbarpressure_bg_progress);
        hasScrollBarBg = resources.getDrawable(R.drawable.seekbarpressure_bg_normal);
        mThumbLow = resources.getDrawable(R.mipmap.rangbar);
        mThumbHigh = resources.getDrawable(R.mipmap.rangbar);

        mThumbLow.setState(STATE_NORMAL);
        mThumbHigh.setState(STATE_NORMAL);

        mScollBarWidth = notScrollBarBg.getIntrinsicWidth();
        mScollBarHeight = notScrollBarBg.getIntrinsicHeight();

        mThumbWidth = mThumbLow.getIntrinsicWidth();
        mThumbHeight = mThumbLow.getIntrinsicHeight();

    }

    //默认执行，计算view的宽高,在onDraw()之前
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
//        int height = measureHeight(heightMeasureSpec);
        mScollBarWidth = width;
        mOffsetHigh = width - mThumbWidth / 2;
        mOffsetLow = mThumbWidth / 2;
        mDistance = width - mThumbWidth;

        mOffsetLow = formatDouble(defaultScreenLow / 100 * (mDistance ))+ mThumbWidth / 2;
        mOffsetHigh = formatDouble(defaultScreenHigh / 100 * (mDistance)) + mThumbWidth / 2;
        setMeasuredDimension(width, mThumbHeight + mThumbMarginTop+50);
    }


    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //wrap_content
        if (specMode == MeasureSpec.AT_MOST) {
        }
        //fill_parent或者精确值
        else if (specMode == MeasureSpec.EXACTLY) {
        }

        return specSize;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int defaultHeight = 100;
        //wrap_content
        if (specMode == MeasureSpec.AT_MOST) {
        }
        //fill_parent或者精确值
        else if (specMode == MeasureSpec.EXACTLY) {
            defaultHeight = specSize;
        }

        return defaultHeight;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint text_Paint = new Paint();
        text_Paint.setTextAlign(Paint.Align.CENTER);
        text_Paint.setTextSize(44);

        if (upState) {
            text_Paint.setColor(Color.WHITE);
        } else {
            text_Paint.setColor(Color.parseColor("#FE6052"));
        }
        int aaa = mThumbMarginTop + mThumbHeight / 2 - mScollBarHeight / 2;
        int bbb = aaa + mScollBarHeight;

        //白色，不会动
        notScrollBarBg.setBounds(mThumbWidth / 2, aaa, mScollBarWidth - mThumbWidth / 2, bbb);
        notScrollBarBg.draw(canvas);

        //蓝色，中间部分会动
        hasScrollBarBg.setBounds((int)mOffsetLow, aaa, (int)mOffsetHigh, bbb);
        hasScrollBarBg.draw(canvas);

        //前滑块
        mThumbLow.setBounds((int)(mOffsetLow - mThumbWidth / 2), mThumbMarginTop, (int)(mOffsetLow + mThumbWidth / 2), mThumbHeight + mThumbMarginTop);
        mThumbLow.draw(canvas);

        //后滑块
        mThumbHigh.setBounds((int)(mOffsetHigh - mThumbWidth / 2), mThumbMarginTop, (int)(mOffsetHigh + mThumbWidth / 2), mThumbHeight + mThumbMarginTop);
        mThumbHigh.draw(canvas);

        double progressLow = formatDouble((mOffsetLow - mThumbWidth / 2) * 100 / mDistance);
        double progressHigh = formatDouble((mOffsetHigh - mThumbWidth / 2) * 100 / mDistance);
        int temp_min = (int)progressLow * 10;
        int temp_max = (int)progressHigh * 10;
        String tv_min = "";
        String tv_max = "";
        if (temp_min == 0) {
            tv_min = "0";
        } else {
            tv_min = temp_min + "万";
        }
        if (temp_max == 1000) {
            tv_max = "不限";
        } else {
            tv_max = temp_max + "万";
        }
        canvas.drawText(tv_min, (int)mOffsetLow - 2 - 2, 44, text_Paint);
        canvas.drawText(tv_max, (int)mOffsetHigh - 2, 44, text_Paint);

        if (mBarChangeListener != null) {
            if (!isEdit) {
                mBarChangeListener.onProgressChanged(this, progressLow, progressHigh);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //按下
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (mBarChangeListener != null) {
                mBarChangeListener.onProgressBefore();
                isEdit = false;
            }
            mFlag = getAreaFlag(e);
            if (mFlag == CLICK_ON_LOW) {
                mThumbLow.setState(STATE_PRESSED);
            } else if (mFlag == CLICK_ON_HIGH) {
                mThumbHigh.setState(STATE_PRESSED);
            } else if (mFlag == CLICK_IN_LOW_AREA) {
                mThumbLow.setState(STATE_PRESSED);
                //如果点击0-mThumbWidth/2坐标
                if (e.getX() < 0 || e.getX() <= mThumbWidth/2) {
                    mOffsetLow = mThumbWidth/2;
                } else if (e.getX() > mScollBarWidth - mThumbWidth/2) {
                    mOffsetLow = mThumbWidth/2 + mDistance;
                } else {
                    mOffsetLow = formatDouble(e.getX());
                }
            } else if (mFlag == CLICK_IN_HIGH_AREA) {
                mThumbHigh.setState(STATE_PRESSED);
                if(e.getX() >= mScollBarWidth - mThumbWidth/2) {
                    mOffsetHigh = mDistance + mThumbWidth/2;
                } else {
                    mOffsetHigh = formatDouble(e.getX());
                }
            }
            //设置进度条
            upState = false;
            refresh();
            //移动move
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (mFlag == CLICK_ON_LOW) {
                if (e.getX() < 0 || e.getX() <= mThumbWidth/2) {
                    mOffsetLow = mThumbWidth/2;
                } else if (e.getX() >= mScollBarWidth - mThumbWidth/2) {
                    mOffsetLow = mThumbWidth/2 + mDistance;
                    mOffsetHigh = mOffsetLow;
                } else {
                    mOffsetLow = formatDouble(e.getX());
                    if (mOffsetHigh - mOffsetLow <= 0) {
                        mOffsetHigh = (mOffsetLow <= mDistance+mThumbWidth/2) ? (mOffsetLow) : (mDistance+mThumbWidth/2);
                    }
                }
            } else if (mFlag == CLICK_ON_HIGH) {
                if (e.getX() <  mThumbWidth/2) {
                    mOffsetHigh = mThumbWidth/2;
                    mOffsetLow = mThumbWidth/2;
                } else if (e.getX() > mScollBarWidth - mThumbWidth/2) {
                    mOffsetHigh = mThumbWidth/2 + mDistance;
                } else {
                    mOffsetHigh = formatDouble(e.getX());
                    if (mOffsetHigh - mOffsetLow <= 0) {
                        mOffsetLow = (mOffsetHigh >= mThumbWidth/2) ? (mOffsetHigh) : mThumbWidth/2;
                    }
                }
            }
            //设置进度条
            upState = false;
            refresh();
            //抬起
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
//            Log.d("ACTION_UP", "------------------");
            mThumbLow.setState(STATE_NORMAL);
            mThumbHigh.setState(STATE_NORMAL);

            if (mBarChangeListener != null) {
                mBarChangeListener.onProgressAfter();
            }
            upState = true;
            refresh();
        }
        return true;
    }

    public int getAreaFlag(MotionEvent e) {

        int top = mThumbMarginTop;
        int bottom = mThumbHeight + mThumbMarginTop;
        if (e.getY() >= top && e.getY() <= bottom && e.getX() >= (mOffsetLow - mThumbWidth / 2) && e.getX() <= mOffsetLow + mThumbWidth / 2) {
            return CLICK_ON_LOW;
        } else if (e.getY() >= top && e.getY() <= bottom && e.getX() >= (mOffsetHigh - mThumbWidth / 2) && e.getX() <= (mOffsetHigh + mThumbWidth / 2)) {
            return CLICK_ON_HIGH;
        } else if (e.getY() >= top
                && e.getY() <= bottom
                && ((e.getX() >= 0 && e.getX() < (mOffsetLow - mThumbWidth / 2)) || ((e.getX() > (mOffsetLow + mThumbWidth / 2))
                && e.getX() <= ((double) mOffsetHigh + mOffsetLow) / 2))) {
            return CLICK_IN_LOW_AREA;
        } else if (e.getY() >= top
                && e.getY() <= bottom
                && (((e.getX() > ((double) mOffsetHigh + mOffsetLow) / 2) && e.getX() < (mOffsetHigh - mThumbWidth / 2)) || (e
                .getX() > (mOffsetHigh + mThumbWidth/2) && e.getX() <= mScollBarWidth))) {
            return CLICK_IN_HIGH_AREA;
        } else if (!(e.getX() >= 0 && e.getX() <= mScollBarWidth && e.getY() >= top && e.getY() <= bottom)) {
            return CLICK_OUT_AREA;
        } else {
            return CLICK_INVAILD;
        }
    }

    //更新滑块
    private void refresh() {
        invalidate();
    }

    //设置前滑块的值
    public void setProgressLow(double  progressLow) {
        this.defaultScreenLow = progressLow;
        mOffsetLow = formatDouble(progressLow / 100 * (mDistance ))+ mThumbWidth / 2;
        isEdit = true;
        refresh();
    }

    //获取当前low的进度
    public void getProgressLow(double  progressLow) {
        this.defaultScreenLow = progressLow;
        mOffsetLow = formatDouble(progressLow / 100 * (mDistance ))+ mThumbWidth / 2;
        isEdit = true;
        refresh();
    }

    //设置后滑块的值
    public void setProgressHigh(double  progressHigh) {
        this.defaultScreenHigh = progressHigh;
        mOffsetHigh = formatDouble(progressHigh / 100 * (mDistance)) + mThumbWidth / 2;
        isEdit = true;
        refresh();
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
        this.mBarChangeListener = mListener;
    }

    //回调函数，在滑动时实时调用，改变输入框的值
    public interface OnSeekBarChangeListener {
        //滑动前
        public void onProgressBefore();

        //滑动时
        public void onProgressChanged(SeekBarPricePressure seekBar, double progressLow,
                                      double progressHigh);

        //滑动后
        public void onProgressAfter();
    }

/*    private int formatInt(double value) {
        BigDecimal bd = new BigDecimal(value);
        BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bd1.intValue();
    }*/

    public static double formatDouble(double pDouble) {
        BigDecimal bd = new BigDecimal(pDouble);
        BigDecimal bd1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        pDouble = bd1.doubleValue();
        return pDouble;
    }
}
