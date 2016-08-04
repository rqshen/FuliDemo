package com.bcb.presentation.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.BanksBean;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.BanksAdapter;
import com.bcb.presentation.view.custom.MyListView;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 充值
 */
public class Activity_Charge_HF extends Activity_Base implements View.OnClickListener {
    TextView tv_left_monery, tv_next, tv_tip_bottom;
    EditText et_add_monery;
    BanksAdapter adapter;
    MyListView lv_banks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_hf);
        initTitle();
        lv_banks = (MyListView) findViewById(R.id.lv_banks);
        tv_left_monery = (TextView) findViewById(R.id.tv_left_monery);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_tip_bottom = (TextView) findViewById(R.id.tv_tip_bottom);
        et_add_monery = (EditText) findViewById(R.id.et_add_monery);

        if (App.getInstance().mUserWallet != null) {
            tv_left_monery.setText(App.getInstance().mUserWallet.getBalanceAmount() + "元");
        }
        tv_next.setOnClickListener(this);
        requestBankList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.tv_next:
                if (TextUtils.isEmpty(et_add_monery.getText().toString().trim())) {
                    Toast.makeText(Activity_Charge_HF.this, "请输入充值金额", Toast.LENGTH_SHORT).show();
                } else
                    requestCharge();
                break;
            default:
                break;
        }
    }

    private void initTitle() {
        //标题
        TextView title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("充值");
        //返回
        View back_img = findViewById(R.id.back_img);
        back_img.setVisibility(View.VISIBLE);
        back_img.setOnClickListener(this);
    }

    /**
     * 充值
     */
    private void requestCharge() {
        String requestUrl = UrlsTwo.UrlCharge;
        String encodeToken = TokenUtil.getEncodeToken(Activity_Charge_HF.this);
        LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】请求路径：" + requestUrl);
        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("Amount", et_add_monery.getText().toString().trim());
            LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】请求参数：" + requestObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, requestObj, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Activity_Charge_HF】【Charge】返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, Activity_Charge_HF.this)) {
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
                            Activity_Browser.launche(Activity_Charge_HF.this, "充值", postUrl, true, postData);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【Activity_Open_Account】【OpenAccount】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(Activity_Charge_HF.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【Activity_Charge_HF】【Charge】网络异常，请稍后重试" + error.toString());
            }
        });
        App.getInstance().getRequestQueue().add(jsonRequest);
    }

    /**
     * 银行列表
     */
    private void requestBankList() {
        String requestUrl = UrlsTwo.UrlBanks;
        String encodeToken = TokenUtil.getEncodeToken(Activity_Charge_HF.this);
        LogUtil.i("bqt", "【Activity_Charge_HF】【BankList】请求路径：" + requestUrl);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Activity_Charge_HF】【BankList】返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, Activity_Charge_HF.this)) {
                    try {
                        List<BanksBean> list = App.mGson.fromJson(response.optJSONArray("result").toString(), new TypeToken<List<BanksBean>>() {
                        }.getType());
                        if (list != null) {
                            adapter = new BanksAdapter(Activity_Charge_HF.this, list);
                            lv_banks.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【Activity_Charge_HF】【BankList】" + e.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【Activity_Charge_HF】【BankList】网络异常，请稍后重试" + error.toString());
            }
        });
        App.getInstance().getRequestQueue().add(jsonRequest);
    }
}
