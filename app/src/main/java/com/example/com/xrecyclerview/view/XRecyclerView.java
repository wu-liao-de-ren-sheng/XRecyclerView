package com.example.com.xrecyclerview.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.com.xrecyclerview.adapter.AdapterDataObserverImpl;
import com.example.com.xrecyclerview.adapter.WrapAdapter;

import java.util.ArrayList;

/**
 * Created by 键盘 on 2016/1/10.
 *
 */
public class XRecyclerView extends RecyclerView implements LoadingMoreFooterClickCallback{

    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }
    private static final float DRAG_RATE = 3;

    private boolean hasMore = false;// 还有更多
    private boolean pullRefreshEnabled = true;//下拉刷新
    private boolean loadingMoreEnabled = true;//上拉加载

    private ArrayList<View> mHeaderViews = new ArrayList<>();//头部view的集合
    private ArrayList<BaseMoreFooter> mFootViews   = new ArrayList<>();//尾部view的集合
    private Adapter mAdapter;//里层的adapter

    private float mLastY = -1;
    private int pageSize = 10;
    private int visibleThreshold = 1; // list到达 最后一个item的时候 触发加载
    private LoadingListener mLoadingListener;
    private ArrowRefreshHeader mRefreshHeader;//header view

    public XRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }
    private void init(Context context) {
        //添加一个header view 用于下拉刷新
        ArrowRefreshHeader refreshHeader = new ArrowRefreshHeader(context);
        mHeaderViews.add(0, refreshHeader);
        mRefreshHeader = refreshHeader;
        //添加一个footer view 用于上拉加载
        LoadingMoreFooter footView = new LoadingMoreFooter(context);
        footView.setLoadingMoreFooterClickCallback(this);
        addFootView(footView);
        mFootViews.get(0).setViewVisibility(GONE);
    }

    /**
     * 给当前RecyclerView添加headerView,可以添加多个,效果类似与listView的addHeaderView
     * @param view view
     */
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    /**
     * 给当前RecyclerView添加一个footView,只能添加一个,最后添加的会覆盖之前的.
     * @param view {@link BaseMoreFooter footer}
     */
    public void addFootView(BaseMoreFooter view) {
        mFootViews.clear();
        mFootViews.add(view);
    }

    /**
     *
     * @param view view
     */
    @Override
    public void onClick(View view) {
        if (mLoadingListener != null && loadingMoreEnabled && !isRefreshing()){
            BaseMoreFooter footView = mFootViews.get(0);
            footView.loading();
            mLoadingListener.onLoadMore();
        }
    }

    /**
     * 隐藏加载更多
     */
    public void stopLoadMore() {
        if (loadingMoreEnabled) {
            BaseMoreFooter footView = mFootViews.get(0);
            footView.complete();
        }
    }

    /**
     * 显示单击加载更多.这个footerView实现了单击加载更多的回调方法
     */
    public void clickLoadMore(){
        if (loadingMoreEnabled) {
            BaseMoreFooter footView = mFootViews.get(0);
            footView.clickLoadMore();
        }
    }

    /**
     * 没有更多数据了.这个时候不会在调用上拉加载的方法
     */
    public void noMoreLoading() {
        if (loadingMoreEnabled) {
            hasMore = true;
            BaseMoreFooter footView = mFootViews.get(0);
            footView.noMore();
        }
    }

    /**
     * 重置footer.如果当前 itemCount > {@link #pageSize} 调用 {@link #stopLoadMore()} 否则调用 {@link #clickLoadMore()}
     */
    public void restoreFooter(){
        if (loadingMoreEnabled){
            int itemCount = mAdapter.getItemCount();
            if (itemCount >= pageSize){
                stopLoadMore();
            }else {
                clickLoadMore();
            }
        }
    }

    /**
     * 恢复下拉加载的状态
     */
    public void refreshComplete() {
        if (pullRefreshEnabled) {
            mRefreshHeader.refreshComplete();
            restoreFooter();
        }
    }

    /**
     * 是否在下拉刷新中
     * @return true 如果是在刷新中 false 否则
     */
    public boolean isRefreshing(){
        return mRefreshHeader.isRefreshing();
    }

    /**
     * 是否开启下拉刷新
     */
    public void setPullRefreshEnabled(boolean enabled){
        pullRefreshEnabled = enabled;
    }

    /**
     * 是否开启上拉加载
     */
    public void setLoadingMoreEnabled(boolean enabled){
        if (loadingMoreEnabled != enabled) {
            loadingMoreEnabled = enabled;
            if (enabled){
                restoreFooter();
            }else {
                if (mFootViews.size() > 0) {
                    mFootViews.get(0).setViewVisibility(GONE);
                }
            }
        }
    }

    public void setArrowImageView(int resId) {
        if (mRefreshHeader != null){
            mRefreshHeader.setArrowImageView(resId);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        WrapAdapter wrapAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
        mAdapter.registerAdapterDataObserver(new AdapterDataObserverImpl(wrapAdapter));
        super.setAdapter(wrapAdapter);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        //上拉加载的实现
        if (loadingMoreEnabled) {
            BaseMoreFooter footView = mFootViews.get(0);
            //如果回调的监听不等与null,并且footView没有在加载中
            if (mLoadingListener != null && !footView.isLoading()) {
                LayoutManager layoutManager = getLayoutManager();
                int lastVisibleItemPosition = getLastVisibleItemPosition(layoutManager);
                //item大于0，并且到最后一个item，并且还有更多数据，并且没有在下拉刷新中,
                // 并且LoadingMoreFooter的状态不是click加载的状态
                if (layoutManager.getChildCount() > 0
                        && lastVisibleItemPosition >= layoutManager.getItemCount() - visibleThreshold
                        && !hasMore
                        && !isRefreshing()
                        && !footView.isClickLoadMore()) {
                    footView.loading();
                    mLoadingListener.onLoadMore();
                }
            }
        }
    }

    /**
     * 返回显示的最后一个view的position
     */
    public int getLastVisibleItemPosition(LayoutManager layoutManager){
        int lastVisibleItemPosition;
        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
            lastVisibleItemPosition = findMax(into);
        } else {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        return lastVisibleItemPosition;
    }
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //下拉刷新的实现
        if (pullRefreshEnabled) {
            if (mLastY == -1) {
                mLastY = ev.getRawY();
            }
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float deltaY = ev.getRawY() - mLastY;
                    mLastY = ev.getRawY();
                    if (isOnTop()) {
                        mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    }
                    break;
                default:
                    mLastY = -1;
                    if (isOnTop()) {
                        if (mRefreshHeader.releaseAction()) {
                            if (mLoadingListener != null) {
                                mLoadingListener.onRefresh();
                                hasMore = false;
                            }
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断RecycleView是否有下拉加载的view
     * @return true 有 false 没有
     */
    private boolean isOnTop() {
        if (mHeaderViews == null || mHeaderViews.isEmpty()) {
            return false;
        }

        View view = mHeaderViews.get(0);
        return null != view.getParent();
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public int getVisibleThreshold() {
        return visibleThreshold;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }
}
