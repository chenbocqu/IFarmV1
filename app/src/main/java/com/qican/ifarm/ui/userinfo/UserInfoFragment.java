package com.qican.ifarm.ui.userinfo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.qican.ifarm.listener.OnFragmentListener;
import com.qican.ifarm.ui.chat.MainActivity;
import com.qican.ifarm.ui.login.LoginActivity;
import com.qican.ifarm.ui.myfriend.MyFriendActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.PullToZoomHelper;

public class UserInfoFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private View view;
    private String TAG = "UserInfoFragment";

    private CommonTools myTool;
    private PullToZoomScrollViewEx scrollView;
    private RelativeLayout llMyInfo, rlSetting, rlService, rlComQuestion, rlFriend, rlLogout;
    private OnFragmentListener mCallBack;
    private ComUser userInfo;
    private TextView tvNickName, tvSignature;
    private ImageView ivHeadImg, ivSex;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mCallBack == null) {
            mCallBack = (OnFragmentListener) activity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        myTool.log("我的个人信息页面，initData()");
        if (!myTool.isLogin()) {
            return;
        }
        userInfo = myTool.getComUserInfoById(myTool.getUserId());
        if (userInfo == null) {
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
        }
    }

    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        scrollView = (PullToZoomScrollViewEx) v.findViewById(R.id.scrollView);
    }

    private void initHead() {
        new PullToZoomHelper(getActivity())
                .ratio(10 / 16f)    //head宽高比
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

        tvNickName = (TextView) scrollView.getRootView().findViewById(R.id.tv_nickname);
        tvSignature = (TextView) scrollView.getRootView().findViewById(R.id.tv_signature);
        ivSex = (ImageView) scrollView.getRootView().findViewById(R.id.iv_usersex);
        ivHeadImg = (ImageView) scrollView.getRootView().findViewById(R.id.iv_headimg);
    }

    private void initEvents() {
        llMyInfo.setOnClickListener(this);
        rlSetting.setOnClickListener(this);
        rlService.setOnClickListener(this);
        rlSetting.setOnLongClickListener(this);
        rlComQuestion.setOnClickListener(this);
        rlFriend.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
        ivHeadImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_userinfo:
                myTool.startActivity(MyInfoActivity.class);
                break;
            case R.id.rl_setting:
                myTool.startActivity(MainActivity.class);
//                myTool.startActivity(LoginActivity.class);
                break;
            case R.id.rl_comquestion:
                break;
            case R.id.rl_friend:
                myTool.startActivity(MyFriendActivity.class);
                break;
            case R.id.rl_service:
                break;
            case R.id.rl_logout:
                myTool.setLoginFlag(false);
                EMClient.getInstance().logout(false, new EMCallBack() {//注销环信账户
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "logout+onSuccess: ");
                    }

                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onProgress(int i, String s) {
                    }
                });
                mCallBack.onMessage(this, "logout");
                break;
            case R.id.iv_headimg:
                myTool.startActivity(HeadInfoActivity.class);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting:
                myTool.startActivity(LoginActivity.class);
                break;
        }
        return false;
    }
}
