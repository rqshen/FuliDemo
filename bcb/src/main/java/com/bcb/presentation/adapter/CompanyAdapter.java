package com.bcb.presentation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.data.bean.CompanyBean;
import com.bcb.data.util.LogUtil;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by cain on 15/12/29.
 */
public class CompanyAdapter extends BaseAdapter {
    private Context ctx;
    private List<CompanyBean> data;
    public CompanyAdapter(Context ctx, List<CompanyBean> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public int getCount() {
        LogUtil.d("this.data.size()", "" + this.data.size());
        return null == this.data ? 0 : this.data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null == this.data ? null : this.data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }


    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (null == view) {
            view = View.inflate(ctx, R.layout.item_company, null);
            viewHolder = new ViewHolder();
            viewHolder.companyIcon = (ImageView) view.findViewById(R.id.company_icon);
            viewHolder.companyShortName = (TextView) view.findViewById(R.id.company_shortname);
            viewHolder.companyFullName = (TextView) view.findViewById(R.id.company_fullname);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        //设置公司图标
        Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.icon_company_default);
        if (!TextUtils.isEmpty(data.get(pos).getLogo())) {
            Glide.with(ctx).load(data.get(pos).getLogo()).into(viewHolder.companyIcon);
        } else {
            viewHolder.companyIcon.setImageBitmap(bitmap);
        }

        //设置Name
        if (!TextUtils.isEmpty(data.get(pos).getName())) {
            viewHolder.companyFullName.setText(data.get(pos).getName());
        } else {
            viewHolder.companyFullName.setText("");
        }

        //设置ShortName
        if (!TextUtils.isEmpty(data.get(pos).getShortName())) {
            viewHolder.companyShortName.setText(data.get(pos).getShortName());
        } else {
            viewHolder.companyShortName.setText("");
        }
        return view;
    }

    class ViewHolder {
        ImageView companyIcon;
        TextView companyShortName;
        TextView companyFullName;
    }


}
