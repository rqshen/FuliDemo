package com.bcb.module.discover.financialproduct.dayprogram;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.constant.ProjectListType;
import com.bcb.data.bean.CPXQbean;
import com.bcb.module.browse.FundCustodianWebActivity;
import com.bcb.module.discover.financialproduct.buy.ProjectBuyActivity;
import com.bcb.module.login.LoginActivity;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.util.PackageUtil;
import com.bcb.util.ProgressDialogrUtils;
import com.bcb.util.SpanUtils;
import com.bcb.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bcb.R.id.back_img;

/**
 * 稳盈宝详情
 * setTitleValue("产品详情"。setTitleValue("详情"。setTitleValue("项目详情"。setTitleValue("立即购买"。setTitleValue("立即申购"。
 */
public class DayProgramIntroductionActivity extends Activity_Base implements View.OnTouchListener {
    Context context;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_rate_add)
    TextView tvRateAdd;
    @BindView(R.id.sdq)
    TextView sdq;
    @BindView(R.id.ktje)
    TextView ktje;
    @BindView(R.id.tv_limite)
    TextView tvLimite;
    @BindView(R.id.tv_u1)
    TextView tvU1;
    @BindView(R.id.tv_u2)
    TextView tvU2;
    @BindView(R.id.qxr)
    TextView qxr;
    @BindView(R.id.tc)
    TextView tc;
    @BindView(R.id.cy)
    TextView cy;
    @BindView(R.id.buy1)
    TextView buy1;
    @BindView(R.id.ll_buy1)
    LinearLayout llBuy1;
    @BindView(R.id.buy2)
    TextView buy2;
    @BindView(R.id.ll_buy2)
    LinearLayout llBuy2;
    @BindView(R.id.more)
    TextView more;
    @BindView(R.id.layout_scrollview)
    ScrollView layoutScrollview;
    @BindView(R.id.buy)
    TextView buy;

    private String packageId = "";

    public static void launche(Context context, String pid) {
        Intent intent = new Intent();
        intent.putExtra("pid", pid);
        intent.setClass(context, DayProgramIntroductionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        MyActivityManager.getInstance().pushOneActivity(DayProgramIntroductionActivity.this);
        if (getIntent() != null) {
            packageId = getIntent().getStringExtra("pid");
        }
        setBaseContentView(R.layout.activity_day_program_introduction);
        ButterKnife.bind(this);// ButterKnife.inject(this) should be called after setContentView()
        setTitleValue("产品详情");
        layout_title.setBackgroundColor(getResources().getColor(R.color.red));
        title_text.setTextColor(getResources().getColor(R.color.white));
        dropdown.setImageResource(R.drawable.return_delault);
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        (findViewById(back_img)).setVisibility(View.GONE);
        requestCPInfo();
        ProgressDialogrUtils.show(this, "正在获取数据，请稍后…");
    }


    private void showData() {
        tvRate.setText("" + String.format("%.1f", bean.Rate));
        //福袋利率
        String welfareRate = TextUtils.isEmpty(MyApplication.getInstance().getWelfare()) ? "%" : "%+" + MyApplication.getInstance().getWelfare() + "%";
        tvRateAdd.setText(welfareRate);
        sdq.setText(bean.MixDuration + "天");//锁定期
        ktje.setText(String.format("%.2f", bean.Balance));//可投金额
        tvLimite.setText(getSpanDayDesc(bean.MixDuration + ""));//最少多天
        tvU2.setText("持续收益，每" + bean.MixDuration + "天退出一次");//多少天退出
        cy.setText(bean.MaxDuration + "天");//最长多少天
        buy1.setText(getSpanBuyDesc(10000, bean.MixDuration, String.format("%.2f", bean.MinPreInterest)));//
        buy2.setText(getSpanBuyDesc(10000, bean.MaxDuration, String.format("%.2f", bean.MaxPreInterest)));
        setTitleValue(bean.Name);
        if (bean.Balance <= 0 || bean.Status == 25) {
            buy.setText("已售罄");
            buy.setTextColor(0xff999999);
            buy.setEnabled(false);
            llBuy1.setEnabled(false);
            llBuy2.setEnabled(false);
            buy.setBackgroundColor(0xffd0d0d0);
        } else {
            buy.setText("立即购买");
            buy.setEnabled(true);
            buy.setBackgroundColor(getResources().getColor(R.color.red));
            buy.setTextColor(getResources().getColor(R.color.white));
        }
        //加入时间，起息时间，锁定到期
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
            Date qxr_ = format.parse(bean.InterestTakeDate);
            Date tc_ = format.parse(bean.HoldingDate);
            qxr.setText(format2.format(qxr_));
            tc.setText(format2.format(tc_));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * 持有天数的描述
     *
     * @param string1
     * @return
     */
    private SpannableString getSpanDayDesc(String string1) {
        String desc = "最少持有" + string1 + "天，不退出自动续期";
        return SpanUtils.updateFontColor(desc, new String[]{string1});
    }

    /**
     * 购买描述
     *
     * @param money
     * @param time
     * @param string2
     * @return
     */
    private SpannableString getSpanBuyDesc(int money, int time, String string2) {
        String desc = "购买" + money + "元," + time + "天可收益" + string2 + "元";
        return SpanUtils.updateFontColor(desc, new String[]{String.valueOf(money), string2});
    }

    @OnClick({R.id.ll_buy1, R.id.ll_buy2, R.id.more, R.id.buy})
    public void onClick(View view) {
        if (MyApplication.saveUserInfo.getAccess_Token() == null && view.getId() != R.id.more) {
            LoginActivity.launche(context);
            return;
        }
        switch (view.getId()) {
            case R.id.ll_buy1:
            case R.id.ll_buy2:
            case R.id.buy:
                //没有开通自动投标
                if (!MyApplication.mUserDetailInfo.AutoTenderPlanStatus) {//
                    altDialog();
                    return;
                }
                //跳转到购买页面
                ProjectBuyActivity.launche(this, packageId, bean.Name, bean, ProjectListType.DAY);
                break;
            case R.id.more:
                ProjectDetailActivity.launche2(this, bean.Name, bean.PageUrl, 20095);
                break;
        }
    }

    CPXQbean bean;

    /**
     * 用户信息
     */
    private void requestCPInfo() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("PackageId", packageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Day_Buy_DETAIL, obj, TokenUtil.getEncodeToken(context), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ProgressDialogrUtils.hide();
                if (PackageUtil.getRequestStatus(response, context)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        //将获取到的银行卡数据写入静态数据区中
                        bean = MyApplication.mGson.fromJson(data.toString(), CPXQbean.class);
                        if (bean != null) {
                            showData();
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ProgressDialogrUtils.hide();
            }
        });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    AlertView alertView;

    private void altDialog() {
        AlertView.Builder ibuilder = new AlertView.Builder(this);
        ibuilder.setTitle("开启份额锁，100%成功买入");
        ibuilder.setMessage("锁定份额，自动买入");
        ibuilder.setPositiveButton("开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autoOpen();
                alertView.dismiss();
            }
        });
        ibuilder.setNegativeButton("取消", null);
        alertView = ibuilder.create();
        alertView.show();
    }
    //******************************************************************************************

    /**
     * APP自动投标流程：
     * 1、新用户买标，没有开通托管账户的引导到汇付开通托管账户。
     * 2、已开通托管账户用户买标没开通自动买标的，引导到汇付开通自动投标。
     * 3、开通自动投标完毕，手动买入理财标。
     */
    private void autoOpen() {
        String requestUrl = UrlsTwo.OPENAUTOTENDERPLAN;
        String encodeToken = TokenUtil.getEncodeToken(context);
        JSONObject obj = new JSONObject();
        try {
            obj.put("Platform", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, obj, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "开通自动投标" + response.toString());
                if (PackageUtil.getRequestStatus(response, context)) {
                    try {
                        /** 后台返回的JSON对象，也是要转发给汇付的对象 */
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result != null) {
                            //网页地址
                            String postUrl = result.optString("PostUrl");
                            result.remove("PostUrl");//移除这个参数
                            //传递的参数
                            String postData = HttpUtils.jsonToStr(result.toString()); //跳转到webview
                            FundCustodianWebActivity.launche(context, "开启份额锁", postUrl, postData);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "开通自动投标2" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(context, response.optString("message"), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【Activity_TuoGuan_HF】【loginAccount】网络异常，请稍后重试" + error.toString());
            }
        });
        MyApplication.getInstance()
                .getRequestQueue()
                .add(jsonRequest);
    }
}