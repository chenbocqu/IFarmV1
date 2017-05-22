/**
 * 萤石视频播放监听器
 */
package com.qican.ifarm.listener;

public interface OnVideoPlayListener {
    void onSuccess();

    void onFail(int errorCode);

    void onVideoStart();//开始播放

    void onVideoStop();//停止播放
}
