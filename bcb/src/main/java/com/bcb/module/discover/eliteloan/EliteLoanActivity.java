package com.bcb.module.discover.eliteloan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.data.bean.loan.LoanKindBean;
import com.bcb.data.bean.loan.LoanRequestInfoBean;
import com.bcb.module.discover.eliteloan.loanborrow.LoanRequestBorrowActivity;
import com.bcb.module.discover.eliteloan.loanlist.LoanListActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.presentation.view.custom.AlertView.DialogBQT;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.ProgressDialogrUtils;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 精英贷
 * 作者：baicaibang
 * 时间：2016/11/28 10:17
 */
public class EliteLoanActivity extends Activity_Base {
    @BindView(R.id.iv_brand)
    ImageView iv_brand;
    @BindView(R.id.iv_it)
    ImageView iv_it;
    @BindView(R.id.iv_signed)
    ImageView iv_signed;
    @BindView(R.id.root)
    LinearLayout root;

    //支持的企业类型集合
    private ArrayList<LoanKindBean> list;
    private LoanKindBean bean1, bean2, bean0;
    //签约企业的借款申请信息
    private LoanRequestInfoBean signedLoanRequestInfo;//**********************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_elite_loan);
        ButterKnife.bind(this);
        setLeftTitleVisible(true);
        setTitleValue("精英贷");
        setRightTitleValue("我的借款", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EliteLoanActivity.this, LoanListActivity.class));
            }
        });
        requestOpenAccount();
        //		getSingLoanCertification();		// 获取签约企业借款数据*****************************************************************
    }

    @OnClick({R.id.iv_brand, R.id.iv_it, R.id.iv_signed})
    public void onClickIv(View v) {
        //请求是否可以申请贷款
        switch (v.getId()) {
            case R.id.iv_brand://名企精英贷
                getLoanCertification(1);
                break;
            case R.id.iv_it://IT互联网精英贷
                getLoanCertification(2);
                break;
            case R.id.iv_signed://签约企业员工贷
                getLoanCertification(0);
                break;
        }
    }

    private void showDialog(String message) {
        DialogBQT diaolog = new DialogBQT(this) {
            @Override
            public void onSureClick(View v) {
                super.onSureClick(v);
                startActivity(new Intent(EliteLoanActivity.this, LoanListActivity.class));
            }
        };
        diaolog.setTitleAndMessageAndIcon(null, message, R.drawable.icon_email);//"您已申请了我们公司的aaa，\n正在处理中，请勿重复申请。"
        diaolog.setButtonText("查看我的借款", "返回", -1, 0xff999999);
        diaolog.show();
    }

    /**
     * 借款首页
     */
    private void requestOpenAccount() {
        ProgressDialogrUtils.show(this, "正在请求数据，请稍后…");
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.LOANKINDLIST, null, TokenUtil.getEncodeToken(this), true,
                new BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressDialogrUtils.hide();
                        LogUtil.i("bqt", " 借款首页" + response.toString());
                        if (PackageUtil.getRequestStatus(response, EliteLoanActivity.this)) {
                            try {
                                JSONObject result = PackageUtil.getResultObject(response);
                                if (result != null) {
                                    JSONArray jsonArray = result.getJSONArray("LoanKindList");
                                    if (jsonArray != null) {
                                        list = MyApplication.mGson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<LoanKindBean>>() {

                                        }.getType());
                                        if (list != null && list.size() >= 3) {
                                            for (int i = 0; i < list.size(); i++) {
                                                switch (list.get(i).LoanKindId) {
                                                    case 0:
                                                        bean0 = list.get(i);
                                                        break;
                                                    case 1:
                                                        bean1 = list.get(i);
                                                        break;
                                                    case 2:
                                                        bean2 = list.get(i);
                                                        break;
                                                }
                                            }
                                        }
                                        root.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (Exception e) {
                                LogUtil.d("bqt", "借款首页" + e.getMessage());
                            }
                        } else if (response != null) {
                            Toast.makeText(EliteLoanActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                        ProgressDialogrUtils.hide();
                        LogUtil.d("bqt", "借款首页" + error.toString());
                    }
                });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    /**
     * 验证借款信息
     */
    private void getLoanCertification(final int LoanKind) {
        JSONObject obj = new org.json.JSONObject();
        try {
            obj.put("LoanKind", LoanKind);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressDialogrUtils.show(this, "正在验证借款信息...");
        final BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.LoanCertification, obj, TokenUtil.getEncodeToken(this), new
                BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressDialogrUtils.hide();
                        try {
                            if (null != response) {
                                LogUtil.i("bqt", "借款信息" + response.toString());
                                //不等于1时失败，弹窗
                                if (response.getInt("status") != 1) {
                                    String message = response.getString("message");
                                    showDialog(message);
                                } else {//否则跳转
                                    switch (LoanKind) {
                                        case 0:
                                            LoanRequestBorrowActivity.launche(EliteLoanActivity.this, bean0, signedLoanRequestInfo);
                                            break;
                                        case 1:
                                            LoanRequestBorrowActivity.launche(EliteLoanActivity.this, bean1, signedLoanRequestInfo);
                                            break;
                                        case 2:
                                            LoanRequestBorrowActivity.launche(EliteLoanActivity.this, bean2, signedLoanRequestInfo);
                                            break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.i("bqt", "借款信息出错" + e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                        ProgressDialogrUtils.hide();
                        ToastUtil.alert(EliteLoanActivity.this, "网络异常，请稍后重试");
                    }
                });
        jsonRequest.setTag(BcbRequestTag.BCB_LOAN_CERTIFICATION_REQUEST);
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }

    //****************************************************************************************************************************************
    //                                                                                                   获取签约企业借款数据
    //****************************************************************************************************************************************
    private void getSingLoanCertification() {
        JSONObject obj = new org.json.JSONObject();
        try {
            obj.put("LoanKind", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.LoanCertification, obj, TokenUtil.getEncodeToken(this), new
                BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("bqt", "签约企业借款信息" + response.toString());
                        if (response.optInt("status") == 1) {
                            signedLoanRequestInfo = new Gson().fromJson(response.optString("result"), LoanRequestInfoBean.class);
                        }
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                    }
                });
        MyApplication.getInstance().getRequestQueue().add(jsonRequest);
    }
}