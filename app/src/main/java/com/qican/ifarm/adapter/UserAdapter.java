package com.qican.ifarm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.ui.near.NearInfoActivity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.TimeUtils;

import java.util.List;

public class UserAdapter extends ComAdapter<ComUser> {
    private CommonTools myTool;
    Bitmap female;
    Bitmap male;

    public UserAdapter(Context context, List<ComUser> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);

        myTool = new CommonTools(context);
        female = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_head_female);
        male = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_head_male);
    }

    @Override
    public void convert(ViewHolder helper, final ComUser near) {
        helper
                .setText(R.id.tv_nickname, near.getNickName())
                .setText(R.id.tv_desc, near.getSignature())
                .setText(R.id.tv_time, TimeUtils.formatDateTime(near.getLastLoginTime()));
        myTool.showSex(near.getSex(), (ImageView) helper.getView(R.id.iv_sex));
        if (near.getHeadImgUrl() != null) {
            myTool.showImage(near.getHeadImgUrl(), (ImageView) helper.getView(R.id.iv_headimg),
                    "男".equals(near.getSex()) ? R.drawable.default_head_male : R.drawable.default_head_female);
//                helper.setImageByUrl(R.id.iv_headimg, near.getHeadImgUrl());
        } else {
            //直接根据性别来设置头像图标
            ImageView headImg = helper.getView(R.id.iv_headimg);
            if (near.getSex() != null) {
                switch (near.getSex()) {
                    case "男":
                        headImg.setImageBitmap(male);
                        break;
                    case "女":
                        headImg.setImageBitmap(female);
                        break;
                    default:
                        break;
                }
            }
        }
        helper.getView(R.id.rl_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTool.startActivity(near, NearInfoActivity.class);
            }
        });
        setLabels(helper, near);
    }

    private void setLabels(ViewHolder helper, ComUser near) {
        //最多只显示这几个标签
        int labelRes[] = {R.id.tv_label1, R.id.tv_label2, R.id.tv_label3};
        //设置标签不见
        for (int i = 0; i < labelRes.length; i++) {
            helper.getView(labelRes[i]).setVisibility(View.GONE);
        }
        //如果设置了标签
        if (near.getLabels() != null) {
            String labels[] = near.getLabels().split(",");//标签以英文的","隔开
            for (int i = 0; i < labels.length && i < labelRes.length; i++) {
                helper.getView(labelRes[i]).setVisibility(View.VISIBLE);
                helper.setText(labelRes[i], labels[i]);
            }
        }
    }
}
