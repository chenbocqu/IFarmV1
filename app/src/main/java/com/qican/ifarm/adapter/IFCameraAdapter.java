package com.qican.ifarm.adapter;

import android.content.Context;
import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.IFarmCamera;

import java.util.List;

public class IFCameraAdapter extends ComAdapter<IFarmCamera> {
    public IFCameraAdapter(Context context, List<IFarmCamera> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(final ViewHolder helper, final IFarmCamera item) {
        helper.setImageByUrl(R.id.iv_img, item.getPreImgUrl(), R.mipmap.default_farm_img);
        helper
                .setText(R.id.tv_shed, item.getShedNo() + "号棚")
                .setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_desc, item.getLocation());

        helper.getView(R.id.rl_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(helper, item);
            }
        });
    }
}
