package com.qican.ifarm.ui_v2.farm;

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
import com.qican.ifarm.bean.DeviceType;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.data.DeviceDataRequest;
import com.qican.ifarm.listener.InfoRequestListener;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class HistoryDataFragment extends Fragment implements OnChartValueSelectedListener {

    private View view;
    private CommonTools myTool;
    NiceSpinner spinner;
    List<String> spMenus;
    List<String> codes;
    MonitorNode mNode;

    LineChart mChart;
    YAxis leftAxis;
    XAxis xAxis;

    ArrayList<Entry> yVals1;
    ArrayList<String> xTimes;

    LineDataSet set;
    Legend l;
    int durationMillis = 1000;
    float axisMargin = 120f;

    int curPos = 0;
    String unit = "%";
    TextView tvUnit, tvValue, tvTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_data, container, false);
        initView();
        initDatas();
        initEvent();
        return view;
    }

    private void initEvent() {
        spinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                curPos = position;
                getDataByIndex(position);
            }
        });
        mChart.setOnChartValueSelectedListener(this);
    }


    private void initDatas() {
        spMenus = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle == null) return;
        mNode = (MonitorNode) bundle.getSerializable(DeviceDataActivity.KEY_NODE_INFO);

        DeviceDataRequest.getMenuByType(mNode, new InfoRequestListener<DeviceType>() {
            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void onSuccess(DeviceType obj) {
                if (obj.getNames() == null)
                    return;
                spMenus = obj.getNames();
                codes = obj.getCodes();

                spinner.attachDataSource(spMenus);

                if (codes.isEmpty()) return;
                getDataByIndex(0);
            }
        });

        initLineStyle();
    }

    private void initLineStyle() {

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

        // add data
        setData(10, 150);
        mChart.animateY(durationMillis);

        // get the legend (only possible after setting data)
        l = mChart.getLegend();

        // modify the legend ...
        l.setEnabled(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextSize(18f);
        l.setTextColor(getResources().getColor(R.color.main_color));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setYOffset(11f);

        xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setYOffset(10f);
        xAxis.setTextColor(getResources().getColor(R.color.text_color));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(250f);
        leftAxis.setAxisMinimum(30f);
        leftAxis.setTextSize(20f);
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

    private void getDataByIndex(final int index) {
        if (index >= codes.size() || mNode.getDeviceId() == null) return;
        OkHttpUtils.get().url("http://" + ConstantValue.IP + ":8080/IFarm/collectorDeviceValue/collectorDeviceCacheVaules")
                .addParams("deviceId", mNode.getDeviceId())
                .addParams("paramType", codes.get(index))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        if (response == null) return;
                        if ("[]".equals(response)) return;

                        myTool.log(response);

                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.has("value") || !obj.has("time")) return;

                            JSONArray values = obj.getJSONArray("value");
                            JSONArray times = obj.getJSONArray("time");

                            if (obj.has("unit"))
                                unit = obj.getString("unit");

                            yVals1.clear();
                            xTimes.clear();

                            for (int i = 0; i < values.length(); i++)
                                yVals1.add(new Entry(i, (float) values.getDouble(i)));

                            for (int i = 0; i < times.length(); i++)
                                xTimes.add(times.getString(i));

                            invalidateLine();

                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }

    private void invalidateLine() {
        set.setLabel(spMenus.get(curPos));
        set.setValues(yVals1);

        leftAxis.setAxisMaximum(set.getYMax() + axisMargin);
        leftAxis.setAxisMinimum(set.getYMin() - axisMargin);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value >= 0 && value < xTimes.size())
                    return TimeUtils.getTime(xTimes.get((int) value));
                else
                    return "";
            }
        };

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        mChart.getData().notifyDataChanged();
        mChart.notifyDataSetChanged();
        mChart.animateY(durationMillis);
    }


    private void initView() {
        myTool = new CommonTools(getActivity());
        spinner = (NiceSpinner) view.findViewById(R.id.spinner);
        mChart = (LineChart) view.findViewById(R.id.chart);
        tvUnit = (TextView) view.findViewById(R.id.tv_unit);
        tvValue = (TextView) view.findViewById(R.id.tv_value);
        tvTime = (TextView) view.findViewById(R.id.tv_time);

        myTool.setHeightByWindow(mChart, 1.5 / 3f);
    }

    private void setData(int count, float range) {

        yVals1 = new ArrayList<Entry>();
        xTimes = new ArrayList<>();
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
            set = new LineDataSet(yVals1, "模拟数据");

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
            data.setValueTextSize(16f);

            // set data
            mChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        //动画滚动到当前位置
        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);

        //设置预览区显示
        tvValue.setText(e.getY() + "");
        tvTime.setText(e.getX() + "");

        if (e.getX() >= 0 && e.getX() < xTimes.size())
            tvTime.setText(TimeUtils.formatTime(xTimes.get((int) e.getX())));

        tvUnit.setText(unit);
    }

    @Override
    public void onNothingSelected() {

    }
}
