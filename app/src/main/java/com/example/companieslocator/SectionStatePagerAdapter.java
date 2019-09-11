package com.example.companieslocator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {
    private final ArrayList<Fragment> fragmentsArray = new ArrayList<>();
    private final ArrayList<String> fragmentsTitleArray = new ArrayList<>();

    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentsArray.add(fragment);
        fragmentsTitleArray.add(title);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentsArray.get(i);
    }

    @Override
    public int getCount() {
        return fragmentsArray.size();
    }
}
