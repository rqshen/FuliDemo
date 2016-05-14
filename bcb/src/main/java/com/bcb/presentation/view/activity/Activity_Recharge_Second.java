package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.MyConstants;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TextUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;

import org.json.JSONObject;

public class Activity_Recharge_Second extends Activity_Base implements View.OnClickListener{

	private static final String TAG = "Activity_Recharge_Second";

	private Receiver receiver;
	
	private TextView user_balauce;
	private TextView bank_card_text;
	private EditText editext_money;
	private Button recharge_button;

	//充值说明
	private LinearLayout recharge_description;
	private LinearLayout description_text;
	private boolean rechargeDescriptionVisible = false;

	private UserWallet mUserWallet;
	private UserDetailInfo mUserDetailInfo;

	//错误提示
    private RelativeLayout error_layout;
    private TextView error_tips;

	//充值提示
	private LinearLayout tip_layout;
	private TextView recharge_tips;

    private ProgressDialog progressDialog;
	//弹框
    private AlertView alertView;

    private BcbRequestQueue requestQueue;

	public static void launche(Context ctx) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Recharge_Second.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Recharge_Second.this);
		setBaseContentView(R.layout.activity_recharge_second);
		setLeftTitleVisible(true);
		setTitleValue("充值");
        requestQueue = BcbNetworkManager.newRequestQueue(this);
        showProgressBar();
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter("com.bcb.money.change.success");
		registerReceiver(receiver, filter);
		//充值按钮
        recharge_button = (Button) findViewById(R.id.recharge_button);
        recharge_button.setOnClickListener(this);

		//充值说明
		description_text = (LinearLayout) findViewById(R.id.description_text);
		recharge_description = (LinearLayout) findViewById(R.id.recharge_description);
		recharge_description.setOnClickListener(this);

        //出错提示
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        error_tips = (TextView) findViewById(R.id.error_tips);

		//充值提示
		tip_layout = (LinearLayout) findViewById(R.id.tip_layout);
		recharge_tips = (TextView) findViewById(R.id.recharge_tips);

		user_balauce = (TextView) findViewById(R.id.user_balauce);
		bank_card_text = (TextView) findViewById(R.id.bank_card_text);
		editext_money = (EditText) findViewById(R.id.editext_money);
		//获取用户余额，如果静态数据区存在数据，则不用从服务器中获取数据
        if (App.mUserWallet != null && App.mUserWallet.getBalanceAmount() > 0) {
            mUserWallet = App.mUserWallet;
            user_balauce.setText("" + mUserWallet.BalanceAmount + "元");
        } else {
            loadUserWalletData();
        }

        //获取账户信息，如果静态数据区存在数据，则不用从服务器中获取数据
        if (App.mUserDetailInfo != null && App.mUserDetailInfo.BankCard != null
                && App.mUserDetailInfo.BankCard.getCardNumber() != null) {
            mUserDetailInfo = App.mUserDetailInfo;
            bank_card_text.setText(TextUtil.delBankNum(mUserDetailInfo.BankCard.CardNumber));
			//设置银行卡充值提示
			setBankCardTip(mUserDetailInfo.BankCard.BankName);
            hideProgressBar();
        } else {
            loadUserBankData();
        }
		
		editext_money.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
	         public void onTextChanged(CharSequence s, int start, int before, int count) {  
					        					
	         }  
	           
	         @Override  
	         public void beforeTextChanged(CharSequence s, int start, int count, int after) {                  
	         
	         }  
	           
			 @Override
			 public void afterTextChanged(Editable s) {
				// 先判断输入框的数字是否正常，不允许输入两个小数点
				String temp = editext_money.getText().toString();
				int inputcount = 0, inputstart = 0;
				while ((inputstart = temp.indexOf(".", inputstart)) >= 0) {
					inputstart += ".".length();
					inputcount++;
				}
				if (temp.indexOf(".") == 0 || inputcount > 1) {
					s.delete(temp.indexOf("."), temp.length());
				}
						 		
			 }  
       });  		
	}


    //请求获取用户余额
	private void loadUserWalletData() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (PackageUtil.getRequestStatus(response, Activity_Recharge_Second.this)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        mUserWallet = App.mGson.fromJson(data.toString(), UserWallet.class);
                    }
                    if (null != mUserWallet) {
                        App.mUserWallet = mUserWallet;
                        showUserWallet();
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_Recharge_Second.this, "网络异常，请稍后重试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
        requestQueue.add(jsonRequest);
	}
	

    //获取用户银行卡信息
    private void loadUserBankData(){
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserBankMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (PackageUtil.getRequestStatus(response, Activity_Recharge_Second.this)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        mUserDetailInfo = App.mGson.fromJson(data.toString(), UserDetailInfo.class);
                    }
                    if (null != mUserDetailInfo) {
                        App.mUserDetailInfo = mUserDetailInfo;
                        showData();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_Recharge_Second.this, "网络异常，请稍后重试");
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
        requestQueue.add(jsonRequest);
    }

    //显示用户余额
	private void showUserWallet(){
		if (mUserWallet.BalanceAmount > 0)
			user_balauce.setText("" + mUserWallet.BalanceAmount + " 元");
		else
			user_balauce.setText("0 元");
	}

    //显示账号信息
	private void showData(){
		bank_card_text.setText(TextUtil.delBankNum(mUserDetailInfo.BankCard.CardNumber));
		//设置银行卡充值提示
		setBankCardTip(mUserDetailInfo.BankCard.BankName);
	}

    //记录充值数据
	private void getRechargeRecord(){
		//没有银行卡信息
		if(null == mUserDetailInfo){
			ToastUtil.alert(Activity_Recharge_Second.this, "无法获取账户银行卡帐号信息，充值失败");
			return;
		}
		//判断是否支持该银行
		if (!mUserDetailInfo.BankCard.isBankCodeSupport()) {
			//如果不支持该银行，则弹框提示
			showAlertView();
			return;
		}

		//没有输入金额
		if(!ToastUtil.checkInputParam(Activity_Recharge_Second.this, editext_money, "请输入充值金额")){
			return;
		}

		//判断输入金额是否大于10000元
		if (Float.parseFloat(editext_money.getText().toString()) > 10000) {
			ToastUtil.alert(Activity_Recharge_Second.this, "充值金额不能大于10000元");
			return;
		}

		//判断输入金额小于等于0的状态
		String money = editext_money.getText().toString();
		if (money.indexOf(".") == money.length()) {
			money = money.replace(".", "");
		}
		if ( Double.parseDouble(money) <= 0) {
			ToastUtil.alert(Activity_Recharge_Second.this, "请输入有效的充值金额");
			return;
		}
		else if (Double.parseDouble(money) <= 2.0) {
            ToastUtil.alert(Activity_Recharge_Second.this, "充值金额必须大于2元");
            return;
        }

		JSONObject data = new JSONObject();
		try {
			data.put("BankCode", mUserDetailInfo.BankCard.BankCode);
			data.put("BankCardNo", mUserDetailInfo.BankCard.CardNumber);
			data.put("IdCardType", MyConstants.IDCARDTYPE);
			data.put("IdCard", mUserDetailInfo.IDCard);
			data.put("HolderName", mUserDetailInfo.RealName);
			data.put("Amount", money);
			Activity_Recharge_Confirm.launche(Activity_Recharge_Second.this, data);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    //弹框提示手机端充值暂不支持某个银行
    private void showAlertView() {
        AlertView.Builder builder = new AlertView.Builder(this);
        builder.setTitle("手机端充值暂不支持" + mUserDetailInfo.BankCard.getBankName());
        builder.setMessage("您可以使用APP进行提现或使用电脑版充值");
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertView.dismiss();
                alertView = null;
                finish();
            }
        });
        alertView = builder.create();
        alertView.show();
	}

    @Override
	public void onClick(View view) {
		switch(view.getId()) {
			//充值下一步按钮
			case R.id.recharge_button:
				getRechargeRecord();
				break;

			//充值说明
			case R.id.recharge_description:
				rechargeDescriptionVisible = !rechargeDescriptionVisible;
				if (rechargeDescriptionVisible) {
					description_text.setVisibility(View.VISIBLE);
				} else {
					description_text.setVisibility(View.GONE);
				}
				break;
		}
	}

	//注册广播，充值完成后进行销毁页面
	class Receiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.bcb.money.change.success")){
				finish();				
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}


	/**
	 * 设置银行卡充值提示
	 * @param bankName 银行名称
     */
	private void setBankCardTip(String bankName){
		String tip;
		switch (bankName){
			case "兴业银行":
				tip = "该卡本次最多可充值300元，每日最多15000元";
				break;
			case "华夏银行":
				tip = "该卡本次最多可充值4999元，每日最多15000元";
				break;
			case "中国银行":
				tip = "该卡本次最多可充值1000元，每日最多15000元";
				break;
			case "中国工商银行":
				tip = "该卡本次最多可充值7999元，每日最多15000元";
				break;
			case "中信银行":
				tip = "该卡本次最多可充值8000元，每日最多15000元";
				break;
			case "光大银行":
				tip = "该卡本次最多可充值8000元，每日最多15000元";
				break;
			case "中国农业银行":
				tip = "该卡本次最多可充值1000元，每日最多15000元";
				break;
			case "平安银行":
				tip = "该卡本次最多可充值8000元，每日最多15000元";
				break;
			default:
				tip = bankName + "渠道维护中，建议您到pc端充值";
				break;
		}
		recharge_tips.setText(tip);
	}


	/********************* 转圈提示 **************************/
	//显示转圈提示
	private void showProgressBar() {
		if(null == progressDialog) progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setMessage("正在加载数据....");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	//隐藏转圈提示
	private void hideProgressBar() {
		if(null != progressDialog && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
}

