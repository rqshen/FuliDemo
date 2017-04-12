package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.BanksBean;
import com.bcb.data.bean.UserBankCard;
import com.bcb.utils.BankLogo;
import com.bcb.utils.HttpUtils;
import com.bcb.utils.LogUtil;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.module.myinfo.financial.financialdetail.projectdetail.ProjectDetailActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值充值充值充值充值充值充值充值充值充值充值充值充值充充值充充值充值充充值充值充充值充值充充值充值充充值充值充充值充值充值充值
 */

public class Activity_Charge_HF extends Activity_Base implements View.OnClickListener, TextWatcher {
	public static float ADD_MONERY = 0;
	TextView tv_left_monery, tv_next, tv_unband;
	EditText et_add_monery;
	ArrayList<BanksBean> list;

	TextView bank_card_text, tv_xianer, bank_name_text;
	//	TextView tv_no, tv_tip_bottom;
	RelativeLayout layout_bank_card;
	LinearLayout ll_card;
	//	LinearLayout  ll_tips;
	ImageView bank_icon, iv_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_hf);
		initTitle();
		findViews();
		initBankCard();
		//		initTv_tip_bottom();
		iv_clear.setOnClickListener(this);
		tv_unband.setOnClickListener(this);
		tv_next.setOnClickListener(this);
		et_add_monery.addTextChangedListener(this);
		requestBankList();
	}

	private void initTitle() {
		//标题
		setTitleValue("充值");
		setLeftTitleVisible(true);
		setLeftTitleListener(this);
	}

	private void findViews() {
		tv_left_monery = (TextView) findViewById(R.id.tv_left_monery);
		tv_next = (TextView) findViewById(R.id.tv_next);
		//		tv_tip_bottom = (TextView) findViewById(R.id.tv_tip_bottom);
		tv_unband = (TextView) findViewById(R.id.tv_unband);
		iv_clear = (ImageView) findViewById(R.id.iv_clear);

		layout_bank_card = (RelativeLayout) findViewById(R.id.layout_bank_card);
		ll_card = (LinearLayout) findViewById(R.id.ll_card);
		//		ll_tips = (LinearLayout) findViewById(R.id.ll_tips);
		bank_card_text = (TextView) findViewById(R.id.bank_card_text);
		bank_name_text = (TextView) findViewById(R.id.bank_name_text);
		tv_xianer = (TextView) findViewById(R.id.tv_xianer);
		//		tv_no = (TextView) findViewById(R.id.tv_no);
		bank_icon = (ImageView) findViewById(R.id.bank_icon);
		et_add_monery = (EditText) findViewById(R.id.et_add_monery);
	}

	private void initBankCard() {
		if (MyApplication.mUserWallet != null) tv_left_monery.setText(String.format("%.2f", MyApplication.mUserWallet.getBalanceAmount()) + "元");
		//【解绑说明】已绑定银行卡账号，且是快捷支付
		if (MyApplication.mUserDetailInfo.BankCard != null && MyApplication.mUserDetailInfo.BankCard.IsQPCard) {
			//bank_card_text.setText(MyTextUtil.delBankNum(MyApplication.mUserDetailInfo.BankCard.getCardNumber()));
			String cardNumber = MyApplication.mUserDetailInfo.BankCard.CardNumber;
			bank_card_text.setText("尾号" + cardNumber.substring(cardNumber.length() - 4));
			//设置银行卡logo
			BankLogo bankLogo = new BankLogo();
			bank_icon.setBackgroundResource(bankLogo.getDrawableBankLogo(MyApplication.mUserDetailInfo.BankCard.BankCode));
			bank_name_text.setText(MyApplication.mUserDetailInfo.BankCard.BankName);
			setRightTitleValue("解绑说明", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ProjectDetailActivity.launcheForResult(Activity_Charge_HF.this, "解绑说明", UrlsTwo.UrlUnBandExplain, 2);
				}
			});

			//			tv_no.setVisibility(View.GONE);
			layout_bank_card.setVisibility(View.VISIBLE);//银行卡号
			ll_card.setVisibility(View.VISIBLE);//该卡本次最多可充值1000元，每日最多2000元
			//			ll_tips.setVisibility(View.VISIBLE);//福利金融由央行监管的****进行资金托管
			//【限额说明】未绑定
		} else {
			setRightTitleValue("限额说明", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (list != null && list.size() > 0) {
						Intent intent = new Intent(Activity_Charge_HF.this, Activity_Charge_Tips.class);
						intent.putParcelableArrayListExtra("data", list);
						startActivity(intent);
					} else Toast.makeText(Activity_Charge_HF.this, "查询限额信息失败", Toast.LENGTH_SHORT).show();
				}
			});
			//			tv_no.setVisibility(View.VISIBLE);//快捷支付银行卡将作为本账户唯一提现卡
			layout_bank_card.setVisibility(View.GONE);
			ll_card.setVisibility(View.GONE);
			//			ll_tips.setVisibility(View.GONE);
		}
	}

	//	private void initTv_tip_bottom() {
	//		SpannableString mSpannableString = new SpannableString("点击联系客服");
	//		ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xff3399ff);
	//		mSpannableString.setSpan(colorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	//		ClickableSpan clickableSpan = new ClickableSpan() {
	//			@Override
	//			public void onClick(View widget) {
	//				String userId = null;
	//				//判断是否为空
	//				if (MyApplication.mUserDetailInfo != null) {
	//					userId = MyApplication.mUserDetailInfo.getCustomerId();
	//				}
	//				MQCustomerManager.getInstance(Activity_Charge_HF.this).showCustomer(userId);
	//			}
	//		};
	//		mSpannableString.setSpan(clickableSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	//		tv_tip_bottom.append(mSpannableString);
	//		tv_tip_bottom.setMovementMethod(LinkMovementMethod.getInstance());
	//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 1:
				if (MyApplication.mUserWallet != null) tv_left_monery.setText(String.format("%.2f", MyApplication.mUserWallet.getBalanceAmount()) + "元");
				break;
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
			case R.id.back_img:
				finish();
				break;
			case R.id.tv_unband:
				ProjectDetailActivity.launcheForResult(this, "解绑", UrlsTwo.UrlUnBand, 2);
				break;
			case R.id.iv_clear:
				et_add_monery.setText("");
				break;
			case R.id.tv_next:
				if (TextUtils.isEmpty(et_add_monery.getText().toString().trim())) {
					Toast.makeText(Activity_Charge_HF.this, "请输入充值金额", Toast.LENGTH_SHORT).show();
					return;
				} else {
					try {
						ADD_MONERY = Float.valueOf(et_add_monery.getText().toString().trim());
					} catch (Exception e) {
						LogUtil.i("bqt", "【Activity_Charge_HF】【onClick】" + e.toString());
						Toast.makeText(Activity_Charge_HF.this, "输入金额有误", Toast.LENGTH_SHORT).show();
						return;
					}
					if (maxMonery > 0 && ADD_MONERY > maxMonery) {
						Toast.makeText(Activity_Charge_HF.this, "充值金额超过单笔最大限额（" + maxMonery + "元），请修改充值金额", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					if (ADD_MONERY >= 10000000.0f) {//服务器限制
						Toast.makeText(Activity_Charge_HF.this, "输入金额超过受理限额", Toast.LENGTH_SHORT).show();
						return;
					}
					requestCharge();
					break;
				}
			default:
				break;
		}
	}

	/**
	 * 充值
	 */
	private void requestCharge() {
		String requestUrl = UrlsTwo.UrlCharge;
		String encodeToken = TokenUtil.getEncodeToken(Activity_Charge_HF.this);
		LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】请求路径：" + requestUrl);
		JSONObject requestObj = new JSONObject();
		try {
			requestObj.put("Amount", ADD_MONERY + "");
			LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】请求参数：" + requestObj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, requestObj, encodeToken, true, new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】返回数据：" + response.toString());
				if (PackageUtil.getRequestStatus(response, Activity_Charge_HF.this)) {
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
							Activity_WebView.launcheForResult(Activity_Charge_HF.this, "充值", postUrl, postData, 1);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "【Activity_Open_Account】【OpenAccount】" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(Activity_Charge_HF.this, response.optString("message"), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】网络异常，请稍后重试" + error.toString());
			}
		});
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	int maxMonery;

	/**
	 * 银行列表
	 */
	private void requestBankList() {
		String requestUrl = UrlsTwo.UrlBanks;
		String encodeToken = TokenUtil.getEncodeToken(Activity_Charge_HF.this);
		LogUtil.i("bqt", "【Activity_Charge_HF】【BankList】请求路径：" + requestUrl);
		BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>
				() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_Charge_HF】【BankList】返回数据：" + response.toString());
				if (PackageUtil.getRequestStatus(response, Activity_Charge_HF.this)) {
					try {
						list = MyApplication.mGson.fromJson(response.optJSONArray("result").toString(), new TypeToken<List<BanksBean>>() {}
								.getType());
						if (list != null) {

						}
						if (MyApplication.mUserDetailInfo.BankCard != null && MyApplication.mUserDetailInfo.BankCard.IsQPCard) {//已绑定，且是快捷支付
							for (int i = 0 ; i < list.size() ; i++) {
								if (MyApplication.mUserDetailInfo.BankCard.BankCode.equalsIgnoreCase(list.get(i).getBankCode())) {
									ll_card.setVisibility(View.VISIBLE);
									maxMonery = list.get(i).getMaxSingle();
									//                                    tv_xianer.setText("该卡本次最多可充值" + list.get(i).getMaxSingle()
									// + "元，每日最多" + list.get(i).getMaxDay() + "元");
									tv_xianer.setText("单笔最高" + initMonery(list.get(i).getMaxSingle()) + "，单日限额" + initMonery(list.get
											(i).getMaxDay()));
								}
							}
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "【Activity_Charge_HF】【BankList】" + e.getMessage());
					}
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【Activity_Charge_HF】【BankList】网络异常，请稍后重试" + error.toString());
			}
		});
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	private String initMonery(int monery) {
		if (monery >= 10000) return monery / 10000 + "万";
		else return monery + "";
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		String text = et_add_monery.getText().toString().trim();
		//        try {
		//            if (!TextUtils.isEmpty(text) && Float.valueOf(text) >0) {//= 500
		//                tv_next.setBackgroundResource(R.drawable.button_solid_red);
		//            } else {
		//                tv_next.setBackgroundResource(R.drawable.button_solid_black);
		//            }
		//        } catch (Exception e) {
		//            Toast.makeText(Activity_Charge_HF.this, "输入金额格式有误", Toast.LENGTH_SHORT).show();
		//            et_add_monery.setText("");
		//        }
		if (!TextUtils.isEmpty(text)) {
			iv_clear.setVisibility(View.VISIBLE);
		} else iv_clear.setVisibility(View.GONE);
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
				if (PackageUtil.getRequestStatus(response, Activity_Charge_HF.this)) {
					JSONObject data = PackageUtil.getResultObject(response);
					if (data != null && MyApplication.mUserDetailInfo != null) {
						MyApplication.mUserDetailInfo.BankCard = MyApplication.mGson.fromJson(data.toString(), UserBankCard.class);
						initBankCard();
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
