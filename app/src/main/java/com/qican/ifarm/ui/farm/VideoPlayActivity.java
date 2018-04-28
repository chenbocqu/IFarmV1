/**
 * 播放视频
 */
package com.qican.ifarm.ui.farm;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.utils.CommonTools;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.wang.avi.AVLoadingIndicatorView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.bingoogolapple.progressbar.BGAProgressBar;

@EActivity(R.layout.activity_videoplay)
public class VideoPlayActivity extends Activity implements SurfaceHolder.Callback {

    private CommonTools myTool;
    @ViewById
    SurfaceView svRealPlay;
    @ViewById
    ImageView ivStartPlay;
    @ViewById
    RelativeLayout rlBack;

    @ViewById
    AVLoadingIndicatorView avi;

    private EZPlayer ezPlayer;
    private EZCamera mEZCamera;
    private SurfaceHolder svhRealPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 页面设置为沉浸式状态栏风格
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRealPlay();
    }

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        initView();
        initData();
        initEvent();
        startRealPlay();
    }

    private void initEvent() {
        svhRealPlay.addCallback(this);
    }

    private void initView() {
        svhRealPlay = svRealPlay.getHolder();
    }

    private void initData() {
        // 获得上一页传过来的参数
        mEZCamera = (EZCamera) myTool.getParam(EZCamera.class);

//        if (mEZCamera.getDeviceSerial() == null) {
//            mEZCamera.setDeviceSerial("626439264");
//            mEZCamera.setChannelNo(1);
//            mEZCamera.setVerifyCode("SEGHDP");
//        }

        myTool.log("Camera Info:" + mEZCamera.toString());

        avi.setVisibility(View.GONE);
    }

    @Click
    void rlBack() {//返回键
        this.finish();
    }//退出

    //播放视频
    @Click
    void ivStartPlay() {
        myTool.log("开始播放【" + mEZCamera.getDeviceSerial() + "," + mEZCamera.getVerifyCode() + "】");
        startRealPlay();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            avi.setVisibility(View.GONE);
            switch (msg.what) {
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                    // handlePlaySuccess(msg);
                    myTool.showInfo("播放成功！");
                    myTool.log("handleMessage(成功): " + msg.toString());
                    avi.setVisibility(View.GONE);
                    break;
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
                    myTool.log("handleMessage(失败): msg.arg1[" + msg.arg1 + "],msg.arg2[" + msg.arg2 + "]");
                    handlePlayFail(msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    private void handlePlayFail(int errorCode, int msg) {
        ivStartPlay.setVisibility(View.VISIBLE);
        switch (errorCode) {
            case ErrorCode.ERROR_INNER_VERIFYCODE_NEED:
                // 需要验证码
                myTool.showInfo("需要输入验证码！");

                break;
            case ErrorCode.ERROR_INNER_VERIFYCODE_ERROR:
                myTool.showInfo("验证码错误！");
                // 验证码错误，输入验证码
//                vCodeDialog.show();
                break;
            case ErrorCode.ERROR_MP_ORDER_ERROR:
                myTool.showInfo("调用顺序不对头！");
                break;
            case ErrorCode.ERROR_TRANSF_ACCESSTOKEN_ERROR:
                myTool.showInfo("Token失效，请重新登录！");
                EZOpenSDK.getInstance().logout();
                EZOpenSDK.getInstance().openLoginPage();
                break;

            default:
                myTool.showInfo("相机不在线！");
        }
    }

    /**
     * 直播视频播放
     */
    private void startRealPlay() {
        if (mEZCamera != null) {
            if (ezPlayer == null) {
                //初始化EZPlayer
                ezPlayer = EZOpenSDK.getInstance().createPlayer(mEZCamera.getDeviceSerial(), mEZCamera.getChannelNo());
            }
            if (ezPlayer == null) {
                return;
            }
            ezPlayer.setHandler(mHandler);
            ezPlayer.setSurfaceHold(svhRealPlay);
            ezPlayer.setPlayVerifyCode(mEZCamera.getVerifyCode());

            ezPlayer.startRealPlay();
            ivStartPlay.setVisibility(View.GONE);
            avi.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 停止直播视频播放
     */
    private void stopRealPlay() {
        if (mEZCamera != null) {
            if (ezPlayer == null) {
                //初始化EZPlayer
                ezPlayer = EZOpenSDK.getInstance().createPlayer(mEZCamera.getDeviceSerial(), mEZCamera.getChannelNo());
            }
            if (ezPlayer == null) {
                return;
            }
            ezPlayer.stopRealPlay();
            ezPlayer.release();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (ezPlayer != null) {
            ezPlayer.setSurfaceHold(svhRealPlay);
        }
        svhRealPlay = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (ezPlayer != null)
            ezPlayer.stopRealPlay();
    }

    //显示退出菜单
    private void showBackMenu() {

        if (rlBack.getVisibility() == View.VISIBLE) {
            //重新开始计时
            hideBackTimer.cancel();
            hideBackTimer.start();
            return;
        }

        rlBack.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(rlBack);
        hideBackTimer.start();
    }

    private CountDownTimer hideBackTimer = new CountDownTimer(3000, 3000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            YoYo.with(Techniques.FadeOut)
                    .duration(1000)
                    .withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            rlBack.setVisibility(View.GONE);
                        }
                    })
                    .playOn(rlBack);
        }
    };

    @Click
    void rlVideoPlay() {
        showBackMenu();
    }
}
