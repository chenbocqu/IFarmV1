package com.qican.ifarm.ui_v2.control;


import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ControlNodeAdapter;
import com.qican.ifarm.bean.ControlNode;
import com.qican.ifarm.bean.ControlTask;
import com.qican.ifarm.ui_v2.base.ContentWithBaseListActivity;
import com.qican.ifarm.utils.TimeUtils;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControlTaskInfoActivity extends ContentWithBaseListActivity<ControlNode> {

    String title, error = "Error";

    List<ControlNode> mDatas;
    TextView tvLoaction, tvStartTime, tvLastTime;
    ControlTask mTask;

    @Override
    public String getUITitle() {
        return title;
    }

    @Override
    public void init() {
        mTask = (ControlTask) myTool.getParam(ControlTask.class);

        if (mTask == null) title = error;
        else title = mTask.getOperation();

        mDatas = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ControlNode node = new ControlNode();
            node.setName("节点" + (i + 1));
            node.setDesc("这是关于" + "节点" + (i + 1) + "的备注信息。");
            mDatas.add(node);
        }


        tvLoaction = (TextView) getHeaderView().findViewById(R.id.tv_location);
        tvStartTime = (TextView) getHeaderView().findViewById(R.id.tv_starttime);
        tvLastTime = (TextView) getHeaderView().findViewById(R.id.tv_lasttime);

        tvLoaction.setText(mTask.getLocation());
        tvStartTime.setText(TimeUtils.formatTime(mTask.getStartTime()));
        tvLastTime.setText(mTask.getLastTime());
    }

    @Override
    protected int getHeadLayout() {
        return R.layout.task_info_header;
    }

    @Override
    public ComAdapter<ControlNode> getAdapter() {
        return new ControlNodeAdapter(this, mDatas, R.layout.item_control_node);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_control_taskinfo;
    }


    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.refreshFinish(true);
            }
        }, 1500);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.loadMoreFinish(true);
            }
        }, 1500);
    }

}
