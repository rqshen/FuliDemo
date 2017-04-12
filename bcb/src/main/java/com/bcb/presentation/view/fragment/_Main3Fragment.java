package com.bcb.presentation.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.BaseFragment;
import com.bcb.MyApplication;
import com.bcb.event.BroadcastEvent;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.network.UrlsTwo;
import com.bcb.data.bean.AdPhotoListBean;
import com.bcb.data.bean.AnnounceRecordsBean;
import com.bcb.data.bean.BannerInfo;
import com.bcb.data.bean.ExpiredRecordsBean;
import com.bcb.data.bean.MainListBean;
import com.bcb.data.bean.ProductRecordsBean;
import com.bcb.data.bean.StringEventBusBean;
import com.bcb.data.bean.UserDetailInfo;
import com.bcb.data.bean.WelfareDto;
import com.bcb.utils.DialogUtil;
import com.bcb.utils.HttpUtils;
import com.bcb.utils.LogUtil;
import com.bcb.utils.MyListView;
import com.bcb.utils.PackageUtil;
import com.bcb.utils.ToastUtil;
import com.bcb.utils.TokenUtil;
import com.bcb.utils.UmengUtil;
import com.bcb.module.home.MainActivity;
import com.bcb.module.myinfo.financial.financialdetail.projectdetail.ProjectDetailActivity;
import com.bcb.presentation.adapter.AnnounceAdapter;
import com.bcb.presentation.adapter.ExpiredAdapter;
import com.bcb.presentation.adapter.ProductAdapter;
import com.bcb.presentation.view.activity.Activity_Daily_Welfare;
import com.bcb.presentation.view.activity.Activity_Daily_Welfare_Static;
import com.bcb.presentation.view.activity.Activity_Login;
import com.bcb.presentation.view.activity.Activity_Login_Introduction;
import com.bcb.presentation.view.activity.Activity_Love;
import com.bcb.presentation.view.activity.Activity_NormalProject_Introduction;
import com.bcb.presentation.view.activity.Activity_Privilege_Money;
import com.bcb.presentation.view.activity.Activity_WebView_Upload;
import com.bcb.presentation.view.custom.AlertView.AlertView;
import com.bcb.presentation.view.custom.CustomDialog.BasicDialog;
import com.bcb.presentation.view.custom.CustomDialog.DialogWidget;
import com.bcb.presentation.view.custom.CustomDialog.RegisterSuccessDialogView;
import com.bcb.presentation.view.custom.PagerIndicator.AutoLoopViewPager;
import com.bcb.presentation.view.custom.PagerIndicator.CirclePageIndicator;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;
import com.bcb.presentation.view.custom.PullableView.PullableScrollView;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.greenrobot.event.EventBus;

public class _Main3Fragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
	private static final String TAG = "HomePagerFragment";
	
	//车险
	RelativeLayout rl_car;

	//刷新控件
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

	//从产品列表中获取数据
	private MyListView additionListview;
	private ProductAdapter mAdditionAdapter;
	private List<ProductRecordsBean> additionRecordsBeans;

	//Banner
	private AdPhotoListBean mAdPhotoListBean;
	private ArrayList<BannerInfo> listBanner;
	private AutoLoopViewPager loopViewPager;

	//滚动广告
	private TextView notice_text;

	private TextView JXPackageAdWord;
	private int successConnectCount = 0;

	private Activity ctx;

	//广播
	private Receiver mReceiver;

	private DialogWidget dialogWidget;

	private BcbRequestQueue requestQueue;

	//悬浮按钮
	private int screenHeight;
	private int screenWidth;
	private int floatButtonBitmapWdith;
	private int floatButtonBitmapHeight;
	private int bottomHeight;
	private float startX;
	private float startY;
	private int lastX;
	private int lastY;
	private Button button_floating;

	//点击每日福利
	private Dialog progressDialog;
	private WelfareDto welfareDto;//完整数据

	public _Main3Fragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_main, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		
		this.ctx = (Activity) view.getContext();
		EventBus.getDefault().register(this);
		requestQueue = MyApplication.getInstance().getRequestQueue();

		progressDialog = DialogUtil.createLoadingDialog(ctx);

		//仅保留下拉刷新，隐藏上拉加载更多
		//隐藏加载更多
		(view.findViewById(R.id.loadmore_view)).setVisibility(View.GONE);
		rl_car= ((RelativeLayout) view.findViewById(R.id.rl_car));
		rl_car.setOnClickListener(this);

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

		//4个按钮:每日福利、理财学院、安全保障、聚爱
		view.findViewById(R.id.ll_daily_welfare).setOnClickListener(this);
		view.findViewById(R.id.ll_wealth_college).setOnClickListener(this);
		view.findViewById(R.id.ll_security).setOnClickListener(this);
		view.findViewById(R.id.ll_love).setOnClickListener(this);

		//滚动广告
		notice_text = (TextView) view.findViewById(R.id.notice_text);

		//体验标
		expiredRecordsBeans = new ArrayList<>();
		expiredAdapter = new ExpiredAdapter(ctx, expiredRecordsBeans);
		expiredListview = (MyListView) view.findViewById(R.id.expired_listview);
		expiredListview.setAdapter(expiredAdapter);

		//新手标(数据格式跟精品项目的一样，只是多了一个新手专享的图片)
		newRecordsBeans = new ArrayList<>();
		newAdapter = new ProductAdapter(ctx, newRecordsBeans, true);
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

		//从产品列表中获取回来的列表
		additionListview = (MyListView) view.findViewById(R.id.addition_listView);
		additionListview.setOnItemClickListener(new boutiqueItemClickListener());
		additionRecordsBeans = new ArrayList<>();
		mAdditionAdapter = new ProductAdapter(ctx, additionRecordsBeans);
		additionListview.setAdapter(mAdditionAdapter);

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

		//浮标按钮
		button_floating = (Button) view.findViewById(R.id.button_floating);
		initFloatingButton();
		refreshLayout.autoRefresh();
	}

	private void initFloatingButton() {
		//获取屏幕宽度
		DisplayMetrics dm = new DisplayMetrics();
		ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		bottomHeight = ((MainActivity) getActivity()).getBottomHeight();
		screenHeight = ctx.getWindowManager().getDefaultDisplay().getHeight();
		//获取图标的宽高
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.floating_button);
		floatButtonBitmapWdith = bitmap.getWidth();
		floatButtonBitmapHeight = bitmap.getHeight();
		bitmap.recycle();
		//浮标按钮
		if (MyApplication.saveUserInfo.getAccess_Token() != null) {
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
							right = left + floatButtonBitmapWdith;
						}
						if (right > screenWidth) {
							right = screenWidth;
							left = right - floatButtonBitmapWdith;
						}
						//状态条的高度
						if (top <= getStatusBarHeight()) {
							top = getStatusBarHeight();
							bottom = top + floatButtonBitmapHeight;
						}
						if (event.getRawY() > screenHeight - bottomHeight) {
							bottom = screenHeight - bottomHeight;
							top = bottom - floatButtonBitmapHeight;
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
						//判断是否位置是否超出底部状态栏
						top = v.getTop();
						if (event.getRawY() > screenHeight - bottomHeight - v.getHeight()) {
							bottom = screenHeight - bottomHeight;
							left = v.getLeft();
							right = left + floatButtonBitmapWdith;
							top = bottom - floatButtonBitmapHeight;
							v.layout(left, top, right, bottom);
							//                            Log.d("1234", "left = " + left + "  top = " + top + "  right = " + right +
							// "  bottom = " + bottom);
						}
						//动画
						movingAnimation(v, top, (int) event.getRawX());
						break;
				}
				return true;
			}
		});
	}

	/**
	 * 获取状态条的高度
	 *
	 * @return 状态条高度
	 */
	public int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 38;//默认为38，貌似大部分是这样的
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	//按钮移动动画
	private void movingAnimation(final View v, final int top, int currentX) {
		final int left;
		TranslateAnimation animation = null;
		if (currentX > screenWidth / 2) {
			animation = new TranslateAnimation(0, screenWidth - v.getRight(), 0, 0);
			left = screenWidth - v.getWidth();
		} else {
			animation = new TranslateAnimation(0, -v.getLeft(), 0, 0);
			left = 0;
		}
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				FrameLayout.LayoutParams ll = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout
						.LayoutParams.WRAP_CONTENT);
				//                Log.d("1234", "left = " + left + "  top = " + top);
				//                Log.d("1234", "floatButtonBitmapHeight = " + floatButtonBitmapHeight);
				//                Log.d("1234", "screenHeight = " + screenHeight);
				//                Log.d("1234", "bottomHeight = " + bottomHeight);
				//注：有BUG，拉到底部会出现压扁情况，暂时这样处理
				int mtop = top;
				if (screenHeight == (top + bottomHeight + floatButtonBitmapHeight)) {
					mtop -= 50;
				}
				ll.setMargins(left, mtop, 0, 0);
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
		//创建新的列表数据
		listBanner = new ArrayList<>();
		for (int i = 0 ; i < mAdPhotoListBean.BannerList.size() ; i++) {
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



	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if (0 == position && button_floating != null) {
			if (MyApplication.saveUserInfo.getAccess_Token() == null) {
				button_floating.setVisibility(View.VISIBLE);
			} else {
				button_floating.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

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
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.MainFragmentListData, obj, TokenUtil.getEncodeToken(ctx), new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "首页：精品项目" + response.toString());

				try {
					if (PackageUtil.getRequestStatus(response, ctx)) {
						JSONObject obj = PackageUtil.getResultObject(response);
						MainListBean mainListBean = null;
						//判断JSON对象是否为空, 不为空则分别获取数据
						if (obj != null) {
							//******************************************************************************************
							mainListBean = MyApplication.mGson.fromJson(obj.toString(), MainListBean.class);
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
				refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}

			@Override
			public void onErrorResponse(Exception error) {
				refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				setupExpiredVisible(View.GONE);
				setupAnnounceVisible(View.GONE);
				setupBoutiqueVisible(View.GONE);
				setupAdditionVisible(View.GONE);
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
			synchronized (this) {
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
		if (expiredListview != null) {
			expiredListview.setVisibility(visible);
		}
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
		if (newListView != null) {
			newListView.setVisibility(visible);
		}
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
		if (announceListView != null) {
			announceListView.setVisibility(visible);
		}
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
		if (boutiqueListview != null) {
			boutiqueListview.setVisibility(visible);
		}
	}

	//从产品列表获取过来的数据
	private void setupAdditionVisible(int visible) {
		if (additionListview != null) {
			additionListview.setVisibility(visible);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_car:
				if (MyApplication.saveUserInfo.getAccess_Token() == null) {
					Activity_Login.launche(ctx);
				} else if (MyApplication.mUserDetailInfo == null ||TextUtils.isEmpty(MyApplication.mUserDetailInfo.CarInsuranceIndexPage)) {
					Toast.makeText(ctx, "网络异常，请刷新后重试", Toast.LENGTH_SHORT).show();
				} else {
					Activity_WebView_Upload.launche(ctx, "车险内购", MyApplication.mUserDetailInfo.CarInsuranceIndexPage);
				}
				break;
			case R.id.ll_daily_welfare://每日福利
				if (MyApplication.saveUserInfo.getAccess_Token() == null) {
					Activity_Login.launche(ctx);
					break;
				}
				UmengUtil.eventById(ctx, R.string.fuli_c);
				//请求统计数据
				getStatisticsData();
				break;
			case R.id.ll_wealth_college://理财学院
				UmengUtil.eventById(ctx, R.string.college_c);
				ProjectDetailActivity.launche(ctx, "理财学院", UrlsOne.CollegeWebView);
				break;
			case R.id.ll_security://安全保障
				UmengUtil.eventById(ctx, R.string.safe_c);
				ProjectDetailActivity.launche(ctx, "安全保障", UrlsOne.SecureWebView);
				break;
			case R.id.ll_love:
				Activity_Love.launche(ctx);
				break;
		}
	}

	//新手标项目点击事件
	class newItemClickListener implements AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			//            Activity_NormalProject_Introduction.launche(ctx,
			//                    newRecordsBeans.get(position).getPackageId(),
			//                    newRecordsBeans.get(position).getName(),
			//                    newRecordsBeans.get(position).getCouponType());
		}
	}

	//精品项目的点击事件
	class boutiqueItemClickListener implements AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			//判断是否属于新标预告的状态，根据状态来判断是否可点击
			if (boutqueRecordsBeans.get(position).Status == 10) {
				return;
			}
			//判断是否可投标的
			if (boutqueRecordsBeans.get(position).Status == 20) {
				UmengUtil.eventById(ctx, R.string.bid_avi);
			} else {
				UmengUtil.eventById(ctx, R.string.bid_unavi);
			}
			int type = 0;
			if (boutqueRecordsBeans.get(position).Type.equals("claim_convey")) type = 1;
			else if (boutqueRecordsBeans.get(position).Type.equals("mon_package")) type = 2;
			Activity_NormalProject_Introduction.launche2(ctx, boutqueRecordsBeans.get(position).PackageId, 0, type);//标类型：prj_package则为普通标，claim_convey则为债权转让标
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
				if (announceAdapter != null) {
					announceAdapter.notifyDataSetChanged();
				}
				showItemVisible();
				if (button_floating != null) {
					if (MyApplication.saveUserInfo.getAccess_Token() == null) {
						button_floating.setVisibility(View.VISIBLE);
					} else {
						button_floating.setVisibility(View.GONE);
					}
				}
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
						MyApplication.mUserDetailInfo = MyApplication.mGson.fromJson(data.toString(), UserDetailInfo.class);
						if (MyApplication.mUserDetailInfo != null && !MyApplication.mUserDetailInfo.HasOpenCustody) {
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
		//        AlertView.Builder ibuilder = new AlertView.Builder(ctx);
		//        ibuilder.setTitle("提示");
		//        ibuilder.setMessage("福利金融接入资金托管啦！" );
		//        //已开通托管
		//            ibuilder.setPositiveButton("开通资金托管账户", new DialogInterface.OnClickListener() {
		//                @Override
		//                public void onClick(DialogInterface dialog, int which) {
		//                    alertView.dismiss();
		//                    alertView = null;
		//                    ctx.startActivity(new Intent(ctx, Activity_Open_Account.class));
		//                }
		//            });
		//
		//        alertView = ibuilder.create();
		//        alertView.show();
	}

	//清空首页标的数据
	private void clearAdapter() {
		expiredRecordsBeans.clear();
		newRecordsBeans.clear();
		boutqueRecordsBeans.clear();
		announceRecordsBeans.clear();
		additionRecordsBeans.clear();
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
		if (mAdditionAdapter != null) {
			mAdditionAdapter.notifyDataSetChanged();
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		showItemVisible();
		if (MyApplication.saveUserInfo.getAccess_Token() == null && button_floating != null) button_floating.setVisibility(View.VISIBLE);
		boolean che= MyApplication.mUserDetailInfo == null ||TextUtils.isEmpty(MyApplication.mUserDetailInfo.CarInsuranceIndexPage);
		LogUtil.i("bqt", "【进入时是否没有获取到车险】"+che);
		if (che){
			rl_car.setVisibility(View.GONE);
		}
	}
	public void onEventMainThread(StringEventBusBean event) {
		if (event.getContent().equals("CXGONE")) {
			rl_car.setVisibility(View.GONE);
			LogUtil.i("bqt", "【隐藏车险】");
		}else if (event.getContent().equals("CXVISIBLE")) {
			rl_car.setVisibility(View.VISIBLE);
			LogUtil.i("bqt", "【显示车险】");
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ctx.unregisterReceiver(mReceiver);
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 判断是否显示列表数据
	 */

	private void showItemVisible() {
		//如果没有登录或者没有投资过的
		if ((MyApplication.saveUserInfo == null || TextUtils.isEmpty(MyApplication.saveUserInfo.getAccess_Token())) || MyApplication.mUserDetailInfo == null ||
				!MyApplication.mUserDetailInfo.HasInvest) {
			setupBoutiqueVisible(View.GONE);
			setupNewVisible(View.VISIBLE);
		} else {
			setupBoutiqueVisible(View.VISIBLE);
			setupNewVisible(View.GONE);
		}
		//如果新标预告或者新手标为空的时候就显示列表
		if (announceRecordsBeans == null || announceRecordsBeans.size() <= 0 || newRecordsBeans == null || newRecordsBeans.size() <=
				0) {
			setupBoutiqueVisible(View.VISIBLE);
			setupNewVisible(View.GONE);
		}
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

	/**
	 * 请求统计数据
	 */
	private void getStatisticsData() {
		progressDialog.show();
		JSONObject obj = new JSONObject();
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.DailyWelfareData, obj, TokenUtil.getEncodeToken(ctx), true, new
				BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "首页：请求统计数据" + response.toString());

				progressDialog.dismiss();
				try {
					boolean status = PackageUtil.getRequestStatus(response, ctx);
					if (status) {
						JSONObject resultObject = response.getJSONObject("result");
						welfareDto = MyApplication.mGson.fromJson(resultObject.toString(), WelfareDto.class);
						//更新UI
						LogUtil.d("统计数据", welfareDto.toString());

						//滚动文字
						String[] rotateValues = new String[welfareDto.getJoinList().size()];
						for (int i = 0 ; i < welfareDto.getJoinList().size() ; i++) {
							rotateValues[i] = welfareDto.getJoinList().get(i).get("Title");
						}
						//参与人数
						String str = String.format("今天已有%s位用户获得加息", welfareDto.getTotalPopulation());
						//加息数值大于0说明已经参加过直接跳转
						if (welfareDto.getRate() > 0) {
							Activity_Daily_Welfare_Static.launche(ctx, String.valueOf(welfareDto.getRate()), String.valueOf
									(welfareDto.getTotalInterest()), str, rotateValues);
						} else {
							Activity_Daily_Welfare.launche(ctx, rotateValues, welfareDto.getTotalPopulation(), welfareDto
									.getTotalInterest());
						}
					} else {
						ToastUtil.alert(ctx, response.getString("message"));
					}
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.alert(ctx, "请求失败，请稍后重试");
				}
			}

			@Override
			public void onErrorResponse(Exception error) {
				progressDialog.dismiss();
				ToastUtil.alert(ctx, "请求失败，请稍后重试");
			}
		});
		requestQueue.add(jsonRequest);
	}
}