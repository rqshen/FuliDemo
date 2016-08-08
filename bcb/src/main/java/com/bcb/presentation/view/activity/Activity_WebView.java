package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.util.DESUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.presentation.view.custom.Browser.X5WebView;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Activity_WebView extends Activity_Base {
    private String mIntentUrl;
    private String title;
    private String postData;
    private Context context;
    private ProgressBar mPageLoadingProgressBar;
    private X5WebView mWebView;
    private ViewGroup mViewParent; /*加密的key*/
    private static final String KEY = "9e469d566f5d41j1a83b9rf4";

    public static void launche(Context ctx, String tittle, String url, String postData) {
        Intent intent = new Intent(ctx, Activity_WebView.class);
        intent.putExtra("title", tittle);
        intent.putExtra("url", url);
        intent.putExtra("postData", postData);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this; /*管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity*/
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_WebView.this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        Intent intent = getIntent();
        if (intent != null) {
            mIntentUrl = getUrlStrWithDES();
            title = intent.getStringExtra("title");
            postData = intent.getStringExtra("postData");
        }
        try {/*硬件加速*/
            if (Build.VERSION.SDK_INT >= 11) getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setBaseContentView(R.layout.activity_web_view);
        setLeftTitleVisible(true);
        setTitleVisiable(View.VISIBLE);
        setTitleValue(title);
        mViewParent = (ViewGroup) findViewById(R.id.layout_webview);
        QbSdk.preInit(this);
        X5WebView.setSmallWebViewEnabled(true);
        init();
        initProgressBar();
    }

    private void initProgressBar() {
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources().getDrawable(R.drawable.color_progressbar));
    }

    private void init() {
        mWebView = new X5WebView(this);
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.i("bqt", "截获的url地址：" + url);
                String message = null;
                try {
                    message = URLDecoder.decode(url.substring(url.lastIndexOf('|') + 1), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //【开户】
                if (url.contains("fulihui://open_result")) {
                    LogUtil.i("bqt", "开户" + url);
                    if (url.contains("000")) {//成功
                        App.getInstance().mUserDetailInfo.HasOpenCustody = true;
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.OPEN_HF_SUCCESS, message);
                    } else Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.OPEN_HF_FAILED, message);
                    finish();
                    return true;
                }
                //【绑卡】
//                else if (url.contains("fulihui://")) {
//                    LogUtil.i("bqt", "绑卡" + url);
//                    if (url.contains("000")) {//成功
//                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.CHARGE__HF_SUCCESS, "");
//                    } else {
//                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.CHARGE_HF_FAILED, "");
//                    }
//                    finish();
//                    return true;
//                }
                //【充值】
                else if (url.contains("fulihui://recharge_result")) {
                    LogUtil.i("bqt", "充值" + url);
                    if (url.contains("000")) {//成功
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.CHARGE__HF_SUCCESS, message);
                    } else {
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.CHARGE_HF_FAILED, message);
                    }
                    finish();
                    return true;
                }
                //【提现】
                else if (url.contains("fulihui://withdraw_result")) {
                    LogUtil.i("bqt", "提现" + url);
                    if (url.contains("000")) {//成功
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.TX_HF_SUCCESS, message);
                    } else {
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.TX_HF_FAILED, message);
                    }
                    finish();
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mPageLoadingProgressBar.setProgress(newProgress);
                if (mPageLoadingProgressBar != null && newProgress != 100) {
                    mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                } else if (mPageLoadingProgressBar != null) {
                    mPageLoadingProgressBar.setVisibility(View.GONE);
                }
            }
        });

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.postUrl(mIntentUrl, EncodingUtils.getBytes(postData, "base64"));

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    //加密后的链接
    private String getUrlStrWithDES() {
        String url = getIntent().getStringExtra("url");
        byte[] data = null;
        byte[] encodeByte_ECB;
        try {
            data = App.saveUserInfo.getLocalPhone().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "";
        if (data == null) {
            return url;
        }
        try {
            encodeByte_ECB = DESUtil.des3EncodeECB(KEY.getBytes(), data);
            param = Base64.encodeToString(encodeByte_ECB, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url + "?accessToken=" + param;
    }
}
