package com.qican.ifarm.ui_v2.control;

import android.app.Activity;

import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.ui_v2.base.Window4ComSelected;
import com.qican.ifarm.utils.IFarmFakeData;

public class PopWin4SelectArea extends Window4ComSelected<Subarea> {

    Farm mFarm;

    public PopWin4SelectArea(Activity context, Farm farm) {
        super(context);
        mFarm = farm;
    }

    @Override
    protected String setTitle() {
        return "请选择区域";
    }

    @Override
    public void refreshData() {
        setRefreshing(true);
        setDatas(IFarmFakeData.getSuberas());
//        if (netRequest != null && mItem != null)
//            netRequest.getSubareaData(mItem, new DataAdapter() {
//                @Override
//                public void subareas(List<Subarea> data) {
//                    setDatas(data);
//                }
//            });

    }
}
