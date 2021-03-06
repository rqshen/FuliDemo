package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.data.bean.StringEventBusBean;
import com.bcb.data.bean.loan.LoanKindBean;
import com.bcb.data.bean.loan.PersonInfoBean;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.util.LoanPersonalConfigUtil;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.util.RegexManager;
import com.bcb.util.SpinnerWheelUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cain on 16/1/5.
 * 个人信息
 */
public class Activity_LoanRequest_Person extends Activity_Base implements View.OnClickListener {

    //借款企业类型
    private LoanKindBean bean;

    public static void launche(Context ctx, LoanKindBean bean) {
        Intent intent = new Intent(ctx, Activity_LoanRequest_Person.class);
        intent.putExtra("bean", bean);
        ctx.startActivity(intent);
    }

    //婚姻状况
    private TextView loan_marital_status;
    private List<String> maritalList;
    private int maritalStatus = 1;

    //孩子情况，从0开始算起的
    private TextView loan_children;
    private List<String> childrenList;
    private int childrenStatus = 0;

    //住房状况
    private TextView loan_house_situation;
    private List<String> houseList;
    private int houseStatus = 1;
    //文化程度
    private TextView loan_literacy_level;
    private List<String> literacyList;
    private int literacyStatus = 1;
    //毕业院校
    private EditText loan_graduating_academy;

    //是否购车
    private boolean buyVechileStatus = false;
    //是否购房
    private boolean buyHouseStatus = false;

    //身份证地址
    private EditText loan_identity_address;
    //现居住地址
    private EditText loan_live_address;
    //芝麻信用分
    private EditText loan_sesame_credit;

    //下一步按钮
    private Button person_button;

    //关系
    private List<String> relationList1;
    private List<String> relationList2;
    //紧急联系人1
    private EditText loan_emergency_case;
    //紧急联系人1
    private TextView loan_relationship;
    //紧急联系人1电话
    private EditText loan_emergency_phone;
    ///紧急联系人1 关系
    private String relationStatus1;
    private int relStatus1;
    //紧急联系人2
    private EditText loan_emergency_case_second;
    //紧急联系人2 适配器
    private TextView loan_relationship_second;
    //紧急联系人2电话
    private EditText loan_emergency_phone_second;
    //紧急联系人2 关系
    private String relationStatus2;
    private int relStatus2;
    //转圈提示
    ProgressDialog progressDialog;
    //个人信息
    PersonInfoBean PersonInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        MyActivityManager.getInstance().pushOneActivity(Activity_LoanRequest_Person.this);
        setBaseContentView(R.layout.activity_loanrequest_personal);
        bean = (LoanKindBean) getIntent().getSerializableExtra("bean");
        setLeftTitleVisible(true);
        setTitleValue("个人信息");
        setupPersonalView();
        //请求数据
        getPersonalLoanMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StringEventBusBean event) {
        if (event.getContent().equals("LoanFinish")) {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化个人信息
     */
    private void setupPersonalView() {

        //婚姻/孩子/住房/文化程度点击绑定
        findViewById(R.id.ll_marital).setOnClickListener(this);
        findViewById(R.id.ll_children).setOnClickListener(this);
        findViewById(R.id.ll_house).setOnClickListener(this);
        findViewById(R.id.ll_literacy).setOnClickListener(this);
        //紧急联系人1/2点击绑定
        findViewById(R.id.ll_relationship).setOnClickListener(this);
        findViewById(R.id.ll_relationship_second).setOnClickListener(this);

        //婚姻状况
        loan_marital_status = (TextView) findViewById(R.id.loan_marital_status);
        //孩子情况
        loan_children = (TextView) findViewById(R.id.loan_children);
        //住房状况
        loan_house_situation = (TextView) findViewById(R.id.loan_house_situation);
        //文化程度
        loan_literacy_level = (TextView) findViewById(R.id.loan_literacy_level);
        //毕业院校
        loan_graduating_academy = (EditText) findViewById(R.id.loan_graduating_academy);
        //身份证地址
        loan_identity_address = (EditText) findViewById(R.id.loan_identity_address);
        //现居住地址
        loan_live_address = (EditText) findViewById(R.id.loan_live_address);
        //芝麻信用分
        loan_sesame_credit = (EditText) findViewById(R.id.loan_sesame_credit);

        //紧急联系人1
        loan_emergency_case = (EditText) findViewById(R.id.loan_emergency_case);
        //紧急联系人1关系
        loan_relationship = (TextView) findViewById(R.id.loan_relationship);
        //紧急联系人1电话
        loan_emergency_phone = (EditText) findViewById(R.id.loan_emergency_phone);

        //紧急联系人2
        loan_emergency_case_second = (EditText) findViewById(R.id.loan_emergency_case_second);
        //紧急联系人2关系
        loan_relationship_second = (TextView) findViewById(R.id.loan_relationship_second);
        //紧急联系人2电话
        loan_emergency_phone_second = (EditText) findViewById(R.id.loan_emergency_phone_second);

        //下一步按钮
        person_button = (Button) findViewById(R.id.person_button);
        person_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personButtonClick();
            }
        });
        //初始化个人信息对象
        PersonInfo = new PersonInfoBean();
        PersonInfo.MaritalStatusList = new ArrayList<>();
        PersonInfo.EducationLevelList = new ArrayList<>();
        PersonInfo.HousingStatusList = new ArrayList<>();
        PersonInfo.RelationshipList = new ArrayList<>();
    }

    /**
     * 获取借款的个人信息
     */
    private void getPersonalLoanMessage() {
        showProgressBar();
        //创建请求
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.GetLoanPersonalMessage, null, TokenUtil.getEncodeToken(this), new
                BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("bqt", "借款的【个人】信息" + response.toString());
                        hideProgressBar();
                        try {
                            if (response.getInt("status") == 1) {
                                String result = response.getString("result").replace("\\", "").replace("\"[", "[").replace("]\"", "]");
                                //将JSON转成PersonInfoBean对象，要先判断是否存在这样的数据
                                String localLoanPersonal = new LoanPersonalConfigUtil(Activity_LoanRequest_Person.this)
                                        .getLoanPersonalMessage();
                                if (!TextUtils.isEmpty(localLoanPersonal)) {
                                    PersonInfo = new Gson().fromJson(localLoanPersonal, PersonInfoBean.class);
                                } else {
                                    PersonInfo = new Gson().fromJson(result, PersonInfoBean.class);
                                }
                                //设置婚姻状况
                                changeMaritalStatusList(PersonInfo);
                                //设置孩子状况
                                changeChildrenStatusList(PersonInfo);
                                //设置住房状况
                                changeHousingStatusList(PersonInfo);
                                //设置文化程度
                                changeEducationLevelList(PersonInfo);
                                //设置紧急联系人关系
                                changeRelationshipList(PersonInfo);
                                //设置个人信息
                                setupPersonalData();
                            } else {
                                Toast.makeText(Activity_LoanRequest_Person.this, "服务器返回数据出错了", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.alert(Activity_LoanRequest_Person.this, "数据解析出错");
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                        hideProgressBar();
                    }
                });
        jsonRequest.setTag(BcbRequestTag.BCB_GET_LOAN_PERSONAL_MESSAGE_REQUEST);
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    /**
     * 转圈提示
     */
    private void showProgressBar() {
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        }
        progressDialog.setMessage("正在验证借款信息....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * 隐藏转圈提示
     */
    private void hideProgressBar() {
        if (!isFinishing() && null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 设置婚姻状况列表
     */
    private void changeMaritalStatusList(PersonInfoBean personInfoBean) {
        //数据
        maritalList = new ArrayList<String>();
        for (int i = 0; i < personInfoBean.MaritalStatusList.size(); i++) {
            maritalList.add(i, personInfoBean.MaritalStatusList.get(i).Name);
        }
    }

    /**
     * 设置孩子情况列表
     */
    private void changeChildrenStatusList(PersonInfoBean personInfoBean) {
        childrenList = new ArrayList<String>();
        for (int i = 0; i < personInfoBean.ChildrenStatusList.size(); i++) {
            childrenList.add(i, personInfoBean.ChildrenStatusList.get(i).Name);
        }
    }

    /**
     * 设置住房状况列表
     */
    private void changeHousingStatusList(PersonInfoBean personInfoBean) {
        houseList = new ArrayList<String>();
        for (int i = 0; i < personInfoBean.HousingStatusList.size(); i++) {
            houseList.add(i, personInfoBean.HousingStatusList.get(i).Name);
        }
    }

    /**
     * 设置文化程度列表
     */
    private void changeEducationLevelList(PersonInfoBean personInfoBean) {
        literacyList = new ArrayList<String>();
        for (int i = 0; i < personInfoBean.EducationLevelList.size(); i++) {
            literacyList.add(i, personInfoBean.EducationLevelList.get(i).Name);
        }

    }

    /**
     * 设置关系列表
     */
    private void changeRelationshipList(PersonInfoBean personInfoBean) {
        relationList1 = new ArrayList<>();
        for (int i = 0; i < personInfoBean.RelationshipList.size(); i++) {
            relationList1.add(i, personInfoBean.RelationshipList.get(i).Name);//固定四个
            if (personInfoBean.RelationshipList.get(i).Name.equals(personInfoBean.Relationship1)) {
                relStatus1 = i;
                relationStatus1 = personInfoBean.RelationshipList.get(i).Name;
            }
        }
        relationList2 = new ArrayList<>();
        for (int i = 0; i < personInfoBean.RelationshipList2.size(); i++) {
            relationList2.add(i, personInfoBean.RelationshipList2.get(i).Name);//固定2个
            if (personInfoBean.RelationshipList2.get(i).Name.equals(personInfoBean.Relationship2)) {
                relStatus2 = i;
                relationStatus2 = personInfoBean.RelationshipList2.get(i).Name;
            }
        }
    }

    /**
     * 设置个人信息，需要前面三个列表初始化之后才能调用
     */
    private void setupPersonalData() {
        //婚姻状况
        for (int i = 0; i < PersonInfo.MaritalStatusList.size(); i++) {
            if (PersonInfo.MaritalStatus == PersonInfo.MaritalStatusList.get(i).Value) {
                loan_marital_status.setText(PersonInfo.MaritalStatusList.get(i).Name);
            }
        }
        //孩子情况
        loan_children.setText(childrenList.get(PersonInfo.ChildrenStatus));
        //住房状况
        if (PersonInfo.HousingStatus > 0) {
            loan_house_situation.setText(houseList.get(PersonInfo.HousingStatus - 1));
        }
        //文化程度
        if (PersonInfo.EducationLevel > 0) {
            int tem = 0;
            if (PersonInfo.EducationLevel >= 3) tem = PersonInfo.EducationLevel - 2;
            LogUtil.i("bqt", "文化程度：" + PersonInfo.EducationLevel);

            loan_literacy_level.setText(literacyList.get(tem));
        }
        //毕业院校
        if (PersonInfo.GraduateSchool != null && !PersonInfo.GraduateSchool.equalsIgnoreCase("null") && !PersonInfo.GraduateSchool
                .equalsIgnoreCase("")) {
            loan_graduating_academy.setText(PersonInfo.GraduateSchool);
        }
        //身份证地址
        if (PersonInfo.Hometown != null && !PersonInfo.Hometown.equalsIgnoreCase("null") && !PersonInfo.Hometown.equalsIgnoreCase
                ("")) {
            loan_identity_address.setText(PersonInfo.Hometown);
        }
        //现居住地址
        if (PersonInfo.Residence != null && !PersonInfo.Residence.equalsIgnoreCase("null") && !PersonInfo.Residence.equalsIgnoreCase
                ("")) {
            loan_live_address.setText(PersonInfo.Residence);
        }
        //芝麻信用分
        loan_sesame_credit.setText(PersonInfo.SesameCredit + "");

        //紧急联系人1
        if (PersonInfo.EmergencyContact1 != null && !PersonInfo.EmergencyContact1.equalsIgnoreCase("null") && !PersonInfo
                .EmergencyContact1.equalsIgnoreCase("")) {
            loan_emergency_case.setText(PersonInfo.EmergencyContact1);
        }
        //紧急联系人1，判断是否存在
        if (PersonInfo.Relationship1 != null && !PersonInfo.Relationship1.equalsIgnoreCase("null") && !PersonInfo.Relationship1
                .equalsIgnoreCase("")) {
            for (int i = 0; i < relationList1.size(); i++) {
                if (relationList1.get(i).equalsIgnoreCase(PersonInfo.Relationship1)) {
                    loan_relationship.setText(relationList1.get(i));
                    relStatus1 = i;
                    break;
                }
            }
        }

        //电话
        if (PersonInfo.ContactPhone1 != null && !PersonInfo.ContactPhone1.equalsIgnoreCase("null") && !PersonInfo.ContactPhone1
                .equalsIgnoreCase("")) {
            loan_emergency_phone.setText(PersonInfo.ContactPhone1);
        }

        //紧急联系人2
        if (PersonInfo.EmergencyContact2 != null && !PersonInfo.EmergencyContact2.equalsIgnoreCase("null") && !PersonInfo
                .EmergencyContact2.equalsIgnoreCase("")) {
            loan_emergency_case_second.setText(PersonInfo.EmergencyContact2);
        }

        //紧急联系人2， 判断关系是否存在
        if (PersonInfo.Relationship2 != null && !PersonInfo.Relationship2.equalsIgnoreCase("null") && !PersonInfo.Relationship2
                .equalsIgnoreCase("")) {
            for (int i = 0; i < relationList2.size(); i++) {
                if (relationList2.get(i).equalsIgnoreCase(PersonInfo.Relationship2)) {
                    loan_relationship_second.setText(relationList2.get(i));
                    relStatus2 = i;
                    break;
                }
            }
            LogUtil.i("bqt", relStatus2 + "--紧急联系人2--" + loan_relationship_second.getText());
        }

        //电话
        if (PersonInfo.ContactPhone2 != null && !PersonInfo.ContactPhone2.equalsIgnoreCase("null") && !PersonInfo.ContactPhone2
                .equalsIgnoreCase("")) {
            loan_emergency_phone_second.setText(PersonInfo.ContactPhone2);
        }
    }

    /**
     * 点击下一步按钮
     */
    private void personButtonClick() {
        //判断是否都填写了信息
        if (loan_identity_address.getText().toString().equalsIgnoreCase("") || loan_live_address.getText().toString()
                .equalsIgnoreCase("") || loan_emergency_case.getText().toString().equalsIgnoreCase("") || loan_emergency_phone
                .getText().toString().equalsIgnoreCase("")) {
            ToastUtil.alert(Activity_LoanRequest_Person.this, "请填写完整的个人信息");
            return;
        }
        //判断是否是手机号码
        if (!RegexManager.isPhoneNum(loan_emergency_phone.getText().toString())) {
            //判断紧急联系人2不为空的时候
            if (!loan_emergency_phone_second.getText().toString().equalsIgnoreCase("") && !RegexManager.isPhoneNum
                    (loan_emergency_phone_second.getText().toString())) {
                ToastUtil.alert(Activity_LoanRequest_Person.this, "请填写正确的手机号码");
                return;
            } else {
                ToastUtil.alert(Activity_LoanRequest_Person.this, "请填写正确的手机号码");
                return;
            }

        }

        //判断紧急联系人是否跟用户姓名一样
        if (loan_emergency_case.getText().toString().equalsIgnoreCase(MyApplication.mUserDetailInfo.getRealName()) ||
                loan_emergency_case_second.getText().toString().equalsIgnoreCase(MyApplication.mUserDetailInfo.getRealName())) {
            ToastUtil.alert(Activity_LoanRequest_Person.this, "紧急联系人不能与本人相同");
            return;
        }

        //判断紧急联系人是否相同
        if (loan_emergency_case.getText().toString().equalsIgnoreCase(loan_emergency_case_second.getText().toString())) {
            ToastUtil.alert(Activity_LoanRequest_Person.this, "紧急联系人不能相同");
            return;
        }
        //判断紧急联系人电话是否相同
        if (loan_emergency_phone.getText().toString().equalsIgnoreCase(loan_emergency_phone_second.getText().toString())) {
            ToastUtil.alert(Activity_LoanRequest_Person.this, "紧急联系人联系方式不能相同");
            return;
        }
        //保存信息到本地，然后跳转至填写工作信息页面
        saveDataAndGotoJobPage();
    }

    /**
     * 保存到本地数据库，然后跳转至填写工作信息页面
     */
    private void saveDataAndGotoJobPage() {
        //婚姻状况
        PersonInfo.MaritalStatus = maritalStatus;
        //孩子情况
        PersonInfo.ChildrenStatus = childrenStatus;
        //住房状况
        PersonInfo.HousingStatus = houseStatus;
        //文化程度
        PersonInfo.EducationLevel = literacyStatus;
        //毕业院校
        PersonInfo.GraduateSchool = loan_graduating_academy.getText().toString();
        //身份证地址
        PersonInfo.Hometown = loan_identity_address.getText().toString();//loan_live_address.getText().toString();//
        //现居住地址
        PersonInfo.Residence = loan_live_address.getText().toString();//loan_identity_address.getText().toString();//
        //芝麻信用分
        PersonInfo.SesameCredit = Integer.parseInt(loan_sesame_credit.getText().toString().isEmpty() ? "0" : loan_sesame_credit
                .getText().toString());
        //紧急联系人1
        PersonInfo.EmergencyContact1 = loan_emergency_case.getText().toString();
        //紧急联系人1 关系
        PersonInfo.Relationship1 = relationStatus1;
        //紧急联系人1 电话
        PersonInfo.ContactPhone1 = loan_emergency_phone.getText().toString();
        //紧急联系人2
        PersonInfo.EmergencyContact2 = loan_emergency_case_second.getText().toString();
        //紧急联系人2 关系
        PersonInfo.Relationship2 = relationStatus2;
        LogUtil.i("bqt", "关系2--" + relationStatus2);

        //紧急联系人2 电话
        PersonInfo.ContactPhone2 = loan_emergency_phone_second.getText().toString();
        //将个人信息缓存在本地
        new LoanPersonalConfigUtil(this).saveLoanPersonalMessage(new Gson().toJson(PersonInfo));
        LogUtil.i("bqt", "关系2--" + new Gson().toJson(PersonInfo));
        //跳转至工作信息页面
        Activity_LoanRequest_Job.launche(Activity_LoanRequest_Person.this, bean, new Gson().toJson(PersonInfo));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_children://孩子情况
                String[] arr1 = childrenList.toArray(new String[childrenList.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr1, childrenStatus, new SpinnerWheelUtil
                        .OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        childrenStatus = currentItem;
                        loan_children.setText(childrenList.get(currentItem));
                    }
                });
                break;
            case R.id.ll_house://住房情况
                String[] arr2 = houseList.toArray(new String[houseList.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr2, houseStatus - 1, new SpinnerWheelUtil
                        .OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        houseStatus = currentItem + 1;
                        loan_house_situation.setText(houseList.get(currentItem));
                    }
                });
                break;
            case R.id.ll_marital://婚姻状况
                String[] arr3 = maritalList.toArray(new String[maritalList.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr3, maritalStatus - 1, new SpinnerWheelUtil
                        .OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        switch (currentItem) {
                            case 0:
                                maritalStatus = 1;
                                break;
                            case 1:
                                maritalStatus = 3;
                                break;
                            case 2:
                                maritalStatus = 4;
                                break;
                            case 3:
                                maritalStatus = 5;
                                break;
                            default:
                                maritalStatus = 1;
                                break;
                        }

                        loan_marital_status.setText(maritalList.get(currentItem));
                    }
                });
                break;
            case R.id.ll_literacy://文化程度
                String[] arr4 = literacyList.toArray(new String[literacyList.size()]);

                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr4, literacyStatus - 1, new SpinnerWheelUtil
                        .OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        if (currentItem == 0) {
                            literacyStatus = 1;
                        } else literacyStatus = currentItem + 2;
                        loan_literacy_level.setText(literacyList.get(currentItem));
                        LogUtil.i("bqt", "新的文化程度：" + literacyStatus);
                    }

                });
                break;

            case R.id.ll_relationship://紧急联系人1
                String[] arr5 = relationList1.toArray(new String[relationList1.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr5, relStatus1, new SpinnerWheelUtil
                        .OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        relStatus1 = currentItem;
                        relationStatus1 = relationList1.get(currentItem);
                        loan_relationship.setText(relationList1.get(currentItem));
                    }
                });
                break;
            case R.id.ll_relationship_second://紧急联系人2
                String[] arr6 = relationList2.toArray(new String[relationList2.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr6, relStatus2, new SpinnerWheelUtil
                        .OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        relStatus2 = currentItem;
                        relationStatus2 = relationList2.get(currentItem);
                        loan_relationship_second.setText(relationList2.get(currentItem));
                    }
                });
                break;
        }
    }
}