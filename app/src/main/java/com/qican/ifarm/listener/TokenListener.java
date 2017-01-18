package com.qican.ifarm.listener;

/**
 * Created by Administrator on 2017/1/14 0014.
 */
public interface TokenListener {
    void updateSuccess(String token);

    void updateFailed(Exception e);
}
