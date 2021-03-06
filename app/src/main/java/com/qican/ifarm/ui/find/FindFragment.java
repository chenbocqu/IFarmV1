/**
 * 发现
 */
package com.qican.ifarm.ui.find;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qican.ifarm.R;
import com.qican.ifarm.ui.near.NearListActivity_;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

public class FindFragment extends FragmentWithOnResume implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private CommonTools myTool;
    private Banner mBanner;
    // 服务器获取
    List<String> images;
    List<String> titles;
    private LinearLayout llExpert, llFriend, llNews;
    private RelativeLayout rlQanda, rlNear;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find, container, false);
        initView(view);
        initData();
        initBanner();
        initEvent();

        return view;
    }

    private void initData() {
        images = new ArrayList<>();
        images.add("http://graduate.cqu.edu.cn/images/slide1.jpg");
        images.add("http://graduate.cqu.edu.cn/images/slide3.jpg");
        images.add("http://graduate.cqu.edu.cn/images/slide2.jpg");

        titles = new ArrayList<>();
        titles.add("一滴水也可以看到一个大世界");
        titles.add("生气勃勃的五教");
        titles.add("最美不过校园一角");
        myTool.setHeightByWindow(mBanner, 9.7 / 16f);
    }

    private void initBanner() {
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
                .setImageLoader(new GlideImageLoader())
                .setImages(images)
                .setBannerAnimation(Transformer.ZoomOut)
                .setBannerTitles(titles)
                .isAutoPlay(true)
                .setDelayTime(3000)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .start();
    }

    private void initEvent() {
        llExpert.setOnClickListener(this);
        llFriend.setOnClickListener(this);
        llNews.setOnClickListener(this);

        rlQanda.setOnClickListener(this);
        rlNear.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        view.findViewById(R.id.rl_concern).setOnClickListener(this);
    }

    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        mBanner = (Banner) v.findViewById(R.id.banner);
        llExpert = (LinearLayout) v.findViewById(R.id.ll_expert);
        llFriend = (LinearLayout) v.findViewById(R.id.ll_friend);
        llNews = (LinearLayout) v.findViewById(R.id.ll_news);

        rlQanda = (RelativeLayout) v.findViewById(R.id.rl_qanda);
        rlNear = (RelativeLayout) v.findViewById(R.id.rl_near);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_expert:
                myTool.startActivity(ExpertsListActivity_.class);
                break;

            case R.id.ll_news:
                myTool.startActivity(NewsActivity.class);
                break;
            case R.id.rl_qanda:
                myTool.startActivity(QuestionsActivity.class);
                break;

            // 朋友
            case R.id.ll_friend:
            case R.id.rl_near:
                myTool.startActivity(NearListActivity_.class);
                break;

            case R.id.rl_concern:
                myTool.toHint();
                break;
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }
}
