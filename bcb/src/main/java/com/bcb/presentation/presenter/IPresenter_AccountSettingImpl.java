package com.bcb.presentation.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.bcb.MyApplication;
import com.bcb.event.BroadcastEvent;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.util.LogUtil;
import com.bcb.util.TokenUtil;
import com.bcb.presentation.model.IModel_UserAccount;
import com.bcb.presentation.model.IModel_UserAccountImpl;
import com.bcb.presentation.view.activity_interface.Interface_AccountSetting;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;


/**
 * Created by cain on 16/3/28.
 */
public class IPresenter_AccountSettingImpl implements IPresenter_AccountSetting {

    //View
    private Interface_AccountSetting interfaceBase;
    //model
    private IModel_UserAccount iModelUserAccount;

    //请求队列
    private BcbRequestQueue requestQueue;

    private Context context;
    
    public IPresenter_AccountSettingImpl(Context context, Interface_AccountSetting interfaceBase) {
        this.iModelUserAccount = new IModel_UserAccountImpl();
        this.context = context;
        this.interfaceBase = interfaceBase;
        this.requestQueue = MyApplication.getInstance().getRequestQueue();
    }

    @Override
    public void updateUserInfo(int status) {
        switch (status) {
            //获取用户数据
            case 1:
                if (iModelUserAccount.isUserDetailInfoEmpty() || TextUtils.isEmpty(iModelUserAccount.getCardNumber())) {
                    getUserInfomationRequest();
                } else {
                    callBackUserInfo();
                }
                break;

            //退出登录
            case 2:
                logoutReuqest();
                break;

            //清除手势密码
            case 4:
                iModelUserAccount.clearGesturePassord(context);
                break;
        }
    }

    private void logoutReuqest() {
        BcbJsonRequest bcbJsonRequest = new BcbJsonRequest(UrlsOne.UserDoLogout, null, TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【退出登录】"+response.toString());

                try{
                    //如果返回成功清空数据
                    if (response.getInt("status") == 1) {
                        if (null != iModelUserAccount){
                            iModelUserAccount.clearAccount();
                        }
                        interfaceBase.onRequestResult(1000, null);
                    }
                    //如果返回不成功，则回调出错信息
                    else {
                        interfaceBase.onRequestResult(response.getInt("status"), response.getString("message"));
                    }
                    EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.LOGOUT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                interfaceBase.onRequestResult(-100, "网络异常，请稍后重试");
            }
        });
        bcbJsonRequest.setTag(BcbRequestTag.BCB_LOGOUT_REQUEST);
        requestQueue.add(bcbJsonRequest);
    }

    /**
     * 获取用户信息
     */
    private void getUserInfomationRequest() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.GetUserInfo, null, TokenUtil.getEncodeToken(context), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "【IPresenter_AccountSettingImpl】【onResponse】获取用户信息" + response.toString());

                try{
                    int status = response.getInt("status");
                    String message = response.getString("message");
                    if(status == 1) {
                        //存储信息
                        iModelUserAccount.saveAcount(response.getJSONObject("result").toString());
                        //回调用户信息
                        callBackUserInfo();
                    } else {
                        interfaceBase.onRequestResult(status, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                interfaceBase.onRequestResult(-100, "请求异常，请稍后再试");
            }
        });
        //给请求添加队列
        jsonRequest.setTag(BcbRequestTag.BCB_GET_USER_INFORMATION_REQUEST);
        requestQueue.add(jsonRequest);
    }

    /**
     * 回调用户信息
     */
    private void callBackUserInfo() {
        String companyMessage;
        if (!TextUtils.isEmpty(iModelUserAccount.getCompanyShortName())) {
            companyMessage = "修改";
        } else {
            companyMessage = "加入公司";
        }
        //回传用户信息
        interfaceBase.onRequestResult(iModelUserAccount.hasCert(),
                iModelUserAccount.hasTradePassword(),
                iModelUserAccount.getUserName(),
                iModelUserAccount.getIDCard(),
                iModelUserAccount.getCardNumber(),
                iModelUserAccount.getLocalPhone(),
                companyMessage);
    }

    @Override
    public void clearDependency() {
        requestQueue.cancelAll(BcbRequestTag.BCB_GET_USER_INFORMATION_REQUEST);
        interfaceBase = null;
        iModelUserAccount = null;
        context = null;
    }
}
