package com.bcb.base;


import android.content.Context;
import android.os.Bundle;

import com.bcb.util.ActivityCollector;
import com.bcb.util.InstanceUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ruiqin.shen.
 * 类说明：所有的Activity的基类
 * 在onCreate调用present的setVM方法，将View和Model关联起来
 */
public abstract class BaseActivity<P extends BasePresenter, M extends BaseModel> extends SlidingActivity {
    public P mPresenter;
    public Context mContext;
    private Unbinder mBind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ActivityCollector.addActivity(this);
        mBind = ButterKnife.bind(this);
        mContext = this;
        if (this instanceof BaseView) {
            mPresenter = InstanceUtil.getInstance(this, 0);
            mPresenter.setVM(this, InstanceUtil.getInstance(this, 1));
        }
        initView(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        mBind.unbind();
    }

    /**
     * 布局中Fragment的ID
     */
    protected abstract int getFragmentContentId();

    /**
     * 添加fragment
     *
     * @param fragment
     */
    protected void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    /**
     * 移除fragment
     */
    protected void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public abstract int getLayoutId();

    public abstract void initView(Bundle savedInstanceState);

    //友盟统计
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
