/**
 * 选择控制系统
 */
package com.qican.ifarm.ui.control;


import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.SystemAdapter;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.bean.Sensor;
import com.qican.ifarm.beanfromservice.FarmSensor;
import com.qican.ifarm.listener.BeanCBWithTkCk;
import com.qican.ifarm.ui.base.CommonListActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sf.json.JSONArray;

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
    public CommonAdapter<ControlSystem> getAdapter() {
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
    }

    private void initSysDatas() {
        mDatas = IFarmFakeData.getSystemList();
        systemAdapter = new SystemAdapter(this, mDatas, R.layout.item_system);

        showProgress();

        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "farmControl/controlSystemState")
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
                        myTool.log("Control System Items:\n" + response);

                        JSONArray array = JSONArray.fromObject(response);
                        for (int i = 0; i < array.size(); i++) {
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
                    }
                });
    }
}
