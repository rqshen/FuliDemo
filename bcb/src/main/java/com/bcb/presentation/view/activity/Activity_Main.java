package com.bcb.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomViewPager;
import com.bcb.presentation.view.fragment.Frag_Main;
import com.bcb.presentation.view.fragment.Frag_Product;
import com.bcb.presentation.view.fragment.Frag_User;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class Activity_Main extends Activity_Base_Fragment {

    //viewpager
    private CustomViewPager content;
    private List<Fragment> mFragments;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

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
    private AlertView alertView = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        content = (CustomViewPager) findViewById(R.id.content);

        //注册广播
        registerBroadcast();
        init();
        UmengUtil.update(Activity_Main.this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        if (intent != null) {
            int jumpTo = getIntent().getIntExtra("jumpTo", 0);
            switch (jumpTo) {
            case 2:
                setFragProduct();
            	break;
            case 3:
                setFragUser();
            	break;
            default:
                setFragMain();
            	break;
            }
        }

    }

    private void init() {
        img_mainpage = (ImageView) findViewById(R.id.img_mainpage);
        img_product = (ImageView) findViewById(R.id.img_product);
        img_user = (ImageView) findViewById(R.id.img_user);
        txt_mainpage = (TextView) findViewById(R.id.txt_mainpage);
        txt_product = (TextView) findViewById(R.id.txt_product);
        txt_user = (TextView) findViewById(R.id.txt_user);
        bottom = (LinearLayout) findViewById(R.id.bottom);

        // 初始化主界面的4个Fragment
        Frag_Main frag_main = new Frag_Main();
        mFragments = new ArrayList<>();
        mFragments.add(frag_main);
        mFragments.add(new Frag_Product());
        mFragments.add(new Frag_User());
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        content.setAdapter(myFragmentPagerAdapter);
        content.setPagingEnabled(false);
        content.setOffscreenPageLimit(3);
        content.addOnPageChangeListener(frag_main);
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
                UmengUtil.eventById(Activity_Main.this, R.string.self_c);
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
        content.setCurrentItem(0, false);
    }

    private void setFragMain() {
        resetstatus(txt_mainpage);
        img_mainpage.setImageResource(R.drawable.main_home_page_select);
        content.setCurrentItem(0, false);
    }

    private void setFragProduct() {
        resetstatus(txt_product);
        img_product.setImageResource(R.drawable.main_product_select);
        content.setCurrentItem(1, false);
    }

    private void setFragUser() {
        resetstatus(txt_user);
        img_user.setImageResource(R.drawable.main_my_select);
        content.setCurrentItem(3, false);
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
        EventBus.getDefault().unregister(this);
    }

    //接收事件
    public void onEventMainThread(BroadcastEvent event) {
        //判断要显示的fragment
        String flag = event.getFlag();
        if (!TextUtils.isEmpty(flag)) {
            switch (flag) {
                case BroadcastEvent.HOME:
                    UmengUtil.eventById(Activity_Main.this, R.string.home);
                    setFragMain();
                    break;
                case BroadcastEvent.PRODUCT:
                    UmengUtil.eventById(Activity_Main.this, R.string.main_product_list);
                    setFragProduct();
                    break;
                case BroadcastEvent.USER:
                    UmengUtil.eventById(Activity_Main.this, R.string.self_c);
                    setFragUser();
                    break;
            }
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        config.fontScale = 1f;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
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


    /**
     * viewpager适配器
     */
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> tabs = null;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> tabs) {
            super(fm);
            this.tabs = tabs;
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment fragment = null;
            if (tabs != null && pos < tabs.size()) {
                fragment = tabs.get(pos);
            }
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            if (tabs != null && tabs.size() > 0)
                return tabs.size();
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

}
