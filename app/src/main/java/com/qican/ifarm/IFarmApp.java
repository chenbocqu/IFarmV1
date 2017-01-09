package com.qican.ifarm;

import android.app.Application;

import com.hyphenate.easeui.controller.EaseUI;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/12/21 0021.
 */
public class IFarmApp extends Application {
    private static Application mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        EaseUI.getInstance().init(this, null);
        SMSSDK.initSDK(this, "1a77f1ffd7152", "b962941f4b300b9799231137d0cca6b9");//短信验证
    }

    public static IFarmApp getApp() {
        return (IFarmApp) mApp;
    }
}
