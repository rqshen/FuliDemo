package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsTwo;
import com.bcb.data.JEnterprise;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.util.PackageUtil;
import com.bcb.util.ProgressDialogrUtils;
import com.bcb.util.TokenUtil;
import com.bcb.presentation.adapter.MyPopupListAdapter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/28 21:15
 */
public class A_CompanyName extends Activity_Base implements TextWatcher {
	@BindView(R.id.et_name) EditText et_name;
	@BindView(R.id.lv_pop) ListView lv_pop;
	@BindView(R.id.job_button) Button job_button;
	@BindView(R.id.tv_top) TextView tv_top;
	@BindView(R.id.tv_bottom) TextView tv_bottom;

	private JEnterprise jBean;//选择的企业
	private JEnterprise.EnterpriseListBean selectBean;//企业列表
	private List<JEnterprise.EnterpriseListBean> mList = new ArrayList<JEnterprise.EnterpriseListBean>();//匹配的企业
	private MyPopupListAdapter popupListAdapter;//适配器

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().pushOneActivity(A_CompanyName.this);
		setBaseContentView(R.layout.layout_pop);
		findViewById(R.id.content).setBackgroundColor(0xffffffff);
		 findViewById(R.id.rl_base_root).setBackgroundColor(0xffffffff);
		ButterKnife.bind(this);

		setLeftTitleVisible(true);
		setTitleValue("工作单位全称");

		et_name.addTextChangedListener(this);
		popupListAdapter = new MyPopupListAdapter(this, mList);
		lv_pop.setAdapter(popupListAdapter);
		initData();
	}

	private void initData() {
		//本地数据
		String data = getData();
		if (data != null && data != "") {
			jBean = new Gson().fromJson(data, JEnterprise.class);
			//本地数据不为空，拿本地的版本请求
			if (jBean != null) {
				requestData(jBean.Version);
				return;
			}
		}
		//拿初始版本请求
		requestData(0);
	}

	@OnItemClick(R.id.lv_pop)
	public void onItemClick(int position) {//though there are 4 parameters, you can just write the one you want to use
		selectBean = mList.get(position);
		et_name.setText(selectBean.Name);
	}

	//点击确定后返回
	@OnClick(R.id.job_button)
	public void onButtonClick() { //the method should not be declared private or static
		String companyNam = et_name.getText().toString().trim();
		if (!TextUtils.isEmpty(companyNam)) {
			Intent intent = new Intent();
			intent.putExtra("COMPANY_NAME", companyNam);
			if (selectBean != null) saveEmail();
			else clearEmail();
			setResult(100, intent);
			finish();
		} else Toast.makeText(A_CompanyName.this, "公司名称不能为空", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void afterTextChanged(Editable s) {
		//如果选中后改变了内容，则清空已选中的公司
		if (selectBean != null && !et_name.getText().toString().trim().equals(selectBean.Name)) {
			selectBean = null;
			LogUtil.i("bqt", "将selectBean置为空");
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mList.clear();
		if (!TextUtils.isEmpty(s) && s.length() >= 2 && jBean != null && jBean.EnterpriseList != null) {
			for (int i = 0 ; i < jBean.EnterpriseList.size() ; i++) {
				if (jBean.EnterpriseList.get(i).Name.contains(s) || isMatchCount(jBean.EnterpriseList.get(i).Name, s.toString())) {
					mList.add(jBean.EnterpriseList.get(i));
				}
			}
		}
		popupListAdapter.notifyDataSetChanged();
		if (mList.size() > 0) {
			tv_top.setVisibility(View.GONE);
			tv_bottom.setVisibility(View.GONE);
		} else {
			tv_top.setVisibility(View.VISIBLE);
			tv_bottom.setVisibility(View.VISIBLE);
		}
	}

	private boolean isMatchCount(String name, String s) {
		Set<String> set = new HashSet<>();
		for (int i = 0 ; i < s.length() ; i++) {
			set.add(String.valueOf(s.charAt(i)));
		}
		int count = 0;
		for (String string : set) {//增强for遍历key
			if (name.contains(string)) count++;
		}
		if (count >= 5) return true;
		else return false;
	}

	/**
	 * 企业列表
	 */
	private void requestData(int version) {
		ProgressDialogrUtils.show(this, "正在请求数据，请稍后…");
		LogUtil.i("bqt", "【本地版本】" + version);
		JSONObject obj = new org.json.JSONObject();
		try {
			obj.put("Version", version);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.ENTERPRISELIST, obj, TokenUtil.getEncodeToken(this), true, new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				ProgressDialogrUtils.hide();
				LogUtil.i("bqt", " 企业列表" + response.toString());
				if (PackageUtil.getRequestStatus(response, A_CompanyName.this)) {
					try {
						JSONObject result = PackageUtil.getResultObject(response);
						//服务器没有返回列表时不做任何操作
						if (result != null && result.getJSONArray("EnterpriseList") != null) {
							//服务器返回列表时，覆盖掉旧的数据
							jBean = new Gson().fromJson(result.toString(), JEnterprise.class);
							if (jBean != null && jBean.EnterpriseList != null && jBean.EnterpriseList.size() > 0) saveData(jBean);
						}
					} catch (Exception e) {
						LogUtil.d("bqt", "企业列表" + e.getMessage());
					}
				} else if (response != null) {
					Toast.makeText(A_CompanyName.this, response.optString("message"), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				ProgressDialogrUtils.hide();
				LogUtil.d("bqt", "企业列表" + error.toString());
			}
		});
		MyApplication.getInstance().getRequestQueue().add(jsonRequest);
	}

	private void saveEmail() {
		SharedPreferences.Editor editor = getSharedPreferences("email", Context.MODE_PRIVATE).edit();
		editor.putString("email", selectBean.EmailSuffix);
		editor.commit();
	}

	private void clearEmail() {
		LogUtil.i("bqt", "清空email");
		SharedPreferences.Editor editor = getSharedPreferences("email", Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	private void saveData(JEnterprise jBean) {
		SharedPreferences sp = this.getSharedPreferences("JEnterprise", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("JEnterprise", new Gson().toJson(jBean));
		editor.commit();
	}

	private String getData() {
		SharedPreferences sp = this.getSharedPreferences("JEnterprise", Context.MODE_PRIVATE);
		String data = sp.getString("JEnterprise", "");
		LogUtil.i("bqt", "【获取保存的数据】" + data);
		return data;
	}
}