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

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.beanfromzhu.User;
import com.qican.ifarm.listener.BeanCallBack;
import com.qican.ifarm.ui.node.NodeInfoActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.androidannotations.annotations.AfterViews;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.progressbar.BGAProgressBar;
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
    private final int REFRESH_CNT = 20;
    Bitmap female;
    Bitmap male;

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
        female = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.female);
        male = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.male);
        mDatas = new ArrayList<>();
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "user/getUsersListAround")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("beginIndex", "0")
                .addParams("count", String.valueOf(REFRESH_CNT))
                .build()
                .execute(new BeanCallBack<List<User>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("用户列表获取异常：" + e.toString());
                    }

                    @Override
                    public void onResponse(List<User> users, int id) {
                        for (int i = 0; i < users.size(); i++) {
                            mDatas.add(new ComUser(users.get(i)));
                            myTool.log(users.get(i).toString());
                        }
                        mAdpater = new UserAdapter(getActivity(), mDatas, R.layout.item_near_person);
                        mListView.setAdapter(mAdpater);
                    }
                });
//        mDatas = IFarmFakeData.getUserList();
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
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatas.clear();
                mDatas.addAll(IFarmFakeData.getUserList());
                mAdpater.notifyDataSetChanged();
                mRefreshLayout.refreshFinish(true);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatas.addAll(IFarmFakeData.getUserList());
                mAdpater.notifyDataSetChanged();
                mRefreshLayout.loadMoreFinish(true);
            }
        }, 1000);
    }


    class UserAdapter extends CommonAdapter<ComUser> {
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
