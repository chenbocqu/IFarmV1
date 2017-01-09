package com.qican.ifarm.bean;

/**
 * Created by Administrator on 2016/12/27 0027.
 */
public class Farm {
    private String id;
    private String name;
    private String desc;
    private String time;
    private String imgUrl;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public static class Builder {
        private Farm farm;

        public Builder() {
            farm = new Farm();
        }

        public Builder setId(String id) {
            farm.setId(id);
            return this;
        }

        public Builder setName(String name) {
            farm.setName(name);
            return this;
        }

        public Builder setDesc(String desc) {
            farm.setDesc(desc);
            return this;
        }

        public Builder setTime(String time) {
            farm.setTime(time);
            return this;
        }

        public Builder setImgUrl(String imgUrl) {
            farm.setImgUrl(imgUrl);
            return this;
        }

        public Farm build() {
            return farm;
        }
    }
}
