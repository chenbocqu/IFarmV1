package com.qican.ifarm.ui.camera;

import android.app.Activity;
import android.widget.EditText;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.ui.farm.VideoPlayActivity_;
import com.qican.ifarm.utils.CommonTools;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_camera_test)
public class CameraTestActivity extends Activity {
    private CommonTools myTool;

    @ViewById
    EditText edtSeries, edtPath, edtVericode;
    private String ser, path, vericode;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
    }


    @Click
    void openCamera() {
        ser = edtSeries.getText().toString().trim();
        path = edtPath.getText().toString().trim();
        vericode = edtVericode.getText().toString().trim();

        if ("".equals(ser)) {
            myTool.showInfo("请输入序列号！");
            return;
        }
        if ("".equals(path)) {
            myTool.showInfo("请输入通道号！");
            return;
        }
        if ("".equals(vericode)) {
            myTool.showInfo("请输入验证码！");
            return;
        }
        EZCamera camera = new EZCamera();
        camera.setDeviceSerial(ser);
        camera.setChannelNo(Integer.parseInt(path));
        camera.setVerifyCode(vericode);

        myTool.startActivity(camera, VideoPlayActivity_.class);
    }

    @Click
    void llBack() {
        this.finish();
    }

    @Click
    void llTest() {
        EZCamera camera = new EZCamera();
        camera.setDeviceSerial("626439264");
        camera.setChannelNo(1);
        camera.setVerifyCode("SEGHDP");

        myTool.startActivity(camera, VideoPlayActivity_.class);
    }
}
