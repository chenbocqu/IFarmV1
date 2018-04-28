package com.qican.ifarm.ui_v2.control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.ui_v2.controlsys.ControlSysListActivity;
import com.qican.ifarm.ui_v2.controlsys.ShuiFeiYaoSysListActivity;
import com.qican.ifarm.ui_v2.task.AllTaskListActivity;
import com.qican.ifarm.ui_v2.task.RunningTaskListActivity;
import com.qican.ifarm.ui_v2.task.TaskIntegrateActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.PhoneCallUtils;

public class ControlFuncsFragment extends FragmentWithOnResume implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private CommonTools myTool;
    private ImageView ivCall;
    SwipeRefreshLayout srl;
    float ratio = 1 / 3.6f;
    TextView tvSerNum;
    String serNum;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_control_funcs, container, false);
        initView(view);
        initDatas();
        initEvent();
        return view;
    }

    private void initEvent() {
        ivCall.setOnClickListener(this);
        srl.setOnRefreshListener(this);

        view.findViewById(R.id.ll_guangai).setOnClickListener(this);
        view.findViewById(R.id.ll_shifei).setOnClickListener(this);
        view.findViewById(R.id.ll_shiyao).setOnClickListener(this);

        view.findViewById(R.id.ll_wendu).setOnClickListener(this);
        view.findViewById(R.id.ll_shidu).setOnClickListener(this);

        view.findViewById(R.id.ll_tongfeng).setOnClickListener(this);
        view.findViewById(R.id.ll_buguang).setOnClickListener(this);
        view.findViewById(R.id.ll_zheyang).setOnClickListener(this);
        view.findViewById(R.id.ll_juanlian).setOnClickListener(this);
        view.findViewById(R.id.ll_shuilian).setOnClickListener(this);

        view.findViewById(R.id.ll_co2).setOnClickListener(this);
        view.findViewById(R.id.ll_o2).setOnClickListener(this);

        view.findViewById(R.id.ll_integrate).setOnClickListener(this);
        view.findViewById(R.id.ll_running).setOnClickListener(this);
        view.findViewById(R.id.ll_all).setOnClickListener(this);
    }

    private void initDatas() {
        String tmp = myTool.getSerNum();
        String[] temps = tmp.split(",");
        if (temps.length == 2) {
            serNum = temps[1];
            tvSerNum.setText("客服（" + temps[0] + "）：" + temps[1] + "。");
        }
    }


    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        ivCall = (ImageView) v.findViewById(R.id.iv_call);
        srl = (SwipeRefreshLayout) v.findViewById(R.id.srl);
        tvSerNum = (TextView) v.findViewById(R.id.tv_ser_num);

        myTool.setHeightByWindow(v.findViewById(R.id.ll_control_group1), ratio);
        myTool.setHeightByWindow(v.findViewById(R.id.ll_control_group2), ratio);
        myTool.setHeightByWindow(v.findViewById(R.id.ll_control_group3), ratio);
        myTool.setHeightByWindow(v.findViewById(R.id.ll_control_group4), ratio);
        myTool.setHeightByWindow(v.findViewById(R.id.ll_control_group5), ratio);
        myTool.setHeightByWindow(v.findViewById(R.id.ll_control_header), 1 / 3f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_call:
                if (serNum != null)
                    PhoneCallUtils.call(serNum);//拨打客服电话
                else
                    myTool.showInfo("客服电话有误，请查证后拨打电话！");
                break;

            case R.id.ll_guangai:
                toShuiFeiSysList("灌溉");
                break;
            case R.id.ll_shifei:
                toShuiFeiSysList("施肥");
                break;
            case R.id.ll_shiyao:
                toShuiFeiSysList("施药");
                break;

            case R.id.ll_wendu:
                toTaskList("温度");
                break;
            case R.id.ll_shidu:
                toTaskList("湿度");
                break;

            case R.id.ll_tongfeng:
                toTaskList("通风");
                break;
            case R.id.ll_buguang:
                toTaskList("补光");
                break;
            case R.id.ll_zheyang:
                toTaskList("遮阳");
                break;
            case R.id.ll_juanlian:
                toTaskList("卷帘");
                break;
            case R.id.ll_shuilian:
                toTaskList("水帘");
                break;

            case R.id.ll_co2:
                toTaskList("二氧化碳");
                break;
            case R.id.ll_o2:
                toTaskList("氧气");
                break;

            case R.id.ll_all:
                myTool.startActivity(AllTaskListActivity.class);
                break;

            case R.id.ll_integrate:
                myTool.startActivity(TaskIntegrateActivity.class);
                break;

            case R.id.ll_running:
                myTool.startActivity(RunningTaskListActivity.class);
                break;
        }
    }

    private void toShuiFeiSysList(String title) {
//        myTool.startActivity(new ControlFunction("ha ha"), SelectSystemActivity.class);
        myTool.startActivity(title, ShuiFeiYaoSysListActivity.class);
    }

    private void toTaskList(String title) {
//        myTool.startActivity(title, ControlTaskManagerActivity.class);
        myTool.startActivity(title, ControlSysListActivity.class);
    }

    @Override
    public void onRefresh() {
        srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                myTool.showInfo("列表内容已更新！");
                srl.setRefreshing(false);
            }
        }, 1500);
    }
}
