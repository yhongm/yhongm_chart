package barchart.yhongm.com.barchart;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yhongm.chart_core.BarChartView;
import com.yhongm.chart_core.CurveChartView;
import com.yhongm.chart_core.PolyLineView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yuhongmiao on 2017/4/9.
 */

public class CurveChartActivity extends Activity {
    private CurveChartView curveChartView;
    private PolyLineView polyLineView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curve_chart);
        curveChartView= (CurveChartView) findViewById(R.id.curve_chart);
        polyLineView= (PolyLineView) findViewById(R.id.polyline_view);
        ArrayList<CurveChartView.VvalueAndHunit> list=new ArrayList<>();
        Random r=new Random();
        for (int i = 0; i <7 ; i++) {
            CurveChartView.VvalueAndHunit vValueAndHunit=curveChartView.new VvalueAndHunit();
            int v = r.nextInt(1000) + 1000;
            Log.i("BarChartActivity", "onCreate,v:"+v);// yuhongmiao 2017/4/9 下午8:16

            vValueAndHunit.setValue(v);
            vValueAndHunit.setUnit(i+"");
            list.add(vValueAndHunit);
        }
        curveChartView.setMaxNum(2500);
        curveChartView.setDatas(list);
        ArrayList<PolyLineView.VvalueAndHunit> testList =new  ArrayList<PolyLineView.VvalueAndHunit>();

        for (int j=0;j<7;j++) {
            PolyLineView.VvalueAndHunit vValueAndHunit = polyLineView.new VvalueAndHunit();
            int v = r.nextInt(100) + 100;
            vValueAndHunit.setValue(v);
            testList.add(vValueAndHunit);
        }
        polyLineView.setMaxNum(200);
        polyLineView.setDatas(testList);
    }
}
