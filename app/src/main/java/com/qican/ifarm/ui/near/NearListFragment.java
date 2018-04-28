package com.qican.ifarm.ui.near;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.beanfromservice.User;
import com.qican.ifarm.listener.BeanCallBack;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class NearListFragment extends Fragment implements PullToRefreshLayout.OnRefreshListener {
    private View view;
    private String TAG = "NearListFragment";

    private CommonTools myTool;

    private List<ComUser> mDatas;
    private UserAdapter mAdpater;

    //下拉刷新
    private PullToRefreshLayout mRefreshLayout;
    private PullListView mListView;
    private final int REFRESH_CNT = 20;//每次刷新多少个数据
    Bitmap female;
    Bitmap male;
    RelativeLayout rlNodata;
    AVLoadingIndicatorView avi;
    ImageView ivNetError;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_near, container, false);
        initView(view);
        initEvent();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDatas();
    }

    private void initDatas() {
        female = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.default_head_female);
        male = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.default_head_male);
        mDatas = new ArrayList<>();
        mAdpater = new UserAdapter(getActivity(), mDatas, R.layout.item_near_person);
        mListView.setAdapter(mAdpater);
        refreshData();
    }

    private void refreshData() {
        avi.smoothToShow();
        ivNetError.setVisibility(View.GONE);

        OkHttpUtils.post().url(myTool.getServAdd() + "user/getUsersListAround")
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

    private void hideNoData() {
        YoYo.with(Techniques.FadeOut)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rlNodata.setVisibility(View.GONE);
                    }
                })
                .duration(100)
                .playOn(rlNodata);
    }

    private void showNoData() {
        rlNodata.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(rlNodata);
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initView(View v) {
        mRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.pullToRefreshLayout);
        mListView = (PullListView) v.findViewById(R.id.pullListView);
        rlNodata = (RelativeLayout) v.findViewById(R.id.rl_nodata);
        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        ivNetError = (ImageView) v.findViewById(R.id.iv_net_error);

        myTool = new CommonTools(getActivity());
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        refreshData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        OkHttpUtils.post().url(myTool.getServAdd()+ "user/getUsersListAround")
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


    class UserAdapter extends ComAdapter<ComUser> {
        public UserAdapter(Context context, List<ComUser> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, final ComUser near) {
            helper
                    .setText(R.id.tv_nickname, near.getNickName())
                    .setText(R.id.tv_desc, near.getSignature())
                    .setText(R.id.tv_time, near.getLastLoginTime());
            myTool.showSex(near.getSex(), (ImageView) helper.getView(R.id.iv_sex));
            if (near.getHeadImgUrl() != null) {
                myTool.showImage(near.getHeadImgUrl(), (ImageView) helper.getView(R.id.iv_headimg),
                        "男".equals(near.getSex()) ? R.drawable.default_head_male : R.drawable.default_head_female);
//                helper.setImageByUrl(R.id.iv_headimg, near.getHeadImgUrl());
            } else {
                //直接根据性别来设置头像图标
                ImageView headImg = helper.getView(R.id.iv_headimg);
                if (near.getSex() != null) {
                    switch (near.getSex()) {
                        case "男":
                            headImg.setImageBitmap(male);
                            break;
                        case "女":
                            headImg.setImageBitmap(female);
                            break;
                        default:
                            break;
                    }
                }
            }
            helper.getView(R.id.rl_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myTool.startActivity(near, NearInfoActivity.class);
                }
            });
            setLabels(helper, near);
        }

        private void setLabels(ViewHolder helper, ComUser near) {
            //最多只显示这几个标签
            int labelRes[] = {R.id.tv_label1, R.id.tv_label2, R.id.tv_label3};
            //设置标签不见
            for (int i = 0; i < labelRes.length; i++) {
                helper.getView(labelRes[i]).setVisibility(View.GONE);
            }
            //如果设置了标签
            if (near.getLabels() != null) {
                String labels[] = near.getLabels().split(",");//标签以英文的","隔开
                for (int i = 0; i < labels.length && i < labelRes.length; i++) {
                    helper.getView(labelRes[i]).setVisibility(View.VISIBLE);
                    helper.setText(labelRes[i], labels[i]);
                }
            }
        }
    }
}
