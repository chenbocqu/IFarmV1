package com.qican.ifarm.ui_v2.task;


import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ControlSys;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.utils.TimeUtils;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class TaskListForShuiFeiActivity extends CommonListActivity<Task> {

    ControlSys mSys;
    List<Task> mData;
    SweetAlertDialog mDialog;
    int remainTime = 0;

    @Override
    public String getUITitle() {
        return mSys.getSystemType() + "任务列表";
    }

    @Override
    public void init() {
        mData = new ArrayList<>();
        mSys = (ControlSys) myTool.getParam(ControlSys.class);
        if (mSys == null) {
            mSys = new ControlSys();
            mSys.setSystemType("*参数错误*");
            return;
        }

        setRightMenu("添加任务", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivity(mSys, AddTaskForShuiFeiActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 请求数据
        refreshData();
    }

    private void refreshData() {
        showProgress();
        OkHttpUtils.post().url(myTool.getServAdd() + "farmControl/farmControlTaskStrategy")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("command", "queryTasks")
                .addParams("controlType", mSys.getControlType())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showError();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        hideProgress();
                        myTool.log(response);

                        if ("lose efficacy".equals(response)) {
                            myTool.showInfo("Token已失效，请重新登录！");
                            myTool.tologin();
                            return;
                        }

                        if (response == null || "[]".equals(response)) {
                            return;
                        }

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (!jsonObject.has("response")) {
                                notifyDatasetChanged();
                                return;
                            }

                            JSONArray array = jsonObject.getJSONArray("response");

                            mData.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Task task = new Task();

                                if (obj.has("controllerLogId"))
                                    task.setControllerLogId(obj.getString("controllerLogId"));

                                if (obj.has("farmId"))
                                    task.setFarmId(obj.getString("farmId"));

                                if (obj.has("farmName"))
                                    task.setFarmName(obj.getString("farmName"));

                                if (obj.has("systemDistrict"))
                                    task.setSystemDistrict(obj.getString("systemDistrict"));

                                if (obj.has("systemNo"))
                                    task.setSystemNo(obj.getString("systemNo"));

                                if (obj.has("controlDeviceId"))
                                    task.setControlDeviceId(obj.getString("controlDeviceId"));

                                if (obj.has("systemId"))
                                    task.setSystemId(obj.getString("systemId"));

                                if (obj.has("controlType"))
                                    task.setControlType(obj.getString("controlType"));

                                if (obj.has("controlOperation"))
                                    task.setControlOperation(obj.getString("controlOperation"));

                                if (obj.has("level"))
                                    task.setLevel(obj.getString("level"));

                                if (obj.has("executionTime"))
                                    task.setExecutionTime(obj.getString("executionTime"));

                                if (obj.has("startExecutionTime"))
                                    task.setStartExecutionTime(obj.getString("startExecutionTime"));

                                if (obj.has("taskState"))
                                    task.setTaskState(obj.getString("taskState"));

                                if (obj.has("functionName"))
                                    task.setName(obj.getString("functionName"));

                                if (obj.has("taskTime"))
                                    task.setTaskTime(obj.getString("taskTime"));

                                if (obj.has("remainExecutionTime"))
                                    task.setRemainExecutionTime(obj.getString("remainExecutionTime"));

                                if (obj.has("systemType"))
                                    task.setSystemType(obj.getString("systemType"));

                                mData.add(task);
                            }
                            notifyDatasetChanged();


                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }

    String getTimeFromNum(int num) {
        int hour = num / 3600;
        int min = (num % 3600) / 60;
        int sec = num % 60;

        String ret =
                (hour != 0 ? hour + "小时" : "") +
                        (min != 0 ? min + "分钟" : "") +
                        (sec != 0 ? sec + "秒" : "");
        if (hour == 0 && min == 0 && sec == 0) ret = "0秒";

        return ret;
    }

    @Override
    public ComAdapter<Task> getAdapter() {
        return new ComAdapter<Task>(this, mData, R.layout.item_common_task) {
            @Override
            public void convert(ViewHolder helper, final Task item) {
                ImageView ivDelete = helper.getView(R.id.iv_delete);// 撤销
                ImageView ivStop = helper.getView(R.id.iv_stop); // 停止任务
                final TextView tvRemainTime = helper.getView(R.id.tv_remaining_time);
                tvRemainTime.setVisibility(View.GONE);
                ivDelete.setVisibility(View.GONE);
                ivStop.setVisibility(View.GONE);

                String executionTime = getTimeFromNum(Integer.parseInt(item.getExecutionTime()));
                helper
                        .setText(R.id.tv_start_time, TimeUtils.formatTime(item.getStartExecutionTime()))
                        .setText(R.id.tv_last_time, executionTime)
                        .setText(R.id.tv_location, item.getFarmName() + item.getSystemDistrict())
                        .setText(R.id.tv_name, item.getName())
                        .setText(R.id.tv_time, TimeUtils.formatTime(item.getTaskTime()))
                        .setText(R.id.tv_desc, "在" + item.getFarmName() + item.getSystemDistrict() + "，从" +
                                TimeUtils.formatTime(item.getStartExecutionTime()) + "开始，持续执行" +
                                executionTime + "。"
                        );

                // 停止任务
                ivStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopTask(item);
                    }
                });
                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delTask(item);//撤销任务
                    }
                });
                helper.getView(R.id.iv_task_info).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myTool.startActivity(item, TaskInfoV2Activity.class);
                    }
                });


                String status = "";
                if (item.getTaskState() == null) return;
                switch (item.getTaskState()) {
                    case "WAITTING":
                        status = "等待中";
                        ivDelete.setVisibility(View.VISIBLE);
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
                                    // 倒计时结束，重新请求数据
                                    refreshData();
                                }
                            }.start();
                        }
                        break;
                    case "CONFICTING":
                        status = "冲突";
                        break;
                }
                helper.setText(R.id.tv_status, status);
            }
        };
    }

    private void delTask(Task task) {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在撤销任务...");
        mDialog.show();
        OkHttpUtils.post().url(myTool.getServAdd() + "farmControl/farmControlTaskStrategy")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("controlType", "wfm")
                .addParams("command", "delete")
                .addParams("controllerLogId", task.getControllerLogId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        myTool.log(response);
                        if (response == null || "[]".equals(response)) return;


                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            if (!object.has("response")) return;

                            String info = object.getString("response");
                            switch (info) {
                                case "success":
                                    mDialog.setTitleText("成功").setContentText("停止指令发送成功，任务已经撤销！")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                    refreshData();
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    break;
                                case "noTask":
                                    mDialog.setTitleText("提醒").setContentText("无此任务或任务已结束！")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                    refreshData();
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    break;
                                case "error":
                                    mDialog.setTitleText("未知错误").setContentText("发生了不可预料的错误，请稍后重试！")
                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    break;
                            }

                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });
    }

    // 停止任务
    private void stopTask(final Task task) {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在停止任务...");
        mDialog.show();
        OkHttpUtils.post().url(myTool.getServAdd() + "farmControl/farmControlTaskStrategy")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("command", "execute")
                .addParams("controlType", "wfm")
                .addParams("commandCategory", "handStop")
                .addParams("controllerLogId", task.getControllerLogId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response == null || "[]".equals(response)) return;
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            if (!object.has("response")) return;
                            String info = object.getString("response");
                            switch (info) {
                                case "running":
                                    mDialog.setTitleText("成功").setContentText("停止指令发送成功，任务正在停止！")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                    refreshData();
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    break;
                                case "noTask":
                                    mDialog.setTitleText("提醒").setContentText("无此任务或任务已结束！")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                    refreshData();
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    break;
                                case "error":
                                    mDialog.setTitleText("未知错误").setContentText("发生了不可预料的错误，请稍后重试！")
                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    break;
                            }

                        } catch (JSONException e) {
                            myTool.showInfo(e.getMessage());
                        }
                    }
                });

    }

    @Override
    public void onRefresh(PullToRefreshLayout l) {
        // 请求数据
        refreshData();
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTool.showInfo("暂无更多任务！");
                l.loadMoreFinish(true);
            }
        }, 1000);
    }

}
