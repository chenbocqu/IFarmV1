/**
 * 设备数据列表
 */

package com.qican.ifarm.ui_v2.farm;


import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.MonitorNodeAdapter;
import com.qican.ifarm.bean.DevicePara;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class DeviceDataListActivity extends CommonListActivity<MonitorNode> {

    Farm mFarm;
    List<MonitorNode> mDatas;
    MonitorNodeAdapter mAdpater;

    @Override
    public String getUITitle() {
        return mFarm.getName();
    }

    @Override
    public void init() {
        mFarm = (Farm) myTool.getParam(Farm.class);
        mDatas = new ArrayList<>();
        mAdpater = new MonitorNodeAdapter(this, mDatas, R.layout.item_monitor_node);
        requestData(null);
    }

    @Override
    public ComAdapter<MonitorNode> getAdapter() {
        return mAdpater;
    }

    @Override
    public void onRefresh(PullToRefreshLayout l) {
        mDatas.clear();
        mAdpater.notifyDataSetChanged();

        requestData(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                pullToRefreshLayout.refreshFinish(false);
            }

            @Override
            public void onResponse(String response, int id) {
                pullToRefreshLayout.refreshFinish(true);
            }
        });
    }

    @Override
    public void onLoadMore(PullToRefreshLayout l) {
        pullToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshLayout.loadMoreFinish(true);
            }
        }, 1000);
    }

    private void requestData(final StringCallback callback) {

        myTool.log("start get farm data ... ");

        OkHttpUtils.post().url(myTool.getServAdd() + "collectorDeviceValue/collectorDeviceCurrentValue")
                .addParams("farmId", mFarm.getId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (callback != null)
                            callback.onError(call, e, id);
                        myTool.log(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (callback != null)
                            callback.onResponse(response, id);
                        myTool.log(response);

                        if (response == null) return;

                        JSONArray array = null;
                        try {
                            array = new JSONArray(response);
                            if (array.length() == 0) {
                                // TODO: 当前无数据
                                myTool.showInfo("无数据！");
                                return;
                            }

                            // for different device type
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                // 类型名称
                                String type = obj.getString("type");
                                String typeName = obj.getString("typeName");

                                // code
                                String unit = obj.getString("unit");
                                String code = obj.getString("code");        // 对应的键值
                                String header = obj.getString("header");    // 中文名称

                                String[] codes = code.split(",");
                                String[] headers = header.split(",");
                                String[] units = unit.split(",");

                                // get data array in each type
                                JSONArray dataArray = obj.getJSONArray("data");

                                // for each device data
                                for (int index = 0; index < dataArray.length(); index++) {
                                    MonitorNode node = new MonitorNode();
                                    JSONObject nodeObj = dataArray.getJSONObject(index);

                                    if (!nodeObj.has("deviceId")) continue;

                                    // device info
                                    node.setDeviceId(nodeObj.getString("deviceId"));
                                    node.setOrderNo(nodeObj.getString("deviceOrderNo"));
                                    node.setDistrict(nodeObj.getString("deviceDistrict"));
                                    node.setLocation(nodeObj.getString("deviceLocation"));
                                    node.setType(nodeObj.getString("deviceType"));

                                    // to check has data or not ...
//                                    if (!nodeObj.has("deviceValueId")) {
//                                        node.setHashData(false);
//                                        mDatas.add(node);
//                                        continue; // for next data ...
//                                    }

                                    // exist data value, start wraping data ...
                                    node.setHashData(true);

                                    List<DevicePara> paras = new ArrayList<DevicePara>();
                                    // paras in data ...
                                    for (
                                            int codeId = 0;
                                            codeId < codes.length && codeId < headers.length && codeId < units.length;
                                            codeId++) {

                                        DevicePara para = new DevicePara();
                                        para.setName(headers[codeId]);
                                        para.setCode(codes[codeId]);
                                        para.setUnit(units[codeId]);

                                        para.setValue(nodeObj.getString(codes[codeId]));

                                        // add into list ...
                                        paras.add(para);

                                        node.setNodeDatas(paras);
                                    }

                                    // other data ...
                                    node.setUpdateTime(nodeObj.getString("updateTime"));

                                    // Add node data to mDatas to show ...
                                    mDatas.add(node);
                                }
                            }

                            myTool.showInfo("更新了" + mDatas.size() + "条数据！");
                            notifyDatasetChanged();


                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }
}
