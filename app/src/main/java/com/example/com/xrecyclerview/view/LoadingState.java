package com.example.com.xrecyclerview.view;

/**
 * Created by 键盘 on 2016/1/13 0013.
 *  上拉加载的状态
 */
public enum LoadingState {
    /**
     * 加载中
     */
    STATE_LOADING,
    /**
     * 加载完成
     */
    STATE_COMPLETE,
    /**
     * 没有更多了
     */
    STATE_NO_MORE,
    /**
     * 单击加载
     */
    STATE_CLICK_LOAD_MORE
}
