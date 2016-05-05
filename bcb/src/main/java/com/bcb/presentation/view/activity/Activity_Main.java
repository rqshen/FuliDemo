package com.bcb.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.fragment.Frag_Main;
import com.bcb.presentation.view.fragment.Frag_Product;
import com.bcb.presentation.view.fragment.Frag_User;

import java.lang.reflect.Field;

public class Activity_Main extends Activity_Base_Fragment {

    //fragment管理器
	private FragmentManager fragmentManager;
	private Fragment fragCurrent;
    //3个Fragment页面
	private Frag_Main fragMain;
	private Frag_Product fragProduct;
	private Frag_User fragUser;
    //3个按钮
	private ImageView img_mainpage;
	private ImageView img_product;
	private ImageView img_user;

	private TextView txt_mainpage;
	private TextView txt_product;
	private TextView txt_user;

	private Receiver mReceiver;

    //底部
    private LinearLayout bottom;
    AlertView alertView = null;

	private int screenHeight;
	private int screenWidth;
	private int floatButtonBitmapWdith;
	private int floatButtonBitmapHeight;
	private int bottomHeight;
	private float startX;
	private float startY;
	private int lastX;
	private int lastY;
	private Button button_floating;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fragmentManager = getSupportFragmentManager();
        //注册广播
        registerBroadcast();
		init();
		UmengUtil.update(Activity_Main.this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

	}

	private void init() {
		img_mainpage = (ImageView) findViewById(R.id.img_mainpage);	
		img_product = (ImageView) findViewById(R.id.img_product);
		img_user = (ImageView) findViewById(R.id.img_user);
		txt_mainpage = (TextView) findViewById(R.id.txt_mainpage);
		txt_product = (TextView) findViewById(R.id.txt_product);
		txt_user = (TextView) findViewById(R.id.txt_user);
        bottom = (LinearLayout) findViewById(R.id.bottom);
		initFloatingButton();
		addFirstFragment();
	}

	private void initFloatingButton() {
		//获取屏幕宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		bottomHeight = getBottomHeight();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		//获取图标的宽高
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.floating_button);
		floatButtonBitmapWdith = bitmap.getWidth();
		floatButtonBitmapHeight = bitmap.getHeight();
		bitmap.recycle();
		//浮标按钮
        button_floating = (Button)findViewById(R.id.button_floating);
        if (App.saveUserInfo.getAccess_Token() != null) {
            button_floating.setVisibility(View.GONE);
        }
        button_floating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    //按下时获取位置
                    case MotionEvent.ACTION_DOWN:
                        //需要判断是否属于该控件
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        startX = lastX;
                        startY = lastY;
                        break;

                    //移动手势
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if (left < 0) {
                            left = 0;
                            right = left + floatButtonBitmapWdith;
                        }
                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - floatButtonBitmapWdith;
                        }
						//状态条的高度
                        if (top < getStatusBarHeight()) {
                            top = getStatusBarHeight();
                            bottom = top + floatButtonBitmapHeight;
                        }
                        if (event.getRawY() > screenHeight - bottomHeight ) {
                            bottom = screenHeight - bottomHeight;
                            top = bottom - floatButtonBitmapHeight;
                        }
                        v.layout(left, top, right, bottom);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    //离开屏幕
                    case MotionEvent.ACTION_UP:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        //当移动距离比较小时，视为点击事件
                        //不用setOnClickListener 是因为setOnClickListener跟setOnTouchListener有冲突，并且只监听到ACTION_DOWN
                        if (Math.abs(lastX - startX) < 5 || Math.abs(lastY - startY) < 5) {
                            Intent intent = new Intent(Activity_Main.this, Activity_Login_Introduction.class);
                            startActivity(intent);
                        }
						//判断是否位置是否超出底部状态栏
                        if (event.getRawY() > screenHeight - bottomHeight - v.getHeight()) {
							bottom = screenHeight - bottomHeight;
							left = v.getLeft();
							right = left + floatButtonBitmapWdith;
							top = bottom - floatButtonBitmapHeight;
							v.layout(left,top,right,bottom);
                        }
						//动画
                        movingAnimation(v, (int) event.getRawX());
                        break;
                }
                return true;
            }
        });

	}

	//按钮移动动画
	private void movingAnimation(final View v, int currentX) {
		final int left;
		final int top = v.getTop();
		TranslateAnimation animation = null;
		if (currentX > screenWidth/2) {
			animation = new TranslateAnimation(0, screenWidth - v.getRight(), 0, 0);
			left = screenWidth - v.getWidth();
		} else {
			animation = new TranslateAnimation(0, - v.getLeft(), 0, 0);
			left = 0;
		}
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				FrameLayout.LayoutParams ll = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				ll.setMargins(left, top, 0, 0);
				v.setLayoutParams(ll);
				v.clearAnimation();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		animation.setDuration(100 * 2 * currentX / screenWidth);
		v.startAnimation(animation);
	}

	//获取状态条的高度
	private int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 38;//默认为38，貌似大部分是这样的

		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.layout_main:
            UmengUtil.eventById(Activity_Main.this, R.string.home);
			setFragMain();
			break;

		case R.id.layout_product:
			UmengUtil.eventById(Activity_Main.this, R.string.main_product_list);
			setFragProduct();
			break;
			
		case R.id.layout_user:
			UmengUtil.eventById(Activity_Main.this,R.string.self_c);
			setFragUser();
			break;

		default:
			break;
		}
	}

	protected void onNewIntent(android.content.Intent intent) {
		if (intent == null)
			return;
		if (null != img_user) {
			img_mainpage.performClick();
		}
	}
	
	private void addFirstFragment() {
		resetstatus(txt_mainpage);
		img_mainpage.setImageResource(R.drawable.main_home_page_select);
		//判断是否为空或者是否已经添加
		if (fragMain == null || !fragMain.isAdded()) {
			fragMain = new Frag_Main(Activity_Main.this);
		}
		fragCurrent = fragMain;
		fragmentManager.beginTransaction().add(R.id.content, fragMain).commitAllowingStateLoss();
		//是否显示悬浮按钮
		if (App.saveUserInfo == null || TextUtils.isEmpty(App.saveUserInfo.getAccess_Token())) {
			button_floating.setVisibility(View.VISIBLE);
		} else {
			button_floating.setVisibility(View.GONE);
		}
	}

	private void setFragMain() {
		resetstatus(txt_mainpage);
		img_mainpage.setImageResource(R.drawable.main_home_page_select);
		//判断是否为空或者是否还没有添加
		if (fragMain == null || !fragMain.isAdded()) {
			fragMain = new Frag_Main(Activity_Main.this);
		}
		switchFragment(fragCurrent, fragMain);
	}

	private void setFragProduct() {
		resetstatus(txt_product);
		img_product.setImageResource(R.drawable.main_product_select);
		//判断是否为空或者是否已经添加
		if (fragProduct == null || !fragProduct.isAdded()) {
			fragProduct = new Frag_Product(Activity_Main.this, App.saveUserInfo.getCurrentCompanyId());
		}
		switchFragment(fragCurrent, fragProduct);
	}

	private void setFragUser() {
        resetstatus(txt_user);
        img_user.setImageResource(R.drawable.main_my_select);
        //判断是否为空或者是否已经添加
        if (fragUser == null || !fragUser.isAdded()) {
            fragUser = new Frag_User(Activity_Main.this);
        }
        switchFragment(fragCurrent, fragUser);
	}

	private void switchFragment(Fragment from, Fragment to) {
		android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先判断是否被add过
		if (!to.isAdded()) {
			// 隐藏当前的fragment，add下一个到Activity中
			transaction.hide(from).add(R.id.content, to).commitAllowingStateLoss();
		} else {
			// 隐藏当前的fragment，显示下一个
			transaction.hide(from).show(to).commitAllowingStateLoss();
		}
		fragCurrent = to;

		//当前页面是首页并且还没登录
		if (fragCurrent instanceof Frag_Main && (App.saveUserInfo == null || TextUtils.isEmpty(App.saveUserInfo.getAccess_Token()))) {
			button_floating.setVisibility(View.VISIBLE);
		} else {
			button_floating.setVisibility(View.GONE);
		}
	}

	private void resetstatus(TextView select) {
		img_mainpage.setImageResource(R.drawable.main_home_page_default);
		img_product.setImageResource(R.drawable.main_product_default);
		img_user.setImageResource(R.drawable.main_my_default);
		txt_mainpage.setTextColor(getResources().getColor(R.color.txt_gray));
		txt_product.setTextColor(getResources().getColor(R.color.txt_gray));
		txt_user.setTextColor(getResources().getColor(R.color.txt_gray));
		select.setTextColor(getResources().getColor(R.color.red));
	}

	class Receiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.bcb.update.mainui")) {
				setFragMain();
			} else if (intent.getAction().equals("com.bcb.product.regular")) {
                setFragProduct();
            }
		}
	}

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

	@Override
	protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
	}

	@Override
	public void onBackPressed() {
        showExitAlertView();
	}

    //主要是为了获取底部的高度，浮标按钮要用到
    public int getBottomHeight() {
        return bottom.getLayoutParams().height;
    }

    private void registerBroadcast() {
        mReceiver = new Receiver();
        registerReceiver(mReceiver, new IntentFilter("com.bcb.update.mainui"));
        registerReceiver(mReceiver, new IntentFilter("com.bcb.product.regular"));
    }

    //提示是否退出APP
    private void showExitAlertView() {
        AlertView.Builder ibuilder = new AlertView.Builder(this);
        ibuilder.setTitle("提示");
        ibuilder.setMessage("确定要退出福利金融吗?");
        ibuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertView.dismiss();
                alertView = null;
                finish();
            }
        });
        ibuilder.setNegativeButton("取消", null);
        alertView = ibuilder.create();
        alertView.show();
    }
}
