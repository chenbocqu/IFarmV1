package com.qican.ifarm.ui.camera;

import android.app.Activity;
import android.view.Window;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.IFarmVideoUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.WindowFeature;

@EActivity(R.layout.activity_video_fullscreen)
@Fullscreen
@WindowFeature(Window.FEATURE_NO_TITLE)
public class VideoFullscreenActivity extends Activity {
    IFarmVideoUtil util;
    EZCamera mEZCamera;
    CommonTools myTool;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        // 获得上一页传过来的参数
        mEZCamera = (EZCamera) myTool.getParam(EZCamera.class);

        if (mEZCamera == null || mEZCamera.getDeviceSerial() == null) {
            mEZCamera = new EZCamera();
            mEZCamera.setDeviceSerial("626439264");
            mEZCamera.setChannelNo(1);
            mEZCamera.setVerifyCode("SEGHDP");
        }

        util = new IFarmVideoUtil(this, mEZCamera, true);
        util.logCameraInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        util.startRealPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        util.stopRealPlay();
    }
}
