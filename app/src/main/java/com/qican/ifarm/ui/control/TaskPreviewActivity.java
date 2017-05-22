package com.qican.ifarm.ui.control;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.ui.base.BaseActivityWithTitlebar;
import com.qican.ifarm.utils.IFarmFakeData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskPreviewActivity extends BaseActivityWithTitlebar implements ViewPager.OnPageChangeListener {

    private static final String TAG = "Socket";
    private ImageView ivWatering,
            ivTank1, ivTank2, ivTank3,
            ivA, ivB, ivC, ivD, ivE;

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    List<Task> mDatas;
    private List<Fragment> mTaskPagers = new ArrayList<Fragment>();
    public static final String TASK_KEY = "TASK_KEY";
    private int runningLoc = 0, curIndex = 0;
    private CountDownTimer timerToRunning;

    @Override
    public String getUITitle() {
        return "灌溉&施肥";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_task_preview;
    }

    @Override
    public void init() {
        initControlSysUI();

        initData();

        setFragemnts(); //加载页面及数据

        setRightMenu("管理任务", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageTask();
            }
        });

        initEvent();
    }

    private void setFragemnts() {
        // 载入主UI的几个页面
        loadFragments();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTaskPagers.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTaskPagers.get(position);
            }

        };
        mViewPager.setAdapter(mAdapter);

        timerToRunning = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                myTool.showInfo(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                mViewPager.setCurrentItem(runningLoc, true);
            }
        };
    }

    private void loadFragments() {
        for (Task task : mDatas) {
            TaskInfoFragment fg = new TaskInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(TASK_KEY, task);
            fg.setArguments(bundle);

            mTaskPagers.add(fg);
        }
    }

    void initEvent() {
        mViewPager.setOnPageChangeListener(this);
    }

    private void manageTask() {
        myTool.startActivity(ManagerTaskActivity.class);
    }

    private void initControlSysUI() {
        mViewPager = (ViewPager) findViewById(R.id.vp_task);

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

    private void initData() {
        mDatas = IFarmFakeData.getTaskList();
        showStatusAllStop();
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getStatus() == Task.TaskStatus.Running)
                runningLoc = i;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showRunningTask();
    }

    private void showRunningTask() {
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(runningLoc, true);
            }
        }, 1200);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
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

    void showStatusByTask(Task task) {
        showStatusByTask(false
                , task.isTank1(), task.isTank2(), task.isTank3()
                , task.isA(), task.isB(), task.isC(), task.isD(), task.isE());
    }

    private void showStatusAllStop() {
        showStatusByTask(true, false, false, false, false, false, false, false, false);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        myTool.log("Timer cancel [ " + positionOffset + " ]");
    }

    @Override
    public void onPageSelected(int position) {
        curIndex = position;
        showStatusByTask(mDatas.get(position));
        setTitle(mDatas.get(position).getName());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 0:
                if (runningLoc != curIndex) {
                    timerToRunning.start();
                }
                break;
            case 1:
            case 2:
                timerToRunning.cancel();
                break;
        }
    }
}
