/**
 * @author 陈波
 * @time 2016-12-21
 */
package com.qican.ifarm;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.hyphenate.easeui.controller.EaseUI;
import com.videogo.openapi.EZOpenSDK;

import java.io.File;

import cn.smssdk.SMSSDK;

public class IFarmApp extends Application {
    private static Application mApp;
    //萤石APPKEY
    public static final String EZ_appKey = "7c6d2cd139684bb896c0011e55052ef7";
    //so库存放位置
    public static final String loadLibraryAbsPath = Environment.getExternalStorageDirectory() + "/"
            + "IFarm/libs/";

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        SMSSDK.initSDK(IFarmApp.this, "1a77f1ffd7152", "b962941f4b300b9799231137d0cca6b9");//短信验证
        EaseUI.getInstance().init(IFarmApp.this, null);
        //初始化萤石云SDK
        new Thread() {
            @Override
            public void run() {
                super.run();
                initEZSDK();
            }
        }.start();
    }

    //放到线程中优化启动
    private void initEZSDK() {
        File dirFile = new File(loadLibraryAbsPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        Log.i("IFarm_APP", "EZSDK 版本: " + EZOpenSDK.getVersion());
        /**
         * sdk日志开关，正式发布需要去掉
         */
        EZOpenSDK.showSDKLog(true);

        /**
         * 设置是否支持P2P取流,详见api
         */
        EZOpenSDK.enableP2P(false);

        boolean success = false;
        success = EZOpenSDK.initLib(IFarmApp.this, EZ_appKey, "");

        if (!success) {
            Log.i("IFarm", "EZOpenSDK初始化失败,请联系开发者,qq:1061315674！");
        } else {
            Log.i("IFarm", "EZSDK初始化成功！");
        }
    }

    public static IFarmApp getApp() {
        return (IFarmApp) mApp;
    }
}
