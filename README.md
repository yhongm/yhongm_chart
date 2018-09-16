 # yhongm_chart,简单的图表库，含有折线图,条形图，柱形图
 
 ## 效果
 
<img src="/preview/Screenshot_1491750474.png">
<img src="/preview/Screenshot_1491750643.png">
<img src="/preview/Screenshot_1491750379.png">
<img src="/preview/Screenshot_1537070842.png">

 ## 使用方法
 
 ### 1.barChartView:  
 
```java
    <com.yhongm.chart_core.BarChartView   
    android:layout_centerInParent="true"   
    custom:gradientStartColor="#ffa666"   
    custom:gradientEndColor="#e57a2e"    
    custom:lineColor="#333333"    
    custom:chartColor="#666666"    
    custom:textColor="#666666"    
    custom:maxValue="2500"    
    custom:unit="单位"    
    custom:horizentalLineNum="2"
    custom:barWidth="16"  
     android:id="@+id/bar_chart"    
    android:layout_width="300dp"    
    android:layout_height="200dp">  
    </com.yhongm.chart_core.BarChartView>
```  
	
 ##### gradientStartColor: 为渐变色起始值
 ##### gradientEndColor: 为渐变色结束值
 ##### horizentalLineNum: 刻度线数量
 ##### unit: 柱形图刻度单位名称
 ##### maxValue: 柱形图最大值
 ##### barWidth: 柱形图宽度
 ##### lineColor，chartColor，textColor: 图表颜色值
	
 ### 2.curveChartView:
```java
     <com.yhongm.chart_core.CurveChartView   
     custom:curveOrBroken="false"   
     custom:max="2500"   
     custom:scaleColor="#666666"    
     custom:curveLineColor="#ff5500"    
     custom:smartCircleColor="#ff5500"     
     custom:smartFillColor="#ffffff"   
     android:layout_centerInParent="true"   
     android:id="@+id/curve_chart"      
     android:layout_width="300dp"   
     android:layout_height="200dp">  
      </com.yhongm.chart_core.CurveChartView>
 
```

  ##### curveOrBroken: true为曲线图,false为折线图
  ##### max:为曲线图或折线图最大值
  ##### scaleColor，curveLineColor，smartCircleColor，smartFillColor: 图表颜色值
  ### 3.PolyLineView
  

