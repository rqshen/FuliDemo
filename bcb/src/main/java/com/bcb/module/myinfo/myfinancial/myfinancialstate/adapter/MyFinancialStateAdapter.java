package com.bcb.module.myinfo.myfinancial.myfinancialstate.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFinancialStateAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> fragmentsList;

	public MyFinancialStateAdapter(FragmentManager fm) {
		super(fm);
	}

	public MyFinancialStateAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.fragmentsList = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentsList.get(arg0);
	}

	@Override
	public int getCount() {
		return fragmentsList.size();
	}

}
