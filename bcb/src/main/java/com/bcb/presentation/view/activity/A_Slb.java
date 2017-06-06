package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.module.browse.FundCustodianWebActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.SlbBasic;
import com.bcb.data.bean.SlbZH;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;
import com.bcb.presentation.view.custom.SlbSyView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 生利宝界面
 */
public class A_Slb extends Activity_Base implements View.OnClickListener {
    TextView tv_yesterday, tv_total_top, value_rate, value_total;
    SlbSyView slbView;
    TextView tv_values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_slb);
        initTitle();
        init();
        requestSLB();
    }

    private void initTitle() {
        setLeftTitleVisible(true);
        setRightBtnVisiable(View.VISIBLE);
        setTitleValue("生利宝");
        setRightBtnImg(R.drawable.right_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(A_Slb.this, A_Slb_List.class));
            }
        });
    }

    private void init() {
        findViewById(R.id.button_out).setOnClickListener(this);
        findViewById(R.id.button_in).setOnClickListener(this);
        findViewById(R.id.iv_about).setOnClickListener(this);
        tv_yesterday = (TextView) findViewById(R.id.tv_yesterday);
        tv_total_top = (TextView) findViewById(R.id.tv_total_top);
        value_rate = (TextView) findViewById(R.id.value_rate);
        value_total = (TextView) findViewById(R.id.value_total);
        tv_values = (TextView) findViewById(R.id.tv_values);
        slbView = (SlbSyView) findViewById(R.id.slbView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestZH();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //交易
            case R.id.button_out:
            case R.id.button_in:
                requestJY();
                break;
            case R.id.iv_about:
                startActivity(new Intent(A_Slb.this, A_AboutSlb.class));
                break;
        }
    }


    //********************************************                        请求网络                    **********************************************

    /**
     * 胜利包产品信息查询
     */
    private void requestSLB() {
        String requestUrl = UrlsTwo.UrlSlb;
        String encodeToken = TokenUtil.getEncodeToken(A_Slb.this);

        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【A_Slb】【SLB】返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, A_Slb.this)) {
                    try {
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result != null) {
                            SlbBasic bean = MyApplication.mGson.fromJson(result.toString(), SlbBasic.class);
                            if (null != bean) {
                                value_rate.setText(String.format("%.2f", bean.getPrdRate()) + "");
                                List<SlbBasic.DailyRateListBean> dailyRateList = bean.getDailyRateList();
                                if (null != dailyRateList && dailyRateList.size() > 0) {
                                    float[] values = new float[7];
                                    StringBuilder sb = new StringBuilder();
                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat format2 = new SimpleDateFormat("MM.dd");
                                    for (int i = 0; i < dailyRateList.size(); i++) {
                                        values[i] = dailyRateList.get(i).getAnnuRate();
                                        String time = format2.format(format1.parse(dailyRateList.get(i).getDate()));
                                        if (i == 1 || i == 4) sb.append(time + "       ");
                                        else sb.append(time + "      ");
                                    }
                                    slbView.setHeights(values);////服务器返回数据有bug******************************************************************************************
//                                    slbView.setHeights(new float[]{2.1112f, 2.2311f, 2.3001f, 2.1234f, 2.3421f, 2.0015f, 2.3256f});
                                    slbView.setVisibility(View.VISIBLE);
                                    tv_values.setText(sb.toString());
                                    LogUtil.i("bqt", "【A_Slb】【onResponse】" + sb.toString());

                                }
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【A_Slb】【SLB】" + e.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【A_Slb】【SLB】网络异常，请稍后重试" + error.toString());
            }
        });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }


    /**
     * 账户
     */
    private void requestZH() {
        String requestUrl = UrlsTwo.UrlSlbZH;
        String encodeToken = TokenUtil.getEncodeToken(A_Slb.this);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【A_Slb】【ZH】返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, A_Slb.this)) {
                    try {
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result != null) {
                            SlbZH bean = MyApplication.mGson.fromJson(result.toString(), SlbZH.class);
                            if (null != bean) {
                                tv_yesterday.setText(String.format("%.2f", bean.getYesterdayProfit()) + "");
                                tv_total_top.setText(String.format("%.2f", bean.getTotalAsset()) + "");
                                value_total.setText(String.format("%.2f", bean.getTotalProfit()) + "");
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【A_Slb】【ZH】" + e.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【A_Slb】【ZH】网络异常，请稍后重试" + error.toString());
            }
        });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    /**
     * 交易
     */
    private void requestJY() {
        String requestUrl = UrlsTwo.UrlSlbJY;
        String encodeToken = TokenUtil.getEncodeToken(this);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【A_Slb】【JY】返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, A_Slb.this)) {
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
                            FundCustodianWebActivity.launche(A_Slb.this, "生利宝", postUrl, postData);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【A_Slb】【JY】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(A_Slb.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【A_Slb】【v】网络异常，请稍后重试" + error.toString());
            }
        });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }
}
