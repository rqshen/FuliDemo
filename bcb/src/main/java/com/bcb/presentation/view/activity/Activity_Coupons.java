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
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.MyFragmentPagerAdapter;
import com.bcb.presentation.view.fragment.Frag_UnusedCoupon;
import com.bcb.presentation.view.fragment.Frag_UsedCoupon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 优惠券
 */
public class Activity_Coupons extends Activity_Base_Fragment {
	
	private static final String TAG = "Activity_Coupons";
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private ImageView ivBottomLine;
	private TextView yhqTV, tqbjTV;
	
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
		requestQueue = App.getInstance().getRequestQueue();
		InitWidth();
		InitTextView();
		InitViewPager();
	}

	private void InitTextView() {
		yhqTV = (TextView) findViewById(R.id.yhq);
		tqbjTV = (TextView) findViewById(R.id.tqbj);
		yhqTV.setOnClickListener(new MyOnClickListener(0));
		tqbjTV.setOnClickListener(new MyOnClickListener(1));
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.coupons_viewpager);
		
		fragmentsList = new ArrayList<Fragment>();
		Fragment fragment01 = new Frag_UnusedCoupon(Activity_Coupons.this);
		Fragment fragment02 = new Frag_UsedCoupon(Activity_Coupons.this);

		fragmentsList.add(fragment01);
		fragmentsList.add(fragment02);

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
		offset = (int) ((screenW / 2.0 - bottomLineWidth) / 2);
		position_one = (int) (screenW / 2.0);
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
					yhqTV.setTextColor(Color.RED);
					break;

				case 1:
					setTextColor();
					tqbjTV.setTextColor(Color.RED);
					break;
			}
			//将当前位置设置为目标位置
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(100);
			ivBottomLine.startAnimation(animation);
		}

		//设置所有的字体颜色为灰色
		private void setTextColor() {
			yhqTV.setTextColor(Color.GRAY);
			tqbjTV.setTextColor(Color.GRAY);
		}
	}

	//兑换优惠券对话框
	private void exchangeCoupon() {
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
		Button button_cancel = (Button) view.findViewById(R.id.button_cancel);

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
				//当输入第五个字符时才在之前插入一个空格，而不能在输入第四个字符时在之后插入一个空格
				//因为这样会导致最后的一个空格无法被删掉（因为删掉后又立即满足插入的条件了）
				//计算时要先去除添加的空格
				//插入空格时要去除空格后重新插入，而不是仅仅在最后插入一个空格，以防止用户在中间插入字符后空格变乱
				String code = edit_code.getText().toString().trim().replace(" ", "");
				LogUtil.i("bqt", "【更改】+code");
				StringBuilder sb = new StringBuilder(code);
				int length = code.length();
				for (int i = 0, j = 0; i < length; i += 4, j++) {
					sb.insert(i + j, " ");
					LogUtil.i("bqt", "【遍历】"+sb.toString().trim());
				}
				if (!edit_code.getText().toString().trim().equals(sb.toString().trim())) {
					edit_code.setText(sb.toString().trim());
					//要改变光标位置
					edit_code.setSelection(sb.toString().trim().length());
				}
			}
		});

		bt_go.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!TextUtils.isEmpty(edit_code.getText().toString().trim())) {
					exchangeCouponFromNumber(edit_code.getText().toString().replace(" ", ""));
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
					if (response.getInt("status") == 1) {
						Intent intent = new Intent();
						intent.setAction("com.bcb.update.couponui");
						sendBroadcast(intent);
						convertDialog.dismiss();
						Toast.makeText(Activity_Coupons.this, "兑换成功", Toast.LENGTH_SHORT).show();
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

