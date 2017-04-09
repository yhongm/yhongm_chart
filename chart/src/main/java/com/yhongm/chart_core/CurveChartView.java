package com.yhongm.chart_core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CurveChartView extends View {
    private int mWidth;//view的宽度
    private int mHeight;//view的高度
    private int interval = 100;//单位间隔
    private ArrayList<Integer> mDatas = new ArrayList<>();//数据源点
    private ArrayList<String> mUnits = new ArrayList<>();//数据单位点
    private int maxNum = 1000;
    private Paint mLinePaint;//画刻度线的画笔
    private Paint mCurveLinePaint;//曲线画笔
    private Paint mSmartCirclePaint;//画小圆点
    private Paint mBrokeLinePaint;//画折线图的画笔
    private Paint mScalePaint;//画刻度的画笔
    private Paint mCurveShadowPaint;//阴影画笔
    private Paint mSmartFillPoint;//小圆点填充画笔
    private PathMeasure mPathMeasure;
    int mLineY;
    private int mLineWidth;
    private int mHorizontalPadding = 14;//两侧的边距
    private int mVerticalPadding = 30;//垂直的间距
    private Context mContext;
    private boolean isFirst = false;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("yyMMdd");

    private Calendar mCalendar;
    private boolean CurveOrBroken = true;//折线或者曲线，true为曲线，false为折线
    private int smartCircleColor = Color.parseColor("#ff5500");
    private int curveLineColor = Color.parseColor("#ff5500");
    private int smartFillColor = Color.parseColor("#ffffff");
    private int scaleColor = Color.parseColor("#666666");
    private int max = 1000;

    public CurveChartView(Context context) {
        this(context, null);
    }

    public CurveChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
        initPaint();
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CurveChartView);

        CurveOrBroken = typedArray.getBoolean(R.styleable.CurveChartView_CurveOrBroken, true);

        max = typedArray.getInteger(R.styleable.CurveChartView_max, 100);
        maxNum = max;
        smartCircleColor = typedArray.getColor(R.styleable.CurveChartView_smartCircleColor, Color.parseColor("#ff5500"));

        curveLineColor = typedArray.getColor(R.styleable.CurveChartView_curveLineColor, Color.parseColor("#ff5500"));

        smartFillColor = typedArray.getColor(R.styleable.CurveChartView_smartFillColor, Color.parseColor("#ffffff"));

        scaleColor = typedArray.getColor(R.styleable.CurveChartView_scaleColor, Color.parseColor("#666666"));

        mCalendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            mDatas.add(0);
        }
        int today = mCalendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < 7; i++) {
            mUnits.add(today - (6 - i) + "");
        }
    }

    private void initPaint() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.BLACK);


        mSmartCirclePaint = new Paint();
        mSmartCirclePaint.setAntiAlias(true);
        mSmartCirclePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mSmartCirclePaint.setColor(smartCircleColor);
        mSmartCirclePaint.setStyle(Paint.Style.STROKE);
        mSmartCirclePaint.setStrokeWidth(3);
        mSmartFillPoint = new Paint();
        mSmartFillPoint.setStyle(Paint.Style.FILL);
        mSmartFillPoint.setAntiAlias(true);
        mSmartFillPoint.setColor(smartFillColor);
        mSmartFillPoint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCurveLinePaint = new Paint();
        mCurveLinePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCurveLinePaint.setStyle(Paint.Style.STROKE);
        mCurveLinePaint.setColor(curveLineColor);
        mCurveLinePaint.setStrokeWidth(3);
        mCurveShadowPaint = new Paint();
        mCurveShadowPaint.setAntiAlias(true);
        mCurveShadowPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCurveShadowPaint.setStyle(Paint.Style.FILL);
        mCurveShadowPaint.setDither(true);
        mBrokeLinePaint = new Paint();
        mBrokeLinePaint.setStyle(Paint.Style.STROKE);
        mBrokeLinePaint.setColor(Color.RED);
        mScalePaint = new Paint();
        mScalePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mScalePaint.setAntiAlias(true);
        mScalePaint.setColor(scaleColor);
        mScalePaint.setTextSize(18);
        mScalePaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        interval = (mWidth - 2 * mHorizontalPadding) / 6;
        mLineY = mHeight - mVerticalPadding;
    }

    /**
     * 设置画曲线图的数据源
     *
     * @param mVvalueAndHunits
     */
    public void setDatas(ArrayList<VvalueAndHunit> mVvalueAndHunits) {
        if (this.mDatas.size() > 0) {
            this.mDatas.clear();
        }
        if (mUnits.size() > 0) {
            this.mUnits.clear();
        }
        for (int i = 0; i < mVvalueAndHunits.size(); i++) {
            this.mDatas.add(mVvalueAndHunits.get(i).yValue);
            this.mUnits.add(mVvalueAndHunits.get(i).xUnit);

        }
        interval = (mWidth - 2 * mHorizontalPadding) / 6;
        invalidate();
    }

    /**
     * 设置曲线图数据最大值
     *
     * @param maxNum
     */
    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    protected void onDraw(Canvas canvas) {
        if (mDatas.size() > 0) {
            ArrayList<Point> dataPoints = calePoint();
            /**
             * 画刻度线
             */
            drawLine(canvas);
            if (CurveOrBroken) {
                /**
                 * 画曲线图
                 */
                drawCurveLine(canvas, dataPoints);
            } else {
                /**
                 * 画折线图
                 */
                drawBrokenLine(canvas, dataPoints);
            }


            /**
             * 画曲线图2
             */
//            drawCurveLine2(canvas, dataPoints);


            /**
             * 画刻度
             */
            if (mUnits.size() > 0) {
                drawScale(canvas, dataPoints);
            }


        }


    }

    /**
     * 第二种曲线图画法
     *
     * @param canvas
     * @param dataPoints
     */
    private void drawCurveLine2(Canvas canvas, ArrayList<Point> dataPoints) {
        Path curve2Path = new Path();
        for (int i = 0; i < dataPoints.size(); i = i + 1) {

            if (i == 0) {// 第一条为二阶贝塞尔
                curve2Path.moveTo(dataPoints.get(i).x, dataPoints.get(i).y);// 起点
                curve2Path.quadTo((dataPoints.get(i).x + dataPoints.get(i + 1).x) / 2, dataPoints.get(i + 1).y + (dataPoints.get(i).y - (dataPoints.get(i + 2).y)) / 4,// 控制点
                        dataPoints.get(i + 1).x, dataPoints.get(i + 1).y);
            } else if (i < dataPoints.size() - 2) {// 三阶贝塞尔
                if (dataPoints.get(i).y == (mHeight - mVerticalPadding) && dataPoints.get(i + 1).y == (mHeight - mVerticalPadding)) {
                    curve2Path.lineTo(dataPoints.get(i + 1).x, dataPoints.get(i + 1).y);
                } else {
                    curve2Path.cubicTo((dataPoints.get(i).x + (dataPoints.get(i + 1).x)) / 2, dataPoints.get(i).y + (dataPoints.get(i + 1).y - (dataPoints.get(i - 1).y)) / 4,// 控制点1
                            (dataPoints.get(i).x + (dataPoints.get(i + 1).x)) / 2, dataPoints.get(i + 1).y + (dataPoints.get(i).y - (dataPoints.get(i + 2).y)) / 4,// 控制点2
                            dataPoints.get(i + 1).x, dataPoints.get(i + 1).y);// 终点
                }

            } else if (i == dataPoints.size() - 2) {// 最后一条为二阶贝塞尔
//                curve2Path.moveTo(dataPoints.get(i).x, dataPoints.get(i).y);// 起点
                curve2Path.quadTo((dataPoints.get(i).x + (dataPoints.get(i + 1).x)) / 2, dataPoints.get(i).y + (dataPoints.get(i + 1).y - (dataPoints.get(i + -1).y)) / 4,//控制点
                        dataPoints.get(i + 1).x, dataPoints.get(i + 1).y);// 终点
            }
        }

        drawShadow(canvas, curve2Path);
        canvas.drawPath(curve2Path, mCurveLinePaint);
        /**
         * 画矩形
         */
        drawRect(canvas);
        drawCirclePoint(canvas, dataPoints);
    }

    /**
     * 画填充矩形
     *
     * @param canvas
     */
    private void drawRect(Canvas canvas) {
        Rect rect = new Rect();
        rect.left = 10;
        rect.right = getWidth() - 10;
        rect.top = 162;
        rect.bottom = getHeight() - 0;
        Paint paint = new Paint();
        paint.setColor(Color.rgb(245, 245, 245));
        canvas.drawRect(rect, paint);
    }

    /**
     * 画刻度
     *
     * @param canvas
     * @param dataPoints
     */
    private void drawScale(Canvas canvas, ArrayList<Point> dataPoints) {
        int scaleY = mLineY;
        for (int i = 0; i < mDatas.size(); i++) {
            if (i == mDatas.size() - 1) {
                canvas.drawText("今天", dataPoints.get(i).x, scaleY + 20, mScalePaint);
            } else {
                canvas.drawText(mUnits.get(i), dataPoints.get(i).x, scaleY + 20, mScalePaint);
            }

        }
    }

    /**
     * 处理需要绘制的点
     *
     * @return
     */
    private ArrayList<Point> calePoint() {
        ArrayList<Point> dataPoints = new ArrayList<Point>();
        int intervals = 0;
        for (int i = 0; i < mDatas.size(); i++) {
            Point point = new Point();
            float curveHeight = mHeight - (mDatas.get(i) / (maxNum + 0f)) * mHeight - mVerticalPadding;
            if (i == 0) {
                point.set(mHorizontalPadding, (int) curveHeight);
                intervals = mHorizontalPadding;
            } else {
                intervals = intervals + interval;
                point.set(intervals, (int) curveHeight);
            }

            dataPoints.add(point);
        }
        return dataPoints;
    }

    /**
     * 画曲线图
     *
     * @param canvas
     * @param dataPoints
     */
    private void drawCurveLine(Canvas canvas, ArrayList<Point> dataPoints) {
        Path curvePath = new Path();
        CurvePoint p1 = new CurvePoint();
        CurvePoint p2 = new CurvePoint();
        CurvePoint p3 = new CurvePoint();
        float xp = dataPoints.get(0).x;
        float yp = dataPoints.get(0).y;
        curvePath.moveTo(xp, yp);
        int length = dataPoints.size();
        float mFirstMultiplier = 0.4f;
        float mSecondMultiplier = 1 - mFirstMultiplier;
        for (int b = 0; b < length; b++) {
            int nextIndex = b + 1 < length ? b + 1 : b;
            int nextNextIndex = b + 2 < length ? b + 2 : nextIndex;
            p1 = calcControlPoint(dataPoints, b, nextIndex, mSecondMultiplier);
            p2.setX(dataPoints.get(nextIndex).x);
            p2.setY(dataPoints.get(nextIndex).y);
            p3 = calcControlPoint(dataPoints, nextIndex, nextNextIndex, mFirstMultiplier);
            curvePath.cubicTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
        }
        reSetPointDrawCurve(curvePath, dataPoints);
        drawShadow(canvas, curvePath);
        canvas.drawPath(curvePath, mCurveLinePaint);
        drawCirclePoint(canvas, dataPoints);
    }

    /**
     * 画圆点
     *
     * @param canvas
     * @param dataPoints
     */
    private void drawCirclePoint(Canvas canvas, ArrayList<Point> dataPoints) {
        int lastPointIndex = dataPoints.size() - 1;
        for (int k = 0; k < dataPoints.size(); k++) {
            if (k == lastPointIndex) {
                mSmartCirclePaint.setColor(Color.parseColor("#ffffff"));
                mSmartFillPoint.setColor(Color.parseColor("#ff5500"));
            } else {
                mSmartCirclePaint.setColor(Color.parseColor("#ff5500"));
                mSmartFillPoint.setColor(Color.parseColor("#ffffff"));
            }
            canvas.drawCircle(dataPoints.get(k).x, dataPoints.get(k).y, 6, mSmartCirclePaint);
            canvas.drawCircle(dataPoints.get(k).x, dataPoints.get(k).y, 4, mSmartFillPoint);
        }
    }

    /**
     * 绘制阴影
     *
     * @param canvas
     * @param curvePath
     */
    private void drawShadow(Canvas canvas, Path curvePath) {
        PorterDuffXfermode duff = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        PathMeasure pathMeasure = new PathMeasure(curvePath, false);
        ArrayList<Point> points = new ArrayList<Point>();
        int pathlength = (int) pathMeasure.getLength();
        for (int i = 0; i < pathlength; i++) {
            float[] pois = new float[2];
            pathMeasure.getPosTan(i, pois, null);
            Point point = new Point();
            point.x = (int) pois[0];
            point.y = (int) pois[1];
            points.add(point);
        }
        Path shadowPath = new Path();
        shadowPath.moveTo(points.get(0).x, mLineY);
        for (int i = 0; i < points.size(); i++) {
            shadowPath.lineTo(points.get(i).x, points.get(i).y);
        }
        shadowPath.lineTo(points.get(points.size() - 1).x, mLineY);
        shadowPath.setFillType(Path.FillType.WINDING);
        int startColor = Color.parseColor("#ffa666");
        int endColor = Color.parseColor("#ffe1cc");
        LinearGradient lg = new LinearGradient(0, 0, 0, mLineY, startColor, endColor, Shader.TileMode.CLAMP);
        mCurveShadowPaint.setShader(lg);
        canvas.drawPath(shadowPath, mCurveShadowPaint);
        canvas.save();
    }

    /**
     * 画折线图
     *
     * @param canvas
     * @param dataPoints
     */
    private void drawBrokenLine(Canvas canvas, ArrayList<Point> dataPoints) {
        for (int i = 0; i < dataPoints.size() - 1; i++) {
            canvas.drawLine(dataPoints.get(i).x, dataPoints.get(i).y, dataPoints.get(i + 1).x, dataPoints.get(i + 1).y, mBrokeLinePaint);
            canvas.drawCircle(dataPoints.get(i).x, dataPoints.get(i).y, 3, mBrokeLinePaint);
        }
        canvas.drawCircle(dataPoints.get(dataPoints.size() - 1).x, dataPoints.get(dataPoints.size() - 1).y, 3, mBrokeLinePaint);
    }

    /**
     * 画刻度线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        canvas.drawLine(0, 1, mWidth, 1, mLinePaint);
        canvas.drawLine(0, mLineY + 1, mWidth, mLineY + 1, mLinePaint);
    }

    /**
     * 计算控制点
     *
     * @param points
     * @param firstPoint  第一个点
     * @param secondPoint 第二个点
     * @param multiplier  控制点距离第一个点的百分比
     * @return 返回控制点
     */
    private CurvePoint calcControlPoint(List<Point> points, int firstPoint, int secondPoint, final float multiplier) {
        CurvePoint result = new CurvePoint();

        float p1x = points.get(firstPoint).x;
        float p1y = points.get(firstPoint).y;
        float p2x = points.get(secondPoint).x;
        float p2y = points.get(secondPoint).y;

        float diffX = p2x - p1x;
        float diffY = p2y - p1y;
        result.setX(p1x + (diffX * multiplier));
        result.setY(p1y + (diffY * multiplier));
        return result;
    }

    /**
     * 从新计算点从而将将点画到曲线上
     *
     * @param curvePath  需要计算曲线的点
     * @param pointsList 点的集合
     */
    public void reSetPointDrawCurve(Path curvePath, List<Point> pointsList) {
        mPathMeasure = new PathMeasure(curvePath, false);
        int length = (int) mPathMeasure.getLength();
        int pointsSize = pointsList.size();
        float[] coords = new float[2];
        for (int b = 0; b < length; b++) {
            mPathMeasure.getPosTan(b, coords, null);
            Point point = new Point();
            point.x = (int) coords[0];
            point.y = (int) coords[1];
            double startDiff = Double.MAX_VALUE;//开始比较前两者的差值
            boolean ok = true;
            for (int j = 0; j < pointsSize && ok; j++) {
                //计算曲线和实际点之间的差值
                Point dataPoint = pointsList.get(j);
                float diff = Math.abs(dataPoint.x - coords[0]);
                if (diff < 1) {
                    //如果曲线上点的X轴坐标和数据源坐标点的X轴坐标的差值小于1,则用曲线上的点替换
                    pointsList.set(j, point);
                    startDiff = diff;
                }
                ok = startDiff > diff;
            }
        }
        ArrayList<String> spPoints = new ArrayList<>();
        for (int i = 0; i < pointsSize; i++) {
            spPoints.add(pointsList.get(i).x + "," + pointsList.get(i).y);
        }
    }

    /**
     * 获取当前日期格式yymmdd
     */
    private String getCurrentDate() {
        String currentDate = dataFormat.format(System.currentTimeMillis());
        return currentDate;
    }

    class CurvePoint implements Serializable {
        private float mX;
        private float mY;

        public CurvePoint() {
        }

        public float getX() {
            return mX;
        }

        public float getY() {
            return mY;
        }

        public void setX(float x) {
            mX = x;
        }

        public void setY(float y) {
            mY = y;
        }
    }

    public class VvalueAndHunit {
        private int yValue; //曲线图柱值
        private String xUnit;//曲线坐标名

        public VvalueAndHunit() {

        }

        public VvalueAndHunit(int yValue, String xUnit) {
            this.yValue = yValue;
            this.xUnit = xUnit;
        }

        public void setValue(int xValue) {
            this.yValue = xValue;
        }

        public int getValue() {
            return yValue;
        }

        public void setUnit(String yUnit) {
            this.xUnit = yUnit;
        }

        public String getUnit() {
            return xUnit;
        }

    }


}