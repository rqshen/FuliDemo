package com.bcb.presentation.presenter;

/**
 * Created by cain on 16/3/18.
 */
public interface IPresenter_Register {
    void getVerificaionCode(String phoneNumber);
    void doRegister(String phoneNumber, String inputPassword, String verificationCode);
    void clearDependency();
}
