package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.RegexManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

//更改密码
public class Activity_Modify_Pwd extends Activity_Base {

	private static final String TAG = "Activity_Modifypwd";
	private Button next;
	
	private boolean isLoginPwd;
	
	private EditText oldpwd, newpwd, confirmpwd;
	
	private TextView error_tips;
	private LinearLayout pwd_strength_layout;
	private TextView strength1, strength2, strength3;

    private BcbRequestQueue requestQueue;
	
	public static void launche(Context ctx, boolean isLoginPwd) {
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_Modify_Pwd.class);
		intent.putExtra("isLoginPwd", isLoginPwd);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Modify_Pwd.this);
		isLoginPwd = getIntent().getBooleanExtra("isLoginPwd", true);
		setBaseContentView(R.layout.activity_modify_pwd);
		setLeftTitleVisible(true);
		if (isLoginPwd) {
			setTitleValue("修改登录密码");
		} else {
			setTitleValue("修改交易密码");
		}
        requestQueue = App.getInstance().getRequestQueue();
		init();
	}

	private void init() {
		oldpwd = (EditText) findViewById(R.id.oldpwd);
		newpwd = (EditText) findViewById(R.id.newpwd);
		confirmpwd = (EditText) findViewById(R.id.confirmpwd); 

		pwd_strength_layout = (LinearLayout) findViewById(R.id.pwd_strength_layout); 
		
		strength1 = (TextView) findViewById(R.id.strength1); 
		strength2 = (TextView) findViewById(R.id.strength2); 
		strength3 = (TextView) findViewById(R.id.strength3);
		
		error_tips = (TextView) findViewById(R.id.error_tips); 
		
		if (isLoginPwd) {
			oldpwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
			newpwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
			confirmpwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
			pwd_strength_layout.setVisibility(View.VISIBLE);
		}else {
			oldpwd.setInputType(InputType.TYPE_CLASS_NUMBER);
			newpwd.setInputType(InputType.TYPE_CLASS_NUMBER);
			confirmpwd.setInputType(InputType.TYPE_CLASS_NUMBER);
			oldpwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
			newpwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
			confirmpwd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
			pwd_strength_layout.setVisibility(View.GONE);
		}

        //原始密码
		oldpwd.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
				if (isLoginPwd) {
					// 判断密码是否规范
					if (!RegexManager.isPasswordNum(oldpwd.getText().toString())) {
						error_tips.setVisibility(View.VISIBLE);
						// 判断是否包含非法字符
						if(!RegexManager.isRightCode(oldpwd.getText().toString())) {
							error_tips.setText("输入的密码包含非法字符");	
						} else {
							error_tips.setText("请输入8-15位原密码");
						}
				
						String editable = oldpwd.getText().toString();  
				        String str = RegexManager.stringFilter(editable.toString());
				        if(!editable.equals(str)){
				           oldpwd.setText(str);
				           // 设置新的光标所在位置  
				           oldpwd.setSelection(str.length());
				        }
					} else {
						error_tips.setVisibility(View.GONE);
						error_tips.setText("");
					}
				} else {
					if (!RegexManager.isResizngCode(oldpwd.getText().toString())) {
						error_tips.setVisibility(View.VISIBLE);		
						error_tips.setText("请输入6位原密码");		
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

        //新密码
		newpwd.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
				if (isLoginPwd) {
					// 判断密码是否规范
					if (!RegexManager.isPasswordNum(newpwd.getText().toString())) {
						error_tips.setVisibility(View.VISIBLE);
						// 判断是否包含非法字符
						if(!RegexManager.isRightCode(newpwd.getText().toString())) {
							error_tips.setText("密码包含非法字符");
						} else {
							error_tips.setText("请输入8-15位新密码");		
						}							
					} else {
						error_tips.setVisibility(View.GONE);
						error_tips.setText("");
					}
					
					String editable = newpwd.getText().toString();  
			        String str = RegexManager.stringFilter(editable.toString());
			        if(!editable.equals(str)){
			           newpwd.setText(str);
			           // 设置新的光标所在位置  
			           newpwd.setSelection(str.length());		                     
			        }
			        
			        if (str.length() < 8) {
                        setupPasswordColor();
					} else {
				        if (RegexManager.isNum(str) || RegexManager.isAZ(str)) {
                            setupPasswordColor();
				        	strength1.setBackgroundResource(R.drawable.button_solid_green);
						}				        		        
				        if (!RegexManager.isNum(str) && !RegexManager.isAZ(str) && str.length() <= 10) {
                            setupPasswordColor();
				        	strength2.setBackgroundResource(R.drawable.button_solid_blue);
						}			        
						if (!RegexManager.isNum(str) && !RegexManager.isAZ(str) && str.length() > 10) {
                            setupPasswordColor();
							strength3.setBackgroundResource(R.drawable.button_solid_red);
						}
					}
				} else{
					// 判断输入的交易密码是否规范
					if (!RegexManager.isResizngCode(newpwd.getText().toString())) {
						error_tips.setVisibility(View.VISIBLE);		
						error_tips.setText("请输入6位新密码");		
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

        //确认密码
		confirmpwd.addTextChangedListener(new TextWatcher() {           
	           
			@Override  
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
				if (isLoginPwd) {
					// 判断密码是否规范
					if (!RegexManager.isPasswordNum(confirmpwd.getText().toString())) {
						error_tips.setVisibility(View.VISIBLE);
						// 判断是否包含非法字符
						if(!RegexManager.isRightCode(confirmpwd.getText().toString())) {
							error_tips.setText("输入的密码包含非法字符");
						} else {
							error_tips.setText("请输入8-15位新密码");
						}
					} else {
						error_tips.setVisibility(View.GONE);
						error_tips.setText("");
					}
					
				    String editable = confirmpwd.getText().toString();  
			        String str = RegexManager.stringFilter(editable.toString());
			        if(!editable.equals(str)){
			           confirmpwd.setText(str);
			           // 设置新的光标所在位置  
			           confirmpwd.setSelection(str.length());
			        }
			   } else {
				   if (!RegexManager.isResizngCode(confirmpwd.getText().toString())) {
						error_tips.setVisibility(View.VISIBLE);		
						error_tips.setText("请输入6位新密码");		
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

		next = (Button) findViewById(R.id.button_confirm);
		next.setOnClickListener(onClickListener);
	}

    //初始化密码强度的颜色
    private void setupPasswordColor() {
        strength3.setBackgroundResource(R.drawable.button_solid_gray);
        strength1.setBackgroundResource(R.drawable.button_solid_gray);
        strength2.setBackgroundResource(R.drawable.button_solid_gray);
    }

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_confirm:
                if (judgePwd()) {
                	// 设置立即修改密码按钮为不可以点击
                	next.setEnabled(false);		
                	toConnect();               	
				}			
				break;

			}
		}
	};

	private boolean judgePwd() {	
		if (isLoginPwd) {
			// 判断密码是否规范
			if (!RegexManager.isPasswordNum(oldpwd.getText().toString())) {
				error_tips.setVisibility(View.VISIBLE);
				// 判断是否包含非法字符
				if(!RegexManager.isRightCode(oldpwd.getText().toString())) {
					error_tips.setText("输入的密码包含非法字符");
				} else {
					error_tips.setText("请输入8-15位原密码");
				}
				oldpwd.requestFocus();
				oldpwd.setSelection(oldpwd.getText().toString().length());
				return false;	
			}
			
			// 判断新密码是否规范
			if (!RegexManager.isPasswordNum(newpwd.getText().toString())) {
				error_tips.setVisibility(View.VISIBLE);
				// 判断是否包含非法字符
				if(!RegexManager.isRightCode(newpwd.getText().toString())) {
					error_tips.setText("输入的密码包含非法字符");
				} else {
					error_tips.setText("请输入8-15位新密码");
				}
				newpwd.requestFocus();
				newpwd.setSelection(newpwd.getText().toString().length());
				return false;
			}
			
			if (!RegexManager.isPasswordNum(confirmpwd.getText().toString())) {
				error_tips.setVisibility(View.VISIBLE);
				// 判断是否包含非法字符
				if(!RegexManager.isRightCode(confirmpwd.getText().toString())) {
					error_tips.setText("输入的密码包含非法字符");	
				} else {
					error_tips.setText("请输入8-15位确认密码");
				}
				confirmpwd.requestFocus();
				confirmpwd.setSelection(confirmpwd.getText().toString().length());
				return false;
			}
		} else {
			if (oldpwd.getText().toString().length() != 6) {
				error_tips.setVisibility(View.VISIBLE);
				error_tips.setText("请输入6位原密码");
				oldpwd.requestFocus();
				oldpwd.setSelection(oldpwd.getText().toString().length());
				return false;
			}
			// 判断密码是否规范
			if (!RegexManager.isResizngCode(newpwd.getText().toString())) {
				error_tips.setVisibility(View.VISIBLE);
				error_tips.setText("请输入6位新密码");
				newpwd.requestFocus();
				newpwd.setSelection(newpwd.getText().toString().length());
				return false;
			}
			if (!RegexManager.isResizngCode(confirmpwd.getText().toString())) {
				error_tips.setVisibility(View.VISIBLE);
				error_tips.setText("请输入6位确认密码");
				confirmpwd.requestFocus();
				confirmpwd.setSelection(confirmpwd.getText().toString().length());
				return false;
			}
		}
			
		if (!newpwd.getText().toString().equals(confirmpwd.getText().toString())) {
			error_tips.setVisibility(View.VISIBLE);
			error_tips.setText("两次密码输入不一致");
			return false;
		}
		return true;
	}
	
	private void toConnect() {
		JSONObject obj = new org.json.JSONObject();
		String url = UrlsOne.ModifyLoginPwd;
        try {
			if (isLoginPwd) {
				obj.put("OriginalPassword", oldpwd.getText().toString());
				obj.put("NewPassword", newpwd.getText().toString());
			} else {
				obj.put("OriginalTradePassword", oldpwd.getText().toString());
				obj.put("NewTradePassword", newpwd.getText().toString());
                url = UrlsOne.ModifyPayPwd;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // 设置立即修改密码按钮为可以点击
                    next.setEnabled(true);
                    String message = response.getString("message");
                    if (PackageUtil.getRequestStatus(response.toString(), Activity_Modify_Pwd.this)) {
                        ToastUtil.alert(Activity_Modify_Pwd.this, "密码修改成功");
                        Activity_ChangePwd_Success.launche(Activity_Modify_Pwd.this, isLoginPwd, false);
						finish();
                    } else {
                        ToastUtil.alert(Activity_Modify_Pwd.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_Modify_Pwd.this, "网络好像有问题，请稍后重试");
                // 设置立即修改密码按钮为可以点击
                next.setEnabled(true);
            }
        });
        jsonRequest.setTag(BcbRequestTag.ModifyPasswordTag);
        requestQueue.add(jsonRequest);
	}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.ModifyPasswordTag);
    }
}
