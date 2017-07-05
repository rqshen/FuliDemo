package com.bcb.module.home;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.BaseActivity1;
import com.bcb.data.bean.transaction.VersionBean;
import com.bcb.event.BroadcastEvent;
import com.bcb.module.discover.DiscoverFragment;
import com.bcb.module.homepager.HomePagerFragment;
import com.bcb.module.myinfo.MyInfoFragment;
import com.bcb.module.myinfo.setting.gesturelock.GestureLockActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.AlertView.DLDialog;
import com.bcb.presentation.view.custom.AlertView.UpdateDialog;
import com.bcb.presentation.view.custom.CustomViewPager;
import com.bcb.util.DownloadUtils;
import com.bcb.util.IpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;
import com.bcb.util.UmengUtil;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity1 {

    //viewpager
    public CustomViewPager content;
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
        MyApplication.instance._mainActivityActivity = this;
        setContentView(R.layout.activity_main);

        requestVersion();
        requestLocation();
        content = (CustomViewPager) findViewById(R.id.content);
        //注册广播
        registerBroadcast();
        init();
        UmengUtil.update(MainActivity.this);
        EventBus.getDefault()
                .register(this);

        if (!MyApplication.saveUserInfo.getGesturePassword()
                .isEmpty() && MyApplication.saveUserInfo.getAccess_Token() != null) {
            GestureLockActivity.launche(MainActivity.this, false, true);
        }
    }

    //记录最后登录的位置
    private void requestLocation() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.LAST_LOGIN, null, TokenUtil.getEncodeToken(this), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }

            @Override
            public void onErrorResponse(Exception error) {
            }
        });
        MyApplication.getInstance()
                .getRequestQueue()
                .add(jsonRequest);
        LogUtil.i("bqt", "【ip】" + IpUtils.getIpAddressString());
    }

    //********************************************************************强制升级*******************************************************************
    VersionBean versionBean;
    DownloadCompleteReceiver downloadCompleteReceiver;
    String fileName;
    File apkFile;

    private void requestVersion() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.VERSION, null, null, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "新版本强制升级：" + response.toString());
                JSONObject data = PackageUtil.getResultObject(response);
                if (data != null) {
                    versionBean = MyApplication.mGson.fromJson(data.toString(), VersionBean.class);
                    //判断版本号！
                    if (versionBean != null && versionBean.Increment > getVersionCode(MainActivity.this)) {
                        MyApplication.versionBean = versionBean;
                        if (versionBean.Force) {//强制升级
                            fileName = "fljr-v" + versionBean.Increment + ".apk";
                            apkFile = new File(Environment.getExternalStorageDirectory()
                                    .getPath() + DownloadUtils.FILE_PATH + File.separator + fileName);
                            showVersionDialog2();
                        } else {//非强制升级
                            MyApplication.isNeedUpdate = true;
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.i("bqt", "新版本强制升级：" + error.toString());
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
        MyApplication.getInstance()
                .getRequestQueue()
                .add(jsonRequest);
    }

    UpdateDialog updateDialog;

    private void showVersionDialog2() {
        updateDialog = new UpdateDialog(this) {
            @Override
            public void onClick() {
                super.onClick();
                boolean hasDoloaded = getSharedPreferences("version", 0).getBoolean(fileName, false);
                if (hasDoloaded) {//已下载完毕
                    if (apkFile == null || !apkFile.exists()) {
                        SharedPreferences.Editor editor = getSharedPreferences("version", 0).edit();
                        editor.clear();
                        editor.commit();
                        registerReceiver();//被删了，重新下载
                    } else installApk(MainActivity.this);//否则，安装
                } else if (getIsFinishedWhenDownloading()) {//上次在下载过程中退出了，下次进入应用时重新下载
                    registerReceiver();//重新下载
                } else if (apkFile != null && apkFile.exists()) {//正在下载
                    Toast.makeText(MainActivity.this, "正在下载，请稍后", Toast.LENGTH_SHORT)
                            .show();
                } else {//没下载过
                    registerReceiver();
                }
            }
        };
        updateDialog.setValues(View.INVISIBLE, false, getTips());
        updateDialog.show();
    }

    private void showVersionDialog() {
        AlertView.Builder ibuilder = new AlertView.Builder(this);
        ibuilder.setTitle("版本更新啦！");
        ibuilder.setMessage(getTips());
        ibuilder.setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertView.dismiss();
                alertView = null;
                finish();
            }
        });
        ibuilder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean hasDoloaded = getSharedPreferences("version", 0).getBoolean(fileName, false);
                        if (hasDoloaded) {//已下载完毕
                            if (apkFile == null || !apkFile.exists()) {
                                SharedPreferences.Editor editor = getSharedPreferences("version", 0).edit();
                                editor.clear();
                                editor.commit();
                                registerReceiver();//被删了，重新下载
                            } else installApk(MainActivity.this);//否则，安装
                        } else if (getIsFinishedWhenDownloading()) {//上次在下载过程中退出了，下次进入应用时重新下载
                            registerReceiver();//重新下载
                        } else if (apkFile != null && apkFile.exists()) {//正在下载
                            Toast.makeText(MainActivity.this, "正在下载，请稍后", Toast.LENGTH_SHORT)
                                    .show();
                        } else {//没下载过
                            registerReceiver();
                        }
                    }
                }

        );
        ibuilder.setGravity(Gravity.LEFT);
        alertView = ibuilder.create();
        alertView.setCanceledOnTouchOutside(false);
        alertView.setCancelable(false);
        alertView.show();
    }

    private void registerReceiver() {
        Toast.makeText(MainActivity.this, "正在下载新版本安装包", Toast.LENGTH_SHORT)
                .show();
        downloadCompleteReceiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);//下载完成的动作
        registerReceiver(downloadCompleteReceiver, intentFilter);
        DownloadUtils.downLoadFile(MainActivity.this, versionBean.Url, fileName);//开始下载
    }

    /**
     * 记录是否在下载过程中退出了，如果在下载过程中退出了，下次进入应用时重新下载
     */
    private void saveIsFinishedWhenDownloading() {
        SharedPreferences.Editor editor = getSharedPreferences("version", 0).edit();
        editor.putBoolean("finish", true);
        editor.commit();
    }

    /**
     * 是否在下载过程中退出了
     */
    private boolean getIsFinishedWhenDownloading() {
        return getSharedPreferences("version", 0).getBoolean("finish", false);
    }

    public String getTips() {
        StringBuilder sb = new StringBuilder("");
        List<String> tips = MyApplication.versionBean.Tips;
        if (tips != null && tips.size() == 1) {
            updateDialog.setTVGravity(Gravity.CENTER);
            return tips.get(0);
        } else if (tips != null && tips.size() > 1) {
            updateDialog.setTVGravity(Gravity.LEFT);
            for (int i = 0; i < tips.size(); i++) {
                sb.append("" + (i + 1) + "、" + tips.get(i) + "\n");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        } else return null;
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                LogUtil.i("bqt", "下载完毕" + apkFile.getPath());
                SharedPreferences.Editor editor = getSharedPreferences("version", 0).edit();
                editor.putBoolean(fileName, true);
                editor.commit();
                installApk(context);
                if (downloadCompleteReceiver != null) {
                    unregisterReceiver(downloadCompleteReceiver);
                    downloadCompleteReceiver = null;
                }
            }
        }
    }

    private void installApk(Context context) {
        Intent mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

    //获取VersionCode
    private int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //***************************************************************************************************************************************
    private void init() {
        img_mainpage = (ImageView) findViewById(R.id.img_mainpage);
        img_product = (ImageView) findViewById(R.id.img_product);
        img_user = (ImageView) findViewById(R.id.img_user);
        txt_mainpage = (TextView) findViewById(R.id.txt_mainpage);
        txt_product = (TextView) findViewById(R.id.txt_product);
        txt_user = (TextView) findViewById(R.id.txt_user);
        bottom = (LinearLayout) findViewById(R.id.bottom);

        // 初始化主界面的4个Fragment
        HomePagerFragment frag_main = new HomePagerFragment();
        mFragments = new ArrayList<>();
        mFragments.add(frag_main);
        mFragments.add(new DiscoverFragment());
        mFragments.add(new MyInfoFragment());
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        content.setAdapter(myFragmentPagerAdapter);
        content.setPagingEnabled(false);
        content.setOffscreenPageLimit(3);
//		content.addOnPageChangeListener(frag_main);
        addFirstFragment();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_main:
                UmengUtil.eventById(MainActivity.this, R.string.home);
                setFragMain();
                break;

            case R.id.layout_product:
                UmengUtil.eventById(MainActivity.this, R.string.main_product_list);
                setFragProduct();
                break;

            case R.id.layout_user:
                UmengUtil.eventById(MainActivity.this, R.string.self_c);
                setFragUser();
                break;

            default:
                break;
        }
    }

    protected void onNewIntent(android.content.Intent intent) {
        if (intent == null) return;
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
        showDialog();
    }

    private void showDialog() {
        if (MyApplication.saveUserInfo.getAccess_Token() == null) {
            DLDialog dialog = new DLDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
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
            if (intent.getAction()
                    .equals("com.bcb.update.mainui")) {
                setFragMain();
            } else if (intent.getAction()
                    .equals("com.bcb.product.regular")) {
                setFragProduct();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
            if (downloadCompleteReceiver != null) {//如果receiver不为空，说明正在下载APK包，即使应用退出了，也会继续下载
                unregisterReceiver(downloadCompleteReceiver);
                saveIsFinishedWhenDownloading();//如果在下载过程中退出了，下次进入应用时重新下载
            }
        } catch (Exception e) {
        }
        EventBus.getDefault()
                .unregister(this);
    }

    //接收事件

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BroadcastEvent event) {
        //判断要显示的fragment
        String flag = event.getFlag();
        if (!TextUtils.isEmpty(flag)) {
            switch (flag) {
                case BroadcastEvent.HOME:
                    UmengUtil.eventById(MainActivity.this, R.string.home);
                    setFragMain();
                    break;
                case BroadcastEvent.PRODUCT:
                    UmengUtil.eventById(MainActivity.this, R.string.main_product_list);
                    setFragProduct();
                    break;
                case BroadcastEvent.USER:
                    UmengUtil.eventById(MainActivity.this, R.string.self_c);
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
                if (MyApplication.saveUserInfo.getAccess_Token() == null) {
                    //                    MyApplication.saveUserInfo.setGesturePassword("");
                }
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
            if (tabs != null && tabs.size() > 0) return tabs.size();
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }


    /**
     * 友盟分享需要调用的代码
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}