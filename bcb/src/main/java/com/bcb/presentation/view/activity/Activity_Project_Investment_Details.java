package com.bcb.presentation.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.bcb.presentation.view.custom.AlertView.DialogBQT2;
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
	private TextView top_amount, earning_expected, income_total, tv_id_number, state_title, state_below;
	private TextView biddingtime, earningtime, endtime;
	private TextView annual_yield, earnings_end, have, left, left_time;
	LinearLayout ll_id_number;
	HorizontalProgressBarWithNumber pb;
	private RelativeLayout rl_hk, rl_zr, tourl;
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
//		setRightBtnVisiable(View.VISIBLE);
//		setRightBtnImg(R.drawable.ico_info, new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (bean != null && bean.PackageId != null && bean.Type != null) {
//					int type = 0;
//					if (bean.Type.equals("claim_convey") || bean.Type.equals("mon_package")) type = 1;
//					//else if (bean.Type.equals("mon_package")) type = 2;//monkey_package
//					Activity_NormalProject_Introduction.launche2(Activity_Project_Investment_Details.this, bean.PackageId, 0, type);//标类型：prj_package则为普通标 claim_convey则为债权转让标
//				} else Toast.makeText(Activity_Project_Investment_Details.this, "获取数据失败", Toast.LENGTH_SHORT).show();
//			}
//		});
		OrderNo = getIntent().getStringExtra("OrderNo");
		initView();
		loadData();
	}

	private void initView() {
		pb = (HorizontalProgressBarWithNumber) findViewById(R.id.pb);
		top_amount = (TextView) findViewById(R.id.top_amount);
		earning_expected = (TextView) findViewById(R.id.earning_expected);
		income_total = (TextView) findViewById(R.id.income_total);
		tv_id_number = (TextView) findViewById(R.id.tv_id_number);
		ll_id_number = (LinearLayout) findViewById(R.id.ll_id_number);
		ll_id_number.setOnClickListener(this);
		biddingtime = (TextView) findViewById(R.id.biddingtime);
		earningtime = (TextView) findViewById(R.id.earningtime);
		endtime = (TextView) findViewById(R.id.endtime);
		annual_yield = (TextView) findViewById(R.id.annual_yield);
		earnings_end = (TextView) findViewById(R.id.earnings_end);
		state_title = (TextView) findViewById(R.id.state_title);
		state_below = (TextView) findViewById(R.id.state_below);
		have = (TextView) findViewById(R.id.have);
		left = (TextView) findViewById(R.id.left);
		left_time = (TextView) findViewById(R.id.left_time);
		button = (Button) findViewById(R.id.button);
		rl_hk = (RelativeLayout) findViewById(R.id.rl_hk);
		rl_zr = (RelativeLayout) findViewById(R.id.rl_zr);
		tourl = (RelativeLayout) findViewById(R.id.tourl);
		ll_exit = (LinearLayout) findViewById(R.id.ll_exit);
		rl_hk.setOnClickListener(this);
		rl_zr.setOnClickListener(this);
		button.setOnClickListener(this);
		tourl.setOnClickListener(this);
	}

	String endTime;

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
									//订单号
									tv_id_number.setText(OrderNo);
									//状态
									state_title.setText(bean.getStatus());
									//在投本金，预期收益，已获收益
									top_amount.setText(String.format("%.2f", bean.getOrderAmount()));
									earning_expected.setText(String.format("%.2f", bean.getPreInterest()));
									income_total.setText(String.format("%.2f", bean.getInterest()));

									//加入时间，起息时间，锁定到期
									SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
									Date biddingDate = format.parse(bean.getPayTime());
									Date endDate = format.parse(bean.getEndDate());
									biddingtime.setText(format.format(biddingDate));
									if (bean.getInterestTakeDate() != null && !bean.getInterestTakeDate().equals("")) {
										Date earningDate = format.parse(bean.getInterestTakeDate());
										earningtime.setText(format.format(earningDate));
									}
									endTime = format.format(endDate);
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
									//退出时间
									Date date = format.parse(bean.getEndDate());
									Date dateFrom = new Date(date.getTime() - 14 * 1000 * 60 * 60 * 24);
									Date dateTo = new Date(date.getTime() - 7 * 1000 * 60 * 60 * 24);
									//left_time.setText(format.format(dateFrom) + "至" + format.format(dateTo));
									SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
									switch (bean.StatusCode) {// 0：不能申请转让 1：已完成 2：可以转让 3：转让中
										case 0:
											button.setVisibility(View.INVISIBLE);
											button.setClickable(false);
											button.setText("申请退出");
											button.setEnabled(false);
											ll_exit.setVisibility(View.GONE);
											break;
										case 1:
											button.setVisibility(View.INVISIBLE);
											button.setClickable(false);
											button.setText("已完成");
											button.setEnabled(false);
											break;
										case 2:
											button.setVisibility(View.VISIBLE);
											button.setEnabled(true);
											button.setClickable(true);
											button.setText("申请退出");
											ll_exit.setVisibility(View.GONE);
											//可申请退出：{债权可退出起始日}~{债权可退出截止日}可申请退出
											state_below.setText(format2.format(dateFrom) + "~" + format2.format(dateTo) + "可申请退出");
											break;
										case 3:
											button.setVisibility(View.VISIBLE);
											button.setEnabled(true);
											button.setClickable(true);
											button.setText("撤销退出");
											//可撤销退出：{债权可退出起始日}~{债权可退出截止日}可撤销退出
											state_below.setText(format2.format(dateFrom) + "~" + format2.format(dateTo) + "可撤销退出");
											break;
										default:
											break;
									}
									switch (bean.Phase) {//订单所处阶段 1：加入；5：加入后至开始计息前；10：开始计息；50：开始计息后至锁定到期前；100: 锁定到期
										case 1:
										case 5://待起息。文案显示为“将于{起息日}开始计息”
											pb.setProgress(30);
											state_below.setText("将于 " + format.format(format.parse(bean.getInterestTakeDate())) + " 开始计息");
											break;
										case 10:
											pb.setProgress(50);
											break;
										case 50:
											pb.setProgress(70);
											break;
										case 100://收益完成：“已于{债权交割日}{退出方式}”。
											//退出方式：1、收益完成（包含完成正常还款、债权转让）2、	提前退出（借款人提前还款）
											pb.setProgress(100);
											state_below.setText("已于" + format.format(endDate) + "退出，退出方式由后台确定");
											break;
//										default:
//											pb.setProgress(10);
//											break;
									}
									//年化利率，锁定期限，已收本息，剩余本息
									annual_yield.setText(String.format("%.2f", bean.getRate()) + "%");
									earnings_end.setText(bean.getDuration());//封闭期 带单位【 "Duration": 3天】
									have.setText(String.format("%.2f", bean.ReceivedPrincipalAndInterest));
									left.setText(String.format("%.2f", bean.WaitPrincipalAndInterest));

									left_time.setText(format.format(format.parse(bean.getEndDate())));

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
			case R.id.tourl:
				Activity_Browser.launche(this, "项目详情", "http://192.168.20.121/doku.php?id=api:doc2.0:investdetail");
				break;
			case R.id.ll_id_number:
				ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				cm.setPrimaryClip(ClipData.newPlainText("label", OrderNo));
				Toast.makeText(this, "订单号已经复制到剪贴板", Toast.LENGTH_SHORT).show();
				break;
			case R.id.rl_hk:
				Intent intent = new Intent(Activity_Project_Investment_Details.this, A_TZ_Cheques.class);
				intent.putExtra("data", bean);
				startActivity(intent);
				break;
			case R.id.rl_zr:
				Activity_Rading_Record.launche(this, OrderNo);
				break;
			case R.id.button:
				showDialog();
				break;
		}
	}

	private void showDialog() {
		switch (bean.StatusCode) {
			case 2:
				DialogBQT2 dialogBQT2 = new DialogBQT2(this) {
					@Override
					public void onSureClick(View v) {
						super.onSureClick(v);
						requestZR(UrlsOne.REQUESTZR);
						dismiss();
					}
				};
				dialogBQT2.getMessage().setText("1、本息预计在 " + endTime + " 回款，\n继续持有将获得更高收益哦\n\n2、本次退出费用为 0 元");
				dialogBQT2.show();
				break;
			case 3:
				requestZR(UrlsOne.UNREQUESTZR);
				break;
		}
	}

	/**
	 * 申请或取消转让
	 */
	private void requestZR(String url) {
		LogUtil.i("bqt", "【Activity_Trading_Cancle】【onResponse】路径" + url);
		JSONObject obj = new JSONObject();
		try {
			obj.put("OrderNo", OrderNo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(this), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "申请债权转让" + response.toString());
				if (response.optBoolean("result", false)) {
					if (bean.StatusCode == 3) {//申请转让
						Toast.makeText(Activity_Project_Investment_Details.this, "撤销成功！项目将继续持有并获得收益", Toast.LENGTH_SHORT).show();
					}
					loadData();
				} else {
					Activity_Tips_FaileOrSuccess.launche(Activity_Project_Investment_Details.this, Activity_Tips_FaileOrSuccess.ZR_FAILED,
							response.optString("message"));
				}
			}

			@Override
			public void onErrorResponse(Exception error) {

			}
		});
		App.getInstance().getRequestQueue().add(jsonRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && data.getBooleanExtra("rufush", false)) {
			loadData();
		}
	}
}