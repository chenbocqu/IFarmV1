package com.qican.ifarm.ui.find;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Expert;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.listener.BeanCBWithTkCk;
import com.qican.ifarm.ui.farm.AddFarmActivity_;
import com.qican.ifarm.ui.subarea.SubareasOfFarmActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

@EActivity(R.layout.activity_expertlist)
public class ExpertsListActivity extends Activity {
    private static final int LABEL_MAX_SHOW_LEN = 20;
    private CommonTools myTool;

    @ViewById(R.id.rl_nodata)
    RelativeLayout rlNoData;

    @ViewById(R.id.pullToRefreshLayout)
    PullToRefreshLayout refreshLayout;

    @ViewById(R.id.pullListView)
    PullListView mListView;

    private ExpertAdapter mAdapter;
    private List<Expert> mDatas;
    private Bitmap defaultFarmPic;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        defaultFarmPic = BitmapFactory.decodeResource(getResources(), R.mipmap.default_farm_img);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                pullToRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.refreshFinish(true);
                    }
                },1000);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                pullToRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.loadMoreFinish(true);
                    }
                },1000);
            }
        });
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdapter = new ExpertAdapter(this, mDatas, R.layout.item_expert);
        mListView.setAdapter(mAdapter);

        showNoData();
        getFarmData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void getFarmData() {
        mDatas.add(new Expert(
                "张三",
                "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=785610095,2402278722&fm=117&gp=0.jpg",
                "有时候、无奈也是一种美"));
        mDatas.add(new Expert(
                "李四",
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=582769181,1316896973&fm=111&gp=0.jpg",
                "我的少年你在忧愁是不是思念让你深锁眉头"));
        mDatas.add(new Expert(
                "王麻子",
                "https://ss0.baidu.com/-Po3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=836042e13f7adab422d01c43bbd5b36b/bf096b63f6246b60774a43bde2f81a4c500fa2b7.jpg",
                "洧些囚 越昰抓緊越昰遠離"));
        notifyData();
    }

    private void notifyData() {
        myTool.log("mDatas大小：" + mDatas.size() + "\nmDatas内容：" + mDatas.toString());
        mAdapter.notifyDataSetChanged();
//        rlNodata.setVisibility(mDatas.isEmpty() ? View.VISIBLE : View.GONE);//直接显示
        if (mDatas.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
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


    class ExpertAdapter extends CommonAdapter<Expert> {

        public ExpertAdapter(Context context, List<Expert> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, final Expert item) {

            LinearLayout rlItem = helper.getView(R.id.ll_item);

            if (item.getImgUrl() != null) {
                helper.setImageByUrl(R.id.iv_img, item.getImgUrl(), R.mipmap.default_farm_img);
            } else {
                ((ImageView) helper.getView(R.id.iv_img)).setImageBitmap(defaultFarmPic);
            }
            helper.setText(R.id.tv_name, item.getName());
            helper.setText(R.id.tv_desc, item.getDesc());
        }
    }

    @Click
    void llBack() {
        this.finish();
    }
}
