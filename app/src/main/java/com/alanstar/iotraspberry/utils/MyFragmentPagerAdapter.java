package com.alanstar.iotraspberry.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * FragmentPagerAdapter
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> data;
    private FragmentManager fm;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.fm = fm;
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }
}
