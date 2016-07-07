package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bcb.R;
import com.bcb.data.util.MyActivityManager;

/**
 * Created by Ray on 2016/7/7.
 * @desc 特权本金
 */
public class Activity_Privilege_Money extends Activity_Base {


    public static void launch(Context context){
        Intent intent = new Intent(context, Activity_Privilege_Money.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_privilege_money);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager.getInstance().pushOneActivity(Activity_Privilege_Money.this);
        setTitleValue("特权本金");
        setLeftTitleVisible(true);
        setRightTitleValue("兑换", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
