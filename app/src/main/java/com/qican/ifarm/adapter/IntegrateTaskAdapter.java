package com.qican.ifarm.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.utils.TimeUtils;

import java.util.List;

import static com.qican.ifarm.utils.TimeUtils.getTimeFromNum;

public class IntegrateTaskAdapter extends ComAdapter<Task> {
    public IntegrateTaskAdapter(Context context, List<Task> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    private String getStartTime(String time) {
        return time == null ? "立即执行" : TimeUtils.formatTime(time);
    }

    @Override
    public void convert(ViewHolder helper, Task item) {
        ImageView ivDelete = helper.getView(R.id.iv_delete);// 删除
        ImageView ivStop = helper.getView(R.id.iv_stop); // 停止任务

        final TextView tvRemainTime = helper.getView(R.id.tv_remaining_time);

        tvRemainTime.setVisibility(View.GONE);
        ivDelete.setVisibility(View.VISIBLE);
        ivStop.setVisibility(View.GONE);
        helper.getView(R.id.ll_menu).setVisibility(View.GONE);

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

        helper.setVisble(R.id.tv_status,false);
    }
}
