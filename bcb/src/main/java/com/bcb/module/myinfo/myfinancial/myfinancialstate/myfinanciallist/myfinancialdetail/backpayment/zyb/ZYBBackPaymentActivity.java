package com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.backpayment.zyb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.BaseActivity;
import com.bcb.base.view.ToolbarView;
import com.bcb.data.bean.Project_Investment_Details_Bean;
import com.bcb.data.bean.ZYBBackPaymentBean;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.backpayment.zyb.adapter.ZYBBackPaymentAdapter;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsOne;
import com.bcb.util.DoubleFormatUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * Created by ruiqin.shen
 * 类说明：周盈宝回款计划
 */
public class ZYBBackPaymentActivity extends BaseActivity {

    @BindView(R.id.toolbar_view)
    ToolbarView mToolbarView;
    private static String EXTRA_DATA = "data";
    private static String EXTRA_ORDERNO = "orderNo";

    Project_Investment_Details_Bean bean;
    @BindView(R.id.donePrincipalInterest)
    TextView mDonePrincipalInterest;
    @BindView(R.id.prePrincipalInterest)
    TextView mPrePrincipalInterest;
    @BindView(R.id.repaymentAllPeriod)
    TextView mRepaymentAllPeriod;
    @BindView(R.id.repaymentHadPeriod)
    TextView mRepaymentHadPeriod;
    @BindView(R.id.zyb_backpayment_recyclerview)
    RecyclerView mZybBackpaymentRecyclerview;
    private String mOrderNo;

    public static Intent newIntent(Context context, Project_Investment_Details_Bean bean, String orderNo) {
        Intent intent = new Intent(context.getApplicationContext(), ZYBBackPaymentActivity.class);
        intent.putExtra(EXTRA_DATA, bean);
        intent.putExtra(EXTRA_ORDERNO, orderNo);
        return intent;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zybback_payment;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        initToolBar();
        getIntentData();
        initData();//初始化数据
        loadZYBBackPaymentData();
    }

    private ZYBBackPaymentBean mZYBBackPaymentBean;

    /**
     * 请求周盈宝的
     */
    private void loadZYBBackPaymentData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("OrderNo", mOrderNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = UrlsOne.ZYB_BackPayment_List;
        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.e("TAG", "【投资回款计划】" + response.toString());
                try {
                    boolean flag = PackageUtil.getRequestStatus(response, mContext);
                    if (flag) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        if (obj != null) {
                            mZYBBackPaymentBean = MyApplication.mGson.fromJson(obj.toString(), ZYBBackPaymentBean.class);
                            mDonePrincipalInterest.setText(DoubleFormatUtils.format(mZYBBackPaymentBean.getDonePrincipalInterest()));
                            mPrePrincipalInterest.setText(DoubleFormatUtils.format(mZYBBackPaymentBean.getPrePrincipalInterest()));
                        }
                        setZYBBackPaymentAdapter();
                    }
                } catch (Exception e) {
                    LogUtil.d("TAG", "" + e.getMessage());
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);

    }

    ZYBBackPaymentAdapter mZYBBackPaymentAdapter;

    /**
     * 设置适配器
     */
    private void setZYBBackPaymentAdapter() {
        if (mZYBBackPaymentAdapter == null) {
            mZYBBackPaymentAdapter = new ZYBBackPaymentAdapter(mZYBBackPaymentBean.getRepaymentPlan());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            mZybBackpaymentRecyclerview.setLayoutManager(linearLayoutManager);
            mZybBackpaymentRecyclerview.setAdapter(mZYBBackPaymentAdapter);
        } else {
            mZYBBackPaymentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mRepaymentAllPeriod.setText(bean.RepaymentAllPeriod);
        mRepaymentHadPeriod.setText(bean.RepaymentHadPeriod);
    }


    /**
     * 从Intent中获取数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            bean = (Project_Investment_Details_Bean) intent.getSerializableExtra(EXTRA_DATA);
            mOrderNo = intent.getStringExtra(EXTRA_ORDERNO);
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
            actionBar.setHomeAsUpIndicator(R.drawable.return_delault);
            toolBar.setBackground(ContextCompat.getDrawable(mContext, R.color.red));
        }
        mToolbarView.setToolBarTitle("回款记录");
        mToolbarView.setToolBarTitleColor(mContext, R.color.white);
    }

    /**
     * ToolBar的点击事件
     *
     * @param item
     * @return
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
}
