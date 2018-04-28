package com.qican.ifarm.data;


import android.content.Context;
import android.util.Log;

import com.qican.ifarm.bean.DevicePara;
import com.qican.ifarm.bean.DeviceType;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.listener.InfoRequestListener;
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

public class DeviceDataRequest {
    public static void getNodeInfoById(Context context, MonitorNode node, final InfoRequestListener<MonitorNode> l) {
        final CommonTools myTool = new CommonTools(context);
        OkHttpUtils.post().url(myTool.getServAdd() + "collectorDeviceValue/collectorDeviceCurrentValue")
                .addParams("deviceId", node.getDeviceId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (l != null)
                            l.onFail(e);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        if (response == null) return;

                        JSONArray array = null;
                        try {
                            array = new JSONArray(response);
                            if (array.length() == 0) {
                                // TODO: 当前无数据
                                if (l != null) l.onFail(new Exception());
                                return;
                            }

                            // for different device type
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                // 类型名称
                                String type = obj.getString("type");
                                // code
                                String code = obj.getString("code");
                                String header = obj.getString("header");
                                String unit = obj.getString("unit");

                                String[] codes = code.split(",");
                                String[] headers = header.split(",");
                                String[] units = unit.split(",");

                                // get data array in each type
                                JSONArray dataArray = obj.getJSONArray("data");

                                // for each device data
                                for (int index = 0; index < dataArray.length(); index++) {
                                    MonitorNode node = new MonitorNode();
                                    JSONObject nodeObj = dataArray.getJSONObject(index);

                                    if (!nodeObj.has("deviceId")) continue;
                                    // device info
                                    node.setDeviceId(nodeObj.getString("deviceId"));
                                    node.setOrderNo(nodeObj.getString("deviceOrderNo"));
                                    node.setDistrict(nodeObj.getString("deviceDistrict"));
                                    node.setLocation(nodeObj.getString("deviceLocation"));
                                    node.setType(nodeObj.getString("deviceType"));

                                    // to check has data or not ...
                                    if (!nodeObj.has("deviceValueId")) {
                                        node.setHashData(false);
                                        if (l != null)
                                            l.onSuccess(node);
                                        continue; // for next data ...
                                    }

                                    // exist data value, start wraping data ...
                                    node.setHashData(true);
                                    node.setId(nodeObj.getString("deviceValueId"));

                                    List<DevicePara> paras = new ArrayList<DevicePara>();
                                    // paras in data ...
                                    for (
                                            int codeId = 0;
                                            codeId < codes.length && codeId < headers.length && codeId < units.length;
                                            codeId++) {

                                        DevicePara para = new DevicePara();
                                        para.setName(headers[codeId]);
                                        para.setCode(codes[codeId]);
                                        para.setUnit(units[codeId]);

                                        para.setValue(nodeObj.getString(codes[codeId]));

                                        // add into list ...
                                        paras.add(para);

                                        node.setNodeDatas(paras);
                                    }
                                    // other data ...
                                    node.setUpdateTime(nodeObj.getString("updateTime"));

                                    // Add node data to mDatas to show ...
                                    if (l != null)
                                        l.onSuccess(node);
                                }
                            }

                        } catch (JSONException e) {
                            if (l != null) l.onFail(new Exception());
                            myTool.showInfo(e.getMessage());
                        }

                    }
                });
    }

    public static void getMenuByType(MonitorNode node, final InfoRequestListener<DeviceType> l) {
        Log.i("IFarm_DEBUG", "deviceType: " + node.getType());
        OkHttpUtils.post().url("http://" + ConstantValue.IP + ":8080/IFarm/farmCollectorDevice/collectorDeviceParamCode")
                .addParams("deviceType", node.getType())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Log.i("IFarm_DEBUG", "onResponse: " + response);

                        if (response == null) return;
                        if ("[]".equals(response) || "{}".equals(response)) {
                            if (l != null) l.onFail(new Exception("The response is null!"));
                            return;
                        }

                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (!obj.has("header") || !obj.has("code")) {
                                if (l != null) l.onFail(new Exception("解析菜单出错！"));
                            }

                            String header = obj.getString("header");
                            String code = obj.getString("code");

                            String[] headers = header.split(",");
                            String[] codes = code.split(",");

                            if (headers.length != codes.length) {
                                if (l != null)
                                    l.onFail(new Exception("The length between header and code is not match!"));
                                return;
                            }

                            List<String> nameList = new ArrayList<String>();
                            List<String> codeList = new ArrayList<String>();

                            for (String temp : headers)
                                nameList.add(temp);
                            for (String temp : codes)
                                codeList.add(temp);

                            DeviceType type = new DeviceType();
                            type.setNames(nameList);
                            type.setCodes(codeList);

                            if (l != null) l.onSuccess(type);

                        } catch (JSONException e) {
                            if (l != null) l.onFail(new Exception("解析菜单出错！"));
                        }
                    }
                });
    }
}
