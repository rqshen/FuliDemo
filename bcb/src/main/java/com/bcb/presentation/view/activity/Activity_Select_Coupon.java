package com.bcb.presentation.view.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcb.R;
import com.bcb.base.old.Activity_Base;
import com.bcb.MyApplication;
import com.bcb.network.BcbJsonRequest;
import com.bcb.network.BcbRequest;
import com.bcb.network.BcbRequestQueue;
import com.bcb.network.BcbRequestTag;
import com.bcb.network.UrlsOne;
import com.bcb.data.bean.CouponListBean;
import com.bcb.data.bean.CouponRecordsBean;
import com.bcb.util.HttpUtils;
import com.bcb.util.LogUtil;
import com.bcb.util.MyActivityManager;
import com.bcb.util.MyListView;
import com.bcb.util.PackageUtil;
import com.bcb.util.ToastUtil;
import com.bcb.util.TokenUtil;
import com.bcb.presentation.adapter.CouponListAdapter;
import com.bcb.presentation.view.custom.PullableView.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Select_Coupon extends Activity_Base {

	private static final String TAG = "Activity_Select_Coupon";

	private MyListView mCouponListView;
	
	private int PageNow = 1; 
	private int PageSize = 10;

	private List<CouponRecordsBean> recordsBeans;
	private CouponListAdapter mCouponListAdapter;
	
	private LinearLayout null_data_layout;
    private TextView value_null_description;

	private float investAmount;
	private int CouponType;

    private Dialog convertDialog;
    private TextView dialog_error_tips;

	private boolean canLoadmore = true;
	private PullToRefreshLayout refreshLayout;
	private RelativeLayout loadmore_view;

    private BcbRequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        MyActivityManager myActivityManager = MyActivityManager.getInstance();
        myActivityManager.pushOneActivity(Activity_Select_Coupon.this);
		setBaseContentView(R.layout.activity_select_coupon);
		setLeftTitleVisible(true);
		setTitleValue("选择优惠券");
		setRightTitleValue("兑换", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConvertDialog();
            }
        });
		requestQueue = MyApplication.getInstance().getRequestQueue();
        init();
	}

    //初始化
	private void init() {
		investAmount = getIntent().getFloatExtra("investAmount", 0);
		CouponType = getIntent().getIntExtra("CouponType", 0);
        //如果是利息抵扣券，则设置标题
        if (CouponType == 16) {
            setTitleValue("选择利息抵扣券");
        }

		null_data_layout = (LinearLayout) findViewById(R.id.null_data_layout);
        value_null_description = (TextView) findViewById(R.id.value_null_description);
        if (CouponType == 16) {
            value_null_description.setText("暂无利息抵扣券，请先兑换");
        } else {
            value_null_description.setText("暂无优惠劵，请先兑换");
        }
		recordsBeans = new ArrayList<>();
		mCouponListAdapter = new CouponListAdapter(Activity_Select_Coupon.this, recordsBeans, investAmount, CouponType);
		mCouponListView = (MyListView) findViewById(R.id.listview_data_layout);
//		mCouponListView.setOnItemClickListener(new onClickViewCoupon());
		mCouponListView.setAdapter(mCouponListAdapter);

		//刷新
		loadmore_view = (RelativeLayout) findViewById(R.id.loadmore_view);
		refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
		refreshLayout.setRefreshResultView(false);
		refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				if (HttpUtils.isNetworkConnected(Activity_Select_Coupon.this)) {
					PageNow = 1;
					recordsBeans.clear();
					loadData();
                    loadmore_view.setVisibility(View.VISIBLE);
				} else {
					ToastUtil.alert(Activity_Select_Coupon.this, "网络异常，请稍后再试");
					refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				if (HttpUtils.isNetworkConnected(Activity_Select_Coupon.this)) {
					if (canLoadmore) {
						loadData();
					} else {
                        loadmore_view.setVisibility(View.GONE);
						refreshLayout.loadmoreFinish(PullToRefreshLayout.NOMORE);
					}
				} else {
					ToastUtil.alert(Activity_Select_Coupon.this, "网络异常，请稍后再试");
					refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				}
			}
		});
		refreshLayout.autoRefresh();
	}

    //加载优惠券数据
    private void loadData() {
    	JSONObject obj = new JSONObject();
		try {
			obj.put("PageNow", PageNow);
			obj.put("PageSize", PageSize);	
			obj.put("Status", 1);
			obj.put("CouponType", CouponType);
		} catch (JSONException e) {
			e.printStackTrace();
		}

        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Select_Coupon, obj, TokenUtil.getEncodeToken(this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean flag = PackageUtil.getRequestStatus(response, Activity_Select_Coupon.this);
                    if (flag) {
                        JSONObject obj = PackageUtil.getResultObject(response);
                        CouponListBean mCouponList = null;
                        if (obj != null) {
                            mCouponList = MyApplication.mGson.fromJson(obj.toString(), CouponListBean.class);
                        }
                        if (null != mCouponList && null != mCouponList.Records && mCouponList.Records.size() > 0) {
                            PageNow++;
                            canLoadmore = true;
                            setupListViewVisible(true);
                            synchronized (this) {
                                recordsBeans.addAll(mCouponList.Records);
                            }
                            if (null != mCouponListAdapter) {
                                mCouponListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            canLoadmore = false;
                            if(null != mCouponListAdapter){
                                mCouponListAdapter.notifyDataSetChanged();
                            }
                            if (PageNow <= 1) {
                                setupListViewVisible(false);
                            }
                        }
                    } else {
                        canLoadmore = false;
                        if (recordsBeans == null || recordsBeans.size() <= 0) {
                            setupListViewVisible(false);
                        } else {
                            setupListViewVisible(true);
                        }
                    }
                } finally {
                    refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }

            @Override
            public void onErrorResponse(Exception error) {
                refreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                if (recordsBeans == null || recordsBeans.size() <= 0) {
                    setupListViewVisible(false);
                } else {
                    setupListViewVisible(true);
                }
            }
        });
        jsonRequest.setTag(BcbRequestTag.BCB_SELECT_COUPON_REQUEST);
        requestQueue.add(jsonRequest);
	}

	//设置是否显示列表数据
	private void setupListViewVisible(boolean visible) {
		if (visible) {
			null_data_layout.setVisibility(View.GONE);
			mCouponListView.setVisibility(View.VISIBLE);
		} else {
			null_data_layout.setVisibility(View.VISIBLE);
			mCouponListView.setVisibility(View.GONE);
		}
	}

	private boolean isNeedRefush;
    //兑换优惠券
    private void loadExchangeData(String ExchangeCode) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("ExchangeCode", ExchangeCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        BcbJsonRequest jsonRequest = new BcbJsonRequest(UrlsOne.Convert_Coupon, obj, TokenUtil.getEncodeToken(Activity_Select_Coupon.this), new BcbRequest.BcbCallBack<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");
                    String message = response.getString("message");
                    if(status == 1) {
                        //加载优惠券数据
                        PageNow = 1;
                        recordsBeans.clear();
                        loadData();
                        //销毁兑换优惠对话框
                        convertDialog.dismiss();
                        convertDialog = null;
						Toast.makeText(Activity_Select_Coupon.this,  "兑换成功", Toast.LENGTH_SHORT).show();
						isNeedRefush=true;//兑换成功后需要刷新一下
                    } else {
                        dialog_error_tips.setVisibility(View.VISIBLE);
                        dialog_error_tips.setText(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(Exception error) {

            }
        });
        jsonRequest.setTag(BcbRequestTag.ConvertCouponTag);
        requestQueue.add(jsonRequest);
    }

	private void showConvertDialog() {
		convertDialog = new Dialog(Activity_Select_Coupon.this);
		View view = View.inflate(Activity_Select_Coupon.this, R.layout.dialog_enter_convertcode, null);

		Window win = convertDialog.getWindow();
		convertDialog.setCanceledOnTouchOutside(true);
		convertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		convertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		win.getDecorView().setPadding(60, 0, 60, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);
		convertDialog.setContentView(view);

		final EditText edit_code = (EditText) view.findViewById(R.id.edit_code);
		edit_code.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		dialog_error_tips = (TextView) view.findViewById(R.id.dialog_error_tips);
		Button bt_go = (Button) view.findViewById(R.id.bt_go);
		Button button_cancel = (Button)view.findViewById(R.id.button_cancel);

		edit_code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(edit_code.getText().toString())) {
					dialog_error_tips.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				//当输入第五个字符时才在之前插入一个空格，而不能在输入第四个字符时在之后插入一个空格
				//因为这样会导致最后的一个空格无法被删掉（因为删掉后又立即满足插入的条件了）
				//计算时要先去除添加的空格
				//插入空格时要去除空格后重新插入，而不是仅仅在最后插入一个空格，以防止用户在中间插入字符后空格变乱
				String code = edit_code.getText().toString().trim().replace(" ", "");
				LogUtil.i("bqt", "【更改】+code");
				StringBuilder sb = new StringBuilder(code);
				int length = code.length();
				for (int i = 0, j = 0; i < length; i += 4, j++) {
					sb.insert(i + j, " ");
					LogUtil.i("bqt", "【遍历】"+sb.toString().trim());
				}
				if (!edit_code.getText().toString().trim().equals(sb.toString().trim())) {
					edit_code.setText(sb.toString().trim());
					//要改变光标位置
					edit_code.setSelection(sb.toString().trim().length());
				}
			}
		});

		bt_go.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!TextUtils.isEmpty(edit_code.getText().toString().trim())) {
					loadExchangeData(edit_code.getText().toString().replace(" ", ""));
				} else {
					dialog_error_tips.setVisibility(View.VISIBLE);
					dialog_error_tips.setText("请输入兑换码");
				}

			}
		});

		button_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				convertDialog.dismiss();
			}
		});

		convertDialog.show();
	}

//	//选择优惠券，功能其实已经在CouponListAdapter适配器中已经完成了相应的功能
//	class onClickViewCoupon implements OnItemClickListener {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            LogUtil.d(Activity_Select_Coupon.this, "加载数据");
//			CouponRecordsBean coupon = (CouponRecordsBean) mCouponListView.getItemAtPosition(position);
//            //如果是体验券(CouponType = 1)，则不用判断是否大于最小金额
//			Intent intent = new Intent();
//			if (investAmount >= coupon.getMinAmount() || CouponType == 1) {
//				intent.putExtra("CouponId", coupon.getCouponId());
//				intent.putExtra("CouponAmount", coupon.getAmount()+"");
//				intent.putExtra("CouponMinAmount", coupon.getMinAmount()+"");
//                //如果是体验券CouponType为1的时候，则返回体验券的描述
//                if (CouponType == 1) {
//                    intent.putExtra("Amount", coupon.getAmount());
//                    intent.putExtra("ConditionDescn", coupon.getConditionDescn() + "");
//                }
//                //如果是利息折扣券，CouponType = 16， 则也需要返回券的描述
//                else if (CouponType == 16) {
//                    //利息抵扣券的金额
//                    intent.putExtra("InterestAmount", coupon.getAmount());
//                    //最小借款金额
//                    intent.putExtra("InterestMinAmount", coupon.getMinAmount());
//                    //借款金额描述
//                    intent.putExtra("InterestDescn", coupon.getConditionDescn() + "");
//                    //返回优惠券张数
//                    intent.putExtra("TotalCount", recordsBeans == null ? 0 : recordsBeans.size());
//                    LogUtil.d("已经返回券的ID", "已经返回");
//                }
//				intent.putExtra("isNeedRefush",isNeedRefush);
//				setResult(1, intent);
//			} else { //不是体验券，并且输入金额小于最小金额的时候，则提示
//                if (CouponType != 16) {
//                    ToastUtil.alert(Activity_Select_Coupon.this, "输入金额小于投标最小金额，无法使用优惠券");
//                }
//                //利息抵扣券，返回的时候，很可能已经兑换过优惠券了，需要返回一个优惠券张数，这样不管选择与否，回去的优惠券张数都是最新的。
//                else {
//                    ToastUtil.alert(Activity_Select_Coupon.this, "借款金额小于最小借款金额，无法使用利息抵扣券");
//                }
//				intent.putExtra("isNeedRefush",isNeedRefush);
//				setResult(2, intent);
//			}
//            finish();
//		}
//    }

}
