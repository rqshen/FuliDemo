package com.bcb.presentation.model;

import android.content.Context;

/**
 * Created by cain on 16/3/18.
 */
public interface IModel_UserAccount {
    void clearAccount();
    void saveAcount(String userDetailInfo);
    void saveAccount(String token, String phoneNumber, String userDetailInfo);
    void saveAccessToken(String token);
    void setFirstLogin(boolean status);
    void updateJoinedCompanyName(String shortName, int status);
    void clearGesturePassord(Context context);
    boolean hasCert();
    boolean hasTradePassword();
    String getUserName();
    String getIDCard();
    String getCardNumber();
    String getLocalPhone();
    int getCompanyStatus();
    String getCompanyShortName();
    boolean isUserDetailInfoEmpty();
}