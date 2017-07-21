package com.bcb.module.discover.financialproduct.buy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.BaseActivity;
import com.bcb.base.view.ToolbarView;
import com.bcb.module.discover.financialproduct.InvestmentFinanceActivity;
import com.bcb.util.MyActivityManager;
import com.bcb.util.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectBuySuccessActivity extends BaseActivity {
    @BindView(R.id.toolbar_view)
    ToolbarView mToolbarView;
    private static String EXTRA_MESSAGE = "message";
    @BindView(R.id.bug_money)
    TextView mBugMoney;
    private String message;

    /**
     * 启动自身
     */
    public static Intent newIntent(Context context, String message) {
        Intent intent = new Intent(context.getApplicationContext(), ProjectBuySuccessActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_project_buy_success;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        MyActivityManager.getInstance().pushOneActivity(ProjectBuySuccessActivity.this);
        initToolBar();
        getIntentData();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mBugMoney.setText(message);
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
        mToolbarView.setToolBarTitle("支付成功");
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

    /**
     * 从Intent中获取数据
     *
     * @return
     */
    public void getIntentData() {
        Intent intent = getIntent();
        String tempMessage = intent.getStringExtra(EXTRA_MESSAGE);
        if (!StringUtils.isEmpty(tempMessage)) {
            message = tempMessage;
        }
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.more_financial_product, R.id.bug_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more_financial_product://更多产品
                startActivity(new Intent(mContext, InvestmentFinanceActivity.class));
                MyActivityManager.getInstance().finishAllActivity();
                break;
            case R.id.bug_complete://完成
                MyActivityManager.getInstance().finishAllActivity();
                break;
        }
    }
}
