package com.qican.ifarm.ui_v2.farm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.DeviceParaAdapter;
import com.qican.ifarm.bean.DevicePara;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.data.DeviceDataRequest;
import com.qican.ifarm.listener.InfoRequestListener;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class RealtimeDataFragment extends Fragment {

    private View view;
    private MonitorNode mNode;

    List<DevicePara> mDatas;
    DeviceParaAdapter mAdapter;
    CommonTools myTool;
    RelativeLayout rlNoData;
    TextView tvUpdateTime;

    GridView gridView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_realtime_data, container, false);
        initView(view);
        initData();
        initEvents();
        return view;
    }

    // 刷新数据
    public void onRefresh(final SwipeRefreshLayout srl) {
        if (mNode == null) return;
        DeviceDataRequest.getNodeInfoById(getActivity(), mNode, new InfoRequestListener<MonitorNode>() {
            @Override
            public void onFail(Exception e) {
                myTool.showInfo(e.getMessage());
                srl.setRefreshing(false);
            }

            @Override
            public void onSuccess(MonitorNode node) {
                srl.setRefreshing(false);
                if (node == null || mNode.getNodeDatas() == null)
                    return;

                mNode = node;
                mDatas = mNode.getNodeDatas();
                notifyData();
            }
        });
    }

    private void initEvents() {
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        mNode = (MonitorNode) bundle.getSerializable(DeviceDataActivity.KEY_NODE_INFO);
        mDatas = mNode.getNodeDatas();

        if (mDatas == null) mDatas = new ArrayList<>();
        mAdapter = new DeviceParaAdapter(getActivity(), mDatas, R.layout.item_device_para);
        gridView.setAdapter(mAdapter);
        notifyData();
    }

    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        gridView = (GridView) view.findViewById(R.id.gridView);
        rlNoData = (RelativeLayout) view.findViewById(R.id.rl_nodata);
        tvUpdateTime = (TextView) view.findViewById(R.id.tv_update_time);
    }

    private void notifyData() {
        tvUpdateTime.setText(TimeUtils.parseTime(mNode.getUpdateTime()));
        mAdapter.notifyDataSetChanged();
        if (mDatas == null || mDatas.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
        myTool.showInfo("实时监测数据已更新 ...");
    }

    private void showNoData() {
        rlNoData.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(rlNoData);
    }

    private void hideNoData() {
        YoYo.with(Techniques.FadeOut)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rlNoData.setVisibility(View.GONE);
                    }
                })
                .duration(100)
                .playOn(rlNoData);
    }
}
