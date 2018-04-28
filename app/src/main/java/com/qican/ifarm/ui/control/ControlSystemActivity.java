/**
 * 灌溉，施肥控制系统
 *
 * @时间 2017-5-4
 * @作者 chenbocqu
 */
package com.qican.ifarm.ui.control;

import android.view.View;
import android.widget.ImageView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.TaskAdapter;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.ui_v2.base.ContentWithBaseListActivity;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.util.List;


public class ControlSystemActivity extends ContentWithBaseListActivity<Task> {

    List<Task> mDatas;
    TaskAdapter mAdapter;
    private ImageView ivWatering,
            ivTank1, ivTank2, ivTank3,
            ivA, ivB, ivC, ivD, ivE;

    @Override
    public String getUITitle() {
        return "灌溉&施肥";
    }

    @Override
    public void init() {
        initView();
        initData();
    }

    @Override
    public ComAdapter<Task> getAdapter() {
        return mAdapter;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_controlsys_with_tasklist;
    }

    @Override
    public void onRefresh(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.refreshFinish(true);
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

    void initView() {
        ivWatering = (ImageView) findViewById(R.id.iv_watering);

        ivTank1 = (ImageView) findViewById(R.id.iv_tank1);
        ivTank2 = (ImageView) findViewById(R.id.iv_tank2);
        ivTank3 = (ImageView) findViewById(R.id.iv_tank3);

        ivA = (ImageView) findViewById(R.id.iv_a);
        ivB = (ImageView) findViewById(R.id.iv_b);
        ivC = (ImageView) findViewById(R.id.iv_c);
        ivD = (ImageView) findViewById(R.id.iv_d);
        ivE = (ImageView) findViewById(R.id.iv_e);
    }

    void initData() {
        mDatas = IFarmFakeData.getTaskList();
        mAdapter = new TaskAdapter(this, mDatas, R.layout.item_task);
        setRightMenu("管理任务", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivity(ManagerTaskActivity.class);
            }
        });

        showStatusByTask(false, true, false, false, true, false, false, true, true);
    }

    private void resetTaskUI() {
        ivWatering.setVisibility(View.GONE);

        ivTank1.setVisibility(View.GONE);
        ivTank2.setVisibility(View.GONE);
        ivTank3.setVisibility(View.GONE);

        ivA.setVisibility(View.GONE);
        ivB.setVisibility(View.GONE);
        ivC.setVisibility(View.GONE);
        ivD.setVisibility(View.GONE);
        ivE.setVisibility(View.GONE);
    }

    private void showStatusAllStop() {
        showStatusByTask(true, false, false, false, false, false, false, false, false);
    }

    void showStatusByTask(Task task) {
        showStatusByTask(false
                , task.isTank1(), task.isTank2(), task.isTank3()
                , task.isA(), task.isB(), task.isC(), task.isD(), task.isE());
    }

    private void showStatusByTask(boolean isAllStop
            , boolean isTank1, boolean isTank2, boolean isTank3
            , boolean isA, boolean isB, boolean isC, boolean isD, boolean isE) {
        resetTaskUI();//清除其他任务状态
        if (isAllStop) {
            return;
        }

        ivWatering.setVisibility(View.VISIBLE);

        if (isTank1)
            ivTank1.setVisibility(View.VISIBLE);
        if (isTank2)
            ivTank2.setVisibility(View.VISIBLE);
        if (isTank3)
            ivTank3.setVisibility(View.VISIBLE);

        if (isA)
            ivA.setVisibility(View.VISIBLE);
        if (isB)
            ivB.setVisibility(View.VISIBLE);
        if (isC)
            ivC.setVisibility(View.VISIBLE);
        if (isD)
            ivD.setVisibility(View.VISIBLE);
        if (isE)
            ivE.setVisibility(View.VISIBLE);
    }
}
