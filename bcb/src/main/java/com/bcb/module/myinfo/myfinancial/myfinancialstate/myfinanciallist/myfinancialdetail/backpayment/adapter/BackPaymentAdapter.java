package com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.backpayment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.ZYBBackPaymentBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class BackPaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<ZYBBackPaymentBean.RepaymentPlanBean> mZYBBackPaymentBean;
    private Context mContext;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            mContext = parent.getContext();
            ZYBBackPaymentNoViewHolder zybBackPaymentNoViewHolder = new ZYBBackPaymentNoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_zyb_back_payment_no, parent, false));
            return zybBackPaymentNoViewHolder;
        } else if (viewType == 1) {
            mContext = parent.getContext();
            ZYBBackPaymentViewHolder zybBackPaymentViewHolder = new ZYBBackPaymentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_zyb_back_payment, parent, false));
            return zybBackPaymentViewHolder;
        } else if (viewType == 10) {
            mContext = parent.getContext();
            ZYBBackPaymentBottomViewHolder zybBackPaymentBottomViewHolder = new ZYBBackPaymentBottomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_zyb_back_payment_bottom, parent, false));
            return zybBackPaymentBottomViewHolder;
        }
        return null;
    }

    public BackPaymentAdapter(List<ZYBBackPaymentBean.RepaymentPlanBean> ZYBBackPaymentBean) {
        mZYBBackPaymentBean = ZYBBackPaymentBean;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ZYBBackPaymentViewHolder) {//有数据的时候
            ZYBBackPaymentViewHolder zYBBackPaymentViewHolder = (ZYBBackPaymentViewHolder) holder;
            zYBBackPaymentViewHolder.mCreateDate.setText(mZYBBackPaymentBean.get(position).getCraeteDate());//回款日期
            zYBBackPaymentViewHolder.mPrincipal.setText(mZYBBackPaymentBean.get(position).getPrincipal() + "");//本金
            zYBBackPaymentViewHolder.mInterest.setText(mZYBBackPaymentBean.get(position).getInterest() + "");//利息
            zYBBackPaymentViewHolder.mDescn.setText(mZYBBackPaymentBean.get(position).getDescn());//操作说明
            /**
             * 判断是是否显示文字说明，只有第一条的时候才显示
             */
            if (position == 0) {
                zYBBackPaymentViewHolder.mLlDescription.setVisibility(View.VISIBLE);
            } else {
                zYBBackPaymentViewHolder.mLlDescription.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mZYBBackPaymentBean.size() == 0 ? 1 : mZYBBackPaymentBean.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mZYBBackPaymentBean.size() == 0) {//0表示无记录
            return 0;
        } else if (position + 1 == getItemCount()) {
            return 10;
        } else {
            return 1;
        }
    }

    /**
     * 周盈宝 无回款记录
     */
    public static class ZYBBackPaymentNoViewHolder extends RecyclerView.ViewHolder {

        public ZYBBackPaymentNoViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 周盈宝有记录 底部文字说明
     */
    public static class ZYBBackPaymentBottomViewHolder extends RecyclerView.ViewHolder {

        public ZYBBackPaymentBottomViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 周盈宝回款记录
     */
    public static class ZYBBackPaymentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_description)
        LinearLayout mLlDescription;
        @BindView(R.id.CreateDate)
        TextView mCreateDate;
        @BindView(R.id.Principal)
        TextView mPrincipal;
        @BindView(R.id.Interest)
        TextView mInterest;
        @BindView(R.id.Descn)
        TextView mDescn;

        public ZYBBackPaymentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
