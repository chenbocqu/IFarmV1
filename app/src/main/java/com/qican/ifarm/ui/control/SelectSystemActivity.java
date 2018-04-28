/**
 * 选择控制系统
 */
package com.qican.ifarm.ui.control;


import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.SystemAdapter;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SelectSystemActivity extends CommonListActivity<ControlSystem> {

    List<ControlSystem> mDatas;
    SystemAdapter systemAdapter;
    ControlFunction controlFun;

    @Override
    public String getUITitle() {
        return "选择控制系统";
    }

    @Override
    public ComAdapter<ControlSystem> getAdapter() {
        return systemAdapter;
    }

    @Override
    public void onRefresh(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.refreshFinish(true);
                notifyDatasetChanged();
            }
        }, 800);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.loadMoreFinish(true);
            }
        }, 800);
    }

    @Override
    public void init() {
        controlFun = (ControlFunction) myTool.getParam(ControlFunction.class);

        initSysDatas();
        setRightMenu("添加系统", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.showInfo("新增待实现···");
            }
        });

    }

    private void initSysDatas() {
//        mDatas = IFarmFakeData.getSystemList(); //模拟数据
        mDatas = new ArrayList<>();

        systemAdapter = new SystemAdapter(this, mDatas, R.layout.item_system);

        showProgress();

        OkHttpUtils.post().url(myTool.getServAdd() + "farmControl/controlSystemState")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        myTool.log("控制系统 Items 内容:\n" + response);

                        JSONArray array = null;
                        try {
                            array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                ControlSystem sys = null;
                                JSONArray sysArr = array.getJSONArray(i);
                                if (sysArr != null) {
                                    sys = new ControlSystem();

                                    sys.setSysId(sysArr.getString(0));
                                    sys.setFarmId(sysArr.getString(1));
                                    sys.setCollectorId(sysArr.getString(2));
                                    sys.setSysType(sysArr.getString(3));
                                    sys.setSysDistrict(sysArr.getString(4));

                                    sys.setDesc(sysArr.getString(5));
                                    sys.setFertilizingNum(sysArr.getInt(6));
                                    sys.setDistrictSum(sysArr.getInt(7));
                                    sys.setLocation(sysArr.getString(8));
                                    sys.setTime(sysArr.getString(9));
                                } else continue;
                                if (sys != null)
                                    mDatas.add(sys);
                            }
                            notifyDatasetChanged();

                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }
}
