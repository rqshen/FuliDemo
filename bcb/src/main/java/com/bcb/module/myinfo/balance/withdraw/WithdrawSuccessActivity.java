package com.bcb.module.myinfo.balance.withdraw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.BaseActivity;
import com.bcb.base.view.ToolbarView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ruiqin.shen
 * 类说明：提现成功
 */
public class WithdrawSuccessActivity extends BaseActivity {
    @BindView(R.id.toolbar_view)
    ToolbarView mToolbarView;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.tv_bank_name)
    TextView mTvBankName;
    @BindView(R.id.tv_bank_num)
    TextView mTvBankNum;
    private static String EXTRA_MESSAGE = "message";

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    public static Intent newIntent(Context context, String message) {
        Intent intent = new Intent(context.getApplicationContext(), WithdrawSuccessActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdraw_success;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initToolBar();
        getIntentData();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (MyApplication.mUserDetailInfo.getBankCard() != null) {
            mTvBankName.setText(MyApplication.mUserDetailInfo.getBankCard().getBankName());
            String cardNumber = MyApplication.mUserDetailInfo.getBankCard().getCardNumber();
            mTvBankNum.setText(cardNumber.substring(cardNumber.length() - 4));
        }
    }

    /**
     * 从Intent中获取数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        String tempMessage = intent.getStringExtra(EXTRA_MESSAGE);
        if (tempMessage != null) {
            mTvMoney.setText(tempMessage);
        }
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
        mToolbarView.setToolBarTitle("提现");
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
        }
        return true;
    }

    @OnClick(R.id.back_balance)
    public void onClick() {
        finish();
    }
}
