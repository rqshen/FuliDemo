package com.bcb.module.myinfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.BaseFragment;
import com.bcb.data.bean.StringEventBusBean;
import com.bcb.data.bean.UserBankCard;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.UserWallet;
import com.bcb.data.bean.WelfareDto;
import com.bcb.event.BroadcastEvent;
import com.bcb.module.discover.eliteloan.EliteLoanActivity;
import com.bcb.module.login.LoginActivity;
import com.bcb.module.myinfo.balance.BalanceActivity;
import com.bcb.module.myinfo.balance.FundCustodianAboutActivity;
import com.bcb.module.browse.FundCustodianWebActivity;
import com.bcb.module.myinfo.balance.recharge.RechargeActivity;
import com.bcb.module.myinfo.balance.trading.TradingRecordActivity;
import com.bcb.module.myinfo.balance.withdraw.WithdrawActivity;
import com.bcb.module.myinfo.joincompany.JoinCompanyActivity;
import com.bcb.module.myinfo.myfinancial.MyFinancialActivity;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.module.myinfo.totalassets.TotalAssetsActivity;
import com.bcb.module.myinfo.welfare.DailyWelfareActivity;
import com.bcb.module.myinfo.welfare.DailyWelfareStaticActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.presentation.view.activity.A_MySecurity;
import com.bcb.presentation.view.activity.A_Slb;
import com.bcb.module.myinfo.setting.AccountSettingActivity;
import com.bcb.presentation.view.activity.Activity_Privilege_Money;
import com.bcb.presentation.view.activity.Activity_Trading_Record;
import com.bcb.presentation.view.activity.Activity_TuoGuan_HF;
import com.bcb.presentation.view.activity._Coupons;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.AlertView.UpdateDialog;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.presentation.view.custom.PullableView.PullableScrollView;
import com.bcb.util.DialogUtil;
import com.bcb.util.DownloadUtils;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MQCustomerManager;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.util.UmengUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.List;


/**
 * Created by ruiqin.shen
 * 类说明：我的信息页面
 */
public class MyInfoFragment extends BaseFragment implements OnClickListener {
    //标题
    private TextView title_text, value_earn_all;
    ImageView layout_update_line, iv_red;
    RelativeLayout layout_update;
    RelativeLayout rl_ye, rl_lc, rl_yhq, rl_about;
    TextView tv_update, value_lc;
    LinearLayout ll_qd;
    private Context ctx;
    //我的保险
    RelativeLayout layout_security;
    ImageView iv_head;
    private TextView value_earn, value_balance, value_back, value_total, value_yhq;
    private UserWallet mUserWallet;
    private UserDetailInfo mUserDetailInfo;
    PullableScrollView layout_scrollview;

    //加载数据状态
    private boolean loadingStatus = true;
    //加载数据失败的状态
    private boolean loadingError = false;

    //加入公司
    private LinearLayout join_company;
    //	private ImageView joinCompany;
    private TextView user_join_name;
    private TextView user_comany_shortname;
    //广播
    private Receiver receiver;
    //专属客服
    private RelativeLayout layout_customer_service;
    //电话客服
    private RelativeLayout layout_phone_service;

    //对话框
    private DialogWidget dialogWidget;
    private AlertView alertView;
    //刷新
    private PullToRefreshLayout refreshLayout;

    private BcbRequestQueue requestQueue;

    public MyInfoFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public MyInfoFragment(Context ctx) {
        super();
        this.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.ctx = view.getContext();
        EventBus.getDefault().register(this);
        requestQueue = MyApplication.getInstance().getRequestQueue();
        //注册监听器
        receiver = new Receiver();
        ctx.registerReceiver(receiver, new IntentFilter("com.bcb.update.company.joined"));
        ctx.registerReceiver(receiver, new IntentFilter("com.bcb.login.success"));
        ctx.registerReceiver(receiver, new IntentFilter("com.bcb.logout.success"));
        //标题
        title_text = (TextView) view.findViewById(R.id.title_text);
        title_text.setText("我");

        iv_red = (ImageView) view.findViewById(R.id.iv_red);
        ll_qd = (LinearLayout) view.findViewById(R.id.ll_qd);
        join_company = (LinearLayout) view.findViewById(R.id.join_company);
        tv_update = (TextView) view.findViewById(R.id.tv_update);
        value_yhq = (TextView) view.findViewById(R.id.value_yhq);
        value_earn_all = (TextView) view.findViewById(R.id.value_earn_all);
        value_lc = (TextView) view.findViewById(R.id.value_lc);
        iv_head = (ImageView) view.findViewById(R.id.iv_head);
        layout_update = (RelativeLayout) view.findViewById(R.id.layout_update);
        layout_security = (RelativeLayout) view.findViewById(R.id.layout_security);
        rl_ye = (RelativeLayout) view.findViewById(R.id.rl_ye);
        rl_lc = (RelativeLayout) view.findViewById(R.id.rl_lc);
        rl_yhq = (RelativeLayout) view.findViewById(R.id.rl_yhq);
        rl_about = (RelativeLayout) view.findViewById(R.id.rl_about);
        rl_ye.setOnClickListener(this);
        rl_lc.setOnClickListener(this);
        rl_yhq.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        iv_red.setOnClickListener(this);
        join_company.setOnClickListener(this);

        ll_qd.setOnClickListener(this);
        layout_security.setOnClickListener(this);
        layout_update_line = (ImageView) view.findViewById(R.id.layout_update_line);
        layout_update.setOnClickListener(this);
        if (MyApplication.isNeedUpdate && MyApplication.versionBean != null) {
            tv_update.setText("发现新版本 V" + MyApplication.versionBean.Version);
        } else {
            tv_update.setText("");
//			layout_update.setVisibility(View.GONE);
//			layout_update_line.setVisibility(View.GONE);
        }

        view.findViewById(R.id.ll_test).setOnClickListener(this);
        //加入公司
        layout_scrollview = (PullableScrollView) view.findViewById(R.id.layout_scrollview);
        user_join_name = (TextView) view.findViewById(R.id.user_join_name);

        user_comany_shortname = (TextView) view.findViewById(R.id.user_comany_shortname);
        // 总资产
        value_earn = (TextView) view.findViewById(R.id.value_earn);
        // 用户余额
        value_balance = (TextView) view.findViewById(R.id.value_balance);
        // 待收本息
        value_back = (TextView) view.findViewById(R.id.value_back);
        // 冻结金额
        value_total = (TextView) view.findViewById(R.id.value_total);
        //叹号
        view.findViewById(R.id.freeze_amount_image)
                .setOnClickListener(this);
        //充值
        view.findViewById(R.id.recharge_btn)
                .setOnClickListener(this);
        //提现
        view.findViewById(R.id.withdraw_btn)
                .setOnClickListener(this);
        //投资记录
        view.findViewById(R.id.trading_record)
                .setOnClickListener(this);
        //资金流水
        view.findViewById(R.id.money_flow_water)
                .setOnClickListener(this);
        //借款
        view.findViewById(R.id.borrow_money)
                .setOnClickListener(this);
        //优惠券
        view.findViewById(R.id.coupons)
                .setOnClickListener(this);
        //特权本金
        view.findViewById(R.id.privilege_money)
                .setOnClickListener(this);
        //胜利包
        view.findViewById(R.id.managed_slb)
                .setOnClickListener(this);
        view.findViewById(R.id.managed_funds)
                .setOnClickListener(this);
        //账号设置
        view.findViewById(R.id.layout_account_settting)
                .setOnClickListener(this);
        //专属客服
        layout_customer_service = (RelativeLayout) view.findViewById(R.id.layout_customer_service);
        layout_customer_service.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = null;
                //判断是否为空
                if (MyApplication.mUserDetailInfo != null) {
                    userId = MyApplication.mUserDetailInfo.getCustomerId();
                }
                MQCustomerManager.getInstance(ctx).showCustomer(userId);
            }
        });

        //第一次安装的时候才显示专属提示
        if (!MyApplication.saveConfigUtil.isNotFirstRun()) {
            //            showPopupWindow(true);
            MyApplication.saveConfigUtil.setNotFirstRun(true);
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
                    requestUserDetailInfo();
                    requestUserWallet();
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
        refreshLayout.autoRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i("bqt", "【MyInfoFragment--onResume】");
        if (mUserDetailInfo != null && mUserDetailInfo.HasOpenEgg)
            iv_red.setVisibility(View.INVISIBLE);
        else iv_red.setVisibility(View.VISIBLE);
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

    //从静态数据区中取出数据
    private void showData() {
        LogUtil.i("bqt", "【】" + MyApplication.saveUserInfo.getAccess_Token());

        if (MyApplication.saveUserInfo.getAccess_Token() == null) {
            //总资产
            value_earn.setText("0.00");
            value_earn_all.setText("累计收益0元");

            //账户余额
            value_balance.setText("0.00");
            //待收本息

            value_back.setText("0.00");
            //冻结金额
            value_total.setText("0.00");
            value_yhq.setText("0张");
            value_lc.setText("0.00");
        } else if (MyApplication.mUserWallet != null) {
            //总资产
            value_earn.setText("" + String.format("%.2f", MyApplication.mUserWallet.getTotalAsset()));
            //累计收益
            value_earn_all.setText("累计收益" + String.format("%.2f", MyApplication.mUserWallet.InvestIncome) + "元");
            //账户余额
            value_balance.setText("￥" + String.format("%.2f", MyApplication.mUserWallet.BalanceAmount));
            //待收本息
            value_lc.setText("￥" + String.format("%.2f", MyApplication.mUserWallet.InvestingAmount + MyApplication.mUserWallet.LeftInterest));
            //冻结金额
            value_total.setText("" + String.format("%.2f", MyApplication.mUserWallet.getFreezeAmount()));
            if (MyApplication.mUserDetailInfo != null)
                value_yhq.setText(MyApplication.mUserDetailInfo.CouponCount + "张");
            if (MyApplication.mUserWallet.getBalanceAmount() == 0) value_balance.setText("去充值");
        }
    }

    //加载用户加入公司的信息
    private void setupJoinCompanyMessage() {
        //没有登陆
        if (MyApplication.saveUserInfo.getAccess_Token() == null) {
            user_join_name.setText("您好，请登录/注册");
            user_comany_shortname.setVisibility(View.GONE);
            iv_head.setImageDrawable(getResources().getDrawable(R.drawable.iv_my_head2));
            return;
        }
        if (MyApplication.mUserDetailInfo == null) return;
        user_comany_shortname.setVisibility(View.VISIBLE);
        iv_head.setImageDrawable(getResources().getDrawable(R.drawable.iv_my_head));
        //审核通过
        if (MyApplication.mUserDetailInfo != null && !TextUtils.isEmpty(mUserDetailInfo.UserName)) {
            user_join_name.setText("您好，" + mUserDetailInfo.UserName);
        } else {
            user_join_name.setText("您好，" + MyApplication.saveUserInfo.getLocalPhone());
        }

        if (MyApplication.mUserDetailInfo.MyCompany != null && !TextUtils.isEmpty(mUserDetailInfo.MyCompany.ShortName)) {
            user_comany_shortname.setText(mUserDetailInfo.MyCompany.ShortName);
            user_comany_shortname.setCompoundDrawablesWithIntrinsicBounds(//
                    getActivity().getResources().getDrawable(R.drawable.rz), null, null, null);
        } else {
            user_comany_shortname.setText("加入我的公司拿员工专属福利>");
            user_comany_shortname.setCompoundDrawables(null, null, null, null);
        }

        layout_scrollview.scrollTo(0, 0);
    }

    //******************************************************************************************
    DownloadCompleteReceiver downloadCompleteReceiver;
    String fileName;
    File apkFile;

    UpdateDialog updateDialog;

    private void showVersionDialog2() {
        updateDialog = new UpdateDialog(ctx) {
            @Override
            public void onClick() {
                super.onClick();
                registerReceiver();
                dismiss();
            }
        };
        updateDialog.setValues(View.VISIBLE, false, getTips());
        updateDialog.show();
    }

    public String getTips() {
        StringBuilder sb = new StringBuilder("");
        List<String> tips = MyApplication.versionBean.Tips;
        if (tips != null && tips.size() == 1) {
            updateDialog.setTVGravity(Gravity.CENTER);
            return tips.get(0);
        } else if (tips != null && tips.size() > 1) {
            updateDialog.setTVGravity(Gravity.LEFT);
            for (int i = 0; i < tips.size(); i++) {
                sb.append("" + (i + 1) + "、" + tips.get(i) + "\n");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        } else return null;
    }

    private void registerReceiver() {
        Toast.makeText(ctx, "正在下载新版本安装包", Toast.LENGTH_SHORT)
                .show();
        downloadCompleteReceiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);//下载完成的动作
        ctx.registerReceiver(downloadCompleteReceiver, intentFilter);
        fileName = "fljr-" + MyApplication.versionBean.Increment + ".apk";
        apkFile = new File(Environment.getExternalStorageDirectory()
                .getPath() + DownloadUtils.FILE_PATH + File.separator + fileName);
        DownloadUtils.downLoadFile(ctx, MyApplication.versionBean.Url, fileName);//开始下载
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                LogUtil.i("bqt", "下载完毕");
                installApk(context);
                if (downloadCompleteReceiver != null) {
                    ctx.unregisterReceiver(downloadCompleteReceiver);
                    downloadCompleteReceiver = null;
                }
            }
        }
    }

    private void installApk(Context context) {
        Intent mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

    //******************************************************************************************
    //点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_update) {
            if (MyApplication.isNeedUpdate && MyApplication.versionBean != null)
                showVersionDialog2();
            else Toast.makeText(ctx, "已是最新版本", Toast.LENGTH_SHORT).show();
            return;
        }
        //除了专属客服和电话客服之外的职位都要在点击之前登陆
        if (MyApplication.saveUserInfo.getAccess_Token() == null) {
            LoginActivity.launche(ctx);
            return;
        }
        switch (v.getId()) {
            //加息
            case R.id.ll_qd:
                getStatisticsData();
                break;
            //总资产
            case R.id.ll_test:
                startActivity(new Intent(ctx, TotalAssetsActivity.class));
                break;
            //余额
            case R.id.rl_ye:
                startActivity(new Intent(ctx, BalanceActivity.class));
                break;
            //理财
            case R.id.rl_lc:
                startActivity(new Intent(ctx, MyFinancialActivity.class));
                break;
            //优惠券
            case R.id.rl_yhq:
                if (isLoading()) return;
                UmengUtil.eventById(ctx, R.string.self_coupon);
                startActivityForResult(new Intent(ctx, _Coupons.class), 1);
                break;
            //关于
            case R.id.rl_about:
                ProjectDetailActivity.launche(ctx, "关于福利金融", UrlsOne.AboutConpany);
                break;
            //我的保险
            case R.id.layout_security:
                startActivity(new Intent(ctx, A_MySecurity.class));
                break;
            //加入公司
            case R.id.join_company:
                UmengUtil.eventById(ctx, R.string.self_auth_c);
                toJoinCompany();
                break;
            //冻结金额提示
            case R.id.freeze_amount_image:
                Toast.makeText(ctx, "投资未计息本金、借款保证金等", Toast.LENGTH_SHORT)
                        .show();
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
                if (isLoading()) return;
                UmengUtil.eventById(ctx, R.string.self_tzjl);
                Activity_Trading_Record.launche(ctx);
                break;

            //资金流水，交易明细
            case R.id.money_flow_water:
                if (isLoading()) return;
                UmengUtil.eventById(ctx, R.string.self_zjls);
                TradingRecordActivity.launche(ctx);
                break;

            //借款--员工贷
            case R.id.borrow_money:
                UmengUtil.eventById(ctx, R.string.loan_blank);
                loanMainPage();
                break;

            // 优惠券
            case R.id.coupons:
                if (isLoading()) return;
                UmengUtil.eventById(ctx, R.string.self_coupon);
                startActivityForResult(new Intent(ctx, _Coupons.class), 1);
                break;

            // 特权本金
            case R.id.privilege_money:
                if (isLoading()) return;
                UmengUtil.eventById(ctx, R.string.self_invate);
                Activity_Privilege_Money.launch(ctx);
                break;

            // 资金托管
            case R.id.managed_funds:
                managedFunds();
                break;
            // 圣力宝
            case R.id.managed_slb:
                slbOpen();
                break;

            // 账号设置
            case R.id.layout_account_settting:
                UmengUtil.eventById(ctx, R.string.self_setting);
                AccountSettingActivity.launche(ctx);
                break;
        }
    }

    //加入公司
    private void toJoinCompany() {
        if (mUserDetailInfo == null || !mUserDetailInfo.HasOpenCustody) {
            startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
        } else {//判断MyCompany字段
            if (MyApplication.mUserDetailInfo.MyCompany == null) {//未申请
                JoinCompanyActivity.launche(ctx);//跳转到加入公司
            } else if (MyApplication.mUserDetailInfo.MyCompany.Status == 5) { //审核中
                companyAlertView("您的认证申请正在审核", "预计2个工作日内完成，请耐心等候");
            } else if (MyApplication.mUserDetailInfo.MyCompany.Status == 15) {//拉黑
                companyAlertView("提示", "你已被拉入黑名单\n详情请咨询工作人员");
            } else if (MyApplication.mUserDetailInfo.MyCompany.Status == 10) {
                changeCompany();
            }
        }
    }

    private void changeCompany() {
        showAlertView2("提示", "您需要修改公司认证信息吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertView.dismiss();
                alertView = null;
                JoinCompanyActivity.launche(ctx);
            }
        });
    }

    //借款
    private void loanMainPage() {
        //已开通托管
        if (isLoading()) return;
        if (MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.HasOpenCustody)
            startActivity(new Intent(ctx, EliteLoanActivity.class));
        else startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
    }

    //充值
    private void rechargeMoney() {
        if (isLoading()) return;
        //已开通托管
        if (MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.HasOpenCustody)
            startActivity(new Intent(ctx, RechargeActivity.class));
        else startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
    }

    //提现
    //绑定提现卡后请求我的银行卡接口同样会返回银行卡信息，不过【IsQPCard】为【false】
    // {"status":1,"message":"","result":{"BankCode":"CIB","BankName":"兴业银行","CardNumber":"6229081111111111112","IsQPCard":false}}
    private void withdrawMoney() {
        //未开通托管
        if (isLoading()) return;
        //没开通托管
        if (MyApplication.mUserDetailInfo == null || !MyApplication.mUserDetailInfo.HasOpenCustody) {
            startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
            return;
        }
        //用户还没绑卡
        if (MyApplication.mUserDetailInfo.BankCard == null) {
            showAlertView("您还没指定提现卡哦", "该银行卡将作为账户唯一提现银行卡", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestBandCard();
                    alertView.dismiss();
                    alertView = null;
                }
            });
        } else startActivity(new Intent(ctx, WithdrawActivity.class));
    }

    //资金托管
    private void managedFunds() {
        if (isLoading()) return;
        if (MyApplication.mUserDetailInfo.HasOpenCustody) {//已开通托管
            startActivity(new Intent(ctx, Activity_TuoGuan_HF.class));
        } else startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
    }

    //生利宝
    private void slbOpen() {
        if (isLoading()) return;
        //已开通托管
        if (MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.HasOpenCustody)
            startActivity(new Intent(ctx, A_Slb.class));
        else startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
    }

    //判断状态，如果获取数据中或者获取数据失败，都表示依旧要加载数据
    private boolean isLoading() {
        if (loadingStatus) {
            //加载数据失败
            if (loadingError) ToastUtil.alert(ctx, "获取用户数据失败，请刷新重试");
                //获取用户数据过程中
            else ToastUtil.alert(ctx, "您的网络不稳定，加载中");
            return true;
        }
        return false;
    }

    /***************************
     * 审核中
     *************************/
    private void companyAlertView(String title, String contentMessage) {
        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
        ibuilder.setTitle(title);
        ibuilder.setMessage(contentMessage);
        ibuilder.setPositiveButton("知道了", null);
        alertView = ibuilder.create();
        alertView.show();
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
        }
    }

    //广播
    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //加入公司
            if (intent.getAction()
                    .equals("com.bcb.update.company.joined")) {
                setupJoinCompanyMessage();
            }
            //登陆成功
            else if (intent.getAction().equals("com.bcb.login.success")) {
//				refreshLayout.autoRefresh();
            }        //注销成功
            else if (intent.getAction().equals("com.bcb.logout.success")) {
                LogUtil.i("bqt", "【Receiver】【onReceive】注销");
                showData();
                setupJoinCompanyMessage();
                layout_scrollview.scrollTo(0, 0);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ctx.unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
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

    //提示对话框
    private void showAlertView2(String titleName, String contentMessage, DialogInterface.OnClickListener onClickListener) {
        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
        ibuilder.setTitle(titleName);
        ibuilder.setMessage(contentMessage);
        ibuilder.setPositiveButton("立即修改", onClickListener);
        ibuilder.setNegativeButton("取消", null);
        alertView = ibuilder.create();
        alertView.show();
    }

    //*****************************************                            请求服务器
    // ****************************************

    /**
     * 用户信息
     */
    private void requestUserDetailInfo() {

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "用户信息返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        mUserDetailInfo = MyApplication.mGson.fromJson(data.toString(), UserDetailInfo.class);
                        //将获取到的银行卡数据写入静态数据区中
                        MyApplication.mUserDetailInfo = mUserDetailInfo;
                        requestUserBankCard();
                        requestUserSecurity();
                        showData();
                        //加载用户加入公司的信息
                        setupJoinCompanyMessage();
                        if (mUserDetailInfo.HasOpenEgg) iv_red.setVisibility(View.INVISIBLE);
                        else iv_red.setVisibility(View.VISIBLE);
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

    /**
     * 用户钱包
     */
    private void requestUserWallet() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "请求用户资产账户返回：" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    if (data != null) {
                        //注意数据结构变了，2016-7-26
                        mUserWallet = MyApplication.mGson.fromJson(data.toString(), UserWallet.class);
                        MyApplication.mUserWallet = mUserWallet;
                        showData();
                    }
                }
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

    /**
     * 用户银行卡
     */
    private void requestUserBankCard() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UrlUserBand, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "绑定的银行卡：" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    if (data != null && MyApplication.mUserDetailInfo != null) {
                        MyApplication.mUserDetailInfo.BankCard = MyApplication.mGson.fromJson(data.toString(), UserBankCard.class);
                        showData();
                    }
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

    /**
     * 保险
     */
    private void requestUserSecurity() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.MYINSURANCE, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【获取车险团险等数据】：" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    MyApplication.mUserDetailInfo.CarInsuranceIndexPage = response.optJSONObject("result").optString("CarInsuranceIndexPage");
                    MyApplication.mUserDetailInfo.CarInsuranceMyOrderPage = response.optJSONObject("result").optString("CarInsuranceMyOrderPage");
                    MyApplication.mUserDetailInfo.GroupInsuranceUrl = response.optJSONObject("result").optString("GroupInsuranceUrl");
                    boolean che = TextUtils.isEmpty(MyApplication.mUserDetailInfo.CarInsuranceIndexPage);
                    LogUtil.i("bqt", "【刷新后是否没有获取到车险】" + che);
                    if (che) EventBus.getDefault().post(new StringEventBusBean("CXGONE"));
                    else EventBus.getDefault().post(new StringEventBusBean("CXVISIBLE"));
                    showData();
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

    /**
     * 绑定提现卡
     */
    private void requestBandCard() {
        String requestUrl = UrlsTwo.UrlBandCard;
        String encodeToken = TokenUtil.getEncodeToken(ctx);
        LogUtil.i("bqt", "【RechargeActivity】【BandCard】请求路径：" + requestUrl);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>
                () {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "绑定提现卡：" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    try {
                        /** 后台返回的JSON对象，也是要转发给汇付的对象 */
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result != null) {
                            //网页地址
                            String postUrl = result.optString("PostUrl");
                            result.remove("PostUrl");//移除这个参数
                            //传递的 参数
                            String postData = HttpUtils.jsonToStr(result.toString());
                            //跳转到webview
                            FundCustodianWebActivity.launche(ctx, "绑定提现卡", postUrl, postData);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【FundCustodianAboutActivity】【OpenAccount】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(ctx, response.optString("message"), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【RechargeActivity】【BandCard】网络异常，请稍后重试" + error.toString());
            }
        });
        MyApplication.getInstance()
                .getRequestQueue()
                .add(jsonRequest);
    }

    //接收事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BroadcastEvent event) {
        String flag = event.getFlag();
        if (!TextUtils.isEmpty(flag)) {
            LogUtil.i("bqt", "【MyInfoFragment】【onEventMainThread】状态：" + flag);
            switch (flag) {
                case BroadcastEvent.LOGOUT:
                    LogUtil.i("bqt", "【wocao】");
                    showData();
                    setupJoinCompanyMessage();
                    layout_scrollview.scrollTo(0, 0);
                    break;
                case BroadcastEvent.LOGIN:
                case BroadcastEvent.REFRESH:
                    refreshLayout.autoRefresh();
                    break;
            }
        }
    }

    private WelfareDto welfareDto;//完整数据
    Dialog loadingDialog;

    /**
     * 请求统计数据
     */
    private void getStatisticsData() {
        loadingDialog = DialogUtil.createLoadingDialog(ctx);
        loadingDialog.show();
        JSONObject obj = new JSONObject();
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.DailyWelfareData, obj, TokenUtil.getEncodeToken(ctx), true, new
                BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("bqt", "首页：请求统计数据" + response.toString());
                        loadingDialog.dismiss();
                        try {
                            boolean status = PackageUtil.getRequestStatus(response, ctx);
                            if (status) {
                                JSONObject resultObject = response.getJSONObject("result");
                                welfareDto = MyApplication.mGson.fromJson(resultObject.toString(), WelfareDto.class);
                                //更新UI
                                LogUtil.d("统计数据", welfareDto.toString());

                                //滚动文字
                                String[] rotateValues = new String[welfareDto.getJoinList().size()];
                                for (int i = 0; i < welfareDto.getJoinList().size(); i++) {
                                    rotateValues[i] = welfareDto.getJoinList().get(i).get("Title");
                                }
                                //参与人数
                                String str = String.format("今天已有%s位用户获得加息", welfareDto.getTotalPopulation());
                                //加息数值大于0说明已经参加过直接跳转
                                if (welfareDto.getRate() > 0) {
                                    DailyWelfareStaticActivity.launche(ctx, String.valueOf(welfareDto.getRate()), String.valueOf(welfareDto.getTotalInterest()), str, rotateValues);
                                } else {
                                    DailyWelfareActivity.launche(ctx, rotateValues, welfareDto.getTotalPopulation(), welfareDto.getTotalInterest());
                                }
                            } else {
                                ToastUtil.alert(ctx, response.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.alert(ctx, "请求失败，请稍后重试");
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                        loadingDialog.dismiss();
                        ToastUtil.alert(ctx, "请求失败，请稍后重试");
                    }
                });
        requestQueue.add(jsonRequest);
    }

}