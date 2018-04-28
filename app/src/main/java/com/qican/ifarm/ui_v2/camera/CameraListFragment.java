package com.qican.ifarm.ui_v2.camera;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.EZCamera;
import com.qican.ifarm.bean.IFarmCamera;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.ui.camera.VideoFullscreenActivity_;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.videogo.openapi.EZOpenSDK;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

public class CameraListFragment extends FragmentWithOnResume implements PullToRefreshLayout.OnRefreshListener, View.OnClickListener {
    private View view;
    private CommonTools myTool;

    private List<IFarmCamera> mDatas;
    private ComAdapter<IFarmCamera> mAdpater;

    //下拉刷新
    private PullToRefreshLayout mRefreshLayout;
    private PullListView mListView;
    NetRequest netRequest;
    int pageStart = 0, pageSize = 20;
    int total = 30;
    boolean isError = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera_list, container, false);
        initView(view);
        initDatas();
        initEvent();

        return view;
    }

    private void initDatas() {
        mDatas = new ArrayList<>();
        mAdpater = new ComAdapter<IFarmCamera>(getActivity(), mDatas, R.layout.item_ifarm_camera) {
            @Override
            public void convert(ViewHolder helper, final IFarmCamera item) {

                myTool.setHeightByWindow(helper.getView(R.id.iv_img), 9 / 16f);
                if (item.getStatus() == 0) helper.setText(R.id.tv_status, "离线");

                helper
                        .setImageByUrl(R.id.iv_img, item.getPicUrl())
                        .setText(R.id.tv_name, item.getName());

                helper.getView(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EZCamera ezCamera = new EZCamera();
                        ezCamera.setDeviceSerial(item.getDeviceSerial());
                        ezCamera.setChannelNo(item.getChannelNo());
                        ezCamera.setVerifyCode(item.getVerifyCode());
                        ezCamera.setChannelName(item.getName());

                        myTool.startActivity(ezCamera, VideoFullscreenActivity_.class);

                    }
                });
            }
        };
        mListView.setAdapter(mAdpater);
    }

    @Override
    public void update() {
        // 刷新数据
        requestData(true);
    }

    void addListenerToThis(@IdRes int id) {
        if (view.findViewById(id) != null)
            view.findViewById(id).setOnClickListener(this);
    }

    private void requestData(final boolean isRefresh) {

        // 开始加载时
        setVisibility(R.id.iv_net_error, false);
        setVisibility(R.id.rl_nodata, false);
        setVisibility(R.id.avi_list, true);
        setVisibility(R.id.rl_not_link_to_ez, false);

        int page = isRefresh ? 0 : (pageStart + 1);

        // 萤石官方获取列表
        OkHttpUtils.post().url("https://open.ys7.com/api/lapp/camera/list")
                .addParams("accessToken", EZOpenSDK.getInstance().getEZAccessToken().getAccessToken())
                .addParams("pageStart", "" + page)
                .addParams("pageSize", "" + pageSize)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log(e.getMessage());
                        mRefreshLayout.refreshFinish(true);
                        mRefreshLayout.loadMoreFinish(true);
                        isError = true;

                        notifyDataset();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        mRefreshLayout.refreshFinish(true);
                        mRefreshLayout.loadMoreFinish(true);

                        myTool.log(response);
                        if (response == null || "{}".equals(response)) return;

                        isError = false;

                        //开始解析
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (obj.has("page")) {
                                JSONObject pageObj = obj.getJSONObject("page");

                                if (pageObj.has("total"))
                                    total = pageObj.getInt("total");

                                if (pageObj.has("page"))
                                    pageStart = pageObj.getInt("page");
                            }

                            if (!obj.has("code")) return;
                            switch (obj.getString("code")) {
                                case "200":

                                    if (!obj.has("data")) return;

                                    JSONArray datas = obj.getJSONArray("data");

                                    if (isRefresh && datas.length() != 0)
                                        mDatas.clear(); //刷新的话先清除原来的数据

                                    for (int i = 0; i < datas.length(); i++) {
                                        JSONObject cameraObj = datas.getJSONObject(i);
                                        IFarmCamera camera = new IFarmCamera();

                                        if (cameraObj.has("deviceSerial"))
                                            camera.setDeviceSerial(cameraObj.getString("deviceSerial"));

                                        if (cameraObj.has("channelNo"))
                                            camera.setChannelNo(cameraObj.getInt("channelNo"));

                                        if (cameraObj.has("verifyCode"))
                                            camera.setVerifyCode(cameraObj.getString("verifyCode"));
                                        else camera.setVerifyCode("SEGHDP");

                                        if (cameraObj.has("channelName")) {
                                            camera.setName(cameraObj.getString("channelName"));
                                            camera.setChannelName(cameraObj.getString("channelName"));
                                        }

                                        if (cameraObj.has("location"))
                                            camera.setLocation(cameraObj.getString("location"));

                                        if (cameraObj.has("status"))
                                            camera.setStatus(cameraObj.getInt("status"));

                                        if (cameraObj.has("picUrl"))
                                            camera.setPicUrl(cameraObj.getString("picUrl"));

                                        mDatas.add(camera);
                                    }
                                    break;

                                case "10001": // 不能为空
                                    showLoginHint("暂未关联萤石，点击空白处关联");
                                    break;
                                case "10002": // Token过期
                                    showLoginHint("萤石账户过期，点击空白处重新关联");
                                    break;

                                default:
                                    showLoginHint("出现位置错误，请稍后再试");
                            }

                            // 按名字排序
                            Collections.sort(mDatas);
                            notifyDataset();

                        } catch (JSONException e) {
                            myTool.showInfo("Error: " + e.getMessage());
                        }
                    }
                });
    }

    private void showLoginHint(String hint) {
        setVisible(R.id.rl_not_link_to_ez, View.VISIBLE);
        setText(R.id.tv_login_hint, hint);
    }

    private void notifyDataset() {
        setVisibility(R.id.avi_list, false);
        mAdpater.notifyDataSetChanged();
        if (mDatas.isEmpty()) {
            if (isError) setVisibility(R.id.iv_net_error, true);
            else setVisibility(R.id.rl_nodata, true);
        } else {
            setVisibility(R.id.iv_net_error, false);
            setVisibility(R.id.rl_nodata, false);
        }
    }

    void setText(@IdRes int id, String text) {
        TextView tv = (TextView) view.findViewById(id);

        if (tv != null && text != null)
            tv.setText(text);
    }

    void setVisible(@IdRes int id, int visible) {
        if (view.findViewById(id) != null)
            view.findViewById(id).setVisibility(visible);
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
        addListenerToThis(R.id.rl_not_link_to_ez);
    }

    private void initView(View v) {
        mRefreshLayout = (PullToRefreshLayout) v.findViewById(R.id.pullToRefreshLayout);
        mListView = (PullListView) v.findViewById(R.id.pullListView);

        myTool = new CommonTools(getActivity());
        setVisible(R.id.rl_not_link_to_ez, View.GONE);
    }

    void setVisibility(@IdRes int id, boolean isVisible) {
        View v = view.findViewById(id);
        v.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout layout) {

        // 刷新数据
        requestData(true);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout layout) {

        if (mDatas.size() >= total) {
            myTool.showInfo("没有更多了！");
            mRefreshLayout.loadMoreFinish(true);
            return;
        }

        // 加载更多
        requestData(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_not_link_to_ez:
                EZOpenSDK.getInstance().logout();
                EZOpenSDK.getInstance().openLoginPage();
                break;
        }
    }
}
