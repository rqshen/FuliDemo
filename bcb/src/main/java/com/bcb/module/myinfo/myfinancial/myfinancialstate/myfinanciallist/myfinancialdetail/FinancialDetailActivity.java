package com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.constant.ProjectListType;
import com.bcb.data.bean.ClaimConveyBean;
import com.bcb.data.bean.Project_Investment_Details_Bean;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.backpayment.BackPaymentActivity;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.presentation.view.activity.Activity_Rading_Record;
import com.bcb.presentation.view.activity.Activity_Tips_FaileOrSuccess;
import com.bcb.presentation.view.custom.AlertView.DialogBQT2;
import com.bcb.util.ActivityCollector;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bcb.R.id.back_img;

/**
 * 投资详情
 */
public class FinancialDetailActivity extends Activity_Base implements View.OnClickListener {

    private static final String TAG = "bqt";
    private String OrderNo;
    private TextView top_amount, earning_expected, tv_id_number, state_title, state_below;
    private TextView earningtime;
    private TextView annual_yield, earnings_end;
    LinearLayout ll_id_number;
    private RelativeLayout rl_hk, rl_zr, tourl;
    private Button button;
    String projectType = ProjectListType.MONTH;//产品类型
    private Project_Investment_Details_Bean bean;

    /**
     * 启动自身
     *
     * @param context
     * @param OrderNo
     * @param Status
     */
    public static void launche(Context context, String OrderNo, String Status) {
        Intent intent = new Intent();
        intent.setClass(context, FinancialDetailActivity.class);
        intent.putExtra("OrderNo", OrderNo);
        intent.putExtra("projectType", Status);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(FinancialDetailActivity.this);
        setBaseContentView(R.layout.activity_project_investment_details);
        setLeftTitleVisible(true);
        setTitleValue("投资详情");
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
        OrderNo = getIntent().getStringExtra("OrderNo");
        projectType = getIntent().getStringExtra("projectType");
        initView();
        loadData();

        ActivityCollector.addActivity(this);
    }

    private void initView() {
        top_amount = (TextView) findViewById(R.id.top_amount);
        earning_expected = (TextView) findViewById(R.id.earning_expected);
        tv_id_number = (TextView) findViewById(R.id.tv_id_number);
        ll_id_number = (LinearLayout) findViewById(R.id.ll_id_number);
        ll_id_number.setOnClickListener(this);
        earningtime = (TextView) findViewById(R.id.earningtime);
        annual_yield = (TextView) findViewById(R.id.annual_yield);
        earnings_end = (TextView) findViewById(R.id.earnings_end);
        state_title = (TextView) findViewById(R.id.state_title);
        state_below = (TextView) findViewById(R.id.state_below);
        button = (Button) findViewById(R.id.button);
        rl_hk = (RelativeLayout) findViewById(R.id.rl_hk);
        rl_zr = (RelativeLayout) findViewById(R.id.rl_zr);
        tourl = (RelativeLayout) findViewById(R.id.tourl);
        rl_hk.setOnClickListener(this);
        rl_zr.setOnClickListener(this);
        button.setOnClickListener(this);
        tourl.setOnClickListener(this);
    }

    String endTime;

    /**
     * 请求购买的产品列表详情
     */
    private void loadData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("OrderNo", OrderNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = UrlsOne.Month_MyFinancial_XQ;

        if (projectType.equals(ProjectListType.MONTH)) {
            url = UrlsOne.Month_MyFinancial_XQ;
        } else if (projectType.equals(ProjectListType.DAY)) {
            url = UrlsOne.Day_MyFinancial_XQ;
        }

        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(this), new
                BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("bqt", "【投资详情】" + response.toString());
                        try {
                            boolean flag = PackageUtil.getRequestStatus(response, FinancialDetailActivity.this);
                            if (flag) {
                                JSONObject obj = PackageUtil.getResultObject(response);
                                if (obj != null)
                                    bean = MyApplication.mGson.fromJson(obj.toString(), Project_Investment_Details_Bean.class);
                                if (null != bean) {
                                    //订单号
                                    tv_id_number.setText(OrderNo);
                                    //状态
                                    state_title.setText(bean.StatusName + "");
                                    //在投本金，预期收益，已获收益
                                    top_amount.setText(String.format("%.2f", bean.OrderAmount));
                                    earning_expected.setText(String.format("%.2f", bean.TotalInterest));
                                    //加入时间，起息时间，锁定到期
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    Date earningDate = null;
                                    if (bean.InterestTakeDate != null && !bean.InterestTakeDate.equals("")) {
                                        earningDate = format.parse(bean.InterestTakeDate);
                                        earningtime.setText(format.format(earningDate));
                                    }

                                    if (bean.EndDate != null) {
                                        endTime = bean.EndDate.substring(0, bean.EndDate.indexOf(" "));//结束时间
                                    }

                                    //月盈宝和日盈宝都有退出选项
                                    switch (bean.StatusCode) {// 0：不能申请转让 1：已完成 2：可以申请退出 3：退出中
                                        case 0:
                                            button.setVisibility(View.INVISIBLE);
                                            button.setClickable(false);
                                            button.setText("不能申请转让");
                                            button.setEnabled(false);
                                            break;
                                        case 1:
                                            button.setVisibility(View.INVISIBLE);
                                            button.setClickable(false);
                                            button.setText("已完成");
                                            button.setEnabled(false);
                                            break;
                                        case 2://可申请退出
                                            button.setVisibility(View.VISIBLE);
                                            button.setEnabled(true);
                                            button.setClickable(true);
                                            button.setText("申请退出");
                                            break;
                                        case 3://退出中
                                            button.setVisibility(View.VISIBLE);
                                            button.setEnabled(false);
                                            button.setClickable(false);
                                            button.setText("退出中");
                                            button.setCompoundDrawables(null, null, null, null);
                                            break;
                                        default:
                                            break;
                                    }

                                    state_below.setText(bean.StatusTips);
                                    //年化利率，锁定期限，已收本息，剩余本息
                                    annual_yield.setText(String.format("%.2f", bean.Rate) + "%");
                                    earnings_end.setText(bean.Duration);//封闭期 带单位【 "Duration": 3天】
                                } else {
                                    LogUtil.e(TAG, "请求项目详情出现错误");
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
        jsonRequest.setTag(BcbRequestTag.TradeRecordDetailTag);
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    private ClaimConveyBean bean2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tourl://项目详情
                ProjectDetailActivity.launche(this, "项目详情", bean.PackageUrl);
                break;
            case R.id.ll_id_number:
                ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("label", OrderNo));
                Toast.makeText(this, "订单号已经复制到剪贴板", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_hk://回款计划
                startActivity(BackPaymentActivity.newIntent(FinancialDetailActivity.this, bean, OrderNo, projectType));
                break;
            case R.id.rl_zr:
                Activity_Rading_Record.launche(this, OrderNo);
                break;
            case R.id.button://申请退出
                showDialog();
                break;
        }
    }

    private void showDialog() {
        switch (bean.StatusCode) {
            case 2://可以申请退出
                DialogBQT2 dialogBQT2 = new DialogBQT2(this) {
                    @Override
                    public void onSureClick(View v) {
                        super.onSureClick(v);
                        if (projectType.equals(ProjectListType.MONTH)) {
                            requestZR(UrlsOne.Month_MyFinancial_Exit);
                        } else {
                            requestZR(UrlsOne.Day_MyFinancial_Exit);
                        }
                        dismiss();
                    }
                };
                dialogBQT2.getMessage().setText("1、本息预计在" + endTime + "回款，继续持有将获得更高收益哦\n\n2、确认退出后将无法撤销\n\n3、本次退出费用为 0 元");
                dialogBQT2.show();
                break;
        }
    }


    /**
     * 请求退出标
     *
     * @param url
     */
    private void requestZR(String url) {
        LogUtil.i("bqt", "【Activity_Trading_Cancle】【onResponse】路径" + url);
        JSONObject obj = new JSONObject();
        try {
            obj.put("OrderNo", OrderNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "申请债权转让" + response.toString());
                if (response.optBoolean("result", true)) {
                    Activity_Tips_FaileOrSuccess.launche(FinancialDetailActivity.this, Activity_Tips_FaileOrSuccess.ZR_SUCCESS, "预计 " + endTime + " 回款本息");
                    loadData();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getBooleanExtra("rufush", false)) {
            loadData();
        }
    }
}