package com.learning.costomviewgroup.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learning.costomviewgroup.R;

/**
 * Created by chengui.zhong
 * on 2022/4/25
 */
public class SlideMenuView extends ViewGroup {
    private static final String TAG = "SlideMenuView";
    private View mEditView;
    private View mContentView;

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
        int function = ta.getInt(R.styleable.SlideMenuView_function, 0x30);
        Log.d(TAG, "SlideMenuView: " + Integer.toBinaryString(function));
        Log.d(TAG, "SlideMenuView: " + Integer.toBinaryString(0x50));

        ta.recycle();
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
        addView(mEditView);
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

        if (contentHeight == LayoutParams.WRAP_CONTENT){
            contentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.AT_MOST);
        }else if (contentHeight == LayoutParams.MATCH_PARENT){
            contentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.EXACTLY);
        }else{
            contentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY);
        }

        mContentView.measure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY),contentHeightMeasureSpec);

        int contentMeasuredHeight = mContentView.getMeasuredHeight();
        // 测编辑部分
        int editWidthSize = parentWidthSize * 3 / 4;
        mEditView.measure(MeasureSpec.makeMeasureSpec(editWidthSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(contentMeasuredHeight, MeasureSpec.EXACTLY));

        // 测量自己
        setMeasuredDimension(parentWidthSize + editWidthSize, contentMeasuredHeight);
    }
    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        //摆放内容部分
        int contentLeft = -800;
        int contentTop = 0;
        int contentRight = contentLeft + mContentView.getMeasuredWidth();
        int contentBottom = contentTop + mContentView.getMeasuredHeight();
        mContentView.layout(contentLeft, contentTop, contentRight, contentBottom);
        //摆放编辑部分
        int editViewLeft = contentRight;
        int editViewTop = 0;
        int editViewRight = editViewLeft + mEditView.getMeasuredWidth();
        int editViewBottom = editViewTop + mEditView.getMeasuredHeight();
        mEditView.layout(editViewLeft, editViewTop, editViewRight, editViewBottom);
    }
}
