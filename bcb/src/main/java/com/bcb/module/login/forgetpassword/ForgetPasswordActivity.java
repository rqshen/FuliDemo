package com.bcb.module.login.forgetpassword;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.util.MQCustomerManager;
import com.bcb.util.MyActivityManager;
import com.bcb.util.PackageUtil;
import com.bcb.util.RegexManager;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.util.UmengUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

//忘记密码
public class ForgetPasswordActivity extends Activity_Base {

	private static final String TAG = "ForgetPasswordActivity";
	private TextView send, error_tips;
	private Button next;
	private ImageView  im_visible;
	private EditText id_card, phone, regservicecode, newpwd;
	
	private int time;
    private Timer timer;
    
    private boolean isLogin;

	private LinearLayout pwd_strength_layout;
//	private ScrollView scrollview;
	private TextView strength1, strength2, strength3;
	private Handler mHandler = new Handler();
	private ProgressDialog progressDialog;
	//是否从服务器中获取验证码
	private boolean verificationStatus = false;

    private BcbRequestQueue requestQueue;

	public static void launche(Context ctx) {
		launche(ctx, false);
	}

	public static void launche(Context ctx, boolean isLogin) {
		Intent intent = new Intent();
		intent.setClass(ctx, ForgetPasswordActivity.class);
		intent.putExtra("isLogin", isLogin);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(ForgetPasswordActivity.this);
		isLogin = getIntent().getBooleanExtra("isLogin", true);
		setBaseContentView(R.layout.activity_forget_pwd);
		setLeftTitleVisible(true);
		setTitleValue("找回密码");
//		if (isLogin){
//			setTitleValue("找回登录密码");
//		} else {
//			setTitleValue("找回交易密码");
//		}
		setRightTitleValue("联系客服", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MQCustomerManager.getInstance(ForgetPasswordActivity.this).showCustomer(null);
			}
		});
        requestQueue = MyApplication.getInstance().getRequestQueue();
		init();
	}

	private void init() {
//		scrollview = (ScrollView) findViewById(R.id.scrollview);
		
		id_card = (EditText) findViewById(R.id.id_card);		
		phone = (EditText) findViewById(R.id.phone);
		if (isLogin) {
			phone.setVisibility(View.VISIBLE);
		} else {
			//隐藏手机号码
			findViewById(R.id.phonenumber_text).setVisibility(View.GONE);
			phone.setVisibility(View.GONE);
		}
		regservicecode = (EditText) findViewById(R.id.regservicecode);
		send = (TextView) findViewById(R.id.send);
		newpwd = (EditText) findViewById(R.id.newpwd);
		error_tips = (TextView) findViewById(R.id.error_tips);
		im_visible = (ImageView) findViewById(R.id.im_visible);

		pwd_strength_layout = (LinearLayout) findViewById(R.id.pwd_strength_layout);
		strength1 = (TextView) findViewById(R.id.strength1); 
		strength2 = (TextView) findViewById(R.id.strength2); 
		strength3 = (TextView) findViewById(R.id.strength3); 	
				
		id_card.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			
//		if (isLogin) {
//			newpwd.setHint("请输入8-15位新密码");
//			newpwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//			newpwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
//			pwd_strength_layout.setVisibility(View.VISIBLE);
//		} else {
//			newpwd.setHint("请输入6位新密码");
//			newpwd.setInputType(InputType.TYPE_CLASS_NUMBER);
//			newpwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
//			pwd_strength_layout.setVisibility(View.GONE);
//		}
		
		newpwd.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {  
		        
				mHandler.postDelayed(new Runnable() {  
		              
		            @Override  
		            public void run() {  
		            	// 将ScrollView滚动到底  
//					    scrollview.fullScroll(View.FOCUS_DOWN);
					    newpwd.requestFocus(); 
		            }  
		        }, 200); 				
			    
		        return false;
			}
		});
		
		id_card.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
				if(!RegexManager.isSecondGenerationIDCardNum(id_card.getText().toString())) {
					error_tips.setVisibility(View.VISIBLE);
					error_tips.setText("请输入正确的身份证号码");
				} else {
					error_tips.setVisibility(View.GONE);
					error_tips.setText("");
				}	        					
            }  
              
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {                  
            
            }  
              
			@Override
			public void afterTextChanged(Editable s) {
			
			}  
        }); 
		
		phone.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
				if (!RegexManager.isPhoneNum(phone.getText().toString())) {
					error_tips.setVisibility(View.VISIBLE);
					error_tips.setText("请输入正确的手机号码");
//					Toast.makeText(ForgetPasswordActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
				} else {
					error_tips.setVisibility(View.GONE);
					error_tips.setText("");
				}	        					
            }  
              
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {                  
            
            }  
              
			@Override
			public void afterTextChanged(Editable s) {
			
			}  
        }); 
		
		regservicecode.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
				if (!RegexManager.isResizngCode(regservicecode.getText().toString())) {
					error_tips.setVisibility(View.VISIBLE);
					if(!RegexManager.isNum(regservicecode.getText().toString()))
						error_tips.setText("验证码必须由数字组成");
					error_tips.setText("请输入6位验证码");
//					Toast.makeText(ForgetPasswordActivity.this, "请输入6位验证码", Toast.LENGTH_SHORT).show();
				} else {
					error_tips.setVisibility(View.GONE);
					error_tips.setText("");
				}		        					
            }  
              
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {                  
            
            }  
              
			@Override
			public void afterTextChanged(Editable s) {
			
			}  
        }); 
					
		newpwd.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
				
				if (isLogin) {
					//是否为密码
					if (!RegexManager.isPasswordNum(newpwd.getText().toString())) {
						error_tips.setVisibility(View.VISIBLE);
						//是否为合法的字符串
						if(!RegexManager.isRightCode(newpwd.getText().toString())) {
							error_tips.setText("密码必须由数字、字母、字符组成");
							Toast.makeText(ForgetPasswordActivity.this, "密码必须由数字、字母、字符组成", Toast.LENGTH_SHORT).show();
						} else {
							error_tips.setText("请输入8-15位新密码");
//							Toast.makeText(ForgetPasswordActivity.this, "请输入8-15位新密码", Toast.LENGTH_SHORT).show();
						}							
					} else {
						error_tips.setVisibility(View.GONE);
						error_tips.setText("");
					}
					//不能输入汉字
				    String editable = newpwd.getText().toString();  
			        String str = RegexManager.stringFilter(editable.toString());
			        if(!editable.equals(str)){
			           newpwd.setText(str);
			           // 设置新的光标所在位置  
			           newpwd.setSelection(str.length());		                     
			        }
			        //判断一开始输入为汉字，会被替换成空字符串，这时候密码强度不应该显示为弱
			        if(str.length() == 0){
			        	setupBackgroung();
			        }
			    	//长度小于8的时候，密码为弱
			        else if (str.length() < 8) {
			        	setupBackgroung();
			        	strength1.setBackgroundResource(R.drawable.button_solid_red);
					} else {

						/**
						 * 一、关于密码强度的设定：
						 密码包含的元素有四种：数字、符号、大写字母、小写字母
						 1、密码强度显示为弱：密码中只出现一种元素
						 2、密码强度显示为中：密码中只出现两种元素
						 3、密码强度显示为强：密码中出现三种或三种以上的元素
						 * 日期：2016/8/3 18:34
						 */
						int num = RegexManager.getMatchNumber(str);
						if (num <= 1) {
							setupBackgroung();
							strength1.setBackgroundResource(R.drawable.button_solid_red);
						}
						if (num == 2) {
							setupBackgroung();
							strength2.setBackgroundResource(R.drawable.button_solid_blue);
						}
						if (num >= 3) {
							setupBackgroung();
							strength3.setBackgroundResource(R.drawable.button_solid_green);
						}
					}
				} else {
					if (!RegexManager.isResizngCode(newpwd.getText().toString())) {
						error_tips.setVisibility(View.VISIBLE);		
						error_tips.setText("请输入6位数字密码");
						Toast.makeText(ForgetPasswordActivity.this,"请输入6位数字密码", Toast.LENGTH_SHORT).show();
					} else {
						error_tips.setVisibility(View.GONE);
						error_tips.setText("");
					}
				}	       		        					
            }  
              
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {                  
            
            }  
              
			@Override
			public void afterTextChanged(Editable s) {
			
			}  
        }); 
			
		findViewById(R.id.send).setOnClickListener(onClickListener);
		
		next = (Button) findViewById(R.id.button_confirm);
		next.setOnClickListener(onClickListener);
		im_visible.setOnClickListener(onClickListener);

	}
	//设置密码强度显示为灰色
	private void setupBackgroung(){
		strength1.setBackgroundResource(R.drawable.button_solid_gray);
    	strength2.setBackgroundResource(R.drawable.button_solid_gray);
    	strength3.setBackgroundResource(R.drawable.button_solid_gray);
	}
	private boolean isShow = true;//默认是隐藏
	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.im_visible:
					if (isShow) {
						newpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						im_visible.setImageResource(R.drawable.r_invisible_3x);
					} else {
						newpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
						im_visible.setImageResource(R.drawable.r_visible_3x);
					}
					isShow = !isShow;
					break;
			case R.id.back_img:
				finish();
				break;
				
			case R.id.send:
				if (judgeInput()) {
					// 设置获取验证码按钮为不可点击，防止获取多条验证码
					send.setEnabled(false);	
//					send.setBackgroundResource(R.drawable.button_shape_unenabled);
					toSend();
				}
				break;
				
			case R.id.button_confirm:
                if (verificationStatus&&judgeNext()) {
                	// 设置立即修改按钮为不可点击，防止请求多次
					next.setEnabled(false);		
                	toNext();	
				} else if (!verificationStatus) {
					error_tips.setVisibility(View.VISIBLE);
					error_tips.setText("请先获取验证码");
	                Toast.makeText(ForgetPasswordActivity.this, "请先获取验证码", Toast.LENGTH_SHORT).show();
				}
				break;

			}
		}
	};
	
	private boolean judgeInput() {
//		String idCardStr = id_card.getText().toString();
		String phoneStr = phone.getText().toString();
//		char[] str = idCardStr.toCharArray();

		// 获取输入身份证、手机号的状态
//		boolean idEmptyState = (idCardStr.length()== 0);
//		boolean idEndState =  (idCardStr.length()==18 && str[17] == 'x');
//		boolean idErrorState = (!RegexManager.isSecondGenerationIDCardNum(idCardStr));
		boolean phoneEmptyState = isLogin && (phoneStr.length()  == 0);
		boolean phoneErrorState = isLogin && (!RegexManager.isPhoneNum(phoneStr));

		// 判断身份证、手机号码是否正确
//		if(idEmptyState ||  idEndState || idErrorState || phoneEmptyState || phoneErrorState) {
		if(phoneEmptyState || phoneErrorState) {
			String toastStr = "请输入正确的手机号码和密码";
//			if (idEmptyState)
//				toastStr = "请先输入身份证号码";
//			else if(idEndState)
//				toastStr = "请大写身份证号码的X";
//			else if(idErrorState)
//				toastStr = "请输入正确的身份证号码";
			 if(phoneEmptyState && isLogin)
				toastStr = "请输入手机号码";
			else if(phoneErrorState && isLogin)
				toastStr = "请输入正确的手机号码";
//			else {
//				if (isLogin) {
//					toastStr = "请输入正确的身份证、手机号";
//				} else {
//					toastStr = "请输入正确的身份证号码";
//				}
//			}
			//提示
			ToastUtil.alert(ForgetPasswordActivity.this, toastStr);
//			id_card.requestFocus();
//			id_card.setSelection(id_card.getText().toString().length());
			return false;
		}
		return true;
	}

	private boolean judgeNext() {
//		String idCardStr = id_card.getText().toString();
		String errorText = "";
		// 获取身份证
//		boolean idErrorState = (!RegexManager.isSecondGenerationIDCardNum(idCardStr));
		//判断手机号码的状态，只有登录的时候才需要
		boolean phoneErrorState = isLogin && (!RegexManager.isPhoneNum(phone.getText().toString()));
		//判断验证码的状态
		boolean regErrorState = (!RegexManager.isResizngCode(regservicecode.getText().toString()));
		
		if(phoneErrorState || regErrorState) {
//			if(idErrorState){
//				errorText = "请输入身份证号码";
//				id_card.requestFocus();
//				id_card.setSelection(id_card.getText().toString().length());
//			}

			 if (phoneErrorState) {
				errorText = "请输入正确的手机号";
				phone.requestFocus();
				phone.setSelection(phone.getText().toString().length());
			} else if (regErrorState) {
				errorText = "请输入6位验证码";
				regservicecode.requestFocus();
				regservicecode.setSelection(regservicecode.getText().toString().length());
			}
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText(errorText);
			Toast.makeText(ForgetPasswordActivity.this, errorText, Toast.LENGTH_SHORT).show();
			return false;
		}	
		if (isLogin) {
			if (!RegexManager.isPasswordNum(newpwd.getText().toString())) {
				error_tips.setVisibility(View.VISIBLE);
				// 如果不是数字、字母或者下划线
				if(!RegexManager.isRightCode(newpwd.getText().toString())) {
					error_tips.setText("密码必须由数字、字母、字符组成");
					Toast.makeText(ForgetPasswordActivity.this, "密码必须由数字、字母、字符组成", Toast.LENGTH_SHORT).show();
				}else {
					error_tips.setText("请输入8-15位新密码");
					Toast.makeText(ForgetPasswordActivity.this, "请输入8-15位新密码", Toast.LENGTH_SHORT).show();
				}return false;
			} 
		} else {
			// 如果密码不是有6位数字组成
			if (!RegexManager.isResizngCode(newpwd.getText().toString())) {
				error_tips.setVisibility(View.VISIBLE);		
				error_tips.setText("请输入6位纯数字密码");
				Toast.makeText(ForgetPasswordActivity.this, "请输入6位纯数字密码", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		error_tips.setVisibility(View.GONE);
		return true;
	}

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 1:
				send.setText(time + "s");
//				send.setBackgroundResource(R.drawable.button_gray);
				break;
				
			case 2:
				// 设置获取验证码按钮为可以点击
				send.setEnabled(true);
				
//				send.setBackgroundResource(R.drawable.request_code_selector);
				send.setText("重新发送");
				break;
			}
		};
	};
	
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

	//获取验证码
	private void toSend() {
		JSONObject obj = new org.json.JSONObject();
		try {
			if (isLogin) {
				//取出输入的手机号码
				obj.put("Mobile", phone.getText().toString());
				obj.put("CodeType", 3);
			} else {
				//只能取出缓存在本地的手机号码
				obj.put("Mobile", MyApplication.saveUserInfo.getLocalPhone());
				obj.put("CodeType", 4);
			}		
			obj.put("Platform", 2);
			showProgressBar("正在获取验证码...");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserGetRegiCode, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    if(response.getInt("status") == 1) {
                        UmengUtil.eventById(ForgetPasswordActivity.this, R.string.captcha_succ);
                        verificationStatus = true;
                        error_tips.setVisibility(View.GONE);
                        //找回登录密码
                        if (isLogin) {
                            ToastUtil.alert(ForgetPasswordActivity.this, "验证码已发送至 " + phone.getText().toString());
                        }
                        //找回支付密码，手机号码必须从本地取出
                        else {
                            ToastUtil.alert(ForgetPasswordActivity.this, "验证码已发送至 " + MyApplication.saveUserInfo.getLocalPhone());
                        }
                        setTimer();
                    } else {
                        ToastUtil.alert(ForgetPasswordActivity.this, message);
	                    send.setEnabled(true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(ForgetPasswordActivity.this, "网络好像有问题，请稍后重试");
                // 设置获取验证码按钮为可以点击
                send.setEnabled(true);
//                send.setBackgroundResource(R.drawable.request_code_selector);
                hideProgressBar();
            }
        });
		jsonRequest.setTag(BcbRequestTag.FogetPasswordTag);
		requestQueue.add(jsonRequest);
	}

	//完成找回密码
	private void toNext() {
		JSONObject obj = new org.json.JSONObject();
        String url = UrlsOne.ForgetLoginPwd;
		try {
			//找回登录密码
			if (isLogin) {
				obj.put("Mobile", phone.getText().toString());
				obj.put("IDCard", id_card.getText().toString());
				obj.put("NewPassword", newpwd.getText().toString());
				obj.put("VCode", regservicecode.getText().toString());
				obj.put("Platform", 2);
				showProgressBar("正在重置密码...");
			} else {
				obj.put("IDCard", id_card.getText().toString());
				obj.put("NewTradePassword", newpwd.getText().toString());
				obj.put("VCode", regservicecode.getText().toString());
				obj.put("Platform", 2);
				showProgressBar("正在重置密码...");
                url = UrlsOne.ForgetPayPwd;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // 设置立即修改按钮为可以点击
                    next.setEnabled(true);
                    String message = response.getString("message");
                    if (PackageUtil.getRequestStatus(response, ForgetPasswordActivity.this)) {
                        //找回交易密码事件统计
                        if (!isLogin) {
                            UmengUtil.eventById(ForgetPasswordActivity.this, R.string.reset_key2);
                        }
                        ToastUtil.alert(ForgetPasswordActivity.this, "成功找回密码");
                        finish();
                    } else {
                        error_tips.setVisibility(View.VISIBLE);
                        error_tips.setText(message);
	                    Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onErrorResponse(Exception error) {
                // 设置立即修改按钮为可以点击
                next.setEnabled(true);
                ToastUtil.alert(ForgetPasswordActivity.this, "网络好像有问题，请稍后重试");
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.FogetPasswordTag);
        requestQueue.add(jsonRequest);
	}

	//显示进度
	private void showProgressBar(String title) {
		if(null == progressDialog) {
			progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
		}
		progressDialog.setMessage(title);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	//隐藏进度
	private void hideProgressBar() {
		if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		requestQueue.cancelAll(BcbRequestTag.FogetPasswordTag);
	}
}
