package com.qican.ifarm.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class MyViewPager extends ViewPager {
    /**
     * 屏幕的宽度
     */
    private int screenWidth;
    private int scrollWidth = 200;//能够滑动的两边距离

    public MyViewPager(Context context) {
        super(context);
        initScreenWidth();
    }

    private void initScreenWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);

        screenWidth = dpMetrics.widthPixels;
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScreenWidth();
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//        Log.i("MyViewPager", "canScroll: v[" + v.getClass().getName() + "],dx[" + dx + "],x[" + x + "],y[" + y + "]");
        // 判断是地图是就可以动，百度地图，高德地图
        if (v.getClass().getName().equals("com.baidu.mapapi.map.MapView") ||
                v.getClass().getName().equals("com.amap.api.maps.MapView") ||
                v.getClass().getName().equals("com.amap.api.maps2d.MapView")) {
            if (x > scrollWidth && x < screenWidth - scrollWidth) {
                return true;
            }
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}