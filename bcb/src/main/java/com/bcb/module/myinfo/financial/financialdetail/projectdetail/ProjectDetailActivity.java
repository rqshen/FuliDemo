package com.bcb.module.myinfo.financial.financialdetail.projectdetail;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.UrlsOne;
import com.bcb.utils.BitmapUtil;
import com.bcb.utils.DESUtil;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MQCustomerManager;
import com.bcb.utils.MyActivityManager;
import com.bcb.constant.MyConstants;
import com.bcb.utils.ToastUtil;
import com.bcb.presentation.view.activity.Activity_Authentication;
import com.bcb.presentation.view.activity.Activity_Join_Company;
import com.bcb.presentation.view.activity.Activity_Login;
import com.bcb.presentation.view.activity.Activity_Open_Account;
import com.bcb.presentation.view.activity.Activity_Register_First;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.Browser.X5WebView;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.bcb.R.id.back_img;

/**
 * 项目详情
 */
public class ProjectDetailActivity extends Activity_Base {

	private X5WebView mWebView;
	private ViewGroup mViewParent;
	//加密的key
	private static final String key = "9e469d566f5d41j1a83b9rf4";
	private String mIntentUrl;
	private String title;
	private int type;
	boolean hasLoad;
	//对话框
	private DialogWidget certDialog;
	//微信分享
	private IWXAPI iwxapi;
	AlertView alertView;
	private Context context;
	private ProgressBar mPageLoadingProgressBar;
	RelativeLayout root;

	public static void launche(Context ctx, String tittle, String url) {
		launche2(ctx, tittle, url, 0);
	}

	public static void launche2(Context ctx, String tittle, String url, int type) {
		Intent intent = new Intent();
		intent.setClass(ctx, ProjectDetailActivity.class);
		intent.putExtra("title", tittle);
		intent.putExtra("url", url);
		intent.putExtra("type", type);
		ctx.startActivity(intent);
	}

	public static void launcheForResult(Activity ctx, String tittle, String url, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(ctx, ProjectDetailActivity.class);
		intent.putExtra("title", tittle);
		intent.putExtra("url", url);
		ctx.startActivityForResult(intent, requestCode);
	}

	/**
	 * 聚爱跳转过来的
	 *
	 * @param ctx
	 * @param tittle
	 * @param isLove
	 * @param url
	 */
	public static void launcheFromLove(Context ctx, String tittle, boolean isLove, String loveTitle, String loveContent, String url) {
		Intent intent = new Intent();
		intent.setClass(ctx, ProjectDetailActivity.class);
		intent.putExtra("title", tittle);
		intent.putExtra("url", url);
		intent.putExtra("isLove", isLove);
		intent.putExtra("loveTitle", loveTitle);
		intent.putExtra("loveContent", loveContent);
		ctx.startActivity(intent);
	}

	/**
	 * 启动WebView，带是否包含token的状态
	 *
	 * @param ctx
	 * @param tittle
	 * @param url
	 * @param hasToken true表示包含token
	 */
	public static void launche(Context ctx, String tittle, String url, boolean hasToken) {
		Intent intent = new Intent(ctx, ProjectDetailActivity.class);
		intent.putExtra("title", tittle);
		intent.putExtra("url", url);
		intent.putExtra("hasToken", hasToken);
		ctx.startActivity(intent);
		LogUtil.d("url", url);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		//管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
		MyActivityManager myActivityManager = MyActivityManager.getInstance();
		myActivityManager.pushOneActivity(ProjectDetailActivity.this);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		Intent intent = getIntent();
		if (intent != null) {
			//最终的URL
			mIntentUrl = getUrlStrWithDES();
			title = intent.getStringExtra("title");
			type = intent.getIntExtra("type", 0);
			if (getIntent().getBooleanExtra("isLove", false)) {
				final String loveTitle = intent.getStringExtra("loveTitle");
				final String loveContent = intent.getStringExtra("loveContent");
				setRightBtnVisiable(View.VISIBLE);
				setRightBtnImg(R.drawable.ico_share, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//注册微信
						registerToWeiXin();
						//打开对话框
						popShareToWeiXin(loveTitle, mIntentUrl, loveContent);
					}
				});
			}

			if (title.equals("解绑")) {
				setRightTitleValue("托管账号", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertView.Builder ibuilder = new AlertView.Builder(context);
						ibuilder.setTitle("账号提示");
						ibuilder.setMessage("您的汇付资金托管账户为：\n" + MyApplication.mUserDetailInfo.CustodyAccount);
						ibuilder.setPositiveButton("复制", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								((android.content.ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE)).setText(MyApplication
										.mUserDetailInfo.CustodyAccount);
								alertView.dismiss();
							}
						});
						ibuilder.setNegativeButton("取消", null);
						alertView = ibuilder.create();
						alertView.show();
					}
				});
			}
		}
		//硬件加速
		try {
			if (Build.VERSION.SDK_INT >= 11) {
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams
						.FLAG_HARDWARE_ACCELERATED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setBaseContentView(R.layout.activity_browser);
		setLeftTitleVisible(true);
		setTitleVisiable(View.VISIBLE);
		setTitleValue(title);

		if (type==20095) {
			//红色标题栏
			layout_title.setBackgroundColor(getResources().getColor(R.color.red));
			title_text.setTextColor(getResources().getColor(R.color.white));
			dropdown.setImageResource(R.drawable.return_delault);
			dropdown.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			(findViewById(back_img)).setVisibility(View.GONE);
		}

		mViewParent = (ViewGroup) findViewById(R.id.layout_webview);
		root = (RelativeLayout) findViewById(R.id.root);

		QbSdk.preInit(this);
		X5WebView.setSmallWebViewEnabled(true);
		mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);

		initProgressBar();
	}

	private void initProgressBar() {
		mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mPageLoadingProgressBar.setMax(100);
		mPageLoadingProgressBar.setProgressDrawable(this.getResources().getDrawable(R.drawable.color_progressbar));
	}

	//加密后的链接
	private String getUrlStrWithDES() {
		String url = getIntent().getStringExtra("url");
		if (!getIntent().getBooleanExtra("hasToken", false)) {
			return url;
		}
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
			encodeByte_ECB = DESUtil.des3EncodeECB(key.getBytes(), data);
			param = Base64.encodeToString(encodeByte_ECB, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url + "?accessToken=" + param;
	}

	/**
	 * 初始化界面元素
	 */
	private void init() {
		mWebView = new X5WebView(this);
		mViewParent.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams
				.MATCH_PARENT));
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.i("bqt", "X5WebView的url = " + url);
				if (type == 20094) {
					root.setVisibility(View.INVISIBLE);
					return true;
				}

				//页面内部跳转至首页的时候，则销毁当前WebView
				if (url.equalsIgnoreCase("fulihui://joincompany")) {
					joinCompany();
					return true;
				} else if (url.contains("fulihui://openwx")) {
					try {
						//注册微信
						registerToWeiXin();
						//打开对话框
						popShareToWeiXin(URLDecoder.decode(url, "UTF-8"));

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					return true;
				} else if (url.contains("fulihui://register")) {
					if (MyApplication.saveUserInfo.getAccess_Token() == null) {

					}
					startActivity(new Intent(ProjectDetailActivity.this, Activity_Register_First.class));
					finish();
					return true;
				} else if (url.contains("fulihui://login")) {
					startActivity(new Intent(ProjectDetailActivity.this, Activity_Login.class));
					finish();
					return true;
				} else if (url.contains("fulihui://callcenter")) {
					//如果ID存在
					String userId = null;
					if (MyApplication.mUserDetailInfo != null) {
						userId = MyApplication.mUserDetailInfo.getCustomerId();
					}
					MQCustomerManager.getInstance(ProjectDetailActivity.this).showCustomer(userId);
					return true;
				} else if (url.contains("mqqapi://forward") || url.contains("wtloginmqq://ptlogin/qlogin")) {
					try {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					} catch (Exception e) {
						ToastUtil.alert(context, "请先安装QQ");
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
			public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
				if (type == 20094) {
					root.setVisibility(View.INVISIBLE);
				}
				super.onPageStarted(webView, s, bitmap);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				root.setVisibility(View.VISIBLE);
				if (type == 20094 && !hasLoad) {
					view.loadUrl(url);
					hasLoad = true;
				} else super.onPageFinished(view, url);
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
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
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
		if (TextUtils.isEmpty(mIntentUrl)) {
			mWebView.loadUrl(UrlsOne.AboutFuliJingRong);//一分钟了解福利金融
		} else {
			mWebView.loadUrl(mIntentUrl);
		}
		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().sync();
	}

	//加入公司
	private void joinCompany() {
		//如果未认证，则去认证
		//        if (MyApplication.mUserDetailInfo == null || !MyApplication.mUserDetailInfo.HasCert) {
		//            popCertDialog();
		//        }
		if (MyApplication.mUserDetailInfo == null || !MyApplication.mUserDetailInfo.HasOpenCustody) popHFDialog();
			//如果不存在公司信息或者状态不为10(通过审核)的时候，则跳转去加入公司页面
		else if (MyApplication.mUserDetailInfo.MyCompany == null || MyApplication.mUserDetailInfo.MyCompany.Status != 10) {
			Activity_Join_Company.launche(ProjectDetailActivity.this);
		}
	}

	/************************
	 * 去开通汇付
	 ******************************/
	private DialogWidget dialogWidget;

	private void popHFDialog() {
		startActivity(new Intent(context, Activity_Open_Account.class));
		//        dialogWidget = new DialogWidget(context, IdentifyAlertView.getInstance(context, new IdentifyAlertView
		// .OnClikListener() {
		//            @Override
		//            public void onCancelClick() {
		//                dialogWidget.dismiss();
		//                dialogWidget = null;
		//            }
		//
		//            @Override
		//            public void onSureClick() {
		//                dialogWidget.dismiss();
		//                dialogWidget = null;
		//                startActivity(new Intent(context, Activity_Open_Account.class));
		//            }
		//        }).getView());
		//        dialogWidget.show();
	}

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
		popShareToWeiXin(title, tmpurl, content);
	}

	//弹框提示是否分享到微信对话还是分享到微信朋友圈
	private void popShareToWeiXin(final String title, final String url, final String content) {
		final Dialog certDialog = new Dialog(ProjectDetailActivity.this);
		View view = View.inflate(ProjectDetailActivity.this, R.layout.dialog_alertview, null);

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
				webpage.webpageUrl = url;
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
				webpage.webpageUrl = url;
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

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	//    /***************************** 认证 *****************************/
	//    private void popCertDialog() {
	//        certDialog = new DialogWidget(ProjectDetailActivity.this, IdentifyAlertView.getInstance(ProjectDetailActivity.this, new
	// IdentifyAlertView.OnClikListener() {
	//            @Override
	//            public void onCancelClick() {
	//                certDialog.dismiss();
	//                certDialog = null;
	//            }
	//
	//            @Override
	//            public void onSureClick() {
	//                certDialog.dismiss();
	//                certDialog = null;
	//                //去认证
	//                gotoAuthenticationActivity();
	//            }
	//        }).getView());
	//        certDialog.show();
	//    }

	/**
	 * 跳转到认证界面
	 */
	private void gotoAuthenticationActivity() {
		Intent newIntent = new Intent(ProjectDetailActivity.this, Activity_Authentication.class);
		startActivityForResult(newIntent, 10);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView != null && mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			} else return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (mWebView != null) {
			mWebView.setVisibility(View.GONE);
			mWebView.destroy();
		}
		super.onDestroy();
	}

	/**
	 * 处理问题：
	 * Activity has leaked window Android.widget.ZoomButtonsController
	 * that was originally added here android.view.WindowLeaked
	 */
	//    @Override
	//    public void finish() {
	//        ViewGroup view = (ViewGroup) getWindow().getDecorView();
	//        view.removeAllViews();
	//        super.finish();
	//        //退出页面使用向右退出
	//        overridePendingTransition(0, R.anim.push_right_out);
	//    }

	public static final int MSG_INIT_UI = 1;
	private Handler mTestHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_INIT_UI:
					init();
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void finish() {
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		view.removeAllViews();
		super.finish();
	}
}
