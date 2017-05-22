package com.qican.ifarm.ui.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.DataAdapter;
import com.qican.ifarm.adapter.FarmAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.listener.OnItemClickListener;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.util.List;

public class FarmListOfCameraFragment extends Fragment implements PullToRefreshLayout.OnRefreshListener, OnItemClickListener<Farm> {
    private View view;
    private CommonTools myTool;

    private List<Farm> mDatas;
    private FarmAdapter mAdpater;

    //下拉刷新
    private PullToRefreshLayout mRefreshLayout;
    private PullListView mListView;
    NetRequest netRequest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.common_list, container, false);
        initView(view);
        initDatas();
        initEvent();

        return view;
    }

    private void initDatas() {
        mDatas = IFarmFakeData.getFarmList();
        mAdpater = new FarmAdapter(getActivity(), mDatas, R.layout.item_common_farm);
        mAdpater.setOnItemClickListener(this);

        netRequest.getFarmList(new DataAdapter() {
            @Override
            public void farmList(List<Farm> farmList) {
                if (farmList != null) {
                    mDatas.addAll(farmList);
                    mAdpater.notifyDataSetChanged();
                }
            }
        });

        mListView.setAdapter(mAdpater);
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initView(View v) {
        mRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.pullToRefreshLayout);
        mListView = (PullListView) v.findViewById(R.id.pullListView);

        myTool = new CommonTools(getActivity());
        netRequest = new NetRequest(getActivity());
    }

    @Override
    public void onRefresh(final PullToRefreshLayout layout) {
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.refreshFinish(true);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout layout) {
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.loadMoreFinish(true);
            }
        }, 1000);
    }

    @Override
    public void onItemClick(ViewHolder helper, Farm item) {
//        myTool.showInfo(item.getName() + ",当前时间：" + DataBindUtils.getCurrentTime());
//        myTool.startActivity(item, SubareasWithVideoActivity.class);
        myTool.startActivity(item, SubareasWithVideoActivity_.class);
    }
}
