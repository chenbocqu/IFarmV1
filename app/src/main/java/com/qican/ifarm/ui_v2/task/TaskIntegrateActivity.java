package com.qican.ifarm.ui_v2.task;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.bean.TaskAddInfo;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.service.TaskService;
import com.qican.ifarm.ui_v2.base.ContentWithBaseListActivity;
import com.qican.ifarm.ui_v2.controlsys.ControlSysList4IntegrateActivity;
import com.qican.ifarm.ui_v2.controlsys.ShuiFeiYaoSysList4IntegrateActivity;
import com.qican.ifarm.ui_v2.userinfo.AboutActivity;
import com.qican.ifarm.utils.TimeUtils;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.qican.ifarm.ui_v2.task.HistoryTaskListActivity.RESULT_TASK_LIST;
import static com.qican.ifarm.utils.TimeUtils.getTimeFromNum;

public class TaskIntegrateActivity extends ContentWithBaseListActivity<Task> {

    List<Task> mData;
    SweetAlertDialog mDialog;
    int remainTime = 0;
    List<View> vControls;
    public final static int REQUEST_FOR_TASK = 1;
    public final static int REQUEST_FOR_MODIFY = 2;
    public final static int REQUEST_FOR_HISTORY_TASK = 3;

    String mFun;
    private boolean isShuiFeiYao;
    private boolean isResponse = false;
    int curIndex = 0;

    @Override
    public String getUITitle() {
        return "组合任务";
    }

    @Override
    public void init() {
        initHeader();
        mData = new ArrayList<>();
        setRightMenu("发布任务", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishTasks();
            }
        });

        findViewById(R.id.tv_peizhi).setOnClickListener(this);
        findViewById(R.id.tv_history).setOnClickListener(this);
    }

    private void initHeader() {
        float ratio = 1 / 6f;
        myTool.setHeightByWindow(findViewById(R.id.ll_control_group1), ratio);
        myTool.setHeightByWindow(findViewById(R.id.ll_control_group2), ratio);
        myTool.setHeightByWindow(findViewById(R.id.ll_control_group3), ratio);
        vControls = new ArrayList<>();

        add(R.id.ll_guangai);
        add(R.id.ll_shifei);
        add(R.id.ll_shiyao);
        add(R.id.ll_wendu);

        add(R.id.ll_shidu);
        add(R.id.ll_tongfeng);
        add(R.id.ll_guangzhao);
        add(R.id.ll_zheyang);

        add(R.id.ll_juanlian);
        add(R.id.ll_shuilian);
        add(R.id.ll_co2);
        add(R.id.ll_o2);

        setListener();
        resetView();
    }

    private void resetView() {
        for (View v : vControls) v.setBackgroundResource(R.drawable.item_unselected);
    }

    private void setListener() {
        for (View v : vControls) v.setOnClickListener(this);
    }

    private void add(int id) {
        View v = findViewById(id);
        if (v != null) vControls.add(v);
    }

    private String getStartTime(String time) {
        return time == null ? "立即执行" : TimeUtils.formatTime(time);
    }

    @Override
    public ComAdapter<Task> getAdapter() {
        return new ComAdapter<Task>(this, mData, R.layout.item_common_task) {
            @Override
            public void convert(final ViewHolder helper, final Task item) {

                ImageView ivDelete = helper.getView(R.id.iv_delete);// 删除
                ImageView ivStop = helper.getView(R.id.iv_stop); // 停止任务

                final TextView tvRemainTime = helper.getView(R.id.tv_remaining_time);

                tvRemainTime.setVisibility(View.GONE);
                ivDelete.setVisibility(View.VISIBLE);
                ivStop.setVisibility(View.GONE);

                String executionTime = getTimeFromNum(Integer.parseInt(item.getExecutionTime()));
                helper
                        .setText(R.id.tv_start_time, getStartTime(item.getStartExecutionTime()))
                        .setText(R.id.tv_last_time, executionTime)
                        .setText(R.id.tv_location, item.getFarmName() + item.getSystemDistrict())
                        .setText(R.id.tv_name, item.getName())

                        .setText(R.id.tv_time,
                                item.getTaskTime() == null ?
                                        getStartTime(item.getStartExecutionTime()) :
                                        TimeUtils.formatTime(item.getTaskTime()))

                        .setText(R.id.tv_desc, "在" + item.getFarmName() + item.getSystemDistrict() + "，" +
                                (item.getStartExecutionTime() == null ? "立刻开始" :
                                        ("从" + TimeUtils.formatTime(item.getStartExecutionTime()) + "开始"))
                                + "，持续执行" + executionTime + "。"
                        );

                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delTask(item); //撤销任务
                    }
                });

                helper.getView(R.id.iv_task_info).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curIndex = helper.getPosition();

                        if ("wfm".equals(item.getControlType()))
                            myTool.startActivityForResult(item, TaskModifyForShuiFeiActivity.class, REQUEST_FOR_MODIFY);
                        else
                            myTool.startActivityForResult(item, TaskModifyActivity.class, REQUEST_FOR_MODIFY);

                    }
                });

                helper.setVisble(R.id.tv_status, false);
                String status = "待下发";
                if (item.getTaskState() != null) {
                    switch (item.getTaskState()) {
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
                            tvRemainTime.setVisibility(View.VISIBLE);
                            ivStop.setVisibility(View.VISIBLE);
                            if (item.getRemainExecutionTime() != null) {
                                remainTime = Integer.parseInt(item.getRemainExecutionTime());
                                new CountDownTimer(remainTime * 1000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        tvRemainTime.setText(getTimeFromNum((int) (millisUntilFinished / 1000)));
                                    }

                                    @Override
                                    public void onFinish() {
                                    }
                                }.start();
                            }
                            break;
                        case "CONFICTING":
                            status = "冲突";
                            break;
                    }
                }
                helper.setText(R.id.tv_status, status);
            }
        };
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_task_integrate;
    }

    private void delTask(final Task task) {

        mDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("撤销任务" + task.getName() + "?");
        mDialog.setCancelClickListener(null)
                .setCancelText("取消")
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        mData.remove(task);
                        notifyDatasetChanged();
                    }
                })
                .show();

    }

    @Override
    public void onRefresh(PullToRefreshLayout l) {
        l.refreshFinish(true);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.loadMoreFinish(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_peizhi:
                if (mFun == null) {
                    myTool.showInfo("请先选择一个功能！");
                    return;
                }

                toAddTask(mFun);
                break;
            case R.id.ll_guangai:
                toShuiFeiShao(v.getId(), "灌溉");
                break;
            case R.id.ll_shifei:
                toShuiFeiShao(v.getId(), "施肥");
                break;
            case R.id.ll_shiyao:
                toShuiFeiShao(v.getId(), "施药");
                break;

            case R.id.ll_wendu:
                toComSys(v.getId(), "温度");
                break;
            case R.id.ll_shidu:
                toComSys(v.getId(), "湿度");
                break;
            case R.id.ll_tongfeng:
                toComSys(v.getId(), "通风");
                break;
            case R.id.ll_guangzhao:
                toComSys(v.getId(), "补光");
                break;
            case R.id.ll_zheyang:
                toComSys(v.getId(), "遮阳");
                break;

            case R.id.ll_juanlian:
                toComSys(v.getId(), "卷帘");
                break;
            case R.id.ll_shuilian:
                toComSys(v.getId(), "水帘");
                break;
            case R.id.ll_co2:
                toComSys(v.getId(), "二氧化碳");
                break;
            case R.id.ll_o2:
                toComSys(v.getId(), "氧气");
                break;

            case R.id.tv_history:
                myTool.startActivityForResult(HistoryTaskinfoActivity.class, REQUEST_FOR_HISTORY_TASK);
                break;
        }
    }

    private void publishTasks() {
        if (mData.isEmpty()) {
            myTool.showInfo("请请先添加预设任务！");
            return;
        }
        // 添加新任务
        if (taskBinder == null) {
            myTool.showInfo("网络异常，请稍后再试！");
            return;
        }


        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在添加...");
        mDialog.show();
        isResponse = false;
        taskBinder.addIntegrateTask(mData);

        // 25秒超时处理
        findViewById(R.id.tv_peizhi).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isResponse)
                    mDialog.setTitleText("超时").setContentText("请求超时，请稍后再试！").changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }, 25000);
    }

    void toShuiFeiShao(int id, String fun) {
        isShuiFeiYao = true;

        chooseItem(id);
        mFun = fun;
    }

    private void toComSys(int id, String fun) {

        isShuiFeiYao = false;

        chooseItem(id);
        mFun = fun;
    }

    private void chooseItem(int id) {
        resetView();

        View v = findViewById(id);
        v.setBackgroundResource(R.drawable.item_selected);
    }

    private void toAddTask(String fun) {
        if (isShuiFeiYao)
            myTool.startActivityForResult(fun, ShuiFeiYaoSysList4IntegrateActivity.class, REQUEST_FOR_TASK);
        else
            myTool.startActivityForResult(fun, ControlSysList4IntegrateActivity.class, REQUEST_FOR_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        myTool.log("onActivityResult, requestCode = " + requestCode + ", resultCode = " + resultCode);

        switch (requestCode) {
            case REQUEST_FOR_TASK:
                if (resultCode == RESULT_OK) {

                    Task task = (Task) data.getSerializableExtra(AddTask4IntegrateActivity.KEY_NEW_TASK);
                    if (task == null) return;

                    mData.add(task);
                    notifyDatasetChanged();
                }

                break;
            case REQUEST_FOR_MODIFY:
                if (resultCode == RESULT_OK) {

                    Task task = (Task) data.getSerializableExtra(AddTask4IntegrateActivity.KEY_NEW_TASK);
                    if (task == null) return;
                    myTool.log(task.toString());
                    mData.set(curIndex, task);

                    notifyDatasetChanged();
                }
                break;

            case REQUEST_FOR_HISTORY_TASK:
                if (resultCode == RESULT_OK) {

                    ArrayList<Task> tasks = (ArrayList<Task>) data.getSerializableExtra(RESULT_TASK_LIST);
                    if (tasks == null) return;

                    mData.clear();
                    mData.addAll(tasks);

                    notifyDatasetChanged();
                }
                break;
        }
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

        isResponse = true;

        myTool.log("Add Integrate Task Activity Received Msg : " + msg);
        if (msg == null || msg.length() == 0 || msg.charAt(0) != '[') return;

        JSONArray array = null;
        try {

            array = new JSONArray(msg);
            ArrayList<TaskAddInfo> infos = new ArrayList<>();

            if (array.length() != mData.size()) {
                myTool.log("发送消息与返回长度不一致！");
                return;
            }

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                // 回复消息解析
                if (!obj.has("response")) {
                    myTool.log("不可解析的信息，msg：" + msg);
                    return;
                }

                String response = obj.getString("response");

                TaskAddInfo info = new TaskAddInfo();
                info.setTaskName(mData.get(i).getName());
                info.setCode(response);
                info.setStatus("失败");

                switch (response) {
                    case "error":
                        info.setInfo("网络传输异常。");
                        break;
                    case "running":
                        info.setStatus("成功");
                        info.setInfo("任务添加成功。");
                        break;
                    case "schemerConflict":
                        info.setInfo("任务时间与计划任务冲突,添加失败。");
                        break;
                    case "currentRunning":
                        info.setInfo("与正在执行的任务相冲突,添加失败。");
                        break;
                    default:
                        info.setInfo("发生了不可预料的错误,添加失败。");
                }
                infos.add(info);
            }

            myTool.log("infos.size : " + infos.size());

            if (!infos.isEmpty()) {
                myTool.startActivity(infos, TaskAddInfoListActivity.class);
                if (mDialog.isShowing()) mDialog.dismissWithAnimation();
                finish();
            }

        } catch (JSONException e) {
            myTool.showInfo(e.getMessage());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // 绑定任务服务
        bindService(new Intent(this, TaskService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 销毁的时候解绑服务
        unbindService(connection);
    }
}
