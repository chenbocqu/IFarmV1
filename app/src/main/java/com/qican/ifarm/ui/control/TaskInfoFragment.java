package com.qican.ifarm.ui.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.ui.task.TaskInfoActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.TimeUtils;

public class TaskInfoFragment extends Fragment implements View.OnClickListener {
    private View view;
    private Task mTask;
    private CommonTools myTool;
    private View rlTitle;
    private TextView tvName, tvDesc, tvStatus;
    private TextView tvArea, tvCan, tvRemainingTime, tvStartTime;
    private ImageView ivStatus;
    Bitmap bWaiting, bRunning, bCompleted, bSending;
    LinearLayout llMain;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_taskinfo, container, false);

        initView(view);
        initData();
        initEvent();

        return view;
    }

    private void initEvent() {
        llMain.setOnClickListener(this);
    }

    private void initData() {
        bWaiting = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.waiting);
        bRunning = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.running);
        bCompleted = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.completed);
        bSending = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.sending);

        mTask = (Task) getArguments().getSerializable(TaskPreviewerActivity.TASK_KEY);

        tvArea.setText(mTask.getControlArea() + " 区");
        tvCan.setText(mTask.getFertilizationCan() + " 罐");
        tvRemainingTime.setText(mTask.getExecutionTime() + " 秒");
        tvStartTime.setText(TimeUtils.formatDateTime(mTask.getStartExecutionTime()));

        tvDesc.setText("任务：" + getDesc(mTask));
        tvName.setText(mTask.getName());

        setStatus();
    }

    private void setStatus() {
        if (mTask.getStatus() == null) return;
        switch (mTask.getStatus()) {
            case Completed:
                ivStatus.setImageBitmap(bCompleted);
                tvStatus.setText("已完成");
                rlTitle.setBackgroundResource(R.drawable.taskbg_completed);
                break;
            case Running:
                tvStatus.setText("运行中");
                ivStatus.setImageBitmap(bRunning);
                rlTitle.setBackgroundResource(R.drawable.taskbg_running);
                break;
            case Waiting:
                ivStatus.setImageBitmap(bWaiting);
                tvStatus.setText("等待中");
                rlTitle.setBackgroundResource(R.drawable.taskbg_waiting);
                break;
            case Blocking:
                ivStatus.setImageBitmap(bSending);
                tvStatus.setText("下发中");
                rlTitle.setBackgroundResource(R.drawable.taskbg_sending);
                break;
        }
    }

    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        tvDesc = (TextView) v.findViewById(R.id.tv_desc);
        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvStatus = (TextView) v.findViewById(R.id.tv_status);
        rlTitle = v.findViewById(R.id.rl_task_title);
        ivStatus = (ImageView) v.findViewById(R.id.iv_status);

        tvStartTime = (TextView) v.findViewById(R.id.tv_starttime);
        tvArea = (TextView) v.findViewById(R.id.tv_area);
        tvCan = (TextView) v.findViewById(R.id.tv_can);
        tvRemainingTime = (TextView) v.findViewById(R.id.tv_remaining_time);

        llMain = (LinearLayout) v.findViewById(R.id.ll_main);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String getDesc(Task task) {
        if (task == null) return "";
        //对A区实施时长为25min的施肥[ 罐 1 2 ]。
        String controlDesc = "灌溉";
        if ("fertilizer".equals(task.getControlType()))
            controlDesc = "施肥（罐" + task.getFertilizationCan() + "）";

        return "对" + task.getControlArea() + "区实施时长为" + task.getExecutionTime() + "秒的" +
                controlDesc + "。";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_main:
                myTool.startActivity(mTask, TaskInfoActivity.class);
                break;
        }
    }
}
