package com.bcb.presentation.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 开通资金托管
 */
public class Activity_Open_Account2 extends Activity_Base {
	@BindView(R.id.tv_title) TextView tv_title;//您好，资金托管需要验证您的身份
	@BindView(R.id.tv_open) TextView tv_open;//下一步
	@BindView(R.id.customer_service) TextView customer_service;//隐藏客服
	@BindView(R.id.et_idcard) EditText et_idcard;//身份证号
	@BindView(R.id.rl_idcard) RelativeLayout rl_idcard;//身份证号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_open_account2);
		setTitleValue("资金托管");
		setLeftTitleVisible(true);
		ButterKnife.bind(this);

		tv_title.setText("您好，" + App.saveUserInfo.getLocalPhone() + "\n资金托管需要验证您的身份证");//您的身份信息已绑定手机号
		tv_open.setText("下一步");
		rl_idcard.setVisibility(View.VISIBLE);
		customer_service.setVisibility(View.INVISIBLE);
		if (App.mUserDetailInfo != null && App.mUserDetailInfo.IDCard != null && App.mUserDetailInfo.IDCard != "") {
			et_idcard.setText(App.mUserDetailInfo.IDCard);
		}
	}

	@OnClick({R.id.tv_open})
	public void click(View v) {
		switch (v.getId()) {
			case R.id.tv_open:
				if (TextUtils.isEmpty(et_idcard.getText().toString().trim())) {
					Toast.makeText(Activity_Open_Account2.this, "身份证号不能为空", Toast.LENGTH_SHORT).show();
				} else requestOpenAccount();
				break;
			default:
				break;
		}
	}

	/**
	 * 开通汇付账户
	 */
	private void requestOpenAccount() {
		LogUtil.i("bqt", "【Activity_Open_Account2】【requestOpenAccount】" + et_idcard.getText().toString().trim());

		JSONObject obj = new org.json.JSONObject();
		try {
			obj.put("IdCard", et_idcard.getText().toString().trim());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.OpenAccount, obj, TokenUtil.getEncodeToken(this), true, new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", " 开通汇付账户" + response.toString());
				if (PackageUtil.getRequestStatus(response, Activity_Open_Account2.this)) {
					try {
						/** 后台返回的JSON对象，也是要转发给汇付的对象 */
						JSONObject result = PackageUtil.getResultObject(response);
						if (result != null) {
							//开通自动投标
							if (response.getInt("status") == 1) {//成功
								//网页地址
								String postUrl = result.optString("PostUrl");
								result.remove("PostUrl");//移除这个参数
								//传递的 参数
								String postData = HttpUtils.jsonToStr(result.toString());
								//跳转到webview
								Activity_WebView.launche(Activity_Open_Account2.this, "资金托管", postUrl, postData);
								finish();
							} else {
								String message = response.getString("message");
								Activity_Open_Account3.launche(Activity_Open_Account2.this, message);
							}
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "【Activity_Open_Account2】【OpenAccount】" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(Activity_Open_Account2.this, response.optString("message"), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【Activity_Open_Account2】【OpenAccount】网络异常，请稍后重试" + error.toString());
			}
		});
		App.getInstance().getRequestQueue().add(jsonRequest);
	}
}