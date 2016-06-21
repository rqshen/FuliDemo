package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.TextUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.model.IModel_UserAccount;
import com.bcb.presentation.model.IModel_UserAccountImpl;
import com.bcb.presentation.presenter.IPresenter_AccountSetting;
import com.bcb.presentation.presenter.IPresenter_AccountSettingImpl;
import com.bcb.presentation.view.activity_interface.Interface_AccountSetting;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.IdentifyAlertView;

import de.greenrobot.event.EventBus;

public class Activity_Account_Setting extends Activity_Base implements OnClickListener, Interface_AccountSetting {
	private RelativeLayout layout_username, layout_id_card, layout_bank_card, layout_phone;
	private RelativeLayout layout_pay_pwd, layout_login_pwd, layout_foget_passwd, layout_logout, layout_update, layout_feedback, layout_guide, layout_aboutus;

	private TextView username_text, id_card_text, bank_card_text, pay_pwd_text, phone_text;
	private TextView pay_pwd_status;

    private LinearLayout layout_name,layout_idcard, layout_bankcard;


	private Receiver mReceiver;

    //手势密码
    private Switch switch_gesture;
    private boolean isFirstCreate = true;

	//所在公司
	private RelativeLayout layout_company;
	private TextView text_company;
	private ImageView company_arrow;

	//APP的版本号
	private TextView version;

	//对话框
	private DialogWidget certDialog;
	private AlertView alertView;

	//转圈提示
	private ProgressDialog progressDialog;

    //Presenter
    private IPresenter_AccountSetting iPresenterAccountSetting;
    //Model
    private IModel_UserAccount iModelUserAccount;

	public static void launche(Context ctx) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Account_Setting.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_account_setting);
		setLeftTitleVisible(true);
		setTitleValue("账户设置");
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Account_Setting.this);
        iPresenterAccountSetting = new IPresenter_AccountSettingImpl(this, this);
        iModelUserAccount = new IModel_UserAccountImpl();
		IntentFilter intentFilter = new IntentFilter("com.bcb.logout");
		mReceiver = new Receiver();
		registerReceiver(mReceiver, intentFilter);
		//更新设置密码的广播
		IntentFilter settedPasswd = new IntentFilter("com.bcb.passwd.setted");
		registerReceiver(mReceiver, settedPasswd);
		//初始化界面
		initView();
		//获取用户信息
		updateUserData();
	}

    private void initView() {
		//用户名
		layout_username = (RelativeLayout) findViewById(R.id.layout_username);
        layout_name = (LinearLayout) findViewById(R.id.layout_name);
		username_text = (TextView) findViewById(R.id.username_text);
		//身份证号
		layout_id_card = (RelativeLayout) findViewById(R.id.layout_id_card);
        layout_idcard = (LinearLayout) findViewById(R.id.layout_idcard);
		id_card_text = (TextView) findViewById(R.id.id_card_text);
		//银行卡号
		layout_bank_card = (RelativeLayout) findViewById(R.id.layout_bank_card);
        layout_bankcard = (LinearLayout) findViewById(R.id.layout_bankcard);
		bank_card_text = (TextView) findViewById(R.id.bank_card_text);
		//手机号
		layout_phone = (RelativeLayout) findViewById(R.id.layout_phone);
		layout_phone.setOnClickListener(this);
		phone_text = (TextView) findViewById(R.id.phone_text);
		//所在公司
		layout_company = (RelativeLayout) findViewById(R.id.layout_company);
		layout_company.setOnClickListener(this);
        text_company = (TextView) findViewById(R.id.text_company);
		company_arrow = (ImageView) findViewById(R.id.company_arrow);
		company_arrow.setVisibility(View.VISIBLE);

		//交易密码
		layout_pay_pwd = (RelativeLayout) findViewById(R.id.layout_pay_pwd);
		layout_pay_pwd.setOnClickListener(this);
		pay_pwd_text = (TextView) findViewById(R.id.pay_pwd_text);
		pay_pwd_status = (TextView) findViewById(R.id.pay_pwd_status);
		//登陆密码
		layout_login_pwd = (RelativeLayout) findViewById(R.id.layout_login_pwd);
		layout_login_pwd.setOnClickListener(this);

        //手势密码
        switch_gesture = (Switch) findViewById(R.id.switch_gesture);
        //如果手势密码不为空，则设置为打开状态
        switch_gesture.setChecked(!App.saveUserInfo.getGesturePassword().isEmpty());
        switch_gesture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //选中状态
                if (isChecked) {
                    //判断是否第一次创建的
                    if (isFirstCreate) {
                        return;
                    }
                    //去设置手势密码
                    settingGesturePassword();
                    switch_gesture.setChecked(false);
                } else {
                    clearGesturePassword();
                }
            }
        });

        isFirstCreate = false;
        //找回交易密码
        layout_foget_passwd = (RelativeLayout) findViewById(R.id.layout_foget_passwd);
        layout_foget_passwd.setOnClickListener(this);
		//检查升级
		layout_update = (RelativeLayout) findViewById(R.id.layout_update);
		layout_update.setOnClickListener(this);
		layout_update.setVisibility(View.GONE);
		//退出登录
		layout_logout = (RelativeLayout) findViewById(R.id.layout_logout);
		layout_logout.setOnClickListener(this);
		//用户反馈
		layout_feedback = (RelativeLayout) findViewById(R.id.layout_feedback);
		layout_feedback.setOnClickListener(this);
		//引导页
		layout_guide = (RelativeLayout) findViewById(R.id.layout_guide);
		layout_guide.setOnClickListener(this);
		//关于我们
		layout_aboutus = (RelativeLayout) findViewById(R.id.layout_aboutus);
		layout_aboutus.setOnClickListener(this);

		//APP的版本
		version = (TextView) findViewById(R.id.version);
		try {
			String pkName = this.getPackageName();
			String versionName = this.getPackageManager().getPackageInfo(pkName, 0).versionName;
			version.setText("当前版本v" + versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

    //设置认证状态
	private void certificateStatus(boolean status) {
		if (status) {
			//用户姓名
			username_text.setVisibility(View.VISIBLE);
            layout_name.setVisibility(View.GONE);
			//身份证号
			id_card_text.setVisibility(View.VISIBLE);
            layout_idcard.setVisibility(View.GONE);
			//银行卡号
			bank_card_text.setVisibility(View.VISIBLE);
            layout_bankcard.setVisibility(View.GONE);
		} else {
			//用户姓名
			layout_username.setOnClickListener(this);
            layout_name.setVisibility(View.VISIBLE);
			username_text.setVisibility(View.GONE);
			//身份证号
			layout_id_card.setOnClickListener(this);
            layout_idcard.setVisibility(View.VISIBLE);
			id_card_text.setVisibility(View.GONE);
			//银行卡号
			layout_bank_card.setOnClickListener(this);
            layout_bankcard.setVisibility(View.VISIBLE);
			bank_card_text.setVisibility(View.GONE);
		}
	}

    //获取数据
	private void updateUserData() {
        showProgressBar();
        //获取用户数据
        iPresenterAccountSetting.updateUserInfo(1);
	}


	/*********************** 点击事件及响应 ***********************************/
	@Override
	public void onClick(View v) {
        switch (v.getId()) {

            //设置点击用户名、身份证、银行卡都跳转到认证界面
            case R.id.layout_username:
            case R.id.layout_id_card:
            case R.id.layout_bank_card:
                authentication();
                break;

            //手机号码
            case R.id.layout_phone:
                changePhoneNumber();
                break;

			//所在公司(加入公司)
			case R.id.layout_company:
				joinCompany();
				break;

            //修改支付密码
            case R.id.layout_pay_pwd:
                changeTragePasswd();
                break;

            //修改登录密码
            case R.id.layout_login_pwd:
                changeLoginPasswd();
                break;

            //找回交易密码
            case R.id.layout_foget_passwd:
                findPayPassword();
                break;

            //退出登录
            case R.id.layout_logout:
                logout();
                break;

            //更新APP
            case R.id.layout_update:
                updateApp();
                break;

            case R.id.layout_feedback:
                UmengUtil.feedback(Activity_Account_Setting.this);
                break;

            //导航页
            case R.id.layout_guide:
                firstGuide();
                break;

            //关于本公司
            case R.id.layout_aboutus:
                aboutCompany();
                break;

            default:
                break;

        }

    }

    //认证
    private void authentication() {
        Activity_Authentication.launche(Activity_Account_Setting.this);
    }

    //修改手机号
    private void changePhoneNumber() {
        ToastUtil.alert(Activity_Account_Setting.this, "手机号码请到电脑版账户中心修改");
    }

	//加入公司
	private void joinCompany() {
        UmengUtil.eventById(Activity_Account_Setting.this, R.string.self_auth_c2);
		//如果未认证，则去认证
		if (App.mUserDetailInfo == null || App.mUserDetailInfo.HasCert == false) {
			popCertDialog();
		}
		//否则去选择公司
		else {
            if (App.mUserDetailInfo.MyCompany == null) {
                Activity_Join_Company.launche(Activity_Account_Setting.this);
            } else if (App.mUserDetailInfo.MyCompany.Status == 5) {
                companyDialog();
            } else if (App.mUserDetailInfo.MyCompany.Status == 10) {
                changeCompany();
            }
		}
	}


	/************************ 去认证 ******************************/
	private void popCertDialog() {
        certDialog = new DialogWidget(Activity_Account_Setting.this, IdentifyAlertView.getInstance(Activity_Account_Setting.this, new IdentifyAlertView.OnClikListener() {
            @Override
            public void onCancelClick() {
                certDialog.dismiss();
                certDialog = null;
            }

            @Override
            public void onSureClick() {
                certDialog.dismiss();
                certDialog = null;
                //去认证
                gotoAuthenticationActivity();
            }
        }).getView());
        certDialog.show();
	}

	//跳转到认证界面
	private void gotoAuthenticationActivity() {
		Intent newIntent = new Intent(Activity_Account_Setting.this, Activity_Authentication.class);
		startActivityForResult(newIntent, 10);
	}

    /*************************** 审核中 *************************/
    private void companyDialog() {
        AlertView.Builder ibuilder = new AlertView.Builder(this);
        ibuilder.setTitle("您的认证申请正在审核");
        ibuilder.setMessage("预计2个工作日内完成");
        ibuilder.setPositiveButton("知道了", null);
        alertView = ibuilder.create();
        alertView.show();
    }

    /*************************** 审核通过去更改公司 *************************/
    private void changeCompany() {
		showAlertView("提示", "您需要修改公司认证信息吗?", "立即修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertView.dismiss();
                alertView = null;
                Activity_Join_Company.launche(Activity_Account_Setting.this);
                finish();
            }
        });
    }


    //修改交易密码
    private void changeTragePasswd() {
        if (!iModelUserAccount.isUserDetailInfoEmpty()) {
            if (iModelUserAccount.hasTradePassword()) {
                Activity_Modify_Pwd.launche(Activity_Account_Setting.this, false);
            } else {
                Activity_Setting_Pay_Pwd.launche(Activity_Account_Setting.this);
            }
        } else {
            Activity_Setting_Pay_Pwd.launche(Activity_Account_Setting.this);
        }
    }

    //修改登录密码
    private void changeLoginPasswd() {
        Activity_Modify_Pwd.launche(Activity_Account_Setting.this, true);
    }

    //找回交易密码
    private void findPayPassword() {
        UmengUtil.eventById(Activity_Account_Setting.this, R.string.find_f_key);
        Activity_Forget_Pwd.launche(Activity_Account_Setting.this);
        finish();
    }

    //退出登录
    private void logout() {
		showAlertView("提示", "是否退出登录?", "立即退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertView.dismiss();
                alertView = null;
                showProgressBar();
                //退出登录
                iPresenterAccountSetting.updateUserInfo(2);
                App.getInstance().setWelfare("");
                EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.LOGOUT));
            }
        });
    }

    //更新
    private void updateApp() {
        ToastUtil.alert(Activity_Account_Setting.this, "检查更新中");
        UmengUtil.update(Activity_Account_Setting.this);
    }

    //导航页
    private void firstGuide() {
    }

    //公司信息
    private void aboutCompany() {
        Activity_Browser.launche(Activity_Account_Setting.this, "关于福利金融", UrlsOne.AboutConpany);
    }


    /**
     * 用户信息回调
     *
     * @param hasCert           是否认证
     * @param hasTradePassword  是否设置交易密码
     * @param userName          用户名
     * @param IDCard            身份证号
     * @param cardNumber        银行卡
     * @param localPhone        手机号
     * @param companyMessage    加入公司信息
     */
    @Override
    public void onRequestResult(boolean hasCert,
                                boolean hasTradePassword,
                                String userName,
                                String IDCard,
                                String cardNumber,
                                String localPhone,
                                String companyMessage) {
        hideProgressBar();
        //显示认证状态
        certificateStatus(hasCert);
        //设置交易密码状态
        if (hasTradePassword) {
            pay_pwd_text.setText("已设置");
            pay_pwd_status.setText("点击修改");
        } else {
            pay_pwd_text.setText("未设置");
            pay_pwd_status.setText("点击设置");
        }
        //用户姓名
        if (!TextUtils.isEmpty(userName)) {
            username_text.setText(userName);
        }
        //身份证号
        if (!TextUtils.isEmpty(IDCard)) {
            id_card_text.setText(TextUtil.delBankNum(IDCard));
        }
        //银行卡号
        if (!TextUtils.isEmpty(cardNumber)) {
            bank_card_text.setText(TextUtil.delBankNum(cardNumber));
        }
        //手机号
        if (!TextUtils.isEmpty(localPhone)) {
            phone_text.setText(localPhone);
        }
        LogUtil.d("1234", "companyMessage = " + companyMessage);
        text_company.setText(companyMessage);
    }

    @Override
    public void onRequestResult(int resultStatus, String message) {
        hideProgressBar();
        //网络异常
        if (resultStatus == -100) {
            ToastUtil.alert(this, message);
        }
        //退出成功
        else if (resultStatus == 1000 && TextUtils.isEmpty(message)) {
            gotoLoginActivity();
        }
        //如果获取用户信息失败
        else if (resultStatus != 1) {
            ToastUtil.alert(this, TextUtils.isEmpty(message) ? "暂时无法用户信息" : message);
            if (resultStatus == -5) {
                gotoLoginActivity();
            }
        }
    }

    //跳转至登陆界面
    private void gotoLoginActivity() {
        Activity_Login.launche(Activity_Account_Setting.this);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.finishAllActivity();
    }

    //自定义广播
	class Receiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.bcb.logout")) {
				finish();
			} else if (intent.getAction().equals("com.bcb.passwd.setted")) {
                updateUserData();
			}
		}
	}



	//销毁注册广播
	@Override
	protected void onDestroy() {
		super.onDestroy();
        //清空依赖
        if (iPresenterAccountSetting != null) {
            iPresenterAccountSetting.clearDependency();
            iPresenterAccountSetting = null;
        }
        iModelUserAccount = null;
        unregisterReceiver(mReceiver);
	}

    /**
     * 显示转圈提示
     */
	private void showProgressBar() {
		if(null == progressDialog) progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setMessage("正在获取用户信息....");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

    /**
     * 隐藏转圈提示
     */
	private void hideProgressBar() {
		if(null != progressDialog && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}

    /**
     * 设置手势密码
     */
    private void settingGesturePassword() {
        UmengUtil.eventById(Activity_Account_Setting.this, R.string.gesture_on);
        Intent newIntent = new Intent(Activity_Account_Setting.this, Activity_Gesture_Lock.class);
        startActivityForResult(newIntent, 1);
    }


    /**
     * Activity跳转结果回调
     *
     * @param requestCode   请求码
     * @param resultCode    回调码
     * @param data          返回的Intent对象
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //设置手势密码成功返回时
            case 1:
                if (data != null && data.getBooleanExtra("SettingGestureSuccess", false)) {
                    isFirstCreate = true;
                    switch_gesture.setChecked(true);
                    isFirstCreate = false;
                }
                break;

            case 10:
                if (data != null) {
                    switch_gesture.setChecked(false);
                    //清除手势密码
                    App.saveUserInfo.remove(getBaseContext(), "GesturePassword");
                }
                break;
        }
    }

    /**
     * 跳转至清空手势密码
     */
    private void clearGesturePassword() {
        UmengUtil.eventById(Activity_Account_Setting.this, R.string.gusture_off);
        Intent newIntent = new Intent(Activity_Account_Setting.this, Activity_Gesture_Lock.class);
        newIntent.putExtra("isSettingPasswd", false);
        startActivityForResult(newIntent, 10);
    }

    /**
     * 提示框
     * @param title             标题
     * @param contentMessage    内容消息
     * @param rightButtonTitle  右键的标题
     * @param onClickListener   右键监听器
     */
	private void showAlertView(String title,
							   String contentMessage,
							   String rightButtonTitle,
							   DialogInterface.OnClickListener onClickListener) {
		AlertView.Builder ibuilder = new AlertView.Builder(this);
		ibuilder.setTitle(title);
		ibuilder.setMessage(contentMessage);
		ibuilder.setNegativeButton("取消", null);
		ibuilder.setPositiveButton(rightButtonTitle, onClickListener);
		alertView = ibuilder.create();
		alertView.show();
	}
}
