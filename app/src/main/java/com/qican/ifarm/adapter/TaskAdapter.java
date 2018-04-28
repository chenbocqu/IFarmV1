package com.qican.ifarm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.ui.task.TaskInfoActivity;
import com.qican.ifarm.utils.TimeUtils;

import java.util.List;

public class TaskAdapter extends ComAdapter<Task> {

    Bitmap bWaiting, bRunning, bCompleted, bSending;

    public TaskAdapter(Context context, List<Task> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);

        bWaiting = BitmapFactory.decodeResource(context.getResources(), R.drawable.waiting);
        bRunning = BitmapFactory.decodeResource(context.getResources(), R.drawable.running);
        bCompleted = BitmapFactory.decodeResource(context.getResources(), R.drawable.completed);
        bSending = BitmapFactory.decodeResource(context.getResources(), R.drawable.sending);

    }

    @Override
    public void convert(final ViewHolder helper, final Task item) {

        LinearLayout llTaskMain = helper.getView(R.id.ll_main);
        TextView tvStatus = helper.getView(R.id.tv_status);

        helper
                .setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_starttime, TimeUtils.formatDateTime(item.getStartExecutionTime())) // 开始执行时间
                .setText(R.id.tv_area, item.getControlArea() + " 区")
                .setText(R.id.tv_can, item.getFertilizationCan() + " 罐")
                .setText(R.id.tv_remaining_time, TimeUtils.formatSecondTime(item.getExecutionTime()));

        if ("irrigate".equals(item.getControlType())) {
            helper.setText(R.id.tv_can, "无");
        }

        ImageView ivStatus = helper.getView(R.id.iv_status);
        RelativeLayout rlTitle = helper.getView(R.id.rl_task_title);

        if (item.getStatus() != null) {
            switch (item.getStatus()) {
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
        llTaskMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivity(item, TaskInfoActivity.class);
            }
        });
    }

}
