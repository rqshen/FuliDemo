package com.bcb.module.myinfo.balance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.module.browse.FundCustodianWebActivity;
import com.bcb.module.login.LoginActivity;
import com.bcb.module.myinfo.balance.recharge.RechargeActivity;
import com.bcb.module.myinfo.balance.withdraw.WithdrawActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsTwo;
import com.bcb.module.myinfo.balance.trading.TradingRecordActivity;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bcb.R.id.back_img;

/**
 * Created by ruiqin.shen
 * 类说明：我的余额
 */
public class BalanceActivity extends Activity_Base {

    @BindView(R.id.layout_cz)
    RelativeLayout layoutcz;
    @BindView(R.id.layout_tx)
    RelativeLayout layouttx;
    @BindView(R.id.layout_mx)
    RelativeLayout layoutmx;
    @BindView(R.id.value_ye)
    TextView value_ye;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setBaseContentView(R.layout.activity_my_ye);
        ButterKnife.bind(this);
        setLeftTitleVisible(true);
        setTitleValue("我的余额");
        layout_title.setBackgroundColor(getResources().getColor(R.color.red));
        title_text.setTextColor(getResources().getColor(R.color.white));
        dropdown.setImageResource(R.drawable.return_delault);
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        (findViewById(back_img)).setVisibility(View.GONE);
        value_ye.setText(MyApplication.mUserWallet.getBalanceAmount() + "");
    }

    @OnClick({R.id.layout_cz, R.id.layout_tx, R.id.layout_mx})
    public void onClick(View view) {
        if (MyApplication.saveUserInfo.getAccess_Token() == null) LoginActivity.launche(ctx);
        switch (view.getId()) {
            case R.id.layout_cz:
                rechargeMoney();
                break;
            case R.id.layout_tx:
                withdrawMoney();
                break;
            case R.id.layout_mx:
                TradingRecordActivity.launche(ctx);
                break;
        }
    }

    //充值
    private void rechargeMoney() {
        //已开通托管
        if (MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.HasOpenCustody) {
            startActivity(new Intent(ctx, RechargeActivity.class));
        } else {
            startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
        }
    }

    //提现
    //绑定提现卡后请求我的银行卡接口同样会返回银行卡信息，不过【IsQPCard】为【false】
    // {"status":1,"message":"","result":{"BankCode":"CIB","BankName":"兴业银行","CardNumber":"6229081111111111112","IsQPCard":false}}
    private void withdrawMoney() {
        //未开通托管
        if (MyApplication.mUserDetailInfo == null || !MyApplication.mUserDetailInfo.HasOpenCustody) {
            startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
            return;
        }
        //用户还没绑卡
        if (MyApplication.mUserDetailInfo.BankCard == null) {
            showAlertView("您还没指定提现卡哦", "该银行卡将作为账户唯一提现银行卡", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestBandCard();
                    alertView.dismiss();
                    alertView = null;
                }
            });
        } else {
            startActivity(new Intent(ctx, WithdrawActivity.class));
        }
    }

    private AlertView alertView;

    //提示对话框
    private void showAlertView(String titleName, String contentMessage, DialogInterface.OnClickListener onClickListener) {
        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
        ibuilder.setTitle(titleName);
        ibuilder.setMessage(contentMessage);
        ibuilder.setPositiveButton("立即设置", onClickListener);
        ibuilder.setNegativeButton("取消", null);
        alertView = ibuilder.create();
        alertView.show();
    }

    /**
     * 绑定提现卡
     */
    private void requestBandCard() {
        String requestUrl = UrlsTwo.UrlBandCard;
        String encodeToken = TokenUtil.getEncodeToken(ctx);
        LogUtil.i("bqt", "【RechargeActivity】【BandCard】请求路径：" + requestUrl);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>
                () {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "绑定提现卡：" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    try {
                        /** 后台返回的JSON对象，也是要转发给汇付的对象 */
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result != null) {
                            //网页地址
                            String postUrl = result.optString("PostUrl");
                            result.remove("PostUrl");//移除这个参数
                            //传递的 参数
                            String postData = HttpUtils.jsonToStr(result.toString());
                            //跳转到webview
                            FundCustodianWebActivity.launche(ctx, "绑定提现卡", postUrl, postData);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【FundCustodianAboutActivity】【OpenAccount】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(ctx, response.optString("message"), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【RechargeActivity】【BandCard】网络异常，请稍后重试" + error.toString());
            }
        });
        MyApplication.getInstance()
                .getRequestQueue()
                .add(jsonRequest);
    }
}