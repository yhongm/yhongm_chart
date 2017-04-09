package com.yhongm.chart_core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BarChartView extends View {
    private Context mContext;
    private Paint mLinePaint;//画线的画笔
    private Paint mTextPaint;//文字的画笔
    private Paint mChartPaint;//柱形图的画笔

    private int mStartX;//x坐标开始值
    private int mStartY;//Y坐标结束的值
    private int mStopX;//x坐标结束的值
    private int mStopY;//y坐标结束的值
    private int barWidth;//每条柱形图的柱宽度
    private int totalBarNum = 6;//柱形图中的柱数量
    private float max = -1;//最大值，用于计算比例
    private int deltaY;//刻度值间距
    private int deltaX;//柱形图之间的间距
    private int currentVerticalLineProgress;//每条柱图的当前比例
    float numPerUnit;//刻度值
    int measuredWidth;//测量后屏幕的宽度
    int measuredHeight;//测量后屏幕的高度
    private int horizentalLineNum = 2;//刻度线的数量值
    private int totalBarWidth;//柱形图总宽度
    private List<VvalueAndHunit> datas;
    private String unit = "单位";//单位
    private int lineColor = Color.parseColor("#333333");//线条颜色
    private int chartColor = Color.parseColor("#666666");//图表颜色
    private int textColor = Color.parseColor("#666666");
    private int gradientStartColor = Color.parseColor("#ffa666");
    private int gradientEndColor = Color.parseColor("#e57a2e");

    public BarChartView(Context context) {
        this(context, null);
    }


    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
        initDefaultData();
    }

    /**
     * 初始化默认数据
     */
    private void initDefaultData() {
        ArrayList<VvalueAndHunit> mDatas = new ArrayList<>();
        Calendar mCalendar = Calendar.getInstance();
        int today = mCalendar.get(Calendar.DAY_OF_MONTH);
        ArrayList<Integer> defaultList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            defaultList.add(0);
        }
        for (int j = 0; j < 7; j++) {
            BarChartView.VvalueAndHunit vah = new VvalueAndHunit();
            vah.setValue(defaultList.get(j));
            vah.setUnit(today - (6 - j) + "");
            mDatas.add(vah);
            this.datas = mDatas;
        }

    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.BarChartView);

        barWidth = typedArray.getInteger(R.styleable.BarChartView_barWidth, 10);

        max = typedArray.getInteger(R.styleable.BarChartView_maxValue, 1000);

        horizentalLineNum = typedArray.getInteger(R.styleable.BarChartView_horizentalLineNum, 2);

        unit = typedArray.getString(R.styleable.BarChartView_unit);

        lineColor = typedArray.getColor(R.styleable.BarChartView_lineColor, Color.parseColor("#333333"));


        chartColor = typedArray.getColor(R.styleable.BarChartView_chartColor, Color.parseColor("#666666"));


        textColor = typedArray.getColor(R.styleable.BarChartView_textColor, Color.parseColor("#666666"));
        gradientStartColor=typedArray.getColor(R.styleable.BarChartView_gradientStartColor,Color.parseColor("#ffa666"));
        gradientEndColor=typedArray.getColor(R.styleable.BarChartView_gradientEndColor,Color.parseColor("#e57a2e"));

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(lineColor);
        mLinePaint.setStyle(Paint.Style.FILL);

        mChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChartPaint.setColor(chartColor);
        mChartPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            //如果高度和宽度都是warp_content,宽度就是输入的最大值，max值，也就是柱形图的最大值，
            // 高度为每条柱形图的宽度加上间距再乘以柱形图条数再加上开始Y值后得到的值
            setMeasuredDimension(mStartX + 10 + totalBarNum * (barWidth + 2 * 10), (int) max);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            //如果宽度为wrap_content，高度为match_parent或者精确值的时候
            setMeasuredDimension((int) max, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            //如果高度为wrap_content,宽度为match_parent或者精确值的时候
            setMeasuredDimension(widthSpecSize, mStartY + 10 + totalBarNum * (barWidth + 2 * 10));
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //测量后的宽度
        measuredWidth = getMeasuredWidth();
        totalBarWidth = measuredWidth * 4 / 5;
        //测量后的高度
        measuredHeight = getMeasuredHeight();
        //计算结束Y的值
        //x坐标开始值为0
        mStartX = 0 * barWidth;
        // y坐标开始值为50
        mStartY = 0 * barWidth;
        mStopX = measuredWidth - 2 * barWidth;
        mStopY = measuredHeight - 2 * barWidth;
        deltaY = (mStopY - (mStartY + 7 * barWidth / 5)) / horizentalLineNum;
        deltaX = (totalBarWidth - mStartX - barWidth * totalBarNum) / totalBarNum;
        numPerUnit = (int) max / horizentalLineNum;
        currentVerticalLineProgress = mStopY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScaleLine(canvas);
        drawBar(canvas);
    }

    /**
     * 画刻度线刻度值
     *
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.drawLine(mStartX - barWidth, mStartY + 1, measuredWidth, mStartY + 1, mLinePaint);
        canvas.drawLine(mStartX - barWidth, mStopY - (mStopY - mStartY) * 1 / 3, measuredWidth, mStopY - (mStopY - mStartY) * 1 / 3, mLinePaint);
        canvas.drawLine(mStartX - barWidth, mStopY - (mStopY - mStartY) * 2 / 3, measuredWidth, mStopY - (mStopY - mStartY) * 2 / 3, mLinePaint);
        canvas.drawLine(mStartX - barWidth, mStopY, measuredWidth, mStopY, mLinePaint);
        canvas.drawText("0", measuredWidth * 19 / 20, mStopY - 5, mTextPaint);
        if (max < 10) {
            canvas.drawText(Math.round(max) + "", measuredWidth * 19 / 20, mStartY + 20, mTextPaint);
        } else {
            canvas.drawText(Math.round(max) + "", (measuredWidth * 19 / 20) - 8, mStartY + 20, mTextPaint);
        }
    }

    /**
     * 画柱形图
     *
     * @param canvas
     */
    private void drawBar(Canvas canvas) {
        for (int i = 0; i < totalBarNum; i++) {
            float top = mStopY - datas.get(i).getValue() / max * mStopY;
            int left = mStartX + deltaX + i * (barWidth + deltaX);
            int right = mStartX + deltaX + i * (barWidth + deltaX) + barWidth;
            int bottom = mStopY;

            LinearGradient lg = new LinearGradient(0, top, 0, bottom, gradientStartColor, gradientEndColor, Shader.TileMode.CLAMP);
            mChartPaint.setShader(lg);
            RectF roundRectF = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(roundRectF, 8, 8, mChartPaint);
            int textX = mStartX + deltaX + i * (barWidth + deltaX);
            int textY = this.mStopY + (barWidth * 3 / 2);
            String name = datas.get(i).getUnit();
            if (i == totalBarNum - 1) {
                canvas.drawText("今天", textX, textY, mTextPaint);
            } else {
                canvas.drawText(name, textX, textY, mTextPaint);
            }
        }
    }

    public void setBarWidth(int width) {
        this.barWidth = width;

        mTextPaint.setTextSize((float) (barWidth * 1.2));

    }

    /**
     * 设置最大值
     *
     * @param max
     */
    public void setMax(float max) {
        this.max = max;
    }

    /**
     * 设置单位
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setHorizentalLineNum(int num) {
        this.horizentalLineNum = num;
    }

    public void setDatas(List<VvalueAndHunit> datas) {
        this.datas = datas;
        this.totalBarNum = datas.size();
        invalidate();
    }

    public class VvalueAndHunit {
        private float xValue; //柱形图柱值
        private String yUnit;//柱形图横坐标名

        public VvalueAndHunit(float xValue, String yUnit) {
            this.xValue = xValue;
            this.yUnit = yUnit;
        }

        public VvalueAndHunit() {

        }

        public void setValue(float xValue) {
            this.xValue = xValue;
        }

        public float getValue() {
            return xValue;
        }

        public void setUnit(String yUnit) {
            this.yUnit = yUnit;
        }

        public String getUnit() {
            return yUnit;
        }

    }
}
