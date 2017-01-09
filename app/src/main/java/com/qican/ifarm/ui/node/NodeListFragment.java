package com.qican.ifarm.ui.node;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.ui.node.NodeInfoActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.util.List;

import cn.bingoogolapple.progressbar.BGAProgressBar;

public class NodeListFragment extends Fragment implements PullToRefreshLayout.OnRefreshListener {
    private View view;
    private String TAG = "NodeListFragment";

    private CommonTools myTool;

    private List<MonitorNode> mDatas;
    private MonitorNodeAdapter mAdpater;

    private String url = ConstantValue.SERVICE_ADDRESS + "findPondByUser";

    //下拉刷新
    private PullToRefreshLayout mRefreshLayout;
    private PullListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_monitorlist, container, false);
        initView(view);
        initDatas();
        initEvent();

        return view;
    }

    private void initDatas() {
        mDatas = IFarmFakeData.getMontiorList();

        mAdpater = new MonitorNodeAdapter(getActivity(), mDatas, R.layout.item_monitor_node);
        mListView.setAdapter(mAdpater);
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initView(View v) {
        mRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.pullToRefreshLayout);
        mListView = (PullListView) v.findViewById(R.id.pullListView);

        myTool = new CommonTools(getActivity());
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }


    class MonitorNodeAdapter extends CommonAdapter<MonitorNode> {
        public MonitorNodeAdapter(Context context, List<MonitorNode> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, MonitorNode node) {
            BGAProgressBar pbHumidity = helper.getView(R.id.pb_humidity);
            BGAProgressBar pbTemperature1 = helper.getView(R.id.pb_temperature1);
            BGAProgressBar pbTemperature2 = helper.getView(R.id.pb_temperature2);
            BGAProgressBar pbWaterContent = helper.getView(R.id.pb_watercontent);

            pbHumidity.setProgress((int) (Float.parseFloat(node.getHumidity())));
            pbTemperature1.setProgress((int) (Float.parseFloat(node.getTemperature1())));
            pbTemperature2.setProgress((int) (Float.parseFloat(node.getTemperature2())));
            pbWaterContent.setProgress((int) (Float.parseFloat(node.getWaterContent())));

            helper
                    .setText(R.id.tv_nodename, node.getName())
                    .setText(R.id.tv_humidity, node.getHumidity() + "%")
                    .setText(R.id.tv_temperature1, node.getTemperature1() + "℃")
                    .setText(R.id.tv_temperature2, node.getTemperature2() + "℃")
                    .setText(R.id.tv_watercontent, node.getWaterContent() + "%")
                    .setText(R.id.tv_updatetime, node.getUpdateTime());

            if (node.getImgUrl() != null) {
                helper.setImageByUrl(R.id.iv_node_img, node.getImgUrl());
            }
            helper.getView(R.id.rl_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myTool.startActivity(NodeInfoActivity.class);
                }
            });
        }
    }
}
