package com.qican.ifarm.adapter;

import android.content.Context;
import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.ui.control.TaskPreviewerActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.TimeUtils;

import java.util.List;

public class SystemAdapter extends ComAdapter<ControlSystem> {

    private CommonTools myTool;

    public SystemAdapter(Context context, List<ControlSystem> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        myTool = new CommonTools(context);
    }

    @Override
    public void convert(ViewHolder helper, final ControlSystem item) {

        if (item.getName() == null) item.setName(item.getSysType());
        if (item.getImgUrl() == null)
            item.setImgUrl("http://img5.imgtn.bdimg.com/it/u=1786812688,1537849891&fm=23&gp=0.jpg");

        helper
                .setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_desc, item.getDesc())
                .setText(R.id.tv_time, TimeUtils.formatDateTime(item.getTime()));

        helper.setImageByUrl(R.id.iv_img, item.getImgUrl());

        helper.getView(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivity(item, TaskPreviewerActivity.class);
            }
        });
    }
}
