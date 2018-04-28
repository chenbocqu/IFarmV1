package com.qican.ifarm.ui.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.listener.PopwindowListener;


public class DialogForSelectArea extends PopupWindow implements OnClickListener {

    PopwindowListener<PopupWindow, Task> mCallBack;

    private Task mTask = null;//单价

    private TextView tvA, tvB, tvC, tvD, tvE;
    private ImageView ivCancel;

    private Button btnOK;
    private View mMenuView;

    public DialogForSelectArea(Activity context, Task task) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_select_area, null);

        initView(mMenuView);
        initData(task);

        initEvent();
        seWindowStyle();
    }

    private void initData(Task task) {
        mTask = task;
        if (mTask == null) mTask = new Task();
        if (mTask.isA()) {
            selectView(tvA);
        }
        if (mTask.isB()) {
            selectView(tvB);
        }
        if (mTask.isC()) {
            selectView(tvC);
        }
        if (mTask.isD()) {
            selectView(tvD);
        }
        if (mTask.isE()) {
            selectView(tvE);
        }
    }

    private void seWindowStyle() {
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popwindow_anim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x88000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    private void initEvent() {
        //取消按钮
        btnOK.setOnClickListener(this);

        tvA.setOnClickListener(this);
        tvB.setOnClickListener(this);
        tvC.setOnClickListener(this);
        tvD.setOnClickListener(this);
        tvE.setOnClickListener(this);
        ivCancel.setOnClickListener(this);

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void initView(View view) {
        btnOK = (Button) view.findViewById(R.id.btn_ok);

        tvA = (TextView) view.findViewById(R.id.tv_A);
        tvB = (TextView) view.findViewById(R.id.tv_B);
        tvC = (TextView) view.findViewById(R.id.tv_C);
        tvD = (TextView) view.findViewById(R.id.tv_D);
        tvE = (TextView) view.findViewById(R.id.tv_E);

        ivCancel = (ImageView) view.findViewById(R.id.iv_cancel);//取消按钮
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_A:
                mTask.setA(dealWithTvByStatus(mTask.isA(), tvA));
                break;
            case R.id.tv_B:
                mTask.setB(dealWithTvByStatus(mTask.isB(), tvB));
                break;
            case R.id.tv_C:
                mTask.setC(dealWithTvByStatus(mTask.isC(), tvC));
                break;
            case R.id.tv_D:
                mTask.setD(dealWithTvByStatus(mTask.isD(), tvD));
                break;
            case R.id.tv_E:
                mTask.setE(dealWithTvByStatus(mTask.isE(), tvE));
                break;

            case R.id.iv_cancel:
                dismiss();
                break;

            case R.id.btn_ok:
                if (mCallBack != null) mCallBack.infoChanged(this, mTask);
                dismiss();
                break;
        }
    }

    //处理UI返回处置结果
    private boolean dealWithTvByStatus(boolean isSelected, TextView tv) {
        if (isSelected) {
            unSelectView(tv);
            return false;
        } else {
            selectView(tv);
            return true;
        }
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

    void unSelectView(TextView tv) {
        tv.setBackgroundResource(R.drawable.item_unselected);
        tv.setTextColor(Color.parseColor("#888888"));
        TextPaint tp1 = tv.getPaint();
        tp1.setFakeBoldText(false);
    }

    public void setOnPowwinListener(PopwindowListener<PopupWindow, Task> callBack) {
        this.mCallBack = callBack;
    }

}