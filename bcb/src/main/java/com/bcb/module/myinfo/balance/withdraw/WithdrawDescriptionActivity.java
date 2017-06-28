package com.bcb.module.myinfo.balance.withdraw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.BaseActivity;
import com.bcb.base.view.ToolbarView;

import butterknife.BindView;

import static com.bcb.constant.Constant.CUSTOMER_SERVICE_PHONE;

/**
 * Created by ruiqin.shen
 * 类说明：提现说明
 */
public class WithdrawDescriptionActivity extends BaseActivity {
    @BindView(R.id.toolbar_view)
    ToolbarView mToolbarView;
    @BindView(R.id.call_phone)
    TextView mCallPhone;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), WithdrawDescriptionActivity.class);
        return intent;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdraw_description;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initToolBar();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mCallPhone.setText(CUSTOMER_SERVICE_PHONE);
    }

    /**
     * 初始化标题栏
     */
    private void initToolBar() {
        Toolbar toolBar = mToolbarView.getToolBar();
        toolBar.setTitle("");
        setSupportActionBar(toolBar);

        //获取actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.r_back_3x);
        }
        mToolbarView.setToolBarTitle("提现说明");
    }

    /**
     * ToolBar的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected boolean enableSliding() {
        return false;
    }
}
