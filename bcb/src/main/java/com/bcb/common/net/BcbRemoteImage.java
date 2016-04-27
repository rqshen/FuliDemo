package com.bcb.common.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.bcb.R;

/**
 * Created by baicaibang on 2016/4/26.
 */
public class BcbRemoteImage {

    private Context context;
    private BcbRemoteImage(Context context) {
        this.context = context;
    }

    public static BcbRemoteImage getInstance(Context context) {
        return new BcbRemoteImage(context);
    }

    public void loadRemoteImage(ImageView mImageView, String imageUrl) {
        BcbRequestQueue requestQueue = BcbNetworkManager.newRequestQueue(context);
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

    public void loadRemoteImage(ImageView mImageView, String imageUrl, int default_placeholder, int error_placeholder) {
        BcbRequestQueue requestQueue = BcbNetworkManager.newRequestQueue(context);
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
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mImageView, default_placeholder, error_placeholder);
        imageLoader.get(imageUrl, listener);
    }
}
