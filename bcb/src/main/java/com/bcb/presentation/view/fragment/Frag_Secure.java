package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bcb.R;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.ToastUtil;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

public class Frag_Secure extends Frag_Base implements OnClickListener {
	
	private static final String TAG = "Frag_Secure";
	
	private ImageView back_img;
	private TextView title_text;
	
	private WebView webView;
	private boolean left_textIsgone;

	private String tittle = "";
	private String url = "";
		
	private Context ctx;

    private PullToRefreshLayout refreshLayout;

    public Frag_Secure() {
        super();
    }

    @SuppressLint("ValidFragment")
	public Frag_Secure(Context ctx, boolean left_textIsgone, String tittle, String url) {
		super();
		this.ctx = ctx;
		this.left_textIsgone = left_textIsgone;
		this.tittle = tittle;
		this.url = url;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_secure, container, false);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.ctx = view.getContext();

        back_img = (ImageView) view.findViewById(R.id.back_img);
        back_img.setOnClickListener(this);

        if (left_textIsgone) {
            back_img.setVisibility(View.GONE);
        }else {
            back_img.setVisibility(View.VISIBLE);
        }

        title_text = (TextView) view.findViewById(R.id.title_text);
        title_text.setText(tittle);
        //判断设备是否Android4.4以上，如果是，则表示使用了浸入式状态栏，需要设置状态栏的位置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((LinearLayout) view.findViewById(R.id.layout_topbar)).setVisibility(View.VISIBLE);
        }

        //初始化webview
        webView = (WebView) view.findViewById(R.id.secure_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);;
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);

        //刷新
        ((RelativeLayout)view.findViewById(R.id.loadmore_view)).setVisibility(View.GONE);
        refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                //加载数据
                if (HttpUtils.isNetworkConnected(ctx)) {
                    webView.reload();
                } else {
                    ToastUtil.alert(ctx, "网络异常，请稍后再试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        });
        refreshLayout.autoRefresh();
    }

	// 关联webview类
	class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

		// 加载结束的时候
		@Override
		public void onPageFinished(WebView view, String url) {
            refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            super.onPageFinished(view, url);
		}



        //接受https请求
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();  //接受所有证书
            super.onReceivedSslError(view, handler, error);
        }

	}

	public boolean onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			return false;
		}
		return true;
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
	public void onClick(View v) {
		Intent intent = new Intent();
        switch (v.getId()) {
       
		case R.id.back_img:
			intent.setAction("com.bcb.secure.main.exit");
			ctx.sendBroadcast(intent);
			break;	
	    }
	}

	
}
