/**
 * 学递来啦主界面
 *
 * @time：2016-6-29
 */
package com.qican.ifarm.ui.myfriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.qican.ifarm.R;
import com.qican.ifarm.ui.chat.ChatActivity;
import com.qican.ifarm.ui.near.NearListFragment;
import com.qican.ifarm.ui.node.NodeListFragment;
import com.qican.ifarm.view.ChangeColorIconWithText;

import java.util.ArrayList;
import java.util.List;

public class MyFriendActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private TextView mTvTitle;
    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private String[] mTitles = new String[]{"会话", "通讯录"};
    private FragmentPagerAdapter mAdapter;
    private NodeListFragment nodeListFragment;
    private LinearLayout llBack;
    private EaseConversationListFragment conversationListFragment;
    private EaseContactListFragment contactListFragment;

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        initView();
        initDatas(); //加载页面及数据
        initEvent();
    }

    /**
     * 初始化所有事件
     */
    private void initEvent() {
        mViewPager.setOnPageChangeListener(this);
        llBack.setOnClickListener(this);
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(MyFriendActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
            }
        });
        contactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                startActivity(new Intent(MyFriendActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
            }
        });
    }

    private void initDatas() {
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
        //会话列表
        conversationListFragment = new EaseConversationListFragment();
        mTabs.add(conversationListFragment);

        // 联系人列表
        contactListFragment = new EaseContactListFragment();
        mTabs.add(contactListFragment);
    }

    private void initView() {

        llBack = (LinearLayout) findViewById(R.id.ll_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);//显示title的textView
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        // 拿到底部的四个自定义View
        ChangeColorIconWithText indicatorHomePage = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);
        ChangeColorIconWithText indicatorNearby = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);

        mTabIndicators.add(indicatorHomePage);
        mTabIndicators.add(indicatorNearby);

        //指示器设置监听
        indicatorHomePage.setOnClickListener(this);
        indicatorNearby.setOnClickListener(this);

        //默认是首页设置为选中状态，默认首页title
        indicatorHomePage.setIconAlpha(1.0f);
        setUITitle(0);
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
            case R.id.ll_back:
                finish();
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
    }
}
