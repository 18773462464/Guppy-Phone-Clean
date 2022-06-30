package com.guppy.phoneclean.adapter;
/**
 * 通用 Adapter长按事件
 */
public interface OnLongClickListener {
    /**
     * 长按事件
     * @param position 位置
     * @return 长按成功否
     */
    boolean onLongClick(int position);
}
