package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.UserExtraInfo;
import com.bcb.data.bean.loan.LoanDurationListBean;
import com.bcb.data.bean.loan.LoanKindBean;
import com.bcb.data.bean.loan.LoanPeriodWithRateBean;
import com.bcb.data.bean.loan.LoanRequestInfoBean;
import com.bcb.data.util.DbUtil;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.MyConstants;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ProgressDialogrUtils;
import com.bcb.data.util.SpinnerWheelUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 借款
 */
public class Activity_LoanRequest_Borrow extends Activity_Base {
	@BindView(R.id.banner_image) ImageView banner_image;//借款Banner
	@BindView(R.id.loan_amount) EditText loan_amount;//借款金额
	@BindView(R.id.loan_rate) TextView loan_rate;//执行利率
	@BindView(R.id.tv_explain) TextView tv_explain;
	@BindView(R.id.loan_programme) TextView loan_programme;    //还款方案
	@BindView(R.id.loan_programme2) TextView loan_programme2;    //还款方案第二栏
	@BindView(R.id.layout_coupon_select) RelativeLayout layout_coupon_select;//申请福利补贴
	@BindView(R.id.coupon_select_image) ImageView coupon_select_image;//申请福利补贴
	@BindView(R.id.value_interest) TextView value_interest;//利息抵扣券
	@BindView(R.id.loan_how) TextView loan_how;//如何获得补贴
	@BindView(R.id.tv_if) TextView tv_if;//企业签约后，员工借款费率可低至
	@BindView(R.id.borrow_button) Button bottoButton;//提交申请
	@BindView(R.id.refresh_view) PullToRefreshLayout refreshLayout;//刷新

	//借款企业类型
	private LoanKindBean bean;

	//利息抵扣券与福利补贴
	private String CouponId;    //利息抵扣券的ID
	private int InterestAmount = 0;    //利息抵扣券金额
	private int InterestMinAmount = 0;//利息抵扣券最小使用金额
	private String InterestDescn = "";//利息抵扣券使用描述
	private int CouponCount = 0;//利息抵扣券张数
	private boolean statusSelectCoupon = false;//是否选择了利息抵扣券
	private boolean statusSubsidy = false;//是否申请了福利补贴

	//借款用途
	@BindView(R.id.loan_purposes) TextView loan_purposes;
	private List<String> purposes_types;
	private int purposeStatus;

	//借款期限
	@BindView(R.id.loan_duration) TextView loan_duration;
	private List<String> duration_types;
	private List<LoanDurationListBean> durationList;
	private int durationStatus = 1;//默认借款期限为1个月，借款期限为 1
	private int durationIndex = 0;   //借款列表下标位置
	private float rate = 0; //借款利率

	//还款期数//******************************************************************************************
	@BindView(R.id.loan_period) TextView loan_period;
	private List<String> period_types;
	List<LoanPeriodWithRateBean> periodList;
	private int loanIndex = 0;//还款位置
	private int periodStatus = 1;//默认还款期数为1

	//借款申请信息
	private LoanRequestInfoBean loanRequestInfo;
	//签约企业的借款申请信息
	private LoanRequestInfoBean signedLoanRequestInfo;//******************************************************************

	//不可申请时的状态信息
	private String message;

	private AlertView alertView;

	public static void launche(Context ctx, LoanKindBean bean, LoanRequestInfoBean signedLoanRequestInfo) {
		Intent intent = new Intent(ctx, Activity_LoanRequest_Borrow.class);
		intent.putExtra("bean", bean);
		intent.putExtra("signedLoanRequestInfo", signedLoanRequestInfo);
		ctx.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().pushOneActivity(this);
		setBaseContentView(R.layout.activity_loanrequest_borrow);
		ButterKnife.bind(this);
		//三种贷款类型
		initType();
		//初始化数据
		initViews();
		//获取借款数据
		getLoanCertification();
	}

	//****************************************************************************************************************************************
	//                                                                                                   初始化
	//****************************************************************************************************************************************
	private void initType() {
		bean = (LoanKindBean) getIntent().getSerializableExtra("bean");
		signedLoanRequestInfo = (LoanRequestInfoBean) getIntent().getSerializableExtra("signedLoanRequestInfo");

		if (bean == null) {
			Toast.makeText(Activity_LoanRequest_Borrow.this, "网络异常", Toast.LENGTH_SHORT).show();
			finish();
		}
		tv_explain.setText(bean.DetailDesc);
		setTitleValue(bean.Title);
		//借款banner
		switch (bean.LoanKindId) {
			case 1:
				banner_image.setImageResource(R.drawable.loan_brand);
				layout_coupon_select.setVisibility(View.GONE);
				tv_if.setVisibility(View.VISIBLE);
				loan_how.setVisibility(View.INVISIBLE);
				//这里可能有bug，卧槽
				//				durationStatus=12;
				//				loan_rate.setText("8.5%");
				//				loan_duration.setText("12个月");
				break;
			case 2:
				banner_image.setImageResource(R.drawable.loan_it);
				layout_coupon_select.setVisibility(View.GONE);
				tv_if.setVisibility(View.VISIBLE);
				loan_how.setVisibility(View.INVISIBLE);
				//				durationStatus=3;
				//				loan_rate.setText("12%");
				//				loan_duration.setText("3个月");
				break;
			default:
				banner_image.setImageResource(R.drawable.loan_signed);
				layout_coupon_select.setVisibility(View.VISIBLE);
				tv_if.setVisibility(View.GONE);
				loan_how.setVisibility(View.VISIBLE);
				//				durationStatus=12;
				//				loan_rate.setText("7%");
				//				loan_duration.setText("12个月");
				break;
		}
		setLeftTitleVisible(true);
	}

	private void initViews() {

		//借款金额
		loan_amount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				//输入借款金额之后需要计算还款方案
				calculateRepayProgramme();
			}
		});
		//初始化刷新控件
		findViewById(R.id.loadmore_view).setVisibility(View.GONE);
		refreshLayout.setRefreshResultView(false);
		refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				if (HttpUtils.isNetworkConnected(Activity_LoanRequest_Borrow.this)) getLoanCertification();
				else {
					ToastUtil.alert(Activity_LoanRequest_Borrow.this, "网络异常，请稍后重试");
					refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		});
	}

	//****************************************************************************************************************************************
	//                                                                                                   获取借款数据
	//****************************************************************************************************************************************
	private void getLoanCertification() {
		JSONObject obj = new org.json.JSONObject();
		try {
			obj.put("LoanKind", bean.LoanKindId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ProgressDialogrUtils.show(this, "正在验证借款信息...");
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.LoanCertification, obj, TokenUtil.getEncodeToken(this), new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogrUtils.hide();

				refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				try {
					if (null == response) {
						ToastUtil.alert(Activity_LoanRequest_Borrow.this, "服务器返回数据为空，无法验证");
						return;
					}
					if (response.getInt("status") == -5) {
						Activity_Login.launche(Activity_LoanRequest_Borrow.this);
						finish();
					}
					LogUtil.i("bqt", "借款信息" + response.toString());
					loanRequestInfo = new Gson().fromJson(response.getString("result"), LoanRequestInfoBean.class);
					message = response.getString("message");
					//没申请过
					if (loanRequestInfo.Status == 0 && loanRequestInfo.AggregateId.equals("00000000-0000-0000-0000-000000000000")) {
						//						initBean();
					}
					//初始化数据
					initLoanRequestInfo();
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.i("bqt", "借款信息出错" + e.getMessage());
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				ProgressDialogrUtils.hide();
				ToastUtil.alert(Activity_LoanRequest_Borrow.this, "网络异常，请稍后重试");
			}
		});
		jsonRequest.setTag(BcbRequestTag.BCB_LOAN_CERTIFICATION_REQUEST);
		App.getInstance().getRequestQueue().add(jsonRequest);
	}

	private void initBean() {
		switch (bean.LoanKindId) {
			case 1:
				loanRequestInfo.LoanTimeType = 12;
				break;
			case 2:
				loanRequestInfo.LoanTimeType = 3;
				break;
			default:
				loanRequestInfo.LoanTimeType = 12;
				break;
		}
	}

	/**
	 * 初始化借款申请信息
	 */
	private void initLoanRequestInfo() {
		durationStatus = loanRequestInfo.LoanTimeType;
		periodStatus = loanRequestInfo.Period;
		//******************************************************************************************
		//判断是否申请了福利补贴，取反的原因是在requestSubsidy()里面又做了一次取反操作
		statusSubsidy = !loanRequestInfo.UseSubsidy;
		requestSubsidy();
		//判断是否使用了利息抵扣券，如果使用了利息抵扣券，则要设置文案和变更利息抵扣券的使用状态
		if (!loanRequestInfo.CouponId.equals("0") && !loanRequestInfo.CouponId.equals("00000000-0000-0000-0000-000000000000")) {
			CouponId = loanRequestInfo.CouponId;
			statusSelectCoupon = true;
			value_interest.setText(loanRequestInfo.CouponDescn);
		} else CouponId = null;
		//获取优惠券张数
		setupCouponCount(false);
		//初始化借款类型
		setupLoanUsage();
		//初始化借款期限
		setupLoanDuration();
		//初始化默认的还款期数
		setupLoanPeriod();//******************************************************************************************
		setupRate();
		//借款金额大于0 时，要设置上一次的借款金额, 需要初始化借款页面的所有元素才可以换算金额
		if (loanRequestInfo.Amount > 0) loan_amount.setText(loanRequestInfo.Amount + "");
		else loan_amount.setText("5000");

		if (loanRequestInfo.Status == 0 && !loanRequestInfo.AggregateId.equals("00000000-0000-0000-0000-000000000000"))
			bottoButton.setText("修改申请");
		else bottoButton.setText("立即申请");
	}

	/**
	 * 获取优惠券张数
	 */

	private void setupCouponCount(final boolean isRefush) {
		//如果使用过券，则设置为券的描述
		if (loanRequestInfo.UseCoupon) {
			value_interest.setText(loanRequestInfo.CouponDescn);
			if (!isRefush) return;
		}
		JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", 1);
			obj.put("PageSize", 10);
			obj.put("Status", 1);
			obj.put("CouponType", MyConstants.LOAN_SUBSIDIES);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Select_Coupon, obj, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if (PackageUtil.getRequestStatus(response.toString(), Activity_LoanRequest_Borrow.this)) {
						JSONObject obj = PackageUtil.getResultObject(response.toString());
						//判断JSON对象是否为空
						if (obj != null) {
							//获取利息抵扣券张数
							CouponCount = Integer.parseInt(obj.getString("TotalCount"));
							if (!isRefush) value_interest.setText("你有" + CouponCount + "张利息抵扣券");
							else {
								LogUtil.i("bqt", "利息抵扣券数量222：" + CouponCount);
								value_interest.setText("你有" + CouponCount + "张利息抵扣券");
							}
						}
					}
				} catch (Exception e) {
					LogUtil.d("bqt", "" + e.getMessage());
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
			}
		});
		jsonRequest.setTag(BcbRequestTag.BCB_SELECT_COUPON_REQUEST);
		App.getInstance().getRequestQueue().add(jsonRequest);
	}

	/**
	 * 初始化借款用途
	 */
	private void setupLoanUsage() {
		//将元素添加到数组中
		purposes_types = new ArrayList<>();
		for (int i = 0 ; i < loanRequestInfo.LoanTypeTable.size() ; i++) {
			LogUtil.d("借款用途", loanRequestInfo.LoanTypeTable.get(i).Name);
			purposes_types.add(loanRequestInfo.LoanTypeTable.get(i).Name);
		}
		for (int i = 0 ; i < loanRequestInfo.LoanTypeTable.size() ; i++) {
			//获取默认选中的借款用途
			if (loanRequestInfo.LoanType == loanRequestInfo.LoanTypeTable.get(i).Value) {
				purposeStatus = loanRequestInfo.LoanTypeTable.get(i).Value;
				loan_purposes.setText(purposes_types.get(i));
				break;
			}
		}
		//设置默认选中的位置
	}

	/**
	 * 初始化借款期限
	 */
	private void setupLoanDuration() {

		//将元素添加到数组中
		duration_types = new ArrayList<>();
		durationList = new ArrayList<>();
		for (int i = 0 ; i < loanRequestInfo.RateTable.size() ; i++) {
			//用于判断数组中是否存在对应的借款期限
			boolean duration = false;
			for (int j = 0 ; j < durationList.size() ; j++) {
				if (durationList.get(j).Value == loanRequestInfo.RateTable.get(i).Duration) {
					duration = true;
					break;
				}
			}
			//如果数组中没有该借款期限值，则需要添加该值
			if (!duration) {
				LoanDurationListBean loanDurationList = new LoanDurationListBean(loanRequestInfo.RateTable.get(i).Duration + "个月",
						loanRequestInfo.RateTable.get(i).Duration);
				durationList.add(loanDurationList);
				duration_types.add(loanRequestInfo.RateTable.get(i).Duration + "个月");
			}
		}

		//找到durationStatus对应的下表值
		for (int i = 0 ; i < duration_types.size() ; i++) {
			if (new String(durationStatus + "个月").equalsIgnoreCase(duration_types.get(i))) {
				durationIndex = i;
				break;
			}
		}

		loan_duration.setText(duration_types.get(durationIndex));
	}

	/**
	 * 初始化还款期数//******************************************************************************************
	 */
	private void setupLoanPeriod() {
		period_types = new ArrayList<>();
		//还款期数要从利率表里面查找，这尼玛简直就是坑爹
		periodList = new ArrayList<>();
		for (int i = 0 ; i < loanRequestInfo.RateTable.size() ; i++) {
			//如果借款期限相同，则将期限加载到页面中去
			if (loanRequestInfo.RateTable.get(i).getDuration() == durationStatus) {
				LoanPeriodWithRateBean periodWithRate = new LoanPeriodWithRateBean(loanRequestInfo.RateTable.get(i).getPeriod(),
						loanRequestInfo.RateTable.get(i).getRate());
				periodList.add(periodWithRate);
				period_types.add("" + periodWithRate.Period);
			}
		}
		//设置还款位置
		for (int i = 0 ; i < periodList.size() ; i++) {
			if (loanRequestInfo.Period == periodList.get(i).Period) {
				loanIndex = i;
				break;
			}
		}
		//获取初始的期数
		periodStatus = Integer.valueOf(period_types.get(loanIndex));
		LogUtil.d("初始化的还款期数", periodStatus + "");
		//设置还款期数位置
		loan_period.setText(period_types.get(loanIndex) + "期");

		//设置完还款期数之后，就要开始算还款方案
		calculateRepayProgramme();
	}

	/**
	 * 计算利率
	 */
	private void setupRate() {
		for (int i = 0 ; i < loanRequestInfo.RateTable.size() ; i++) {
			String monthNum = loanRequestInfo.RateTable.get(i).getDuration() + "个月";
			int periodNum = loanRequestInfo.RateTable.get(i).Period;
			if (periodStatus==periodNum&&monthNum.equalsIgnoreCase(loan_duration.getText().toString().trim())) {
				rate = loanRequestInfo.RateTable.get(i).Rate / 100;
				break;
			}
		}
		loan_rate.setText(new DecimalFormat("######0.##").format(rate * 100) + "%");
		//企业签约后，员工借款费率可低至
		//tv_if.setText("企业签约后，员工借款费率可低至" + new DecimalFormat("######0.##").format(signedRate * 100) + "%");
	}

	//	/**
	//	 * 计算还款方案//******************************************************************************************
	//	 */
	//	private void calculateRepayProgramme() {
	//		String repayProgramme = "";
	//		//借款月数等于还款期数
	//		if (getLoanAmount() <= 0 || durationStatus == -1) repayProgramme = "0元";
	//		else {
	//			float amount = (getLoanAmount() + getLoanAmount() * durationStatus / 12 * rate) / durationStatus;
	//			String value = new DecimalFormat("######0.##").format(amount);
	//			//如果是一个月的时候，就是"到期还款XXX元"
	//			if (durationStatus == 1) repayProgramme = "到期还款" + value + "元";
	//			else repayProgramme = "每月还款" + value + "元";
	//		}
	//		loan_programme.setText(repayProgramme);
	//	}

	/**
	 * 计算还款方案//******************************************************************************************
	 */
	private void calculateRepayProgramme() {
		loan_programme2.setVisibility(View.GONE);
		DecimalFormat df = new DecimalFormat("######0.##");
		float rate = 0;
		//根据表查找利率
		for (int i = 0 ; i < loanRequestInfo.RateTable.size() ; i++) {
			if (durationStatus == loanRequestInfo.RateTable.get(i).getDuration() && periodStatus == loanRequestInfo.RateTable.get(i)
					.getPeriod()) {
				rate = loanRequestInfo.RateTable.get(i).Rate / 100;
				break;
			}
		}

		String repayProgramme = "";
		if (getLoanAmount() <= 0) {
			repayProgramme = "0元";
		}
		//借款月数等于还款期数
		else if (durationStatus == periodStatus && durationStatus != -1) {
			float amount = (getLoanAmount() + getLoanAmount() * durationStatus / 12 * rate) / periodStatus;
			String value = df.format(amount);
			//如果是一个月的时候，就是"到期还款XXX元"
			if (durationStatus == 1) {
				repayProgramme = "到期还款" + value + "元";
			} else {
				repayProgramme = "每月还款 低至" + value + "元";
			}
		}
		//还款期数为2时
		else if (periodStatus == 2) {
			float amount = getLoanAmount() * (float) durationStatus / 12 * rate / durationStatus;
			String value = df.format(amount);
			String val = df.format(getLoanAmount() / periodStatus);
			repayProgramme = "每月还利息" + value + "元" + "每12个月还本金" + val + "元";
		}
		//划款期数为3时
		else if (periodStatus == 3) {
			float amount = getLoanAmount() * (float) durationStatus / 12 * rate / durationStatus;
			String value = df.format(amount);
			String val1 = df.format(getLoanAmount() * 0.3);
			String val2 = df.format(getLoanAmount() * 0.4);
			repayProgramme = "每月还息低至" + value + "元";
			loan_programme2.setVisibility(View.VISIBLE);
			loan_programme2.setText("每12个月还本金\n" +  val1 + "元、" + val1 +"元、" + val2 + "元");
		} else {
			repayProgramme = "暂不支持该方案";
		}
		loan_programme.setText(repayProgramme);
	}

	/**
	 * 获取借款金额
	 */
	private float getLoanAmount() {
		String amount = loan_amount.getText().toString().replace(",", "");
		if (amount.equalsIgnoreCase("")) amount = "0";
		return Float.parseFloat(amount);
	}

	@OnClick({R.id.rl_purposes, R.id.rl_duration, R.id.borrow_button, R.id.loan_protocol, R.id.layout_interest,//
			R.id.layout_customer_service, R.id.layout_coupon_select, R.id.loan_how, R.id.rl_period})
	public void onClick(View view) {
		switch (view.getId()) {
			//点击借款用途
			case R.id.rl_purposes:
				String[] ar = purposes_types.toArray(new String[purposes_types.size()]);
				LogUtil.i("bqt", "借款用途" + Arrays.toString(ar));
				SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, ar, purposeStatus - 2, new SpinnerWheelUtil
						.OnDoneClickListener() {
					@Override
					public void onClick(int currentItem) {
						purposeStatus = currentItem + 2;
						loan_purposes.setText(purposes_types.get(currentItem));
						if (purposes_types.get(currentItem).equals("其他用途")) purposeStatus = 100;
						LogUtil.i("bqt", purposeStatus + "--" + purposes_types.get(currentItem));
					}
				});
				break;
			//点击借款期限
			case R.id.rl_duration:
				String[] arr = duration_types.toArray(new String[duration_types.size()]);
				LogUtil.i("bqt", "借款期限" + Arrays.toString(arr));
				SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr, durationIndex, new SpinnerWheelUtil
						.OnDoneClickListener() {
					@Override
					public void onClick(int currentItem) {
						durationIndex = currentItem;
						loan_duration.setText(duration_types.get(currentItem));//设置借款期限
						float signedRate = 0;
						for (int i = 0 ; i < loanRequestInfo.RateTable.size() ; i++) {
							String monthNum = loanRequestInfo.RateTable.get(i).getDuration() + "个月";
							if (monthNum.equalsIgnoreCase(duration_types.get(currentItem))) {
								durationStatus = loanRequestInfo.RateTable.get(i).getDuration();
								rate = loanRequestInfo.RateTable.get(i).Rate / 100;
								//signedRate = signedLoanRequestInfo.RateTable.get(i).Rate / 100;//***********************************
								break;
							}
						}
						//重新设置利率
						//loan_rate.setText(new DecimalFormat("######0.##").format(rate * 100) + "%");
						setupRate();
						//企业签约后，员工借款费率可低至
						//tv_if.setText("企业签约后，员工借款费率可低至" + new DecimalFormat("######0.##").format(signedRate * 100) + "%");
						//初始化默认的还款期数
						loanIndex=0;
						setupLoanPeriod();//******************************************************************************************
						//重新设置还款期数
						calculateRepayProgramme();
					}
				});
				break;
			//选择还款期数//******************************************************************************************
			case R.id.rl_period:
				String[] array = period_types.toArray(new String[period_types.size()]);
				SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, array, loanIndex, new SpinnerWheelUtil
						.OnDoneClickListener() {
					@Override
					public void onClick(int currentItem) {
						periodStatus = periodList.get(currentItem).Period;
						LogUtil.d("选中的还款期数", periodStatus + "");
						loan_period.setText(period_types.get(currentItem) + "期");//设置还款期数
						setupRate();
						calculateRepayProgramme();
					}
				});
				break;
			//点击立即申请按钮
			case R.id.borrow_button:
				UmengUtil.eventById(Activity_LoanRequest_Borrow.this, R.string.loan_act);
				borrowButtonClick();
				break;
			//借款服务协议
			case R.id.loan_protocol:
				Activity_Browser.launche(Activity_LoanRequest_Borrow.this, "借款服务协议", UrlsOne.LoanProtocol);
				break;
			//如何获得补贴
			case R.id.loan_how:
				Activity_Browser.launche(Activity_LoanRequest_Borrow.this, "如何获得补贴", UrlsOne.How2GetSubsidy);
				break;

			//点击利息抵扣券
			case R.id.layout_interest:
				//如果已经选择了福利补贴，则弹Toast提示不能同时福利补贴和利息抵扣券不能同时使用
				if (statusSubsidy) ToastUtil.alert(Activity_LoanRequest_Borrow.this, "福利补贴和利息抵扣券不能同时使用");
					//选择利息抵扣券
				else selectInterestCoupon();
				break;

			//申请福利补贴
			case R.id.layout_coupon_select:
				//已经选择了利息抵扣券，则弹框提示是否仅申请福利补贴
				if (statusSelectCoupon) showAlertView();
					//如果没选过利息抵扣券，则正常申请福利补贴
				else requestSubsidy();
				break;

			//客服
			case R.id.layout_customer_service:
				MQCustomerManager.getInstance(this).showCustomer(App.mUserDetailInfo.getCustomerId());
				break;
		}
	}

	/**
	 * 弹框提示
	 */
	private void showAlertView() {
		AlertView.Builder ibuilder = new AlertView.Builder(this);
		ibuilder.setTitle("提示");
		ibuilder.setMessage("你已经使用了利息抵扣券，不能再申请福利补贴。是否仅申请福利补贴？");
		ibuilder.setPositiveButton("立即申请", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//将利息抵扣券的描述设置为"你有xx张利息抵扣券"
				LogUtil.i("bqt", "利息抵扣券数量1111：" + CouponCount);
				setupCouponCount(true);

				//清空利息抵扣券的ID、金额和描述等，将利息抵扣券的选中状态置为false
				InterestAmount = 0;
				InterestMinAmount = 0;
				InterestDescn = "";
				statusSelectCoupon = false;
				//正常申请福利补贴
				requestSubsidy();
				//销毁弹框
				alertView.dismiss();
				alertView = null;
			}
		});
		ibuilder.setNegativeButton("取消", null);
		alertView = ibuilder.create();
		alertView.show();
	}

	/**
	 * 申请福利补贴
	 */
	private void requestSubsidy() {
		statusSubsidy = !statusSubsidy;
		if (statusSubsidy) coupon_select_image.setBackgroundResource(R.drawable.loan_request_select);
		else coupon_select_image.setBackgroundResource(R.drawable.loan_rect);
	}

	/**
	 * 选择利息抵扣券
	 */
	private void selectInterestCoupon() {
		Intent newIntent = new Intent(this, Activity_Select_Coupon.class);
		newIntent.putExtra("investAmount", getLoanAmount());
		newIntent.putExtra("CouponType", MyConstants.LOAN_SUBSIDIES);
		startActivityForResult(newIntent, 1);
		//清空利息抵扣券的ID、金额和描述等，将利息抵扣券的选中状态置为false
		CouponId = null;
		InterestAmount = 0;
		InterestMinAmount = 0;
		statusSelectCoupon = false;
	}

	/**
	 * 立即借款按钮事件
	 */
	private void borrowButtonClick() {
		//判断是否写了借款金额和期望到账时间
		if (getLoanAmount() <= 0) {
			ToastUtil.alert(Activity_LoanRequest_Borrow.this, "请填写完整的借款信息");
			return;
		}

		//借款金额小于5000元，提示不能小于5000元
		if (getLoanAmount() < 5000) {
			ToastUtil.alert(Activity_LoanRequest_Borrow.this, "借款金额必须大于5000元");
			return;
		}

		//借款金额大于20W元，提示不能大于20W元
		if (getLoanAmount() > 200000) {
			ToastUtil.alert(Activity_LoanRequest_Borrow.this, "借款金额不能大于200000元");
			return;
		}
		//已经选择了利息抵扣券
		LogUtil.i("bqt", "是否选择了抵用券" + statusSelectCoupon + "，抵扣券金额" + getLoanAmount() + "，最低借款金额" + InterestMinAmount);
		if (statusSelectCoupon && getLoanAmount() < InterestMinAmount) altDialog2();
		else {
			//可以申请借款
			if (loanRequestInfo.Status == 0) {
				//判断是否跟原来的数据一样，如果跟原来申请的借款一样没有变化，直接提示完善个人信息
				if (isNeedToPostData()) pushLoanMessageToService();
					//跳转至填写借款信息页面
				else Activity_LoanRequest_Person.launche(Activity_LoanRequest_Borrow.this, bean);
				//存在已审核通过的借款
			} else {
				LogUtil.i("bqt", "存在已审核通过的借款时的提示信息：" + message);
				ToastUtil.alert(Activity_LoanRequest_Borrow.this, message);
			}
		}
	}

	/**
	 * 判断是否需要提交借款申请数据到服务器
	 */
	private boolean isNeedToPostData() {
		//判断传进来的借款值为小于等于0，或者是第一次编辑的时候
		if (loanRequestInfo.Amount <= 0 || loanRequestInfo.AggregateId.equals("0") || loanRequestInfo.AggregateId.equals
				("00000000-0000-0000-0000-000000000000")) {
			return true;
		} else if (purposeStatus != loanRequestInfo.LoanType           //借款用途不一致
				|| getLoanAmount() != loanRequestInfo.Amount    //借款金额不一致
				|| durationStatus != loanRequestInfo.LoanTimeType   //借款期限不一致
				|| periodStatus != loanRequestInfo.Period
				//还款期数不一致//*******************************************************************************
				|| statusSubsidy != loanRequestInfo.UseSubsidy      //申请福利补贴不一致
				|| statusSelectCoupon != loanRequestInfo.UseCoupon  //使用利息抵扣券不一致
				) {
			return true;
		}
		return false;
	}

	/**
	 * 将借款信息提交到服务器
	 */
	private void pushLoanMessageToService() {
		//提交数据时候上传用户定位数据
		UserExtraInfo userExtraInfo = DbUtil.getUserExtra();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("LoanKind", bean.LoanKindId);
			if (!loanRequestInfo.AggregateId.equals("0") && !loanRequestInfo.AggregateId.equals
					("00000000-0000-0000-0000-000000000000")) {
				jsonObject.put("AggregateId", loanRequestInfo.AggregateId);
			} else jsonObject.put("AggregateId", null);

			jsonObject.put("LoanType", purposeStatus);//借款用途
			jsonObject.put("UseSubsidy", statusSubsidy); //是否申请福利补贴
			jsonObject.put("Amount", getLoanAmount());//借款金额
			jsonObject.put("LoanTimeType", durationStatus);//借款期限
			jsonObject.put("Period", periodStatus);//还款期数	************************************************************************

			//是否使用了利息抵扣券，存在利息抵扣券的时候才去提交
			if (!TextUtils.isEmpty(CouponId)) jsonObject.put("CouponId", CouponId);
			else jsonObject.put("CouponId", null);

			//位置信息、imei、机型、网络环境
			if (null != userExtraInfo) {
				jsonObject.put("MobileMode", userExtraInfo.getModel());
				jsonObject.put("IMEI", userExtraInfo.getImei());
				jsonObject.put("Network", userExtraInfo.getNetwork());
				jsonObject.put("Position", userExtraInfo.getLocation());
			}

			LogUtil.i("bqt", "借款第一页【请求】的数据是" + jsonObject.toString());

			ProgressDialogrUtils.show(this, "正在验证借款信息...");
			BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.PostRequestMessage, jsonObject, TokenUtil.getEncodeToken(this),
					new BcbRequest.BcbCallBack<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtil.i("bqt", "借款第一页【返回】的数据是" + response.toString());
					ProgressDialogrUtils.hide();
					try {
						//提示申请成功，是否填写个人信息
						if (response.getInt("status") == 1)
							startActivity(new Intent(Activity_LoanRequest_Borrow.this, Activity_LoanRequest_Person.class));
						else {
							ToastUtil.alert(Activity_LoanRequest_Borrow.this, response.getString("message").equalsIgnoreCase("") ?
									"服务器繁忙，请稍候再试" : response.getString("message"));
							//判断是否是Token过期，如果过期则跳转至登陆界面
							if (response.getInt("status") == -5) Activity_Login.launche(Activity_LoanRequest_Borrow.this);
						}
					} catch (Exception e) {
						e.printStackTrace();
						//数据出错重新登录
						Activity_Login.launche(Activity_LoanRequest_Borrow.this);
					}
				}

				@Override
				public void onErrorResponse(Exception error) {
					ProgressDialogrUtils.hide();
				}
			});
			jsonRequest.setTag(BcbRequestTag.BCB_CREATE_LOAN_REQUEST_MESSAGE_REQUEST);
			App.getInstance().getRequestQueue().add(jsonRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 选择优惠券回调
	 *
	 * @param requestCode 请求码
	 * @param resultCode  回调码
	 * @param data        回调数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			//判断是否选择了利息抵扣券
			case 1:
				if (data != null) {
					//利息抵扣券ID
					CouponId = data.getStringExtra("CouponId");
					//利息抵扣券金额
					InterestAmount = data.getIntExtra("InterestAmount", 0);
					//利息抵扣券最小使用金额
					InterestMinAmount = data.getIntExtra("InterestMinAmount", 0);
					LogUtil.i("bqt", "利息抵扣券最小使用金额" + InterestMinAmount);

					//利息抵扣券使用条件描述
					InterestDescn = data.getStringExtra("InterestDescn");
					//设置利息抵扣券的描述
					value_interest.setText("已勾选利息抵扣券");
					//value_interest.setText(InterestDescn);
					//已经选择了利息抵扣券
					statusSelectCoupon = true;
				} else setupCouponCount(false);
				break;
		}
	}

	//销毁广播
	@Override
	public void onDestroy() {
		super.onDestroy();
		App.getInstance().getRequestQueue().cancelAll(BcbRequestTag.BCB_SELECT_COUPON_REQUEST);
		App.getInstance().getRequestQueue().cancelAll(BcbRequestTag.BCB_CREATE_LOAN_REQUEST_MESSAGE_REQUEST);
	}

	private void altDialog2() {
		AlertView.Builder ibuilder = new AlertView.Builder(this);
		ibuilder.setTitle("温馨提示");
		ibuilder.setMessage("本优惠券使用的最低借款额度为" + String.format("%.2f", Float.valueOf(InterestMinAmount)) + "元。\n" +//
				"若继续申请，则默认不使用该优惠券。");
		ibuilder.setPositiveButton("继续申请", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertView.dismiss();
				CouponId = null;
				//利息抵扣券金额
				InterestAmount = 0;
				//利息抵扣券最小使用金额
				InterestMinAmount = 0;
				//利息抵扣券使用条件描述
				//已经选择了利息抵扣券
				statusSelectCoupon = false;
				//******************************************************************************************
				//可以申请借款
				if (loanRequestInfo.Status == 0) {
					//判断是否跟原来的数据一样，如果跟原来申请的借款一样没有变化，直接提示完善个人信息
					if (isNeedToPostData()) pushLoanMessageToService();
						//跳转至填写借款信息页面
					else startActivity(new Intent(Activity_LoanRequest_Borrow.this, Activity_LoanRequest_Person.class));
					//存在已审核通过的借款
				} else {
					LogUtil.i("bqt", "存在已审核通过的借款时的提示信息：" + message);
					ToastUtil.alert(Activity_LoanRequest_Borrow.this, message);
				}
			}
		});
		ibuilder.setNegativeButton("修改金额", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertView.dismiss();
			}
		});
		alertView = ibuilder.create();
		alertView.setCanceledOnTouchOutside(false);
		alertView.show();
	}
}