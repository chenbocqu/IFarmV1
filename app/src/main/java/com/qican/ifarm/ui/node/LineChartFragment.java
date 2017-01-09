package com.qican.ifarm.ui.node;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qican.ifarm.R;
import com.qican.ifarm.utils.CommonTools;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LineChartFragment extends Fragment implements OnChartValueSelectedListener {
    private View view;
    private String TAG = "LineChartFragment";

    private CommonTools myTool;
    private LineChart mChart;
    private String[] mMonths = new String[]{"一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月"};
    List<String> dataset = new LinkedList<>(Arrays.asList("湿度", "温度1", "温度2", "土壤水"));
    private NiceSpinner spinner;
    private ArrayList<Entry> yVals1, yVals2, yVals3;
    private LineDataSet set1;
    private Legend l;
    private YAxis leftAxis;
    private int durationMillis = 1000;
    private float axisMargin = 35f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_linechart, container, false);
        initView(view);
        initDatas();
        initEvents();
        return view;
    }

    private void initEvents() {
        spinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myTool.showInfo(dataset.get(position));
                switch (position) {
                    case 0:
                        //湿度
                        set1.setLabel("湿度");
                        set1.setValues(yVals1);
                        break;
                    case 1:
                        //温度1
                        set1.setLabel("温度1");
                        set1.setValues(yVals2);
                        break;
                    case 2:
                        //温度2
                        set1.setLabel("温度2");
                        set1.setValues(yVals3);
                        break;
                    case 3:
                        //土壤水
                        set1.setLabel("土壤水");
                        set1.setValues(yVals2);
                        break;
                }

                leftAxis.setAxisMaximum(set1.getYMax() + axisMargin);
                leftAxis.setAxisMinimum(set1.getYMin() - axisMargin);

                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.animateY(durationMillis);
            }
        });
    }

    private void initDatas() {
        spinner.attachDataSource(dataset);
        // 生产数据
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
//        mChart.setBackgroundColor(Color.LTGRAY);//背景颜色

        // add data
        setData(10, 30);
        mChart.animateY(durationMillis);

        // get the legend (only possible after setting data)
        l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextSize(15f);
        l.setTextColor(getResources().getColor(R.color.main_color));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(15f);
        xAxis.setYOffset(10f);
        xAxis.setTextColor(getResources().getColor(R.color.text_color));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(250f);
        leftAxis.setAxisMinimum(30f);
        leftAxis.setTextSize(12f);
        leftAxis.setXOffset(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaximum(180);
        rightAxis.setAxisMinimum(0);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
    }


    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        mChart = (LineChart) v.findViewById(R.id.chart);
        spinner = (NiceSpinner) v.findViewById(R.id.spinner);
    }


    private void setData(int count, float range) {

        yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 150;
            yVals1.add(new Entry(i, val));
        }
        yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count - 1; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 450;
            yVals2.add(new Entry(i, val));
        }

        yVals3 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 500;
            yVals3.add(new Entry(i, val));
        }

        if (mChart.getData() != null) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "湿 度");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);//依靠左边的Y轴
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(getResources().getColor(R.color.main_color));
            set1.setLineWidth(1f);
            set1.setCircleRadius(5f);
            set1.setFillAlpha(55);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.enableDashedLine(10f, 10f, 0f);//将折线设置为曲线

            set1.setDrawFilled(true);
            set1.setFillFormatter(new DefaultFillFormatter());
            //set1.setVisible(false);
            set1.setDrawCircleHole(true);
            set1.setCircleHoleRadius(4f);
            set1.setCircleColorHole(getResources().getColor(R.color.main_color));
            set1.setDrawHorizontalHighlightIndicator(true);
            set1.setCubicIntensity(23f);

            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTextColor(Color.GRAY);
            data.setValueTextSize(12f);

            // set data
            mChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {

    }
}
