package com.qican.ifarm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.qican.ifarm.IFarmApp;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.NavigationUtils;
import com.qican.ifarm.utils.PhoneCallUtils;


/**
 * Created by Teprinciple on 2016/8/23.
 * 地图上自定义的infowindow的适配器
 */
public class InfoWinAdapter implements AMap.InfoWindowAdapter, View.OnClickListener {
    private static final String TAG = "InfoWinAdapter";
    private Context mContext = IFarmApp.getApp().getBaseContext();

    private LatLng latLng;
    private LinearLayout call;
    private LinearLayout navigation;
    private TextView tvName, tvAddress, tvUpdateTime;
    private MonitorNode node;
    private CommonTools myTool;
    private ImageView ivNodeImg;

    @Override
    public View getInfoWindow(Marker marker) {

        Log.i(TAG, "getInfoWindow: ");

        View view = initView();
        initData(marker);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
        node = (MonitorNode) marker.getObject();

        if (node != null) {
            Log.i(TAG, "initData: " + node.toString());
            tvName.setText(node.getName());
            tvAddress.setText(String.format(mContext.getString(R.string.agent_addr), node.getLocation()));
            tvUpdateTime.setText(node.getUpdateTime());
            myTool.showImage(node.getImgUrl(), ivNodeImg);
        }
    }

    @NonNull
    private View initView() {
        myTool = new CommonTools(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_infowindow, null);
        navigation = (LinearLayout) view.findViewById(R.id.navigation_LL);
        call = (LinearLayout) view.findViewById(R.id.call_LL);
        tvName = (TextView) view.findViewById(R.id.name);
        tvAddress = (TextView) view.findViewById(R.id.addr);
        ivNodeImg = (ImageView) view.findViewById(R.id.iv_node_img);
        tvUpdateTime = (TextView) view.findViewById(R.id.tv_updatetime);

        navigation.setOnClickListener(this);
        call.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.navigation_LL:  //点击导航
                NavigationUtils.Navigation(latLng);
                break;

            case R.id.call_LL:  //点击打电话
                PhoneCallUtils.call("10086"); //TODO 处理电话号码
                break;
        }
    }

}
