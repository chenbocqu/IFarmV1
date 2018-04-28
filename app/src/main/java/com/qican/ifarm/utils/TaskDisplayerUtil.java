/**
 * 任务显示的工具类，需要传过来任务显示的主控件
 *
 * @作者 陈波
 * @Time 2017-5-27
 * @Email qiurenbieyuan@gmail.com
 */
package com.qican.ifarm.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;

public class TaskDisplayerUtil {
    private Context mContext;
    private View mainView;
    private Task mTask;

    private ImageView ivWatering,
            ivTank1, ivTank2, ivTank3,
            ivA, ivB, ivC, ivD, ivE;

    public TaskDisplayerUtil(Context mContext) {
        this(mContext, ((Activity) mContext).findViewById(R.id.rl_task));
    }

    public TaskDisplayerUtil(Context mContext, View mainView) {
        this(mContext, mainView, null);
    }

    public TaskDisplayerUtil(Context mContext, View mainView, Task task) {
        this.mContext = mContext;
        this.mainView = mainView;
        this.mTask = task;

        initView();
        showStatusAllStop();
        notifyDataChanged();
    }

    private void initView() {
        ivWatering = (ImageView) mainView.findViewById(R.id.iv_watering);

        ivTank1 = (ImageView) mainView.findViewById(R.id.iv_tank1);
        ivTank2 = (ImageView) mainView.findViewById(R.id.iv_tank2);
        ivTank3 = (ImageView) mainView.findViewById(R.id.iv_tank3);

        ivA = (ImageView) mainView.findViewById(R.id.iv_a);
        ivB = (ImageView) mainView.findViewById(R.id.iv_b);
        ivC = (ImageView) mainView.findViewById(R.id.iv_c);
        ivD = (ImageView) mainView.findViewById(R.id.iv_d);
        ivE = (ImageView) mainView.findViewById(R.id.iv_e);
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

    public void showStatusByTask(Task task) {

        if (task == null) return;

        resetTaskUI();
        ivWatering.setVisibility(View.VISIBLE);

        // 解析区域
        String area = task.getControlArea();
        if (area != null) {
            String[] areas = area.split(",");
            for (int i = 0; i < areas.length; i++) {
                switch (areas[i]) {
                    case "A":
                        ivA.setVisibility(View.VISIBLE);
                        break;
                    case "B":
                        ivB.setVisibility(View.VISIBLE);
                        break;
                    case "C":
                        ivC.setVisibility(View.VISIBLE);
                        break;
                    case "D":
                        ivD.setVisibility(View.VISIBLE);
                        break;
                    case "E":
                        ivE.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        // 解析施肥罐
        String can = task.getFertilizationCan();
        if (area != null) {
            String[] cans = can.split(",");
            for (int i = 0; i < cans.length; i++) {
                switch (cans[i]) {
                    case "1":
                        ivTank1.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        ivTank2.setVisibility(View.VISIBLE);
                        break;
                    case "3":
                        ivTank3.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    public void showStatusAllStop() {
        resetTaskUI();
    }

    public void notifyDataChanged() {
        if (mTask == null) {
            Log.i("TaskDisplayerUtil", "未设置数据源，不能调用notifyDataChanged()！");
            return;
        }
        showStatusByTask(mTask);
    }
}
