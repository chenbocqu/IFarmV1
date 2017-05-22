/**
 * 通用列表
 */
package com.qican.ifarm.ui.base;

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
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

public abstract class ContentWithBaseListActivity<T> extends Activity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {
    protected CommonTools myTool;
    CommonAdapter<T> mAdapter;

    PullToRefreshLayout pullToRefreshLayout;

    PullListView pullListView;

    protected AVLoadingIndicatorView avi;

    TextView tvTitle,tvMenu;
    LinearLayout llBack;
    View rlNodata;
    ImageView ivNetError;

    private LinearLayout llMenu, llIconMenu;
    private View.OnClickListener menuListener, iconListener;
    private String menuText;
    private int iconResId = 0;
    private ImageView ivIconMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
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

        avi.setVisibility(View.GONE);
        mAdapter = getAdapter();
        pullListView.setAdapter(mAdapter);
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
        myTool = new CommonTools(this);
        pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pullToRefreshLayout);
        pullListView = (PullListView) findViewById(R.id.pullListView);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi_list);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        rlNodata = findViewById(R.id.rl_nodata);
        ivNetError = (ImageView) findViewById(R.id.iv_net_error);

        // titleBar
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        llIconMenu = (LinearLayout) findViewById(R.id.ll_icon_menu);
        tvMenu = (TextView) findViewById(R.id.tv_menu);
        ivIconMenu = (ImageView) findViewById(R.id.iv_icon_menu);
    }

    private void showNoData() {
        rlNodata.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(rlNodata);
    }

    private void hideNoData() {
        YoYo.with(Techniques.FadeOut)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rlNodata.setVisibility(View.GONE);
                    }
                })
                .duration(100)
                .playOn(rlNodata);
    }


    public void notifyDatasetChanged() {
        mAdapter.notifyDataSetChanged();
        if (mAdapter.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
        avi.smoothToHide();
        ivNetError.setVisibility(View.GONE);
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

    public abstract CommonAdapter<T> getAdapter();

    //设置布局文件
    protected abstract int getContentView();

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
}
