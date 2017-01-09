package com.qican.ifarm.listener;

import android.support.v4.app.Fragment;

public interface OnFragmentListener {
    //处理不同的消息
    void onMessage(Fragment fg, Object obj);
}
