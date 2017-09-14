package com.fqxyi.convenientbanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fqxyi.library.ConvenientBanner;
import com.fqxyi.library.holder.CBViewHolderCreator;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ConvenientBanner banner;

    private Button loopBtn;
    private Button scrollBtn;

    private String[] images = {
            "http://img2.3lian.com/2014/f4/25/d/85.jpg",
            "http://img2.3lian.com/2014/f4/25/d/82.jpg",
            "http://img2.3lian.com/2014/f4/25/d/88.jpg",
            "http://img2.3lian.com/2014/f4/25/d/90.jpg",
            "http://img2.3lian.com/2014/f4/25/d/89.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        banner = (ConvenientBanner) findViewById(R.id.banner);
        banner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new ImageHolderView();
            }
        }, Arrays.asList(images));

        loopBtn = (Button) findViewById(R.id.loop_btn);
        scrollBtn = (Button) findViewById(R.id.scroll_btn);

        initEvent();
    }

    private void initEvent() {
        loopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banner.isCanLoop()) {
                    banner.setCanLoop(false);
                    loopBtn.setText("开启无限循环");
                } else {
                    banner.setCanLoop(true);
                    loopBtn.setText("关闭无限循环");
                }
            }
        });
        scrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banner.isCanScroll()) {
                    banner.setCanScroll(false);
                    scrollBtn.setText("开启滚动");
                } else {
                    banner.setCanScroll(true);
                    scrollBtn.setText("关闭滚动");
                }
            }
        });
    }

    public void pointerType1Left(View view) {
        banner.setPointViewVisible(true);
        banner.setPageIndicator();
        banner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_LEFT);
    }

    public void pointerType2Center(View view) {
        banner.setPointViewVisible(true);
        banner.setPageIndicator(new int[]{
                R.drawable.banner_point_normal,
                R.drawable.banner_point_select});
        banner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
    }

    public void pointerType1Right(View view) {
        banner.setPointViewVisible(true);
        banner.setPageIndicator();
        banner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
    }

    public void pointerHide(View view) {
        banner.setPointViewVisible(false);
    }

    public void startAutoTurn(View view) {
        banner.startTurning(2000);
    }

    public void pauseAutoTurn(View view) {
        banner.pauseTurning();
    }

    public void stopAutoTurn(View view) {
        banner.stopTurning();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        banner.destroy();
    }
}
