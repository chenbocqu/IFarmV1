/**
 * 智能农场主页面
 *
 * @time：2016-6-29
 */
package com.qican.ifarm.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.listener.OnFragmentListener;
import com.qican.ifarm.ui.find.FindFragment;
import com.qican.ifarm.ui.login.LoginNewActivity;
import com.qican.ifarm.ui_v2.qrcode.ScanActivity;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.ui_v2.camera.CameraListFragment;
import com.qican.ifarm.ui_v2.control.ControlFuncsFragment;
import com.qican.ifarm.ui_v2.farm.FarmListsFragment;
import com.qican.ifarm.ui_v2.userinfo.UserInfoFragment;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.TabIndicators;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_main)
public class ComMainActivity extends FragmentActivity
        implements View.OnClickListener, ViewPager.OnPageChangeListener, OnFragmentListener {

    @ViewById(R.id.tv_title)
    TextView mTvTitle;

    @ViewById(R.id.id_viewpager)
    ViewPager mViewPager;

    private List<FragmentWithOnResume> mTabs = new ArrayList<FragmentWithOnResume>();
    private String[] mTitles = new String[]{"农场", "控制", "监控", "发现", "个人信息"};
    private FragmentPagerAdapter mAdapter;

    int tabIds[] = {
            R.id.id_indicator_one,
            R.id.id_indicator_two,
            R.id.id_indicator_camera,
            R.id.id_indicator_three,
            R.id.id_indicator_four
    };

    int curIndex = 0, pageIndex = 0;

    @ViewById(R.id.ll_login)
    LinearLayout llLogin;

    @ViewById(R.id.ll_msg)
    LinearLayout llMsg;

    private List<TabIndicators> mTabIndicators = new ArrayList<TabIndicators>();
    private CommonTools myTool;

    @AfterViews
    void doSomeThing() {

        myTool = new CommonTools(this);

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
        mTabs.add(new FarmListsFragment());     // 农场数据
        mTabs.add(new ControlFuncsFragment());  // 控制功能
        mTabs.add(new CameraListFragment());    // 监控视频
        mTabs.add(new FindFragment());          // 发现
        mTabs.add(new UserInfoFragment());      // 个人信息

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

    private void initView() {

        // 添加底部指示器
        mTabIndicators.add((TabIndicators) findViewById(R.id.id_indicator_one));
        mTabIndicators.add((TabIndicators) findViewById(R.id.id_indicator_two));
        mTabIndicators.add((TabIndicators) findViewById(R.id.id_indicator_camera));
        mTabIndicators.add((TabIndicators) findViewById(R.id.id_indicator_three));
        mTabIndicators.add((TabIndicators) findViewById(R.id.id_indicator_four));

        // 指示器设置点击监听
        for (TabIndicators indicator : mTabIndicators)
            indicator.setOnClickListener(this);

        // 默认是首页设置为选中状态，默认首页title
        mTabIndicators.get(0).setIconAlpha(1.0f);
        setUITitle(0);
    }

    @Override
    public void onClick(View v) {

        // 处理底部tab
        for (int i = 0; i < tabIds.length; i++)
            if (v.getId() == tabIds[i])
                selectPage(i);

        // 处理其他按钮
        switch (v.getId()) {
            case R.id.ll_login:
                myTool.startActivity(LoginNewActivity.class);
                break;
        }
    }

    private void selectPage(int index) {
        //首先把其他Tab清除
        resetOtherTabs();
        mTabIndicators.get(index).setIconAlpha(1.0f);
        mViewPager.setCurrentItem(index, false);
        curIndex = index;

        updatePage();
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
            TabIndicators left = mTabIndicators.get(position);
            TabIndicators right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
        curIndex = position;
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
        if (state == 0) {
            updatePage();
        }
    }

    private void updatePage() {

        if (pageIndex == curIndex)
            return;

        pageIndex = curIndex;
        mTabs.get(pageIndex).update();
    }

    /**
     * MainActivity重新获得焦点是读取登录状态并设置属性
     */
    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
        llLogin.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mTabs.isEmpty())
                    mTabs.get(curIndex).update();
            }
        }, 1000);
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
                    mViewPager.setCurrentItem(0, false);
                    mViewPager.setCurrentItem(mTabs.size() - 1, false);
                    break;
            }
        }
    }

    @Click(R.id.iv_scan)
    void scan() {
        myTool.startActivity(ScanActivity.class);
    }
}
