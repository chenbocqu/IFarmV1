package com.qican.ifarm.ui.subarea;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.RealtimeData;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.ui.farm.DownloadExcelActivity_;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bingoogolapple.progressbar.BGAProgressBar;
import okhttp3.Call;

public class RealtimeDataFragment extends Fragment implements View.OnClickListener {
    private View view;
    private String TAG = "NodeDataFragment";
    private Subarea mArea;

    private List<RealtimeData> mDatas;
    private RealtimeAdapter mAdapter;
    private Bitmap temperature, humidity, illuminate;
    private LinearLayout llRefresh, llExcel;
    private RelativeLayout rlNoData;
    private NetRequest netRequest;
    private BGAProgressBar mPbDownloadExcel;
    private PullToRefreshLayout pullToRefreshLayout;
    private PullListView mListView;


    private CommonTools myTool;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subarea_data, container, false);
        initView(view);
        initData();
        initEvents();
        return view;
    }

    private void initEvents() {
        llRefresh.setOnClickListener(this);
        llExcel.setOnClickListener(this);
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout l) {
                l.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        l.refreshFinish(true);
                    }
                }, 500);
            }

            @Override
            public void onLoadMore(final PullToRefreshLayout l) {
                l.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        l.loadMoreFinish(true);
                    }
                }, 500);
            }
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        mArea = (Subarea) bundle.getSerializable(SubareaActivity.KEY_AREA_INFO);
        initRes();//初始化图像资源
        showNoData();

        mDatas = new ArrayList<>();

        parseSubareaData();
        mAdapter = new RealtimeAdapter(getActivity(), mDatas, R.layout.item_realtime_data);
        mListView.setAdapter(mAdapter);
        notifyData();
    }

    private void parseSubareaData() {
        Map<String, List<String>> map = mArea.getDataMap();
        Set<String> set = map.keySet();

        for (String key : set) {
            RealtimeData data = new RealtimeData();
            data.setName(key);
            ArrayList<String> values = (ArrayList<String>) map.get(key);
            double sum = 0.0;
            if (values != null && values.size() != 0) {
                for (String v : values) {
                    sum = sum + Double.valueOf(v);
                }
                data.setAverageValue(String.valueOf(sum / values.size()));
                data.setValues(values);
            }
            mDatas.add(data);
        }
    }

    private void initRes() {
        temperature = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.temperature);
        humidity = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.humidity);
        illuminate = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.illuminate);
    }


    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        llRefresh = (LinearLayout) v.findViewById(R.id.ll_refresh);
        llExcel = (LinearLayout) v.findViewById(R.id.ll_excel);
        rlNoData = (RelativeLayout) v.findViewById(R.id.rl_nodata);
        mPbDownloadExcel = (BGAProgressBar) v.findViewById(R.id.progressBar);
        pullToRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.pullToRefreshLayout);
        mListView = (PullListView) v.findViewById(R.id.pullListView);

        netRequest = new NetRequest(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_refresh:
                refreshData();
                break;
            case R.id.ll_excel:
                myTool.startActivity(
                        new Farm.Builder()
                                .setId(mArea.getFarmId())
                                .build(), DownloadExcelActivity_.class);
//                netRequest.downLoadExcel(mFarm, mPbDownloadExcel);//下载历史数据
                break;
        }
    }

    private void refreshData() {

        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "sensor/getSensorVaules")
                .addParams("farmId", mArea.getFarmId())
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("分区列表e：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        if (response == null || "[]".equals(response)) {
                            myTool.showInfo("该农场暂无分区，请先添加！");
                            return;
                        }

                        myTool.log("分区列表list：" + response);
                        JSONArray arr = JSONArray.fromObject(response);
                        JSONObject jsonObject = arr.getJSONObject(0);
                        Iterator<String> iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            Subarea area = new Subarea();
                            String key = iterator.next();
                            myTool.log("\n当前分区为:" + key);
                            //不是当前分区就直接继续
                            if (!key.equals(mArea.getName())) {
                                continue;
                            }

                            Map<String, List<String>> map = new HashMap<String, List<String>>();

                            JSONArray array = jsonObject.getJSONArray(key);

                            for (int i = 0; i < array.size(); i++) {
                                // 取参数
                                JSONObject object = array.getJSONObject(i);
                                JSONArray paraName = object.getJSONArray("sensorParam");
                                JSONArray paraData = object.getJSONArray("data");
                                for (int j = 0; j < paraName.size() && j < paraData.size(); j++) {
                                    String paraKey = paraName.getString(j);
                                    String paraValue = paraData.getString(j);

                                    if (!map.containsKey(paraKey)) {
                                        //如果当前没有出现该Key值则新建一个空间用于存放
                                        List<String> temp = new ArrayList<String>();
                                        temp.clear();
                                        temp.add(paraValue);
                                        map.put(paraKey, temp);
                                    } else {
                                        ArrayList<String> dataList = (ArrayList<String>) map.get(paraKey);
                                        dataList.add(paraValue);
                                        map.put(paraKey, dataList);
                                    }

                                    myTool.log("\nmap为:" + map.toString());
                                }
                            }
                            area.setName(key);
                            area.setFarmId(mArea.getFarmId());
                            area.setDataMap(map);
                            area.setFarm(mArea.getFarm());

                            mArea = area;
                            mDatas.clear();

                            parseSubareaData();
                            notifyData();
                            break;
                        }
                    }
                });
    }

    private void notifyData() {
        mAdapter.notifyDataSetChanged();
        if (mDatas.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
    }

    private void showNoData() {
        rlNoData.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(rlNoData);
    }

    private void hideNoData() {
        YoYo.with(Techniques.FadeOut)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rlNoData.setVisibility(View.GONE);
                    }
                })
                .duration(100)
                .playOn(rlNoData);
    }

    class RealtimeAdapter extends CommonAdapter<RealtimeData> {

        public RealtimeAdapter(Context context, List<RealtimeData> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, RealtimeData item) {
            double averageValue = Double.valueOf(item.getAverageValue());
            helper
                    .setText(R.id.tv_name, item.getName())
                    .setText(R.id.tv_average_value, String.format("%.2f", averageValue));//保留两位小数

            ImageView ivItem = helper.getView(R.id.iv_img);

            // 设置icon和单位
            String unit = "";
            if (item.getName().contains("温度")) {
                ivItem.setImageBitmap(temperature);
                unit = "℃";
            } else if (item.getName().contains("湿度")) {
                ivItem.setImageBitmap(humidity);
                unit = "%RH";
            } else if (item.getName().contains("光照")) {
                ivItem.setImageBitmap(illuminate);
                unit = "LUX";
            }
            helper.setText(R.id.tv_unit, unit);//单位

            List<String> values = item.getValues();
            LinearLayout llMore = helper.getView(R.id.llMore);
            LinearLayout llItem1 = helper.getView(R.id.llItem1);
            LinearLayout llItem2 = helper.getView(R.id.llItem2);
            TextView tvValue1 = helper.getView(R.id.tv_value1);
            TextView tvValue2 = helper.getView(R.id.tv_value2);

            llMore.setVisibility(values.size() > 2 ? View.VISIBLE : View.GONE);//设置更多按钮
            llItem2.setVisibility(View.GONE);
            llItem1.setVisibility(View.GONE);

            int showItems = values.size() > 2 ? 2 : values.size();
            switch (showItems) {
                case 2:
                    tvValue2.setText(getFormatValue(values.get(1)) + unit);
                    llItem2.setVisibility(View.VISIBLE);
                case 1:
                    tvValue1.setText(getFormatValue(values.get(0)) + unit);
                    llItem1.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private String getFormatValue(String s) {
        double v = Double.valueOf(s);
        return String.format("%.2f", v);
    }
}
