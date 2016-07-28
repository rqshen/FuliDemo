package com.bcb.presentation.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bcb.R;

public class Activity_Open_Account extends Activity_Base implements View.OnClickListener{
    //标题
    private TextView title_text;
    private View back_img;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_account);
        ctx=this;
        //标题
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("汇付天下资金托管");
        back_img = findViewById(R.id.back_img);
        back_img.setVisibility(View.VISIBLE);
        back_img.setOnClickListener(this);
        findViewById(R.id.tv_open).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        openAccount();
    }


    private void openAccount() {
//        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.OpenAccount, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                LogUtil.i("bqt", "开通汇付天下资金托管账户返回：" + response.toString());
//                if (PackageUtil.getRequestStatus(response, ctx)) {
//                    JSONObject data = PackageUtil.getResultObject(response);
//                    if (data != null) {
//                        String postUrl=data.optString("PostUrl");
//                    }
//                }
//
//                @Override
//                public void onErrorResponse(Exception error) {
//                    ToastUtil.alert(ctx, "网络异常，请稍后重试");
//                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
//                    loadingError = true;
//                }
//            });
//            jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
//            App.getInstance().getRequestQueue().add(jsonRequest);
        }
}
