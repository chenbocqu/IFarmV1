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


public class DialogForSelectType extends PopupWindow implements OnClickListener {

    OnInfoListener<Task> l;

    private Task mTask = null;//单价

    private TextView tvTank1, tvTank2, tvTank3;
    private ImageView ivCancel;

    private Button btnOK;
    private View mMenuView;

    public DialogForSelectType(Activity context, Task task) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_select_type, null);

        initView(mMenuView);
        initData(task);

        initEvent();
        seWindowStyle();
    }

    private void initData(Task task) {
        mTask = task;
        if (mTask == null) mTask = new Task();
        if (mTask.isTank1()) {
            selectView(tvTank1);
        }
        if (mTask.isTank2()) {
            selectView(tvTank2);
        }
        if (mTask.isTank3()) {
            selectView(tvTank3);
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

        tvTank1.setOnClickListener(this);
        tvTank2.setOnClickListener(this);
        tvTank3.setOnClickListener(this);
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

        tvTank1 = (TextView) view.findViewById(R.id.tv_tank1);
        tvTank2 = (TextView) view.findViewById(R.id.tv_tank2);
        tvTank3 = (TextView) view.findViewById(R.id.tv_tank3);

        ivCancel = (ImageView) view.findViewById(R.id.iv_cancel);//取消按钮
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tank1:
                mTask.setTank1(dealWithTvByStatus(mTask.isTank1(), tvTank1));
                break;
            case R.id.tv_tank2:
                mTask.setTank2(dealWithTvByStatus(mTask.isTank2(), tvTank2));
                break;
            case R.id.tv_tank3:
                mTask.setTank3(dealWithTvByStatus(mTask.isTank3(), tvTank3));
                break;
            case R.id.iv_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                if (l != null) l.onInfoChanged(mTask);
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

    public void setOnInfoChangeListener(OnInfoListener<Task> l) {
        this.l = l;
    }
}