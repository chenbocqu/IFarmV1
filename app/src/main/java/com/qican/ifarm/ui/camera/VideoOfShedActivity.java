/**
 * 大棚的视屏
 */
package com.qican.ifarm.ui.camera;

import android.app.Activity;
import android.view.SurfaceView;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.bean.IFarmCamera;
import com.qican.ifarm.ui.control.SelectSystemActivity;
import com.qican.ifarm.ui.subarea.SubareaActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.IFarmVideoUtil;
import com.qican.ifarm.utils.SubareaDataUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_video_of_shed)
public class VideoOfShedActivity extends Activity {

    private CommonTools myTool;

    @ViewById(R.id.avi_video)
    AVLoadingIndicatorView aviVideo;

    @ViewById
    SurfaceView svRealPlay;

    @ViewById(R.id.tv_title)
    TextView tvTitle;

    @ViewById
    TextView tvTemperature, tvHumidity, tvIlluminate;

    EZCamera mEZCamera;
    IFarmCamera iFarmCamera;

    IFarmVideoUtil iFarmVideoUtil;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        initView();
    }

    private void initView() {
    }

    private void initData() {

        iFarmCamera = (IFarmCamera) myTool.getParam(IFarmCamera.class);

        if (iFarmCamera == null) {
            myTool.log("camera 为空！！");
            return;
        }
        tvTitle.setText(iFarmCamera.getShedNo() + "号棚");

        mEZCamera = iFarmCamera;
        myTool.log("大棚视频监控相机信息" +
                "：序列号，" + mEZCamera.getDeviceSerial() +
                "；通道号，" + mEZCamera.getChannelNo() +
                "；验证码，" + mEZCamera.getVerifyCode() + "。");

        if (mEZCamera.getDeviceSerial() == null) {
            mEZCamera.setDeviceSerial("761008117");
            mEZCamera.setChannelNo(1);
            mEZCamera.setVerifyCode("PGGSWF");
        }

        iFarmVideoUtil = new IFarmVideoUtil(this, mEZCamera);
        iFarmVideoUtil.startRealPlay();

        // 视频上的实时数据温湿度、光照
        SubareaDataUtil.parseSubareaData(iFarmCamera.getSubarea(), new SubareaDataUtil.realDataListener() {
            @Override
            public void onParseFinshed(String temperature, String humidity, String illuminate) {
                if (temperature != null) tvTemperature.setText(temperature + " ℃");
                if (humidity != null) tvHumidity.setText(humidity + " %");
                if (illuminate != null) tvIlluminate.setText(illuminate + " LUX");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Click
    void llBack() {
        this.finish();
    }

    @Click
    void llData() {
        //实时数据
        myTool.startActivity(iFarmCamera.getSubarea(), SubareaActivity.class);
    }

    @Click
    void llWatering() {
        myTool.showInfo("Watering```");
        myTool.startActivity(new ControlFunction("灌溉"), SelectSystemActivity.class);
    }

    @Click
    void llFertilize() {
        myTool.showInfo("Fertilize```");
        myTool.startActivity(new ControlFunction("施肥"), SelectSystemActivity.class);
    }

    @Click
    void llDrug() {
        myTool.showInfo("Drug```");
        myTool.startActivity(new ControlFunction("施药"), SelectSystemActivity.class);
    }

    @Click
    void llIlluminate() {
        myTool.showInfo("Illuminate```");
        myTool.startActivity(new ControlFunction("光照"), SelectSystemActivity.class);
    }

    @Click
    void llAir() {
        myTool.showInfo("Air```");
        myTool.startActivity(new ControlFunction("通风"), SelectSystemActivity.class);
    }

    @Click
    void llScreenshot() {
        //截屏
        iFarmVideoUtil.snapshot();
    }

    @Click
    void llMore() {
        myTool.showInfo("More```");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iFarmVideoUtil.stopRealPlay();
    }
}
