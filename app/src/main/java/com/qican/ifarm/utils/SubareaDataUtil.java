/**
 * 分区数据工具
 */
package com.qican.ifarm.utils;

import android.widget.ImageView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.RealtimeData;
import com.qican.ifarm.bean.Subarea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubareaDataUtil {
    public interface realDataListener {
        void onParseFinshed(String temperature, String humidity, String illuminate);
    }

    public static void parseSubareaData(Subarea area, realDataListener l) {
        List<RealtimeData> datas = new ArrayList<>();

        Map<String, List<String>> map = area.getDataMap();
        Set<String> set = map.keySet();

        for (String key : set) {
            RealtimeData data = new RealtimeData();
            data.setName(key);
            ArrayList<String> values = (ArrayList<String>) map.get(key);
            double sum = 0.0;
            if (values != null && values.size() != 0) {
                for (String v : values) {
                    sum = sum + Double.valueOf(v);
                }
                data.setAverageValue(String.valueOf(sum / values.size()));
                data.setValues(values);
            }
            datas.add(data);
        }

        String temperature = null, humidity = null, illuminate = null;
        for (RealtimeData item : datas) {
            double averageValue = Double.valueOf(item.getAverageValue());
            String temp = String.format("%.2f", averageValue);

            if (item.getName().contains("温度")) {
                temperature = temp;
            } else if (item.getName().contains("湿度")) {
                humidity = temp;
            } else if (item.getName().contains("光照")) {
                illuminate = temp;
            }
        }
        l.onParseFinshed(temperature, humidity, illuminate);
    }
}
