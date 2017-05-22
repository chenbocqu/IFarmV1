/**
 * 自定义Item点击事件
 */
package com.qican.ifarm.listener;

import com.qican.ifarm.adapter.ViewHolder;

public interface OnItemClickListener<T> {
    void onItemClick(ViewHolder helper, T item);
}
