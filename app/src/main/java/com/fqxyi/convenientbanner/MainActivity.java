package com.fqxyi.convenientbanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fqxyi.library.TurnBanner;
import com.fqxyi.library.holder.HolderCreator;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TurnBanner turnBanner;

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

        turnBanner = (TurnBanner) findViewById(R.id.banner);
        turnBanner.setPages(new HolderCreator() {
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
                if (turnBanner.isCanLoop()) {
                    turnBanner.setCanLoop(false);
                    loopBtn.setText("开启无限循环");
                } else {
                    turnBanner.setCanLoop(true);
                    loopBtn.setText("关闭无限循环");
                }
            }
        });
        scrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (turnBanner.isCanScroll()) {
                    turnBanner.setCanScroll(false);
                    scrollBtn.setText("开启滚动");
                } else {
                    turnBanner.setCanScroll(true);
                    scrollBtn.setText("关闭滚动");
                }
            }
        });
    }

    public void pointerType1Left(View view) {
        turnBanner.setPointViewVisible(true);
        turnBanner.setPageIndicator(new int[]{
                R.drawable.banner_point_normal,
                R.drawable.banner_point_select});
        turnBanner.setPageIndicatorAlign(TurnBanner.PageIndicatorAlign.ALIGN_PARENT_LEFT);
    }

    public void pointerType2Center(View view) {
        turnBanner.setPointViewVisible(true);
        turnBanner.setPageIndicator(new int[]{
                R.drawable.banner_point_normal,
                R.drawable.banner_point_select});
        turnBanner.setPageIndicatorAlign(TurnBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
    }

    public void pointerType1Right(View view) {
        turnBanner.setPointViewVisible(true);
        turnBanner.setPageIndicator(new int[]{
                R.drawable.banner_point_normal,
                R.drawable.banner_point_select});
        turnBanner.setPageIndicatorAlign(TurnBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
    }

    public void pointerHide(View view) {
        turnBanner.setPointViewVisible(false);
    }

    public void startAutoTurn(View view) {
        turnBanner.startTurn(2000);
    }

    public void pauseAutoTurn(View view) {
        turnBanner.pauseTurn();
    }

    public void stopAutoTurn(View view) {
        turnBanner.stopTurn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnBanner.destroy();
    }
}
