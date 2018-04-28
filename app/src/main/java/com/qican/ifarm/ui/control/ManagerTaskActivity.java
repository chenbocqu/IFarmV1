/**
 * 管理任务
 */
package com.qican.ifarm.ui.control;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.TaskAdapter;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.service.TaskService;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagerTaskActivity extends CommonListActivity<Task> implements OnInfoListener<String> {
    List<Task> mDatas;
    TaskAdapter mAdapter;
    ControlSystem mSystem;

    @Override
    public String getUITitle() {
        return "管理任务";
    }

    @Override
    public void init() {
        mSystem = (ControlSystem) myTool.getParam(ControlSystem.class);
        if (mSystem == null) mSystem = new ControlSystem();

        // 绑定任务服务
        bindService(new Intent(this, TaskService.class), connection, BIND_AUTO_CREATE);
        myTool.log("绑定服务···");

//        mDatas = IFarmFakeData.getTaskList();
        mDatas = new ArrayList<>();
        mAdapter = new TaskAdapter(this, mDatas, R.layout.item_taskinfo);

        setRightMenu("新任务", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivity(NewTaskActivity.class);
            }
        });

        if (taskBinder != null) {
            myTool.log("设置监听成功···");
            taskBinder.setOnTaskMsgListener(this);
        }
    }

    @Override
    public ComAdapter<Task> getAdapter() {
        return mAdapter;
    }

    @Override
    public void onRefresh(PullToRefreshLayout l) {
        if (taskBinder != null) {
            taskBinder.queryTasks(mSystem);
            showProgress();
        }
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.loadMoreFinish(true);
            }
        }, 1000);
    }

    private TaskService.TaskBinder taskBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myTool.log("onServiceConnected");
            taskBinder = (TaskService.TaskBinder) service;
            taskBinder.setOnTaskMsgListener(ManagerTaskActivity.this);
            taskBinder.queryTasks(mSystem);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myTool.log("onServiceDisconnected");
        }
    };

    // 处理回复信息，更新列表数据
    private void dealByResponse(String response) {

        hideProgress();

        myTool.log("response:" + response);

        if (response == null) return;

        if (response.length() == 0) return;

        if (response.charAt(0) == '{') {

            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                if (obj.has("response")) {
                    if ("no work".equals(obj.getString("response"))) {
                        mDatas.clear();
                        notifyDatasetChanged();
                        return;
                    } else {
                        // 后台有推送信息，则刷新当前数据
                        if (!obj.has("type")) return;
                        if (taskBinder != null) {
                            taskBinder.queryTasks(mSystem);
                            showProgress();
                        }
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

                            case "BLOCKING"://下发中
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
                        // 解析异常处理

                    }
                    mDatas.add(task);
                }
                notifyDatasetChanged();

            } catch (JSONException e) {
                myTool.showInfo(e.getMessage());
            }


        } else myTool.log("没有解析操作，response：" + response);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁的时候解绑服务
        unbindService(connection);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (taskBinder != null) {
            taskBinder.queryTasks(mSystem);
            taskBinder.setOnTaskMsgListener(this);
            showProgress();
        }
    }

    @Override
    public void onInfoChanged(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dealByResponse(msg);
            }
        });
    }
}
