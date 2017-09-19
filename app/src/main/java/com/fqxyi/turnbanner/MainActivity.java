package com.fqxyi.turnbanner;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fqxyi.library.TurnBanner;
import com.fqxyi.library.holder.HolderCreator;
import com.fqxyi.library.listener.PointChangeListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Context context;

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

        context = getApplicationContext();

        turnBanner = (TurnBanner) findViewById(R.id.banner);
        // 设置数据
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
        // 开关 无限循环
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
        // 开关 banner滚动
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

    /**
     * 指示点类型1 居中展示
     */
    public void pointerType1Center(View view) {
        turnBanner.setPointViewVisible(true);
        turnBanner.setPageIndicator(new int[]{
                R.drawable.banner_point_normal,
                R.drawable.banner_point_select});
        turnBanner.setPageIndicatorAlign(TurnBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
    }

    /**
     * 指示点类型自定义 居右展示
     */
    public void pointerTypeCustomRight(View view) {
        turnBanner.setPointViewVisible(true);
        turnBanner.setPageIndicator(R.layout.point_layout, new PointChangeListener() {
            @Override
            public void getInfo(View pointView, int position, int pointSize) {
                TextView pointTxt = (TextView) pointView.findViewById(R.id.point_txt);
                pointTxt.setText(position+1 + "/" + pointSize);
            }
        });
        turnBanner.setPageIndicatorAlign(TurnBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
    }

    /**
     * 指示点 隐藏
     */
    public void pointerHide(View view) {
        turnBanner.setPointViewVisible(false);
    }

    /**
     * 开始自动轮播
     */
    public void startAutoTurn(View view) {
        turnBanner.startTurn(2000);
    }

    /**
     * 暂停自动轮播
     */
    public void pauseAutoTurn(View view) {
        turnBanner.pauseTurn();
    }

    /**
     * 停止自动轮播
     */
    public void stopAutoTurn(View view) {
        turnBanner.stopTurn();
    }

    private void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 防止内存泄漏
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnBanner.destroy();
    }
}
