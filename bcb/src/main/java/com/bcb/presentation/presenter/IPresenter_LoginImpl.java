package com.bcb.presentation.presenter;

import android.content.Context;
import android.text.TextUtils;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.presentation.model.IModel_UserAccount;
import com.bcb.presentation.model.IModel_UserAccountImpl;
import com.bcb.presentation.view.activity_interface.Interface_Base;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.util.RegexManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cain on 16/3/18.
 *
 * Presenter 实例
 */
public class IPresenter_LoginImpl implements IPresenter_Login {
    //View
    private Interface_Base interfaceLogin;
    //Model
    private IModel_UserAccount iModelUserAccount;
    //暂存手机号码
    private String phoneNumber;

    //队列
    private BcbRequestQueue requestQueue;

    //构造器
    public IPresenter_LoginImpl(Interface_Base interfaceLogin) {
        this.interfaceLogin = interfaceLogin;
        this.iModelUserAccount = new IModel_UserAccountImpl();
        requestQueue = BcbNetworkManager.newRequestQueue((Context) interfaceLogin);
    }

    @Override
    public void clearAccount() {
        //清空账户信息
        iModelUserAccount.clearAccount();
    }

    //登录操作
    @Override
    public void doLogin(String name, String passwd) {
        //判断出错
        if (TextUtils.isEmpty(name)) {
            interfaceLogin.onRequestResult(1, "请输入手机号码");
            return;
        } else if (TextUtils.isEmpty(passwd)) {
            interfaceLogin.onRequestResult(1, "请输入密码");
            return;
        } else if (!RegexManager.isPhoneNum(name)){
            interfaceLogin.onRequestResult(1, "请输入正确的手机号码");
            return;
        } else if (!RegexManager.isPasswordNum(passwd)) {
            interfaceLogin.onRequestResult(1, "请输入正确的密码");
            return;
        }
        //缓存手机号码
        phoneNumber = name;
        //请求登录
        JSONObject obj = new JSONObject();
        try {
            obj.put("Mobile", name);
            obj.put("Password", passwd);
            obj.put("Platform", 2);
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserDoLogin, obj, null, jsonCallBack);
            //给请求添加队列
            jsonRequest.setTag(BcbRequestTag.BCB_LOGIN_REQUEST);
            requestQueue.add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    BcbRequest.BcbCallBack<JSONObject> jsonCallBack = new BcbRequest.BcbCallBack<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if (response != null) {
                    if (response.getInt("status") == 1) {
                        JSONObject result = response.getJSONObject("result");
                        if (result != null) {
                            //清除掉缓存数据
                            iModelUserAccount.clearAccount();
                            //保存账号信息
                            iModelUserAccount.saveAccount(result.getString("Access_Token"), phoneNumber, result.toString());
                        }
                        //登录成功
                        interfaceLogin.onRequestResult(0, "登录成功");
                    } else {
                        //登录失败
                        interfaceLogin.onRequestResult(response.getInt("status"),
                                TextUtils.isEmpty(response.getString("message")) ? "请检查账号或密码是否正确" : response.getString("message"));
                    }
                } else {
                    interfaceLogin.onRequestResult(4, "网络异常，请稍后重试");
                }
            } catch (JSONException e) {
                e.getMessage();
                interfaceLogin.onRequestResult(-100, "解析数据出错");
            }
        }

        @Override
        public void onErrorResponse(Exception error) {
            interfaceLogin.onRequestResult(4, "请求异常，请稍后重试");
        }
    };


    @Override
    public void clearDependency() {
        requestQueue.cancelAll(BcbRequestTag.BCB_LOGIN_REQUEST);
        requestQueue = null;
        interfaceLogin = null;
        iModelUserAccount = null;
    }
}