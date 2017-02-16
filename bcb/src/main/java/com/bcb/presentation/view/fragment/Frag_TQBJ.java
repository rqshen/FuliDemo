package com.bcb.presentation.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcb.R;
import com.bcb.common.app.App;
import com.bcb.common.net.BcbJsonRequest;
import com.bcb.common.net.BcbRequest;
import com.bcb.common.net.BcbRequestQueue;
import com.bcb.common.net.BcbRequestTag;
import com.bcb.common.net.UrlsTwo;
import com.bcb.data.bean.PrivilegeMoneyBasic;
import com.bcb.data.bean.PrivilegeMoneyDto;
import com.bcb.data.util.HttpUtils;
import com.bcb.data.util.LogUtil;
import com.bcb.data.util.MyListView;
import com.bcb.data.util.PackageUtil;
import com.bcb.data.util.ToastUtil;
import com.bcb.data.util.TokenUtil;
import com.bcb.presentation.adapter.PrivilegeMoneyAdapter;
import com.bcb.presentation.view.activity.Activity_Browser;
import com.bcb.presentation.view.activity.Activity_Money_Flowing_Water;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有优惠券
 */
public class Frag_TQBJ extends Frag_Base  implements AdapterView.OnItemClickListener, PrivilegeMoneyAdapter.IloadAfterRegeist  {

	private BcbRequestQueue requestQueue;
	private List<PrivilegeMoneyDto> datas;
	private MyListView mListView;
	private PrivilegeMoneyAdapter myAdapter;

	private Context ctx;
	private LinearLayout null_data_layout;
	private boolean canLoadmore = false;//就一条数据，不需要加载更多
	private PullToRefreshLayout refreshLayout;
	private RelativeLayout loadmore_view;
	TextView tv_shouyi_all, tv_benjin, tv_shouyi, total_privilege_money;
	ImageView iv_about;


	public Frag_TQBJ() {
		super();
	}

	@SuppressLint("ValidFragment")
	public Frag_TQBJ(Context ctx) {
		super();
		this.ctx = ctx;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_privilege_money, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.ctx = getActivity();
		total_privilege_money = (TextView) view.findViewById(R.id.total_privilege_money);
		tv_shouyi_all = (TextView) view.findViewById(R.id.tv_shouyi_all);
		tv_benjin = (TextView) view.findViewById(R.id.tv_benjin);
		tv_shouyi = (TextView) view.findViewById(R.id.tv_shouyi);
		iv_about = (ImageView) view.findViewById(R.id.iv_about);
		total_privilege_money.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ctx, Activity_Money_Flowing_Water.class));
			}
		});
		iv_about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity_Browser.launche(ctx, "关于特权本金", UrlsTwo.AboutExpiredProjectIntroduction);
			}
		});

		requestQueue = App.getInstance().getRequestQueue();
		datas = new ArrayList<>();
		myAdapter = new PrivilegeMoneyAdapter(ctx, datas);
		myAdapter.setIloadAfterRegeist(this);
		mListView = (MyListView) view.findViewById(R.id.listview_data_layout);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(myAdapter);
		//刷新
		null_data_layout = (LinearLayout) view.findViewById(R.id.null_data_layout);
		loadmore_view = (RelativeLayout) view.findViewById(R.id.loadmore_view);
		refreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
		refreshLayout.setRefreshResultView(false);
		refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				if (HttpUtils.isNetworkConnected(ctx)) {
//                    PageNow = 1;
					datas.clear();
					loadData();
					loadmore_view.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.alert(ctx, "网络异常，请稍后再试");
					refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				if (HttpUtils.isNetworkConnected(ctx)) {
					if (canLoadmore) {
						loadData();
					} else {
						loadmore_view.setVisibility(View.GONE);
						refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
					}
				} else {
					ToastUtil.alert(ctx, "网络异常，请稍后再试");
					refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				}
			}
		});
		refreshLayout.autoRefresh();
	}

	private void loadData() {

//******************************************************************************************
		BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsTwo.UserPrivilegeMoneyDto, null, TokenUtil.getEncodeToken(ctx), new BcbRequest.BcbCallBack<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LogUtil.i("bqt", "【特权金返回数据】" + response.toString());
				if (PackageUtil.getRequestStatus(response, ctx)) {
					JSONObject data = PackageUtil.getResultObject(response);
					//判断JSON对象是否为空
					if (data != null) {
						PrivilegeMoneyBasic bean = App.mGson.fromJson(data.toString(), PrivilegeMoneyBasic.class);
						tv_shouyi_all.setText(String.format("%.2f", bean.TotalIncome));
						tv_benjin.setText(String.format("%.2f", bean.ActivedPrincipal));
						tv_shouyi.setText(String.format("%.2f", bean.ActivedIncome));

						//列表
						JSONArray jsonArray = data.optJSONArray("DataList");
						if (jsonArray != null) {
							for (int i = 0; i < jsonArray.length(); i++) {
								try {
									JSONObject item = jsonArray.getJSONObject(i);
									datas.add(App.mGson.fromJson(item.toString(), PrivilegeMoneyDto.class));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
					myAdapter.notifyDataSetChanged();
					setupListViewVisible(true);
					refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
					refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}
			}

			@Override
			public void onErrorResponse(Exception error) {

			}
		});
		jsonRequest.setTag(BcbRequestTag.UserPrivilegeMoneyDtoTag);
		requestQueue.add(jsonRequest);
	}

	private void setupListViewVisible(boolean status) {
		// 如果存在数据的时候，显示
		if (status) {
			null_data_layout.setVisibility(View.GONE);
		} else {
			null_data_layout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}


	@Override
	public void loadAfterRegeist() {
		refreshLayout.autoRefresh();
	}
}
