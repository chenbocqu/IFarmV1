/**
 * 带工具类的任务预览，布局文件中必须include control_system.xml 文件
 */
package com.qican.ifarm.ui.control;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.service.TaskService;
import com.qican.ifarm.ui_v2.base.BaseActivityWithTitlebar;
import com.qican.ifarm.utils.TaskDisplayerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskPreviewerActivity extends BaseActivityWithTitlebar implements ViewPager.OnPageChangeListener, OnInfoListener<String> {

    TaskService.TaskBinder taskBinder;

    private static final String TAG = "Socket";

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    List<Task> mDatas;
    private List<Fragment> mTaskPagers;
    public static final String TASK_KEY = "TASK_KEY";
    private int runningLoc = 0, curIndex = 0;
    private CountDownTimer timerToRunning;
    ControlSystem mSystem;
    private TaskDisplayerUtil taskDisplayer;
    RelativeLayout rlNodata;
    TextView tvNewTask;

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

        // 载入主UI的几个页面
        updateFragmentByData();

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

    private void updateFragmentByData() {

        mTaskPagers.clear();

        if (!mDatas.isEmpty()) {
            for (Task task : mDatas) {
                TaskInfoFragment fg = new TaskInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(TASK_KEY, task);
                fg.setArguments(bundle);

                mTaskPagers.add(fg);
            }
        }
        notifyDatasetChanged();
    }

    public void notifyDatasetChanged() {
        mAdapter.notifyDataSetChanged();
        if (mTaskPagers.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
    }

    private void showNoData() {
        rlNodata.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(rlNodata);
    }

    private void hideNoData() {
        YoYo.with(Techniques.FadeOut)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rlNodata.setVisibility(View.GONE);
                    }
                })
                .duration(100)
                .playOn(rlNodata);
    }

    void initEvent() {
        mViewPager.setOnPageChangeListener(this);
        tvNewTask.setOnClickListener(this);
    }

    private void manageTask() {
        myTool.startActivity(mSystem, ManagerTaskActivity.class);
    }

    private void initControlSysUI() {
        mViewPager = (ViewPager) findViewById(R.id.vp_task);
        rlNodata = (RelativeLayout) findViewById(R.id.rl_nothing_main);
        tvNewTask = (TextView) findViewById(R.id.tv_newtask);
        taskDisplayer = new TaskDisplayerUtil(this, findViewById(R.id.rl_task));
        myTool.setHeightByWindow(findViewById(R.id.rl_task), 2.0 / 3.2);
    }

    private void initData() {
        mSystem = (ControlSystem) myTool.getParam(ControlSystem.class);
        myTool.log("[farmId:" + mSystem.getFarmId() + ",collectorId:" + mSystem.getCollectorId() + "]");

//        mDatas = IFarmFakeData.getTaskList();
        mDatas = new ArrayList<>();
        addFarmInfo();
        mTaskPagers = new ArrayList<>();

        taskDisplayer.showStatusAllStop();

        moveToRunningTaskPager();
    }

    private void addFarmInfo() {
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).setFarmId(mSystem.getFarmId());
            mDatas.get(i).setCollectorId(mSystem.getCollectorId());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定任务服务
        bindService(new Intent(this, TaskService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
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
            case R.id.tv_newtask:
                myTool.startActivity(NewTaskActivity.class);
                break;
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        myTool.log("Timer cancel [ " + positionOffset + " ]");
    }

    @Override
    public void onPageSelected(int position) {
        curIndex = position;
        taskDisplayer.showStatusByTask(mDatas.get(position));
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

    // 与服务通信
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myTool.log("onServiceConnected");
            taskBinder = (TaskService.TaskBinder) service;
            taskBinder.setOnTaskMsgListener(TaskPreviewerActivity.this);
            taskBinder.queryTasks(mSystem);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myTool.log("onServiceDisconnected [ " + name.toString() + " ]");
        }
    };

    @Override
    public void onInfoChanged(final String obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parseTaskList(obj);
            }
        });
    }

    private void parseTaskList(String response) {

        myTool.log(response);

        if (response.charAt(0) == '{') {

            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                if (obj.has("response")) {
                    if ("no work".equals(obj.getString("response"))) {
                        mDatas.clear();
                        updateFragmentByData();
                        return;
                    }
                }

            } catch (JSONException e) {
                myTool.showInfo(e.getMessage());
            }
        } else if (response.charAt(0) == '[') {

            mDatas.clear();

            JSONArray array = null;
            try {
                array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject taskObj = array.getJSONObject(i);
                    Task task = new Task();
                    try {

                        task.setControllerLogId(taskObj.getString("controllerLogId"));
                        task.setTaskTime(taskObj.getString("taskTime"));

                        task.setStartExecutionTime(taskObj.getString("startExecutionTime"));
                        task.setControlArea(taskObj.getString("controlArea"));
                        task.setControlType(taskObj.getString("controlType"));
                        task.setFertilizationCan(taskObj.getString("fertilizationCan"));
                        task.setExecutionTime(taskObj.getString("executionTime"));

                        String taskState = taskObj.getString("taskState");
                        switch (taskState) {
                            case "EXECUTE":
                                task.setStatus(Task.TaskStatus.Running);
                                break;
                            case "WAITTING":
                                task.setStatus(Task.TaskStatus.Waiting);
                                break;
                            case "BLOCKING":
                                task.setStatus(Task.TaskStatus.Blocking);
                                break;
                            default:
                                task.setStatus(Task.TaskStatus.Completed);
                                break;
                        }

                        String controlType = "灌溉";
                        if ("fertilizer".equals(task.getControlType()))
                            controlType = "施肥";

                        task.setName(controlType);

                        task.setFarmId(mSystem.getFarmId());
                        task.setCollectorId(mSystem.getCollectorId());

                    } catch (Exception e) {
                        // TODO: 解析异常处理

                    }
                    mDatas.add(task);
                }

                updateFragmentByData();

                if (!mDatas.isEmpty()) {
                    // 显示最后一个,也即是最新的一个Item
                    taskDisplayer.showStatusByTask(mDatas.get(mDatas.size() - 1));
                    setTitle(mDatas.get(mDatas.size() - 1).getName());
                }

                moveToRunningTaskPager();

            } catch (JSONException e) {
                myTool.showInfo(e.getMessage());
            }


        } else myTool.log("没有解析操作，response：" + response);
    }

    // 移动到正在运行的任务页
    private void moveToRunningTaskPager() {

        if (mTaskPagers.isEmpty()) return;

        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getStatus() == Task.TaskStatus.Running)
                runningLoc = i;
        }

        showRunningTask(); // 显示正在运行的任务
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        myTool.log("onResumeFragments()");
        if (runningLoc != curIndex) {
            timerToRunning.start();
        }
        // 查询任务
        if (taskBinder != null) {
            taskBinder.setOnTaskMsgListener(this);
            taskBinder.queryTasks(mSystem);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myTool.log("onPause()");
        timerToRunning.cancel();
    }
}
