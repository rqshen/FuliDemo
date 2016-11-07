//package com.bcb.presentation.view.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.PixelFormat;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Base64;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.webkit.WebView;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.bcb.R;
//import com.bcb.common.app.App;
//import com.bcb.data.util.DESUtil;
//import com.bcb.data.util.MyActivityManager;
//import com.bcb.presentation.view.activity_interface.WebAppinterface;
//
//import java.io.UnsupportedEncodingException;
//
//public class Activity_Web_Normal extends Activity_Base {
//	private String mIntentUrl;
//	private String title;
//	private Context context;
//	private ProgressBar mPageLoadingProgressBar;
//	private WebView webview;
//	private ViewGroup mViewParent;
//	/*加密的key*/
//	private static final String KEY = "9e469d566f5d41j1a83b9rf4";
//
//	public static void launche(Context ctx, String tittle, String url) {
//		Intent intent = new Intent(ctx, Activity_Web_Normal.class);
//		intent.putExtra("title", tittle);
//		intent.putExtra("url", url);
//		ctx.startActivity(intent);
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		context = this;
//		MyActivityManager.getInstance().pushOneActivity(this);
//		getWindow().setFormat(PixelFormat.TRANSLUCENT);
//		Intent intent = getIntent();
//		if (intent != null) {
//			mIntentUrl = getUrlStrWithDES();
//			title = intent.getStringExtra("title");
//		}
//
//		setBaseContentView(R.layout.activity_web_normal);
//		setLeftTitleVisible(true);
//		setTitleVisiable(View.VISIBLE);
//		setTitleValue(title);
//		mViewParent = (ViewGroup) findViewById(R.id.layout_webview);
//
//		init();
//		initProgressBar();
//	}
//
//	private void initProgressBar() {
//		mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//		mPageLoadingProgressBar.setMax(100);
//		mPageLoadingProgressBar.setProgressDrawable(this.getResources().getDrawable(R.drawable.color_progressbar));
//	}
//
//	private void init() {
//		webview=new WebView(this);
//		webview.getSettings().setJavaScriptEnabled(true);//支持javascript。这个属性基本也是必须的，否则网页内容不会自适应手机屏幕
//		webview.setWebViewClient(new MyWebViewClient(progress_bar));//在本WebView中显示网页内容。
//		webview.addJavascriptInterface(new WebAppinterface(this), JS_INTERFACE);// 注册后可以在JS中调用此接口中定义的方法
//
//
//		}
//	}
//	@Override
//	//点击后退按钮不退出Activity，而是让WebView后退一页。也可以通过webview.setOnKeyListener设置
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
//			webview.goBack(); //后退，goForward() 前进
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	//加密后的链接
//	private String getUrlStrWithDES() {
//		String url = getIntent().getStringExtra("url");
//		byte[] data = null;
//		byte[] encodeByte_ECB;
//		try {
//			data = App.saveUserInfo.getLocalPhone().getBytes("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		String param = "";
//		if (data == null) {
//			return url;
//		}
//		try {
//			encodeByte_ECB = DESUtil.des3EncodeECB(KEY.getBytes(), data);
//			param = Base64.encodeToString(encodeByte_ECB, Base64.DEFAULT);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return url + "?accessToken=" + param;
//	}
//
//	@Override
//	public void finish() {
//		try {
//			mWebView.removeAllViews();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		super.finish();
//	}
//}
