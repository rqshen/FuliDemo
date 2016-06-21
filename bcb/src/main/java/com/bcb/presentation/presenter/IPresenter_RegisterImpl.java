package com.bcb.presentation.presenter;

import android.content.Context;

import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.presentation.model.IModel_UserAccount;
import com.bcb.presentation.model.IModel_UserAccountImpl;
import com.bcb.presentation.view.activity_interface.Interface_Verification;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cain on 16/3/18.
 */
public class IPresenter_RegisterImpl implements IPresenter_Register {

    private Interface_Verification interfaceRegister;

    private IModel_UserAccount iModelUserAccount;

    private Context context;

    private BcbRequestQueue requestQueue;

    public IPresenter_RegisterImpl(Context context, Interface_Verification interfaceRegister) {
        this.interfaceRegister = interfaceRegister;
        this.iModelUserAccount = new IModel_UserAccountImpl();
        this.context = context;
        requestQueue = App.getInstance().getRequestQueue();
    }

    @Override
    public void getVerificaionCode(String phoneNumber) {
        //判断手机号码是否正确
        //获取验证码
        JSONObject obj = new org.json.JSONObject();
        try {
            obj.put("Mobile", phoneNumber);
            obj.put("CodeType", 1);
            obj.put("Platform", 2);
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserGetRegiCode, obj, null, new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        String message = response.getString("message");
                        interfaceRegister.getVerificationResult(status, message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(Exception error) {
                    interfaceRegister.getVerificationResult(-100, "网络异常，请稍后重试");
                }
            });
            jsonRequest.setTag(BcbRequestTag.BCB_VERIFICATION_REQUEST);
            requestQueue.add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doRegister(String phoneNumber, String inputPassword, String verificationCode) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Mobile",  phoneNumber);
            obj.put("VCode", verificationCode);
            obj.put("Password", inputPassword);
            obj.put("Platform", 2);
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserDoRegister, obj, null, new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        String message = response.getString("message");
                        interfaceRegister.onRequestResult(status, message);
                        if(status == 1) {
                            //存储Token
                            iModelUserAccount.saveAccessToken(response.getJSONObject("result").getString("Access_Token"));
                            iModelUserAccount.setFirstLogin(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        interfaceRegister.onRequestResult(-100, "解析数据出错");
                    }
                }

                @Override
                public void onErrorResponse(Exception error) {
                    interfaceRegister.onRequestResult(-1, "网络异常，请稍后重试");
                }
            });
            jsonRequest.setTag(BcbRequestTag.BCB_REGISTER_REQUEST);
            requestQueue.add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearDependency() {
        requestQueue.cancelAll(BcbRequestTag.BCB_VERIFICATION_REQUEST);
        interfaceRegister = null;
        iModelUserAccount = null;
    }
}
