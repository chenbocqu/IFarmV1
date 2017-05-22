/**
 * 大棚的视屏
 */
package com.qican.ifarm.ui.camera;

import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.bean.IFarmCamera;
import com.qican.ifarm.listener.OnVideoPlayListener;
import com.qican.ifarm.ui.subarea.SubareaActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.SubareaDataUtil;
import com.qican.ifarm.utils.VideoUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_video_of_shed)
public class VideoOfShedActivity extends Activity implements OnVideoPlayListener {

    private CommonTools myTool;

    @ViewById(R.id.avi_video)
    AVLoadingIndicatorView aviVideo;

    @ViewById
    SurfaceView svRealPlay;

    @ViewById(R.id.tv_title)
    TextView tvTitle;

    @ViewById
    TextView tvTemperature, tvHumidity, tvIlluminate;

    @ViewById
    ImageView ivStartPlay;

    EZCamera mEZCamera;
    IFarmCamera iFarmCamera;

    private SurfaceHolder surfaceHolder;
    VideoUtil videoUtil;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        initView();
    }

    private void initView() {
        surfaceHolder = svRealPlay.getHolder();
    }

    private void initData() {

        iFarmCamera = (IFarmCamera) myTool.getParam(IFarmCamera.class);
        if (iFarmCamera == null) {
            myTool.log("camera 为空！！");
            return;
        }
        tvTitle.setText(iFarmCamera.getShedNo() + "号棚");

        mEZCamera = new EZCamera();
        if (mEZCamera.getDeviceSerial() == null) {
            mEZCamera.setDeviceSerial("626439264");
            mEZCamera.setChannelNo(1);
            mEZCamera.setVerifyCode("SEGHDP");
        }

        videoUtil = new VideoUtil(this, iFarmCamera, surfaceHolder, aviVideo);
        ivStartPlay.setVisibility(View.GONE);
        videoUtil.setOnVideoPlayListener(this);
        videoUtil.startRealPlay();

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

    @Override
    public void onSuccess() {
        ivStartPlay.setVisibility(View.GONE);
    }

    @Override
    public void onFail(int errorCode) {
        ivStartPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoStart() {
        ivStartPlay.setVisibility(View.GONE);
    }

    @Override
    public void onVideoStop() {
    }

    @Click
    void llData() {
        //实时数据
        myTool.startActivity(iFarmCamera.getSubarea(), SubareaActivity.class);
    }

    @Click
    void llWatering() {
        myTool.showInfo("Watering```");
    }

    @Click
    void llFertilize() {
        myTool.showInfo("Fertilize```");
    }

    @Click
    void llScreenshot() {
        //截屏
        videoUtil.snapshot();
    }

    @Click
    void llMore() {
        myTool.showInfo("More```");
    }

}
