package com.qican.ifarm.bean;

import java.io.Serializable;

public class IFarmCamera extends EZCamera implements Serializable {
    protected String id;
    protected String name;

    protected String location;
    protected int shedNo; //大棚编号
    private String preImgUrl;

    private Subarea subarea;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getShedNo() {
        return shedNo;
    }

    public void setShedNo(int shedNo) {
        this.shedNo = shedNo;
    }

    public String getPreImgUrl() {
        return preImgUrl;
    }

    public void setPreImgUrl(String preImgUrl) {
        this.preImgUrl = preImgUrl;
    }

    public Subarea getSubarea() {
        return subarea;
    }

    public void setSubarea(Subarea subarea) {
        this.subarea = subarea;
    }

    @Override
    public String toString() {
        return "IFarmCamera{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", shedNo=" + shedNo +
                ", preImgUrl='" + preImgUrl + '\'' +
                '}';
    }

    public static class Builder {
        private IFarmCamera camera;

        public Builder() {
            camera = new IFarmCamera();
        }

        public Builder setId(String id) {
            camera.setId(id);
            return this;
        }

        public Builder setName(String name) {
            camera.setName(name);
            return this;
        }

        public Builder setLoaction(String location) {
            camera.setLocation(location);
            return this;
        }

        public Builder setDeviceSerial(String serial) {
            camera.setDeviceSerial(serial);
            return this;
        }

        public Builder setChannelNo(int no) {
            camera.setChannelNo(no);
            return this;
        }

        public Builder setShedNo(int shedNo) {
            camera.setShedNo(shedNo);
            return this;
        }

        public Builder setPreImgUrl(String imgUrl) {
            camera.setPreImgUrl(imgUrl);
            return this;
        }

        public Builder setVerifyCode(String verifyCode) {
            camera.setVerifyCode(verifyCode);
            return this;
        }

        public IFarmCamera build() {
            return camera;
        }
    }
}
