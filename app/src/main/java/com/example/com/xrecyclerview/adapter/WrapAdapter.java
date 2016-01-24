package com.example.com.xrecyclerview.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.example.com.xrecyclerview.view.BaseMoreFooter;

import java.util.ArrayList;

/**
 * Created by 键盘 on 2016/1/10.
 *
 */
public class WrapAdapter extends RecyclerView.Adapter{
    private static final int TYPE_REFRESH_HEADER =  -5;
    private static final int TYPE_HEADER =  -4;
    private static final int TYPE_FOOTER =  -3;
    private static final int TYPE_NORMAL =  0;

    private RecyclerView.Adapter adapter;
    private ArrayList<View> mHeaderViews;
    private ArrayList<BaseMoreFooter> mFootViews;

    private int headerPosition = 1;

    public WrapAdapter(ArrayList<View> headerViews, ArrayList<BaseMoreFooter> footViews, RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        this.mHeaderViews = headerViews;
        this.mFootViews = footViews;
    }
//        解释一下，我们设置了一个SpanSizeLookup，这个类是一个抽象类，而且仅有一个抽象方法getSpanSize，
//        这个方法的返回值决定了我们每个position上的item占据的单元格个数。假如GridLayoutManager设置的每行的个数为2的话，
//        如果当前位置是header的位置，那么该item占据2个单元格，正常情况下占据1个单元格
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeader(position)||  isFooter(position)) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }
    //        这里的处理方式是用通过LayoutParams，而且这里更简单，StaggeredGridLayoutManager.LayoutParams为我们提供了一个setFullSpan方法来设置占领全部空间
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if(lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                &&  (isHeader( holder.getLayoutPosition()) || isFooter( holder.getLayoutPosition())) ) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    public boolean isHeader(int position) {
        return position >= 0 && position < mHeaderViews.size();
    }

    public boolean isFooter(int position) {
        return position < getItemCount() && position >= getItemCount() - mFootViews.size();
    }

    public boolean isRefreshHeader(int position) {
        return position == 0 ;
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_REFRESH_HEADER) {
            return new SimpleViewHolder(mHeaderViews.get(0));
        } else if (viewType == TYPE_HEADER) {
            return new SimpleViewHolder(mHeaderViews.get(headerPosition++ ));
        } else if (viewType == TYPE_FOOTER) {
            return new SimpleViewHolder((View) mFootViews.get(0));
        }
        return adapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position)) {
            return;
        }
        int adjPosition = position - getHeadersCount();
        int adapterCount;
        if (adapter != null) {
            adapterCount = adapter.getItemCount();
            if (adjPosition < adapterCount) {
                adapter.onBindViewHolder(holder, adjPosition);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (adapter != null) {
            return getHeadersCount() + getFootersCount() + adapter.getItemCount();
        } else {
            return getHeadersCount() + getFootersCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isRefreshHeader(position)){
            return TYPE_REFRESH_HEADER;
        }
        if (isHeader(position)) {
            return TYPE_HEADER;
        }
        if(isFooter(position)){
            return TYPE_FOOTER;
        }
        int adjPosition = position - getHeadersCount();
        int adapterCount;
        if (adapter != null) {
            adapterCount = adapter.getItemCount();
            if (adjPosition < adapterCount) {
                return adapter.getItemViewType(adjPosition);
            }
        }
        return TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        if (adapter != null && position >= getHeadersCount()) {
            int adjPosition = position - getHeadersCount();
            int adapterCount = adapter.getItemCount();
            if (adjPosition < adapterCount) {
                return adapter.getItemId(adjPosition);
            }
        }
        return -1;
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(observer);
        }
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
