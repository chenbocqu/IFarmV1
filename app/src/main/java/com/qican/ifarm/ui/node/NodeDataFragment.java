package com.qican.ifarm.ui.node;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.DataAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Sensor;
import com.qican.ifarm.beanfromservice.FarmPara;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.ui.farm.DownloadExcelActivity_;
import com.qican.ifarm.utils.CommonTools;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.progressbar.BGAProgressBar;

public class NodeDataFragment extends Fragment implements View.OnClickListener {
    private View view;
    private String TAG = "NodeDataFragment";
    private Farm mFarm;
    private GridView gridView;
    private List<Sensor> mSensorList;
    private List<FarmPara> mDatas;
    private ParaAdapter mAdapter;
    private Bitmap temperature, humidity, illuminate;
    private LinearLayout llRefresh, llExcel;
    private RelativeLayout rlNoData;
    private NetRequest netRequest;
    private BGAProgressBar mPbDownloadExcel;

    private CommonTools myTool;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_farm_data, container, false);
        initView(view);
        initData();
        initEvents();
        return view;
    }

    private void initEvents() {
        llRefresh.setOnClickListener(this);
        llExcel.setOnClickListener(this);
    }

    private void initData() {
        Bundle bundle = getArguments();
        mFarm = (Farm) bundle.getSerializable(FarmActivity.KEY_FARM_INFO);
        initRes();//初始化图像资源
        showNoData();

        mSensorList = new ArrayList<>();
        mDatas = new ArrayList<>();
        mAdapter = new ParaAdapter(getActivity(), mDatas, R.layout.item_sensor_para);
        gridView.setAdapter(mAdapter);
//        vNodata.setVisibility(View.VISIBLE);
        // 先获取传感器列表
        getSensorList();
    }

    private void initRes() {
        temperature = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fun_temperature);
        humidity = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fun_humidity);
        illuminate = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fun_illuminate);
    }

    private void getSensorList() {
        netRequest.getSensorList(mFarm.getId(), new DataAdapter() {
            @Override
            public void sensors(List<Sensor> sensorList) {
                super.sensors(sensorList);
                mSensorList = sensorList;
                if (!mSensorList.isEmpty()) {
                    // 再根据传感器列表获取参数
                    getParaData();
                }
            }
        });
    }

    // 获得传感器参数列表
    private void getParaData() {
        for (final Sensor sensor : mSensorList) {
            netRequest.getSensorPara(sensor.getId(), new DataAdapter() {
                @Override
                public void farmParas(List<FarmPara> paras) {
                    super.farmParas(paras);
                    mDatas.addAll(paras);
                    //改变数据
                    if (!mDatas.isEmpty()) {
                        notifyData();
                    }
                }
            });
        }
    }


    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        gridView = (GridView) v.findViewById(R.id.gridView);
        llRefresh = (LinearLayout) v.findViewById(R.id.ll_refresh);
        llExcel = (LinearLayout) v.findViewById(R.id.ll_excel);
        rlNoData = (RelativeLayout) v.findViewById(R.id.rl_nodata);
        mPbDownloadExcel = (BGAProgressBar) v.findViewById(R.id.progressBar);
        netRequest = new NetRequest(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_refresh:
                mDatas.clear();
                notifyData();
                getSensorList();
                break;
            case R.id.ll_excel:
                myTool.startActivity(mFarm, DownloadExcelActivity_.class);
//                netRequest.downLoadExcel(mFarm, mPbDownloadExcel);//下载历史数据
                break;
        }
    }

    private void notifyData() {
        mAdapter.notifyDataSetChanged();
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

    class ParaAdapter extends CommonAdapter<FarmPara> {
        public ParaAdapter(Context context, List<FarmPara> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, FarmPara item) {
            double value = Double.valueOf(item.getValue());
            helper.setText(R.id.tv_name, item.getName())
                    .setText(R.id.tv_value, String.format("%.2f", value));//保留两位小数
            ImageView ivItem = helper.getView(R.id.iv_item);

            // 设置icon
            if (item.getName().contains("温度")) {
                ivItem.setImageBitmap(temperature);
                helper.setText(R.id.tv_unit, "℃");//单位
            } else if (item.getName().contains("湿度")) {
                ivItem.setImageBitmap(humidity);
                helper.setText(R.id.tv_unit, "%RH");//单位
            } else if (item.getName().contains("光照")) {
                ivItem.setImageBitmap(illuminate);
                helper.setText(R.id.tv_unit, "LUX");//单位
            }
        }
    }
}
