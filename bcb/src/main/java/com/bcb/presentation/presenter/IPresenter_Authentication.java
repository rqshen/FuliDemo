package com.bcb.presentation.presenter;

/**
 * Created by cain on 16/3/30.
 */
public interface IPresenter_Authentication {
    void onUpdateUserInfo();
    void onAuthenticate(String bankCode, String bankName, String bankCard, String IDCard, String userName, String bankCardPhone);
    void clearDependency();
}
