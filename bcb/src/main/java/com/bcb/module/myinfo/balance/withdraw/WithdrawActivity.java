package com.bcb.module.myinfo.balance.withdraw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.module.login.forgetpassword.ForgetPasswordActivity;
import com.bcb.module.myinfo.balance.FundCustodianWebActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.presentation.view.activity.Activity_ChangeMoney_Success;
import com.bcb.presentation.view.activity.Activity_Province;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;
import com.bcb.utils.BankLogo;
import com.bcb.utils.HttpUtils;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.MyTextUtil;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.PasswordEditText;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.utils.UmengUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ruiqin.shen
 * 类说明：提现
 */
public class WithdrawActivity extends Activity_Base implements View.OnClickListener {

    private static final String TAG = "WithdrawActivity";

    private TextView username_balance;
    private TextView bank_name_text;
    private TextView bank_card_text;
    private ImageView bank_icon;
    private LinearLayout forgetPayPassWord;
    private EditText editext_money;

    private Button withdraw_button;
    private PasswordEditText userpwd;

    //提现说明
    private LinearLayout coupon_used_status;
    private ImageView coupon_select_image;
    private boolean couponStatus = false;
    private LinearLayout withdraw_description;
    private LinearLayout description_text;
    private boolean descriptionVisible = false;
    //如果获取提现券?
    private LinearLayout coupon_description;
    private DialogWidget dialogWidget;

    private EditText sub_branch_area, sub_branch_name;
    private String pcode, pname;
    private String ccode;
    private String cname;
    private String value_branch_name;

    private String withDrawMoney = "";

//    private Receiver mReceiver;

    private UserDetailInfo mUserBankInfo;
    private UserWallet mUserWallet;

    private LinearLayout bank_area_info_layout;

    private TextView withdraw_rule, error_tips;

    private ProgressDialog progressDialog;

    //提现券Id
    private String CouponId;
    private int CouponCount = 0;

    private TextView couponcount_text;

    private BcbRequestQueue requestQueue;

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
    }

    private void findViews() {
        bank_name_text = (TextView) findViewById(R.id.bank_name_text);
        bank_card_text = (TextView) findViewById(R.id.bank_card_text);
        bank_icon = (ImageView) findViewById(R.id.bank_icon);
        username_balance = (TextView) findViewById(R.id.username_balance);
        userpwd = (PasswordEditText) findViewById(R.id.userpwd);
//		userpwd.setInputType(InputType.TYPE_CLASS_NUMBER);

        bank_area_info_layout = (LinearLayout) findViewById(R.id.bank_area_info_layout);
        sub_branch_area = (EditText) findViewById(R.id.sub_branch_area);
        sub_branch_name = (EditText) findViewById(R.id.sub_branch_name);

        withdraw_button = (Button) findViewById(R.id.withdraw_button);
        editext_money = (EditText) findViewById(R.id.editext_money);

        forgetPayPassWord = (LinearLayout) findViewById(R.id.forgetPayPassWord);
        withdraw_rule = (TextView) findViewById(R.id.withdraw_rule);
        error_tips = (TextView) findViewById(R.id.error_tips);

        sub_branch_area.setOnClickListener(this);
        withdraw_button.setOnClickListener(this);
        forgetPayPassWord.setOnClickListener(this);


        //提现说明
        coupon_used_status = (LinearLayout) findViewById(R.id.coupon_used_status);
        coupon_select_image = (ImageView) findViewById(R.id.coupon_select_image);
        coupon_used_status.setOnClickListener(this);
        withdraw_description = (LinearLayout) findViewById(R.id.withdraw_description);
        withdraw_description.setOnClickListener(this);
        description_text = (LinearLayout) findViewById(R.id.description_text);
        coupon_description = (LinearLayout) findViewById(R.id.coupon_description);
        coupon_description.setOnClickListener(this);

        couponcount_text = (TextView) findViewById(R.id.couponcount_text);
    }

    private void initViews() {
        //账户余额
        if (null != MyApplication.mUserWallet) {
            username_balance.setText(String.format("%.2f", MyApplication.mUserWallet.BalanceAmount) + "  元");
            mUserWallet = MyApplication.mUserWallet;
        }

        //银行卡账号
        if (MyApplication.mUserDetailInfo.BankCard != null) {
            bank_card_text.setText(MyTextUtil.delBankNum(MyApplication.mUserDetailInfo.BankCard.getCardNumber()));
            //设置银行卡logo
            BankLogo bankLogo = new BankLogo();
            bank_icon.setBackgroundResource(bankLogo.getDrawableBankLogo(MyApplication.mUserDetailInfo.BankCard.getBankCode()));
            mUserBankInfo = MyApplication.mUserDetailInfo;
        }

//        IntentFilter intentFilter = new IntentFilter("com.bcb.bank.area.complete");
//        mReceiver = new Receiver();
//        registerReceiver(mReceiver, intentFilter);

        //输入交易密码
        userpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                error_tips.setVisibility(View.GONE);
            }
        });

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
                //隐藏出错提示
                error_tips.setVisibility(View.GONE);

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

            case R.id.sub_branch_area:
                Intent intent = new Intent();
                intent.setClass(WithdrawActivity.this, Activity_Province.class);
                startActivity(intent);
                break;

            //忘记密码
            case R.id.forgetPayPassWord:
                UmengUtil.eventById(WithdrawActivity.this, R.string.recharge_f_secrt);
                ForgetPasswordActivity.launche(WithdrawActivity.this);
                break;

            //提现说明
            case R.id.withdraw_description:
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

            //提现券选中
            case R.id.coupon_used_status:
                if (CouponCount > 0) {
                    couponStatus = !couponStatus;
                    setupCouponTips();
                }
                break;
        }
    }

//    class Receiver extends BroadcastReceiver {
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals("com.bcb.bank.area.complete")) {
//                pcode = intent.getStringExtra(Activity_City.PARAM_pcode);
//                pname = intent.getStringExtra(Activity_City.PARAM_pname);
//                AreaBean cityObject = (AreaBean) intent.getSerializableExtra(Activity_City.PARAM_cityObject);
//                ccode = cityObject.Code;
//                cname = cityObject.Name;
//                if (null != cityObject) {
//                    showBankArea();
//                }
//            }
//
//        }
//    }

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
                        CouponCount = response.getJSONObject("result").getInt("CouponCount");
                        showCouponInfo(response.getJSONObject("result").getInt("CouponCount"));
                    } else {
                        CouponId = "";
                        CouponCount = 0;
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
        couponcount_text.setText("(可用 " + couponCount + " 张)");
        if (couponCount > 0) {
            couponStatus = true;
        }
        setupCouponTips();
    }


    private void showBankArea() {
        sub_branch_area.setText(pname + cname);
        if (null != value_branch_name) {
            sub_branch_name.setText(value_branch_name);
        }
    }


    //显示银行卡信息
    private void showBankInfo() {
        bank_card_text.setText(MyTextUtil.delBankNum(mUserBankInfo.BankCard.CardNumber));
        if (null != mUserBankInfo.BankCard.CityCode && null != mUserBankInfo.BankCard.CityName &&
                null != mUserBankInfo.BankCard.ProvinceCode &&
                null != mUserBankInfo.BankCard.ProvinceName &&
                null != mUserBankInfo.BankCard.BranchBankName) {
            pcode = mUserBankInfo.BankCard.ProvinceCode;
            pname = mUserBankInfo.BankCard.ProvinceName;
            ccode = mUserBankInfo.BankCard.CityCode;
            cname = mUserBankInfo.BankCard.CityName;
            value_branch_name = mUserBankInfo.BankCard.BranchBankName;
            showBankArea();
        }
    }

    //显示余额信息
    private void showBanlanceData() {
        if (mUserWallet.BalanceAmount > 0)
            username_balance.setText("" + mUserWallet.BalanceAmount + " 元");
        else
            username_balance.setText("0.00 元");
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
        if (doubleMoney <= 2) {
            ToastUtil.alert(WithdrawActivity.this, "提现金额必须大于手续费");
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
                if (couponStatus) {
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

    private void onWithdrawSuccess() {
        //取现成功之后，将余额写入静态数据区，方便后面充值成功获取余额
        MyApplication.mUserWallet.setBalanceAmount(MyApplication.mUserWallet.getBalanceAmount() - Float.parseFloat(withDrawMoney));
        Activity_ChangeMoney_Success.launche(WithdrawActivity.this, Activity_ChangeMoney_Success.ACTION_Withdrawals);
        finish();
    }

    /**
     * 显示手续费
     */
    private void setupCouponTips() {
        if (couponStatus) {
            coupon_select_image.setBackgroundResource(R.drawable.withdraw_hook);
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
            coupon_select_image.setBackgroundResource(R.drawable.withdraw_rect);
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


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(mReceiver);
//    }

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
//
//    /*********************
//     * 转圈提示
//     **************************/
//    //显示转圈提示
//    private void showProgressBar() {
//        if (null == progressDialog) progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
//        progressDialog.setMessage("正在加载数据...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setCancelable(true);
//        progressDialog.show();
//    }
//
//    //隐藏转圈提示
//    private void hideProgressBar() {
//        if (!isFinishing() && null != progressDialog && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }
}
