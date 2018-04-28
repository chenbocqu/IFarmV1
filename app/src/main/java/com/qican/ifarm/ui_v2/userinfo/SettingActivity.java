package com.qican.ifarm.ui_v2.userinfo;

import android.view.View;

import com.qican.ifarm.R;
import com.qican.ifarm.ui.login.IpModifyDialog;
import com.qican.ifarm.ui_v2.base.BaseActivityWithTitlebar;
import com.videogo.openapi.EZOpenSDK;


public class SettingActivity extends BaseActivityWithTitlebar {

    IpModifyDialog ipModifyDialog;

    @Override
    public String getUITitle() {
        return "设置";
    }

    @Override
    public void init() {

        ipModifyDialog = new IpModifyDialog(this, R.style.Translucent_NoTitle);

        findViewById(R.id.rl_ipaddress).setOnClickListener(this);
        findViewById(R.id.rl_about).setOnClickListener(this);
        findViewById(R.id.rl_link_ez).setOnClickListener(this);
        findViewById(R.id.rl_logout_ez).setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.rl_ipaddress:
                ipModifyDialog.show();
                break;

            case R.id.rl_link_ez:
                EZOpenSDK.getInstance().openLoginPage();
                break;

            case R.id.rl_logout_ez:
                EZOpenSDK.getInstance().logout();//注销
                myTool.showInfo("注销成功！");
                break;

            case R.id.rl_about:
                myTool.startActivity(AboutActivity.class);
                break;
        }
    }
}
