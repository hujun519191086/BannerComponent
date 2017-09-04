package com.fqxyi.library.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.fqxyi.library.R;
import com.fqxyi.library.holder.CBViewHolderCreator;
import com.fqxyi.library.holder.Holder;
import com.fqxyi.library.view.CBLoopViewPager;

import java.util.List;

public class CBPageAdapter<T> extends PagerAdapter {

    protected List<T> data;
    protected CBViewHolderCreator holderCreator;
    private boolean canLoop = true;
    private CBLoopViewPager viewPager;
    private final int MULTIPLE_COUNT = 300;

    private boolean rightIndicator;

    private int childCount = 0;

    public int toRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        if (canLoop) {
            return position % realCount;
        }
        return position;
    }

    @Override
    public int getCount() {
        int count = getRealCount();
        if (0 == count) {
            return 0;
        }
        if (canLoop) {
            return count * MULTIPLE_COUNT;
        } else {
            if (rightIndicator) {
                return count + 1;
            }
            return count;
        }
    }

    public int getRealCount() {
        return data == null ? 0 : data.size();
    }

    public void setRightIndicator(boolean indicator) {
        rightIndicator = indicator;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);

        View view = getView(realPosition, null, container);
        container.addView(view);
        return view;
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
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    public void setViewPager(CBLoopViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public CBPageAdapter(CBViewHolderCreator holderCreator, List<T> data) {
        this.holderCreator = holderCreator;
        this.data = data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public View getView(int position, View view, ViewGroup container) {
        Holder holder = null;
        if (view == null) {
            holder = (Holder) holderCreator.createHolder();
            if (position == getRealCount()) {
                view = holder.createView(container.getContext(), rightIndicator);
            } else {
                view = holder.createView(container.getContext(), false);
            }
            view.setTag(R.id.cb_item_tag, holder);
        } else {
            holder = (Holder<T>) view.getTag(R.id.cb_item_tag);
        }
        if (data != null && !data.isEmpty()) {
            if (position < data.size()) {
                holder.updateUI(container.getContext(), view, position, data.get(position));
            }
        }
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        childCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (childCount > 0) {
            childCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
