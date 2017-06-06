package com.bcb.presentation.presenter;

import android.content.Context;

import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;
import com.bcb.presentation.model.IModel_UserAccount;
import com.bcb.presentation.model.IModel_UserAccountImpl;
import com.bcb.presentation.view.activity_interface.Interface_Base;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cain on 16/3/30.
 */
public class IPresenter_UpdateUserInfoImpl implements IPresenter_Base {
    private Interface_Base interfaceBase;
    private IModel_UserAccount iModelUserAccount;
    private Context context;
    private BcbRequestQueue requestQueue;

    public IPresenter_UpdateUserInfoImpl(Context context, Interface_Base interfaceBase) {
        this.context = context;
        this.iModelUserAccount = new IModel_UserAccountImpl();
        this.interfaceBase = interfaceBase;
        requestQueue = MyApplication.getInstance().getRequestQueue();
    }

    @Override
    public void onRequest(int statusCode, String message) {
        if (statusCode == 1) {
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int status = response.getInt("status");
                        String message = response.getString("message");
                        //如果获取成功，则缓存数据
                        if (PackageUtil.getRequestStatus(response, context)) {
                            iModelUserAccount.saveAcount(response.toString());
                        }
                        interfaceBase.onRequestResult(status, message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        interfaceBase.onRequestResult(-100, "解析数据出错");
                    }
                }

                @Override
                public void onErrorResponse(Exception error) {
                    interfaceBase.onRequestResult(-100, "网络异常，稍后再试");
                }
            });
            jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
            requestQueue.add(jsonRequest);
        }
    }

    @Override
    public void clearDependency() {
        interfaceBase = null;
        iModelUserAccount = null;
        context = null;
    }
}