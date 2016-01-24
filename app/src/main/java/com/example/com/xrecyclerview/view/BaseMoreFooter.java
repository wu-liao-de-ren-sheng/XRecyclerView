package com.example.com.xrecyclerview.view;

/**
 * Created by 键盘 on 2016/1/13 0013.
 * 上拉加载的接口,实现类必须继承于view
 * 如果你的项目里只到了一种上拉加载的view,那么你完全可以把这个接口删除掉.
 */
public interface BaseMoreFooter {
    /**
     * 加载中
     */
    public void loading();

    /**
     * 加载完成
     */
    public void complete();

    /**
     * 没有更多数据了
     */
    public void noMore();

    /**
     * 点击加载更多
     */
    public void clickLoadMore();

    /**
     * 当前状态是否是点击加载更多
     * @return true 是 false 不是
     */
    public boolean isClickLoadMore();

    /**
     * 当前是否是加载中
     * @return true 是 false 不是
     */
    public boolean isLoading();

    /**
     * 这个在子类掉用view 的 {@link android.view.View#setVisibility(int visibility) setVisibility}
     */
    public void setViewVisibility(int visibility);
}
