package com.qican.ifarm.ui.near;

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
import com.qican.ifarm.adapter.UserAdapter;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.beanfromservice.User;
import com.qican.ifarm.listener.BeanCallBack;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
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
public class NearListActivity extends Activity implements PullToRefreshLayout.OnRefreshListener {

    private CommonTools myTool;

    @ViewById(R.id.rl_nodata)
    RelativeLayout rlNoData;

    @ViewById(R.id.pullToRefreshLayout)
    PullToRefreshLayout mRefreshLayout;

    @ViewById(R.id.pullListView)
    PullListView mListView;

    private List<ComUser> mDatas;
    private UserAdapter mAdpater;

    private final int REFRESH_CNT = 20;//每次刷新多少个数据

    @ViewById(R.id.avi)
    AVLoadingIndicatorView avi;

    @ViewById(R.id.iv_net_error)
    ImageView ivNetError;

    @ViewById(R.id.tv_title)
    TextView tvTitle;

    @Click
    void llBack() {
        this.finish();
    }

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        tvTitle.setText("附近的人");

        initEvent();
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdpater = new UserAdapter(this, mDatas, R.layout.item_near_person);
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

        OkHttpUtils.post().url(myTool.getServAdd()+ "user/getUsersListAround")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("beginIndex", "0")
                .addParams("count", String.valueOf(REFRESH_CNT))
                .build()
                .execute(new BeanCallBack<List<User>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("用户列表获取异常：" + e.toString());
                        mRefreshLayout.refreshFinish(true);
                        avi.smoothToHide();
                        ivNetError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(List<User> users, int id) {
                        mRefreshLayout.refreshFinish(true);
                        if (users.isEmpty()) {
                            myTool.showInfo("没有查询到数据！");
                            return;
                        }
                        mDatas.clear();
                        for (int i = 0; i < users.size(); i++) {
                            mDatas.add(new ComUser(users.get(i)));
                            myTool.log(users.get(i).toString());
                        }
                        notifyData();
                    }
                });
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
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        OkHttpUtils.post().url(myTool.getServAdd() + "user/getUsersListAround")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("beginIndex", String.valueOf(mDatas.size()))
                .addParams("count", String.valueOf(REFRESH_CNT))
                .build()
                .execute(new BeanCallBack<List<User>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("用户列表获取异常：" + e.toString());
                        mRefreshLayout.loadMoreFinish(true);
                    }

                    @Override
                    public void onResponse(List<User> users, int id) {
                        mRefreshLayout.loadMoreFinish(true);
                        if (users.isEmpty()) {
                            myTool.showInfo("没有更多数据了！");
                            return;
                        }
                        for (int i = 0; i < users.size(); i++) {
                            mDatas.add(new ComUser(users.get(i)));
                            myTool.log(users.get(i).toString());
                        }
                        notifyData();
                    }
                });
    }
}
