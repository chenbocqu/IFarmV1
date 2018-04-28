package com.qican.ifarm.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qican.ifarm.bean.IFarmDate;
import com.qican.ifarm.bean.Label;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataBindUtils {
    private static final int LABEL_MAX_SHOW_LEN = 20;
    static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * 设置标签
     */
    public static void setLabel(List<TextView> tvLabels, TextView tvMore, Label label) {
        boolean showMore = false;
        //全部设置为不见
        for (int i = 0; i < tvLabels.size(); i++) {
            tvLabels.get(i).setVisibility(View.GONE);
        }

        if (label == null) {
            return;
        }

        List<String> labelList = label.getLabelList();
        int totalLen = 0;
        for (int i = 0; i < tvLabels.size() && i < labelList.size(); i++) {
            totalLen = totalLen + labelList.get(i).length();
            if (totalLen > LABEL_MAX_SHOW_LEN) {
                showMore = true;
                break;
            }
            tvLabels.get(i).setVisibility(View.VISIBLE);
            tvLabels.get(i).setText(labelList.get(i));
        }
        // 本身比标签容量多，或者显示不够完全，则显示更多
        if (tvMore == null) {
            return;
        }
        tvMore.setVisibility(
                labelList.size() > tvLabels.size() ||
                        showMore ?
                        View.VISIBLE : View.GONE);
    }

    public static String getCurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    public static String getFormatTime(String t) {
        IFarmDate time = new IFarmDate(t);
        IFarmDate curTime = new IFarmDate(getCurrentTime());

        if (curTime.getYear() != time.getYear())
            return time.getYear() + "-" + time.getMonth() + "-" + time.getDay();

        if (curTime.getMonth() != time.getMonth())
            return time.getMonth() + "-" + time.getDay();

        int days = curTime.differ(time);

        if (days == 0) {
            return time.getHour() + ":" + time.getMin();
        }
        if (days == 1) {
            return "昨天" + time.getHour() + ":" + time.getMin();
        }
        if (days == 2) {
            return "前天" + time.getHour() + ":" + time.getMin();
        }

        return time.getMonth() + "-" + time.getDay();
    }

    public static String getTime(String time) {
        String currtent = getCurrentTime();
        Date timeDate;
        String ret = "";
        try {
            timeDate = sDateFormat.parse(time);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long create = sdf.parse(time).getTime();
            Calendar now = Calendar.getInstance();
            long ms = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));//毫秒数
            long ms_now = now.getTimeInMillis();
            if (ms_now - create < ms) {
//                ret = "今天";
                ret = timeDate.getHours() + ":" + timeDate.getMinutes();
            } else if (ms_now - create < (ms + 24 * 3600 * 1000)) {
                ret = "昨天";
            } else if (ms_now - create < (ms + 24 * 3600 * 1000 * 2)) {
                ret = "前天";
            } else {
                if (now.getTime().getYear() == timeDate.getMonth())
                    ret = timeDate.getMonth() + "-" + timeDate.getDay();
                else
                    ret = timeDate.getYear() + "-" + timeDate.getMonth();
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static void setImgs(Context context, List<ImageView> ivs, List<String> urls) {
        CommonTools myTool = new CommonTools(context);
        //全部设置为不见
        for (int i = 0; i < ivs.size(); i++) {
            ivs.get(i).setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < ivs.size() && i < urls.size(); i++) {
            ivs.get(i).setVisibility(View.VISIBLE);
            myTool.showImage(urls.get(i), ivs.get(i));
        }
    }
}
