/**
 * 时间工具
 */
package com.qican.ifarm.utils;

public class TimeUtils {

    // 2017-01-06 15:01:44.0
    public static String getTime(String timeStamp) {
        String hour = "0";
        String min = "00";
        String[] time = timeStamp.split(" ");
        if (time.length != 2) {
            throw new IllegalArgumentException("timeStamp 格式有问题，请检查！");
        } else {
            String[] temp = time[1].split(":");
            hour = temp[0];
            min = temp[1];
        }
        return hour + ":" + min;
    }

    // 2017-01-06 15:01:44.0
    public static String getDate(String timeStamp) {
        String[] time = timeStamp.split(" ");
        if (time.length != 2) {
            throw new IllegalArgumentException("timeStamp 格式有问题，请检查！");
        }
        return time[0];
    }
}
