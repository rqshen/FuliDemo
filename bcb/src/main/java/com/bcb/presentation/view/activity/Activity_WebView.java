package com.bcb.presentation.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.data.util.BitmapUtil;
import com.bcb.data.util.DESUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.MyConstants;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.IdentifyAlertView;
import com.bcb.presentation.view.custom.custom_webview.X5WebView;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.bcb.data.util.LoadingLayoutMgr;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Activity_WebView extends Activity_Base {
	
	private static final String TAG = "Activity_WebView";

    //微信分享
    private IWXAPI iwxapi;

    private String tittle;
    private String url;

    private ViewGroup mViewParent;
    private X5WebView webView;
    //加载刷新工具
    private RelativeLayout loading_layout;
    private LoadingLayoutMgr ld;

    //微信
    private boolean WeiXinStatus = false;
    private String urlStr;

    //加密的key
    private static final String key = "9e469d566f5d41j1a83b9rf4";

    //对话框
    private DialogWidget certDialog;


    public static void launche(Context ctx, String tittle, String url){
		Intent intent = new Intent();
		intent.setClass(ctx, Activity_WebView.class);
		intent.putExtra("tittle", tittle);
		intent.putExtra("url", url);
		ctx.startActivity(intent);
	}


    public static void launche(Context ctx, String tittle, String url, boolean WeiXinStatus) {
        Intent intent = new Intent();
        intent.setClass(ctx, Activity_WebView.class);
        intent.putExtra("tittle", tittle);
        intent.putExtra("url", url);
        intent.putExtra("WeiXinStatus", WeiXinStatus);
        ctx.startActivity(intent);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_WebView.this);
		tittle = getIntent().getStringExtra("tittle");
		url = getIntent().getStringExtra("url");
        setBaseContentView(R.layout.activity_webview);
        setLeftTitleVisible(true);
        setTitleValue(tittle);
        WeiXinStatus = getIntent().getBooleanExtra("WeiXinStatus", false);
        //加密
        getUrlStrWithDES();

        //开启硬件加速
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //X5SDK初始化
        QbSdk.preInit(this);
        X5WebView.setSmallWebViewEnabled(true);

        handler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);

	}

    private final int MSG_INIT_UI = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_INIT_UI:
                    //初始化界面
                    setupView();
                    break;
            }

        }
    };


    //加密后的链接
    private void getUrlStrWithDES() {
        byte[] data = new byte[0];
        byte[] encodeByte_ECB = new byte[0];
        try {
            data =  App.saveUserInfo.getLocalPhone().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "";
        try {
            encodeByte_ECB = DESUtil.des3EncodeECB(key.getBytes(), data);
            param = Base64.encodeToString(encodeByte_ECB, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        urlStr = url + "?accessToken=" + param;
    }

    //初始化界面
    private void setupView() {


        //加载控件
        loading_layout = (RelativeLayout) findViewById(R.id.load_layout);
        ld = new LoadingLayoutMgr(Activity_WebView.this, false, "正在加载...");
        loading_layout.addView(ld.getLayout());


        //初始化WebView页面
        mViewParent = (ViewGroup) findViewById(R.id.webview);
        webView = new X5WebView(this);
        mViewParent.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setDefaultTextEncodingName("UTF-8");
//        webView.getSettings().setSupportZoom(false);
//        webView.getSettings().setBuiltInZoomControls(false);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.setVerticalScrollBarEnabled(false);
//        webView.setHorizontalScrollBarEnabled(false);
//        webView.getSettings().setBlockNetworkImage(false);
//        webView.setWebViewClient(new MyWebViewClient());


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //页面内部跳转至首页的时候，则销毁当前WebView
                if (url.equalsIgnoreCase("fulihui://joincompany")) {
                    joinCompany();
                    return true;
                } else if (url.contains("fulihui://openwx")) {
                    try {
                        //解析URL
                        urlStr = URLDecoder.decode(url, "UTF-8");
                        //注册微信
                        registerToWeiXin();
                        //打开对话框
                        popShareToWeiXin(urlStr);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }




            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading_layout.setVisibility(View.GONE);
                loading_layout.removeAllViews();
            }


        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
            }



            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                loading_layout.setVisibility(View.GONE);
                loading_layout.removeAllViews();
            }

        });

        WebSettings webSetting = webView.getSettings();
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
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();

        //判断是否是微信分享活动页面，微信分享页面带有加密之后的参数
        if (WeiXinStatus) {
            webView.loadUrl(urlStr);
        } else {
            webView.loadUrl(url);
        }
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != webView){
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != webView){
            webView.onPause();
        }
    }

//    // 关联webview类
//    class MyWebViewClient extends WebViewClient {
//
//        //开始加载数据时
//        @Override
//        public void onLoadResource(WebView view, String url) {
//            super.onLoadResource(view, url);
//        }
//
//        // 加载结束的时候
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            loading_layout.setVisibility(View.GONE);
//            loading_layout.removeAllViews();
//        }
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            //页面内部跳转至首页的时候，则销毁当前WebView
//            if (url.equalsIgnoreCase("fulihui://joincompany")) {
//                joinCompany();
//                return true;
//            } else if (url.contains("fulihui://openwx")) {
//                try {
//                    //解析URL
//                    urlStr = URLDecoder.decode(url, "UTF-8");
//                    //注册微信
//                    registerToWeiXin();
//                    //打开对话框
//                    popShareToWeiXin(urlStr);
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                return true;
//            } else {
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//        }
//    }

    //注册微信
    private void registerToWeiXin() {
        iwxapi = WXAPIFactory.createWXAPI(this, MyConstants.APP_ID, true);
        iwxapi.registerApp(MyConstants.APP_ID);
    }

    //弹框提示是否分享到微信对话还是分享到微信朋友圈
    private void popShareToWeiXin(String urlStr) {
        String values[] = urlStr.split("\\|");
        final String title = values[1];
        final String tmpurl = values[2];
        final String content = values[3];
        final Dialog certDialog = new Dialog(Activity_WebView.this);
        View view = View.inflate(Activity_WebView.this, R.layout.dialog_alertview, null);

        Window win = certDialog.getWindow();
        certDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        win.getDecorView().setPadding(60, 0, 60, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        certDialog.setContentView(view);

        //取消按钮
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                certDialog.dismiss();
            }
        });

        //分享到朋友圈
        LinearLayout layoutTimeLine = (LinearLayout) view.findViewById(R.id.layout_timeline);
        layoutTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = tmpurl;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = content;
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.bcb_logo);
                msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                iwxapi.sendReq(req);
                certDialog.dismiss();
            }
        });
        //分享到微信对话群
        LinearLayout layoutWeixin = (LinearLayout) view.findViewById(R.id.layout_weixin);
        layoutWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = tmpurl;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = content;
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.bcb_logo);
                msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                iwxapi.sendReq(req);
                certDialog.dismiss();
            }
        });
        certDialog.show();
    }

    //加入公司
    private void joinCompany() {
        //如果未认证，则去认证
        if (App.mUserDetailInfo == null || !App.mUserDetailInfo.HasCert) {
            popCertDialog();
        }
        //如果不存在公司信息或者状态不为10(通过审核)的时候，则跳转去加入公司页面
        else if (App.mUserDetailInfo.MyCompany == null || App.mUserDetailInfo.MyCompany.Status != 10) {
            Activity_Join_Company.launche(Activity_WebView.this);
        }
    }

    /***************************** 认证 *****************************/
    private void popCertDialog() {
        certDialog = new DialogWidget(Activity_WebView.this, IdentifyAlertView.getInstance(Activity_WebView.this, new IdentifyAlertView.OnClikListener() {
            @Override
            public void onCancelClick() {
                certDialog.dismiss();
                certDialog = null;
            }

            @Override
            public void onSureClick() {
                certDialog.dismiss();
                certDialog = null;
                //去认证
                gotoAuthenticationActivity();
            }
        }).getView());
        certDialog.show();
    }
    //跳转到认证界面
    private void gotoAuthenticationActivity() {
        Intent newIntent = new Intent(Activity_WebView.this, Activity_Authentication.class);
        startActivityForResult(newIntent, 10);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
