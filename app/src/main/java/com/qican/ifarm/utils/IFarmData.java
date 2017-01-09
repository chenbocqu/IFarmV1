/**
 * 处理数据的类
 */
package com.qican.ifarm.utils;

import android.content.Context;

import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.beanfromzhu.User;
import com.qican.ifarm.listener.BeanCallBack;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
}
