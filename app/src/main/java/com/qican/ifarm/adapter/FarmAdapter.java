package com.qican.ifarm.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.listener.OnItemClickListener;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.DataBindUtils;
import com.qican.ifarm.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class FarmAdapter extends ComAdapter<Farm> {
    CommonTools myTool;
    OnItemClickListener<Farm> l;

    public FarmAdapter(Context context, List<Farm> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        myTool = new CommonTools(context);
    }

    @Override
    public void convert(final ViewHolder helper, final Farm item) {
        helper
                .setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_desc, item.getDesc())
                .setText(R.id.tv_time, TimeUtils.formatDateTime(item.getTime()))
                .setImageByUrl(R.id.iv_img, item.getImgUrl(), R.mipmap.default_farm_img);

        Label label = item.getLabel();
        List<TextView> tvList = new ArrayList<>();
        tvList.add((TextView) helper.getView(R.id.tv_label1));
        tvList.add((TextView) helper.getView(R.id.tv_label2));
        tvList.add((TextView) helper.getView(R.id.tv_label3));

        DataBindUtils.setLabel(tvList, null, label);

        LinearLayout llItem = helper.getView(R.id.ll_item);//Item点击事件
        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l != null) {
                    l.onItemClick(helper, item);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener<Farm> l) {
        this.l = l;
    }
}
