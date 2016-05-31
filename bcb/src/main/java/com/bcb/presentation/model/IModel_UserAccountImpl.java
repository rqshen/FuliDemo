package com.bcb.presentation.model;

import android.content.Context;

import com.bcb.common.app.App;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.util.DESUtil;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyConstants;

/**
 * Created by cain on 16/3/18.
 */
public class IModel_UserAccountImpl implements IModel_UserAccount {
    @Override
    public void clearAccount() {
        App.saveUserInfo.clear();
        App.mUserDetailInfo = null;
        App.mUserWallet = null;
        App.viewJoinBanner = true;
    }

    @Override
    public void saveAcount(String userDetailInfo) {
        if (userDetailInfo.isEmpty()){
            return;
        }
        App.mUserDetailInfo = App.mGson.fromJson(userDetailInfo, UserDetailInfo.class);
    }

    @Override
    public void saveAccount(String token, String phoneNumber, String userDetailInfo) {
        //本地只需要保存Token和第一次登陆信息
        App.saveUserInfo.setAccess_Token(token);
        App.saveUserInfo.setFirstLogin(true);
        App.saveUserInfo.setLocalPhone(phoneNumber);
        App.mUserDetailInfo = App.mGson.fromJson(userDetailInfo, UserDetailInfo.class);
    }

    @Override
    public void saveAccessToken(String token) {
        App.saveUserInfo.setAccess_Token(token);
    }

    @Override
    public void setFirstLogin(boolean status) {
        App.saveUserInfo.setFirstLogin(status);
    }
    
    @Override
    public void updateJoinedCompanyName(String shortName, int status) {
        App.mUserDetailInfo.MyCompany.setShortName(shortName);
        App.mUserDetailInfo.MyCompany.Status = status;
    }

    @Override
    public void clearGesturePassord(Context context) {
        App.saveUserInfo.remove(context, "GesturePassword");
    }

    @Override
    public boolean hasCert() {
        return App.mUserDetailInfo.isHasCert();
    }

    @Override
    public boolean hasTradePassword() {
        return App.mUserDetailInfo.isHasTradePassword();
    }

    @Override
    public String getUserName() {
        return App.mUserDetailInfo.getRealName();
    }

    @Override
    public String getIDCard() {
        if (App.mUserDetailInfo != null) {
            return App.mUserDetailInfo.getIDCard();
        }
        return null;
    }

    @Override
    public String getCardNumber() {
        if (App.mUserDetailInfo.BankCard != null) {
            return App.mUserDetailInfo.getBankCard().getCardNumber();
        }
        return null;
    }

    @Override
    public String getLocalPhone() {
        return App.saveUserInfo.getLocalPhone();
    }

    @Override
    public int getCompanyStatus() {
        if (App.mUserDetailInfo.MyCompany != null) {
            return App.mUserDetailInfo.getMyCompany().getStatus();
        }
        //表示公司信息为空
        return -100;
    }

    @Override
    public String getCompanyShortName() {
        if (App.mUserDetailInfo.MyCompany != null) {
            return App.mUserDetailInfo.MyCompany.getShortName();
        }
        return null;
    }

    @Override
    public boolean isUserDetailInfoEmpty() {
        return App.mUserDetailInfo == null;
    }

}
