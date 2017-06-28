package com.bcb.module.discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.BaseFragment1;
import com.bcb.module.discover.carinsurance.CarInsuranceActivity;
import com.bcb.module.discover.eliteloan.EliteLoanActivity;
import com.bcb.module.discover.financialproduct.InvestmentFinanceActivity;
import com.bcb.module.discover.welfare.Activity_Love;
import com.bcb.module.login.LoginActivity;
import com.bcb.module.myinfo.balance.FundCustodianAboutActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.bcb.R.id.title_text;

/**
 * Created by ruiqin.shen
 * 类说明：发现页面
 */
public class DiscoverFragment extends BaseFragment1 {
    @BindView(R.id.ll_jk)
    LinearLayout ll_jk;
    @BindView(R.id.ll_cx)
    LinearLayout ll_cx;
    @BindView(R.id.ll_lc)
    LinearLayout ll_lc;
    @BindView(R.id.ll_gjj)
    LinearLayout ll_gjj;
    @BindView(R.id.ll_zx)
    LinearLayout ll_zx;
    @BindView(R.id.ll_ja)
    LinearLayout ll_ja;
    @BindView(R.id.jk)
    TextView jk;
    @BindView(R.id.cx)
    TextView cx;
    @BindView(R.id.lc)
    TextView lc;
    @BindView(R.id.gjj)
    TextView gjj;
    @BindView(R.id.zx)
    TextView zx;
    @BindView(R.id.ja)
    TextView ja;

    private Unbinder unbinder;
    private Context ctx;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        unbinder = ButterKnife.bind(this, view);
        ((TextView) view.findViewById(title_text)).setText("发现");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.ctx = view.getContext();
    }

    @OnClick({R.id.ll_jk, R.id.ll_cx, R.id.ll_gjj, R.id.ll_zx, R.id.ll_ja, R.id.ll_lc})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_jk:
                loanMainPage();
                break;
            case R.id.ll_cx:
                cheXian();
                break;
            case R.id.ll_lc:
                startActivity(InvestmentFinanceActivity.newIntent(ctx));
                break;
            case R.id.ll_gjj:
                Toast.makeText(ctx, "敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_zx:
                Toast.makeText(ctx, "敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_ja:
                Activity_Love.launche(ctx);
                break;
        }
    }

    //车险
    private void cheXian() {
        if (MyApplication.saveUserInfo.getAccess_Token() == null) {
            LoginActivity.launche(ctx);
        } else if (MyApplication.mUserDetailInfo == null || TextUtils.isEmpty(MyApplication.mUserDetailInfo.CarInsuranceIndexPage)) {
            Toast.makeText(ctx, "网络异常，请刷新后重试", Toast.LENGTH_SHORT).show();
        } else {
            CarInsuranceActivity.launche(ctx, "车险内购", MyApplication.mUserDetailInfo.CarInsuranceIndexPage);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    //借款
    private void loanMainPage() {
        if (MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.HasOpenCustody) {//已开通托管
            startActivity(new Intent(ctx, EliteLoanActivity.class));
        } else {//未开通托管
            startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
        }
    }
}