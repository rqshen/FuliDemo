package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.data.bean.loan.LoanItemDetailBean;
import com.bcb.common.net.UrlsOne;
import com.bcb.presentation.view.activity.Activity_Recharge_Second;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;
import org.json.JSONObject;

/**
 * Created by cain on 16/1/13.
 */
public class Frag_LoanDetail extends Frag_Base {


    private Context context;
    private String loanUniqueId;
    //借款用途
    private TextView loanType;
    //借款期限
    private TextView loanDuration;
    //借款金额
    private TextView loanAmount;
    //借款利率
    private TextView loanRate;
    //分期还款
    private TextView loanRepayment;
    //逾期罚款
    private TextView loanPunitive;
    //下一个还款日
    private LinearLayout layoutNextPayDate;
    //下一个还款日
    private TextView loanNextDate;
    //还款金额
    private TextView loanNextPayment;

    //审核状态图标
    private ImageView statusView;

    //客服和邮箱
    private LinearLayout layoutOtherMessage;
    //描述
    private TextView text_des, text_des1, text_des2, text_email, text_qq;
    private LoanItemDetailBean loanItemDetailBean;


    //立即还款
    private Button button_recharge;

    private BcbRequestQueue requestQueue;

    //构造函数
    public Frag_LoanDetail() {
        super();
    }

    @SuppressLint("ValidFragment")
    public Frag_LoanDetail(Context context, String loanUniqueId) {
        super();
        this.context = context;
        this.loanUniqueId = loanUniqueId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_loan_detail, container, false);
    }

    //初始化页面要在这里进行，多线程情况下，在onCreateView中初始化会崩溃
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        requestQueue = BcbNetworkManager.newRequestQueue(context);
        setupView(view);
        loanLoanDetailData();
    }

    //初始化界面元素
    private void setupView(View view) {
        loanType = (TextView) view.findViewById(R.id.loan_type);
        loanDuration = (TextView) view.findViewById(R.id.loanduration);
        loanAmount = (TextView) view.findViewById(R.id.loan_amount);
        loanRate = (TextView) view.findViewById(R.id.loan_rate);
        loanRepayment = (TextView) view.findViewById(R.id.loan_repayment);
        loanPunitive = (TextView) view.findViewById(R.id.loan_punitive);
        statusView = (ImageView) view.findViewById(R.id.icon_status);
        //下一个还款日
        layoutNextPayDate = (LinearLayout) view.findViewById(R.id.layout_next_pay);
        loanNextDate = (TextView) view.findViewById(R.id.loan_next_date);
        loanNextPayment = (TextView) view.findViewById(R.id.loan_next_payment);
        //客服和邮箱
        layoutOtherMessage = (LinearLayout) view.findViewById(R.id.layout_other_message);
        layoutOtherMessage.setVisibility(View.VISIBLE);
        text_des = (TextView) view.findViewById(R.id.text_des);
        text_des1 = (TextView) view.findViewById(R.id.text_des1);
        text_des2 = (TextView) view.findViewById(R.id.text_des2);
        text_email = (TextView) view.findViewById(R.id.text_email);
        text_qq = (TextView) view.findViewById(R.id.text_qq);

        //还款按钮
        button_recharge = (Button) view.findViewById(R.id.button_recharge);
        button_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至充值按钮
                Activity_Recharge_Second.launche(context);
            }
        });

        //获取分辨率，根据分辨率调整客服和邮箱的字号和照片的位置
        DisplayMetrics dm = new DisplayMetrics();
        if (dm.widthPixels < 720 || dm.heightPixels < 1024) {
            text_des.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            text_des1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            text_des2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            text_email.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            text_qq.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        }
    }

    //获取借款详情数据
    private void loanLoanDetailData() {
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("LoanUniqueId", loanUniqueId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MyLoanItemDetailMessage, jsonObject, TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    //如果存在返回数据时
                    if(PackageUtil.getRequestStatus(response, context)) {
                        JSONObject resultObject = PackageUtil.getResultObject(response);

                        //判断JSON对象是否为空
                        if (resultObject != null) {
                            loanItemDetailBean = App.mGson.fromJson(resultObject.toString(), LoanItemDetailBean.class);
                            setupDetailData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.MyLoanItemDetailMessageTag);
        requestQueue.add(jsonRequest);
    }

    //设置界面数据
    private void setupDetailData() {
        loanType.setText(loanItemDetailBean.LoanType);
        loanDuration.setText(loanItemDetailBean.Duration);
        loanAmount.setText(String.format("%.0f", loanItemDetailBean.Amount) + "元");
        loanRate.setText(loanItemDetailBean.Rate);
        loanRepayment.setText(loanItemDetailBean.LoanPeriod +"期");
        loanPunitive.setText(loanItemDetailBean.LatePenaltyRate * 100 + "%");
        loanNextDate.setText(loanItemDetailBean.NextPayDate);
        loanNextPayment.setText(String.format("%.2f", loanItemDetailBean.NextPayAmount) + "元");
        //status 等于 15 时，表示正在还款中
        if (loanItemDetailBean.Status == 15) {
            layoutNextPayDate.setVisibility(View.VISIBLE);
            layoutOtherMessage.setVisibility(View.GONE);
            statusView.setBackgroundResource(R.drawable.ico_my_refund);
        }
        //表示还款完成
        else if (loanItemDetailBean.Status == 20) {
            layoutNextPayDate.setVisibility(View.GONE);
            layoutOtherMessage.setVisibility(View.GONE);
            statusView.setBackgroundResource(R.drawable.ico_my_done);
        }
        //其余状态均表示审核中的状态
        else {
            layoutNextPayDate.setVisibility(View.GONE);
            layoutOtherMessage.setVisibility(View.VISIBLE);
            statusView.setBackgroundResource(R.drawable.ico_my_checking);
        }
    }
}