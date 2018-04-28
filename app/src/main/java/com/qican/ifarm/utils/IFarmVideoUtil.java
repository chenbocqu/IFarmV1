/**
 * 播放视频的工具,需要在主布局中 include videoutil_layout.xml 文件
 */
package com.qican.ifarm.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.listener.OnSnapshotListener;
import com.qican.ifarm.listener.OnVideoPlayListener;
import com.qican.ifarm.ui.camera.VideoFullscreenActivity_;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.videogo.exception.ErrorCode.ERROR_STREAM_ERR;
import static com.videogo.exception.ErrorCode.ERROR_STREAM_VTDU_STATUS_405;

public class IFarmVideoUtil implements SurfaceHolder.Callback, View.OnClickListener {

    interface VideoListener {
        void back();
    }

    enum InfoType {
        // 需要验证码，验证码错误，TOKEN错误重新登录,超时，账号失效重新登录
        VERIFYCODE_NEED, VERIFYCODE_ERROR, ACCESSTOKEN_ERROR, REQ_TMOUT, PARAM_ERROR, OFF_LINE
    }

    InfoType type;

    VideoListener mCallback;
    View mainView, rlBack, rlBottomBar, rlInfo;
    private Context mContext;
    private View progressBar;
    private EZCamera mEZCamera;
    private EZPlayer ezPlayer;
    SurfaceView mSurfaceView;
    TextView tvTitle, tvInfo, tvOperate;
    private SurfaceHolder svhRealPlay;
    private OnVideoPlayListener l;
    CommonTools myTool;
    ImageView ivVideoPlay, ivFullScreen;
    boolean isFullScreen = false;
    boolean isVideoPlay = false;
    Bitmap bmpStartPlay, bmpStopPlay, bmpRestore;

    /**
     * Video Util构造函数
     *
     * @param mContext  上下文环境
     * @param mEZCamera 相机参数
     */
    public IFarmVideoUtil(Context mContext, EZCamera mEZCamera) {
        this(mContext, mEZCamera, false);
    }

    public IFarmVideoUtil(Context mContext, EZCamera mEZCamera, boolean isFullScreen) {
        this.mContext = mContext;
        this.mEZCamera = mEZCamera;
        this.isFullScreen = isFullScreen;

        init();
    }

    public void logCameraInfo() {
        myTool.log(
                "deviceSerial:" + mEZCamera.getDeviceSerial() + "\n" +
                        "channelNo:" + mEZCamera.getChannelNo() + "\n" +
                        "verifyCode:" + mEZCamera.getVerifyCode() + "\n" +
                        "channelName:" + mEZCamera.getChannelName()

        );
    }

    private void init() {
        myTool = new CommonTools(mContext);

        initView();
        initData();
        initEvent();
    }

    private void initData() {

        type = InfoType.PARAM_ERROR;

        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        rlBack.setVisibility(View.GONE);
        rlBottomBar.setVisibility(View.GONE);
        rlInfo.setVisibility(View.GONE);

        bmpStartPlay = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.video_start);
        bmpStopPlay = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.video_stop);
        bmpRestore = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.video_restore);

        if (isFullScreen) ivFullScreen.setImageBitmap(bmpRestore);
        if (mEZCamera.getChannelName() != null) tvTitle.setText(mEZCamera.getChannelName());

        hideMenuTimer.start();
    }

    private void initEvent() {
        mainView.setOnClickListener(this);
        svhRealPlay.addCallback(this);
        rlBack.setOnClickListener(this);
        ivVideoPlay.setOnClickListener(this);
        ivFullScreen.setOnClickListener(this);
        tvOperate.setOnClickListener(this);
    }

    private void initView() {
        mainView = ((Activity) (mContext)).findViewById(R.id.rl_video_root);

        mSurfaceView = (SurfaceView) mainView.findViewById(R.id.svRealPlay);
        svhRealPlay = mSurfaceView.getHolder();
        progressBar = mainView.findViewById(R.id.avi_progressbar);
        tvTitle = (TextView) mainView.findViewById(R.id.tv_title);
        rlBack = mainView.findViewById(R.id.rlBack);

        rlInfo = mainView.findViewById(R.id.rlInfo);
        tvInfo = (TextView) mainView.findViewById(R.id.tv_info);
        tvOperate = (TextView) mainView.findViewById(R.id.tv_operate);

        ivVideoPlay = (ImageView) mainView.findViewById(R.id.iv_video_play);
        ivFullScreen = (ImageView) mainView.findViewById(R.id.iv_full_srceen);

        rlBottomBar = mainView.findViewById(R.id.rlBottomBar);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            switch (msg.what) {
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                    myTool.showInfo("播放成功！");
                    myTool.log("handleMessage(成功): " + msg.toString());
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    if (l != null) l.onSuccess();
                    isVideoPlay = true;

                    break;
                case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL:
                    myTool.log("handleMessage(失败): msg.arg1[" + msg.arg1 + "],msg.arg2[" + msg.arg2 + "]");
                    handlePlayFail(msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    private void handlePlayFail(int errorCode, int msg) {
        if (l != null) l.onFail(errorCode);
        isVideoPlay = false;
        ivVideoPlay.setImageBitmap(bmpStartPlay);
        switch (errorCode) {
            case ErrorCode.ERROR_INNER_VERIFYCODE_NEED:
                // 需要验证码
                myTool.showInfo("需要输入验证码！");
                break;
            case ErrorCode.ERROR_INNER_VERIFYCODE_ERROR:
                myTool.showInfo("验证码错误！");
                break;
            case ErrorCode.ERROR_MP_ORDER_ERROR:
                myTool.showInfo("调用顺序不对！");
                break;
            case ErrorCode.ERROR_TRANSF_ACCESSTOKEN_ERROR:
//                myTool.showInfo("Token失效，请重新登录！");
                showInfo("Token失效，请重新登录！", InfoType.ACCESSTOKEN_ERROR);
                EZOpenSDK.getInstance().logout();
//                EZOpenSDK.getInstance().openLoginPage();
                break;
            case ErrorCode.ERROR_STREAM_VTDU_STATUS_454:
                myTool.showInfo("设备通信异常,重新连接中···");
                stopRealPlay();
                startRealPlay();
                break;
            case ErrorCode.ERROR_STREAM_STREAM_VTM_VTDUINFO_REQ_TMOUT:
//                myTool.showInfo("请求超时！");
                showInfo("请求超时，请稍后重试！", InfoType.REQ_TMOUT);
                break;
            case ErrorCode.ERROR_WEB_PARAM_ERROR:
                showInfo("萤石账号失效，请登录！", InfoType.PARAM_ERROR);
//                myTool.showInfo("萤石账号失效，请登录！");
//                EZOpenSDK.getInstance().openLoginPage();
                break;
            case ERROR_STREAM_VTDU_STATUS_405:
                myTool.showInfo("网络信号弱！");
                break;
            case ERROR_STREAM_ERR:
                myTool.log("通用错误");
                break;
            default:
                showInfo("相机不在线，请稍后再试！", InfoType.OFF_LINE);
                myTool.showInfo("相机不在线！");
        }
    }

    /**
     * 直播视频播放
     */
    public void startRealPlay() {
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
            rlInfo.setVisibility(View.GONE);
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            if (l != null)
                l.onVideoStart();
            ivVideoPlay.setImageBitmap(bmpStopPlay);
        }
    }

    /**
     * 停止直播视频播放
     */
    public void stopRealPlay() {
        if (mEZCamera != null) {
            if (ezPlayer == null) {
                //初始化EZPlayer
                ezPlayer = EZOpenSDK.getInstance().createPlayer(mEZCamera.getDeviceSerial(), mEZCamera.getChannelNo());
            }
            if (ezPlayer == null) {
                return;
            }
            ezPlayer.stopRealPlay();
//            ezPlayer.release();
            if (l != null)
                l.onVideoStop();

            isVideoPlay = false;
            ivVideoPlay.setImageBitmap(bmpStartPlay);
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

    public void setOnVideoPlayListener(OnVideoPlayListener l) {
        this.l = l;
    }

    /**
     * 截图
     */
    public void snapshot() {
        snapshot(null);
    }

    /**
     * 截图，结果以回调形式返回
     *
     * @param l 装载结果的回调接口
     */
    public void snapshot(final OnSnapshotListener l) {
        Thread snapshotThread = new Thread() {
            @Override
            public void run() {
                Bitmap bmp = ezPlayer.capturePicture();

                if (l != null)
                    l.onSuccess(bmp);

                FileOutputStream fos = null;
                if (bmp != null) {
                    try {
                        String path = CommonTools.USER_FILE_PATH + "/" + myTool.getUserId() + "/video_snapshots[" + DataBindUtils.getCurrentTime() + "].jpg";
                        fos = new FileOutputStream(path);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        bmp.recycle();
                        bmp = null;

                        myTool.log("截图以存放至" + path);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                super.run();
            }
        };
        snapshotThread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_video_root:
                showMenu();
                break;

            case R.id.rlBack:
                if (mCallback != null) mCallback.back();
                ((Activity) (mContext)).finish();
                break;

            case R.id.iv_video_play:
                if (isVideoPlay) stopRealPlay();
                else startRealPlay();

                break;

            case R.id.iv_full_srceen:
                stopRealPlay();
                if (isFullScreen) ((Activity) (mContext)).finish();
                else myTool.startActivity(mEZCamera, VideoFullscreenActivity_.class);
                break;

            case R.id.tv_operate:
                handleInfoBtn();
                break;
        }
    }

    private void handleInfoBtn() {
        switch (type) {
            case ACCESSTOKEN_ERROR:
            case PARAM_ERROR:
                EZOpenSDK.getInstance().openLoginPage();
                break;
            case OFF_LINE:
            case REQ_TMOUT:
                startRealPlay();
                break;
        }
    }

    public void setOnVideoListener(VideoListener l) {
        this.mCallback = l;
    }

    public void setTitle(String title) {
        if (tvTitle != null && title != null) tvTitle.setText(title);
    }

    //显示退出菜单
    private void showMenu() {

        if (rlBottomBar.getVisibility() == View.VISIBLE) {
            //重新开始计时
            hideMenuTimer.cancel();
            hideMenuTimer.start();
            return;
        }

        rlBottomBar.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(rlBottomBar);

        if (isFullScreen) {
            rlBack.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn)
                    .duration(1000)
                    .playOn(rlBack);
        }

        hideMenuTimer.start();
    }

    private CountDownTimer hideMenuTimer = new CountDownTimer(3000, 3000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (isFullScreen) {
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

            YoYo.with(Techniques.FadeOut)
                    .duration(1000)
                    .withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            rlBottomBar.setVisibility(View.GONE);
                        }
                    })
                    .playOn(rlBottomBar);
        }
    };

    // 处理，显示信息
    void showInfo(String info, InfoType type) {
        rlInfo.setVisibility(View.VISIBLE);
        tvInfo.setText(info);
        this.type = type;

        switch (type) {

            case ACCESSTOKEN_ERROR:
            case PARAM_ERROR:
                tvOperate.setText("登录萤石账号");
                break;

            case VERIFYCODE_ERROR:
            case VERIFYCODE_NEED:
                tvOperate.setText("设置验证码");
                break;

            case OFF_LINE:
            case REQ_TMOUT:
                tvOperate.setText("重试");
                break;
        }

    }
}
