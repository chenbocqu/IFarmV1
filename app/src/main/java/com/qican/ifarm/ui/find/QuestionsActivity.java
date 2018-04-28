package com.qican.ifarm.ui.find;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.QuestionAdapter;
import com.qican.ifarm.bean.Question;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.utils.GlideImageLoader;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends CommonListActivity<Question> {

    private Banner mBanner;
    // 服务器获取
    List<String> images;
    List<String> titles;
    List<Question> mDatas;

    @Override
    public String getUITitle() {
        return "问答";
    }

    @Override
    public void init() {
        initViews();
        initDatas();
        initBanner();
    }

    private void initViews() {
        mBanner = (Banner) getHeaderView().findViewById(R.id.banner);
        myTool.setHeightByWindow(mBanner, 9 / 16f);
    }

    private void initBanner() {
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
                .setImageLoader(new GlideImageLoader())
                .setImages(images)
                .setBannerAnimation(Transformer.Default)
                .setBannerTitles(titles)
                .isAutoPlay(true)
                .setDelayTime(3000)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .start();
    }

    private void initDatas() {
        images = new ArrayList<>();
        images.add("https://gss0.bdstatic.com/7051cy89RcgCncy6lo7D0j9wexYrbOWh7c50/zhidaoribao/2017/0603/top.jpg");
        images.add("https://gss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D800%2C450/sign=e0b7e78eb63eb1354492bfb3962e84e7/9213b07eca8065386c6a6f759ddda144ac3482d0.jpg");
        images.add("https://gss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D800%2C450/sign=b02dc79ac9fdfc03e52debb0e40faba0/b8389b504fc2d562fde4e189ed1190ef77c66ce1.jpg");

        titles = new ArrayList<>();
        titles.add("双胞胎之间真的有心灵感应吗？");
        titles.add("除了宫崎骏，他也是不能被忽略的动画电影大师！");
        titles.add("你没看错，兔子不爱吃胡萝卜，大熊猫也不是吃素的！");

        mDatas = IFarmFakeData.getQuestions();
    }

    @Override
    public ComAdapter<Question> getAdapter() {
        return new QuestionAdapter(this, mDatas, R.layout.item_question);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.refreshFinish(true);
            }
        }, 1500);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout l) {
        l.postDelayed(new Runnable() {
            @Override
            public void run() {
                l.loadMoreFinish(true);
            }
        }, 1500);
    }

    @Override
    protected int getHeadLayout() {
        return R.layout.header_news_activity;
    }

}
