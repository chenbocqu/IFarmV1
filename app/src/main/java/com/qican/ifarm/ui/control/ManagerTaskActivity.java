/**
 * 管理任务
 */
package com.qican.ifarm.ui.control;

import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.TaskAdapter;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.ui.base.CommonListActivity;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.util.List;

public class ManagerTaskActivity extends CommonListActivity<Task> {
    List<Task> mDatas;
    TaskAdapter mAdapter;

    @Override
    public String getUITitle() {
        return "管理任务";
    }

    @Override
    public void init() {
        mDatas = IFarmFakeData.getTaskList();
        mAdapter = new TaskAdapter(this, mDatas, R.layout.item_task);
        setRightMenu("新任务", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivity(NewTaskActivity.class);
            }
        });
    }

    @Override
    public CommonAdapter<Task> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onRefresh(PullToRefreshLayout l) {

    }

    @Override
    public void onLoadMore(PullToRefreshLayout l) {

    }
}
