package com.qican.ifarm.adapter;


import android.content.Context;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.DevicePara;

import java.util.List;

public class DeviceParaAdapter extends ComAdapter<DevicePara> {
    public DeviceParaAdapter(Context context, List<DevicePara> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, DevicePara item) {
        myTool.setHeightByWindow(helper.getView(R.id.rl_item), 1.0 / 3.9);
        helper
                .setText(R.id.tv_para, item.getValue())
                .setText(R.id.tv_unit, item.getUnit())
                .setText(R.id.tv_name, item.getName());
    }
}
