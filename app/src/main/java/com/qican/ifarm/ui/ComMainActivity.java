/**
 * 学递来啦主界面
 *
 * @time：2016-6-29
 */
package com.qican.ifarm.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.listener.OnFragmentListener;
import com.qican.ifarm.ui.control.ControlListFragment;
import com.qican.ifarm.ui.login.LoginActivity;
import com.qican.ifarm.ui.near.NearListFragment;
import com.qican.ifarm.ui.node.NodeListFragment;
import com.qican.ifarm.ui.userinfo.UserInfoFragment;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.ChangeColorIconWithText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_main)
public class ComMainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, OnFragmentListener {
    public static final int REQUSET_FOR_CURRENTLOCATION = 1; //请求当前位置

    @ViewById(R.id.tv_title)
    TextView mTvTitle;

    @ViewById(R.id.id_viewpager)
    ViewPager mViewPager;

    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private String[] mTitles = new String[]{"附近的人", "我的农场", "控制", "个人信息"};
    private FragmentPagerAdapter mAdapter;
    private NodeListFragment nodeListFragment;

    @ViewById(R.id.ll_login)
    LinearLayout llLogin;

    @ViewById(R.id.ll_msg)
    LinearLayout llMsg;

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();
    private CommonTools myTool;

    @AfterViews
    void doSomeThing() {
        initView();
        setFragemnts(); //加载页面及数据
        initEvent();
    }

    /**
     * 初始化所有事件
     */
    private void initEvent() {
        llLogin.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);
    }

    private void setFragemnts() {
        // 载入主UI的几个页面
        loadFragments();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }

        };
        mViewPager.setAdapter(mAdapter);
    }

    /**
     * 加载主界面的几个子UI
     */
    private void loadFragments() {
        //控制列表
        NearListFragment tabFragment1 = new NearListFragment();
        mTabs.add(tabFragment1);

        // 监测列表
        nodeListFragment = new NodeListFragment();
        mTabs.add(nodeListFragment);

        //我的包裹
//        MapFragment tabFragment2 = new MapFragment();
        ControlListFragment tabFragment2 = new ControlListFragment();
        mTabs.add(tabFragment2);

        //个人信息
        UserInfoFragment tabFragment3 = new UserInfoFragment();
        mTabs.add(tabFragment3);
    }

    private void initView() {
        myTool = new CommonTools(this);

        // 拿到底部的四个自定义View
        ChangeColorIconWithText indicatorHomePage = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);
        ChangeColorIconWithText indicatorNearby = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);
        ChangeColorIconWithText indicatorMyparcel = (ChangeColorIconWithText) findViewById(R.id.id_indicator_three);
        ChangeColorIconWithText indicatorMine = (ChangeColorIconWithText) findViewById(R.id.id_indicator_four);

        mTabIndicators.add(indicatorHomePage);
        mTabIndicators.add(indicatorNearby);
        mTabIndicators.add(indicatorMyparcel);
        mTabIndicators.add(indicatorMine);

        //指示器设置监听
        indicatorHomePage.setOnClickListener(this);
        indicatorNearby.setOnClickListener(this);
        indicatorMyparcel.setOnClickListener(this);
        indicatorMine.setOnClickListener(this);

        //默认是首页设置为选中状态，默认首页title
        indicatorHomePage.setIconAlpha(1.0f);
        setUITitle(0);
        myTool.log("Hello Android Annotations!");
    }

    @Override
    public void onClick(View v) {
        setIndicators(v);
        doBtnClick(v);
    }

    /**
     * 处理按钮的点击事件
     *
     * @param v
     */
    private void doBtnClick(View v) {
        switch (v.getId()) {
            case R.id.ll_login:
                myTool.startActivity(LoginActivity.class);
                break;
        }
    }


    /**
     * 设置底部指示器的点击及指示效果
     *
     * @param v
     */
    private void setIndicators(View v) {
        // 根据按键切换ViewPager
        switch (v.getId()) {
            case R.id.id_indicator_one:
                //首先把其他Tab清除
                resetOtherTabs();
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                //首先把其他Tab清除
                resetOtherTabs();
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                //首先把其他Tab清除
                resetOtherTabs();
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_four:
                //首先把其他Tab清除
                resetOtherTabs();
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
        }
    }

    /**
     * 重置其他的TabIndicator的颜色
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

        // Log.e("TAG", "position = " + position + " ,positionOffset =  "
        // + positionOffset);
        // 设置指示器，渐变效果
        if (positionOffset > 0) {
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        // 设置title
        setUITitle(position);
    }

    /**
     * 设置导航顶部的显示文字
     *
     * @param position
     */
    private void setUITitle(int position) {
        mTvTitle.setText(mTitles[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * MainActivity重新获得焦点是读取登录状态并设置属性
     */
    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    private void initDatas() {
        //设置见或者不见
        llLogin.setVisibility(myTool.isLogin() ? View.GONE : View.VISIBLE);
        llMsg.setVisibility(myTool.isLogin() ? View.VISIBLE : View.GONE);
    }

    /**
     * 子页面发过来的消息
     */
    @Override
    public void onMessage(Fragment fg, Object obj) {
        if (fg.getClass().equals(UserInfoFragment.class)) {
            switch ((String) obj) {
                // 注销后重新刷新页面
                case "logout":
                    initDatas();
                    break;
            }
        }
    }
}
