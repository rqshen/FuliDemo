package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.UserWallet;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MQCustomerManager;
import com.bcb.utils.MyActivityManager;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.ScreenUtils;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_ChangeMoney_Success extends Activity_Base implements View.OnClickListener {
	//按钮
	private Button buttonOK;
	//充值状态标题
	private TextView txt_status;
	//充值成功显示账户余额
	private TextView balance;
	//用户余额
	private UserWallet mUserWallet;
	//判断是否充值还是提现
	private String action;

	//充值状态
	private int rechargeStatus = 0;
    private boolean rechargeSuccess = false;
	//充值失败原因
	private String errorMessage = "";

	public static String ACTION_Recharge = "Recharge";
	public static String ACTION_Withdrawals = "Withdrawals";

	//充值成功
	private RelativeLayout layout_success;

	//充值失败
	private RelativeLayout layout_failed;
	private TextView failText;
	private LinearLayout layout_recharge_failed;

	//充值过程中
	private RelativeLayout layout_recharging;
	private TextView secondText;
	private GifView rechargingGif;

	//充值超时
	private RelativeLayout layout_overtime;
	private Button buttonOvertime;


	//定时器
	private int time;
	private Timer timer;
	private int tradeStatus = 10;

	//订单号
	private String transNo;
	//充值余额
	private float amount;

    private BcbRequestQueue requestQueue;

	private ImageView image_view;

	//提现构造函数
	public static void launche(Context ctx, String action) {
		Intent intent = new Intent();
		intent.putExtra("action", action);
		intent.setClass(ctx, Activity_ChangeMoney_Success.class);
		ctx.startActivity(intent);
	}

	//充值构造函数
	public static void launche(Context context, 		//上下文
							   String action, 			//充值还是提现
							   int rechargeStatus,		//充值状态
							   String errorMessage,		//出错消息
							   String transNo,			//充值订单号
							   float amount)
	{
		Intent intent = new Intent();
		intent.putExtra("action", action);
		intent.putExtra("rechargeStatus", rechargeStatus);
		intent.putExtra("errorMessage", errorMessage);
		intent.putExtra("transNo", transNo);
		intent.putExtra("amount", amount);
		intent.setClass(context, Activity_ChangeMoney_Success.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_ChangeMoney_Success.this);
		action = getIntent().getStringExtra("action");
		rechargeStatus = getIntent().getIntExtra("rechargeStatus", 0);
		errorMessage = getIntent().getStringExtra("errorMessage");
		transNo = getIntent().getStringExtra("transNo");
		amount = getIntent().getFloatExtra("amount", 0);
		setBaseContentView(R.layout.activity_change_money_success);
		setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rechargeSuccess) {
                    sendBroardCast();
                }
                MyActivityManager.getInstance().finishAllActivity();
            }
        });
        requestQueue = MyApplication.getInstance().getRequestQueue();
		init();
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		//充值成功
		layout_success = (RelativeLayout) findViewById(R.id.layout_success);
		//充值失败
		layout_failed = (RelativeLayout) findViewById(R.id.layout_failed);
		//充值过程中
		layout_recharging = (RelativeLayout) findViewById(R.id.layout_recharging);
		//Gif图层
		rechargingGif = (GifView) findViewById(R.id.recharging_gif);
		rechargingGif.setGifImage(R.drawable.refreshing);
		rechargingGif.setGifImageType(GifView.GifImageType.COVER);

		//充值超时
		layout_overtime = (RelativeLayout) findViewById(R.id.layout_overtime);
		buttonOvertime = (Button) findViewById(R.id.button_overtime);
		buttonOvertime.setOnClickListener(this);

		//初始化layout
		setupLayout();

		//充值成功的文字
		txt_status = (TextView) findViewById(R.id.txt_status);

		//按钮
		buttonOK = (Button) findViewById(R.id.button_ok);
		buttonOK.setOnClickListener(this);
		//显示金额
		balance = (TextView) findViewById(R.id.balance);

		//充值过程的秒数
		secondText = (TextView) findViewById(R.id.second_text);

		//充值失败提示
		failText = (TextView) findViewById(R.id.fail_text);
        layout_recharge_failed = (LinearLayout) findViewById(R.id.layout_recharge_failed);
        layout_recharge_failed.setOnClickListener(this);

		//充值
		if(null != action && action.equals(ACTION_Recharge)) {

			switch (rechargeStatus) {
				//充值过程中
				case 0:
					setTitleValue("充值中");
					setupRecharging();
					//开启定时器
					setTimer();
					break;

				//充值成功
				case 1:
					setTitleValue("充值成功");
					txt_status.setText("充值成功");
                    rechargeSuccess = true;
					rechargeSuccess();
					break;

				//充值失败
				case -1:
					setTitleValue("充值失败");
					rechargeFailed();
					break;
			}

		}
		//提现
		else if (null != action && action.equals(ACTION_Withdrawals)) {
			setTitleValue("提现成功");
			txt_status.setText("当前账户余额为：" + (MyApplication.mUserWallet.getBalanceAmount() <= 0 ? "0.00" : String.format("%.2f", MyApplication.mUserWallet.getBalanceAmount())) + "元");
			layout_success.setVisibility(View.VISIBLE);
			setupImageView();
		}
	}

	/**
	 *  提现结果早知道
	 */
	private void setupImageView() {
		//底部Banner图片要根据屏幕分辨率调整宽高比
		image_view = (ImageView) findViewById(R.id.image_view);
		image_view.setVisibility(View.VISIBLE);
		image_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击图片，跳转至微信
				ProjectDetailActivity.launche(Activity_ChangeMoney_Success.this, "提现结果早知道", UrlsOne.WxBindIndex);
			}
		});
		//根据Banner的宽高比进行等比缩放
		ViewGroup.LayoutParams params = image_view.getLayoutParams();
		int width = ScreenUtils.getScreenDispaly(this)[0];
		params.height= width * 316 / 1280;
		params.width = width;
		image_view.setLayoutParams(params);
	}

	//定时器
	private void setTimer() {
		time = 30;
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				time--;
				//如果时间小于等于0，则停止计时
				if (time <= 0) {
					handler.sendEmptyMessage(4);
				}
				//判断如果仍然在充值过程中，就每隔五秒进行一次请求
				if (tradeStatus == 10) {
					//更新秒数
					handler.sendEmptyMessage(3);
					//每隔5秒请求一次服务器
					if (time % 5 == 0) {
						handler.sendEmptyMessage(1);
					}
				} else {
					handler.sendEmptyMessage(2);
				}
			}
		};
		//一秒一次
		timer.schedule(task, 0, 1000);
	}

	private void stopTimer() {
		if(timer != null){
			timer.cancel();
			timer = null;
		}
	}


	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				//请求一次
				case 1:
					requestOrderStatus();
					break;

				//结束定时器，判断状态
				case 2:
					judgeStatus(tradeStatus);
					break;

				//更新秒数
				case 3:
					secondText.setText(time + "");
					break;

				//超时了
				case 4:
					stopTimer();
					setupLayout();
					layout_overtime.setVisibility(View.VISIBLE);
					break;
			}
		};
	};


	/**
	 * 请求订单详情
	 */
	private void requestOrderStatus() {
        JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("TransNo", transNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.RechargeOrderStatus, jsonObject, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (PackageUtil.getRequestStatus(response, Activity_ChangeMoney_Success.this)) {
                        JSONObject resultObject = PackageUtil.getResultObject(response);
                        //判断JSON对象是否为空
                        if (resultObject != null) {
                            //充值状态码
                            tradeStatus = resultObject.getInt("TradeStatus");
                            //出错信息
                            errorMessage = resultObject.getString("StatusDescn");
                            //判断状态码，是否成功还是失败
                            judgeStatus(tradeStatus);
                        }
                    }
                    //状态不为1的时候，请求结果
                    else {
                        LogUtil.d("请求失败", response.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.RechargeOrderStatusTag);
        requestQueue.add(jsonRequest);
	}

	/**
	 * 判断状态码
	 * @param status 状态码
     */
	private void judgeStatus(int status) {
		switch (status) {
			//充值中
			case 10:
				//FIXME 暂时不能测试
//				secondText.setText(time + "");
				break;

			//充值成功
			case 20:
				stopTimer();
				rechargeSuccess();
				break;

			//充值失败
			case 30:
				stopTimer();
				rechargeFailed();
				break;
		}
	}

	//充值中
	private void setupRecharging() {
		setupLayout();
		layout_recharging.setVisibility(View.VISIBLE);
	}

	//充值成功
	private void rechargeSuccess() {
		//隐藏其他页面
		setupLayout();
		layout_success.setVisibility(View.VISIBLE);
		//将充值金额写入全局静态数据区，要判断是否存在
		if (MyApplication.mUserWallet != null) {
			double balanceAmount = MyApplication.mUserWallet.getBalanceAmount() + amount;
			MyApplication.mUserWallet.setBalanceAmount(balanceAmount);
		}
		//静态数据区中不存在数据时，则加载数据
		else {
			loadData();
		}
		//显示成功信息，加载用户余额
		balance.setText("当前账户余额为：" + String.format("%.2f", MyApplication.mUserWallet.getBalanceAmount()) + "元");
}

	//充值失败
	private void rechargeFailed() {
		//隐藏其他页面
		setupLayout();
		layout_failed.setVisibility(View.VISIBLE);
		//显示充值失败原因
		failText.setText(errorMessage);
	}

	//初始化layout
	private void setupLayout() {
		layout_failed.setVisibility(View.GONE);
		layout_success.setVisibility(View.GONE);
		layout_recharging.setVisibility(View.GONE);
		layout_overtime.setVisibility(View.GONE);
	}

    /**
     * 加载用户余额
     */
	private void loadData() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (PackageUtil.getRequestStatus(response, Activity_ChangeMoney_Success.this)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        mUserWallet = MyApplication.mGson.fromJson(data.toString(), UserWallet.class);
                    }
                    //判断是否存在用户余额信息
                    if (null != mUserWallet) {
                        balance.setText("当前账户余额为：" + String.format("%.2f", mUserWallet.getBalanceAmount()) + "元");
                        //将用户余额存入静态数据区
                        MyApplication.mUserWallet = mUserWallet;
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_ChangeMoney_Success.this, "网络异常，请稍后重试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
        requestQueue.add(jsonRequest);
	}

	/**
	 * 重写点击返回按钮，发送广播并销毁Activity对象
	 */
	@Override
	public void onBackPressed() {
		//如果充值成功，则发送广播
		if(rechargeSuccess) {
			sendBroardCast();
		}
		MyActivityManager.getInstance().finishAllActivity();
	}

	//暂存充值的订单号的信息
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		//只有订单号不为空时才缓存
		if (transNo != null
				&& !transNo.equalsIgnoreCase("null")
				&& !transNo.equalsIgnoreCase("")) {
			savedInstanceState.putString("transNo", transNo);
		}

	}

	//取出暂存的订单号信息
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//判断只有取出暂存的订单号不为空时才将其取出
		if (!savedInstanceState.getString("transNo").isEmpty()
				&& !savedInstanceState.getString("transNo").equalsIgnoreCase("null")
				&& !savedInstanceState.getString("transNo").equalsIgnoreCase("")) {
			transNo = savedInstanceState.getString("transNo");
		}
	}
	
	//将买标成功信息发送出去
	private void sendBroardCast() {
		Intent intent = new Intent();
		intent.setAction("com.bcb.money.change.success");
		sendBroadcast(intent);
	}

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            //充值成功，点击返回账号
            case R.id.button_ok:
                sendBroardCast();
                MyActivityManager.getInstance().finishAllActivity();
                break;

            //充值失败联系客服
            case R.id.layout_recharge_failed:
                String userId = null;
                if (MyApplication.mUserDetailInfo != null) {
                    userId = MyApplication.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(this).showCustomer(userId);
                break;

            //充值超时刷新按钮，重新开始定时器
            case R.id.button_overtime:
                setupLayout();
                layout_recharging.setVisibility(View.VISIBLE);
                setTimer();
                break;

        }
    }
}

