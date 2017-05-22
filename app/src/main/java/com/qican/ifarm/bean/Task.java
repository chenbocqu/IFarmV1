/**
 * 控制任务
 */
package com.qican.ifarm.bean;

import java.io.Serializable;

public class Task implements Serializable {

    public enum TaskStatus {
        Running, Waiting, Completed
    }

    int id;
    String area;
    String name;
    String startTime;
    String duration;
    TaskStatus status;

    boolean isTank1;
    boolean isTank2;
    boolean isTank3;

    boolean isA, isB, isC, isD, isE;

    /**
     * 以二进制编码形式设置任务状态描述
     *
     * @param num 例子 [ 010 ] [ 11010 ] ，低5位由低到高 分别是A->E,高3位由低到高 分别是 Tank1 -> Tank3
     */
    public void setStatusByNum(int num) {
        for (int i = 0; i < 8; i++) {
            int flag = get(num, i);
            switch (i) {
                case 0:
                    setA(getFlagStatus(flag));
                    break;
                case 1:
                    setB(getFlagStatus(flag));
                    break;
                case 2:
                    setC(getFlagStatus(flag));
                    break;
                case 3:
                    setD(getFlagStatus(flag));
                    break;
                case 4:
                    setE(getFlagStatus(flag));
                    break;

                case 5:
                    setTank1(getFlagStatus(flag));
                    break;
                case 6:
                    setTank2(getFlagStatus(flag));
                    break;
                case 7:
                    setTank3(getFlagStatus(flag));
                    break;
            }
        }

    }

    private boolean getFlagStatus(int flag) {
        return flag == 1 ? true : false;
    }

    public boolean isTank1() {
        return isTank1;
    }

    public void setTank1(boolean tank1) {
        isTank1 = tank1;
    }

    public boolean isTank2() {
        return isTank2;
    }

    public void setTank2(boolean tank2) {
        isTank2 = tank2;
    }

    public boolean isTank3() {
        return isTank3;
    }

    public void setTank3(boolean tank3) {
        isTank3 = tank3;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void clearArea() {
        setA(false);
        setB(false);
        setC(false);
        setD(false);
        setE(false);
    }

    public static class Builder {
        private Task task;

        public Builder() {
            task = new Task();
        }

        public Builder setId(int id) {
            task.setId(id);
            return this;
        }

        public Builder setStatusByNum(int num) {
            task.setStatusByNum(num);
            return this;
        }

        public Builder setName(String name) {
            task.setName(name);
            return this;
        }

        public Builder setArea(String area) {
            task.setArea(area);
            return this;
        }

        public Builder setStartTime(String startTime) {
            task.setStartTime(startTime);
            return this;
        }

        public Builder setDuration(String time) {
            task.setDuration(time);
            return this;
        }

        public Builder setStatus(TaskStatus status) {
            task.setStatus(status);
            return this;
        }

        public Task build() {
            return task;
        }
    }

    public String getOperationDesc() {
        String type = "";
        if (this.isTank1())
            type = type + "1 ";
        if (this.isTank2())
            type = type + "2 ";
        if (this.isTank3())
            type = type + "3 ";
        if (type.equals("")) {
            type = "灌溉";
        } else {
            type = "施肥[ 罐 " + type + "]";
        }
        return type;
    }

    /**
     * @param num:要获取二进制值的数
     * @param index:倒数第一位为0，依次类推
     */
    public static int get(int num, int index) {
        return (num & (0x1 << index)) >> index;
    }

    public boolean isA() {
        return isA;
    }

    public void setA(boolean a) {
        isA = a;
    }

    public boolean isB() {
        return isB;
    }

    public void setB(boolean b) {
        isB = b;
    }

    public boolean isC() {
        return isC;
    }

    public void setC(boolean c) {
        isC = c;
    }

    public boolean isD() {
        return isD;
    }

    public void setD(boolean d) {
        isD = d;
    }

    public boolean isE() {
        return isE;
    }

    public void setE(boolean e) {
        isE = e;
    }
}
