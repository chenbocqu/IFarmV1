package com.qican.ifarm.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;

public class TaskStatusDisplayerUtil {
    private Context mContext;
    private Task mTask;
    private View mainView;
    View vCircleCenter, vCircleLeft, vCircleRight, vLineLeft, vLineRight;
    TextView tvTask, tvTaskTime, tvStart, tvStartTime, tvComplete, tvCompleteTime;

    public TaskStatusDisplayerUtil(Context mContext) {
        this(mContext, null);
    }

    public TaskStatusDisplayerUtil(Context mContext, Task mTask) {
        this.mContext = mContext;
        this.mTask = mTask;
        init();
    }

    private void init() {
        initView();
        resetUI();
    }

    private void resetUI() {
        vCircleLeft.setBackgroundResource(R.drawable.unselected_radius);
        vCircleCenter.setBackgroundResource(R.drawable.unselected_radius);
        vCircleRight.setBackgroundResource(R.drawable.unselected_radius);

        vLineLeft.setBackgroundResource(R.color.white);
        vLineRight.setBackgroundResource(R.color.white);

        unselectTv(tvTask);
        unselectTv(tvTaskTime);
        unselectTv(tvStart);
        unselectTv(tvStartTime);
        unselectTv(tvComplete);
        unselectTv(tvCompleteTime);
    }

    private void initView() {
        mainView = ((Activity) mContext).findViewById(R.id.rl_task_status);

        vCircleLeft = mainView.findViewById(R.id.v_circleLeft);
        vCircleCenter = mainView.findViewById(R.id.v_circleCenter);
        vCircleRight = mainView.findViewById(R.id.v_circleRight);

        vLineLeft = mainView.findViewById(R.id.v_line_left);
        vLineRight = mainView.findViewById(R.id.v_line_right);

        tvTask = (TextView) mainView.findViewById(R.id.tv_task);
        tvTaskTime = (TextView) mainView.findViewById(R.id.tv_tasktime);
        tvStart = (TextView) mainView.findViewById(R.id.tv_start);
        tvStartTime = (TextView) mainView.findViewById(R.id.tv_starttime);
        tvComplete = (TextView) mainView.findViewById(R.id.tv_complete);
        tvCompleteTime = (TextView) mainView.findViewById(R.id.tv_complete_time);
    }

    void selectTv(TextView tv) {
        Resources resource = mContext.getResources();
        ColorStateList csl = resource.getColorStateList(R.color.yellow_light);
        if (csl != null) {
            tv.setTextColor(csl);
        }
    }

    void unselectTv(TextView tv) {
        Resources resource = mContext.getResources();
        ColorStateList csl = resource.getColorStateList(R.color.white);
        if (csl != null) {
            tv.setTextColor(csl);
        }
    }

    public void showStatus(Task task) {
        if (task == null) return;
        if (task.getStatus() == null) return;
        resetUI();
        switch (task.getStatus()) {
            case Completed:
                selectTv(tvComplete);
                selectTv(tvCompleteTime);
                vLineRight.setBackgroundResource(R.color.yellow_light);
                vCircleRight.setBackgroundResource(R.drawable.selected_radius);
            case Running:
                selectTv(tvStart);
                selectTv(tvStartTime);
                vLineLeft.setBackgroundResource(R.color.yellow_light);
                vCircleCenter.setBackgroundResource(R.drawable.selected_radius);
            case Blocking:
            case Waiting:
                selectTv(tvTask);
                selectTv(tvTaskTime);
                vCircleLeft.setBackgroundResource(R.drawable.selected_radius);
                break;
        }
    }

    public TaskStatusDisplayerUtil setTaskTime(String time) {
        tvTaskTime.setText(time);
        return this;
    }

    public TaskStatusDisplayerUtil setStartTime(String time) {
        tvStartTime.setText(time);
        return this;
    }

    public TaskStatusDisplayerUtil setCompleteTime(String time) {
        tvCompleteTime.setText(time);
        return this;
    }
}
