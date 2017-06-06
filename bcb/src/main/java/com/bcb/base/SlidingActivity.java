package com.bcb.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bcb.base.view.SlidingLayout;


/**
 * Created by ruiqin.shen
 * 类说明：
 */
public class SlidingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (enableSliding()) {
            SlidingLayout rootView = new SlidingLayout(this);
            rootView.bindActivity(this);
        }
    }

    protected boolean enableSliding() {
        return true;
    }
}
