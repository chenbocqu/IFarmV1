/**
 * 分区
 */
package com.qican.ifarm.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.listener.ChooseItems;

import java.util.List;

public class CommonChooseAdapter<T extends ChooseItems> extends ComAdapter<T> {


    public CommonChooseAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(final ViewHolder helper, final T item) {

        helper.setText(R.id.tv_name, item.getName()).setImageByUrl(R.id.iv_img, item.getImgUrl());

        final CheckBox cbSelected = helper.getView(R.id.cb_selected);
        cbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(cbSelected.isChecked());
                if (cbSelected.isChecked()) {
                    removeChecked(item);
                }
                notifyDataSetChanged();

                if (itemClickListener != null) {
                    itemClickListener.onItemClick(helper, item);
                }
            }
        });
        cbSelected.setChecked(item.isSelected());
    }

    private void removeChecked(T defaultBean) {
        for (T bean : mDatas) {
            if (!(bean.equals(defaultBean))) {
                bean.setSelected(false);
            }
        }
    }
}
