/**
 * 添加池塘
 */
package com.qican.ifarm.ui.subarea;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomListViewEx;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.utils.CommonTools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class SubareasOfFarmActivity extends Activity implements View.OnClickListener {
    private static final int LABEL_MAX_SHOW_LEN = 20;
    private CommonTools myTool;
    private LinearLayout llBack;
    private PullToZoomListViewEx listView;
    private SubareaAdapter mAdapter;
    private List<Subarea> mData;
    private TextView tvTitle, tvDesc;
    private ImageView ivBg;
    private Farm mFarm;
    private TextView tvLabel1, tvLabel2, tvLabel3, tvMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subareas_of_farm);
        initView();
        initHeader();
        initEvent();
    }

    private void initHeader() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        listView.setHeaderLayoutParams(localObject);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        mFarm = new Farm();
        mFarm = (Farm) myTool.getParam(mFarm);
        if (mFarm != null) {
            myTool.showImage(mFarm.getImgUrl(), ivBg, R.mipmap.default_farm_img);
            tvDesc.setText(mFarm.getDesc());
            tvTitle.setText(mFarm.getName());
            setLabel(mFarm.getLabel());
        }

        mData = new ArrayList<>();
        mAdapter = new SubareaAdapter(this, mData, R.layout.item_subarea);
        listView.setAdapter(mAdapter);
        getSubareaData();
    }

    private void getSubareaData() {
        OkHttpUtils.post().url(myTool.getServAdd() + "sensor/getSensorVaules")
                .addParams("farmId", mFarm.getId())
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

                        if ("lose efficacy".equals(response)) {
                            myTool.showTokenLose();
                            return;
                        }

                        if (response == null || "[]".equals(response)) {
                            myTool.showInfo("该农场暂无分区，请先添加！");
                            return;
                        }

                        myTool.log("分区列表list：" + response);
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(response);
                            JSONObject jsonObject = arr.getJSONObject(0);
                            Iterator<String> iterator = jsonObject.keys();
                            while (iterator.hasNext()) {
                                Subarea area = new Subarea();
                                String key = iterator.next();
                                myTool.log("\n当前分区为:" + key);

                                Map<String, List<String>> map = new HashMap<String, List<String>>();

                                JSONArray array = jsonObject.getJSONArray(key);

                                for (int i = 0; i < array.length(); i++) {
                                    // 取参数
                                    JSONObject object = array.getJSONObject(i);
                                    JSONArray paraName = object.getJSONArray("sensorParam");
                                    JSONArray paraData = object.getJSONArray("data");
                                    for (int j = 0; j < paraName.length() && j < paraData.length(); j++) {
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
                                area.setFarmId(mFarm.getId());
                                area.setDataMap(map);
                                area.setFarm(mFarm);
                                area.setImgUrl("http://img4.imgtn.bdimg.com/it/u=3436548158,2996155944&fm=23&gp=0.jpg");

                                mData.add(area);
                            }
                            notifyData();

                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }

    private void notifyData() {
        myTool.log("分区条数：" + mData.size());
        mAdapter.notifyDataSetChanged();
        if (mData.isEmpty()) {
//            showNoData();
        } else {
//            hideNoData();
        }
    }

    private void initView() {
        myTool = new CommonTools(this);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        listView = (PullToZoomListViewEx) findViewById(R.id.listview);

        ivBg = (ImageView) listView.getPullRootView().findViewById(R.id.iv_bgimg);
        tvDesc = (TextView) listView.getPullRootView().findViewById(R.id.tv_desc);

        tvLabel1 = (TextView) listView.getPullRootView().findViewById(R.id.tvLabel1);
        tvLabel2 = (TextView) listView.getPullRootView().findViewById(R.id.tvLabel2);
        tvLabel3 = (TextView) listView.getPullRootView().findViewById(R.id.tvLabel3);
        tvMore = (TextView) listView.getPullRootView().findViewById(R.id.tvMore);

        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    private void initEvent() {
        llBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
        }
    }

    /**
     * 设置标签
     */
    private void setLabel(Label label) {

        List<TextView> tvLabels = new ArrayList<>();
        tvLabels.add(tvLabel1);
        tvLabels.add(tvLabel2);
        tvLabels.add(tvLabel3);

        boolean showMore = false;
        //全部设置为不见
        for (int i = 0; i < tvLabels.size(); i++) {
            tvLabels.get(i).setVisibility(View.GONE);
        }

        if (label == null) {
            return;
        }
        List<String> labelList = label.getLabelList();
        int totalLen = 0;
        for (int i = 0; i < tvLabels.size() && i < labelList.size(); i++) {
            totalLen = totalLen + labelList.get(i).length();
            if (totalLen > LABEL_MAX_SHOW_LEN) {
                showMore = true;
                break;
            }
            tvLabels.get(i).setVisibility(View.VISIBLE);
            tvLabels.get(i).setText(labelList.get(i));
        }
        // 本身比标签容量多，或者显示不够完全，则显示更多
        tvMore.setVisibility(
                labelList.size() > tvLabels.size() ||
                        showMore ?
                        View.VISIBLE : View.GONE);

    }

    class SubareaAdapter extends ComAdapter<Subarea> {

        public SubareaAdapter(Context context, List<Subarea> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, final Subarea item) {
            helper.setText(R.id.tv_name, item.getName()).setImageByUrl(R.id.iv, item.getImgUrl());

            RelativeLayout rlItem = helper.getView(R.id.rl_item);
            rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myTool.startActivity(item, SubareaActivity.class);
                }
            });
        }
    }
}
