/**
 * 发现
 */
package com.qican.ifarm.ui_v2.userinfo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.listener.OnFragmentListener;
import com.qican.ifarm.listener.OnInfoLoadListener;
import com.qican.ifarm.ui.login.IpModifyDialog;
import com.qican.ifarm.ui.userinfo.HeadInfoActivity;
import com.qican.ifarm.ui.userinfo.MyInfoActivity;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.IFarmData;


public class UserInfoFragment extends FragmentWithOnResume implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RelativeLayout llMyInfo, rlSetting, rlService, rlComQuestion,
            rlFriend, rlLogout, rlFarm, rlLinkEz, rlLogoutEZ;
    private OnFragmentListener mCallBack;
    private ComUser userInfo;
    private TextView tvNickName, tvSignature;
    private ImageView ivHeadImg, ivSex, ivBgImg;
    IpModifyDialog ipModifyDialog;

    private View view;
    private CommonTools myTool;
    SwipeRefreshLayout refreshLayout;

    @Override
    public void update() {
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_userinfo_v2, container, false);
        initView(view);
        initData();
        initEvent();

        return view;
    }

    private void initData() {

        if (myTool == null) return;

        if (!myTool.isLogin()) return;

        userInfo = myTool.getComUserInfoById(myTool.getUserId());
        if (userInfo == null) {
            IFarmData.updateUserInfo(getActivity(), new OnInfoLoadListener() {
                @Override
                public void userInfoLoadFinished(boolean isSuccess) {
                    if (isSuccess) {
                        initData();
                    } else {
                        tvNickName.setText("小农人");
                    }
                }
            });
            return;
        }

        tvNickName.setText(userInfo.getNickName());
        tvSignature.setText(userInfo.getSignature());
        myTool.showSex(userInfo.getSex(), ivSex);

        //设置头像
        if (!"".equals(userInfo.getHeadImgUrl())) {
            myTool.showImage(userInfo.getHeadImgUrl(),
                    ivHeadImg, "男"
                            .equals(userInfo.getSex()) ?
                            R.drawable.default_head_male :
                            R.drawable.default_head_female);
        } else {
            myTool.showDefaultHeadImgBySex(ivHeadImg, userInfo.getSex());//显示默认头像
        }
        //  设置背景图片
        if (userInfo.getBgImgUrl() != null) {
            myTool.showImage(userInfo.getBgImgUrl(),
                    ivBgImg,
                    R.drawable.defaultbg);
        }
    }

    private void initEvent() {
        refreshLayout.setOnRefreshListener(this);
        view.findViewById(R.id.rl_userinfo).setOnClickListener(this);
        view.findViewById(R.id.rl_logout).setOnClickListener(this);
        view.findViewById(R.id.rl_about).setOnClickListener(this);
        view.findViewById(R.id.rl_comquestion).setOnClickListener(this);
        view.findViewById(R.id.rl_service).setOnClickListener(this);
        view.findViewById(R.id.rl_setting).setOnClickListener(this);

        ivHeadImg.setOnClickListener(this);
        ivBgImg.setOnClickListener(this);
    }

    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        myTool.setHeightByWindow(v.findViewById(R.id.rl_head_view), 11 / 16f);


        tvNickName = (TextView) v.findViewById(R.id.tv_nickname);
        tvSignature = (TextView) v.findViewById(R.id.tv_signature);
        ivSex = (ImageView) v.findViewById(R.id.iv_usersex);
        ivHeadImg = (ImageView) v.findViewById(R.id.iv_headimg);
        ivBgImg = (ImageView) v.findViewById(R.id.iv_bgimg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_userinfo:
                myTool.startActivity(MyInfoActivity.class);
                break;

            case R.id.rl_logout:
                myTool.setLoginFlag(false);
                mCallBack.onMessage(this, "logout");
                break;

            case R.id.iv_headimg:
                myTool.previewImg(ivHeadImg, userInfo.getHeadImgUrl());
                break;
            case R.id.iv_bgimg:
                myTool.previewImg(ivBgImg, userInfo.getBgImgUrl());
                break;

            case R.id.rl_about:
                myTool.startActivity(AboutActivity.class);
                break;

            case R.id.rl_comquestion:
            case R.id.rl_service:
                myTool.toHint();
                break;

            case R.id.rl_setting:
                myTool.startActivity(SettingActivity.class);
                break;


        }
    }

    @Override
    public void onRefresh() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 1500);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mCallBack == null) {
            mCallBack = (OnFragmentListener) activity;
        }
    }
}
