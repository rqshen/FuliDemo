package com.bcb.module.homepager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.MyApplication;
import com.bcb.R;
import com.bcb.base.BaseFragment;
import com.bcb.constant.ProjectListStatus;
import com.bcb.constant.ProjectListType;
import com.bcb.data.bean.AdPhotoListBean;
import com.bcb.data.bean.BannerInfo;
import com.bcb.data.bean.MainListBean2;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.event.BroadcastEvent;
import com.bcb.module.discover.adapter.FinanceListAdapter;
import com.bcb.module.discover.carinsurance.CarInsuranceActivity;
import com.bcb.module.discover.financialproduct.InvestmentFinanceActivity;
import com.bcb.module.discover.financialproduct.normalproject.NormalProjectIntroductionActivity;
import com.bcb.module.discover.financialproduct.wrapprogram.WrapProgramIntroductionActivity;
import com.bcb.module.homepager.adapter.AnnounceListAdapter;
import com.bcb.module.login.LoginActivity;
import com.bcb.module.login.register.RegisterFirstActivity;
import com.bcb.module.myinfo.balance.FundCustodianAboutActivity;
import com.bcb.module.myinfo.joincompany.JoinCompanyActivity;
import com.bcb.module.myinfo.myfinancial.myfinancialstate.myfinanciallist.myfinancialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.presentation.view.activity.Activity_Privilege_Money;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomDialog.BasicDialog;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.RegisterSuccessDialogView;
import com.bcb.presentation.view.custom.PagerIndicator.AutoLoopViewPager;
import com.bcb.presentation.view.custom.PagerIndicator.CirclePageIndicator;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.presentation.view.custom.PullableView.PullableScrollView;
import com.bcb.presentation.view.custom.UPMarqueeView;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MyListView;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.util.UmengUtil;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.bcb.MyApplication.mUserDetailInfo;

public class HomePagerFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "HomePagerFragment";
    UPMarqueeView tb;
    //车险
    View ll_car, ll_lb, ll_xj, iv_zc, tv_more;

    //刷新控件
    private PullToRefreshLayout refreshLayout;
    private PullableScrollView layout_scrollview;

    MainListBean2 mainListBean2;
    //新标预告
    private MyListView announceListView;
    private AnnounceListAdapter mAnnounceListAdapter;
    private List<MainListBean2.XbygBean> announceRecordsBeans;

    //精品项目
    private MyListView boutiqueListview;
    private FinanceListAdapter mBoutiqueAdapter;
    private List<MainListBean2.JpxmBean> boutqueRecordsBeans;

    //Banner
    private AdPhotoListBean mAdPhotoListBean;
    private ArrayList<BannerInfo> listBanner;
    private AutoLoopViewPager loopViewPager;

    private TextView JXPackageAdWord;
    private int successConnectCount = 0;
    View line_yg;

    private Activity ctx;

    //广播
    private Receiver mReceiver;

    private DialogWidget dialogWidget;

    private BcbRequestQueue requestQueue;

    public HomePagerFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        this.ctx = (Activity) view.getContext();
        EventBus.getDefault().register(this);
        requestQueue = MyApplication.getInstance().getRequestQueue();

        //仅保留下拉刷新，隐藏上拉加载更多
        //隐藏加载更多
        (view.findViewById(R.id.loadmore_view)).setVisibility(View.GONE);
        ll_car = view.findViewById(R.id.ll_car);
        ll_car.setOnClickListener(this);
        ll_lb = view.findViewById(R.id.ll_lb);
        ll_lb.setOnClickListener(this);
        ll_xj = view.findViewById(R.id.ll_xj);
        line_yg = view.findViewById(R.id.line_yg);
        ll_xj.setOnClickListener(this);

        tb = (UPMarqueeView) view.findViewById(R.id.tb);

        iv_zc = view.findViewById(R.id.iv_zc);
        iv_zc.setOnClickListener(this);
        tv_more = view.findViewById(R.id.tv_more);
        tv_more.setOnClickListener(this);

        refreshLayout = ((PullToRefreshLayout) view.findViewById(R.id.refresh_view));
        //不显示刷新结果
        refreshLayout.setRefreshResultView(false);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (HttpUtils.isNetworkConnected(ctx)) {
                    loadBanner();
                    loadMainListViewData();
                    if (MyApplication.saveUserInfo.getAccess_Token() == null) {
                        iv_zc.setVisibility(View.VISIBLE);
                    } else iv_zc.setVisibility(View.GONE);
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

        //新标预告
        announceRecordsBeans = new ArrayList<>();
        mAnnounceListAdapter = new AnnounceListAdapter(ctx, announceRecordsBeans);
        announceListView = (MyListView) view.findViewById(R.id.announce_listView);
        announceListView.setAdapter(mAnnounceListAdapter);

        //精品项目
        boutqueRecordsBeans = new ArrayList<>();
        mBoutiqueAdapter = new FinanceListAdapter(ctx, boutqueRecordsBeans);
        JXPackageAdWord = (TextView) view.findViewById(R.id.JXPackageAdWord);
        boutiqueListview = (MyListView) view.findViewById(R.id.boutique_listview);
        boutiqueListview.setOnItemClickListener(this);
        boutiqueListview.setAdapter(mBoutiqueAdapter);

        //文案配置
        loadCopyWriter();
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
        refreshLayout.autoRefresh();
        if (MyApplication.saveUserInfo.getAccess_Token() == null) {
            iv_zc.setVisibility(View.VISIBLE);
        } else iv_zc.setVisibility(View.GONE);
    }

    /**
     * 文案配置
     */
    private void loadCopyWriter() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.WordDataConfig, null, null, new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                successConnectCount = successConnectCount + 1;
                LogUtil.i("bqt", "首页：文案配置" + response.toString());

                try {
                    if (response.getInt("status") == 1) {
                        //存入静态数据区
                        MyApplication.saveUserInfo.setJXPackageAdWord(response.getJSONObject("result").getString("JXPackageAdWord"));
                        MyApplication.saveUserInfo.setXFBPackageAdWord(response.getJSONObject("result").getString("XFBPackageAdWord"));
                        MyApplication.saveUserInfo.setInvestButtonAdWord(response.getJSONObject("result").getString("InvestButtonAdWord"));
                        MyApplication.saveUserInfo.setUpgradeWord(response.getJSONObject("result").getString("UpgradeWord"));
                        //设置精品项目和新房宝文案
                        String JXPackageAdWordString = MyApplication.saveUserInfo.getJXPackageAdWord();
                        //判断是否为空，包括 null、"null"、""
                        if (JXPackageAdWordString != null && !JXPackageAdWordString.equalsIgnoreCase("null") &&
                                !JXPackageAdWordString.equalsIgnoreCase("")) {
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
        requestQueue.add(jsonRequest);
    }

    /**
     * 获取首页Banner
     */
    private void loadBanner() {
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MainpageAdRotator, null, TokenUtil.getEncodeToken(ctx), new
                BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("bqt", "首页：Banner" + response.toString());
                        successConnectCount = successConnectCount + 1;
                        try {
                            if (PackageUtil.getRequestStatus(response, ctx)) {
                                synchronized (this) {
                                    JSONObject obj = PackageUtil.getResultObject(response);
                                    if (null != obj) {
                                        mAdPhotoListBean = MyApplication.mGson.fromJson(obj.toString(), AdPhotoListBean.class);
                                    }
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

    //初始化Banner
    private void initBanner() {
        //如果为空，直接返回
        if (null == mAdPhotoListBean || null == mAdPhotoListBean.BannerList || 0 == mAdPhotoListBean.BannerList.size()) {
            return;
        }
        tb.setViews(mAdPhotoListBean.NoticeList);
        tb.startFlipping();
        //创建新的列表数据
        listBanner = new ArrayList<>();
        for (int i = 0; i < mAdPhotoListBean.BannerList.size(); i++) {
            BannerInfo item = new BannerInfo();
            item.Title = mAdPhotoListBean.BannerList.get(i).Title;
            item.ImageUrl = mAdPhotoListBean.BannerList.get(i).ImageUrl;
            item.PageUrl = mAdPhotoListBean.BannerList.get(i).PageUrl;
            listBanner.add(item);
        }
        //Banner
        loopViewPager = (AutoLoopViewPager) ctx.findViewById(R.id.autoLoop);
        loopViewPager.setBoundaryCaching(true);
        loopViewPager.setClipChildren(false);
        loopViewPager.setAutoScrollDurationFactor(10d);
        loopViewPager.setInterval(6000);
        loopViewPager.startAutoScroll();
        loopViewPager.setAdapter(new LoopImageAdapter(ctx, listBanner));
        CirclePageIndicator indy = (CirclePageIndicator) ctx.findViewById(R.id.indy);
        indy.setViewPager(loopViewPager);
    }

    //Banner适配器
    private class LoopImageAdapter extends PagerAdapter {
        private int count = 100;
        private Queue<View> views;
        private List<BannerInfo> data;
        private LayoutInflater lay;
        private Activity context;

        LoopImageAdapter(Activity ct, List<BannerInfo> listNews) {
            views = new LinkedList<>();
            data = listNews;
            lay = LayoutInflater.from(ct);
            context = ct;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View mage = views.poll();

            if (mage == null) {
                mage = lay.inflate(R.layout.news_gallery_page, null);
                mage.setId(count++);
            }
            ImageView iv = (ImageView) mage.findViewById(R.id.mage);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(data.get(position).PageUrl)) {
                        ProjectDetailActivity.launche(context, data.get(position).Title, data.get(position).PageUrl, true);
                    }
                }
            });
            //异步加载网络图片
            Glide.with(context).load(data.get(position).ImageUrl).into(iv);
            container.addView(mage);
            return mage;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View mage = (View) object;
            views.add(mage);
            container.removeView(mage);
        }
    }

    /**
     * 获取新标预告、精品项目
     */
    private void loadMainListViewData() {
        //首先要清空数据
        clearAdapter();
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MainFragmentListData2, null, TokenUtil.getEncodeToken(ctx), new
                BcbRequest.BcbCallBack<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i("bqt", "首页项目" + response.toString());
                        try {
                            if (PackageUtil.getRequestStatus(response, ctx)) {
                                JSONObject obj = PackageUtil.getResultObject(response);
                                if (obj != null) {
                                    //******************************************************************************************
                                    mainListBean2 = MyApplication.mGson.fromJson(obj.toString(), MainListBean2.class);
                                    //设置新标预告
                                    setupAnnounceView();
                                    //设置精品项目
                                    setupBoutiqueView();
                                }
                            }
                        } catch (Exception e) {
                            LogUtil.d(TAG, "" + e.getMessage());
                        }
                        refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }

                    @Override
                    public void onErrorResponse(Exception error) {
                        refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                        setupAnnounceVisible(View.GONE);
                        setupBoutiqueVisible(View.GONE);
                    }
                });
        requestQueue.add(jsonRequest);
    }

    //设置新标预告
    private void setupAnnounceView() {
        announceRecordsBeans.clear();
        //如果数据存在的时候
        if (null != mainListBean2 && null != mainListBean2.Xbyg && mainListBean2.Xbyg.size() > 0) {
            announceRecordsBeans.addAll(mainListBean2.Xbyg);
            //刷新适配器，如果适配器没有则创建新的适配器
            if (mAnnounceListAdapter != null) {
                mAnnounceListAdapter.notifyDataSetChanged();
            }
            setupAnnounceVisible(View.VISIBLE);
        } else {
            setupAnnounceVisible(View.GONE);
        }
    }

    //设置新标预告的显示状态
    private void setupAnnounceVisible(int visible) {
        if (announceListView != null) {
            announceListView.setVisibility(visible);
            line_yg.setVisibility(visible);
        }
    }

    //设置精品项目
    private void setupBoutiqueView() {
        //清空原来的数据
        boutqueRecordsBeans.clear();
        //如果数据存在的时候
        if (null != mainListBean2 && null != mainListBean2.Jpxm && mainListBean2.Jpxm.size() > 0) {
            boutqueRecordsBeans.addAll(mainListBean2.Jpxm);
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
        if (boutiqueListview != null) {
            boutiqueListview.setVisibility(visible);
        }
    }

    @Override
    public void onClick(View v) {
        if (MyApplication.saveUserInfo.getAccess_Token() == null && v.getId() != R.id.tv_more) {
            LoginActivity.launche(ctx);
            return;
        }
        switch (v.getId()) {
            case R.id.ll_car:
                if (mUserDetailInfo == null || TextUtils.isEmpty(mUserDetailInfo.CarInsuranceIndexPage)) {
                    Toast.makeText(ctx, "您尚未注册", Toast.LENGTH_SHORT).show();
                } else {
                    CarInsuranceActivity.launche(ctx, "车险内购", mUserDetailInfo.CarInsuranceIndexPage);
                }
                break;
            case R.id.ll_lb:
                toJoinCompany();
                break;
            case R.id.ll_xj:
                Toast.makeText(ctx, "敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_more://更多理财
                startActivity(new Intent(ctx, InvestmentFinanceActivity.class));
                break;
            case R.id.iv_zc:
                RegisterFirstActivity.launche(ctx);
                break;
        }
    }

    //加入公司
    private void toJoinCompany() {
        if (mUserDetailInfo == null || !mUserDetailInfo.HasOpenCustody) {
            startActivity(new Intent(ctx, FundCustodianAboutActivity.class));
        }
        //否则需要判断MyCompany字段
        else {
            //未申请
            if (mUserDetailInfo.MyCompany == null) {
                JoinCompanyActivity.launche(ctx);
            }
            //审核中
            else if (mUserDetailInfo.MyCompany.Status == 5) {
                companyAlertView("您的认证申请正在审核", "预计2个工作日内完成，请耐心等候");
            }
            //拉黑
            else if (mUserDetailInfo.MyCompany.Status == 15) {
                companyAlertView("提示", "你已被拉入黑名单\n详情请咨询工作人员");
            } else if (MyApplication.mUserDetailInfo.MyCompany.Status == 10) {
                changeCompany();
            }
        }
    }

    private void changeCompany() {
        showAlertView("提示", "您需要修改公司认证信息吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertView.dismiss();
                alertView = null;
                JoinCompanyActivity.launche(ctx);
            }
        });
    }

    //提示对话框
    private void showAlertView(String titleName, String contentMessage, DialogInterface.OnClickListener onClickListener) {
        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
        ibuilder.setTitle(titleName);
        ibuilder.setMessage(contentMessage);
        ibuilder.setPositiveButton("立即修改", onClickListener);
        ibuilder.setNegativeButton("取消", null);
        alertView = ibuilder.create();
        alertView.show();
    }

    /***************************
     * 审核中
     *************************/
    private void companyAlertView(String title, String contentMessage) {
        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
        ibuilder.setTitle(title);
        ibuilder.setMessage(contentMessage);
        ibuilder.setPositiveButton("知道了", null);
        alertView = ibuilder.create();
        alertView.show();
    }

    //精品项目的点击事件
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        MainListBean2.JpxmBean jpxm = boutqueRecordsBeans.get(position);
        //判断是否属于新标预告的状态，根据状态来判断是否可点击
        if (jpxm.Status == 10) {
            Toast.makeText(ctx, "Status == 10，不可购买", Toast.LENGTH_SHORT).show();
        }
        //0稳盈宝【月】，1涨薪宝【普通】，2周盈宝【周】
        if (jpxm.Type != null && jpxm.Type.equals(ProjectListType.WYB)) {//稳盈宝【月】mon_package
            WrapProgramIntroductionActivity.launche2(ctx, jpxm.PackageId, ProjectListStatus.WYB);
        } else if (jpxm.Type != null && jpxm.Type.equals(ProjectListType.ZXB)) {//涨薪宝，原始
            NormalProjectIntroductionActivity.launche(ctx, jpxm.PackageId);
        } else if (jpxm.Type != null && jpxm.Type.equals(ProjectListType.ZYB)) {//周盈宝，周
            WrapProgramIntroductionActivity.launche2(ctx, jpxm.PackageId, ProjectListStatus.ZYB);
        }

    }

    //注册广播，用于接收广播之后更新精品项目的数据
    class Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.bcb.project.buy.success")) {
                loadMainListViewData();
            }
            //登陆和退出的时候要重新创建新标预告，防止没法刷新数据
            else if (intent.getAction().equals("com.bcb.login.success")) {
                //需要判断announceAdapter是否存在，有可能退到后台时间过长被内存被释放掉了。
                if (mAnnounceListAdapter != null) {
                    mAnnounceListAdapter.notifyDataSetChanged();
                }
                showItemVisible();
                requestUserDetailInfo();
            } else if (intent.getAction().equals("com.bcb.register.success")) {
                showRegisterSuccessTips();
            }
        }
    }

    /**
     * 用户信息
     */
    private void requestUserDetailInfo() {

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserMessage, null, TokenUtil.getEncodeToken(ctx), new BcbRequest
                .BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.i("bqt", "首页：用户信息返回数据：" + response.toString());
                if (PackageUtil.getRequestStatus(response, ctx)) {
                    JSONObject data = PackageUtil.getResultObject(response);
                    //判断JSON对象是否为空
                    if (data != null) {
                        //将获取到的银行卡数据写入静态数据区中
                        mUserDetailInfo = MyApplication.mGson.fromJson(data.toString(), UserDetailInfo.class);
                        if (mUserDetailInfo != null && !mUserDetailInfo.HasOpenCustody) {
                            alterHFOpen();
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.UserBankMessageTag);
        requestQueue.add(jsonRequest);
    }

    AlertView alertView;

    private void alterHFOpen() {
        new BasicDialog(ctx).show();
    }

    //清空首页标的数据
    private void clearAdapter() {
        boutqueRecordsBeans.clear();
        announceRecordsBeans.clear();
        if (mBoutiqueAdapter != null) {
            mBoutiqueAdapter.notifyDataSetChanged();
        }
        if (mAnnounceListAdapter != null) {
            mAnnounceListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 判断是否显示列表数据
     */
    private void showItemVisible() {
        //如果没有登录或者没有投资过的
        if ((MyApplication.saveUserInfo == null || TextUtils.isEmpty(MyApplication.saveUserInfo.getAccess_Token())) || MyApplication.mUserDetailInfo == null ||
                !MyApplication.mUserDetailInfo.HasInvest) {
            setupBoutiqueVisible(View.GONE);
        } else {
            setupBoutiqueVisible(View.VISIBLE);
        }
        //如果新标预告或者新手标为空的时候就显示列表
        if (announceRecordsBeans == null || announceRecordsBeans.size() <= 0) {
            setupBoutiqueVisible(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ctx.unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
    }

    //显示送体验金对话框
    private void showRegisterSuccessTips() {
        synchronized (this) {
            dialogWidget = new DialogWidget(ctx, RegisterSuccessDialogView.getInstance(ctx, new RegisterSuccessDialogView
                    .OnClikListener() {
                @Override
                public void onCancelClick() {
                    dialogWidget.dismiss();
                    dialogWidget = null;
                }

                @Override
                public void onSureClick() {
                    //跳到特权本金页面！
                    dialogWidget.dismiss();
                    dialogWidget = null;
                    ToastUtil.alert(ctx, "特权本金已存入账户中心，请查看");
                    startActivity(new Intent(ctx, Activity_Privilege_Money.class));
                }
            }).getView());
        }
        dialogWidget.show();
    }

    //接收事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BroadcastEvent event) {
        String flag = event.getFlag();
        if (!TextUtils.isEmpty(flag)) {
            switch (flag) {
                case BroadcastEvent.LOGOUT:
                case BroadcastEvent.LOGIN:
                case BroadcastEvent.REFRESH:
                    refreshLayout.autoRefresh();
                    break;
            }
        }
    }
}