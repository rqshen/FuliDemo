package com.bcb.module.myinfo.balance.trading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.data.bean.transaction.MoneyItemDetailBean;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.utils.LogUtil;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ruiqin.shen
 * 类说明：收支明细 详情
 */
public class TradingRecordDetailActivity extends Activity_Base {

    private String BillId;
    //返回数据
    private MoneyItemDetailBean moneyItemDetailBean;
    //状态
    private TextView value_status;
    //金额(大)
    private TextView value_amount;

    //提现券、回款利息或者保证金
    private LinearLayout layout4, layout5;
    private TextView title1, title2, title3, title4, title5, value1, value2, value3, value4, value5;

    public static void launche(Context ctx, String BillId) {
        Intent intent = new Intent();
        intent.setClass(ctx, TradingRecordDetailActivity.class);
        intent.putExtra("BillId", BillId);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BillId = getIntent().getStringExtra("BillId");
        setBaseContentView(R.layout.activity_money_item_detail);
        setLeftTitleVisible(true);
        setTitleValue("交易详情");
        initView();
        loadData();
    }

    //初始化
    private void initView() {
        value_status = (TextView) findViewById(R.id.value_status);
        value_amount = (TextView) findViewById(R.id.value_amount);

        layout4 = (LinearLayout) findViewById(R.id.layout4);
        layout5 = (LinearLayout) findViewById(R.id.layout5);
        title1 = (TextView) findViewById(R.id.title1);
        title2 = (TextView) findViewById(R.id.title2);
        title3 = (TextView) findViewById(R.id.title3);
        title4 = (TextView) findViewById(R.id.title4);
        title5 = (TextView) findViewById(R.id.title5);
        value1 = (TextView) findViewById(R.id.value1);
        value2 = (TextView) findViewById(R.id.value2);
        value3 = (TextView) findViewById(R.id.value3);
        value4 = (TextView) findViewById(R.id.value4);
        value5 = (TextView) findViewById(R.id.value5);
    }

    //加载数据
    private void loadData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("BillId", BillId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MoneyItemDetail, obj, TokenUtil.getEncodeToken(this), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "交易详情" + response.toString());
                try {
                    if (PackageUtil.getRequestStatus(response, TradingRecordDetailActivity.this)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        if (obj != null) {
                            moneyItemDetailBean = MyApplication.mGson.fromJson(obj.toString(), MoneyItemDetailBean.class);
                            //设置界面信息
                            setupViewData();
                        }
                    }
                } catch (Exception e) {
                    LogUtil.i("bqt", "【TradingRecordDetailActivity】【onResponse】" + e.toString());

                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.TranscationDetailTag);
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    //设置界面信息
    private void setupViewData() {
        value_status.setText(moneyItemDetailBean.Status);
        //设置金额，收入支付类型（1收入0支出）
        if (moneyItemDetailBean.Type == 0)
            value_amount.setText("-" + String.format("%.2f", moneyItemDetailBean.Amount));
        else value_amount.setText("+" + String.format("%.2f", moneyItemDetailBean.Amount));

        /**
         * 1001	Int	投资
         1005	Int	充值
         1010	Int	提现
         1015	Int	回款
         1020	Int	奖励
         1025	Int	借款
         1040	Int	还款
         */
        switch (moneyItemDetailBean.TopCategoryId) {
            case 1010://提现
                setupViewWithWithdraw();//
                break;
            case 1015://回款
                setupViewWithRepay();//
                break;
            case 1025://借款
                setupViewWithDebt();//
                break;
            case 1005://充值
                setupViewWithCZ();//
                break;
            case 1040://还款
                setupViewWithHK();//
                break;
            //其他默认不显示信息服务费
            default://投资
                title1.setText("订单号");
                value1.setText(moneyItemDetailBean.TransNo);
                title2.setText("订单金额");
                value2.setText(String.format("%.2f", moneyItemDetailBean.Amount));
                title3.setText("订单时间");
                value3.setText(moneyItemDetailBean.Time);
                layout4.setVisibility(View.GONE);
                layout5.setVisibility(View.GONE);
                break;
        }
    }

    //【充值】
    private void setupViewWithCZ() {
        title1.setText("订单号");
        value1.setText(moneyItemDetailBean.TransNo);
        title2.setText("充值金额");
        value2.setText(String.format("%.2f", moneyItemDetailBean.Amount));
        title3.setText("充值手续费");
        if (moneyItemDetailBean.RechargeExt.ProcedureFee > 0) {
            value3.setText(String.format("-%.2f", moneyItemDetailBean.RechargeExt.ProcedureFee));
        } else {
            value3.setText(String.format("%.2f", moneyItemDetailBean.RechargeExt.ProcedureFee));
        }
        title4.setText("到账时间");
        value4.setText(moneyItemDetailBean.RechargeExt.ReceivedDate);
        layout5.setVisibility(View.GONE);
    }

    //【还款】
    private void setupViewWithHK() {
        title1.setText("还款本金");
        value1.setText(String.format("-%.2f", moneyItemDetailBean.LoanerRepayExt.Principal));

        title2.setText("利息金额");
        value2.setText(String.format("-%.2f", moneyItemDetailBean.LoanerRepayExt.Interest));

        title3.setText("赠券补贴");
        value3.setText(String.format("%.2f", moneyItemDetailBean.LoanerRepayExt.SubsidyAmount));

        title4.setText("违约金");
        if (moneyItemDetailBean.LoanerRepayExt.PenaltyAmount > 0) {
            value4.setText(String.format("-%.2f", moneyItemDetailBean.LoanerRepayExt.PenaltyAmount));
        } else {
            value4.setText(String.format("%.2f", moneyItemDetailBean.LoanerRepayExt.PenaltyAmount));
        }

        title5.setText("还款时间");
        value5.setText(moneyItemDetailBean.LoanerRepayExt.getPayDate());
    }

    //【提现】
    private void setupViewWithWithdraw() {
        title1.setText("订单号");
        value1.setText(moneyItemDetailBean.TransNo);
        title2.setText("提现券");
        if (moneyItemDetailBean.WithdrawExt.CouponId != null && moneyItemDetailBean.WithdrawExt.CouponId != ""//
                && !moneyItemDetailBean.WithdrawExt.CouponId.equals("00000000-0000-0000-0000-000000000000")) {
            value2.setText("2.00");
        } else value2.setText("0.00");
        title3.setText("提现手续费");
        //提现手续费，如果大于0，则需要添加 “-”号
        if (moneyItemDetailBean.WithdrawExt.ProcedureFee > 0)
            value3.setText(String.format("-%.2f", moneyItemDetailBean.WithdrawExt.ProcedureFee));
        else value3.setText(String.format("%.2f", moneyItemDetailBean.WithdrawExt.ProcedureFee));
        title4.setText("订单时间");
        value4.setText(moneyItemDetailBean.Time);
        layout5.setVisibility(View.GONE);
    }

    //【回款】
    private void setupViewWithRepay() {
        title1.setText("项目名称");
        value1.setText(moneyItemDetailBean.InvestorRepay.getPackageName());
        title2.setText("回款本金");
        value2.setText(String.format("%.2f", moneyItemDetailBean.InvestorRepay.Principal));
        title3.setText("回款利息");
        value3.setText(String.format("+%.2f", moneyItemDetailBean.InvestorRepay.getInterest()));
        title4.setText("信息服务费");
        //信息服务费
        if (moneyItemDetailBean.InvestorRepay.getServiceFeeAmount() > 0) {
            value4.setText(String.format("-%.2f", moneyItemDetailBean.InvestorRepay.getServiceFeeAmount()));
        } else {
            value4.setText(String.format("%.2f", moneyItemDetailBean.InvestorRepay.getServiceFeeAmount()));
        }
        title5.setText("回款时间");
        value5.setText(moneyItemDetailBean.InvestorRepay.getPayDate());
    }

    //【借款】
    private void setupViewWithDebt() {
        title1.setText("订单号");
        value1.setText(moneyItemDetailBean.TransNo);//OK
        title2.setText("保证金费用");
        if (moneyItemDetailBean.DebtExt.getSecurityDepositFeeAmount() > 0) {
            value2.setText(String.format("-%.2f", moneyItemDetailBean.DebtExt.getSecurityDepositFeeAmount()));
        } else {
            value2.setText(String.format("%.2f", moneyItemDetailBean.DebtExt.getSecurityDepositFeeAmount()));
        }
        title3.setText("信息服务费");
        //信息服务费
        if (moneyItemDetailBean.DebtExt.getServiceFeeAmount() > 0) {
            value3.setText(String.format("-%.2f", moneyItemDetailBean.DebtExt.getServiceFeeAmount()));
        } else {
            value3.setText(String.format("%.2f", moneyItemDetailBean.DebtExt.getServiceFeeAmount()));
        }
        title4.setText("放款时间");
        value4.setText(moneyItemDetailBean.Time);//OK
        layout5.setVisibility(View.GONE);
    }
}