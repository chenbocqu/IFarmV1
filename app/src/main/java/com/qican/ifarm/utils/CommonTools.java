/**
 * 通用工具类
 *
 * @时间：2016-7-6
 */
package com.qican.ifarm.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.listener.LoggingListener;
import com.qican.ifarm.ui.login.LoginActivity;
import com.zhy.base.cache.disk.DiskLruCacheHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class CommonTools {

    //    private final static String ALBUM_PATH
    //    = Environment.getExternalStorageDirectory() + "/download_test/";
    public static final String PACKAGE_NAME = "com.qican.ygj";

    public static final String USER_FILE_PATH = Environment.getExternalStorageDirectory() + "/"
            + PACKAGE_NAME; //用户文件

    public static final String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/"
            + PACKAGE_NAME;

    private Context mContext;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private CropHelper mCropHelper;
    private DiskLruCacheHelper cacheHelper = null;

    public CommonTools(Context context) {
        this.mContext = context;

        sp = mContext.getSharedPreferences("userinfo", mContext.MODE_PRIVATE);
        editor = sp.edit();

        try {
            cacheHelper = new DiskLruCacheHelper(mContext);

        } catch (IOException e) {
            showExceptionInfo(e.toString());
        }
    }

    /**
     * Toast消息
     *
     * @param msg
     * @param duration
     */
    public void showInfo(String msg, int duration) {
        Toast.makeText(mContext, msg, duration).show();
    }

    public void showInfo(String msg) {
        showInfo(msg, Toast.LENGTH_SHORT);
    }

    /**
     * 从网络平滑加载图片到指定ImageView上
     *
     * @param url
     * @param imageView
     */
    public CommonTools showImage(String url, ImageView imageView) {
//        Picasso.with(mContext).load(url).into(imageView);
        Glide.with(mContext).load(url).error(R.drawable.tab_bg)
                .listener(new LoggingListener<String, GlideDrawable>())
                .centerCrop().crossFade().into(imageView);
//        Glide.with(mContext).load(url).placeholder(R.drawable.img_loading).error(R.drawable.img_error)
//                .listener(new LoggingListener<String, GlideDrawable>()).centerCrop().crossFade().into(imageView);
        return this;
    }

    public CommonTools showImage(String url, ImageView imageView, @DrawableRes int errRes) {
        Glide.with(mContext)
                .load(url)
                .listener(new LoggingListener<String, GlideDrawable>())
                .centerCrop().error(errRes)
                .crossFade().into(imageView);
        return this;
    }

    public CommonTools showImageByOkHttp(String url, final ImageView imageView) {
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showInfo("异常：" + e.toString());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
        return this;
    }

    /**
     * 从网络平滑加载图片到指定ImageView上
     *
     * @param url
     * @param imageView
     */
    public CommonTools showImageWithoutCrop(String url, ImageView imageView) {
        Glide.with(mContext).load(url).error(R.drawable.img_error)
                .crossFade().into(imageView);
        return this;
    }

    /**
     * 从路径中得到bitmap
     */
    public Bitmap getBitmapByPath(String path) {

        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(path, null);

        return bitmap;
    }

    /**
     * 启动activity
     *
     * @param activity
     */
    public void startActivity(Class<?> activity) {
        try {
            mContext.startActivity(new Intent(mContext, activity));
        } catch (Exception e) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("异常！")
                    .setContentText("启动Activity失败！[e:" + e.toString() + "]")
                    .setConfirmText("确  定!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
    }

    /**
     * 带参数的启动
     *
     * @param o        传递参数
     * @param activity 要启动的avtivity
     * @return 当前实例
     */
    public CommonTools startActivity(Serializable o, Class<?> activity) {
        try {
            //跳转到详细信息界面
            Bundle bundle = new Bundle();
            bundle.putSerializable(o.getClass().getName(), o);
            Intent intent = new Intent(mContext, activity);
            intent.putExtras(bundle);

            mContext.startActivity(intent);
        } catch (Exception e) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("异常！")
                    .setContentText("启动Activity失败！[e:" + e.toString() + "]")
                    .setConfirmText("确  定!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
        return this;
    }

    /**
     * 得到上一页面传递过来的参数
     *
     * @param o 已经实例化的类型传递参数
     * @return 上一页面传递过来的参数
     */
    public Serializable getParam(@NonNull Serializable o) {
        Bundle bundle = ((Activity) mContext).getIntent().getExtras();
        if (bundle == null)
            return "";
        return (Serializable) bundle.get(o.getClass().getName());
    }

    /**
     * 启动activity,得到返回结果
     *
     * @param activity 要启动的activity
     */
    public void startActivityForResult(Class<?> activity, int requestCode) {
        try {
            ((Activity) mContext).startActivityForResult(new Intent(mContext, activity), requestCode);
        } catch (Exception e) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("异常！")
                    .setContentText("启动Activity失败！[e:" + e.toString() + "]")
                    .setConfirmText("确  定!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
    }

    /**
     * 带参数的启动
     *
     * @param o        xx
     * @param activity XXX
     * @return 当前实例
     */
    public CommonTools startActivityForResult(Serializable o, Class<?> activity, int requestCode) {
        try {
            //跳转到详细信息界面
            Bundle bundle = new Bundle();
            bundle.putSerializable(o.getClass().getName(), o);
            Intent intent = new Intent(mContext, activity);
            intent.putExtras(bundle);

            ((Activity) mContext).startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("异常！")
                    .setContentText("启动Activity失败！[e:" + e.toString() + "]")
                    .setConfirmText("确  定!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
        return this;
    }

    /**
     * 设置用户登录标志
     *
     * @param isLogin
     * @return
     */
    public CommonTools setLoginFlag(boolean isLogin) {
        editor.putBoolean(ConstantValue.KEY_ISLOGIN, isLogin);
        editor.commit();
        return this;
    }

    /**
     * 判断用户是否登录
     *
     * @return true-已登录，false-未登录
     */
    public boolean isLogin() {
        return sp.getBoolean(ConstantValue.KEY_ISLOGIN, false);
    }

    /**
     * 设置第一次登陆
     *
     * @param firstIn
     * @return
     */
    public CommonTools setFirstIn(boolean firstIn) {
        editor.putBoolean(ConstantValue.KEY_FIRSTIN, firstIn);
        editor.commit();
        return this;
    }

    public boolean isFirstIn() {
        return sp.getBoolean(ConstantValue.KEY_FIRSTIN, true);
    }

    public CommonTools setUserName(String userName) {
        editor.putString(ConstantValue.KEY_USERNAME, userName);
        editor.commit();
        return this;
    }

    public String getUserName() {
        return sp.getString(ConstantValue.KEY_USERNAME, "");
    }

    public CommonTools setNickName(String nickName) {
        editor.putString(ConstantValue.KEY_NICKNAME, nickName);
        editor.commit();
        return this;
    }

    public String getNickName() {
        return sp.getString(ConstantValue.KEY_NICKNAME, "小农人_001");
    }

    public CommonTools setUserSex(String userSex) {
        editor.putString(ConstantValue.KEY_SEX, userSex);
        editor.commit();
        return this;
    }

    public String getUserSex() {
        return sp.getString(ConstantValue.KEY_SEX, "男");
    }

    public CommonTools setUserHeadURL(String url) {
        editor.putString(ConstantValue.KEY_HEADURL, url);
        editor.commit();
        return this;
    }

    public String getUserHeadURL() {
        return sp.getString(ConstantValue.KEY_HEADURL, "");
    }

    /**
     * 用户签名
     *
     * @param autograph
     * @return
     */
    public CommonTools setAutograph(String autograph) {
        editor.putString(ConstantValue.KEY_AUTOGRAPH, autograph);
        editor.commit();
        return this;
    }

    public String getSignature() {
        return sp.getString(ConstantValue.KEY_AUTOGRAPH, "这个家伙很懒，没有留下什么！");
    }

    /**
     * 带有提示框的登录状态检测
     *
     * @return
     */
    public boolean isLoginWithDialog() {
        // 未登录则提示
        if (!isLogin()) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("未登录")
                    .setContentText("您还没有登录唷,亲!")
                    .setConfirmText("立即登录")
                    .setCancelText("取  消")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            startActivity(LoginActivity.class);
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
        return isLogin();
    }


    public String getUserId() {
        return getUserName();
    }

    public Uri getUserHeadFileUri() {
        File file = new File(USER_FILE_PATH + "/" + getUserId(), "head.jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }
    public Uri getUserBgFileUri() {
        File file = new File(USER_FILE_PATH + "/" + getUserId(), "background.jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }

    public Uri getPondFileUri() {
        File file = new File(USER_FILE_PATH + "/" + getUserId(), "pond.jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }


    public void showExceptionInfo(Exception e) {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("异常！")
                .setContentText("未处置的异常，异常信息为[e:" + e.toString() + "]")
                .setConfirmText("确  定!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public void showExceptionInfo(String info) {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("异常！")
                .setContentText("未处置的异常，异常信息为[e:" + info + "]")
                .setConfirmText("确  定!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    /**
     * 打印log
     *
     * @param msg
     */
    public void log(String msg) {
        Log.i("IFarm_DEBUG_" + mContext.getClass().getSimpleName(), msg);
    }

    /**
     * 设置性别图标
     *
     * @param sex
     * @param imageView
     */
    public CommonTools showSex(String sex, ImageView imageView) {
        if (sex == null) {//为空就直接返回了避免错误
            return this;
        }
        Bitmap female = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.female);
        Bitmap male = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.male);
        //设置性别图标
        switch (sex) {
            case "男":
                imageView.setImageBitmap(male);
                break;
            case "女":
                imageView.setImageBitmap(female);
                break;
            default:
                log("显示性别不成功:sex[" + sex + "],imageView[" + imageView.toString() + "]");
                break;
        }
        return this;
    }

    /**
     * 存入token
     *
     * @param token
     * @return
     */
    public CommonTools setToken(String token) {
        editor.putString(ConstantValue.KEY_TOKEN, token);
        editor.commit();
        return this;
    }

    /**
     * 得到token
     *
     * @return token信息
     */
    public String getToken() {
        return sp.getString(ConstantValue.KEY_TOKEN, "");
    }

    public CommonTools setCountExist(boolean isEmcountExist) {
        editor.putBoolean(ConstantValue.KEY_ISEMCOUNTEXIST, isEmcountExist);
        editor.commit();
        return this;
    }

    public boolean hasEMCount() {
        return sp.getBoolean(ConstantValue.KEY_ISEMCOUNTEXIST, false);
    }

    /**
     * @############本地数据存储################
     */
    /**
     * 通过id从本地获取普通用户信息
     *
     * @return 池塘列表
     */
    public ComUser getComUserInfoById(String userId) {
        ComUser user = new ComUser();
        if (cacheHelper != null) {
            user = cacheHelper.getAsSerializable(ConstantValue.KEY_COMUSERINFO + userId);
        }
        if (user != null)
            log("取出用户信息，key:" + ConstantValue.KEY_COMUSERINFO + userId + ",value:" + user.toString());
        else
            log("用户信息取出失败了，key:" + ConstantValue.KEY_COMUSERINFO + userId);
        return user;
    }

    /**
     * 通过用户Id存储用户信息到本地
     *
     * @param userInfo 要保存用户信息
     * @return 当前实例
     */
    public CommonTools setComUserInfoById(String userId, ComUser userInfo) {
        log("存入用户信息，key:" + ConstantValue.KEY_COMUSERINFO + userId + ",value:" + userInfo.toString());
        cacheHelper.put(ConstantValue.KEY_COMUSERINFO + userId, userInfo);
        return this;
    }

    public CommonTools showDefaultHeadImgBySex(ImageView ivHeadImg, String sex) {
        if (sex == null) {//为空就直接返回了避免错误
            return this;
        }
        Bitmap female = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_head_female);
        Bitmap male = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_head_male);
        //设置性别图标
        switch (sex) {
            case "男":
                ivHeadImg.setImageBitmap(male);
                break;
            case "女":
                ivHeadImg.setImageBitmap(female);
                break;
            default:
                log("显示默认头像不成功:sex[" + sex + "]");
                break;
        }
        return this;
    }

    public void showTokenLose() {
        // 未登录则提示
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Token失效")
                .setContentText("token失效了哦（账号在远程登录，确认是否为本人操作，为保证账号安全，可及时修改密码）!")
                .setConfirmText("重新登录")
                .setCancelText("取  消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(LoginActivity.class);
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}
