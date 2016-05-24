package com.bcb.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bcb.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2016/4/23.
 * 仅用借款界面附件上传
 */
public class GridAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> pics;
    private Context context;

    public GridAdapter(Context context, List<String> pics) {
        inflater = LayoutInflater.from(context);
        if (null == pics){
            pics = new ArrayList<>();
        }
        this.pics = pics;
        this.context = context;
    }

    public int getCount() {
        return pics.size() + 1;
    }

    public Object getItem(int position) {
        return pics.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_grid, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.image = (ImageView) convertView.findViewById(R.id.item_grid_image);
        if ((getCount()-1) == position){
//            holder.image_del = (ImageView) convertView.findViewById(R.id.item_grida_del);
//            holder.image_del.setVisibility(View.GONE);
            holder.image.setImageResource(R.drawable.ico_img_add);
        }else{
            Glide.with(context).load(pics.get(position)).centerCrop().into(holder.image);
        }
        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
//        public ImageView image_del;
    }
}
