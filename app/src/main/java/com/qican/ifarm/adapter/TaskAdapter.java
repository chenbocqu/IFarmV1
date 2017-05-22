package com.qican.ifarm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.utils.DataBindUtils;

import java.util.List;

public class TaskAdapter extends CommonAdapter<Task> {

    Bitmap bWaiting, bRunning, bCompleted;

    public TaskAdapter(Context context, List<Task> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);

        bWaiting = BitmapFactory.decodeResource(context.getResources(), R.drawable.waiting);
        bRunning = BitmapFactory.decodeResource(context.getResources(), R.drawable.running);
        bCompleted = BitmapFactory.decodeResource(context.getResources(), R.drawable.completed);

    }

    @Override
    public void convert(ViewHolder helper, Task item) {

        LinearLayout llTaskMain = helper.getView(R.id.ll_task_main);
        TextView tvStatus = helper.getView(R.id.tv_status);

        helper
                .setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_desc, getDesc(item))
                .setText(R.id.tv_starttime, item.getStartTime())
                .setText(R.id.tv_endtime, "23:00");

        setItemBgByStatus(llTaskMain, tvStatus, item.getStatus());

        ImageView ivStatus = helper.getView(R.id.iv_status);

        switch (item.getStatus()) {
            case Waiting:
                ivStatus.setImageBitmap(bWaiting);
                break;
            case Running:
                ivStatus.setImageBitmap(bRunning);
                break;
            case Completed:
                ivStatus.setImageBitmap(bCompleted);
                break;
        }
    }

    private String getDesc(Task task) {
        //对A区实施时长为25min的施肥[ 罐 1 2 ]。
        return "对" + task.getArea() + "区实施时长为" + task.getDuration() + "的" +
                task.getOperationDesc() + "。";
    }

    /**
     * 选中TextView
     */
    void selectView(TextView tv) {
        tv.setBackgroundResource(R.drawable.item_selected);
        tv.setTextColor(Color.parseColor("#019978"));
        TextPaint tp1 = tv.getPaint();
        tp1.setFakeBoldText(true);
    }

    void setItemBgByStatus(View v, TextView tvStatus, Task.TaskStatus status) {
        switch (status) {
            case Completed:
                tvStatus.setText("已完成");
                v.setBackgroundResource(R.drawable.taskbg_completed);
                break;
            case Running:
                tvStatus.setText("运行中");
                v.setBackgroundResource(R.drawable.taskbg_running);
                break;
            case Waiting:
                tvStatus.setText("等待中");
                v.setBackgroundResource(R.drawable.taskbg_waiting);
                break;
        }
    }

}
