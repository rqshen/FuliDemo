package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.module.login.LoginActivity;
import com.bcb.module.myinfo.balance.FundCustodianWebActivity;
import com.bcb.module.myinfo.balance.FundCustodianAboutActivity;
import com.bcb.module.myinfo.balance.recharge.RechargeActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.CPXQbean;
import com.bcb.data.bean.CouponListBean;
import com.bcb.data.bean.CouponRecordsBean;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.utils.HttpUtils;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.MyTextUtil;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.utils.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bcb.R.id.buy_all;

/**
 * setTitleValue("买标"。setTitleValue("购买"。setTitleValue("立即购买"
 */
public class Activity_Project_Buy2 extends Activity_Base implements View.OnClickListener, TextWatcher {

	@BindView(R.id.more_value) TextView moreValue;
	@BindView(R.id.iv_tips) ImageView iv_tips;
	@BindView(buy_all) TextView buyAll;
	@BindView(R.id.prospective_earning_yuan) TextView prospective_earning_yuan;
	//标题
	private String title;
	//投资金额
	private EditText invest_money;
	//选择优惠券
	private RelativeLayout layout_coupon;
	//优惠券信息
	private TextView coupon_description;
	private ImageView coupon_arrow;
	//账户余额
	private TextView wallet_money;
	//充值
	private LinearLayout layout_recharge;
	//平台服务费
	private LinearLayout project_service_fee;
	//预期收益描述
	private TextView earnings_description;
	//预期收益
	private TextView prospective_earning;
	//立即购买按钮
	private Button button_buy;
	//出错信息
	private TextView error_tips;
	//普通标的
	private CPXQbean mSimpleProjectDetail;
	private int CouponType = 0;//CouponType默认为0，为1的时候表示体验标， 为2表示体验券
	private int countDate = 0;
	//银行卡信息
	public UserDetailInfo mUserDetailInfo;
	//账户余额
	private UserWallet mUserWallet;
	//判断是否正在购买中
	private boolean isPaying = false;
	//输入密码的对话框
	private DialogWidget enterPswDialog;
	private DialogWidget certDialog;
	//转圈提示
	private ProgressDialog progressDialog;
	//标的Id
	private String packageId = "";
	private String CouponId, CouponAmount, CouponMinAmount, PackageToken = "";
	//广播
	private Receiver buySuccessReceiver;
	private AlertView alertView;
	private int type = 0;
	private double amount;

	//默认构造函数，用来传递普通标的数据
	public static void launche2(Context ctx, String pid, String title, int CouponType, int countDate, CPXQbean
			simpleProjectDetail, int type) {
		Intent intent = new Intent(ctx, Activity_Project_Buy2.class);
		intent.putExtra("pid", pid);
		intent.putExtra("title", title);
		intent.putExtra("CouponType", CouponType);
		intent.putExtra("countDate", countDate);
		intent.putExtra("type", type);
		Bundle bundle = new Bundle();
		bundle.putSerializable("SimpleProjectDetail", simpleProjectDetail);
		intent.putExtras(bundle);
		ctx.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance()
				.pushOneActivity(Activity_Project_Buy2.this);
		if (getIntent() != null) {
			packageId = this.getIntent()
					.getStringExtra("pid");
			title = this.getIntent()
					.getStringExtra("title");
			CouponType = this.getIntent()
					.getIntExtra("CouponType", 0);
			countDate = this.getIntent()
					.getIntExtra("countDate", 0);
			mSimpleProjectDetail = (CPXQbean) this.getIntent()
					.getSerializableExtra("SimpleProjectDetail");
			type = getIntent().getIntExtra("type", 0);
		}
		setBaseContentView(R.layout.activity_project_buy);
		ButterKnife.bind(this);
		buyAll.setOnClickListener(Activity_Project_Buy2.this);
		iv_tips.setOnClickListener(Activity_Project_Buy2.this);
		setLeftTitleVisible(true);
		setTitleValue(title);
		setupView();//初始化页面
		setupRegister();//注册广播
	}

	//****************************************************************注册广播********************************************
	private void setupRegister() {
		//注册广播
		buySuccessReceiver = new Receiver();
		//注册更改余额成功广播
		IntentFilter rechargeChange = new IntentFilter("com.bcb.money.change.success");
		registerReceiver(buySuccessReceiver, rechargeChange);
		//注册交易密码设置成功广播
		IntentFilter setTragePasswd = new IntentFilter("com.bcb.passwd.setted");
		registerReceiver(buySuccessReceiver, setTragePasswd);
	}

	//*************************************************************初始化页面 ************************************************
	private void setupView() {

		if (MyApplication.mUserWallet.BalanceAmount <= 0) buyAll.setEnabled(false);
		PackageToken = mSimpleProjectDetail.PackageToken;
		//投资金额
		invest_money = (EditText) findViewById(R.id.invest_money);

		//设置光标的位置，添加输入监听器
		invest_money.addTextChangedListener(this);
		invest_money.setSelection(invest_money.getText()
				.length());

		//债券标，且可投金额含小数时，可输入小数。判断是否含小数部分【a % 1 != 0】或【a - (int) a != 0】
		if ((type == 1 || type == 2) && mSimpleProjectDetail.Balance % 1 != 0) {
			invest_money.setHint(String.format("%.2f", mSimpleProjectDetail.StartingAmount) + "元起投");
			invest_money.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
		} else {//否则不可输入小数
			invest_money.setHint((int) mSimpleProjectDetail.StartingAmount + "元起投");
			invest_money.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
		}

		//选择优惠券
		layout_coupon = (RelativeLayout) findViewById(R.id.layout_coupon);
		layout_coupon.setOnClickListener(this);
		//优惠券信息
		coupon_description = (TextView) findViewById(R.id.coupon_description);
		coupon_arrow = (ImageView) findViewById(R.id.coupon_arrow);
		coupon_arrow.setVisibility(View.GONE);
		//账户余额
		wallet_money = (TextView) findViewById(R.id.wallet_money);
		//充值
		layout_recharge = (LinearLayout) findViewById(R.id.layout_recharge);
		layout_recharge.setOnClickListener(this);
		//平台服务费
		project_service_fee = (LinearLayout) findViewById(R.id.project_service_fee);
		project_service_fee.setOnClickListener(this);
		//预期收益描述
		earnings_description = (TextView) findViewById(R.id.earnings_description);
		//预期收益
		prospective_earning = (TextView) findViewById(R.id.prospective_earning);
		//立即购买按钮
		button_buy = (Button) findViewById(R.id.button_buy);
		button_buy.setOnClickListener(this);
		button_buy.setClickable(false);
		//出错信息，默认隐藏
		error_tips = (TextView) findViewById(R.id.error_tips);
		error_tips.setVisibility(View.GONE);
		//债权标，剩余金额小于起投金额
//		if ((type == 1 || type == 2) && mSimpleProjectDetail.Balance < mSimpleProjectDetail.StartingAmount) {
//			invest_money.setText(mSimpleProjectDetail.StartingAmount + "");
//			//invest_money.setEnabled(false);
//			invest_money.setKeyListener(null);
//			button_buy.setClickable(true);
//			button_buy.setBackgroundResource(R.drawable.button_solid_red);
//		}
		//获取优惠券张数
		if (MyApplication.saveUserInfo.getAccess_Token() != null) {
			getCouponCount();
			getUserBanlance();
		}

		if (type == 2) requestAmount();
		else moreValue.setText(String.format("%.2f", mSimpleProjectDetail.Balance));
	}

	int number = 0;

	//获取优惠券张数
	private void getCouponCount() {
		LogUtil.i("bqt", "【优惠券的类型】CouponType=" + CouponType);
		//if (CouponType==0)  return;//********************转让标和正常标，CouponType>0才去请求我的赠券，跟之前正常标一样。
		JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", 1);
			obj.put("PageSize", 10);
			obj.put("Status", 1);
			//优先检查是否体验券(包括可能填错了，填了优惠券和体验券都有)
//			if ((CouponType & 1) == 1) {
//				obj.put("CouponType", 1);
//			} else {
//				obj.put("CouponType", CouponType);
//			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Select_Coupon, obj, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_Project_Buy】【onResponse】获取优惠券张数" + response.toString());

				try {
					boolean flag = PackageUtil.getRequestStatus(response, Activity_Project_Buy2.this);
					if (flag) {
						JSONObject obj = PackageUtil.getResultObject(response);
						//显示优惠券数量
						if (obj != null) {
							//******************************************************************************************
							List<CouponRecordsBean> Records = MyApplication.mGson.fromJson(obj.toString(), CouponListBean.class).Records;
							number = 0;
							for (int i = 0; i < Records.size(); i++) {
								if (Records.get(i)
										.getCouponType() == 2) number++;
							}
							//******************************************************************************************
							ShowCouponCount(number + "");
							LogUtil.i("bqt", CouponType + "个数" + number);
						}
					}
				} catch (Exception e) {
					LogUtil.d("bqt", "" + e.getMessage());
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.i("bqt", "【获取优惠券张数】" + error.toString());
			}
		});
		jsonRequest.setTag(BcbRequestTag.BCB_SELECT_COUPON_REQUEST);
		MyApplication.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}

	//************************************************* 显示优惠券张数*******************************************************
	protected void ShowCouponCount(String TotalCount) {
		int totalCount = Integer.parseInt(TotalCount);
		if (totalCount == 0) coupon_description.setText("暂无可用券");
		else coupon_description.setText(totalCount + " 张，" + " 点击选择");

		if (!TextUtils.isEmpty(TotalCount)) {
			//不为0 时，始终显示箭头
			if (CouponType != 0) coupon_arrow.setVisibility(View.VISIBLE);
			else {
				if (totalCount == 0) coupon_arrow.setVisibility(View.GONE);
				else coupon_arrow.setVisibility(View.VISIBLE);
			}
		}
	}

	//********************************************************* 获取用户银行卡信息*****************************************
	private void loadUserDetailInfoData() {
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					int status = response.getInt("status");
					if (PackageUtil.getRequestStatus(response, Activity_Project_Buy2.this)) {
						JSONObject data = PackageUtil.getResultObject(response);
						//判断JSON对象是否为空
						if (data != null) {
							mUserDetailInfo = MyApplication.mGson.fromJson(data.toString(), UserDetailInfo.class);
						}
						//将用户信息写入静态数据区
						if (mUserDetailInfo != null) {
							MyApplication.mUserDetailInfo = mUserDetailInfo;
						}
					}
					//去登陆
					else if (status == -5) {
						startActivity(new Intent(Activity_Project_Buy2.this, LoginActivity.class));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				ToastUtil.alert(Activity_Project_Buy2.this, "网络异常，请稍后再试");
			}
		});
		jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
		MyApplication.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}

	//****************************************************** 获取用户余额 *****************************************
	private void getUserBanlance() {
		showProgressBar("正在获取用户余额...");
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(this), new
				BcbRequest.BcbCallBack<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideProgressBar();
						try {
							int status = response.getInt("status");
							if (PackageUtil.getRequestStatus(response, Activity_Project_Buy2.this)) {
								JSONObject data = PackageUtil.getResultObject(response);
								//判断JSON对象是否为空
								if (data != null) {
									mUserWallet = MyApplication.mGson.fromJson(data.toString(), UserWallet.class);
								}
								if (null != mUserWallet) {
									MyApplication.mUserWallet = mUserWallet;
									wallet_money.setText(String.format("%.2f", MyApplication.mUserWallet.BalanceAmount) + "元");
									button_buy.setText("立即申购");
								}
							} else if (status == -5) {
								startActivity(new Intent(Activity_Project_Buy2.this, LoginActivity.class));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(Exception error) {
						hideProgressBar();
					}
				});
		jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
		MyApplication.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}

	//*********************************************************获取打包项目可投余额*******************************************
	private void requestAmount() {
		String encodeToken = TokenUtil.getEncodeToken(Activity_Project_Buy2.this);
		JSONObject requestObj = new JSONObject();
		try {
			requestObj.put("PackageToken", PackageToken);
			requestObj.put("PackageId", packageId);
			LogUtil.i("bqt", "【打包项目可投余额参数】" + PackageToken + "--" + packageId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.MONKEYPACKAGEBALANCE, requestObj, encodeToken, true, new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【获取打包项目可投余额】" + response.toString());
				amount = response.optDouble("result");
				moreValue.setText(String.format("%.2f", amount));
				if ((float) amount < mSimpleProjectDetail.Balance) {
					iv_tips.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【获取打包项目可投余额】" + error.toString());
				Toast.makeText(Activity_Project_Buy2.this, error.toString(), Toast.LENGTH_SHORT)
						.show();
			}
		});
		MyApplication.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}

	//*********************************************************** 点击购买按钮 ******************************************
	float inputMoney;

	private void clickButton() {
		//无法获取买标数据
		if (mSimpleProjectDetail == null) {
			ToastUtil.alert(Activity_Project_Buy2.this, "无法获取买标数据，请稍后重试");
			return;
		}
		//项目可投金额是否大于0元
		if (mSimpleProjectDetail.Balance <= 0) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("可投金额为0，该标不能投");
			return;
		}
		// 未登录
		if (MyApplication.saveUserInfo.getAccess_Token() == null) {
			ToastUtil.alert(Activity_Project_Buy2.this, "请先登录");
			startActivity(new Intent(Activity_Project_Buy2.this, LoginActivity.class));
			return;
		}
		//无法获取用户余额
		if (MyApplication.mUserWallet == null) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("暂时无法获取用户余额");
			return;
		}
		//未开通托管
		if (MyApplication.mUserDetailInfo == null || !MyApplication.mUserDetailInfo.HasOpenCustody) {
			startActivity(new Intent(Activity_Project_Buy2.this, FundCustodianAboutActivity.class));
			return;
		}
		//未输入投资金额
		if (TextUtils.isEmpty(invest_money.getText().toString())) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("请输入投资金额");
			return;
		}
		//输入金额格式错误
		if (invest_money.getText().toString().startsWith(".")) {
			Toast.makeText(Activity_Project_Buy2.this, "您的输入有误", Toast.LENGTH_SHORT).show();
			return;
		}
		//输入金额
		inputMoney = Float.parseFloat(invest_money.getText().toString().replace(",", ""));

		//用户余额小于输入金额
		if ((float) MyApplication.mUserWallet.BalanceAmount < inputMoney) {
			altDialog();
			//ToastUtil.alert(Activity_Project_Buy.this, "余额不足，请先充值");
			//Activity_Recharge_Second.launche(Activity_Project_Buy.this);
			return;
		}
		//输入金额超出单笔限额
		if (inputMoney > mSimpleProjectDetail.SingletonAmount) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("当前输入超出单笔限额" + (int) mSimpleProjectDetail.SingletonAmount + "元");
			return;
		}
		//输入金额大于可投金额
		if (inputMoney > mSimpleProjectDetail.Balance) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("超出项目可投金额");
			return;
		}
		//可投金额大于起投金额（正常情况）
		if (mSimpleProjectDetail.Balance >= mSimpleProjectDetail.StartingAmount) {
			if (inputMoney < mSimpleProjectDetail.StartingAmount) {
				error_tips.setVisibility(View.VISIBLE);
				error_tips.setText("当前输入小于起投金额" + (int) mSimpleProjectDetail.StartingAmount + "元");
				return;
			}
			//可投金额小于起投金额（尾标）
		} else {
			//不做处理，即可以正常购买
		}

		//打包标时，超出自己可投的金额
		if (type == 2 && inputMoney > (float) amount) {
			Toast.makeText(this, "购买金额不能大于可投金额", Toast.LENGTH_SHORT).show();
			return;
		}
		//不满足优惠券使用限额
		if (CouponMinAmount != null && inputMoney < Float.valueOf(CouponMinAmount)) {
			altDialog2();
			return;
		}

		//满足所有条件后，请求买标
		switch (type) {
			case 0:
				requestBuy();
				break;
			case 1:
				requestBuy2(UrlsTwo.RRECLAIMCONVEY);
				break;
			case 2:
				requestBuy2(UrlsTwo.BOOKINGMONKEYPACKAGE);
				break;
			default:
				break;
		}
	}

	private void altDialog() {
		AlertView.Builder ibuilder = new AlertView.Builder(this);
		ibuilder.setTitle(" 温馨提示");
		ibuilder.setMessage("账户余额不足，是否充值？");
		ibuilder.setPositiveButton("立即充值", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				rechargeMoney();
				alertView.dismiss();
			}
		});
		ibuilder.setNegativeButton("暂不充值", null);
		alertView = ibuilder.create();
		alertView.show();
	}

	private void altDialog2() {
		AlertView.Builder ibuilder = new AlertView.Builder(this);
		ibuilder.setTitle("暂无法使用该优惠券");
		ibuilder.setMessage("本优惠券使用的买入最低额度为" + String.format("%.2f", Float.valueOf(CouponMinAmount)) + "元。\n" //
				+ "若继续购买，则默认不使用该优惠券。");
		ibuilder.setPositiveButton("继续购买", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertView.dismiss();
				CouponId = null;
				requestBuy();
			}
		});
		ibuilder.setNegativeButton("修改金额", null);
		alertView = ibuilder.create();
		alertView.setCanceledOnTouchOutside(false);
		alertView.show();
	}

	//***************************************************************买债权标*******************************************
	private void requestBuy2(String url) {
		String encodeToken = TokenUtil.getEncodeToken(Activity_Project_Buy2.this);
		LogUtil.i("bqt", "买债权标请求路径：" + url);
		JSONObject requestObj = new JSONObject();
		try {
			requestObj.put("Amount", inputMoney + "");
			switch (type) {
				case 1:
					requestObj.put("PackageId", packageId);//a6a400e5ed86这个接口中，ClaimConveyId参数改为PackageId
					break;
				case 2:
					requestObj.put("PackageId", packageId);
					break;
				default:
					break;
			}
			requestObj.put("PackageToken", PackageToken);
			requestObj.put("CouponId", CouponId);
			LogUtil.i("bqt", "买债权标请求参数：" + requestObj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(url, requestObj, encodeToken, true, new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "买债权标返回数据：" + response.toString());
				if (response.optInt("result") != -3) {
					Activity_Tips_FaileOrSuccess.launche(Activity_Project_Buy2.this, Activity_Tips_FaileOrSuccess.BUY_HF_SUCCESS, "");
				} else {
					Activity_Tips_FaileOrSuccess.launche(Activity_Project_Buy2.this, Activity_Tips_FaileOrSuccess.BUY_HF_FAILED, response.optString("message"));
				}
				finish();
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【买债权标】" + error.toString());
				Toast.makeText(Activity_Project_Buy2.this, error.toString(), Toast.LENGTH_SHORT)
						.show();
			}
		});
		MyApplication.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}

	//***************************************************************买平常标****************************
	private void requestBuy() {
		String requestUrl = UrlsTwo.UrlBuyProject;
		String encodeToken = TokenUtil.getEncodeToken(Activity_Project_Buy2.this);
		LogUtil.i("bqt", "【Activity_Project_Buy】【Buy】请求路径：" + requestUrl);
		JSONObject requestObj = new JSONObject();
		try {
			requestObj.put("Amount", inputMoney + "");
			requestObj.put("PackageId", packageId);
			requestObj.put("CouponId", CouponId);
			requestObj.put("PackageToken", PackageToken);
			LogUtil.i("bqt", "【Activity_Project_Buy】【Buy】请求参数：" + requestObj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, requestObj, encodeToken, true, new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_Project_Buy】【Buy】返回数据：" + response.toString());
				if (PackageUtil.getRequestStatus(response, Activity_Project_Buy2.this)) {
					try {
						JSONObject result = PackageUtil.getResultObject(response);
						if (result != null) {
							//网页地址
							String postUrl = result.optString("PostUrl");
							result.remove("PostUrl");//移除这个参数
							//传递的 参数
							String postData = HttpUtils.jsonToStr(result.toString());
							//跳转到webview
							FundCustodianWebActivity.launche(Activity_Project_Buy2.this, "投资确认", postUrl, postData);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "【Activity_Project_Buy】【Buy】" + e.getMessage());
					}
				} else Toast.makeText(Activity_Project_Buy2.this, response.optString("message"), Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【买平常标】" + error.toString());
				Toast.makeText(Activity_Project_Buy2.this, error.toString(), Toast.LENGTH_SHORT)
						.show();
			}
		});
		MyApplication.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}

	//***********************************************转圈提示**************************************
	//显示转圈提示
	private void showProgressBar(String title) {
		if (null == progressDialog) progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setMessage(title);
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

	//*********************************************************点击事件*****************************************
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.buy_all:
				double all = Math.min(mSimpleProjectDetail.Balance, MyApplication.mUserWallet.BalanceAmount);
				if (type == 2) {
					all = Math.min(all, amount);
				}
				invest_money.setText(String.format("%.2f", all));
				break;
			case R.id.iv_tips:
				Toast.makeText(this, "不能申购本人持有债权，可投金额变小", Toast.LENGTH_SHORT).show();
				break;
			//点击选择优惠券
			case R.id.invest_money:
			case R.id.layout_coupon:
				//先判断是否登录
				if (MyApplication.saveUserInfo.getAccess_Token() == null)
					startActivity(new Intent(Activity_Project_Buy2.this, LoginActivity.class));
					//判断优惠券类型，跳转
				else startupActivity(2);
				break;
			//投资信息费
			case R.id.project_service_fee:
//				UmengUtil.eventById(Activity_Project_Buy2.this, R.string.bid_buy_info3);
//				showServiceFee(mSimpleProjectDetail.Duration, mSimpleProjectDetail.DurationExchangeType, mSimpleProjectDetail.Rate,
//						mSimpleProjectDetail.RewardRate);
				break;
			//点击充值
			case R.id.layout_recharge:
				UmengUtil.eventById(Activity_Project_Buy2.this, R.string.bid_bug_charge);
				rechargeMoney();
				break;
			//点击购买
			case R.id.button_buy:
				UmengUtil.eventById(Activity_Project_Buy2.this, R.string.bid_buy_act2);
				clickButton();
				break;
			default:
				break;
		}
	}

	//***************************************************************去开通汇付*****************************************

	//充值
	private void rechargeMoney() {
		// 用户是否登录
		if (MyApplication.saveUserInfo.getAccess_Token() == null) {
			ToastUtil.alert(Activity_Project_Buy2.this, "请先登录");
			startActivity(new Intent(Activity_Project_Buy2.this, LoginActivity.class));
			return;
		}
		//已开通托管
		if (MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.HasOpenCustody)
			startActivity(new Intent(Activity_Project_Buy2.this, RechargeActivity.class));
		else startActivity(new Intent(Activity_Project_Buy2.this, FundCustodianAboutActivity.class));
	}

	//对话框

	//**************************************************** 跳转到选择优惠券页面**********************************
	private void startupActivity(int couponType) {
		Intent newIntent = new Intent(Activity_Project_Buy2.this, Activity_Select_Coupon.class);
		String newInput = invest_money.getText()
				.toString()
				.replace(",", "");
		float newInvestAmount = 0;
		if (!TextUtils.isEmpty(newInput)) {
			newInvestAmount = Float.parseFloat(newInput);
		}
		newIntent.putExtra("investAmount", newInvestAmount);
		newIntent.putExtra("CouponType", couponType);
		startActivityForResult(newIntent, 1);
		CouponId = null;
		CouponMinAmount = "0";
		CouponAmount = "0";
	}

	//******************************************************点击投资服务费图标实现蒙版效果*****************************
	private void showServiceFee(int duration, int durationExchangeType, float rate, float rewardRate) {
		//计算信息服务费
		double fee = 0.0;
		switch (durationExchangeType) {
			case 1:
				fee = (rate + rewardRate) * duration / 360 * 0.1;
				break;
			case 2:
				fee = (rate + rewardRate) * duration / 12 * 0.1;
				break;
			default:
				break;
		}

		//对话框
		DecimalFormat df = new DecimalFormat("######0.##");
		String value = df.format(fee);
		certDialog = new DialogWidget(Activity_Project_Buy2.this, getServiceFeeView("该标收取投资金额的" + value + "% 作为信息服务费"), true);
		certDialog.show();
	}

	protected View getServiceFeeView(String tips) {
		return MyMaskFullScreenView.getInstance(Activity_Project_Buy2.this, "信息服务费", tips, new MyMaskFullScreenView.OnClikListener() {
			@Override
			public void onViewClik() {
				certDialog.dismiss();
				certDialog = null;
			}
		})
				.getView();
	}

	//返回优惠券选择结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			//选择优惠券返回
			case 1:
				if (data != null) {
					UmengUtil.eventById(Activity_Project_Buy2.this, R.string.bid_buy_coupon_use);
					CouponId = data.getStringExtra("CouponId");
					CouponAmount = data.getStringExtra("CouponAmount");
					CouponMinAmount = data.getStringExtra("CouponMinAmount");
					coupon_description.setText(data.getStringExtra("ConditionDescn"));
					if ((CouponType & 1) == 1) {
						error_tips.setVisibility(View.GONE);
						coupon_description.setText(data.getStringExtra("ConditionDescn"));
						float amountMoney = data.getFloatExtra("Amount", 0);
						//将体验券金额显示成整数
						invest_money.setText((int) amountMoney + "");
						//计算收益
						countEarnMoney(amountMoney);
					} else if ((CouponType & 2) == 2) {
						error_tips.setVisibility(View.GONE);
						coupon_description.setText("投资满" + CouponMinAmount + "返现" + CouponAmount + "元");
					}
				} else getCouponCount();
				break;
			//认证成功返回
			case 10:
				if (data != null) {
					loadUserDetailInfoData();//加载银行卡信息
					getCouponCount();//加载用户信息
				}
				break;
			default:
				break;
		}
	}

	//注册广播，买标成功之后，刷新余额
	class Receiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			//充值成功
			if (intent.getAction()
					.equals("com.bcb.money.change.success")) getUserBanlance();//加载用户余额
				//设置交易密码成功
			else if (intent.getAction()
					.equals("com.bcb.passwd.setted")) loadUserDetailInfoData(); //更新用户信息
		}
	}

	//暂存标的订单号的信息
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("PackageId", packageId);
	}

	//取出暂存的订单号信息
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		packageId = savedInstanceState.getString("PackageId");
	}

	//销毁广播
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(buySuccessReceiver);
	}

	//输入框监听******************************************************************************************
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String input = invest_money.getText()
				.toString();

		if (!TextUtils.isEmpty(input)) {
			error_tips.setVisibility(View.GONE);
			//计算收益，在优惠那一栏中显示返现
			try {
				if (input.startsWith(".")) return;
				float money = Float.valueOf(input.replace(",", ""));
				if (!TextUtils.isEmpty(CouponMinAmount) && money >= Float.parseFloat(CouponMinAmount)) {
					prospective_earning.setText("投资满" + CouponMinAmount + "返现" + CouponAmount + "元");
				}
				//计算收益
				countEarnMoney(money);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else countEarnMoney(0);
	}

	@Override
	public void afterTextChanged(Editable s) {
		String text = invest_money.getText()
				.toString()
				.replace(",", "")
				.trim();
		float monery = 0;
		if (!TextUtils.isEmpty(text)) {
			if (text.startsWith(".")) return;
			monery = Float.valueOf(text);
		}
		//大于起投金额
		if (monery >= mSimpleProjectDetail.StartingAmount) {
			button_buy.setBackgroundResource(R.drawable.button_solid_red);
			button_buy.setClickable(true);
			button_buy.setTextColor(getResources().getColor(R.color.white));
			//尾标
		} else if (mSimpleProjectDetail.Balance < mSimpleProjectDetail.StartingAmount && monery == mSimpleProjectDetail.Balance) {
			button_buy.setBackgroundResource(R.drawable.button_solid_red);
			button_buy.setClickable(true);
			button_buy.setTextColor(getResources().getColor(R.color.white));
		} else {
			button_buy.setBackgroundResource(R.drawable.button_solid_xy);
			button_buy.setClickable(false);
			button_buy.setTextColor(0xff999999);
		}
	}

	//*********************************************************************** 计算收益***********************************
	private void countEarnMoney(float moneyinput) {
		// 如果传错了月标(1)、天标(2)，则不显示收益
//		if (countDate != 0) {
			//显示值
			prospective_earning.setVisibility(View.VISIBLE);
			LogUtil.i("bqt", "【金额】"+moneyinput);
			//如果输入金额为0时，不显示
			if (moneyinput == 0) {
				earnings_description.setText("预期收益");
				prospective_earning.setText("0.00");
				LogUtil.i("bqt", "【金额2】"+moneyinput);
			} else {
				// 年化基本收益
				float shouyi = moneyinput * mSimpleProjectDetail.MinPreInterest / 10000;
				LogUtil.i("bqt", "【收益】"+shouyi);

				//计算奖励收益
//				float jiangliamount = ((moneyinput * mSimpleProjectDetail.RewardRate) / countDate * 0.01f) * mSimpleProjectDetail
//						.Duration;
//				//奖励描述
//				String description = "";
//				//不是新手体验标的时候
//				if (mSimpleProjectDetail.RewardRateDescn != null && !mSimpleProjectDetail.RewardRateDescn.equalsIgnoreCase("null")) {
//					description = mSimpleProjectDetail.RewardRateDescn;
//				}
//				//设置奖励描述
//				prospective_earning.setText(rewardDescription(description, shouyi, jiangliamount));
				prospective_earning.setText(String.format("%.2f", shouyi));
			}
//		} else {
//			prospective_earning.setVisibility(View.GONE);
//		}
	}

	//设置奖励描述
	private String rewardDescription(String description, float shouyi, float jiangliamount) {
		String valueText = "";
		if (jiangliamount < 0.01) {
			prospective_earning_yuan.setVisibility(View.VISIBLE);
			valueText = MyTextUtil.delFloat(shouyi);
		} else {
			prospective_earning_yuan.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(description)) {
				valueText = MyTextUtil.delFloat(shouyi + jiangliamount) + "元" +
						"(含" + MyTextUtil.delFloat(jiangliamount) + "元" + "奖励)";
			} else {
				valueText = MyTextUtil.delFloat(shouyi + jiangliamount) + "元" +
						"(含" + MyTextUtil.delFloat(jiangliamount) + "元" + description + "奖励)";
			}
		}
		return valueText;
	}
}