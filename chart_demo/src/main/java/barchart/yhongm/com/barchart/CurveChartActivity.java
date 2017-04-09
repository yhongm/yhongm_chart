package barchart.yhongm.com.barchart;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yhongm.chart_core.BarChartView;
import com.yhongm.chart_core.CurveChartView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yuhongmiao on 2017/4/9.
 */

public class CurveChartActivity extends Activity {
    CurveChartView curveChartView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curve_chart);
        curveChartView= (CurveChartView) findViewById(R.id.curve_chart);
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
    }
}
