package com.bcb.presentation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bcb.common.app.App;
import com.bcb.R;
import com.bcb.data.bean.BankItem;
import com.bcb.data.util.LogUtil;

import java.util.List;

public class BankListAdapter extends BaseAdapter {
	
	private Context ctx;
	private List<BankItem> data;

	public BankListAdapter(Context ctx, List<BankItem> data) {
		if (data != null) {
			this.ctx = ctx;
			this.data = data;
		}
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
		Holder mHolder;
		if (null == view) {
			view = View.inflate(ctx, R.layout.item_bank, null);
			mHolder = new Holder();
			mHolder.bank_icon = (ImageView) view.findViewById(R.id.bank_icon);
			mHolder.bank_name = (TextView) view.findViewById(R.id.bank_name);
			mHolder.bank_rule = (TextView) view.findViewById(R.id.bank_rule);
			view.setTag(mHolder);
		} else {
			mHolder = (Holder) view.getTag();
		}

		if (data.get(pos).getLogo() != null) {
//			App.mFinalBitmap.display(mHolder.bank_icon, data.get(pos).getLogo());
            loadImageByVolley(mHolder.bank_icon, data.get(pos).getLogo());
		}

		if (data.get(pos).getBankName() != null) {
			mHolder.bank_name.setText(data.get(pos).getBankName());
		}
        //如果单笔限额和每日限额都大于0.0万时才显示
		mHolder.bank_rule.setText("单笔限额"+ data.get(pos).getMaxSingle() + "万，每日限额" + data.get(pos).getMaxDay() + "万");

		return view;
	}

	class Holder {		
		ImageView bank_icon;
		TextView bank_name;
		TextView bank_rule;
	}

    /**
     * 利用Volley异步加载图片
     *
     * 注意方法参数:
     * getImageListener(ImageView view, int defaultImageResId, int errorImageResId)
     * 第一个参数:显示图片的ImageView
     * 第二个参数:默认显示的图片资源
     * 第三个参数:加载错误时显示的图片资源
     */
    private void loadImageByVolley(ImageView mImageView, String imageUrl) {
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };
        ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
        //默认图片是没有的
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mImageView, R.drawable.edittext_none_background, R.drawable.edittext_none_background);
        imageLoader.get(imageUrl, listener);
    }
}