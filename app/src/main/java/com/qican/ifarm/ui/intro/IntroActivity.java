package com.qican.ifarm.ui.intro;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.qican.ifarm.ui.ComMainActivity;
import com.qican.ifarm.utils.CommonTools;

public class IntroActivity extends AppIntro {
    private CommonTools myTool;
    private Slide1 slide1;
    private Slide2 slide2;
    private Slide3 slide3;
    private Slide4 slide4;
    private String msg = "null";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Note here that we DO NOT use setContentView();
        // 页面设置为沉浸式状态栏风格
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myTool = new CommonTools(this);

        if (myTool != null)
            msg = (String) myTool.getParam("isFirstIn");

        // Add your slide fragments here.
        slide1 = new Slide1();
        slide2 = new Slide2();
        slide3 = new Slide3();
        slide4 = new Slide4();

        addSlide(slide1);
        addSlide(slide2);
        addSlide(slide3);
        addSlide(slide4);

        setColorTransitionsEnabled(true);
        // OPTIONAL METHODS
        setSeparatorColor(Color.parseColor("#ffffff"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        pager.setCurrentItem(fragments.size() - 1, true);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        if (msg == null) msg = "null";
        switch (msg) {
            case "firstIn":
                myTool.startActivity(ComMainActivity.class);
                break;
            case "notFirstIn":
                break;
            default:
                myTool.startActivity(ComMainActivity.class);
        }
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
