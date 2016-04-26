package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.bcb.R;
import com.bcb.data.bean.loan.PersonInfoBean;
import com.bcb.data.util.LoanPersonalConfigUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.RegexManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.presentation.view.custom.EditTextWithDate.EditTextWithDate;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cain on 16/1/5.
 */
public class Activity_LoanRequest_Job extends Activity_Base {

    //工作单位全称
    private EditText loan_office;
    //工作职位
    private EditText loan_jobs;
    //是否管理岗
    private RadioGroup loan_management;
    private RadioButton radioFalse, radioTrue;
    private boolean managementStatus = false;
    //所在事业部
    private EditText loan_department;
    //入职时间
    private EditTextWithDate loan_entry_date;
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
    }

    /**
     * 初始化界面元素
     */
    private void initJobs() {
        //工作单位全称
        loan_office = (EditText) findViewById(R.id.loan_office);
        //工作职位
        loan_jobs = (EditText) findViewById(R.id.loan_jobs);
        //是否管理岗位
        loan_management = (RadioGroup) findViewById(R.id.loan_management);
        radioFalse = (RadioButton) findViewById(R.id.radio_false);
        radioTrue = (RadioButton) findViewById(R.id.radio_true);
        loan_management.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == R.id.radio_false) {
                    managementStatus = false;
                } else {
                    managementStatus = true;
                }
            }
        });
        //所在事业部
        loan_department = (EditText) findViewById(R.id.loan_department);
        //入职时间
        loan_entry_date = (EditTextWithDate) findViewById(R.id.loan_entry_date);
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
        if (personInfoBean.WorkUnit != null && !personInfoBean.WorkUnit.equalsIgnoreCase("null") && !personInfoBean.WorkUnit.equalsIgnoreCase("")) {
            loan_office.setText(personInfoBean.WorkUnit);
        }
        //工作职位
        if (personInfoBean.Position != null && !personInfoBean.Position.equalsIgnoreCase("null") && !personInfoBean.Position.equalsIgnoreCase("")) {
            loan_jobs.setText(personInfoBean.Position);
        }
        //是否管理岗位
        managementStatus = personInfoBean.IsManagePost;
        if (personInfoBean.IsManagePost) {
            radioFalse.setChecked(false);
            radioTrue.setChecked(true);
        } else {
            radioFalse.setChecked(true);
            radioTrue.setChecked(false);
        }
        //所在事业部
        if (personInfoBean.Department != null && !personInfoBean.Department.equalsIgnoreCase("null") && !personInfoBean.Department.equalsIgnoreCase("")) {
            loan_department.setText(personInfoBean.Department);
        }
        //入职时间
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");//将dateString格式化成 XXX-XX-XX 的形式
        Date date = new Date();
        try {
            //判断入职时间是否存在
            if (personInfoBean.EntryDate != null) {
                date = dateFormater.parse(personInfoBean.EntryDate);
                String dateStr = dateFormater.format(date);
                if (!dateStr.isEmpty()){
                    loan_entry_date.setText(dateStr);
                }
            }
        } catch (ParseException e) {

        }
        //办公地点
        if (personInfoBean.WorkAddress != null && !personInfoBean.WorkAddress.equalsIgnoreCase("null") && !personInfoBean.WorkAddress.equalsIgnoreCase("")) {
            loan_office_address.setText(personInfoBean.WorkAddress);
        }
        //月均收入
        if (personInfoBean.IncomeAmount > 0) {
            loan_earn.setText(String.format("%.2f", personInfoBean.IncomeAmount));
        } else {
            loan_earn.setText("0");
        }
        //年税后总收入
        if (personInfoBean.TotalIncomePerYear > 0) {
            loan_earn_total.setText(String.format("%.2f", personInfoBean.TotalIncomePerYear));
        }
        //邮箱地址
        if (personInfoBean.Email != null && !personInfoBean.Email.equalsIgnoreCase("null") && !personInfoBean.Email.equalsIgnoreCase("")) {
            loan_email.setText(personInfoBean.Email);
        }
        //公积金缴存额度
        if (personInfoBean.FundLimit >= 0) {
            loan_accumulation_fund.setText(String.format("%d", personInfoBean.FundLimit));
        } else {
            loan_accumulation_fund.setText("0");
        }
    }

    /****************************** 点击下一步按钮 ***********************************/
    private void jobButtonClick() {
        //判断是否存在信息为空
        if (loan_office.getText().toString().isEmpty()  //工作单位全称
                || loan_jobs.getText().toString().isEmpty() //工作职位
                || loan_department.getText().toString().isEmpty()   //工作部门
                || loan_entry_date.getText().toString().isEmpty()   //入职时间
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
    }


    /**
     * 保存信息并跳转至填写资产信息页面
     */
    private void saveDateAndGotoAssetPage() {

        //工作单位
        personInfoBean.WorkUnit = loan_office.getText().toString();
        //工作职位
        personInfoBean.Position = loan_jobs.getText().toString();
        //管理岗位
        personInfoBean.IsManagePost = managementStatus;
        //所在部门
        personInfoBean.Department = loan_department.getText().toString();
        //入职时间
        personInfoBean.EntryDate = loan_entry_date.getText().toString();
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
        //暂存数据到本地
        Gson mGson = new Gson();
        (new LoanPersonalConfigUtil(this)).saveLoanPersonalMessage(mGson.toJson(personInfoBean));
        //跳转至资产页面
        Intent intent = new Intent(Activity_LoanRequest_Job.this, Activity_LoanRequest_Asset.class);
        intent.putExtra("personInfoBean", mGson.toJson(personInfoBean).toString());
        startActivity(intent);
    }
}