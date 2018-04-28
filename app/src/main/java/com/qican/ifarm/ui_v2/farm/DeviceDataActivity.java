package com.qican.ifarm.ui_v2.farm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.utils.CommonTools;


public class DeviceDataActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    RealtimeDataFragment fgRealtimeData;
    HistoryDataFragment fgHistoryData;
    SwipeRefreshLayout srl;
    Button btnDownload;
    LinearLayout llBack;
    TextView tvTitle;
    CommonTools myTool;
    MonitorNode mNode;
    public static String KEY_NODE_INFO = "KEY_NODE_INFO";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_data);

        initView();
        initData();

        initTitleBar();
        initEvent();
    }

    private void initTitleBar() {
        tvTitle.setText(mNode.getLocation() + mNode.getDistrict() + "区[" + mNode.getOrderNo() + "号]");
    }

    private void initEvent() {
        srl.setOnRefreshListener(this);
        llBack.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
    }

    private void initView() {
        myTool = new CommonTools(this);
        llBack = (LinearLayout) findViewById(R.id.llBack);
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnDownload = (Button) findViewById(R.id.btn_download);
    }

    private void initData() {

        mNode = (MonitorNode) myTool.getParam(MonitorNode.class);

        if (fgRealtimeData == null)
            fgRealtimeData = new RealtimeDataFragment();

        if (fgHistoryData == null)
            fgHistoryData = new HistoryDataFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_NODE_INFO, mNode);

        fgRealtimeData.setArguments(bundle);
        fgHistoryData.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fg_real_data, fgRealtimeData).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fg_history_data, fgHistoryData).commit();
    }

    @Override
    public void onRefresh() {
//        srl.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                srl.setRefreshing(false);
//            }
//        }, 2500);
        fgRealtimeData.onRefresh(srl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.btn_download:
                myTool.startActivity(mNode, DataDownloadActivity_.class);
                break;
        }
    }
}
