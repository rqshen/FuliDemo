package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.AreaBean;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.data.util.BankLogo;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.PasswordEditText;
import com.bcb.data.util.TextUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity_Withdraw extends Activity_Base implements View.OnClickListener{

	private static final String TAG = "Activity_Withdraw";

	private TextView username_balance;
	private TextView bank_name_text;
	private TextView bank_card_text;
	private ImageView bank_icon;
	private LinearLayout forgetPayPassWord;
	private EditText editext_money;

	private Button withdraw_button;
	private PasswordEditText userpwd;

	//提现说明
	private LinearLayout coupon_used_status;
	private ImageView coupon_select_image;
	private boolean couponStatus = false;
	private LinearLayout withdraw_description;
	private LinearLayout description_text;
	private boolean descriptionVisible = false;
    //如果获取提现券?
    private LinearLayout coupon_description;
    private DialogWidget dialogWidget;

	private EditText sub_branch_area, sub_branch_name;	
	private String pcode, pname;
	private String ccode;
	private String cname;
	private String value_branch_name;

    private String withDrawMoney = "";

	private Receiver mReceiver;
	
	private UserDetailInfo mUserBankInfo;
	private UserWallet mUserWallet;
	
	private LinearLayout bank_area_info_layout;
	
	private TextView withdraw_rule, error_tips;

    private ProgressDialog progressDialog;

	//提现券Id
	private String CouponId;
    private int CouponCount = 0;

	private TextView couponcount_text;

    private BcbRequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Withdraw.this);
		setBaseContentView(R.layout.activity_withdraw);
		setLeftTitleVisible(true);
		setTitleValue("提现");
        requestQueue = App.getInstance().getRequestQueue();
		init();
	}
	
	private void init() {
		bank_name_text = (TextView) findViewById(R.id.bank_name_text);
		bank_card_text = (TextView) findViewById(R.id.bank_card_text);
		bank_icon = (ImageView) findViewById(R.id.bank_icon);
        username_balance = (TextView) findViewById(R.id.username_balance);
		userpwd = (PasswordEditText) findViewById(R.id.userpwd);
		userpwd.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		bank_area_info_layout = (LinearLayout) findViewById(R.id.bank_area_info_layout);
		sub_branch_area = (EditText) findViewById(R.id.sub_branch_area);
		sub_branch_name = (EditText) findViewById(R.id.sub_branch_name);

		// 隐藏银行开户地区信息输入框
		bank_area_info_layout.setVisibility(View.GONE);
		
		withdraw_button = (Button) findViewById(R.id.withdraw_button);	
		editext_money = (EditText) findViewById(R.id.editext_money);
		
		forgetPayPassWord  = (LinearLayout) findViewById(R.id.forgetPayPassWord);
		withdraw_rule = (TextView) findViewById(R.id.withdraw_rule);
		withdraw_rule.setText("提现需收取手续费2元");
		error_tips = (TextView) findViewById(R.id.error_tips);
		
		sub_branch_area.setOnClickListener(this);
		withdraw_button.setOnClickListener(this);
		forgetPayPassWord.setOnClickListener(this);


		//提现说明
		coupon_used_status = (LinearLayout) findViewById(R.id.coupon_used_status);
		coupon_select_image = (ImageView) findViewById(R.id.coupon_select_image);
		coupon_used_status.setOnClickListener(this);
		withdraw_description = (LinearLayout) findViewById(R.id.withdraw_description);
		withdraw_description.setOnClickListener(this);
		description_text = (LinearLayout) findViewById(R.id.description_text);
        coupon_description = (LinearLayout) findViewById(R.id.coupon_description);
        coupon_description.setOnClickListener(this);
        //设置账户余额，有账户余额就不用加载数据了
        if (App.mUserWallet.getBalanceAmount() > 0) {
            username_balance.setText("" + App.mUserWallet.BalanceAmount + " 元");
            mUserWallet = App.mUserWallet;
        } else {
            loadBanlanceData();
        }

        //设置银行卡账号，有账号信息则不用从服务器中加载数据，直接在本地静态数据区中取得
		if (App.mUserDetailInfo.BankCard != null) {
            bank_card_text.setText(TextUtil.delBankNum(App.mUserDetailInfo.BankCard.getCardNumber()));
			//设置银行卡logo
			BankLogo bankLogo = new BankLogo();
			bank_icon.setBackgroundResource(bankLogo.getDrawableBankLogo(App.mUserDetailInfo.BankCard.getBankCode()));
            mUserBankInfo = App.mUserDetailInfo;
            hideProgressBar();
        } else {
            loadBankInfo();
        }

		IntentFilter intentFilter = new IntentFilter("com.bcb.bank.area.complete"); 	
		mReceiver = new Receiver();
		registerReceiver(mReceiver, intentFilter);

        //输入交易密码
        userpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                error_tips.setVisibility(View.GONE);
            }
        });

        //输入金额
		editext_money.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				//隐藏出错提示
				error_tips.setVisibility(View.GONE);

				// 先判断输入框的数字是否正常，允许输入两个小数点
				String temp = editext_money.getText().toString();
				int inputcount = 0, inputstart = 0;
				while ((inputstart = temp.indexOf(".", inputstart)) >= 0) {
					inputstart += ".".length();
					inputcount++;
				}
				//删除一开始就输入的小数点
                if (temp.indexOf(".") == 0 || inputcount > 1) {
					s.delete(temp.indexOf("."), temp.length());
				}

                //只保留小数点后面两位小数
                if (temp.indexOf(".") > 0 && temp.length() - temp.indexOf(".") > 2) {
                    s.delete(temp.indexOf(".") + 3, temp.length());
                }

                //无法提现100万以上的金额
                if (temp.indexOf(".") <= 0 && temp.length() > 5) {
                    s.delete(5, temp.length());
                }
                //显示提示
                setupCouponTips();
			}
		});

		couponcount_text = (TextView) findViewById(R.id.couponcount_text);

		//获取提现券信息
		getCouponInfo();

	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.withdraw_button:
				onWithdraw();
				break;

			case R.id.sub_branch_area:
				Intent intent = new Intent();
				intent.setClass(Activity_Withdraw.this, Activity_Province.class);
				startActivity(intent);
				break;

			//忘记密码
			case R.id.forgetPayPassWord:
				UmengUtil.eventById(Activity_Withdraw.this, R.string.recharge_f_secrt);
				Activity_Forget_Pwd.launche(Activity_Withdraw.this);
				break;

			//提现说明
			case R.id.withdraw_description:
				descriptionVisible = ! descriptionVisible;
				if (descriptionVisible) {
					description_text.setVisibility(View.VISIBLE);
				} else {
					description_text.setVisibility(View.GONE);
				}
				break;

			//如何获取提现券
			case R.id.coupon_description:
				showGetCouponDialog();
				break;

			//提现券选中
			case R.id.coupon_used_status:
				if (CouponCount > 0) {
					couponStatus = !couponStatus;
					setupCouponTips();
				}
				break;
		}
	}

	class Receiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.bcb.bank.area.complete")){
				pcode = intent.getStringExtra(Activity_City.PARAM_pcode);
				pname = intent.getStringExtra(Activity_City.PARAM_pname);				
				AreaBean cityObject = (AreaBean) intent.getSerializableExtra(Activity_City.PARAM_cityObject);
				ccode = cityObject.Code;
				cname = cityObject.Name;
				if (null != cityObject) {
					showBankArea();
				}	
			}
						
		}
	}

	//请求提现券信息
	private void getCouponInfo() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.WithdrawCouponInfo, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        //显示提现券信息
                        CouponId = response.getJSONObject("result").getString("CouponId");
                        CouponCount = response.getJSONObject("result").getInt("CouponCount");
                        showCouponInfo(response.getJSONObject("result").getInt("CouponCount"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.WithdrawCouponInfoTag);
        requestQueue.add(jsonRequest);
	}

    //显示提现券信息
	private void showCouponInfo(int couponCount) {
		couponcount_text.setText("(可用 "+ couponCount +" 张)");
        if (couponCount > 0) {
            couponStatus = true;
        }
        setupCouponTips();
	}


	private void showBankArea(){
		sub_branch_area.setText(pname + cname);
		if(null != value_branch_name){
			sub_branch_name.setText(value_branch_name);
		}	
	}

    /**
     * 加载用户银行卡信息
     */
	private void loadBankInfo() {
		showProgressBar();
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserBankMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (PackageUtil.getRequestStatus(response, Activity_Withdraw.this)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        mUserBankInfo = App.mGson.fromJson(data.toString(), UserDetailInfo.class);
                    }
                    if (null != mUserBankInfo) {
                        App.mUserDetailInfo = mUserBankInfo;
						//设置银行卡logo
						BankLogo bankLogo = new BankLogo();
						bank_icon.setBackgroundResource(bankLogo.getDrawableBankLogo(App.mUserDetailInfo.BankCard.getBankCode()));
                        showBankInfo();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onErrorResponse(Exception error) {
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
        requestQueue.add(jsonRequest);
	}


    /**
     * 获取余额信息
     */
	private void loadBanlanceData(){
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (PackageUtil.getRequestStatus(response, Activity_Withdraw.this)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        mUserWallet = App.mGson.fromJson(data.toString(), UserWallet.class);
                    }
                    if (null != mUserWallet) {
                        App.mUserWallet = mUserWallet;
                        showBanlanceData();
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
        requestQueue.add(jsonRequest);
	}

    //显示银行卡信息
	private void showBankInfo(){
		bank_card_text.setText(TextUtil.delBankNum(mUserBankInfo.BankCard.CardNumber));
		if(null != mUserBankInfo.BankCard.CityCode && null != mUserBankInfo.BankCard.CityName && 
				null != mUserBankInfo.BankCard.ProvinceCode &&
				null != mUserBankInfo.BankCard.ProvinceName &&
				null != mUserBankInfo.BankCard.BranchBankName) {
			pcode = mUserBankInfo.BankCard.ProvinceCode;
			pname = mUserBankInfo.BankCard.ProvinceName;
			ccode = mUserBankInfo.BankCard.CityCode;
			cname = mUserBankInfo.BankCard.CityName;
			value_branch_name = mUserBankInfo.BankCard.BranchBankName;
			showBankArea();
		}
	}

    //显示余额信息
	private void showBanlanceData() {
		if(mUserWallet.BalanceAmount > 0)
			username_balance.setText("" + mUserWallet.BalanceAmount + " 元");
		else
			username_balance.setText("0 元");
	}

    //提现
	private void onWithdraw(){
		// 没有输入提现金额
		if (!ToastUtil.checkInputParam(Activity_Withdraw.this, editext_money, "请输入提现金额")) {
			return;
		}

		// 提现金额不能大于账户余额
		String money = editext_money.getText().toString();
		double doubleMoney = Double.parseDouble(money);
		if (mUserWallet.BalanceAmount < doubleMoney) {
			ToastUtil.alert(Activity_Withdraw.this, "提现金额不能大于账户余额");
			return;
		}

		//判断金额是否大于50000元
		if (doubleMoney > 50000) {
			ToastUtil.alert(Activity_Withdraw.this, "单笔提现金额不能超过50000元");
			return;
		}
		// 是否输入了交易密码
		if (!ToastUtil.checkInputParam(Activity_Withdraw.this, userpwd, "请输入交易密码")) {
			return;
		}
		// 输入交易密码位数不对
		if (userpwd.getText().toString().length() != 6) {
			ToastUtil.alert(Activity_Withdraw.this, "请输入6位交易密码");
			return;
		}
		//判断余额是否大于手续费
		if (doubleMoney <= 2) {
			ToastUtil.alert(Activity_Withdraw.this, "提现金额必须大于手续费");
			return;
		}

		String psw = userpwd.getText().toString();
		String sub_branch_nameStr = sub_branch_name.getText().toString();
		JSONObject data = new JSONObject();
        //将取现的现金取出来，存放到局部变量
        withDrawMoney = money;
		try {
			data.put("ProvinceCode", pcode);
			data.put("ProvinceName", pname);
			data.put("CityCode", ccode);
			data.put("CityName", cname);
			data.put("BranchBankName", sub_branch_nameStr);
			data.put("Amount", money);
			data.put("TradePassword", psw);
			//判断是否存在CouponId
			if (CouponId != null && !CouponId.isEmpty() && couponStatus) {
				data.put("CouponId", CouponId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UrlWithdrawals, data, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    if (PackageUtil.getRequestStatus(response, Activity_Withdraw.this)) {
                        onWithdrawSuccess();
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
                ToastUtil.alert(Activity_Withdraw.this, "网络好像有问题，请稍后重试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.UrlWithdrawalTag);
        requestQueue.add(jsonRequest);
	}

	private void onWithdrawSuccess(){
        //取现成功之后，将余额写入静态数据区，方便后面充值成功获取余额
        App.mUserWallet.setBalanceAmount(App.mUserWallet.getBalanceAmount() - Float.parseFloat(withDrawMoney));

		Activity_ChangeMoney_Success.launche(Activity_Withdraw.this, Activity_ChangeMoney_Success.ACTION_Withdrawals);
		finish();
	}

	/**
	 * 显示手续费
	 */
    private void setupCouponTips() {
        if (couponStatus) {
            coupon_select_image.setBackgroundResource(R.drawable.withdraw_hook);
            if (editext_money.getText().toString().isEmpty()) {
                withdraw_rule.setText("提现需收取手续费2元");
            } else {
                if (Float.parseFloat(editext_money.getText().toString()) < 2.00) {
                    withdraw_rule.setText("提现金额必须大于2元");
                } else {
                    withdraw_rule.setText("本次提现将收取手续费0元，实际到账" + editext_money.getText().toString() + "元");
                }
            }
        } else {
            coupon_select_image.setBackgroundResource(R.drawable.withdraw_rect);
            if (editext_money.getText().toString().isEmpty() || Float.parseFloat(editext_money.getText().toString()) < 2) {
                withdraw_rule.setText("提现需收取手续费2元");
            } else {
                String withdrawMoney = String.format("%.2f", Double.parseDouble(editext_money.getText().toString()) - 2);
                withdraw_rule.setText("本次提现将收取手续费2元，实际到账" + withdrawMoney + "元");
            }
        }
    }

	/**
	 * 显示冻结金额对话框
	 */
    private void showGetCouponDialog() {
        dialogWidget = new DialogWidget(Activity_Withdraw.this, getCouponView(), true);
        dialogWidget.show();
    }

    protected View getCouponView() {
        return MyMaskFullScreenView.getInstance(Activity_Withdraw.this, "您每月可获赠1张提现抵扣券，您还可以通过参加平台活动获得提现券", new MyMaskFullScreenView.OnClikListener() {
            @Override
            public void onViewClik() {
                dialogWidget.dismiss();
                dialogWidget = null;
            }
        }).getView();
    }



	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	//缓存提现券
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		//缓存提现券ID
		if (CouponId != null && !CouponId.isEmpty()) {
			savedInstanceState.putString("CouponId", CouponId);
		}
	}

	//取出暂存的订单号信息
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//获取提现券ID
		if (!savedInstanceState.getString("CouponId").isEmpty()
				&& !savedInstanceState.getString("CouponId").equalsIgnoreCase("")
				&& !savedInstanceState.getString("CouponId").equalsIgnoreCase("null")) {
			CouponId = savedInstanceState.getString("CouponId");
		}
	}

	/********************* 转圈提示 **************************/
	//显示转圈提示
	private void showProgressBar() {
		if(null == progressDialog) progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
		progressDialog.setMessage("正在加载数据...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	//隐藏转圈提示
	private void hideProgressBar() {
		if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
}
