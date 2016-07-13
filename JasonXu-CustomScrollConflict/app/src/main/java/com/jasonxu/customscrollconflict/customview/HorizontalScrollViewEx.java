package com.jasonxu.customscrollconflict.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by t_xuz on 7/7/16.
 * //实现思路:
 * 1.先处理onMeasure和onLayout内的逻辑,让其能放入几个等屏幕宽的listview
 * 2.再处理滑动冲突;
 *
 * 获得的东西:
 * 1.onmesure过程可以通过measureChild 也可以通过 measureChildren来测量每个子元素的大小,通过子元素来计算viewgroup的大小
 * 2.onLayout 过程中,child.layout(l,t,r,b)可以加入布局参数,来布局子元素的位置
 *
 * 3.滑动冲突发生时,采用外部拦截法做处理:
 * a.当某个条件产生时,如距离差来判断是水平滑动还是竖直滑动,如果最外层是需要水平滑动的话,那么在外层的onInterceptTouchEvent方法中,根据距离差
 * 拦截事件,返回true,其他情况不能拦截,尤其是down条件下,因为一旦拦截,不会传递给子元素了;
 * b.父容器拦截了事件,需要自己在父容器的onTouchEvent方法中做逻辑处理.
 *
 */
public class HorizontalScrollViewEx extends ViewGroup{

    //分别记录上次滑动的坐标:
    private int mLastX = 0 ;
    private int mLastY = 0;

    //分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastInterceptX = 0;
    private int mLastInterceptY = 0;

    private int mChildWidth;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMinVelocity;
    private int childCounts;
    private int mChildIndex;

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public HorizontalScrollViewEx(Context context) {
        this(context,null);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mScroller = new Scroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                if (!mScroller.isFinished()){
                    //这句是为了优化滑动体验加入的
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastInterceptX;
                int deltaY = y - mLastInterceptY;
                if (Math.abs(deltaX) > Math.abs(deltaY)){ //水平方向滑动距离大于竖直方向滑动距离的时候拦截
                    intercepted = true;
                }else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }
        mLastInterceptX = x;
        mLastInterceptY = y;

        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltax = x-mLastX;
                scrollBy(-deltax,0);
                break;
            case MotionEvent.ACTION_UP:
                int scollX = getScrollX();
                int scrollToChildIndex = scollX / mChildWidth;
                //用于计算速度
                mVelocityTracker.computeCurrentVelocity(1000);
                //获取x方向的速度
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity)>=mMinVelocity) {
                    mChildIndex = xVelocity>0?mChildIndex-1:mChildIndex+1;
                }else {
                    mChildIndex = (scollX + mChildWidth/2)/mChildWidth;
                }
                mChildIndex = Math.max(0,Math.min(mChildIndex,childCounts-1));
                int dx = mChildIndex * mChildWidth -scollX;
                smoothScrollBy(dx,0);
                mVelocityTracker.clear();
                break;
        }

        mLastX = x;
        mLastY = y;

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);

        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int measureWidth = 0;
        int measureHeight = 0;
        int childCount = getChildCount();

        if (childCount == 0){
            setMeasuredDimension(0,0);
        }else {
            if (heightMeasureMode == MeasureSpec.AT_MOST){
                final View childView = getChildAt(0);
                measureHeight = childView.getMeasuredHeight();
                setMeasuredDimension(widthMeasureSize,measureHeight);
            }else if (widthMeasureMode == MeasureSpec.AT_MOST){
                final View childView = getChildAt(0);
                measureWidth = childView.getMeasuredWidth() * childCount;
                setMeasuredDimension(measureWidth,heightMeasureSize);
            }else {
                final View childView = getChildAt(0);
                measureHeight = childView.getMeasuredHeight();
                measureWidth = childView.getMeasuredWidth()* childCount;
                setMeasuredDimension(measureWidth,measureHeight);
            }
        }
    }

    @Override
    protected void onLayout(boolean b, int left, int topp, int right, int bottom) {
        int childCount = getChildCount();
        childCounts = childCount;
        int childLeft = 0;
        for (int i=0;i<childCount;i++){
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                mChildWidth = getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + child.getMeasuredWidth(), child.getMeasuredHeight());
                childLeft += child.getMeasuredWidth();
            }
        }
    }

    private void smoothScrollBy(int dx,int dy){
        //代表500ms内滑向dx,效果就是缓慢滑动
        mScroller.startScroll(getScrollX(),0,dx,0,500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }
}
