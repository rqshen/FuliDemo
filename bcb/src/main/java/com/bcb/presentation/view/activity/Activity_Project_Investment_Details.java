package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.ClaimConveyBean;
import com.bcb.data.bean.Project_Investment_Details_Bean;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.HorizontalProgressBarWithNumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 2、投资详情
 */
public class Activity_Project_Investment_Details extends Activity_Base implements View.OnClickListener {

	private static final String TAG = "bqt";
	private String OrderNo;
	private TextView top_amount, earning_expected, income_total;
	private TextView biddingtime, earningtime, endtime;
	private TextView annual_yield, earnings_end, have, left, left_time;
	HorizontalProgressBarWithNumber pb;
	private RelativeLayout rl_hk, rl_zr;
	private LinearLayout ll_exit;
	private Button button;

	private Project_Investment_Details_Bean bean;

	public static void launche(Context ctx, String OrderNo) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Project_Investment_Details.class);
		intent.putExtra("OrderNo", OrderNo);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().pushOneActivity(Activity_Project_Investment_Details.this);
		setBaseContentView(R.layout.activity_project_investment_details);
		setLeftTitleVisible(true);
		setTitleValue("投资详情");
		setRightBtnVisiable(View.VISIBLE);
		setRightBtnImg(R.drawable.ico_info, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bean != null && bean.PackageId != null && bean.Type != null) {
					int type = 0;
					if (bean.Type.equals("claim_convey")) type = 1;
					else if (bean.Type.equals("mon_package")) type = 2;
					Activity_NormalProject_Introduction.launche2(Activity_Project_Investment_Details.this, bean.PackageId, 0, type);//标类型：prj_package则为普通标 claim_convey则为债权转让标
				} else Toast.makeText(Activity_Project_Investment_Details.this, "获取数据失败", Toast.LENGTH_SHORT).show();
			}
		});
		OrderNo = getIntent().getStringExtra("OrderNo");
		initView();
		loadData();
	}

	private void initView() {
		pb = (HorizontalProgressBarWithNumber) findViewById(R.id.pb);
		top_amount = (TextView) findViewById(R.id.top_amount);
		earning_expected = (TextView) findViewById(R.id.earning_expected);
		income_total = (TextView) findViewById(R.id.income_total);
		biddingtime = (TextView) findViewById(R.id.biddingtime);
		earningtime = (TextView) findViewById(R.id.earningtime);
		endtime = (TextView) findViewById(R.id.endtime);
		annual_yield = (TextView) findViewById(R.id.annual_yield);
		earnings_end = (TextView) findViewById(R.id.earnings_end);
		have = (TextView) findViewById(R.id.have);
		left = (TextView) findViewById(R.id.left);
		left_time = (TextView) findViewById(R.id.left_time);
		button = (Button) findViewById(R.id.button);
		rl_hk = (RelativeLayout) findViewById(R.id.rl_hk);
		rl_zr = (RelativeLayout) findViewById(R.id.rl_zr);
		ll_exit = (LinearLayout) findViewById(R.id.ll_exit);
		rl_hk.setOnClickListener(this);
		rl_zr.setOnClickListener(this);
		button.setOnClickListener(this);
	}

	private void loadData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("OrderNo", OrderNo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.TradingRecordDetail, obj, TokenUtil.getEncodeToken(this), new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【Activity_Project_Investment_Details】【onResponse】投资详情" + response.toString());
				try {
					boolean flag = PackageUtil.getRequestStatus(response, Activity_Project_Investment_Details.this);
					if (flag) {
						JSONObject obj = PackageUtil.getResultObject(response);
						if (obj != null) bean = App.mGson.fromJson(obj.toString(), Project_Investment_Details_Bean.class);
						if (null != bean) {
							//在投本金，预期收益，已获收益
							top_amount.setText(String.format("%.2f", bean.getOrderAmount()));
							earning_expected.setText(String.format("%.2f", bean.getPreInterest()));
							income_total.setText(String.format("%.2f", bean.getInterest()));

							//加入时间，起息时间，锁定到期
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							Date biddingDate = format.parse(bean.getPayTime());
							Date earningDate = format.parse(bean.getInterestTakeDate());
							Date endDate = format.parse(bean.getEndDate());
							biddingtime.setText(format.format(biddingDate));
							earningtime.setText(format.format(earningDate));
							endtime.setText(format.format(endDate));
							//进度
							//							Date dateNow = new Date();
							//							if (dateNow.getTime() < earningDate.getTime()) {//未到起息时间
							//								pb.setProgress(10);
							//							} else if (dateNow.getTime() >= endDate.getTime()) {//已到锁定期
							//								pb.setProgress(100);
							//							} else {
							//								pb.setProgress(50);
							//							}
							switch (bean.Phase) {//订单所处阶段 1：加入；5：加入后至开始计息前；10：开始计息；50：开始计息后至锁定到期前；100: 锁定到期
								case 5:
									pb.setProgress(30);
									break;
								case 10:
									pb.setProgress(50);
									break;
								case 50:
									pb.setProgress(70);
									break;
								case 100:
									pb.setProgress(100);
									break;
								default:
									pb.setProgress(10);
									break;
							}
							//年化利率，锁定期限，已收本息，剩余本息
							annual_yield.setText(String.format("%.2f", bean.getRate()) + "%");
							earnings_end.setText(bean.getDuration());//封闭期 带单位【 "Duration": 3天】
							have.setText(String.format("%.2f", bean.ReceivedPrincipalAndInterest));
							left.setText(String.format("%.2f", bean.WaitPrincipalAndInterest));

							//退出时间
							Date date = format.parse(bean.getEndDate());
							Date dateFrom = new Date(date.getTime() - 14 * 1000 * 60 * 60 * 24);
							Date dateTo = new Date(date.getTime() - 7 * 1000 * 60 * 60 * 24);
							//left_time.setText(format.format(dateFrom) + "至" + format.format(dateTo));
							left_time.setText(format.format(format.parse(bean.getEndDate())));

							switch (bean.StatusCode) {// 0：不能申请转让 1：已完成 2：可以转让 3：转让中
								case 0:
									button.setClickable(false);
									button.setText("申请转让");
									button.setBackgroundResource(R.drawable.button_solid_gray);
									button.setEnabled(false);
									ll_exit.setVisibility(View.GONE);
									rl_zr.setVisibility(View.GONE);
									break;
								case 1:
									button.setClickable(false);
									button.setText("已完成");
									button.setBackgroundResource(R.drawable.button_solid_gray);
									button.setEnabled(false);
									break;
								case 2:
									button.setEnabled(true);
									button.setClickable(true);
									button.setText("申请转让");
									button.setBackgroundResource(R.drawable.button_solid_red);
									ll_exit.setVisibility(View.GONE);
									rl_zr.setVisibility(View.GONE);
									break;
								case 3:
									button.setEnabled(true);
									button.setClickable(true);
									button.setText("取消转让");
									button.setBackgroundResource(R.drawable.button_solid_red);
									break;
								default:
									break;
							}

						} else {
							LogUtil.e(TAG, "请求项目详情出现错误");
						}
					}
				} catch (Exception e) {
					LogUtil.d(TAG, "" + e.getMessage());
				}
			}

			@Override
			public void onErrorResponse(Exception error) {

			}
		});
		jsonRequest.setTag(BcbRequestTag.TradeRecordDetailTag);
		App.getInstance().getRequestQueue().add(jsonRequest);
	}

	private ClaimConveyBean bean2;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_hk:
				Intent intent = new Intent(Activity_Project_Investment_Details.this, A_TZ_Cheques.class);
				intent.putExtra("data", bean);
				startActivity(intent);
				break;
			case R.id.rl_zr:
				Activity_Rading_Record.launche(this, OrderNo);
				break;
			case R.id.button:
				Activity_Trading_Cancle.launche(this, OrderNo, bean.StatusCode);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && data.getBooleanExtra("rufush", false)) {
			loadData();
		}
	}
}