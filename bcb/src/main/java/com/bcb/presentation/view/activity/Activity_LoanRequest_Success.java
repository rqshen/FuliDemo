package com.bcb.presentation.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bcb.R;
import com.bcb.data.util.MyActivityManager;

/**
 * Created by cain on 16/1/8.
 */
public class Activity_LoanRequest_Success extends Activity_Base {

    private ImageView image_view;

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
}