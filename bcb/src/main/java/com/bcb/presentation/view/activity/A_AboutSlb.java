package com.bcb.presentation.view.activity;

import android.os.Bundle;

import com.bcb.R;
import com.bcb.base.Activity_Base;

public class A_AboutSlb extends Activity_Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_about_slb);
        setLeftTitleVisible(true);
        setTitleValue("关于生利宝");
    }
}
