package com.bcb.presentation.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Activity_TuoGuan_HF extends Activity_Base implements View.OnClickListener {

    TextView tv_account, tv_monery, tv_charge, tv_get;
    RelativeLayout rl_look, rl_login, rl_find_deal, rl_find_login, rl_alert_deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuo_guan_hf);
        initTitle();
        findViews();
        setOnClickListenerd();

    }

    private void initTitle() {
        //标题
        TextView title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("汇付天下资金托管");
        //返回
        View back_img = findViewById(R.id.back_img);
        back_img.setVisibility(View.VISIBLE);
        back_img.setOnClickListener(this);
    }

    private void setOnClickListenerd() {
        tv_charge.setOnClickListener(Activity_TuoGuan_HF.this);
        tv_get.setOnClickListener(Activity_TuoGuan_HF.this);
        rl_look.setOnClickListener(Activity_TuoGuan_HF.this);
        rl_login.setOnClickListener(Activity_TuoGuan_HF.this);
        rl_find_deal.setOnClickListener(Activity_TuoGuan_HF.this);
        rl_find_login.setOnClickListener(Activity_TuoGuan_HF.this);
        rl_alert_deal.setOnClickListener(Activity_TuoGuan_HF.this);
    }

    private void findViews() {
        tv_account = (TextView) findViewById(R.id.tv_account);
        tv_monery = (TextView) findViewById(R.id.tv_monery);
        tv_charge = (TextView) findViewById(R.id.tv_charge);
        tv_get = (TextView) findViewById(R.id.tv_get);
        rl_look = (RelativeLayout) findViewById(R.id.rl_look);
        rl_login = (RelativeLayout) findViewById(R.id.rl_login);
        rl_find_deal = (RelativeLayout) findViewById(R.id.rl_find_deal);
        rl_find_login = (RelativeLayout) findViewById(R.id.rl_find_login);
        rl_alert_deal = (RelativeLayout) findViewById(R.id.rl_alert_deal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.tv_charge:
                Toast.makeText(Activity_TuoGuan_HF.this, "………………充值……………", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_get:
                Toast.makeText(Activity_TuoGuan_HF.this, "………………提款……………", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_look:
                Toast.makeText(Activity_TuoGuan_HF.this, "………………暂无……………", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_login:
                loginAccount();
                break;
            case R.id.rl_find_deal:
                alertLoginPassword("找回交易密码");
                break;
            case R.id.rl_find_login:
                alertLoginPassword("找回登录密码");
                break;
            case R.id.rl_alert_deal:
                alertLoginPassword("修改交易密码");
                break;

            default:
                break;
        }
    }

    /**
     * 登录汇付账户
     */
    private void loginAccount() {
        String requestUrl = UrlsTwo.LoginAccount;
        String encodeToken = TokenUtil.getEncodeToken(Activity_TuoGuan_HF.this);
        LogUtil.i("bqt", "【Activity_TuoGuan_HF】【loginAccount】请求路径：" + requestUrl);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Activity_TuoGuan_HF】【loginAccount】" + response.toString());
                if (PackageUtil.getRequestStatus(response, Activity_TuoGuan_HF.this)) {
                    try {
                        /** 后台返回的JSON对象，也是要转发给汇付的对象 */
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result != null) {
                            //网页地址
                            String postUrl = result.optString("PostUrl");
                            result.remove("PostUrl");//移除这个参数
                            //传递的参数
                            String postData = jsonToStr(result.toString()); //跳转到webview
                            Activity_Browser.launche(Activity_TuoGuan_HF.this, "登录汇付账户", postUrl, true, postData);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【Activity_TuoGuan_HF】【loginAccount】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(Activity_TuoGuan_HF.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【Activity_TuoGuan_HF】【loginAccount】网络异常，请稍后重试" + error.toString());
            }
        });
        App.getInstance().getRequestQueue().add(jsonRequest);
    }

    /**
     * 修改汇付登录密码
     */
    private void alertLoginPassword(final String title) {
        String requestUrl = UrlsTwo.LoginAccountAlert;
        String encodeToken = TokenUtil.getEncodeToken(Activity_TuoGuan_HF.this);
        LogUtil.i("bqt", "【Activity_TuoGuan_HF】【alertLoginPassword】请求路径：" + requestUrl);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Activity_TuoGuan_HF】【alertLoginPassword】" + response.toString());
                if (PackageUtil.getRequestStatus(response, Activity_TuoGuan_HF.this)) {
                    try {
                        /** 后台返回的JSON对象，也是要转发给汇付的对象 */
                        JSONObject result = PackageUtil.getResultObject(response);
                        if (result != null) {
                            //网页地址
                            String postUrl = result.optString("PostUrl");
                            result.remove("PostUrl");//移除这个参数
                            //传递的参数
                            String postData = jsonToStr(result.toString()); //跳转到webview
                            Activity_Browser.launche(Activity_TuoGuan_HF.this, title, postUrl, true, postData);
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【Activity_TuoGuan_HF】【alertLoginPassword】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(Activity_TuoGuan_HF.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【Activity_TuoGuan_HF】【alertLoginPassword】网络异常，请稍后重试" + error.toString());
            }
        });
        App.getInstance().getRequestQueue().add(jsonRequest);
    }

    /**
     * 将json格式的字符串解析成http中的传递的参数
     */
    public static String jsonToStr(String jString) throws JSONException {
        JSONObject jObject = new JSONObject(jString);
        // 将json字符串转换成jsonObject
        if (jObject != null && !jObject.equals("")) {
            Iterator<String> it = jObject.keys();
            StringBuilder strBuilder = new StringBuilder();
            // 遍历JSON数据，添加到Map对象
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                Object value = jObject.get(key);
                strBuilder.append(key + "=").append(value.toString()).append("&");
            }
            if (strBuilder.toString().endsWith("&")) {
                strBuilder.deleteCharAt(strBuilder.length() - 1);
            }
            return strBuilder.toString();
        } else {
            return "";
        }
    }
}
