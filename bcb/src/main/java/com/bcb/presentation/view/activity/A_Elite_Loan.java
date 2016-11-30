package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.loan.LoanKindBean;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ProgressDialogrUtils;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.AlertView.DialogBQT;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 精英贷
 * 作者：baicaibang
 * 时间：2016/11/28 10:17
 */
public class A_Elite_Loan extends Activity_Base {
	@BindView(R.id.iv_brand) ImageView iv_brand;
	@BindView(R.id.iv_it) ImageView iv_it;
	@BindView(R.id.iv_signed) ImageView iv_signed;
	@BindView(R.id.root) LinearLayout root;

	//支持的企业类型集合
	private ArrayList<LoanKindBean> list;
	private LoanKindBean bean1, bean2, bean0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.activity_elite_loan);
		ButterKnife.bind(this);
		setLeftTitleVisible(true);
		setTitleValue("精英贷");
		setRightTitleValue("我的借款", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(A_Elite_Loan.this, Activity_LoanList.class));
			}
		});
		requestOpenAccount();
	}
	
	@OnClick({R.id.iv_brand, R.id.iv_it, R.id.iv_signed})
	public void onClickIv(View v) {
		//存在已申请的贷款，弹窗提示
		if (new Random().nextBoolean()) showDialog();//***********************************************************************
		else if (bean0 != null && bean1 != null && bean2 != null) {
			switch (v.getId()) {
				case R.id.iv_brand:
					Activity_LoanRequest_Borrow.launche(A_Elite_Loan.this, bean1);
					break;
				case R.id.iv_it:
					Activity_LoanRequest_Borrow.launche(A_Elite_Loan.this, bean2);
					break;
				case R.id.iv_signed:
					Activity_LoanRequest_Borrow.launche(A_Elite_Loan.this, bean0);
					break;
			}
		}
	}
	
	private void showDialog() {
		DialogBQT diaolog = new DialogBQT(this) {
			@Override
			public void onSureClick(View v) {
				super.onSureClick(v);
				startActivity(new Intent(A_Elite_Loan.this, Activity_LoanList.class));
			}
		};
		diaolog.setTitleAndMessageAndIcon(null, "您已申请了我们公司的aaa，\n正在处理中，请勿重复申请。", R.drawable.icon_email);
		diaolog.setButtonText("查看我的借款", "返回", -1, 0xff999999);
		diaolog.show();
	}

	/**
	 * 借款首页
	 */
	private void requestOpenAccount() {
		ProgressDialogrUtils.show(this,"正在请求数据，请稍后…");
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.LOANKINDLIST, null, TokenUtil.getEncodeToken(this), true, new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogrUtils.hide();
				LogUtil.i("bqt", " 借款首页" + response.toString());
				if (PackageUtil.getRequestStatus(response, A_Elite_Loan.this)) {
					try {
						JSONObject result = PackageUtil.getResultObject(response);
						if (result != null) {
							JSONArray jsonArray = result.getJSONArray("LoanKindList");
							if (jsonArray != null) {
								list = App.mGson.fromJson(jsonArray.toString(), //
										new TypeToken<ArrayList<LoanKindBean>>() {}.getType());
								if (list != null && list.size() >= 3) {
									for (int i = 0 ; i < list.size() ; i++) {
										switch (list.get(i).LoanKindId) {
											case 0:
												bean0 = list.get(i);
												break;
											case 1:
												bean1 = list.get(i);
												break;
											case 2:
												bean2 = list.get(i);
												break;
										}
									}
								}
								root.setVisibility(View.VISIBLE);
							}
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "借款首页" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(A_Elite_Loan.this, response.optString("message"), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				ProgressDialogrUtils.hide();
				LogUtil.d("bqt", "借款首页" + error.toString());
			}
		});
		App.getInstance().getRequestQueue().add(jsonRequest);
	}
}