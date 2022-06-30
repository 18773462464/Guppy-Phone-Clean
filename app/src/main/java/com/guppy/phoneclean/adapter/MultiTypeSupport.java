package com.guppy.phoneclean.adapter;

/**
 * 多布局支持接口
 */
public interface MultiTypeSupport<T> {
    /**
     * 根据当前位置或者条目数据返回布局
     * @param item 布局对象
     * @param position 位置
     * @return 返回一个id
     */
    int getLayoutId(T item, int position);
}