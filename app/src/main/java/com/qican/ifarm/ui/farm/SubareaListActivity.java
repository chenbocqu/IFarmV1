package com.qican.ifarm.ui.farm;

import android.app.Activity;
import android.content.Context;
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
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.listener.BeanCBWithTkCk;
import com.qican.ifarm.ui.subarea.SubareaActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

@EActivity(R.layout.activity_subarealist)
public class SubareaListActivity extends Activity {

    private CommonTools myTool;

    @ViewById(R.id.rl_nodata)
    RelativeLayout rlNoData;

    @ViewById(R.id.pullToRefreshLayout)
    PullToRefreshLayout refreshLayout;

    @ViewById(R.id.pullListView)
    PullListView mListView;

    private SubareaAdapter mAdapter;
    private Farm mFarm;

    private List<Subarea> mDatas;


    @AfterViews
    void main() {
        myTool = new CommonTools(this);

        mFarm = new Farm();
        mFarm = (Farm) myTool.getParam(mFarm);
        initEvents();
    }

    private void initEvents() {
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout l) {
                l.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        l.refreshFinish(true);
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore(final PullToRefreshLayout l) {
                l.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        l.loadMoreFinish(true);
                    }
                }, 1000);
            }
        });
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdapter = new SubareaAdapter(this, mDatas, R.layout.item_subarea);
        mListView.setAdapter(mAdapter);

        showNoData();
        getSubareaData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void getSubareaData() {
//        mDatas.addAll(IFarmData.getFarmList());
//        notifyData();
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "sensor/getSensorVaules")
                .addParams("farmId", mFarm.getId())
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("分区列表e：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        if ("lose efficacy".equals(response)) {
                            myTool.showTokenLose();
                            return;
                        }

                        if (response == null || "[]".equals(response)) {
                            myTool.showInfo("该农场暂无分区，请先添加！");
                            return;
                        }

                        myTool.log("分区列表list：" + response);
                        JSONArray arr = JSONArray.fromObject(response);
                        JSONObject jsonObject = arr.getJSONObject(0);
                        Iterator<String> iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            Subarea area = new Subarea();
                            String key = iterator.next();
                            myTool.log("\n当前分区为:" + key);

                            Map<String, List<String>> map = new HashMap<String, List<String>>();

                            JSONArray array = jsonObject.getJSONArray(key);

                            for (int i = 0; i < array.size(); i++) {
                                // 取参数
                                JSONObject object = array.getJSONObject(i);
                                JSONArray paraName = object.getJSONArray("sensorParam");
                                JSONArray paraData = object.getJSONArray("data");
                                for (int j = 0; j < paraName.size() && j < paraData.size(); j++) {
                                    String paraKey = paraName.getString(j);
                                    String paraValue = paraData.getString(j);

                                    if (!map.containsKey(paraKey)) {
                                        //如果当前没有出现该Key值则新建一个空间用于存放
                                        List<String> temp = new ArrayList<String>();
                                        temp.clear();
                                        temp.add(paraValue);
                                        map.put(paraKey, temp);
                                    } else {
                                        ArrayList<String> dataList = (ArrayList<String>) map.get(paraKey);
                                        dataList.add(paraValue);
                                        map.put(paraKey, dataList);
                                    }

                                    myTool.log("\nmap为:" + map.toString());
                                }
                            }
                            area.setName(key);
                            area.setFarmId(mFarm.getId());
                            area.setDataMap(map);
                            area.setFarm(mFarm);
                            area.setImgUrl("http://img4.imgtn.bdimg.com/it/u=3436548158,2996155944&fm=23&gp=0.jpg");

                            mDatas.add(area);
                        }
                        notifyData();
                    }
                });
    }

    private void notifyData() {
        myTool.log("分区条数：" + mDatas.size());
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

    class SubareaAdapter extends CommonAdapter<Subarea> {

        public SubareaAdapter(Context context, List<Subarea> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, final Subarea item) {
            helper.setText(R.id.tv_name, item.getName());
            RelativeLayout rlItem = helper.getView(R.id.rl_item);
            rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myTool.startActivity(item, SubareaActivity.class);
                }
            });
        }
    }

    @Click
    void llAddSubarea() {
        //启动添加分区
        myTool.showInfo("添加分区待实现！");
    }

    @Click
    void llBack() {
        this.finish();
    }
}
