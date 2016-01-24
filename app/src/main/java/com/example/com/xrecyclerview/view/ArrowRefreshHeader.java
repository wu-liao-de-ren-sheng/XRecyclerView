package com.example.com.xrecyclerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.com.xrecyclerview.R;
import com.example.com.xrecyclerview.utils.DateTools;

import java.util.Date;

public class ArrowRefreshHeader extends LinearLayout{

    private static final int ROTATE_ANIM_DURATION = 180;

    private LinearLayout mContainer;
    private ImageView mArrowImageView;//小箭头
    private SpinView mProgressBar; //菊花加载
    private TextView mStatusTextView; //状态

    private TextView mHeaderTimeView; //时间
    private Animation mRotateUpAnim;

    private Animation mRotateDownAnim;
    private RefreshState mState = RefreshState.STATE_NORMAL; //当前的状态

	public int mMeasuredHeight;

	public ArrowRefreshHeader(Context context) {
		super(context);
		initView(context);
	}

	public ArrowRefreshHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {

        // 初始情况，设置下拉刷新view高度为0
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listview_header, null, false);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);

		this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

		addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.listview_header_arrow);
		mStatusTextView = (TextView)findViewById(R.id.refresh_status_textview);
        mHeaderTimeView = (TextView)findViewById(R.id.last_refresh_time);

        mProgressBar = (SpinView)findViewById(R.id.listview_header_progressbar);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);

        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

		measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		mMeasuredHeight = getMeasuredHeight();
	}

    public void setArrowImageView(int resId){
        mArrowImageView.setImageResource(resId);
    }

    /**
     * 刷新完成
     */
    public void refreshComplete(){
        mHeaderTimeView.setText(DateTools.friendlyTime(new Date()));
        done();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 500);
    }

    /**
     * 释放动作,如果当前显示的高度大于布局测量高度并且没有在刷新中,当前显示刷新状态.
     * 如果不满足刷新的条件,恢复到隐藏状态
     * @return true 满足刷新的条件 false 不满足刷新的条件
     */
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getCurrentLayoutHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if(getCurrentLayoutHeight() > mMeasuredHeight &&  !isRefreshing()){
            refreshing();
            isOnRefresh = true;
        }

        int destHeight = 0;
        if (mState == RefreshState.STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    /**
     * 刷新中的状态
     */
    private void refreshing(){
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mStatusTextView.setText(R.string.refreshing);
        mState = RefreshState.STATE_REFRESHING;
    }

    /**
     * 刷新完成的状态
     */
    private void done(){
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mStatusTextView.setText(R.string.refresh_done);
        mState = RefreshState.STATE_DONE;
    }

    /**
     * 刷新之前的状态
     */
    private void normal(){
        mArrowImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mState == RefreshState.STATE_RELEASE_TO_REFRESH) {
            mArrowImageView.startAnimation(mRotateDownAnim);
        }
        if (mState == RefreshState.STATE_REFRESHING) {
            mArrowImageView.clearAnimation();
        }
        mStatusTextView.setText(R.string.listview_header_hint_normal);
        mState = RefreshState.STATE_NORMAL;
    }

    /**
     * 释放后刷新的状态
     */
    private void releaseToRefresh(){
        mArrowImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mState != RefreshState.STATE_RELEASE_TO_REFRESH) {
            mArrowImageView.clearAnimation();
            mArrowImageView.startAnimation(mRotateUpAnim);
            mStatusTextView.setText(R.string.listview_header_hint_release);
        }
        mState = RefreshState.STATE_RELEASE_TO_REFRESH;
    }

    /**
     * 设置当前布局显示的高度
     * @param height 高度
     */
	private void setVisibleHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

    /**
     * @return 当前布局的高度
     */
	public int getCurrentLayoutHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
	}

    /**
     * 改变RefreshHeader的高度根据 (movedY + {@link #getCurrentLayoutHeight()})
     * @param movedY 移动的y
     */
    public void onMove(float movedY) {
        if(getCurrentLayoutHeight() > 0 || movedY > 0) {
            setVisibleHeight((int) movedY + getCurrentLayoutHeight());
            // 未处于刷新状态，更新箭头
            if (!isRefreshing()) {
                if (getCurrentLayoutHeight() > mMeasuredHeight) {
                    releaseToRefresh();
                }else {
                    normal();
                }
            }
        }
    }

    /**
     * 重置,恢复到刷新之前的状态
     */
    private void reset() {
        smoothScrollTo(0);
        normal();
    }

    /**
     * 设置当前view到一个指定的高度的过渡动画
     * @param destHeight 高度
     */
    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getCurrentLayoutHeight(), destHeight);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
     * 是否在刷新中
     * @return true 是 false 不是
     */
    public boolean isRefreshing(){
        return mState == RefreshState.STATE_REFRESHING || mState == RefreshState.STATE_DONE;
    }
}
