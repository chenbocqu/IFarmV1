package com.qican.ifarm.listener;


public interface InfoRequestListener<T> {
    void onFail(Exception e);

    void onSuccess(T obj);
}
