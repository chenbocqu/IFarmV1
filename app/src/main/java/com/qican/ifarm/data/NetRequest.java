/**
 * 智能农场网络请求类
 */
package com.qican.ifarm.data;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;

import com.github.mikephil.charting.data.Entry;
import com.qican.ifarm.adapter.DataAdapter;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.ParaCacheValues;
import com.qican.ifarm.bean.Sensor;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.beanfromservice.FarmPara;
import com.qican.ifarm.beanfromservice.FarmSensor;
import com.qican.ifarm.listener.BeanCBWithTkCk;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.WPSUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.progressbar.BGAProgressBar;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class NetRequest {
    private Context mContext;
    private CommonTools myTool;

    public NetRequest(Context context) {
        this.mContext = context;
        myTool = new CommonTools(mContext);
    }

    /**
     * 请求某个农场的传感器列表
     *
     * @param farmId 农场id
     * @param adaper 返回结果适配器
     */
    public void getSensorList(final String farmId, final DataAdapter adaper) {
        OkHttpUtils.post().url(myTool.getServAdd() + "farmSensor/getFarmSensorsList")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("farmId", farmId)
                .build()
                .execute(new BeanCBWithTkCk<List<FarmSensor>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("传感器列表请求异常,farmId=" + farmId + ",e：[" + e.getMessage() + "]");
                    }

                    @Override
                    public void onResponse(List<FarmSensor> sensorList, int id) {
                        List<Sensor> mSensorList = new ArrayList<Sensor>();
                        //处理token失效问题
                        if (sensorList == null) {
                            myTool.showTokenLose();
                            return;
                        }
                        for (FarmSensor farmSensor : sensorList) {
                            Sensor sensor = new Sensor(farmSensor);
                            mSensorList.add(sensor);
                        }
                        adaper.sensors(mSensorList);
                    }
                });
    }

    public void getSensorPara(final String sensorId, final DataAdapter adapter) {
        // 请求传感器数据
        OkHttpUtils.post().url(myTool.getServAdd() + "sensor/getSensorVaules")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("sensorId", sensorId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("传感器参数请求异常,sensorId=" + sensorId + ",e：[" + e.getMessage() + "]");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<FarmPara> mDatas = new ArrayList<FarmPara>();
                        try {
                            JSONArray arr = new JSONArray(response);

                            JSONObject obj = arr.getJSONObject(0);
                            JSONArray dataArr = obj.getJSONArray("data");//值
                            JSONArray paraArr = obj.getJSONArray("sensorParam");//参数名

                            for (int i = 0; i < dataArr.length(); i++) {
                                FarmPara para = new FarmPara();
                                para.setName(paraArr.getString(i));
                                para.setValue(dataArr.getString(i));
                                para.setTime(obj.getString("time"));
                                para.setSensorId(sensorId);

                                mDatas.add(para);
                            }
                            adapter.farmParas(mDatas);
                        } catch (JSONException e) {
                            myTool.log("JSON解析出错,sensorId=" + sensorId + ",e：[" + e.getMessage() + "]");
                        }
                    }
                });
    }

    public void getSensorCacheValues(final String sensorId, final DataAdapter adapter) {
        // 请求传感器数据
        OkHttpUtils.post().url(myTool.getServAdd() + "sensor/getSensorCacheVaules")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("sensorId", sensorId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("传感器参数请求异常,sensorId=" + sensorId + ",e：[" + e.getMessage() + "]");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<ParaCacheValues> mData = new ArrayList<ParaCacheValues>();
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject obj = arr.getJSONObject(0);

                            JSONArray paraArr = obj.getJSONArray("sensorParam");//参数名
                            //获得Cache值
                            for (int i = 0; i < paraArr.length(); i++) {

                                JSONArray valueArr = obj.getJSONArray(String.valueOf(i + 1));

                                ParaCacheValues paraCacheValues = new ParaCacheValues();
                                List<Entry> points = new ArrayList<Entry>();

                                for (int j = 0; j < valueArr.length(); j++) {
                                    String value = valueArr.getString(j);
                                    String[] values = value.split("=");
                                    String x = values[0];
                                    float y = Float.valueOf(values[1]);
                                    Entry p = new Entry(j, y);
                                    p.setData(x); // 采集点的时间信息

                                    points.add(p);
                                }
                                paraCacheValues.setName(paraArr.getString(i));
                                paraCacheValues.setSensorId(sensorId);
                                paraCacheValues.setPoints(points);

                                mData.add(paraCacheValues);
                            }
                            adapter.paraCaches(mData);
                        } catch (JSONException e) {
                            myTool.log("JSON解析出错,sensorId=" + sensorId + ",e：[" + e.getMessage() + "]");
                        }
                    }
                });
    }

    public void downLoadExcel(final Farm mFarm, final BGAProgressBar pb) {
        pb.setVisibility(View.VISIBLE);
        OkHttpUtils.get().url(myTool.getServAdd() + "sensor/historySensorValuesExcel")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("farmId", mFarm.getId())
//                .addParams("sensorId", "20160004")
                .addParams("beginTime", "2017-01-16 15:58:21")
                .addParams("endTime", "2017-02-27 19:54:57")    // 从00:00:00到23:59:59撒
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), mFarm.getName() + "历史数据.xls") {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        pb.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pb.setVisibility(View.GONE);
                        myTool.log("传感器参数请求异常,farmId=" + mFarm.getId() + ",e：[" + e.getMessage() + "]");
                    }

                    @Override
                    public void onResponse(final File file, int id) {
                        pb.setVisibility(View.GONE);
                        myTool.showInfo("已保存至[" + file.getAbsolutePath() + "]");

                        // 提示是否打开
                        new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("下载成功")
                                .setContentText("立即打开文档吗？")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog dlg) {
                                        dlg.dismissWithAnimation();
                                        Intent intent = WPSUtils.getExcelFileIntent(file.getAbsolutePath());
                                        mContext.startActivity(intent); // 打开Excel
                                    }
                                })
                                .show();
                    }
                });
    }


    /**
     * 请求某个农村分区下的传感器列表
     *
     * @param area   农场分区信息
     * @param adaper 返回结果适配器
     */
    public void getSensorList(final Subarea area, final DataAdapter adaper) {
        OkHttpUtils.post().url(myTool.getServAdd() + "sensor/sensorDistrict")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("farmId", area.getFarmId())
                .addParams("sensorDistrict", area.getName())
                .build()
                .execute(new BeanCBWithTkCk<List<FarmSensor>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("传感器列表请求异常,farmId=" + area.getFarmId() + ",分区：" + area.getName() + ",e：[" + e.getMessage() + "]");
                    }

                    @Override
                    public void onResponse(List<FarmSensor> sensorList, int id) {
                        List<Sensor> mSensorList = new ArrayList<Sensor>();
                        //处理token失效问题
                        if (sensorList == null) {
                            myTool.showTokenLose();
                            return;
                        }
                        for (FarmSensor farmSensor : sensorList) {
                            Sensor sensor = new Sensor(farmSensor);
                            mSensorList.add(sensor);
                        }
                        adaper.sensors(mSensorList);
                    }
                });
    }

    public void getFarmList(final DataAdapter adapter) {
        myTool.log(
                "URL : " + myTool.getServAdd() + "farm/getFarmColletorsList" +
                        "\nUserID : " + myTool.getUserId() +
                        "\nsignature : " + myTool.getToken());

        OkHttpUtils.post().url(myTool.getServAdd() + "farm/getFarmColletorsList")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .build()
                .execute(new BeanCBWithTkCk<List<com.qican.ifarm.beanfromservice.Farm>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(List<com.qican.ifarm.beanfromservice.Farm> farmList, int id) {
                        List<Farm> datas = new ArrayList<Farm>();
                        //处理token失效问题
                        if (farmList == null) {
                            myTool.showTokenLose();
                            return;
                        }
                        for (com.qican.ifarm.beanfromservice.Farm farm : farmList) {
                            Farm myFarm = new Farm(farm);
                            datas.add(myFarm);
                        }
                        adapter.farmList(datas);
                    }
                });
    }

    public void getSubareaData(final Farm mFarm, final DataAdapter adapter) {

        final List<Subarea> mData = new ArrayList<>();

        OkHttpUtils.post().url(myTool.getServAdd() + "sensor/getSensorVaules")
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
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(response);
                            JSONObject jsonObject = arr.getJSONObject(0);
                            Iterator<String> iterator = jsonObject.keys();
                            while (iterator.hasNext()) {
                                Subarea area = new Subarea();
                                String key = iterator.next();
                                myTool.log("\n当前分区为:" + key);

                                Map<String, List<String>> map = new HashMap<String, List<String>>();

                                JSONArray array = jsonObject.getJSONArray(key);

                                for (int i = 0; i < array.length(); i++) {
                                    // 取参数
                                    JSONObject object = array.getJSONObject(i);
                                    JSONArray paraName = object.getJSONArray("sensorParam");
                                    JSONArray paraData = object.getJSONArray("data");
                                    for (int j = 0; j < paraName.length() && j < paraData.length(); j++) {
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

                                mData.add(area);
                            }
                            if (adapter != null)
                                adapter.subareas(mData);

                        } catch (JSONException e) {
                            myTool.log(e.getMessage());
                        }
                    }
                });
    }
}
