package com.qican.ifarm.adapter;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ControlNode;

import java.util.List;

public class ControlNodeAdapter extends ComAdapter<ControlNode> {
    public ControlNodeAdapter(Context context, List<ControlNode> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(final ViewHolder helper, final ControlNode item) {

        helper
                .setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_desc, item.getDesc());

        final CheckBox cbSelected = helper.getView(R.id.cb_selected);

        cbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(cbSelected.isChecked());
//                if (cbSelected.isChecked()) {
//                    removeChecked(item);
//                }
                notifyDataSetChanged(); //处理好状态后要通知ListView重新装填数据（即是数据发生变化）

                if (itemClickListener != null) {
                    itemClickListener.onItemClick(helper, item);
                }
            }
        });
        cbSelected.setChecked(item.isSelected());
    }
}
