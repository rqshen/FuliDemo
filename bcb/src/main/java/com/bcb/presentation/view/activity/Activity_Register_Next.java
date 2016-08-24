package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.RegexManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.presenter.IPresenter_Register;
import com.bcb.presentation.presenter.IPresenter_RegisterImpl;
import com.bcb.presentation.view.activity_interface.Interface_Verification;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_Register_Next extends Activity_Base implements Interface_Verification, View.OnClickListener {

    private static final String TAG = "Activity_Register_Next";

    private EditText userpwd, userpwdconfirm, regservicecode;
    private Button send, next;
    private String phonenum;
    private int time;
    private Timer timer;
    private TextView errortips_passwd, errortips_confirm;

    //显示密码强度
    private TextView strength1, strength2, strength3;

    //转圈提示
    private ProgressDialog mProgressBar;

    //Presenter
    private IPresenter_Register iPresenterRegister;

    //客服
    private TextView customer_service;
    //是否成功获取了验证码
//    private boolean isHasGetVerification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Register_Next.this);
        setBaseContentView(R.layout.activity_register_next);
        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.eventById(Activity_Register_Next.this, R.string.reg_back2);
                finish();
            }
        });
        setTitleValue("注册");
        //创建Presenter
        iPresenterRegister = new IPresenter_RegisterImpl(Activity_Register_Next.this, this);
        init();
        //进入登陆密码设置界面
        UmengUtil.eventById(this, R.string.reg_input_sec);
    }

    private void init() {
        userpwd = (EditText) findViewById(R.id.userpwd);
        userpwdconfirm = (EditText) findViewById(R.id.userpwdconfirm);
        errortips_passwd = (TextView) findViewById(R.id.errortips_passwd);
        errortips_confirm = (TextView) findViewById(R.id.errortips_confirm);
        regservicecode = (EditText) findViewById(R.id.regservicecode);
        customer_service = (TextView) findViewById(R.id.customer_service);
        customer_service.setOnClickListener(this);
        strength1 = (TextView) findViewById(R.id.strength1);
        strength2 = (TextView) findViewById(R.id.strength2);
        strength3 = (TextView) findViewById(R.id.strength3);

        send = (Button) findViewById(R.id.send);
        next = (Button) findViewById(R.id.button_confirm);

        send.setOnClickListener(this);
        next.setOnClickListener(this);
        //获取到的手机号码
        phonenum = getIntent().getStringExtra("phone");

        //用户密码
        userpwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 判断密码是否规范
                if (!RegexManager.isPasswordNum(userpwd.getText().toString())) {
                    errortips_passwd.setVisibility(View.VISIBLE);
                    // 判断是否包含非法字符
                    if (!RegexManager.isRightCode(userpwd.getText().toString())) {
                        errortips_passwd.setText("输入的密码包含非法字符");
                    } else {
                        errortips_passwd.setText("请输入8-15位的密码");
                    }
                } else {
                    errortips_passwd.setVisibility(View.GONE);
                    errortips_passwd.setText("");
                }
                errortips_confirm.setVisibility(View.GONE);
                errortips_confirm.setText("");

                //不能输入中文
                String editable = userpwd.getText().toString();
                String str = RegexManager.stringFilter(editable.toString());
                if (!editable.equals(str)) {
                    userpwd.setText(str);
                    // 设置新的光标所在位置
                    userpwd.setSelection(str.length());
                }

                //如果一开始输入的是中文，用空来替换，这时候密码强度不应该显示为弱
                if (str.length() == 0) {
                    setupPasswordColor();
                }
                //长度小于8的时候，密码为弱
                else if (str.length() < 8) {
                    setupPasswordColor();
                    strength1.setBackgroundResource(R.drawable.button_solid_red);

                } else {
                    /**
                     * 一、关于密码强度的设定：
                     密码包含的元素有四种：数字、符号、大写字母、小写字母
                     1、密码强度显示为弱：密码中只出现一种元素
                     2、密码强度显示为中：密码中只出现两种元素
                     3、密码强度显示为强：密码中出现三种或三种以上的元素
                     * 日期：2016/8/3 18:34
                     */
                    int num = RegexManager.getMatchNumber(str);
                    if (num <= 1) {
                        setupPasswordColor();
                        strength1.setBackgroundResource(R.drawable.button_solid_red);
                    }
                    if (num == 2) {
                        setupPasswordColor();
                        strength2.setBackgroundResource(R.drawable.button_solid_blue);
                    }
                    if (num >= 3) {
                        setupPasswordColor();
                        strength3.setBackgroundResource(R.drawable.button_solid_green);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            //默认为灰色
            private void setupPasswordColor() {
                strength1.setBackgroundResource(R.drawable.button_solid_gray);
                strength2.setBackgroundResource(R.drawable.button_solid_gray);
                strength3.setBackgroundResource(R.drawable.button_solid_gray);
            }
        });

        userpwdconfirm.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 判断密码是否规范
                if (!RegexManager.isPasswordNum(userpwdconfirm.getText().toString())) {
                    errortips_passwd.setVisibility(View.VISIBLE);
                    // 判断是否包含非法字符
                    if (!RegexManager.isRightCode(userpwdconfirm.getText().toString())) {
                        errortips_passwd.setText("输入密码包含非法字符");
                    } else {
                        errortips_passwd.setText("请输入8-15位的密码");
                    }
                } else {
                    errortips_passwd.setVisibility(View.GONE);
                    errortips_passwd.setText("");
                    if (!userpwd.getText().toString().equals(userpwdconfirm.getText().toString())) {
                        errortips_passwd.setVisibility(View.VISIBLE);
                        errortips_passwd.setText("两次密码输入不一致");
                    }
                }
                errortips_confirm.setVisibility(View.GONE);
                errortips_confirm.setText("");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        regservicecode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!RegexManager.isResizngCode(regservicecode.getText().toString())) {
                    errortips_confirm.setVisibility(View.VISIBLE);
                    errortips_confirm.setText("请输入6位验证码");
                } else {
                    errortips_confirm.setVisibility(View.GONE);
                    errortips_confirm.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //检测密码
    private boolean toGoBeforeCheck() {
        if (!RegexManager.isPasswordNum(userpwdconfirm.getText().toString())) {
            //如果输入密码位数不对
            if (userpwd.getText().toString().length() < 8 || userpwd.getText().toString().length() > 15) {
                userpwd.requestFocus();
                userpwd.setSelection(userpwd.getText().toString().length());
                errortips_passwd.setVisibility(View.VISIBLE);
                errortips_passwd.setText("请输入8-15位的密码");
                return false;
            } else {
                if (!RegexManager.isResizngCode(userpwdconfirm.getText().toString())) {
                    errortips_passwd.setVisibility(View.VISIBLE);
                    errortips_passwd.setText("输入密码包含非法字符");
                    return false;
                }
            }

            if (userpwdconfirm.getText().toString().length() < 8 || userpwdconfirm.getText().toString().length() > 15) {
                userpwdconfirm.requestFocus();
                userpwdconfirm.setSelection(userpwdconfirm.getText().toString().length());
                errortips_passwd.setVisibility(View.VISIBLE);
                errortips_passwd.setText("请输入8-15位的确认密码");
                return false;
            } else {
                if (!RegexManager.isResizngCode(userpwdconfirm.getText().toString())) {
                    errortips_passwd.setVisibility(View.VISIBLE);
                    errortips_passwd.setText("输入密码包含非法字符");
                    return false;
                }
            }
            return false;
        } else {
            errortips_passwd.setVisibility(View.GONE);
            errortips_passwd.setText("");
            errortips_confirm.setVisibility(View.GONE);
            errortips_confirm.setText("");
        }

        //判断输入密码是否一致
        if (!judgePwdSame()) {
            errortips_passwd.setVisibility(View.VISIBLE);
            errortips_passwd.setText("两次密码输入不一致");
            return false;
        } else {
            errortips_passwd.setVisibility(View.GONE);
            errortips_passwd.setText("");
            errortips_confirm.setVisibility(View.GONE);
            errortips_confirm.setText("");
        }
        return true;
    }

    //判断两次输入的密码是否相同
    private boolean judgePwdSame() {
        String inputpwd = userpwd.getText().toString();
        String confirmpwd = userpwdconfirm.getText().toString();
        if (!inputpwd.equals(confirmpwd)) {
            return false;
        }
        return true;
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    send.setText(time + "S 后可再次发送");
                    send.setBackgroundResource(R.drawable.button_gray);
                    break;

                case 2:
                    // 设置获取验证码按钮为可以点击
                    send.setEnabled(true);
                    send.setBackgroundResource(R.drawable.request_code_selector);
                    send.setText("重新发送");
                    break;
            }
        }

        ;
    };

    //定时器
    private void setTimer() {
        time = 60;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                time--;
                if (time > 0) {
                    handler.sendEmptyMessage(1);
                } else {
                    handler.sendEmptyMessage(2);
                    stopTimer();
                }
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //获取验证码
    private void getVerificationCode() {
        showProgressBar("正在获取验证码...");
        iPresenterRegister.getVerificaionCode(phonenum);
    }

    //注册的
    private void toNext() {
        String inputPwd = userpwd.getText().toString().trim();
        String inputCode = regservicecode.getText().toString().trim();
        showProgressBar("正在注册...");
        iPresenterRegister.doRegister(phonenum, inputPwd, inputCode);
    }

    //暂存手机号码
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //保存要注册的手机号
        savedInstanceState.putString("phonenum", phonenum);
        super.onSaveInstanceState(savedInstanceState);
    }

    //取出暂存的手机号码
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        phonenum = savedInstanceState.getString("phonenum");
    }


    //显示进度
    private void showProgressBar(String title) {
        if (null == mProgressBar) {
            mProgressBar = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        }
        mProgressBar.setMessage(title);
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setCancelable(true);
        mProgressBar.show();
    }

    //隐藏进度
    private void hideProgressBar() {
        if (!isFinishing() && null != mProgressBar && mProgressBar.isShowing()) {
            mProgressBar.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UmengUtil.eventById(Activity_Register_Next.this, R.string.reg_back2);
        iPresenterRegister.clearDependency();
        iPresenterRegister = null;
        finish();
    }

    //将登陆成功的信息发送出去
    private void sendBroardCast() {
        Intent intent = new Intent();
        intent.setAction("com.bcb.register.success");
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发送验证码
            case R.id.send:
                //检查是否输入了密码
                if (toGoBeforeCheck()) {
                    // 设置获取验证码按钮为不可点击，防止获取多条验证码
                    send.setEnabled(false);
                    send.setBackgroundResource(R.drawable.button_shape_unenabled);
                    //发送验证码
                    UmengUtil.eventById(Activity_Register_Next.this, R.string.captcha_sent);
                    getVerificationCode();
                }
                break;

            case R.id.button_confirm:
                UmengUtil.eventById(Activity_Register_Next.this, R.string.reg3);
                //检查密码是否正确
                if (userpwdconfirm.getText().toString().length() < 8) {
                    errortips_confirm.setVisibility(View.VISIBLE);
                    errortips_confirm.setText("密码长度不够，请重新输入");
                    userpwd.requestFocus();
                    userpwd.setSelection(userpwd.getText().toString().length());
                    break;
                }
                if (!toGoBeforeCheck()) {
                    //输入密码不一致
                    if (!judgePwdSame() && userpwdconfirm.getText().toString().length() > 0) {
                        errortips_confirm.setVisibility(View.VISIBLE);
                        errortips_confirm.setText("两次密码输入不一致");
                        userpwd.requestFocus();
                        userpwd.setSelection(userpwd.getText().toString().length());
                        break;
                    }
                    break;
                }
                //没有点击获取验证码
//                if (!isHasGetVerification) {
//                    Toast.makeText(Activity_Register_Next.this, "请先获取验证码", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (regservicecode.getText().toString().length() != 6) {
                    errortips_confirm.setVisibility(View.VISIBLE);
                    errortips_confirm.setText("请输入6位验证码");
                    if (regservicecode.getText().toString().length() != 6) {
                        regservicecode.requestFocus();
                        regservicecode.setSelection(regservicecode.getText().toString().length());
                    }
                    break;
                } else {
                    errortips_confirm.setVisibility(View.GONE);
                    errortips_confirm.setText("");
                }
                toNext();
                break;

            //客服
            case R.id.customer_service:
                MQCustomerManager.getInstance(this).showCustomer(null);
                break;
        }
    }


    //获取验证码回调
    @Override
    public void getVerificationResult(int resultStatus, String message) {
        //无论成功或失败，都要开启倒数计时并且要隐藏隐藏转圈
        hideProgressBar();
        //网络异常
        if (resultStatus == -100) {
            ToastUtil.alert(Activity_Register_Next.this, message);
            // 设置获取验证码按钮为可以点击
            send.setEnabled(true);
            send.setBackgroundResource(R.drawable.request_code_selector);
        }
        //成功
        else {
            //启动定时器
            setTimer();
            if (resultStatus == 1) {
                ToastUtil.alert(Activity_Register_Next.this, " 验证码已发送至 " + phonenum);
//                isHasGetVerification = true;
            } else {
                errortips_confirm.setVisibility(View.VISIBLE);
                errortips_confirm.setText(message);
            }
        }
    }

    @Override
    public void onRequestResult(int resultStatus, String message) {
        //不管成功或者失败，都要隐藏转圈
        LogUtil.i("bqt", "注册后返回码：" + resultStatus);
        hideProgressBar();
        //成功状态
        if (resultStatus == 1) {
            //校验验证码成功
            UmengUtil.eventById(Activity_Register_Next.this, R.string.captcha_succ);
            //注册成功
            UmengUtil.eventById(Activity_Register_Next.this, R.string.reg_success);
            // 销毁所有页面，回到首页
            sendBroardCast();
//            MyActivityManager.getInstance().finishAllActivity();
            startActivity(new Intent(Activity_Register_Next.this, Activity_Main.class));
        }
        //出错信息
        else {
            //出错信息为空的时候
            if (message.isEmpty()) {
                ToastUtil.alert(Activity_Register_Next.this, "注册失败，请联系客服人员咨询");
            } else {
                ToastUtil.alert(Activity_Register_Next.this, message);
            }
        }
    }
}
