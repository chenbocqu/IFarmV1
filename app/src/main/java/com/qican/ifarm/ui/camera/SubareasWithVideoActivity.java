/**
 * 带有农场视频的分区
 */
package com.qican.ifarm.ui.camera;

import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.DataAdapter;
import com.qican.ifarm.adapter.SubareaAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.listener.OnItemClickListener;
import com.qican.ifarm.listener.OnVideoPlayListener;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.IFarmVideoUtil;
import com.qican.ifarm.utils.VideoUtil;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_subareaoffarm_with_video)
public class SubareasWithVideoActivity extends Activity implements OnItemClickListener<Subarea> {

    private CommonTools myTool;

    @ViewById(R.id.rl_nodata)
    RelativeLayout rlNoData;

    @ViewById(R.id.pullToRefreshLayout)
    PullToRefreshLayout refreshLayout;

    @ViewById(R.id.pullListView)
    PullListView mListView;

    @ViewById(R.id.avi)
    AVLoadingIndicatorView aviLoadData;

    @ViewById(R.id.tv_title)
    TextView tvTitle;

    SubareaAdapter mAdapter;
    private List<Subarea> mDatas;
    EZCamera mEZCamera;

    IFarmVideoUtil iFarmVideoUtil;
    NetRequest netRequest;

    Farm mFarm;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        netRequest = new NetRequest(this);
        mFarm = (Farm) myTool.getParam(Farm.class);
        initView();
    }

    private void initView() {
    }

    private void initData() {
        tvTitle.setText("选择分区");

        mEZCamera = new EZCamera();
        if (mEZCamera.getDeviceSerial() == null) {
//            mEZCamera.setDeviceSerial("626439264");
//            mEZCamera.setVerifyCode("SEGHDP");
            mEZCamera.setDeviceSerial("761008117");
            mEZCamera.setVerifyCode("PGGSWF");
            mEZCamera.setChannelNo(1);
        }

        iFarmVideoUtil = new IFarmVideoUtil(this, mEZCamera);
        iFarmVideoUtil.startRealPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initListData();
    }

    private void initListData() {
        showNoData();
        mDatas = new ArrayList<>();
        mAdapter = new SubareaAdapter(SubareasWithVideoActivity.this, mDatas, R.layout.item_subarea);
        mAdapter.setItemClickListener(this);
        mListView.setAdapter(mAdapter);

        netRequest.getSubareaData(mFarm, new DataAdapter() {
            @Override
            public void subareas(List<Subarea> data) {
                mDatas.addAll(data);
                aviLoadData.setVisibility(View.GONE);
                notifyData();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void notifyData() {
        myTool.log("mDatas大小：" + mDatas.size() + "\nmDatas内容：" + mDatas.toString());
        mAdapter.notifyDataSetChanged();
        if (mDatas.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
    }

    private void showNoData() {
        rlNoData.setVisibility(View.VISIBLE);
        aviLoadData.setVisibility(View.GONE);
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

    @Click
    void llBack() {
        this.finish();
    }

    @Override
    public void onItemClick(ViewHolder helper, Subarea item) {
        myTool.startActivity(item, VideoListActivity_.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iFarmVideoUtil.stopRealPlay();
    }
}
