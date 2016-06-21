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
import com.bcb.data.util.MoneyTextUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by cain on 16/1/5.
 */
public class Activity_LoanRequest_Asset extends Activity_Base {
    private static final String TAG = "Activity_LoanRequest_Asset";
    //家庭总收入
    private EditText total_household_income;
    private int totalIncome = -1;
    //家庭固定支出
    private EditText household_fixed;
    private int householdFixed = -1;
    //房产价值
    private EditText property_value;
    private int propertyValue = -1;
    //车产价值
    private EditText vehicle_value;
    private int vehicleValue = -1;
    //有价证券
    private EditText marketable_securities;
    private int marketableSecurities = -1;
    //其他资产
    private EditText other_assets;
    private int otherAsset = -1;
    //房产负债
    private EditText property_liabilities;
    private int propertyLiabilities = -1;
    //车产负债
    private EditText vehicle_liabilities;
    private int vehicleLiabilities = -1;
    //信用卡负债
    private EditText credit_card_debt;
    private int creditCardDebt = -1;
    //其他负债
    private EditText other_liabilities;
    private int otherLiabilities = -1;

    //按钮
    private Button asset_button;

    //转圈提示
    private ProgressDialog progressDialog;

    //个人信息
    private PersonInfoBean personInfoBean;

    //请求队列
    private BcbRequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_LoanRequest_Asset.this);
        setBaseContentView(R.layout.activity_loanrequest_asset);
        setLeftTitleVisible(true);
        setTitleValue("个人资产信息");
        //初始化页面元素
        initAssetView();
        //初始化数据对象
        setupObjectData();
        //初始化队列
        setupQueue();
        //设置房产信息
        setupAssetMessage();
    }

    /**
     * 初始化界面元素
     */
    private void initAssetView() {
        //家庭总收入
        total_household_income = (EditText) findViewById(R.id.total_household_income);
        total_household_income.addTextChangedListener(new CustomTextWatcher(total_household_income));
        //家庭固定支出
        household_fixed = (EditText) findViewById(R.id.household_fixed);
        household_fixed.addTextChangedListener(new CustomTextWatcher(household_fixed));
        //房产价值
        property_value = (EditText) findViewById(R.id.property_value);
        property_value.addTextChangedListener(new CustomTextWatcher(property_value));
        //车产价值
        vehicle_value = (EditText) findViewById(R.id.vehicle_value);
        vehicle_value.addTextChangedListener(new CustomTextWatcher(vehicle_value));
        //有价证券
        marketable_securities = (EditText) findViewById(R.id.marketable_securities);
        marketable_securities.addTextChangedListener(new CustomTextWatcher(marketable_securities));
        //其他资产
        other_assets = (EditText) findViewById(R.id.other_assets);
        other_assets.addTextChangedListener(new CustomTextWatcher(other_assets));
        //房产负债
        property_liabilities = (EditText) findViewById(R.id.property_liabilities);
        property_liabilities.addTextChangedListener(new CustomTextWatcher(property_liabilities));
        //车产负债
        vehicle_liabilities = (EditText) findViewById(R.id.vehicle_liabilities);
        vehicle_liabilities.addTextChangedListener(new CustomTextWatcher(vehicle_liabilities));
        //信用卡负债
        credit_card_debt = (EditText) findViewById(R.id.credit_card_debt);
        credit_card_debt.addTextChangedListener(new CustomTextWatcher(credit_card_debt));
        //其他负债
        other_liabilities = (EditText) findViewById(R.id.other_liabilities);
        other_liabilities.addTextChangedListener(new CustomTextWatcher(other_liabilities));
        //按钮
        asset_button = (Button) findViewById(R.id.asset_button);
        asset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick();
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
     * 初始化队列
     */
    private void setupQueue() {
        requestQueue = App.getInstance().getRequestQueue();
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
     * 设置房产信息
     */
    private void setupAssetMessage() {
        //家庭总收入
        if (personInfoBean.TotalHomeIncome >= 0) {
            total_household_income.setText(personInfoBean.TotalHomeIncome + "");
        } else {
            total_household_income.setText("0");
        }
        //家庭固定支出
        if (personInfoBean.HomeFixedExpenditure >= 0) {
            household_fixed.setText(personInfoBean.HomeFixedExpenditure + "");
        } else {
            household_fixed.setText("0");
        }
        //房产价值
        if (personInfoBean.HousingWorth >= 0) {
            property_value.setText(personInfoBean.HousingWorth + "");
        } else {
            property_value.setText("0");
        }
        //车产价值
        if (personInfoBean.CarWorth >= 0) {
            vehicle_value.setText(personInfoBean.CarWorth + "");
        } else {
            vehicle_value.setText("0");
        }
        //有价证券
        if (personInfoBean.SecuritiesWorth >= 0) {
            marketable_securities.setText(personInfoBean.SecuritiesWorth + "");
        } else {
            marketable_securities.setText("0");
        }
        //其他资产
        if (personInfoBean.OtherAsset >= 0) {
            other_assets.setText(personInfoBean.OtherAsset + "");
        } else {
            other_assets.setText("0");
        }
        //房产负债
        if (personInfoBean.HousingDebt >= 0) {
            property_liabilities.setText(personInfoBean.HousingDebt + "");
        } else {
            property_liabilities.setText("0");
        }
        //车产负债
        if (personInfoBean.CarDebt >= 0) {
            vehicle_liabilities.setText(personInfoBean.CarDebt + "");
        } else {
            vehicle_liabilities.setText("0");
        }
        //信用卡负债
        if (personInfoBean.CreditCardDebt >= 0) {
            credit_card_debt.setText(personInfoBean.CreditCardDebt + "");
        } else {
            credit_card_debt.setText("0");
        }
        //其他负债
        if (personInfoBean.OtherAsset >= 0) {
            other_liabilities.setText(personInfoBean.OtherDebt + "");
        } else {
            other_liabilities.setText("0");
        }
    }

    /**
     * 获取界面的数据
     */
    private void getDataFromView() {
        //家庭总收入
        if (total_household_income.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            totalIncome = 0;
        } else {
            totalIncome = Integer.parseInt(total_household_income.getText().toString().replace(",", ""));
        }
        //家庭固定支出
        if (household_fixed.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            householdFixed = 0;
        } else {
            householdFixed = Integer.parseInt(household_fixed.getText().toString().replace(",", ""));
        }
        //房产价值
        if (property_value.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            propertyValue = 0;
        } else {
            propertyValue = Integer.parseInt(property_value.getText().toString().replace(",", ""));
        }
        //车产价值
        if (vehicle_value.getText().toString().replace(",", "").equalsIgnoreCase("")) {
          vehicleValue = 0;
        } else {
            vehicleValue = Integer.parseInt(vehicle_value.getText().toString().replace(",", ""));
        }
        //有价证券
        if (marketable_securities.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            marketableSecurities = 0;
        } else {
            marketableSecurities = Integer.parseInt(marketable_securities.getText().toString().replace(",", ""));
        }
        //其他资产
        if (other_assets.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            otherAsset = 0;
        } else {
            otherAsset = Integer.parseInt(other_assets.getText().toString().replace(",", ""));
        }
        //房产负债
        if (property_liabilities.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            propertyLiabilities = 0;
        } else {
            propertyLiabilities = Integer.parseInt(property_liabilities.getText().toString().replace(",", ""));
        }
        //车产负债
        if (vehicle_liabilities.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            vehicleLiabilities = 0;
        } else {
            vehicleLiabilities = Integer.parseInt(vehicle_liabilities.getText().toString().replace(",", ""));
        }
        //信用卡负债
        if (credit_card_debt.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            creditCardDebt = 0;
        } else {
            creditCardDebt = Integer.parseInt(credit_card_debt.getText().toString().replace(",", ""));
        }
        //其他负债
        if (other_liabilities.getText().toString().replace(",", "").equalsIgnoreCase("")) {
            otherLiabilities = 0;
        } else {
            otherLiabilities = Integer.parseInt(other_liabilities.getText().toString().replace(",", ""));
        }
    }

    /**
     * 点击按钮事件
     */
    private void buttonClick() {
        //现获取信息
        getDataFromView();
        //判断是否存在未填写的信息
        if (totalIncome < 0 || householdFixed < 0 || propertyValue < 0 || vehicleValue < 0
                || marketableSecurities < 0 || otherAsset < 0 || propertyLiabilities < 0
                || vehicleLiabilities < 0 || creditCardDebt < 0 || otherLiabilities < 0) {
            ToastUtil.alert(Activity_LoanRequest_Asset.this, "请填写完整的资产信息");
            return;
        }
        //保存数据并上传
        postDatatoService();
    }

    /**
     * 将个人信息提交给服务器
     */
    private void postDatatoService() {
        //家庭总收入
        personInfoBean.TotalHomeIncome = totalIncome;
        //家庭固定支出
        personInfoBean.HomeFixedExpenditure = householdFixed;
        //房产价值
        personInfoBean.HousingWorth = propertyValue;
        //车产价值
        personInfoBean.CarWorth = vehicleValue;
        //有价证券
        personInfoBean.SecuritiesWorth = marketableSecurities;
        //其他资产
        personInfoBean.OtherAsset = otherAsset;
        //房贷
        personInfoBean.HousingDebt = propertyLiabilities;
        //车贷
        personInfoBean.CarDebt = vehicleLiabilities;
        //信用卡负债
        personInfoBean.CreditCardDebt = creditCardDebt;
        //其他负债
        personInfoBean.OtherDebt = otherLiabilities;
        //使用Gson将对象转成JSOnObject对象
        Gson mGson = new Gson();
        (new LoanPersonalConfigUtil(this)).saveLoanPersonalMessage(mGson.toJson(personInfoBean));
        try {
            JSONObject jsonObject = new JSONObject(mGson.toJson(personInfoBean));
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.PostLoanPersonalMessage, jsonObject, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideProgressBar();
                    try {
                        if (response.getInt("status") == 1) {
                            //跳转至借款申请成功页面
                            Intent intent = new Intent(Activity_LoanRequest_Asset.this, Activity_LoanRequest_Success.class);
                            startActivity(intent);
                            finish();
                            //清空暂存在本地的数据
                            (new LoanPersonalConfigUtil(Activity_LoanRequest_Asset.this)).clear();
                        } else {
                            ToastUtil.alert(Activity_LoanRequest_Asset.this, response.getString("message").equalsIgnoreCase("") ? "服务器繁忙，请稍候再试" : response.getString("message"));
                            //判断是否是Token过期，如果过期则跳转至登陆界面
                            if (response.getInt("status") == -5) {
                                Activity_Login.launche(Activity_LoanRequest_Asset.this);
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
     * 输入监听器
     */
    private class CustomTextWatcher implements TextWatcher {

        private EditText editText;
        //构造器
        public CustomTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String textValue = editText.getText().toString();
            //清除分割符
            String tmpValue = textValue.replace(",", "");
            //如果输入的值为0，则替换成空字符串
            if (!tmpValue.equalsIgnoreCase("") && Float.parseFloat(tmpValue) == 0) {
                textValue = "";
            }
            editText.removeTextChangedListener(this);
            editText.setText(MoneyTextUtil.ConversionThousandUnit(textValue.replace(",", "")));
            editText.setSelection(editText.getText().toString().length());
            editText.addTextChangedListener(this);
        }
    }

    //销毁广播
    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.BCB_POST_LOAN_PERSONAL_MESSAGE_REQUEST);
    }
}