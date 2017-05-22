/**
 * 选择农场
 */
package com.qican.ifarm.ui.control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.bean.Device;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.DataBindUtils;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class SelectFarmActivity extends Activity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {
    private LinearLayout llBack;
    private ControlFunction fun;
    private CommonTools myTool;
    //下拉刷新
    private PullToRefreshLayout mRefreshLayout;
    private PullListView mListView;
    private List<Farm> mData;
    private FunForFarmAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigate_list);
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        fun = (ControlFunction) myTool.getParam(ControlFunction.class);
        if (fun != null)
            ((TextView) (findViewById(R.id.tv_title))).setText(fun.getName());

        mData = IFarmFakeData.getFarmList();
        mAdapter = new FunForFarmAdapter(this, mData, R.layout.item_farm_activity);
        mListView.setAdapter(mAdapter);
    }

    private void initEvent() {
        llBack.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initView() {
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        myTool = new CommonTools(this);
        mRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pullToRefreshLayout);
        mListView = (PullListView) findViewById(R.id.pullListView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.refreshFinish(true);
                myTool.showInfo("已刷新~~");
            }
        }, 1000);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.loadMoreFinish(true);
                myTool.showInfo("没有更多了~~");
            }
        }, 1000);
    }

    class FunForFarmAdapter extends CommonAdapter<Farm> {

        public FunForFarmAdapter(Context context, List<Farm> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(final ViewHolder helper, final Farm item) {
            List<Device> data = IFarmFakeData.getDeviceList();


            helper
                    .setText(R.id.tv_name, item.getName())
                    .setText(R.id.tv_desc, item.getDesc())
                    .setText(R.id.tv_time, item.getTime());

            if (item.getImgUrl() != null)//设置农场照片
                helper.setImageByUrl(R.id.iv_img, item.getImgUrl());

            List<TextView> mTvs = new ArrayList<>();
            mTvs.add((TextView) helper.getView(R.id.tvLabel1));
            mTvs.add((TextView) helper.getView(R.id.tvLabel2));
            mTvs.add((TextView) helper.getView(R.id.tvLabel3));

            DataBindUtils.setLabel(mTvs, (TextView) helper.getView(R.id.tvMore), item.getLabel());
            helper.getView(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myTool.startActivity(item, SystemListActivity_.class);
                }
            });
        }
    }
}
