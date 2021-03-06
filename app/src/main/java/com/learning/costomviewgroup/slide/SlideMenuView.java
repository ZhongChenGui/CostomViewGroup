package com.learning.costomviewgroup.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.learning.costomviewgroup.R;

/**
 * Created by chengui.zhong
 * on 2022/4/25
 */
public class SlideMenuView extends ViewGroup implements View.OnClickListener {
    private static final String TAG = "SlideMenuView";
    private View mEditView;
    private View mContentView;
    private OnEditClickListener mOnEditClickListener = null;
    private View mTopTv;
    private View mReadTv;
    private View mDeleteTv;
    private int mFunction;
    private float mDownX;
    private int mContentLeft = 0;
    private Scroller mScroller;
    private float mInterceptDownX;
    private float mInterceptDownY;

    public SlideMenuView(Context context) {
        this(context, null);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 设置属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideMenuView);
        mFunction = ta.getInt(R.styleable.SlideMenuView_function, 0x30);
        ta.recycle();
        mScroller = new Scroller(context);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 1) {
            throw new IllegalArgumentException("只能添加一个子view");
        }
        mContentView = getChildAt(0);
        // 载入view
        mEditView = LayoutInflater.from(getContext()).inflate(R.layout.item_slide_layout, this, false);
        initEditView();
        addView(mEditView);
    }

    private void initEditView() {
        mReadTv = mEditView.findViewById(R.id.read_tv);
        mTopTv = mEditView.findViewById(R.id.top_tv);
        mDeleteTv = mEditView.findViewById(R.id.delete_tv);

        mReadTv.setOnClickListener(this);
        mTopTv.setOnClickListener(this);
        mDeleteTv.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        // 测量第一个孩子，也就是内容部分
        // 宽度：跟父控件一样宽，高度有三种情况，如果置顶大小，那就获取大小，直接测量
        // 如果是包裹内容，就给at_most, 如果match_parent, 就给定跟父控件一样高
        LayoutParams contentLayoutParams = mContentView.getLayoutParams();
        int contentHeight = contentLayoutParams.height;
        int contentHeightMeasureSpec;

        if (contentHeight == LayoutParams.WRAP_CONTENT) {
            contentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.AT_MOST);
        } else if (contentHeight == LayoutParams.MATCH_PARENT) {
            contentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.EXACTLY);
        } else {
            contentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
        }

        mContentView.measure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), contentHeightMeasureSpec);

        int contentMeasuredHeight = mContentView.getMeasuredHeight();
        // 测编辑部分
        int editWidthSize = parentWidthSize * 3 / 4;
        mEditView.measure(MeasureSpec.makeMeasureSpec(editWidthSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(contentMeasuredHeight, MeasureSpec.EXACTLY));

        // 测量自己
        setMeasuredDimension(parentWidthSize + editWidthSize, contentMeasuredHeight);
    }

    private boolean isOpen = false;  // 是否打开

    enum Direction {
        LEFT, RIGHT, NONE
    }

    private Direction mDirection = Direction.NONE;

    private void open() {
        int dx = mEditView.getMeasuredWidth() - getScrollX();
        int duration = (int) (dx / (mEditView.getMeasuredWidth() * 4 / 5f) * mDuration);
        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(duration));
        invalidate();
        isOpen = true;
    }

    private void close() {
        int dx = -getScrollX();
        int duration = (int) (dx / (mEditView.getMeasuredWidth() * 4 / 5f) * mDuration);
        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(duration));
        invalidate();
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInterceptDownX = ev.getX();
                mInterceptDownY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                if (x - mInterceptDownX > 5) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    // 滑动时长
    private int mDuration = 600;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "onTouchEvent: up...............");
                int hasBeenScrolled = getScrollX();
                if (isOpen) {
                    // 打开
                    if (mDirection == Direction.LEFT) {
                        open();
                    } else if (mDirection == Direction.RIGHT) {
                        if (hasBeenScrolled <= mEditView.getMeasuredWidth() * 4 / 5) {
                            close();
                        } else {
                            open();
                        }
                    }
                } else {
                    //关闭
                    if (mDirection == Direction.LEFT) {
                        // 向左滑动
                        if (hasBeenScrolled > mEditView.getMeasuredWidth() / 5) {
                            open();
                        } else {
                            close();
                        }
                    } else if (mDirection == Direction.RIGHT) {
                        // 向右滑动
                        close();
                    }
                }
                if (hasBeenScrolled >= mEditView.getMeasuredWidth() / 2) {
                    // 显示
                } else {
                    // 隐藏
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                int dx = (int) (moveX - mDownX);
//                Log.d(TAG, "onTouchEvent: dx is  - > " + dx);
                if (dx > 0) {
                    mDirection = Direction.RIGHT;
                } else {
                    mDirection = Direction.LEFT;
                }

                int scrollX = getScrollX();
                int resultScrollX = -dx + scrollX;
                if (resultScrollX <= 0) {
                    scrollTo(0, 0);
                } else if (resultScrollX > mEditView.getMeasuredWidth()) {
                    scrollTo(mEditView.getMeasuredWidth(), 0);
                } else {
                    scrollBy(-dx, 0);
                }
//                mContentLeft += dx;
//                if (mContentLeft >= 0) {
//                    mContentLeft = 0;
//                } else if (mContentLeft <= -mEditView.getMeasuredWidth()) {
//                    mContentLeft = -mEditView.getMeasuredWidth();
//                }
//                Log.d(TAG, "onTouchEvent: mContentLeft is  - > " + mContentLeft);
//                requestLayout();
                mDownX = moveX;
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        //摆放内容部分
        int contentTop = 0;
        int contentRight = mContentLeft + mContentView.getMeasuredWidth();
        int contentBottom = contentTop + mContentView.getMeasuredHeight();
        mContentView.layout(mContentLeft, contentTop, contentRight, contentBottom);
        //摆放编辑部分
        int editViewLeft = contentRight;
        int editViewTop = 0;
        int editViewRight = editViewLeft + mEditView.getMeasuredWidth();
        int editViewBottom = editViewTop + mEditView.getMeasuredHeight();
        mEditView.layout(editViewLeft, editViewTop, editViewRight, editViewBottom);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.mOnEditClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnEditClickListener == null) {
            Log.d(TAG, "onClick: mOnEditClickListener is  null..........");
            return;
        }
        close();
        if (v == mReadTv) {
            mOnEditClickListener.onReadClick();
        } else if (v == mTopTv) {
            mOnEditClickListener.onTopClick();
        } else if (v == mDeleteTv) {
            mOnEditClickListener.onDeleteClick();
        }
    }

    public interface OnEditClickListener {
        void onReadClick();

        void onTopClick();

        void onDeleteClick();
    }

}
