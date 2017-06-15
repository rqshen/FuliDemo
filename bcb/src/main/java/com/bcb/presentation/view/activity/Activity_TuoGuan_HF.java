package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.module.browse.FundCustodianWebActivity;
import com.bcb.module.myinfo.balance.recharge.RechargeActivity;
import com.bcb.module.myinfo.balance.withdraw.WithdrawActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.UserBankCard;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.presentation.view.custom.AlertView.AlertView;

import org.json.JSONObject;

public class Activity_TuoGuan_HF extends Activity_Base implements View.OnClickListener {
	Context ctx;
	TextView tv_account, tv_monery, tv_charge, tv_get;
	RelativeLayout rl_look, rl_login, rl_find_deal, rl_find_login, rl_alert_deal, rl_unband;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tuo_guan_hf);
		ctx = this;
		setTitleValue("资金托管");
		setLeftTitleVisible(true);
		findViews();
		setOnClickListenerd();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (MyApplication.mUserDetailInfo != null) {
			tv_account.setText(MyApplication.mUserDetailInfo.CustodyAccount);
		}
		if (MyApplication.mUserWallet != null) {
			tv_monery.setText("" + String.format("%.2f", MyApplication.mUserWallet.getBalanceAmount()));
		}
	}

	private void setOnClickListenerd() {
		tv_charge.setOnClickListener(Activity_TuoGuan_HF.this);
		tv_get.setOnClickListener(Activity_TuoGuan_HF.this);
		rl_look.setOnClickListener(Activity_TuoGuan_HF.this);
		rl_login.setOnClickListener(Activity_TuoGuan_HF.this);
		rl_find_deal.setOnClickListener(Activity_TuoGuan_HF.this);
		rl_find_login.setOnClickListener(Activity_TuoGuan_HF.this);
		rl_alert_deal.setOnClickListener(Activity_TuoGuan_HF.this);
		rl_unband.setOnClickListener(Activity_TuoGuan_HF.this);
	}

	private void findViews() {
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_monery = (TextView) findViewById(R.id.tv_monery);
		tv_charge = (TextView) findViewById(R.id.tv_charge);
		tv_get = (TextView) findViewById(R.id.tv_get);
		rl_look = (RelativeLayout) findViewById(R.id.rl_look);
		rl_login = (RelativeLayout) findViewById(R.id.rl_login);
		rl_find_deal = (RelativeLayout) findViewById(R.id.rl_find_deal);
		rl_find_login = (RelativeLayout) findViewById(R.id.rl_find_login);
		rl_alert_deal = (RelativeLayout) findViewById(R.id.rl_alert_deal);
		rl_unband = (RelativeLayout) findViewById(R.id.rl_unband);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 2:
				requestUserBankCard();
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_unband:
				ProjectDetailActivity.launcheForResult(this, "解绑", UrlsTwo.UrlUnBand, 2);
				break;
			case R.id.tv_charge:
				startActivity(new Intent(Activity_TuoGuan_HF.this, RechargeActivity.class));
				break;
			case R.id.tv_get:
				withdrawMoney();
				break;
			case R.id.rl_look:
				ProjectDetailActivity.launcheForResult(this, "托管接入证明", UrlsTwo.UrlZM, 3);
				break;
			case R.id.rl_login:
				loginAccount();
				break;
			case R.id.rl_find_deal:
				alertLoginPassword("找回交易密码");
				break;
			case R.id.rl_find_login:
				alertLoginPassword("找回登录密码");
				break;
			case R.id.rl_alert_deal:
				alertLoginPassword("修改交易密码");
				break;

			default:
				break;
		}
	}

	private AlertView alertView;

	//提现
	private void withdrawMoney() {
		//用户还没绑卡
		if (MyApplication.mUserDetailInfo.BankCard == null) {
			showAlertView("您还没指定提现卡哦", "该银行卡将作为账户唯一提现银行卡", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					requestBandCard();
					alertView.dismiss();
					alertView = null;
				}
			});
		} else//跳到提现页面
			startActivity(new Intent(ctx, WithdrawActivity.class));
	}

	//提示对话框
	private void showAlertView(String titleName, String contentMessage, DialogInterface.OnClickListener onClickListener) {
		AlertView.Builder ibuilder = new AlertView.Builder(ctx);
		ibuilder.setTitle(titleName);
		ibuilder.setMessage(contentMessage);
		ibuilder.setPositiveButton("立即设置", onClickListener);
		ibuilder.setNegativeButton("取消", null);
		alertView = ibuilder.create();
		alertView.show();
	}

	//*****************************************                            请求服务器
	// ****************************************

	/**
	 * 登录汇付账户
	 */
	private void loginAccount() {
		String requestUrl = UrlsTwo.LoginAccount;
		String encodeToken = TokenUtil.getEncodeToken(Activity_TuoGuan_HF.this);
		LogUtil.i("bqt", "【Activity_TuoGuan_HF】【loginAccount】请求路径：" + requestUrl);
		BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>
				() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_TuoGuan_HF】【loginAccount】" + response.toString());
				if (PackageUtil.getRequestStatus(response, Activity_TuoGuan_HF.this)) {
					try {
						/** 后台返回的JSON对象，也是要转发给汇付的对象 */
						JSONObject result = PackageUtil.getResultObject(response);
						if (result != null) {
							//网页地址
							String postUrl = result.optString("PostUrl");
							result.remove("PostUrl");//移除这个参数
							//传递的参数
							String postData = HttpUtils.jsonToStr(result.toString()); //跳转到webview
							FundCustodianWebActivity.launche(Activity_TuoGuan_HF.this, "登录汇付账户", postUrl, postData);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "【Activity_TuoGuan_HF】【loginAccount】" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(Activity_TuoGuan_HF.this, response.optString("message"), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【Activity_TuoGuan_HF】【loginAccount】网络异常，请稍后重试" + error.toString());
			}
		});
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	/**
	 * 修改汇付登录密码
	 */
	private void alertLoginPassword(final String title) {
		String requestUrl = UrlsTwo.LoginAccountAlert;
		String encodeToken = TokenUtil.getEncodeToken(Activity_TuoGuan_HF.this);
		LogUtil.i("bqt", "【Activity_TuoGuan_HF】【alertLoginPassword】请求路径：" + requestUrl);
		BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>
				() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_TuoGuan_HF】【alertLoginPassword】" + response.toString());
				if (PackageUtil.getRequestStatus(response, Activity_TuoGuan_HF.this)) {
					try {
						/** 后台返回的JSON对象，也是要转发给汇付的对象 */
						JSONObject result = PackageUtil.getResultObject(response);
						if (result != null) {
							//网页地址
							String postUrl = result.optString("PostUrl");
							result.remove("PostUrl");//移除这个参数
							//传递的参数
							String postData = HttpUtils.jsonToStr(result.toString()); //跳转到webview
							FundCustodianWebActivity.launche(Activity_TuoGuan_HF.this, title, postUrl, postData);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "【Activity_TuoGuan_HF】【alertLoginPassword】" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(Activity_TuoGuan_HF.this, response.optString("message"), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【Activity_TuoGuan_HF】【alertLoginPassword】网络异常，请稍后重试" + error.toString());
			}
		});
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	/**
	 * 绑定提现卡
	 */
	private void requestBandCard() {

		String requestUrl = UrlsTwo.UrlBandCard;
		String encodeToken = TokenUtil.getEncodeToken(ctx);
		LogUtil.i("bqt", "【RechargeActivity】【BandCard】请求路径：" + requestUrl);
		BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>
				() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【RechargeActivity】【BandCard】返回数据：" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					try {
						/** 后台返回的JSON对象，也是要转发给汇付的对象 */
						JSONObject result = PackageUtil.getResultObject(response);
						if (result != null) {
							//网页地址
							String postUrl = result.optString("PostUrl");
							result.remove("PostUrl");//移除这个参数
							//传递的 参数
							String postData = HttpUtils.jsonToStr(result.toString());
							//跳转到webview
							FundCustodianWebActivity.launche(ctx, "绑定提现卡", postUrl, postData);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "【FundCustodianAboutActivity】【OpenAccount】" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(ctx, response.optString("message"), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【RechargeActivity】【BandCard】网络异常，请稍后重试" + error.toString());
			}
		});
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	/**
	 * 用户银行卡
	 */
	private void requestUserBankCard() {
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UrlUserBand, null, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "绑定的银行卡：" + response.toString());
				if (PackageUtil.getRequestStatus(response, Activity_TuoGuan_HF.this)) {
					JSONObject data = PackageUtil.getResultObject(response);
					if (data != null && MyApplication.mUserDetailInfo != null) {
						MyApplication.mUserDetailInfo.BankCard = MyApplication.mGson.fromJson(data.toString(), UserBankCard.class);
					}
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
			}
		});
		MyApplication.instance.getRequestQueue().add(jsonRequest);
	}
}
