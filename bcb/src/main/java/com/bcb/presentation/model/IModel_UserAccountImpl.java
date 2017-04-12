package com.bcb.presentation.model;

import android.content.Context;

import com.bcb.MyApplication;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.utils.LogUtil;

/**
 * Created by cain on 16/3/18.
 */
public class IModel_UserAccountImpl implements IModel_UserAccount {
    @Override
    public void clearAccount() {
        MyApplication.saveUserInfo.clear();
        MyApplication.mUserDetailInfo = null;
        MyApplication.mUserWallet = null;
        MyApplication.viewJoinBanner = true;
        LogUtil.i("bqt", "【clearAccount】"+ MyApplication.saveUserInfo.getAccess_Token());
    }

    @Override
    public void saveAcount(String userDetailInfo) {
        if (userDetailInfo.isEmpty()){
            return;
        }
        MyApplication.mUserDetailInfo = MyApplication.mGson.fromJson(userDetailInfo, UserDetailInfo.class);
    }

    @Override
    public void saveAccount(String token, String phoneNumber, String userDetailInfo) {
        //本地只需要保存Token和第一次登陆信息
        MyApplication.saveUserInfo.setAccess_Token(token);
        MyApplication.saveUserInfo.setFirstLogin(true);
        MyApplication.saveUserInfo.setLocalPhone(phoneNumber);
        MyApplication.mUserDetailInfo = MyApplication.mGson.fromJson(userDetailInfo, UserDetailInfo.class);
    }


    @Override
    public void saveAccessToken(String token) {
        MyApplication.saveUserInfo.setAccess_Token(token);
    }

    @Override
    public void setFirstLogin(boolean status) {
        MyApplication.saveUserInfo.setFirstLogin(status);
    }
    
    @Override
    public void updateJoinedCompanyName(String shortName, int status) {
        MyApplication.mUserDetailInfo.MyCompany.setShortName(shortName);
        MyApplication.mUserDetailInfo.MyCompany.Status = status;
    }

    @Override
    public void clearGesturePassord(Context context) {
        MyApplication.saveUserInfo.remove(context, "GesturePassword");
    }

    @Override
    public boolean hasCert() {
//        return MyApplication.mUserDetailInfo.isHasCert();
        return MyApplication.mUserDetailInfo.HasOpenCustody;//更改为是否开通托管
    }

    @Override
    public boolean hasTradePassword() {
        return MyApplication.mUserDetailInfo.isHasTradePassword();
    }

    @Override
    public String getUserName() {
        return MyApplication.mUserDetailInfo.getRealName();
    }

    @Override
    public String getIDCard() {
        if (MyApplication.mUserDetailInfo != null) {
            return MyApplication.mUserDetailInfo.getIDCard();
        }
        return null;
    }

    @Override
    public String getCardNumber() {
        if (MyApplication.mUserDetailInfo.BankCard != null) {
            return MyApplication.mUserDetailInfo.getBankCard().getCardNumber();
        }
        return null;
    }

    @Override
    public String getLocalPhone() {
        return MyApplication.saveUserInfo.getLocalPhone();
    }

    @Override
    public int getCompanyStatus() {
        if (MyApplication.mUserDetailInfo.MyCompany != null) {
            return MyApplication.mUserDetailInfo.getMyCompany().getStatus();
        }
        //表示公司信息为空
        return -100;
    }

    @Override
    public String getCompanyShortName() {
        if (MyApplication.mUserDetailInfo.MyCompany != null) {
            return MyApplication.mUserDetailInfo.MyCompany.getShortName();
        }
        return null;
    }

    @Override
    public boolean isUserDetailInfoEmpty() {
        return MyApplication.mUserDetailInfo == null;
    }

}
