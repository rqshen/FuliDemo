package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.ProgressDialogrUtils;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.AlertView.DialogBQT;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 校验邮箱
 * 作者：baicaibang
 * 时间：2016/11/29 11:53
 */
public class A_Email_Check extends Activity_Base {
	@BindView(R.id.send) Button send;//发送验证码
	@BindView(R.id.et_email) EditText et_email;//输入邮箱
	@BindView(R.id.et_yzm) EditText et_yzm;//输入验证码
	@BindView(R.id.tv_tips) TextView tv_tips;//提示
	@BindView(R.id.tv_email_end) TextView tv_email_end;//邮箱后缀

	private int time;//倒计时
	private Timer timer;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					send.setText(time + "S");
					send.setBackgroundResource(R.drawable.button_gray);
					break;
				
				case 2:
					send.setEnabled(true);
					send.setBackgroundResource(R.drawable.request_code_selector);
					send.setText("重新发送");
					break;
			}
		}
	};

	public static void launche(Context ctx) {
		Intent intent = new Intent(ctx, A_Email_Check.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_email_check);
		ButterKnife.bind(this);
		setLeftTitleVisible(true);
		setTitleValue("非签约IT精英贷");
		//获取保存的email
		String email = getSharedPreferences("email", Context.MODE_PRIVATE).getString("email", "");
		tv_email_end.setText(email == null ? "" : email);
	}

	@OnClick({R.id.send, R.id.next})
	public void onClickIv(View v) {
		switch (v.getId()) {
			case R.id.send:
				//检查邮箱是否合法
				if (toGoBeforeCheck()) {
					// 设置获取验证码按钮为不可点击，防止获取多条验证码
					send.setEnabled(false);
					send.setBackgroundResource(R.drawable.button_shape_unenabled);
					//发送验证码
					getVerificationCode();
				}
				break;
			
			case R.id.next:
				if (TextUtils.isEmpty(et_yzm.getText().toString().trim())) {
					Toast.makeText(A_Email_Check.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
				} else pushVerificationCode();
				break;
		}
	}

	//检查邮箱
	private boolean toGoBeforeCheck() {
		//是否为空
		String begin = et_email.getText().toString().trim();
		if (TextUtils.isEmpty(begin)) {
			Toast.makeText(A_Email_Check.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		//获取保存的email
		String end = getSharedPreferences("email", Context.MODE_PRIVATE).getString("email", "");
		String email=begin+(end == null ? "" : end);
		//是否为邮箱
		Pattern pattern = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
		Matcher m = pattern.matcher(email);
		if (!m.matches()) {
			Toast.makeText(A_Email_Check.this, "邮箱格式非法", Toast.LENGTH_SHORT).show();
			return false;
		}
//		//是否为指定后缀
//		boolean isContainEmail = false;
//		for (int i = 0 ; i < mListAll.size() ; i++) {
//			if (email.contains(mListAll.get(i).email)) {
//				isContainEmail = true;
//				break;
//			}
//		}
		//		//不包含
		//		if (!isContainEmail) {
		//			showDialog("您输入的邮箱后缀不是公司邮箱地址\n将导致您的借款审核不通过，请重新\n填写。");
		//			return false;
		//		}
		return true;
	}
	
	//获取验证码
	private void getVerificationCode() {
		ProgressDialogrUtils.show(this, "正在获取验证码…");
		JSONObject obj = new org.json.JSONObject();
		try {
			String end = tv_email_end.getText().toString().trim();
			obj.put("Email", et_email.getText().toString().trim() + (end == null ? "" : end));
			BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.CREATEVALIDATECODE, obj, TokenUtil.getEncodeToken(this), new
					BcbRequest.BcbCallBack<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					ProgressDialogrUtils.hide();
					LogUtil.i("bqt", "获取验证码返回：" + response.toString());
					//成功，启动定时器
					if (response.optInt("status") == 1) {
						setTimer();
						tv_tips.setVisibility(View.VISIBLE);
					} else if (response.optInt("status") == -3) {
						//	Toast.makeText(A_Email_Check.this, response.optString("message"), Toast.LENGTH_SHORT).show();
						showDialog(response.optString("message"));
						send.setEnabled(true);
						send.setBackgroundResource(R.drawable.request_code_selector);
					} else Toast.makeText(A_Email_Check.this, response.optString("message"), Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onErrorResponse(Exception error) {
					Toast.makeText(A_Email_Check.this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
					send.setEnabled(true);
					send.setBackgroundResource(R.drawable.request_code_selector);
				}
			});

			App.getInstance().getRequestQueue().add(jsonRequest);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//提交验证码
	private void pushVerificationCode() {
		ProgressDialogrUtils.show(this, "正在提交…");
		JSONObject obj = new org.json.JSONObject();
		try {
			obj.put("Email", et_email.getText().toString().trim());
			obj.put("VerifyCode", et_yzm.getText().toString().trim());
			BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.EDITMYBORROWEREMAIL, obj, TokenUtil.getEncodeToken(this), new
					BcbRequest.BcbCallBack<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					ProgressDialogrUtils.hide();
					LogUtil.i("bqt", "提交验证码返回：" + response.toString());
					if (response.optInt("status") == 1) {//成功结果页
						Activity_Tips_FaileOrSuccess.launche(A_Email_Check.this, Activity_Tips_FaileOrSuccess.EMAIL_SUCCESS, "");
						finish();
					} else if (response.optInt("status") == -3) {//失败提示弹窗
						showDialog(response.optString("message"));
						//showDialog("邮箱校验失败，验证码错误或已过期\n请重新验证");
					} else Toast.makeText(A_Email_Check.this, response.optString("message"), Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onErrorResponse(Exception error) {
					Toast.makeText(A_Email_Check.this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			});

			App.getInstance().getRequestQueue().add(jsonRequest);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void showDialog(String message) {
		DialogBQT diaolog = new DialogBQT(this);
		diaolog.setTitleAndMessageAndIcon(null, message, R.drawable.icon_email);
		diaolog.setOneButtonText("我知道了", -1);
		diaolog.show();
	}
	
	//定时器
	private void setTimer() {
		time = 60;
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				time--;
				if (time > 0) handler.sendEmptyMessage(1);
				else {
					handler.sendEmptyMessage(2);
					destoryTimer();
				}
			}
		};
		timer.schedule(task, 0, 1000);
	}

	private void destoryTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		destoryTimer();
	}
}