package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.utils.MyActivityManager;

/**
 * Created by cain on 16/1/8.
 */
public class Activity_LoanRequest_Success extends Activity_Base implements View.OnClickListener{

    private ImageView image_view;

    //个人信用报告
    private TextView personal_credit_report;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_LoanRequest_Success.this);
        setBaseContentView(R.layout.activity_loanrequest_success);
        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAllPage();
            }
        });
        setTitleValue("申请成功");

        //个人信用报告
        personal_credit_report = (TextView) findViewById(R.id.personal_credit_report);
        personal_credit_report.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        personal_credit_report.getPaint().setAntiAlias(true);//抗锯齿
        personal_credit_report.setOnClickListener(this);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAllPage();
    }

    //销毁所有页面回到首页
    private void finishAllPage() {
        MyActivityManager.getInstance().finishAllActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //个人信用报告
            case R.id.personal_credit_report:
                try {
                    Uri uri = Uri.parse(personal_credit_report.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
}