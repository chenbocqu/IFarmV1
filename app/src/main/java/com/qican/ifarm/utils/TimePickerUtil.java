package com.qican.ifarm.utils;

import android.content.Context;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.qican.ifarm.bean.OperationArea;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimePickerUtil {

    public static void pickTimeDialog(Context context, TimePickerView.OnTimeSelectListener l) {
        pickTimeDialog(context, "时间选择", null, l);
    }

    public static void pickTimeDialog(Context context, String title, Date date, TimePickerView.OnTimeSelectListener l) {

        Calendar selectedDate = Calendar.getInstance();

        if (date != null)
            selectedDate.setTime(date);

        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) - 5); // 5个月前
        startDate.set(Calendar.DAY_OF_MONTH, 1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.getInstance().get(Calendar.YEAR) + 1, 1, 1);

        TimePickerView pvTime = new TimePickerView.Builder(context, l)
                .setType(TimePickerView.Type.MONTH_DAY_HOUR_MIN)//月日时分
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText(title)//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(0xff636363)//标题文字颜色
                .setSubmitColor(0xff00abff)//确定按钮文字颜色
                .setCancelColor(0xff636363)//取消按钮文字颜色
                .setTitleBgColor(0xffe7e7e7)//标题背景颜色 Night mode
                .setBgColor(0xffffffff)//滚轮背景颜色 Night mode
//                .setRange(Calendar.getInstance().get(Calendar.YEAR) - 2, Calendar.getInstance().get(Calendar.YEAR) + 2)//默认是1900-2100年
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();

        pvTime.show();
    }


    // 选择持续时间
    public static void pickLastTime(Context context, String title, Date date, TimePickerView.OnTimeSelectListener l) {

        Calendar selectedDate = Calendar.getInstance();

        if (date != null)
            selectedDate.set(0, 0, 0, date.getHours(), date.getMinutes(), date.getSeconds());
        else
            selectedDate.set(0, 0, 0, 0, 5, 0);

        TimePickerView pvTime = new TimePickerView.Builder(context, l)
//                .setType(TimePickerView.Type.MONTH_DAY_HOUR_MIN)
                .setType(TimePickerView.Type.HOURS_MINS)//月日时分
//                .setType(TimePickerView.Type.ALL)//月日时分
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText(title)//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(0xff636363)//标题文字颜色
                .setSubmitColor(0xff00abff)//确定按钮文字颜色
                .setCancelColor(0xff636363)//取消按钮文字颜色
                .setTitleBgColor(0xffe7e7e7)//标题背景颜色 Night mode
                .setBgColor(0xffffffff)//滚轮背景颜色 Night mode
//                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setLabel("年", "月", "日", "小时", "分钟", "秒")
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    public static void pickOperationArea(Context context, List<OperationArea> options1Items, OptionsPickerView.OnOptionsSelectListener l) {

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(context, l)
                .setSubmitText("确定")//确定按钮文字
                .setCancelText("取消")//取消按钮文字
                .setTitleText("区域选择")//标题
                .setSubCalSize(18)//确定和取消文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleColor(0xff636363)//标题文字颜色
                .setSubmitColor(0xff00abff)//确定按钮文字颜色
                .setCancelColor(0xff636363)//取消按钮文字颜色
                .setTitleBgColor(0xffe7e7e7)//标题背景颜色 Night mode
                .setBgColor(0xffffffff)//滚轮背景颜色 Night mode
                .setContentTextSize(18)//滚轮文字大小
                .setLinkage(false)//设置是否联动，默认true
                .setLabels("区", "市", "区")//设置选择的三级单位
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setCyclic(true, false, false)//循环与否
                .setSelectOptions(1, 1, 1)  //设置默认选中项
                .setOutSideCancelable(true)//点击外部dismiss default true
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvOptions.setPicker(options1Items);//添加数据源
        pvOptions.show();
    }
}
