package com.qican.ifarm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

import com.qican.ifarm.IFarmApp;
import com.qican.ifarm.R;

public interface ConstantValue {
    public static Context mContext = IFarmApp.getApp().getBaseContext();

    String SERVICE_PHONE = "1008611";//客服电话
    String GAODU_APPKEY = "ad2bab0dae5fa7f603079e8313af618c";

    String KEY_ISLOGIN = "KEY_ISLOGIN";
    String KEY_USERNAME = "KEY_USERNAME";
    String KEY_HEADURL = "KEY_HEADURL";
    String KEY_NICKNAME = "KEY_NICKNAME";
    String KEY_AUTOGRAPH = "KEY_AUTOGRAPH";
    String KEY_SEX = "KEY_SEX";
    String KEY_POND_LIST = "KEY_POND_LIST";//池塘列表
    String KEY_CAMERA_LIST = "KEY_CAMERA_LIST";//相机列表
    String KEY_FIRSTIN = "KEY_FIRSTIN";

    //IP地址和端口号
    String IP = "172.20.33.237";
//    String IP = "47.93.91.45";
    String PORT = "8080";
    String SERVICE_ADDRESS = "http://" + IP + ":" + PORT + "//IFarm/";

    //URL，从萤石请求摄像机列表
    String URL_EZ_CAMERA_LIST = "https://open.ys7.com/api/lapp/camera/list";

    @DrawableRes
    public static final int[] iconResFunOn = new int[]{//控制功能图标资源
            R.drawable.fun_irrigate,
            R.drawable.fun_air,
            R.drawable.fun_illuminate,
            R.drawable.fun_temperature,
            R.drawable.fun_humidity,
            R.drawable.fun_co2,
            R.drawable.fun_o2,
            R.drawable.fun_sunshade,
            R.drawable.fun_add};

    final Bitmap[] iconFunOn = new Bitmap[]{
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_irrigate),
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_air),
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_illuminate),
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_temperature),
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_humidity),
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_co2),
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_o2),
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_sunshade),
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fun_add)
    };
    String UPLOAD_HEADIMG = "0";
    String UPLOAD_BGIMG = "1";

    String KEY_TOKEN = "KEY_TOKEM";
    String KEY_ISEMCOUNTEXIST = "KEY_ISEMCOUNTEXIST";
    String KEY_COMUSERINFO = "INFO_";//普通用户信息
    String LABEL_SPLIT = ",";
    String TOKEN_LOSE = "lose efficacy";
}
