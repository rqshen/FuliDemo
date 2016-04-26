package com.bcb.presentation.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.presentation.adapter.MyFragmentPagerAdapter;
import com.bcb.R;
import com.bcb.common.net.UrlsOne;
import com.bcb.presentation.view.fragment.Frag_ExpiredCoupon;
import com.bcb.presentation.view.fragment.Frag_UnusedCoupon;
import com.bcb.presentation.view.fragment.Frag_UsedCoupon;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Activity_Coupons extends Activity_Base_Fragment {
	
	private static final String TAG = "Activity_Coupons";
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private ImageView ivBottomLine;
	private TextView unusedTextView, usedTextView, expiredTextView;
	
	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;

	private TextView dialog_error_tips;
	private Dialog convertDialog;

    private BcbRequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Coupons.this);
		setBaseContentView(R.layout.activity_coupons);
		setLeftTitleVisible(true);
		setTitleValue("优惠券");
		setRightTitleValue("兑换", new OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeCoupon();
            }
        });
		requestQueue = BcbNetworkManager.newRequestQueue(this);
        InitWidth();
		InitTextView();
		InitViewPager();
	}

	private void InitTextView() {
		unusedTextView = (TextView) findViewById(R.id.tv_unused);
		usedTextView = (TextView) findViewById(R.id.tv_used);
		expiredTextView = (TextView) findViewById(R.id.tv_expired);
		unusedTextView.setOnClickListener(new MyOnClickListener(0));
		usedTextView.setOnClickListener(new MyOnClickListener(1));
		expiredTextView.setOnClickListener(new MyOnClickListener(2));
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.coupons_viewpager);
		
		fragmentsList = new ArrayList<Fragment>();
	    Fragment fragment01 = new Frag_UnusedCoupon(Activity_Coupons.this);
		Fragment fragment02 = new Frag_UsedCoupon(Activity_Coupons.this);
		Fragment fragment03 = new Frag_ExpiredCoupon(Activity_Coupons.this);
		
		fragmentsList.add(fragment01);
		fragmentsList.add(fragment02);
		fragmentsList.add(fragment03);

		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
		mPager.setCurrentItem(0);
		mPager.setOffscreenPageLimit(2);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.coupons_cursor);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		DisplayMetrics dmDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dmDisplayMetrics);
		int screenW = dmDisplayMetrics.widthPixels;
		offset = (int) ((screenW / 3.0 - bottomLineWidth) / 2);
		position_one = (int) (screenW / 3.0);
		LinearLayout.LayoutParams params = (LayoutParams) ivBottomLine.getLayoutParams();
		params.leftMargin = offset;
		params.rightMargin = 0;
		ivBottomLine.setLayoutParams(params);
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
            //根据当前位置和将要移动到的位置创建动画
			Animation animation = new TranslateAnimation(currIndex * position_one, arg0 * position_one, 0, 0);
            //设置字体颜色
            switch (arg0) {
                case 0:
                    setTextColor();
                    unusedTextView.setTextColor(Color.RED);
                    break;

                case 1:
                    setTextColor();
                    usedTextView.setTextColor(Color.RED);
                    break;

                case 2:
                    setTextColor();
                    expiredTextView.setTextColor(Color.RED);
                    break;
            }
            //将当前位置设置为目标位置
            currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(100);
			ivBottomLine.startAnimation(animation);
		}

        //设置所有的字体颜色为灰色
        private void setTextColor(){
            unusedTextView.setTextColor(Color.GRAY);
            usedTextView.setTextColor(Color.GRAY);
            expiredTextView.setTextColor(Color.GRAY);
        }
	}

    //兑换优惠券对话框
    private void exchangeCoupon(){
        convertDialog = new Dialog(this);
        View view = View.inflate(this, R.layout.dialog_enter_convertcode, null);

        Window win = convertDialog.getWindow();
        convertDialog.setCanceledOnTouchOutside(true);
        convertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        convertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        win.getDecorView().setPadding(60, 0, 60, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        convertDialog.setContentView(view);

        final EditText edit_code = (EditText) view.findViewById(R.id.edit_code);
        edit_code.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        dialog_error_tips = (TextView) view.findViewById(R.id.dialog_error_tips);
        Button bt_go = (Button) view.findViewById(R.id.bt_go);
        Button button_cancel = (Button)view.findViewById(R.id.button_cancel);

        edit_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(edit_code.getText().toString())) {
                    dialog_error_tips.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bt_go.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edit_code.getText().toString())) {
                    exchangeCouponFromNumber(edit_code.getText().toString());
                } else {
                    dialog_error_tips.setVisibility(View.VISIBLE);
                    dialog_error_tips.setText("请输入兑换码");
                }

            }
        });

        button_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                convertDialog.dismiss();
            }
        });

        convertDialog.show();
    }

    //用输入的号码兑换优惠券
    private void exchangeCouponFromNumber(String ExchangeCode) {
    	JSONObject obj = new JSONObject();
		try {
			obj.put("ExchangeCode", ExchangeCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Convert_Coupon, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    if(response.getInt("status") == 1) {
                        Intent intent = new Intent();
                        intent.setAction("com.bcb.update.couponui");
                        sendBroadcast(intent);
                        convertDialog.dismiss();
                    } else {
                        dialog_error_tips.setVisibility(View.VISIBLE);
                        dialog_error_tips.setText(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.ConvertCouponTag);
        requestQueue.add(jsonRequest);
	}

}


