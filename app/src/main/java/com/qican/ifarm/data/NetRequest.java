/**
 * 智能农场网络请求类
 */
package com.qican.ifarm.data;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;
import com.qican.ifarm.adapter.DataAdapter;
import com.qican.ifarm.bean.ParaCacheValues;
import com.qican.ifarm.bean.Sensor;
import com.qican.ifarm.beanfromzhu.FarmPara;
import com.qican.ifarm.beanfromzhu.FarmSensor;
import com.qican.ifarm.listener.BeanCBWithTkCk;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "farmSensor/getFarmSensorsList")
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
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "sensor/getSensorVaules")
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
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "sensor/getSensorCacheVaules")
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
}
