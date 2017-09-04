package com.fqxyi.convenientbanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.drawable.ScalingUtils;
import com.fqxyi.library.ConvenientBanner;
import com.fqxyi.library.holder.CBViewHolderCreator;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ConvenientBanner convenientBanner;
    private String[] images = {
            "http://pic62.nipic.com/file/20150319/12632424_132215178296_2.jpg",
            "http://pic55.nipic.com/file/20141208/19462408_171130083000_2.jpg",
            "http://pic.58pic.com/58pic/16/66/85/47v58PICMYf_1024.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convenientBanner = (ConvenientBanner) findViewById(R.id.convenient_banner);

        // 显示图片
        final ImageHolderView imageHolderView = new ImageHolderView();
        imageHolderView.setScaleType(ScalingUtils.ScaleType.FIT_XY);
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return imageHolderView;
            }
        }, null);
        convenientBanner.setData(Arrays.asList(images));

    }

}
