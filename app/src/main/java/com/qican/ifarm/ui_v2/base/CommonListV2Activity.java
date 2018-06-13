/**
 * 通用列表
 */
package com.qican.ifarm.ui_v2.base;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.HintView;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

public abstract class CommonListV2Activity<T> extends Activity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {
    protected CommonTools myTool;
    ComAdapter<T> mAdapter;

    protected PullToRefreshLayout pullToRefreshLayout;

    PullListView pullListView;

    TextView tvTitle, tvMenu;
    LinearLayout llBack;
    View vHeader = null;
    private LinearLayout llMenu, llIconMenu;
    private View.OnClickListener menuListener, iconListener;
    private String menuText;
    private int iconResId = 0;
    private ImageView ivIconMenu;
    protected HintView hintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list_v2);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        llBack.setOnClickListener(this);
        llMenu.setOnClickListener(this);
        llIconMenu.setOnClickListener(this);
        pullToRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
        init();

        initTitleBar();

        mAdapter = getAdapter();
        if (vHeader != null) pullListView.addHeaderView(vHeader);
        pullListView.setAdapter(mAdapter);
        notifyDatasetChanged();
    }

    private void initTitleBar() {
        tvTitle.setText(getUITitle());

        if (menuListener != null)
            llMenu.setVisibility(View.VISIBLE);

        if (menuText != null) {
            llMenu.setVisibility(View.VISIBLE);
            tvMenu.setText(menuText);
        }

        if (iconListener != null) {
            llIconMenu.setVisibility(View.VISIBLE);
        }

        if (iconResId != 0) {
            llIconMenu.setVisibility(View.VISIBLE);
            ivIconMenu.setImageBitmap(BitmapFactory.decodeResource(getResources(), iconResId));
        }
    }

    private void initView() {

        hintView = new HintView(this, findViewById(R.id.rl_hint));

        myTool = new CommonTools(this);
        pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pullToRefreshLayout);
        pullListView = (PullListView) findViewById(R.id.pullListView);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llBack = (LinearLayout) findViewById(R.id.llBack);

        // titleBar
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        llIconMenu = (LinearLayout) findViewById(R.id.ll_icon_menu);
        tvMenu = (TextView) findViewById(R.id.tv_menu);
        ivIconMenu = (ImageView) findViewById(R.id.iv_icon_menu);

        // header
        if (getHeadLayout() != 0)
            vHeader = View.inflate(this, getHeadLayout(), null);
    }

    protected void showLoading() {
        if (hintView != null)
            hintView.showLoading();
    }

    protected void showErr(String errInfo) {
        showErr(errInfo, null);
    }

    protected void showErr(String errInfo, View.OnClickListener l) {

        if (hintView != null)
            hintView.showError(errInfo, l);

    }

    protected void showNoLogin() {

        if (hintView != null)
            hintView.showNoLogin();

    }

    protected void showContentByData(boolean hasData) {
        if (hintView != null)
            hintView.showContentByData(hasData);
    }

    public void notifyDatasetChanged() {
        mAdapter.notifyDataSetChanged();
        hintView.showContentByData(!mAdapter.isEmpty());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                this.finish();
                break;
            case R.id.ll_menu:
                if (menuListener != null) menuListener.onClick(llMenu); // 文字菜单点击事件接口回调
                break;
            case R.id.ll_icon_menu:
                if (iconListener != null) iconListener.onClick(llIconMenu);
                break;
        }
    }

    public abstract String getUITitle();

    public abstract void init();

    public abstract ComAdapter<T> getAdapter();

    @Override
    public abstract void onRefresh(PullToRefreshLayout l);

    @Override
    public abstract void onLoadMore(PullToRefreshLayout l);

    public void setRightMenu(String menuText, View.OnClickListener menuListener) {
        this.menuText = menuText;
        this.menuListener = menuListener;
    }

    public void setIconMenu(@DrawableRes int resId, View.OnClickListener iconListener) {
        this.iconResId = resId;
        this.iconListener = iconListener;
    }

    protected View getHeaderView() {
        return vHeader;
    }

    protected int getHeadLayout() {
        return 0;
    }
}
