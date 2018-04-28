package com.qican.ifarm.ui.find;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.NewsAdapter;
import com.qican.ifarm.bean.News;
import com.qican.ifarm.ui_v2.base.CommonListActivity;
import com.qican.ifarm.utils.GlideImageLoader;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends CommonListActivity<News> {

    private Banner mBanner;
    // 服务器获取
    List<String> images;
    List<String> titles;
    List<News> mDatas;

    @Override
    public String getUITitle() {
        return "最新资讯";
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
        images.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3503896818,2196755042&fm=26&gp=0.jpg");
        images.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=205959090,940481771&fm=26&gp=0.jpg");
        images.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2569483618,3183958190&fm=23&gp=0.jpg");

        titles = new ArrayList<>();
        titles.add("首届农牧行业互联网大会");
        titles.add("邢台宁晋：发展高效现代农业");
        titles.add("河北发力农业供给侧改革");

        mDatas = IFarmFakeData.getNews();
    }

    @Override
    public ComAdapter<News> getAdapter() {
        return new NewsAdapter(this, mDatas, R.layout.item_news);
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
