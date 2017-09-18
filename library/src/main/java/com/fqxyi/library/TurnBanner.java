package com.fqxyi.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fqxyi.library.adapter.BannerPageAdapter;
import com.fqxyi.library.holder.HolderCreator;
import com.fqxyi.library.listener.OnItemClickListener;
import com.fqxyi.library.listener.CustomPageChangeListener;
import com.fqxyi.library.listener.PointChangeListener;
import com.fqxyi.library.util.DensityUtil;
import com.fqxyi.library.view.BannerViewPager;
import com.fqxyi.library.view.ViewPagerScroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面翻转控件
 */
public class TurnBanner<T> extends LinearLayout {

    // layout
    private BannerViewPager bannerViewPager;
    private ViewGroup pointerContainer;
    // attr
    private long turnTime; // 翻页时间
    private boolean canTurn = false; // 能否手动触发翻页
    private boolean canAutoTurn = false; // 能否自动触发翻页
    final int MSG_AUTO_TURN = 1; // 自动翻页的消息标识
    private boolean canLoop = true; // 是否支持无限循环
    // component
    private ViewPagerScroller scroller;
    private BannerPageAdapter<T> pageAdapter;
    private CustomPageChangeListener customPageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    // data
    private List<T> data;
    private List<ImageView> pointViews = new ArrayList<>();
    private int[] pointImgIds;
    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_AUTO_TURN) {
                removeMessages(MSG_AUTO_TURN);
                if (bannerViewPager != null && null != pageAdapter && canAutoTurn) {
                    int page = bannerViewPager.getCurrentItem() + 1;
                    if (page >= pageAdapter.getCount()) page = 0;
                    bannerViewPager.setCurrentItem(page);
                    if (canAutoTurn) sendEmptyMessageDelayed(MSG_AUTO_TURN, turnTime);
                }
            }
        }
    };

    public TurnBanner(Context context) {
        this(context, null);
        init(context, null);
    }

    public TurnBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TurnBanner);

            canLoop = typedArray.getBoolean(R.styleable.TurnBanner_canLoop, true);

            typedArray.recycle();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.view_banner, this, true);
        bannerViewPager = (BannerViewPager) view.findViewById(R.id.banner_view_pager);
        pointerContainer = (ViewGroup) view.findViewById(R.id.banner_point_container);
        // 初始化ViewPager的滑动速度
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(getContext());
            mScroller.set(bannerViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置ViewPager的滚动速度
     */
    public TurnBanner setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
        return this;
    }

    /**
     * 初始化页面 但 不初始化数据
     */
    public TurnBanner setPages(HolderCreator holderCreator) {
        setPages(holderCreator, null);
        return this;
    }

    /**
     * 初始化页面 并且 初始化数据
     */
    public TurnBanner setPages(HolderCreator holderCreator, List<T> data) {
        this.data = data;
        pageAdapter = new BannerPageAdapter<>(holderCreator, data);
        bannerViewPager.setAdapter(pageAdapter, canLoop);
        return this;
    }

    /**
     * 仅初始化数据
     */
    public TurnBanner setData(List<T> data) {
        this.data = data;
        if (null == pageAdapter) return this;
        pageAdapter.setData(data);
        if (pointImgIds != null) setPageIndicator(pointImgIds);
        return this;
    }

    public int getDataSize() {
        return null == data ? 0 : data.size();
    }

    public List<T> getData() {
        return data;
    }

    /***
     * 开始翻页
     * @param turnTime 翻页时间
     */
    public TurnBanner startTurn(long turnTime) {
        stopTurn();
        this.turnTime = turnTime;
        // 设置可以翻页并开启翻页
        canTurn = true;
        canAutoTurn = true;
        handler.sendEmptyMessageDelayed(MSG_AUTO_TURN, turnTime);
        return this;
    }

    public void pauseTurn() {
        canAutoTurn = false;
        handler.removeMessages(MSG_AUTO_TURN);
    }

    public void stopTurn() {
        canAutoTurn = false;
        canTurn = false;
        handler.removeMessages(MSG_AUTO_TURN);
    }

    /***
     * 是否开启了翻页
     */
    public boolean isCanAutoTurn() {
        return canAutoTurn;
    }

    /**
     * 设置可以循环播放
     */
    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        bannerViewPager.setCanLoop(canLoop);
    }

    public boolean isCanLoop() {
        return bannerViewPager.isCanLoop();
    }

    /**
     * 设置可以滚动
     */
    public void setCanScroll(boolean isCanScroll) {
        bannerViewPager.setCanScroll(isCanScroll);
    }

    public boolean isCanScroll() {
        return bannerViewPager.isCanScroll();
    }

    /**
     * 触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL ||
                action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (canTurn) {
                startTurn(turnTime);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 暂停翻页
            if (canTurn) {
                pauseTurn();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置当前的页面index
     *
     * @param index
     */
    public void setcurrentitem(int index) {
        if (bannerViewPager != null) {
            bannerViewPager.setCurrentItem(index);
        }
    }

    /**
     * 获取当前的页面index
     */
    public int getCurrentItem() {
        if (bannerViewPager != null) {
            return bannerViewPager.getRealItem();
        }
        return -1;
    }

    /**
     * 自定义指示器样式：. . . . .
     *
     * pointImgIds大小只能为2
     */
    public TurnBanner setPageIndicator(int[] pointImgIds) {
        if (null == pointImgIds || pointImgIds.length != 2) {
            throw new RuntimeException("pointImgIds大小只能为2");
        }
        setPageIndicator(pointImgIds, DensityUtil.dip2px(getContext(), 10), DensityUtil.dip2px(getContext(), 10));
        return this;
    }

    /**
     * 自定义指示器样式：. . . . .
     *
     * pointImgIds大小只能为2
     */
    public TurnBanner setPageIndicator(int[] pointImgIds, int right, int bottom) {
        if (null == pointImgIds || pointImgIds.length != 2) {
            throw new RuntimeException("pointImgIds大小只能为2");
        }
        if (null == data) return this;
        // clear view
        pointerContainer.removeAllViews();
        // clear data
        pointViews.clear();
        // get view
        this.pointImgIds = pointImgIds;
        for (int count = 0; count < data.size(); count++) {
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(0, 0, 10, 10);
            if (pointViews.isEmpty()) {
                pointView.setImageResource(pointImgIds[1]);
            } else {
                pointView.setImageResource(pointImgIds[0]);
            }
            pointViews.add(pointView);
            pointerContainer.addView(pointView);
        }
        if (null == customPageChangeListener) {
            customPageChangeListener = new CustomPageChangeListener(pointViews, pointImgIds);
        } else {
            customPageChangeListener.setPointImgData(pointViews, pointImgIds);
        }
        bannerViewPager.setOnPageChangeListener(customPageChangeListener);
        if (null != onPageChangeListener) customPageChangeListener.setPageChangeListener(onPageChangeListener);
        return this;
    }

    /**
     * 完全自定义的指示点布局
     * @param layoutId 外部传入的指示点布局
     */
    public TurnBanner setPageIndicator(@LayoutRes int layoutId, PointChangeListener pointChangeListener) {
        if (null == data || null == pointChangeListener) return this;
        // clear view
        pointerContainer.removeAllViews();
        // clear data
        pointViews.clear();
        pointImgIds = null;
        // add view
        View view = LayoutInflater.from(getContext()).inflate(layoutId, null);
        pointerContainer.addView(view);
        // other
        if (null == customPageChangeListener) {
            customPageChangeListener = new CustomPageChangeListener(view, data.size(), pointChangeListener);
        } else {
            customPageChangeListener.setPointChangeListener(view, data.size(), pointChangeListener);
        }
        bannerViewPager.setOnPageChangeListener(customPageChangeListener);
        if (null != onPageChangeListener) customPageChangeListener.setPageChangeListener(onPageChangeListener);
        return this;
    }

    /**
     * 设置底部指示器是否可见
     */
    public TurnBanner setPointViewVisible(boolean visible) {
        pointerContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 指示器的方向
     *
     * @param align 三个方向：
     *              居左 （RelativeLayout.ALIGN_PARENT_LEFT），
     *              居中 （RelativeLayout.CENTER_HORIZONTAL），
     *              居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     */
    public TurnBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pointerContainer.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        pointerContainer.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 自定义翻页动画效果
     */
    public TurnBanner setPageTransformer(ViewPager.PageTransformer transformer) {
        bannerViewPager.setPageTransformer(true, transformer);
        return this;
    }

    public BannerViewPager getBannerViewPager() {
        return bannerViewPager;
    }

    /**
     * 设置翻页监听器
     */
    public TurnBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        // 如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if (customPageChangeListener != null) {
            customPageChangeListener.setPageChangeListener(onPageChangeListener);
        } else {
            bannerViewPager.setOnPageChangeListener(onPageChangeListener);
        }
        return this;
    }

    /**
     * 监听item点击
     */
    public TurnBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        bannerViewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }

    public void destroy() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
    }

}
