package com.bcb.presentation.view.activity;

import android.content.Intent;
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
import com.bcb.data.SimpleCompanyBean;
import com.bcb.data.util.MyActivityManager;
import com.bcb.presentation.adapter.MyPopupListAdapter;

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

	private List<SimpleCompanyBean> mList;
	private List<SimpleCompanyBean> mListAll;
	private MyPopupListAdapter popupListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().pushOneActivity(A_CompanyName.this);
		setBaseContentView(R.layout.layout_pop);
		ButterKnife.bind(this);

		setLeftTitleVisible(true);
		setTitleValue("工作单位全称");

		et_name.addTextChangedListener(this);
		initList();
		popupListAdapter = new MyPopupListAdapter(this, mList);
		lv_pop.setAdapter(popupListAdapter);
	}

	private void initList() {
		mList = new ArrayList<SimpleCompanyBean>();
		mListAll = new ArrayList<SimpleCompanyBean>();
		for (int i = 0 ; i < 100. ; i++) {
			mListAll.add(new SimpleCompanyBean(i + "包青天", "@163.com"));
			mListAll.add(new SimpleCompanyBean(i + "超过2个字符才开始检索", "@163.com"));
			mListAll.add(new SimpleCompanyBean(i + "全部匹配，或匹配输入内容的5个字符，都算成功", "@163.com"));
		}
	}

	@OnItemClick(R.id.lv_pop)
	void onItemClick(int position) {//though there are 4 parameters, you can just write the one you want to use
		et_name.setText(mList.get(position).name);
	}

	@OnClick(R.id.job_button)
	void onButtonClick() { //the method should not be declared private or static
		String companyNam = et_name.getText().toString().trim();
		if (!TextUtils.isEmpty(companyNam)) {
			Intent intent = new Intent();
			intent.putExtra("COMPANY_NAME", companyNam);
			setResult(100, intent);
			finish();
		} else Toast.makeText(A_CompanyName.this, "公司名称不能为空", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void afterTextChanged(Editable s) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mList.clear();
		if (!TextUtils.isEmpty(s) && s.length() >= 2) {
			for (int i = 0 ; i < mListAll.size() ; i++) {
				if (mListAll.get(i).name.contains(s) || isMatchCount(mListAll.get(i).name, s.toString())) {
					mList.add(mListAll.get(i));
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
}
