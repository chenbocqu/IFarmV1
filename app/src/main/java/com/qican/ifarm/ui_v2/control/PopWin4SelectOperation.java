package com.qican.ifarm.ui_v2.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Operations;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.listener.OnItemClickListener;
import com.qican.ifarm.listener.PopwindowListener;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;


public class PopWin4SelectOperation extends PopupWindow implements OnClickListener, PullToRefreshLayout.OnRefreshListener, OnItemClickListener<Operations> {

    PopwindowListener<Object, Object> mCallBack;

    View mMainView;
    AVLoadingIndicatorView loadView;
    RelativeLayout rlNodata;
    PullListView pullListView;
    PullToRefreshLayout pullToRefreshLayout;

    CommonTools myTool;
    Operations mItem;

    List<Operations> mDatas;
    NetRequest netRequest;
    ComAdapter<Operations> mAdpater;
    Activity mActivity;

    public PopWin4SelectOperation(Activity context, List<Operations> chooseItems) {
        super(context);
        mDatas = chooseItems;

        mActivity = context;
        myTool = new CommonTools(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMainView = inflater.inflate(R.layout.popwin_select_operation, null);
        initView(mMainView);
        initDatas();
        initEvent();
        seWindowStyle();
    }

    private void initDatas() {
        mAdpater = new ComAdapter<Operations>(mActivity, mDatas, R.layout.item_operation_choose) {
            @Override
            public void convert(final ViewHolder helper, final Operations item) {

                helper.setText(R.id.tv_name, item.getName());
                final CheckBox cbSelected = helper.getView(R.id.cb_selected);

                cbSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItem = item;
                        item.setSelected(cbSelected.isChecked());
                        if (cbSelected.isChecked()) {
                            removeChecked(item);
                        }
                        notifyDataSetChanged(); //处理好状态后要通知ListView重新装填数据（即是数据发生变化）

                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(helper, item);
                        }
                    }
                });
                cbSelected.setChecked(item.isSelected());
            }
        };
        mAdpater.setItemClickListener(this);

        pullListView.setAdapter(mAdpater);
    }

    private void removeChecked(Operations defaultBean) {
        for (Operations bean : mDatas) {
            if (!(bean.equals(defaultBean))) {
                bean.setSelected(false);
            }
        }
    }

    private void notifyDatasetChanged() {
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
                if (mCallBack != null) mCallBack.infoChanged(this, mItem);
                dismiss();
                break;
        }
    }

    public void setOnPowwinListener(PopwindowListener<Object, Object> callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshLayout.refreshFinish(true);
            }
        }, 1000);
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

    @Override
    public void onItemClick(ViewHolder helper, Operations item) {

    }

    public void setmDatas(List<Operations> mDatas) {
        this.mDatas = mDatas;
        notifyDatasetChanged();
    }
}