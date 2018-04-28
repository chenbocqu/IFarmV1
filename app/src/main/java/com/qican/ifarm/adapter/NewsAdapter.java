package com.qican.ifarm.adapter;

import android.content.Context;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.News;
import com.qican.ifarm.utils.TimeUtils;

import java.util.List;

public class NewsAdapter extends ComAdapter<News> {
    public NewsAdapter(Context context, List<News> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, News item) {
        helper.setText(R.id.tv_title, item.getTitle())
                .setText(R.id.tv_time, TimeUtils.formatTime(item.getTime()))
                .setText(R.id.tv_desc, item.getNewsDesc())
                .setImageByUrl(R.id.iv, item.getImgUrl());
    }
}
