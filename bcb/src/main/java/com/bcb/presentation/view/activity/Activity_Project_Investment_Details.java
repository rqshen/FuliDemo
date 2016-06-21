package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.Project_Investment_Details_Bean;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ScreenUtils;
import com.bcb.data.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity_Project_Investment_Details extends Activity_Base {
	
	private static final String TAG = "Activity_Project_Investment_Details";
	private String OrderNo;
    private TextView amount,decimal;//投标金额整数及小数
	private TextView name;//项目名称
	private TextView expected_earning;//预期收益
	private TextView bidding_time;//投资时间
	private TextView financing_amount;//融资金额
	private TextView annual_yield;//年化收益率
	private TextView earnings_beginning;//项目起利期
	private TextView earnings_end;//项目到期日

    private ImageView image_view;

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
		requestQueue = App.getInstance().getRequestQueue();
        init();
	}
	private void init() {
		OrderNo = getIntent().getStringExtra("OrderNo");
        amount = (TextView) findViewById(R.id.amount);
        decimal = (TextView) findViewById(R.id.decimal);
		name = (TextView) findViewById(R.id.name);
		expected_earning = (TextView) findViewById(R.id.expected_earning);
		bidding_time = (TextView) findViewById(R.id.bidding_time);
		financing_amount = (TextView) findViewById(R.id.financing_amount);
		annual_yield = (TextView) findViewById(R.id.annual_yield);
		earnings_beginning = (TextView) findViewById(R.id.earnings_beginning);
		earnings_end = (TextView) findViewById(R.id.earnings_end);
		
        setupImageView();
		loadData();
	}

    /**
     *  回款第一时间通知
     */
    private void setupImageView() {
        //底部Banner图片要根据屏幕分辨率调整宽高比
        image_view = (ImageView) findViewById(R.id.image_view);
        image_view.setVisibility(View.VISIBLE);
        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击图片，跳转至微信
                Activity_WebView.launche(Activity_Project_Investment_Details.this, "回款第一时间通知", UrlsOne.WxBindIndex);
            }
        });
        //根据Banner的宽高比进行等比缩放
        ViewGroup.LayoutParams params = image_view.getLayoutParams();
        int width = ScreenUtils.getScreenDispaly(this)[0];
        params.height= width * 316 / 1280;
        params.width = width;
        image_view.setLayoutParams(params);
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
                    if(flag){
                        JSONObject obj = PackageUtil.getResultObject(response);
                        //判断JSON对象是否为空
                        if (obj != null) {
                            bean = App.mGson.fromJson(obj.toString(), Project_Investment_Details_Bean.class);
                        }
                        if (null != bean) {
                            name.setText(bean.getPackageName());
                            //投资金额截取整数跟小数，size设计不一样
                            String[] moneys = String.format("%.2f", bean.getOrderAmount()).split("\\.");
                            amount.setText("-" + moneys[0]);
                            decimal.setText("." + moneys[1]);

                            bidding_time.setText(TextUtils.isEmpty(bean.getPayTime()) ? "" : bean.getPayTime() );
                            financing_amount.setText(bean.getAmountTotal() > 0 ? String.format("%.2f元", bean.getAmountTotal()) : "0元");
                            //预期收益还要加上奖励
                            String expectMoney = bean.getInterestAmount() > 0 ? String.format("%.2f元", bean.getInterestAmount()) + "" : "0元"
                                    + (bean.getRewardAmount() > 0 ? "(含" + String.format("%.2f", bean.getRewardAmount()) + "元奖励)" : "");
                            //年化收益率要加上奖励
                            String annualRate = (bean.getRate() > 0 ? String.format("%.2f", bean.getRate()) + "%" : "0")
                                    + (bean.getRewardRate() > 0 ? "(含" + String.format("%.2f", bean.getRewardRate()) + "%奖励)" : "");
                            if (bean.getRewardRateDescn() != null && !bean.getRewardRateDescn().equalsIgnoreCase("null") && !bean.getRewardRateDescn().equalsIgnoreCase("")) {
                                annualRate = (bean.getRate() > 0 ? bean.getRate()+ "%" : "0") +
                                        (bean.getRewardRate() > 0 ? "(含" + bean.getRewardRate() + "%" + bean.getRewardRateDescn() + "奖励)" : "");
                                expectMoney = bean.getInterestAmount() > 0 ? String.format("%.2f元", bean.getInterestAmount()) + "" : "0元"
                                        + (bean.getRewardAmount() > 0 ? "(含" + String.format("%.2f", bean.getRewardAmount()) + "元"+ bean.getRewardRateDescn() +"奖励)" : "");
                            }
                            expected_earning.setText(expectMoney);
                            annual_yield.setText(annualRate);
                            earnings_beginning.setText(TextUtils.isEmpty(bean.getAuditTime()) ? "" : bean.getAuditTime() );
                            earnings_end.setText(TextUtils.isEmpty(bean.getPayEndDate()) ? "" : bean.getPayEndDate());

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

