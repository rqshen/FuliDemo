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
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.WelfareBean;
import com.bcb.data.bean.project.ExpiredProjectDetail;
import com.bcb.data.util.DbUtil;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.IdentifyAlertView;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;

import org.json.JSONObject;

/**
 * Created by cain on 16/1/30.
 */
public class Activity_ExpiredProject_Introduction extends Activity_Base implements View.OnClickListener {

    private static final String TAG = "Activity_ExpiredProject_Introduction";

    //标题
    private String title;
    //年化利率
    private TextView value_lilv;
    //加息利率
    private LinearLayout add_rate;
    private TextView expire_value_reward;
    //体验标名额
    private TextView value_total;
    //投资期限
    private TextView time_value;
//    //万元收益
//    private TextView value_gain;
    //奖励说明
    private TextView value_description;
    //本息保障
    private LinearLayout layout_investment;
    //投标进度条
    private ProgressBar progress_percent;
    //投标进度百分比
    private TextView value_percent;
    //融资总额
    private TextView value_invest_mini;
    //还款方式
    private TextView payment_type;

    //投标截止日期
    private TextView end_time;
    //体验标说明
    private RelativeLayout layout_description;
    // 立即购买按钮
    private Button button_buy;
    //转圈提示
    private ProgressDialog progressDialog;
    //标的Id
    private String packageId = "";
    //记录天标还是月标的数值360/12
    private int countDate = 0;
    //体验标数据
    private ExpiredProjectDetail expiredProjectDetail;

    //弹框
    private DialogWidget dialogWidget;

    //专属客服
    private LinearLayout layout_customer_service;

    private BcbRequestQueue requestQueue;

    //默认的构造函数
    public static void launche(Context ctx,
                               String pid,
                               String title) {
        Intent intent = new Intent();
        intent.putExtra("pid", pid);
        intent.putExtra("title", title);
        intent.setClass(ctx, Activity_ExpiredProject_Introduction.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_ExpiredProject_Introduction.this);
        packageId = getIntent().getStringExtra("pid");
        title = getIntent().getStringExtra("title");
        setBaseContentView(R.layout.activity_expiredproject_introduction);
        setLeftTitleVisible(true);
        setTitleValue(title);
        requestQueue = BcbNetworkManager.newRequestQueue(this);
        //初始化标的界面
        setupView();
        //加载数据
        loadExpiredData();
        //获取用户银行卡信息
        loadUserDetailInfoData();
    }


    /****************************** 初始化标的界面 **************************************/
    private void setupView() {
        //年化利率
        value_lilv = (TextView) findViewById(R.id.value_lilv);
        //加息利率
        add_rate = (LinearLayout) findViewById(R.id.add_rate);
        expire_value_reward = (TextView) findViewById(R.id.expire_value_reward);
        //福袋数据
        if (!TextUtils.isEmpty(App.getInstance().getWelfare())){
            add_rate.setVisibility(View.VISIBLE);
            expire_value_reward.setText("+" + App.getInstance().getWelfare() + "%");
        }else{
            add_rate.setVisibility(View.GONE);
        }
        //可投金额
        value_total = (TextView) findViewById(R.id.value_total);
        //投资期限
        time_value = (TextView) findViewById(R.id.time_value);
        //万元收益
//        value_gain = (TextView) findViewById(R.id.value_gain);
        //奖励说明
        value_description = (TextView) findViewById(R.id.value_description);
        //本息保障
        layout_investment = (LinearLayout) findViewById(R.id.layout_investment);
        layout_investment.setOnClickListener(this);
        //进度条
        progress_percent = (ProgressBar) findViewById(R.id.progress_percent);
        //进度条百分比
        value_percent = (TextView) findViewById(R.id.value_percent);
        //融资总额
        value_invest_mini = (TextView) findViewById(R.id.value_invest_mini);
        //还款方式
        payment_type = (TextView) findViewById(R.id.payment_type);
        //截止日期
        end_time = (TextView) findViewById(R.id.end_time);
        //体验标说明
        layout_description = (RelativeLayout) findViewById(R.id.layout_description);
        layout_description.setOnClickListener(this);
        // 立即购买按钮
        button_buy = (Button) findViewById(R.id.button_buy);
        button_buy.setOnClickListener(this);

        layout_customer_service = (LinearLayout) findViewById(R.id.layout_customer_service);
        layout_customer_service.setOnClickListener(this);
    }

    /****************************** 加载体验标数据 ***************************************/
    private void loadExpiredData() {
        showProgressBar("正在加载标的数据...");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PackageId", packageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.ExpiredProjectIntroduction, jsonObject, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(PackageUtil.getRequestStatus(response, Activity_ExpiredProject_Introduction.this)) {
                    // 数据解析
                    JSONObject resultObject = PackageUtil.getResultObject(response.toString().replace("\\", "").replace("\"[", "[").replace("]\"", "]"));
                    //判断JSON对象是否为空
                    if (resultObject != null) {
                        expiredProjectDetail = App.mGson.fromJson(resultObject.toString(), ExpiredProjectDetail.class);
                    }
                    if(null != expiredProjectDetail){
                        showExpiredData();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_ExpiredProject_Introduction.this, "网络异常，请稍后重试");
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.ExpiredProjectIntroductionTag);
        requestQueue.add(jsonRequest);

    }

    /******************** 显示新手体验标标的数据 *******************************/
    private void showExpiredData() {
        //年化利率
        value_lilv.setText(String.format("%.2f", expiredProjectDetail.Rate));
        //可投金额
        value_total.setText(String.format("%.2f", expiredProjectDetail.AmountBalance) + "元");

        //判断借款期限的天标月标
        switch (expiredProjectDetail.DurationExchangeType) {
            case 1:
                //这里转成字符串的原因是防止出现NullPointerException崩溃
                time_value.setText(String.format("%d", expiredProjectDetail.Duration) + "天");
                countDate = 360;
                break;

            case 2:
                //这里转成字符串的原因是防止出现NullPointerException崩溃
                time_value.setText(String.format("%d", expiredProjectDetail.Duration) + "个月");
                countDate = 12;
                break;

            default:
                time_value.setText("");
                break;
        }

//        //万元收益
//        value_gain.setText(String.format("%.2f", (expiredProjectDetail.Rate + expiredProjectDetail.RewardRate) * 100));
        //融资总额
        value_invest_mini.setText(String.format("%.2f", expiredProjectDetail.AmountTotal) + "元");

        //奖励说明
        if (expiredProjectDetail.RewardRateDescn != null
                && !expiredProjectDetail.RewardRateDescn.equalsIgnoreCase("null")
                && !expiredProjectDetail.RewardRateDescn.equalsIgnoreCase("")) {
            value_description.setVisibility(View.VISIBLE);
            value_description.setText(expiredProjectDetail.RewardRateDescn);
        } else {
            value_description.setVisibility(View.GONE);
        }

        //投标进度条
        progress_percent.setProgress((int) (100 - ((float) expiredProjectDetail.AmountBalance / expiredProjectDetail.AmountTotal) * 100));
        //投资进度百分比
        value_percent.setText(String.format("%.2f", (1 - (float)expiredProjectDetail.AmountBalance/expiredProjectDetail.AmountTotal) * 100) + "%");
//        //投标人数
//        value_person.setText(347 + "");

        //还款方式
        payment_type.setText(expiredProjectDetail.PaymentType);

        //截止日期
        end_time.setText(expiredProjectDetail.ApplyEndTime);

        //设置按钮颜色
        setButtonColor(expiredProjectDetail.Status);
    }

    /******************* 设置按钮不可用时的颜色和可用状态 *****************************/
    private void setButtonColor(int status) {
        if (status != 20) {
            button_buy.setBackgroundResource(R.drawable.button_project_gray);
            button_buy.setTextColor(getResources().getColor(R.color.project_button_gray));
            button_buy.setEnabled(false);
        }
    }


    /**
     * 获取用户银行卡信息
     */
    private void loadUserDetailInfoData(){
        //如果银行卡信息不为空，则停止请求
        if (App.saveUserInfo.getAccess_Token() == null || App.mUserDetailInfo != null && App.mUserDetailInfo.BankCard != null) {
            return;
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserBankMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    int status = response.getInt("status");
                    if (PackageUtil.getRequestStatus(response, Activity_ExpiredProject_Introduction.this)) {
                        JSONObject data = PackageUtil.getResultObject(response);
                        //判断JSON对象是否为空
                        if (data != null) {
                            UserDetailInfo mUserDetailInfo = App.mGson.fromJson(data.toString(), UserDetailInfo.class);
                            //将用户信息写入静态数据区
                            if (mUserDetailInfo != null) {
                                App.mUserDetailInfo = mUserDetailInfo;
                            }
                        }
                    }
                    //去登陆
                    else if (status == -5) {
                        gotoLoginActivity();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(Activity_ExpiredProject_Introduction.this, "网络异常，请稍后再试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
        requestQueue.add(jsonRequest);
    }


    /****************** 跳转到登录页面 ***************/
    private void gotoLoginActivity() {
        UmengUtil.eventById(Activity_ExpiredProject_Introduction.this, R.string.bid_buy_n_login);
        Intent newIntent = new Intent(Activity_ExpiredProject_Introduction.this, Activity_Login.class);
        startActivity(newIntent);
    }

    /******************* 点击事件 ******************/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //本息保障
            case R.id.layout_investment:
                showAdviserTips("该项目受福利金融风险保证金保障");
                break;

            //体验标说明
            case R.id.layout_description:
                Activity_WebView.launche(Activity_ExpiredProject_Introduction.this, "体验标说明", "http://cnt.flh001.com/2016/01/27/tiyanbiaoshuoming/");
                break;

            //专属客服
            case R.id.layout_customer_service:
                //如果ID存在
                String userId = null;
                if (App.mUserDetailInfo != null) {
                    userId = App.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(this).showCustomer(userId);
                break;

            //立即购买按钮
            case R.id.button_buy:
                clickButton();
                break;
        }
    }

    /********************* 风控提示 ************************/
    private void showAdviserTips(String tips) {
        dialogWidget = new DialogWidget(Activity_ExpiredProject_Introduction.this, getAdviserView(tips), true);
        dialogWidget.show();
    }

    protected View getAdviserView(String tips) {
        return MyMaskFullScreenView.getInstance(Activity_ExpiredProject_Introduction.this, tips, new MyMaskFullScreenView.OnClikListener() {
            @Override
            public void onViewClik() {
                dialogWidget.dismiss();
                dialogWidget = null;
            }
        }).getView();
    }

    /***************** 购买函数 **********************/
    private void clickButton() {
        //判断是否存在买标数据
        if (expiredProjectDetail == null) {
            ToastUtil.alert(Activity_ExpiredProject_Introduction.this, "无法获取买标数据");
            return;
        }
        //判断可投金额是否大于0元
        if (expiredProjectDetail.AmountBalance <= 0) {
            ToastUtil.alert(Activity_ExpiredProject_Introduction.this, "可投金额为0元，该体验标不能投");
            return;
        }
        //还没有登陆时，跳转至登陆页面
        if (App.saveUserInfo.getAccess_Token() == null) {
            startActivity(new Intent(Activity_ExpiredProject_Introduction.this, Activity_Login_Introduction.class));
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
        dialogWidget = new DialogWidget(Activity_ExpiredProject_Introduction.this, IdentifyAlertView.getInstance(Activity_ExpiredProject_Introduction.this, new IdentifyAlertView.OnClikListener() {
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
        Intent newIntent = new Intent(Activity_ExpiredProject_Introduction.this, Activity_Authentication.class);
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

    /********************* 去买标页面 **********************/
    private void gotoBuyPrjectPage() {
        Activity_Project_Buy.launche(Activity_ExpiredProject_Introduction.this, packageId, title, 1, countDate, expiredProjectDetail, true);
        finish();
    }

    /********************* 转圈提示 **************************/
    //显示转圈提示
    private void showProgressBar(String title) {
        if(null == progressDialog)progressDialog= new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage(title);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    //隐藏转圈提示
    private void hideProgressBar(){
        if(null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}