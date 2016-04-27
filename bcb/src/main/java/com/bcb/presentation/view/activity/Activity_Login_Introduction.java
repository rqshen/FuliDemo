package com.bcb.presentation.view.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bcb.R;
import com.bcb.presentation.adapter.MyPagerAdapter;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.UmengUtil;

import java.util.ArrayList;


public class Activity_Login_Introduction extends Activity_Base implements View.OnClickListener {
    //返回
    private ImageView back_img;
    //注册
    private Button button_register;
    //登陆
    private Button button_login;

    //图片
    private ViewPager viewPager;
    private ArrayList<View> viewArrayList;
    //三个点
    private ImageView imageView1;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Login_Introduction.this);
        setBaseContentView(R.layout.activity_login_introduction);
        setLeftTitleVisible(true);
        setTitleValue("注册");
        setupView();
        setupViewPager();
    }

    private void setupView() {
        back_img = (ImageView) findViewById(R.id.back_img);
        back_img.setOnClickListener(this);
        button_register = (Button) findViewById(R.id.button_register);
        button_register.setOnClickListener(this);
        button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(this);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        //判断设备是否Android4.4以上，如果是，则表示使用了浸入式状态栏，需要设置状态栏的位置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((LinearLayout)findViewById(R.id.layout_topbar)).setVisibility(View.VISIBLE);
        }
    }

    //初始化ViewPager
    private void setupViewPager() {
        //获取手机屏幕宽度
        WindowManager wm=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        //初始化ViewPager
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewArrayList = new ArrayList<View>();

        for (int i = 0; i < 2; i++) {
            //第一张图片
            ImageView imageView = new ImageView(Activity_Login_Introduction.this);
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.login_introduction_image02);
            } else {
                imageView.setBackgroundResource(R.drawable.login_introduction_image01);
            }
            viewArrayList.add(i, imageView);
        }

        //设置viewpager的适配器和监听器
        viewPager.setAdapter(new MyPagerAdapter(Activity_Login_Introduction.this, viewArrayList));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ImagePagerListener());
    }

    //监听器
    private class ImagePagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                imageView1.setBackgroundResource(R.drawable.circle_gray_introduction);
                imageView2.setBackgroundResource(R.drawable.circle_gray_stroke);
            } else if (position == 1) {
                imageView1.setBackgroundResource(R.drawable.circle_gray_stroke);
                imageView2.setBackgroundResource(R.drawable.circle_gray_introduction);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /******************* 点击事件 ***********************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;

            case R.id.button_register:
                gotoRegisterPage();
                break;

            case R.id.button_login:
                gotoLoginPage();
                break;

            default:
                break;
        }
    }

    /**************** 去注册页面 **********************/
    private void gotoRegisterPage() {
        Activity_Register_First.launche(Activity_Login_Introduction.this);
        finish();
    }

    /******************* 去登陆页面 *******************/
    private void gotoLoginPage() {
        UmengUtil.eventById(Activity_Login_Introduction.this, R.string.bid_buy_n_login);
        Activity_Login.launche(Activity_Login_Introduction.this);
        finish();
    }
}