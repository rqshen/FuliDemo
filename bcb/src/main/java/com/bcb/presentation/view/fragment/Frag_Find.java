package com.bcb.presentation.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.presentation.view.activity.A_Elite_Loan;
import com.bcb.presentation.view.activity.Activity_Login;
import com.bcb.presentation.view.activity.Activity_Love;
import com.bcb.presentation.view.activity.Activity_Open_Account;
import com.bcb.presentation.view.activity.Activity_WebView_Upload;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.bcb.R.id.title_text;

public class Frag_Find extends Frag_Base {
	@BindView(R.id.ll_jk) LinearLayout ll_jk;
	@BindView(R.id.ll_cx) LinearLayout ll_cx;
	@BindView(R.id.ll_lc) LinearLayout ll_lc;
	@BindView(R.id.ll_gjj) LinearLayout ll_gjj;
	@BindView(R.id.ll_zx) LinearLayout ll_zx;
	@BindView(R.id.ll_ja) LinearLayout ll_ja;

	@BindView(R.id.jk) TextView jk;
	@BindView(R.id.cx) TextView cx;
	@BindView(R.id.lc) TextView lc;
	@BindView(R.id.gjj) TextView gjj;
	@BindView(R.id.zx) TextView zx;
	@BindView(R.id.ja) TextView ja;
	private Unbinder unbinder;
	private Context ctx;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_find, container, false);
		unbinder = ButterKnife.bind(this, view);
		((TextView) view.findViewById(title_text)).setText("发现");
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.ctx = view.getContext();
	}

	@OnClick({R.id.ll_jk, R.id.ll_cx, R.id.ll_gjj, R.id.ll_zx, R.id.ll_ja,R.id.ll_lc})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_jk:
				loanMainPage();
				break;
			case R.id.ll_cx:
				cheXian();
				break;
			case R.id.ll_lc:
				Toast.makeText(ctx, "敬请期待", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ll_gjj:
				Toast.makeText(ctx, "敬请期待", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ll_zx:
				Toast.makeText(ctx, "敬请期待", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ll_ja:
				Activity_Love.launche(ctx);
				break;
		}
	}

	//车险
	private void cheXian() {
		if (App.saveUserInfo.getAccess_Token() == null) {
			Activity_Login.launche(ctx);
		} else if (App.mUserDetailInfo == null || TextUtils.isEmpty(App.mUserDetailInfo.CarInsuranceIndexPage)) {
			Toast.makeText(ctx, "网络异常，请刷新后重试", Toast.LENGTH_SHORT).show();
		} else {
			Activity_WebView_Upload.launche(ctx, "车险内购", App.mUserDetailInfo.CarInsuranceIndexPage);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbinder.unbind();
	}

	//借款
	private void loanMainPage() {
		//已开通托管
		if (App.mUserDetailInfo != null && App.mUserDetailInfo.HasOpenCustody) startActivity(new Intent(ctx, A_Elite_Loan.class));
		else startActivity(new Intent(ctx, Activity_Open_Account.class));
	}

}