package com.example.com.xrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by 键盘 on 2016/1/13 0013.
 *
 */
public class AdapterDataObserverImpl extends RecyclerView.AdapterDataObserver {
    private RecyclerView.Adapter mWrapAdapter;

    public AdapterDataObserverImpl(RecyclerView.Adapter wrapAdapter){
        this.mWrapAdapter = wrapAdapter;
    }

    @Override
    public void onChanged() {
        mWrapAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
    }
}
