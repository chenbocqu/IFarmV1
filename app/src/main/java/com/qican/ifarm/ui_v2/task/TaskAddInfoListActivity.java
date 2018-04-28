package com.qican.ifarm.ui_v2.task;

import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.TaskAddInfo;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.util.ArrayList;


public class TaskAddInfoListActivity extends CommonListActivity<TaskAddInfo> {
    ArrayList<TaskAddInfo> mData;

    @Override
    public String getUITitle() {
        return "任务添加情况";
    }

    @Override
    public void init() {
        mData = (ArrayList<TaskAddInfo>) myTool.getParam(ArrayList.class);
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    @Override
    public ComAdapter<TaskAddInfo> getAdapter() {
        return new ComAdapter<TaskAddInfo>(this, mData, R.layout.item_task_addinfo) {
            @Override
            public void convert(ViewHolder helper, TaskAddInfo item) {

                TextView tvStatus = helper.getView(R.id.tv_status);
                tvStatus.setBackgroundResource(R.drawable.label_bg_fill);

                switch (item.getCode()) {

                    case "running":
                        tvStatus.setBackgroundResource(R.drawable.label_bg_fill_green);
                        tvStatus.setText("成功");
                        break;

                    case "error":
                        tvStatus.setText("失败");
                        tvStatus.setBackgroundResource(R.drawable.label_bg_fill_red);
                        break;

                    case "schemerConflict":
                    case "currentRunning":
                        tvStatus.setBackgroundResource(R.drawable.label_bg_fill);
                        tvStatus.setText("冲突");
                        break;

                    default:
                        tvStatus.setText("未知错误");
                        tvStatus.setBackgroundResource(R.drawable.label_bg_fill_red);

                }

                helper
                        .setText(R.id.tv_name, item.getTaskName())
                        .setText(R.id.tv_desc, item.getInfo());
            }
        };
    }

    @Override
    public void onRefresh(final PullToRefreshLayout l) {
        l.refreshFinish(true);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.loadMoreFinish(true);
    }
}
