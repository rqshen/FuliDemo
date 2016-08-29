package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.BanksBean;
import com.bcb.data.util.BankLogo;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值
 */
public class Activity_Charge_HF extends Activity_Base implements View.OnClickListener, TextWatcher {
    public static float ADD_MONERY = 0;
    TextView tv_left_monery, tv_next, tv_tip_bottom, tv_unband;
    EditText et_add_monery;
    ArrayList<BanksBean> list;

    TextView bank_card_text, tv_xianer, tv_no, bank_name_text;
    RelativeLayout layout_bank_card;
    LinearLayout ll_card, ll_tips;
    ImageView bank_icon, iv_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_hf);
        initTitle();

        tv_left_monery = (TextView) findViewById(R.id.tv_left_monery);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_tip_bottom = (TextView) findViewById(R.id.tv_tip_bottom);
        tv_unband = (TextView) findViewById(R.id.tv_unband);
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
        iv_clear.setOnClickListener(this);
        tv_unband.setOnClickListener(this);

        initTv_tip_bottom();
        initBankCard();
        et_add_monery = (EditText) findViewById(R.id.et_add_monery);
        et_add_monery.addTextChangedListener(this);
        if (App.getInstance().mUserWallet != null) {
            tv_left_monery.setText(String.format("%.2f", App.getInstance().mUserWallet.getBalanceAmount()) + "元");
        }
        tv_next.setOnClickListener(this);
        requestBankList();
    }

    private void initBankCard() {
        layout_bank_card = (RelativeLayout) findViewById(R.id.layout_bank_card);
        ll_card = (LinearLayout) findViewById(R.id.ll_card);
        ll_tips = (LinearLayout) findViewById(R.id.ll_tips);
        bank_card_text = (TextView) findViewById(R.id.bank_card_text);
        bank_name_text = (TextView) findViewById(R.id.bank_name_text);
        tv_xianer = (TextView) findViewById(R.id.tv_xianer);
        tv_no = (TextView) findViewById(R.id.tv_no);
        bank_icon = (ImageView) findViewById(R.id.bank_icon);
        //银行卡账号
        if (App.mUserDetailInfo.BankCard != null && App.mUserDetailInfo.BankCard.IsQPCard) {//已绑定，且是快捷支付
//            bank_card_text.setText(MyTextUtil.delBankNum(App.mUserDetailInfo.BankCard.getCardNumber()));
            String cardNumber = App.mUserDetailInfo.BankCard.CardNumber;
            bank_card_text.setText("尾号" + cardNumber.substring(cardNumber.length() - 4));
            //设置银行卡logo
            BankLogo bankLogo = new BankLogo();
            bank_icon.setBackgroundResource(bankLogo.getDrawableBankLogo(App.mUserDetailInfo.BankCard.BankCode));
            bank_name_text.setText(App.mUserDetailInfo.BankCard.BankName);
            setRightTitleValue("解绑说明", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity_WebView.launche(Activity_Charge_HF.this, "解绑说明", UrlsTwo.UrlUnBandExplain, "");
                }
            });

            tv_no.setVisibility(View.GONE);
            layout_bank_card.setVisibility(View.VISIBLE);//银行卡号
            ll_card.setVisibility(View.VISIBLE);//该卡本次最多可充值1000元，每日最多2000元
            ll_tips.setVisibility(View.VISIBLE);//福利金融由央行监管的****进行资金托管
        } else {//未绑定
            setRightTitleValue("限额说明", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list != null && list.size() > 0) {
                        Intent intent = new Intent(Activity_Charge_HF.this, Activity_Charge_Tips.class);
                        intent.putParcelableArrayListExtra("data", list);
                        startActivity(intent);
                    } else
                        Toast.makeText(Activity_Charge_HF.this, "查询限额信息失败", Toast.LENGTH_SHORT).show();
                }
            });
            tv_no.setVisibility(View.VISIBLE);//快捷支付银行卡将作为本账户唯一提现卡
            layout_bank_card.setVisibility(View.GONE);
            ll_card.setVisibility(View.GONE);
            ll_tips.setVisibility(View.GONE);
        }
    }

    private void initTv_tip_bottom() {
        SpannableString mSpannableString = new SpannableString("点击联系客服");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xff3399ff);
        mSpannableString.setSpan(colorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String userId = null;
                //判断是否为空
                if (App.mUserDetailInfo != null) {
                    userId = App.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(Activity_Charge_HF.this).showCustomer(userId);
            }
        };
        mSpannableString.setSpan(clickableSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_tip_bottom.append(mSpannableString);
        tv_tip_bottom.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        if (App.mUserWallet != null) {
            tv_left_monery.setText(String.format("%.2f", App.mUserWallet.getBalanceAmount()) + "元");
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.tv_unband:
                Activity_WebView.launche(this, "解绑", UrlsTwo.UrlUnBand, "");
                break;
            case R.id.iv_clear:
                et_add_monery.setText("");
                break;
            case R.id.tv_next:
                if (TextUtils.isEmpty(et_add_monery.getText().toString().trim())) {
                    Toast.makeText(Activity_Charge_HF.this, "请输入充值金额", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        ADD_MONERY = Float.valueOf(et_add_monery.getText().toString().trim());
                    } catch (Exception e) {
                        LogUtil.i("bqt", "【Activity_Charge_HF】【onClick】" + e.toString());
                        Toast.makeText(Activity_Charge_HF.this, "输入金额有误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (maxMonery > 0 && ADD_MONERY > maxMonery) {
                        Toast.makeText(Activity_Charge_HF.this, "充值金额超过单笔最大限额（" + maxMonery + "元），请修改充值金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (ADD_MONERY >= 10000000.0f) {//服务器限制
                        Toast.makeText(Activity_Charge_HF.this, "输入金额超过受理限额", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    requestCharge();
                    break;
                }
            default:
                break;
        }
    }

    private void initTitle() {
        //标题
        setTitleValue("充值");
        setLeftTitleVisible(true);
        setLeftTitleListener(this);
    }

    /**
     * 充值
     */
    private void requestCharge() {
        String requestUrl = UrlsTwo.UrlCharge;
        String encodeToken = TokenUtil.getEncodeToken(Activity_Charge_HF.this);
        LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】请求路径：" + requestUrl);
        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("Amount", ADD_MONERY + "");
            LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】请求参数：" + requestObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, requestObj, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, Activity_Charge_HF.this)) {
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
                            Activity_WebView.launche(Activity_Charge_HF.this, "充值", postUrl, postData);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【Activity_Open_Account】【OpenAccount】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(Activity_Charge_HF.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】网络异常，请稍后重试" + error.toString());
            }
        });
        App.getInstance().getRequestQueue().add(jsonRequest);
    }

    int maxMonery;

    /**
     * 银行列表
     */
    private void requestBankList() {
        String requestUrl = UrlsTwo.UrlBanks;
        String encodeToken = TokenUtil.getEncodeToken(Activity_Charge_HF.this);
        LogUtil.i("bqt", "【Activity_Charge_HF】【BankList】请求路径：" + requestUrl);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Activity_Charge_HF】【BankList】返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, Activity_Charge_HF.this)) {
                    try {
                        list = App.mGson.fromJson(response.optJSONArray("result").toString(), new TypeToken<List<BanksBean>>() {
                        }.getType());
                        if (list != null) {

                        }
                        if (App.mUserDetailInfo.BankCard != null && App.mUserDetailInfo.BankCard.IsQPCard) {//已绑定，且是快捷支付
                            for (int i = 0; i < list.size(); i++) {
                                if (App.mUserDetailInfo.BankCard.BankCode.equalsIgnoreCase(list.get(i).getBankCode())) {
                                    ll_card.setVisibility(View.VISIBLE);
                                    maxMonery = list.get(i).getMaxSingle();
//                                    tv_xianer.setText("该卡本次最多可充值" + list.get(i).getMaxSingle() + "元，每日最多" + list.get(i).getMaxDay() + "元");
                                    tv_xianer.setText("单笔最高" + initMonery(list.get(i).getMaxSingle()) + "，单日限额" + initMonery(list.get(i).getMaxDay()));
                                }
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【Activity_Charge_HF】【BankList】" + e.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【Activity_Charge_HF】【BankList】网络异常，请稍后重试" + error.toString());
            }
        });
        App.getInstance().getRequestQueue().add(jsonRequest);
    }

    private String initMonery(int monery) {
        if (monery >= 10000) return monery / 10000 + "万";
        else return monery + "";
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = et_add_monery.getText().toString().trim();
//        try {
//            if (!TextUtils.isEmpty(text) && Float.valueOf(text) >0) {//= 500
//                tv_next.setBackgroundResource(R.drawable.button_solid_red);
//            } else {
//                tv_next.setBackgroundResource(R.drawable.button_solid_black);
//            }
//        } catch (Exception e) {
//            Toast.makeText(Activity_Charge_HF.this, "输入金额格式有误", Toast.LENGTH_SHORT).show();
//            et_add_monery.setText("");
//        }
        if (!TextUtils.isEmpty(text)) {
            iv_clear.setVisibility(View.VISIBLE);
        } else
            iv_clear.setVisibility(View.GONE);
    }
}
