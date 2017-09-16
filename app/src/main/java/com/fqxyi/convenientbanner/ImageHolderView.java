package com.fqxyi.convenientbanner;

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

    private int height;
    private int count;
    private int color;

    private ScaleType scaleType = ScaleType.FIT_CENTER;

    private ImageCLickListener listener;

    public interface ImageCLickListener {
        void click(View view, int position, String path);
    }

    private ImageLongCLickListener longListener;

    public interface ImageLongCLickListener {
        void longClick(View view, int position, String path);
    }

    public ImageHolderView(Context context, int height) {
        this.height = DensityUtil.dip2px(context, height);
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ImageHolderView() {
        this.height = LayoutParams.MATCH_PARENT;
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public void setListener(ImageCLickListener listener) {
        this.listener = listener;
    }

    public void setLongListener(ImageLongCLickListener longListener) {
        this.longListener = longListener;
    }

    /**
     * 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
     */
    @Override
    public View createView(Context context) {
        SimpleDraweeView imageView = new SimpleDraweeView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        imageView.setLayoutParams(params);
        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setActualImageScaleType(scaleType);

        if (color != 0) {
            imageView.setBackgroundColor(color);
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
                if (null != listener) {
                    listener.click(view, position, data);
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

    public void setBackground(int color) {
        this.color = color;
    }
}
