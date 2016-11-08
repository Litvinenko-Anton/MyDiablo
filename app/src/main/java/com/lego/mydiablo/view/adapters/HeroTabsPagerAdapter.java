package com.lego.mydiablo.view.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lego.mydiablo.view.fragments.ItemDetailFragment;


import java.util.ArrayList;
import java.util.List;


public class HeroTabsPagerAdapter extends FragmentPagerAdapter {

    public static final String POSITION_PAGE = "position_page";
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public HeroTabsPagerAdapter(FragmentManager manager, int rank) {
        super(manager);
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION_PAGE, 0);
        addFragment("1", rank);
    }

    public void compare(){
        addFragment("2",2); //hardcoded
        addSummaryFragment("3");
    }

    private void addFragment(String title, int rank) {
        mFragmentList.add(ItemDetailFragment.newInstance(rank));
        mFragmentTitleList.add(title);
    }

    private void addSummaryFragment(String title){
        mFragmentList.add(ItemDetailFragment.newInstance(2));  //hardcoded
        mFragmentTitleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
