package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.GesturePatternLock.View.ContentView;
import com.bcb.presentation.view.custom.GesturePatternLock.View.Drawl;

/**
 * Created by cain on 16/3/2.
 */
public class Activity_Gesture_Lock extends Activity_Base {
    private FrameLayout body_layout;
    private ContentView content;
    private boolean isSettingPasswd = true;
    private TextView phone_description; //设置时的提示和输入手势密码时的手机号码
    private LinearLayout forgetpwd; //忘记密码
    private String password;    //暂存密码

    private AlertView alertView;

    public static void launche(Context context, boolean isSettingPasswd) {
        Intent intent = new Intent();
        intent.setClass(context, Activity_Gesture_Lock.class);
        intent.putExtra("isSettingPasswd", isSettingPasswd);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_lock);
        isSettingPasswd = getIntent().getBooleanExtra("isSettingPasswd", true);
        //清除上一次进入后台的时间
        clearEnterTime();

        phone_description = (TextView) findViewById(R.id.phone_description);
        if (isSettingPasswd) {
            phone_description.setText("绘制解锁图案");
        } else {
            phone_description.setText("请滑动输入密码");
        }

        //忘记密码
        forgetpwd = (LinearLayout) findViewById(R.id.layout_foget);
        //表示设置手势密码
        if (isSettingPasswd) {
            forgetpwd.setVisibility(View.GONE);
        }
        //表示需要输入手势密码
        else {
            forgetpwd.setVisibility(View.VISIBLE);
            forgetpwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UmengUtil.eventById(Activity_Gesture_Lock.this, R.string.gesture_forget);
                    gotoLoginPageView();

                }
            });
        }

        //手势密码的父容器
        body_layout = (FrameLayout) findViewById(R.id.body_layout);

        // 初始化一个显示各个点的viewGroup
        if (isSettingPasswd) {
            //设置手势密码时的构造器
            content = new ContentView(this, new Drawl.GestureCallBack() {
                @Override
                public void checkedSuccess() {
                    if (isSettingPasswd) {
                        Toast.makeText(Activity_Gesture_Lock.this, "密码设置成功!", Toast.LENGTH_SHORT).show();
                        isSettingPasswd = false;
                        App.saveUserInfo.setGesturePassword(password);
                    }
                    Intent intent = new Intent();
                    intent.putExtra("SettingGestureSuccess", true);
                    setResult(1, intent);
                    finish();
                }

                @Override
                public void checkedFail() {
                    if (isSettingPasswd) {
                        phone_description.setTextColor(getResources().getColor(R.color.red));
                        phone_description.setText("与上次绘制不一致，请重新绘制");
                    }
                }

                @Override
                public void settingPasswdSuccess(StringBuilder stringBuilder, boolean settingPasswdStatus) {
                    if (settingPasswdStatus) {
                        isSettingPasswd = settingPasswdStatus;
                        phone_description.setTextColor(getResources().getColor(R.color.txt_gray));
                        phone_description.setText("再次绘制解锁图案");
                        password = stringBuilder.toString();
                    }
                }
            });
        } else {
            //输入手势密码的构造器
            content = new ContentView(this, App.saveUserInfo.getGesturePassword(), new Drawl.GestureCallBack() {
                @Override
                public void checkedSuccess() {
                    //清除上一次退到后台时保存的时间
                    clearEnterTime();
                    finish();
                }

                @Override
                public void checkedFail() {
                    phone_description.setTextColor(getResources().getColor(R.color.red));
                    phone_description.setText("输入密码错误");
                    clearEnterTime();
                }

                @Override
                public void settingPasswdSuccess(StringBuilder stringBuilder, boolean settingPasswdStatus) {
                    if (!settingPasswdStatus) {
                        phone_description.setTextColor(getResources().getColor(R.color.red));
                        phone_description.setText("请连接至少4个点");
                    }
                }
            });
        }

        isSettingPasswd = false;
        //设置手势解锁显示到哪个布局里面
        content.setParentView(body_layout);
    }

    @Override
    public void onBackPressed() {
    }

    //退出登录
    private void gotoLoginPageView() {
        AlertView.Builder ibuilder = new AlertView.Builder(this);
        ibuilder.setTitle("提示");
        ibuilder.setNegativeButton("取消", null);
        ibuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertView.dismiss();
                alertView = null;
                MyActivityManager myActivityManager = MyActivityManager.getInstance();
                myActivityManager.finishAllActivity();
                /* 清空当前用户的信息 */
                App.saveUserInfo.clear();
                App.mUserWallet = null;
                App.mUserDetailInfo = null;
                App.viewJoinBanner = true;
                Activity_Login.launche(Activity_Gesture_Lock.this);
                sendBroadcast(new Intent("com.bcb.logout.success"));
                //销毁当前页面
                finish();
            }
        });
        alertView = ibuilder.create();
        alertView.show();
    }

}