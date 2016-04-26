package com.bcb.presentation.view.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.common.app.App;
import com.bcb.R;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.data.bean.Project_Investment_Details_Bean;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ScreenUtils;
import com.bcb.data.util.TokenUtil;

public class Activity_Project_Investment_Details extends Activity_Base {
	
	private static final String TAG = "Activity_Project_Investment_Details";
	private String OrderNo;
	private TextView name;
	private TextView bidding_status;
	private TextView bidding_money;
	private TextView expected_earning;
	private TextView bidding_time;
	private TextView financing_amount;
	private TextView annual_yield;
	private TextView earnings_beginning;
	private TextView earnings_end;

    private ImageView image_view;

	private LinearLayout Reward_layout;
	private RelativeLayout Reward_layout_1, Reward_layout_2;
	private TextView textView1, textView2, textView3, textView4, textView5, textView6;
	
	private Project_Investment_Details_Bean bean;

    private BcbRequestQueue requestQueue;
	
	public static void launche(Context ctx, String OrderNo) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Project_Investment_Details.class);
		intent.putExtra("OrderNo", OrderNo);
		ctx.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Project_Investment_Details.this);
		setBaseContentView(R.layout.activity_project_investment_details);
		setLeftTitleVisible(true);
		setTitleValue("项目投资详情");
		requestQueue = BcbNetworkManager.newRequestQueue(this);
        init();
	}
	private void init() {
		OrderNo = getIntent().getStringExtra("OrderNo");
		name = (TextView) findViewById(R.id.name);
		bidding_status = (TextView) findViewById(R.id.bidding_status);
		bidding_money = (TextView) findViewById(R.id.bidding_money);
		expected_earning = (TextView) findViewById(R.id.expected_earning);
		bidding_time = (TextView) findViewById(R.id.bidding_time);
		financing_amount = (TextView) findViewById(R.id.financing_amount);
		annual_yield = (TextView) findViewById(R.id.annual_yield);
		earnings_beginning = (TextView) findViewById(R.id.earnings_beginning);
		earnings_end = (TextView) findViewById(R.id.earnings_end);
		
		Reward_layout  = (LinearLayout) findViewById(R.id.Reward_layout);
		Reward_layout_1  = (RelativeLayout) findViewById(R.id.Reward_layout_1);
		Reward_layout_2  = (RelativeLayout) findViewById(R.id.Reward_layout_2);
		
		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView4);
		textView5 = (TextView) findViewById(R.id.textView5);
		textView6 = (TextView) findViewById(R.id.textView6);
        //底部Banner图片要根据屏幕分辨率调整宽高比
        image_view = (ImageView) findViewById(R.id.image_view);
        //根据Banner的宽高比进行等比缩放
        ViewGroup.LayoutParams params = image_view.getLayoutParams();
        int width = ScreenUtils.getScreenDispaly(this)[0];
        params.height= width * 316 / 1280;
        params.width = width;
        image_view.setLayoutParams(params);
		loadData();
	}
	
	private void loadData() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("OrderNo", OrderNo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.TradingRecordDetail, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean flag = PackageUtil.getRequestStatus(response, Activity_Project_Investment_Details.this);
                    if(true == flag){
                        JSONObject obj = PackageUtil.getResultObject(response);
                        //判断JSON对象是否为空
                        if (obj != null) {
                            bean = App.mGson.fromJson(obj.toString(), Project_Investment_Details_Bean.class);
                        }
                        if (null != bean) {
                            name.setText(bean.getPackageName());
                            bidding_status.setText(TextUtils.isEmpty(bean.getOrderStatus()) ? "" : bean.getOrderStatus());
                            bidding_money.setText(bean.getOrderAmount()>0 ? String.format("%.2f", bean.getOrderAmount()) : "0" );
                            bidding_time.setText(TextUtils.isEmpty(bean.getPayTime()) ? "" : bean.getPayTime() );
                            financing_amount.setText(bean.getAmountTotal() > 0 ? String.format("%.2f", bean.getAmountTotal()) : "0");
                            //预期收益还要加上奖励
                            String expectMoney = bean.getInterestAmount() > 0 ? String.format("%.2f", bean.getInterestAmount()) + "" : "0"
                                    + (bean.getRewardAmount() > 0 ? "(含" + String.format("%.2f", bean.getRewardAmount()) + "元奖励)" : "");
                            //年化收益率要加上奖励
                            String annualRate = (bean.getRate() > 0 ? String.format("%.2f", bean.getRate()) + "%" : "0")
                                    + (bean.getRewardRate() > 0 ? "(含" + String.format("%.2f", bean.getRewardRate()) + "%奖励)" : "");
                            if (bean.getRewardRateDescn() != null && !bean.getRewardRateDescn().equalsIgnoreCase("null") && !bean.getRewardRateDescn().equalsIgnoreCase("")) {
                                annualRate = (bean.getRate() > 0 ? bean.getRate()+ "%" : "0") +
                                        (bean.getRewardRate() > 0 ? "(含" + bean.getRewardRate() + "%" + bean.getRewardRateDescn() + "奖励)" : "");
                                expectMoney = bean.getInterestAmount() > 0 ? String.format("%.2f", bean.getInterestAmount()) + "" : "0"
                                        + (bean.getRewardAmount() > 0 ? "(含" + String.format("%.2f", bean.getRewardAmount()) + "元"+ bean.getRewardRateDescn() +"奖励)" : "");
                            }
                            expected_earning.setText(expectMoney);
                            annual_yield.setText(annualRate);
                            earnings_beginning.setText(TextUtils.isEmpty(bean.getAuditTime()) ? "" : bean.getAuditTime() );
                            earnings_end.setText(TextUtils.isEmpty(bean.getPayEndDate()) ? "" : bean.getPayEndDate());

                            if (!TextUtils.isEmpty(bean.getRewardRateDescn()) || !TextUtils.isEmpty(bean.getDiscountDescn())) {
                                Reward_layout.setVisibility(View.VISIBLE);
                            } else {
                                Reward_layout.setVisibility(View.GONE);
                            }

                            if (!TextUtils.isEmpty(bean.getRewardRateDescn())) {
                                Reward_layout_1.setVisibility(View.VISIBLE);
                                textView1.setText(bean.getRewardAmount()+"元");
                                textView2.setText(bean.getRewardRateDescn());
                                textView3.setText(bean.getRewardRateDescn() + "将以现金形式到期结算到用户资金账户");
                            } else {
                                Reward_layout_1.setVisibility(View.GONE);
                            }

                            if (!TextUtils.isEmpty(bean.getDiscountDescn())) {
                                Reward_layout_2.setVisibility(View.VISIBLE);
                                textView4.setText(bean.getDiscountDescn());
                                textView5.setText("购房补贴金");
                                textView6.setText("项目地址：无");
                            } else {
                                Reward_layout_2.setVisibility(View.GONE);
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
        requestQueue.add(jsonRequest);
	}
}

