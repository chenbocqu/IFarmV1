package com.qican.ifarm.ui.control;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.OperationArea;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.ui.base.BaseActivityWithTitlebar;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.utils.TimePickerUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewTaskActivity extends BaseActivityWithTitlebar implements OnInfoListener<Task> {
    private RelativeLayout rlStartTime, rlLastTime, rlArea, rlType;
    private TextView tvStartTime, tvLastTime, tvArea, tvType;
    private Date starTimeDate, lastTimeDate;
    private List<OperationArea> mData;
    DialogForSelectType dialogForSelectType;
    private Task mTask;

    private ImageView ivWatering,
            ivTank1, ivTank2, ivTank3,
            ivA, ivB, ivC, ivD, ivE;


    @Override
    public String getUITitle() {
        return "添加任务";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_newtask;
    }

    @Override
    public void init() {
        rlStartTime = (RelativeLayout) findViewById(R.id.rlStartTime);
        rlLastTime = (RelativeLayout) findViewById(R.id.rlLastTime);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvLastTime = (TextView) findViewById(R.id.tvLastTime);
        rlArea = (RelativeLayout) findViewById(R.id.rlArea);
        tvArea = (TextView) findViewById(R.id.tvArea);
        rlType = (RelativeLayout) findViewById(R.id.rlOperation);
        tvType = (TextView) findViewById(R.id.tvOperation);

        initControlSysUI();

        //实例化SelectPicPopupWindow
        dialogForSelectType = new DialogForSelectType(this, mTask);

        setRightMenu("立即添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        initData();

        initEvent();
    }

    private void addTask() {
        // TODO: 添加任务待实现
//        myTool.showInfo("添加任务成功！");
//        this.finish();
        myTool.startActivity(TaskPreviewActivity.class);
    }

    private void initControlSysUI() {
        ivWatering = (ImageView) findViewById(R.id.iv_watering);

        ivTank1 = (ImageView) findViewById(R.id.iv_tank1);
        ivTank2 = (ImageView) findViewById(R.id.iv_tank2);
        ivTank3 = (ImageView) findViewById(R.id.iv_tank3);

        ivA = (ImageView) findViewById(R.id.iv_a);
        ivB = (ImageView) findViewById(R.id.iv_b);
        ivC = (ImageView) findViewById(R.id.iv_c);
        ivD = (ImageView) findViewById(R.id.iv_d);
        ivE = (ImageView) findViewById(R.id.iv_e);
    }

    private void initData() {
        mData = IFarmFakeData.getOperationAreas();
        mTask = new Task();
        showStatusAllStop();
    }

    void initEvent() {
        rlStartTime.setOnClickListener(this);
        rlLastTime.setOnClickListener(this);
        rlArea.setOnClickListener(this);
        rlType.setOnClickListener(this);
        dialogForSelectType.setOnInfoChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.rlStartTime:
                TimePickerUtil.pickTimeDialog(this, "时间选择", starTimeDate, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        starTimeDate = date;
                        tvStartTime.setText(getTime(date));
                    }
                });
                break;

            case R.id.rlLastTime:
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
            case R.id.rlArea:
                TimePickerUtil.pickOperationArea(this, mData, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        tvArea.setText(mData.get(options1).getName() + " 区");
                        mTask.clearArea();
                        switch (mData.get(options1).getName()) {
                            case "A":
                                mTask.setA(true);
                                break;
                            case "B":
                                mTask.setB(true);
                                break;
                            case "C":
                                mTask.setC(true);
                                break;
                            case "D":
                                mTask.setD(true);
                                break;
                            case "E":
                                mTask.setE(true);
                                break;
                        }
                        showStatusByTask(mTask);
                    }
                });
                break;

            case R.id.rlOperation:
                //显示窗口
                dialogForSelectType.showAtLocation(this.findViewById(R.id.ll_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                break;
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    public void onInfoChanged(Task obj) {
        mTask = obj;
        String type = "";
        if (mTask.isTank1())
            type = type + "1 ";
        if (mTask.isTank2())
            type = type + "2 ";
        if (mTask.isTank3())
            type = type + "3 ";
        if (type.equals("")) {
            type = "灌溉";
        } else {
            type = "施肥[ 罐 " + type + "]";
        }
        tvType.setText(type);
        showStatusByTask(mTask);
    }

    private void resetTaskUI() {
        ivWatering.setVisibility(View.GONE);

        ivTank1.setVisibility(View.GONE);
        ivTank2.setVisibility(View.GONE);
        ivTank3.setVisibility(View.GONE);

        ivA.setVisibility(View.GONE);
        ivB.setVisibility(View.GONE);
        ivC.setVisibility(View.GONE);
        ivD.setVisibility(View.GONE);
        ivE.setVisibility(View.GONE);
    }

    void showStatusByTask(Task task) {
        showStatusByTask(false
                , task.isTank1(), task.isTank2(), task.isTank3()
                , task.isA(), task.isB(), task.isC(), task.isD(), task.isE());
    }

    private void showStatusAllStop() {
        showStatusByTask(true, false, false, false, false, false, false, false, false);
    }

    private void showStatusByTask(boolean isAllStop
            , boolean isTank1, boolean isTank2, boolean isTank3
            , boolean isA, boolean isB, boolean isC, boolean isD, boolean isE) {
        resetTaskUI();//清除其他任务状态
        if (isAllStop) {
            return;
        }

        ivWatering.setVisibility(View.VISIBLE);

        if (isTank1)
            ivTank1.setVisibility(View.VISIBLE);
        if (isTank2)
            ivTank2.setVisibility(View.VISIBLE);
        if (isTank3)
            ivTank3.setVisibility(View.VISIBLE);

        if (isA)
            ivA.setVisibility(View.VISIBLE);
        if (isB)
            ivB.setVisibility(View.VISIBLE);
        if (isC)
            ivC.setVisibility(View.VISIBLE);
        if (isD)
            ivD.setVisibility(View.VISIBLE);
        if (isE)
            ivE.setVisibility(View.VISIBLE);
    }
}
