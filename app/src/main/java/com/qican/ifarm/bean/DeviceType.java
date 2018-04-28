package com.qican.ifarm.bean;

import java.io.Serializable;
import java.util.List;

public class DeviceType implements Serializable {

    List<String> names;
    List<String> codes;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    @Override
    public String toString() {
        return "DeviceType{" +
                "names=" + names +
                ", codes=" + codes +
                '}';
    }
}
