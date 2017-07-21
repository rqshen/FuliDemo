package com.bcb.module.discover.financialproduct.buy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.BaseActivity;
import com.bcb.base.view.ToolbarView;
import com.bcb.util.MQCustomerManager;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectBuyFailActivity extends BaseActivity {
    @BindView(R.id.toolbar_view)
    ToolbarView mToolbarView;
    private static String EXTRA_DESC = "desc";
    @BindView(R.id.tv_desc)
    TextView mTvDesc;

    /**
     * 启动自身
     */
    public static Intent newIntent(Context context, String desc) {
        Intent intent = new Intent(context.getApplicationContext(), ProjectBuyFailActivity.class);
        intent.putExtra(EXTRA_DESC, desc);
        return intent;
    }


    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_project_buy_fail;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initToolBar();
        getIntentData();
    }

    /**
     * 从Intent中获取数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String tempDesc = intent.getStringExtra(EXTRA_DESC);
            if (tempDesc != null) {
                mTvDesc.setText(tempDesc);
            }
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
        mToolbarView.setToolBarTitle("支付失败");
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

    @OnClick({R.id.buy_again, R.id.contact_customer_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buy_again:
                finish();
                break;
            case R.id.contact_customer_service:
                String userId = null;
                //判断是否为空
                if (MyApplication.mUserDetailInfo != null) {
                    userId = MyApplication.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(mContext).showCustomer(userId);
                break;
        }
    }
}
