package com.qican.ifarm.ui.map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.InfoWinAdapter;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.utils.CommonTools;

public class MapFragment extends Fragment implements LocationSource, AMapLocationListener, View.OnClickListener {
    private View view;
    private String TAG = "MapFragment";

    private CommonTools myTool;
    private MapView mMapView;
    private AMap mMap;
    private UiSettings mUiSettings;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private TextView tvNormal, tvNight, tvSatellite;
    private InfoWinAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_map, container, false);
        initView(view);
        initMap(savedInstanceState);
        initEvents();
        return view;
    }

    private void initEvents() {
        tvNight.setOnClickListener(this);
        tvSatellite.setOnClickListener(this);
        tvNormal.setOnClickListener(this);
    }

    private void initMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mMap = mMapView.getMap();

        mUiSettings = mMap.getUiSettings();

        mMap.setMapType(AMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));//初始缩放级别

        selecetTextView(tvNormal);

        mMap.setLocationSource(this);// 设置定位监听
        mUiSettings.setMyLocationButtonEnabled(true); // 显示默认的定位按钮
        mMap.setMyLocationEnabled(true);// 可触发定位并显示定位层
        adapter = new InfoWinAdapter();
        mMap.setInfoWindowAdapter(adapter);
    }

    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        mMapView = (MapView) v.findViewById(R.id.map);

        tvNormal = (TextView) v.findViewById(R.id.tv_normal);
        tvSatellite = (TextView) v.findViewById(R.id.tv_satellite);
        tvNight = (TextView) v.findViewById(R.id.tv_night);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        mMapView.onPause();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Log.i(TAG, "activate: ");
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(getActivity());
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        Log.i(TAG, "deactivate: ");
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (mListener != null && location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            addMarkerToMap(latLng, "重庆市", "重庆市人民政府",
                    new MonitorNode.Builder()
                            .setName("花园新村")
                            .setUpdateTime("2016-2-18 18:34:12")
                            .setLocation("重庆市长寿区云集镇174号")
                            .setImgUrl("http://image57.360doc.com/DownloadImg/2012/12/1116/28840930_86.jpg")
                            .build());

            if (location != null
                    && location.getErrorCode() == 0) {
                mListener.onLocationChanged(location);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + location.getErrorCode() + ": " + location.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_normal:
                mMap.setMapType(AMap.MAP_TYPE_NORMAL);
                selecetTextView(tvNormal);
                break;
            case R.id.tv_satellite:
                mMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                selecetTextView(tvSatellite);
                break;
            case R.id.tv_night:
                selecetTextView(tvNight);
                myTool.showInfo("夜间模式暂未开放~");
                break;
        }
    }

    private void selecetTextView(TextView tv) {
        resetTextView();
        tv.setBackgroundResource(R.drawable.tv_selected);
        tv.setTextColor(Color.parseColor("#ffffff"));
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }

    private void unSelectTextView(TextView tv) {
        tv.setBackgroundResource(R.drawable.tv_unselected);
        tv.setTextColor(Color.parseColor("#575757"));
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(false);
    }

    private void resetTextView() {
        unSelectTextView(tvNight);
        unSelectTextView(tvNormal);
        unSelectTextView(tvSatellite);
    }

    private void addMarkerToMap(LatLng latLng, String title, String snippet) {
        mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal))
        );
    }

    private void addMarkerToMap(LatLng latLng, String title, String snippet, MonitorNode node) {
        mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal))
        ).setObject(node);
    }

    private void addMarkerToMap(LatLng latLng, MonitorNode node) {
        mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal))
        ).setObject(node);
    }
}
