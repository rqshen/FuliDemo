package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.loan.PersonInfoBean;
import com.bcb.data.util.LoanPersonalConfigUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.ProgressDialogrUtils;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.EditTextWithDate.EditTextWithDate;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by cain on 16/1/5.
 * 工作信息
 */
public class Activity_LoanRequest_Job extends Activity_Base implements TextWatcher {
	//借款企业类型
	private int LOAN_TYPE;

	@BindView(R.id.loan_office) TextView loan_office;//工作单位全称
	@BindView(R.id.loan_jobs) EditText loan_jobs;//工作职位
	@BindView(R.id.loan_department) EditText loan_department;    //所在部门
	@BindView(R.id.loan_work_experience) EditTextWithDate loan_work_experience;//入职时间
	@BindView(R.id.loan_office_address) EditText loan_office_address;    //办公地点
	@BindView(R.id.loan_earn) EditText loan_earn;    //月均收入
	@BindView(R.id.loan_earn_total) EditText loan_earn_total;    //年税后总收入

	//工作信息
	private PersonInfoBean personInfoBean;
	private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");

	public static void launche(Context ctx, int LOAN_TYPE, String personInfoBean) {
		Intent intent = new Intent(ctx, Activity_LoanRequest_Job.class);
		intent.putExtra("LOAN_TYPE", LOAN_TYPE);
		intent.putExtra("personInfoBean", personInfoBean);
		ctx.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().pushOneActivity(Activity_LoanRequest_Job.this);
		setBaseContentView(R.layout.activity_loanrequest_job);
		ButterKnife.bind(this);

		setLeftTitleVisible(true);
		setTitleValue("工作信息");
		LOAN_TYPE = getIntent().getIntExtra("LOAN_TYPE", 0);
		String personInfoString = getIntent().getStringExtra("personInfoBean");
		LogUtil.i("bqt", "【借款信息】" + personInfoString);
		personInfoBean = new Gson().fromJson(personInfoString, PersonInfoBean.class);

		personInfoBean.Email = "fuck@100cb.com";//★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★

		//设置工作信息
		setupJobMessage();
		loan_work_experience.addTextChangedListener(this);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }

	@Override
	public void afterTextChanged(Editable s) {
		try {
			Date date = dateFormater.parse(s.toString());
			StringBuilder stringBuilder = new StringBuilder();
			Calendar calendar = Calendar.getInstance();
			stringBuilder.append("").append(calendar.get(Calendar.YEAR)).append("-").append(calendar.get(Calendar.MONTH) + 1).append
					("-").append(calendar.get(Calendar.DAY_OF_MONTH)).append("");
			Date today = dateFormater.parse(stringBuilder.toString());
			if (date.after(today)) {
				ToastUtil.alert(Activity_LoanRequest_Job.this, "入职时间不能晚于今天");
				loan_work_experience.setText("");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置工作信息
	 */
	private void setupJobMessage() {
		//工作单位全称
		if (null != personInfoBean && personInfoBean.WorkUnit != null && !personInfoBean.WorkUnit.equalsIgnoreCase("null") &&
				!personInfoBean.WorkUnit.equalsIgnoreCase("")) {
			loan_office.setText(personInfoBean.WorkUnit);
		}
		//工作职位
		if (null != personInfoBean && personInfoBean.Position != null && !personInfoBean.Position.equalsIgnoreCase("null") &&
				!personInfoBean.Position.equalsIgnoreCase("")) {
			loan_jobs.setText(personInfoBean.Position);
		}
		//所在事业部
		if (null != personInfoBean && personInfoBean.Department != null && !personInfoBean.Department.equalsIgnoreCase("null") &&
				!personInfoBean.Department.equalsIgnoreCase("")) {
			loan_department.setText(personInfoBean.Department);
		}
		//入职时间
		if (null != personInfoBean && personInfoBean.EntryDate != null && !personInfoBean.EntryDate.equalsIgnoreCase("null") &&
				!personInfoBean.EntryDate.equalsIgnoreCase("")) {
			try {
				Date date = dateFormater.parse(personInfoBean.EntryDate);
				String dateStr = dateFormater.format(date);
				if (!dateStr.isEmpty()) loan_work_experience.setText(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		//办公地点
		if (null != personInfoBean && personInfoBean.WorkAddress != null && !personInfoBean.WorkAddress.equalsIgnoreCase("null") &&
				!personInfoBean.WorkAddress.equalsIgnoreCase("")) {
			loan_office_address.setText(personInfoBean.WorkAddress);
		}
		//月均收入
		if (null != personInfoBean && personInfoBean.IncomeAmount > 0) {
			loan_earn.setText(String.format("%.2f", personInfoBean.IncomeAmount));
		}
		//年税后总收入
		if (null != personInfoBean && personInfoBean.TotalIncomePerYear > 0) {
			loan_earn_total.setText(String.format("%.2f", personInfoBean.TotalIncomePerYear));
		}
	}

	@OnClick({R.id.rl_company, R.id.loan_office, R.id.company_right})
	public void selectCompanyName() {
		startActivityForResult(new Intent(Activity_LoanRequest_Job.this, A_CompanyName.class),100);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==100&&resultCode==100&&data!=null) {
			loan_office.setText(data.getStringExtra("COMPANY_NAME"));
		}
	}

	//点击下一步按钮
	@OnClick(R.id.job_button)
	public void jobButtonClick() {
		////******************************************************************************************判断是否存在信息为空
		if (loan_office.getText().toString().isEmpty()  //工作单位全称
				|| loan_jobs.getText().toString().isEmpty() //工作职位
				|| loan_department.getText().toString().isEmpty()   //工作部门
				|| loan_work_experience.getText().toString().isEmpty()   //工作时间
				|| loan_office_address.getText().toString().isEmpty()   //办公地址
				|| loan_earn.getText().toString().isEmpty() //月收入
				|| Float.parseFloat(loan_earn.getText().toString()) <= 0 || loan_earn_total.getText().toString().isEmpty()   //年税后总收入
				|| Float.parseFloat(loan_earn_total.getText().toString()) <= 0) { //工作邮箱
			ToastUtil.alert(Activity_LoanRequest_Job.this, "请填写完整的工作信息");
			return;
		}

		////******************************************************************************************暂存数据并跳转至填写资产信息页面
		//工作单位
		personInfoBean.WorkUnit = loan_office.getText().toString();
		//工作职位
		personInfoBean.Position = loan_jobs.getText().toString();
		//所在部门
		personInfoBean.Department = loan_department.getText().toString();
		//工作时间
		personInfoBean.EntryDate = loan_work_experience.getText().toString();
		//工作地点
		personInfoBean.WorkAddress = loan_office_address.getText().toString();
		//月收入
		personInfoBean.IncomeAmount = Float.parseFloat(loan_earn.getText().toString().equalsIgnoreCase("") ? "0" : loan_earn.getText
				().toString());
		//年收入
		personInfoBean.TotalIncomePerYear = Float.parseFloat(loan_earn_total.getText().toString().equalsIgnoreCase("") ? "0" :
				loan_earn_total.getText().toString());

		////******************************************************************************************提交到服务器
		ProgressDialogrUtils.show(this, "正在验证借款信息...");
		new LoanPersonalConfigUtil(this).saveLoanPersonalMessage(new Gson().toJson(personInfoBean));
		try {
			JSONObject jsonObject = new JSONObject(new Gson().toJson(personInfoBean));
			LogUtil.i("bqt", "提交给服务器的数据" + jsonObject.toString());
			BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.PostLoanPersonalMessage, jsonObject, TokenUtil.getEncodeToken
					(this), new BcbRequest.BcbCallBack<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					ProgressDialogrUtils.hide();
					LogUtil.i("bqt", "将个人信息提交给服务器后返回" + response.toString());
					try {
						if (response.getInt("status") == 1) {
							Activity_Tips_FaileOrSuccess.launche(Activity_LoanRequest_Job.this, Activity_Tips_FaileOrSuccess
									.JK_SUCCESS, "");
							finish();
							//清空暂存在本地的数据
							(new LoanPersonalConfigUtil(Activity_LoanRequest_Job.this)).clear();
						} else {
							ToastUtil.alert(Activity_LoanRequest_Job.this, response.getString("message").equalsIgnoreCase("") ?
									"服务器繁忙，请稍候再试" : response.getString("message"));
							//判断是否是Token过期，如果过期则跳转至登陆界面
							if (response.getInt("status") == -5) {
								Activity_Login.launche(Activity_LoanRequest_Job.this);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onErrorResponse(Exception error) {
					ProgressDialogrUtils.hide();
				}
			});
			jsonRequest.setTag(BcbRequestTag.BCB_POST_LOAN_PERSONAL_MESSAGE_REQUEST);
			App.getInstance().getRequestQueue().add(jsonRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}