package com.bcb.presentation.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.bcb.R;
import com.bcb.data.SimpleCompanyBean;
import com.bcb.data.util.MyActivityManager;
import com.bcb.presentation.adapter.MyPopupListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/28 21:15
 */
public class A_CompanyName extends Activity_Base implements TextWatcher {
	@BindView(R.id.et_name) EditText et_name;
	@BindView(R.id.lv_pop) ListView lv_pop;

	private List<SimpleCompanyBean> mList;
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
		mList=new ArrayList<SimpleCompanyBean>();
		for (int i = 0 ; i < 100. ; i++) {
			mList.add(new SimpleCompanyBean(i + "包青天", "@163.com"));
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
