package com.qican.ifarm.ui.control;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.OperationArea;
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
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NewTaskActivity extends BaseActivityWithTitlebar implements OnInfoListener<Task>, PopwindowListener<PopupWindow, Task> {
    private RelativeLayout rlStartTime, rlLastTime, rlArea, rlType;
    private TextView tvStartTime, tvLastTime, tvArea, tvType;
    private Date starTimeDate, lastTimeDate;
    private List<OperationArea> mData;
    DialogForSelectType dialogForSelectType;
    DialogForSelectArea dialogForSelectArea;
    private Task mTask;

    private ImageView ivWatering,
            ivTank1, ivTank2, ivTank3,
            ivA, ivB, ivC, ivD, ivE;
    SweetAlertDialog mDialog;
    boolean isResponse = false;

    @Override
    public String getUITitle() {
        return "添加任务";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_newtask;
    }

    @Override
    public void init() {
        rlStartTime = (RelativeLayout) findViewById(R.id.rlStartTime);
        rlLastTime = (RelativeLayout) findViewById(R.id.rlLastTime);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvLastTime = (TextView) findViewById(R.id.tvLastTime);
        rlArea = (RelativeLayout) findViewById(R.id.rlArea);
        tvArea = (TextView) findViewById(R.id.tvArea);
        rlType = (RelativeLayout) findViewById(R.id.rlOperation);
        tvType = (TextView) findViewById(R.id.tvOperation);

        initControlSysUI();

        //实例化SelectPicPopupWindow
        dialogForSelectType = new DialogForSelectType(this, mTask);
        dialogForSelectArea = new DialogForSelectArea(this, mTask);

        setRightMenu("立即添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        initData();

        initEvent();
    }

    private void addTask() {
        // 添加新任务
        if (taskBinder == null) return;

        if (mTask.getControlArea() == null || mTask.getControlArea() == "") {
            myTool.showInfo("请设置实施区域~");
            return;
        }
        if (mTask.getExecutionTime() == null) {
            myTool.showInfo("请设置持续时间~");
            return;
        }
        if (mTask.getStartExecutionTime() == null) {
            myTool.showInfo("请设置开始时间~");
            return;
        }
        if (mTask.getFertilizationCan() == null) {
            mTask.setControlType("irrigate");
            mTask.setFertilizationCan("");
        }
        /**
         * 5、添加作业的数据格式(所有字段不可或缺)：
         * 输入：{
         * "farmId": 10000001,
         * "collectorId": 17130000,//设备编号
         * "level": 2,
         * "commandCategory": "add",
         * "command": "execute",
         * "controlArea": "A,B",//控制区域
         * "controlType": "fertilizer",
         * "fertilizationCan": "2",  //施肥罐，用‘，’分开
         * "waitTime": 0,
         * "executionTime": 120  //执行时间
         * "startExecutionTime": "2017-05-15 10:45:04"  //开始执行时间
         * }
         * 回复：{"response":"running"}   //已经添加到任务
         * {"response":"exist task"}  //已经添加到任务，但是要注意在这任务之前还有其他任务未执行
         * {"response", "error")}  //网络异常
         */
        mTask.setFarmId("10000001");
        mTask.setCollectorId("17130000");
        mTask.setLevel("2");

        mTask.setCommandCategory("add");
        mTask.setCommand("execute");

        mTask.setWaitTime("0");
        taskBinder.addTask(mTask);

        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在添加...");
        mDialog.show();

        // 15秒超时处理
        rlStartTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isResponse)
                    mDialog.setTitleText("超时").setContentText("请求超时，请稍后再试！").changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }, 20000);
    }

    private void initControlSysUI() {
        ivWatering = (ImageView) findViewById(R.id.iv_watering);

        ivTank1 = (ImageView) findViewById(R.id.iv_tank1);
        ivTank2 = (ImageView) findViewById(R.id.iv_tank2);
        ivTank3 = (ImageView) findViewById(R.id.iv_tank3);

        ivA = (ImageView) findViewById(R.id.iv_a);
        ivB = (ImageView) findViewById(R.id.iv_b);
        ivC = (ImageView) findViewById(R.id.iv_c);
        ivD = (ImageView) findViewById(R.id.iv_d);
        ivE = (ImageView) findViewById(R.id.iv_e);

        myTool.setHeightByWindow(findViewById(R.id.rl_task), 2.0 / 3.2);
    }

    private void initData() {
        mData = IFarmFakeData.getOperationAreas();
        mTask = new Task();
        showStatusAllStop();
        // 显示状态
        tvType.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvType.setText("灌溉");
                showStatusByTask(mTask);
            }
        }, 800);
    }

    void initEvent() {
        rlStartTime.setOnClickListener(this);
        rlLastTime.setOnClickListener(this);
        rlArea.setOnClickListener(this);
        rlType.setOnClickListener(this);
        dialogForSelectType.setOnInfoChangeListener(this);
        dialogForSelectArea.setOnPowwinListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.rlStartTime:
                TimePickerUtil.pickTimeDialog(this, "时间选择", starTimeDate, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        starTimeDate = date;
                        tvStartTime.setText(TimeUtils.formatDateTime(getTime(date)));
                        mTask.setStartExecutionTime(getTime(date));
                    }
                });
                break;

            case R.id.rlLastTime:
                TimePickerUtil.pickLastTime(this, "持续时间", lastTimeDate, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        lastTimeDate = date;
                        if (date.getHours() != 0)
                            tvLastTime.setText(date.getHours() + "小时 " + date.getMinutes() + "分钟");
                        else
                            tvLastTime.setText(date.getMinutes() + "分钟");

                        mTask.setExecutionTime((date.getHours() * 60 + date.getMinutes()) * 60 + "");
                    }
                });
                break;

            case R.id.rlArea:
                //显示窗口
                dialogForSelectArea.showAtLocation(this.findViewById(R.id.ll_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                break;

            case R.id.rlOperation:
                //显示窗口
                dialogForSelectType.showAtLocation(this.findViewById(R.id.ll_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                break;
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onInfoChanged(Task obj) {

        String can = "";

        mTask.setTank1(obj.isTank1());
        mTask.setTank2(obj.isTank2());
        mTask.setTank3(obj.isTank3());

        String type = "";
        if (mTask.isTank1()) {
            type = type + "1 ";
            can = addCan(can, "1");
        }
        if (mTask.isTank2()) {
            type = type + "2 ";
            can = addCan(can, "2");
        }
        if (mTask.isTank3()) {
            type = type + "3 ";
            can = addCan(can, "3");
        }
        mTask.setFertilizationCan(can);

        if (type.equals("")) {
            type = "灌溉";
            mTask.setControlType("irrigate");
        } else {
            type = "施肥[ 罐 " + type + "]";
            mTask.setControlType("fertilizer");
        }
        tvType.setText(type);
        showStatusByTask(mTask);
    }

    private String addCan(String can, String newCan) {
        if ("".equals(can)) {
            return newCan;
        } else {
            return can + "," + newCan;
        }
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

        if (msg == null) return;
        if (msg.length() == 0 || msg.charAt(0) != '{') return;

        JSONObject obj = null;
        try {
            obj = new JSONObject(msg);
            // 回复消息解析
            if (!obj.has("response")) {
                myTool.log("不可解析的信息，msg：" + msg);
                return;
            }

            isResponse = true;// 有相应结果，不处理为延时操作

            String response = obj.getString("response");
            switch (response) {
                case "error":
                    mDialog.setTitleText("错误").setContentText("网络传输异常，请稍后重试！")
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
                case "running":
                    mDialog.setTitleText("成功").setContentText("添加成功，服务器正与下级控制器通信！")
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
                case "exist task":
                    mDialog.setTitleText("成功").setContentText("添加成功，目前缓存中存在其他任务！")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    break;
                case "time conflict":
                    mDialog.setTitleText("失败").setContentText("任务时间与缓存任务冲突（最近相隔2min）！")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    break;
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

    @Override
    public void infoChanged(PopupWindow w, Task obj) {
//       if (w.getClass() == DialogForSelectArea.class) {
//       }
        mTask.setA(obj.isA());
        mTask.setB(obj.isB());
        mTask.setC(obj.isC());
        mTask.setD(obj.isD());
        mTask.setE(obj.isE());

        showStatusByTask(mTask);

        // 计算控制区域和TV显示字串
        String area = "";
        if (mTask.isA()) area = area + "A";
        if (mTask.isB())
            if ("".equals(area)) area = area + "B";
            else area = area + ",B";
        if (mTask.isC())
            if ("".equals(area)) area = area + "C";
            else area = area + ",C";
        if (mTask.isD())
            if ("".equals(area)) area = area + "D";
            else area = area + ",D";
        if (mTask.isE())
            if ("".equals(area)) area = area + "E";
            else area = area + ",E";

        mTask.setControlArea(area);

        tvArea.setText(area + " 区");
    }
}
