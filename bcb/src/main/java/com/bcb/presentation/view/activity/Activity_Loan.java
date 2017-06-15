package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.module.discover.eliteloan.loanlist.LoanListActivity;
import com.bcb.util.MyActivityManager;
import com.bcb.util.UmengUtil;

/**
 * Created by cain on 15/12/22.
 */

public class Activity_Loan extends Activity_Base implements View.OnClickListener {
    private static final String TAG = "Activity_Loan";

    //人人买房福利
    private LinearLayout houseButton;
    private TextView loan_house_titleview;
    //乐享生活福利
    private LinearLayout liveButton;
    private TextView loan_live_titleview;
    //及时雨福利
    private LinearLayout timeButton;
    private TextView loan_time_titleview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Loan.this);
        setBaseContentView(R.layout.activity_loan);
        setLeftTitleVisible(true);
        setTitleValue("我要借款");
        setRightTitleValue("我的借款", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoListPage();
                finish();
            }
        });
        //人人买房福利
        houseButton = (LinearLayout) findViewById(R.id.house_request);
        houseButton.setOnClickListener(this);
        loan_house_titleview = (TextView) findViewById(R.id.loan_house_titleview);
        //乐享生活福利
        liveButton = (LinearLayout) findViewById(R.id.live_request);
        liveButton.setOnClickListener(this);
        loan_live_titleview = (TextView) findViewById(R.id.loan_live_titleview);
        //及时雨福利
        timeButton = (LinearLayout) findViewById(R.id.time_request);
        timeButton.setOnClickListener(this);
        loan_time_titleview = (TextView) findViewById(R.id.loan_time_titleview);
    }

    //借款列表页
    private void gotoListPage() {
        UmengUtil.eventById(Activity_Loan.this, R.string.loan_my);
        Intent listIntent = new Intent(Activity_Loan.this, LoanListActivity.class);
        startActivity(listIntent);
    }

    //跳转至借款介绍页面
    private void gotoIntroductionPage(String title, String description) {
        Activity_Loan_Introduction.launche(Activity_Loan.this, title, description);
    }

    //点击返回键，销毁页面
    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //人人买房福利
            case R.id.house_request:
                UmengUtil.eventById(Activity_Loan.this, R.string.loan_house);
                gotoIntroductionPage(loan_house_titleview.getText().toString(), "仅限购房借款，方便您安家立业。");
                finish();
                break;

            //乐享生活福利
            case R.id.live_request:
                UmengUtil.eventById(Activity_Loan.this, R.string.loan_life);
                gotoIntroductionPage(loan_live_titleview.getText().toString(), "适用于买车、置业、装修、教育支出等，提高您的生活品质。");
                finish();
                break;

            //及时雨福利
            case R.id.time_request:
                UmengUtil.eventById(Activity_Loan.this, R.string.loan_urgent);
                gotoIntroductionPage(loan_time_titleview.getText().toString(), "适用于还信用卡、临时缴费等短期资金周转，解决您的不时之需。");
                finish();
                break;

            default:
                break;
        }
    }
}
