package com.qican.ifarm.adapter;

import android.content.Context;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Question;
import com.qican.ifarm.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends ComAdapter<Question> {
    public QuestionAdapter(Context context, List<Question> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, Question item) {
        helper.setText(R.id.tv_title, item.getTitle())
                .setText(R.id.tv_desc, item.getDesc())
                .setText(R.id.tv_time, TimeUtils.formatTime(item.getTime()));
        ArrayList<String> imgUrls = item.getImgUrls();

        for (int i = 0; i < imgUrls.size(); i++) {
            switch (i) {
                case 0:
                    helper.setImageByUrl(R.id.iv_img1, imgUrls.get(i));
                    break;
                case 1:
                    helper.setImageByUrl(R.id.iv_img2, imgUrls.get(i));
                    break;
                case 2:
                    helper.setImageByUrl(R.id.iv_img3, imgUrls.get(i));
                    break;
                default:
                    break;
            }
        }
    }
}
