package com.bcb.module.homepager.slideshow;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.BaseFragment;
import com.bcb.event.RefreshHomeBanerEvent;
import com.bcb.module.homepager.slideshow.bean.RespHomeBaner;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.UrlsOne;
import com.bcb.util.LogUtil;
import com.bcb.util.PackageUtil;
import com.bcb.util.TokenUtil;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by ruiqin.shen
 * 类说明：首页的轮播图,抽取为一个Fragmnet，降低首页SlideShowFragment的业务逻辑
 */

public class SlideShowFragment extends BaseFragment {

    @BindView(R.id.convenientBanner)
    ConvenientBanner convenientBanner;


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        requestHomeBaner();
    }

    /**
     * 联网请求首页的图片数据
     */
    private void requestHomeBaner() {
        JSONObject obj = new JSONObject();
        String url = UrlsOne.Home_Activities;
        BcbJsonRequest jsonRequest = new BcbJsonRequest(url, obj, TokenUtil.getEncodeToken(mContext), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.e("TAG", "【首页活动】" + response.toString());
                try {
                    boolean flag = PackageUtil.getRequestStatus(response, mContext);
                    if (flag) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        if (obj != null) {
                            // TODO: 2017/6/16
//                            String s = "[{url:\"http://www.baidu.com\",img:\"http://img05.tooopen.com/images/20160121/tooopen_sy_155168162826.jpg\"},{url:\"http://www.baidu.com\",img:\"http://img.171u.com/image/1309/1309322513428.jpg\"}]";
                            Type type = new TypeToken<ArrayList<RespHomeBaner>>() {
                            }.getType();
                            mRespHomeBaners = MyApplication.mGson.fromJson(obj.toString(), type);
                            initData(mRespHomeBaners);
                        }
                    }
                } catch (Exception e) {
                    LogUtil.d("TAG", "" + e.getMessage());
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });

        MyApplication.getInstance().getRequestQueue().add(jsonRequest);

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void stopLoad() {

    }

    List<RespHomeBaner> mRespHomeBaners;

    /**
     * 初始化数据
     */
    private void initData(List<RespHomeBaner> respHomeBaners) {
        //一条数据都没有的时候停止
        if (respHomeBaners == null || respHomeBaners.size() == 0) {
            return;
        }
        mRespHomeBaners = respHomeBaners;
        setSlideImage(respHomeBaners);
    }

    /**
     * 设置导航栏图片
     */
    private void setSlideImage(List<RespHomeBaner> respHomeBaners) {
        if (convenientBanner == null) {
            return;
        }
        convenientBanner.setPages(new CBViewHolderCreator<LocalImageHolderView>() {
            @Override
            public LocalImageHolderView createHolder() {
                return new LocalImageHolderView();
            }
        }, respHomeBaners)
                .setPageIndicator(new int[]{R.drawable.shape_fixed_point, R.drawable.shape_slide_point})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String title = mRespHomeBaners.get(position).getTitle();
                        String pageUrl = mRespHomeBaners.get(position).getUrl();
                        if (!StringUtils.isEmpty(pageUrl)) {//不为空跳转
//                            startActivity(X5WebViewBrowseActivity.newIntent(mContext, pageUrl, title));
                            ProjectDetailActivity.launche(mContext, "活动详情", pageUrl, true);
                        }
                    }
                });
    }

    public class LocalImageHolderView implements Holder<RespHomeBaner> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, RespHomeBaner respHomeBaners) {
            Glide.with(mContext).load(respHomeBaners.getImg()).into(imageView);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_slide_show;
    }

    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(5000);
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接受hoemPageFragment中传过来的EventBus的数据
     *
     * @param refreshHomeBanerEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshHomeBanerEvent refreshHomeBanerEvent) {
        requestHomeBaner();
    }
}
