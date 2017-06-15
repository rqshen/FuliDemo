package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.BuyProjectSuccess;
import com.bcb.util.MyActivityManager;
import com.bcb.util.ScreenUtils;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.presentation.presenter.IPresenter_Base;
import com.bcb.presentation.presenter.IPresenter_UpdateUserInfoImpl;
import com.bcb.presentation.view.activity_interface.Interface_Base;

public class Activity_BuyProject_Success extends Activity_Base implements Interface_Base {
	private TextView OrderAmount, Duration, InterestAmount, RewardRateDescn_content, RewardRateDescn_tips;
	private ImageView image_view;
	private BuyProjectSuccess successInfo;
	private LinearLayout RewardRateDescn_layout;
    private IPresenter_Base iPresenterUpdateUserInfo;

	public static void launche(Context ctx, BuyProjectSuccess successInfo) {
		Intent intent = new Intent();
		intent.putExtra("successInfo", successInfo);
		intent.setClass(ctx, Activity_BuyProject_Success.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_BuyProject_Success.this);
		setBaseContentView(R.layout.activity_buyproject_success);
		setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroardCast();
                MyActivityManager.getInstance().finishAllActivity();
            }
        });
		setTitleValue("购买成功");
		successInfo = (BuyProjectSuccess) getIntent().getSerializableExtra("successInfo");
        //刷新用户数据
        iPresenterUpdateUserInfo = new IPresenter_UpdateUserInfoImpl(this, this);
        //刷新用户数据
        iPresenterUpdateUserInfo.onRequest(1, "刷新用户数据");
        //初始化界面元素
		init();
		//初始化微信助手
		setupImageView();
	}

	private void init() {
		//获取要显示的元素
		OrderAmount = (TextView) findViewById(R.id.OrderAmount);
		Duration = (TextView) findViewById(R.id.Duration);
		InterestAmount = (TextView) findViewById(R.id.InterestAmount);
		RewardRateDescn_content = (TextView) findViewById(R.id.RewardRateDescn_content);
		RewardRateDescn_tips = (TextView) findViewById(R.id.RewardRateDescn_tips);
		RewardRateDescn_layout = (LinearLayout) findViewById(R.id.RewardRateDescn_layout);

		if (null == successInfo){
			return;
		}

        //显示投资金额
		OrderAmount.setText(successInfo.getOrderAmount()+"元");

		//根据天标还是月标来显示期限
		switch (successInfo.getDurationExchangeType()) {
            case 1:
                Duration.setText(successInfo.getDuration() + "天");
                break;

            case 2:
                Duration.setText(successInfo.getDuration() + "个月");
                break;

            default:
                Duration.setText("");
                break;
        }

        //预期收益元
		InterestAmount.setText(successInfo.getInterestAmount()+"元");
		//判断额外收益是否大于0
		if (successInfo.getRewardInterestAmount() > 0) {
			RewardRateDescn_layout.setVisibility(View.VISIBLE);
			RewardRateDescn_content.setText(successInfo.getRewardInterestAmount() + "元");
			//判断奖励描述是否存在，如果存在，则显示存在的信息，不存在则显示默认的描述
			if (successInfo.getRewardRateDescn() != null
					&& !successInfo.getRewardRateDescn().equalsIgnoreCase("null")
					&& !successInfo.getRewardRateDescn().equalsIgnoreCase("")) {
				RewardRateDescn_tips.setText(successInfo.getRewardRateDescn());
			} else {
				RewardRateDescn_tips.setText("将以现金形式，到期结算到用户资金账户");
			}
		} else {
			RewardRateDescn_layout.setVisibility(View.GONE);
		}

	}


	/**
	 *  起息第一时间通知
	 */
	private void setupImageView() {
		//底部Banner图片要根据屏幕分辨率调整宽高比
		image_view = (ImageView) findViewById(R.id.image_view);
		image_view.setVisibility(View.VISIBLE);
		image_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击图片，跳转至微信
				ProjectDetailActivity.launche(Activity_BuyProject_Success.this, "起息第一时间通知", UrlsOne.WxBindIndex);
			}
		});
		//根据Banner的宽高比进行等比缩放
		ViewGroup.LayoutParams params = image_view.getLayoutParams();
		int width = ScreenUtils.getScreenDispaly(this)[0];
		params.height= width * 316 / 1280;
		params.width = width;
		image_view.setLayoutParams(params);
	}


	//重写点击返回按钮，发送广播并销毁Activity对象
	@Override
	public void onBackPressed() {
        sendBroardCast();
        MyActivityManager.getInstance().finishAllActivity();
	}

	//将买标成功信息发送出去
	private void sendBroardCast() {
		Intent intent = new Intent();
		intent.setAction("com.bcb.project.buy.success");
		sendBroadcast(intent);
	}

    //请求用户信息回调
    @Override
    public void onRequestResult(int resultStatus, String message) {
    }
}


