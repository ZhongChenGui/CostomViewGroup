package com.learning.costomviewgroup.flow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learning.costomviewgroup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengui.zhong
 * on 2022/4/21
 */
public class FlowLayout extends ViewGroup {
    // 默认最大行数
    private static final int DEFAULT_MAX_LINE = -1;
    // 默认圆角像素
    private static final int DEFAULT_BORDER_RADIUS = 10;
    // 默认最多文字数量
    private static final int DEFAULT_MAX_TEXT_LENGTH = -1;
    // item之间的默认垂直距离
    private static final float DEFAULT_VERTICAL_MARGIN = 20;
    // item之间的默认水平距离
    private static final float DEFAULT_HORIZONTAL_MARGIN = 20;
    private static final String TAG = "FlowLayout";
    private int mMaxLine;
    private float mItemVerticalMargin;
    private int mItemHorizontalMargin;
    private int mTextColor;
    private int mBorderColor;
    private float mBorderRadius;
    private int mTextMaxLength;
    private List<String> mData = new ArrayList<>();
    private OnItemClickListener mItemClickListener = null;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //todo 添加属性，设置属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mMaxLine = ta.getInteger(R.styleable.FlowLayout_maxLine, DEFAULT_MAX_LINE);
        mItemVerticalMargin = ta.getDimension(R.styleable.FlowLayout_ItemVerticalMargin, DEFAULT_VERTICAL_MARGIN);
        mItemHorizontalMargin = (int) ta.getDimension(R.styleable.FlowLayout_itemHorizontalMargin, DEFAULT_HORIZONTAL_MARGIN);
        mTextColor = ta.getColor(R.styleable.FlowLayout_textColor, -1);
        mBorderColor = ta.getColor(R.styleable.FlowLayout_borderColor, -1);
        mBorderRadius = ta.getDimension(R.styleable.FlowLayout_borderRadius, DEFAULT_BORDER_RADIUS);
        mTextMaxLength = ta.getInteger(R.styleable.FlowLayout_textMaxLength, DEFAULT_MAX_TEXT_LENGTH);
        ta.recycle();
    }

    public void setTextData(List<String> data) {
        this.mData.clear();
        this.mData.addAll(data);

        setUpChildren();
    }

    private void setUpChildren() {
        removeAllViews();
        for (String datum : mData) {
            TextView textView = new TextView(getContext());
            // todo 设置样式
            if (mTextMaxLength > 0) {
                textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mTextMaxLength)});
            }
            textView.setPadding(20, 10, 20, 10);
            textView.setTextSize(20);
            textView.setBackground(getResources().getDrawable(R.drawable.shape_flow_text_bg_normal));
            textView.setText(datum);
            textView.setSingleLine();
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextColor(R.drawable.selector_flow_text_color);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(view, datum);
                    }
                }
            });
            addView(textView);
        }
    }

    private List<List<View>> mLines = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        Log.d(TAG, "onMeasure:  parentWidthSize is  == > " + parentWidthSize);
        Log.d(TAG, "onMeasure:  mode is  == > " + mode);

        // 测量孩子
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }
        mLines.clear();
        List<View> line = new ArrayList<>();
        mLines.add(line);

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidthSpec = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.AT_MOST);
            int childHeightSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.AT_MOST);
            measureChild(child, childWidthSpec, childHeightSpec);

            if (line.size() == 0) {
                line.add(child);
            } else {
                // 判断是否能够添加
                boolean canBeAdd = checkChildCanBeAdd(child, line, parentWidthSize);
                if (canBeAdd) {
                    line.add(child);
                } else {
                    if (mMaxLine > 0 && mLines.size() >= mMaxLine) {
                        break;
                    }
                    line = new ArrayList<>();
                    line.add(child);
                    mLines.add(line);
                }
            }
        }
        // 测量自己
        View child = mLines.get(0).get(0);
        int parentHeightSpec = MeasureSpec.makeMeasureSpec(
                mLines.size() * child.getMeasuredHeight()
                        + (mLines.size() + 1) * (int) mItemVerticalMargin + getPaddingTop() + getPaddingBottom()
                , MeasureSpec.AT_MOST);
        setMeasuredDimension(widthMeasureSpec, parentHeightSpec);
    }

    private boolean checkChildCanBeAdd(View child, List<View> line, int parentWidthSize) {
        int measuredWidth = child.getMeasuredWidth();
        int totalWidth = getPaddingLeft() + getPaddingRight();
        for (View view : line) {
            totalWidth += view.getMeasuredWidth() + mItemHorizontalMargin;
        }
        totalWidth += measuredWidth;
        return totalWidth <= parentWidthSize;
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        // 摆放
        View firstChild = mLines.get(0).get(0);
        int currentLeft = mItemHorizontalMargin + getPaddingLeft();
        int currentTop = (int) mItemVerticalMargin + getPaddingTop();
        int currentRight = getPaddingLeft();
        int currentBottom = 0;
        for (List<View> line : mLines) {
            for (View child : line) {
                currentRight += child.getMeasuredWidth() + mItemHorizontalMargin;
                currentBottom = child.getMeasuredHeight() + currentTop;
                if (currentRight > getMeasuredWidth() - (mItemHorizontalMargin + getPaddingRight() + getPaddingLeft())) {
                    currentRight = getMeasuredWidth() - (mItemHorizontalMargin + getPaddingRight() + getPaddingLeft());
                }

                child.layout(currentLeft, currentTop, currentRight, currentBottom);
                currentLeft += child.getMeasuredWidth() + mItemHorizontalMargin;

            }
            currentLeft = mItemHorizontalMargin + getPaddingLeft();
            currentRight = getPaddingLeft();
            currentTop += firstChild.getHeight() + mItemVerticalMargin;
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, String text);
    }

    public int getMaxLine() {
        return mMaxLine;
    }

    public void setMaxLine(int maxLine) {
        mMaxLine = maxLine;
    }

    public float getItemVerticalMargin() {
        return mItemVerticalMargin;
    }

    public void setItemVerticalMargin(float itemVerticalMargin) {
        mItemVerticalMargin = itemVerticalMargin;
    }

    public float getItemHorizontalMargin() {
        return mItemHorizontalMargin;
    }

    public void setItemHorizontalMargin(int itemHorizontalMargin) {
        mItemHorizontalMargin = itemHorizontalMargin;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public float getBorderRadius() {
        return mBorderRadius;
    }

    public void setBorderRadius(float borderRadius) {
        mBorderRadius = borderRadius;
    }

    public int getTextMaxLength() {
        return mTextMaxLength;
    }

    public void setTextMaxLength(int textMaxLength) {
        mTextMaxLength = textMaxLength;
    }
}
