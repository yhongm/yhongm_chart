/**
 *                   Copyright [yhongm]
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at

 *         http://www.apache.org/licenses/LICENSE-2.0

 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */

package com.yhongm.chart_core;

import android.content.Context;
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
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * @author yhongm
 */
public class PolyLineView extends View {
    private int mWidth;//view的宽度
    private int mHeight;//view的高度
    private int interval = 100;//单位间隔
    private ArrayList<Integer> mDatas = new ArrayList<>();//数据源点
    private ArrayList<String> mUnits = new ArrayList<>();//数据单位点
    private int maxNum = 1000;
    private Paint mLinePaint;//画刻度线的画笔
    private Paint mSmartCirclePaint;//画小圆点
    private Paint mPolyLinePaint;//画折线图的画笔
    private Paint mScalePaint;//画刻度的画笔
    private Paint mShadowPaint;//阴影画笔
    private Paint mSmartFillPoint;//小圆点填充画笔
    int mLineY;
    private int mLineWidth;
    private int mHorizontalPadding = 14;//两侧的边距
    private int mVerticalPadding = 30;//垂直的间距
    private Context mContext;
    private int smartCircleColor = Color.parseColor("#006F6A");
    private int polyLineColor = Color.parseColor("#006F6A");
    private int shadowColor = Color.parseColor("#36006F6A");
    private int smartFillColor = Color.parseColor("#006F6A");
    private int scaleColor = Color.parseColor("#e2e2e2");
    private int max = 1000;
    private float verticalNum = 5.0f;

    public PolyLineView(Context context) {
        this(context, null);
    }

    public PolyLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaint();
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
        mSmartCirclePaint.setStrokeWidth(2);
        mSmartFillPoint = new Paint();
        mSmartFillPoint.setStyle(Paint.Style.FILL);
        mSmartFillPoint.setAntiAlias(true);
        mSmartFillPoint.setColor(smartFillColor);
        mSmartFillPoint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(shadowColor);
        mShadowPaint.setDither(true);
        mPolyLinePaint = new Paint();
        mPolyLinePaint.setStyle(Paint.Style.STROKE);
        mPolyLinePaint.setAntiAlias(true);
        mPolyLinePaint.setColor(polyLineColor);
        mPolyLinePaint.setStrokeWidth(2);
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

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDatas.size() > 0) {
            ArrayList<Point> dataPoints = calePoint();
            /**
             * 画刻度线
             */
            drawLine(canvas, dataPoints);
            drawBrokenLine(canvas, dataPoints);
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
            float curveHeight = mHeight - (mDatas.get(i) / (maxNum + 0f)) * (mHeight - mVerticalPadding);
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
//
        canvas.drawPath(shadowPath, mShadowPaint);
        canvas.save();
    }

    /**
     * 画折线图
     *
     * @param canvas
     * @param dataPoints
     */
    private void drawBrokenLine(Canvas canvas, ArrayList<Point> dataPoints) {
        Path brokenLine = new Path();
        brokenLine.moveTo(dataPoints.get(0).x, mLineY);
        for (int i = 0; i < dataPoints.size() - 1; i++) {

            brokenLine.lineTo(dataPoints.get(i).x, dataPoints.get(i).y);
            canvas.drawLine(dataPoints.get(i).x, dataPoints.get(i).y, dataPoints.get(i + 1).x, dataPoints.get(i + 1).y, mPolyLinePaint);
            drawCircle(canvas, dataPoints.get(i).x, dataPoints.get(i).y);
        }
        brokenLine.lineTo(dataPoints.get(dataPoints.size() - 1).x, dataPoints.get(dataPoints.size() - 1).y);
        brokenLine.moveTo(mWidth, mLineY);

        drawShadow(canvas, brokenLine);

        drawCircle(canvas, dataPoints.get(dataPoints.size() - 1).x, dataPoints.get(dataPoints.size() - 1).y);
    }

    private void drawCircle(Canvas canvas, int x, int y) {

        canvas.drawCircle(x, y, 3, mSmartFillPoint);
        canvas.drawCircle(x, y, 5, mSmartCirclePaint);

    }

    /**
     * 画刻度线
     *
     * @param canvas
     * @param dataPoints
     */
    private void drawLine(Canvas canvas, ArrayList<Point> dataPoints) {
        canvas.drawLine(0, 1, mWidth, 1, mLinePaint);
        canvas.drawLine(0, mLineY + 1, mWidth, mLineY + 1, mLinePaint);
        for (int i = 0; i < dataPoints.size(); i++) {
            canvas.drawLine(dataPoints.get(i).x, 0, dataPoints.get(i).x, mLineY, mLinePaint);
        }
        float cv = mLineY / verticalNum;
        Log.i("PolyLineView", "15:06,drawLine: cv:" + cv);// 2018/7/17,yhongm
        for (int j = 0; j < verticalNum; j++) {
            canvas.drawLine(0, cv * j, mWidth, cv * j, mLinePaint);
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