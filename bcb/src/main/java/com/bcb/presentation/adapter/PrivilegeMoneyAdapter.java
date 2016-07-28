package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.PrivilegeMoneyDto;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.TokenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ray on 2016/7/8.
 *
 * @desc 特权本金
 */
public class PrivilegeMoneyAdapter extends BaseAdapter {

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

    //设置ViewHolder数据
    private void setDataWithViewHolder(final ViewHolder viewHolder, final int pos) {
        viewHolder.title.setText(datas.get(pos).getAmount() + "元特权金");
        viewHolder.income.setText(String.format("￥%f", datas.get(pos).getIncome()));
        viewHolder.tv_rate.setText("(年化" + datas.get(pos).getRate() + "%*" + datas.get(pos).getDays() + "天)");
        viewHolder.term.setText(datas.get(pos).getExpireDate() + "过期");

        viewHolder.tv_time.setVisibility(View.GONE);
        viewHolder.tv_status.setTextColor(0xfff46548);
        viewHolder.tv_status.setBackgroundResource(R.drawable.main_item_stroke_green);

        viewHolder.tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("GoldNo", datas.get(pos).getGoldNo());
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
                                String valueDate = data.optString("ValueDate");
                                Toast.makeText(ctx, "发放日期" + valueDate, Toast.LENGTH_SHORT).show();
                                viewHolder.tv_status.setText("已使用");
                                viewHolder.tv_status.setClickable(false);
                            }
                        } else
                            Toast.makeText(ctx, response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                    }
                });
                jsonRequest.setTag(BcbRequestTag.UserPrivilegeMoneyActivatedTag);
                App.getInstance().getRequestQueue().add(jsonRequest);
            }
        });

        viewHolder.tv_status.setClickable(false);
        switch (datas.get(pos).getStatus()) {
            case 0:
                viewHolder.tv_status.setText("未使用");
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
                break;
        }
    }
}