package com.bcb.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bcb.R;
import com.bcb.MyApplication;
import com.meiqia.core.MQManager;
import com.meiqia.core.callback.OnClientInfoCallback;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;

import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by cain on 16/3/30.
 */
public class MQCustomerManager {
    private static final int REQUEST_CODE_CONVERSATION_PERMISSIONS = 1;
    //请替换成APP的APPKey
    private static String meiqiaKey = "4e8d341b193f4271791767e0c834da84";
    //采用单利模式创建管理对象
    private static MQCustomerManager instance;
    private Context context;
    private static boolean hasSetClientInfo;

    public static synchronized MQCustomerManager getInstance(Context context) {
        if (instance == null) {
            instance = new MQCustomerManager(context);
            hasSetClientInfo = false;
        }
        return instance;
    }

    private MQCustomerManager(final Context context) {
        this.context = context;
        //初始化美洽客服
        MQConfig.init(context, meiqiaKey, new OnInitCallback() {
            @Override
            public void onSuccess(String clientId) {
            }

            @Override
            public void onFailure(int code, String message) {
            }
        });
        MQManager.setDebugMode(false);
    }

    //打开客服
    public void showCustomer(String userId) {
        //上传用户信息到后台
        if (!hasSetClientInfo && null != MyApplication.saveUserInfo.getAccess_Token()){
            Map<String, String> info = new HashMap<>();
            info.put("用户名", MyApplication.mUserDetailInfo.CustomerId);
            MQManager.getInstance(context).setClientInfo(info, new OnClientInfoCallback(){

                @Override
                public void onFailure(int i, String s) {

                }

                @Override
                public void onSuccess() {
                    hasSetClientInfo = true;
                }
            });
        }

        //没有美洽用户ID时，使用游客登录
        if (TextUtils.isEmpty(userId)) {
            conversationWrapper();
        }
        //存在用户时，使用APP用户的CustomerID登录
        else {
            Intent intent = new MQIntentBuilder(context)
                    .setCustomizedId(userId)
                    .build();
            context.startActivity(intent);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_CONVERSATION_PERMISSIONS)
    private void conversationWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(context, perms)) {
            conversation();
        } else {
            EasyPermissions.requestPermissions(this, context.getString(R.string.mq_runtime_permission_tip), REQUEST_CODE_CONVERSATION_PERMISSIONS, perms);
        }
    }
    private void conversation() {
        MQConfig.ui.backArrowIconResId = android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha;
        MQConfig.ui.titleBackgroundResId = R.color.red;
        MQConfig.ui.titleTextColorResId = android.R.color.white;
        MQConfig.ui.titleGravity = MQConfig.ui.MQTitleGravity.CENTER;
        Intent intent = new MQIntentBuilder(context).build();
        context.startActivity(intent);
    }
}
