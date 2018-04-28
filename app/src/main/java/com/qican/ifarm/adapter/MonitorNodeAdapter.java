package com.qican.ifarm.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.DevicePara;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.ui_v2.farm.DeviceDataActivity;
import com.qican.ifarm.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.progressbar.BGAProgressBar;


public class MonitorNodeAdapter extends ComAdapter<MonitorNode> {

    public MonitorNodeAdapter(Context context, List<MonitorNode> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, final MonitorNode node) {

        ImageView ivNodata = helper.getView(R.id.iv_nodata);
        LinearLayout llPara = helper.getView(R.id.ll_para);

        TextView tvStatus = helper.getView(R.id.tv_status);
        helper
                .setText(R.id.tv_value_id, "ID:" + node.getDeviceId())
                .setText(R.id.tv_nodename,
                        node.getLocation() + node.getDistrict() + "区[" + node.getOrderNo() + "号]")
                .setText(R.id.tv_updatetime, "当前节点暂无监测数据 ...");

        helper.getView(R.id.rl_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivity(node, DeviceDataActivity.class);
            }
        });

        if (!node.hashData()) {
            tvStatus.setText("离线");
            llPara.setVisibility(View.GONE);
            ivNodata.setVisibility(View.VISIBLE);
            return;
        }

        llPara.setVisibility(View.VISIBLE);
        ivNodata.setVisibility(View.GONE);

        List<BGAProgressBar> progressBars = new ArrayList<>();
        progressBars.add((BGAProgressBar) helper.getView(R.id.pb_1));
        progressBars.add((BGAProgressBar) helper.getView(R.id.pb_2));
        progressBars.add((BGAProgressBar) helper.getView(R.id.pb_3));
        progressBars.add((BGAProgressBar) helper.getView(R.id.pb_4));
        progressBars.add((BGAProgressBar) helper.getView(R.id.pb_5));

        List<TextView> textViews = new ArrayList<>();
        textViews.add((TextView) helper.getView(R.id.tv_para1));
        textViews.add((TextView) helper.getView(R.id.tv_para2));
        textViews.add((TextView) helper.getView(R.id.tv_para3));
        textViews.add((TextView) helper.getView(R.id.tv_para4));
        textViews.add((TextView) helper.getView(R.id.tv_para5));

        List<TextView> teNames = new ArrayList<>();
        teNames.add((TextView) helper.getView(R.id.tv_para_name1));
        teNames.add((TextView) helper.getView(R.id.tv_para_name2));
        teNames.add((TextView) helper.getView(R.id.tv_para_name3));
        teNames.add((TextView) helper.getView(R.id.tv_para_name4));
        teNames.add((TextView) helper.getView(R.id.tv_para_name5));

        List<DevicePara> paras = node.getNodeDatas();
        for (int i = 0; paras != null && i < paras.size() && i < progressBars.size(); i++) {
            DevicePara para = paras.get(i);
            progressBars.get(i).setProgress((int) (Float.parseFloat(para.getValue())));
            textViews.get(i).setText(para.getValue() + " " + para.getUnit());
            teNames.get(i).setText(para.getName());
        }
        helper
                .setText(R.id.tv_value_id, "ID:" + node.getId()) // value ID
                .setText(R.id.tv_updatetime, TimeUtils.formatTime(node.getUpdateTime()));

        if (node.getImgUrl() != null) {
            helper.setImageByUrl(R.id.iv_node_img, node.getImgUrl());
        }
    }
}
