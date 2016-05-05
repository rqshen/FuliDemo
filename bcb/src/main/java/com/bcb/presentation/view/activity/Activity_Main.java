package com.bcb.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.fragment.Frag_Main;
import com.bcb.presentation.view.fragment.Frag_Product;
import com.bcb.presentation.view.fragment.Frag_User;

public class Activity_Main extends Activity_Base_Fragment {

    //fragment管理器
	private FragmentManager fragmentManager;
	private Fragment fragCurrent;
    //3个Fragment页面
	private Frag_Main fragMain;
	private Frag_Product fragProduct;
	private Frag_User fragUser;
    //4个按钮
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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fragmentManager = getSupportFragmentManager();
        //注册广播
        registerBroadcast();
		init();
		UmengUtil.update(Activity_Main.this);
	}
	
	private void init() {
		img_mainpage = (ImageView) findViewById(R.id.img_mainpage);	
		img_product = (ImageView) findViewById(R.id.img_product);
		img_user = (ImageView) findViewById(R.id.img_user);
		txt_mainpage = (TextView) findViewById(R.id.txt_mainpage);
		txt_product = (TextView) findViewById(R.id.txt_product);
		txt_user = (TextView) findViewById(R.id.txt_user);
        bottom = (LinearLayout) findViewById(R.id.bottom);
		addFirstFragment();
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
