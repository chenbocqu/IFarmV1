package com.qican.ifarm.ui_v2.task;


import android.content.Intent;
import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.IntegrateTaskAdapter;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.util.ArrayList;

public class HistoryTaskListActivity extends CommonListActivity<Task> {

    ArrayList<Task> mData;
    public static final String RESULT_TASK_LIST = "RESULT_TASK_LIST";

    @Override
    public String getUITitle() {
        return "历史任务";
    }

    @Override
    public void init() {
        mData = (ArrayList<Task>) myTool.getParam(ArrayList.class);
        if (mData == null) mData = new ArrayList<>();

        setRightMenu("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra(RESULT_TASK_LIST, mData);
                setResult(RESULT_OK, intent);

                finish();
            }
        });

    }

    @Override
    public ComAdapter<Task> getAdapter() {
        return new IntegrateTaskAdapter(this, mData, R.layout.item_common_task);
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
