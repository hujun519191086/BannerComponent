package com.fqxyi.convenientbanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fqxyi.library.ConvenientBanner;
import com.fqxyi.library.holder.CBViewHolderCreator;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ConvenientBanner convenientBanner;
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

        convenientBanner = (ConvenientBanner) findViewById(R.id.convenient_banner);
    }

    public void simple(View view) {
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new ImageHolderView();
            }
        }, Arrays.asList(images));
    }

}
