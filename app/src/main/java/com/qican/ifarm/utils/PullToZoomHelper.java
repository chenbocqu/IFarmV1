package com.qican.ifarm.utils;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;

public class PullToZoomHelper {
    private Activity a;
    private float ratio = 9 / 16f;
    private int headLayoutId = 0;
    private int zoomLayoutId = 0;
    private int contenLayoutId = 0;

    private View headView = null;
    private View zoomView = null;
    private View contentView = null;

    public PullToZoomHelper(Activity a) {
        this.a = a;
    }

    public PullToZoomHelper ratio(float ratio) {
        this.ratio = ratio;
        return this;
    }

    public PullToZoomHelper headView(@LayoutRes int layoutId) {
        headLayoutId = layoutId;
        return this;
    }

    public PullToZoomHelper headView(View headView) {
        this.headView = headView;
        return this;
    }

    public PullToZoomHelper zoomView(@LayoutRes int layoutId) {
        zoomLayoutId = layoutId;
        return this;
    }

    public PullToZoomHelper zoomView(View zoomView) {
        this.zoomView = zoomView;
        return this;
    }

    public PullToZoomHelper contentView(@LayoutRes int layoutId) {
        contenLayoutId = layoutId;
        return this;
    }

    public PullToZoomHelper contentView(View contentView) {
        this.contentView = contentView;
        return this;
    }

    public void into(PullToZoomScrollViewEx scrollView) {

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (ratio * mScreenWidth));
        scrollView.setHeaderLayoutParams(localObject);

        if (headLayoutId != 0) {
            View headView = LayoutInflater.from(a).inflate(headLayoutId, null, false);
            scrollView.setHeaderView(headView);
        }
        if (zoomLayoutId != 0) {
            View zoomView = LayoutInflater.from(a).inflate(zoomLayoutId, null, false);
            scrollView.setZoomView(zoomView);
        }
        if (contenLayoutId != 0) {
            View contentView = LayoutInflater.from(a).inflate(contenLayoutId, null, false);
            scrollView.setScrollContentView(contentView);
        }

        if (zoomView != null) {
            scrollView.setZoomView(zoomView);
        }
        if (headView != null) {
            scrollView.setZoomView(headView);
        }
        if (contentView != null) {
            scrollView.setZoomView(contentView);
        }
    }
}
