package com.qican.ifarm.ui.control;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.SystemAdapter;
import com.qican.ifarm.adapter.UserAdapter;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.beanfromservice.User;
import com.qican.ifarm.listener.BeanCallBack;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.IFarmData;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

@EActivity(R.layout.activity_nearlist)
public class SystemListActivity extends Activity implements PullToRefreshLayout.OnRefreshListener {

    private CommonTools myTool;

    @ViewById(R.id.rl_nodata)
    RelativeLayout rlNoData;

    @ViewById(R.id.pullToRefreshLayout)
    PullToRefreshLayout mRefreshLayout;

    @ViewById(R.id.pullListView)
    PullListView mListView;

    private List<ControlSystem> mDatas;
    private SystemAdapter mAdpater;

    @ViewById(R.id.avi)
    AVLoadingIndicatorView avi;

    @ViewById(R.id.iv_net_error)
    ImageView ivNetError;

    @ViewById(R.id.tv_title)
    TextView tvTitle;

    Farm mFarm;

    @Click
    void llBack() {
        this.finish();
    }

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        mFarm = (Farm) myTool.getParam(Farm.class);

        tvTitle.setText("控制系统");
        initEvent();
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdpater = new SystemAdapter(this, mDatas, R.layout.item_system);
        mListView.setAdapter(mAdpater);
        refreshData();
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void refreshData() {
        avi.smoothToShow();
        ivNetError.setVisibility(View.GONE);
        ivNetError.postDelayed(new Runnable() {
            @Override
            public void run() {
                setData();
                mRefreshLayout.refreshFinish(true);
            }
        }, 2000);
    }

    private void setData() {
        mDatas.clear();
        mDatas.addAll(IFarmFakeData.getSystemList());
        notifyData();
    }

    private void notifyData() {
        mAdpater.notifyDataSetChanged();
        if (mDatas.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
        avi.smoothToHide();
        ivNetError.setVisibility(View.GONE);
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
                mDatas.addAll(IFarmFakeData.getSystemList());
                notifyData();
            }
        }, 1000);
    }
}
