package barchart.yhongm.com.barchart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yhongm.chart_core.CurveChartView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener(v -> startActivity(new Intent(MainActivity.this,BarChartActivity.class)));
        findViewById(R.id.btn2).setOnClickListener(v -> startActivity(new Intent(MainActivity.this,CurveChartActivity.class)));
    }
}
