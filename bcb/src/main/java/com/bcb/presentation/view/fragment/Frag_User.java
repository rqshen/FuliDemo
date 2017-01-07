package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.UserBankCard;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.data.util.DownloadUtils;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.activity.A_Elite_Loan;
import com.bcb.presentation.view.activity.A_MySecurity;
import com.bcb.presentation.view.activity.A_Slb;
import com.bcb.presentation.view.activity.Activity_Account_Setting;
import com.bcb.presentation.view.activity.Activity_Charge_HF;
import com.bcb.presentation.view.activity.Activity_Coupons;
import com.bcb.presentation.view.activity.Activity_Join_Company;
import com.bcb.presentation.view.activity.Activity_Login;
import com.bcb.presentation.view.activity.Activity_Money_Flowing_Water;
import com.bcb.presentation.view.activity.Activity_Open_Account;
import com.bcb.presentation.view.activity.Activity_Privilege_Money;
import com.bcb.presentation.view.activity.Activity_Trading_Record;
import com.bcb.presentation.view.activity.Activity_TuoGuan_HF;
import com.bcb.presentation.view.activity.Activity_WebView;
import com.bcb.presentation.view.activity.Activity_Withdraw;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.AlertView.UpdateDialog;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.presentation.view.custom.PullableView.PullableScrollView;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;

public class Frag_User extends Frag_Base implements OnClickListener {
	//标题
	private TextView title_text;
	ImageView layout_update_line;
	RelativeLayout layout_update;
	TextView tv_update;
	private Context ctx;
	//我的保险
	RelativeLayout layout_security;

	private TextView value_earn, value_balance, value_back, value_total;
	private UserWallet mUserWallet;
	private UserDetailInfo mUserDetailInfo;
	PullableScrollView layout_scrollview;

	//加载数据状态
	private boolean loadingStatus = true;
	//加载数据失败的状态
	private boolean loadingError = false;

	//加入公司
	private LinearLayout user_company_layout;
	private ImageView joinCompany;
	private TextView user_join_name;
	private TextView user_comany_shortname;
	//广播
	private Receiver receiver;
	//专属客服
	private RelativeLayout layout_customer_service;
	//电话客服
	private RelativeLayout layout_phone_service;

	//对话框
	private DialogWidget dialogWidget;
	private AlertView alertView;
	//刷新
	private PullToRefreshLayout refreshLayout;

	private BcbRequestQueue requestQueue;

	public Frag_User() {
		super();
	}

	@SuppressLint("ValidFragment")
	public Frag_User(Context ctx) {
		super();
		this.ctx = ctx;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_user, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.ctx = view.getContext();
		EventBus.getDefault()
				.register(this);
		requestQueue = App.getInstance()
				.getRequestQueue();
		//注册监听器
		receiver = new Receiver();
		ctx.registerReceiver(receiver, new IntentFilter("com.bcb.update.company.joined"));
		ctx.registerReceiver(receiver, new IntentFilter("com.bcb.login.success"));
		ctx.registerReceiver(receiver, new IntentFilter("com.bcb.logout.success"));
		//标题
		title_text = (TextView) view.findViewById(R.id.title_text);
		title_text.setText("我");

		tv_update = (TextView) view.findViewById(R.id.tv_update);
		layout_update = (RelativeLayout) view.findViewById(R.id.layout_update);
		layout_security = (RelativeLayout) view.findViewById(R.id.layout_security);
		layout_security.setOnClickListener(this);
		layout_update_line = (ImageView) view.findViewById(R.id.layout_update_line);
		if (App.isNeedUpdate && App.versionBean != null) {
			tv_update.setText("发现新版本 V" + App.versionBean.Version);
			layout_update.setOnClickListener(this);
		} else {
			layout_update.setVisibility(View.GONE);
			layout_update_line.setVisibility(View.GONE);
		}

		view.findViewById(R.id.ll_test)
				.setOnClickListener(this);
		//加入公司
		joinCompany = (ImageView) view.findViewById(R.id.join_company);
		layout_scrollview = (PullableScrollView) view.findViewById(R.id.layout_scrollview);
		joinCompany.setOnClickListener(this);

		//已经加入公司的LinearLayout及其元素
		user_company_layout = (LinearLayout) view.findViewById(R.id.user_company_layout);
		user_company_layout.setVisibility(View.GONE);
		user_join_name = (TextView) view.findViewById(R.id.user_join_name);
		user_join_name.setText("");
		user_comany_shortname = (TextView) view.findViewById(R.id.user_comany_shortname);
		user_comany_shortname.setText("");

		// 总资产
		value_earn = (TextView) view.findViewById(R.id.value_earn);
		// 用户余额
		value_balance = (TextView) view.findViewById(R.id.value_balance);
		// 待收本息
		value_back = (TextView) view.findViewById(R.id.value_back);
		// 冻结金额
		value_total = (TextView) view.findViewById(R.id.value_total);
		//叹号
		view.findViewById(R.id.freeze_amount_image)
				.setOnClickListener(this);
		//充值
		view.findViewById(R.id.recharge_btn)
				.setOnClickListener(this);
		//提现
		view.findViewById(R.id.withdraw_btn)
				.setOnClickListener(this);
		//投资记录
		view.findViewById(R.id.trading_record)
				.setOnClickListener(this);
		//资金流水
		view.findViewById(R.id.money_flow_water)
				.setOnClickListener(this);
		//借款
		view.findViewById(R.id.borrow_money)
				.setOnClickListener(this);
		//优惠券
		view.findViewById(R.id.coupons)
				.setOnClickListener(this);
		//特权本金
		view.findViewById(R.id.privilege_money)
				.setOnClickListener(this);
		//胜利包
		view.findViewById(R.id.managed_slb)
				.setOnClickListener(this);
		view.findViewById(R.id.managed_funds)
				.setOnClickListener(this);
		//账号设置
		view.findViewById(R.id.layout_account_settting)
				.setOnClickListener(this);
		//专属客服
		layout_customer_service = (RelativeLayout) view.findViewById(R.id.layout_customer_service);
		layout_customer_service.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//                showPopupWindow(false);
				String userId = null;

				//判断是否为空
				if (App.mUserDetailInfo != null) {
					userId = App.mUserDetailInfo.getCustomerId();
				}
				MQCustomerManager.getInstance(ctx)
						.showCustomer(userId);
			}
		});
		//        //专属客服提示
		//        user_customer_tips.setOnClickListener(new OnClickListener() {
		//            @Override
		//            public void onClick(View v) {
		//                showPopupWindow(false);
		//            }
		//        });
		//第一次安装的时候才显示专属提示
		if (!App.saveConfigUtil.isNotFirstRun()) {
			//            showPopupWindow(true);
			App.saveConfigUtil.setNotFirstRun(true);
		}
		//电话客服
		layout_phone_service = (RelativeLayout) view.findViewById(R.id.layout_phone_service);
		layout_phone_service.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPhoneService();
			}
		});
		//刷新
		(view.findViewById(R.id.loadmore_view)).setVisibility(View.GONE);
		refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
		refreshLayout.setRefreshResultView(false);
		refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				if (HttpUtils.isNetworkConnected(ctx)) {
					requestUserDetailInfo();
					requestUserWallet();
				} else {
					ToastUtil.alert(ctx, "网络异常，请稍后重试");
					refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		});

		if (App.saveUserInfo.getAccess_Token() != null) {
			requestUserDetailInfo();
			requestUserWallet();
		}
		//Token不存在时，则表示没有登陆
		else {
			//设置banner
			setupJoinCompanyMessage();
			//初始化余额信息
			showData();
		}
	}

	@Override
	public void onResume() {
		showData();
		super.onResume();
	}

	private void showPhoneService() {
		AlertView.Builder ibuilder = new AlertView.Builder(ctx);
		ibuilder.setTitle("是否电联客服？");
		ibuilder.setMessage("客服热线：020-38476886");
		ibuilder.setPositiveButton("立即联系", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:020-38476886"));
				ctx.startActivity(intent);
				alertView.dismiss();
			}
		});
		ibuilder.setNegativeButton("取消", null);
		alertView = ibuilder.create();
		alertView.show();
	}

	//从静态数据区中取出数据
	private void showData() {
		if (App.mUserWallet != null) {
			//总资产
			value_earn.setText("" + String.format("%.2f", App.mUserWallet.getTotalAsset()));
			//账户余额
			value_balance.setText("" + String.format("%.2f", App.mUserWallet.getBalanceAmount()));
			//待收本息
			value_back.setText("" + String.format("%.2f", App.mUserWallet.getIncomingMoney()));
			//冻结金额
			value_total.setText("" + String.format("%.2f", App.mUserWallet.getFreezeAmount()));
		} else {
			//总资产
			value_earn.setText("0.00");

			//账户余额
			value_balance.setText("0.00");
			//待收本息

			value_back.setText("0.00");
			//冻结金额
			value_total.setText("0.00");
		}

	}

	//加载用户加入公司的信息
	private void setupJoinCompanyMessage() {
		//如果mUserDetailInfo为空，则表示没有登陆
		if (App.mUserDetailInfo == null) {
			joinCompany.setVisibility(View.VISIBLE);
			user_company_layout.setVisibility(View.GONE);
			return;
		}
		//如果加入公司信息不为空并且状态值为10(通过)的时候，则显示用户名和加入公司的缩写
		if (App.mUserDetailInfo.MyCompany != null) {
			//审核通过
			if (!TextUtils.isEmpty(mUserDetailInfo.MyCompany.getShortName())) {
				joinCompany.setVisibility(View.GONE);
				user_company_layout.setVisibility(View.VISIBLE);
				user_comany_shortname.setText(mUserDetailInfo.MyCompany.getShortName());
				user_join_name.setText(mUserDetailInfo.UserName);
			} else {
				joinCompany.setVisibility(View.VISIBLE);

				user_company_layout.setVisibility(View.GONE);
			}
		}
		//如果加入公司信息为空的时候，则要判断是否要隐藏Banner
		else {
			user_company_layout.setVisibility(View.GONE);
			//根据标志为判断是否隐藏加入公司Banner
			if (App.viewJoinBanner) {
				joinCompany.setVisibility(View.VISIBLE);
			} else joinCompany.setVisibility(View.GONE);
		}
		layout_scrollview.scrollTo(0, 0);
		//        layout_scrollview.fullScroll(ScrollView.FOCUS_UP);
	}

	//******************************************************************************************
	DownloadCompleteReceiver downloadCompleteReceiver;
	String fileName;
	File apkFile;

	UpdateDialog updateDialog;
	private void showVersionDialog2() {
		 updateDialog = new UpdateDialog(ctx) {
			@Override
			public void onClick() {
				super.onClick();
				registerReceiver();
				dismiss();
			}
		};
		updateDialog.setValues(View.VISIBLE, false, getTips());
		updateDialog.show();
	}

	public String getTips() {
		StringBuilder sb = new StringBuilder("");
		List<String> tips = App.versionBean.Tips;
		if (tips != null && tips.size() == 1) {
			updateDialog.setTVGravity(Gravity.CENTER);
			return tips.get(0);
		} else if (tips != null && tips.size() > 1) {
			updateDialog.setTVGravity(Gravity.LEFT);
			for (int i = 0; i < tips.size(); i++) {
				sb.append("" + (i + 1) + "、" + tips.get(i) + "\n");
			}
			return sb.deleteCharAt(sb.length() - 1).toString();
		} else return null;
	}

	private void registerReceiver() {
		Toast.makeText(ctx, "正在下载新版本安装包", Toast.LENGTH_SHORT)
				.show();
		downloadCompleteReceiver = new DownloadCompleteReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);//下载完成的动作
		ctx.registerReceiver(downloadCompleteReceiver, intentFilter);
		fileName = "fljr-" + App.versionBean.Increment + ".apk";
		apkFile = new File(Environment.getExternalStorageDirectory()
				.getPath() + DownloadUtils.FILE_PATH + File.separator + fileName);
		DownloadUtils.downLoadFile(ctx, App.versionBean.Url, fileName);//开始下载
	}

	class DownloadCompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				LogUtil.i("bqt", "下载完毕");
				installApk(context);
				if (downloadCompleteReceiver != null) {
					ctx.unregisterReceiver(downloadCompleteReceiver);
					downloadCompleteReceiver = null;
				}
			}
		}
	}

	private void installApk(Context context) {
		Intent mIntent = new Intent(Intent.ACTION_VIEW);
		mIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mIntent);
	}

	//******************************************************************************************
	//点击事件
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ll_test) {

			return;
		}
		if (v.getId() == R.id.layout_update) {
			showVersionDialog2();
			return;
		}
		//除了专属客服和电话客服之外的职位都要在点击之前登陆
		if (App.saveUserInfo.getAccess_Token() == null) {
			Activity_Login.launche(ctx);
			return;
		}
		switch (v.getId()) {
			//我的保险
			case R.id.layout_security:
				startActivity(new Intent(ctx, A_MySecurity.class));
				break;
			//加入公司
			case R.id.join_company:
				UmengUtil.eventById(ctx, R.string.self_auth_c);
				toJoinCompany();
				break;
			//冻结金额提示
			case R.id.freeze_amount_image:
				Toast.makeText(ctx, "投资未计息本金、借款保证金等", Toast.LENGTH_SHORT)
						.show();
				break;

			//点击充值按钮
			case R.id.recharge_btn:
				rechargeMoney();
				break;

			//点击提现按钮
			case R.id.withdraw_btn:
				withdrawMoney();
				break;

			//投资记录
			case R.id.trading_record:
				if (isLoading()) return;
				UmengUtil.eventById(ctx, R.string.self_tzjl);
				Activity_Trading_Record.launche(ctx);
				break;

			//资金流水，交易明细
			case R.id.money_flow_water:
				if (isLoading()) return;
				UmengUtil.eventById(ctx, R.string.self_zjls);
				Activity_Money_Flowing_Water.launche(ctx);
				break;

			//借款--员工贷
			case R.id.borrow_money:
				UmengUtil.eventById(ctx, R.string.loan_blank);
				loanMainPage();
				break;

			// 优惠券
			case R.id.coupons:
				if (isLoading()) return;
				UmengUtil.eventById(ctx, R.string.self_coupon);
				startActivityForResult(new Intent(ctx, Activity_Coupons.class), 1);
				break;

			// 特权本金
			case R.id.privilege_money:
				if (isLoading()) return;
				UmengUtil.eventById(ctx, R.string.self_invate);
				Activity_Privilege_Money.launch(ctx);
				break;

			// 资金托管
			case R.id.managed_funds:
				managedFunds();
				break;
			// 圣力宝
			case R.id.managed_slb:
				slbOpen();
				break;

			// 账号设置
			case R.id.layout_account_settting:
				UmengUtil.eventById(ctx, R.string.self_setting);
				Activity_Account_Setting.launche(ctx);
				break;
		}
	}

	//加入公司
	private void toJoinCompany() {
		if (mUserDetailInfo == null || !mUserDetailInfo.HasOpenCustody) {
			startActivity(new Intent(ctx, Activity_Open_Account.class));
		}
		//否则需要判断MyCompany字段
		else {
			//未申请
			if (App.mUserDetailInfo.MyCompany == null) {
				Activity_Join_Company.launche(ctx);
			}
			//审核中
			else if (App.mUserDetailInfo.MyCompany.Status == 5) {
				companyAlertView("您的认证申请正在审核", "预计2个工作日内完成，请耐心等候");
			}
			//拉黑
			else if (App.mUserDetailInfo.MyCompany.Status == 15) {
				companyAlertView("提示", "你已被拉入黑名单\n详情请咨询工作人员");
			}
		}
	}

	//借款
	private void loanMainPage() {
		//已开通托管
		if (isLoading()) return;
		if (App.mUserDetailInfo != null && App.mUserDetailInfo.HasOpenCustody) startActivity(new Intent(ctx, A_Elite_Loan.class));
		else startActivity(new Intent(ctx, Activity_Open_Account.class));
	}

	//充值
	private void rechargeMoney() {
		if (isLoading()) return;
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
		if (isLoading()) return;
		//没开通托管
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

	//资金托管
	private void managedFunds() {
		if (isLoading()) return;
		if (App.mUserDetailInfo.HasOpenCustody) {//已开通托管
			startActivity(new Intent(ctx, Activity_TuoGuan_HF.class));
		} else startActivity(new Intent(ctx, Activity_Open_Account.class));
	}

	//生利宝
	private void slbOpen() {
		if (isLoading()) return;
		//已开通托管
		if (App.mUserDetailInfo != null && App.mUserDetailInfo.HasOpenCustody) startActivity(new Intent(ctx, A_Slb.class));
		else startActivity(new Intent(ctx, Activity_Open_Account.class));
	}

	//判断状态，如果获取数据中或者获取数据失败，都表示依旧要加载数据
	private boolean isLoading() {
		if (loadingStatus) {
			//加载数据失败
			if (loadingError) ToastUtil.alert(ctx, "获取用户数据失败，请刷新重试");
				//获取用户数据过程中
			else ToastUtil.alert(ctx, "您的网络不稳定，加载中");
			return true;
		}
		return false;
	}

	/***************************
	 * 审核中
	 *************************/
	private void companyAlertView(String title, String contentMessage) {
		AlertView.Builder ibuilder = new AlertView.Builder(ctx);
		ibuilder.setTitle(title);
		ibuilder.setMessage(contentMessage);
		ibuilder.setPositiveButton("知道了", null);
		alertView = ibuilder.create();
		alertView.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			//选择优惠券返回
			case 1:
				if (data != null) {
					Intent intent = new Intent();
					//跳转至首页
					intent.setAction("com.bcb.update.mainui");
					ctx.sendBroadcast(intent);
				}
				break;
		}
	}

	//广播
	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//加入公司
			if (intent.getAction()
					.equals("com.bcb.update.company.joined")) {
				setupJoinCompanyMessage();
			}
			//登陆成功
			else if (intent.getAction()
					.equals("com.bcb.login.success")) {
				refreshLayout.autoRefresh();
			}        //注销成功
			else if (intent.getAction()
					.equals("com.bcb.logout.success")) {
				LogUtil.i("bqt", "【Receiver】【onReceive】注销");
				joinCompany.setVisibility(View.VISIBLE);
				user_company_layout.setVisibility(View.GONE);
				layout_scrollview.scrollTo(0, 0);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ctx.unregisterReceiver(receiver);
		EventBus.getDefault()
				.unregister(this);
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
	 * 用户信息
	 */
	private void requestUserDetailInfo() {

		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "用户信息返回数据：" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					JSONObject data = PackageUtil.getResultObject(response);
					//判断JSON对象是否为空
					if (data != null) {
						mUserDetailInfo = App.mGson.fromJson(data.toString(), UserDetailInfo.class);
						//将获取到的银行卡数据写入静态数据区中
						App.mUserDetailInfo = mUserDetailInfo;
						requestUserBankCard();
						requestUserSecurity();
						//加载用户加入公司的信息
						setupJoinCompanyMessage();
					}
				}
			}

			@Override
			public void onErrorResponse(Exception error) {

			}
		});
		jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
		requestQueue.add(jsonRequest);
	}

	/**
	 * 用户钱包
	 */
	private void requestUserWallet() {
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(ctx), new
				BcbRequest.BcbCallBack<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						LogUtil.i("bqt", "请求用户资产账户返回：" + response.toString());
						if (PackageUtil.getRequestStatus(response, ctx)) {
							JSONObject data = PackageUtil.getResultObject(response);
							if (data != null) {
								//注意数据结构变了，2016-7-26
								mUserWallet = App.mGson.fromJson(data.toString(), UserWallet.class);
								App.mUserWallet = mUserWallet;
								showData();
							}
						}
						refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
					}

					@Override
					public void onErrorResponse(Exception error) {
						ToastUtil.alert(ctx, "网络异常，请稍后重试");
						refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
						loadingError = true;
					}
				});
		jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
		requestQueue.add(jsonRequest);
	}

	/**
	 * 用户银行卡
	 */
	private void requestUserBankCard() {
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UrlUserBand, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "绑定的银行卡：" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					JSONObject data = PackageUtil.getResultObject(response);
					if (data != null && App.mUserDetailInfo != null) {
						App.mUserDetailInfo.BankCard = App.mGson.fromJson(data.toString(), UserBankCard.class);
					}
				}
				loadingStatus = false;
				refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}

			@Override
			public void onErrorResponse(Exception error) {
				ToastUtil.alert(ctx, "网络异常，请稍后重试");
				refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				loadingError = true;
			}
		});
		jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
		requestQueue.add(jsonRequest);
	}
	/**
	 * 保险
	 */
	private void requestUserSecurity() {
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.MYINSURANCE, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "保险：" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					App.mUserDetailInfo.CarInsuranceIndexPage=response.optJSONObject("result").optString("CarInsuranceIndexPage");
					App.mUserDetailInfo.CarInsuranceMyOrderPage=response.optJSONObject("result").optString("CarInsuranceMyOrderPage");
					App.mUserDetailInfo.GroupInsuranceUrl=response.optJSONObject("result").optString("GroupInsuranceUrl");
				}
				loadingStatus = false;
				refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}

			@Override
			public void onErrorResponse(Exception error) {
				ToastUtil.alert(ctx, "网络异常，请稍后重试");
				refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				loadingError = true;
			}
		});
		jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
		requestQueue.add(jsonRequest);
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

	//***************************************************************************************************************************************
	//接收事件
	public void onEventMainThread(BroadcastEvent event) {
		String flag = event.getFlag();
		if (!TextUtils.isEmpty(flag)) {
			LogUtil.i("bqt", "【Frag_User】【onE  ventMainThread】状态：" + flag);
			switch (flag) {
				case BroadcastEvent.LOGOUT:
					joinCompany.setVisibility(View.VISIBLE);
					user_company_layout.setVisibility(View.GONE);
					layout_scrollview.scrollTo(0, 0);
					break;
				case BroadcastEvent.LOGIN:
				case BroadcastEvent.REFRESH:
					break;
			}
		}
	}
}