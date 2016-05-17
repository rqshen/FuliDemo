package com.bcb.presentation.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.loan.LoanDurationListBean;
import com.bcb.data.bean.loan.LoanPeriodWithRateBean;
import com.bcb.data.bean.loan.LoanRequestInfoBean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.MyActivityManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ScreenUtils;
import com.bcb.data.util.SpinnerWheelUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.dg.spinnerwheel.WheelVerticalView;
import com.dg.spinnerwheel.adapters.ArrayWheelAdapter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cain on 15/12/24.
 */
public class Activity_LoanRequest_Borrow extends Activity_Base implements View.OnClickListener {
    private static final String TAG = "Activity_LoanRequest_Borrow";

    private static final int SCROLL = 100;//滚屏文字

    private static final int couponType = 16;
    //利息抵扣券的ID
    private String CouponId;
    //利息抵扣券金额
    private int InterestAmount = 0;
    //利息抵扣券最小使用金额
    private int InterestMinAmount = 0;
    //利息抵扣券使用描述
    private String InterestDescn = "";
    //利息抵扣券张数
    private int CouponCount = 0;

    //借款Banner
    private ImageView banner_image;
    //借款金额
    private EditText loan_amount;

    //借款用途
    private TextView loan_purposes;
    private List<String> purposes_types;
    private int purposeStatus = 1;//默认借款期限为置业首付，对应值为1

    //借款期限
    private TextView loan_duration;
    private List<String> duration_types;
    private List<LoanDurationListBean> durationList;
    private int durationStatus = 1;//默认借款期限为1个月，借款期限为 1
    int durationIndex = 0;   //借款列表下标位置

    //还款期数
    private TextView loan_period;
    private List<String> period_types;
    List<LoanPeriodWithRateBean> periodList;
    private int loanIndex = 0;//还款位置
    private int periodStatus = 1;//默认还款期数为1

    //还款方案
    private TextView loan_programme;

    //申请福利补贴
    private RelativeLayout layout_coupon_select;
    private ImageView coupon_select_image;

    //是否申请了福利补贴
    private boolean statusSubsidy = false;
    //是否选择了利息抵扣券
    private boolean statusSelectCoupon = false;
    //利息抵扣券
    private RelativeLayout layout_interest;
    //利息抵扣券描述
    private TextView value_interest;
    //如何获得补贴？
    private TextView loan_gain;

    //滚屏文字
    private WheelVerticalView value_rotate;
    //滚屏计时器
    private Timer adTimer;
    private TimerTask adTimerTask;
    //是否停止滚动
    private boolean isPause;
    private ArrayWheelAdapter<String> adapter;

    //客服
    private LinearLayout layout_customer_service;
    //立即申请借款按钮
    private Button bottoButton;
    //借款申请信息
    private LoanRequestInfoBean loanRequestInfo;

    //转圈提示
    ProgressDialog progressDialog;
    private AlertView alertView;

    //网络请求队列
    private BcbRequestQueue requestQueue;

    //刷新
    private PullToRefreshLayout refreshLayout;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCROLL:
                    value_rotate.scroll(1, 1000);
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理Activity栈，用于忘记密码的时候，跳转至登陆界面之前销毁栈中所有的Activity
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_LoanRequest_Borrow.this);
        setBaseContentView(R.layout.activity_loanrequest_borrow);
        setLeftTitleVisible(true);
        setTitleValue("借款信息");
        setRightTitleValue("我的借款", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoListPage();
                finish();
            }
        });
        //创建请求队列
        requestQueue = BcbNetworkManager.newRequestQueue(this);
        //初始化Banner
        setupBanner();
        //初始化刷新控件
        setupRefreshLayout();
        //获取验证信息
        getLoanCertification();
    }

    //初始化Banner
    private void setupBanner() {
        //借款banner
        banner_image = (ImageView) findViewById(R.id.banner_image);
        //根据Banner的宽高比进行等比缩放
        ViewGroup.LayoutParams params = banner_image.getLayoutParams();
        int width = ScreenUtils.getScreenDispaly(Activity_LoanRequest_Borrow.this)[0];
        params.height= width * 608 / 1440;
        params.width = width;
        banner_image.setLayoutParams(params);
    }

    //借款列表页
    private void gotoListPage() {
        UmengUtil.eventById(Activity_LoanRequest_Borrow.this, R.string.loan_my);
        Intent listIntent = new Intent(Activity_LoanRequest_Borrow.this, Activity_LoanList.class);
        startActivity(listIntent);
    }

    //初始化刷新控件
    private void setupRefreshLayout() {
        //刷新
        (findViewById(R.id.loadmore_view)).setVisibility(View.GONE);
        refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(Activity_LoanRequest_Borrow.this)) {
                    getLoanCertification();
                } else {
                    ToastUtil.alert(Activity_LoanRequest_Borrow.this, "网络异常，请稍后重试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }

    /**
     * 初始化
     */
    private void init(String loanRequestInfoString){
        //获取从上一个页面传递过来的数据
        loanRequestInfo = (new Gson()).fromJson(loanRequestInfoString, LoanRequestInfoBean.class);
        durationStatus = loanRequestInfo.LoanTimeType;
        periodStatus = loanRequestInfo.Period;
        initLoanMessage();
        //请求获取优惠券张数
        getCouponCount();
        //初始化借款类型
        setupLoanUsage();
        //初始化借款期限
        setupLoanDuration();
        //初始化默认的还款期数
        setupLoanPeriod();
        //借款金额大于0 时，要设置上一次的借款金额, 需要初始化借款页面的所有元素才可以换算金额
        if (loanRequestInfo.Amount > 0) {
            loan_amount.setText(loanRequestInfo.Amount + "");
        } else {
            loan_amount.setText("5000");
        }
    }


    /**
     * 初始化页面元素
     */
    private void initLoanMessage() {
        //借款用途
        findViewById(R.id.rl_purposes).setOnClickListener(this);
        loan_purposes = (TextView) findViewById(R.id.loan_purposes);
        //借款金额
        loan_amount = (EditText) findViewById(R.id.loan_amount);
        loan_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //输入借款金额之后需要计算还款方案
                setupRepayProgramme();
            }
        });
        //借款期限
        findViewById(R.id.rl_duration).setOnClickListener(this);
        loan_duration = (TextView) findViewById(R.id.loan_duration);
        //还款期数
        findViewById(R.id.rl_period).setOnClickListener(this);
        loan_period = (TextView) findViewById(R.id.loan_period);
        //还款方案
        loan_programme = (TextView) findViewById(R.id.loan_programme);
        //申请福利补贴
        layout_coupon_select = (RelativeLayout) findViewById(R.id.layout_coupon_select);
        layout_coupon_select.setOnClickListener(this);
        coupon_select_image = (ImageView) findViewById(R.id.coupon_select_image);
        //判断是否申请了福利补贴，取反的原因是在requestSubsidy()里面又做了一次取反操作
        statusSubsidy = !loanRequestInfo.UseSubsidy;
        requestSubsidy();
        //利息抵扣券
        layout_interest = (RelativeLayout) findViewById(R.id.layout_interest);
        layout_interest.setOnClickListener(this);
        value_interest = (TextView) findViewById(R.id.value_interest);
        //判断是否使用了利息抵扣券，如果使用了利息抵扣券，则要设置文案和变更利息抵扣券的使用状态
        if (!loanRequestInfo.CouponId.equals("0") && !loanRequestInfo.CouponId.equals("00000000-0000-0000-0000-000000000000")) {
            CouponId = loanRequestInfo.CouponId;
            statusSelectCoupon = true;
            value_interest.setText(loanRequestInfo.CouponDescn);
        } else {
            CouponId = null;
        }
        //如何获得补贴？
        loan_gain = (TextView) findViewById(R.id.loan_gain);
        loan_gain.setOnClickListener(this);

        //文字滚动
        value_rotate  = (WheelVerticalView) findViewById(R.id.value_rotate);
        isPause = false;
        startRotate();

        //客服
        layout_customer_service = (LinearLayout) findViewById(R.id.layout_customer_service);
        layout_customer_service.setOnClickListener(this);
        //按钮
        bottoButton = (Button) findViewById(R.id.borrow_button);
        bottoButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //恢复滚屏
        if (isPause){
            if (null == adTimer){
                adTimer = new Timer();
                adTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(SCROLL);
                    }
                };
                adTimer.schedule(adTimerTask, 1000, 2000);
            }
            isPause = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //关闭滚屏定时器
        if (adTimerTask != null)
            adTimerTask.cancel();
        if (adTimer != null){
            adTimer.cancel();
            adTimer.purge();
            adTimer = null;
            isPause = true;
        }
    }

    /**
     * 初始化并开始滚动文字
     */
    private void startRotate(){
        String[] rotateValues = getResources().getStringArray(R.array.rotateValues);
        if (!this.isFinishing()){
            adapter = new ArrayWheelAdapter<>(this, rotateValues);
            adapter.setTextGravity(Gravity.CENTER);
            adapter.setTextSize(15);
            adapter.setTextTypeface(Typeface.DEFAULT);
            value_rotate.setVisibleItems(1);
            value_rotate.setViewAdapter(adapter);
            value_rotate.setCurrentItem(0);
            value_rotate.setCyclic(true);
            value_rotate.setEnabled(false);

            if (null == adTimer){
                adTimer = new Timer();
                adTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(SCROLL);
                    }
                };
                adTimer.schedule(adTimerTask, 1000, 2000);
            }
        }
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
     * 隐藏转圈
     */
    private void hideProgressBar() {
        if(null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * 获取优惠券张数
     */
    private void getCouponCount() {
        //如果使用过券，则设置为券的描述
        if (loanRequestInfo.UseCoupon) {
            value_interest.setText(loanRequestInfo.CouponDescn);
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("PageNow", 1);
            obj.put("PageSize", 10);
            obj.put("Status", 1);
            obj.put("CouponType", couponType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Select_Coupon, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (PackageUtil.getRequestStatus(response.toString(), Activity_LoanRequest_Borrow.this)) {
                        JSONObject obj = PackageUtil.getResultObject(response.toString());
                        //判断JSON对象是否为空
                        if (obj != null) {
                            //获取利息抵扣券张数
                            CouponCount = Integer.parseInt(obj.getString("TotalCount"));
                            ShowCouponCount(CouponCount);
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


    /**
     * 初始化借款用途
     */
    private void setupLoanUsage() {
        //将元素添加到数组中
        purposes_types = new ArrayList<String>();
        for (int i = 0; i <loanRequestInfo.LoanTypeTable.size(); i++) {
            LogUtil.d("借款用途", loanRequestInfo.LoanTypeTable.get(i).Name);
            purposes_types.add(loanRequestInfo.LoanTypeTable.get(i).Name);
        }
        int index = 0;
        for (int i = 0; i <loanRequestInfo.LoanTypeTable.size(); i++) {
            //获取默认选中的借款用途
            if (loanRequestInfo.LoanType == loanRequestInfo.LoanTypeTable.get(i).Value) {
                index = i;
                break;
            }
        }
        //设置默认选中的位置
        loan_purposes.setText(purposes_types.get(index));
    }

    /**
     * 初始化借款期限
     */
    private void setupLoanDuration() {
        //将元素添加到数组中
        duration_types = new ArrayList<>();
        durationList = new ArrayList<LoanDurationListBean>();
        for (int i = 0; i < loanRequestInfo.RateTable.size(); i++) {
            //用于判断数组中是否存在对应的借款期限
            boolean duration = false;
            for (int j = 0; j < durationList.size(); j++) {
                if (durationList.get(j).Value == loanRequestInfo.RateTable.get(i).Duration) {
                    duration = true;
                    break;
                }
            }
            //如果数组中没有该借款期限值，则需要添加该值
            if (!duration) {
                LoanDurationListBean loanDurationList = new LoanDurationListBean(loanRequestInfo.RateTable.get(i).Duration + "个月", loanRequestInfo.RateTable.get(i).Duration);
                durationList.add(loanDurationList);
                duration_types.add(loanRequestInfo.RateTable.get(i).Duration + "个月");
            }
        }

        //找到durationStatus对应的下表值
        for (int i =0; i < duration_types.size(); i ++) {
            if (new String(durationStatus + "个月").equalsIgnoreCase(duration_types.get(i))) {
                durationIndex = i;
                break;
            }
        }
        loan_duration.setText(duration_types.get(durationIndex));
    }

    /**
     * 初始化还款期数
     */
    private void setupLoanPeriod() {
        period_types = new ArrayList<>();
        //还款期数要从利率表里面查找，这尼玛简直就是坑爹
        periodList = new ArrayList<LoanPeriodWithRateBean>();
        for (int i = 0; i < loanRequestInfo.RateTable.size(); i++) {
            //如果借款期限相同，则将期限加载到页面中去
            if (loanRequestInfo.RateTable.get(i).getDuration() == durationStatus) {
                LoanPeriodWithRateBean periodWithRate = new LoanPeriodWithRateBean(loanRequestInfo.RateTable.get(i).getPeriod(), loanRequestInfo.RateTable.get(i).getRate());
                periodList.add(periodWithRate);
                period_types.add("" + periodWithRate.Period);
            }
        }
        //设置还款位置
        for (int i= 0; i < periodList.size(); i++) {
            if (loanRequestInfo.Period == periodList.get(i).Period) {
                loanIndex = i;
                break;
            }
        }
        //获取初始的期数
        periodStatus = Integer.valueOf(period_types.get(loanIndex));
        LogUtil.d("初始化的还款期数", periodStatus + "");
        //设置还款期数位置
        loan_period.setText(period_types.get(loanIndex) + "期");

        //设置完还款期数之后，就要开始算还款方案
        setupRepayProgramme();
    }

    /**
     * 计算还款方案
     */
    private void setupRepayProgramme() {
        DecimalFormat df = new DecimalFormat("######0.##");
        float rate = 0;
        //根据表查找利率
        for (int i = 0; i < loanRequestInfo.RateTable.size();  i ++) {
            if (durationStatus == loanRequestInfo.RateTable.get(i).getDuration() && periodStatus == loanRequestInfo.RateTable.get(i).getPeriod()) {
                rate = loanRequestInfo.RateTable.get(i).Rate / 100;
                break;
            }
        }

        String repayProgramme = "";
        if (getLoanAmount() <= 0) {
            repayProgramme = "0元";
        }
        //借款月数等于还款期数
        else if (durationStatus == periodStatus && durationStatus != -1) {
            float amount = (getLoanAmount() + getLoanAmount() * durationStatus/12 * rate) / periodStatus;
            String value = df.format(amount);
            //如果是一个月的时候，就是"到期还款XXX元"
            if (durationStatus == 1) {
                repayProgramme = "到期还款" + value + "元";
            } else {
                repayProgramme = "每月还款" + value + "元";
            }
        }
        //还款期数为2时
        else if (periodStatus == 2) {
            float amount = getLoanAmount() * (float) durationStatus / 12 * rate /durationStatus;
            String value = df.format(amount);
            String val = df.format(getLoanAmount()/periodStatus);
            repayProgramme = "每月还利息" + value + "元" + "每12个月还本金" + val + "元";
        }
        //划款期数为3时
        else if (periodStatus == 3) {
            float amount = getLoanAmount() * (float)durationStatus/12 * rate/durationStatus;
            String value = df.format(amount);
            String val1 = df.format(getLoanAmount() * 0.3);
            String val2 = df.format(getLoanAmount() * 0.4);
            repayProgramme = "每月还利息" + value + "每12个月还本金(" + val1 + "元," + val1 + "元," + val2 + "元)";
        } else {
            repayProgramme = "暂不支持该方案";
        }
        loan_programme.setText(repayProgramme);
    }

    /**
     * 获取借款金额
     * @return  借款金额
     */
    private float getLoanAmount() {
        String amount = loan_amount.getText().toString().replace(",", "");
        if (amount.equalsIgnoreCase("")) {
            amount = "0";
        }
        return Float.parseFloat(amount);
    }

    /**
     * 显示利息抵扣券张数
     */
    private void ShowCouponCount(int TotalCount) {
        value_interest.setText("你有"+ TotalCount +"张利息抵扣券");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击借款用途
            case R.id.rl_purposes:
                String[] ar = purposes_types.toArray(new String[purposes_types.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, ar, purposeStatus - 1, new SpinnerWheelUtil.OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        purposeStatus = currentItem + 1;
                        loan_purposes.setText(purposes_types.get(currentItem));
                    }
                });
                break;
            //点击借款期限
            case R.id.rl_duration:
                String[] arr = duration_types.toArray(new String[duration_types.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, arr, durationIndex, new SpinnerWheelUtil.OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        durationIndex = currentItem;
                        loan_duration.setText(duration_types.get(currentItem));//设置借款期限
                        for (int i = 0; i < loanRequestInfo.RateTable.size(); i++) {
                            if (new String(loanRequestInfo.RateTable.get(i).getDuration() + "个月").equalsIgnoreCase(duration_types.get(currentItem))) {
                                durationStatus = loanRequestInfo.RateTable.get(i).getDuration();
                            }
                        }
                        //选中某期限的时候，设置还款期数
                        setupLoanPeriod();
                        setupRepayProgramme();
                    }
                });
                break;

            //选择还款期数
            case R.id.rl_period:
                String[] array = period_types.toArray(new String[period_types.size()]);
                SpinnerWheelUtil.getInstance().initSpinnerWheelDialog(this, array, loanIndex, new SpinnerWheelUtil.OnDoneClickListener() {
                    @Override
                    public void onClick(int currentItem) {
                        periodStatus = periodList.get(currentItem).Period;
                        LogUtil.d("选中的还款期数", periodStatus + "");
                        loan_period.setText(period_types.get(currentItem) + "期");//设置还款期数
                        setupRepayProgramme();
                    }
                });
                break;

            //点击立即申请按钮
            case  R.id.borrow_button:
                borrowButtonClick();
                break;

            //如何获得补贴？
            case R.id.loan_gain:
                Activity_Browser.launche(Activity_LoanRequest_Borrow.this, "如何获得补贴?", "http://192.168.1.111:7073/loan/couponactivity/index");
                break;

            //点击利息抵扣券
            case R.id.layout_interest:
                //如果已经选择了福利补贴，则弹Toast提示不能同时福利补贴和利息抵扣券不能同时使用
                if (statusSubsidy) {
                    ToastUtil.alert(Activity_LoanRequest_Borrow.this, "福利补贴和利息抵扣券不能同时使用");
                }
                //选择福利补贴
                else {
                    selectInterestCoupon();
                }
                break;

            //申请福利补贴
            case R.id.layout_coupon_select:
                //已经选择了利息抵扣券，则弹框提示是否仅申请福利补贴
                 if (statusSelectCoupon) {
                    showAlertView();
                }
                //如果没选过利息抵扣券，则正常申请福利补贴
                else {
                    requestSubsidy();
                }
                break;

            //客服
            case R.id.layout_customer_service:
                customerService();
                break;
        }
    }

    /**
     * 弹框提示
     */
    private void showAlertView() {
        AlertView.Builder ibuilder = new AlertView.Builder(this);
        ibuilder.setTitle("提示");
        ibuilder.setMessage("你已经使用了利息抵扣券，不能再申请福利补贴。是否仅申请福利补贴？");
        ibuilder.setPositiveButton("立即申请", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //销毁弹框
                alertView.dismiss();
                alertView = null;
                //清空利息抵扣券的ID、金额和描述等，将利息抵扣券的选中状态置为false
                InterestAmount = 0;
                InterestMinAmount = 0;
                InterestDescn = "";
                statusSelectCoupon = false;
                //将利息抵扣券的描述设置为"你有xx张利息抵扣券"
                ShowCouponCount(CouponCount);
                //正常申请福利补贴
                requestSubsidy();
            }
        });
        ibuilder.setNegativeButton("取消", null);
        alertView = ibuilder.create();
        alertView.show();
    }

    /**
     * 申请福利补贴
     */
    private void requestSubsidy() {
        statusSubsidy = !statusSubsidy;
        if (statusSubsidy) {
            coupon_select_image.setBackgroundResource(R.drawable.loan_request_select);
        } else {
            coupon_select_image.setBackgroundResource(R.drawable.loan_rect);
        }
    }
    /**
     * 选择利息抵扣券
     */
    private void selectInterestCoupon() {
        Intent newIntent = new Intent(this, Activity_Select_Coupon.class);
        newIntent.putExtra("investAmount", getLoanAmount());
        newIntent.putExtra("CouponType", couponType);
        startActivityForResult(newIntent, 1);
    }

    /**
     * 点击客服事件
     */
    private void customerService() {
        String userId = null;
        //判断是否为空
        if (App.mUserDetailInfo != null) {
            userId = App.mUserDetailInfo.getCustomerId();
        }
        MQCustomerManager.getInstance(this).showCustomer(userId);
    }

    /**
     * 立即借款按钮事件
     */
    private void borrowButtonClick() {
        //判断是否写了借款金额和期望到账时间
        if (getLoanAmount()<= 0) {
            ToastUtil.alert(Activity_LoanRequest_Borrow.this, "请填写完整的借款信息");
            return;
        }

        //借款金额小于5000元，提示不能小于5000元
        if (getLoanAmount() < 5000) {
            ToastUtil.alert(Activity_LoanRequest_Borrow.this, "借款金额必须大于5000元");
            return;
        }

        //借款金额大于30W元，提示不能大于30W元
        if (getLoanAmount() > 300000) {
            ToastUtil.alert(Activity_LoanRequest_Borrow.this, "借款金额不能大于300000元");
            return;
        }

        LogUtil.d("借款", loanRequestInfo.toString());
        if (loanRequestInfo.getStatus().equals("0")){//可以申请借款
            //判断是否跟原来的数据一样，如果跟原来申请的借款一样没有变化，直接提示完善个人信息
            if (isNeedToPostData()) {
                pushLoanMessageToService();
            } else {
                gotoRequestSuccessPage();
            }
        }else{//存在已审核通过的借款
            ToastUtil.alert(Activity_LoanRequest_Borrow.this, loanRequestInfo.getMessage());
        }

    }

    /**
     * 判断是否需要提交借款申请数据到服务器
     * @return 是否需要提交数据
     */
    private boolean isNeedToPostData() {
        //判断传进来的借款值为小于等于0，或者是第一次编辑的时候
        if (loanRequestInfo.Amount <= 0
                || loanRequestInfo.AggregateId.equals("0")
                || loanRequestInfo.AggregateId.equals("00000000-0000-0000-0000-000000000000")) {
            return true;
        }
        else if (purposeStatus != loanRequestInfo.LoanType           //借款用途不一致
                || getLoanAmount() != loanRequestInfo.Amount    //借款金额不一致
                || durationStatus != loanRequestInfo.LoanTimeType   //借款期限不一致
                || periodStatus != loanRequestInfo.Period           //还款期数不一致
                || statusSubsidy != loanRequestInfo.UseSubsidy      //申请福利补贴不一致
                || statusSelectCoupon != loanRequestInfo.UseCoupon  //使用利息抵扣券不一致
                ) {
            return true;
        }
        return false;
    }


    /**
     * 将借款信息提交到服务器
     */
    private void pushLoanMessageToService() {
        JSONObject jsonObject = new JSONObject();
        try{
            if (!loanRequestInfo.AggregateId.equals("0") && !loanRequestInfo.AggregateId.equals("00000000-0000-0000-0000-000000000000")) {
                jsonObject.put("AggregateId", loanRequestInfo.AggregateId);
            } else {
                jsonObject.put("AggregateId", null);
            }
            jsonObject.put("LoanType", purposeStatus);//借款用途
            jsonObject.put("UseSubsidy", statusSubsidy); //是否申请福利补贴
            jsonObject.put("Amount", getLoanAmount());//借款金额
            jsonObject.put("LoanTimeType", durationStatus);//借款期限
            jsonObject.put("Period", periodStatus);//还款期数
            //是否使用了利息抵扣券，存在利息抵扣券的时候才去提交
            if (!TextUtils.isEmpty(CouponId)) {
                jsonObject.put("CouponId", CouponId);
            } else {
                jsonObject.put("CouponId", null);
            }
            LogUtil.d("请求的数据是", jsonObject.toString());
            showProgressBar();
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.PostRequestMessage, jsonObject, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideProgressBar();
                    try {
                        if (response.getInt("status") == 1) {
                            //提示申请成功，是否填写个人信息
                            gotoRequestSuccessPage();
                        } else {
                            ToastUtil.alert(Activity_LoanRequest_Borrow.this, response.getString("message").equalsIgnoreCase("") ? "服务器繁忙，请稍候再试" : response.getString("message"));
                            //判断是否是Token过期，如果过期则跳转至登陆界面
                            if (response.getInt("status") == -5) {
                                Activity_Login.launche(Activity_LoanRequest_Borrow.this);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.alert(Activity_LoanRequest_Borrow.this, "解析数据出错");
                    }
                }

                @Override
                public void onErrorResponse(Exception error) {
                    hideProgressBar();
                }
            });
            jsonRequest.setTag(BcbRequestTag.BCB_CREATE_LOAN_REQUEST_MESSAGE_REQUEST);
            requestQueue.add(jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提示提交成功是否去填写个人借款信息
     */
    private void gotoRequestSuccessPage() {
        //跳转至填写借款信息页面
        Intent intent = new Intent(Activity_LoanRequest_Borrow.this, Activity_LoanRequest_Person.class);
        startActivity(intent);
        finish();
    }

    /**
     * 选择优惠券回调
     * @param requestCode   请求码
     * @param resultCode    回调码
     * @param data          回调数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //判断是否选择了利息抵扣券
            case 1:
                if (data != null) {
                    //利息抵扣券ID
                    CouponId = data.getStringExtra("CouponId");
                    LogUtil.d("CouponId", CouponId);
                    //利息抵扣券金额
                    InterestAmount = data.getIntExtra("InterestAmount", 0);
                    //利息抵扣券最小使用金额
                    InterestMinAmount = data.getIntExtra("InterestMinAmount", 0);
                    //利息抵扣券使用条件描述
                    InterestDescn = data.getStringExtra("InterestDescn");
                    //设置利息抵扣券的描述
                    value_interest.setText(InterestDescn);
                    //已经选择了利息抵扣券
                    statusSelectCoupon = true;
                } else {
                    getCouponCount();
                }
                break;
        }
    }

    //销毁广播
    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue.cancelAll(BcbRequestTag.BCB_SELECT_COUPON_REQUEST);
        requestQueue.cancelAll(BcbRequestTag.BCB_CREATE_LOAN_REQUEST_MESSAGE_REQUEST);
    }

    /****************************** 获取借款验证 ************************************/
    private void getLoanCertification() {
        showProgressBar();
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.LoanCertification, null, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressBar();
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    if(null == response) {
                        ToastUtil.alert(Activity_LoanRequest_Borrow.this, "服务器返回数据为空，无法验证");
                        return;
                    }
                    //初始化数据，因为不管状态是不是-1或者1，这里面都要返回列表数据
                    init(response.getString("result"));
                    if (response.getInt("status") != 1) {
                        String message = response.getString("message");
                        //出错信息不为空时，将出错信息打印出来，否则将出错信息设置为"未知错误"
                        if (message != null && !message.equalsIgnoreCase("null") && !message.equalsIgnoreCase("")) {
                            //判断是否Token过期，如果过期则跳转至登陆界面
                            if (response.getInt("status") == -5) {
                                Activity_Login.launche(Activity_LoanRequest_Borrow.this);
                                finish();
                            }
                            loanRequestInfo.setMessage(message);
                        }
                    } else {
                        UmengUtil.eventById(Activity_LoanRequest_Borrow.this, R.string.loan_blank);
                        init(response.getString("result"));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    ToastUtil.alert(Activity_LoanRequest_Borrow.this, "解析数据出错");
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                hideProgressBar();
                ToastUtil.alert(Activity_LoanRequest_Borrow.this, "网络异常，请稍后重试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.BCB_LOAN_CERTIFICATION_REQUEST);
        requestQueue.add(jsonRequest);
    }
}