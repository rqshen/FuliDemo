package com.bcb.module.discover.financialproduct.normalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.module.login.LoginActivity;
import com.bcb.module.myinfo.balance.FundCustodianWebActivity;
import com.bcb.module.myinfo.balance.FundCustodianAboutActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.project.SimpleProjectDetail;
import com.bcb.presentation.view.activity.Activity_Project_Buy;
import com.bcb.utils.HttpUtils;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MQCustomerManager;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.utils.UmengUtil;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.bcb.R.id.back_img;

/**
 * setTitleValue("产品详情"。setTitleValue("详情"。setTitleValue("项目详情"。setTitleValue("立即购买"。setTitleValue("立即申购"。
 */
public class NormalProjectIntroductionActivity extends Activity_Base implements View.OnClickListener {

	//立即购买按钮
	private Button button_buy;

	//标的数据
	private SimpleProjectDetail mSimpleProjectDetail;

	private ProgressDialog mProgressBar;
	private String packageId = "";
	private TextView value_lilv;
	private TextView text_description;
	private TextView tv_qx;
	private LinearLayout add_rate;
	private TextView value_reward;
	private TextView total_money;
	private TextView time_value;
	private TextView miniValue_invest;
	private LinearLayout layout_investment;
	private TextView value_description;
	private ProgressBar progress_percent;
	private TextView value_percent;
	private TextView payment_type;
	private TextView end_time;
	private RelativeLayout layout_examine;
	private RelativeLayout layout_source;
	private TextView source_inspector;
	private RelativeLayout layout_company_hr;
	private TextView text_company;
	private TextView company_inspector;
	private RelativeLayout layout_advisory;
	private TextView text_advisers;
	private TextView beset_advisers;
	private LinearLayout layout_detail;
	private RelativeLayout layout_project_detail;
	private RelativeLayout layout_security;
	private RelativeLayout layout_identify;
	private RelativeLayout layout_invest_list;
	private TextView investLeader;
	private DialogWidget dialogWidget;
	private LinearLayout layout_customer_service;

	private int CouponType = 0;
	private int countDate = 0;

	//初始化******************************************************************************************
	//0正常标，1转让标，2福鸡包
	private int type = 0;

	public static void launche2(Context ctx, String pid, int CouponType, int type) {
		Intent intent = new Intent();
		intent.putExtra("pid", pid);
		intent.putExtra("CouponType", CouponType);
		intent.putExtra("type", type);
		intent.setClass(ctx, NormalProjectIntroductionActivity.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance()
				.pushOneActivity(NormalProjectIntroductionActivity.this);
		if (getIntent() != null) {
			packageId = getIntent().getStringExtra("pid");
			CouponType = getIntent().getIntExtra("CouponType", 0);
			type = getIntent().getIntExtra("type", 0);
		}
		setBaseContentView(R.layout.activity_normalproject_introduction);
		setLeftTitleVisible(true);
		layout_title.setBackgroundColor(getResources().getColor(R.color.red));
		title_text.setTextColor(getResources().getColor(R.color.white));
		dropdown.setImageResource(R.drawable.return_delault);
		dropdown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		(findViewById(back_img)).setVisibility(View.GONE);


		UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.buy_home);
		//初始化界面
		setupView();
		//加载普通标的数据
		loadProjectData();
		//获取用户信息
		loadUserDetailInfoData();
	}

	//*********************************************************初始化界面**********************************************
	private void setupView() {
		showProgressBar("正在获取标的数据...");
		// 年化利率
		value_lilv = (TextView) findViewById(R.id.value_lilv);
		text_description = (TextView) findViewById(R.id.text_description);
		text_description.setVisibility(View.GONE);
		//加息利率
		add_rate = (LinearLayout) findViewById(R.id.add_rate);
		value_reward = (TextView) findViewById(R.id.value_reward);
		//福袋数据
		if (!TextUtils.isEmpty(MyApplication.getInstance()
				.getWelfare())) {
			add_rate.setVisibility(View.VISIBLE);
			value_reward.setText("+" + MyApplication.getInstance()
					.getWelfare() + "%");
		} else add_rate.setVisibility(View.GONE);
		//可投金额
		total_money = (TextView) findViewById(R.id.value_total);
		tv_qx = (TextView) findViewById(R.id.tv_qx);
		// 理财期限
		time_value = (TextView) findViewById(R.id.time_value);
		//融资总额（元）
		miniValue_invest = (TextView) findViewById(R.id.value_invest_mini);
		//本息保障
		layout_investment = (LinearLayout) findViewById(R.id.layout_investment);
		layout_investment.setOnClickListener(this);
		//描述
		value_description = (TextView) findViewById(R.id.value_description);
		value_description.setVisibility(View.GONE);
		// 立即购买按钮
		button_buy = (Button) findViewById(R.id.button_buy);
		button_buy.setOnClickListener(this);
		//************************************************** 进度
		//投资进度条
		progress_percent = (ProgressBar) findViewById(R.id.progress_percent);
		progress_percent.setMax(100);
		progress_percent.setProgress(0);
		progress_percent.setSecondaryProgress(0);
		//投资进度百分比
		value_percent = (TextView) findViewById(R.id.value_percent);
		//还款方式
		payment_type = (TextView) findViewById(R.id.payment_type);
		//投标截止日期
		end_time = (TextView) findViewById(R.id.end_time);
		//*************************************************** 风控
		//风控整体
		layout_examine = (RelativeLayout) findViewById(R.id.layout_examine);
		//借款来源公司
		layout_source = (RelativeLayout) findViewById(R.id.layout_source);
		layout_source.setOnClickListener(this);
		source_inspector = (TextView) findViewById(R.id.source_inspector);
		//公司审查员
		text_company = (TextView) findViewById(R.id.text_company);
		layout_company_hr = (RelativeLayout) findViewById(R.id.layout_company_hr);
		layout_company_hr.setOnClickListener(this);
		company_inspector = (TextView) findViewById(R.id.company_inspector);
		//风控顾问团
		text_advisers = (TextView) findViewById(R.id.text_advisers);
		layout_advisory = (RelativeLayout) findViewById(R.id.layout_advisory);
		layout_advisory.setOnClickListener(this);
		beset_advisers = (TextView) findViewById(R.id.beset_advisers);
		//********************************************** 项目详情介绍
		//项目详情整体
		layout_detail = (LinearLayout) findViewById(R.id.layout_detail);
		//项目详情
		layout_project_detail = (RelativeLayout) findViewById(R.id.layout_project_detail);
		layout_project_detail.setOnClickListener(this);
		//安全保障
		layout_security = (RelativeLayout) findViewById(R.id.layout_security);
		layout_security.setOnClickListener(this);
		//证明文件图片
		layout_identify = (RelativeLayout) findViewById(R.id.layout_identify);
		layout_identify.setOnClickListener(this);
		//投资列表
		layout_invest_list = (RelativeLayout) findViewById(R.id.layout_invest_list);
		layout_invest_list.setOnClickListener(this);
		//领投人
		investLeader = (TextView) findViewById(R.id.investLeader);
		//专属客服
		layout_customer_service = (LinearLayout) findViewById(R.id.layout_customer_service);
		layout_customer_service.setOnClickListener(this);
	}

	//***********************************************************加载普通标的数据***************************************************
	private void loadProjectData() {
		JSONObject jObject = new JSONObject();
		try {
			jObject.put("PackageId", packageId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = UrlsTwo.NormalProjectIntroduction;//普通标
		//注意：债权标和普通标使用不同的接口
		switch (type) {
			case 1:
				url = UrlsTwo.CLAIMCONVEYPACKAGEDETAIL;//债权标
				break;
			case 2:
				url = UrlsTwo.GETMONKEYPACKAGEDETAIL;//福鸡宝
				break;
		}
		LogUtil.i("bqt", "【请求地址】"+url+"【packageId】"+packageId);

		BcbJsonRequest jsonRequest = new BcbJsonRequest(url, jObject, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				if (type == 1 || type == 2 || PackageUtil.getRequestStatus(response, NormalProjectIntroductionActivity.this)) {
					LogUtil.i("bqt", "【标类型】" + type + "【返回内容】" + response.toString());
					try {
						//先转义
						String resultString = response.getString("result")
								.replace("\\", "")
								.replace("\"[", "[")
								.replace("]\"", "]");
						JSONObject resultObject = new JSONObject(resultString);
						//注意：不去掉会出现json解析语法错误
						if (TextUtils.isEmpty(resultObject.getString("AssetAuditContent"))) resultObject.remove("AssetAuditContent");
						mSimpleProjectDetail = MyApplication.mGson.fromJson(resultObject.toString(), SimpleProjectDetail.class);
						//显示标的数据
						if (null != mSimpleProjectDetail) showProjectData();
					} catch (Exception e) {
						e.printStackTrace();
						LogUtil.i("bqt", "【标详情】" + e.toString());
					}
				}
				hideProgressBar();
			}

			@Override
			public void onErrorResponse(Exception error) {
				ToastUtil.alert(NormalProjectIntroductionActivity.this, "网络异常，请稍后重试");
				hideProgressBar();
			}
		});
		jsonRequest.setTag(BcbRequestTag.NormalProjectIntroductionTag);
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	//**********************************************************显示标的数据 **********************************************
	private void showProjectData() {
		setTitleValue(mSimpleProjectDetail.Name);
		// 年化利率
		value_lilv.setText(String.format("%.2f", mSimpleProjectDetail.Rate));
		text_description.setVisibility(View.VISIBLE);

		//领投人
		if (!TextUtils.isEmpty(mSimpleProjectDetail.InvestLeader)) investLeader.setText(mSimpleProjectDetail.InvestLeader);

		//奖励描述
		if (!TextUtils.isEmpty(mSimpleProjectDetail.RewardRateDescn)) {
			value_description.setVisibility(View.VISIBLE);
			value_description.setText("返" + mSimpleProjectDetail.RewardRateDescn);
		}

		//可投金额
		total_money.setText(String.format("%.2f", mSimpleProjectDetail.Balance));

		//理财期限
		switch (mSimpleProjectDetail.DurationExchangeType) {
			case 1:
				//这里转成字符串的原因是防止出现NullPointerException崩溃
				time_value.setText(String.format("%d", mSimpleProjectDetail.Duration) + "天");
				countDate = 360;
				break;
			case 2:
				//这里转成字符串的原因是防止出现NullPointerException崩溃
				time_value.setText(String.format("%d", mSimpleProjectDetail.Duration) + "个月");
				countDate = 12;
				break;
			default:
				time_value.setText("");
				break;
		}

		//融资总额
		miniValue_invest.setText(String.format("%.2f", mSimpleProjectDetail.Amount));

		//投标进度条
		progress_percent.setProgress((int) (100 - (mSimpleProjectDetail.Balance / mSimpleProjectDetail.Amount) * 100));
		progress_percent.setSecondaryProgress((int) (100 - (mSimpleProjectDetail.Balance / mSimpleProjectDetail.Amount) * 100));

		//投资进度百分比
		value_percent.setText(String.format("%.2f", (1 - mSimpleProjectDetail.Balance / mSimpleProjectDetail.Amount) * 100) + "%");

		//还款方式
		payment_type.setText(mSimpleProjectDetail.PaymentType);

		try {
			Date ApplyEndTime = new SimpleDateFormat("yyyy-MM-dd").parse(mSimpleProjectDetail.ApplyEndTime);
			Date InterestTakeDate = new SimpleDateFormat("yyyy-MM-dd").parse(mSimpleProjectDetail.InterestTakeDate);
			//截止日期
			end_time.setText(SimpleDateFormat.getDateInstance()
					.format(ApplyEndTime));
			//起息日
			tv_qx.setText(SimpleDateFormat.getDateInstance()
					.format(InterestTakeDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//借款来源公司
		if (!TextUtils.isEmpty(mSimpleProjectDetail.CompanyName)) {
			layout_source.setVisibility(View.VISIBLE);
			source_inspector.setText(mSimpleProjectDetail.CompanyName);
		} else layout_source.setVisibility(View.GONE);

		if (mSimpleProjectDetail.AssetAuditContent != null) {
			layout_examine.setVisibility(View.VISIBLE);
			//公司审查员标题
			text_company.setText(mSimpleProjectDetail.AssetAuditContent.get(0).Title);
			//公司审查员内容
			company_inspector.setText(mSimpleProjectDetail.AssetAuditContent.get(0).Content);
			//风控顾问团标题
			text_advisers.setText(mSimpleProjectDetail.AssetAuditContent.get(1).Title);
			//风控顾问团内容
			beset_advisers.setText(mSimpleProjectDetail.AssetAuditContent.get(1).Content);
		} else layout_examine.setVisibility(View.GONE);

		//项目详情
		layout_detail.setVisibility(View.VISIBLE);

		//设置按钮颜色
		setButtonColor();
	}

	//***********************************************设置按钮不可用时的颜色和可用状态*********************************************
	private void setButtonColor() {
		try {
			Date beginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(mSimpleProjectDetail.ApplyBeginTime);
			Date nowTime = new Date();
			LogUtil.i("bqt", "【允许购买时间-当前时间(小时)】" + ((beginTime.getTime() - nowTime.getTime()) / 1000 / 60 / 60));
			if (nowTime.getTime() < beginTime.getTime() || mSimpleProjectDetail.Status != 20) {
				button_buy.setBackgroundResource(R.drawable.button_project_gray);
				button_buy.setTextColor(getResources().getColor(R.color.project_button_gray));
				button_buy.setEnabled(false);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	//******************************************************************************************

	/**
	 * APP自动投标流程：
	 * 1、新用户买标，没有开通托管账户的引导到汇付开通托管账户。
	 * 2、已开通托管账户用户买标没开通自动买标的，引导到汇付开通自动投标。
	 * 3、开通自动投标完毕，手动买入理财标。
	 */
	private void autoOpen() {
		String requestUrl = UrlsTwo.OPENAUTOTENDERPLAN;
		String encodeToken = TokenUtil.getEncodeToken(NormalProjectIntroductionActivity.this);
		JSONObject obj = new JSONObject();
		try {
			obj.put("Platform", 2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, obj, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "开通自动投标" + response.toString());
				if (PackageUtil.getRequestStatus(response, NormalProjectIntroductionActivity.this)) {
					try {
						/** 后台返回的JSON对象，也是要转发给汇付的对象 */
						JSONObject result = PackageUtil.getResultObject(response);
						if (result != null) {
							//网页地址
							String postUrl = result.optString("PostUrl");
							result.remove("PostUrl");//移除这个参数
							//传递的参数
							String postData = HttpUtils.jsonToStr(result.toString()); //跳转到webview
							FundCustodianWebActivity.launche(NormalProjectIntroductionActivity.this, "开启份额锁", postUrl, postData);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "开通自动投标2" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(NormalProjectIntroductionActivity.this, response.optString("message"), Toast.LENGTH_SHORT)
							.show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【Activity_TuoGuan_HF】【loginAccount】网络异常，请稍后重试" + error.toString());
			}
		});
		MyApplication.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 10:
				if (data != null) loadUserDetailInfoData();
				break;
			default:
				break;
		}
	}

	//*******************************************************获取用户银行卡信息 ***************************************
	private void loadUserDetailInfoData() {
		//如果Token为空或者银行卡信息不为空，则停止请求
		if (MyApplication.saveUserInfo.getAccess_Token() == null || MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.BankCard != null) {
			return;
		} else {
			BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest
					.BcbCallBack<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {

						int status = response.getInt("status");
						if (status == 1) {
							JSONObject data = PackageUtil.getResultObject(response);
							UserDetailInfo mUserDetailInfo;
							//判断JSON对象是否为空
							if (data != null) {
								mUserDetailInfo = MyApplication.mGson.fromJson(data.toString(), UserDetailInfo.class);
								//将用户信息写入静态数据区
								if (mUserDetailInfo != null) {
									MyApplication.mUserDetailInfo = mUserDetailInfo;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onErrorResponse(Exception error) {
					ToastUtil.alert(NormalProjectIntroductionActivity.this, "网络异常，请稍后再试");
				}
			});
			jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
			MyApplication.getInstance()
					.getRequestQueue()
					.add(jsonRequest);
		}
	}

	//************************************************************转圈提示******************************************
	//显示转圈提示
	private void showProgressBar(String title) {
		if (null == mProgressBar) mProgressBar = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		mProgressBar.setMessage(title);
		mProgressBar.setCanceledOnTouchOutside(false);
		mProgressBar.setCancelable(true);
		mProgressBar.show();
	}

	//隐藏转圈提示
	private void hideProgressBar() {
		if (!isFinishing() && null != mProgressBar && mProgressBar.isShowing()) {
			mProgressBar.dismiss();
		}
	}

	//*************************************************************蒙版效果提示****************************************
	private void showAdviserTips(String tips) {
		dialogWidget = new DialogWidget(NormalProjectIntroductionActivity.this, getAdviserView(tips), true);
		dialogWidget.show();
	}

	protected View getAdviserView(String tips) {
		return MyMaskFullScreenView.getInstance(NormalProjectIntroductionActivity.this, tips, new MyMaskFullScreenView
				.OnClikListener() {
			@Override
			public void onViewClik() {
				dialogWidget.dismiss();
				dialogWidget = null;
			}
		})
				.getView();
	}

	@Override
	public void onBackPressed() {
		UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_back);
		finish();
	}

	//点击事件**************************************************************************************************************
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//本息保障
			case R.id.layout_investment:
				if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
					UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_info2);
				}
				showAdviserTips("该项目受福利金融风险保证金保障");
				break;

			//专属客服
			case R.id.layout_customer_service:
				UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_kefu);
				//如果ID存在
				String userId = null;
				if (MyApplication.mUserDetailInfo != null) {
					userId = MyApplication.mUserDetailInfo.getCustomerId();
				}
				MQCustomerManager.getInstance(this)
						.showCustomer(userId);
				break;

			//点击立即购买按钮
			case R.id.button_buy:
				if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
					UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_act);
				}
				clickButton();
				break;

			//借款来源公司
			case R.id.layout_source:
				ProjectDetailActivity.launche(this, TextUtils.isEmpty(mSimpleProjectDetail.CompanyName) ? "借款来源公司详情" :
						mSimpleProjectDetail.CompanyName, mSimpleProjectDetail.CompanyUrl);
				break;

			//风控审查员
			case R.id.layout_company_hr:
				if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
					UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_info);
				}
				if (mSimpleProjectDetail.AssetAuditContent.get(0).Introduction != null && !mSimpleProjectDetail.AssetAuditContent.get
						(0).Introduction.equalsIgnoreCase("null") && !mSimpleProjectDetail.AssetAuditContent.get(0).Introduction
						.equalsIgnoreCase("")) {
					showAdviserTips(mSimpleProjectDetail.AssetAuditContent.get(0).Introduction);
				}
				break;

			//风控顾问团
			case R.id.layout_advisory:
				if (mSimpleProjectDetail.AssetAuditContent.get(1).Introduction != null && !mSimpleProjectDetail.AssetAuditContent.get
						(1).Introduction.equalsIgnoreCase("null") && !mSimpleProjectDetail.AssetAuditContent.get(1).Introduction
						.equalsIgnoreCase("")) {
					showAdviserTips(mSimpleProjectDetail.AssetAuditContent.get(1).Introduction);
				}
				break;

			//项目详情
			case R.id.layout_project_detail:
				if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
					UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_detail1);
				}
				if (null != mSimpleProjectDetail && !TextUtils.isEmpty(mSimpleProjectDetail.PageUrl)) {
					ProjectDetailActivity.launche2(NormalProjectIntroductionActivity.this, mSimpleProjectDetail.Name,
							mSimpleProjectDetail.PageUrl + "&tab=0", 20095);
					//							"http://192.168.1.200:7073/", 20095);
				}
				break;
			//保障信息
			case R.id.layout_security:
				if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
					UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_detail2);
				}
				if (null != mSimpleProjectDetail && !TextUtils.isEmpty(mSimpleProjectDetail.PageUrl)) {
					ProjectDetailActivity.launche2(NormalProjectIntroductionActivity.this, mSimpleProjectDetail.Name,
							mSimpleProjectDetail.PageUrl + "&tab=1", 20095);
					//							"http://192.168.1.200:7073/", 20095);
				}
				break;
			//证明文件
			case R.id.layout_identify:
				if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
					UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_detail3);
				}
				if (null != mSimpleProjectDetail && !TextUtils.isEmpty(mSimpleProjectDetail.PageUrl)) {
					ProjectDetailActivity.launche2(NormalProjectIntroductionActivity.this, mSimpleProjectDetail.Name,
							mSimpleProjectDetail.PageUrl + "&tab=3", 20095);
					//							"http://192.168.1.200:7073/", 20095);
				}
				break;
			//投资列表
			case R.id.layout_invest_list:
				if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
					UmengUtil.eventById(NormalProjectIntroductionActivity.this, R.string.bid_buy_detail4);
				}
				if (null != mSimpleProjectDetail && !TextUtils.isEmpty(mSimpleProjectDetail.PageUrl)) {
					ProjectDetailActivity.launche2(NormalProjectIntroductionActivity.this, mSimpleProjectDetail.Name,
							mSimpleProjectDetail.PageUrl + "&tab=2", 20095);
					//							"http://192.168.1.200:7073/", 20095);
				}
				break;
		}
	}

	//********************************************************************购买*******************************************************
	private void clickButton() {
		//判断是否存在买标数据
		if (mSimpleProjectDetail == null) {
			ToastUtil.alert(NormalProjectIntroductionActivity.this, "无法获取买标数据");
			return;
		}

		//判断可投金额是否大于0元
		if (mSimpleProjectDetail.Balance <= 0) {
			ToastUtil.alert(NormalProjectIntroductionActivity.this, "可投金额为0元，该标不能投");
			return;
		}

		//还没有登陆时，跳转至登陆页面
		if (MyApplication.saveUserInfo.getAccess_Token() == null) {
			startActivity(new Intent(NormalProjectIntroductionActivity.this, LoginActivity.class));
			finish();
			return;
		}

		//没有开通汇付托管
		if (MyApplication.mUserDetailInfo == null || !MyApplication.mUserDetailInfo.HasOpenCustody) {
			startActivity(new Intent(this, FundCustodianAboutActivity.class));
			finish();
			return;
		}

		//没有开通自动投标
		if ((type == 1 || type == 2) && !MyApplication.mUserDetailInfo.AutoTenderPlanStatus) {
			altDialog();
			return;
		}

		//跳转到购买页面
		Activity_Project_Buy.launche2(this, packageId, mSimpleProjectDetail.Name, CouponType, countDate, mSimpleProjectDetail, type);
	}

	AlertView alertView;

	private void altDialog() {
		AlertView.Builder ibuilder = new AlertView.Builder(this);
		ibuilder.setTitle("开启份额锁，100%成功买入");
		ibuilder.setMessage("锁定份额，自动买入");
		ibuilder.setPositiveButton("开启", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				autoOpen();
				alertView.dismiss();
			}
		});
		ibuilder.setNegativeButton("取消", null);
		alertView = ibuilder.create();
		alertView.show();
	}
}