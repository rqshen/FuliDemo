package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.loan.LoanRequestInfoBean;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.IdentifyAlertView;

import org.json.JSONObject;

/**
 * Created by cain on 16/1/6.
 */
public class Activity_Loan_Introduction extends Activity_Base implements View.OnClickListener {
    private static final String TAG = "Activity_Loan_Introduction";
    //标题
    private String title;
    private String description;

    //描述
    private TextView text_description;

    //我能贷多少？
    private RelativeLayout loan_amount;
    //利息怎么算？
    private RelativeLayout loan_interest;
    //如何还款？
    private RelativeLayout loan_repayment;
    //准备哪些材料
    private RelativeLayout loan_material;
    //在线
    private RelativeLayout loan_customer_service;
    //电话客服
    private RelativeLayout loan_phone_service;
    //立即申请按钮
    private Button loan_button;

    //弹框提示
    private DialogWidget dialogWidget;

    //转圈提示
    ProgressDialog progressDialog;

    private BcbRequestQueue requestQueue;

    //构造函数
    public static void launche(Context context, String title, String description) {
        Intent intent = new Intent();
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.setClass(context, Activity_Loan_Introduction.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Loan_Introduction.this);
        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        setBaseContentView(R.layout.activity_loan_introduce);
        setLeftTitleVisible(true);
        setTitleValue(title);
        setupView();
        requestQueue = App.getInstance().getRequestQueue();
    }

    //初始化界面
    private void setupView() {
        //描述
        text_description = (TextView) findViewById(R.id.text_description);
        text_description.setText(description);

        //我能贷多少？
        loan_amount = (RelativeLayout) findViewById(R.id.loan_amount);
        loan_amount.setOnClickListener(this);
        //利息怎么算？
        loan_interest = (RelativeLayout) findViewById(R.id.loan_interest);
        loan_interest.setOnClickListener(this);
        //如何还款？
        loan_repayment = (RelativeLayout) findViewById(R.id.loan_repayment);
        loan_repayment.setOnClickListener(this);
        //准备哪些材料？
        loan_material = (RelativeLayout) findViewById(R.id.loan_material);
        loan_material.setOnClickListener(this);
        //专属客服
        loan_customer_service = (RelativeLayout) findViewById(R.id.loan_customer_service);
        loan_customer_service.setOnClickListener(this);
        //电话客服
        loan_phone_service = (RelativeLayout) findViewById(R.id.loan_phone_service);
        loan_phone_service.setOnClickListener(this);
        //立即申请按钮
        loan_button = (Button) findViewById(R.id.loan_button);
        loan_button.setOnClickListener(this);
    }

    /****************************** 点击事件 **************************************/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //我能贷多少？
            case R.id.loan_amount:
                UmengUtil.eventById(Activity_Loan_Introduction.this, R.string.loan_info);
                Activity_Browser.launche(Activity_Loan_Introduction.this, "我能贷多少", UrlsOne.LoanCalculated);
                break;

            //利息怎么算？
            case R.id.loan_interest:
                UmengUtil.eventById(Activity_Loan_Introduction.this, R.string.loan_info2);
                Activity_Browser.launche(Activity_Loan_Introduction.this, "利息怎么算", UrlsOne.InterestCalculated);
                break;

            //如何还款？
            case R.id.loan_repayment:
                UmengUtil.eventById(Activity_Loan_Introduction.this, R.string.loan_info3);
                Activity_Browser.launche(Activity_Loan_Introduction.this, "如何还款", UrlsOne.How2Repay);
                break;

            //准备哪些材料?
            case R.id.loan_material:
                UmengUtil.eventById(Activity_Loan_Introduction.this, R.string.loan_info4);
                Activity_Browser.launche(Activity_Loan_Introduction.this, "准备材料", UrlsOne.LoanMaterial);
                break;

            //立即申请借款
            case R.id.loan_button:
                loanButtonClick();
                break;

            //咨询在线客服
            case R.id.loan_customer_service:
                String userId = null;
                if (App.mUserDetailInfo != null) {
                    userId = App.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(this).showCustomer(userId);
                break;

            //电话咨询
            case R.id.loan_phone_service:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:020-38476886"));
                startActivity(intent);
                break;
        }
    }

    /************************* 点击立即借款按钮事件 *********************************/
    private void loanButtonClick() {
        UmengUtil.eventById(Activity_Loan_Introduction.this, R.string.loan_act);
        if (!certificateStatus()){
            popAlertDialog();
            return;
        }

        //获取借款验证的信息
        getLoanCertification();
    }

    /***************************** 判断是否认证 ************************************/
    private boolean certificateStatus() {
        return App.mUserDetailInfo.isHasCert();
    }

    /****************************** 弹出对话框 *************************************/
    private void popAlertDialog() {
        dialogWidget = new DialogWidget(Activity_Loan_Introduction.this, IdentifyAlertView.getInstance(Activity_Loan_Introduction.this, new IdentifyAlertView.OnClikListener() {
            @Override
            public void onCancelClick() {
                dialogWidget.dismiss();
                dialogWidget = null;
            }

            @Override
            public void onSureClick() {
                dialogWidget.dismiss();
                dialogWidget = null;
                //去认证
                gotoAuthenticationActivity();
            }
        }).getView());
        dialogWidget.show();
    }


    /**************************** 跳转到认证界面 ************************************/
    private void gotoAuthenticationActivity() {
        Intent newIntent = new Intent(Activity_Loan_Introduction.this, Activity_Authentication.class);
        startActivityForResult(newIntent, 10);
    }

    /************************ 跳转到加入公司的列表界面 ********************************/
    private void gotoJoinCompanyPage() {
        Intent newIntent = new Intent(Activity_Loan_Introduction.this, Activity_Join_Company.class);
        startActivityForResult(newIntent, 100);
    }

    /****************************** 获取借款验证 ************************************/
    private void getLoanCertification() {
        showProgressBar();
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.LoanCertification, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                try {
                    if(null == response) {
                        ToastUtil.alert(Activity_Loan_Introduction.this, "服务器返回数据为空，无法验证");
                        return;
                    }
                    if (response.getInt("status") != 1) {
                        String message = response.getString("message");
                        //出错信息不为空时，将出错信息打印出来，否则将出错信息设置为"未知错误"
                        if (message != null && !message.equalsIgnoreCase("null") && !message.equalsIgnoreCase("")) {
                            ToastUtil.alert(Activity_Loan_Introduction.this, message);
                            //判断是否Token过期，如果过期则跳转至登陆界面
                            if (response.getInt("status") == -5) {
                                Activity_Login.launche(Activity_Loan_Introduction.this);
                                finish();
                            }
                        } else {
                            ToastUtil.alert(Activity_Loan_Introduction.this, "未知错误，请与工作人员联系");
                        }
                    } else {
                        LoanRequestInfoBean loanRequestInfoBean = App.mGson.fromJson(response.getString("result"), LoanRequestInfoBean.class);
                        UmengUtil.eventById(Activity_Loan_Introduction.this, R.string.loan_blank);
                        //跳转借款页面
                        Intent intent = new Intent(Activity_Loan_Introduction.this, Activity_LoanRequest_Borrow.class);
                        intent.putExtra("loanRequestInfoBean", loanRequestInfoBean);
                        intent.putExtra("loanRequestInfoString", response.getString("result"));
                        startActivity(intent);
                        finish();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    ToastUtil.alert(Activity_Loan_Introduction.this, "解析数据出错");
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                hideProgressBar();
                ToastUtil.alert(Activity_Loan_Introduction.this, "网络异常，请稍后重试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.BCB_LOAN_CERTIFICATION_REQUEST);
        requestQueue.add(jsonRequest);
    }

    /******************************* 转圈提示 ***************************************/
    private void showProgressBar(){
        if(null == progressDialog) {
            progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
        }
        progressDialog.setMessage("正在验证借款信息....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void hideProgressBar(){
        if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }



    /************************** 点击系统返回按钮销毁页面 ******************************/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.BCB_LOAN_CERTIFICATION_REQUEST);
    }
}