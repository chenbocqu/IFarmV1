package com.ui.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.squareup.timessquare.CalendarPickerView;

public class MainActivity extends AppCompatActivity {
    private EditText et_date;
    private CalendarPickerView pickerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_date = (EditText) findViewById(R.id.et_date);
        et_date.setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.showChooseDateDialog(MainActivity.this, "请选择日期", "确定", "取消", new CustomerDialog.ClickCallBack() {
                @Override
                public void onOk(CustomerDialog dlg) {
                    dlg.dismissDlg();
                }
                @Override
                public void onCancel(CustomerDialog dlg) {
                    dlg.dismissDlg();
                }
            },et_date);
        }
    };
}
