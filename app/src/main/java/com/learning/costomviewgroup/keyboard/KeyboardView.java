package com.learning.costomviewgroup.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learning.costomviewgroup.R;

/**
 * Created by chengui.zhong
 * on 2022/4/24
 */
public class KeyboardView extends ViewGroup {
    private static final float DEFAULT_NUMBER_SIZE = 30f;
    private static final String TAG = "KeyboardView";

    private static final int DEFAULT_ROW = 4;
    private static final int DEFAULT_COLUMN = 3;
    private static final int DEFAULT_MARGIN = 10;

    private int mColor;
    private float mNumberSize;
    private int mItemPressBg;
    private int mItemNormalBg;

    private int row = DEFAULT_ROW;
    private int column = DEFAULT_COLUMN;
    private int mItemMargin;
    private OnItemClickListener mOnItemClickListener;

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化属性
        initAttrs(context, attrs);
        // 添加子view
        setUpView();
    }

    private void setUpView() {
        for (int i = 0; i < 11; i++) {
            TextView tv = new TextView(getContext());
            if (i == 10) {
                tv.setTag(true);
                // 文字
                tv.setText("删除");
            } else {
                tv.setTag(false);
                // 文字
                tv.setText(String.valueOf(i));
            }

            // 居中
            tv.setGravity(Gravity.CENTER);
            // 大小
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNumberSize);
            // 字体颜色
            tv.setTextColor(mColor);
            // 设置背景
            tv.setBackground(itemProviderBg());

            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        if (view instanceof TextView) {
                            CharSequence text = ((TextView) view).getText();
                            mOnItemClickListener.onItemClick((String) text);
                        }
                    }
                }
            });
            addView(tv);
        }
    }

    private Drawable itemProviderBg() {

        GradientDrawable pressDrawable = new GradientDrawable();
        pressDrawable.setColor(mItemPressBg);
        pressDrawable.setCornerRadius(5);

        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(mItemNormalBg);
        normalDrawable.setCornerRadius(5);

        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
        bg.addState(new int[]{}, normalDrawable);
        return bg;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int verticalPadding = getPaddingTop() + getPaddingBottom();

        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "onMeasure: parentWidthSize is  -- > " + parentWidthSize);
        Log.d(TAG, "onMeasure: parentHeightSize is  -- > " + parentHeightSize);
        // 测量孩子
        int itemWidth = (parentWidthSize - ((column + 1) * mItemMargin) - horizontalPadding) / column;
        int itemHeight = (parentHeightSize - ((row + 1) * mItemMargin) - verticalPadding) / row;
        Log.d(TAG, "onMeasure: itemWidth is  - > " + itemWidth);
        Log.d(TAG, "onMeasure: itemHeight is  - > " + itemHeight);
        int normalWidthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);
        int deleteWidthSpec = MeasureSpec.makeMeasureSpec(itemWidth * 2 + mItemMargin, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View item = getChildAt(i);
            boolean isDelete = (boolean) item.getTag();
            item.measure(isDelete ? deleteWidthSpec : normalWidthSpec, heightSpec);
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        // 定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KeyboardView);
        // 文字颜色
        mColor = ta.getColor(R.styleable.KeyboardView_numberColor, getResources().getColor(R.color.teal_200));
        // 文字大小
        mNumberSize = ta.getDimension(R.styleable.KeyboardView_numberSize, DEFAULT_NUMBER_SIZE);
        // 边距
        mItemMargin = (int) ta.getDimension(R.styleable.KeyboardView_itemMargin, DEFAULT_MARGIN);
        // 按下背景色
        mItemPressBg = ta.getColor(R.styleable.KeyboardView_itemPressBg, getResources().getColor(R.color.item_press_gray));
        // 默认背景色
        mItemNormalBg = ta.getColor(R.styleable.KeyboardView_itemNormalBg, getResources().getColor(R.color.item_normal_gray));
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int childCount = getChildCount();
        int left = mItemMargin + paddingLeft, top, right, bottom;
        for (int j = 0; j < childCount; j++) {
            View view = getChildAt(j);
            int rowIndex = j / column;
            int columnIndex = j % column;
            if (columnIndex == 0) {
                left = mItemMargin + paddingLeft;
            }
            top = rowIndex * view.getMeasuredHeight() + mItemMargin * (rowIndex + 1) + paddingTop;
            right = left + view.getMeasuredWidth();
            bottom = top + view.getMeasuredHeight();
            view.layout(left, top, right, bottom);
            left += view.getMeasuredWidth() + mItemMargin;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String text);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public float getNumberSize() {
        return mNumberSize;
    }

    public void setNumberSize(float numberSize) {
        mNumberSize = numberSize;
    }

    public int getItemPressBg() {
        return mItemPressBg;
    }

    public void setItemPressBg(int itemPressBg) {
        mItemPressBg = itemPressBg;
    }

    public int getItemNormalBg() {
        return mItemNormalBg;
    }

    public void setItemNormalBg(int itemNormalBg) {
        mItemNormalBg = itemNormalBg;
    }

    public int getItemMargin() {
        return mItemMargin;
    }

    public void setItemMargin(int itemMargin) {
        mItemMargin = itemMargin;
    }
}
