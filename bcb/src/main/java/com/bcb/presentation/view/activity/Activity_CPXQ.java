package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.CPXQbean;
import com.bcb.data.util.DensityUtils;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ProgressDialogrUtils;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bcb.R.id.back_img;

/**
 * setTitleValue("产品详情"。setTitleValue("详情"。setTitleValue("项目详情"。setTitleValue("立即购买"。setTitleValue("立即申购"。
 */
public class Activity_CPXQ extends Activity_Base implements View.OnTouchListener {
	Context ctx;
	@BindView(R.id.tv_rate) TextView tvRate;
	@BindView(R.id.tv_rate_add) TextView tvRateAdd;
	@BindView(R.id.sdq) TextView sdq;
	@BindView(R.id.ktje) TextView ktje;
	@BindView(R.id.tv_limite) TextView tvLimite;
	@BindView(R.id.tv_u1) TextView tvU1;
	@BindView(R.id.tv_u2) TextView tvU2;
	@BindView(R.id.qxr) TextView qxr;
	@BindView(R.id.tc) TextView tc;
	@BindView(R.id.cy) TextView cy;
	@BindView(R.id.buy1) TextView buy1;
	@BindView(R.id.ll_buy1) LinearLayout llBuy1;
	@BindView(R.id.buy2) TextView buy2;
	@BindView(R.id.ll_buy2) LinearLayout llBuy2;
	@BindView(R.id.more) TextView more;
	@BindView(R.id.layout_scrollview) ScrollView layoutScrollview;
	@BindView(R.id.buy) TextView buy;

	private String packageId = "";

	private int CouponType = 0;
	private int countDate = 0;

	//初始化******************************************************************************************
	//0正常标，1转让标，2福鸡包
	private int type = 0;

	public static void launche2(Context ctx, String pid, int type) {
		Intent intent = new Intent();
		intent.putExtra("pid", pid);
		intent.putExtra("type", type);
		intent.setClass(ctx, Activity_CPXQ.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		MyActivityManager.getInstance().pushOneActivity(Activity_CPXQ.this);
		if (getIntent() != null) {
			packageId = getIntent().getStringExtra("pid");
			CouponType = getIntent().getIntExtra("CouponType", 0);
			type = getIntent().getIntExtra("type", 0);
		}
		setBaseContentView(R.layout.activity_cpxq);
		ButterKnife.bind(this);// ButterKnife.inject(this) should be called after setContentView()
		setTitleValue("产品详情");
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
		requestCPInfo();
		ProgressDialogrUtils.show(this, "正在获取数据，请稍后…");
	}

	private void showData() {
		tvRate.setText("" + String.format("%.1f", bean.Rate));
		//福袋利率
		String welfareRate = TextUtils.isEmpty(App.getInstance().getWelfare()) ? "%" : "%+" + App.getInstance().getWelfare() + "%";
		tvRateAdd.setText(welfareRate);
		sdq.setText(bean.MixDuration + "个月");
		ktje.setText(String.format("%.2f", bean.Balance));
		tvLimite.setText(getSpan(bean.MixDuration + "", bean.MaxDuration + ""));
		cy.setText(bean.MaxDuration + "个月");
		buy1.setText(getSpan2(10000,bean.MixDuration, String.format("%.2f", bean.MinPreInterest)));//
		buy2.setText(getSpan2(10000,bean.MaxDuration, String.format("%.2f", bean.MaxPreInterest)));
		setTitleValue(bean.Name);
		if (bean.Balance <= 0) {
			buy.setText("已售罄");
			buy.setTextColor(0xff999999);
			buy.setEnabled(false);
			buy.setBackgroundColor(0xffd0d0d0);
		} else {
			buy.setText("立即购买");
			buy.setEnabled(true);
			buy.setBackgroundColor(getResources().getColor(R.color.red));
			buy.setTextColor(getResources().getColor(R.color.white));
		}
		//加入时间，起息时间，锁定到期
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
			Date qxr_ = format.parse(bean.InterestTakeDate);
			Date tc_ = format.parse(bean.HoldingDate);

			qxr.setText(format2.format(qxr_));
			tc.setText(format2.format(tc_));
			//进度
			Date dateNow = new Date();
//			if (dateNow.getTime() < qxr_.getTime()) {//未到起息时间
//				pb.setProgress(2);
//			} else pb.setProgress(30);

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private SpannableString getSSpannableString(String string) {
		SpannableString mSpannableString = new SpannableString(string);
		//颜色
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xffff4c4c);
		mSpannableString.setSpan(colorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//大小
		AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(DensityUtils.dp2px(this, 22));
		mSpannableString.setSpan(absoluteSizeSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return mSpannableString;
	}

	private SpannableStringBuilder getSpan(String string1, String string2) {
		SpannableStringBuilder needStartSSB = new SpannableStringBuilder("最少持有");
		needStartSSB.append(getSSpannableString(string1)).append("个月，按月续期，最长")//
				.append(getSSpannableString(string2)).append("个月");
		return needStartSSB;
	}

	private SpannableString getSSpannableString2(String string) {
		SpannableString mSpannableString = new SpannableString(string);
		//颜色
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xffff4c4c);
		mSpannableString.setSpan(colorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return mSpannableString;
	}

	private SpannableStringBuilder getSpan2(int money,int time, String string2) {
		SpannableStringBuilder needStartSSB = new SpannableStringBuilder("购买");
		needStartSSB.append(getSSpannableString2(" "+money)).append(" 元，" + time + "个月可收益 ")//
				.append(getSSpannableString2(string2)).append(" 元");
		return needStartSSB;
	}

	@OnClick({R.id.ll_buy1, R.id.ll_buy2, R.id.more, R.id.buy})
	public void onClick(View view) {
		if (App.saveUserInfo.getAccess_Token() == null && view.getId() != R.id.more) {
			Activity_Login.launche(ctx);
			return;
		}
		switch (view.getId()) {
			case R.id.ll_buy1:
			case R.id.ll_buy2:
			case R.id.buy:
				//没有开通自动投标
				if ( !App.mUserDetailInfo.AutoTenderPlanStatus) {//(type == 1 || type == 2) &&
					altDialog();
					return;
				}
				//跳转到购买页面
				Activity_Project_Buy2.launche2(this, packageId, bean.Name, CouponType, countDate, bean, type);
				break;
			case R.id.more:
				Activity_Browser.launche2(this, bean.Name, bean.PageUrl,20095);
				break;
		}
	}

	CPXQbean bean;

	/**
	 * 用户信息
	 */
	private void requestCPInfo() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("PackageId", packageId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String url = UrlsTwo.SB_ZXB;//普通标
		//注意：债权标和普通标使用不同的接口
		switch (type) {
			case 2:
				url = UrlsTwo.DBB_WYB;//债权标
				break;
		}
		LogUtil.i("bqt", "【新标：请求地址】" + url + "【packageId】" + packageId);
		BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogrUtils.hide();
				LogUtil.i("bqt", "新标：数据" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					JSONObject data = PackageUtil.getResultObject(response);
					//判断JSON对象是否为空
					if (data != null) {
						//将获取到的银行卡数据写入静态数据区中
						bean = App.mGson.fromJson(data.toString(), CPXQbean.class);
						if (bean != null) {
							showData();
						}
					}
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				ProgressDialogrUtils.hide();
			}
		});
		App.getInstance().getRequestQueue().add(jsonRequest);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
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
	//******************************************************************************************

	/**
	 * APP自动投标流程：
	 * 1、新用户买标，没有开通托管账户的引导到汇付开通托管账户。
	 * 2、已开通托管账户用户买标没开通自动买标的，引导到汇付开通自动投标。
	 * 3、开通自动投标完毕，手动买入理财标。
	 */
	private void autoOpen() {
		String requestUrl = UrlsTwo.OPENAUTOTENDERPLAN;
		String encodeToken = TokenUtil.getEncodeToken(ctx);
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
				if (PackageUtil.getRequestStatus(response, ctx)) {
					try {
						/** 后台返回的JSON对象，也是要转发给汇付的对象 */
						JSONObject result = PackageUtil.getResultObject(response);
						if (result != null) {
							//网页地址
							String postUrl = result.optString("PostUrl");
							result.remove("PostUrl");//移除这个参数
							//传递的参数
							String postData = HttpUtils.jsonToStr(result.toString()); //跳转到webview
							Activity_WebView.launche(ctx, "开启份额锁", postUrl, postData);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "开通自动投标2" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(ctx, response.optString("message"), Toast.LENGTH_SHORT)
							.show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				LogUtil.d("bqt", "【Activity_TuoGuan_HF】【loginAccount】网络异常，请稍后重试" + error.toString());
			}
		});
		App.getInstance()
				.getRequestQueue()
				.add(jsonRequest);
	}
}