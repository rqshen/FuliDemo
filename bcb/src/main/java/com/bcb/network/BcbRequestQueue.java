package com.bcb.network;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;

/**
 * Created by cain on 16/4/19.
 */
public class BcbRequestQueue extends RequestQueue{
    public BcbRequestQueue(Cache cache, Network network, int threadPoolSize, ResponseDelivery delivery) {
        super(cache, network, threadPoolSize, delivery);
    }

    public BcbRequestQueue(Cache cache, Network network, int threadPoolSize) {
        super(cache, network, threadPoolSize);
    }

    public BcbRequestQueue(Cache cache, Network network) {
        super(cache, network);
    }
}
