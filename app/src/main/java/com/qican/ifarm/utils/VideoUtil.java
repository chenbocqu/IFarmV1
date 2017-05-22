/**
 * 播放视频的工具
 */
package com.qican.ifarm.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.View;

import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.listener.OnSnapshotListener;
import com.qican.ifarm.listener.OnVideoPlayListener;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoUtil implements SurfaceHolder.Callback {
    private Context mContext;
    private View progressBar;
    private EZCamera mEZCamera;
    private EZPlayer ezPlayer;
    private SurfaceHolder svhRealPlay;
    private OnVideoPlayListener l;
    CommonTools myTool;

    public VideoUtil(Context context, EZCamera camera, SurfaceHolder surfaceHolder) {
        this(context, camera, surfaceHolder, null);
    }

    /**
     * 构造函数
     *
     * @param context       上下文
     * @param camera        相机实例
     * @param surfaceHolder 要播放的holder
     * @param progressBar   进度
     */
    public VideoUtil(Context context, EZCamera camera, SurfaceHolder surfaceHolder, View progressBar) {
        this.mContext = context;
        this.mEZCamera = camera;
        this.svhRealPlay = surfaceHolder;
        this.progressBar = progressBar;

        init();
    }

    private void init() {
        myTool = new CommonTools(mContext);
        svhRealPlay.addCallback(this);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
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
                myTool.showInfo("Token失效，请重新登录！");
                EZOpenSDK.getInstance().logout();
                EZOpenSDK.getInstance().openLoginPage();
                break;
            case ErrorCode.ERROR_STREAM_VTDU_STATUS_454:
                myTool.showInfo("设备通信异常,重新连接中···");
                stopRealPlay();
                startRealPlay();
                break;
            case ErrorCode.ERROR_STREAM_STREAM_VTM_VTDUINFO_REQ_TMOUT:
                myTool.showInfo("请求超时！");
                break;
            case ErrorCode.ERROR_WEB_PARAM_ERROR:
                myTool.showInfo("萤石账号失效，请登录！");
                EZOpenSDK.getInstance().openLoginPage();
                break;
            default:
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
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            if (l != null)
                l.onVideoStart();
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
            ezPlayer.release();
            if (l != null)
                l.onVideoStop();
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

    public void setProgressBar(View progressBar) {
        this.progressBar = progressBar;
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
}
