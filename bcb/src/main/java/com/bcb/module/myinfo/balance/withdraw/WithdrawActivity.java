package com.bcb.module.myinfo.balance.withdraw;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.data.bean.UserWallet;
import com.bcb.module.browse.FundCustodianWebActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;
import com.bcb.util.BankLogo;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.bcb.R.id.withdraw_description;

/**
 * Created by ruiqin.shen
 * 类说明：提现
 */
public class WithdrawActivity extends Activity_Base implements View.OnClickListener {

    private TextView username_balance;
    private TextView bank_card_text;
    private ImageView bank_icon;
    private EditText editext_money;
    private Button withdraw_button;
    private LinearLayout description_text;
    private boolean descriptionVisible = false;
    //如果获取提现券?
    private LinearLayout coupon_description;
    private DialogWidget dialogWidget;
    private UserWallet mUserWallet;
    //提现券Id
    private String CouponId;
    private BcbRequestQueue requestQueue;
    TextView couponcount_text;
    TextView withdraw_rule;
    private TextView tv_xianer;
    private CheckBox checkbox_coupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(WithdrawActivity.this);
        setBaseContentView(R.layout.activity_withdraw);
        setLeftTitleVisible(true);
        setTitleValue("提现");
        requestQueue = MyApplication.getInstance().getRequestQueue();
        findViews();
        initViews();
        setCouponCheckedListener();//设置提现券的点击事件
    }

    /**
     * 设置提现券的点击事件
     */
    private void setCouponCheckedListener() {
        checkbox_coupon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setupCouponTips();
            }
        });
    }

    private void findViews() {
        bank_card_text = (TextView) findViewById(R.id.bank_card_text);
        bank_icon = (ImageView) findViewById(R.id.bank_icon);
        username_balance = (TextView) findViewById(R.id.username_balance);
        withdraw_button = (Button) findViewById(R.id.withdraw_button);
        editext_money = (EditText) findViewById(R.id.editext_money);
        withdraw_button.setOnClickListener(this);


        description_text = (LinearLayout) findViewById(R.id.description_text);
        coupon_description = (LinearLayout) findViewById(R.id.coupon_description);
        coupon_description.setOnClickListener(this);

        couponcount_text = (TextView) findViewById(R.id.couponcount_text);
        withdraw_rule = (TextView) findViewById(R.id.withdraw_rule);

        tv_xianer = (TextView) findViewById(R.id.tv_xianer);
        checkbox_coupon = (CheckBox) findViewById(R.id.checkbox_coupon);
    }

    private void initViews() {
        //账户余额
        if (null != MyApplication.mUserWallet) {
            username_balance.setText(String.format("%.2f", MyApplication.mUserWallet.BalanceAmount) + "元");
            tv_xianer.setText(String.format("%.2f", MyApplication.mUserWallet.BalanceAmount));
            mUserWallet = MyApplication.mUserWallet;
        }

        //银行卡账号
        if (MyApplication.mUserDetailInfo.BankCard != null) {
            String cardNumber = MyApplication.mUserDetailInfo.BankCard.CardNumber;
            bank_card_text.setText("尾号" + cardNumber.substring(cardNumber.length() - 4));
            //设置银行卡logo
            BankLogo bankLogo = new BankLogo();
            bank_icon.setBackgroundResource(bankLogo.getDrawableBankLogo(MyApplication.mUserDetailInfo.BankCard.getBankCode()));
        }

        //输入金额
        editext_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 先判断输入框的数字是否正常，允许输入两个小数点
                String temp = editext_money.getText().toString();
                int inputcount = 0, inputstart = 0;
                while ((inputstart = temp.indexOf(".", inputstart)) >= 0) {
                    inputstart += ".".length();
                    inputcount++;
                }
                //删除一开始就输入的小数点
                if (temp.indexOf(".") == 0 || inputcount > 1) {
                    s.delete(temp.indexOf("."), temp.length());
                }

                //只保留小数点后面两位小数
                if (temp.indexOf(".") > 0 && temp.length() - temp.indexOf(".") > 2) {
                    s.delete(temp.indexOf(".") + 3, temp.length());
                }

                //无法提现100万以上的金额
                if (temp.indexOf(".") <= 0 && temp.length() > 5) {
                    s.delete(5, temp.length());
                }
                //显示提示
                setupCouponTips();
            }
        });


        //获取提现券信息
        getCouponInfo();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.withdraw_button://立即提现
                onWithdraw();
                break;

            //提现说明
            case withdraw_description:
                descriptionVisible = !descriptionVisible;
                if (descriptionVisible) {
                    description_text.setVisibility(View.VISIBLE);
                } else {
                    description_text.setVisibility(View.GONE);
                }
                break;

            //如何获取提现券
            case R.id.coupon_description:
                showGetCouponDialog();
                break;
        }
    }


    //请求提现券信息
    private void getCouponInfo() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.WithdrawCouponInfo, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        LogUtil.i("bqt", "【WithdrawActivity】【onResponse】提现券" + response.toString());

                        //显示提现券信息
                        CouponId = response.getJSONObject("result").getString("CouponId");
                        showCouponInfo(response.getJSONObject("result").getInt("CouponCount"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.WithdrawCouponInfoTag);
        requestQueue.add(jsonRequest);
    }

    //显示提现券信息
    private void showCouponInfo(int couponCount) {
        couponcount_text.setText(couponCount + "");
        if (couponCount > 0) {
            checkbox_coupon.setClickable(true);
            checkbox_coupon.setChecked(true);
        } else {
            checkbox_coupon.setClickable(false);
        }
        setupCouponTips();
    }

    //提现
    private void onWithdraw() {
        // 没有输入提现金额
        if (!ToastUtil.checkInputParam(WithdrawActivity.this, editext_money, "请输入提现金额")) {
            return;
        }

        // 提现金额不能大于账户余额
        String money = editext_money.getText().toString();
        double doubleMoney = Double.parseDouble(money);
        if (mUserWallet.BalanceAmount < doubleMoney) {
            ToastUtil.alert(WithdrawActivity.this, "提现金额不能大于账户余额");
            return;
        }

        //判断余额是否大于手续费
        if (doubleMoney <= 3) {
            ToastUtil.alert(WithdrawActivity.this, "提现金额最低不能少于3元");
            return;
        }
        requestTX(money, CouponId);
    }

    /**
     * 提现
     */
    private void requestTX(String Amount, String CouponId) {
        String requestUrl = UrlsTwo.UrlTX_HF;
        String encodeToken = TokenUtil.getEncodeToken(WithdrawActivity.this);
        LogUtil.i("bqt", "【WithdrawActivity】【TX】请求路径：" + requestUrl);
        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("Amount", Amount);
            if (!TextUtils.isEmpty(CouponId) && !CouponId.equalsIgnoreCase("null")) {
                if (checkbox_coupon.isChecked()) {
                    requestObj.put("CouponId", CouponId);
                }
            }
            LogUtil.i("bqt", "【WithdrawActivity】【TX】请求参数：" + requestObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, requestObj, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【WithdrawActivity】【TX】返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, WithdrawActivity.this)) {
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
                            FundCustodianWebActivity.launche(WithdrawActivity.this, "提现", postUrl, postData);
                            finish();
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【WithdrawActivity】【TX】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(WithdrawActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【WithdrawActivity】【TX】网络异常，请稍后重试" + error.toString());
            }
        });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }


    /**
     * 显示手续费
     */
    private void setupCouponTips() {
        if (checkbox_coupon.isChecked()) {
            if (editext_money.getText().toString().isEmpty()) {
                withdraw_rule.setText("提现需收取手续费2元");
            } else {
                if (Float.parseFloat(editext_money.getText().toString()) < 2.00) {
                    withdraw_rule.setText("提现金额必须大于2元");
                } else {
                    withdraw_rule.setText("本次提现将收取手续费0元，实际到账" + editext_money.getText().toString() + "元");
                }
            }
        } else {
            if (editext_money.getText().toString().isEmpty() || Float.parseFloat(editext_money.getText().toString()) < 2) {
                withdraw_rule.setText("提现需收取手续费2元");
            } else {
                String withdrawMoney = String.format("%.2f", Double.parseDouble(editext_money.getText().toString()) - 2);
                withdraw_rule.setText("本次提现将收取手续费2元，实际到账" + withdrawMoney + "元");
            }
        }
    }

    /**
     * 显示冻结金额对话框
     */
    private void showGetCouponDialog() {
        dialogWidget = new DialogWidget(WithdrawActivity.this, getCouponView(), true);
        dialogWidget.show();
    }

    protected View getCouponView() {
        return MyMaskFullScreenView.getInstance(WithdrawActivity.this, "您每月可获赠1张提现抵扣券，您还可以通过参加平台活动获得提现券", new MyMaskFullScreenView.OnClikListener() {
            @Override
            public void onViewClik() {
                dialogWidget.dismiss();
                dialogWidget = null;
            }
        }).getView();
    }

    //缓存提现券
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //缓存提现券ID
        if (CouponId != null && !CouponId.isEmpty()) {
            savedInstanceState.putString("CouponId", CouponId);
        }
    }

    //取出暂存的订单号信息
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //获取提现券ID
        if (!savedInstanceState.getString("CouponId").isEmpty()
                && !savedInstanceState.getString("CouponId").equalsIgnoreCase("")
                && !savedInstanceState.getString("CouponId").equalsIgnoreCase("null")) {
            CouponId = savedInstanceState.getString("CouponId");
        }
    }

}
