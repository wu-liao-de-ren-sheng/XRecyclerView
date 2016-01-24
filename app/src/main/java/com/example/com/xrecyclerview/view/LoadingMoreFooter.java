package com.example.com.xrecyclerview.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.com.xrecyclerview.R;

/**
 * Created by 键盘 on 2016/1/13 0013.
 * 上拉加载的footerView
 */
public class LoadingMoreFooter extends LinearLayout implements View.OnClickListener, BaseMoreFooter{

    private Context mContext;

    private SpinView mSpinView;
    private TextView mText;
    private LoadingState mState = LoadingState.STATE_COMPLETE;

    private LoadingMoreFooterClickCallback loadingMoreFooterClickCallback;

    public void setLoadingMoreFooterClickCallback(LoadingMoreFooterClickCallback loadingMoreFooterClickCallback) {
        this.loadingMoreFooterClickCallback = loadingMoreFooterClickCallback;
    }

    public LoadingMoreFooter(Context context) {
		super(context);
		initView(context);
	}

	public LoadingMoreFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
    public void initView(Context context ){
        mContext = context;
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int paddingSpace = getResources().getDimensionPixelOffset(R.dimen.space);
        setPadding(0, paddingSpace, 0, paddingSpace);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                getResources().getDimensionPixelOffset(R.dimen.progress_bar_size)
                ,getResources().getDimensionPixelOffset(R.dimen.progress_bar_size));
        mSpinView = new SpinView(context);
        mSpinView.setLayoutParams(lp);

        addView(mSpinView);
        mText = new TextView(context);
        mText.setText("正在加载...");

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) getResources().getDimension(R.dimen.textandiconmargin), 0, 0, 0);

        mText.setLayoutParams(layoutParams);
        addView(mText);
    }
    @Override
    public void loading(){
        mSpinView.setVisibility(View.VISIBLE);
        mText.setText(mContext.getText(R.string.listview_loading));
        this.setVisibility(View.VISIBLE);
        setOnClickListener(null);
        mState = LoadingState.STATE_LOADING;
    }
    @Override
    public void complete(){
        mText.setText(mContext.getText(R.string.listview_loading));
        this.setVisibility(View.GONE);
        setOnClickListener(null);
        mState = LoadingState.STATE_COMPLETE;
    }
    @Override
    public void noMore(){
        mText.setText(mContext.getText(R.string.nomore_loading));
        mSpinView.setVisibility(View.GONE);
        this.setVisibility(View.VISIBLE);
        setOnClickListener(null);
        mState = LoadingState.STATE_NO_MORE;
    }
    @Override
    public void clickLoadMore(){
        mText.setText("点击加载");
        mSpinView.setVisibility(View.GONE);
        this.setVisibility(View.VISIBLE);
        setOnClickListener(this);
        mState = LoadingState.STATE_CLICK_LOAD_MORE;
    }

    public boolean isClickLoadMore(){
        return mState == LoadingState.STATE_CLICK_LOAD_MORE;
    }

    @Override
    public boolean isLoading() {
        return mState == LoadingState.STATE_LOADING;
    }

    @Override
    public void setViewVisibility(int visibility) {
        setVisibility(visibility);
    }

    @Override
    public void onClick(View v) {
        if (loadingMoreFooterClickCallback != null){
            loadingMoreFooterClickCallback.onClick(v);
        }
    }
}
