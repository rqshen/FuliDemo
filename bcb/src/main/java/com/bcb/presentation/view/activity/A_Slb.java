package com.bcb.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.SlbBasic;
import com.bcb.data.bean.SlbZH;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * 生利宝界面
 */
public class A_Slb extends Activity_Base implements View.OnClickListener {
    TextView tv_yesterday, tv_total_top, value_rate, value_total;
    TextView tv_sy;

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
        tv_sy = (TextView) findViewById(R.id.tv_sy);
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
                            SlbBasic bean = App.mGson.fromJson(result.toString(), SlbBasic.class);
                            if (null != bean) {
                                value_rate.setText(bean.getPrdRate() + "");
                                List<SlbBasic.DailyRateListBean> dailyRateList = bean.getDailyRateList();
                                if (null != dailyRateList && dailyRateList.size() > 0) {
                                    for (SlbBasic.DailyRateListBean b : dailyRateList) {
                                        tv_sy.append(b.getPrdRate() + "\n");
                                    }
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
        App.getInstance().getRequestQueue().add(jsonRequest);
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
                            SlbZH bean = App.mGson.fromJson(result.toString(), SlbZH.class);
                            if (null != bean) {
                                tv_yesterday.setText(bean.getYesterdayProfit() + "");
                                tv_total_top.setText(bean.getTotalAsset() + "");
                                value_total.setText(bean.getTotalProfit() + "");
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
        App.getInstance().getRequestQueue().add(jsonRequest);
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
                            Activity_WebView.launche(A_Slb.this, "生利宝", postUrl, postData);
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
        App.getInstance().getRequestQueue().add(jsonRequest);
    }
}
