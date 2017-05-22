/**
 * 处理数据的类
 */
package com.qican.ifarm.utils;

import android.content.Context;

import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.beanfromservice.User;
import com.qican.ifarm.listener.BeanCallBack;
import com.qican.ifarm.listener.OnInfoLoadListener;
import com.qican.ifarm.listener.TokenListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class IFarmData {
    private Context mContext;
    private CommonTools myTool;

    public IFarmData(Context mContext) {
        this.mContext = mContext;
        myTool = new CommonTools(mContext);
    }

    /**
     * 更新用户信息到本地
     *
     * @param context
     */
    public static void updateUserInfo(Context context) {
        final CommonTools myTool = new CommonTools(context);
        OkHttpUtils.get().url(ConstantValue.SERVICE_ADDRESS + "user/getUserById")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new BeanCallBack<User>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("查询用户信息失败，异常为：" + e.toString());
                    }

                    @Override
                    public void onResponse(User user, int id) {
                        myTool.log("用户信息查询结果：" + user.toString());
                        myTool.setComUserInfoById(myTool.getUserId(), new ComUser(user));
                    }
                });
    }

    /**
     * 更新用户信息到本地
     *
     * @param context
     */
    public static void updateUserInfo(Context context, final OnInfoLoadListener l) {
        final CommonTools myTool = new CommonTools(context);
        OkHttpUtils.get().url(ConstantValue.SERVICE_ADDRESS + "user/getUserById")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new BeanCallBack<User>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("查询用户信息失败，异常为：" + e.toString());
                        l.userInfoLoadFinished(false);
                    }

                    @Override
                    public void onResponse(User user, int id) {
                        myTool.log("用户信息查询结果：" + user.toString());
                        myTool.setComUserInfoById(myTool.getUserId(), new ComUser(user));
                        l.userInfoLoadFinished(true);
                    }
                });
    }

    /**
     * 更新用户信息到本地
     *
     * @param context
     */
    public static void updateToken(Context context, final TokenListener l) {
        final CommonTools myTool = new CommonTools(context);
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "user/getUserToken")
                .addParams("userId", myTool.getUserId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        l.updateFailed(e);
                        myTool.log("token Exception:" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        myTool.log("token response:" + response);
                        l.updateSuccess(response);
                        myTool.setToken(response);
                    }
                });
    }

    public static List<Farm> getFarmList() {
        List<Farm> farmList = new ArrayList<Farm>();
        for (int i = 0; i < 3; i++) {
            Farm farm = new Farm();
            farm.setName("最小农场");
            farm.setLabels("花花,草草");
            farm.setDesc("测试用");
            farm.setTime("昨天 下午5:50");
            farmList.add(farm);
        }
        return farmList;
    }
}
