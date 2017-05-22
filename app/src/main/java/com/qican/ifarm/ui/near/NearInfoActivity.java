/**
 * 添加池塘
 */
package com.qican.ifarm.ui.near;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomListViewEx;
import com.hyphenate.easeui.EaseConstant;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.listener.BeanCBWithTkCk;
import com.qican.ifarm.ui.chat.ChatActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.CircleImageView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class NearInfoActivity extends Activity implements View.OnClickListener {
    private static final int LABEL_MAX_SHOW_LEN = 20;
    private CommonTools myTool;
    private LinearLayout llBack;
    private PullToZoomListViewEx listView;
    private FarmAdapter mAdapter;
    private List<Farm> mData;
    private ComUser friend;
    private TextView tvTitle, tvSignature, tvConcern, tvSendMsg;
    private ImageView ivBg;
    private CircleImageView ivHeadImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearinfo);
        initView();
        initHeader();
        initEvent();
        initData();
    }

    private void initHeader() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        listView.setHeaderLayoutParams(localObject);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        friend = (ComUser) myTool.getParam(new ComUser());
        if (friend != null) {
            tvTitle.setText(friend.getNickName());
            tvSignature.setText(friend.getSignature());

            //设置头像
            if (!"".equals(friend.getHeadImgUrl())) {
                myTool.showImage(friend.getHeadImgUrl(),
                        ivHeadImg, "男"
                                .equals(friend.getSex()) ?
                                R.drawable.default_head_male :
                                R.drawable.default_head_female);
            } else {
                myTool.showDefaultHeadImgBySex(ivHeadImg, friend.getSex());//显示默认头像
            }
            myTool.log("bgurl:" + friend.getBgImgUrl());
            //  设置背景图片
            if (friend.getBgImgUrl() != null) {
                myTool.showImage(friend.getBgImgUrl(),
                        ivBg,
                        R.drawable.defaultbg);
            }
//            myTool.showImage(friend.getHeadImgUrl(), ivHeadImg).showImage(friend.getBgImgUrl(), ivBg);
            setLabel(friend.getLabels());
        }
        // 通过文件路径设置
        mData = new ArrayList<>();
        mData = IFarmFakeData.getFarmList();
        myTool.log(mData.toString());

        mAdapter = new FarmAdapter(this, mData, R.layout.item_farm_activity);
        listView.setAdapter(mAdapter);
        getFarmDataFromNet();
    }

    /**
     * 从服务器获取农场数据！
     */
    private void getFarmDataFromNet() {
        myTool.log("请求参数,userId:" + myTool.getUserId() + ",friendId:" + friend.getId());
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "farm/getUserAroundFarmList")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("aroundPersonId", friend.getId())
                .addParams("beginIndex", "0")
                .addParams("count", "10")
                .build()
                .execute(new BeanCBWithTkCk<List<com.qican.ifarm.beanfromservice.Farm>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        myTool.log("好友农场列表e：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(List<com.qican.ifarm.beanfromservice.Farm> farmList, int id) {
                        //处理token失效问题
                        if (farmList == null) {
                            myTool.showTokenLose();
                            return;
                        }
                        myTool.log("好友农场列表list：" + farmList.toString());
                        for (com.qican.ifarm.beanfromservice.Farm farm : farmList) {
                            Farm myFarm = new Farm(farm);
                            mData.add(myFarm);
                        }
                        mAdapter.notifyDataSetChanged();
//                        notifyData();
                    }
                });
    }

    private void initView() {
        myTool = new CommonTools(this);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        listView = (PullToZoomListViewEx) findViewById(R.id.listview);
        ivBg = (ImageView) listView.getPullRootView().findViewById(R.id.iv_bgimg);
        ivHeadImg = (CircleImageView) listView.getPullRootView().findViewById(R.id.iv_headimg);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSignature = (TextView) listView.getPullRootView().findViewById(R.id.tv_signature);
        tvConcern = (TextView) listView.getPullRootView().findViewById(R.id.tv_concern);
        tvSendMsg = (TextView) listView.getPullRootView().findViewById(R.id.tv_sendmsg);
    }

    private void initEvent() {
        llBack.setOnClickListener(this);
        tvConcern.setOnClickListener(this);
        tvSendMsg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_concern:
                myTool.showInfo("关注待实现");
                break;
            case R.id.tv_sendmsg:
                startActivity(new Intent(this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, friend.getId()));
                break;
        }
    }

    class FarmAdapter extends CommonAdapter<Farm> {

        public FarmAdapter(Context context, List<Farm> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, Farm item) {
            helper
                    .setText(R.id.tv_name, item.getName())
                    .setText(R.id.tv_desc, item.getDesc())
                    .setText(R.id.tv_time, item.getTime());
            if (item.getImgUrl() != null) {
                helper.setImageByUrl(R.id.iv_img, item.getImgUrl(), R.mipmap.default_farm_img);
            }
            //有标签的话，显示标签
            if (item.getLabel() != null)
                setLabel(helper, item.getLabel());
        }
    }

    private void setLabel(@NonNull String label) {
        //最多只显示这几个标签
        int labelRes[] = {R.id.tv_label1, R.id.tv_label2, R.id.tv_label3, R.id.tv_label4};
        TextView tvLabel[] = new TextView[labelRes.length];

        //设置标签不见
        for (int i = 0; i < labelRes.length; i++) {
            tvLabel[i] = (TextView) findViewById(labelRes[i]);
            tvLabel[i].setVisibility(View.GONE);
        }
        //如果设置了标签
        if (label != null) {
            String labels[] = label.split(",");//标签以英文的","隔开
            for (int i = 0; i < labels.length && i < labelRes.length; i++) {
                tvLabel[i].setVisibility(View.VISIBLE);
                tvLabel[i].setText(labels[i]);
            }
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
}
