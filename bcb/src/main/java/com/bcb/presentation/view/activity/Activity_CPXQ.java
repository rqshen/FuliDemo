package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.CPXQbean;
import com.bcb.data.util.DensityUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ProgressDialogrUtils;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.HorizontalProgressBarWithNumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * setTitleValue("产品详情"。setTitleValue("详情"。setTitleValue("项目详情"。setTitleValue("立即购买"。setTitleValue("立即申购"。
 */
public class Activity_CPXQ extends Activity_Base {
	Context ctx;
	@BindView(R.id.tv_rate) TextView tvRate;
	@BindView(R.id.tv_rate_add) TextView tvRateAdd;
	@BindView(R.id.sdq) TextView sdq;
	@BindView(R.id.ktje) TextView ktje;
	@BindView(R.id.tv_limite) TextView tvLimite;
	@BindView(R.id.pb) HorizontalProgressBarWithNumber pb;
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
		dropdown.setImageDrawable(getResources().getDrawable(R.drawable.right_more));
		requestCPInfo();
		ProgressDialogrUtils.show(this,"请稍后…");
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
		buy1.setText(getSpan2(3, String.format("%.2f", bean.MinPreInterest)));
		buy2.setText(getSpan2(3, String.format("%.2f", bean.MaxPreInterest)));
		setTitleValue(bean.Name);
		if (bean.Balance <= 0) {
			buy.setText("售罄");
			buy.setTextColor(0xff999999);
			buy.setEnabled(false);
			buy.setBackgroundColor(0xdbdbdb);
		} else {
			buy.setText("立即购买");
			buy.setEnabled(false);
			buy.setBackgroundColor(getResources().getColor(R.color.red));
			buy.setTextColor(getResources().getColor(R.color.white));
		}
		//加入时间，起息时间，锁定到期
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date qxr_ = format.parse(bean.InterestTakeDate);
			Date tc_ = format.parse(bean.HoldingDate);

			qxr.setText(format.format(qxr_));
			tc.setText(format.format(tc_));
			//进度
			Date dateNow = new Date();
			if (dateNow.getTime() < qxr_.getTime()) {//未到起息时间
				pb.setProgress(2);
			} else pb.setProgress(30);

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

	private SpannableStringBuilder getSpan2(int time, String string2) {
		SpannableStringBuilder needStartSSB = new SpannableStringBuilder("购买");
		needStartSSB.append(getSSpannableString2(" 10000 ")).append("元，" + time + "个月可收益 ")//
				.append(getSSpannableString2(string2)).append(" 元");
		return needStartSSB;
	}
	@OnClick({R.id.ll_buy1, R.id.ll_buy2, R.id.more, R.id.buy})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_buy1:
			case R.id.ll_buy2:
			case R.id.buy:
				//跳转到购买页面
				Activity_Project_Buy2.launche2(this, packageId, bean.Name, CouponType, countDate, bean, type);
				break;
			case R.id.more:
				Activity_Browser.launche(this,bean.Name,bean.PageUrl);
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

}