package com.qican.ifarm.ui.userinfo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.listener.OnFragmentListener;
import com.qican.ifarm.listener.OnInfoLoadListener;
import com.qican.ifarm.ui.camera.CameraTestActivity_;
import com.qican.ifarm.ui.camera.VideoFullscreenActivity_;
import com.qican.ifarm.ui.farm.AddFarmActivity_;
import com.qican.ifarm.ui.farm.FarmListActivity_;
import com.qican.ifarm.ui.login.IpModifyDialog;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.IFarmData;
import com.qican.ifarm.utils.PullToZoomHelper;
import com.videogo.openapi.EZOpenSDK;

public class UserInfoFragment extends FragmentWithOnResume implements View.OnClickListener, View.OnLongClickListener {
    private View view;
    private String TAG = "UserInfoFragment";

    private CommonTools myTool;
    private PullToZoomScrollViewEx scrollView;
    private RelativeLayout llMyInfo, rlSetting, rlService, rlComQuestion,
            rlFriend, rlLogout, rlFarm, rlLinkEz, rlLogoutEZ;
    private OnFragmentListener mCallBack;
    private ComUser userInfo;
    private TextView tvNickName, tvSignature;
    private ImageView ivHeadImg, ivSex, ivBgImg;
    IpModifyDialog ipModifyDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_userinfo, container, false);
        initView(view);
        initHead();
        initEvents();
        return view;
    }

    @Override
    public void update() {
        super.update();
        initData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mCallBack == null) {
            mCallBack = (OnFragmentListener) activity;
        }
    }

    private void initData() {

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
        myTool.log("bgurl:" + userInfo.getBgImgUrl());
        //  设置背景图片
        if (userInfo.getBgImgUrl() != null) {
            myTool.showImage(userInfo.getBgImgUrl(),
                    ivBgImg,
                    R.drawable.defaultbg);
        }
    }

    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        scrollView = (PullToZoomScrollViewEx) v.findViewById(R.id.scrollView);
        ipModifyDialog = new IpModifyDialog(getActivity(), R.style.Translucent_NoTitle);
    }

    private void initHead() {
        new PullToZoomHelper(getActivity())
                .ratio(9 / 16f)    //head宽高比
                .headView(R.layout.profile_head_view)
                .zoomView(R.layout.profile_zoom_view)
                .contentView(R.layout.profile_content_view)
                .into(scrollView);
        llMyInfo = (RelativeLayout) scrollView.getRootView().findViewById(R.id.rl_userinfo);
        rlSetting = (RelativeLayout) scrollView.getRootView().findViewById(R.id.rl_setting);
        rlService = (RelativeLayout) scrollView.findViewById(R.id.rl_service);
        rlComQuestion = (RelativeLayout) scrollView.getRootView().findViewById(R.id.rl_comquestion);
        rlFriend = (RelativeLayout) scrollView.getRootView().findViewById(R.id.rl_friend);
        rlLogout = (RelativeLayout) scrollView.getRootView().findViewById(R.id.rl_logout);
        rlFarm = (RelativeLayout) scrollView.getRootView().findViewById(R.id.rl_farm);
        rlLinkEz = (RelativeLayout) scrollView.getRootView().findViewById(R.id.rl_link_ez);
        rlLogoutEZ = (RelativeLayout) scrollView.getRootView().findViewById(R.id.rl_logout_ez);

        tvNickName = (TextView) scrollView.getRootView().findViewById(R.id.tv_nickname);
        tvSignature = (TextView) scrollView.getRootView().findViewById(R.id.tv_signature);
        ivSex = (ImageView) scrollView.getRootView().findViewById(R.id.iv_usersex);
        ivHeadImg = (ImageView) scrollView.getRootView().findViewById(R.id.iv_headimg);
        ivBgImg = (ImageView) scrollView.getRootView().findViewById(R.id.iv_bgimg);
    }

    private void initEvents() {
        llMyInfo.setOnClickListener(this);
        llMyInfo.setOnLongClickListener(this);
        rlSetting.setOnClickListener(this);
        rlService.setOnClickListener(this);
        rlService.setOnLongClickListener(this);
        rlSetting.setOnLongClickListener(this);
        rlComQuestion.setOnClickListener(this);
        rlFriend.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
        ivHeadImg.setOnClickListener(this);
        ivBgImg.setOnClickListener(this);
        rlFarm.setOnClickListener(this);
        rlLinkEz.setOnClickListener(this);
        rlLogoutEZ.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rl_userinfo:
                myTool.startActivity(MyInfoActivity.class);
                break;

            // 设置
            case R.id.rl_setting:


                break;
            case R.id.rl_comquestion:
                break;
            case R.id.rl_friend:
//                myTool.startActivity(MyFriendActivity.class);
                break;
            case R.id.rl_service:
                break;
            case R.id.rl_logout:
                myTool.setLoginFlag(false);
                mCallBack.onMessage(this, "logout");
//                EMClient.getInstance().logout(false, new EMCallBack() {//注销环信账户
//                    @Override
//                    public void onSuccess() {
//                        Log.i(TAG, "logout+onSuccess: ");
//                    }
//
//                    @Override
//                    public void onError(int i, String s) {
//                    }
//
//                    @Override
//                    public void onProgress(int i, String s) {
//                    }
//                });
                break;
            case R.id.iv_headimg:
                myTool.previewImg(ivHeadImg, userInfo.getHeadImgUrl());
//                myTool.startActivity(HeadInfoActivity.class);
//                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.iv_bgimg:
                myTool.previewImg(ivBgImg, userInfo.getBgImgUrl());
                break;
            case R.id.rl_farm:
                myTool.startActivity(FarmListActivity_.class);
                break;
            case R.id.rl_link_ez:
                EZOpenSDK.getInstance().openLoginPage();
                break;
            case R.id.rl_logout_ez:
                EZOpenSDK.getInstance().logout();//注销
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting:
                ipModifyDialog.show();
                break;
            // 长按客服添加农场
            case R.id.rl_service:
                myTool.startActivity(AddFarmActivity_.class);
                break;

            case R.id.rl_userinfo:
                myTool.log(EZOpenSDK.getInstance().getEZAccessToken().getAccessToken());
                myTool.startActivity(CameraTestActivity_.class);
                break;
        }
        return false;
    }
}
