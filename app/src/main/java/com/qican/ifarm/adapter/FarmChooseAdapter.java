package com.qican.ifarm.adapter;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.listener.OnItemClickListener;

import java.util.List;

public class FarmChooseAdapter extends ComAdapter<Farm> {

    OnItemClickListener<Farm> l;

    public FarmChooseAdapter(Context context, List<Farm> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(final ViewHolder helper, final Farm item) {

        final CheckBox cbSelected = helper.getView(R.id.cb_selected);

//        helper.getView(R.id.ll_item);
        cbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(cbSelected.isChecked());
                if (cbSelected.isChecked()) {
                    removeChecked(item);
                }
                notifyDataSetChanged(); //处理好状态后要通知ListView重新装填数据（即是数据发生变化）

                if (l != null) {
                    l.onItemClick(helper, item);
                }
            }
        });
        cbSelected.setChecked(item.isSelected());

        helper.setText(R.id.tv_name, item.getName());
        if (item.getImgUrl() != null)
            helper.setImageByUrl(R.id.iv_img, item.getImgUrl());

    }

    private void removeChecked(Farm defaultBean) {
        for (Farm bean : mDatas) {
            if (!(bean.equals(defaultBean))) {
                bean.setSelected(false);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener<Farm> l) {
        this.l = l;
    }
}
