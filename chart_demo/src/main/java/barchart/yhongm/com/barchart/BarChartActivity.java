package barchart.yhongm.com.barchart;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yhongm.chart_core.BarChartView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yuhongmiao on 2017/4/9.
 */

public class BarChartActivity extends Activity {
    BarChartView barChartView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barchart);
        barChartView= (BarChartView) findViewById(R.id.bar_chart);
        barChartView.setBarWidth(16);
        barChartView.setMax(2500);
        barChartView.setUnit("单位");
        ArrayList<BarChartView.VvalueAndHunit> list=new ArrayList<>();
        Random r=new Random();
        for (int i = 0; i <7 ; i++) {
            BarChartView.VvalueAndHunit vValueAndHunit=barChartView.new VvalueAndHunit();
            float v = r.nextInt(1000) + 1000;
            Log.i("BarChartActivity", "onCreate,v:"+v);// yuhongmiao 2017/4/9 下午8:16

            vValueAndHunit.setValue(v);
            vValueAndHunit.setUnit(i+"");
            list.add(vValueAndHunit);
        }
        barChartView.setDatas(list);
    }
}
