package com.bcb.presentation.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsOne;
import com.bcb.data.bean.AnnounceRecordsBean;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by cain on 16/2/17.
 */
public class AnnounceAdapter extends BaseAdapter {

    private Context context;
    private List<AnnounceRecordsBean> data;
    private int tokenTicker = 0;
    private ProgressDialog progressDialog;
    private String AccessToken;

    private BcbRequestQueue requestQueue;

    //构造函数
    public AnnounceAdapter(Context context, List<AnnounceRecordsBean> data) {
        if (data != null) {
            this.context = context;
            this.data = data;
        }
        requestQueue = App.getInstance().getRequestQueue();
    }

    @Override
    public int getCount() {
        return null == this.data ? 0 : this.data.size();
    }

    @Override
    public Object getItem(int i) {
        return null == this.data ? null : this.data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = View.inflate(context, R.layout.item_announce, null);
            viewHolder = new ViewHolder();
            setupViewholder(viewHolder, view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        //设置ViewHolder数据
        setDataWithViewHolder(viewHolder, i, false);
        return view;
    }

    //初始化ViewHolder
    private void setupViewholder(ViewHolder viewHolder, View view) {
        viewHolder.announce_name = (TextView) view.findViewById(R.id.announce_name);
        viewHolder.announce_rate = (TextView) view.findViewById(R.id.announce_rate);
        viewHolder.announce_text = (TextView) view.findViewById(R.id.announce_text);
        viewHolder.announce_predict = (TextView) view.findViewById(R.id.announce_predict);
    }

    //设置ViewHolder数据
    private void setDataWithViewHolder(ViewHolder viewHolder, final int pos, boolean viewStatus) {
        //请求数据，判断是否预约了
        if (TokenUtil.getEncodeToken(context) != null && AccessToken != TokenUtil.getEncodeToken(context)) { //Token不一致时，包括一开始创建的时候AccessToken不一致
            tokenTicker++;
            if (tokenTicker >= data.size()) {
                AccessToken = TokenUtil.getEncodeToken(context);
                //如果APP在运行过程中token过期或者更换了账号，则表明Token已经过期了，需要重新遍历查询新标预告的状态
                //这里的Ticker 类似时钟的作用，如果tokenTicker大于新标预告的值，则表明这时候是退出重新登录、token过期或者更换了账号，此时需要重新遍历插叙新标预告的状态
                if (tokenTicker > data.size()) {
                    //判断标的数据是否大于1个，如果小于等于1，则表示只有一个数据，置0，结束当前查询，否则置1，进入下一轮查询。
                    if (data.size() <= 1) {
                        tokenTicker = 0;
                    } else {
                        tokenTicker = 1;
                    }

                }
            }
            //查询新标预告的标的状态
            getAnnounceStatus(pos);
        } else if (TokenUtil.getEncodeToken(context) == null) {
            AccessToken = null;
        }

        //项目名称
        viewHolder.announce_name.setText(data.get(pos).getName());
        //年化收益
        viewHolder.announce_rate.setText(data.get(pos).getRate() + "");

        //设置预约成功人数
        viewHolder.announce_predict.setText("已有" + data.get(pos).getPredictCount() + "人预约");
        viewHolder.announce_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TokenUtil.getEncodeToken(context) != null) {
                    //统计预约事件
                    UmengUtil.eventById(context, R.string.book);
                    requestAnnounce(pos);
                } else {
                    ToastUtil.alert(context, "请登录后再操作");
                }
            }
        });
        viewHolder.announce_text.setBackgroundResource(R.drawable.announce_button_background);
        viewHolder.announce_text.setText("立即预约");
        viewHolder.announce_text.setTextColor(Color.argb(221, 72, 127, 248));
        //判断是否预约
        if (TokenUtil.getEncodeToken(context)!= null && App.saveUserInfo.isPreviewInvest(data.get(pos).PackageId)) {
            viewHolder.announce_text.setBackgroundResource(R.drawable.announce_button_gray);
            viewHolder.announce_text.setOnClickListener(null);
            viewHolder.announce_text.setText("已预约");
            viewHolder.announce_text.setTextColor(Color.rgb(153, 153, 153));
        }
    }

    //查询数据
    private void getAnnounceStatus(final int pos) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("UniqueId", data.get(pos).getPackageId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.AnnounceStatus, obj, TokenUtil.getEncodeToken(context), pos, new BcbRequest.BcbIndexCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response, int index) {
                try {
                    if (response.getInt("status") == 1) {
                        //设置对应位置的数据
                        if (response.getJSONObject("result").getInt("PackageStatus") == 1) {
                            App.saveUserInfo.setPreviewInvest(data.get(pos).PackageId);
                            //更新UI
                            notifyDataSetChanged();
                        } else {
                            //更新UI
                            notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.AnnounceStatusTag);
        requestQueue.add(jsonRequest);
    }


    //点击请求预约
    private void requestAnnounce(final int pos) {
        JSONObject obj = new JSONObject();
        try {
            showProgressBar();
            obj.put("UniqueId", data.get(pos).getPackageId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.RequestAnnounce, obj, TokenUtil.getEncodeToken(context), pos, new BcbRequest.BcbIndexCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response, int index) {
                try {
                    if (response.getInt("status") == 1) {
                        //设置对应位置的数据
                        if (response.getJSONObject("result").getInt("PredictCount") > 0) {
                            data.get(index).setPredictCount(response.getJSONObject("result").getInt("PredictCount"));
                            App.saveUserInfo.setPreviewInvest(data.get(pos).PackageId);
                            //更新数据
                            notifyDataSetChanged();
                        }
                    } else {
                        ToastUtil.alert(context, response.getString("message").isEmpty() ? "预约失败" : response.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hideProgressBar();
            }

            @Override
            public void onErrorResponse(Exception error) {
                hideProgressBar();
            }
        });
        jsonRequest.setTag(BcbRequestTag.RequestAnnounceTag);
        requestQueue.add(jsonRequest);
    }


    /********************* 转圈提示 **************************/
    //显示转圈提示
    private void showProgressBar() {
        if(null == progressDialog) progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage("正在加载数据....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    //隐藏转圈提示
    private void hideProgressBar() {
        if(null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    class ViewHolder {
        TextView    announce_name;      //名称
        TextView    announce_rate;      //年化利率
        TextView    announce_text;  //立即预约、已预约字样
        TextView    announce_predict;   //预约人数
    }
}
