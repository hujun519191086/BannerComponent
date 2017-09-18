package com.fqxyi.library.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.fqxyi.library.holder.HolderCreator;
import com.fqxyi.library.holder.Holder;
import com.fqxyi.library.view.BannerViewPager;

import java.util.List;

public class BannerPageAdapter<T> extends PagerAdapter {

    private BannerViewPager viewPager;

    private HolderCreator holderCreator;

    protected List<T> data;

    private boolean canLoop = true;  // 是否支持无限循环

    private int childCount = 0;

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    @Override
    public int getCount() {
        int realCount = getRealCount();
        if (0 == realCount) return 0;
        if (canLoop) return Integer.MAX_VALUE;
        return realCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int getRealCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = getRealPosition(position);
        View view = getView(container, realPosition, null);
        container.addView(view);
        return view;
    }

    public int getRealPosition(int position) {
        int realCount = getRealCount();
        if (0 == realCount) return 0;
        if (canLoop) return position % realCount;
        return position;
    }

    private View getView(ViewGroup container, int position, View view) {
        Holder holder;
        if (view == null) {
            holder = (Holder) holderCreator.createHolder();
            view = holder.createView(container.getContext());
            view.setTag(this);
        } else {
            holder = (Holder) view.getTag();
        }
        if (data != null && !data.isEmpty() && position < data.size()) {
            holder.updateUI(container.getContext(), view, position, data.get(position));
        }
        return view;
    }

    public BannerPageAdapter(HolderCreator holderCreator, List<T> data) {
        this.holderCreator = holderCreator;
        this.data = data;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            position = viewPager.getFirstItem();
        } else if (position == getCount() - 1) {
            position = viewPager.getLastItem();
        }
        try {
            viewPager.setCurrentItem(position, false);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void setViewPager(BannerViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public int getItemPosition(Object object) {
        if (childCount > 0) {
            childCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        childCount = getCount();
        super.notifyDataSetChanged();
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

}
