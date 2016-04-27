package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbNetworkManager;
import com.bcb.common.net.BcbRemoteImage;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.presentation.adapter.AnnounceAdapter;
import com.bcb.presentation.adapter.ExpiredAdapter;
import com.bcb.presentation.adapter.ProductAdapter;
import com.bcb.common.app.App;
import com.bcb.R;
import com.bcb.data.bean.AdPhotoListBean;
import com.bcb.data.bean.AnnounceRecordsBean;
import com.bcb.data.bean.BannerInfo;
import com.bcb.data.bean.ExpiredRecordsBean;
import com.bcb.data.bean.MainListBean;
import com.bcb.data.bean.ProductRecordsBean;
import com.bcb.common.net.UrlsOne;
import com.bcb.presentation.view.activity.Activity_ExpiredProject_Introduction;
import com.bcb.presentation.view.activity.Activity_Login;
import com.bcb.presentation.view.activity.Activity_NormalProject_Introduction;
import com.bcb.presentation.view.activity.Activity_Login_Introduction;
import com.bcb.presentation.view.activity.Activity_Main;
import com.bcb.presentation.view.activity.Activity_WebView;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.data.util.UmengUtil;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.RegisterSuccessDialogView;
import com.bcb.presentation.view.custom.ImageCycleView.ImageCycleView;
import com.bcb.presentation.view.custom.ImageCycleView.ImageCycleView.ImageCycleViewListener;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.presentation.view.custom.PullableView.PullableScrollView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Frag_Main extends Frag_Base {
	private static final String TAG = "Frag_Main";

    //标题和刷新控件
	private TextView title_text, right_text;
    private PullToRefreshLayout refreshLayout;
    private PullableScrollView layout_scrollview;

    //体验标
    private MyListView expiredListview;
    private ExpiredAdapter expiredAdapter;
    private List<ExpiredRecordsBean> expiredRecordsBeans;

    //新标预告
    private MyListView announceListView;
    private AnnounceAdapter announceAdapter;
    private List<AnnounceRecordsBean> announceRecordsBeans;

    //新手标
    private MyListView newListView;
    private ProductAdapter newAdapter;
    private List<ProductRecordsBean> newRecordsBeans;

    //精品项目
    private MyListView boutiqueListview;
    private ProductAdapter mBoutiqueAdapter;
    private List<ProductRecordsBean> boutqueRecordsBeans;
    //Banner
	private AdPhotoListBean mAdPhotoListBean;
    private ArrayList<BannerInfo> listBanner;
    private ImageCycleView mImageCycleView;

    //标识
	private boolean canReFresh = true;
	private boolean firstLoadCopyWriter = true;
    private boolean firstLoadBanner = true;

	private TextView JXPackageAdWord;
    private int successConnectCount = 0;

	private Context ctx;
	
	private String titleName;

    //浮标按钮
    private int screenWidth;
    private int screenHeight;
    private int bottomHeight;
    private float startX;
    private float startY;
    private int lastX;
    private int lastY;
    private Button button_floating;

    //一分钟了解福利金融
    private Button button_introduction;

    //广播
    private Receiver mReceiver;

    private DialogWidget dialogWidget;

    private BcbRequestQueue requestQueue;

    public Frag_Main(){
        super();
    }

    @SuppressLint("ValidFragment")
    public Frag_Main(Context context) {
        super();
    }

    //每次显示的时候都刷新一次
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && announceAdapter != null) {
            announceAdapter.notifyDataSetChanged();
            if (App.saveUserInfo.getAccess_Token() == null && button_floating != null) {
                button_floating.setVisibility(View.VISIBLE);
            }
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_main, container, false);
	}


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.ctx = view.getContext();
        requestQueue = BcbNetworkManager.newRequestQueue(ctx);
        bottomHeight = ((Activity_Main)getActivity()).getBottomHeight();
        //仅保留下拉刷新，隐藏上拉加载更多
        //隐藏加载更多
        ((RelativeLayout)view.findViewById(R.id.loadmore_view)).setVisibility(View.GONE);
        refreshLayout = ((PullToRefreshLayout) view.findViewById(R.id.refresh_view));
        //不显示刷新结果
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(ctx)) {
                    loadMainListViewData();
                } else {
                    ToastUtil.alert(ctx, "网络异常，请稍后再试");
                    refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                }
            }
            //上拉加载更多
            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
        //自动刷新
        refreshLayout.autoRefresh();

        //判断设备是否Android4.4以上，如果是，则表示使用了浸入式状态栏，需要设置状态栏的位置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((LinearLayout) view.findViewById(R.id.layout_topbar)).setVisibility(View.VISIBLE);
        }

        //设置标题
        titleName = "福利金融";
        title_text = (TextView) view.findViewById(R.id.title_text);
        title_text.setText(titleName);
        //右标题
        right_text = (TextView) view.findViewById(R.id.right_text);
        right_text.setText("登录");
        right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Login.launche(ctx);
            }
        });
        //如果已经登陆则显示否则隐藏
        if (App.saveUserInfo.getAccess_Token() != null) {
            right_text.setVisibility(View.GONE);
        } else {
            right_text.setVisibility(View.VISIBLE);
        }
        //banner
        mImageCycleView = (ImageCycleView) view.findViewById(R.id.ad_view);
        mAdPhotoListBean = new AdPhotoListBean();
        mAdPhotoListBean.result = new ArrayList<BannerInfo>();
        //根据宽度设置高度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        int mScreenHeight = mScreenWidth * 232 / 640;
        ViewGroup.LayoutParams lp = mImageCycleView.getLayoutParams();
        lp.width = mScreenWidth;
        lp.height = mScreenHeight;
        mImageCycleView.requestLayout();

        //设置banner的placeholder图片
        mImageCycleView.setBackgroundResource(R.drawable.banner_placeholder);

        //体验标
        expiredRecordsBeans = new ArrayList<>();
        expiredAdapter = new ExpiredAdapter(ctx, expiredRecordsBeans);
        expiredListview = (MyListView) view.findViewById(R.id.expired_listview);
        expiredListview.setOnItemClickListener(new expiredItemClickListener());
        expiredListview.setAdapter(expiredAdapter);

        //新手标(数据格式跟精品项目的一样)
        newRecordsBeans = new ArrayList<>();
        newAdapter = new ProductAdapter(ctx, newRecordsBeans);
        newListView = (MyListView) view.findViewById(R.id.new_listview);
        newListView.setOnItemClickListener(new newItemClickListener());
        newListView.setAdapter(newAdapter);

        //新标预告
        announceRecordsBeans = new ArrayList<>();
        announceAdapter = new AnnounceAdapter(ctx, announceRecordsBeans);
        announceListView = (MyListView) view.findViewById(R.id.announce_listView);
        announceListView.setAdapter(announceAdapter);

        //精品项目
        boutqueRecordsBeans = new ArrayList<>();
        mBoutiqueAdapter = new ProductAdapter(ctx, boutqueRecordsBeans);
        JXPackageAdWord = (TextView) view.findViewById(R.id.JXPackageAdWord);
        boutiqueListview = (MyListView) view.findViewById(R.id.boutique_listview);
        boutiqueListview.setOnItemClickListener(new boutiqueItemClickListener());
        boutiqueListview.setAdapter(mBoutiqueAdapter);

        //文案配置
        loadCopyWriter();
        //banner
        loadBanner();
        //更新
        UmengUtil.update(this.ctx);

        //注册广播
        mReceiver = new Receiver();
        ctx.registerReceiver(mReceiver, new IntentFilter("com.bcb.project.buy.success"));
        ctx.registerReceiver(mReceiver, new IntentFilter("com.bcb.login.success"));
        ctx.registerReceiver(mReceiver, new IntentFilter("com.bcb.register.success"));

        //监听是否滑动到底部
        layout_scrollview = (PullableScrollView) view.findViewById(R.id.layout_scrollview);
        layout_scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                        int scrollY = v.getScrollY();
                        int height = v.getHeight();
                        int scrollViewMeasuredHeight = layout_scrollview.getChildAt(0).getMeasuredHeight();
                        //判断滑动到底部
                        if ((scrollY + height) == scrollViewMeasuredHeight) {
                            UmengUtil.eventById(ctx, R.string.home_s_bottom);
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        //浮标按钮
        screenWidth = dm.widthPixels;
        //屏幕高度要去掉虚拟按钮的高度
        screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        button_floating = (Button)view.findViewById(R.id.button_floating);
        if (App.saveUserInfo.getAccess_Token() != null) {
            button_floating.setVisibility(View.GONE);
        }

        button_floating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    //按下时获取位置
                    case MotionEvent.ACTION_DOWN:
                        //需要判断是否属于该控件
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        startX = lastX;
                        startY = lastY;
                        break;

                    //移动手势
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (event.getRawY() > screenHeight - bottomHeight - v.getHeight()) {
                            bottom = screenHeight - bottomHeight - v.getHeight();
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    //离开屏幕
                    case MotionEvent.ACTION_UP:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        //当移动距离比较小时，视为点击事件
                        //不用setOnClickListener 是因为setOnClickListener跟setOnTouchListener有冲突，并且只监听到ACTION_DOWN
                        if (Math.abs(lastX - startX) < 5 || Math.abs(lastY - startY) < 5) {
                            Intent intent = new Intent(ctx, Activity_Login_Introduction.class);
                            startActivity(intent);
                        }
                        if (event.getRawY() > screenHeight - bottomHeight - v.getHeight()) {
                            v.setBottom(screenHeight - bottomHeight - v.getHeight());
                            v.setTop(v.getBottom() - v.getHeight());
                            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        }
                        movingAnimation(v, (int) event.getRawX());
                        break;
                }
                return true;
            }
        });

        // 一分钟了解福利金融
        button_introduction = (Button) view.findViewById(R.id.button_introduction);
        button_introduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUtil.eventById(ctx, R.string.self_about);
                Activity_WebView.launche(ctx, "一分钟了解福利金融", "http://wap.flh001.com/static/1minute/index.html");
            }
        });

    }

    //按钮移动动画
    private void movingAnimation(final View v, int currentX) {
        final int left;
        final int top = v.getTop();
        TranslateAnimation animation = null;
        if (currentX > screenWidth/2) {
            animation = new TranslateAnimation(0, screenWidth - v.getRight(), 0, 0);
            left = screenWidth - v.getWidth();
        } else {
            animation = new TranslateAnimation(0, - v.getLeft(), 0, 0);
            left = 0;
        }
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout.LayoutParams ll = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                ll.setMargins(left, top, 0, 0);
                v.setLayoutParams(ll);
                v.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(100 * 2 * currentX / screenWidth);
        v.startAnimation(animation);
    }

    /**
     * 文案配置
     */
	private void loadCopyWriter() {
        //只需要第一次加载的时候获取一次数据就行，获取了数据之后会写入到静态数据区中，不用每次都请求
        if (firstLoadCopyWriter) {
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.WordDataConfig, null, null, new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    successConnectCount = successConnectCount + 1;
                    firstLoadCopyWriter = false;
                    try {
                        if(response.getInt("status") == 1) {
                            //存入静态数据区
                            App.saveUserInfo.setJXPackageAdWord(response.getJSONObject("result").getString("JXPackageAdWord"));
                            App.saveUserInfo.setXFBPackageAdWord(response.getJSONObject("result").getString("XFBPackageAdWord"));
                            App.saveUserInfo.setInvestButtonAdWord(response.getJSONObject("result").getString("InvestButtonAdWord"));
                            App.saveUserInfo.setUpgradeWord(response.getJSONObject("result").getString("UpgradeWord"));
                            //设置精品项目和新房宝文案
                            String JXPackageAdWordString = App.saveUserInfo.getJXPackageAdWord();
                            //判断是否为空，包括 null、"null"、""
                            if (JXPackageAdWordString != null
                                    && !JXPackageAdWordString.equalsIgnoreCase("null")
                                    && !JXPackageAdWordString.equalsIgnoreCase("")) {
                                JXPackageAdWord.setText(JXPackageAdWordString);
                            } else {
                                JXPackageAdWord.setText("");
                                JXPackageAdWord.setVisibility(View.GONE);
                            }
                        } else {
                            LogUtil.e(TAG, "获取文案配置失败:" + response.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(Exception error) {

                }
            });
            jsonRequest.setTag(BcbRequestTag.WordDataConfigTag);
        }
	}


    /**
     * 首页Banner
     */
    private void loadBanner() {
        //Banner只需要第一次创建的时候加载一次，不用每次都请求服务器
        if (firstLoadBanner) {
            BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MainpageAdRotator, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    successConnectCount = successConnectCount + 1;
                    firstLoadBanner = false;
                    try {
                        if (PackageUtil.getRequestStatus(response, ctx)) {
                            synchronized (this) {
                                mAdPhotoListBean = App.mGson.fromJson(response.toString(), AdPhotoListBean.class);
                            }
                        }
                        initBanner();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorResponse(Exception error) {

                }
            });
            jsonRequest.setTag(BcbRequestTag.MainAdRotatorTag);
            requestQueue.add(jsonRequest);
        }
    }

    //初始化Banner
	private void initBanner(){
        //如果为空，直接返回
        if (mAdPhotoListBean == null) {
            return;
        }
        //如果结果为空，直接返回
		if (mAdPhotoListBean.result.isEmpty()) {
            return;
        }
        //创建新的列表数据
		listBanner = new ArrayList<BannerInfo>();	
		for (int i = 0; i < mAdPhotoListBean.result.size(); i++) {
			BannerInfo  item = new BannerInfo();
			item.Title = mAdPhotoListBean.result.get(i).Title;
			item.ImageUrl = mAdPhotoListBean.result.get(i).ImageUrl;
			item.PageUrl = mAdPhotoListBean.result.get(i).PageUrl;
			listBanner.add(item);
		}
		mImageCycleView.setImageResources(listBanner, mAdCycleViewListener);
	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {
        @Override
		public void onImageClick(BannerInfo info, int position, View imageView) {
			UmengUtil.eventById(ctx, R.string.banner_c);
			if (!TextUtils.isEmpty(info.PageUrl)) {
                LogUtil.d("图片地址", info.PageUrl);
				Activity_WebView.launche(ctx, info.Title, info.PageUrl, true);
			}
		}

		@Override
		public void displayImage(String imageURL, ImageView imageView) {
            //异步加载网络图片
            BcbRemoteImage.getInstance(ctx).loadRemoteImage(imageView, imageURL, R.drawable.banner_placeholder, R.drawable.banner_placeholder);
    	}
	};

    /**
     * 新接口同意获取新手推荐、新标预告、精品项目
     */
    private void loadMainListViewData() {
        //首先要清空数据
        clearAdapter();
        JSONObject obj = new JSONObject();
        try {
            obj.put("IsRecommand", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MainFragmentListData, obj, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (PackageUtil.getRequestStatus(response, ctx)) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        MainListBean mainListBean = null;
                        //判断JSON对象是否为空, 不为空则分别获取数据
                        if (obj != null) {
                            mainListBean = App.mGson.fromJson(obj.toString(), MainListBean.class);
                            //设置体验标
                            setupExpiredView(mainListBean);
                            //设置新手标
                            setupNewView(mainListBean);
                            //设置新标预告
                            setupAnnounceView(mainListBean);
                            //设置精品项目
                            setupBoutiqueView(mainListBean);
                            //显示新手标还是精品标
                            showItemVisible();
                        }
                    }
                } catch (Exception e) {
                    LogUtil.d(TAG, "" + e.getMessage());
                }
                canReFresh = true;
                refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onErrorResponse(Exception error) {
                canReFresh = true;
                refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                setupExpiredVisible(View.GONE);
                setupAnnounceVisible(View.GONE);
                setupBoutiqueVisible(View.GONE);
            }
        });
        jsonRequest.setTag(BcbRequestTag.MainFragmentListDataTag);
        requestQueue.add(jsonRequest);
    }


    //设置体验标数据
    private void setupExpiredView(MainListBean mainListBean) {
        //清空原来的数据
        expiredRecordsBeans.clear();
        //如果数据存在的时候
        if (null != mainListBean && null != mainListBean.Tyb && mainListBean.Tyb.size() > 0) {
            synchronized (this){
                expiredRecordsBeans.addAll(mainListBean.Tyb);
            }
            if (expiredAdapter != null) {
                expiredAdapter.notifyDataSetChanged();
            }
            //将精品项目所在的listview显示出来
            setupExpiredVisible(View.VISIBLE);
        }
        //新手推荐不存在的时候，隐藏列表
        else {
            //将listView 隐藏
            setupExpiredVisible(View.GONE);
        }
    }
    //设置体验标显示状态
    private void setupExpiredVisible(int visible) {
        expiredListview.setVisibility(visible);
    }

    //设置新手标
    private void setupNewView(MainListBean mainListBean) {
        //清空原来的数据
        newRecordsBeans.clear();
        //如果数据存在的时候
        if (null != mainListBean && null != mainListBean.Xszx && mainListBean.Xszx.size() > 0) {
            newRecordsBeans.addAll(mainListBean.Xszx);
            //刷新适配器，如果适配器没有则创建新的适配器
            if (newAdapter != null) {
                newAdapter.notifyDataSetChanged();
            }
            //将精品项目所在的listview显示出来
            setupNewVisible(View.VISIBLE);
        }
        //精品项目不存在的时候，隐藏列表
        else {
            //将listView 隐藏
            setupNewVisible(View.GONE);
        }
    }
    //设置新手标的显示状态
    private void setupNewVisible(int visible) {
        newListView.setVisibility(visible);
    }

    //设置新标预告
    private void setupAnnounceView(MainListBean mainListBean) {
        announceRecordsBeans.clear();
        //如果数据存在的时候
        if (null != mainListBean && null != mainListBean.Xbyg && mainListBean.Xbyg.size() > 0) {
            announceRecordsBeans.addAll(mainListBean.Xbyg);
            //刷新适配器，如果适配器没有则创建新的适配器
            if (announceAdapter != null) {
                announceAdapter.notifyDataSetChanged();
            }
            setupAnnounceVisible(View.VISIBLE);
        } else {
            setupAnnounceVisible(View.GONE);
        }
    }
    //设置新标预告的显示状态
    private void setupAnnounceVisible(int visible) {
        announceListView.setVisibility(visible);
    }

    //设置精品项目
    private void setupBoutiqueView(MainListBean mainListBean) {
        //清空原来的数据
        boutqueRecordsBeans.clear();
        //如果数据存在的时候
        if (null != mainListBean && null != mainListBean.Jpxm && mainListBean.Jpxm.size() > 0) {
            boutqueRecordsBeans.addAll(mainListBean.Jpxm);
            //刷新适配器，如果适配器没有则创建新的适配器
            if (mBoutiqueAdapter != null) {
                mBoutiqueAdapter.notifyDataSetChanged();
            }
            setupBoutiqueVisible(View.VISIBLE);
        }
        //精品项目不存在的时候，隐藏列表
        else {
            //将listView 隐藏
            setupBoutiqueVisible(View.GONE);
        }
    }
    //设置精品项目显示状态
    private void setupBoutiqueVisible(int visible) {
        boutiqueListview.setVisibility(visible);
    }

    //体验标的点击事件
    class expiredItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
            Activity_ExpiredProject_Introduction.launche(ctx,
                    expiredRecordsBeans.get(position).getPackageId(),
                    expiredRecordsBeans.get(position).getName());
        }
    }

    //新手标项目点击事件
    class newItemClickListener implements AdapterView.OnItemClickListener{
        public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
            Activity_NormalProject_Introduction.launche(ctx,
                    newRecordsBeans.get(position).getPackageId(),
                    newRecordsBeans.get(position).getName(),
                    newRecordsBeans.get(position).getCouponType());
        }
    }

    //精品项目的点击事件
    class boutiqueItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
            //判断是否属于新标预告的状态，根据状态来判断是否可点击
            if (boutqueRecordsBeans.get(position).getStatus() == 10) {
                return;
            }
            //判断是否可投标的
            if (boutqueRecordsBeans.get(position).getStatus() == 20) {
                UmengUtil.eventById(ctx, R.string.bid_avi);
            } else {
                UmengUtil.eventById(ctx, R.string.bid_unavi);
            }
            Activity_NormalProject_Introduction.launche(ctx,
                    boutqueRecordsBeans.get(position).getPackageId(),
                    boutqueRecordsBeans.get(position).getName(),
                    boutqueRecordsBeans.get(position).getCouponType());
        }
    }

    //注册广播，用于接收广播之后更新精品项目的数据
    class Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.bcb.project.buy.success")) {
                loadMainListViewData();
            }
            //登陆和退出的时候要重新创建新标预告，防止没法刷新数据
            else if (intent.getAction().equals("com.bcb.login.success")) {
                //需要判断announceAdapter是否存在，有可能退到后台时间过长被内存被释放掉了。
                if (announceAdapter != null) {
                    announceAdapter.notifyDataSetChanged();
                }
                //隐藏注册有礼
                button_floating.setVisibility(View.GONE);
                right_text.setVisibility(View.GONE);
                showItemVisible();
            } else if (intent.getAction().equals("com.bcb.register.success")) {
                showRegisterSuccessTips();
            }
        }
    }

    //清空首页标的数据
    private void clearAdapter() {
        expiredRecordsBeans.clear();
        newRecordsBeans.clear();
        boutqueRecordsBeans.clear();
        announceRecordsBeans.clear();
        if (expiredAdapter != null) {
            expiredAdapter.notifyDataSetChanged();
        }
        if (newAdapter != null) {
            newAdapter.notifyDataSetChanged();
        }
        if (mBoutiqueAdapter != null) {
            mBoutiqueAdapter.notifyDataSetChanged();
        }
        if (announceAdapter != null) {
            announceAdapter.notifyDataSetChanged();
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		mImageCycleView.startImageCycle();
        if (App.saveUserInfo.getAccess_Token() == null ) {
            if (button_floating != null) {
                button_floating.setVisibility(View.VISIBLE);
            }
            if (right_text != null) {
                right_text.setVisibility(View.VISIBLE);
            }
        }
        showItemVisible();
	}

	@Override
    public void onPause() {
		super.onPause();
		mImageCycleView.pushImageCycle();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageCycleView.pushImageCycle();
        ctx.unregisterReceiver(mReceiver);
	}

    //判断是否显示数据
    private void showItemVisible() {
        //如果没有登录或者是没有用银行卡信息
        if ((App.saveUserInfo == null || App.saveUserInfo.getAccess_Token() == null) || (App.mUserDetailInfo == null || !App.mUserDetailInfo.HasInvest)) {
            setupBoutiqueVisible(View.GONE);
            setupNewVisible(View.VISIBLE);
        } else {
            setupBoutiqueVisible(View.VISIBLE);
            setupNewVisible(View.GONE);
        }
    }

    //显示送体验金对话框
    private void showRegisterSuccessTips() {
        synchronized (this) {
            dialogWidget = new DialogWidget(ctx, RegisterSuccessDialogView.getInstance(ctx, new RegisterSuccessDialogView.OnClikListener() {
                @Override
                public void onCancelClick() {
                    dialogWidget.dismiss();
                    dialogWidget = null;
                }

                @Override
                public void onSureClick() {
                    dialogWidget.dismiss();
                    dialogWidget = null;
                    if (expiredRecordsBeans != null) {
                        Activity_ExpiredProject_Introduction.launche(ctx,
                                expiredRecordsBeans.get(0).getPackageId(),
                                expiredRecordsBeans.get(0).getName());
                    } else {
                        ToastUtil.alert(ctx, "体验金已存入账户中心，请查看");
                    }
                }
            }).getView());
        }
        dialogWidget.show();
    }
}