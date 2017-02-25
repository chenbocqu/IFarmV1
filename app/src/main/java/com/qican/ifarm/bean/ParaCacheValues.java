/**
 * 用于曲线显示的的数值类型
 */
package com.qican.ifarm.bean;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class ParaCacheValues {
    private String sensorId;
    private String name;
    private List<Entry> points;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Entry> getPoints() {
        return points;
    }

    public void setPoints(List<Entry> points) {
        this.points = points;
    }
}
