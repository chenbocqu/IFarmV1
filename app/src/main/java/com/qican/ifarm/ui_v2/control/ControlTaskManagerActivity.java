package com.qican.ifarm.ui_v2.control;


import android.content.Intent;
import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ControlTask;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.utils.TimeUtils;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ControlTaskManagerActivity extends CommonListActivity<ControlTask> {

    String title, error = "Error";
    List<ControlTask> mDatas;
    public static int REQUEST_NEW_TASK = 1;

    @Override
    public String getUITitle() {
        return title;
    }

    @Override
    public void init() {
        title = (String) myTool.getParam(String.class);
        if (title == null) title = error;

//        mDatas = IFarmFakeData.getControlTasks();
        mDatas = new ArrayList<>();

        setRightMenu("新任务", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivityForResult(title, ControlActivity.class, REQUEST_NEW_TASK);
            }
        });
    }

    @Override
    public ComAdapter<ControlTask> getAdapter() {
        return new ComAdapter<ControlTask>(this, mDatas, R.layout.item_control_task) {
            @Override
            public void convert(ViewHolder helper, final ControlTask item) {
                helper.setText(R.id.tv_time, item.getStartTime());
                helper.setText(R.id.tv_name, item.getOperation());
                helper.setText(R.id.tv_desc, item.getLocation() +
                        "的所有点位，自" + TimeUtils.formatTime(item.getStartTime()) +
                        "开始，持续执行" + item.getLastTime() + "。");
                helper.getView(R.id.iv_task_info).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myTool.startActivity(item, ControlTaskInfoActivity.class);
                    }
                });
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        ControlTask task = (ControlTask) data.getSerializableExtra(ControlActivity.KEY_NEW_TASK);
        if (task != null) {
            mDatas.add(task);
            notifyDatasetChanged();
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout l) {
        l.refreshFinish(true);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout l) {
        l.loadMoreFinish(true);
    }
}
