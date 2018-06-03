package com.qican.ifarm.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.utils.CommonTools;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * 用于列表信息提示的View，包括未登录，错误，无数据
 * 使用时，必须在布局文件中添加hint_view.xml
 */

public class HintView implements View.OnClickListener {

    CommonTools myTool;
    Activity mAct;
    View mainView;

    View rlNoLogin, rlErr, rlNoData;
    TextView tvErrInfo;
    AVLoadingIndicatorView loadView;
    View.OnClickListener refreshListener;


    public HintView(Activity mAct, View mainView) {
        this.mAct = mAct;
        this.mainView = mainView;

        initView();
        resetView();

        showContentByData(false);
        initEvent();
    }

    private void initEvent() {
        rlNoLogin.setOnClickListener(this);
        rlNoData.setOnClickListener(this);
    }


    public void showNoLogin() {

        resetView();
        rlNoLogin.setVisibility(View.VISIBLE);
    }

    public void showContentByData(boolean hasData) {

        if (!isLogin()) return;

        rlNoData.setVisibility(hasData ? View.GONE : View.VISIBLE);
    }

    public void showLoading() {
        resetView();
        loadView.smoothToShow();
    }

    public void showError(String err) {
        showError(err, null);
    }

    // 错误带点击事件
    public void showError(String err, View.OnClickListener l) {

        if (!isLogin()) return;

        rlErr.setVisibility(View.VISIBLE);

        if (tvErrInfo != null)
            tvErrInfo.setText(err);

        if (l != null)
            rlErr.setOnClickListener(l);
    }


    private void resetView() {

        rlNoLogin.setVisibility(View.GONE);
        rlErr.setVisibility(View.GONE);
        rlNoData.setVisibility(View.GONE);

        if (loadView.isShown())
            loadView.smoothToHide();
    }

    private void initView() {

        myTool = new CommonTools(mAct);

        rlNoLogin = mainView.findViewById(R.id.rl_no_login);
        rlErr = mainView.findViewById(R.id.rl_error);
        rlNoData = mainView.findViewById(R.id.rl_nodata);
        loadView = (AVLoadingIndicatorView) mainView.findViewById(R.id.load_view);
        tvErrInfo = (TextView) mainView.findViewById(R.id.tv_err_info);
    }

    public boolean isLogin() {

        resetView();

        if (!myTool.isLogin())
            rlNoLogin.setVisibility(View.VISIBLE);

        return myTool.isLogin();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_no_login:
                myTool.tologin();
                break;

            case R.id.rl_nodata:
                if (this.refreshListener != null)
                    this.refreshListener.onClick(rlNoData);
                break;
        }
    }

    public void setRefreshListener(View.OnClickListener refreshListener) {
        this.refreshListener = refreshListener;
    }
}
