package com.bcb.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bcb.util.LogUtil;
import com.bcb.constant.MyConstants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by cain on 16/1/12.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, MyConstants.APP_ID, false);
        //注册通知
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //注册通知，如果不注册的话
        api.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
//        switch (req.getType()) {
//            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                finish();
//                break;
//            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                finish();
//                break;
//            default:
//                break;
//        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        LogUtil.d("返回结果码", resp.errCode+"");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
//                ToastUtil.alert(WXEntryActivity.this, "分享成功");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                ToastUtil.alert(WXEntryActivity.this, "取消");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                ToastUtil.alert(WXEntryActivity.this, "认证失败");
                break;
            default:
//                ToastUtil.alert(WXEntryActivity.this, "未知结果");
                break;
        }
        //更新后调用finish结束当前页面
        this.finish();
    }
}