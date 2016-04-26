package com.bcb.presentation.presenter;

import android.graphics.Bitmap;

/**
 * Created by cain on 16/3/18.
 */
public interface IPresenter_JoinCompany {
    //请求加入公司
    void onJoinCompany(String companyFullName, String companyShortName, String companyWebsite, int fileType);
    //删除图片缓存
    void deletePhotoCache();
    //获取缓存的图片
    Bitmap getPhotoCache();
    void clearDependency();
}
