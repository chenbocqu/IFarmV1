package com.qican.ifarm.ui_v2.farm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.DataAdapter;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.utils.CommonTools;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import me.kaelaela.verticalviewpager.VerticalViewPager;

public class FarmListsFragment extends FragmentWithOnResume implements View.OnClickListener {
    private View view;
    private CommonTools myTool;

    private List<Farm> mDatas;
    List<FragmentWithOnResume> mFragments;

    public static final String FARM_KEY = "FARM_KEY";

    //下拉刷新
    NetRequest netRequest;
    VerticalViewPager mViewPage;
    FragmentPagerAdapter mAdapter;
    RelativeLayout rlNodata;

    TextView tvRefresh;
    AVLoadingIndicatorView loadView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myTool = new CommonTools(getActivity());

        view = inflater.inflate(R.layout.fragment_farmlists, container, false);
        initView(view);
        initDatas();
        initEvent();

        return view;
    }

    @Override
    public void update() {
        refreshData();
    }

    private void initDatas() {
//        mDatas = IFarmFakeData.getFarmList();
        mDatas = new ArrayList<>();
        mFragments = new ArrayList<>();

        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragments.get(i);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        mViewPage.setAdapter(mAdapter);

//        updateFragmentByData();
//        refreshData();
    }

    private void updateFragmentByData() {

        mFragments.clear();

        if (!mDatas.isEmpty()) {

            for (Farm farm : mDatas) {
                FarmInfoFragment fg = new FarmInfoFragment();
//                FarmInfov2Fragment fg = new FarmInfov2Fragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(FARM_KEY, farm);
                fg.setArguments(bundle);

                mFragments.add(fg);
            }
        }
        notifyDatasetChanged();
    }

    private void refreshData() {
        if (netRequest == null) {
            myTool.log("netRequest is null!");
            return;
        }

        myTool.log("refresh Farm data ... ");

        loadView.smoothToShow();

        netRequest.getFarmList(new DataAdapter() {
            @Override
            public void farmList(List<Farm> farmList) {

                loadView.smoothToHide();

                if (farmList != null) {
                    mDatas.clear();
                    mDatas.addAll(farmList);
                    updateFragmentByData();
                } else {
                    myTool.showInfo("网络请求异常！");
                }
            }
        });
    }

    public void notifyDatasetChanged() {
        mAdapter.notifyDataSetChanged();
        if (mFragments.isEmpty()) {
            showNoData();
        } else {
            hideNoData();

            for (FragmentWithOnResume fragmentWithOnResume : mFragments)
                fragmentWithOnResume.update();
        }
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

    private void initEvent() {
        tvRefresh.setOnClickListener(this);
    }

    private void initView(View v) {
        mViewPage = (VerticalViewPager) view.findViewById(R.id.view_pager);

        netRequest = new NetRequest(getActivity());
        rlNodata = (RelativeLayout) view.findViewById(R.id.rl_nothing_main);
        tvRefresh = (TextView) view.findViewById(R.id.tv_refresh);

        loadView = (AVLoadingIndicatorView) view.findViewById(R.id.load_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_refresh:
                refreshData();
//                myTool.startActivity(AddFarmActivity_.class);
                break;
        }
    }
}
