package com.qican.ifarm.ui_v2.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonChooseAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.listener.ChooseItems;
import com.qican.ifarm.listener.OnItemClickListener;
import com.qican.ifarm.listener.OnItemsLoadListener;
import com.qican.ifarm.listener.PopwindowListener;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;


public abstract class Window4ComSelected<T extends ChooseItems> extends PopupWindow implements OnClickListener, OnItemClickListener<T>, PullToRefreshLayout.OnRefreshListener {

    PopwindowListener<Object, Object> mCallBack;

    View mMainView;
    AVLoadingIndicatorView loadView;
    RelativeLayout rlNodata;
    PullListView pullListView;
    PullToRefreshLayout pullToRefreshLayout;

    protected CommonTools myTool;
    T mBean;
    protected List<T> mDatas;
    protected NetRequest netRequest;
    CommonChooseAdapter<T> mAdpater;
    Activity mActivity;

    public Window4ComSelected(Activity context) {
        super(context);

        mActivity = context;

        myTool = new CommonTools(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMainView = inflater.inflate(R.layout.popwin_select_items, null);
        initView(mMainView);
        initDatas();
        initEvent();
        seWindowStyle();
    }

    private void initDatas() {

        ((TextView) mMainView.findViewById(R.id.tv_title)).setText(setTitle());
//        mDatas = IFarmFakeData.getFarmList();

        mDatas = new ArrayList<>();
    }

    // 刷新数据前后调用
    protected void setRefreshing(boolean isRefresh) {
        if (isRefresh) {
            rlNodata.setVisibility(View.GONE);
            loadView.setVisibility(View.VISIBLE);
        } else {
            loadView.setVisibility(View.GONE);
            pullToRefreshLayout.refreshFinish(true);
        }
    }

    protected abstract String setTitle();

    protected abstract void refreshData();

    public void notifyDatasetChanged() {
        mAdpater.notifyDataSetChanged();
        rlNodata.setVisibility(mDatas.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void seWindowStyle() {
        //设置SelectPicPopupWindow的View
        this.setContentView(mMainView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popwindow_anim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        myTool.setHeightByWindow(mMainView.findViewById(R.id.ll_list), 1.2 / 2f);
    }

    private void initEvent() {
        //取消按钮
        mMainView.findViewById(R.id.tv_ok).setOnClickListener(this);
        mMainView.findViewById(R.id.tv_cannel).setOnClickListener(this);
        pullToRefreshLayout.setOnRefreshListener(this);

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMainView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMainView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void initView(View view) {
        loadView = (AVLoadingIndicatorView) mMainView.findViewById(R.id.avi_list);
        rlNodata = (RelativeLayout) mMainView.findViewById(R.id.rl_nodata);
        pullListView = (PullListView) mMainView.findViewById(R.id.pullListView);
        pullToRefreshLayout = (PullToRefreshLayout) mMainView.findViewById(R.id.pullToRefreshLayout);

        netRequest = new NetRequest(mActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_cannel:
                dismiss();
                break;

            case R.id.tv_ok:
                if (mCallBack != null) mCallBack.infoChanged(this, mBean);
                dismiss();
                break;
        }
    }

    public void setOnPowwinListener(PopwindowListener<Object, Object> callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void onItemClick(ViewHolder helper, T item) {
        mBean = item;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        refreshData();
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshLayout.loadMoreFinish(true);
            }
        }, 1000);
    }

    protected void setDatas(List<T> datas) {
        mDatas = datas;

        mAdpater = new CommonChooseAdapter<T>(mActivity, mDatas, R.layout.item_common_choose);
        mAdpater.setItemClickListener(this);

        pullListView.setAdapter(mAdpater);

        myTool.log("size: " + mDatas.size());
        setRefreshing(false);
        notifyDatasetChanged();
    }
}