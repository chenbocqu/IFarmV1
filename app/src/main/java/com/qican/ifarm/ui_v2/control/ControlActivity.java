package com.qican.ifarm.ui_v2.control;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ControlNodeAdapter;
import com.qican.ifarm.bean.ControlNode;
import com.qican.ifarm.bean.ControlTask;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Operations;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.listener.PopwindowListener;
import com.qican.ifarm.ui_v2.base.ContentWithBaseListActivity;
import com.qican.ifarm.utils.TimePickerUtil;
import com.qican.ifarm.utils.TimeUtils;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ControlActivity extends ContentWithBaseListActivity<ControlNode> implements PopwindowListener<Object, Object> {
    public static final String KEY_NEW_TASK = "KEY_NEW_TASK";
    String title, error = "Error";

    List<ControlNode> mDatas;
    Window4SelectFarm window4SelectFarm;
    PopWin4SelectArea popWin4SelectArea;
    PopWin4SelectOperation popWin4SelectOperation;
    Farm mFarm;
    Subarea mSubarea;
    TextView tvLoaction;
    private Date starTimeDate, lastTimeDate;
    TextView tvStartTime, tvLastTime, tvOperation;
    Operations mOpera;
    SweetAlertDialog mDialog;
    ControlTask newTask;
    String loaction = "";
    String startTime = "";

    @Override
    public String getUITitle() {
        return title;
    }

    @Override
    public void init() {
        title = (String) myTool.getParam(String.class);
        if (title == null) title = error;
        mDatas = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ControlNode node = new ControlNode();
            node.setName("节点" + (i + 1));
            node.setDesc("这是关于" + "节点" + (i + 1) + "的备注信息。");
            mDatas.add(node);
        }

        findViewById(R.id.rl_choose_loc).setOnClickListener(this);
        tvLoaction = (TextView) findViewById(R.id.tv_location);
        tvStartTime = (TextView) findViewById(R.id.tv_starttime);
        tvLastTime = (TextView) findViewById(R.id.tv_lasttime);
        tvOperation = (TextView) findViewById(R.id.tv_operate);

        window4SelectFarm = new Window4SelectFarm(this);
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        setRightMenu("立即添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewTask();
            }
        });

        initOperations();
    }

    private void addNewTask() {

        if (isEmpty(tvLoaction)) {
            myTool.showInfo("请选择操作区域！");
            return;
        }
        if (isEmpty(tvOperation)) {
            myTool.showInfo("请选择操作类型！");
            return;
        }
        if (isEmpty(tvStartTime)) {
            myTool.showInfo("请选择开始时间！");
            return;
        }
        if (isEmpty(tvLastTime)) {
            myTool.showInfo("请选择持续时间！");
            return;
        }
        newTask = new ControlTask();
        newTask.setOperation(tvOperation.getText().toString());
        newTask.setStartTime(startTime);
        newTask.setLastTime(tvLastTime.getText().toString());
        newTask.setLocation(loaction);

        Intent intent = new Intent();
        intent.putExtra(KEY_NEW_TASK, newTask);
        setResult(0, intent);

        mDialog.setTitleText("正在添加...").show();
        tvLoaction.postDelayed(new Runnable() {
            @Override
            public void run() {

                mDialog.setTitleText("添加成功")
                        .setConfirmText("确定")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        }).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            }
        }, 3000);

    }

    private boolean isEmpty(TextView tv) {
        return TextUtils.isEmpty(tv.getText());
    }

    private void initOperations() {
        ArrayList<String> items = null;
        switch (title) {
            case "温度":
                items = new ArrayList<String>(Arrays.asList("升温", "降温"));
                break;
            case "湿度":
                items = new ArrayList<String>(Arrays.asList("增湿", "除湿"));
                break;
            case "通风":
                items = new ArrayList<String>(Arrays.asList("进风", "出风", "轴流"));
                break;
            case "补光":
                items = new ArrayList<String>(Arrays.asList("一组", "二组", "三组", "四组", "五组", "六组", "七组"));
                break;
            case "遮阳":
                items = new ArrayList<String>(Arrays.asList("外遮阳（正转）", "外遮阳（反转）", "内遮阳（正转）", "内遮阳（反转）"));
                break;
            case "卷帘":
                items = new ArrayList<String>(Arrays.asList("顶卷帘（正转）", "顶卷帘（反转）", "侧卷帘（正转）", "侧卷帘（反转）"));
                break;
            case "二氧化碳":
                items = new ArrayList<String>(Arrays.asList("增加CO2浓度", "降低CO2浓度"));
                break;
            case "氧气":
                items = new ArrayList<String>(Arrays.asList("增加O2浓度", "降低O2浓度"));
                break;
            default:
                items = new ArrayList<String>(Arrays.asList("无相关操作"));
        }
        List<Operations> operationses = new ArrayList<>();
        for (String item : items) {
            Operations op = new Operations();
            op.setName(item);
            operationses.add(op);
        }
        popWin4SelectOperation = new PopWin4SelectOperation(this, operationses);
        popWin4SelectOperation.setOnPowwinListener(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();

        window4SelectFarm.setOnPowwinListener(this);
        findViewById(R.id.rl_operation).setOnClickListener(this);
        findViewById(R.id.rl_start_time).setOnClickListener(this);
        findViewById(R.id.rl_last_time).setOnClickListener(this);
    }

    @Override
    public ComAdapter<ControlNode> getAdapter() {
        return new ControlNodeAdapter(this, mDatas, R.layout.item_control_node);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_control_nodes;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_choose_loc:
                myTool.showPopFormBottom(window4SelectFarm, findViewById(R.id.ll_main));
                break;
            case R.id.rl_operation:
                myTool.showPopFormBottom(popWin4SelectOperation, findViewById(R.id.ll_main));
                break;
            case R.id.rl_start_time:
                TimePickerUtil.pickTimeDialog(this, "开始时间", starTimeDate, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        startTime = getTime(date);
                        starTimeDate = date;
                        tvStartTime.setText(TimeUtils.formatDateTime(getTime(date)));
                    }
                });
                break;
            case R.id.rl_last_time:
                TimePickerUtil.pickLastTime(this, "持续时间", lastTimeDate, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        lastTimeDate = date;
                        if (date.getHours() != 0)
                            tvLastTime.setText(date.getHours() + "小时 " + date.getMinutes() + "分钟");
                        else
                            tvLastTime.setText(date.getMinutes() + "分钟");
                    }
                });
                break;
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.refreshFinish(true);
            }
        }, 1500);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.loadMoreFinish(true);
            }
        }, 1500);
    }

    // 处理选择框消息
    @Override
    public void infoChanged(Object w, Object obj) {
        if ("Window4SelectFarm".equals(w.getClass().getSimpleName())) {
            mFarm = (Farm) obj;
            popWin4SelectArea = new PopWin4SelectArea(this, (Farm) obj);
            popWin4SelectArea.setOnPowwinListener(this);
            pullListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myTool.showPopFormBottom(popWin4SelectArea, findViewById(R.id.ll_main));
                    popWin4SelectArea.refreshData();
                }
            }, 500);
        } else if ("PopWin4SelectArea".equals(w.getClass().getSimpleName())) {
            mSubarea = (Subarea) obj;
            tvLoaction.setText(mFarm.getName() + " > " + mSubarea.getName());
            loaction = mFarm.getName() + mSubarea.getName();
            // todo 开始筛选控制节点（随机筛选模拟）

            float ratio = 0.3f; // 删除比例

            Random random = new Random();

            int cnt = (int) (mDatas.size() * ratio);
            while (cnt > 0) {
                int s = random.nextInt(mDatas.size()) % mDatas.size();
                if (s >= 0 && s < mDatas.size()) {
                    mDatas.remove(s);
                    cnt--;
                }
            }
            for (int i = 0; i < mDatas.size(); i++)
                mDatas.get(i).setSelected(true);
            notifyDatasetChanged();

        } else if ("PopWin4SelectOperation".equals(w.getClass().getSimpleName())) {
            mOpera = (Operations) obj;
            if (mOpera != null) tvOperation.setText(mOpera.getName());
        }
    }
}
