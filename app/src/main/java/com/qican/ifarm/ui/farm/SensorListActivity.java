package com.qican.ifarm.ui.farm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Sensor;
import com.qican.ifarm.beanfromservice.FarmSensor;
import com.qican.ifarm.listener.BeanCBWithTkCk;
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

@EActivity(R.layout.activity_sensorlist)
public class SensorListActivity extends Activity {
    private static final int LABEL_MAX_SHOW_LEN = 20;
    private CommonTools myTool;
    @ViewById(R.id.rl_nodata)
    RelativeLayout rlNoData;
    @ViewById(R.id.pullToRefreshLayout)
    PullToRefreshLayout refreshLayout;
    @ViewById(R.id.pullListView)
    PullListView mListView;
    private SensorAdapter mAdapter;
    private List<Sensor> mSensorList;

    private Bitmap defaultFarmPic;
    private Farm mFarm;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        defaultFarmPic = BitmapFactory.decodeResource(getResources(), R.mipmap.default_farm_img);

        mFarm = new Farm();
        mFarm = (Farm) myTool.getParam(mFarm);
    }

    private void initData() {
        mSensorList = new ArrayList<>();
        mAdapter = new SensorAdapter(this, mSensorList, R.layout.item_sensor);
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
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "farmSensor/getFarmSensorsList")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("farmId", mFarm.getId())
                .build()
                .execute(new BeanCBWithTkCk<List<FarmSensor>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        myTool.log("农场列表e：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(List<FarmSensor> sensorList, int id) {
                        //处理token失效问题
                        if (sensorList == null) {
                            myTool.showTokenLose();
                            return;
                        }
                        myTool.log("农场列表list：" + sensorList.toString());
                        for (FarmSensor farmSensor : sensorList) {
                            Sensor sensor = new Sensor(farmSensor);
                            mSensorList.add(sensor);
                        }
                        notifyData();
                    }
                });
    }

    private void notifyData() {
        myTool.log("mDatas大小：" + mSensorList.size() + "\nmDatas内容：" + mSensorList.toString());
        mAdapter.notifyDataSetChanged();
//        rlNodata.setVisibility(mDatas.isEmpty() ? View.VISIBLE : View.GONE);//直接显示
        if (mSensorList.isEmpty()) {
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

    @Click
    void llAddFarm() {
        myTool.startActivity(AddFarmActivity_.class);//启动添加农场
    }

    @Click
    void llBack() {
        this.finish();
    }

    class SensorAdapter extends CommonAdapter<Sensor> {
        public SensorAdapter(Context context, List<Sensor> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, final Sensor item) {
            helper.setText(R.id.tv_name, item.getType())
                    .setText(R.id.tv_location, item.getLocation())
                    .setText(R.id.tv_time, item.getCreateTime());
        }
    }
}
