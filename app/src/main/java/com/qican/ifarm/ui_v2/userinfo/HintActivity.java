package com.qican.ifarm.ui_v2.userinfo;

import com.qican.ifarm.R;
import com.qican.ifarm.ui_v2.base.BaseActivityWithTitlebar;

public class HintActivity extends BaseActivityWithTitlebar {


    @Override
    public String getUITitle() {
        return "提示";
    }

    @Override
    public void init() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_hint;
    }
}
