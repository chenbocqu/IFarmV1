package com.qican.ifarm.ui_v2.task;


import android.os.CountDownTimer;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.ui_v2.base.BaseActivityWithTitlebar;
import com.qican.ifarm.utils.TaskTimeDisplayer;
import com.qican.ifarm.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class TaskInfoV2Activity extends BaseActivityWithTitlebar implements SwipeRefreshLayout.OnRefreshListener {

    Task mTask;
    TaskTimeDisplayer taskDisplayerUtil;
    SwipeRefreshLayout srl;
    boolean isRequested = false;


    @Override
    public String getUITitle() {
        return "任务详情";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_task_info;
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestRemainTime();
    }

    private void requestRemainTime() {

        if (isRequested) return;

        // 查询任务状态（如果运行中，则会返回剩余时间）
        srl.setRefreshing(true);
        OkHttpUtils.post().url(myTool.getServAdd() + "farmControl/farmControlTaskStrategy")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("controlType", mTask.getControlType())
                .addParams("command", "execute")
                .addParams("commandCategory", "query")
                .addParams("controllerLogId", mTask.getControllerLogId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        myTool.log(e);

                        myTool.showInfo("网络异常，稍后再试！");
                        srl.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        myTool.log("response : " + response);

                        srl.setRefreshing(false);
                        if (response == null || "[]".equals(response)) return;
                        if (response.length() == 0 || response.charAt(0) != '{') return;

                        JSONObject obj = null;

                        try {

                            obj = new JSONObject(response);

                            if (!obj.has("response")) return;
                            String info = obj.getString("response");

                            if ("noTask".equals(info)) {

                                setTextById(R.id.tv_msg, "已失效");

                            } else if ("error".equals(info)) {

                                setTextById(R.id.tv_msg, "请求异常");

                                myTool.showInfo("请求异常，请稍后再试！");

                            } else {

                                JSONObject taskObj = obj.getJSONObject("response");

                                if (taskObj.has("taskState"))
                                    mTask.setTaskState(taskObj.getString("taskState"));

                                if (taskObj.has("addResultTime"))
                                    mTask.setAddResultTime(taskObj.getString("addResultTime"));

                                if (taskObj.has("stopTime"))
                                    mTask.setStopTime(taskObj.getString("stopTime"));

                                if (obj.has("systemType"))
                                    mTask.setSystemType(obj.getString("systemType"));

                                initTaskInfo();

                                myTool.showInfo("数据已更新！");

                                if (!taskObj.has("remainExecutionTime")) return;
                                int remainTime = taskObj.getInt("remainExecutionTime");
                                showRemainTime(remainTime);
                            }

                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }

    private void toExitWithInfo(String info) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(info)
                .setConfirmText("退出")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    }
                }).show();
    }

    private void showRemainTime(int remainTime) {
        new CountDownTimer(remainTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setText(R.id.tv_remaining_time, TimeUtils.getTimeFromNum((int) (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                setText(R.id.tv_remaining_time, "执行完毕");
                requestRemainTime();
                isRequested = true;
            }
        }.start();
    }

    @Override
    public void init() {

        mTask = new Task();
        mTask = (Task) myTool.getParam(Task.class);

        if (mTask == null) return;

        initViews();

        taskDisplayerUtil = new TaskTimeDisplayer(this);

        taskDisplayerUtil.showStatus(mTask);
        taskDisplayerUtil.setTaskTime(TimeUtils.formatTime(mTask.getTaskTime()));
        if (mTask.getStartExecutionTime() != null)
            taskDisplayerUtil.setStartTime(TimeUtils.formatTime(mTask.getStartExecutionTime()));
        if (mTask.getStopTime() != null)
            taskDisplayerUtil.setCompleteTime(TimeUtils.formatTime(mTask.getStopTime()));
        initTaskInfo();
        initEvent();
    }

    private void initTaskInfo() {
        setText(R.id.tv_farm_name, mTask.getFarmName());
        setText(R.id.tv_district, mTask.getSystemDistrict());
        setText(R.id.tv_system, mTask.getSystemNo());
        setText(R.id.tv_sys_type, mTask.getSystemType());
        setText(R.id.tv_control_type, mTask.getName());
        setText(R.id.tv_last_time, TimeUtils.getTimeFromNum(Integer.parseInt(mTask.getExecutionTime())));
        setText(R.id.tv_task_time, TimeUtils.formatTime(mTask.getTaskTime()));
        setText(R.id.tv_start_time, TimeUtils.formatTime(mTask.getStartExecutionTime()));
        setText(R.id.tv_stop_time, TimeUtils.formatTime(mTask.getStopTime()));
        setStatus();
    }

    private void setStatus() {

        String status = null;

        if (mTask.getTaskState() == null) return;

        switch (mTask.getTaskState()) {
            case "WAITTING":
                status = "等待中";
                break;
            case "BLOCKING":
                status = "下发中";
                break;
            case "STOPPING":
                status = "停止中";
                break;
            case "STOPPED":
                status = "已停止";
                break;
            case "EXECUTEING":
                status = "运行中";
                break;
            case "CONFICTING":
                status = "冲突";
                break;
        }
        setText(R.id.tv_status, status);
    }

    private void initViews() {
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
    }

    TaskInfoV2Activity setText(@IdRes int id, String info) {
        TextView tv = (TextView) findViewById(id);
        if (info != null) tv.setText(info);
        return this;
    }

    private boolean isEmpty(TextView tv) {
        return TextUtils.isEmpty(tv.getText());
    }

    void initEvent() {
        srl.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_operation:
                break;
            case R.id.rl_start_time:
                break;
            case R.id.rl_last_time:
                break;
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onRefresh() {
        requestRemainTime();
    }
}
