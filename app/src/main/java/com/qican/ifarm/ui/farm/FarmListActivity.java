package com.qican.ifarm.ui.farm;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.listener.BeanCBWithTkCk;
import com.qican.ifarm.listener.BeanCallBack;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

@EActivity(R.layout.activity_farmlist)
public class FarmListActivity extends Activity {
    private static final int LABEL_MAX_SHOW_LEN = 20;
    private CommonTools myTool;
    @ViewById(R.id.rl_nodata)
    RelativeLayout rlNoData;
    @ViewById(R.id.pullToRefreshLayout)
    PullToRefreshLayout refreshLayout;
    @ViewById(R.id.pullListView)
    PullListView mListView;
    private FarmAdaper mAdapter;
    private List<Farm> mDatas;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        initData();
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdapter = new FarmAdaper(this, mDatas, R.layout.item_farm_activity);
        mListView.setAdapter(mAdapter);
        showNoData();
        getFarmData();
    }

    private void getFarmData() {
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "farm/getFarmColletorsList")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new BeanCBWithTkCk<List<com.qican.ifarm.beanfromzhu.Farm>>(this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("农场列表e：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(List<com.qican.ifarm.beanfromzhu.Farm> farmList, int id) {
                        //处理token失效问题
                        if (farmList == null) {
                            myTool.showTokenLose();
                            return;
                        }
                        myTool.log("农场列表list：" + farmList.toString());
                        for (com.qican.ifarm.beanfromzhu.Farm farm : farmList) {
                            Farm myFarm = new Farm(farm);
                            mDatas.add(myFarm);
                        }
                        notifyData();
                    }
                });
    }

    private void notifyData() {
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


    class FarmAdaper extends CommonAdapter<Farm> {

        public FarmAdaper(Context context, List<Farm> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, Farm item) {
            if (item.getImgUrl() != null) {
                helper.setImageByUrl(R.id.iv_img, item.getImgUrl());
            }
            helper.setText(R.id.tv_name, item.getName());
            helper.setText(R.id.tv_time, item.getTime());
            helper.setText(R.id.tv_desc, item.getDesc());
            setLabel(helper, item.getLabel());//设置农场标签
        }
    }

    /**
     * 设置标签
     */
    private void setLabel(ViewHolder helper, Label label) {
        List<TextView> tvLabels = new ArrayList<>();
        tvLabels.add((TextView) helper.getView(R.id.tvLabel1));
        tvLabels.add((TextView) helper.getView(R.id.tvLabel2));
        tvLabels.add((TextView) helper.getView(R.id.tvLabel3));
        TextView tvMore = helper.getView(R.id.tvMore);

        List<String> labelList = label.getLabelList();
        boolean showMore = false;
        //全部设置为不见
        for (int i = 0; i < tvLabels.size(); i++) {
            tvLabels.get(i).setVisibility(View.GONE);
        }
        int totalLen = 0;
        for (int i = 0; i < tvLabels.size() && i < labelList.size(); i++) {
            totalLen = totalLen + labelList.get(i).length();
            if (totalLen > LABEL_MAX_SHOW_LEN) {
                showMore = true;
                break;
            }
            tvLabels.get(i).setVisibility(View.VISIBLE);
            tvLabels.get(i).setText(labelList.get(i));
        }
        // 本身比标签容量多，或者显示不够完全，则显示更多
        tvMore.setVisibility(
                labelList.size() > tvLabels.size() ||
                        showMore ?
                        View.VISIBLE : View.GONE);

    }

    @Click
    void llAddFarm() {
        myTool.startActivity(AddFarmActivity_.class);//启动添加农场
    }

    @Click
    void llBack() {
        this.finish();
    }
}
