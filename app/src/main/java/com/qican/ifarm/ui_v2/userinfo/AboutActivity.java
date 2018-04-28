package com.qican.ifarm.ui_v2.userinfo;

import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.ui.intro.IntroActivity;
import com.qican.ifarm.ui_v2.base.BaseActivityWithTitlebar;


public class AboutActivity extends BaseActivityWithTitlebar {

    @Override
    public String getUITitle() {
        return "关于";
    }

    @Override
    public void init() {
        findViewById(R.id.rl_intro).setOnClickListener(this);
        findViewById(R.id.rl_evaluate).setOnClickListener(this);
        findViewById(R.id.rl_msg).setOnClickListener(this);
        findViewById(R.id.rl_upgrade).setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_about;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.rl_intro:
                myTool.startActivityForResult("notFirstIn", IntroActivity.class, 0);
                break;

            case R.id.rl_evaluate:
                myTool.toHint();
                break;

            case R.id.rl_msg:
                myTool.toHint();
                break;

            case R.id.rl_upgrade:
                myTool.toHint();
                break;
        }
    }
}
