package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.bcb.presentation.view.custom.AlertView.AlertView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class A_MyYE extends Activity_Base {
	
	@BindView(R.id.layout_cz) RelativeLayout layoutcz;
	@BindView(R.id.layout_tx) RelativeLayout layouttx;
	@BindView(R.id.layout_mx) RelativeLayout layoutmx;
	private Context ctx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		setBaseContentView(R.layout.activity_my_ye);
		ButterKnife.bind(this);
		setLeftTitleVisible(true);
		setTitleValue("我的余额");
	}
	
	@OnClick({R.id.layout_cz, R.id.layout_tx,R.id.layout_mx})
	public void onClick(View view) {
		if (App.saveUserInfo.getAccess_Token() == null) Activity_Login.launche(ctx);
		switch (view.getId()) {
			case R.id.layout_cz:
				rechargeMoney();
				break;
			case R.id.layout_tx:
				withdrawMoney();
				break;
			case R.id.layout_mx:
				Activity_Trading_Record.launche(ctx);
				break;
		}
	}

	//充值
	private void rechargeMoney() {
		//已开通托管
		if (App.mUserDetailInfo != null && App.mUserDetailInfo.HasOpenCustody)
			startActivity(new Intent(ctx, Activity_Charge_HF.class));
		else startActivity(new Intent(ctx, Activity_Open_Account.class));
	}

	//提现
	//绑定提现卡后请求我的银行卡接口同样会返回银行卡信息，不过【IsQPCard】为【false】
	// {"status":1,"message":"","result":{"BankCode":"CIB","BankName":"兴业银行","CardNumber":"6229081111111111112","IsQPCard":false}}
	private void withdrawMoney() {
		//未开通托管
		if (App.mUserDetailInfo == null || !App.mUserDetailInfo.HasOpenCustody) {
			startActivity(new Intent(ctx, Activity_Open_Account.class));
			return;
		}
		//用户还没绑卡
		if (App.mUserDetailInfo.BankCard == null) {
			showAlertView("您还没指定提现卡哦", "该银行卡将作为账户唯一提现银行卡", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					requestBandCard();
					alertView.dismiss();
					alertView = null;
				}
			});
		} else startActivity(new Intent(ctx, Activity_Withdraw.class));
	}

	private AlertView alertView;

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

	/**
	 * 绑定提现卡
	 */
	private void requestBandCard() {
		String requestUrl = UrlsTwo.UrlBandCard;
		String encodeToken = TokenUtil.getEncodeToken(ctx);
		LogUtil.i("bqt", "【Activity_Charge_HF】【BandCard】请求路径：" + requestUrl);
		BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>
				() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "绑定提现卡：" + response.toString());
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
							Activity_WebView.launche(ctx, "绑定提现卡", postUrl, postData);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "【Activity_Open_Account】【OpenAccount】" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(ctx, response.optString("message"), Toast.LENGTH_SHORT)
							.show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【Activity_Charge_HF】【BandCard】网络异常，请稍后重试" + error.toString());
			}
		});
		App.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}
}