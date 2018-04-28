package com.qican.ifarm.listener;

public interface OnItemsLoadListener<T> {
    void loadCompleted(T items);
}
