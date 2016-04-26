package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.common.app.App;
import com.bcb.R;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.data.bean.CardPwdPostData;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.RegexManager;
import com.bcb.data.util.SystemUtil;
import com.bcb.data.util.TextUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

public class Activity_Recharge_Confirm extends Activity_Base implements View.OnClickListener{

	private static final String TAG = "Activity_Recharge_Confirm";
	private Receiver receiver;

	private TextView recharge_amount;
	private TextView bank_card_text;
	private TextView error_tips;
	private EditText regservicecode;
	private Button sendVerification, recharge_button;

	//充值说明
	private LinearLayout recharge_description;
	private LinearLayout description_text;
	private boolean descriptionVisible = false;

	private ProgressDialog mProgressBar;
	//判断是否充值中
	private boolean isReqPayOrder = false;

	private String BankCode, BankCardNo, IdCardType, IdCard, HolderName, Mobile, Amount; 

	//贝付
	private String amount_Server_Back, transNo_Server_Back;
	//融宝订单号
	private String reapalTransNo;
	private int repalAmount = 1;
	//bankChannel
	private int bankChannel = 1;

    private int time;
    private Timer timer;

    //手机号码
    private EditText bank_card_mobile;

	//对话框
	private DialogWidget dialogWidget;
    private AlertView alertView;
	//是否发送验证码
	private boolean verificationStatus = false;

    private BcbRequestQueue requestQueue;

	public static void launche(Context ctx, JSONObject data) {
		Intent intent = new Intent();
		try {
			intent.putExtra("BankCode", data.getString("BankCode"));
			intent.putExtra("BankCardNo", data.getString("BankCardNo"));
			intent.putExtra("IdCardType", data.getString("IdCardType"));
			intent.putExtra("IdCard", data.getString("IdCard"));
			intent.putExtra("HolderName", data.getString("HolderName"));
			intent.putExtra("Amount", data.getString("Amount"));
        } catch (JSONException e) {			
			e.printStackTrace();
		}
		
		intent.setClass(ctx, Activity_Recharge_Confirm.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Recharge_Confirm.this);
		setBaseContentView(R.layout.activity_recharge_confirm);
		setLeftTitleVisible(true);
		setTitleValue("确认充值");
        requestQueue = BcbNetworkManager.newRequestQueue(this);
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter("com.bcb.money.change.success");
		registerReceiver(receiver, filter);

		BankCode = getIntent().getStringExtra("BankCode"); 
		BankCardNo = getIntent().getStringExtra("BankCardNo"); 
		IdCardType = getIntent().getStringExtra("IdCardType"); 
		IdCard = getIntent().getStringExtra("IdCard");  
		HolderName = getIntent().getStringExtra("HolderName");
		Amount = getIntent().getStringExtra("Amount");
		//需要判断BankCard的信息是否为空
		//这里如果为空，表示数据可被Android 系统清空了，这样会导致"on a null object reference"奔溃
		//为了防止这个崩溃出现，这里将BankChannel的值缓存到这里，然后退到后台的时候把他保存起来
		if (App.mUserDetailInfo != null && App.mUserDetailInfo.getBankCard() != null && App.mUserDetailInfo.getBankCard().getBankChannel() > 0) {
			bankChannel = App.mUserDetailInfo.getBankCard().getBankChannel();
		}
		setupView();
	}
	
	private void setupView() {

		//充值金额
		recharge_amount = (TextView) findViewById(R.id.amount);
		recharge_amount.setText(Amount + "元");
		//银行卡号
		bank_card_text = (TextView) findViewById(R.id.bank_card_text);
		bank_card_text.setText(TextUtil.delBankNum(BankCardNo));
        //预留手机号
        bank_card_mobile = (EditText) findViewById(R.id.bank_card_mobile);
        //如果返回的信息存在手机号码，则禁止输入
        if (App.mUserDetailInfo.BankCard.getCardMobile() != null
					&& !App.mUserDetailInfo.BankCard.CardMobile.equalsIgnoreCase("null")
					&& !App.mUserDetailInfo.BankCard.CardMobile.equalsIgnoreCase("")) {
            //强制隐藏光标和键盘
            bank_card_mobile.setCursorVisible(false);
            bank_card_mobile.setFocusable(false);
            bank_card_mobile.setInputType(InputType.TYPE_NULL);
            bank_card_mobile.setHint("");
            bank_card_mobile.setText(App.mUserDetailInfo.BankCard.getCardMobile());
            Mobile = App.mUserDetailInfo.BankCard.getCardMobile();
        } else {
            bank_card_mobile.setCursorVisible(true);
            bank_card_mobile.setFocusable(true);
            bank_card_mobile.setInputType(InputType.TYPE_CLASS_PHONE);
            bank_card_mobile.setText("");
            bank_card_mobile.setHint("请输入银行预留手机号");
        }

		//密码输入框
		regservicecode = (EditText) findViewById(R.id.regservicecode);
		regservicecode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				error_tips.setVisibility(View.GONE);
				error_tips.setText("");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (regservicecode.getText().toString().length() == 6) {
					error_tips.setVisibility(View.GONE);
					error_tips.setText("");
					return;
				}
			}
		});
		//出错提示
		error_tips = (TextView) findViewById(R.id.error_tips);
		//发送验证码
        sendVerification = (Button) findViewById(R.id.sendVerification);
        sendVerification.setOnClickListener(this);
		//立即充值
		recharge_button = (Button) findViewById(R.id.recharge_button);
		recharge_button.setOnClickListener(this);

		//充值说明
		recharge_description = (LinearLayout) findViewById(R.id.recharge_description);
		recharge_description.setOnClickListener(this);
		description_text = (LinearLayout) findViewById(R.id.description_text);
	}

	//发送验证码，根据获取验证码的通道判断是使用贝付验证还是融宝验证
	private void sendVerificationNumbers() {
		showProgressBar("正在获取验证码...");
		switch (bankChannel) {
			//贝付验证
			case 1:
				ebatongVerification();
				break;

			//融宝验证
			case 2:
				reapalVerification();
				break;

			default:
				break;
		}
	}

	//融宝充值发送验证码
	private void reapalVerification() {
		JSONObject obj = new org.json.JSONObject();
		try {
			obj.put("Mobile", Mobile);
			obj.put("Amount", Amount);
		} catch (JSONException e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.ReapalVerification, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                try {

                    boolean status = PackageUtil.getRequestStatus(response, Activity_Recharge_Confirm.this);
                    if (status) {
                        verificationStatus = true;
                        JSONObject result = response.getJSONObject("result");
                        //判断JSON对象是否为空
                        if (result != null) {
                            repalAmount = result.getInt("Amount");
                            reapalTransNo = result.getString("TransNo");
                        }
                        //判断是否回传URL，如果回传URL，则跳转到浏览器
                        if (result.getString("CardPwdPostUrl") != null
                                && !result.getString("CardPwdPostUrl").equalsIgnoreCase("null")
                                && !result.getString("CardPwdPostUrl").equalsIgnoreCase("")) {
                            //将JSON对象转义
                            String tmpCardPostData = result.getString("CardPwdPostData");
                            CardPwdPostData cardData = App.mGson.fromJson(tmpCardPostData, CardPwdPostData.class);

                            //如果数据不为空
                            if (cardData != null && cardData.data != null && cardData.encryptkey != null && cardData.merchant_id != null) {
                                //将参数中的特殊字符转义
                                String data = URLEncoder.encode(cardData.data, "utf-8");
                                String encryptkey = URLEncoder.encode(cardData.encryptkey, "utf-8");
                                String merchant_id = URLEncoder.encode(cardData.merchant_id, "utf-8");
                                //参数拼接
                                String params = "?data=" + data + "&encryptkey=" + encryptkey + "&merchant_id=" + merchant_id;
                                String url = result.getString("CardPwdPostUrl") + params;
                                //弹窗提示去校验
                                showAlertView(url);
                            } else {
                                setTimer();
                            }
                        }
                        //如果没有返回URL，则开启定时器进行倒计时
                        else {
                            setTimer();
                        }
                    } else {
                        //判断出错消息是否存在，包括是否是null， "null"，""
                        if (response.getString("message") != null
                                && !response.getString("message").equalsIgnoreCase("null")
                                && !response.getString("message").equalsIgnoreCase("")) {
                            error_tips.setVisibility(View.VISIBLE);
                            error_tips.setText(response.getString("message"));
                        } else {
                            error_tips.setVisibility(View.VISIBLE);
                            error_tips.setText("获取验证码失败");
                        }
                        handler.sendEmptyMessage(2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_Recharge_Confirm.this, "网络异常，请稍后重试");
                hideProgressBar();
                // 设置获取验证码按钮为可以点击
                sendVerification.setEnabled(true);
                sendVerification.setBackgroundResource(R.drawable.request_code_selector);
            }
        });
        jsonRequest.setTag(BcbRequestTag.ReapalVerificationTag);
        requestQueue.add(jsonRequest);
	}


	//弹框提示
	private void showAlertView(final String url) {
        AlertView.Builder builder = new AlertView.Builder(this);
        builder.setTitle("首次充值需到中国银联进行卡密校验");
        builder.setMessage("校验银行卡会让你的资金更安全");
        builder.setPositiveButton("去校验", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent newIntent = new Intent(Activity_Recharge_Confirm.this, Activity_Recharge_WebView.class);
                newIntent.putExtra("tittle", "银行卡实名认证");
                newIntent.putExtra("url", url);
                startActivityForResult(newIntent, 1);
                alertView.dismiss();
                alertView = null;
            }
        });
        alertView = builder.create();
        //点击返回按钮时开启定时器
        alertView.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                //点击返回按钮时，触发计时
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    setTimer();
                }
                return false;
            }
        });
        alertView.show();
	}

	//贝付充值发送验证码
	private void ebatongVerification() {
		JSONObject obj = new org.json.JSONObject();
		try {
			obj.put("BankCode", BankCode);
			obj.put("BankCardNo", BankCardNo);
			obj.put("IdCardType", IdCardType);
			obj.put("IdCard", IdCard);
			obj.put("HolderName", HolderName);
			obj.put("Mobile", Mobile);
			obj.put("Amount", Amount);
		} catch (JSONException e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Beifu_Recharge_Code, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                try {
                    String message = response.getString("message");
                    boolean flag = PackageUtil.getRequestStatus(response, Activity_Recharge_Confirm.this);
                    if (flag) {
                        verificationStatus = true;
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result.getString("TradeStatus").equals("1")) {
                            amount_Server_Back = response.getJSONObject("result").getString("Amount");
                            transNo_Server_Back = response.getJSONObject("result").getString("TransNo");
                            ToastUtil.alert(Activity_Recharge_Confirm.this, " 验证码已发送至 " + Mobile);
                        } else {
                            error_tips.setVisibility(View.VISIBLE);
                            // error_tips.setText(result.getString("ErrorMessage"));
                            error_tips.setText("操作失败");
                        }
                        setTimer();
                    } else {
                        error_tips.setVisibility(View.VISIBLE);
                        error_tips.setText(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                hideProgressBar();
                ToastUtil.alert(Activity_Recharge_Confirm.this, "网络异常，请稍后再试");
                // 设置获取验证码按钮为可以点击
                sendVerification.setEnabled(true);
                sendVerification.setBackgroundResource(R.drawable.request_code_selector);
            }
        });
        jsonRequest.setTag(BcbRequestTag.BeifuRechargeCodeTag);
        requestQueue.add(jsonRequest);

	}

	private void setTimer() {
		time = 60;
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				time--;
				if(time > 0){
					handler.sendEmptyMessage(1);
				}else{				
					handler.sendEmptyMessage(2);
					stopTimer();
				}
			}
		};
		timer.schedule(task, 0, 1000);
	}
	
	private void stopTimer(){
		if(timer != null){
			timer.cancel();
			timer = null;
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 1:
				sendVerification.setText(time + "S 后可再次发送");
				sendVerification.setBackgroundResource(R.drawable.button_gray);
				break;
				
			case 2:
				// 设置获取验证码按钮为可以点击
				sendVerification.setEnabled(true);
				sendVerification.setBackgroundResource(R.drawable.request_code_selector);
				sendVerification.setText("重新发送");
				break;
			}
		};
	};

	//确认充值
	private void rechargeConfirm() {
		showProgressBar("正在充值，请耐心等待...");
		switch (bankChannel) {
			//贝付充值
			case 1:
				ebatongRecharge();
				break;

			//融宝充值
			case 2:
				reapalRecharge();
				break;

			default:
				break;
		}
	}

	//融宝充值
	private void reapalRecharge() {
		JSONObject data = new JSONObject();
		try {
			if(isReqPayOrder) {
				return;
			}
			isReqPayOrder = true;

			data.put("DynamicCode", regservicecode.getText().toString());
			data.put("TransNo", reapalTransNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.ReapalRecharge, data, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isReqPayOrder = false;
                hideProgressBar();
                try {
                    if (PackageUtil.getRequestStatus(response, Activity_Recharge_Confirm.this)) {
                        //校验验证码成功
                        UmengUtil.eventById(Activity_Recharge_Confirm.this, R.string.captcha_succ);
                        JSONObject result = PackageUtil.getResultObject(response);
                        //处理中
                        if (result.getInt("TradeStatus") == 0) {
                            Activity_ChangeMoney_Success.launche(Activity_Recharge_Confirm.this,
                                    Activity_ChangeMoney_Success.ACTION_Recharge,
                                    0,
                                    "",
                                    reapalTransNo,
                                    Float.parseFloat(Amount));
                            finish();
                        }
                        //充值状态返回成功，不一定是充值成功
                        else if (result.getInt("TradeStatus") == 1) {
                            Activity_ChangeMoney_Success.launche(Activity_Recharge_Confirm.this,
                                    Activity_ChangeMoney_Success.ACTION_Recharge,
                                    0,
                                    "",
                                    reapalTransNo,
                                    Float.parseFloat(Amount));
                            finish();
                        }
                        //充值失败
                        else {
                            //判断返回消息是否为空， 包括 null、"null"、""
                            if (result.getString("ErrorMessage") == null
                                    || result.getString("ErrorMessage").equalsIgnoreCase("null")
                                    || result.getString("ErrorMessage").equalsIgnoreCase("")) {
                                Activity_ChangeMoney_Success.launche(Activity_Recharge_Confirm.this,
                                        Activity_ChangeMoney_Success.ACTION_Recharge,
                                        -1,
                                        "充值失败",
                                        reapalTransNo,
                                        Float.parseFloat(Amount));
                            } else {
                                Activity_ChangeMoney_Success.launche(Activity_Recharge_Confirm.this,
                                        Activity_ChangeMoney_Success.ACTION_Recharge,
                                        -1,
                                        result.getString("ErrorMessage"),
                                        reapalTransNo,
                                        Float.parseFloat(Amount));
                            }
                            finish();
                        }
                    } else {
                        error_tips.setVisibility(View.VISIBLE);
                        error_tips.setText(response.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_Recharge_Confirm.this, "网络好像有问题，请稍后重试");
                isReqPayOrder = false;
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.ReapalRechargeTag);
        requestQueue.add(jsonRequest);
	}


    /**
     * 贝付充值， 暂时没有了
     */
	private void ebatongRecharge() {
		JSONObject data = new JSONObject();
		try {
			if(isReqPayOrder) {
				return;
			}
			isReqPayOrder = true;
			data.put("DynamicCode", regservicecode.getText().toString());
			data.put("TransNo", transNo_Server_Back);
			data.put("InvokeIp", SystemUtil.getLocalHostIp());
		} catch (Exception e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Beifu_Recharge_Confirm, data, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isReqPayOrder = false;
                hideProgressBar();
                try {
                    String message = response.getString("message");
                    boolean flag = PackageUtil.getRequestStatus(response, Activity_Recharge_Confirm.this);

                    if (flag) {
                        //校验验证码成功
                        UmengUtil.eventById(Activity_Recharge_Confirm.this, R.string.captcha_succ);
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result.getString("TradeStatus").equals("1")) {
                            //将充值金额写入全局静态数据区
                            double balanceAmount = App.mUserWallet.getBalanceAmount() + Double.parseDouble(Amount);
                            App.mUserWallet.setBalanceAmount(balanceAmount);
                            //跳转至充值成功页面
                            Activity_ChangeMoney_Success.launche(Activity_Recharge_Confirm.this, Activity_ChangeMoney_Success.ACTION_Recharge);
                            ToastUtil.alert(Activity_Recharge_Confirm.this, "本次充值金额 + " + amount_Server_Back + "元");
                            sendBroadcast(new Intent("com.bcb.changemoney.success"));
                        } else {
                            error_tips.setVisibility(View.VISIBLE);
                            //判断是否返回错误信息，包括 null、"null"、""
                            if (result.getString("ErrorMessage") == null
                                    || result.getString("ErrorMessage").equalsIgnoreCase("null")
                                    || result.getString("ErrorMessage").equalsIgnoreCase("")){
                                error_tips.setText("充值失败");
                            } else {
                                error_tips.setText(result.getString("ErrorMessage"));
                            }
                        }
                    } else {
                        error_tips.setVisibility(View.VISIBLE);
                        error_tips.setText(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_Recharge_Confirm.this, "网络好像有问题，请稍后重试");
                isReqPayOrder = false;
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.BeifuRechargeConfirmTag);
        requestQueue.add(jsonRequest);

	}

	/***************************** 转圈提示 *****************************************/
	//显示进度
	private void showProgressBar(String title) {
		if(null == mProgressBar) {
			mProgressBar = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
		}
		mProgressBar.setMessage(title);
		mProgressBar.setCanceledOnTouchOutside(false);
		mProgressBar.setCancelable(true);
		mProgressBar.show();
	}

	//隐藏进度
	private void hideProgressBar(){
		if(null != mProgressBar && mProgressBar.isShowing()){
			mProgressBar.dismiss();
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.sendVerification:
				//判断输入的手机号码是否合法
				String cardPhone = bank_card_mobile.getText().toString();
				if (!RegexManager.isPhoneNum(cardPhone)) {
					error_tips.setVisibility(View.VISIBLE);
					error_tips.setText("请输入正确的手机号码");
					return;
				} else {
					Mobile = cardPhone;
					error_tips.setVisibility(View.GONE);
				}
				//发送验证码
				UmengUtil.eventById(Activity_Recharge_Confirm.this, R.string.captcha_sent);
				sendVerificationNumbers();
				// 设置获取验证码按钮为不可点击，防止获取多条验证码
				sendVerification.setEnabled(false);
				sendVerification.setBackgroundResource(R.drawable.button_shape_unenabled);
				break;

			case R.id.recharge_button:
				Mobile = bank_card_mobile.getText().toString();
				//判断是否输入验证码
				if (regservicecode.getText().toString().length() != 6) {
					error_tips.setVisibility(View.VISIBLE);
					error_tips.setText("请输入6位验证码");
					return;
				}
				//判断是否从服务器中获取验证码，获取则为true
				if (!verificationStatus) {
					error_tips.setVisibility(View.VISIBLE);
					error_tips.setText("请先获取验证码");
					return;
				}
				error_tips.setVisibility(View.GONE);
				//确认充值
				rechargeConfirm();
				break;

			//充值说明
			case R.id.recharge_description:
				descriptionVisible = !descriptionVisible;
				if (descriptionVisible) {
					description_text.setVisibility(View.VISIBLE);
				} else {
					description_text.setVisibility(View.GONE);
				}
				break;
		}
	}

	class Receiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.bcb.money.change.success")){
				finish();				
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			//如果页面返回，则开始倒计时
			case 1:
				if (data != null) {
					setTimer();
				}
				break;

			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	//暂存充值的订单号的信息
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		//保存融宝充值的订单号，只有在订单号不为空的时候才缓存
		if (reapalTransNo != null
				&& !reapalTransNo.equalsIgnoreCase("null")
				&& !reapalTransNo.equalsIgnoreCase("")) {
			savedInstanceState.putString("reapalTransNo", reapalTransNo);
		}
		//保存贝付充值的订单号，只有在订单号不为空的时候才缓存
		if (transNo_Server_Back != null
				&& !transNo_Server_Back.equalsIgnoreCase("null")
				&& !transNo_Server_Back.equalsIgnoreCase("")) {
			savedInstanceState.putString("transNo_Server_Back", transNo_Server_Back);
		}
		//保存BankChannel
		savedInstanceState.putInt("bankChannel", bankChannel);
		//暂存验证码状态
		savedInstanceState.putBoolean("verificationStatus", verificationStatus);
	}

	//取出暂存的订单号信息
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//获取融宝支付的订单号
		if (!savedInstanceState.getString("reapalTransNo").isEmpty()
				&& !savedInstanceState.getString("reapalTransNo").equalsIgnoreCase("null")
				&& !savedInstanceState.getString("reapalTransNo").equalsIgnoreCase("")) {
			reapalTransNo = savedInstanceState.getString("reapalTransNo");
		}
		//获取贝付支付的订单号
		if (!savedInstanceState.getString("transNo_Server_Back").isEmpty()
				&& !savedInstanceState.getString("transNo_Server_Back").equalsIgnoreCase("null")
				&& !savedInstanceState.getString("transNo_Server_Back").equalsIgnoreCase("")) {
			transNo_Server_Back = savedInstanceState.getString("transNo_Server_Back");
		}
		//获取BankChannel
		bankChannel = savedInstanceState.getInt("bankChannel", 1);
		//获取验证码状态
		verificationStatus = savedInstanceState.getBoolean("verificationStatus", false);
	}
}