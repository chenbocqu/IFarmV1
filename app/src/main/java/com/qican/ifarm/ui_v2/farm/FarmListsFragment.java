package com.qican.ifarm.ui_v2.farm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.HintView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.kaelaela.verticalviewpager.VerticalViewPager;
import okhttp3.Call;

public class FarmListsFragment extends FragmentWithOnResume {
    private View view;
    private CommonTools myTool;

    private List<Farm> mDatas;
    List<FragmentWithOnResume> mFragments;

    public static final String FARM_KEY = "FARM_KEY";

    //下拉刷新
    VerticalViewPager mViewPage;
    FragmentPagerAdapter mAdapter;
    HintView hintView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myTool = new CommonTools(getActivity());

        view = inflater.inflate(R.layout.fragment_farmlists, container, false);
        initView(view);
        initDatas();

        return view;
    }

    @Override
    public void update() {
        myTool.log("FarmList Fragment update()...");
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

        updateFragmentByData();
        refreshData();
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

        mDatas.clear();
        updateFragmentByData();

        hintView.showLoading();

        String url = myTool.getServAdd() + "farm/farmsList";

        OkHttpUtils.post().url(url)
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        myTool.log(e.getMessage());

                        hintView.showError(e.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refreshData();
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        myTool.log(response);

                        if (response == null) {
                            myTool.showInfo("数据为空！");
                            hintView.showContentByData(false);
                            return;
                        }

                        // 账号过期
                        if ("lose efficacy".equals(response)) {
                            hintView.showNoLogin();
                            return;
                        }

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<com.qican.ifarm.beanfromservice.Farm>>() {
                        }.getType();

                        try {
                            mDatas.clear();

                            List<com.qican.ifarm.beanfromservice.Farm> zhuFarms = gson.fromJson(response, type);
                            for (com.qican.ifarm.beanfromservice.Farm farm : zhuFarms) {
                                Farm myFarm = new Farm(farm);
                                mDatas.add(myFarm);
                            }

                            hintView.showContentByData(!mDatas.isEmpty());

                            updateFragmentByData();

                        } catch (Exception e) {
                            hintView.showError(e.getMessage());
                        }
                    }
                });
    }

    public void notifyDatasetChanged() {

        mAdapter.notifyDataSetChanged();

        if (mFragments.isEmpty()) {
        } else {
            for (FragmentWithOnResume fragmentWithOnResume : mFragments)
            {
                fragmentWithOnResume.update();
                fragmentWithOnResume.onResume();
            }

        }
        
        mAdapter.notifyDataSetChanged();
    }

    private void initView(View v) {
        mViewPage = (VerticalViewPager) view.findViewById(R.id.view_pager);
        hintView = new HintView(getActivity(), view.findViewById(R.id.rl_hint));

        // 空白处可以刷新
        hintView.setRefreshListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
    }
}
