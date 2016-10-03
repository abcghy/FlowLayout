package me.daolema.sakura.flowlayoutlib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by 高惠宇 on 16/10/3.
 */
public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.d("test", "widthMode: " + widthMode);
        Log.d("test", "widthSize: " + widthSize);
        Log.d("test", "heightMode: " + heightMode);
        Log.d("test", "heightSize: " + heightSize);

        int childCount = getChildCount();

        // 该控件总的宽和高
        int mWidth = 0;
        int mHeight = 0;

        // 计算时改行的宽和高
        int lineWidth = 0;
        int lineHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (childWidth + lineWidth > widthSize) {
                // 如果加上该子 view 的宽度,该列宽度超过最大宽度
                mWidth = Math.max(childWidth, lineWidth);
                lineWidth = childWidth;
                mHeight += lineHeight;
                lineHeight = childHeight;
            } else {
                // 如果没有超过最大宽度
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            if (i == childCount - 1) {
                // 如果是最后一个,宽度比比谁大,高度直接加上去
                mWidth = Math.max(lineWidth, mWidth);
                mHeight += lineHeight;
            }
        }

        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? widthSize : mWidth,
                (heightMode == MeasureSpec.EXACTLY) ? heightSize : mHeight);
    }

    private ArrayList<ArrayList<View>> mViewList = new ArrayList<>();
    private ArrayList<Integer> mLineHeight = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mViewList.clear();
        mLineHeight.clear();

        int mWidth = getMeasuredWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        ArrayList<View> lineViews = new ArrayList<>();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (childWidth + lineWidth > mWidth) {
                // 如果子 view + 现有宽度 大于 总宽度
                mLineHeight.add(lineHeight);
                mViewList.add(lineViews);
                lineHeight = childHeight;
                lineWidth = childWidth;
                lineViews = new ArrayList<>();
                lineViews.add(child);
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(childHeight, lineHeight);
                lineViews.add(child);
            }
        }
        // 最后一行
        mViewList.add(lineViews);
        mLineHeight.add(lineHeight);

        int left = 0;
        int top = 0;
        for (int i = 0; i < mViewList.size(); i++) {
            lineViews = mViewList.get(i);
            lineHeight = mLineHeight.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                child.layout(lc, tc, rc, bc);
                left += lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin;
            }

            left = 0;
            top += lineHeight;
        }
    }

}