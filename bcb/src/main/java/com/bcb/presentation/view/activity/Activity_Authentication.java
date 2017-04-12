package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.data.bean.BankItem;
import com.bcb.utils.BankLogo;
import com.bcb.utils.MQCustomerManager;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.UmengUtil;
import com.bcb.presentation.presenter.IPresenter_Authentication;
import com.bcb.presentation.presenter.IPresenter_AuthenticationImpl;
import com.bcb.presentation.view.activity_interface.Interface_Authentication;

/**
 * 废弃了
 */
public class Activity_Authentication extends Activity_Base implements Interface_Authentication, View.OnClickListener {
    private static final String TAG = "Activity_Authentication";
    //加载数据
    private ProgressDialog progressDialog;
    //认证
    private Button authentication_button;
    //选择银行
    private LinearLayout select_bank;
    //用户名、身份证、银行卡号
    private EditText username, idcard, bankcardcode, bankcard_phone;
    //选择银行提示
    private TextView txt_choose_bank;
    //选择后的银行结果
    private RelativeLayout layout_choosed_bank;
    //用于存放已经选择的银行
    private BankItem mBankItem;

    //剩余认证状态
    boolean remainStatus = true;
    //错误信息
    RelativeLayout error_layout;
    TextView error_tips;

    //客服
    private TextView customer_service;

    private IPresenter_Authentication iPresenterAuthentication;

    //静态加载
    public static void launche(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, Activity_Authentication.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Authentication.this);
        setBaseContentView(R.layout.activity_authentication);
        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUtil.eventById(Activity_Authentication.this, R.string.autu_back);
                finish();
            }
        });
        setTitleValue("账户认证");
        iPresenterAuthentication = new IPresenter_AuthenticationImpl(this, this);
        //加载用户数据
        showProgressBar();
        iPresenterAuthentication.onUpdateUserInfo();
        //初始化界面
        init();
        //统计认证事件
        UmengUtil.eventById(this, R.string.auth_entry);
    }

    //初始化界面
    private void init() {
        txt_choose_bank = (TextView) findViewById(R.id.txt_choose_bank);
        layout_choosed_bank = (RelativeLayout) findViewById(R.id.layout_choosed_bank);
        layout_choosed_bank.setVisibility(View.GONE);
        //选择银行
        select_bank = (LinearLayout) findViewById(R.id.select_bank);
        select_bank.setOnClickListener(this);
        //立即认证
        authentication_button = (Button) findViewById(R.id.authentication_button);
        authentication_button.setOnClickListener(this);
        //用户名、身份证和银行卡号
        username = (EditText) findViewById(R.id.username);
        idcard = (EditText) findViewById(R.id.idcard);
        bankcardcode = (EditText) findViewById(R.id.bankcardcode);
        bankcard_phone = (EditText) findViewById(R.id.bankcard_phone);
        //错误信息
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        error_tips = (TextView) findViewById(R.id.error_tips);
        //认证有问题，客服帮解决
        customer_service = (TextView) findViewById(R.id.customer_service);
        customer_service.setOnClickListener(this);
    }

    /********************立即认证***************************/
    //立即认证
    private void authenticationRightNow(){
        //判断是否选择了填写了必要的信息和选择银行
        if(!ToastUtil.checkInputParam(Activity_Authentication.this, username, "请输入您的姓名")) {
            return;
        }

        if(!ToastUtil.checkInputParam(Activity_Authentication.this, idcard, "请输入您的身份证号")) {
            return;
        }

        if (null == mBankItem) {
            ToastUtil.alert(Activity_Authentication.this, "请选择银行");
            return;
        }

        if(!ToastUtil.checkInputParam(Activity_Authentication.this, bankcardcode, "请填写银行卡号")) {
            return;
        }

        if (!ToastUtil.checkInputParam(Activity_Authentication.this, bankcard_phone, "请填写银行卡预留手机号")) {
            return;
        }

        //根据剩余认证次数判断，如果为false，则表示不可用，提示到客服中心联系开通
        if (!remainStatus) {
            //显示提示
            error_layout.setVisibility(View.VISIBLE);
            error_tips.setText("认证失败，请联系客服进行认证");
            return;
        }
        showProgressBar();
        iPresenterAuthentication.onAuthenticate(mBankItem.getBankCode(),
                mBankItem.getBankName(),
                bankcardcode.getText().toString(),
                idcard.getText().toString(),
                username.getText().toString(), bankcard_phone.getText().toString());
    }

    //提示还有多少次认证机会
    private void tipsForRemainCount(int count) {
        error_layout.setVisibility(View.VISIBLE);
        //判断如果count <= 0，则提示联系客服
        if (count <= 0) {
            error_tips.setText("认证失败，请联系客服进行认证");
            remainStatus = false;
        } else {
            remainStatus = true;
            error_tips.setText("你还有" + count + "次认证机会");
        }

    }

    //选择银行返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    String bankCode = data.getStringExtra("bankCode");
                    String bankLogo = data.getStringExtra("bankLogo");
                    String bankName = data.getStringExtra("bankName");
                    float maxSingle = data.getFloatExtra("maxSingle", 0);
                    float maxDay = data.getFloatExtra("maxDay", 0);
                    mBankItem = new BankItem(bankCode, bankLogo, bankName, maxSingle, maxDay);
                    onShowBankInfo();
                }
                break;

            default:
                break;
        }

    }

    //显示银行信息
    private void onShowBankInfo(){
        txt_choose_bank.setVisibility(View.GONE);
        layout_choosed_bank.setVisibility(View.VISIBLE);
        //设置银行logo和名称
        ImageView bank_icon = (ImageView) findViewById(R.id.bank_icon);
        TextView bank_name = (TextView) findViewById(R.id.bank_name);
        BankLogo bankLogo = new BankLogo();
        bank_icon.setBackgroundResource(bankLogo.getDrawableBankLogo(mBankItem.getBankCode()));
        bank_name.setText(mBankItem.getBankName());
    }

    /********************* 转圈提示 **************************/
    //显示转圈提示
    private void showProgressBar() {
        if(null == progressDialog) progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage("正在获取用户认证信息....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    //隐藏转圈提示
    private void hideProgressBar() {
        if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UmengUtil.eventById(Activity_Authentication.this, R.string.autu_back);
        iPresenterAuthentication.clearDependency();
        iPresenterAuthentication = null;
        finish();
    }

    //获取用户信息结果回调
    @Override
    public void onRequestResult(int resultStatus, String message) {
        hideProgressBar();
        switch (resultStatus) {
            //已经认证成功
            case 1:
                UmengUtil.eventById(Activity_Authentication.this, R.string.self_charge);
                Activity_Recharge_Second.launche(Activity_Authentication.this);
                //将已认证的信息发送出去
                Intent intent = new Intent();
                intent.putExtra("authenticationStatus", true);
                setResult(10, intent);
                finish();
                break;

            //你还没有认证
            default:
                ToastUtil.alert(Activity_Authentication.this, TextUtils.isEmpty(message) ? "网络异常，请稍后再试" : message);
                break;
        }
    }

    //认证结果回调
    @Override
    public void onRequestResult(int resultStatus, String message, int remainCount) {
        hideProgressBar();
        if (resultStatus == 1) {
            remainStatus = true;
            //将登录成功的信息发送出去
            Intent intent = new Intent();
            intent.putExtra("authenticationStatus", true);
            setResult(10, intent);
            UmengUtil.eventById(Activity_Authentication.this, R.string.auth_y);
            finish();
        } else {
            UmengUtil.eventById(Activity_Authentication.this, R.string.auth_n);
            ToastUtil.alert(Activity_Authentication.this, message);
            //回传的结果，如果剩余次数为 -10， 则表示成功，否则计算次数
            if (remainCount != -10) {
                tipsForRemainCount(remainCount);
            } else {
                error_layout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            //立即认证
            case R.id.authentication_button:
                authenticationRightNow();
                break;

            //选择银行
            case R.id.select_bank:
                Intent intent = new Intent(Activity_Authentication.this, Activity_Select_Bank.class);
                startActivityForResult(intent, 1);
                break;

            //客服
            case R.id.customer_service:
                String userId = null;
                if (MyApplication.mUserDetailInfo != null) {
                    userId = MyApplication.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(this).showCustomer(userId);
                break;
        }
    }

}