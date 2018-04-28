package com.qican.ifarm.ui.node;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.MonitorNodeAdapter;
import com.qican.ifarm.bean.DevicePara;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class NodeListFragment extends Fragment implements PullToRefreshLayout.OnRefreshListener {

    private View view;
    private CommonTools myTool;

    private List<MonitorNode> mDatas;
    private MonitorNodeAdapter mAdpater;

    //下拉刷新
    private PullToRefreshLayout mRefreshLayout;
    private PullListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_monitorlist, container, false);
        initView(view);
        initDatas();
        initEvent();

        return view;
    }

    private void initDatas() {

        mDatas = new ArrayList<>();
        mAdpater = new MonitorNodeAdapter(getActivity(), mDatas, R.layout.item_monitor_node);
        mListView.setAdapter(mAdpater);

        requestData(null);
    }

    private void requestData(final StringCallback callback) {
        myTool.log("start get farm data ... ");
        OkHttpUtils.post().url("http://172.22.206.64:8080/IFarm/collectorDeviceValue/collectorDeviceCurrentValue")
                .addParams("farmId", "10000001")
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
                                // code
                                String code = obj.getString("code");
                                String header = obj.getString("header");
                                String unit = obj.getString("unit");

                                String[] codes = code.split(",");
                                String[] headers = header.split(",");
                                String[] units = unit.split(",");

                                // get data array in each type
                                JSONArray dataArray = obj.getJSONArray("data");

                                // for each device data
                                for (int index = 0; index < dataArray.length(); index++) {
                                    MonitorNode node = new MonitorNode();
                                    JSONObject nodeObj = dataArray.getJSONObject(index);

                                    // device info
                                    node.setDeviceId(nodeObj.getString("deviceId"));
                                    node.setOrderNo(nodeObj.getString("deviceOrderNo"));
                                    node.setDistrict(nodeObj.getString("deviceDistrict"));
                                    node.setLocation(nodeObj.getString("deviceLocation"));

                                    // to check has data or not ...
                                    if (!nodeObj.has("deviceValueId")) {
                                        node.setHashData(false);
                                        mDatas.add(node);
                                        continue; // for next data ...
                                    }

                                    // exist data value, start wraping data ...
                                    node.setHashData(true);
                                    node.setId(nodeObj.getString("deviceValueId"));

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
                            mAdpater.notifyDataSetChanged();

                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initView(View v) {
        mRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.pullToRefreshLayout);
        mListView = (PullListView) v.findViewById(R.id.pullListView);

        myTool = new CommonTools(getActivity());
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {

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
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshLayout.loadMoreFinish(true);
            }
        }, 1000);
    }

}
