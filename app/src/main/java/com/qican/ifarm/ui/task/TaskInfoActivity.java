package com.qican.ifarm.ui.task;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.service.TaskService;
import com.qican.ifarm.ui_v2.base.BaseActivityWithTitlebar;
import com.qican.ifarm.utils.TaskDisplayerUtil;
import com.qican.ifarm.utils.TaskStatusDisplayerUtil;
import com.qican.ifarm.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TaskInfoActivity extends BaseActivityWithTitlebar implements OnInfoListener<String> {


    TaskDisplayerUtil taskDisplayerUtil;
    Task mTask;
    TextView tvStatus, tvArea, tvCan, tvRemainingTime, tvExecute;
    ImageView ivTaskBg;
    TaskStatusDisplayerUtil taskStatusUtil;
    Button btn;
    TaskService.TaskBinder taskBinder;
    LinearLayout llRemainTime;
    int remainTime;

    @Override
    public String getUITitle() {
        return mTask.getName();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_taskinfo;
    }

    @Override
    public void init() {
        mTask = (Task) myTool.getParam(Task.class);

        initViews();
        initDatas();
        initEvents();
    }

    private void initViews() {
        taskDisplayerUtil = new TaskDisplayerUtil(this);
        taskStatusUtil = new TaskStatusDisplayerUtil(this);

        tvStatus = (TextView) findViewById(R.id.tv_status);
        ivTaskBg = (ImageView) findViewById(R.id.iv_taskbg);

        tvArea = (TextView) findViewById(R.id.tv_area);
        tvCan = (TextView) findViewById(R.id.tv_can);
        tvRemainingTime = (TextView) findViewById(R.id.tv_remaining_time);
        tvExecute = (TextView) findViewById(R.id.tv_execute_time);
        llRemainTime = (LinearLayout) findViewById(R.id.ll_remain_time);

        btn = (Button) findViewById(R.id.btn);
    }

    private void initDatas() {

        myTool.log(mTask.toString());

        tvStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                taskDisplayerUtil.showStatusByTask(mTask);
                taskStatusUtil.showStatus(mTask);

                taskStatusUtil.setTaskTime(mTask.getTaskTime() != null ? TimeUtils.formatDateTime(mTask.getTaskTime()) : "- - : - - : - -")
                        .setStartTime(mTask.getStartExecutionTime() != null ? TimeUtils.formatDateTime(mTask.getStartExecutionTime()) : "- - : - - : - -")
                        .setCompleteTime(mTask.getStopTime() != null ? TimeUtils.formatDateTime(mTask.getStopTime()) : "- - : - - : - -");

            }
        }, 500);

        tvStatus.setText(mTask.getStatus().toString());

        tvArea.setText(mTask.getControlArea() + " 区");

        if ("irrigate".equals(mTask.getControlType())) // 当前为灌溉
            tvCan.setText("无");
        else tvCan.setText(mTask.getFertilizationCan() + " 罐");
        ;
        tvExecute.setText(TimeUtils.formatSecondTime(mTask.getExecutionTime()));

        setStatus();
    }

    private void initEvents() {
        btn.setOnClickListener(this);
    }

    private void setStatus() {

        llRemainTime.setVisibility(View.GONE);

        if (mTask.getStatus() == null) return;
        switch (mTask.getStatus()) {
            case Completed:
                tvStatus.setText("已完成");
                ivTaskBg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.taskinfo_completed));
                btn.setVisibility(View.GONE);
                break;

            case Running:
                tvStatus.setText("运行中");
                ivTaskBg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.taskinfo_running));
                btn.setText("停止任务");
                llRemainTime.setVisibility(View.VISIBLE);
                break;

            case Waiting:
                tvStatus.setText("等待中");
                ivTaskBg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.taskinfo_waiting));
                btn.setText("撤销任务");
                break;

            case Blocking:
                taskStatusUtil.setStartTime("- - : - - : - -");
                ivTaskBg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.taskinfo_waiting));
                tvStatus.setText("下发中");
                btn.setVisibility(View.GONE);
//                btn.setText("撤销任务");
                break;
        }
    }

    private void updateRemainingTime() {
        ControlSystem system = new ControlSystem();
        system.setFarmId(mTask.getFarmId());
        system.setCollectorId(mTask.getCollectorId());

        taskBinder.queryRemainTime(system);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn:
                dealBtnClickByStatus();
                break;
        }
    }

    private void dealBtnClickByStatus() {
        if (mTask.getStatus() == null) return;
        switch (mTask.getStatus()) {
            //停止任务
            case Running:
                if (taskBinder != null) taskBinder.stopTask(mTask);
                break;

            //撤销任务
            case Blocking:
            case Waiting:
                if (taskBinder != null) taskBinder.deleteTask(mTask);
                break;
        }
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

    // 与服务通信
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myTool.log("onServiceConnected");
            taskBinder = (TaskService.TaskBinder) service;
            taskBinder.setOnTaskMsgListener(TaskInfoActivity.this);
            if (mTask.getStatus() == Task.TaskStatus.Running) {
                updateRemainingTime(); //从服务器更新剩余时间
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myTool.log("onServiceDisconnected [ " + name.toString() + " ]");
        }
    };

    @Override
    public void onInfoChanged(final String response) {

        if (response == null || response.length() == 0) return;
        if (response.charAt(0) != '{') return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);

                    if (obj.has("response")) {
                        // 停止、撤销任务的响应
                        showInfoByResponse(obj.getString("response"));

                        // 运行中,可以查询查询剩余时间
                        if (mTask.getStatus() == Task.TaskStatus.Running)
                            parseRemainingTime(obj.getString("response"));
                    }

                } catch (JSONException e) {
                    myTool.showInfo(e.getMessage());
                }
            }
        });
    }

    // 处理服务器返回的信息
    private void showInfoByResponse(String response) {
        if (mTask.getStatus() == null) return;
        switch (mTask.getStatus()) {
            case Running:
                /**
                 * 回复：{"response":"running"}   //下发指令成功
                 {"response":"no task"}  //当前无任务
                 {"response", "error")}  //网络异常
                 */
                switch (response) {
                    case "running":
                        myTool.showDialog(SweetAlertDialog.SUCCESS_TYPE, "成功", "停止任务成功！", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        break;
                    case "no task":
                        myTool.showDialog(SweetAlertDialog.WARNING_TYPE, "提示", "当前无任务！", null);
                        break;
                    case "error":
                        myTool.showDialog(SweetAlertDialog.ERROR_TYPE, "错误", "停止失败，网络传输错误！", null);
                        break;
                }
                break;
            //撤销任务
            case Waiting:
                switch (response) {
                    case "success":
                        myTool.showDialog(SweetAlertDialog.SUCCESS_TYPE, "成功", "撤销任务成功！", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        break;
                    case "error":
                        myTool.showDialog(SweetAlertDialog.WARNING_TYPE, "失败", "撤销失败，请稍后再试！", null);
                        break;
                }
                break;
        }
    }

    private void parseRemainingTime(String response) {
        myTool.log("parseRemainingTime : " + response);

        if ("run work".equals(response)) {
            // TODO: 任务已完成的一些处理
            myTool.showDialog(SweetAlertDialog.WARNING_TYPE, "完成", "当前任务已完成~", null);
        }

        if (response.length() == 0 || response.charAt(0) != '{') {
            myTool.log("剩余时间的JSON解析出错");
            return;
        }

        JSONObject obj = null;
        try {
            obj = new JSONObject(response);
            if (!obj.has("remainExecutionTime")) return;
            remainTime = obj.getInt("remainExecutionTime");
            showRemainTime();

        } catch (JSONException e) {
            myTool.showInfo(e.getMessage());
        }
    }

    // 处理UI
    private void showRemainTime() {

        // 大于10秒用一个大的计数器
        if (remainTime > 10) {

            if (smallTimer != null) smallTimer.cancel();

            bigTimer = new CountDownTimer(remainTime * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    int remainSecond = (int) (millisUntilFinished / 1000);

                    // 小于10秒, 重新请求对时(确保对时精度)
                    if (remainSecond < 10) {
                        updateRemainingTime();
                        return;
                    }

                    int remainMin = remainSecond / 60;
                    remainSecond = remainSecond % 60;

                    String remainText = "";
                    if (remainMin <= 0) remainText = remainSecond + " 秒";
                    else remainText = remainMin + " 分 " + remainSecond + " 秒";

                    tvRemainingTime.setText(remainText);
                }

                @Override
                public void onFinish() {

                }
            };

            bigTimer.start();

        } else {

            if (bigTimer != null) bigTimer.cancel();

            smallTimer = new CountDownTimer(remainTime * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    int remainSecond = (int) (millisUntilFinished / 1000);
                    int remainMin = remainSecond / 60;
                    remainSecond = remainSecond % 60;

                    String remainText = "";
                    if (remainMin <= 0) remainText = remainSecond + " 秒";
                    else remainText = remainMin + " 分 " + remainSecond + " 秒";

                    tvRemainingTime.setText(remainText);
                }

                @Override
                public void onFinish() {
                    mTask.setStatus(Task.TaskStatus.Completed);
                    initDatas();
//                    tvRemainingTime.setText("已完成");
                }
            };
            smallTimer.start();
        }
    }

    CountDownTimer bigTimer;
    CountDownTimer smallTimer;

}
