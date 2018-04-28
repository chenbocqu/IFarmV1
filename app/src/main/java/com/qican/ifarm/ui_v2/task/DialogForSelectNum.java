package com.qican.ifarm.ui_v2.task;

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


public class DialogForSelectNum extends PopupWindow implements OnClickListener {

    NumListener mCallBack;

    interface NumListener {
        void numChanged(int winId, String info);
    }

    private Task mTask = null;//单价

    private TextView tvA, tvB, tvC, tvD, tvE, tv6, tvTitle;
    private ImageView ivCancel;

    private Button btnOK;
    private View mMenuView;
    String title;
    int winId;

    boolean is1, is2, is3, is4, is5, is6;

    public DialogForSelectNum(Activity context, String title, int id) {
        super(context);
        this.title = title;
        winId = id;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_select_num, null);

        initView(mMenuView);

        initEvent();
        seWindowStyle();
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
        tv6.setOnClickListener(this);
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
        tv6 = (TextView) view.findViewById(R.id.tv_6);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        ivCancel = (ImageView) view.findViewById(R.id.iv_cancel);//取消按钮

        tvTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_A:
                is1 = dealWithTvByStatus(is1, tvA);
                break;
            case R.id.tv_B:
                is2 = dealWithTvByStatus(is2, tvB);
                break;
            case R.id.tv_C:
                is3 = dealWithTvByStatus(is3, tvC);
                break;
            case R.id.tv_D:
                is4 = dealWithTvByStatus(is4, tvD);
                break;
            case R.id.tv_E:
                is5 = dealWithTvByStatus(is5, tvE);
                break;
            case R.id.tv_6:
                is6 = dealWithTvByStatus(is6, tv6);
                break;

            case R.id.iv_cancel:
                dismiss();
                break;

            case R.id.btn_ok:
                toOk();
                break;
        }
    }

    private void toOk() {
        String ret = "";

        ret = addArea(ret, is1, "1");
        ret = addArea(ret, is2, "2");
        ret = addArea(ret, is3, "3");
        ret = addArea(ret, is4, "4");
        ret = addArea(ret, is5, "5");
        ret = addArea(ret, is6, "6");

        if (mCallBack != null) mCallBack.numChanged(winId, ret);
        dismiss();
    }

    private String addArea(String ret, boolean selected, String s) {
        if (selected) {
            if ("".equals(ret))
                ret = s;
            else
                ret = ret + "," + s;
        }
        return ret;
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

    public void setOnPowwinListener(NumListener callBack) {
        this.mCallBack = callBack;
    }

}