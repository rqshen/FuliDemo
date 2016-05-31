package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.transaction.MoneyItemDetailBean;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class Activity_Money_Item_Detail extends Activity_Base {

    private String BillId;
    private int TopCategoryId;
    private String Type;
    private String StatusDescn;
    //返回数据
    private MoneyItemDetailBean moneyItemDetailBean;
    //状态
    private TextView value_status;
    //金额(大)
    private TextView value_amount;

    //项目名称
    private LinearLayout layout_project;
    private TextView value_project;
    //订单号
    private LinearLayout layout_transno;
    private TextView value_transno;
    //提现券、回款利息或者保证金
    private LinearLayout layout_trans_amount;
    private TextView title_trans_amount;
    private TextView value_trans_amount;
    //服务费
    private LinearLayout layout_service;
    private TextView title_service;
    private TextView value_service;
    //订单时间
    private TextView value_time;

    private BcbRequestQueue requestQueue;

    public static void launche(Context ctx, String BillId, int TopCategoryId, String Type, String StatusDescn) {
        Intent intent = new Intent();
        intent.setClass(ctx, Activity_Money_Item_Detail.class);
        intent.putExtra("BillId", BillId);
        intent.putExtra("TopCategoryId", TopCategoryId);
        intent.putExtra("Type", Type);
        intent.putExtra("StatusDescn", StatusDescn);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BillId = getIntent().getStringExtra("BillId");
        TopCategoryId = getIntent().getIntExtra("TopCategoryId", -1);
        Type = getIntent().getStringExtra("Type");
        StatusDescn = getIntent().getStringExtra("StatusDescn");
        setBaseContentView(R.layout.activity_money_item_detail);
        //设置标题
        setLeftTitleVisible(true);
        setTitleValue("明细详情");
        requestQueue = BcbNetworkManager.newRequestQueue(this);
        //初始化界面
        initView();
        //加载数据
        loadData();
    }

    //初始化
    private void initView() {
        value_status = (TextView) findViewById(R.id.value_status);
        if (TextUtils.isEmpty(StatusDescn)) {
            value_status.setVisibility(View.GONE);
        } else {
            value_status.setVisibility(View.VISIBLE);
            value_status.setText(StatusDescn);
        }
        value_amount = (TextView) findViewById(R.id.value_amount);
        layout_project = (LinearLayout) findViewById(R.id.layout_project);
        value_project = (TextView) findViewById(R.id.value_project);
        layout_transno = (LinearLayout) findViewById(R.id.layout_transno);
        value_transno = (TextView) findViewById(R.id.value_transno);
        layout_trans_amount = (LinearLayout) findViewById(R.id.layout_trans_amount);
        title_trans_amount = (TextView) findViewById(R.id.title_trans_amount);
        value_trans_amount = (TextView) findViewById(R.id.value_trans_amount);
        layout_service = (LinearLayout) findViewById(R.id.layout_service);
        title_service = (TextView) findViewById(R.id.title_service);
        value_service = (TextView) findViewById(R.id.value_service);
        value_time = (TextView) findViewById(R.id.value_time);
        //回款时是有项目名称的
        if (TopCategoryId == 1015) {
            layout_project.setVisibility(View.VISIBLE);
        } else {
            layout_project.setVisibility(View.GONE);
        }
        //提现券、回款利息或者保证金费用，其余的时候隐藏
        layout_trans_amount.setVisibility(View.VISIBLE);
        if (TopCategoryId == 1010) {
            title_service.setText("提现手续费");
            title_trans_amount.setText("提现券");
        } else if (TopCategoryId == 1015) {
            title_service.setText("信息服务费");
            title_trans_amount.setText("回款利息");
        } else if (TopCategoryId == 1025) {
            title_service.setText("信息服务费");
            title_trans_amount.setText("保证金费用");
        } else {
            layout_trans_amount.setVisibility(View.GONE);
        }

    }

    //加载数据
    private void loadData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("BillId", BillId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MoneyItemDetail, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(PackageUtil.getRequestStatus(response, Activity_Money_Item_Detail.this)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        //判断JSON对象是否为空
                        if (obj != null) {
                            moneyItemDetailBean = App.mGson.fromJson(obj.toString(), MoneyItemDetailBean.class);
                        }
                        //设置界面信息
                        setupViewData();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.TranscationDetailTag);
        requestQueue.add(jsonRequest);
    }


    //设置界面信息
    private void setupViewData() {
        //设置金额
        if (Type.equals("支出")) {
            value_amount.setText("-" + String.format("%.2f", moneyItemDetailBean.getAmount()));
        } else {
            value_amount.setText("+" + String.format("%.2f", moneyItemDetailBean.getAmount()));
        }

        //如果存在订单号则显示，否则隐藏
        if (TextUtils.isEmpty(moneyItemDetailBean.getTransNo())) {
            layout_transno.setVisibility(View.GONE);
        } else {
            layout_transno.setVisibility(View.VISIBLE);
            value_transno.setText(moneyItemDetailBean.getTransNo());
        }

        //设置订单时间
        value_time.setText(moneyItemDetailBean.getTime());

        //操作成功时，判断是否是提现、回款或者借款，只有这三者操作成功才扣费
        layout_service.setVisibility(View.VISIBLE);
        switch (TopCategoryId) {
            //提现
            case 1010:
                setupViewWithWithdraw();
                break;

            //回款
            case 1015:
                setupViewWithRepay();
                break;

            //借款
            case 1025:
                setupViewWithDebt();
                break;

            //其他默认不显示信息服务费
            default:
                layout_service.setVisibility(View.GONE);
                break;
        }
    }

    //设置提现时的提现手续费、提现券费用
    private void setupViewWithWithdraw() {
        //提现手续费，如果大于0，则需要添加 “-”号
        if (moneyItemDetailBean.WithdrawExt.getProcedureFeeAmount() > 0) {
            value_service.setText(String.format("-%.2f", moneyItemDetailBean.WithdrawExt.getProcedureFeeAmount()));
        } else {
            value_service.setText(String.format("%.2f", moneyItemDetailBean.WithdrawExt.getProcedureFeeAmount()));
        }
        //判断返回来的CouponId不为空，则表示使用了提现券，由于旧数据可能没有提现手续费，为了收支平衡，则需要设置提现手续费为 -2.00元
        if (!TextUtils.isEmpty(moneyItemDetailBean.WithdrawExt.getCouponId())
                && ! moneyItemDetailBean.WithdrawExt.getCouponId().equalsIgnoreCase("null")) {
            value_trans_amount.setText("2.00");
            if (moneyItemDetailBean.WithdrawExt.getProcedureFeeAmount() <= 0) {
                value_service.setText("-2.00");
            }
        } else {
            value_trans_amount.setText("0.00");
        }
    }

    //设置回款显示项目名称、信息服务费、回款利息
    private void setupViewWithRepay() {
        //信息服务费
        if (moneyItemDetailBean.InvestorRepay.getServiceFeeAmount() > 0) {
            value_service.setText(String.format("-%.2f", moneyItemDetailBean.InvestorRepay.getServiceFeeAmount()));
        } else {
            value_service.setText(String.format("%.2f", moneyItemDetailBean.InvestorRepay.getServiceFeeAmount()));
        }
        //回款利息
        value_trans_amount.setText(String.format("+%.2f", moneyItemDetailBean.InvestorRepay.getInterest()));
        //项目名称
        value_project.setText(moneyItemDetailBean.InvestorRepay.getPackageName());
    }

    //设置借款显示信息服务费、保证金费用
    private void setupViewWithDebt() {
        //信息服务费
        if (moneyItemDetailBean.DebtExt.getServiceFeeAmount() > 0) {
            value_service.setText(String.format("-%.2f", moneyItemDetailBean.DebtExt.getServiceFeeAmount()));
        } else {
            value_service.setText(String.format("%.2f", moneyItemDetailBean.DebtExt.getServiceFeeAmount()));
        }
        //保证金费用
        if (moneyItemDetailBean.DebtExt.getSecurityDepositFeeAmount() > 0) {
            value_trans_amount.setText(String.format("-%.2f", moneyItemDetailBean.DebtExt.getSecurityDepositFeeAmount()));
        } else {
            value_trans_amount.setText(String.format("%.2f", moneyItemDetailBean.DebtExt.getSecurityDepositFeeAmount()));
        }
    }
}
