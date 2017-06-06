package com.bcb.module.browse;

import android.app.Activity;
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

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.module.discover.financialproduct.normalproject.buy.ProjectBuyFailActivity;
import com.bcb.module.discover.financialproduct.normalproject.buy.ProjectBuySuccessActivity;
import com.bcb.module.myinfo.balance.withdraw.WithdrawSuccessActivity;
import com.bcb.presentation.view.activity.Activity_Tips_FaileOrSuccess;
import com.bcb.presentation.view.custom.Browser.X5WebView;
import com.bcb.util.DESUtil;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
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

/**
 * 资金托管
 * 汇付天下 开户 界面
 */
public class FundCustodianWebActivity extends Activity_Base {
    private String mIntentUrl;
    private String title;
    private String postData;
    private Context context;
    private ProgressBar mPageLoadingProgressBar;
    private X5WebView mWebView;
    private ViewGroup mViewParent;
    /*加密的key*/
    private static final String KEY = "9e469d566f5d41j1a83b9rf4";

    public static void launche(Context ctx, String tittle, String url, String postData) {
        Intent intent = new Intent(ctx, FundCustodianWebActivity.class);
        intent.putExtra("title", tittle);
        intent.putExtra("url", url);
        intent.putExtra("postData", postData);
        ctx.startActivity(intent);
    }

    public static void launcheForResult(Activity ctx, String tittle, String url, String postData, int requestCode) {
        Intent intent = new Intent(ctx, FundCustodianWebActivity.class);
        intent.putExtra("title", tittle);
        intent.putExtra("url", url);
        intent.putExtra("postData", postData);
        ctx.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this; /*管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity*/
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(FundCustodianWebActivity.this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        Intent intent = getIntent();
        if (intent != null) {
            mIntentUrl = getUrlStrWithDES();
            title = intent.getStringExtra("title");
            postData = intent.getStringExtra("postData");
        }
        try {/*硬件加速*/
            if (Build.VERSION.SDK_INT >= 11)
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams
                        .FLAG_HARDWARE_ACCELERATED);
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
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams
                .MATCH_PARENT));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) {
                    LogUtil.i("bqt", "截获的url地址为空");
                    return true;
                }
                LogUtil.i("bqt", "截获的url地址：" + url);
                String message = "";
                try {
                    message = URLDecoder.decode(url.substring(url.lastIndexOf('|') + 1), "UTF-8");
                    LogUtil.i("bqt", "最后一部分消息内容" + message);
                } catch (Exception e) {
                    LogUtil.i("bqt", "提取消息出错" + e.toString());
                    e.printStackTrace();
                }
                //******************************************************************************************
                //开通自动投标
                if (url.contains("fulihui://openautotenderplanresult")) {
                    LogUtil.i("bqt", "开通自动投标" + url);
                    if (url.contains("000")) {//成功
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.ZDTB_SUCCESS, message);
                    } else
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.ZDTB_FAILED, message);
                    finish();
                    return true;
                }
                //【开户】
                else if (url.contains("fulihui://open_result")) {
                    LogUtil.i("bqt", "开户" + url);
                    if (url.contains("000")) {//成功
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.OPEN_HF_SUCCESS, message);
                    } else
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.OPEN_HF_FAILED, message);
                    finish();
                    return true;
                }
                //【充值】
                else if (url.contains("fulihui://recharge_result")) {
                    LogUtil.i("bqt", "充值" + url);
                    //针对【充值失败、绑卡失败】--绑卡没有结果页，暂不做；436也暂不做
                    if (url.contains("434")) message = "银行卡余额不足";
                    else if (url.contains("435")) message = "金额超过单笔限额";
                    //	else if (url.contains("436")) message = "姓名、证件信息或手机与银行账户预留信息不一致";
                    if (url.contains("000")) {
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.CHARGE__HF_SUCCESS, message);
                    } else
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.CHARGE_HF_FAILED, message);
                    finish();
                    return true;
                }
                //【提现】
                else if (url.contains("fulihui://withdraw_result")) {
                    LogUtil.i("bqt", "提现" + url);
                    if (url.contains("000")) {
                        startActivity(WithdrawSuccessActivity.newIntent(context, message));//提现成功
                    } else
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.TX_HF_FAILED, message);
                    finish();
                    return true;
                    //买标、申购
                } else if (url.contains("fulihui://invest_result")) {
                    LogUtil.i("bqt", "买标" + url);
                    if (url.contains("000")) {
                        startActivity(ProjectBuySuccessActivity.newIntent(context, message));
                    } else {
                        startActivity(ProjectBuyFailActivity.newIntent(context));
                    }
                    finish();
                    return true;
                    //生利宝
                } else if (url.contains("fulihui://fsstrans_result")) {
                    LogUtil.i("bqt", "生利宝" + url);
                    //重新生成message
                    try {
                        message = URLDecoder.decode(url.substring(url.indexOf('|') + 1), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    LogUtil.i("bqt", "【FundCustodianWebActivity】【shouldOverrideUrlLoading】" + message);
                    if (url.contains("000")) {
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.SLB_SUCCESS, message);
                    } else
                        Activity_Tips_FaileOrSuccess.launche(context, Activity_Tips_FaileOrSuccess.SLB_FAILED, message);
                    finish();
                    return true;
                } else return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
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

        byte[] base64s = EncodingUtils.getBytes(postData, "base64");
        mWebView.postUrl(mIntentUrl, base64s);

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    //加密后的链接
    private String getUrlStrWithDES() {
        String url = getIntent().getStringExtra("url");
        byte[] data = null;
        byte[] encodeByte_ECB;
        try {
            data = MyApplication.saveUserInfo.getLocalPhone().getBytes("UTF-8");
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

    @Override
    public void finish() {
        //        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        //        view.removeAllViews();
        try {
            mWebView.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.finish();
    }
}
