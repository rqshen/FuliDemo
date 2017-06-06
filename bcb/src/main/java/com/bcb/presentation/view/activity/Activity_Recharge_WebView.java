package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.util.LoadingLayoutMgr;
import com.bcb.util.MyActivityManager;

/**
 * Created by cain on 15/12/25.
 */
public class Activity_Recharge_WebView extends Activity_Base {

    private static final String TAG = "Activity_Recharge_WebView";
    private String bcbUrl = "fulihui://banktoapp";
    private String tittle;
    private String myUrl;

    //设置标题和返回设置
    private WebView webView;

    //加载刷新工具
    private RelativeLayout loading_layout;
    private LoadingLayoutMgr ld;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Recharge_WebView.this);
        tittle = getIntent().getStringExtra("tittle");
        myUrl = getIntent().getStringExtra("url");
        setBaseContentView(R.layout.activity_recharge_webview);
        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dimissActivity();
            }
        });
        setTitleValue(tittle);
        //初始化界面
        setupView();

    }


    //初始化界面
    private void setupView() {
        //加载控件
        loading_layout = (RelativeLayout) findViewById(R.id.load_layout);
        ld = new LoadingLayoutMgr(Activity_Recharge_WebView.this, false, "正在加载...");
        loading_layout.addView(ld.getLayout());
        //初始化WebView页面
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setVerticalScrollBarEnabled(false);
        webView.setVerticalScrollbarOverlay(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setHorizontalScrollbarOverlay(false);
        webView.getSettings().setBlockNetworkImage(false);
        webView.setWebViewClient(new MyWebViewClient());
        //加载URL
        webView.loadUrl(myUrl);
    }



    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }


    @Override
    public void onBackPressed() {
        dimissActivity();
    }


    // 关联webview类
    class MyWebViewClient extends WebViewClient {

        //开始加载数据时
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            //页面内部跳转至首页的时候，则销毁当前WebView
            if (url.indexOf(bcbUrl) != -1){
                dimissActivity();
            }
        }

        //加载开始的时候
        @Override
        public  void onPageStarted(WebView view, String url, Bitmap favicon) {
            //页面内部跳转至首页的时候，则销毁当前WebView
            if (url.indexOf(bcbUrl) != -1) {
                dimissActivity();
            }
        }

        // 加载结束的时候
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loading_layout.setVisibility(View.GONE);
            loading_layout.removeAllViews();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //页面内部跳转至首页的时候，则销毁当前WebView
            if (url.indexOf(bcbUrl) != -1){
                dimissActivity();
                return true;
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }
    }

    //退出界面
    private void dimissActivity() {
        Intent intent = new Intent();
        intent.putExtra("RechargeVerification", true);
        setResult(1, intent);
        finish();
    }
}