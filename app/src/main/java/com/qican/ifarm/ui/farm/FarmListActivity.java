package com.qican.ifarm.ui.farm;

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
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.listener.BeanCBWithTkCk;
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
    private Bitmap defaultFarmPic;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        defaultFarmPic = BitmapFactory.decodeResource(getResources(), R.mipmap.default_farm_img);
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdapter = new FarmAdaper(this, mDatas, R.layout.item_farm_activity);
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
//        mDatas.addAll(IFarmData.getFarmList());
//        notifyData();
        OkHttpUtils.post().url(myTool.getServAdd() + "farm/getFarmColletorsList")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new BeanCBWithTkCk<List<com.qican.ifarm.beanfromservice.Farm>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        myTool.log("农场列表e：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(List<com.qican.ifarm.beanfromservice.Farm> farmList, int id) {
                        //处理token失效问题
                        if (farmList == null) {
                            myTool.showTokenLose();
                            return;
                        }
                        myTool.log("农场列表list：" + farmList.toString());
                        for (com.qican.ifarm.beanfromservice.Farm farm : farmList) {
                            Farm myFarm = new Farm(farm);
                            mDatas.add(myFarm);
                        }
                        notifyData();
                    }
                });
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


    class FarmAdaper extends ComAdapter<Farm> {

        public FarmAdaper(Context context, List<Farm> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, final Farm item) {

            LinearLayout rlItem = helper.getView(R.id.ll_item);

            if (item.getImgUrl() != null) {
                helper.setImageByUrl(R.id.iv_img, item.getImgUrl(), R.mipmap.default_farm_img);
            } else {
                ((ImageView) helper.getView(R.id.iv_img)).setImageBitmap(defaultFarmPic);
            }
            helper.setText(R.id.tv_name, item.getName());
            helper.setText(R.id.tv_time, item.getTime());
            helper.setText(R.id.tv_desc, item.getDesc());
            if (item.getLabel() != null) {
                setLabel(helper, item.getLabel());//设置农场标签
            }

            rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转至选择分区页面
                    myTool.startActivity(item, SubareasOfFarmActivity.class);
//                    myTool.startActivity(item, SubareaListActivity_.class);
//                    myTool.startActivity(item, FarmActivity.class);
                }
            });
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

        boolean showMore = false;
        //全部设置为不见
        for (int i = 0; i < tvLabels.size(); i++) {
            tvLabels.get(i).setVisibility(View.GONE);
        }

        List<String> labelList = label.getLabelList();
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
