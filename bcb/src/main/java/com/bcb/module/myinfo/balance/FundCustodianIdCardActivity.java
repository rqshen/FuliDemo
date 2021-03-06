package com.bcb.module.myinfo.balance;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.module.browse.FundCustodianWebActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsTwo;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 开通资金托管
 * 输入身份证
 */
public class FundCustodianIdCardActivity extends Activity_Base {
    @BindView(R.id.tv_title)
    TextView tv_title;//您好，资金托管需要验证您的身份
    @BindView(R.id.tv_open)
    TextView tv_open;//下一步
    @BindView(R.id.customer_service)
    TextView customer_service;//隐藏客服
    @BindView(R.id.et_idcard)
    EditText et_idcard;//身份证号
    @BindView(R.id.rl_idcard)
    RelativeLayout rl_idcard;//身份证号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_open_account2);
        setTitleValue("资金托管");
        setLeftTitleVisible(true);
        ButterKnife.bind(this);

        tv_title.setText("您好，" + MyApplication.saveUserInfo.getLocalPhone() + "\n资金托管需要验证您的身份证");//您的身份信息已绑定手机号
        tv_open.setText("下一步");
        rl_idcard.setVisibility(View.VISIBLE);
        customer_service.setVisibility(View.INVISIBLE);
        if (MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.IDCard != null && MyApplication.mUserDetailInfo.IDCard != "") {
            et_idcard.setText(MyApplication.mUserDetailInfo.IDCard);
        }
    }

    @OnClick({R.id.tv_open})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.tv_open:
                if (TextUtils.isEmpty(et_idcard.getText().toString().trim())) {
                    Toast.makeText(FundCustodianIdCardActivity.this, "身份证号不能为空", Toast.LENGTH_SHORT).show();
                } else requestOpenAccount();
                break;
            default:
                break;
        }
    }

    /**
     * 开通汇付账户
     */
    private void requestOpenAccount() {
        LogUtil.i("bqt", "【FundCustodianIdCardActivity】【requestOpenAccount】" + et_idcard.getText().toString().trim());

        JSONObject obj = new org.json.JSONObject();
        try {
            obj.put("IdCard", et_idcard.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.OpenAccount, obj, TokenUtil.getEncodeToken(this), true, new
                BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("bqt", " 开通汇付账户" + response.toString());
                        if (PackageUtil.getRequestStatus(response, FundCustodianIdCardActivity.this)) {
                            try {
                                /** 后台返回的JSON对象，也是要转发给汇付的对象 */
                                JSONObject result = PackageUtil.getResultObject(response);
                                if (result != null) {
                                    //开通自动投标
                                    if (response.getInt("status") == 1) {//成功
                                        //网页地址
                                        String postUrl = result.optString("PostUrl");
                                        result.remove("PostUrl");//移除这个参数
                                        //传递的 参数
                                        String postData = HttpUtils.jsonToStr(result.toString());
                                        //跳转到webview
                                        FundCustodianWebActivity.launche(FundCustodianIdCardActivity.this, "资金托管", postUrl, postData);
                                        finish();
                                    } else {
                                        String message = response.getString("message");
                                        Toast.makeText(FundCustodianIdCardActivity.this, message, Toast.LENGTH_SHORT).show();
//								Activity_Open_Account3.launche(FundCustodianIdCardActivity.this, message);
                                    }
                                }
                            } catch (Exception e) {
                                LogUtil.d("bqt", "【FundCustodianIdCardActivity】【OpenAccount】" + e.getMessage());
                            }
                        } else if (response != null) {
                            String message = response.optString("message");
//					Activity_Open_Account3.launche(FundCustodianIdCardActivity.this, message);
                            Toast.makeText(FundCustodianIdCardActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                        LogUtil.d("bqt", "【FundCustodianIdCardActivity】【OpenAccount】网络异常，请稍后重试" + error.toString());
                    }
                });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }
}