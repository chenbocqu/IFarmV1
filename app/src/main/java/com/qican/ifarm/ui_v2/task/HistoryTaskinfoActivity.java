package com.qican.ifarm.ui_v2.task;


import android.content.Intent;
import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.bean.TaskList;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.utils.TimeUtils;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;

public class HistoryTaskinfoActivity extends CommonListActivity<TaskList> {

    ArrayList<TaskList> mData;
    public static final int REQUEST_FOR_TASK_LIST = 1;

    @Override
    public String getUITitle() {
        return "历史配置";
    }

    @Override
    public void init() {
        mData = new ArrayList<>();

        showProgress();
        // 刷新数据
        OkHttpUtils.post().url(myTool.getServAdd() + "farmControl/combinationControlHistory")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hideProgress();
                        showError();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        hideProgress();
                        if (response == null) return;

                        myTool.log("历史配置信息：\n" + response);
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                TaskList info = new TaskList();
                                info.setUserId(obj.getString("userId"));
                                info.setTaskTime(obj.getString("taskTime"));

                                JSONArray list = obj.getJSONArray("taskContent");
                                ArrayList<Task> datas = new ArrayList<Task>();

                                for (int j = 0; j < list.length(); j++) {
                                    JSONObject taskObj = list.getJSONObject(j);
                                    Task task = new Task();
                                    task.setFunctionName(taskObj.getString("functionName"));
                                    task.setFarmId(taskObj.getString("farmId"));
                                    task.setFarmName(taskObj.getString("farmName"));
                                    task.setSystemDistrict(taskObj.getString("systemDistrict"));
                                    task.setSystemNo(taskObj.getString("systemNo"));
                                    task.setSystemId(taskObj.getString("systemId"));
                                    task.setControlType(taskObj.getString("controlType"));
                                    task.setControlOperation(taskObj.getString("controlOperation"));
                                    task.setCommandCategory(taskObj.getString("commandCategory"));
                                    task.setCommand(taskObj.getString("command"));
                                    task.setExecutionTime(taskObj.getString("executionTime"));

                                    task.setName(task.getFunctionName());

                                    if (taskObj.has("controlArea"))
                                        task.setControlArea(taskObj.getString("controlArea"));

                                    datas.add(task);
                                }
                                info.setTasks(datas);

                                mData.add(info);
                            }
                            notifyDatasetChanged();
                        } catch (JSONException e) {
                            myTool.log(e.getMessage());
                            showError();
                        }
                    }
                });

    }

    @Override
    public ComAdapter<TaskList> getAdapter() {
        return new ComAdapter<TaskList>(this, mData, R.layout.item_task_history) {
            @Override
            public void convert(ViewHolder helper, final TaskList item) {
                helper.setText(R.id.tv_time, TimeUtils.formatTime(item.getTaskTime()));
                helper.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myTool.startActivityForResult(item.getTasks(), HistoryTaskListActivity.class, REQUEST_FOR_TASK_LIST);
                    }
                });
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FOR_TASK_LIST:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout l) {
        l.refreshFinish(true);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout l) {
        l.loadMoreFinish(true);
    }
}
