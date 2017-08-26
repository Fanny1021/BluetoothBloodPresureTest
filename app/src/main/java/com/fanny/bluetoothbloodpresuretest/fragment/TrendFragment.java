package com.fanny.bluetoothbloodpresuretest.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fanny.bluetoothbloodpresuretest.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Fanny on 17/6/13.
 */

public class TrendFragment extends Fragment {

    private View view;
    private LineChart mychart;
    //自定义一个显示字体
    Typeface mTf;
    private List<Integer> lists;
    private LineData lineData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = LinearLayout.inflate(getContext(), R.layout.trendlayout,null);

        /**
         * 表格
         */
        mychart = (LineChart) view.findViewById(R.id.linechat);
        /**
         * 设置字体
         */
//        mTf=Typeface.createFromAsset(getAssets(),"");
        /**
         * 填充数据
         */
//        lists = new ArrayList<>();
//        initListData();
        /**
         * 绘图
         */
        drawTheChat();

        return view;
    }



    private void initListData() {
        lists.clear();
        for(int i=1;i<40;i++){
            int value= (int) (Math.random()*100);
            lists.add(value);
        }
    }
    private void drawTheChat() {
        lineData = getLineData(36,1000);
        showTheChat(mychart,lineData,Color.rgb(137,230,81));

    }
    private void showTheChat(LineChart lineChart,LineData lineData,int color) {
        lineChart.setDrawBorders(false);

        lineChart.setDrawGridBackground(false);
        lineChart.setGridBackgroundColor(Color.WHITE & 0x70ffffff);

        lineChart.setTouchEnabled(true);

        lineChart.setDragEnabled(true);

        lineChart.setScaleEnabled(true);

        lineChart.setPinchZoom(false);

        lineChart.setBackgroundColor(Color.BLUE);

        lineChart.setData(lineData);

        Legend mLegend=lineChart.getLegend();
        mLegend.setForm(Legend.LegendForm.CIRCLE); //样式
        mLegend.setFormSize(6f); //字体
        mLegend.setTextColor(Color.WHITE); //颜色

        lineChart.setVisibleXRange(1, 7);   //x轴可显示的坐标范围
        XAxis xAxis = lineChart.getXAxis();  //x轴的标示
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x轴位置
        xAxis.setTextColor(Color.WHITE);    //字体的颜色
        xAxis.setTextSize(10f); //字体大小
        xAxis.setGridColor(Color.WHITE);//网格线颜色
        xAxis.setDrawGridLines(false); //不显示网格线
        xAxis.setTypeface(mTf);

        YAxis axisLeft = lineChart.getAxisLeft(); //y轴左边标示
        YAxis axisRight = lineChart.getAxisRight(); //y轴右边标示
        axisLeft.setTextColor(Color.WHITE); //字体颜色
        axisLeft.setTextSize(10f); //字体大小
        axisLeft.setAxisMaxValue(1000f); //最大值
        axisLeft.setLabelCount(6, true); //显示格数
        axisLeft.setGridColor(Color.WHITE); //网格线颜色
        axisLeft.setTypeface(mTf);

        axisRight.setDrawAxisLine(false);
        axisRight.setDrawGridLines(false);
        axisRight.setDrawLabels(false);

        lineChart.animateX(2500);  //立即执行动画


    }

    private LineData getLineData(int count,float range) {
        /**
         * x轴数据
         */
        ArrayList<String> xValues= new ArrayList<>();
        for(int i=0;i<count;i++){
            xValues.add(""+(i+1));
        }


        /**
         * y轴数据
         */
        ArrayList<Entry> yValues=new ArrayList<>();
        for(int i=0;i<count;i++){
            float value= (int) (Math.random()*range);
            yValues.add(new Entry(value,i));
        }

        /**
         * 设置折线的样式
         */
        LineDataSet lineDataSet =new LineDataSet(yValues,"数据统计");


        /**
         * 使用y轴的集合来设置参数
         */
        lineDataSet.setLineWidth(1.75f);
        lineDataSet.setCircleSize(3f);
        lineDataSet.setCircleColor(Color.WHITE);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setHighLightColor(Color.WHITE);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setValueTextSize(8f);

        ArrayList<ILineDataSet> lineDataSets= new ArrayList<>();
        lineDataSets.add(lineDataSet);

//        LineData lineData=new LineData(xValues,lineDataSets);
        LineData lineData=new LineData(lineDataSets);
        return lineData;

    }


}
