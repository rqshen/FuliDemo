package com.bcb.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.event.BroadcastEvent;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.UserWallet;
import com.bcb.data.util.DensityUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MQCustomerManager;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;

import org.json.JSONObject;

import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * 错误提示界面
 */
public class Activity_Tips_FaileOrSuccess extends Activity_Base implements View.OnClickListener {
    public static final String TYPE = "type";
    private int type;
    public static final String MESSAGE = "message";
    private String message;//提示消息，目前是写死的

    public static final int OPEN_HF_SUCCESS = 1;//开户成功
    public static final int OPEN_HF_FAILED = 2;//开户失败
    public static final int BAND_HF_SUCCESS = 3;//绑卡成功
    public static final int BAND_HF_FAILED = 4;//绑卡失败
    public static final int CHARGE__HF_SUCCESS = 5;//充值成功
    public static final int CHARGE_HF_FAILED = 6;//充值失败
    public static final int TX_HF_SUCCESS = 7;//提现成功
    public static final int TX_HF_FAILED = 8;//提现失败
    public static final int BUY_HF_SUCCESS = 9;//申购成功
    public static final int BUY_HF_FAILED = 10;//申购失败
    public static final int SLB_SUCCESS = 11;//生利宝成功
    public static final int SLB_FAILED = 12;//生利宝失败
    ImageView iv_pic;
    TextView title_text, tv_up, tv_down, tv_next;

    public static void launche(Context ctx, int type, String message) {
        Intent intent = new Intent(ctx, Activity_Tips_FaileOrSuccess.class);
        intent.putExtra(TYPE, type);
        intent.putExtra(MESSAGE, message);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_tips);
        ctx = this;
        init();
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra(TYPE, 0);
            message = intent.getStringExtra(MESSAGE);
            initView();
        }
    }

    private void initView() {
        //message = message == null ? "银行卡余额不足" : message;
        switch (type) {
            case OPEN_HF_SUCCESS:
                title_text.setText("开户成功");
                iv_pic.setImageResource(R.drawable.success_open_hf);
                iv_pic.setPadding(0, DensityUtils.dp2px(this, 50), 0, 0);
                tv_up.setText("开户成功！");
                tv_down.setVisibility(View.GONE);
                tv_next.setVisibility(View.GONE);
                break;
            case OPEN_HF_FAILED:
                title_text.setText("开户失败");
                iv_pic.setImageResource(R.drawable.failed_open_fh);
                tv_up.setText("开户失败！");
//                tv_down.setText("身份信息识别失败");
                tv_down.setText(message);
                tv_next.setText("联系客服");
                break;
            case BAND_HF_SUCCESS:
                title_text.setText("绑卡成功");
                iv_pic.setImageResource(R.drawable.success_open_hf);
                tv_up.setText("绑卡成功！");
                tv_down.setVisibility(View.GONE);
                tv_next.setText("返回个人中心");
                break;
            case BAND_HF_FAILED:
                title_text.setText("绑卡失败");
                iv_pic.setImageResource(R.drawable.failed_band_fh);
                tv_up.setText("绑卡失败！");
                tv_down.setText("信息验证出现错误");
                tv_next.setText("联系客服");
                break;
            case CHARGE__HF_SUCCESS:
                title_text.setText("充值成功");
                iv_pic.setImageResource(R.drawable.success_open_hf);
                iv_pic.setPadding(0, DensityUtils.dp2px(this, 30), 0, 0);
                tv_up.setText("充值成功！");
                tv_next.setVisibility(View.GONE);
                SystemClock.sleep(3000);//让子弹飞一会
                requestUserWallet();
                break;
            case CHARGE_HF_FAILED:
                title_text.setText("充值失败");
                iv_pic.setImageResource(R.drawable.failed_charge_hf);
                tv_up.setText("充值失败！");
                message = message == null ? "银行卡余额不足" : message;
                tv_down.setText(message);
                tv_next.setText("联系客服");
                break;
            case TX_HF_SUCCESS:
                title_text.setText("提现成功");
                iv_pic.setImageResource(R.drawable.success_open_hf);
                iv_pic.setPadding(0, DensityUtils.dp2px(this, 30), 0, 0);
                tv_up.setText("提现成功！");
                tv_next.setVisibility(View.GONE);
                SystemClock.sleep(3000);//让子弹飞一会
                requestUserWallet();
                break;
            case TX_HF_FAILED:
                title_text.setText("提现失败");
                iv_pic.setImageResource(R.drawable.failed_band_fh);
                iv_pic.setPadding(0, DensityUtils.dp2px(this, 50), 0, 0);
                tv_up.setText("提现失败！");
                tv_down.setText("账户余额不足");
                tv_next.setVisibility(View.GONE);
                break;
            case BUY_HF_SUCCESS:
                title_text.setText("申购成功");
                iv_pic.setImageResource(R.drawable.success_open_hf);
                tv_up.setText("申购成功！");
                tv_down.setVisibility(View.GONE);
                tv_next.setText("返回个人中心");
                break;
            case BUY_HF_FAILED:
                title_text.setText("申购失败");
                iv_pic.setImageResource(R.drawable.failed_buy_fh);
                tv_up.setText("申购失败！");
                tv_down.setText(message);
                tv_next.setText("联系客服");
                break;
            case SLB_SUCCESS:
                String[] splitStrs = Pattern.compile("\\|").split(message);//Java Split以竖线作为分隔符
                title_text.setText("生利宝");
                iv_pic.setImageResource(R.drawable.success_open_hf);
                if (splitStrs[2].equalsIgnoreCase("I")) tv_up.setText("成功转入");
                else if (splitStrs[2].equalsIgnoreCase("O")) tv_up.setText("成功转出");
                else tv_up.setText("成功");
                tv_up.setTextColor(0xff22bb66);
                tv_down.setText("¥ " + splitStrs[3]);
                tv_down.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                tv_next.setText("返回生利宝");
                break;
            case SLB_FAILED:
                String[] splitStrs2 = Pattern.compile("\\|").split(message);//Java Split以竖线作为分隔符
                title_text.setText("生利宝");
                iv_pic.setImageResource(R.drawable.failed_charge_hf);
                if (splitStrs2[2].equalsIgnoreCase("I")) tv_up.setText("转入失败");
                else if (splitStrs2[2].equalsIgnoreCase("O")) tv_up.setText("转出失败");
                else tv_up.setText("失败");
                tv_down.setText("第三方支付平台出错");
                tv_next.setText("返回生利宝");
                break;
            default:
                break;
        }
    }

    private void init() {
        title_text = (TextView) findViewById(R.id.title_text);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        tv_up = (TextView) findViewById(R.id.tv_up);
        tv_down = (TextView) findViewById(R.id.tv_down);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);
        //返回
        View back_img = findViewById(R.id.back_img);
        back_img.setVisibility(View.VISIBLE);
        back_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                JumpToUser();
                break;
            case R.id.tv_next:
                switch (type) {
                    //个人中心
                    case BAND_HF_SUCCESS:
                    case BUY_HF_SUCCESS:
                        JumpToUser();
                        break;
                    //生利宝
                    case SLB_SUCCESS:
                    case SLB_FAILED:
                        finish();
                        break;
                    //客服
                    default:
                        String userId = null;
                        if (App.mUserDetailInfo != null) userId = App.mUserDetailInfo.getCustomerId();
                        MQCustomerManager.getInstance(this).showCustomer(userId);
                        finish();
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void JumpToUser() {
//        Intent intent = new Intent(Activity_Tips_FaileOrSuccess.this, Activity_Main.class);
//        intent.putExtra("jumpTo", 3);
//        startActivity(intent);
        EventBus.getDefault().post(new BroadcastEvent(BroadcastEvent.USER));
        startActivity(new Intent(this, Activity_Main.class));
        finish();
    }

    UserWallet mUserWallet;
    Context ctx;

    private void requestUserWallet() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.UserWalletMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "刷新－－账户余额：" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    if (data != null) {
                        //注意数据结构变了，2016-7-26
                        mUserWallet = App.mGson.fromJson(data.toString(), UserWallet.class);
                        App.mUserWallet = mUserWallet;
                        tv_down.setText("当前账户余额：" + String.format("%.2f", App.mUserWallet.getBalanceAmount()));
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                ToastUtil.alert(ctx, "网络异常，请稍后重试");
            }
        });
        jsonRequest.setTag(BcbRequestTag.UserWalletMessageTag);
        App.getInstance().getRequestQueue().add(jsonRequest);
    }
}
