package com.bcb.module.myinfo.balance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.network.UrlsTwo;

/**
 * 资金托管，关于汇付
 */
public class FundCustodianAboutActivity extends Activity_Base {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_open_account);
        setTitleValue("资金托管");
        setLeftTitleVisible(true);
        setRightBtnVisiable(View.VISIBLE);
        setRightTitleValue("关于汇付", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectDetailActivity.launche(FundCustodianAboutActivity.this, "关于汇付", UrlsTwo.UrlAboutHF);
            }
        });
        findViewById(R.id.tv_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FundCustodianAboutActivity.this, FundCustodianIdCardActivity.class));
            }
        });
    }
}