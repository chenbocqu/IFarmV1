/**
 * 选择农场
 */
package com.qican.ifarm.ui.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.bean.Device;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

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
        fun = (ControlFunction) myTool.getParam(new ControlFunction("dd"));
        if (fun != null)
            ((TextView) (findViewById(R.id.tv_title))).setText(fun.getName());
        mData = IFarmFakeData.getFarmList();
        mAdapter = new FunForFarmAdapter(this, mData, R.layout.item_farm_fun);
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
        public void convert(ViewHolder helper, Farm item) {
            List<Device> data = IFarmFakeData.getDeviceList();
            GridView gridView = helper.getView(R.id.gridView);

            helper
                    .setText(R.id.tv_name, item.getName())
                    .setText(R.id.tv_desc, item.getDesc());
            if (item.getImgUrl() != null)//设置农场照片
                helper.setImageByUrl(R.id.iv_img, item.getImgUrl());

            //TODO: 动画有点bug
            gridView.setAdapter(new CommonAdapter<Device>(SelectFarmActivity.this, data, R.layout.item_device) {
                @Override
                public void convert(ViewHolder helper, Device item) {
                    helper.setText(R.id.tv_item, item.getName());
                    final ImageView ivItem = helper.getView(R.id.iv_item);
                    ivItem.setImageBitmap(ConstantValue.iconFunOn[fun.getIndex()]);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            while (true) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        YoYo.with(Techniques.Wobble)
                                                .duration(1000)
                                                .playOn(ivItem);
                                    }
                                });
                                try {
                                    Thread.sleep(2500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
            });
        }
    }
}
