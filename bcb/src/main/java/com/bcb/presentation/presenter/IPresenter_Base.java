package com.bcb.presentation.presenter;

/**
 * Created by cain on 16/3/30.
 */
public interface IPresenter_Base {
    void onRequest(int statusCode, String message);
    void clearDependency();
}
