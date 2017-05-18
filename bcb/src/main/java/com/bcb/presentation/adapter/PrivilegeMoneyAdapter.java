package com.bcb.presentation.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.PrivilegeMoneyDto;
import com.bcb.utils.LogUtil;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.module.myinfo.balance.FundCustodianAboutActivity;
import com.bcb.presentation.view.custom.AlertView.AlertView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ray on 2016/7/8.
 *
 * @desc 特权本金
 */
public class PrivilegeMoneyAdapter extends BaseAdapter {
    public interface IloadAfterRegeist {
        void loadAfterRegeist();
    }

    IloadAfterRegeist iloadAfterRegeist;

    public void setIloadAfterRegeist(IloadAfterRegeist iloadAfterRegeist) {
        this.iloadAfterRegeist = iloadAfterRegeist;
    }

    private Context ctx;
    private List<PrivilegeMoneyDto> datas;

    public PrivilegeMoneyAdapter(Context ctx, List<PrivilegeMoneyDto> datas) {
        this.ctx = ctx;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return null == datas ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == view) {
            view = View.inflate(ctx, R.layout.item_privilege_money, null);
            viewHolder = new ViewHolder();
            setupViewholder(viewHolder, view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //设置ViewHolder的数据
        setDataWithViewHolder(viewHolder, position);
        return view;
    }

    private class ViewHolder {
        public TextView title, income, term, tv_status, tv_rate, tv_time;
    }

    //初始化ViewHolder
    private void setupViewholder(ViewHolder viewHolder, View view) {
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        viewHolder.income = (TextView) view.findViewById(R.id.income);
        viewHolder.term = (TextView) view.findViewById(R.id.term);
        viewHolder.tv_status = (TextView) view.findViewById(R.id.tv_status);
        viewHolder.tv_rate = (TextView) view.findViewById(R.id.tv_rate);
        viewHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);
    }

    AlertView alertView;

    //设置ViewHolder数据
    private void setDataWithViewHolder(final ViewHolder viewHolder, final int pos) {
        viewHolder.title.setText(String.format("%.2f", datas.get(pos).Amount) + "元特权金");
        viewHolder.income.setText(String.format("￥%.2f", datas.get(pos).Income));
        viewHolder.tv_rate.setText("(年化" + String.format("￥%.2f", datas.get(pos).Rate) + "%*" + datas.get(pos).Days + "天)");
        viewHolder.term.setText(datas.get(pos).ExpireDate);//+ "过期"

        viewHolder.tv_time.setVisibility(View.GONE);
        viewHolder.tv_status.setTextColor(0xfff46548);
        viewHolder.tv_status.setBackgroundResource(R.drawable.main_item_stroke_green);

        viewHolder.tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("GoldNo", datas.get(pos).GoldNo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserPrivilegeMoneyActivated, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("bqt", "激活特权金返回数据：" + response.toString());
                        if (PackageUtil.getRequestStatus(response, ctx)) {
                            JSONObject data = PackageUtil.getResultObject(response);
                            //判断JSON对象是否为空
                            if (data != null) {
                                viewHolder.tv_status.setText("收益中");
                                viewHolder.tv_time.setVisibility(View.VISIBLE);
                                viewHolder.tv_status.setClickable(false);
                                //发放日期
                                String valueDate = data.optString("ValueDate");
                                AlertView.Builder ibuilder = new AlertView.Builder(ctx);
                                ibuilder.setTitle("激活成功");
                                ibuilder.setMessage("特权本金将在" + valueDate + "日发送");
                                //已开通托管
                                if (MyApplication.mUserDetailInfo != null && MyApplication.mUserDetailInfo.HasOpenCustody) {
                                    ibuilder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            alertView.dismiss();
                                            alertView = null;
                                        }
                                    });
                                } //未开通托管
                                else {
                                    ibuilder.setPositiveButton("开通托管账户领取收益", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            alertView.dismiss();
                                            alertView = null;
                                            ctx.startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
                                        }
                                    });
                                }
                                alertView = ibuilder.create();
                                alertView.show();
                                if (iloadAfterRegeist != null) {
                                    iloadAfterRegeist.loadAfterRegeist();
                                }
                            }
                        } else
                            Toast.makeText(ctx, response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                    }
                });
                jsonRequest.setTag(BcbRequestTag.UserPrivilegeMoneyActivatedTag);
                MyApplication.getInstance().getRequestQueue().add(jsonRequest);
            }
        });

        viewHolder.tv_status.setClickable(false);
        switch (datas.get(pos).Status) {
            case 0:
                viewHolder.tv_status.setText("立即激活");
                viewHolder.tv_status.setClickable(true);
                break;
            case 1:
                viewHolder.tv_status.setText("收益中");
                viewHolder.tv_time.setVisibility(View.VISIBLE);
                break;
            case 2:
                viewHolder.tv_status.setText("已使用");
                viewHolder.tv_status.setTextColor(0xffaaaaaa);
                viewHolder.tv_status.setBackgroundResource(R.drawable.main_item_stroke_gray);
                break;
            case 3:
                viewHolder.tv_status.setText("已过期");
                viewHolder.tv_status.setTextColor(0xffaaaaaa);
                viewHolder.tv_status.setBackgroundResource(R.drawable.main_item_stroke_gray);
                break;
        }
    }
}