package com.qican.ifarm.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lwkandroid.widget.ninegridview.INineGridImageLoader;


public class ImageLoader implements INineGridImageLoader {
    @Override
    public void displayNineGridImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height) {
        Glide.with(context).load(url).into(imageView);
    }
}
