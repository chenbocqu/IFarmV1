package com.qican.ifarm.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.qican.ifarm.bean.Img;
import com.qican.ifarm.utils.CommonTools;

public class ImageAdapter extends NineGridImageViewAdapter<Img> {
    @Override
    protected void onDisplayImage(Context context, ImageView imageView, Img img) {
        CommonTools myTool = new CommonTools(context);
        myTool.showImage(img.getUrl(), imageView);
    }
}
