/**
 * 添加池塘
 */
package com.qican.ifarm.ui.userinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.utils.CommonTools;

import me.xiaopan.sketch.SketchImageView;


public class HeadInfoActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "HeadInfoActivity";
    private String path;
    private SketchImageView sketchImageView;
    private CommonTools myTool;
    private LinearLayout llBack;
    private ComUser userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headinfo);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        if (!myTool.isLogin()) {
            return;
        }
        userInfo = myTool.getComUserInfoById(myTool.getUserId());
        if (userInfo == null) {
            return;
        }
        // 通过文件路径设置
        sketchImageView.displayImage(userInfo.getHeadImgUrl());
        sketchImageView.setSupportZoom(true);
    }

    private void initView() {
        sketchImageView = (SketchImageView) findViewById(R.id.iv_preview);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        myTool = new CommonTools(this);
    }

    private void initEvent() {
        sketchImageView.setOnClickListener(this);
        llBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_preview:
                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }
}
