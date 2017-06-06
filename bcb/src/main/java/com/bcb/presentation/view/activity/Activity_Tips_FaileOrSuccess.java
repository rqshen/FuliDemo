package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.event.BroadcastEvent;
import com.bcb.module.discover.eliteloan.loanlist.LoanListActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.StringEventBusBean;
import com.bcb.data.bean.UserBankCard;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.util.ActivityCollector;
import com.bcb.util.DensityUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MQCustomerManager;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.module.home.MainActivity1;

import org.json.JSONObject;

import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * 提示界面
 */
public class Activity_Tips_FaileOrSuccess extends Activity_Base implements View.OnClickListener {
	public static final int OPEN_HF_SUCCESS = 1;//开户成功
	public static final int OPEN_HF_FAILED = 2;//开户失败
	//绑卡没有结果页
	public static final int BAND_HF_SUCCESS = 3;//绑卡成功
	public static final int BAND_HF_FAILED = 4;//绑卡失败
	public static final int CHARGE__HF_SUCCESS = 5;//充值成功
	public static final int CHARGE_HF_FAILED = 6;//充值失败
	public static final int TX_HF_SUCCESS = 7;//提现成功
	public static final int TX_HF_FAILED = 8;//提现失败
	public static final int BUY_HF_SUCCESS = 9;//申购成功
	public static final int BUY_HF_FAILED = 10;//申购失败
	public static final int SLB_SUCCESS = 11;//生利宝成功
	public static final int SLB_FAILED = 12;//生利宝失败
	public static final int JK_SUCCESS = 13;//借款成功

	public static final int ZR_SUCCESS = 14;//债权转让成功
	public static final int ZR_FAILED = 15;//债权转让失败

	public static final int ZDTB_SUCCESS = 16;//开通自动投标成功
	public static final int ZDTB_FAILED = 17;//开通自动投标失败

	public static final int EMAIL_SUCCESS = 18;

	private void initView() {
		switch (type) {
			case OPEN_HF_SUCCESS:
				title_text.setText("资金托管");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				iv_pic.setPadding(0, DensityUtils.dp2px(this, 50), 0, 0);
				tv_down.setVisibility(View.GONE);
				tv_up.setText("开户成功");
				tv_next.setText("查看托管账户");
				tv_next.setClickable(false);//╮(╯▽╰)╭
				handler.sendMessageDelayed(Message.obtain(handler, 1), 2000);
				break;
			case OPEN_HF_FAILED:
				title_text.setText("开户失败");
				iv_pic.setImageResource(R.drawable.failed_open_fh);
				tv_up.setText("开户失败");
				tv_down.setText(message);
				tv_next.setText("联系客服");
				break;
			case BAND_HF_SUCCESS:
				title_text.setText("绑卡成功");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				tv_up.setText("绑卡成功");
				tv_down.setVisibility(View.GONE);
				tv_next.setText("返回个人中心");
				tv_next.setClickable(false);//╮(╯▽╰)╭
				handler.sendMessageDelayed(Message.obtain(handler, 2), 2000);
				break;
			case BAND_HF_FAILED:
				title_text.setText("绑卡失败");
				iv_pic.setImageResource(R.drawable.failed_band_fh);
				tv_up.setText("绑卡失败");
				tv_down.setText("信息验证出现错误");
				tv_next.setText("联系客服");
				break;
			case CHARGE__HF_SUCCESS:
				title_text.setText("充值成功");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				iv_pic.setPadding(0, DensityUtils.dp2px(this, 30), 0, 0);
				tv_up.setText("充值成功");
				tv_next.setVisibility(View.GONE);
				tv_next.setClickable(false);//╮(╯▽╰)╭
				handler.sendMessageDelayed(Message.obtain(handler, 3), 2000);
				break;
			case CHARGE_HF_FAILED:
				title_text.setText("充值失败");
				iv_pic.setImageResource(R.drawable.failed_charge_hf);
				tv_up.setText("充值失败");
				message = message == null ? "银行卡余额不足" : message;
				tv_down.setText(message);
				tv_next.setText("联系客服");
				break;
			case TX_HF_SUCCESS:
				title_text.setText("提现成功");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				iv_pic.setPadding(0, DensityUtils.dp2px(this, 30), 0, 0);
				tv_up.setText("提现成功");
				tv_next.setVisibility(View.GONE);
				tv_down.setText("本次提现：" + message);
				tv_next.setClickable(false);//╮(╯▽╰)╭
				handler.sendMessageDelayed(Message.obtain(handler, 2), 2000);
				break;
			case TX_HF_FAILED:
				title_text.setText("提现失败");
				iv_pic.setImageResource(R.drawable.failed_band_fh);
				iv_pic.setPadding(0, DensityUtils.dp2px(this, 50), 0, 0);
				tv_up.setText("提现失败");
				tv_down.setText("账户余额不足");
				tv_next.setVisibility(View.GONE);
				break;
			case BUY_HF_SUCCESS:
				title_text.setText("购买成功");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				tv_up.setText("购买成功");
				tv_down.setVisibility(View.GONE);
				tv_next.setText("返回个人中心");
				tv_next.setClickable(false);//╮(╯▽╰)╭
				handler.sendMessageDelayed(Message.obtain(handler, 2), 2000);
				break;
			case BUY_HF_FAILED:
				title_text.setText("购买失败");
				iv_pic.setImageResource(R.drawable.failed_buy_fh);
				tv_up.setText("购买失败");
				tv_down.setText(message);
				tv_next.setText("联系客服");
				break;
			case SLB_SUCCESS:
				String[] splitStrs = Pattern.compile("\\|").split(message);//Java Split以竖线作为分隔符
				title_text.setText("生利宝");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				if (splitStrs[2].equalsIgnoreCase("I")) tv_up.setText("成功转入");
				else if (splitStrs[2].equalsIgnoreCase("O")) tv_up.setText("成功转出");
				else tv_up.setText("成功");
				tv_up.setTextColor(0xff22bb66);
				tv_down.setText("¥ " + splitStrs[3]);
				tv_down.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
				tv_next.setText("返回生利宝");
				tv_next.setClickable(false);//╮(╯▽╰)╭
				handler.sendMessageDelayed(Message.obtain(handler, 2), 2000);
				break;
			case SLB_FAILED:
				String[] splitStrs2 = Pattern.compile("\\|").split(message);//Java Split以竖线作为分隔符
				title_text.setText("生利宝");
				iv_pic.setImageResource(R.drawable.failed_charge_hf);
				if (splitStrs2[2].equalsIgnoreCase("I")) tv_up.setText("转入失败");
				else if (splitStrs2[2].equalsIgnoreCase("O")) tv_up.setText("转出失败");
				else tv_up.setText("失败");
				tv_down.setText("第三方支付平台出错");
				tv_next.setText("返回生利宝");
				break;
			case JK_SUCCESS:
				title_text.setText("借款");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				tv_up.setText("申请资料已提交");
				tv_down.setText("还差一步即可完成借款申请");
				tv_down.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				tv_next.setText("去校验邮箱");
				break;
			case ZR_SUCCESS:
				title_text.setText("提交成功");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				tv_up.setText("退出申请提交成功");//【您已成功申请债权转让】【成功取消债权转让】
				tv_down.setText(message);
				tv_next.setText("返回投资记录");
				break;
			case ZR_FAILED:
				title_text.setText("债权转让");
				iv_pic.setImageResource(R.drawable.failed_open_fh);
				tv_up.setText("失败");
				tv_down.setText(message);
				tv_next.setText("联系客服");
				break;
			case ZDTB_SUCCESS:
				title_text.setText("自动买入");
				iv_pic.setImageResource(R.drawable.success_open_hf);
				tv_up.setText("开通成功");
				tv_down.setText("");
				tv_next.setText("完成");
				//				MyApplication.mUserDetailInfo.AutoTenderPlanStatus=true;//手动更改
				tv_next.setClickable(false);//╮(╯▽╰)╭
				handler.sendMessageDelayed(Message.obtain(handler, 1), 2000);//请求服务器最新信息
				break;
			case ZDTB_FAILED:
				title_text.setText("自动买入");
				iv_pic.setImageResource(R.drawable.failed_open_fh);
				tv_up.setText("开通失败");
				tv_down.setText("");
				tv_next.setText("联系客服");
				break;
			case EMAIL_SUCCESS:
				title_text.setText("提交成功");
				setLeftTitleVisible(false);
				iv_pic.setImageResource(R.drawable.success_open_hf);
				tv_up.setText("邮箱验证成功！");
				tv_down.setText("工作人员会在1-3个工作日内完成审核");
				tv_down.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				tv_next.setText("完成");
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_img://返回
				switch (type) {
					//个人中心
					case CHARGE__HF_SUCCESS:
					case CHARGE_HF_FAILED:
					case TX_HF_SUCCESS:
					case TX_HF_FAILED:
						JumpToUser();
						break;
					//借款
					case JK_SUCCESS://返回借款列表
						startActivity(new Intent(Activity_Tips_FaileOrSuccess.this, LoanListActivity.class));
						finish();
						break;
					//客服
					default:
						finish();
						break;
				}
				break;
			//******************************************************************************************
			case R.id.tv_next://下一步
				switch (type) {
					case EMAIL_SUCCESS://借款列表
						startActivity(new Intent(this, LoanListActivity.class));//EliteLoanActivity
						EventBus.getDefault().post(new StringEventBusBean("LoanFinish"));
						finish();
						break;
					//托管
					case OPEN_HF_SUCCESS:
						startActivity(new Intent(Activity_Tips_FaileOrSuccess.this, Activity_TuoGuan_HF.class));
						finish();
						break;
					//个人中心
					case BAND_HF_SUCCESS:
					case BUY_HF_SUCCESS:
						JumpToUser();
						break;
					//结束当前页
					case SLB_SUCCESS:
					case SLB_FAILED:
					case ZDTB_SUCCESS:
						finish();
						break;
					//投资记录
					case ZR_SUCCESS:
//						startActivity(new Intent(Activity_Tips_FaileOrSuccess.this, Activity_Trading_Record.class));
//						finish();
						ActivityCollector.finishAll();
						break;
					//借款
					case JK_SUCCESS://校验邮箱
						A_Email_Check.launche(this);
						finish();
						break;
					//客服
					default:
						String userId = null;
						if (MyApplication.mUserDetailInfo != null) userId = MyApplication.mUserDetailInfo.getCustomerId();
						MQCustomerManager.getInstance(this).showCustomer(userId);
						finish();
						break;
				}
				break;
			default:
				break;
		}
	}

	//***********************************************************************************************************************************
	//
	//
	//***********************************************************************************************************************************
	public static final String TYPE = "type";
	private int type;
	public static final String MESSAGE = "message";
	private String message;//提示消息

	ImageView iv_pic;
	TextView title_text, tv_up, tv_down, tv_next;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					requestUserDetailInfo();
					break;
				case 2:
					requestUserBankCard();
					break;
				case 3:
					requestUserWallet();
					break;
				default:
					break;
			}
		}
	};

	public static void launche(Context ctx, int type, String message) {
		Intent intent = new Intent(ctx, Activity_Tips_FaileOrSuccess.class);
		intent.putExtra(TYPE, type);
		intent.putExtra(MESSAGE, message);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_failed_tips);
		ctx = this;
		init();
		Intent intent = getIntent();
		if (intent != null) {
			type = intent.getIntExtra(TYPE, 0);
			message = intent.getStringExtra(MESSAGE);
			initView();
		}

		ActivityCollector.addActivity(this);

	}

	private void init() {
		title_text = (TextView) findViewById(R.id.title_text);
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
		tv_up = (TextView) findViewById(R.id.tv_up);
		tv_down = (TextView) findViewById(R.id.tv_down);
		tv_next = (TextView) findViewById(R.id.tv_next);
		tv_next.setOnClickListener(this);
		//返回
		View back_img = findViewById(R.id.back_img);
		back_img.setVisibility(View.VISIBLE);
		back_img.setOnClickListener(this);
	}

	private void JumpToUser() {
		EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.USER));
		startActivity(new Intent(this, MainActivity1.class));
		finish();
	}

	Context ctx;

	//钱包
	private void requestUserWallet() {
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(ctx), new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				tv_next.setClickable(true);
				requestUserBankCard();
				LogUtil.i("bqt", "刷新－－账户余额：" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					JSONObject data = PackageUtil.getResultObject(response);
					if (data != null) {
						MyApplication.mUserWallet = MyApplication.mGson.fromJson(data.toString(), UserWallet.class);
						switch (type) {
							case CHARGE__HF_SUCCESS:
								tv_down.setText("当前账户余额：" + String.format("%.2f", MyApplication.mUserWallet.getBalanceAmount()));
								break;
						}
					}
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				tv_next.setClickable(true);
				ToastUtil.alert(ctx, "网络异常，请稍后重试");
			}
		});
		jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	/**
	 * 用户银行卡
	 */
	private void requestUserBankCard() {
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UrlUserBand, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				tv_next.setClickable(true);
				LogUtil.i("bqt", "绑定的银行卡：" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					JSONObject data = PackageUtil.getResultObject(response);
					if (data != null && MyApplication.mUserDetailInfo != null) {
						MyApplication.mUserDetailInfo.BankCard = MyApplication.mGson.fromJson(data.toString(), UserBankCard.class);
					}
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				tv_next.setClickable(true);
			}
		});
		jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	@Override
	public void onBackPressed() {
		JumpToUser();
	}

	/**
	 * 用户信息
	 */
	private void requestUserDetailInfo() {

		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
				.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				tv_next.setClickable(true);
				LogUtil.i("bqt", "用户信息返回数据：" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					JSONObject data = PackageUtil.getResultObject(response);
					if (data != null) {
						//将获取到的银行卡数据写入静态数据区中
						MyApplication.mUserDetailInfo = MyApplication.mGson.fromJson(data.toString(), UserDetailInfo.class);
					}
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				tv_next.setClickable(true);
			}
		});
		jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}
}
