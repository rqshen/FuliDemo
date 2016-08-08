package com.bcb.presentation.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;

import org.json.JSONObject;

public class Activity_Open_Account extends Activity_Base implements View.OnClickListener {
    //标题
    private TextView title_text;
    //返回
    private View back_img;
    /**
     * 开通
     */
    private TextView tv_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_account);
        //标题
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("汇付天下资金托管");
        back_img = findViewById(R.id.back_img);
        back_img.setVisibility(View.VISIBLE);
        back_img.setOnClickListener(this);
        findViewById(R.id.tv_open).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.tv_open:
                requestOpenAccount();
                break;
            default:
                break;
        }
    }


    /**
     * 开通汇付账户
     */
    private void requestOpenAccount() {
        String requestUrl = UrlsTwo.OpenAccount;
        String encodeToken = TokenUtil.getEncodeToken(Activity_Open_Account.this);
        LogUtil.i("bqt", "【Activity_Open_Account】【OpenAccount】请求路径：" + requestUrl);
        BcbJsonRequest jsonRequest = new BcbJsonRequest(requestUrl, null, encodeToken, true, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【Activity_Open_Account】【onResponse】" + response.toString());
                if (PackageUtil.getRequestStatus(response, Activity_Open_Account.this)) {
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
                            Activity_WebView.launche(Activity_Open_Account.this, "汇付天下资金托管", postUrl,  postData);
                            finish();
                        }
                    } catch (Exception e) {
                        LogUtil.d("bqt", "【Activity_Open_Account】【OpenAccount】" + e.getMessage());
                    }
                } else if (response != null) {
                    Toast.makeText(Activity_Open_Account.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                LogUtil.d("bqt", "【Activity_Open_Account】【OpenAccount】网络异常，请稍后重试" + error.toString());
            }
        });
        App.getInstance().getRequestQueue().add(jsonRequest);
    }

}