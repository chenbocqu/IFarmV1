package com.qican.ifarm.ui.subarea;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.DataAdapter;
import com.qican.ifarm.bean.ParaCacheValues;
import com.qican.ifarm.bean.Sensor;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.TimeUtils;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HistoryDataLineFragment extends Fragment implements OnChartValueSelectedListener {
    private View view;
    private String TAG = "HistoryDataLineFragment";

    private CommonTools myTool;
    private LineChart mChart;
    private Subarea mArea;
    private String[] mMonths = new String[]{"一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月"};
    List<String> dataset = new LinkedList<>();
    private NiceSpinner spinner;
    private ArrayList<Entry> yVals1;
    private LineDataSet set;
    private Legend l;
    private YAxis leftAxis;
    private int durationMillis = 1000;
    private float axisMargin = 35f;
    private NetRequest netRequest;
    private List<Sensor> mSensorList;
    private List<ParaCacheValues> mDatas;
    private TextView tvValue, tvTime, tvUnit;
    private int curPos = 0;//当前显示的是第几条曲线

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_line, container, false);
        initView(view);
        initDatas();
        initEvents();
        return view;
    }

    private void initEvents() {
        spinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                curPos = position;
                set.setLabel(mDatas.get(position).getName());
                set.setValues(mDatas.get(position).getPoints());

                leftAxis.setAxisMaximum(set.getYMax() + axisMargin);
                leftAxis.setAxisMinimum(set.getYMin() - axisMargin);

                IAxisValueFormatter formatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if (mDatas.get(position).getPoints().size() > value)
                            return TimeUtils.getTime(mDatas.get(position).getPoints().get((int) value).getData().toString());
                        else
                            return "Q";
                    }
                };

                XAxis xAxis = mChart.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);

                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.animateY(durationMillis);
            }
        });
    }

    private void initDatas() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        mArea = (Subarea) bundle.getSerializable(SubareaActivity.KEY_AREA_INFO);
        mDatas = new ArrayList<>();
//        spinner.attachDataSource(dataset);
        getSensorList();

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
        tvValue = (TextView) v.findViewById(R.id.tv_value);
        tvTime = (TextView) v.findViewById(R.id.tv_time);
        tvUnit = (TextView) v.findViewById(R.id.tv_unit);
        netRequest = new NetRequest(getActivity());
    }


    private void setData(int count, float range) {

        yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 150;
            yVals1.add(new Entry(i, val));
        }

        if (mChart.getData() != null) {
            set = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set = new LineDataSet(yVals1, "湿 度");

            set.setAxisDependency(YAxis.AxisDependency.LEFT);//依靠左边的Y轴
            set.setColor(ColorTemplate.getHoloBlue());
            set.setCircleColor(getResources().getColor(R.color.main_color));
            set.setLineWidth(1f);
            set.setCircleRadius(5f);
            set.setFillAlpha(55);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.enableDashedLine(10f, 10f, 0f);//将折线设置为曲线

            set.setDrawFilled(true);
            set.setFillFormatter(new DefaultFillFormatter());
            //set.setVisible(false);
            set.setDrawCircleHole(true);
            set.setCircleHoleRadius(4f);
            set.setCircleColorHole(getResources().getColor(R.color.main_color));
            set.setDrawHorizontalHighlightIndicator(true);
            set.setCubicIntensity(23f);

            // create a data object with the datasets
            LineData data = new LineData(set);
            data.setValueTextColor(Color.GRAY);
            data.setValueTextSize(12f);

            // set data
            mChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        //动画滚动到当前位置
        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);

        if (e.getData() == null) {
            myTool.showInfo("当前为模拟数据！");
            return;
        }

        //设置预览区显示
        tvValue.setText(e.getY() + "");
        tvTime.setText(e.getData().toString());
        setUnit();//设置预览区数值单位
    }

    private void setUnit() {
        String name = mDatas.get(curPos).getName();
        if (name.contains("温度")) {
            tvUnit.setText("℃");
        } else if (name.contains("湿度")) {
            tvUnit.setText("%RH");
        } else if (name.contains("光照")) {
            tvUnit.setText("LUX");
        }
    }

    @Override
    public void onNothingSelected() {

    }

    private void getSensorList() {
//        netRequest.getSensorList(mArea.getFarmId(), new DataAdapter() {
//            @Override
//            public void sensors(List<Sensor> sensorList) {
//                super.sensors(sensorList);
//                mSensorList = sensorList;
//                if (!mSensorList.isEmpty()) {
//                    // 再根据传感器列表获取参数
//                    getParaData();
//                }
//            }
//        });
        netRequest.getSensorList(mArea, new DataAdapter() {
            @Override
            public void sensors(List<Sensor> sensorList) {
                super.sensors(sensorList);
                mSensorList = sensorList;
                if (!mSensorList.isEmpty()) {
                    // 再根据传感器列表获取参数
                    getParaData();
                }
            }
        });
    }

    // 获得传感器参数列表
    private void getParaData() {
        for (final Sensor sensor : mSensorList) {
            netRequest.getSensorCacheValues(sensor.getId(), new DataAdapter() {
                @Override
                public void paraCaches(List<ParaCacheValues> cacheValues) {

                    myTool.log("lineChart的mDatas:" + mDatas.toString());
                    mDatas.addAll(cacheValues);
                    //改变数据
                    if (!mDatas.isEmpty()) {
                        // 重置菜单
                        dataset.clear();
                        for (ParaCacheValues para : mDatas) {
                            dataset.add(para.getName());
                        }
                        spinner.attachDataSource(dataset);
                    }
                }
            });
        }
    }

    // the labels that should be drawn on the XAxis
    final String[] quarters = new String[]{"Q1", "Q2", "Q3", "Q4"};
}
