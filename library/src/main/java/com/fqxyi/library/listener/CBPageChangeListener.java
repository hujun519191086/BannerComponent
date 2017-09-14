package com.fqxyi.library.listener;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 翻页指示器适配器
 */
public class CBPageChangeListener implements ViewPager.OnPageChangeListener {

    private ArrayList<ImageView> pointViews;
    private int[] page_indicatorId;

    private TextView textView;
    private int size;

    private ViewPager.OnPageChangeListener onPageChangeListener;

    public CBPageChangeListener(TextView textView, int size) {
        this.textView = textView;
        this.size = size;
        this.pointViews = null;
    }

    public CBPageChangeListener(ArrayList<ImageView> pointViews, int page_indicatorId[]) {
        this.pointViews = pointViews;
        this.page_indicatorId = page_indicatorId;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (onPageChangeListener != null)
            onPageChangeListener.onPageScrollStateChanged(state);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (onPageChangeListener != null)
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int index) {
        if (pointViews != null) {
            for (int i = 0; i < pointViews.size(); i++) {
                pointViews.get(index).setImageResource(page_indicatorId[1]);
                if (index != i) {
                    pointViews.get(i).setImageResource(page_indicatorId[0]);
                }
            }
        } else {
            textView.setText(index + 1 + "/" + size);
        }
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(index);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }
}
