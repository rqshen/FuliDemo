package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bcb.common.app.App;
import com.bcb.R;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.presentation.view.activity.Activity_Join_Company;
import com.bcb.presentation.view.activity.Activity_Loan;
import com.bcb.presentation.view.activity.Activity_LoanRequest_Borrow;
import com.bcb.presentation.view.activity.Activity_Login;
import com.bcb.presentation.view.activity.Activity_Money_Flowing_Water;
import com.bcb.presentation.view.activity.Activity_Account_Setting;
import com.bcb.presentation.view.activity.Activity_Authentication;
import com.bcb.presentation.view.activity.Activity_Coupons;
import com.bcb.presentation.view.activity.Activity_Recharge_Second;
import com.bcb.presentation.view.activity.Activity_Setting_Pay_Pwd;
import com.bcb.presentation.view.activity.Activity_Trading_Record;
import com.bcb.presentation.view.activity.Activity_Withdraw;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.IdentifyAlertView;
import com.bcb.presentation.view.custom.CustomDialog.MyMaskFullScreenView;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONObject;

public class Frag_User extends Frag_Base implements OnClickListener {
    //标题
	private TextView  title_text;

	private LinearLayout  trading_record, money_flow_water, borrow_money, coupons, invitation_award, account_setting;

	private Context ctx;

	private Button recharge_btn, withdraw_btn;

	private TextView value_earn, value_balance, value_back, value_total;
    private LinearLayout freezeAmountImage;

	private UserWallet mUserWallet;

	private UserDetailInfo mUserDetailInfo;

    private boolean firstLoadWallet;
    private boolean firstLoadBankInfo;

    //加载数据状态
    private boolean loadingStatus;
    //加载数据失败的状态
    private boolean loadingError;

    //加入公司
    private LinearLayout user_company_layout;
    private ImageView joinCompany;
    private TextView user_join_name;
    private TextView user_comany_shortname;
    //广播
    private Receiver receiver;
    //专属客服
    private RelativeLayout layout_customer_service;
    //专属客服提示
    private ImageView user_customer_tips;
    //电话客服
    private RelativeLayout layout_phone_service;

    //对话框
    private DialogWidget dialogWidget;
    private AlertView alertView;
    //刷新
    private PullToRefreshLayout refreshLayout;

    private BcbRequestQueue requestQueue;

	public Frag_User() {
		super();
	}

    @SuppressLint("ValidFragment")
	public Frag_User(Context ctx) {
		super();	
		this.ctx = ctx;
	}

    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);
        firstLoadWallet = true;
        firstLoadBankInfo =true;
        loadingStatus = true;
        loadingError = false;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_user, container, false);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.ctx = view.getContext();
        requestQueue = BcbNetworkManager.newRequestQueue(ctx);
        //注册监听器
        receiver = new Receiver();
        ctx.registerReceiver(receiver, new IntentFilter("com.bcb.update.company.joined"));
        ctx.registerReceiver(receiver, new IntentFilter("com.bcb.login.success"));
        //标题
        title_text = (TextView) view.findViewById(R.id.title_text);
        title_text.setText("我");

        //加入公司
        joinCompany = (ImageView) view.findViewById(R.id.join_company);
        joinCompany.setVisibility(View.GONE);
        joinCompany.setOnClickListener(this);

        //已经加入公司的LinearLayout及其元素
        user_company_layout = (LinearLayout) view.findViewById(R.id.user_company_layout);
        user_company_layout.setVisibility(View.GONE);
        user_join_name = (TextView) view.findViewById(R.id.user_join_name);
        user_join_name.setText("");
        user_comany_shortname = (TextView) view.findViewById(R.id.user_comany_shortname);
        user_comany_shortname.setText("");

        // 总资产
        value_earn = (TextView) view.findViewById(R.id.value_earn);
        // 用户余额
        value_balance = (TextView) view.findViewById(R.id.value_balance);
        // 待收本息
        value_back = (TextView) view.findViewById(R.id.value_back);
        // 冻结金额
        value_total = (TextView) view.findViewById(R.id.value_total);
        //叹号
        freezeAmountImage = (LinearLayout) view.findViewById(R.id.freeze_amount_image);
        freezeAmountImage.setOnClickListener(this);

        //充值
        recharge_btn = (Button) view.findViewById(R.id.recharge_btn);
        recharge_btn.setOnClickListener(this);
        //提现
        withdraw_btn = (Button) view.findViewById(R.id.withdraw_btn);
        withdraw_btn.setOnClickListener(this);

        //投资记录
        trading_record = (LinearLayout) view.findViewById(R.id.trading_record);
        trading_record.setOnClickListener(this);
        //资金流水
        money_flow_water = (LinearLayout) view.findViewById(R.id.money_flow_water);
        money_flow_water.setOnClickListener(this);
        //借款
        borrow_money = (LinearLayout) view.findViewById(R.id.borrow_money);
        borrow_money.setOnClickListener(this);
        //优惠券
        coupons = (LinearLayout)view.findViewById(R.id.coupons);
        coupons.setOnClickListener(this);
        //邀请奖励
        invitation_award = (LinearLayout)view.findViewById(R.id.invitation_award);
        invitation_award.setOnClickListener(this);
        //账号设置
        account_setting = (LinearLayout)view.findViewById(R.id.account_setting);
        account_setting.setOnClickListener(this);
        //专属客服
        layout_customer_service = (RelativeLayout) view.findViewById(R.id.layout_customer_service);
        layout_customer_service.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(false);
                String userId = null;
                //判断是否为空
                if (App.mUserDetailInfo != null) {
                    userId = App.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(ctx).showCustomer(userId);
            }
        });
        //专属客服提示
        user_customer_tips = (ImageView) view.findViewById(R.id.user_customer_tips);
        user_customer_tips.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(false);
            }
        });
        //第一次安装的时候才显示专属提示
        if (!App.saveConfigUtil.isNotFirstRun()) {
            showPopupWindow(true);
            App.saveConfigUtil.setNotFirstRun(true);
        }
        //电话客服
        layout_phone_service = (RelativeLayout) view.findViewById(R.id.layout_phone_service);
        layout_phone_service.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneService();
            }
        });
        //刷新
        (view.findViewById(R.id.loadmore_view)).setVisibility(View.GONE);
        refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(ctx)) {
                    //刷新跟第一次加载是一样的效果，都是要求请求
                    firstLoadWallet = true;
                    loadUserWallet();
                    firstLoadBankInfo = true;
                    loadUserBankInfo();
                } else {
                    ToastUtil.alert(ctx, "网络异常，请稍后重试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
        if (App.saveUserInfo.getAccess_Token() != null) {
            refreshLayout.autoRefresh();
        }
    }

    private void showPhoneService() {
        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
        ibuilder.setTitle("是否电联客服？");
        ibuilder.setMessage("客服热线：020-38476886");
        ibuilder.setPositiveButton("立即联系", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:020-38476886"));
                ctx.startActivity(intent);
                alertView.dismiss();
            }
        });
        ibuilder.setNegativeButton("取消", null);
        alertView = ibuilder.create();
        alertView.show();
    }

	@Override
	public void onStart()  {
        super.onStart();
		if (App.saveUserInfo.getAccess_Token() != null) {
            //加载用户余额
            loadUserWallet();
            //加载用户会员银行卡信息
            loadUserBankInfo();
        }
        //Token不存在时，则表示没有登陆
        else {
            //设置banner
            setupJoinCompanyMessage();
            //初始化余额信息
            showData();
        }
	}

    @Override
    public void onResume() {
        super.onResume();
    }

    /************** 请求获取用户余额信息 *************************/
	//加载用户余额信息
	private void loadUserWallet() {
        //第一次加载Fragment的时候，要请求数据，新创建Fragment和刷新都算第一次加载
        if (firstLoadWallet) {
            firstLoadWallet = false;
            requestUserWallet();
        }
        //如果不是第一次加载数据，则先从静态数据区中加载数据（如果存在的时候），不存在则从服务器中获取
        //这里是防止用户退出，数据清空了，然后重新登录，出现数据错误
        else {
            //判断是否为空
            if (App.mUserWallet == null) {
                requestUserWallet();
            } else {
                showData();
                refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        }
	}

    private void requestUserWallet() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        mUserWallet = App.mGson.fromJson(data.toString(), UserWallet.class);
                    }
                    if (null != mUserWallet) {
                        //先将数据写入全局静态数据区
                        App.mUserWallet = mUserWallet;
                        //显示数据
                        showData();
                    }
                } else {
                    // ToastUtil.alert(ctx, "网络好像有问题，请稍后重试");
                }
                loadingStatus = false;
                refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(ctx, "网络异常，请稍后重试");
                refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                loadingError = true;
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
        requestQueue.add(jsonRequest);
    }

    //从静态数据区中取出数据
	private void showData() {
        if (App.mUserDetailInfo == null) {
            value_earn.setText("0.00");
            value_balance.setText("0.00");
            value_back.setText("0.00");
            value_total.setText("0.00");
            return;
        }
        //总资产
		value_earn.setText("" + String.format("%.2f", App.mUserWallet.getTotalAsset()));
        //账户余额
        if (App.mUserWallet.getBalanceAmount() <= 0) {
            value_balance.setText("0.00");
        } else {
            value_balance.setText("" + String.format("%.2f", App.mUserWallet.getBalanceAmount()));
        }
        //待收本息
        value_back.setText("" + String.format("%.2f", App.mUserWallet.getIncomingMoney()));
        //冻结金额
		value_total.setText("" + String.format("%.2f", App.mUserWallet.getFreezeAmount()));
	}


    /********************* 获取用户银行卡信息 ***************************/
    //获取用户银行卡信息
	private void loadUserBankInfo() {
        //第一次加载Fragment的时候，则获取网络数据，新创建Fragment和刷新都算第一次加载
        if (firstLoadBankInfo) {
            firstLoadBankInfo = false;
            requestUserBankMessage();
        }
        //不是第一次记载的时候，从静态数据区获取数据
        else {
            //如果静态数据区没有数据，则请求
            if (App.mUserDetailInfo == null) {
                requestUserBankMessage();
            }
            //静态数据区存在数据，则直接赋值，不用再请求了
            else {
                mUserDetailInfo = App.mUserDetailInfo;
                //加载用户加入公司的信息
                setupJoinCompanyMessage();
            }
        }
	}

    private void requestUserBankMessage() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserBankMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //获取用户银行卡信息
                    //判断JSON对象是否为空
                    if (data != null) {
                        mUserDetailInfo = App.mGson.fromJson(data.toString(), UserDetailInfo.class);
                    }
                    //用户银行卡信息不为空，则将其存放到静态数据区
                    if (null != mUserDetailInfo) {
                        //将获取到的银行卡数据写入静态数据区中
                        App.mUserDetailInfo = mUserDetailInfo;
                        //加载用户加入公司的信息
                        setupJoinCompanyMessage();
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
        requestQueue.add(jsonRequest);
    }

    //加载用户加入公司的信息
    private void setupJoinCompanyMessage() {
        //如果mUserDetailInfo为空，则表示没有登陆
        if (App.mUserDetailInfo == null) {
            joinCompany.setVisibility(View.VISIBLE);
            user_company_layout.setVisibility(View.GONE);
            user_comany_shortname.setText("");
            user_join_name.setText("");
            return;
        }
        //如果加入公司信息不为空并且状态值为10(通过)的时候，则显示用户名和加入公司的缩写
        if (App.mUserDetailInfo.MyCompany != null) {
            joinCompany.setVisibility(View.GONE);
            user_company_layout.setVisibility(View.VISIBLE);
            //审核中
            if (App.mUserDetailInfo.MyCompany.Status == 5) {
                user_comany_shortname.setText(mUserDetailInfo.MyCompany.getShortName() + "(审核中)");
                user_join_name.setText(mUserDetailInfo.RealName);
            }
            //审核通过
            else if (App.mUserDetailInfo.MyCompany.Status == 10) {
                user_comany_shortname.setText(mUserDetailInfo.MyCompany.getShortName());
                user_join_name.setText(mUserDetailInfo.RealName);
            } else {
                joinCompany.setVisibility(View.GONE);
                user_company_layout.setVisibility(View.GONE);
            }

        }
        //如果加入公司信息为空的时候，则要判断是否要隐藏Banner
        else {
            //根据标志为判断是否隐藏加入公司Banner
            if (App.viewJoinBanner) {
                joinCompany.setVisibility(View.VISIBLE);
            } else {
                joinCompany.setVisibility(View.GONE);
                user_company_layout.setVisibility(View.GONE);
                user_join_name.setText("");
                user_comany_shortname.setText("");
            }
        }
    }


    //点击事件
	@Override
	public void onClick(View v) {
        //除了专属客服和电话客服之外的职位都要在点击之前登陆
        if (App.saveUserInfo.getAccess_Token() == null) {
            Activity_Login.launche(ctx);
            return;
        }
		switch (v.getId()) {
            //加入公司
            case R.id.join_company:
                UmengUtil.eventById(ctx, R.string.self_auth_c);
                toJoinCompany();
                break;

            //冻结金额提示
            case R.id.freeze_amount_image:
                showFreezeAmountDialog();
                break;

            //点击充值按钮
            case R.id.recharge_btn:
                rechargeMoney();
                break;

            //点击提现按钮
            case R.id.withdraw_btn:
                withdrawMoney();
                break;

            //投资记录
            case R.id.trading_record:
                UmengUtil.eventById(ctx, R.string.self_tzjl);
                Activity_Trading_Record.launche(ctx);
                break;

            //资金流水
            case R.id.money_flow_water:
                UmengUtil.eventById(ctx, R.string.self_zjls);
                Activity_Money_Flowing_Water.launche(ctx);
                break;

            //借款
            case R.id.borrow_money:
                UmengUtil.eventById(ctx, R.string.self_borrow);
                loanMainPage();
                break;

            // 优惠券
            case R.id.coupons:
                UmengUtil.eventById(ctx, R.string.self_coupon);
                checkCoupon();
                break;

            // 邀请奖励
            case R.id.invitation_award:
                UmengUtil.eventById(ctx, R.string.self_invate);
                invitationAward();
                break;

            // 账号设置
            case R.id.account_setting:
                accountSetting();
                break;
        }
	}

    //显示冻结金额对话框
    private void showFreezeAmountDialog() {
        dialogWidget = new DialogWidget(ctx, getFreezeAmountView(), true);
        dialogWidget.show();
    }
    protected View getFreezeAmountView() {
        return MyMaskFullScreenView.getInstance(ctx, "冻结金额包括：提现中资金、投标中资金、借款保证金等", new MyMaskFullScreenView.OnClikListener() {
            @Override
            public void onViewClik() {
                dialogWidget.dismiss();
                dialogWidget = null;
            }
        }).getView();
    }

    //加入公司
    private void toJoinCompany() {
        //如果未认证，则去认证
        if (mUserDetailInfo == null || mUserDetailInfo.HasCert == false) {
            popCertDialog();
        }
        //否则需要判断MyCompany字段
        else {
            //未申请
            if (App.mUserDetailInfo.MyCompany == null) {
                Activity_Join_Company.launche(ctx);
            }
            //审核中
            else if (App.mUserDetailInfo.MyCompany.Status == 5) {
                companyAlertView("您的认证申请正在审核", "预计2个工作日内完成，请耐心等候");
            }
            //拉黑
            else if (App.mUserDetailInfo.MyCompany.Status == 15) {
                companyAlertView("提示", "你已被拉入黑名单\n详情请咨询工作人员");
            }
        }
    }


    /************************ 去认证 ******************************/
    private void popCertDialog() {
        dialogWidget = new DialogWidget(ctx, IdentifyAlertView.getInstance(ctx, new IdentifyAlertView.OnClikListener() {
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

    //跳转到认证界面
    private void gotoAuthenticationActivity() {
        Intent newIntent = new Intent(ctx, Activity_Authentication.class);
        startActivityForResult(newIntent, 10);
    }

    /*************************** 审核中 *************************/
    private void companyAlertView(String title, String contentMessage) {
        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
        ibuilder.setTitle(title);
        ibuilder.setMessage(contentMessage);
        ibuilder.setPositiveButton("知道了", null);
        alertView = ibuilder.create();
        alertView.show();
    }

    //借款
    private void loanMainPage() {
        //判断状态，如果获取数据中或者获取数据失败，都表示依旧要加载数据
        if (loadingStatus) {
            //加载数据失败
            if (loadingError) {
                ToastUtil.alert(ctx, "获取用户数据失败，请刷新重试");
            }
            //获取用户数据过程中
            else {
                ToastUtil.alert(ctx, "正在获取用户数据");
            }
            return;
        }

        //存在用户信息
        if(null != mUserDetailInfo){
            //用户还没认证时，先去认证
            if(!App.mUserDetailInfo.HasCert || App.mUserDetailInfo.BankCard == null){
                showAlertView("提示", "您仍未认证，请先认证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ctx, Activity_Authentication.class));
                        alertView.dismiss();
                        alertView = null;
                    }
                });
            } else {
                //        Intent newIntent = new Intent(ctx, Activity_Loan.class);
                Intent newIntent = new Intent(ctx, Activity_LoanRequest_Borrow.class);
                startActivity(newIntent);
            }
        }
        //不存在用户信息时，先去认证
        else {
            startActivity(new Intent(ctx, Activity_Authentication.class));
        }

    }

    //充值
    private void rechargeMoney() {
        //判断状态，如果获取数据中或者获取数据失败，都表示依旧要加载数据
        if (loadingStatus) {
            if (loadingError) {
                ToastUtil.alert(ctx, "获取用户数据失败，请刷新重试");
            } else {
                ToastUtil.alert(ctx, "正在获取用户数据");
            }
            return;
        }

        //只有存在用户信息、有银行卡号并且已经认证三要素都满足时才去充值界面，否则先获取数据，
        if (App.mUserDetailInfo != null && App.mUserDetailInfo.HasCert && App.mUserDetailInfo.BankCard != null) {
            UmengUtil.eventById(ctx, R.string.self_charge);
            Activity_Recharge_Second.launche(ctx);
        } else {
            UmengUtil.eventById(ctx, R.string.auth_act);
            gotoAuthenticationActivity();
        }
    }

    //提现
    private void withdrawMoney() {
        UmengUtil.eventById(ctx, R.string.self_crash);
        //判断状态，如果获取数据中或者获取数据失败，都表示依旧要加载数据
        if (loadingStatus) {
            //加载数据失败
            if (loadingError) {
                ToastUtil.alert(ctx, "获取用户数据失败，请刷新重试");
            }
            //获取用户数据过程中
            else {
                ToastUtil.alert(ctx, "正在获取用户数据");
            }
            return;
        }

        //存在用户信息
        if(null != mUserDetailInfo){
            //用户还没认证时，先去认证
            if(!App.mUserDetailInfo.HasCert || App.mUserDetailInfo.BankCard == null){
                showAlertView("提示", "您仍未认证，请先认证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ctx, Activity_Authentication.class));
                        alertView.dismiss();
                        alertView = null;
                    }
                });
            }
            //未设置交易密码，先去设置密码
            else if (!App.mUserDetailInfo.HasTradePassword) {
                startActivity(new Intent(ctx, Activity_Setting_Pay_Pwd.class));
            }
            //去提现
            else {
                startActivity(new Intent(ctx, Activity_Withdraw.class));
            }
        }
        //不存在用户信息时，先去认证
        else {
            startActivity(new Intent(ctx, Activity_Authentication.class));
        }
    }

    //优惠券
    private void checkCoupon() {
        startActivityForResult(new Intent(ctx, Activity_Coupons.class), 1);
    }

    //邀请奖励
    private void invitationAward() {
        ToastUtil.alert(ctx, "即将上线，敬请期待");
    }

    //账号设置
    private void accountSetting() {
        UmengUtil.eventById(ctx, R.string.self_setting);
        Activity_Account_Setting.launche(ctx);
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //选择优惠券返回
            case 1:
                if (data != null) {
                    Intent intent = new Intent();
                    //跳转至首页
                    intent.setAction("com.bcb.update.mainui");
                    ctx.sendBroadcast(intent);
                }
                break;

            //认证成功返回
            case 10:
                if (data != null) {
                    loadUserWallet();
                    loadUserBankInfo();
                }
                break;
        }
	}


    //广播
    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //加入公司
            if (intent.getAction().equals("com.bcb.update.company.joined")) {
                setupJoinCompanyMessage();
            }
            //登陆成功
            else if (intent.getAction().equals("com.bcb.login.success")) {
                refreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ctx.unregisterReceiver(receiver);
    }

    //提示对话框
    private void showAlertView(String titleName, String contentMessage, DialogInterface.OnClickListener onClickListener) {
        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
        ibuilder.setTitle(titleName);
        ibuilder.setMessage(contentMessage);
        ibuilder.setPositiveButton("立即设置", onClickListener);
        ibuilder.setNegativeButton("取消", null);
        alertView = ibuilder.create();
        alertView.show();
    }

    private void showPopupWindow(boolean visible) {
        if (visible){
            user_customer_tips.setVisibility(View.VISIBLE);
        } else {
            user_customer_tips.setVisibility(View.GONE);
        }
    }
}