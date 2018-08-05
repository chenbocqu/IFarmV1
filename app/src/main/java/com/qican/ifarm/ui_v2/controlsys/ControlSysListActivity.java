package com.qican.ifarm.ui_v2.controlsys;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ControlSys;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.ui_v2.base.CommonListV2Activity;
import com.qican.ifarm.ui_v2.task.TaskListActivity;
import com.qican.ifarm.utils.TimeUtils;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ControlSysListActivity extends CommonListV2Activity<ControlSys> {

    List<ControlSys> mData;
    String title, error = "Error";
    private String systemCode;
    Bitmap bitmap;

    @Override
    public String getUITitle() {
        return title;
    }

    @Override
    public void init() {
        title = (String) myTool.getParam(String.class);
        if (title == null) title = error;

        mData = new ArrayList<>();

        switch (title) {
            case "温度":
                systemCode = "temperature";
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_temperature);
                break;
            case "湿度":
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_humidity);
                systemCode = "humidity";
                break;
            case "通风":
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_air);
                systemCode = "ventilate";
                break;
            case "补光":
                systemCode = "supplementaryLighting";
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_illuminate);
                break;
            case "遮阳":
                systemCode = "sunshade";
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_yusan);
                break;
            case "卷帘":
                systemCode = "rollerShutters";
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_juanlian);
                break;
            case "水帘":
                systemCode = "waterRollerShutters"; //修改
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_shuilian);
                break;
            case "二氧化碳":
                systemCode = "carbonDioxide";
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_co2);
                break;
            case "氧气":
                systemCode = "oxygen";
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fun_o2);
                break;
            default:
        }

        requestData();
        setRefreshListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
    }

    private void requestData() {

        // 请求数据
        myTool.log("systemCode: " + systemCode);

        showLoading();

        OkHttpUtils.post().url(myTool.getServAdd() + "farmControl/controlSystemList")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("systemCode", systemCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        // 点击空白处重新刷新
                        showErr(e.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestData();
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        myTool.log(response);

                        if ("lose efficacy".equals(response)) {
                            showNoLogin();
                            return;
                        }

                        if (response == null || "[]".equals(response)) {
                            showContentByData(false);
                            return;
                        }

                        JSONArray array = null;
                        try {
                            array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                ControlSys sys = new ControlSys();
                                if (obj.has("systemId"))
                                    sys.setSystemId(obj.getString("systemId"));

                                if (obj.has("farmId"))
                                    sys.setFarmId(obj.getString("farmId"));

                                if (obj.has("farmName"))
                                    sys.setFarmName(obj.getString("farmName"));

                                if (obj.has("systemCode"))
                                    sys.setSystemCode(obj.getString("systemCode"));

                                if (obj.has("systemType"))
                                    sys.setSystemType(obj.getString("systemType"));

                                if (obj.has("systemTypeCode"))
                                    sys.setSystemTypeCode(obj.getString("systemTypeCode"));

                                if (obj.has("systemDistrict"))
                                    sys.setSystemDistrict(obj.getString("systemDistrict"));

                                if (obj.has("systemDescription"))
                                    sys.setSystemDescription(obj.getString("systemDescription"));

                                if (obj.has("canNum"))
                                    sys.setCanNum(obj.getString("canNum"));

                                if (obj.has("districtSum"))
                                    sys.setDistrictSum(obj.getString("districtSum"));

                                if (obj.has("systemLocation"))
                                    sys.setSystemLocation(obj.getString("systemLocation"));

                                if (obj.has("systemCreateTime"))
                                    sys.setSystemCreateTime(obj.getString("systemCreateTime"));

                                if (obj.has("systemNo"))
                                    sys.setSystemNo(obj.getString("systemNo"));

                                myTool.log(sys.toString());
                                mData.add(sys);
                            }

                            notifyDatasetChanged();


                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public ComAdapter<ControlSys> getAdapter() {
        return new ComAdapter<ControlSys>(this, mData, R.layout.item_control_sys) {
            @Override
            public void convert(ViewHolder helper, final ControlSys item) {
                ImageView ivHead = helper.getView(R.id.iv_img);
                ivHead.setImageBitmap(bitmap);

                helper
                        .setText(R.id.tv_name, item.getSystemType())
                        .setText(R.id.tv_time, TimeUtils.formatTime(item.getSystemCreateTime()))
                        .setText(R.id.tv_desc,
                                ("".equals(item.getSystemDescription()) ? item.getSystemDescription() : "暂无系统描述") +
                                        "（" + item.getFarmName() + item.getSystemDistrict() + "）");

                helper.getView(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myTool.startActivity(item, TaskListActivity.class);
                    }
                });
            }
        };
    }

    @Override
    public void onRefresh(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.refreshFinish(true);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.loadMoreFinish(true);
            }
        }, 1000);
    }

}
