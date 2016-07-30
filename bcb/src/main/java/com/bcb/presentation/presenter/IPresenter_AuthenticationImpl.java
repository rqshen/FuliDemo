package com.bcb.presentation.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.model.IModel_UserAccount;
import com.bcb.presentation.model.IModel_UserAccountImpl;
import com.bcb.presentation.view.activity_interface.Interface_Authentication;

import org.json.JSONObject;

/**
 * Created by cain on 16/3/30.
 */
public class IPresenter_AuthenticationImpl implements IPresenter_Authentication {

    //View
    private Interface_Authentication interfaceBase;
    //Model
    private IModel_UserAccount iModelUserAccount;

    private Context context;

    //队列
    private BcbRequestQueue requestQueue;

    public IPresenter_AuthenticationImpl(Context context, Interface_Authentication interfaceBase) {
        this.context = context;
        this.interfaceBase = interfaceBase;
        iModelUserAccount = new IModel_UserAccountImpl();
        requestQueue = App.getInstance().getRequestQueue();
    }

    @Override
    public void onUpdateUserInfo() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserMessage, null,
                TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (PackageUtil.getRequestStatus(response, context)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        //获取用户银行卡信息
                        iModelUserAccount.saveAcount(data.toString());
                    }
                    //存在银行卡信息、已认证、存在银行卡时才是已经认证
                    if (!iModelUserAccount.isUserDetailInfoEmpty() && iModelUserAccount.hasCert() && !TextUtils.isEmpty(iModelUserAccount.getCardNumber())) {
                        interfaceBase.onRequestResult(1, "你已经认证过了");
                    } else {
                        interfaceBase.onRequestResult(2, "您还没有认证");
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                interfaceBase.onRequestResult(-100, "获取用户个人信息失败，请稍后再试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
        requestQueue.add(jsonRequest);
    }

    @Override
    public void onAuthenticate(String bankCode, String bankName, String bankCard, String IDCard, String userName, String bankcardPhone) {
        JSONObject data = new JSONObject();
        try {
            data.put("BankCode", bankCode);//银行BankCode
            data.put("BankName", bankName);//银行名称
            data.put("CardNumber", bankCard);//银行卡号
            data.put("IDCard", IDCard);          //身份证号
            data.put("HolderName", userName);    //用户名
            data.put("CardMobile", bankcardPhone); //预留手机号
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Authentication, data,
                    TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (PackageUtil.getRequestStatus(response, context)) {
                            JSONObject data = PackageUtil.getResultObject(response.toString());
                            //判断JSON对象是否为空
                            if (data != null) {
                                //获取用户银行卡信息
                                iModelUserAccount.saveAcount(data.toString());
                            }
                            //存在银行卡信息、已认证、存在银行卡时才是已经认证
                            if (!iModelUserAccount.isUserDetailInfoEmpty() && iModelUserAccount.hasCert() && !TextUtils.isEmpty(iModelUserAccount.getCardNumber())) {
                                interfaceBase.onRequestResult(1, "你已经认证过了");
                            } else {
                                interfaceBase.onRequestResult(2, "您还没有认证");
                            }
                        } else {
                            interfaceBase.onRequestResult(response.getInt("status"), response.getString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        interfaceBase.onRequestResult(-100, "返回数据异常，解析出错");
                    }
                }

                @Override
                public void onErrorResponse(Exception error) {
                    interfaceBase.onRequestResult(-100, "网络异常，请稍后再试");
                }
            });
            jsonRequest.setTag(BcbRequestTag.BCB_AUTHENTICATION_REQUEST);
            requestQueue.add(jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //页面退出前清空依赖
    @Override
    public void clearDependency() {
        requestQueue.cancelAll(BcbRequestTag.UserBankMessageTag);
        requestQueue.cancelAll(BcbRequestTag.BCB_AUTHENTICATION_REQUEST);
        interfaceBase = null;
        iModelUserAccount = null;
        context = null;
    }
}
