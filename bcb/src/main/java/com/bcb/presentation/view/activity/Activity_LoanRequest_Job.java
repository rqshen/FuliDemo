package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.loan.PersonInfoBean;
import com.bcb.data.util.LoanPersonalConfigUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.RegexManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.view.custom.EditTextWithDate.EditTextWithDate;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cain on 16/1/5.
 */
public class Activity_LoanRequest_Job extends Activity_Base {

    //工作单位全称
    private EditText loan_office;
    //工作职位
    private EditText loan_jobs;
    //所在部门
    private EditText loan_department;
    //入职时间
    private EditTextWithDate loan_work_experience;
    //办公地点
    private EditText loan_office_address;
    //月均收入
    private EditText loan_earn;
    //年税后总收入
    private EditText loan_earn_total;
    //邮箱地址
    private EditText loan_email;
    //公积金缴存额度
    private EditText loan_accumulation_fund;
    //工作信息
    private PersonInfoBean personInfoBean;
    //下一步按钮
    private Button job_button;
    //请求队列
    private BcbRequestQueue requestQueue;
    //转圈提示
    private ProgressDialog progressDialog;
    private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");//将dateString格式化成 XXX-XX-XX 的形式

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_LoanRequest_Job.this);
        setBaseContentView(R.layout.activity_loanrequest_job);
        setLeftTitleVisible(true);
        setTitleValue("工作信息");
        //初始化界面元素
        initJobs();
        //初始化数据对象
        setupObjectData();
        //设置工作信息
        setupJobMessage();
        //初始化队列
        setupQueue();
    }

    /**
     * 初始化队列
     */
    private void setupQueue() {
        requestQueue = App.getInstance().getRequestQueue();
    }

    /**
     * 初始化界面元素
     */
    private void initJobs() {
        //工作单位全称
        loan_office = (EditText) findViewById(R.id.loan_office);
        //工作职位
        loan_jobs = (EditText) findViewById(R.id.loan_jobs);
        //所在部门
        loan_department = (EditText) findViewById(R.id.loan_department);
        //入职时间
        loan_work_experience = (EditTextWithDate) findViewById(R.id.loan_work_experience);
        loan_work_experience.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Date date = dateFormater.parse(s.toString());
                    StringBuilder stringBuilder = new StringBuilder();
                    Calendar calendar = Calendar.getInstance();
                    stringBuilder.append("").append(calendar.get(Calendar.YEAR)).append("-").append(calendar.get(Calendar.MONTH) + 1).append("-").append(calendar.get(Calendar.DAY_OF_MONTH)).append("");
                    Date today = dateFormater.parse(stringBuilder.toString());
                    if (date.after(today)){
                        ToastUtil.alert(Activity_LoanRequest_Job.this, "入职时间不能晚于今天");
                        loan_work_experience.setText("");
                    }
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }
        });
        //办公地点
        loan_office_address = (EditText) findViewById(R.id.loan_office_address);
        //月均收入
        loan_earn = (EditText) findViewById(R.id.loan_earn);
        //年税后总收入
        loan_earn_total = (EditText) findViewById(R.id.loan_earn_total);
        //邮箱记录
        loan_email = (EditText) findViewById(R.id.loan_email);
        //公积金月缴存额度
        loan_accumulation_fund = (EditText) findViewById(R.id.loan_accumulation_fund);

        //下一步按钮
        job_button = (Button) findViewById(R.id.job_button);
        job_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobButtonClick();
            }
        });
    }

    /**
     * 初始化数据对象
     */
    private void setupObjectData() {
        //如果本地没有数据，则使用前一个页面传过来的数据进行初始化，否则使用本地数据进行初始化
        Gson mGson = new Gson();
        personInfoBean = mGson.fromJson(getIntent().getStringExtra("personInfoBean"), PersonInfoBean.class);
    }

    /**
     * 设置工作信息
     */
    private void setupJobMessage() {
        //工作单位全称
        if (null != personInfoBean && personInfoBean.WorkUnit != null && !personInfoBean.WorkUnit.equalsIgnoreCase("null") && !personInfoBean.WorkUnit.equalsIgnoreCase("")) {
            loan_office.setText(personInfoBean.WorkUnit);
        }
        //工作职位
        if (null != personInfoBean && personInfoBean.Position != null && !personInfoBean.Position.equalsIgnoreCase("null") && !personInfoBean.Position.equalsIgnoreCase("")) {
            loan_jobs.setText(personInfoBean.Position);
        }
        //所在事业部
        if (null != personInfoBean && personInfoBean.Department != null && !personInfoBean.Department.equalsIgnoreCase("null") && !personInfoBean.Department.equalsIgnoreCase("")) {
            loan_department.setText(personInfoBean.Department);
        }
        //入职时间
        if (null != personInfoBean && personInfoBean.EntryDate != null && !personInfoBean.EntryDate.equalsIgnoreCase("null") && !personInfoBean.EntryDate.equalsIgnoreCase("")) {
            try {
                Date date = dateFormater.parse(personInfoBean.EntryDate);
                String dateStr = dateFormater.format(date);
                if (!dateStr.isEmpty()){
                    loan_work_experience.setText(dateStr);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //办公地点
        if (null != personInfoBean && personInfoBean.WorkAddress != null && !personInfoBean.WorkAddress.equalsIgnoreCase("null") && !personInfoBean.WorkAddress.equalsIgnoreCase("")) {
            loan_office_address.setText(personInfoBean.WorkAddress);
        }
        //月均收入
        if (null != personInfoBean && personInfoBean.IncomeAmount > 0) {
            loan_earn.setText(String.format("%.2f", personInfoBean.IncomeAmount));
        }
//        else {
//            loan_earn.setText("0");
//        }
        //年税后总收入
        if (null != personInfoBean && personInfoBean.TotalIncomePerYear > 0) {
            loan_earn_total.setText(String.format("%.2f", personInfoBean.TotalIncomePerYear));
        }
        //邮箱地址
        if (null != personInfoBean && personInfoBean.Email != null && !personInfoBean.Email.equalsIgnoreCase("null") && !personInfoBean.Email.equalsIgnoreCase("")) {
            loan_email.setText(personInfoBean.Email);
        }
        //公积金缴存额度
        if (null != personInfoBean && personInfoBean.FundLimit >= 0) {
            loan_accumulation_fund.setText(String.format("%d", personInfoBean.FundLimit));
        }
//        else {
//            loan_accumulation_fund.setText("0");
//        }
    }

    /****************************** 点击下一步按钮 ***********************************/
    private void jobButtonClick() {
        //判断是否存在信息为空
        if (loan_office.getText().toString().isEmpty()  //工作单位全称
                || loan_jobs.getText().toString().isEmpty() //工作职位
                || loan_department.getText().toString().isEmpty()   //工作部门
                || loan_work_experience.getText().toString().isEmpty()   //工作时间
                || loan_office_address.getText().toString().isEmpty()   //办公地址
                || loan_earn.getText().toString().isEmpty() //月收入
                || Float.parseFloat(loan_earn.getText().toString()) <= 0
                || loan_earn_total.getText().toString().isEmpty()   //年税后总收入
                || Float.parseFloat(loan_earn_total.getText().toString()) <= 0
                || loan_email.getText().toString().isEmpty()) { //工作邮箱
            ToastUtil.alert(Activity_LoanRequest_Job.this, "请填写完整的工作信息");
            return;
        }

        //判断是否是正确的邮箱地址
        if (!RegexManager.isEmail(loan_email.getText().toString())) {
            ToastUtil.alert(Activity_LoanRequest_Job.this, "请填写正确的邮箱地址");
            return;
        }
        //暂存数据并跳转至填写资产信息页面
        saveDateAndGotoAssetPage();
        //提交到服务器
        postDatatoService();
    }


    /**
     * 保存信息并跳转至填写资产信息页面
     */
    private void saveDateAndGotoAssetPage() {

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
        personInfoBean.IncomeAmount = Float.parseFloat(loan_earn.getText().toString().equalsIgnoreCase("") ? "0" : loan_earn.getText().toString());
        //年收入
        personInfoBean.TotalIncomePerYear = Float.parseFloat(loan_earn_total.getText().toString().equalsIgnoreCase("") ? "0" : loan_earn_total.getText().toString());
        //常用邮箱
        personInfoBean.Email = loan_email.getText().toString();
        //公积金
        personInfoBean.FundLimit = Integer.parseInt(loan_accumulation_fund.getText().toString().equalsIgnoreCase("") ? "0" : loan_accumulation_fund.getText().toString());
    }

    /**
     * 将个人信息提交给服务器
     */
    private void postDatatoService() {

        //使用Gson将对象转成JSOnObject对象
        Gson mGson = new Gson();
        (new LoanPersonalConfigUtil(this)).saveLoanPersonalMessage(mGson.toJson(personInfoBean));
        try {
            JSONObject jsonObject = new JSONObject(mGson.toJson(personInfoBean));
            LogUtil.i("bqt", "【Activity_LoanRequest_Job】【postDatatoService】提交给服务器的数据" + jsonObject.toString());

            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.PostLoanPersonalMessage, jsonObject, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    LogUtil.i("bqt", "【Activity_LoanRequest_Job】【onResponse】将个人信息提交给服务器后返回" + response.toString());

                    hideProgressBar();
                    try {
                        if (response.getInt("status") == 1) {
                            //跳转至借款申请成功页面
                            Intent intent = new Intent(Activity_LoanRequest_Job.this, Activity_LoanRequest_Success.class);
                            startActivity(intent);
                            finish();
                            //清空暂存在本地的数据
                            (new LoanPersonalConfigUtil(Activity_LoanRequest_Job.this)).clear();
                        } else {
                            ToastUtil.alert(Activity_LoanRequest_Job.this, response.getString("message").equalsIgnoreCase("") ? "服务器繁忙，请稍候再试" : response.getString("message"));
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
                    hideProgressBar();
                }
            });
            jsonRequest.setTag(BcbRequestTag.BCB_POST_LOAN_PERSONAL_MESSAGE_REQUEST);
            requestQueue.add(jsonRequest);
            showProgressBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 转圈提示
     */
    private void showProgressBar() {
        if(null == progressDialog) {
            progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
        }
        progressDialog.setMessage("正在验证借款信息...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * 隐藏转圈提示
     */
    private void hideProgressBar() {
        if(!isFinishing() && null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.BCB_POST_LOAN_PERSONAL_MESSAGE_REQUEST);
    }
}