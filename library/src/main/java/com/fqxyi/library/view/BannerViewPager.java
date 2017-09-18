package com.fqxyi.library.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.fqxyi.library.adapter.BannerPageAdapter;
import com.fqxyi.library.listener.OnItemClickListener;

public class BannerViewPager extends ViewPager {

    private BannerPageAdapter adapter;
    private OnPageChangeListener listener;

    private boolean isCanScroll = true; // 能否滑动视图
    private boolean canLoop = true; // 是否支持无限循环

    private OnItemClickListener onItemClickListener;

    public BannerViewPager(Context context) {
        super(context);
        init();
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.setOnPageChangeListener(new OnPageChangeListener() {
            private float previousPosition = -1;

            @Override
            public void onPageSelected(int position) {
                int realPosition = adapter.getRealPosition(position);
                if (previousPosition != realPosition) {
                    previousPosition = realPosition;
                    if (listener != null) {
                        listener.onPageSelected(realPosition);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (listener != null) {
                    if (position != adapter.getRealCount() - 1) {
                        listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    } else {
                        if (positionOffset > .5) {
                            listener.onPageScrolled(0, 0, 0);
                        } else {
                            listener.onPageScrolled(position, 0, 0);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (listener != null) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.listener = listener;
    }

    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    public boolean isCanScroll() {
        return isCanScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private float oldX = 0, newX = 0;
    private static final float sens = 5;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            if (onItemClickListener != null) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = ev.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        newX = ev.getX();
                        if (Math.abs(oldX - newX) < sens) {
                            onItemClickListener.onItemClick((getRealItem()));
                        }
                        oldX = 0;
                        newX = 0;
                        break;
                }
            }
            try {
                return super.onTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setAdapter(PagerAdapter adapter, boolean canLoop) {
        this.adapter = (BannerPageAdapter) adapter;
        this.adapter.setCanLoop(canLoop);
        this.adapter.setViewPager(this);
        super.setAdapter(this.adapter);

        setCurrentItem(getFirstItem(), false);
    }

    public BannerPageAdapter getAdapter() {
        return adapter;
    }

    public int getFirstItem() {
        return canLoop ? adapter.getRealCount() : 0;
    }

    public int getLastItem() {
        return adapter.getRealCount() - 1;
    }

    public int getRealItem() {
        return adapter != null ? adapter.getRealPosition(super.getCurrentItem()) : 0;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        if (!canLoop) {
            setCurrentItem(getRealItem(), false);
        }
        if (adapter == null)
            return;
        adapter.setCanLoop(canLoop);
        adapter.notifyDataSetChanged();
    }

    public boolean isCanLoop() {
        return canLoop;
    }

}
