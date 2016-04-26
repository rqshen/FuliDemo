package com.bcb.presentation.presenter;

/**
 * Created by cain on 16/3/18.
 */
public interface IPresenter_Login {
    void clearAccount();
    void doLogin(String name, String passwd);
    void clearDependency();
}
