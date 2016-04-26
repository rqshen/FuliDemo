package com.bcb.presentation.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.bcb.common.app.App;
import com.bcb.R;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.data.bean.loan.MaritalStatusListbean;
import com.bcb.data.bean.loan.PersonInfoBean;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.LoanPersonalConfigUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.RegexManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cain on 16/1/5.
 */
public class Activity_LoanRequest_Person extends Activity_Base {

    //婚姻状况
    private Spinner loan_marital_status;
    private List<String> maritalList;
    private ArrayAdapter<String> maritalAdapter;
    private int maritalStatus = 1;
    //住房状况
    private Spinner loan_house_situation;
    private List<String> houseList;
    private ArrayAdapter<String> houseAdapter;
    private int houseStatus = 1;
    //文化程度
    private Spinner loan_literacy_level;
    private List<String> literacyList;
    private ArrayAdapter<String> literacyAdapter;
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
    private List<String> relationList;
    //紧急联系人1
    private EditText loan_emergency_case;
    //紧急联系人1 适配器
    private Spinner loan_relationship;
    //紧急联系人1电话
    private EditText loan_emergency_phone;
    ///紧急联系人1 关系
    private String relationStatus1 = "";
    private ArrayAdapter<String> relationAdapter1;
    //紧急联系人2
    private EditText loan_emergency_case_second;
    //紧急联系人2 适配器
    private Spinner loan_relationship_second;
    //紧急联系人2电话
    private EditText loan_emergency_phone_second;
    //紧急联系人2 关系
    private String relationStatus2 = "";
    private ArrayAdapter<String> relationAdapter2;
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
        //婚姻状况
        loan_marital_status = (Spinner) findViewById(R.id.loan_marital_status);
        //住房状况
        loan_house_situation = (Spinner) findViewById(R.id.loan_house_situation);
        //文化程度
        loan_literacy_level = (Spinner) findViewById(R.id.loan_literacy_level);
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
        loan_relationship = (Spinner) findViewById(R.id.loan_relationship);
        //紧急联系人1电话
        loan_emergency_phone = (EditText) findViewById(R.id.loan_emergency_phone);

        //紧急联系人2
        loan_emergency_case_second = (EditText) findViewById(R.id.loan_emergency_case_second);
        //紧急联系人2关系
        loan_relationship_second = (Spinner) findViewById(R.id.loan_relationship_second);
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
                        if (TextUtils.isEmpty(new LoanPersonalConfigUtil(Activity_LoanRequest_Person.this).getLoanPersonalMessage())) {
                            PersonInfo = mGson.fromJson(result, PersonInfoBean.class);
                        } else {
                            PersonInfo = mGson.fromJson(new LoanPersonalConfigUtil(Activity_LoanRequest_Person.this).getLoanPersonalMessage(), PersonInfoBean.class);
//                            //更新列表数据，可能之前填写的数据已经跟现在的列表数据不一样了
//                            PersonInfoBean personInfoBean =mGson.fromJson(result, PersonInfoBean.class);
//                            PersonInfo.MaritalStatusList = personInfoBean.MaritalStatusList;
//                            PersonInfo.EducationLevelList = personInfoBean.EducationLevelList;
//                            PersonInfo.HousingStatusList = personInfoBean.HousingStatusList;
//                            PersonInfo.RelationshipList = personInfoBean.RelationshipList;
//                            PersonInfo.ChildrenStatusList = personInfoBean.ChildrenStatusList;
                        }

                        //设置婚姻状况
                        changeMaritalStatusList(PersonInfo);
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
                LogUtil.d("请求个人信息出错:", error.getMessage());
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
        //适配器
        maritalAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, maritalList);
        //设置下拉列表
        maritalAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //绑定适配器
        loan_marital_status.setAdapter(maritalAdapter);
        //设置点击Item事件
        loan_marital_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maritalStatus = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 设置住房状况列表
     */
    private void changeHousingStatusList(PersonInfoBean personInfoBean) {
        houseList = new ArrayList<String>();
        for (int i = 0; i < personInfoBean.HousingStatusList.size(); i++) {
            houseList.add(i, personInfoBean.HousingStatusList.get(i).Name);
        }
        houseAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, houseList);
        houseAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        loan_house_situation.setAdapter(houseAdapter);
        //设置点击Item事件
        loan_house_situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                houseStatus = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * 设置文化程度列表
     */
    private void changeEducationLevelList(PersonInfoBean personInfoBean) {
        literacyList = new ArrayList<String>();
        for (int i = 0; i < personInfoBean.EducationLevelList.size(); i++) {
            literacyList.add(i, personInfoBean.EducationLevelList.get(i).Name);
        }
        literacyAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, literacyList);
        literacyAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        loan_literacy_level.setAdapter(literacyAdapter);
        loan_literacy_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                literacyStatus = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 设置关系列表
     */
    private void changeRelationshipList(PersonInfoBean personInfoBean) {
        relationList = new ArrayList<String>();
        for (int i = 0; i < personInfoBean.RelationshipList.size(); i++) {
            relationList.add(i, personInfoBean.RelationshipList.get(i).Name);
        }
        relationStatus1 = relationList.get(0);
        relationAdapter1 = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, relationList);
        relationAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        loan_relationship.setAdapter(relationAdapter1);
        loan_relationship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                relationStatus1 = relationList.get(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        relationStatus2 = relationList.get(0);
        relationAdapter2 = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, relationList);
        relationAdapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        loan_relationship_second.setAdapter(relationAdapter2);
        loan_relationship_second.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                relationStatus2 = relationList.get(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    /**
     * 设置个人信息，需要前面三个列表初始化之后才能调用
     */
    private void  setupPersonalData() {
        //婚姻状况
        if (PersonInfo.MaritalStatus > 0) {
            loan_marital_status.setSelection(PersonInfo.MaritalStatus - 1);
        } else {
            loan_marital_status.setSelection(0);
        }
        //住房状况
        if (PersonInfo.HousingStatus > 0){
            loan_house_situation.setSelection(PersonInfo.HousingStatus - 1);
        } else {
            loan_house_situation.setSelection(0);
        }

        //文化程度
        if (PersonInfo.EducationLevel > 0) {
            loan_literacy_level.setSelection(PersonInfo.EducationLevel - 1);
        } else {
            loan_literacy_level.setSelection(0);
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
            for (int i = 0; i < relationList.size(); i++) {
                if (relationList.get(i).equalsIgnoreCase(PersonInfo.Relationship1)) {
                    loan_relationship.setSelection(i);
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
            for (int i = 0; i < relationList.size(); i++) {
                if (relationList.get(i).equalsIgnoreCase(PersonInfo.Relationship2)) {
                    loan_relationship_second.setSelection(i);
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
        if (loan_graduating_academy.getText().toString().equalsIgnoreCase("")
                || loan_identity_address.getText().toString().equalsIgnoreCase("")
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
        (new LoanPersonalConfigUtil(this)).saveLoanPersonalMessage(mGson.toJson(PersonInfo).toString());
        //跳转至工作信息页面
        Intent intent = new Intent(Activity_LoanRequest_Person.this, Activity_LoanRequest_Job.class);
        intent.putExtra("personInfoBean", mGson.toJson(PersonInfo).toString());
        startActivity(intent);
    }


    //销毁广播
    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.BCB_GET_LOAN_PERSONAL_MESSAGE_REQUEST);
    }

}