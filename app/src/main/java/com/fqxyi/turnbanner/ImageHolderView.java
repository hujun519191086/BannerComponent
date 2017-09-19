package com.fqxyi.turnbanner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fqxyi.library.holder.Holder;
import com.fqxyi.library.util.DensityUtil;

/**
 * 本地、网络图片加载
 * Created by fengqingxiuyi on 17/9/1.
 */

public class ImageHolderView implements Holder<String> {

    private int bannerHeight;
    private int bannerBgColor;

    private ScaleType scaleType = ScaleType.FIT_CENTER;

    // 点击事件
    private ImageClickListener clickListener;
    interface ImageClickListener {
        void click(View view, int position, String path);
    }
    public void setClickListener(ImageClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // 长按事件
    private ImageLongClickListener longListener;
    interface ImageLongClickListener {
        void longClick(View view, int position, String path);
    }
    public void setLongClickListener(ImageLongClickListener longListener) {
        this.longListener = longListener;
    }

    public ImageHolderView() {
        this.bannerHeight = LayoutParams.MATCH_PARENT;
    }

    /**
     * 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
     */
    @Override
    public View createView(Context context) {
        SimpleDraweeView imageView = new SimpleDraweeView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, bannerHeight);
        imageView.setLayoutParams(params);
        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setActualImageScaleType(scaleType);

        if (bannerBgColor != 0) {
            imageView.setBackgroundColor(bannerBgColor);
        }
        return imageView;
    }

    @Override
    public void updateUI(Context context, final View view, final int position, final String data) {
        if (view instanceof SimpleDraweeView) {
            ((SimpleDraweeView)view).setImageURI(data);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clickListener) {
                    clickListener.click(view, position, data);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != longListener) {
                    longListener.longClick(view, position, data);
                }
                return false;
            }
        });
    }

    public void setBannerHeight(Context context, int bannerHeight) {
        this.bannerHeight = DensityUtil.dip2px(context, bannerHeight);
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public void setBannerBgColor(int color) {
        this.bannerBgColor = color;
    }
}
