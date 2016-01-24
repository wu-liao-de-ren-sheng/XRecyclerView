package com.example.com.xrecyclerview.view;

/**
 * Created by 键盘 on 2016/1/12 0012.
 * 下拉刷新的状态
 */
public enum RefreshState {
    /**
     * 刷新之前的状态
     */
    STATE_NORMAL,
    /**
     * 释放刷新
     */
    STATE_RELEASE_TO_REFRESH,
    /**
     * 刷新中
     */
    STATE_REFRESHING,
    /**
     * 刷新完成
     */
    STATE_DONE
}
