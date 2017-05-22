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
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.utils.CommonTools;

public class TaskInfoFragment extends Fragment {
    private View view;
    private Task mTask;
    private CommonTools myTool;
    private View rlMain;
    private TextView tvName, tvDesc, tvStatus;
    private ImageView ivStatus;
    Bitmap bWaiting, bRunning, bCompleted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_task, container, false);

        initView(view);
        initData();

        return view;
    }

    private void initData() {
        bWaiting = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.waiting);
        bRunning = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.running);
        bCompleted = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.completed);

        mTask = (Task) getArguments().getSerializable(TaskPreviewActivity.TASK_KEY);
        tvDesc.setText(getDesc(mTask));
        tvName.setText(mTask.getName());
        setStatus();

    }

    private void setStatus() {
        switch (mTask.getStatus()) {
            case Completed:
                ivStatus.setImageBitmap(bCompleted);
                tvStatus.setText("已完成");
                rlMain.setBackgroundResource(R.drawable.taskbg_completed);
                break;
            case Running:
                tvStatus.setText("运行中");
                ivStatus.setImageBitmap(bRunning);
                rlMain.setBackgroundResource(R.drawable.taskbg_running);
                break;
            case Waiting:
                ivStatus.setImageBitmap(bWaiting);
                tvStatus.setText("等待中");
                rlMain.setBackgroundResource(R.drawable.taskbg_waiting);
                break;
        }
    }

    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        tvDesc = (TextView) v.findViewById(R.id.tv_desc);
        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvStatus = (TextView) v.findViewById(R.id.tv_status);
        rlMain = v.findViewById(R.id.rl_task_main);
        ivStatus = (ImageView) v.findViewById(R.id.iv_status);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String getDesc(Task task) {
        if (task == null) return "";
        //对A区实施时长为25min的施肥[ 罐 1 2 ]。
        return "对" + task.getArea() + "区实施时长为" + task.getDuration() + "的" +
                task.getOperationDesc() + "。";
    }

}
