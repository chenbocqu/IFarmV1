package com.qican.ifarm.ui.control;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.utils.CommonTools;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_control_system)
public class ControlSysActivity extends Activity {
    @ViewById
    TextView tvTitle;

    @ViewById
    ImageView ivTank1, ivTank2, ivTank3;
    boolean isTank1On = false, isTank2On = false, isTank3On = false;

    CommonTools myTool;
    Bitmap tankOn, tankOff;

    ControlSystem system;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        tankOn = BitmapFactory.decodeResource(getResources(), R.drawable.tank_on);
        tankOff = BitmapFactory.decodeResource(getResources(), R.drawable.tank_off);
        system = (ControlSystem) myTool.getParam(ControlSystem.class);

        tvTitle.setText("主控系统");
    }


    @Click
    void llBack() {
        this.finish();
    }

    @Click
    void ivTank1() {
        if (isTank1On) {
            isTank1On = false;
            ivTank1.setImageBitmap(tankOff);
        } else {
            ivTank1.setImageBitmap(tankOn);
            isTank1On = true;
        }
    }

    @Click
    void ivTank2() {
        if (isTank2On) {
            ivTank2.setImageBitmap(tankOff);
            isTank2On = false;
        } else {
            ivTank2.setImageBitmap(tankOn);
            isTank2On = true;
        }
    }

    @Click
    void ivTank3() {
        if (isTank3On) {
            ivTank3.setImageBitmap(tankOff);
            isTank3On = false;
        } else {
            ivTank3.setImageBitmap(tankOn);
            isTank3On = true;
        }
    }
}
