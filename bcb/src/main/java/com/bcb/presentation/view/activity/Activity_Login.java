package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.event.BroadcastEvent;
import com.bcb.data.bean.WelfareBean;
import com.bcb.utils.DbUtil;
import com.bcb.utils.LoanPersonalConfigUtil;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MQCustomerManager;
import com.bcb.utils.RegexManager;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.UmengUtil;
import com.bcb.utils.VerificationCode;
import com.bcb.presentation.presenter.IPresenter_Login;
import com.bcb.presentation.presenter.IPresenter_LoginImpl;
import com.bcb.presentation.view.activity_interface.Interface_Base;

import de.greenrobot.event.EventBus;

import static com.bcb.R.id.userpwd;

public class Activity_Login extends Activity_Base implements Interface_Base, OnClickListener {

	private static final String TAG = "Activity_Login";

	private EditText user;
	private EditText pwd;
	private LinearLayout forgetpwd;
	private Button login;

	private RelativeLayout localcode_layout;
	private int errorCount = 0;
	private ImageView vc_image, im_visible;
	private String getCode = "";
	private EditText vc_code;
	private boolean isShow = true;//默认是隐藏
	//客服
	private TextView customer_service;

	//转圈提示
	private ProgressDialog progressDialog;

	//Presenter
	private IPresenter_Login iPresenterLogin;
	
	public static void launche(Context ctx) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Login.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i("bqt", "【Activity_Login】【onCreate】");

		setBaseContentView(R.layout.activity_login);
		setLeftTitleVisible(true);
		setTitleValue("登录");
		setRightTitleValue("联系客服", new OnClickListener() {
			@Override
			public void onClick(View v) {
				MQCustomerManager.getInstance(Activity_Login.this).showCustomer(null);
			}
		});
		init();
		//创建新Presenter 实例
		iPresenterLogin = new IPresenter_LoginImpl(this);
		//清空账户信息
		iPresenterLogin.clearAccount();
		//清空暂存在本地的数据
		(new LoanPersonalConfigUtil(this)).clear();
	}

	private void init() {
		user = (EditText) findViewById(R.id.userphone);
		pwd = (EditText) findViewById(userpwd);
		forgetpwd = (LinearLayout) findViewById(R.id.layout_foget);
		forgetpwd.setOnClickListener(this);
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
		localcode_layout = (RelativeLayout) findViewById(R.id.localcode_layout);
		vc_image = (ImageView) findViewById(R.id.vc_image);
		im_visible = (ImageView) findViewById(R.id.im_visible);
		vc_image.setImageBitmap(VerificationCode.getInstance().getBitmap());
		vc_code = (EditText) findViewById(R.id.vc_code);
		vc_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vc_image.setImageBitmap(VerificationCode.getInstance().getBitmap());
				getCode = VerificationCode.getInstance().getCode();
			}
		});

		pwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String editable = pwd.getText().toString();
				String str = RegexManager.stringFilter(editable.toString());
				if (!editable.equals(str)) {
					pwd.setText(str);
					pwd.setSelection(str.length());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
			                              int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		//客服
		customer_service = (TextView) findViewById(R.id.customer_service);
		customer_service.setOnClickListener(this);
		im_visible.setOnClickListener(this);
	}

	/*********************
	 * 转圈提示
	 **************************/
	//显示转圈提示
	private void showProgressBar() {
		if (null == progressDialog) progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setMessage("正在登录...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	//隐藏转圈提示
	private void hideProgressBar() {
		if (!isFinishing() && null != progressDialog && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	/****************
	 * 登陆成功发送广播
	 *****************/
	private void sendBroardCast() {
		Intent intent = new Intent();
		intent.setAction("com.bcb.login.success");
		sendBroadcast(intent);

		EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.LOGIN));//通知刷新

		//设置极光推送别名
//        MyApplication.getInstance().setAlias();//2016-7-26，服务器未返回任何用户信息，所以后续操作都是不需要的

		//获取数据库缓存数据,若有数据就显示已经缓存的数据,则不去请求
		WelfareBean welfareBean = DbUtil.getWelfare();
		if (null != welfareBean && !TextUtils.isEmpty(welfareBean.getValue())) {
			MyApplication.getInstance().setWelfare(welfareBean.getValue());
		} else {
			MyApplication.getInstance().requestWelfare();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (iPresenterLogin != null) {
			iPresenterLogin.clearDependency();
			iPresenterLogin = null;
		}
	}

	//登陆结果回调
	@Override
	public void onRequestResult(int resultCode, String message) {
		hideProgressBar();
		//根据状态码判断是否存在
		switch (resultCode) {
			//成功
			case 0:
				//将登录成功的信息发送出去
				sendBroardCast();
				Intent intent = new Intent();
				intent.putExtra("loginStatus", true);
				setResult(100, intent);
				//销毁当前页面
				finish();
				break;

			//输入错误
			case 1:
				ToastUtil.alert(Activity_Login.this, message);
				break;

			//登陆失败
			case -1:
				ToastUtil.alert(Activity_Login.this, message);
				UmengUtil.eventById(Activity_Login.this, R.string.login_key_n);
				errorCount++;
				if (errorCount >= 3) {
					localcode_layout.setVisibility(View.VISIBLE);
					vc_image.setImageBitmap(VerificationCode.getInstance().getBitmap());
					getCode = VerificationCode.getInstance().getCode();
				}
				break;

			default:
				ToastUtil.alert(Activity_Login.this, message);
				break;
		}
	}

	//点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.im_visible:
				if (isShow) {
					pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					im_visible.setImageResource(R.drawable.r_invisible_3x);
				} else {
					pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					im_visible.setImageResource(R.drawable.r_visible_3x);
				}
				isShow = !isShow;
				break;
			//登陆
			case R.id.login:
				String username = user.getText().toString().trim();
				String password = pwd.getText().toString().trim();
				if (vc_code.getText().toString().trim().equals(getCode)) {
					showProgressBar();
					//登陆
					iPresenterLogin.doLogin(username, password);
				} else {
					ToastUtil.alert(Activity_Login.this, "验证码不正确");
					vc_image.setImageBitmap(VerificationCode.getInstance().getBitmap());
					getCode = VerificationCode.getInstance().getCode();
				}
				break;

			//忘记密码
			case R.id.layout_foget:
				UmengUtil.eventById(Activity_Login.this, R.string.login_key_f);
				Activity_Forget_Pwd.launche(Activity_Login.this, true);
				break;

			//客服
			case R.id.customer_service:
				Activity_Register_First.launche(Activity_Login.this);
				break;
		}
	}
}