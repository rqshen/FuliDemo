package com.bcb.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bcb.common.app.App;
import com.bcb.R;
import com.bcb.data.bean.BankItem;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.MyConstants;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.UmengUtil;

import org.json.JSONObject;

public class Activity_Recharge_First extends Activity_Base implements View.OnClickListener{

	private static final String TAG = "Activity_Recharge_First";

	private Button recharge_button;
	
	private Receiver receiver;
	
	private LinearLayout select_bank;
	
	private EditText username, idcard, bankcardcode, phone, money;
	private TextView txt_choose_bank;
	private RelativeLayout layout_choosed_bank;
	private BankItem mBankItem;

	//充值说明
	private LinearLayout recharge_description;
	private LinearLayout description_text;
	private boolean descriptionVisible = false;


	public static void launche (Context ctx) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Recharge_First.class);
		ctx.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Recharge_First.this);
		setBaseContentView(R.layout.activity_recharge_first);
		setLeftTitleVisible(true);
		setTitleValue("账户认证");
		init();
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter("com.bcb.changemoney.success");
		registerReceiver(receiver, filter);
		
		initBankInfo();
		UmengUtil.eventById(this, R.string.auth_entry);
	}
	
	private void initBankInfo(){
		txt_choose_bank = (TextView) findViewById(R.id.txt_choose_bank);
		layout_choosed_bank = (RelativeLayout) findViewById(R.id.layout_choosed_bank);
		layout_choosed_bank.setVisibility(View.GONE);
	}

	private void init() {
		select_bank = (LinearLayout) findViewById(R.id.select_bank);
		select_bank.setOnClickListener(this);
		
		recharge_button = (Button) findViewById(R.id.recharge_button);
		recharge_button.setOnClickListener(this);

		//充值说明
		recharge_description = (LinearLayout) findViewById(R.id.recharge_description);
		recharge_description.setOnClickListener(this);
		description_text = (LinearLayout) findViewById(R.id.description_text);

		username = (EditText) findViewById(R.id.username);
		idcard = (EditText) findViewById(R.id.idcard);
		bankcardcode = (EditText) findViewById(R.id.bankcardcode);
		phone = (EditText) findViewById(R.id.phone);
		money = (EditText) findViewById(R.id.money);
		
		money.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
	         public void onTextChanged(CharSequence s, int start, int before, int count) {  
					        					
	         }  
	           
	         @Override  
	         public void beforeTextChanged(CharSequence s, int start, int count, int after) {                  
	         
	         }  
	           
			 @Override
			 public void afterTextChanged(Editable s) {
				// 先判断输入框的数字是否正常，不允许输入两个小数点
				String temp = money.getText().toString();
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
	

	private void getRechargeRecord() {
		if(false == ToastUtil.checkInputParam(Activity_Recharge_First.this,username, "请输入您的姓名")){
			return;
		}
		if(false == ToastUtil.checkInputParam(Activity_Recharge_First.this,idcard, "请输入您的身份证号")){
			return;
		}
		if(null == mBankItem){
			ToastUtil.alert(Activity_Recharge_First.this, "请选择银行");
			return;
		}
		if(false == ToastUtil.checkInputParam(Activity_Recharge_First.this,bankcardcode, "请输入您的银行卡号")){
			return;
		}
		if(false == ToastUtil.checkInputParam(Activity_Recharge_First.this,phone, "请输入银行卡预留手机号码")){
			return;
		}
		if(false == ToastUtil.checkInputParam(Activity_Recharge_First.this,money,"请输入充值金额")){
			return;
		}
		String usernameStr = username.getText().toString(); 
		String idcardStr = idcard.getText().toString(); 
		String bankcardcodeStr = bankcardcode.getText().toString(); 
		String phoneStr = phone.getText().toString(); 
		
		String moneyStr = money.getText().toString(); 
		if (moneyStr.indexOf(".") == moneyStr.length()) {
			moneyStr = moneyStr.replace(".", "");
		}
		//充值金额小于等于0时，提示输入有效充值金额
		if( Double.parseDouble(moneyStr) <= 0) {
			ToastUtil.alert(Activity_Recharge_First.this, "请输入有效的充值金额");
			return;
		} else if (Double.parseDouble(moneyStr) <= 2.0) {
            ToastUtil.alert(Activity_Recharge_First.this, "充值金额必须大于2元");
            return;
        }
		
		JSONObject data = new JSONObject();
		
		try {			
			data.put("BankCode", mBankItem.getBankCode());
			data.put("BankCardNo", bankcardcodeStr);
			data.put("IdCardType", MyConstants.IDCARDTYPE);
			data.put("IdCard", idcardStr);
			data.put("HolderName", usernameStr);
			data.put("Mobile", phoneStr);
			data.put("Amount", moneyStr);
			Activity_Recharge_Confirm.launche(Activity_Recharge_First.this, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (data != null) {
				String bankCode = data.getStringExtra("bankCode");
				String bankLogo = data.getStringExtra("bankLogo");
				String bankName = data.getStringExtra("bankName");
				float maxSingle = data.getFloatExtra("maxSingle", 0);
				float maxDay = data.getFloatExtra("maxDay", 0);
				mBankItem = new BankItem(bankCode, bankLogo, bankName, maxSingle, maxDay);
				onShowBankInfo();
			}
		}
		
	}
	
	private void onShowBankInfo(){
		txt_choose_bank.setVisibility(View.GONE);
		layout_choosed_bank.setVisibility(View.VISIBLE);
		
		ImageView bank_icon = (ImageView) findViewById(R.id.bank_icon); 
		TextView bank_name = (TextView) findViewById(R.id.bank_name); 
		TextView bank_rule = (TextView) findViewById(R.id.bank_rule);
        loadImageByVolley(bank_icon, mBankItem.getLogo());
		bank_name.setText(mBankItem.getBankName());
		bank_rule.setText("每笔限额" + mBankItem.getMaxSingle() + "万，每日限" + mBankItem.getMaxDay() + "万");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.recharge_button:
				getRechargeRecord();
				break;

			case R.id.select_bank:
				Intent intent = new Intent(Activity_Recharge_First.this, Activity_Select_Bank.class);
				startActivityForResult(intent, 1);
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

    /**
     * 利用Volley异步加载图片
     *
     * 注意方法参数:
     * getImageListener(ImageView view, int defaultImageResId, int errorImageResId)
     * 第一个参数:显示图片的ImageView
     * 第二个参数:默认显示的图片资源
     * 第三个参数:加载错误时显示的图片资源
     */
    private void loadImageByVolley(ImageView mImageView, String imageUrl) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };
        ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
        //默认图片是没有的
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mImageView, R.drawable.edittext_none_background, R.drawable.edittext_none_background);
        imageLoader.get(imageUrl, listener);
    }


    class Receiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.bcb.changemoney.success")){
				finish();				
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}


}
