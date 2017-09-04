package com.fqxyi.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fqxyi.library.adapter.CBPageAdapter;
import com.fqxyi.library.holder.CBViewHolderCreator;
import com.fqxyi.library.listener.CBPageChangeListener;
import com.fqxyi.library.listener.OnItemClickListener;
import com.fqxyi.library.util.DensityUtil;
import com.fqxyi.library.view.CBLoopViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面翻转控件，极方便的广告栏
 * 支持无限循环，自动翻页，翻页特效
 */
public class ConvenientBanner<T> extends LinearLayout {
    private List<T> data;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private CBPageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    @SuppressWarnings("rawtypes")
    private CBPageAdapter pageAdapter;
    private CBLoopViewPager viewPager;
    private ViewPagerScroller scroller;
    private ViewGroup loPageTurningPoint;
    private long autoTurningTime;
    private boolean autoTurning;
    private boolean canTurn = false;
    private boolean canLoop = true;

    float cbMarginLeft, cbMarginTop, cbMarginRight, cbMarginBottom;
    float cbPointMarginLeft, cbPointMarginTop, cbPointMarginRight, cbPointMarginBottom;

    boolean rightIndicator;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    private int pageIndicatorBottom;

    final int MSG_TURNING = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_TURNING) {
                removeMessages(MSG_TURNING);
                if (viewPager != null && null != pageAdapter && autoTurning) {
                    int page = viewPager.getCurrentItem() + 1;
                    if (page >= pageAdapter.getCount()) {
                        page = 0;
                    }
                    if (page >= pageAdapter.getCount()) {
                        return;
                    }
                    viewPager.setCurrentItem(page);
                    if (autoTurning) {
                        sendEmptyMessageDelayed(MSG_TURNING, autoTurningTime);
                    }
                }
            }
        }
    };

    public ConvenientBanner(Context context, boolean canLoop) {
        this(context, null);
        init(context, null);
    }

    public ConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ConvenientBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);

            canLoop = typedArray.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
            cbMarginLeft = typedArray.getDimension(R.styleable.ConvenientBanner_cb_margin_left, 0);
            cbMarginTop = typedArray.getDimension(R.styleable.ConvenientBanner_cb_margin_top, 0);
            cbMarginRight = typedArray.getDimension(R.styleable.ConvenientBanner_cb_margin_right, 0);
            cbMarginBottom = typedArray.getDimension(R.styleable.ConvenientBanner_cb_margin_bottom, 0);
            cbPointMarginLeft = typedArray.getDimension(R.styleable.ConvenientBanner_cb_point_margin_left, 10);
            cbPointMarginTop = typedArray.getDimension(R.styleable.ConvenientBanner_cb_point_margin_top, 10);
            cbPointMarginRight = typedArray.getDimension(R.styleable.ConvenientBanner_cb_point_margin_right, 10);
            cbPointMarginBottom = typedArray.getDimension(R.styleable.ConvenientBanner_cb_point_margin_bottom, 10);

            typedArray.recycle();
        }

        pageIndicatorBottom = DensityUtil.dip2px(context, 10);
        View hView = LayoutInflater.from(context).inflate(R.layout.include_viewpager, this, true);
        viewPager = (CBLoopViewPager) hView.findViewById(R.id.cbLoopViewPager);
        loPageTurningPoint = (ViewGroup) hView.findViewById(R.id.loPageTurningPoint);
        initViewPagerScroll();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
        params.setMargins((int) cbMarginLeft, (int) cbMarginTop, (int) cbMarginRight, (int) cbMarginBottom);
        viewPager.setLayoutParams(params);

        RelativeLayout.LayoutParams pointParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        pointParams.setMargins((int) cbPointMarginLeft, (int) cbPointMarginTop, (int) cbPointMarginRight, (int) cbPointMarginBottom);
        loPageTurningPoint.setLayoutParams(pointParams);

    }

    public void setRightIndicator(boolean indicator) {
        rightIndicator = indicator;
    }

    /**
     * 设置数据
     *
     * @param holderCreator
     * @param data
     * @return
     */
    public ConvenientBanner setPages(CBViewHolderCreator holderCreator, List<T> data) {
        this.data = data;
        pageAdapter = new CBPageAdapter(holderCreator, this.data);
        pageAdapter.setRightIndicator(rightIndicator);
        viewPager.setAdapter(pageAdapter, canLoop);

        if (page_indicatorId != null) {
            setPageIndicator(page_indicatorId);
        }
        return this;
    }

    /**
     * 设置数据
     *
     * @param data
     * @return
     */
    public ConvenientBanner setData(List<T> data) {
        this.data = data;
        if (null == pageAdapter) {
            return this;
        }
        pageAdapter.setData(data);
        pageAdapter.notifyDataSetChanged();
        return this;
    }

    public int getCount() {
        if (null == data) {
            return 0;
        }
        return data.size();
    }

    public List<T> getData() {
        return data;
    }

    /**
     * 通知数据变化
     * 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (page_indicatorId != null) {
            setPageIndicator(page_indicatorId);
        }
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public ConvenientBanner setPointViewVisible(boolean visible) {
        loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置默认的指示器样式：1/5 2/5 3/5 4/5 5/5
     */
    public ConvenientBanner setPageIndicator(int layoutId) {
        loPageTurningPoint.removeAllViews();
        if (data == null) {
            return this;
        }
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(layoutId, null);
        loPageTurningPoint.addView(textView);
        pageChangeListener = new CBPageChangeListener(textView, data.size());
        viewPager.setOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(viewPager.getRealItem());
        if (onPageChangeListener != null) {
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        }
        return this;
    }

    /**
     * 设置默认的指示器样式：1/5 2/5 3/5 4/5 5/5
     */
    public ConvenientBanner setPageIndicator() {
        return setPageIndicator(R.layout.page_indicator);
    }

    /**
     * 自定义指示器样式：. . . . .
     */
    public ConvenientBanner setPageIndicator(int[] page_indicatorId) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if (data == null) {
            return this;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.bottomMargin = pageIndicatorBottom;

        for (int count = 0; count < data.size(); count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty()) {
                pointView.setImageResource(page_indicatorId[1]);
            } else {
                pointView.setImageResource(page_indicatorId[0]);
            }
            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
        pageChangeListener = new CBPageChangeListener(mPointViews, page_indicatorId);
        viewPager.setOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(viewPager.getRealItem());
        if (onPageChangeListener != null) {
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        }
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
    public ConvenientBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }

    /***
     * 是否开启了翻页
     * @return
     */
    public boolean isAutoTurning() {
        return autoTurning;
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public ConvenientBanner startTurning(long autoTurningTime) {
        stopTurning();

        //设置可以翻页并开启翻页
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        autoTurning = true;
        handler.sendEmptyMessageDelayed(MSG_TURNING, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        autoTurning = false;
        handler.removeMessages(MSG_TURNING);
    }

    public void destoryHandler() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public ConvenientBanner setPageTransformer(PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(getContext());
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isManualPageable() {
        return viewPager.isCanScroll();
    }

    /**
     * 设置可以滚动
     *
     * @param manualPageable
     */
    public void setManualPageable(boolean manualPageable) {
        viewPager.setCanScroll(manualPageable);
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL ||
                action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (canTurn) {
                startTurning(autoTurningTime);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            if (canTurn) {
                stopTurning();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取当前的页面index
     */
    public int getCurrentItem() {
        if (viewPager != null) {
            return viewPager.getRealItem();
        }
        return -1;
    }

    /**
     * 设置当前的页面index
     *
     * @param index
     */
    public void setcurrentitem(int index) {
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
        }
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 设置翻页监听器
     *
     * @param onPageChangeListener
     * @return
     */
    public ConvenientBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if (pageChangeListener != null) {
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        } else {
            viewPager.setOnPageChangeListener(onPageChangeListener);
        }
        return this;
    }

    public boolean isCanLoop() {
        return viewPager.isCanLoop();
    }

    /**
     * 监听item点击
     *
     * @param onItemClickListener
     */
    public ConvenientBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            viewPager.setOnItemClickListener(null);
            return this;
        }
        viewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    public CBLoopViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 设置可以循环播放
     *
     * @param canLoop
     */
    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        viewPager.setCanLoop(canLoop);
    }

    /**
     * 设置指示器位置
     *
     * @param bottom
     */
    public void setPageIndicatorBottom(int bottom) {
        pageIndicatorBottom = bottom;
    }
}
