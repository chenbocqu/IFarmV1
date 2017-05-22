package com.qican.ifarm.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.view.MyCalendarDialog;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogUtils {
    public static MyCalendarDialog showChooseDateDialog(Activity context,
                                                        final String title,
                                                        final String okText,
                                                        final String cancelText,
                                                        final MyCalendarDialog.ClickCallBack clickCallBack,
                                                        final View view) {
        final MyCalendarDialog customerDialog = new MyCalendarDialog(context, R.layout.dialog_choosedate_layout);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        customerDialog.setDlgIfClick(true);
        customerDialog.setOnCustomerViewCreated(new MyCalendarDialog.CustomerViewInterface() {
            @Override
            public void getCustomerView(Window window, AlertDialog dlg) {
                TextView tv_title = (TextView) window.findViewById(R.id.title);
                Button left_button = (Button) window.findViewById(R.id.left_button);
                Button right_button = (Button) window.findViewById(R.id.right_button);
                final CalendarPickerView pickerView = (CalendarPickerView) window.findViewById(R.id.calendar_picker);
                Calendar lastYear = Calendar.getInstance();
                lastYear.add(Calendar.YEAR, -1);
                Calendar currentYear = Calendar.getInstance();
                currentYear.add(Calendar.YEAR, 0);
                Calendar nextYear = Calendar.getInstance();
                nextYear.add(Calendar.YEAR, 1);
                pickerView.init(lastYear.getTime(), nextYear.getTime()).withSelectedDate(new Date());
                if (!TextUtils.isEmpty(title)) {
                    tv_title.setText(title);
                } else {
                    tv_title.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(cancelText)) {
                    left_button.setText(cancelText);
                }
                left_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickCallBack.onCancel(customerDialog);
                    }
                });
                if (!TextUtils.isEmpty(okText)) {
                    right_button.setText(okText);
                }
                right_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickCallBack.onOk(customerDialog);
                        long time = pickerView.getSelectedDate().getTime();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String result = format.format(time);
                        ((TextView) view).setText(result);
                    }
                });
            }
        });

        customerDialog.showDlg();
        return customerDialog;
    }
}
