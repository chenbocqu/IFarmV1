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
import com.qican.ifarm.bean.ControlTask;
import com.qican.ifarm.bean.Operations;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.listener.PopwindowListener;
import com.qican.ifarm.service.TaskService;
import com.qican.ifarm.ui_v2.base.BaseActivityWithTitlebar;
import com.qican.ifarm.ui_v2.control.PopWin4SelectOperation;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.utils.TimePickerUtil;
import com.qican.ifarm.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class AddTaskActivity extends BaseActivityWithTitlebar implements PopwindowListener<Object, Object> {

    public static final String KEY_NEW_TASK = "KEY_NEW_TASK";
    String title, error = "Error";
    ControlSys mSys;

    PopWin4SelectOperation popWin4SelectOperation;
    private Date starTimeDate, lastTimeDate;
    TextView tvStartTime, tvLastTime, tvOperation;
    Operations mOpera;
    SweetAlertDialog mDialog;
    ControlTask newTask;
    String loaction = "";
    String startTime = "", commandCategory = "ImmediateExecution"; //初始化为立即执行选项

    List<Operations> operationses;
    Task mTask;
    int lastTime = 0;
    private boolean isResponse = false;

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

        myTool.log(mSys.toString());

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

        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initOperations();
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

        mTask.setName(mOpera.getName());
        mTask.setFarmId(mSys.getFarmId());
        mTask.setFarmName(mSys.getFarmName());
        mTask.setSystemDistrict(mSys.getSystemDistrict());
        mTask.setSystemNo(mSys.getSystemNo());
        mTask.setSystemId(mSys.getSystemId());
        mTask.setControlType(mSys.getSystemTypeCode());
        mTask.setControlOperation(mOpera.getControlOperation());

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
                    mDialog.setTitleText("超时").setContentText("请求超时，请稍后再试！").changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }, 25000);
    }

    private boolean isEmpty(TextView tv) {
        return TextUtils.isEmpty(tv.getText());
    }

    private void initOperations() {

        // 自编选项
        operationses = IFarmFakeData.getOperationsByCode(mSys.getSystemTypeCode());

        popWin4SelectOperation = new PopWin4SelectOperation(this, operationses);

        myTool.log("请求功能 url :" + myTool.getServAdd() + "farmControl/farmControlOperationList\n"
                + "userId:" + myTool.getUserId() + "\n" +
                "signature:" + myTool.getToken() + "\n" +
                "systemId:" + mSys.getSystemId());

        // 从服务器请求选项
        OkHttpUtils.post().url(myTool.getServAdd() + "farmControl/farmControlOperationList")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("systemId", mSys.getSystemId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.showInfo("网络错误，稍后重试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        myTool.log("请求功能res : " + response);

                        if (response == null || "{}".equals(response)) return;

                        JSONArray array = null;
                        try {
                            array = new JSONArray(response);
                            if (array.length() == 0) return;
                            operationses.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Operations operations = new Operations();

                                if (obj.has("functionName"))
                                    operations.setName(obj.getString("functionName"));

                                if (obj.has("functionCode"))
                                    operations.setControlOperation(obj.getString("functionCode"));

                                operationses.add(operations);
                            }
                            popWin4SelectOperation.setmDatas(operationses);

                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
        popWin4SelectOperation.setOnPowwinListener(this);
    }

    void initEvent() {
        findViewById(R.id.rl_operation).setOnClickListener(this);
        findViewById(R.id.rl_start_time).setOnClickListener(this);
        findViewById(R.id.rl_last_time).setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_add_task;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_operation:
                myTool.showPopFormBottom(popWin4SelectOperation, findViewById(R.id.ll_main));
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
}
