package com.qican.ifarm.ui_v2.task;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.ControlSys;
import com.qican.ifarm.bean.Operations;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.listener.PopwindowListener;
import com.qican.ifarm.service.TaskService;
import com.qican.ifarm.ui_v2.base.BaseActivityWithTitlebar;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.utils.TimePickerUtil;
import com.qican.ifarm.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddTaskForShuiFeiActivity extends BaseActivityWithTitlebar implements PopwindowListener<Object, Object>, OnInfoListener<Task>, DialogForSelectNum.NumListener {

    public static final String KEY_NEW_TASK = "KEY_NEW_TASK";
    String title, error = "Error";
    ControlSys mSys;

    private Date starTimeDate, lastTimeDate;
    TextView tvStartTime, tvLastTime, tvOperation;
    Operations mOpera;
    SweetAlertDialog mDialog;
    String startTime = "", commandCategory = "ImmediateExecution", can, area; //初始化为立即执行选项

    Task mTask;
    int lastTime = 0;
    private boolean isResponse = false;

    DialogForSelectNum dialogForSelectArea;
    DialogForSelectNum dialogForSelectCan;

    @Override
    public String getUITitle() {
        return mSys.getSystemType();
    }

    @Override
    public void init() {
        mTask = new Task();
        mSys = (ControlSys) myTool.getParam(ControlSys.class);
        if (mSys == null) {
            mSys = new ControlSys();
            mSys.setSystemType("error");
        }

        tvStartTime = (TextView) findViewById(R.id.tv_starttime);
        tvLastTime = (TextView) findViewById(R.id.tv_lasttime);
        tvOperation = (TextView) findViewById(R.id.tv_operate);

        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        setRightMenu("立即添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });

        String operation = null;
        if (mSys.getControlOperation() != null)
            switch (mSys.getControlOperation()) {
                case "irrigate":
                    operation = "灌溉";
                    findViewById(R.id.rl_can).setVisibility(View.GONE);
                    break;
                case "fertilizer":
                    operation = "施肥";
                    break;
                case "medicine":
                    operation = "施药";
                    break;
            }

        setTextById(R.id.tv_operate, operation);

        //实例化SelectPicPopupWindow
        dialogForSelectArea = new DialogForSelectNum(this, "请选择区域", 0);
        dialogForSelectArea.setOnPowwinListener(this);

        dialogForSelectCan = new DialogForSelectNum(this, "请选择罐", 1);
        dialogForSelectCan.setOnPowwinListener(this);

        initEvent();
    }

    private TaskService.TaskBinder taskBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            taskBinder = (TaskService.TaskBinder) service;
            taskBinder.setOnTaskMsgListener(new OnInfoListener<String>() {
                @Override
                public void onInfoChanged(final String msg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dealByResponse(msg);
                        }
                    });
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    // 处理回复信息
    private void dealByResponse(String msg) {
        myTool.log("Add Task Activity Received Msg : " + msg);
        if (msg == null || msg.length() == 0 || msg.charAt(0) != '{') return;


        JSONObject obj = null;
        try {
            obj = new JSONObject(msg);
            // 回复消息解析
            if (!obj.has("response")) {
                myTool.log("不可解析的信息，msg：" + msg);
                return;
            }

            String response = obj.getString("response");
            switch (response) {
                case "error":
                    mDialog.setTitleText("错误").setContentText("网络传输异常，请稍后重试！")
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
                case "running":
                    mDialog.setTitleText("成功").setContentText("恭喜，任务添加成功！")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
                                }
                            })
                            .setCancelClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    break;
                case "schemerConflict":
                    mDialog.setTitleText("添加失败").setContentText("任务时间与计划任务冲突！")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
                case "currentRunning":
                    mDialog.setTitleText("添加失败").setContentText("与正在执行的任务相冲突！")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
                default:
                    mDialog.setTitleText("未知错误").setContentText("发生了不可预料的错误，请稍后重试！")
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }

        } catch (JSONException e) {
            myTool.showInfo(e.getMessage());
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
        // 销毁的时候解绑服务
        unbindService(connection);
    }

    private void addNewTask() {
        if (mSys.getControlOperation() == null) return;

        if (isEmpty(tvOperation)) {
            myTool.showInfo("请选择操作类型！");
            return;
        }

        if (isEmpty(tvLastTime)) {
            myTool.showInfo("请选择持续时间！");
            return;
        }

        // 添加新任务
        if (taskBinder == null) {
            myTool.showInfo("网络异常，请稍后再试！");
            return;
        }

        switch (mSys.getControlOperation()) {
            case "irrigate":
                mTask.setControlOperation("irrigate");
                mTask.setName("灌溉(区域" + area + ")");
                break;
            case "fertilizer":
                mTask.setControlOperation("fertilizer");
                mTask.setCanNo(can);
                mTask.setName("施肥(区域" + area + ")");
                break;
            case "medicine":
                mTask.setControlOperation("medicine");
                mTask.setCanNo(can);
                mTask.setName("施药(区域" + area + ")");
                break;
        }

        mTask.setFarmId(mSys.getFarmId());
        mTask.setFarmName(mSys.getFarmName());
        mTask.setSystemDistrict(mSys.getSystemDistrict());
        mTask.setSystemNo(mSys.getSystemNo());
        mTask.setSystemId(mSys.getSystemId());
        mTask.setControlType(mSys.getControlType());

        mTask.setControlArea(area);

        // 三种这里要操作一下
        mTask.setCommandCategory(commandCategory);
        mTask.setCommand("execute");
        mTask.setExecutionTime(lastTime + "");
        if (!"ImmediateExecution".equals(commandCategory))
            mTask.setStartExecutionTime(startTime);

        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在添加...");
        mDialog.show();
        taskBinder.addTaskV2(mTask);

        // 25秒超时处理
        tvLastTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isResponse)
                    mDialog.setTitleText("未响应").setContentText("服务器正忙，请稍后再试！").changeAlertType(SweetAlertDialog.WARNING_TYPE);
            }
        }, 25000);
    }

    private boolean isEmpty(TextView tv) {
        return TextUtils.isEmpty(tv.getText());
    }


    void initEvent() {
        findViewById(R.id.rl_operation).setOnClickListener(this);
        findViewById(R.id.rl_start_time).setOnClickListener(this);
        findViewById(R.id.rl_last_time).setOnClickListener(this);
        findViewById(R.id.rl_can).setOnClickListener(this);
        findViewById(R.id.rl_area).setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_add_shuifei_task;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_can:
                myTool.showPopFormBottom(dialogForSelectCan, findViewById(R.id.ll_main));
                break;
            case R.id.rl_area:
                myTool.showPopFormBottom(dialogForSelectArea, findViewById(R.id.ll_main));
                break;
            case R.id.rl_start_time:
                TimePickerUtil.pickTimeDialog(this, "开始时间", starTimeDate, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        startTime = getTime(date);
                        starTimeDate = date;
                        tvStartTime.setText(TimeUtils.formatDateTime(getTime(date)));
                        commandCategory = "fixedTimeExecution";
                    }
                });
                break;
            case R.id.rl_last_time:
                TimePickerUtil.pickLastTime(this, "持续时间", lastTimeDate, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        lastTimeDate = date;
//                        if (date.getHours() != 0)
//                            tvLastTime.setText(date.getHours() + "小时 " + date.getMinutes() + "分钟");
//                        else
//                            tvLastTime.setText(date.getMinutes() + "分钟");

                        lastTime = date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds();
                        tvLastTime.setText(TimeUtils.getTimeFromNum(lastTime));
                    }
                });
                break;
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    // 处理选择框消息
    @Override
    public void infoChanged(Object w, Object obj) {
        if ("PopWin4SelectOperation".equals(w.getClass().getSimpleName())) {
            mOpera = (Operations) obj;
            if (mOpera == null || IFarmFakeData.NONE_OPERATION.equals(mOpera.getControlOperation())) {
                myTool.showInfo("相关操作暂未开放！");
                return;
            }
            tvOperation.setText(mOpera.getName());
        }
    }

    @Override
    public void onInfoChanged(Task obj) {

    }

    @Override
    public void numChanged(int winId, String info) {
        switch (winId) {
            case 0:
                // 区域
                setTextById(R.id.tv_area, info);
                area = info;
                break;
            case 1:
                // 罐
                setTextById(R.id.tv_can, info);
                can = info;
                break;
        }
    }
}
