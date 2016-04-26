package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.data.bean.BuyProjectSuccess;
import com.bcb.data.bean.project.ExpiredProjectDetail;
import com.bcb.data.bean.project.SimpleProjectDetail;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.common.net.UrlsOne;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MoneyTextUtil;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TextUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.IdentifyAlertView;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;
import com.bcb.presentation.view.custom.PayPasswordView.PayPasswordView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cain on 16/1/28.
 */
public class Activity_Project_Buy extends Activity_Base implements View.OnClickListener {

    private static final String TAG = "Activity_Project_Buy";

    //标题
    private String title;

    //投资金额
    private EditText invest_money;
    //选择优惠券
    private RelativeLayout layout_coupon;
    //优惠券信息
    private TextView coupon_description;
    private ImageView coupon_arrow;
    //账户余额
    private TextView wallet_money;
    //充值
    private LinearLayout layout_recharge;
    //平台服务费
    private LinearLayout project_service_fee;
    //预期收益描述
    private TextView earnings_description;
    //预期收益
    private TextView prospective_earning;
    //立即购买按钮
    private Button button_buy;
    //忘记密码
    private LinearLayout forgetPayPassWord;
    //出错信息
    private TextView error_tips;


    private RelativeLayout layout_money;
    //是否体验标
    private boolean isExpired = false;
    private ExpiredProjectDetail expiredProjectDetail;
    //普通标的
    private SimpleProjectDetail mSimpleProjectDetail;
    //CouponType默认为0，为1的时候表示体验标， 为2表示体验券
    private int CouponType = 0;
    private int countDate = 0;
    //银行卡信息
    public UserDetailInfo mUserDetailInfo;
    //账户余额
    private UserWallet mUserWallet;
    //判断是否正在购买中
    private boolean isPaying = false;
    //输入密码的对话框
    private DialogWidget enterPswDialog;

    private DialogWidget certDialog;

    //转圈提示
    private ProgressDialog progressDialog;

    //标的Id
    private String packageId = "";
    //
    private String CouponId, CouponAmount, CouponMinAmount;

    //广播
    private Receiver buySuccessReceiver;

    private AlertView alertView;

    private BcbRequestQueue requestQueue;

    //默认构造函数，用来传递普通标的数据
    public static void launche(Context ctx,
                               String pid,
                               String title,
                               int CouponType,
                               int countDate,
                               SimpleProjectDetail simpleProjectDetail, boolean isExpired) {
        Intent intent = new Intent();
        intent.putExtra("pid", pid);
        intent.putExtra("title", title);
        intent.putExtra("CouponType", CouponType);
        intent.putExtra("countDate", countDate);
        intent.putExtra("isExpired", isExpired);
        intent.setClass(ctx, Activity_Project_Buy.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("SimpleProjectDetail", simpleProjectDetail);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }
    //加载体验标数据
    public static void launche(Context ctx,
                               String pid,
                               String title,
                               int CouponType,
                               int countDate,
                               ExpiredProjectDetail expiredProjectDetail,
                               boolean isExpired) {
        Intent intent = new Intent();
        intent.putExtra("pid", pid);
        intent.putExtra("title", title);
        intent.putExtra("CouponType", CouponType);
        intent.putExtra("countDate", countDate);
        intent.putExtra("isExpired", isExpired);
        intent.setClass(ctx, Activity_Project_Buy.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ExpiredProjectDetail", expiredProjectDetail);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Project_Buy.this);
        packageId = this.getIntent().getStringExtra("pid");
        title = this.getIntent().getStringExtra("title");
        CouponType = this.getIntent().getIntExtra("CouponType", 0);
        countDate = this.getIntent().getIntExtra("countDate", 0);
        mSimpleProjectDetail = (SimpleProjectDetail)this.getIntent().getSerializableExtra("SimpleProjectDetail");
        expiredProjectDetail = (ExpiredProjectDetail)this.getIntent().getSerializableExtra("ExpiredProjectDetail");
        isExpired = this.getIntent().getBooleanExtra("isExpired", false);
        setBaseContentView(R.layout.activity_project_buy);
        setLeftTitleListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_back2);
                finish();
            }
        });
        setTitleValue(title);
        requestQueue = BcbNetworkManager.newRequestQueue(this);
        //初始化页面
        setupView();
        //注册广播
        setupRegister();
        //获取优惠券张数
        if (App.saveUserInfo.getAccess_Token() != null) {
            //获取优惠券张数
            getCouponCount();
            //加载用户余额
            getUserBanlance();
        }
    }

    /********************** 注册广播 **************************/
    private void setupRegister() {
        //注册广播
        buySuccessReceiver = new Receiver();
        //注册更改余额成功广播
        IntentFilter rechargeChange = new IntentFilter("com.bcb.money.change.success");
        registerReceiver(buySuccessReceiver, rechargeChange);
        //注册交易密码设置成功广播
        IntentFilter setTragePasswd = new IntentFilter("com.bcb.passwd.setted");
        registerReceiver(buySuccessReceiver, setTragePasswd);
    }

    /******************* 初始化页面 ***************************/
    private void setupView() {
        //体验标隐藏账户余额
        if (isExpired) {
            ((RelativeLayout)findViewById(R.id.layout_money)).setVisibility(View.GONE);
        }
        //投资金额
        invest_money = (EditText) findViewById(R.id.invest_money);
        invest_money.setTextColor(getResources().getColor(R.color.txt_gray));
        if (!isExpired && mSimpleProjectDetail != null) {
            invest_money.setHint(String.format("%d", (int)mSimpleProjectDetail.StartingAmount) + "元起投");
        } else if (isExpired && expiredProjectDetail != null) {
            invest_money.setHint(String.format("%d", (int)expiredProjectDetail.StartingAmount) + "元起投");
        }
        //如果是体验券，设置光标不可显示，并且设置提示——“请选择体验券”
        if ((CouponType & 1) == 1) {
            invest_money.setOnClickListener(this);
            invest_money.setCursorVisible(false);
            //强制隐藏键盘
            invest_money.setInputType(InputType.TYPE_NULL);
        }
        //不是体验券，设置光标的位置，添加输入监听器
        else {
            invest_money.setSelection(invest_money.getText().toString().length());
            invest_money.addTextChangedListener(mTextWatcher);
        }
            //选择优惠券
        layout_coupon = (RelativeLayout) findViewById(R.id.layout_coupon);
        layout_coupon.setOnClickListener(this);
        if (CouponType == 0) {
            layout_coupon.setVisibility(View.GONE);
        }
        //优惠券信息
        coupon_description = (TextView) findViewById(R.id.coupon_description);
        coupon_arrow = (ImageView) findViewById(R.id.coupon_arrow);
        coupon_arrow.setVisibility(View.GONE);
        //账户余额
        wallet_money = (TextView) findViewById(R.id.wallet_money);
        //充值
        layout_recharge = (LinearLayout) findViewById(R.id.layout_recharge);
        layout_recharge.setOnClickListener(this);
        //平台服务费
        project_service_fee = (LinearLayout) findViewById(R.id.project_service_fee);
        project_service_fee.setOnClickListener(this);
        //预期收益描述
        earnings_description = (TextView) findViewById(R.id.earnings_description);
        //预期收益
        prospective_earning = (TextView) findViewById(R.id.prospective_earning);
        //立即购买按钮
        button_buy = (Button) findViewById(R.id.button_buy);
        button_buy.setOnClickListener(this);
        //忘记密码
        forgetPayPassWord = (LinearLayout) findViewById(R.id.forgetPayPassWord);
        forgetPayPassWord.setOnClickListener(this);

        //出错信息，默认隐藏
        error_tips = (TextView) findViewById(R.id.error_tips);
        error_tips.setVisibility(View.GONE);
    }

    /**************** 输入监听器 ***********************/
    TextWatcher mTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            invest_money.setTextColor(Color.BLACK);
            String input = invest_money.getText().toString();
            if(!TextUtils.isEmpty(input)){
                error_tips.setVisibility(View.GONE);
                //如果不是体验券，则计算收益，在优惠那一栏中显示返现
                if ((CouponType & 1) != 1) {
                    // 实时去运算投资带来的收益
                    try {
                        float money = Float.valueOf(input.replace(",", ""));
                        if (!TextUtils.isEmpty(CouponMinAmount)) {
                            if (money >= Float.parseFloat(CouponMinAmount)) {
                                prospective_earning.setText("投资满" + CouponMinAmount + "返现" + CouponAmount + "元");
                            }
                        }
                        //计算收益
                        countEarnMoney(money);
                        //按钮状态
                        checkCommitBtnStatus(money);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                countEarnMoney(0);
                checkCommitBtnStatus(0);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void afterTextChanged(Editable s) {
            //获取值
            String textValue = invest_money.getText().toString();
            //清除分割符
            String tmpValue = textValue.replace(",", "");
            //如果输入的值为0，则替换成空字符串
            if (!tmpValue.equalsIgnoreCase("") && Float.parseFloat(tmpValue) == 0) {
                textValue = "";
            }
            invest_money.removeTextChangedListener(mTextWatcher);
            invest_money.setText(MoneyTextUtil.ConversionThousandUnit(textValue.replace(",", "")));
            invest_money.setSelection(invest_money.getText().toString().length());
            invest_money.addTextChangedListener(mTextWatcher);
        }
    };

    /*************** 设置按钮文字的样式 *****************/
    private void checkCommitBtnStatus(float money){
        //是否存在用户信息
        if (null != App.mUserWallet) {
            //如果不是体验券，则判断账户余额是否足够
            if ((CouponType & 1) != 1) {
                //账户余额是否充足
                if (App.mUserWallet.BalanceAmount < money) {
                    button_buy.setText("立即充值");

                } else {
                    button_buy.setText("立即购买");
                }
            }
            //是体验券
            else {
                button_buy.setText("立即购买");
            }
        }
    }

    //获取优惠券张数
    /*************** 获取优惠券张数 **********************/
    private void getCouponCount() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("PageNow", 1);
            obj.put("PageSize", 10);
            obj.put("Status", 1);
            //优先检查是否体验券(包括可能填错了，填了优惠券和体验券都有)
            if ((CouponType & 1) == 1) {
                obj.put("CouponType", 1);
            } else {
                obj.put("CouponType", CouponType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Select_Coupon, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean flag = PackageUtil.getRequestStatus(response, Activity_Project_Buy.this);
                    if(true == flag){
                        JSONObject obj = PackageUtil.getResultObject(response);
                        //显示优惠券数量
                        //判断JSON对象是否为空
                        if (obj != null) {
                            ShowCouponCount(obj.getString("TotalCount"));
                        }
                    }
                } catch (Exception e) {
                    LogUtil.d(TAG, "" + e.getMessage());
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.BCB_SELECT_COUPON_REQUEST);
        requestQueue.add(jsonRequest);
    }

    /**************************** 显示优惠券张数 ***********************************/
    protected void ShowCouponCount(String TotalCount) {
        int totalCount = Integer.parseInt(TotalCount);
        String couponName = "优惠券";
        if ((CouponType & 1) == 1) {
            couponName = "体验券";
        } else if ((CouponType & 2) == 2) {
            couponName = "现金券";
        }

        if (totalCount == 0){
            coupon_description.setText("您当前无可用" + couponName);
        }else{
            coupon_description.setText("您当前有(" + totalCount + ")张" + couponName);
            UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_coupon_avi);
        }

        if (!TextUtils.isEmpty(TotalCount)) {
            //不为0 时，始终显示箭头
            if (CouponType != 0) {
                coupon_arrow.setVisibility(View.VISIBLE);
            } else {
                if (totalCount == 0) {
                    coupon_arrow.setVisibility(View.GONE);
                } else {
                    coupon_arrow.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /*************** 获取用户银行卡信息 *********************/
    private void loadUserDetailInfoData(){
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserBankMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (PackageUtil.getRequestStatus(response, Activity_Project_Buy.this)) {
                        JSONObject data = PackageUtil.getResultObject(response);
                        //判断JSON对象是否为空
                        if (data != null) {
                            mUserDetailInfo = App.mGson.fromJson(data.toString(), UserDetailInfo.class);
                        }
                        //将用户信息写入静态数据区
                        if (mUserDetailInfo != null) {
                            App.mUserDetailInfo = mUserDetailInfo;
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
                ToastUtil.alert(Activity_Project_Buy.this, "网络异常，请稍后再试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
        requestQueue.add(jsonRequest);
    }


    /************ 获取用户余额 *********************/
    private void getUserBanlance() {
        showProgressBar("正在获取用户余额...");
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                try {
                    int status = response.getInt("status");
                    if (PackageUtil.getRequestStatus(response, Activity_Project_Buy.this)) {
                        JSONObject data = PackageUtil.getResultObject(response);
                        //判断JSON对象是否为空
                        if (data != null) {
                            mUserWallet = App.mGson.fromJson(data.toString(), UserWallet.class);
                        }
                        if (null != mUserWallet) {
                            App.mUserWallet = mUserWallet;
                            showUserBanlance();
                        }
                    } else if (status == -5) {
                        gotoLoginActivity();
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
        jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
        requestQueue.add(jsonRequest);
    }

    //显示用户余额
    private void showUserBanlance() {
        wallet_money.setText(String.format("%.2f", App.mUserWallet.BalanceAmount) + "元");
        button_buy.setText("立即购买");
    }


    /***************************** 计算收益 ***********************************/
    private void countEarnMoney(float moneyinput){
        // 如果传错了月标(1)、天标(2)，则不显示收益
        if (countDate != 0) {
            //显示值
            prospective_earning.setVisibility(View.VISIBLE);
            //如果输入金额为0时，不显示
            if(moneyinput == 0) {
                earnings_description.setText("预期收益");
                prospective_earning.setText("0元");
            } else {
                // 年化基本收益
                float shouyi;
                //计算奖励收益
                float jiangliamount;
                //奖励描述
                String description  = "";
                //如果是新手体验标
                if (isExpired) {
                    shouyi = ((moneyinput * expiredProjectDetail.Rate * 0.01f)/countDate) * expiredProjectDetail.Duration ;
                    jiangliamount = ((moneyinput * expiredProjectDetail.RewardRate)/countDate * 0.01f) * expiredProjectDetail.Duration;
                    if (expiredProjectDetail.RewardRateDescn != null && !expiredProjectDetail.RewardRateDescn.equalsIgnoreCase("null")) {
                        description = expiredProjectDetail.RewardRateDescn;
                    }
                }
                //不是新手体验标的时候
                else {
                    shouyi = ((moneyinput * mSimpleProjectDetail.Rate * 0.01f)/countDate)*mSimpleProjectDetail.Duration ;
                    jiangliamount = ((moneyinput * mSimpleProjectDetail.RewardRate)/countDate * 0.01f)*mSimpleProjectDetail.Duration;
                    if (mSimpleProjectDetail.RewardRateDescn != null && !mSimpleProjectDetail.RewardRateDescn.equalsIgnoreCase("null")) {
                        description = mSimpleProjectDetail.RewardRateDescn;
                    }
                }
                //设置奖励描述
                prospective_earning.setText(rewardDescription(description, shouyi, jiangliamount));
            }
        } else {
            prospective_earning.setVisibility(View.GONE);
        }
    }
    //设置奖励描述
    private String rewardDescription(String description, float shouyi, float jiangliamount) {
        String valueText = "";
        if (jiangliamount < 0.01) {
            valueText = TextUtil.delFloat(shouyi) + "元";
        } else {
            if (description != null && !description.equalsIgnoreCase("") && !description.equalsIgnoreCase("null")) {
                valueText = TextUtil.delFloat(shouyi + jiangliamount) + "元" +
                        "(含" + TextUtil.delFloat(jiangliamount) + "元" + "奖励)";
            } else {
                valueText = TextUtil.delFloat(shouyi + jiangliamount) + "元" +
                        "(含" + TextUtil.delFloat(jiangliamount) + "元" + description +"奖励)";
            }
        }
        return valueText;
    }

    /***************** 点击购买按钮 **********************/
    private void clickButton() {
        // 用户是否登录
        if (App.saveUserInfo.getAccess_Token() == null) {
            ToastUtil.alert(Activity_Project_Buy.this, "请先登录");
            gotoLoginActivity();
            return;
        }

        //判断是否获取了数据
        //属于新手体验标的情况
        if (isExpired) {
            if (expiredProjectDetail == null) {
                ToastUtil.alert(Activity_Project_Buy.this, "无法获取买标数据");
                return;
            }
            if (expiredProjectDetail.AmountBalance <=0) {
                error_tips.setVisibility(View.VISIBLE);
                error_tips.setText("可投金额为0，该标不能投");
                return;
            }
        }
        //不属于新手体验标的情况
        else {
            if (mSimpleProjectDetail == null) {
                ToastUtil.alert(Activity_Project_Buy.this, "无法获取买标数据");
                return;
            }
            //判断可投金额是否大于0元
            if (mSimpleProjectDetail.AmountBalance <= 0) {
                error_tips.setVisibility(View.VISIBLE);
                error_tips.setText("可投金额为0，该标不能投");
                return;
            }
        }

        //如果是新手体验标时，需要判断输入框是否为空
        if ((CouponType & 1) == 1) {
            // 判断是否输入金额
            String input_moneyStr = invest_money.getText().toString().replace(",", "");
            if(null == input_moneyStr || input_moneyStr.trim().equals("")){
                error_tips.setVisibility(View.VISIBLE);
                error_tips.setText("请选择体验券投标");
                return;
            }
            if (Float.parseFloat(input_moneyStr) <= 0) {
                error_tips.setVisibility(View.VISIBLE);
                error_tips.setText("请选择体验券投标");
                return;
            }
            //判断优惠券的金额大于体验标的剩余可投金额
            if (expiredProjectDetail.AmountBalance < Float.parseFloat(input_moneyStr)) {
                error_tips.setVisibility(View.VISIBLE);
                error_tips.setText("标的剩余可投金额不足，无法投标");
                return;
            }
        }
        //不是体验券的时候才判断是否输入金额、起投金额、单笔限额这些
        else {
            // 判断是否输入金额
            String input_moneyStr = invest_money.getText().toString().replace(",", "");
            if(null == input_moneyStr || input_moneyStr.trim().equals("")){
                error_tips.setVisibility(View.VISIBLE);
                error_tips.setText("请输入投资金额");
                return;
            }

            //输入金额
            float inputMoney = Float.parseFloat(invest_money.getText().toString().replace(",", ""));

            // 判断是否大于起投金额
            if (mSimpleProjectDetail.StartingAmount > 0) {
                if (inputMoney < mSimpleProjectDetail.StartingAmount) {
                    error_tips.setVisibility(View.VISIBLE);
                    error_tips.setText("当前输入小于起投金额" + (int)mSimpleProjectDetail.StartingAmount + "元");
                    return;
                }
            }

            // 判断输入金额是否超出单笔限额
            if (mSimpleProjectDetail.SingletonAmount > 0) {
                if (Float.parseFloat(invest_money.getText().toString().replace(",", "")) > mSimpleProjectDetail.SingletonAmount) {
                    error_tips.setVisibility(View.VISIBLE);
                    error_tips.setText("当前输入超出单笔限额" + (int)mSimpleProjectDetail.SingletonAmount + "元");
                    return;
                }
            }
            //要先认证用户信息，判断是否绑卡和设置了交易密码
            if(null != App.mUserDetailInfo){
                // 检测是否绑卡
                if(!App.mUserDetailInfo.HasBindCard){
                    popCertDialog();
                    return;
                }
                // 检测是否设置交易密码
                if (!App.mUserDetailInfo.HasTradePassword) {
                    // 弹出设置密码提示框
                    popSetPswDialog();
                    return;
                }
            }
            //判断用户余额是否大于输入金额
            if (App.mUserWallet != null) {
                if (App.mUserWallet.getBalanceAmount() < inputMoney) {
                    UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_n_money);
                    ToastUtil.alert(Activity_Project_Buy.this, "余额不足，请先充值");
                    Activity_Recharge_Second.launche(Activity_Project_Buy.this);
                    return;
                }
            } else {
                error_tips.setVisibility(View.VISIBLE);
                error_tips.setText("暂时无法获取用户余额");
                return;
            }
            //判断输入金额是否大于可投金额
            float moneyf = Float.valueOf(invest_money.getText().toString().replace(",", ""));
            if(moneyf > mSimpleProjectDetail.AmountBalance){
                error_tips.setVisibility(View.VISIBLE);
                error_tips.setText("超出项目可投金额");
                return;
            }
        }
        //买标
        popInputPswDialog(invest_money.getText().toString().replace(",", ""));
    }

    /****************** 跳转到登录页面 ***************/
    private void gotoLoginActivity() {
        UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_n_login);
        Intent newIntent = new Intent(Activity_Project_Buy.this, Activity_Login.class);
        startActivity(newIntent);
    }

    /***************** 设置交易密码 ***********************/
    private void popSetPswDialog() {
        UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_n_key);
        AlertView.Builder builder = new AlertView.Builder(this);
        builder.setTitle("您还没有设置交易密码");
        builder.setMessage("为保证您的资金安全,请先设置交易密码");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("立即设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Activity_Setting_Pay_Pwd.launche(Activity_Project_Buy.this);
                alertView.dismiss();
                alertView = null;
            }
        });
        alertView = builder.create();
        alertView.show();
    }

    /************************ 去认证 ******************************/
    private void popCertDialog() {
        UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_n_auth);
        certDialog = new DialogWidget(Activity_Project_Buy.this, IdentifyAlertView.getInstance(Activity_Project_Buy.this, new IdentifyAlertView.OnClikListener() {
            @Override
            public void onCancelClick() {
                certDialog.dismiss();
                certDialog = null;
            }

            @Override
            public void onSureClick() {
                certDialog.dismiss();
                certDialog = null;
                //去认证
                gotoAuthenticationActivity();
            }
        }).getView());
        certDialog.show();
    }


    //跳转到认证界面
    private void gotoAuthenticationActivity() {
        Intent newIntent = new Intent(Activity_Project_Buy.this, Activity_Authentication.class);
        startActivityForResult(newIntent, 10);
    }

    /***************** 输入交易密码 *************************/
    private void popInputPswDialog(String moneyInvest) {
        UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_confirm);
        enterPswDialog = new DialogWidget(Activity_Project_Buy.this, getDecorViewDialog(moneyInvest));
        enterPswDialog.show();
    }

    //显示输入交易密码界面
    protected View getDecorViewDialog(String moneyInvest) {
        return PayPasswordView.getInstance(moneyInvest, this, new PayPasswordView.OnPayListener() {

            @Override
            public void onSurePay(String password) {
                enterPswDialog.dismiss();
                enterPswDialog = null;
                onPostBuyProject(password);
            }

            @Override
            public void onCancelPay() {
                enterPswDialog.dismiss();
                enterPswDialog = null;
                Toast.makeText(getApplicationContext(), "交易已取消", Toast.LENGTH_SHORT).show();

            }
        }).getView();
    }

    /***************** 购买请求 ******************************/
    private void onPostBuyProject(String psw){
        //是否请求过一次
        if(isPaying)return;
        isPaying = true;
        showProgressBar("正在买标，请耐心等候...");
        JSONObject data = new JSONObject();
        float money = Float.valueOf(invest_money.getText().toString().replace(",", ""));
        try {
            data.put("OrderAmount", money);
            data.put("PackageId", packageId);
            data.put("TradePassword", psw);
            data.put("Platform", "2");

            //如果没有优惠券，输入金额小于优惠券的最小使用金额
            if (!TextUtils.isEmpty(CouponMinAmount)) {
                if (Float.valueOf(invest_money.getText().toString().replace(",", "")) < Float.parseFloat(CouponMinAmount)) {
                    CouponId = null;
                }
            } else {
                CouponId = null;
            }
            //传CounonId不能为空字符串
            data.put("CouponId", CouponId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UrlBuyProject, data, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                isPaying = false;
                if(PackageUtil.getRequestStatus(response, Activity_Project_Buy.this)){
                    JSONObject obj = PackageUtil.getResultObject(response);
                    BuyProjectSuccess successInfo = null;
                    UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_succ);
                    //判断JSON对象是否为空
                    if (obj != null) {
                        successInfo = App.mGson.fromJson(obj.toString(), BuyProjectSuccess.class);
                    }
                    if (successInfo != null) {
                        onPostSuccess(successInfo);
                        ToastUtil.alert(Activity_Project_Buy.this, "投资成功");
                    }
                } else {
                    UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_n_key2);
                    try {
                        String message = response.getString("message");
                        //如果返回错误信息不为空的时候，显示错误信息
                        if (!TextUtils.isEmpty(message)) {
                            error_tips.setVisibility(View.VISIBLE);
                            error_tips.setText(message);
                        } else {
                            error_tips.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                onPostFail();
            }
        });
        jsonRequest.setTag(BcbRequestTag.UrlBuyProjectTag);
        requestQueue.add(jsonRequest);
    }

    public void onPostSuccess(BuyProjectSuccess successInfo){
        UmengUtil.eventById(this, R.string.buy_success);
        Activity_BuyProject_Success.launche(this, successInfo);
        finish();
    }

    public void onPostFail(){
        isPaying = false;
        hideProgressBar();
    }

    /********************* 转圈提示 **************************/
    //显示转圈提示
    private void showProgressBar(String title) {
        if(null == progressDialog) progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
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


    /******************************* 点击事件 ****************************/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //点击选择优惠券
            case R.id.invest_money:
            case R.id.layout_coupon:
                //先判断是否登录
                if (App.saveUserInfo.getAccess_Token() == null) {
                    gotoLoginActivity();
                }
                //判断优惠券类型，跳转
                else {
                    UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_coupon);
                    if ((CouponType & 1) == 1) {
                        startupActivity(1);
                    } else {
                        startupActivity(CouponType);
                    }
                }
                break;

            //投资信息费
            case R.id.project_service_fee:
                UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_info3);
                if (isExpired) {
                    showServiceFee(expiredProjectDetail.Duration, expiredProjectDetail.DurationExchangeType, expiredProjectDetail.Rate, expiredProjectDetail.RewardRate);
                } else {
                    showServiceFee(mSimpleProjectDetail.Duration, mSimpleProjectDetail.DurationExchangeType, mSimpleProjectDetail.Rate, mSimpleProjectDetail.RewardRate);
                }
                break;

            //点击充值
            case R.id.layout_recharge:
                UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_bug_charge);
                gotoRechargePage();
                break;

            //点击购买
            case R.id.button_buy:
                UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_act2);
                clickButton();
                break;
            //忘记密码
            case R.id.forgetPayPassWord:
                UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_f_key);
                Activity_Forget_Pwd.launche(Activity_Project_Buy.this);
                break;

            default:
                break;
        }
    }

    /************************** 去充值 *****************************************/
    private void gotoRechargePage() {
        // 用户是否登录
        if (App.saveUserInfo.getAccess_Token() == null) {
            ToastUtil.alert(Activity_Project_Buy.this, "请先登录");
            gotoLoginActivity();
            return;
        }
        //要先认证用户信息，判断是否绑卡和设置了交易密码
        if(null != App.mUserDetailInfo){
            // 检测是否绑卡
            if(!App.mUserDetailInfo.HasBindCard){
                popCertDialog();
                return;
            }
            // 检测是否设置交易密码
            if (!App.mUserDetailInfo.HasTradePassword) {
                // 弹出设置密码提示框
                popSetPswDialog();
                return;
            }
        }
        //去充值
        Activity_Recharge_Second.launche(Activity_Project_Buy.this);
    }

    /************************** 跳转到选择优惠券页面 **********************/
    private void startupActivity(int couponType){
        Intent newIntent = new Intent(Activity_Project_Buy.this, Activity_Select_Coupon.class);
        String newInput = invest_money.getText().toString().replace(",", "");
        float newInvestAmount = 0;
        if (!TextUtils.isEmpty(newInput)) {
            newInvestAmount = Float.parseFloat(newInput);
        }
        newIntent.putExtra("investAmount", newInvestAmount);
        newIntent.putExtra("CouponType", couponType);
        startActivityForResult(newIntent, 1);
    }

    /*************** 点击投资服务费图标实现蒙版效果 *****************/
    private void showServiceFee(int duration, int durationExchangeType, float rate, float rewardRate) {
        //计算信息服务费
        double fee = 0.0;
        switch (durationExchangeType) {
            case 1:
                fee = (rate + rewardRate) * duration / 360 * 0.1;
                break;

            case 2:
                fee = (rate + rewardRate) * duration / 12 * 0.1;
                break;

            default:
                break;
        }

        //对话框
        certDialog = new DialogWidget(Activity_Project_Buy.this, getServiceFeeView("该标收取投资金额的" + String.format("%.4f", fee) + "% 作为信息服务费"), true);
        certDialog.show();
    }

    protected View getServiceFeeView(String tips) {
        return MyMaskFullScreenView.getInstance(Activity_Project_Buy.this, "信息服务费", tips, new MyMaskFullScreenView.OnClikListener() {
            @Override
            public void onViewClik() {
                certDialog.dismiss();
                certDialog = null;
            }
        }).getView();
    }

    //返回优惠券选择结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //选择优惠券返回
            case 1:
                if (data != null) {
                    UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_coupon_use);
                    CouponId = data.getStringExtra("CouponId");
                    CouponAmount = data.getStringExtra("CouponAmount");
                    CouponMinAmount = data.getStringExtra("CouponMinAmount");
                    if ((CouponType & 1) == 1) {
                        error_tips.setVisibility(View.GONE);
                        coupon_description.setText(data.getStringExtra("ConditionDescn"));
                        float amountMoney = data.getFloatExtra("Amount", 0);
                        //将体验券金额显示成整数
                        invest_money.setText((int) amountMoney + "");
                        //计算收益
                        countEarnMoney(amountMoney);
                    } else if ((CouponType & 2) == 2) {
                        error_tips.setVisibility(View.GONE);
                        coupon_description.setText("投资满" + CouponMinAmount + "返现" + CouponAmount + "元");
                    }
                }
                break;

            //认证成功返回
            case 10:
                if (data != null) {
                    //加载银行卡信息
                    loadUserDetailInfoData();
                    //加载用户信息
                    getCouponCount();
                }
                break;

            default:
                break;
        }
    }

    //注册广播，买标成功之后，刷新余额
    class Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            //充值成功
            if (intent.getAction().equals("com.bcb.money.change.success")) {
                getUserBanlance();//加载用户余额
            }
            //设置交易密码成功
            else if (intent.getAction().equals("com.bcb.passwd.setted")) {
                loadUserDetailInfoData(); //更新用户信息
            }
        }
    }


    //暂存标的订单号的信息
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //订单号
        savedInstanceState.putString("PackageId", packageId);
    }

    //取出暂存的订单号信息
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //获取标的订单号
        packageId = savedInstanceState.getString("PackageId");
    }


    //重写点击返回按钮，发送广播并销毁Activity对象
    @Override
    public void onBackPressed() {
        UmengUtil.eventById(Activity_Project_Buy.this, R.string.bid_buy_back2);
        finish();
    }

    //销毁广播
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(buySuccessReceiver);
    }
}