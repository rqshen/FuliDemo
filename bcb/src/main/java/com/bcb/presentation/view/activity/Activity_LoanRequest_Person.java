package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.loan.PersonInfoBean;
import com.bcb.data.util.LoanPersonalConfigUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.RegexManager;
import com.bcb.data.util.SpinnerWheelUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cain on 16/1/5.
 */
public class Activity_LoanRequest_Person extends Activity_Base implements View.OnClickListener{

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
    private String relationStatus1 = "";
    private int relStatus1;
    //紧急联系人2
    private EditText loan_emergency_case_second;
    //紧急联系人2 适配器
    private TextView loan_relationship_second;
    //紧急联系人2电话
    private EditText loan_emergency_phone_second;
    //紧急联系人2 关系
    private String relationStatus2 = "";
    private int relStatus2;
    //转圈提示
    ProgressDialog progressDialog;
    //个人信息
    PersonInfoBean PersonInfo;
    //请求队列
    private BcbRequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_LoanRequest_Person.this);
        setBaseContentView(R.layout.activity_loanrequest_personal);
        setLeftTitleVisible(true);
        setTitleValue("个人信息");
        setupPersonalView();
        //请求数据
        getPersonalLoanMessage();
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
        //创建队列
        requestQueue = BcbNetworkManager.newRequestQueue(this);
        //创建请求
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.GetLoanPersonalMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                if(null == response) {
                    ToastUtil.alert(Activity_LoanRequest_Person.this, "服务器返回数据出错");
                    return;
                }
                try {
                    if (response.getInt("status") == 1) {
                        String result = response.getString("result").replace("\\", "").replace("\"[", "[").replace("]\"", "]");
                        //将JSON转成PersonInfoBean对象，要先判断是否存在这样的数据
                        Gson mGson = new Gson();
                        String localLoanPersonal = new LoanPersonalConfigUtil(Activity_LoanRequest_Person.this).getLoanPersonalMessage();
                        if (!TextUtils.isEmpty(localLoanPersonal)){
                            PersonInfo = mGson.fromJson(localLoanPersonal, PersonInfoBean.class);
                        }else {
                            PersonInfo = mGson.fromJson(result, PersonInfoBean.class);
                        }
                        /////////////此处代码是为了兼容旧版本，否则会出现空指针异常
                        if (null == PersonInfo.Relationship2){
                            PersonInfoBean temp = mGson.fromJson(result, PersonInfoBean.class);
                            PersonInfo.Relationship2 = temp.Relationship2;
                        }
                        /////////////
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
                        //Token过期，需要重登陆
                        if (response.getInt("status") == -5) {

                        } else {

                        }
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
        requestQueue.add(jsonRequest);
    }

    /**
     * 转圈提示
     */
    private void showProgressBar() {
        if(null == progressDialog) {
            progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
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
        if(null != progressDialog && progressDialog.isShowing()){
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
            relationList1.add(i, personInfoBean.RelationshipList.get(i).Name);
        }
        relationStatus1 = relationList1.get(0);
        relationList2 = new ArrayList<>();
        for (int i = 0; i < personInfoBean.RelationshipList2.size(); i++) {
            relationList2.add(i, personInfoBean.RelationshipList2.get(i).Name);
        }
        relationStatus2 = relationList2.get(0);
    }

    /**
     * 设置个人信息，需要前面三个列表初始化之后才能调用
     */
    private void  setupPersonalData() {
        //婚姻状况
        if (PersonInfo.MaritalStatus > 0) {
            loan_marital_status.setText(maritalList.get(PersonInfo.MaritalStatus - 1));
        } else {
            loan_marital_status.setText(maritalList.get(0));
        }
        //孩子情况
        loan_children.setText(childrenList.get(PersonInfo.ChildrenStatus));
        //住房状况
        if (PersonInfo.HousingStatus > 0){
            loan_house_situation.setText(houseList.get(PersonInfo.HousingStatus - 1));
        } else {
            loan_house_situation.setText(houseList.get(0));
        }

        //文化程度
        if (PersonInfo.EducationLevel > 0) {
            loan_literacy_level.setText(literacyList.get(PersonInfo.EducationLevel - 1));
        } else {
            loan_literacy_level.setText(literacyList.get(0));
        }
        //毕业院校
        if (PersonInfo.GraduateSchool != null && !PersonInfo.GraduateSchool.equalsIgnoreCase("null") && !PersonInfo.GraduateSchool.equalsIgnoreCase("")) {
            loan_graduating_academy.setText(PersonInfo.GraduateSchool);
        }
        //身份证地址
        if (PersonInfo.Hometown != null && !PersonInfo.Hometown.equalsIgnoreCase("null") && !PersonInfo.Hometown.equalsIgnoreCase("")) {
            loan_identity_address.setText(PersonInfo.Hometown);
        }
        //现居住地址
        if (PersonInfo.Residence != null && !PersonInfo.Residence.equalsIgnoreCase("null") && !PersonInfo.Residence.equalsIgnoreCase("")) {
            loan_live_address.setText(PersonInfo.Residence);
        }
        //芝麻信用分
        loan_sesame_credit.setText(PersonInfo.SesameCredit + "");

        //紧急联系人1
        if (PersonInfo.EmergencyContact1 != null && !PersonInfo.EmergencyContact1.equalsIgnoreCase("null") && !PersonInfo.EmergencyContact1.equalsIgnoreCase("")) {
            loan_emergency_case.setText(PersonInfo.EmergencyContact1);
        }
        //紧急联系人1，判断是否存在
        if (PersonInfo.Relationship1 != null && !PersonInfo.Relationship1.equalsIgnoreCase("null") && !PersonInfo.Relationship1.equalsIgnoreCase("")) {
            for (int i = 0; i < relationList1.size(); i++) {
                if (relationList1.get(i).equalsIgnoreCase(PersonInfo.Relationship1)) {
                    loan_relationship.setText(relationList1.get(i));
                    relStatus1 = i;
                    break;
                }
            }
        }

        //电话
        if (PersonInfo.ContactPhone1 != null && !PersonInfo.ContactPhone1.equalsIgnoreCase("null") && !PersonInfo.ContactPhone1.equalsIgnoreCase("")) {
            loan_emergency_phone.setText(PersonInfo.ContactPhone1);
        }

        //紧急联系人2
        if (PersonInfo.EmergencyContact2 != null && !PersonInfo.EmergencyContact2.equalsIgnoreCase("null") && !PersonInfo.EmergencyContact2.equalsIgnoreCase("")) {
            loan_emergency_case_second.setText(PersonInfo.EmergencyContact2);
        }

        //紧急联系人2， 判断关系是否存在
        if (PersonInfo.Relationship2 != null && !PersonInfo.Relationship2.equalsIgnoreCase("null") && !PersonInfo.Relationship2.equalsIgnoreCase("")) {
            for (int i = 0; i < relationList2.size(); i++) {
                if (relationList2.get(i).equalsIgnoreCase(PersonInfo.Relationship2)) {
                    loan_relationship_second.setText(relationList2.get(i));
                    relStatus2 = i;
                    break;
                }
            }
        }

        //电话
        if (PersonInfo.ContactPhone2 != null && !PersonInfo.ContactPhone2.equalsIgnoreCase("null") && !PersonInfo.ContactPhone2.equalsIgnoreCase("")) {
            loan_emergency_phone_second.setText(PersonInfo.ContactPhone2);
        }
    }

    /**
     * 点击下一步按钮
     */
    private void personButtonClick() {
        //判断是否都填写了信息
        if (loan_identity_address.getText().toString().equalsIgnoreCase("")
                || loan_live_address.getText().toString().equalsIgnoreCase("")
                || loan_emergency_case.getText().toString().equalsIgnoreCase("")
                || loan_emergency_phone.getText().toString().equalsIgnoreCase("")) {
            ToastUtil.alert(Activity_LoanRequest_Person.this, "请填写完整的个人信息");
            return;
        }
        //判断是否是手机号码
        if (!RegexManager.isPhoneNum(loan_emergency_phone.getText().toString())) {
            //判断紧急联系人2不为空的时候
            if (!loan_emergency_phone_second.getText().toString().equalsIgnoreCase("") && !RegexManager.isPhoneNum(loan_emergency_phone_second.getText().toString())) {
                ToastUtil.alert(Activity_LoanRequest_Person.this, "请填写正确的手机号码");
                return;
            } else {
                ToastUtil.alert(Activity_LoanRequest_Person.this, "请填写正确的手机号码");
                return;
            }

        }

        //判断紧急联系人是否跟用户姓名一样
        if (loan_emergency_case.getText().toString().equalsIgnoreCase(App.mUserDetailInfo.getRealName())
                || loan_emergency_case_second.getText().toString().equalsIgnoreCase(App.mUserDetailInfo.getRealName())) {
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
        PersonInfo.Hometown = loan_identity_address.getText().toString();
        //现居住地址
        PersonInfo.Residence = loan_live_address.getText().toString();
        //芝麻信用分
        PersonInfo.SesameCredit = Integer.parseInt(loan_sesame_credit.getText().toString().isEmpty() ? "0" : loan_sesame_credit.getText().toString());
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
        //紧急联系人2 电话
        PersonInfo.ContactPhone2 = loan_emergency_phone_second.getText().toString();
        //将个人信息缓存在本地
        Gson mGson = new Gson();
        (new LoanPersonalConfigUtil(this)).saveLoanPersonalMessage(mGson.toJson(PersonInfo));
        //跳转至工作信息页面
        Intent intent = new Intent(Activity_LoanRequest_Person.this, Activity_LoanRequest_Job.class);
        intent.putExtra("personInfoBean", mGson.toJson(PersonInfo));
        startActivity(intent);
    }


    //销毁广播
    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.BCB_GET_LOAN_PERSONAL_MESSAGE_REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_children://孩子情况
                String[] arr1 = childrenList.toArray(new String[childrenList.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr1, childrenStatus, new SpinnerWheelUtil.OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        childrenStatus = currentItem;
                        loan_children.setText(childrenList.get(currentItem));
                    }
                });
                break;
            case R.id.ll_house://住房情况
                String[] arr2 = houseList.toArray(new String[houseList.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr2, houseStatus - 1, new SpinnerWheelUtil.OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        houseStatus = currentItem + 1;
                        loan_house_situation.setText(houseList.get(currentItem));
                    }
                });
                break;
            case R.id.ll_marital://婚姻状况
                String[] arr3 = maritalList.toArray(new String[maritalList.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr3, maritalStatus - 1, new SpinnerWheelUtil.OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        maritalStatus = currentItem + 1;
                        loan_marital_status.setText(maritalList.get(currentItem));
                    }
                });
                break;
            case R.id.ll_literacy://文化程度
                String[] arr4 = literacyList.toArray(new String[literacyList.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr4, literacyStatus - 1, new SpinnerWheelUtil.OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        literacyStatus = currentItem + 1;
                        loan_literacy_level.setText(literacyList.get(currentItem));
                    }
                });
                break;
            case R.id.ll_relationship://紧急联系人1
                String[] arr5 = relationList1.toArray(new String[relationList1.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr5, relStatus1, new SpinnerWheelUtil.OnDoneClickListener() {
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
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr6, relStatus2, new SpinnerWheelUtil.OnDoneClickListener() {
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