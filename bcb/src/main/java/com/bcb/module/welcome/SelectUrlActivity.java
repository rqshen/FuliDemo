package com.bcb.module.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bcb.R;
import com.bcb.base.BaseActivity;
import com.bcb.constant.MyConstants;

import butterknife.OnClick;

/**
 * 选择网路页面，测试的时候使用
 */
public class SelectUrlActivity extends BaseActivity {

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_url;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

    }

    @OnClick({R.id.Location, R.id.TT, R.id.Release})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Location:
                MyConstants.TESTMODE = 0;
                break;
            case R.id.TT:
                MyConstants.TESTMODE = 1;
                break;
            case R.id.Release:
                MyConstants.TESTMODE = 2;
                break;
        }
        startActivity(new Intent(mContext, WelcomeActivity.class));
        finish();
    }
}
