/**
 * 分区
 */
package com.qican.ifarm.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.listener.OnItemClickListener;
import com.qican.ifarm.utils.CommonTools;

import java.util.List;

public class SubareaAdapter extends CommonAdapter<Subarea> {
    private CommonTools myTool;

    public SubareaAdapter(Context context, List<Subarea> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        myTool = new CommonTools(context);
    }

    @Override
    public void convert(final ViewHolder helper, final Subarea item) {

        helper.setText(R.id.tv_name, item.getName()).setImageByUrl(R.id.iv, item.getImgUrl());
        RelativeLayout rlItem = helper.getView(R.id.rl_item);

        rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(helper, item);
            }
        });
    }
}
