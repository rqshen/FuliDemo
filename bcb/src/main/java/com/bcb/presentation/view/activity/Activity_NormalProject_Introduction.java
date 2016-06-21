package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.project.SimpleProjectDetail;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.IdentifyAlertView;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cain on 16/1/28.
 */
public class Activity_NormalProject_Introduction extends Activity_Base implements View.OnClickListener {
    private static final String TAG = "Activity_NormalProject_Introduction";
    /************** 标题栏 *****************/
    private String title;

    /*************** 立即购买按钮 ********************/
    private Button button_buy;
    //普通标的数据
    private SimpleProjectDetail mSimpleProjectDetail;

    private ProgressDialog mProgressBar;

    private String packageId = "";

    /************************* 头部年化利率等信息 ************************************/
    //年化利率
    private TextView value_lilv;
    private TextView text_description;
    //加息利率
    private LinearLayout add_rate;
    private TextView value_reward;
    //可投金额
    private TextView total_money;
    //投资期限
    private TextView time_value;
    //融资总额（元）
    private TextView miniValue_invest;
    //本息保障
    private LinearLayout layout_investment;
    //描述
    private TextView value_description;

    /*************************** 投标进度 ********************************/
    //投资进度条
    private ProgressBar progress_percent;
    //投资进度百分比
    private TextView value_percent;
    //还款方式
    private TextView payment_type;
    //截止日期
    private TextView end_time;

    /********************************* 风控 ******************************************/
    //风控整体
    private RelativeLayout layout_examine;

    //借款来源公司
    private RelativeLayout layout_source;
    private TextView source_inspector;

    //风控审查员
    private RelativeLayout layout_company_hr;
    private TextView text_company;
    private TextView company_inspector;

    //风控团顾问
    private RelativeLayout layout_advisory;
    private TextView text_advisers;
    private TextView beset_advisers;


    /*************************** 项目详情 ****************************************************/
    //项目详情整体
    private LinearLayout layout_detail;
    //项目详情
    private RelativeLayout layout_project_detail;
    //保障信息
    private RelativeLayout layout_security;
    //证明文件图片
    private RelativeLayout layout_identify;
    //投资列表
    private RelativeLayout layout_invest_list;
    //领投人
    private TextView investLeader;

    //提示对话框
    private DialogWidget dialogWidget;

    //专属客服
    private LinearLayout layout_customer_service;

    private String companyUrl;
    private String companyName;
    private int durationExchangeType = 1;
    private float rate = 0;
    private float rewardRate = 0;
    private int duration = 1;
    private int CouponType = 0;
    private int countDate = 0;

    private BcbRequestQueue requestQueue;
    //默认的构造函数
    public static void launche(Context ctx,
                               String pid,
                               String title,
                               int CouponType) {
        Intent intent = new Intent();
        intent.putExtra("pid", pid);
        intent.putExtra("title", title);
        intent.putExtra("CouponType", CouponType);
        intent.setClass(ctx, Activity_NormalProject_Introduction.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_NormalProject_Introduction.this);
        packageId = getIntent().getStringExtra("pid");
        title = getIntent().getStringExtra("title");
        CouponType = this.getIntent().getIntExtra("CouponType", 0);
        setBaseContentView(R.layout.activity_normalproject_introduction);
        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
                    UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_back);
                }
                finish();
            }
        });
        setTitleValue(title);
        requestQueue = App.getInstance().getRequestQueue();
        UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.buy_home);
        //初始化界面
        setupView();
        //加载普通标的数据
        loadProjectData();
        //获取用户信息
        loadUserDetailInfoData();
    }


    /*************** 初始化界面 *************************/
    private void setupView() {
        showProgressBar("正在获取标的数据...");
        /*************** 年化利率等信息 ****************************/
        // 年化利率
        value_lilv = (TextView) findViewById(R.id.value_lilv);
        text_description = (TextView) findViewById(R.id.text_description);
        text_description.setVisibility(View.GONE);
        //加息利率
        add_rate = (LinearLayout) findViewById(R.id.add_rate);
        value_reward = (TextView) findViewById(R.id.value_reward);

        //福袋数据
        if (!TextUtils.isEmpty(App.getInstance().getWelfare())){
            add_rate.setVisibility(View.VISIBLE);
            value_reward.setText("+" + App.getInstance().getWelfare() + "%");
        }else{
            add_rate.setVisibility(View.GONE);
        }
        //可投金额
        total_money = (TextView) findViewById(R.id.value_total);
        // 理财期限
        time_value = (TextView) findViewById(R.id.time_value);
        //融资总额（元）
        miniValue_invest = (TextView) findViewById(R.id.value_invest_mini);
        //本息保障
        layout_investment = (LinearLayout) findViewById(R.id.layout_investment);
        layout_investment.setOnClickListener(this);

        //描述
        value_description = (TextView) findViewById(R.id.value_description);
        value_description.setVisibility(View.GONE);

        // 立即购买按钮
        button_buy = (Button) findViewById(R.id.button_buy);
        button_buy.setOnClickListener(this);

        /***************************** 进度 ******************************/
        //投资进度条
        progress_percent = (ProgressBar) findViewById(R.id.progress_percent);
        progress_percent.setMax(100);
        progress_percent.setProgress(0);
        progress_percent.setSecondaryProgress(0);
        //投资进度百分比
        value_percent = (TextView) findViewById(R.id.value_percent);

        //还款方式
        payment_type = (TextView) findViewById(R.id.payment_type);

        //投标截止日期
        end_time = (TextView) findViewById(R.id.end_time);

        /****************************** 风控 *******************************/
        //风控整体
        layout_examine = (RelativeLayout) findViewById(R.id.layout_examine);

        //借款来源公司
        layout_source = (RelativeLayout) findViewById(R.id.layout_source);
        layout_source.setOnClickListener(this);
        source_inspector = (TextView) findViewById(R.id.source_inspector);

        //公司审查员
        text_company = (TextView) findViewById(R.id.text_company);
        layout_company_hr = (RelativeLayout) findViewById(R.id.layout_company_hr);
        layout_company_hr.setOnClickListener(this);
        company_inspector = (TextView) findViewById(R.id.company_inspector);
        //风控顾问团
        text_advisers = (TextView) findViewById(R.id.text_advisers);
        layout_advisory = (RelativeLayout) findViewById(R.id.layout_advisory);
        layout_advisory.setOnClickListener(this);
        beset_advisers = (TextView) findViewById(R.id.beset_advisers);


        /************************* 项目详情介绍 ******************************/
        //项目详情整体
        layout_detail = (LinearLayout) findViewById(R.id.layout_detail);
        //项目详情
        layout_project_detail = (RelativeLayout) findViewById(R.id.layout_project_detail);
        layout_project_detail.setOnClickListener(this);
        //安全保障
        layout_security = (RelativeLayout) findViewById(R.id.layout_security);
        layout_security.setOnClickListener(this);
        //证明文件图片
        layout_identify = (RelativeLayout) findViewById(R.id.layout_identify);
        layout_identify.setOnClickListener(this);
        //投资列表
        layout_invest_list = (RelativeLayout) findViewById(R.id.layout_invest_list);
        layout_invest_list.setOnClickListener(this);
        //领投人
        investLeader = (TextView) findViewById(R.id.investLeader);

        //专属客服
        layout_customer_service = (LinearLayout) findViewById(R.id.layout_customer_service);
        layout_customer_service.setOnClickListener(this);
    }

    /****************** 加载普通标的数据 *******************************/
    private void loadProjectData() {
        JSONObject js = new JSONObject();
        try {
            js.put("PackageId", packageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.NormalProjectIntroduction, js, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(PackageUtil.getRequestStatus(response, Activity_NormalProject_Introduction.this)){
                    try {
                        //先转义
                        String resultString = response.getString("result").replace("\\", "").replace("\"[", "[").replace("]\"", "]");
                        JSONObject resultObject = new JSONObject(resultString);
                        //注意：不去掉会出现json解析语法错误
                        if (TextUtils.isEmpty(resultObject.getString("AssetAuditContent"))){
                            resultObject.remove("AssetAuditContent");
                        }
                        mSimpleProjectDetail = App.mGson.fromJson(resultObject.toString(), SimpleProjectDetail.class);
                        if(null != mSimpleProjectDetail){
                            //暂存数据
                            durationExchangeType = mSimpleProjectDetail.DurationExchangeType;
                            rate = mSimpleProjectDetail.Rate;
                            rewardRate = mSimpleProjectDetail.RewardRate;
                            duration = mSimpleProjectDetail.Duration;
                            companyName = mSimpleProjectDetail.CompanyName;
                            companyUrl = mSimpleProjectDetail.CompanyUrl;
                            //显示数据
                            showProjectData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_NormalProject_Introduction.this, "网络异常，请稍后重试");
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.NormalProjectIntroductionTag);
        requestQueue.add(jsonRequest);
    }

    /***************** 显示普通标的数据 *******************************/
    private void showProjectData() {
        // 年化利率
        value_lilv.setText(mSimpleProjectDetail.Rate+"");
        text_description.setVisibility(View.VISIBLE);

        //领投人
        if (!TextUtils.isEmpty(mSimpleProjectDetail.InvestLeader) && !mSimpleProjectDetail.InvestLeader.equalsIgnoreCase("null")) {
            investLeader.setText(mSimpleProjectDetail.InvestLeader);
        }

        //奖励描述
        if (mSimpleProjectDetail.RewardRateDescn != null
                && !mSimpleProjectDetail.RewardRateDescn.equalsIgnoreCase("null")
                && !mSimpleProjectDetail.RewardRateDescn.equalsIgnoreCase("")) {
            value_description.setVisibility(View.VISIBLE);
            value_description.setText("返" + mSimpleProjectDetail.RewardRateDescn);
        }

        //可投金额
        total_money.setText(String.format("%d", (int)mSimpleProjectDetail.AmountBalance) + "元");

        //理财期限
        switch (mSimpleProjectDetail.DurationExchangeType) {
            case 1:
                //这里转成字符串的原因是防止出现NullPointerException崩溃
                time_value.setText(String.format("%d", mSimpleProjectDetail.Duration) + "天");
                countDate = 360;
                break;

            case 2:
                //这里转成字符串的原因是防止出现NullPointerException崩溃
                time_value.setText(String.format("%d", mSimpleProjectDetail.Duration) + "个月");
                countDate = 12;
                break;

            default:
                time_value.setText("");
                break;
        }

        //融资总额
        miniValue_invest.setText(String.format("%d", (int)mSimpleProjectDetail.AmountTotal) + "元");

        /************************* 进度等 ************************************/
        //投标进度条
        progress_percent.setProgress((int) (100 - ((float) mSimpleProjectDetail.AmountBalance / mSimpleProjectDetail.AmountTotal) * 100));
        progress_percent.setSecondaryProgress((int)(100 - ((float)mSimpleProjectDetail.AmountBalance/mSimpleProjectDetail.AmountTotal) * 100));
        //投资进度百分比
        value_percent.setText(String.format("%.2f", (1 - (float)mSimpleProjectDetail.AmountBalance/mSimpleProjectDetail.AmountTotal) * 100) + "%");
        //还款方式
        payment_type.setText(mSimpleProjectDetail.PaymentType);
        //截止日期
        end_time.setText(mSimpleProjectDetail.ApplyEndTime);

        /************************** 风控 ********************************/
        //借款来源公司
        if (!TextUtils.isEmpty(companyName)) {
            layout_source.setVisibility(View.VISIBLE);
            source_inspector.setText(companyName);
        } else {
            layout_source.setVisibility(View.GONE);
        }
        if (mSimpleProjectDetail.AssetAuditContent != null) {
            layout_examine.setVisibility(View.VISIBLE);
            //公司审查员标题
            text_company.setText(mSimpleProjectDetail.AssetAuditContent.get(0).Title);
            //公司审查员内容
            company_inspector.setText(mSimpleProjectDetail.AssetAuditContent.get(0).Content);
            //风控顾问团标题
            text_advisers.setText(mSimpleProjectDetail.AssetAuditContent.get(1).Title);
            //风控顾问团内容
            beset_advisers.setText(mSimpleProjectDetail.AssetAuditContent.get(1).Content);
        } else {
            layout_examine.setVisibility(View.GONE);
        }

        /*************************** 项目详情 ******************************/
        layout_detail.setVisibility(View.VISIBLE);
        //设置按钮颜色
        setButtonColor(mSimpleProjectDetail.Status);
    }

    /******************* 设置按钮不可用时的颜色和可用状态 *****************************/
    private void setButtonColor(int status) {
        if (status != 20) {
            button_buy.setBackgroundResource(R.drawable.button_project_gray);
            button_buy.setTextColor(getResources().getColor(R.color.project_button_gray));
            button_buy.setEnabled(false);
        }
    }

    /***************** 购买函数 **********************/
    private void clickButton() {
        //判断是否存在买标数据
        if (mSimpleProjectDetail == null) {
            ToastUtil.alert(Activity_NormalProject_Introduction.this, "无法获取买标数据");
            return;
        }
        //判断可投金额是否大于0元
        if (mSimpleProjectDetail.AmountBalance <= 0) {
            ToastUtil.alert(Activity_NormalProject_Introduction.this, "可投金额为0元，该标不能投");
            return;
        }
        //还没有登陆时，跳转至登陆页面
        if (App.saveUserInfo.getAccess_Token() == null) {
            startActivity(new Intent(Activity_NormalProject_Introduction.this, Activity_Login_Introduction.class));
            finish();
            return;
        }
        //如果没有认证则跳转至认证界面
        if (App.mUserDetailInfo == null ||  !App.mUserDetailInfo.HasCert || !App.mUserDetailInfo.HasBindCard) {
            identifyPageTips();
            return;
        }

        gotoBuyPrjectPage();
    }

    /******************* 弹出认证提示界面 *************************/
    private void identifyPageTips() {
        dialogWidget = new DialogWidget(Activity_NormalProject_Introduction.this, IdentifyAlertView.getInstance(Activity_NormalProject_Introduction.this, new IdentifyAlertView.OnClikListener() {
            @Override
            public void onCancelClick() {
                dialogWidget.dismiss();
                dialogWidget = null;
            }

            @Override
            public void onSureClick() {
                dialogWidget.dismiss();
                dialogWidget = null;
                //去认证
                gotoAuthenticationActivity();
            }
        }).getView());
        dialogWidget.show();
    }

    /********************** 去认证页面 ***********************/
    private void gotoAuthenticationActivity() {
        Intent newIntent = new Intent(Activity_NormalProject_Introduction.this, Activity_Authentication.class);
        startActivityForResult(newIntent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (data != null) {
                    loadUserDetailInfoData();
                }
                break;

            default:
                break;
        }
    }

    /*************** 获取用户银行卡信息 *********************/
    private void loadUserDetailInfoData() {
        //如果Token为空或者银行卡信息不为空，则停止请求
        if (App.saveUserInfo.getAccess_Token() == null || App.mUserDetailInfo != null && App.mUserDetailInfo.BankCard != null) {
            return;
        } else {
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserBankMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        int status = response.getInt("status");
                        if (status == 1) {
                            JSONObject data = PackageUtil.getResultObject(response);

                            UserDetailInfo mUserDetailInfo;
                            //判断JSON对象是否为空
                            if (data != null) {
                                mUserDetailInfo = App.mGson.fromJson(data.toString(), UserDetailInfo.class);
                                //将用户信息写入静态数据区
                                if (mUserDetailInfo != null) {
                                    App.mUserDetailInfo = mUserDetailInfo;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(Exception error) {
                    ToastUtil.alert(Activity_NormalProject_Introduction.this, "网络异常，请稍后再试");
                }
            });
            jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
            requestQueue.add(jsonRequest);
        }
    }

    /********************* 去买标页面 **********************/
    private void gotoBuyPrjectPage() {
        Activity_Project_Buy.launche(Activity_NormalProject_Introduction.this, packageId, title, CouponType, countDate, mSimpleProjectDetail, false);
        finish();
    }

    /********************* 转圈提示 **************************/
    //显示转圈提示
    private void showProgressBar(String title) {
        if(null == mProgressBar)mProgressBar= new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
        mProgressBar.setMessage(title);
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setCancelable(true);
        mProgressBar.show();
    }
    //隐藏转圈提示
    private void hideProgressBar(){
        if(null != mProgressBar && mProgressBar.isShowing()){
            mProgressBar.dismiss();
        }
    }


    /********************* 蒙版效果提示 ************************/
    private void showAdviserTips(String tips) {
        dialogWidget = new DialogWidget(Activity_NormalProject_Introduction.this, getAdviserView(tips), true);
        dialogWidget.show();
    }

    protected View getAdviserView(String tips) {
        return MyMaskFullScreenView.getInstance(Activity_NormalProject_Introduction.this, tips, new MyMaskFullScreenView.OnClikListener() {
            @Override
            public void onViewClik() {
                dialogWidget.dismiss();
                dialogWidget = null;
            }
        }).getView();
    }

    /***************************** 更新用户数据 **************************************/
    private void updateUserInfo() {
        showProgressBar("正在更新用户信息...");

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.GetUserInfo, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    String message = response.getString("message");
                    if(status == 1) {
                        UserDetailInfo userDetailInfo = App.mGson.fromJson(response.getJSONObject("result").toString(), UserDetailInfo.class);
                        //判断返回的用户信息是否为空
                        if(null != userDetailInfo) {
                            //将获取到的信息存放到静态数据区中
                            App.mUserDetailInfo = userDetailInfo;
                        }
                    } else {
                        ToastUtil.alert(Activity_NormalProject_Introduction.this, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_NormalProject_Introduction.this, "网络异常，请稍后重试");
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.BCB_GET_USER_INFORMATION_REQUEST);
        requestQueue.add(jsonRequest);
    }

    //重写点击返回按钮，发送广播并销毁Activity对象
    @Override
    public void onBackPressed() {
        UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_back);
        finish();
    }

    //销毁广播
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //暂存标的订单号的信息
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //订单号
        savedInstanceState.putString("PackageId", packageId);
        savedInstanceState.putInt("durationExchangeType", durationExchangeType);
        savedInstanceState.putFloat("rate", rate);
        savedInstanceState.putFloat("rewardRate", rewardRate);
        savedInstanceState.putInt("duration", duration);
        savedInstanceState.putString("companyName", companyName);
        savedInstanceState.putString("companyUrl", companyUrl);
    }

    //取出暂存的订单号信息
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //获取标的订单号
        packageId = savedInstanceState.getString("PackageId");
        //获取天标还是月标状态
        durationExchangeType = savedInstanceState.getInt("durationExchangeType");
        rate = savedInstanceState.getFloat("rate");
        rewardRate = savedInstanceState.getFloat("rewardRate");
        duration = savedInstanceState.getInt("duration");
        companyName = savedInstanceState.getString("companyName");
        companyUrl = savedInstanceState.getString("companyUrl");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //本息保障
            case R.id.layout_investment:
                if (mSimpleProjectDetail != null &&mSimpleProjectDetail.Status != 20) {
                    UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_info2);
                }
                showAdviserTips("该项目受福利金融风险保证金保障");
                break;

            //专属客服
            case R.id.layout_customer_service:
                UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_kefu);
                //如果ID存在
                String userId = null;
                if (App.mUserDetailInfo != null) {
                    userId = App.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(this).showCustomer(userId);
                break;

            
            //点击立即购买按钮
            case R.id.button_buy:
                if (mSimpleProjectDetail != null &&mSimpleProjectDetail.Status != 20) {
                    UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_act);
                }
                clickButton();
                break;

            //借款来源公司
            case R.id.layout_source:
                Activity_Browser.launche(this, TextUtils.isEmpty(companyName)? "借款来源公司详情" : companyName, companyUrl);
                break;

            //风控审查员
            case R.id.layout_company_hr:
                if (mSimpleProjectDetail != null &&mSimpleProjectDetail.Status != 20){
                    UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_info);
                }
                if (mSimpleProjectDetail.AssetAuditContent.get(0).Introduction != null
                        && !mSimpleProjectDetail.AssetAuditContent.get(0).Introduction.equalsIgnoreCase("null")
                        && !mSimpleProjectDetail.AssetAuditContent.get(0).Introduction.equalsIgnoreCase("")) {
                    showAdviserTips(mSimpleProjectDetail.AssetAuditContent.get(0).Introduction);
                }
                break;

            //风控顾问团
            case R.id.layout_advisory:
                if (mSimpleProjectDetail.AssetAuditContent.get(1).Introduction != null
                        && !mSimpleProjectDetail.AssetAuditContent.get(1).Introduction.equalsIgnoreCase("null")
                        && !mSimpleProjectDetail.AssetAuditContent.get(1).Introduction.equalsIgnoreCase("")) {
                    showAdviserTips(mSimpleProjectDetail.AssetAuditContent.get(1).Introduction);
                }
                break;

            //项目详情
            case R.id.layout_project_detail:
                if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
                    UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_detail1);
                }
                if (null != mSimpleProjectDetail && !TextUtils.isEmpty(mSimpleProjectDetail.PageUrl)){
                    Activity_Browser.launche(Activity_NormalProject_Introduction.this, title, mSimpleProjectDetail.PageUrl + "&tab=1");
                }
                break;
            //保障信息
            case R.id.layout_security:
                if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
                    UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_detail2);
                }
                if (null != mSimpleProjectDetail && !TextUtils.isEmpty(mSimpleProjectDetail.PageUrl)){
                    Activity_Browser.launche(Activity_NormalProject_Introduction.this, title, mSimpleProjectDetail.PageUrl+"&tab=2");
                }
                break;
            //证明文件
            case R.id.layout_identify:
                if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
                    UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_detail3);
                }
                if (null != mSimpleProjectDetail && !TextUtils.isEmpty(mSimpleProjectDetail.PageUrl)){
                    Activity_Browser.launche(Activity_NormalProject_Introduction.this, title, mSimpleProjectDetail.PageUrl+"&tab=3");
                }
                break;
            //投资列表
            case R.id.layout_invest_list:
                if (mSimpleProjectDetail != null && mSimpleProjectDetail.Status != 20) {
                    UmengUtil.eventById(Activity_NormalProject_Introduction.this, R.string.bid_buy_detail4);
                }
                if (null != mSimpleProjectDetail && !TextUtils.isEmpty(mSimpleProjectDetail.PageUrl)){
                    Activity_Browser.launche(Activity_NormalProject_Introduction.this, title, mSimpleProjectDetail.PageUrl+"&tab=4");
                }
                break;
        }
    }
}